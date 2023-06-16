package dspocr.model.active;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;

/**
 * out=max(0,min(1,slope*x+offset))
 */
public class HardSigmodActivationFunc extends Layer implements ActivationFunc {
	public static final String TYPE = "HardSigmodActivationFunc";

	double slope=0.2;
	double offset=0.5;

	public HardSigmodActivationFunc(){
	}
	public HardSigmodActivationFunc(double slope, double offset){
		this.slope=slope;
		this.offset=offset;
	}

	@Override
	public double active(double in) {
		// TODO Auto-generated method stub
		double result = 0;
		result = Math.max(0,Math.min(1,slope*in+offset));
		return result;
	}

	@Override
	public double diffActive(double in) {
		// TODO Auto-generated method stub
		double result = 0.0f;
		result = in<=-offset/slope||in>=-offset/slope?0:slope;
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
