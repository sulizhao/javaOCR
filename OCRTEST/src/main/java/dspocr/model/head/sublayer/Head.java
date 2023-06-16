package dspocr.model.head.sublayer;

import dspocr.dataloader.data.Blob;
import dspocr.model.active.ReluActivationFunc;
import dspocr.model.active.SigmodActivationFunc;
import dspocr.model.backbone.sublayer.SeModule;
import dspocr.model.layer.Conv2DTransposeLayer;
import dspocr.model.layer.Conv2dLayer;
import dspocr.model.layer.Layer;
import dspocr.modules.BatchNormal2d;

public class Head extends Layer {

    Conv2dLayer conv1;
    BatchNormal2d conv_bn1;
    Conv2DTransposeLayer conv2;
    BatchNormal2d conv_bn2;
    Conv2DTransposeLayer conv3;

    public Head(int inChannel){
        int[] kernel_sizes = new int[]{3,2,2};
        this.conv1 = new Conv2dLayer(inChannel, inChannel/4,  kernel_sizes[0], 1, kernel_sizes[0]/2, 1, false);
        this.conv_bn1 = new BatchNormal2d(inChannel/4,1,1e-4);
        conv_bn1.setActivationFunc(new ReluActivationFunc());
        this.conv2 = new Conv2DTransposeLayer(inChannel/4, inChannel/4,  kernel_sizes[1],    2,false);
        this.conv_bn2 = new BatchNormal2d(inChannel/4,1,1e-4);
        conv_bn2.setActivationFunc(new ReluActivationFunc());
        this.conv3 = new Conv2DTransposeLayer(inChannel/4, 1,  kernel_sizes[2],    2,false);
    }

    @Override
    public Blob forward(Blob blob) {
        blob = this.conv1.forward(blob);
        blob = this.conv_bn1.forward(blob);
        blob = this.conv2.forward(blob);
        blob = this.conv_bn2.forward(blob);
        blob = this.conv3.forward(blob);
        SigmodActivationFunc act = new SigmodActivationFunc();
        blob = act.forward(blob);
        return blob;
    }

    public Conv2dLayer getConv1() {
        return conv1;
    }

    public void setConv1(Conv2dLayer conv1) {
        this.conv1 = conv1;
    }

    public BatchNormal2d getConv_bn1() {
        return conv_bn1;
    }

    public void setConv_bn1(BatchNormal2d conv_bn1) {
        this.conv_bn1 = conv_bn1;
    }

    public Conv2DTransposeLayer getConv2() {
        return conv2;
    }

    public void setConv2(Conv2DTransposeLayer conv2) {
        this.conv2 = conv2;
    }

    public BatchNormal2d getConv_bn2() {
        return conv_bn2;
    }

    public void setConv_bn2(BatchNormal2d conv_bn2) {
        this.conv_bn2 = conv_bn2;
    }

    public Conv2DTransposeLayer getConv3() {
        return conv3;
    }

    public void setConv3(Conv2DTransposeLayer conv3) {
        this.conv3 = conv3;
    }
}
