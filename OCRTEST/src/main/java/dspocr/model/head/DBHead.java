package dspocr.model.head;

import dspocr.dataloader.data.Blob;
import dspocr.model.BaseModel;
import dspocr.model.head.sublayer.Head;
import dspocr.model.layer.Layer;
import dspocr.model.neck.sublayer.RSELayer;
import tools.MathFunctions;

import java.util.ArrayList;
import java.util.List;

public class DBHead extends BaseModel {

    Head binarize;
    Head thresh;

    public DBHead(int outChannel, int k){
        this.binarize = new Head(outChannel);
        this.thresh = new Head(outChannel);
    }

    public void build(){

    }
    public Blob forward(Blob blob){
        blob = this.binarize.forward(blob);
        return blob;
    }

    public Head getBinarize() {
        return binarize;
    }

    public void setBinarize(Head binarize) {
        this.binarize = binarize;
    }

    public Head getThresh() {
        return thresh;
    }

    public void setThresh(Head thresh) {
        this.thresh = thresh;
    }
}
