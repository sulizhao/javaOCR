package dspocr.model.active;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

public class SigmodActivationFunc extends Layer implements ActivationFunc {
	public static final String TYPE = "SigmodActivationFunc";
	double threshold = 0.999999;

	@Override
	public double active(double in) {
		// TODO Auto-generated method stub
		double result = 1.0f/(1.0f+Math.exp(-in));
		return result>threshold?result:0;
//		return result;
	}

	@Override
	public double diffActive(double in) {
		// TODO Auto-generated method stub
		double result = 0.0f;
		result = (double) ((Math.exp(-in))/((1+Math.exp(-in))*(1+Math.exp(-in))));
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
