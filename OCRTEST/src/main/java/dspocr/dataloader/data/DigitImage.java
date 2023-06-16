package dspocr.dataloader.data;

import java.util.List;

public class DigitImage {

	private String fileName;
	public List<String> texts;
	public Blob polygons;
	public Blob imageData;


	public DigitImage(){

	}
	public DigitImage(Blob polygons, Blob imageData)
	{
		this.polygons=polygons;
		this.imageData=imageData;
	}  
	public DigitImage(Blob polygons, Blob imageData, List<String> texts)
	{
		this.polygons=polygons;
		this.imageData=imageData;
		this.texts=texts;
	}
	public DigitImage(Blob polygons, Blob imageData, List<String> texts, String fileName)
	{
		this.polygons=polygons;
		this.imageData=imageData;
		this.texts=texts;
		this.fileName=fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getTexts() {
		return texts;
	}

	public void setTexts(List<String> texts) {
		this.texts = texts;
	}

	public Blob getPolygons() {
		return polygons;
	}

	public void setPolygons(Blob polygons) {
		this.polygons = polygons;
	}

	public Blob getImageData() {
		return imageData;
	}

	public void setImageData(Blob imageData) {
		this.imageData = imageData;
	}
}
