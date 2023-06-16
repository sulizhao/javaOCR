package dspocr.modules;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

public class BatchNormal2d extends Layer {

    /**
     * 归一化 对一张图的归一化
     *
     * @author SXC 2020年9月11日 下午2:00:15
     */

    Blob weight;//默认为weight =1,bias=0
    Blob bias;
    private int batchSize = 1;
    private int channels = 1;
    private Blob _mean;
    private Blob stds;
    private Blob _variance;


    public BatchNormal2d(int numChannel) {//输入图片通道数，也为特征数，此处无用
        this.weight = new Blob(1, 1, 1, numChannel);
        this.bias = new Blob(1, 1, 1, numChannel);
        this.weight.fillValue(1);
        this.bias.fillValue(0);

    }


    public BatchNormal2d(int numChannel, Blob _mean, Blob stds) {//输入图片通道数，也为特征数，此处无用
        this._mean = _mean;
        this.stds = stds;
        this.weight = new Blob(1, 1, 1, numChannel);
        this.bias = new Blob(1, 1, 1, numChannel);
        this.weight.fillValue(1.0);
        this.bias.fillValue(0.0);
    }

    public BatchNormal2d(int numChannel, double weightV, double biasV) {//输入图片通道数，也为特征数，此处无用
        this.weight = new Blob(1, 1, 1, numChannel);
        this.bias = new Blob(1, 1, 1, numChannel);
        this.weight.fillValue(weightV);
        this.bias.fillValue(biasV);
    }

    public double mean(Blob blob) {
        double meanvalue = 0;
        double[][][][] data = blob.getData();
        for (int n = 0; n < blob.getNumbers(); n++) {
            for (int c = 0; c < blob.getChannels(); c++) {
                for (int h = 0; h < blob.getHeight(); h++) {
                    for (int w = 0; w < blob.getWidth(); w++) {
                        meanvalue += data[n][c][h][w];
                    }
                }
            }
        }
        return meanvalue / blob.get4DSize();
    }

    public double var(Blob blob, double meanvalue) {
        double varvalue = 0;
        double[][][][] data = blob.getData();
        for (int n = 0; n < blob.getNumbers(); n++) {
            for (int c = 0; c < blob.getChannels(); c++) {
                for (int h = 0; h < blob.getHeight(); h++) {
                    for (int w = 0; w < blob.getWidth(); w++) {
                        varvalue += Math.pow((data[n][c][h][w] - meanvalue), 2);
                    }
                }
            }
        }
        return varvalue / blob.get4DSize();
    }


    @Override
    public Blob forward(Blob blob) {
        Blob output = new Blob(blob.getNumbers(), blob.getChannels(), blob.getHeight(), blob.getWidth());
        double[][][][] x = blob.getData();
        this.batchSize = blob.getNumbers();
        this.channels = blob.getChannels();
        double[][][][] out = output.getData();
        if (this._mean == null && this._variance == null) {
            this._mean = new Blob(1, 1, 1, channels);
            this._variance = new Blob(1, 1, 1, channels);
            this.stds = new Blob(1, 1, 1, channels);
            for (int c = 0; c < this.channels; c++) {
                Blob eachChannel = new Blob(batchSize, 1, blob.getHeight(), blob.getWidth());
                for (int n = 0; n < batchSize; n++) {
                    eachChannel.getData()[n][0] = x[n][c];
                }
                this._mean.getData()[0][0][0][c] = mean(eachChannel);
                double var = var(eachChannel, this._mean.getData()[0][0][0][c]);
                this._variance.getData()[0][0][0][c] = var;
                double std = Math.pow(var + 1e-5, 0.5);
                this.stds.getData()[0][0][0][c] = std;
            }
        } else {
            boolean caculate_stds = false;
            for (int c = 0; c < this.channels; c++) {
                if (this.stds == null) {
                    caculate_stds = true;
                    this.stds = new Blob(1, 1, 1, channels);
                }
                if(caculate_stds){
                    this.stds.getData()[0][0][0][c] = Math.pow(this._variance.getData()[0][0][0][c] + 1e-5, 0.5);
                }
                for (int n = 0; n < batchSize; n++) {
                    for (int h = 0; h < output.getHeight(); h++) {
                        for (int w = 0; w < output.getWidth(); w++) {
                            double mean = this._mean.getData()[0][0][0][c];
                            if (this.activationFunc != null) {
                                out[n][c][h][w] = activationFunc.active(((x[n][c][h][w] - mean) / this.stds.getData()[0][0][0][c]) * weight.getData()[0][0][0][c] + bias.getData()[0][0][0][c]);
                            } else {
                                out[n][c][h][w] = ((x[n][c][h][w] - mean) / this.stds.getData()[0][0][0][c]) * weight.getData()[0][0][0][c] + bias.getData()[0][0][0][c];
                            }
                        }
                    }
                }

            }
        }
        output.setData(out);
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

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public Blob get_mean() {
        return _mean;
    }

    public void set_mean(Blob _mean) {
        this._mean = _mean;
    }

    public Blob getStds() {
        return stds;
    }

    public void setStds(Blob stds) {
        this.stds = stds;
    }

    public Blob get_variance() {
        return _variance;
    }

    public void set_variance(Blob _variance) {
        this._variance = _variance;
    }

}
