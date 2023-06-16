package dspocr.model.backbone.sublayer;

import dspocr.dataloader.data.Blob;
import dspocr.model.active.ActivationFunc;
import dspocr.model.layer.Layer;

public class ResidualUnit extends Layer {
    boolean if_shortcut;
    boolean if_use_se;
    ConvBNLayer expand_conv;
    ConvBNLayer bottleneck_conv;
    SeModule mid_se;
    ConvBNLayer linear_conv;

    public ResidualUnit(int in_channels,
                        int mid_channels,
                        int out_channels,
                        int kernel_size,
                        int stride,
                        boolean use_se,
                        Class<? extends Layer> act) {

        this.if_shortcut = (stride == 1) && in_channels == out_channels;
        this.if_use_se = use_se;
        this.expand_conv = new ConvBNLayer(in_channels, mid_channels, 1, 1, 0, 1, true, act);
        this.bottleneck_conv = new ConvBNLayer(mid_channels, mid_channels, kernel_size, 1, (kernel_size - 1) / 2, mid_channels, true, act);
        if(this.if_use_se){
            this.mid_se = new SeModule(mid_channels);
        }
        this.linear_conv = new ConvBNLayer(mid_channels, out_channels, 1, stride, 0, 1, false, null);
    }


    @Override
    public Blob forward(Blob blob) {
        Blob outBlob = this.expand_conv.forward(blob);
        outBlob = this.bottleneck_conv.forward(outBlob);
        if (this.if_use_se){
            outBlob = this.mid_se.forward(outBlob);
        }
        outBlob = this.linear_conv.forward(outBlob);
        if(this.if_shortcut){
            outBlob = outBlob.sum(blob);
        }
        return outBlob;
    }

    public ConvBNLayer getExpand_conv() {
        return expand_conv;
    }

    public void setExpand_conv(ConvBNLayer expand_conv) {
        this.expand_conv = expand_conv;
    }

    public ConvBNLayer getBottleneck_conv() {
        return bottleneck_conv;
    }

    public void setBottleneck_conv(ConvBNLayer bottleneck_conv) {
        this.bottleneck_conv = bottleneck_conv;
    }

    public SeModule getMid_se() {
        return mid_se;
    }

    public void setMid_se(SeModule mid_se) {
        this.mid_se = mid_se;
    }

    public ConvBNLayer getLinear_conv() {
        return linear_conv;
    }

    public void setLinear_conv(ConvBNLayer linear_conv) {
        this.linear_conv = linear_conv;
    }
}

