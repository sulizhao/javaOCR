package dspocr.model.architecture;

import dspocr.dataloader.DetLoader.DBProcess;
import dspocr.dataloader.DetLoader.DetLoader;
import dspocr.dataloader.data.Blob;
import dspocr.model.backbone.MobileNetV3;
import dspocr.model.head.DBHead;
import dspocr.model.neck.RseFpn;
import tools.FileOperater;
import tools.ImageUtil;
import tools.LoadModelUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class DetDBModel {

    private String algorithm = "DB";
    private String backboneType = "large";


    private MobileNetV3 backbone;
    private RseFpn neck;
    private DBHead head;

    public DetDBModel() {
        this.backbone = this.buildMobileNetV3Network();
        this.neck = this.buildRseFpnNetwork(backbone.getOutChannelList(), 96, true);
        this.head = this.buildDbHead(neck.getOut_channels(), 50);

    }


    private MobileNetV3 buildMobileNetV3Network() {
        MobileNetV3 mobileNetV3 = new MobileNetV3(backboneType);
        mobileNetV3.build();
        return mobileNetV3;
    }

    private RseFpn buildRseFpnNetwork(List<Integer> outChannelList, int outChannel, boolean shortcut) {
        RseFpn rseFpn = new RseFpn(outChannelList, outChannel, shortcut);
        rseFpn.build();
        return rseFpn;
    }

    private DBHead buildDbHead(int inChannel, int k) {
        DBHead rseFpn = new DBHead(inChannel, k);
        rseFpn.build();
        return rseFpn;
    }


    public void perdict(File imgFile) {
        Blob blob = DBProcess.resizeImage(imgFile);
        Blob blob1 = new Blob(blob, true);
        Blob blobBn = DBProcess.bNImage(blob);
        List<Blob> forward = this.backbone.forward(blobBn);
        Blob forward1 = this.neck.forward(forward);
        Blob forward2 = this.head.forward(forward1);
//        BufferedImage bufferedImage = DetLoader.blob2Image(forward2);


        double[][][][] outputData = forward2.getData();
        double[][][][] data = blob1.getData();
        for(int i =0; i< forward2.getHeight(); i++){
            for(int j =0; j< forward2.getWidth(); j++){
                if(outputData[0][0][i][j]>0){
                    data[0][0][i][j]=255;
                    data[0][1][i][j]=0;
                    data[0][2][i][j]=0;
                }
            }
        }
        BufferedImage destBufferedImage = ImageUtil.blob2Image(blob1);
        try {
            ImageIO.write(destBufferedImage, "jpg", new File("D:/21result.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void saveModel(String name) {

    }

    public void loadModel(String name) {
        try {
            String path = this.getClass().getClassLoader().getResource("").getPath();
//            InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);
            InputStream is = new FileInputStream(new File(path+name));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\t");
                String key = split[0];
                String v = split[1];
                LoadModelUtil.loadByKey(key, v, this);
            }
            bufferedReader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public MobileNetV3 getBackbone() {
        return backbone;
    }

    public void setBackbone(MobileNetV3 backbone) {
        this.backbone = backbone;
    }

    public RseFpn getNeck() {
        return neck;
    }

    public void setNeck(RseFpn neck) {
        this.neck = neck;
    }

    public DBHead getHead() {
        return head;
    }

    public void setHead(DBHead head) {
        this.head = head;
    }
}
