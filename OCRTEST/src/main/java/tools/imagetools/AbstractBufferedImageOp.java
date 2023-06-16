package tools.imagetools;
import java.awt.image.BufferedImage;
/**
 Created by LENOVO on 18-1-29.
 */
public class AbstractBufferedImageOp {
    public static final double clo60 = 1.0/60.0;
    public static final double clo255 = 1.0/255.0;
    public int tr = 0,tg = 0,tb = 0;
    public AbstractBufferedImageOp(){}
//读取像素数据
    public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels){
        int type = image.getType();
        if(type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB){
            return (int[]) image.getRaster().getDataElements(x,y,width,height,pixels);
        }else{
            return image.getRGB(x,y,width,height,pixels,0,width);
        }
    }
    //写入像素数据
    public void setRGB(BufferedImage image,int x, int y, int width, int height, int[] pixels){
        int type = image.getType();
        if(type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB){
            image.getRaster().setDataElements(x,y,width,height,pixels);
        }else {
            image.setRGB(x,y,width,height,pixels,0,width);
        }
    }
    public BufferedImage creatCompatibleDestImage(BufferedImage src,BufferedImage dest){
        return new BufferedImage(src.getWidth(),src.getHeight(),BufferedImage.TYPE_INT_RGB);
    }
    /**
     * @param rgb
     * @return
     */
    public static double[] rgb2Hsl(int[] rgb) {
        if (rgb == null) {
            return null;
        }
        float H, S, L, var_Min, var_Max, del_Max, del_R, del_G, del_B;
        H = 0;
        var_Min = Math.min(rgb[0], Math.min(rgb[2], rgb[1]));
        var_Max = Math.max(rgb[0], Math.max(rgb[2], rgb[1]));
        del_Max = var_Max - var_Min;
        L = (var_Max + var_Min) / 2;
        if (del_Max == 0) {
            H = 0;
            S = 0;
        } else {
            if (L < 128) {
                S = 256 * del_Max / (var_Max + var_Min);
            } else {
                S = 256 * del_Max / (512 - var_Max - var_Min);
            }
            del_R = ((360 * (var_Max - rgb[0]) / 6) + (360 * del_Max / 2))
                    / del_Max;
            del_G = ((360 * (var_Max - rgb[1]) / 6) + (360 * del_Max / 2))
                    / del_Max;
            del_B = ((360 * (var_Max - rgb[2]) / 6) + (360 * del_Max / 2))
                    / del_Max;
            if (rgb[0] == var_Max) {
                H = del_B - del_G;
            } else if (rgb[1] == var_Max) {
                H = 120 + del_R - del_B;
            } else if (rgb[2] == var_Max) {
                H = 240 + del_G - del_R;
            }
            if (H < 0) {
                H += 360;
            }
            if (H >= 360) {
                H -= 360;
            }
            if (L >= 256) {
                L = 255;
            }
            if (S >= 256) {
                S = 255;
            }
        }
        return new double[]{H, S, L};
    }
    /**
     * @param hsl
     * @return
     */
    public static int[] hsl2RGB(double[] hsl) {
        if (hsl == null) {
            return null;
        }
        double H = hsl[0];
        double S = hsl[1];
        double L = hsl[2];
        double R, G, B, var_1, var_2;
        if (S == 0) {
            R = L;
            G = L;
            B = L;
        } else {
            if (L < 128) {
                var_2 = (L * (256 + S)) / 256;
            } else {
                var_2 = (L + S) - (S * L) / 256;
            }
            if (var_2 > 255) {
                var_2 = Math.round(var_2);
            }
            if (var_2 > 254) {
                var_2 = 255;
            }
            var_1 = 2 * L - var_2;
            R = RGBFromHue(var_1, var_2, H + 120);
            G = RGBFromHue(var_1, var_2, H);
            B = RGBFromHue(var_1, var_2, H - 120);
        }
        R = R < 0 ? 0 : R;
        R = R > 255 ? 255 : R;
        G = G < 0 ? 0 : G;
        G = G > 255 ? 255 : G;
        B = B < 0 ? 0 : B;
        B = B > 255 ? 255 : B;
        return new int[]{(int) Math.round(R), (int) Math.round(G), (int) Math.round(B)};
    }
    /**
     * @param a
     * @param b
     * @param h
     * @return
     */
    public static double RGBFromHue(double a, double b, double h) {
        if (h < 0) {
            h += 360;
        }
        if (h >= 360) {
            h -= 360;
        }
        if (h < 60) {
            return a + ((b - a) * h) / 60;
        }
        if (h < 180) {
            return b;
        }
        if (h < 240) {
            return a + ((b - a) * (240 - h)) / 60;
        }
        return a;
    }
}
