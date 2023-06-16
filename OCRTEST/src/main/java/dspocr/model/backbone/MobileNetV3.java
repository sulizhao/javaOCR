package dspocr.model.backbone;

import dspocr.dataloader.data.Blob;
import dspocr.model.BaseModel;
import dspocr.model.active.HardSwishActivationFunc;
import dspocr.model.active.ReluActivationFunc;
import dspocr.model.backbone.sublayer.ConvBNLayer;
import dspocr.model.backbone.sublayer.ResidualUnit;
import dspocr.model.layer.Conv2dLayer;
import dspocr.model.layer.Layer;
import dspocr.modules.BatchNormal2d;
import tools.Environment;

import java.util.ArrayList;
import java.util.List;

public class MobileNetV3 extends BaseModel {
    private List<Block> blockList = new ArrayList<>();
    private int cls_ch_squeeze;
    public static int inchannel=3;
    private String modelName;
    private ConvBNLayer conv;
    private List<List<Layer>> stage = new ArrayList<>();
    private List<Integer> outChannelList = new ArrayList<>();

    public MobileNetV3(String modelName) {
        this.modelName = modelName;
        if ("large".equalsIgnoreCase(modelName)) {
            // k, exp, c,  se,     nl,  s,
            blockList.add(new Block(3, 16, 16, 16, false, ReluActivationFunc.class, 1));
            blockList.add(new Block(3, 16, 64, 24, false, ReluActivationFunc.class, 2));
            blockList.add(new Block(3, 24, 72, 24, false, ReluActivationFunc.class, 1));
            blockList.add(new Block(5, 24, 72, 40, true, ReluActivationFunc.class, 2));
            blockList.add(new Block(5, 40, 120, 40, true, ReluActivationFunc.class, 1));
            blockList.add(new Block(5, 40, 120, 40, true, ReluActivationFunc.class, 1));
            blockList.add(new Block(3, 40, 240, 80, false, HardSwishActivationFunc.class, 2));
            blockList.add(new Block(3, 80, 200, 80, false, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(3, 80, 184, 80, false, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(3, 80, 184, 80, false, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(3, 80, 480, 112, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(3, 112, 672, 112, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 112, 672, 160, true, HardSwishActivationFunc.class, 2));
            blockList.add(new Block(5, 160, 960, 160, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 160, 960, 160, true, HardSwishActivationFunc.class, 1));

            this.cls_ch_squeeze = 960;
        } else if ("small".equalsIgnoreCase(modelName)) {
            blockList.add(new Block(3, 16, 16, 16, true, ReluActivationFunc.class, 2));
            blockList.add(new Block(3, 16, 72, 24, false, ReluActivationFunc.class, 2));
            blockList.add(new Block(3, 24, 88, 24, false, ReluActivationFunc.class, 1));
            blockList.add(new Block(5, 24, 96, 40, true, HardSwishActivationFunc.class, 2));
            blockList.add(new Block(5, 40, 240, 40, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 40, 240, 40, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 40, 120, 48, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 48, 144, 48, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 48, 288, 96, true, HardSwishActivationFunc.class, 2));
            blockList.add(new Block(5, 96, 576, 96, true, HardSwishActivationFunc.class, 1));
            blockList.add(new Block(5, 96, 576, 96, true, HardSwishActivationFunc.class, 1));
            this.cls_ch_squeeze = 576;
        }
    }

    public void build() {
        int inplanes = 16;
        this.conv = new ConvBNLayer(this.inchannel, makeDivisible(Environment.scale * inplanes), 3, 2, 1, 1, true, HardSwishActivationFunc.class);
        inplanes = makeDivisible(Environment.scale * inplanes);
        List<Layer> list = new ArrayList<>();
        for (int i = 0; i < blockList.size(); i++) {
            int start_idx = "large".equals(modelName) ? 2 : 0;
            Block block = blockList.get(i);
            boolean use_se = block.getSe() && !Environment.disable_Se;
            int step = block.getStep();
            if (step == 2 && i > start_idx) {
                outChannelList.add(inplanes);
                stage.add(list);
                list = new ArrayList<>();
            }
            list.add(new ResidualUnit(inplanes,
                    makeDivisible(Environment.scale * block.getExpandSize()),
                    makeDivisible(Environment.scale * block.getOutputChannel()),
                    block.getKernel(),
                    block.getStep(),
                    use_se,
                    block.getRelu()));
            inplanes = makeDivisible(Environment.scale * block.getOutputChannel());
        }
        list.add(new ConvBNLayer(inplanes,
                makeDivisible(Environment.scale * cls_ch_squeeze),
                1,
                1,
                0,
                1,
                true, HardSwishActivationFunc.class));
        this.stage.add(list);
        this.outChannelList.add(makeDivisible(Environment.scale * cls_ch_squeeze));
    }

    public List<Blob> forward(Blob blob){
        blob = this.conv.forward(blob);
        List<Blob> result = new ArrayList<>();
        for(int i =0; i< this.stage.size(); i++){
            List<Layer> layers = this.stage.get(i);
            for(Layer layer : layers) {
                blob = layer.forward(blob);
            }
            result.add(blob);

        }
        return result;
    }

    private class Block {
        int kernel;
        int inchannel;
        int expandSize;
        int outputChannel;
        Boolean se;
        Class<? extends Layer> relu;
        int step;

        public Block(int kernel, int inchannel, int expandSize, int outputChannel, Boolean se, Class<? extends Layer> relu, int step) {
            this.kernel = kernel;
            this.inchannel = inchannel;
            this.expandSize = expandSize;
            this.outputChannel = outputChannel;
            this.se = se;
            this.relu = relu;
            this.step = step;
        }

        public int getKernel() {
            return kernel;
        }

        public void setKernel(int kernel) {
            this.kernel = kernel;
        }

        public int getExpandSize() {
            return expandSize;
        }

        public void setExpandSize(int expandSize) {
            this.expandSize = expandSize;
        }

        public int getOutputChannel() {
            return outputChannel;
        }

        public void setOutputChannel(int outputChannel) {
            this.outputChannel = outputChannel;
        }

        public Boolean getSe() {
            return se;
        }

        public void setSe(Boolean se) {
            this.se = se;
        }

        public Class<? extends Layer> getRelu() {
            return relu;
        }

        public void setRelu(Class<? extends Layer> relu) {
            this.relu = relu;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getInchannel() {
            return inchannel;
        }

        public void setInchannel(int inchannel) {
            this.inchannel = inchannel;
        }
    }

    /**
     * # ------------------------------------------------------#
     * #   这个函数的目的是确保Channel个数能被8整除。
     * #   离它最近的8的倍数
     * #	很多嵌入式设备做优化时都采用这个准则
     * # ------------------------------------------------------#
     *
     * @param doubleInput
     * @return
     */
    private int makeDivisible(double doubleInput) {
        int input = (int)Math.round(doubleInput);
        int divisor = 8;
        int new_v = Math.max(divisor, (input + divisor / 2) / divisor * divisor);
        if (new_v < 0.9 * input) {
            new_v += divisor;
        }
        return new_v;
    }


    public int getCls_ch_squeeze() {
        return cls_ch_squeeze;
    }

    public void setCls_ch_squeeze(int cls_ch_squeeze) {
        this.cls_ch_squeeze = cls_ch_squeeze;
    }

    public List<List<Layer>> getStage() {
        return stage;
    }

    public void setStage(List<List<Layer>> stage) {
        this.stage = stage;
    }

    public List<Integer> getOutChannelList() {
        return outChannelList;
    }

    public void setOutChannelList(List<Integer> outChannelList) {
        this.outChannelList = outChannelList;
    }

    public ConvBNLayer getConv() {
        return conv;
    }

    public void setConv(ConvBNLayer conv) {
        this.conv = conv;
    }
    
    
}
