package dspocr.model;

import dspocr.dataloader.data.Blob;
import dspocr.model.layer.Layer;
import dspocr.model.loss.Loss;
import dspocr.model.optimizer.Optimizer;

import java.util.ArrayList;
import java.util.List;


public class Network {
	public static String MODEL_BEGIN = "BEGIN";
	public static String MODEL_END = "END";
	private List<Blob> datas = new ArrayList<Blob>();
	private List<Blob> diffs = new ArrayList<Blob>();
	private List<Layer> layers = new ArrayList<Layer>();
	private Loss loss;
	private Optimizer optimizer;
	private int batch = 1;
	private int threadNum = 4;
	private double lrDecay = 0.8f;
	private int imgWidth;
	private int imgHeight;

	private static Network network;
	public static Network getInstance(){
		synchronized (Network.class){
			if(network==null) {
				network = new Network();
				network.setThreadNum(50);
				network.setBatch(1);
				network.setLrDecay(0.9f);
			}
			return network;
		}
	}
	
	private Network(){
	}

	public int getThreadNum() {
		return threadNum;
	}
	
	public void setThreadNum(int num) {
		threadNum = num;
	}
	/*
	 *添加创建的层
	 */
	public void addLayer(Layer layer){
		layers.add(layer);
	}
	
	/*
	 * 获取datas
	 */
	public List<Blob> getDatas(){
		return datas;
	}
	/*
	 * 获取diffs
	 */
	public List<Blob> getDiffs(){
		return diffs;
	}
	/*
	 * 获取Layers
	 */
	public List<Layer> getLayers(){
		return layers;
	}
	
	public double getLrDecay() {
		return lrDecay;
	}

	public void setLrDecay(double decay) {
		this.lrDecay = decay;
	}
	
	public void setLoss(Loss loss){
		this.loss = loss;
	}

	
	public void setBatch(int batch){
		this.batch = batch;
	}
	
	public int getBatch(){
		return this.batch;
	}
	
	public void setOptimizer(Optimizer optimizer){
		this.optimizer = optimizer;
	}
	
	public void updateW(Blob params,Blob gradient) {
		optimizer.updateW(params, gradient);
	}
	
	public void updateB(Blob params,Blob gradient) {
		optimizer.updateW(params, gradient);
	}
	


	private int getMaxIndexInArray(double[] data){
		int maxIndex = 0;
		double maxValue = 0;
		for(int i=0;i<data.length;i++){
			if(maxValue<data[i]){
				maxValue = data[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	private int[] getBatchOutputLabel(double[] data){
		int[] outLabels = new int[getDatas().get(getDatas().size()-1).getHeight()];
		int outDataSize = getDatas().get(getDatas().size()-1).getWidth();
		for(int n=0;n<outLabels.length;n++){
			int maxIndex = 0;
			double maxValue = 0;
			for(int i=0;i<outDataSize;i++){
				if(maxValue<data[n*outDataSize+i]){
					maxValue = data[n*outDataSize+i];
					maxIndex = i;
				}	
			}
			outLabels[n] = maxIndex;
		}
		return outLabels;
	}
	


	public int getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}
}
