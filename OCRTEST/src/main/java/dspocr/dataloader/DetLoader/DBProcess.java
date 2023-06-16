package dspocr.dataloader.DetLoader;

import dspocr.dataloader.data.Blob;
import dspocr.modules.BatchNormal2d;
import tools.ImageUtil;
import tools.MathFunctions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DBProcess {

    public static Blob bNImage(Blob blob){
        try {
            MathFunctions.dataDivConstant(blob, 255.0);
            Blob means = new Blob(1, 1, 1, 3);
            Blob stds = new Blob(1, 1, 1, 3);
            //mean=[0.485, 0.456, 0.406],std=[0.229, 0.224, 0.225] RGB
            means.setData(new double[][][][]{{{{0.485, 0.456, 0.406}}}});
            stds.setData(new double[][][][]{{{{0.229, 0.224, 0.225}}}});
            BatchNormal2d bn = new BatchNormal2d(3, means, stds);
            return bn.forward(blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Blob resizeImage(File file){
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Object[] objects=ImageUtil.DetResizeForTest(bufferedImage);
            Blob resize =(Blob) objects[0];
            double ratio_h =(double) objects[1];
            double ratio_w =(double) objects[2];
            return resize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
