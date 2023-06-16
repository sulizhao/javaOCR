package dspocr.utils;

import dspocr.dataloader.DetLoader.DBProcess;
import dspocr.dataloader.data.Blob;
import dspocr.model.architecture.DetDBModel;

import java.io.File;

public class DetPredict {

    public static void main(String[] args) throws Exception {
        Program.preprocess();
        DetDBModel model = new DetDBModel();
        model.loadModel("det_model.txt");


        model.perdict(new File("D:\\13.jpg"));

        System.out.println("program finish!");
    }
}
