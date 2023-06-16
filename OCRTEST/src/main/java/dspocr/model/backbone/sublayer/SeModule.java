package dspocr.model.backbone.sublayer;

import dspocr.dataloader.data.Blob;
import dspocr.model.active.ActivationFunc;
import dspocr.model.active.HardSigmodActivationFunc;
import dspocr.model.active.HardSwishActivationFunc;
import dspocr.model.active.ReluActivationFunc;
import dspocr.model.layer.Conv2dLayer;
import dspocr.model.layer.Layer;
import dspocr.modules.AdaptiveAvgPool2d;

public class SeModule extends Layer {
    AdaptiveAvgPool2d avg_pool;
    Conv2dLayer conv1;
    Conv2dLayer conv2;
    public SeModule(int in_channels) {
        int reduction = 4;
        this.avg_pool = new AdaptiveAvgPool2d();
        this.conv1 = new Conv2dLayer(in_channels, in_channels / reduction, 1, 1, 0);
        this.conv2 = new Conv2dLayer(in_channels / reduction, in_channels, 1, 1, 0);

    }

    @Override
    public Blob forward(Blob blob) {
        Blob outblob = avg_pool.forward(blob);
        outblob = conv1.forward(outblob);
        ReluActivationFunc relu1 = new ReluActivationFunc();
        outblob = relu1.forward(outblob);
        outblob = conv2.forward(outblob);
        HardSigmodActivationFunc relu2 = new HardSigmodActivationFunc(0.2, 0.5);
        outblob = relu2.forward(outblob);
        Blob muti = blob.muti(outblob);
        return muti;
    }

    public AdaptiveAvgPool2d getAvg_pool() {
        return avg_pool;
    }

    public void setAvg_pool(AdaptiveAvgPool2d avg_pool) {
        this.avg_pool = avg_pool;
    }

    public Conv2dLayer getConv1() {
        return conv1;
    }

    public void setConv1(Conv2dLayer conv1) {
        this.conv1 = conv1;
    }

    public Conv2dLayer getConv2() {
        return conv2;
    }

    public void setConv2(Conv2dLayer conv2) {
        this.conv2 = conv2;
    }
}

