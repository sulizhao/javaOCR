import net.sourceforge.tess4j.Tesseract;

import java.io.File;

public class Tess4JTest {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println(readStr("D:\\data\\ocr\\sfz.jpeg"));
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }


    public static String readStr(String path) throws Exception {
        File file = new File(path);
        Tesseract instance = new Tesseract();
        //设置 tessdata 文件夹 的本地路径
        instance.setDatapath("D:\\Program Files\\Tesseract-OCR\\tessdata");
        //设置需要使用的训练集，不设置则默认为eng
        instance.setLanguage("chi_sim");
        return instance.doOCR(file);
    }
}
