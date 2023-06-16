package tools;

import com.alibaba.fastjson.JSONArray;
import dspocr.dataloader.data.Blob;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class LoadModelUtil {

    private static final Pattern pattern = Pattern.compile("stage\\d");

    public static void loadByKey(String key, String v, Object object) {
        String res = "";
        try {
            Blob blob = parseBlob(v);
            String[] split = key.split("\\.");
            for (int i = 0; i < split.length - 1; i++) {
                String k = split[i];
                res = k;
                if (object instanceof List) {
                    object = ((List) object).get(Integer.parseInt(k));
                } else {
                    if (pattern.matcher(k).matches()) {
                        Method method = object.getClass().getMethod("getStage");
                        object = method.invoke(object);
                        object = ((List) object).get(Integer.parseInt(k.replace("stage", "")));
                    } else {
                        Method method = object.getClass().getMethod("get" + firstCharBig(k));
                        object = method.invoke(object);
                    }
                }
            }
            Method method = object.getClass().getMethod("set" + firstCharBig(split[split.length-1]), Blob.class);
            method.invoke(object, blob);

        } catch (Exception e) {
            System.out.println(key+"\t"+object+"\t"+res);
            System.out.println("模型文件格式错误！");
            e.printStackTrace();
        }
    }


    private static Blob parseBlob(String s) {
        List<String> numberValues = JSONArray.parseArray(s, String.class);
        int number = 1;
        int channel = 1;
        int height = 1;
        int width = 1;
        if (!numberValues.get(0).contains("[")) {
            width = numberValues.size();
        } else {
            List<String> channelValues = JSONArray.parseArray(numberValues.get(0), String.class);
            if (!channelValues.get(0).contains("[")) {
                width = channelValues.size();
                height = numberValues.size();
            } else {
                List<String> heightValues = JSONArray.parseArray(channelValues.get(0), String.class);
                if (!heightValues.get(0).contains("[")) {
                    width = heightValues.size();
                    height = channelValues.size();
                    channel = numberValues.size();
                } else {
                    List<String> widthValues = JSONArray.parseArray(heightValues.get(0), String.class);
                    width = widthValues.size();
                    height = heightValues.size();
                    channel = channelValues.size();
                    number = numberValues.size();
                }
            }
        }
        Blob blob = new Blob(number, channel, height, width);
        double[][][][] data = blob.getData();
        s = s.replace("[", "").replace("]", "").replace(" ", "");
        String[] split = s.split(",");
        int d2Size = blob.get2DSize();
        int d3Size = blob.get3DSize();
        for (int n = 0; n < number; n++) {
            for (int c = 0; c < channel; c++) {
                for (int h = 0; h < height; h++) {
                    for (int w = 0; w < width; w++) {
                        data[n][c][h][w] = Double.parseDouble(split[n*d3Size+c*d2Size+h*blob.getWidth()+w]);
                    }
                }
            }
        }
        return blob;
    }

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    private static String firstCharBig(String s) {
        return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
    }
}
