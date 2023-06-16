package dspocr.model.layer;

import dspocr.dataloader.data.Blob;
import dspocr.model.Network;
import dspocr.model.active.ActivationFunc;
import dspocr.modules.BatchNormal2d;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public abstract class Layer{
	protected int id;
	public final Network network = Network.getInstance();
	public ActivationFunc activationFunc;
	public BatchNormal2d bn;
	//BlobParams中的四个参数说明
	//第一个：batch,就是一个批次中有多少个图片
	//第二个：channel,一张图片有多少个通道
	//第三个：图片的高
	//第四个：图片的宽
	private Layer preLayer;
	private Layer nextLayer;
	private Blob out;
	private Blob diffs;
	private List<Layer> layerList;
	protected int batch;
	protected int height;
	protected int width;
	protected int outChannel;

	public Layer(Layer layer){
		if(layer!=null) {
			this.setPreLayer(layer);
			layer.setNextLayer(this);
		}
		this.network.addLayer(this);
		this.out = this.createOutBlob();
		network.getDatas().add(out);
		this.diffs = this.createDiffBlob();
		network.getDiffs().add(diffs);
		this.setId(network.getDatas().size());

	}
	public Layer(){
		this.network.addLayer(this);
	}
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return id;
	}
	public Blob createOutBlob(){
		return null;
	}
	public Blob createDiffBlob(){
		return null;
	}
	
	public void setActivationFunc(ActivationFunc func){
		this.activationFunc = func;
	}
	//类型
	public String getType(){
		return "baseLayer";
	}

	//准备数据
	public void prepare(){}
	
	//前向传播和反向传播
	abstract public Blob forward(Blob blob);
	public void backward(){}
	
	//参数保存和装载
	public void saveModel(ObjectOutputStream out){}
	public void loadModel(ObjectInputStream in){}

	public Layer getPreLayer() {
		return preLayer;
	}

	public void setPreLayer(Layer preLayer) {
		this.preLayer = preLayer;
	}

	public Layer getNextLayer() {
		return nextLayer;
	}

	public void setNextLayer(Layer nextLayer) {
		this.nextLayer = nextLayer;
	}

	public Blob getDiffs() {
		return diffs;
	}

	public void setDiffs(Blob diffs) {
		this.diffs = diffs;
	}

	public int getOutChannel() {
		return outChannel;
	}

	public void setOutChannel(int outChannel) {
		this.outChannel = outChannel;
	}

	public Blob getOut() {
		return out;
	}

	public void setOut(Blob out) {
		this.out = out;
	}


	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public List<Layer> getLayerList() {
		return layerList;
	}

	public void setLayerList(List<Layer> layerList) {
		this.layerList = layerList;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public ActivationFunc getActivationFunc() {
		return activationFunc;
	}

	public BatchNormal2d getBn() {
		return bn;
	}

	public void setBn(BatchNormal2d bn) {
		this.bn = bn;
	}
}