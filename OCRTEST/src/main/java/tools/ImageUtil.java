package tools;

import dspocr.dataloader.DetLoader.DetLoader;
import dspocr.dataloader.data.Blob;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static tools.MathFunctions.upsample;

public class ImageUtil {
    //图片对比度调整
    public static BufferedImage img_color_contrast(BufferedImage imgsrc, int contrast) {
        try {
            int contrast_average = 128;
            //创建一个不带透明度的图片
            BufferedImage back = new BufferedImage(imgsrc.getWidth(), imgsrc.getHeight(), BufferedImage.TYPE_INT_RGB);
            int width = imgsrc.getWidth();
            int height = imgsrc.getHeight();
            int pix;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = imgsrc.getRGB(j, i);
                    Color color = new Color(pixel);
                    if (color.getRed() < contrast_average) {
                        pix = color.getRed() - Math.abs(contrast);
                        if (pix < 0) pix = 0;
                    } else {
                        pix = color.getRed() + Math.abs(contrast);
                        if (pix > 255) pix = 255;
                    }
                    int red = pix;
                    if (color.getGreen() < contrast_average) {
                        pix = color.getGreen() - Math.abs(contrast);
                        if (pix < 0) pix = 0;
                    } else {
                        pix = color.getGreen() + Math.abs(contrast);
                        if (pix > 255) pix = 255;
                    }
                    int green = pix;
                    if (color.getBlue() < contrast_average) {
                        pix = color.getBlue() - Math.abs(contrast);
                        if (pix < 0) pix = 0;
                    } else {
                        pix = color.getBlue() + Math.abs(contrast);
                        if (pix > 255) pix = 255;
                    }
                    int blue = pix;
                    color = new Color(red, green, blue);
                    int x = color.getRGB();
                    back.setRGB(j, i, x);
                }
            }
            return back;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Blob resize(BufferedImage image, int desH, int desW) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][][] matrixBGR = getMatrixBGR(image);
        // w,h,c 转c,h,w  nearest biliner
        Blob blob = bytes2Blob(matrixBGR, 3, height, width);
        blob = upsample(blob, desH / (double) height,desW / (double) width, "biliner");
        return blob;

    }

    public static Object[] DetResizeForTest(BufferedImage image) {
        double limit_side_len = 736.0;
        int w = image.getWidth();
        int h = image.getHeight();
        double ratio = 1.0;
//        if (Math.min(h, w) < limit_side_len) {
            if (h < w) {
                ratio = limit_side_len / h;
            } else {
                ratio = limit_side_len / w;
            }
//        }
        double resize_h1 = h * ratio;
        double resize_w1= w * ratio;

        int resize_h = Math.max(((int)Math.round(resize_h1 / 32) * 32), 32);
        int resize_w = Math.max(((int)Math.round(resize_w1 / 32) * 32), 32);
        Blob resize = resize(image, resize_h, resize_w);
//        BufferedImage destImage = DetLoader.blob2Image(resize);
//        try {
//            ImageIO.write(destImage, "jpg", new File("D:\\data\\21.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        double ratio_h = resize_h / (double) h;
        double ratio_w = resize_w / (double) w;

        return new Object[]{resize, ratio_h, ratio_w};

    }

    public static Blob imgFile2Blob(File imgFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(imgFile);
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            int[][][][] matrixBGR = getMatrixBGR(bufferedImage);
            return bytes2Blob(matrixBGR, 3, height, width);
        } catch (Exception e) {
            throw new RuntimeException("读取图片失败！", e);
        }
    }

    public static BufferedImage blob2Image(Blob blob) {
        try {
            double[][][][] data = blob.getData();
            BufferedImage destImage = new BufferedImage(blob.getWidth(), blob.getHeight(), BufferedImage.TYPE_INT_RGB);
            for(int h=0; h<blob.getHeight(); h++) {
                for(int w=0; w<blob.getWidth(); w++) {
                    int b= (int)Math.round(data[0][0][h][w]);
                    int g= (int)Math.round(data[0][1][h][w]);
                    int r= (int)Math.round(data[0][2][h][w]);
                    int rgb = (0xFF << 24)|(r << 16)|(g << 8)|b;
                    destImage.setRGB(w,h,rgb);
                }
            }
            return destImage;
        } catch (Exception e) {
            throw new RuntimeException("Blob生成图片失败！", e);
        }
    }

    /**
     * 对图像解码返回BGR格式矩阵数据
     *
     * @param image
     * @return
     */
    private static int[][][][] getMatrixBGR(BufferedImage image) {
        if (null == image) {
            throw new NullPointerException();
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][][] matrixBGR = new int[1][3][height][width];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int rgb = image.getRGB(w, h);
                //[(argb & 0xff0000) >> 16, (argb & 0xff00) >> 8, argb & 0xff];
                matrixBGR[0][0][h][w] = rgb & 0xff;
                matrixBGR[0][1][h][w] = rgb >> 8 & 0xff;
                matrixBGR[0][2][h][w] = rgb >> 16 & 0xff;
            }
        }

        return matrixBGR;
    }

    private static Blob bytes2Blob(int[][][][] matrix, int channels, int height, int width) {
        Blob imgBlob = new Blob(channels, height, width);
        double[][][][] data = imgBlob.getData();
        for(int n=0; n<imgBlob.getNumbers();n++) {
            for(int c =0; c< imgBlob.getChannels();c++){
                for(int h=0; h< imgBlob.getHeight();h++){
                    for(int w=0; w< imgBlob.getWidth();w++){
                        data[n][c][h][w]=(double)(matrix[n][c][h][w]);
                    }
                }
            }
        }
        return imgBlob;
    }

}
