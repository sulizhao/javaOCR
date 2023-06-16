package dspocr.model.active;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

/**
 * x * F.relu6(x + 3) / 6
 */
public class HardSwishActivationFunc extends  Layer implements ActivationFunc{
	public static final String TYPE = "HardSwishActivationFunc";

	public HardSwishActivationFunc(){
	}

	@Override
	public double active(double in) {
		// TODO Auto-generated method stub
		return in*Math.min(6,Math.max(0, in+3))/6;
	}

	@Override
	public double diffActive(double in) {
		// TODO Auto-generated method stub
		double result = in<=-3 ? 0.0f:in>3?0:0.5f+in/3;
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
