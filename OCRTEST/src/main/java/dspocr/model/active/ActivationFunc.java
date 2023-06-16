package dspocr.model.active;

public interface ActivationFunc {
	public abstract double active(double in);
	public abstract double diffActive(double in);
	public abstract String getType();

}
