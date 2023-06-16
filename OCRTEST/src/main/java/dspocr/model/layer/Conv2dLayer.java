package dspocr.model.layer;


import dspocr.dataloader.data.Blob;
import tools.MathFunctions;

/*
 * 标准卷积
 */

public class Conv2dLayer extends Layer {
    public static final String TYPE = "Conv2dLayer";
    private Blob weight;
    private Blob bias;
    private int inChannel;
    private int outChannel;
    private int kernelSize;
    private int stride;
    private int padding;
    private boolean bias_attr;
    private int group = 1;


    public Conv2dLayer(int inChannel, int outChannel, int kernelSize, int stride, int padding) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.padding = padding;
    }


    public Conv2dLayer(int inChannel, int outChannel, int kernelSize, int stride, int padding, int group, boolean bias_attr) {
        this.inChannel = inChannel;
        this.outChannel = outChannel;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.padding = padding;
        this.group = group;
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
            weight = new Blob(outChannel, inChannel, kernelSize, kernelSize);
            bias = new Blob(outChannel);
            //init params
            MathFunctions.gaussianInitData(weight);
            MathFunctions.constantInitData(bias, 0.001f);
        }
    }

    @Override
    public Blob forward(Blob blob) {
        Blob input = blob;
        Blob output = new Blob(blob.getNumbers(), outChannel, blob.getHeight() / stride, blob.getWidth() / stride);

        //卷积后的结果存贮在output中
//        output.fillValue(0);
        if (inChannel == group
                && inChannel != 1 && outChannel % inChannel == 0) {
            MathFunctions.deepWiseConv2dSame(input, weight, bias, output);
        } else {
            MathFunctions.conv2dBlobSame(input, padding, stride, weight, bias, output);
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
