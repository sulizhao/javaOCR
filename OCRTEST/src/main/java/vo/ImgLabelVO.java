package vo;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImgLabelVO {

    private String fileName;

    private List<float[]> polygons;

    private BufferedImage bufferedImage;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<float[]> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<float[]> polygons) {
        this.polygons = polygons;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
}
