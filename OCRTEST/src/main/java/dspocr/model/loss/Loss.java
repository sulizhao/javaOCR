package dspocr.model.loss;

import dspocr.dataloader.data.Blob;

public abstract class Loss {
	abstract public double loss(Blob label,Blob output);
	abstract public void diff(Blob label,Blob output,Blob diff);
}
