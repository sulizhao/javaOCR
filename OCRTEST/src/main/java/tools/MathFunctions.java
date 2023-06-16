/*
 *
 *
 */

package tools;

import dspocr.dataloader.DetLoader.DBProcess;
import dspocr.dataloader.data.Blob;
import dspocr.model.Network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.Vector;

public class MathFunctions {

    public static void gaussianInitData(Blob blob) {
        Random random = new Random();

        double[][][][] data = blob.getData();
        for (int n = 0; n < blob.getNumbers(); n++) {
            for (int c = 0; c < blob.getChannels(); c++) {
                for (int h = 0; h < blob.getHeight(); h++) {
                    for (int w = 0; w < blob.getWidth(); w++) {
                        data[n][c][h][w] = random.nextGaussian() * 0.1;
                    }
                }
            }
        }
    }

    public static void constantInitData(Blob blob, double value) {
        blob.fillValue(value);
    }

    public static void randomInitData(Blob blob) {
        Random random = new Random();
        double[][][][] data = blob.getData();
        for (int n = 0; n < blob.getNumbers(); n++) {
            for (int c = 0; c < blob.getChannels(); c++) {
                for (int h = 0; h < blob.getHeight(); h++) {
                    for (int w = 0; w < blob.getWidth(); w++) {
                        data[n][c][h][w] = random.nextDouble();
                    }
                }
            }
        }
    }

    public static void dataDivConstant(Blob blob, double constant) {
        double[][][][] data = blob.getData();
        for (int n = 0; n < blob.getNumbers(); n++) {
            for (int c = 0; c < blob.getChannels(); c++) {
                for (int h = 0; h < blob.getHeight(); h++) {
                    for (int w = 0; w < blob.getWidth(); w++) {
                        data[n][c][h][w] /= constant;
                    }
                }
            }
        }
    }

    public static void deepWiseConv2dSame(Blob input, Blob kernel, Blob bias, Blob output) {
        double[][][][] inputData = input.getData();
        double[][][][] kernelData = kernel.getData();
        double[][][][] outputData = output.getData();

        int features = output.getChannels() / input.getChannels();
        Vector<Task<Object>> workers = new Vector<Task<Object>>();
        for (int n = 0; n < output.getNumbers(); n++) {
            for (int co = 0; co < output.getChannels(); co++) {
                workers.add(new Task<Object>(n, co) {
                    @Override
                    public Object call() throws Exception {

                        int inputChannelIndex = co / features;
                        for (int h = 0; h < output.getHeight(); h++) {
                            for (int w = 0; w < output.getWidth(); w++) {
                                //先定位到输出的位置
                                //然后遍历kernel,通过kernel定位输入的位置
                                //然后将输入乘以kernel
                                int inStartX = w - kernel.getWidth() / 2;
                                int inStartY = h - kernel.getHeight() / 2;
                                //和卷积核乘加
                                for (int kh = 0; kh < kernel.getHeight(); kh++) {
                                    for (int kw = 0; kw < kernel.getWidth(); kw++) {
                                        int inY = inStartY + kh;
                                        int inX = inStartX + kw;
                                        if (inY >= 0 && inY < input.getHeight() && inX >= 0 && inX < input.getWidth()) {
                                            outputData[n][co][h][w] += kernelData[co][0][kh][kw] *
                                                    inputData[n][inputChannelIndex][inY][inX];
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }
        ThreadPoolManager.getInstance(Network.getInstance()).dispatchTask(workers);

        //加偏置
        if (bias != null) {
            for (int n = 0; n < output.getNumbers(); n++) {
                for (int co = 0; co < output.getChannels(); co++) {
                    for (int h = 0; h < output.getHeight(); h++) {
                        for (int w = 0; w < output.getWidth(); w++) {
                            outputData[n][co][h][w] += bias.getData()[0][0][0][co];
                        }
                    }
                }
            }
        }
    }


    public static void conv2dBlobSame(Blob input, int padding, int stride, Blob weight, Blob bias, Blob output) {
        double[][][][] inputData = input.getData();
        double[][][][] kernelData = weight.getData();
        double[][][][] outputData = output.getData();
        Vector<Task<Object>> workers = new Vector<Task<Object>>();
        for (int n = 0; n < output.getNumbers(); n++) {
            for (int co = 0; co < output.getChannels(); co++) {
                workers.add(new Task<Object>(n, co) {
                    @Override
                    public Object call() throws Exception {

                        for (int ci = 0; ci < input.getChannels(); ci++) {
                            for (int ih = 0; ih < input.getHeight(); ih += stride) {
                                for (int iw = 0; iw < input.getWidth(); iw += stride) {
                                    //先定位到输出的位置
                                    //然后遍历kernel,通过kernel定位输入的位置
                                    //然后将输入乘以kernel
                                    int inStartX = iw - padding;
                                    int inStartY = ih - padding;
                                    //和卷积核乘加
                                    for (int kh = 0; kh < weight.getHeight(); kh++) {
                                        for (int kw = 0; kw < weight.getWidth(); kw++) {
                                            int inY = inStartY + kh;
                                            int inX = inStartX + kw;
                                            if (inY >= 0 && inY < input.getHeight() && inX >= 0 && inX < input.getWidth()) {
                                                outputData[n][co][ih / stride][iw / stride] += kernelData[co][ci][kh][kw] *
                                                        inputData[n][ci][inY][inX];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }
        ThreadPoolManager.getInstance(Network.getInstance()).dispatchTask(workers);
        //加偏置
        if (bias != null) {
            for (int n = 0; n < output.getNumbers(); n++) {
                for (int co = 0; co < output.getChannels(); co++) {
                    for (int h = 0; h < output.getHeight(); h++) {
                        for (int w = 0; w < output.getWidth(); w++) {
                            outputData[n][co][h][w] += bias.getData()[0][0][0][co];
                        }
                    }
                }
            }
        }
    }


    /**
     * 转置卷积
     * @param input
     * @param padding
     * @param stride
     * @param weight
     * @param bias
     * @param output
     */
    public static void conv2dBlobSameTranspose(Blob input, int padding, int stride, Blob weight, Blob bias, Blob output) {

        double[][][][] data = input.getData();
        double[][][][] weightData = weight.getData();
        double[][][][] outputData = output.getData();
        Vector<Task<Object>> workers = new Vector<Task<Object>>();
        for (int n = 0; n < input.getNumbers(); n++) {
            for (int co = 0; co < output.getChannels(); co++) {
                workers.add(new Task<Object>(n, co) {
                    @Override
                    public Object call() throws Exception {
                        for (int ic = 0; ic < input.getChannels(); ic++) {
                            for (int ih = 0; ih < input.getHeight(); ih++) {
                                for (int iw = 0; iw < input.getWidth(); iw++) {
                                    for (int wh = 0; wh < weight.getHeight(); wh++) {
                                        for (int ww = 0; ww < weight.getWidth(); ww++) {
                                            int oh = ih * stride + wh - padding;
                                            int ow = iw * stride + ww - padding;
                                            if (oh >= 0 && oh < output.getHeight() && ow >= 0 && ow < output.getWidth()) {
                                                outputData[n][co][oh][ow] += weightData[ic][co][wh][ww] * data[n][ic][ih][iw];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }

        ThreadPoolManager.getInstance(Network.getInstance()).dispatchTask(workers);
        //加偏置
        if (bias != null) {
            for (int n = 0; n < output.getNumbers(); n++) {
                for (int co = 0; co < output.getChannels(); co++) {
                    for (int h = 0; h < output.getHeight(); h++) {
                        for (int w = 0; w < output.getWidth(); w++) {
                            outputData[n][co][h][w] += bias.getData()[0][0][0][co];
                        }
                    }
                }
            }
        }
    }
    /**
     * 转置卷积
     * @param input
     * @param padding
     * @param stride
     * @param weight
     * @param bias
     * @param output
     */
    public static void deepWiseConv2dBlobSameTranspose(Blob input, int padding, int stride, Blob weight, Blob bias, Blob output) {

        double[][][][] data = input.getData();
        double[][][][] weightData = weight.getData();
        double[][][][] outputData = output.getData();
        Vector<Task<Object>> workers = new Vector<Task<Object>>();
        for (int n = 0; n < input.getNumbers(); n++) {
            for (int co = 0; co < output.getChannels(); co++) {
                workers.add(new Task<Object>(n, co) {
                    @Override
                    public Object call() throws Exception {
                            for (int ih = 0; ih < input.getHeight(); ih++) {
                                for (int iw = 0; iw < input.getWidth(); iw++) {
                                    for (int wh = 0; wh < weight.getHeight(); wh++) {
                                        for (int ww = 0; ww < weight.getWidth(); ww++) {
                                            int oh = ih * stride + wh - padding;
                                            int ow = iw * stride + ww - padding;
                                            if (oh >= 0 && oh < output.getHeight() && ow >= 0 && ow < output.getWidth()) {
                                                outputData[n][co][oh][ow] += weightData[co][0][wh][ww] * data[n][co][ih][iw];
                                            }
                                        }
                                    }
                                }
                            }
                        return null;
                    }
                });
            }
        }

        ThreadPoolManager.getInstance(Network.getInstance()).dispatchTask(workers);
        //加偏置
        if (bias != null) {
            for (int n = 0; n < output.getNumbers(); n++) {
                for (int co = 0; co < output.getChannels(); co++) {
                    for (int h = 0; h < output.getHeight(); h++) {
                        for (int w = 0; w < output.getWidth(); w++) {
                            outputData[n][co][h][w] += bias.getData()[0][0][0][co];
                        }
                    }
                }
            }
        }
    }


    public static Blob rotate180Blob(Blob input) {
        Blob output = new Blob(input.getNumbers(), input.getChannels(), input.getHeight(), input.getWidth());
        double[][][][] inputData = input.getData();
        double[][][][] outputData = output.getData();
        /*
         * 旋转180度就是上下颠倒，同时左右镜像
         */
        for (int n = 0; n < output.getNumbers(); n++) {
            for (int c = 0; c < output.getChannels(); c++) {
                for (int h = 0; h < output.getHeight(); h++) {
                    for (int w = 0; w < output.getWidth(); w++) {
                        outputData[n][c][h][w] = inputData[n][c][input.getHeight() - h - 1][input.getWidth() - w - 1];
                    }
                }
            }
        }
        return output;
    }

    public static Blob upsample(Blob input, double scale, String modeType) {
        return upsample(input, scale, scale, modeType);
    }

    /**
     * @param input
     * @param scalew
     * @param modeType nearest biliner
     * @return
     */
    public static Blob upsample(Blob input, double scaleh, double scalew, String modeType) {
        int numbers = input.getNumbers();
        int channels = input.getChannels();
        int height = input.getHeight();
        int width = input.getWidth();
        double[][][][] inputData = input.getData();
        if (scalew > 1) {
            if ("nearest".equalsIgnoreCase(modeType)) {
                int outHeight = (int) (height * scaleh);
                int outWidth = (int) (width * scalew);
                Blob outBlob = new Blob(numbers, channels, outHeight, outWidth);
                double[][][][] data = outBlob.getData();
                for (int n = 0; n < numbers; n++) {
                    for (int c = 0; c < channels; c++) {
                        for (int h = 0; h < height; h++) {
                            for (int w = 0; w < width; w++) {
                                double orgData = inputData[n][c][h][w];
                                for (int oh = (int) (h * scaleh); oh < (h + 1) * scaleh; oh++) {
                                    for (int ow = (int) (w * scalew); ow < (w + 1) * scalew; ow++) {
                                        data[n][c][oh][ow] = orgData;
                                    }
                                }
                            }
                        }
                    }
                }
                return outBlob;
            } else if ("biliner".equalsIgnoreCase(modeType)) {
                int outHeight = (int) (Math.round(height * scaleh));
                int outWidth = (int) (Math.round(width * scalew));
                Blob outBlob = new Blob(numbers, channels, outHeight, outWidth);
                scaleh = (outHeight - 1) / (double) (height - 1);
                scalew = (outWidth - 1) / (double) (width - 1);
                double[][][][] data = outBlob.getData();
                for (int n = 0; n < numbers; n++) {
                    for (int c = 0; c < channels; c++) {
                        for (int h = 0; h < height - 1; h++) {
                            for (int w = 0; w < width - 1; w++) {
                                double orgData = inputData[n][c][h][w];
                                int nextIndexH = h + 1;
                                int nextIndexW = w + 1;
                                double nextWdata = inputData[n][c][h][nextIndexW];
                                double nextHdata = inputData[n][c][nextIndexH][w];
                                double nextHWdata = inputData[n][c][nextIndexH][nextIndexW];
                                int maxH = (int) (Math.round((h + 1) * scaleh));
                                int minH = (int) (Math.round(h * scaleh));
                                for (int oh = minH; oh < maxH; oh++) {
                                    int maxW = (int) (Math.round((w + 1) * scalew));
                                    int minW = (int) (Math.round(w * scalew));
                                    for (int ow = minW; ow < maxW; ow++) {
                                        data[n][c][oh][ow] = doubleLinearInterpolation(ow, oh, minW, maxW, minH, maxH,
                                                orgData, nextWdata, nextHdata, nextHWdata);
                                        if (oh == outHeight - 1) {
                                            data[n][c][outHeight - 1][ow] = inputData[n][c][height - 1][w];
                                        }
                                        if (ow == outWidth - 1) {
                                            data[n][c][oh][outWidth - 1] = inputData[n][c][h][width - 1];
                                        }
                                    }

                                }
                            }
                        }
                        data[n][c][outHeight - 1][outWidth - 1] = inputData[n][c][height - 1][width - 1];
                    }
                }
                return outBlob;
            } else {
                System.err.println("不支持的unsample方式");
                return null;
            }
        } else if (scalew < 1) {
            int outHeight = (int) (height * scaleh);
            int outWidth = (int) (width * scalew);
            Blob outBlob = new Blob(numbers, channels, outHeight, outWidth);
            double[][][][] data = outBlob.getData();
            for (int n = 0; n < numbers; n++) {
                for (int c = 0; c < channels; c++) {
                    for (int h = 0; h < outHeight; h++) {
                        for (int w = 0; w < outWidth; w++) {
                            data[n][c][h][w] = inputData[n][c][(int) (h / scaleh)][(int) (w / scalew)];
                        }
                    }
                }
            }
            return outBlob;
        } else {
            return input;
        }
    }


    /**
     * 线性插值
     * p1------p-------p2
     * v1------v-------v2
     *
     * @param p  插值点坐标
     * @param p1 顶点坐标1
     * @param p2 顶点坐标2
     * @param v1 顶点数值1
     * @param v2 顶点数值2
     * @return 插值后的数值
     */
    public static double linearInterpolation(double p, double p1, double p2, double v1, double v2) {
        if (v1 == v2 || p1 == p2) {
            //value值相等 距离为0 不进行插值计算
            return v1;
        } else {
            return ((p2 - p) / (p2 - p1) * v1) + ((p - p1) / (p2 - p1) * v2);
        }

    }


    /**
     * 双线性插值 计算点v处的实际值
     * 点位分布如图  v(x,y) v1(x1,y1) v2(x1,y2) v3(x2,y1) v4(x2,y2)
     * v2-----------v4
     * |            |
     * r1-----v-----r2
     * |            |
     * v1-----------v3
     * 先通过线性插值计算r1 r2处的实际值 然后通过r1 r2计算v的值
     *
     * @param x  实际x坐标
     * @param y  实际y坐标
     * @param x1 x1
     * @param x2 x2
     * @param y1 y1
     * @param y2 y2
     * @param v1 左下value
     * @param v2 左上value
     * @param v3 右下value
     * @param v4 右上value
     * @return double
     */
    public static double doubleLinearInterpolation(double x, double y,
                                                   double x1, double x2, double y1, double y2,
                                                   double v1, double v2, double v3, double v4) {
        double r1 = linearInterpolation(x, x1, x2, v1, v2);
        double r2 = linearInterpolation(x, x1, x2, v3, v4);
        return linearInterpolation(y, y1, y2, r1, r2);

    }

    /**
     * 目前只实现4维横向拼接
     *
     * @param blob1
     * @param blob2
     * @param axis
     * @return
     */
    public static Blob concat(Blob blob1, Blob blob2, int axis) {
        double[][][][] data1 = blob1.getData();
        double[][][][] data2 = blob2.getData();
        if ((axis == 1 || axis == -1)) {
            Blob out = new Blob(blob1.getNumbers(), blob1.getChannels() + blob2.getChannels(), blob1.getHeight(), blob1.getWidth());
            double[][][][] data = out.getData();
            for (int n = 0; n < out.getNumbers(); n++) {
                for (int c = 0; c < out.getChannels(); c++) {
                    if (c < blob1.getChannels()) {
                        data[n][c] = data1[n][c];
                    } else {
                        data[n][c] = data2[n][c - blob1.getChannels()];
                    }
                }
            }
            return out;
        } else {
            System.err.println("concat其他axis暂未实现");
            return null;
        }
    }

    public static void main(String[] args) {
        File file = new File("D:\\data\\13.jpg");
        try {
//            Blob blob = ImageUtil.imgFile2Blob(file);
//            //nearest  biliner
//            blob = upsample(blob, 0.05, "biliner");

            Blob blob = DBProcess.resizeImage(file);
            BufferedImage destImage = ImageUtil.blob2Image(blob);
            ImageIO.write(destImage, "jpg", new File("D:\\13.jpg"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
