package dspocr.model.active;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

/**
 * 公式：min(6,max(0,x))
 * 导数：
 */
public class Relu6ActivationFunc extends Layer implements ActivationFunc{
	public static final String TYPE = "Relu6ActivationFunc";

	public Relu6ActivationFunc(Layer layer){
		super(layer);
	}
	public Relu6ActivationFunc(){

	}

	@Override
	public double active(double in) {
		// TODO Auto-generated method stub
		return Math.min(6,Math.max(0, in));
	}

	@Override
	public double diffActive(double in) {
		// TODO Auto-generated method stub
		double result = in<=0 ? 0.0f:in>6?0:1.0f;
		return result;
	}
	
	@Override
	public String getType(){
		return TYPE;
	}

	@Override
	public Blob forward(Blob blob) {
		double[][][][] data = blob.getData();
		for (int n = 0; n < blob.getNumbers(); n++) {
			for (int c = 0; c < blob.getChannels(); c++) {
				for (int h = 0; h < blob.getHeight(); h++) {
					for (int w = 0; w < blob.getWidth(); w++) {
						data[n][c][h][w] =active(data[n][c][h][w]);
					}
				}
			}
		}
		return blob;
	}
}
