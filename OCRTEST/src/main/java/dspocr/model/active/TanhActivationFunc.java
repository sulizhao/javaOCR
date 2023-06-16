package dspocr.model.active;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

public class TanhActivationFunc extends Layer implements ActivationFunc{
	public static final String TYPE = "TanhActivationFunc";

	private double tanh(double in){
		double ef = (double) Math.exp(in);
		double efx = (double) Math.exp(-in);
		return (ef-efx)/(ef+efx);
	}
	@Override
	public double active(double in) {
		// TODO Auto-generated method stub
		return tanh(in);
	}

	@Override
	public double diffActive(double in) {
		// TODO Auto-generated method stub
		return (1-tanh(in)*tanh(in));
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
