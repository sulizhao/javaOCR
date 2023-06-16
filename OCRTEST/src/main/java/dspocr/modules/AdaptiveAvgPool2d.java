package dspocr.modules;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

public class AdaptiveAvgPool2d extends Layer {

    /**
     * 归一化 对一张图的归一化
     *
     * @author SXC 2020年9月11日 下午2:00:15
     */
    private int outSize=1;


    public AdaptiveAvgPool2d() {//输入图片通道数，也为特征数，此处无用
    }

    public AdaptiveAvgPool2d(int outSize) {//输入图片通道数，也为特征数，此处无用
        this.outSize=outSize;
    }

    @Override
    public Blob forward(Blob blob){
        Blob input = blob;
        int batchSize = input.getNumbers();
        int channels = input.getChannels();
        int height = input.getHeight();
        int width = input.getWidth();
        Blob output = new Blob(batchSize,channels,outSize,outSize);
        double[][][][] out = output.getData();
        for(int b =0; b< batchSize; b++){
            for(int c=0; c<channels;c++) {
                for(int h =0; h< outSize; h++){
                    for(int w=0; w<outSize;w++) {
                        out[b][c][h][w]=pool2d(input, b, c,h,w, height, width);
                    }
                }
            }
        }
        return output;
    }

    private double pool2d(Blob input, int b, int c, int h, int w, int height, int width){
        double[][][][] data = input.getData();
        int wstart = (int)Math.floor(w*width/outSize); //向下取整
        int wend = (int)Math.ceil((w+1)*width/outSize); //向上取整
        int hstart = (int)Math.floor(h*height/outSize); //向下取整
        int hend = (int)Math.ceil((h+1)*height/outSize); //向上取整
        double sum=0.0;
        for(int i =hstart;i<hend; i++){
            for(int j =wstart;j<wend; j++){
                sum+=data[b][c][i][j];
            }
        }
        return sum/((wend-wstart)*(hend-hstart));
    }
}
