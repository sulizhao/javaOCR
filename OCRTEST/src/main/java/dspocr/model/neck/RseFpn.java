package dspocr.model.neck;

import dspocr.dataloader.data.Blob;
import dspocr.model.BaseModel;
import dspocr.model.layer.Layer;
import dspocr.model.neck.sublayer.RSELayer;
import tools.MathFunctions;

import java.util.ArrayList;
import java.util.List;

public class RseFpn extends BaseModel {

    List<Layer>ins_conv = new ArrayList();
    List<Layer>inp_conv = new ArrayList();
    int out_channels;

    public RseFpn(List<Integer> inChannelList, int outChannel, boolean shortcut){
        for(int i =0; i< inChannelList.size(); i++){
            this.ins_conv.add(new RSELayer(inChannelList.get(i), outChannel, 1, shortcut));
            this.inp_conv.add(new RSELayer(outChannel, outChannel/4, 3, shortcut));
        }
        this.out_channels=outChannel;
    }

    public void build(){

    }
    public Blob forward(List<Blob> blobList){
        Blob c2 = blobList.get(0);
        Blob c3 = blobList.get(1);
        Blob c4 = blobList.get(2);
        Blob c5 = blobList.get(3);

        Blob in5 = this.ins_conv.get(3).forward(c5);
        Blob in4 = this.ins_conv.get(2).forward(c4);
        Blob in3 = this.ins_conv.get(1).forward(c3);
        Blob in2 = this.ins_conv.get(0).forward(c2);

        Blob out4 = in4.sum(MathFunctions.upsample(in5, 2, "nearest"));
        Blob out3 = in3.sum(MathFunctions.upsample(out4, 2, "nearest"));
        Blob out2 = in2.sum(MathFunctions.upsample(out3, 2, "nearest"));

        Blob p5 = this.inp_conv.get(3).forward(in5);
        Blob p4 = this.inp_conv.get(2).forward(out4);
        Blob p3 = this.inp_conv.get(1).forward(out3);
        Blob p2 = this.inp_conv.get(0).forward(out2);

        p5 = MathFunctions.upsample(p5, 8, "nearest");
        p4 = MathFunctions.upsample(p4, 4, "nearest");
        p3 = MathFunctions.upsample(p3, 2, "nearest");

        Blob fuse = MathFunctions.concat(p5, MathFunctions.concat(p4, MathFunctions.concat(p3, p2, 1), 1), 1);
        return fuse;
    }

    public int getOut_channels() {
        return out_channels;
    }

    public void setOut_channels(int out_channels) {
        this.out_channels = out_channels;
    }

    public List<Layer> getIns_conv() {
        return ins_conv;
    }

    public void setIns_conv(List<Layer> ins_conv) {
        this.ins_conv = ins_conv;
    }

    public List<Layer> getInp_conv() {
        return inp_conv;
    }

    public void setInp_conv(List<Layer> inp_conv) {
        this.inp_conv = inp_conv;
    }
}
