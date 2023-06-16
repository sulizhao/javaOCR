package dspocr.dataloader.DetLoader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dspocr.dataloader.data.Blob;
import dspocr.dataloader.data.DigitImage;
import tools.FileOperater;
import tools.ImageUtil;
import tools.Task;
import tools.ThreadPoolManager;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentSampleModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class DetLoader {

    private String imgDirPath = "";

    private String labelDirPath = "";

    private int batchSize = 1;

    public Boolean useMemory = false;

    public List<DigitImage> digitImageLists;


    public DetLoader(String imgDirPath, String labelDirPath, Boolean useMemory) {
        this.imgDirPath = imgDirPath;
        this.labelDirPath = labelDirPath;
        this.useMemory = useMemory;
        this.batchSize = 1;
        this.digitImageLists = loadLabel(labelDirPath);
    }

    public DetLoader(String imgDirPath, String labelDirPath) {
        this.imgDirPath = imgDirPath;
        this.labelDirPath = labelDirPath;
        this.useMemory = false;
        this.batchSize = 1;
        this.digitImageLists = loadLabel(labelDirPath);
    }

    private List<DigitImage> loadLabel(String labelDirPath) {
        List<DigitImage> digitImageLists = new ArrayList<>();
        File file = new File(labelDirPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File labelFile : files) {
                parseLabelFile(labelFile, digitImageLists);
            }
        } else if (file.exists() && file.isFile()) {
            parseLabelFile(file, digitImageLists);
        } else {
            throw new RuntimeException("Label 文件不存在！");
        }
        return digitImageLists;
    }

    private void parseLabelFile(File file, List<DigitImage> digitImageLists) {
        List<String> readLabelFile = FileOperater.readLabelFile(file, true);
        if (readLabelFile.size() > 0) {
            for (String line : readLabelFile) {
                String[] split = line.split("\t");
                if (split.length == 2) {
                    String imgPath = split[0];
                    String jsonPolys = split[1];
                    String imageName = this.imgDirPath + "\\" + imgPath.substring(imgPath.lastIndexOf("/") + 1);
                    File imgFile = new File(imageName);
                    if (imgFile.exists()) {
                        DigitImage digitImage = new DigitImage();
                        digitImage.setFileName(imageName);
                        JSONArray objects = JSONObject.parseArray(jsonPolys);
                        Blob polygons = new Blob(objects.size(), 4, 2);
                        double[][][][] polygonData = polygons.getData();
                        List<String> texts = new ArrayList<>();
                        for (int i = 0; i < objects.size(); i++) {
                            JSONObject jsonObject = objects.getJSONObject(i);
                            String transcription = jsonObject.getString("transcription");
                            String points = jsonObject.getString("points");
                            JSONArray objects1 = JSONArray.parseArray(points);
                            for (int j = 0; j < objects1.size(); j++) {
                                JSONArray jsonArray = objects1.getJSONArray(j);
                                polygonData[0][i][j][0] = Float.parseFloat(jsonArray.getString(0));
                                polygonData[0][i][j][1] = Float.parseFloat(jsonArray.getString(1));
                            }
                            texts.add(transcription);
                        }
                        digitImage.setPolygons(polygons);
                        digitImage.setTexts(texts);
                        digitImageLists.add(digitImage);
                    }
                }
            }
            if (this.useMemory) {
                loadimg(digitImageLists);
            }
        } else {
            throw new RuntimeException("Label 文件内容为空！");
        }
    }


    /**
     * @param image
     * @param bandOffset 用于判断通道顺序
     * @return
     */

    private static boolean equalBandOffsetWith3Byte(BufferedImage image, int[] bandOffset) {

        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            if (image.getData().getSampleModel() instanceof ComponentSampleModel) {
                ComponentSampleModel sampleModel = (ComponentSampleModel) image.getData().getSampleModel();
                if (Arrays.equals(sampleModel.getBandOffsets(), bandOffset)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断图像是否为BGR格式
     *
     * @return
     */

    public static boolean isBGR3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{0, 1, 2});
    }

    /**
     * 判断图像是否为RGB格式
     *
     * @return
     */

    public static boolean isRGB3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{2, 1, 0});

    }




    private static void loadimg(List<DigitImage> digitImageLists) {
        Vector<Task<Object>> workers = new Vector<Task<Object>>();
        for (int n = 0; n < digitImageLists.size(); n++) {
            workers.add(new Task<Object>(n,0) {
                @Override
                public Object call() throws Exception {
                    DigitImage digitImage = digitImageLists.get(n);
                    Blob imgBlob = ImageUtil.imgFile2Blob(new File(digitImage.getFileName()));
                    digitImage.setImageData(imgBlob);
                    return null;
                }
            });
        }
        ThreadPoolManager.getInstance(4).dispatchTask(workers);

    }
}




