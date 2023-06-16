package dspocr.model.backbone.sublayer;

import dspocr.dataloader.data.Blob;
import dspocr.model.active.HardSwishActivationFunc;
import dspocr.model.active.ReluActivationFunc;
import dspocr.model.layer.Conv2dLayer;
import dspocr.model.layer.Layer;
import dspocr.modules.BatchNormal2d;

public class ConvBNLayer extends Layer {

    private int inchannel;
    private int outchannel;
    private int kernel_size;
    private int stride;
    private int padding;
    private int groups = 1;
    private boolean if_act;
    private Class<? extends Layer> act;

    Conv2dLayer conv;
    BatchNormal2d bn;

    public ConvBNLayer(int inchannel, int outchannel, int kernel_size, int stride, int padding, int groups, boolean if_act, Class<? extends Layer> act) {
        this.inchannel = inchannel;
        this.outchannel = outchannel;
        this.kernel_size = kernel_size;
        this.stride = stride;
        this.padding = padding;
        this.groups = groups;
        this.if_act = if_act;
        this.act = act;
        this.conv = new Conv2dLayer(this.inchannel, this.outchannel, this.kernel_size, this.stride, this.padding, this.groups, false);
        this.bn = new BatchNormal2d(this.outchannel);
    }



    @Override
    public Blob forward(Blob blob) {
        blob = this.conv.forward(blob);
        blob = bn.forward(blob);
        if (this.if_act) {
            if (ReluActivationFunc.class.equals(act)) {
                ReluActivationFunc activationFunc = new ReluActivationFunc();
                blob = activationFunc.forward(blob);
            } else if (HardSwishActivationFunc.class.equals(act)) {
                HardSwishActivationFunc activationFunc = new HardSwishActivationFunc();
                blob = activationFunc.forward(blob);
            }
        }

        return blob;
    }

    public Conv2dLayer getConv() {
        return conv;
    }

    public void setConv(Conv2dLayer conv) {
        this.conv = conv;
    }

    public BatchNormal2d getBn() {
        return bn;
    }

    public void setBn(BatchNormal2d bn) {
        this.bn = bn;
    }
}