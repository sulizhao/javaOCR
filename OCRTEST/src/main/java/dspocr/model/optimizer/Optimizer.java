package dspocr.model.optimizer;

import dspocr.dataloader.data.Blob;

public abstract class Optimizer {
	protected double lr = 0.0f;
	protected double lamda = 0.0f;
	public static enum GMode{
		NONE,
		L1,
		L2
	}
	GMode mode;
	public Optimizer(double lr){
		this.lr = lr;
		this.mode = GMode.NONE;
	}
	
	
	public Optimizer(double lr,GMode mode,double lamda){
		this.lr = lr;
		this.lamda = lamda;
		this.mode = mode;
	}
	public abstract void updateW(Blob params,Blob gradient);
	public abstract void updateB(Blob params,Blob gradient);
	public void setLr(double lr){
		this.lr = lr;
	}
	public double getLr(){
		return this.lr;
	}

}
