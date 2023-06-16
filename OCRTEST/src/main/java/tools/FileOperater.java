package tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class FileOperater {

    public static List<String> readLabelFile(File file, boolean shuffle) {
        List list  = new ArrayList();
        try{
            InputStream is = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line=null;
            while((line=bufferedReader.readLine())!=null) {
                list.add(line);
            }
            bufferedReader.close();
            is.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        if(shuffle) {
            Collections.shuffle(list);
        }
        return list;
    }

    public static BufferedImage readFromBase64(String base64Str){
        try {
            byte[] decode = Base64.getDecoder().decode(base64Str);
            ByteArrayInputStream in = new ByteArrayInputStream(decode);    //将b作为输入流；
            return ImageIO.read(in);     //将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(File file, String content, boolean append){
        try(BufferedWriter bw = new BufferedWriter( new FileWriter(file, append))){
            bw.write(content);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
