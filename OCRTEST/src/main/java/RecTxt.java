import ai.djl.nn.convolutional.Conv2d;
import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecTxt {


    public static void main(String[] args) throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader(new File("D:\\data\\032306\\idtest\\rec_gt.txt")));
//        String line = null;
//        Map<String, List<String>> resultMap = new HashMap<>();
//        while ((line = br.readLine()) != null) {
//            String[] split = line.split("\t");
//            if(split.length==2) {
//                List<String> strings = resultMap.get(split[1]);
//                if(strings==null) {
//                    strings = new ArrayList<>();
//                }
//                strings.add(split[0]);
//                resultMap.put(split[1], strings);
//            }
//        }
//        br.close();
//
//        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:\\data\\032306\\idtest\\rec_gt2.txt")));
//        for(Map.Entry<String, List<String>> entry: resultMap.entrySet()) {
//            List<String> value = entry.getValue();
//            String writeLine = value.get(0)+"\t"+ entry.getKey();
//            if(value.size()>1) {
//                writeLine = JSON.toJSONString(value)+"\t"+ entry.getKey();
//            }
//            bw.write(writeLine);
//            bw.newLine();
//
//        }
//        bw.close();
    }
}
