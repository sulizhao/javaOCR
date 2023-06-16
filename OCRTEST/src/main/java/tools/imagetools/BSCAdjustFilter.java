package tools.imagetools;
import java.awt.image.BufferedImage;
public class BSCAdjustFilter extends AbstractBufferedImageOp {
    private double brightness;
    private double contrast;
    private double saturation;
    public double getBrightness() {
        return brightness;
    }
    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }
    public double getSaturation() {
        return saturation;
    }
    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }
    public double getContrast() {
        return contrast;
    }
    public void setContrast(double contrast) {
        this.contrast = contrast;
    }
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        handleParameters();//调整各系数取值范围
        int width = src.getWidth();
        int height = src.getHeight();
        if (dest == null) {
            dest = creatCompatibleDestImage(src, null);
        }
        int[] inpixels = new int[width * height];
        int[] outpixels = new int[width * height];
        getRGB(src, 0, 0, width, height, inpixels);
        int index = 0;
        for (int row = 0; row<width; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col<height; col++) {
                index = row * width + col;
                ta = (inpixels[index] >> 24) & 0xff;
                tr = (inpixels[index] >> 16) & 0xff;
                tg = (inpixels[index] >> 8) & 0xff;
                tb = inpixels[index] & 0xff;
                //RGB转换为HSL色彩空间
                double[] hsl = rgb2Hsl(new int[]{tr, tg, tb});
                //调整饱和度
                hsl[1] = hsl[1] * saturation;
                if (hsl[1] < 0.0) {
                    hsl[1] = 0.0;
                }
                if (hsl[1] > 255.0) {
                    hsl[1] = 255.0;
                }
                //调整亮度
                hsl[2] = hsl[2] * brightness;
                if (hsl[2] < 0.0) {
                    hsl[2] = 0.0;
                }
                if (hsl[2] > 255.0) {
                    hsl[2] = 255.0;
                }
//HSL转换为rgb空间
                int[] rgb = hsl2RGB(hsl);
                tr = clamp(rgb[0]);
                tg = clamp(rgb[1]);
                tb = clamp(rgb[2]);
//调整对比度
                double cr = ((tr / 255.0d) - 0.5d) * contrast;
                double cg = ((tg / 255.0d) - 0.5d) * contrast;
                double cb = ((tb / 255.0d) - 0.5d) * contrast;
//输出RGB值
                tr = (int) ((cr + 0.5f) * 255.0f);
                tg = (int) ((cg + 0.5f) * 255.0f);
                tb = (int) ((cb + 0.5f) * 255.0f);
                outpixels[index] = (ta << 24) | (clamp(tr) << 16) | (clamp(tg) << 8) | clamp(tb);
            }
        }
        setRGB(dest, 0, 0, width, height, outpixels);
        return dest;
    }
    public void handleParameters() {
        contrast = (1.0 + contrast / 100.0);
        brightness = (1.0 + brightness / 100.0);
        saturation = (1.0 + saturation / 100.0);
    }
    public int clamp(int value) {
        return value > 255 ? 255 : ((value < 0) ? 0 : value);
    }
}
