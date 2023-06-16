package dspocr.model.neck.sublayer;

import dspocr.dataloader.data.Blob;
import dspocr.model.backbone.sublayer.SeModule;
import dspocr.model.layer.Conv2dLayer;
import dspocr.model.layer.Layer;

public class RSELayer extends Layer {

    Conv2dLayer in_conv;
    SeModule se_block;
    boolean shortcut;

    public RSELayer(int inChannel, int outChannel, int kernel) {
        new RSELayer(inChannel, outChannel, kernel, true);
    }
    public RSELayer(int inChannel, int outChannel, int kernel, boolean shortcut) {
        this.in_conv = new Conv2dLayer(inChannel, outChannel, kernel, 1, kernel / 2, 1, false);
        this.se_block = new SeModule(outChannel);
        this.shortcut = shortcut;

    }

    @Override
    public Blob forward(Blob blob) {
        blob = this.in_conv.forward(blob);
        if(this.shortcut){
            Blob out = this.se_block.forward(blob);
            blob = blob.sum(out);
        } else {
            blob = this.se_block.forward(blob);
        }
        return blob;
    }

    public Conv2dLayer getIn_conv() {
        return in_conv;
    }

    public void setIn_conv(Conv2dLayer in_conv) {
        this.in_conv = in_conv;
    }

    public SeModule getSe_block() {
        return se_block;
    }

    public void setSe_block(SeModule se_block) {
        this.se_block = se_block;
    }

}
