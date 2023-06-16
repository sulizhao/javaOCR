package dspocr.model.layer;


import dspocr.dataloader.data.Blob;
import tools.MathFunctions;
import tools.Task;

import java.util.Vector;

/*
 * 逆卷积
 */

public class Conv2DTransposeLayer extends Layer {
    public static final String TYPE = "Conv2DTransposeLayer";
    private Blob weight;
    private Blob bias;
    private int inChannel;
    private int outChannel;
    private int kernelSize;
    private int stride;
    private int padding = 0;
    private int group =1;
    private boolean bias_attr;


    public Conv2DTransposeLayer(int inChannel, int outChannel, int kernelSize, int stride, boolean bias_attr) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.bias_attr = bias_attr;

    }


    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return TYPE;
    }

    @Override
    public void prepare() {
        // TODO Auto-generated method stub
        //layerParams.getHeight()表示该层需要提取的特征数量
        if (weight == null && bias == null) {
            weight = new Blob(inChannel * outChannel, kernelSize, kernelSize);
            bias = new Blob(outChannel);
            //init params
            MathFunctions.gaussianInitData(weight);
            MathFunctions.constantInitData(bias, 0.001f);
        }
    }

    @Override
    public Blob forward(Blob blob) {
        Blob output = new Blob(blob.getNumbers(), outChannel, blob.getHeight() * this.stride, blob.getWidth() * this.stride);
//        op_type = 'conv2d_transpose'
//        num_filters = weight.shape[1]
//        if num_channels == groups and num_channels != 1 and num_filters == 1:
//        op_type = 'depthwise_conv2d_transpose'
        if(!(blob.getChannels() == group && blob.getChannels()!=1 && weight.getChannels()==1)){
            //反卷积
            MathFunctions.conv2dBlobSameTranspose(blob, padding, stride, weight, bias, output);
        } else {
            //深度反卷积
            MathFunctions.deepWiseConv2dBlobSameTranspose(blob, padding, stride, weight, bias, output);
        }
        return output;
    }

    public Blob getWeight() {
        return weight;
    }

    public void setWeight(Blob weight) {
        this.weight = weight;
    }

    public Blob getBias() {
        return bias;
    }

    public void setBias(Blob bias) {
        this.bias = bias;
    }
}
