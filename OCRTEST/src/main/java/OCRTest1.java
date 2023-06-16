import net.coobird.thumbnailator.Thumbnails;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OCRTest1 {

    // 横向切分块数
    private static final int widthSplitNum = 12;

    // 纵向切分块数
    private static final int heightSplitNum = 14;

    // 面积小于50像素的字符，舍弃掉
    private static final int ignoreArea = 50;

    public static void main(String[] args) throws IOException {
        test0811();
    }

    /**
     * 批量二值化图片，可用
     *
     * @throws java.io.IOException
     */
    @Test
    public static void test0811() throws IOException {
        // 指定原始照片文件夹
        String fromPic = "D:\\data\\idsourc\\";
        File directory = new File(fromPic);

        if (!directory.isDirectory()) {
            return;
        }

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }
            BufferedImage bufferedImage = ImageIO.read(file);

            if (bufferedImage.getWidth() < 100) {
                continue;
            }

            // 压缩图片
            bufferedImage = Thumbnails.of(bufferedImage).size(600, 1200).asBufferedImage();

            // 切割图片，分别处理，防止出现大片黑斑
            Map<Integer, BufferedImage> map = splitImage(bufferedImage);

            // 二值化
            for (Integer key : map.keySet()) {
                BufferedImage binaryImage = binaryImage(map.get(key));
                map.put(key, binaryImage);
            }

            // 合并图片
            BufferedImage destImage = mergeImage(map);
//            BufferedImage destImage = binaryImage(bufferedImage);

            // 黑白图片输出目录
            File newFile = new File("D:\\data\\OCR\\finish\\" + file.getName().replace(file.getName().substring(file.getName().lastIndexOf(".")), "") + ".png");
            ImageIO.write(destImage, "png", newFile);
        }
    }

    @Test
    public void test1012() throws IOException {
        long start = System.currentTimeMillis();
        // 待分割的黑白图片存放路径
        String filePath = "D:\\data\\OCR\\\\finish\\";
        File directory = new File(filePath);

        if (!directory.isDirectory()) {
            return;
        }

        int i = 200;
        for (File file : directory.listFiles()) {
            if(file.isDirectory()) {
                continue;
            }
            BufferedImage bufferedImage = ImageIO.read(file);

            // 去除图片左右两边的污点
            BufferedImage cutLRImage = cutLR(bufferedImage);

            // 将字符行染黑
            BufferedImage expandWhite = expandWhite2(cutLRImage);

            // 根据上面确定的字符行位置，提取出字符行
            List<BufferedImage> list = getCharRow4(expandWhite, cutLRImage);


            // 切割字符
            for (BufferedImage item : list) {
                List<BufferedImage> charImages = splitChar3(item);

                for (BufferedImage item2 : charImages) {
                    // 单个字符的存储路径
                    File newFile = new File("d:\\data\\OCR\\finish\\"+file.getName().substring(0,file.getName().lastIndexOf("."))+"\\" + (i++) + ".png");
                    ImageIO.write(item2, "png", newFile);
                    double height = item2.getHeight() * 1.5;
                    if (item2.getWidth() > height && item2.getWidth() < item2.getHeight() * 3) {
                        System.out.println((i - 1) + ".png");
                    }
                }
            }
        }

        System.out.println("耗时2：" + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test1018() throws IOException {
        long start = System.currentTimeMillis();

        // 黑白图片
        String fromPic = "D:\\data\\OCR\\\\finish\\1.png";
        //String fromPic = "d:\\IMG_20221001_084253.png";
        //String fromPic = "d:\\IMG_20221007_153516.png";
        File file = new File(fromPic);

        BufferedImage bufferedImage = ImageIO.read(file);

        // 去除图片左右两边的污点
        BufferedImage cutLRImage = cutLR(bufferedImage);

        // 将字符行染黑
        BufferedImage expandWhite = expandWhite2(cutLRImage);


        //System.out.println("耗时-1：" + (System.currentTimeMillis()- start) + "ms");

        File newFile2 = new File("d:\\bb.png");
        ImageIO.write(cutLRImage, "png", newFile2);
        //System.out.println("耗时0：" + (System.currentTimeMillis()- start) + "ms");

        // 根据上面确定的字符行位置，提取出字符行
        List<BufferedImage> list = getCharRow4(expandWhite, cutLRImage);

        //System.out.println("耗时1：" + (System.currentTimeMillis()- start) + "ms");

        int i = 200;
        // 切割字符
        for (BufferedImage item : list) {
            List<BufferedImage> charImages = splitChar3(item);

            /*for (BufferedImage item2 : charImages) {
                File newFile = new File("d:\\temp\\" + (i++) + ".png");
                ImageIO.write(item2, "png", newFile);
                double height = item2.getHeight()*1.5;
                if (item2.getWidth() > height && item2.getWidth() < item2.getHeight() * 3) {
                    System.out.println((i-1) + ".png");
                }
            }*/
        }

        System.out.println("耗时2：" + (System.currentTimeMillis()- start) + "ms");

    }

    /**
     * 同一水平线上的两个黑色像素点，如果他们之家的间距小于设定的阈值，那么将他们之间的所有白色像素点变成黑色像素点
     * @param bufferedImage
     * @return
     */
    private BufferedImage expandWhite2(BufferedImage bufferedImage) {
        BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        //long start1 = System.currentTimeMillis();
        grayImage.setData(bufferedImage.getData());
        //System.out.println("expandWhite2耗时1：" + (System.currentTimeMillis()- start1) + "ms");



        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        map3 = new HashMap<>();
        for (int col=0; col<height; col++) {
            for (int row = 0; row < width; row++) {
                int color = bufferedImage.getRGB(row, col);
                if ((color & 0xff) == 0) {
                    map3.put(col, 1);
                    break;
                }
            }
        }

        // 抹黑字符，确定字符行的位置，便于后续的调整行间距
        // 从左往右扫描，遇到黑色像素，就将他左边的像素抹黑
        int threshold = 120;

        // 第一个黑色像素点下标
        int start = -1;
        int end = -1;
        // 两个黑色像素点之间的白色像素点个数
        int whiteCount = 0;
        for (int j=0; j<height; j++) {
            if (!map3.containsKey(j)) {
                continue;
            }

            for (int i=0; i<width; i++) {

                int color = grayImage.getRGB(i, j);
                int red = color & 0xFF;

                // 黑色像素点
                if (red == 0) {
                    if (start == -1) {
                        start = i;
                    } else {
                        if (whiteCount == 0) {
                            start = i;
                        } else {
                            if (whiteCount < threshold) {
                                for (int k=start+1; k<i; k++) {
                                    grayImage.setRGB(k, j, 0);
                                }
                            }
                            start = i;
                            whiteCount = 0;
                        }
                    }
                } else if (red == 255 && start != -1) {
                    // 白色像素点
                    whiteCount++;
                }
            }

            start = -1;
            whiteCount = 0;
        }

        //System.out.println("expandWhite2耗时2" + (System.currentTimeMillis()- start1) + "ms");
        return grayImage;
    }

    /**将白色像素膨胀为两倍，黑色像素保持不变，便于分割字符串
     *
     * @param bufferedImage
     * @return
     */
    private BufferedImage expandWhite3(BufferedImage bufferedImage) {
        BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth()*2, bufferedImage.getHeight(), bufferedImage.getType());

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int col=0; col<height; col++) {
            for (int row = 0; row < width; row++) {
                int color = bufferedImage.getRGB(row, col);
                if (color == -1) {
                    map3.put(col, 1);
                } else {
                    grayImage.setRGB(row, col, 0);
                }
            }
        }

        //long start1 = System.currentTimeMillis();
        grayImage.setData(bufferedImage.getData());
        //System.out.println("expandWhite2耗时1：" + (System.currentTimeMillis()- start1) + "ms");




        map3 = new HashMap<>();
        for (int col=0; col<height; col++) {
            for (int row = 0; row < width; row++) {
                int color = bufferedImage.getRGB(row, col);
                if ((color & 0xff) == 0) {
                    map3.put(col, 1);
                    break;
                }
            }
        }

        // 抹黑字符，确定字符行的位置，便于后续的调整行间距
        // 从左往右扫描，遇到黑色像素，就将他左边的像素抹黑
        int threshold = 120;

        // 第一个黑色像素点下标
        int start = -1;
        int end = -1;
        // 两个黑色像素点之间的白色像素点个数
        int whiteCount = 0;
        for (int j=0; j<height; j++) {
            if (!map3.containsKey(j)) {
                continue;
            }

            for (int i=0; i<width; i++) {

                int color = grayImage.getRGB(i, j);
                int red = color & 0xFF;

                // 黑色像素点
                if (red == 0) {
                    if (start == -1) {
                        start = i;
                    } else {
                        if (whiteCount == 0) {
                            start = i;
                        } else {
                            if (whiteCount < threshold) {
                                for (int k=start+1; k<i; k++) {
                                    grayImage.setRGB(k, j, 0);
                                }
                            }
                            start = i;
                            whiteCount = 0;
                        }
                    }
                } else if (red == 255 && start != -1) {
                    // 白色像素点
                    whiteCount++;
                }
            }

            start = -1;
            whiteCount = 0;
        }

        //System.out.println("expandWhite2耗时2" + (System.currentTimeMillis()- start1) + "ms");
        return grayImage;
    }

    /**
     * 处理整个图片
     * 截图，去除左右边框
     * @param bufferedImage
     * @return
     */
    private BufferedImage cutLR(BufferedImage bufferedImage) {
        //long start = System.currentTimeMillis();

        // 横坐标上的映射图片，如果首行连续空白像素的个数超过阈值，就认为连续空白像素前面的都是污点，需要清除掉
        int threshold = 5;

        int ignoreHeight = 3;

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // 存储黑色行号，key为x轴坐标，value为对应x坐标上的黑色像素个数
        Map<Integer, Integer> map = new HashMap<>();

        // 统计
        for (int i = 0; i < width; i++) {
            int blackCount = 0;
            for (int j = 0; j < height; j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    blackCount++;
                    if (blackCount > ignoreHeight) {
                        break;
                    }
                }
            }

            map.put(i, blackCount);
        }
        //System.out.println("耗时-4：" + (System.currentTimeMillis()- start) + "ms");

        int whiteCount = 0;
        int startIndex = -1;
        int endIndex = width-1;

        // 找出起始像素点
        for (int i = 0; i < width/2; i++) {
            int value = map.get(i);

            // x轴上像素个数小于3的都认为是空白
            if (value < ignoreHeight) {
                whiteCount++;
                if (whiteCount >= threshold) {
                    startIndex = i;
                }
            } else {
                whiteCount = 0;
            }
        }

        // 找出结束像素点
        whiteCount = 0;
        for (int i = width-1; i > width/2; i--) {
            int value = map.get(i);

            if (value < ignoreHeight) {
                whiteCount++;
                if (whiteCount >= threshold) {
                    endIndex = i;
                }
            } else {
                whiteCount = 0;
            }
        }


        // 截掉图片左右两个空白部分
        BufferedImage grayImage = bufferedImage.getSubimage(startIndex, 0, endIndex-startIndex, height);

        /*BufferedImage grayImage = new BufferedImage(endIndex-startIndex, height, bufferedImage.getType());
        int newWidth = endIndex-startIndex;

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < height; j++) {
                int color = bufferedImage.getRGB(startIndex+i, j);
                grayImage.setRGB(i, j, color);
            }
        }*/

        //System.out.println("耗时-3：" + (System.currentTimeMillis()- start) + "ms");

        return grayImage;
    }

    /**
     * 去除单个字符上下边框
     * @param bufferedImage
     * @return
     */
    private BufferedImage cutUD(BufferedImage bufferedImage) {
        int startIndex = 0;

        // 找到起始像素点
        out:
        for (int j = 0; j < bufferedImage.getHeight(); j++) {
            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    startIndex = j;
                    break out;
                }
            }
        }

        // 找到结束像素点
        int endIndex = bufferedImage.getWidth()-1;
        out:
        for (int j = bufferedImage.getHeight()-1; j > -1; j--) {
            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    endIndex = j;
                    break out;
                }
            }
        }

        // 截掉图片上下两个空白部分

        //BufferedImage grayImage = bufferedImage.getSubimage(0,startIndex,bufferedImage.getWidth(), endIndex-startIndex+1);

        /*BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth(), endIndex-startIndex+1, bufferedImage.getType());
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < endIndex-startIndex+1; j++) {
                int color = bufferedImage.getRGB(i, startIndex + j);
                grayImage.setRGB(i, j, color);
            }
        }*/

        return bufferedImage.getSubimage(0, startIndex, bufferedImage.getWidth(), endIndex-startIndex+1);
    }

    /**
     * 去除单个字符左右边框
     * @param bufferedImage
     * @return
     */
    private BufferedImage cutLR2(BufferedImage bufferedImage) {
        int startIndex = 0;

        // 找到起始像素点
        out:
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    startIndex = i;
                    break out;
                }
            }
        }

        // 找到结束像素点
        int endIndex = bufferedImage.getWidth()-1;
        out:
        for (int i = bufferedImage.getWidth()-1; i > -1; i--) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    endIndex = i;
                    break out;
                }
            }
        }

        return bufferedImage.getSubimage(startIndex, 0, endIndex-startIndex+1, bufferedImage.getHeight());
    }

    /**
     * 对一行字符进行分割
     * 紧贴分割，且不会造成将一个正常字符分割成两个部分的情况
     * 如果过度分割，那么就进行合并
     * 合并的原理就是只要两个相邻的字符合并后不超过最大字符宽度，就认为是一个字符，否则就是两个字符
     * @param bufferedImage
     * @return
     */
    private List<BufferedImage> splitChar3(BufferedImage bufferedImage) {
        List<BufferedImage> charImages = new ArrayList<>();

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        // 横坐标上的映射图片，如果x坐标上的像素个数小于这个阈值，就认为是噪点，舍弃掉，值越大，达到分割的间距越小，分的越细
        //int threshold = 2;
        // 字符间距，值越小，达到分割的间距越小，分的越细
        //int interval = 1;

        // 横坐标上的映射图片，如果x坐标上的像素个数小于这个阈值，就认为是噪点，舍弃掉，值越大，达到分割的间距越小，分的越细
        int threshold = 1;
        // 字符间距，值越小，达到分割的间距越小，分的越细
        int interval = 2;

        // 存储黑色像素在x轴上的映射，key为x轴坐标，value为对应x坐标上的黑色像素个数
        Map<Integer, Integer> map = new HashMap<>();

        // 存储字符起始和结束下标，key为起始下标，value为结束下标后一位，二者差就是字符实际宽度
        Map<Integer, Integer> charsMap = new LinkedHashMap<>();
        // 统计
        for (int i = 0; i < width; i++) {
            int blackCount = 0;
            for (int j = 0; j < height; j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    blackCount++;
                    if (blackCount > threshold) {
                        break;
                    }
                }
            }

            map.put(i, blackCount);
        }

        // 从左到右遍历时，记录白色像素点的宽度，超过这个值，就表示当前处于字符之间的空白处
        int whiteCount = 0;
        int startIndex = -1;

        // 单个字符的最小宽度，用于判断当前字符是否需要合并
        // 存在“出”字被分成两个部分，左边宽度大于bufferedImage.getHeight()*2/3，右边宽度为1像素，导致不能合并，需要增大minWidth
        //int minWidth = bufferedImage.getHeight()*2/3;
        // 对于字符行是弯曲的情况，直接使用图片高度不适用
        //int minWidth = bufferedImage.getHeight();

        // 对于“小”字这种宽度大于高度的，如果他所在行的有效高度正好等于他的高度，就会造成”小“被分割成两个部分，所以需要额外增加1/20和一个像素
        int maxHeight = getMaxHeight(bufferedImage);
        int minWidth = maxHeight + maxHeight/20 + 1;

        // 单个字符的最大宽度
        // 存在“呀，”合并的情况，因为它们的宽度小于bufferedImage.getHeight()*5/4，需要缩小他的值
        //int maxWidth = bufferedImage.getHeight()*5/4;
        // 对于字符行是弯曲的情况，直接使用图片高度不适用
        //int maxWidth = bufferedImage.getHeight();

        int maxWidth = minWidth;
        int lastKey = -1;
        int lastValue = -1;
        // 找出起始像素点
        for (int i = 0; i < width; i++) {
            int value = map.get(i);

            // 字符起始下标
            if (startIndex == -1) {
                // 对于高度小于设定阈值的坐标，直接舍弃
                if (value >= threshold) {
                    startIndex = i;
                }
            } else {
                // x轴上像素个数小于3的都认为是空白
                if (value < threshold) {
                    whiteCount++;
                } else {
                    // 对于一个字符中间存在空白的情况，需要排除掉
                    whiteCount = 0;
                }

                if (whiteCount > interval) {
                    int charWidth = i-interval-startIndex;
                    // 防止一个字符被分割成两个部分
                    if (charWidth < minWidth) {
                        // 如果是类似“儿”这样的左右分开的字符
                        if (lastKey == -1) {
                            lastKey = startIndex;
                            lastValue = i-interval;
                        } else {
                            if (i-interval-lastKey > maxWidth) {
                                charsMap.put(lastKey, lastValue);
                                lastKey = startIndex;
                                lastValue = i-interval;
                            } else {
                                /*charsMap.put(lastKey, i-interval);
                                lastKey = -1;
                                lastValue = -1;*/

                                // 存在一个字符被分割成多个情况，这时需要多次合并
                                lastValue = i-interval;
                            }
                        }
                    } else {
                        if (lastKey != -1) {
                            charsMap.put(lastKey, lastValue);
                            lastKey = -1;
                            lastValue = -1;
                        }
                        charsMap.put(startIndex, i-interval);
                    }

                    startIndex = -1;
                    whiteCount = 0;
                }
            }
        }

        // 处理行尾的字符
        if (lastKey != -1 && startIndex != -1) {
            int i = width;

            // 后一个字符的宽度
            int charWidth = i-interval-startIndex;
            // 防止一个字符被分割成两个部分
            if (charWidth < minWidth) {
                // 如果是类似“儿”这样的左右分开的字符
                if (i-interval-lastKey > maxWidth) {
                    charsMap.put(lastKey, lastValue);
                    charsMap.put(startIndex, i-interval);
                } else {
                    // 存在一个字符被分割成多个情况，这时需要多次合并
                    charsMap.put(lastKey, i-interval);
                }
            } else {
                charsMap.put(lastKey, lastValue);
                charsMap.put(startIndex, i-interval);
            }
            lastKey = -1;
            lastValue = -1;
            startIndex = -1;
        }


        if (lastKey != -1 && startIndex == -1) {
            // 过滤噪点，暂时不考虑与最后一个字符合并
            if (lastValue - lastKey > 1) {
                charsMap.put(lastKey, lastValue);
                lastKey = -1;
                lastValue = -1;
            }
        }

        if (startIndex != -1 && lastKey == -1) {
            charsMap.put(startIndex, width);
            startIndex = -1;
        }

        // 根据上面已经标注好的字符下标，提取字符图片
        for (Integer key : charsMap.keySet()) {
            int value = charsMap.get(key);
            if (value <= key) {
                continue;
            }

            BufferedImage grayImage = null;

            try {
                grayImage = bufferedImage.getSubimage(key,0,value-key,height);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*BufferedImage grayImage = new BufferedImage(value-key, height, bufferedImage.getType());
            for (int i = 0; i < value-key; i++) {
                for (int j = 0; j < height; j++) {
                    int color = bufferedImage.getRGB(key+i, j);
                    grayImage.setRGB(i, j, color);
                }
            }*/


            // 清除无效字符
            if (isInvalidChar(grayImage)) {
                continue;
            }

            // 清除字符上下空白部分
            BufferedImage cutUDImage = cutUD(grayImage);

            // 再次进行分割
            //if (cutUDImage.getWidth() > bufferedImage.getHeight() && cutUDImage.getWidth() > cutUDImage.getHeight()*2) {
            // 案例：“这样”字符宽80像素，高39像素，maxWidth=42,不满足cutUDImage.getWidth() > maxWidth*1.92，没有进行分割
            //if (cutUDImage.getWidth() > maxHeight*1.92) {
            if (cutUDImage.getWidth() > maxHeight*splitThreshold) {
                List<BufferedImage> list2 = splitChar4(cutUDImage);
                charImages.addAll(list2);
            } else {
                charImages.add(cutUDImage);
            }
        }

        // “个儿”这两个字符会分成三个部分，需要将后面两个部分合在一起
        for (int i=0; i<charImages.size()-1; i++) {
            BufferedImage bufferedImage1 = charImages.get(i);

            if (bufferedImage1.getWidth() * 2 < bufferedImage1.getHeight()
                    && bufferedImage1.getWidth() * 3 > bufferedImage1.getHeight()) {
                BufferedImage bufferedImage2 = charImages.get(i+1);

                // 两个字符的宽度和高度都差不多，且高度都大致等于宽度的两倍
                if (bufferedImage2.getWidth() * 1.8 < bufferedImage2.getHeight()
                        && bufferedImage2.getWidth() * 3 > bufferedImage2.getHeight()
                        && Math.abs(bufferedImage1.getHeight()-bufferedImage2.getHeight()) < 5) {

                    // 两个字符合并后中间的空白宽度
                    int blankWidth = 4;

                    int newWidth = bufferedImage1.getWidth() + blankWidth + bufferedImage2.getWidth();
                    int newHeight = Math.max(bufferedImage1.getHeight(), bufferedImage2.getHeight());
                    // 进行合并
                    BufferedImage grayImage2 = new BufferedImage(newWidth, newHeight, bufferedImage.getType());

                    // 设置白色背景
                    for (int row=0; row<newWidth; row++) {
                        for (int col=0; col<newHeight; col++) {
                            grayImage2.setRGB(row, col, 0xFFFFFF);
                        }
                    }

                    for (int row=0; row<bufferedImage1.getWidth(); row++) {
                        for (int col=0; col<bufferedImage1.getHeight(); col++) {
                            grayImage2.setRGB(row, col, bufferedImage1.getRGB(row, col));
                        }
                    }

                    for (int row=0; row<bufferedImage2.getWidth(); row++) {
                        for (int col=0; col<bufferedImage2.getHeight(); col++) {
                            grayImage2.setRGB(row+bufferedImage1.getWidth()+blankWidth, col, bufferedImage2.getRGB(row, col));
                        }
                    }

                    charImages.set(i, grayImage2);
                    charImages.remove(i+1);

                    i++;
                }
            }
        }

        return charImages;
    }

    private double splitThreshold = 1.4;

    /**
     * 调小分割的间隙，再进行分割
     * @param bufferedImage
     * @return
     */
    private List<BufferedImage> splitChar4(BufferedImage bufferedImage) {
        List<BufferedImage> charImages = new ArrayList<>();

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        // 横坐标上的映射图片，如果x坐标上的像素个数小于这个阈值，就认为是噪点，舍弃掉，值越大，达到分割的间距越小，分的越细
        //int threshold = 2;
        // 字符间距，值越小，达到分割的间距越小，分的越细
        //int interval = 1;

        // 横坐标上的映射图片，如果x坐标上的像素个数小于这个阈值，就认为是噪点，舍弃掉，值越大，达到分割的间距越小，分的越细
        int threshold = 2;
        // 字符间距，值越小，达到分割的间距越小，分的越细
        int interval = 1;

        // 存储黑色像素在x轴上的映射，key为x轴坐标，value为对应x坐标上的黑色像素个数
        Map<Integer, Integer> map = new HashMap<>();

        // 存储字符起始和结束下标，key为起始下标，value为结束下标
        Map<Integer, Integer> charsMap = new LinkedHashMap<>();
        // 统计
        for (int i = 0; i < width; i++) {
            int blackCount = 0;
            for (int j = 0; j < height; j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    blackCount++;
                }
            }

            map.put(i, blackCount);
        }

        // 从左到右遍历时，记录白色像素点的宽度，超过这个值，就表示当前处于字符之间的空白处
        int whiteCount = 0;
        int startIndex = -1;

        // 单个字符的最小宽度，用于判断当前字符是否需要合并
        // 存在“出”字被分成两个部分，左边宽度大于bufferedImage.getHeight()*2/3，右边宽度为1像素，导致不能合并，需要增大minWidth
        //int minWidth = bufferedImage.getHeight()*2/3;
        int minWidth = bufferedImage.getHeight();

        // 单个字符的最大宽度
        // 存在“呀，”合并的情况，因为它们的宽度小于bufferedImage.getHeight()*5/4，需要缩小他的值
        //int maxWidth = bufferedImage.getHeight()*5/4;
        int maxWidth = height;
        int lastKey = -1;
        int lastValue = -1;

        // 下标从左到右顺序：lastKey lastValue startIndex i
        // 字符切割逻辑，从左往右依次扫描，碰到第一个黑色像素，标记为startIndex，继续扫描，碰到第一个白色像素，标记为i，
        // 这时认为i-startIndex为第一个字符的宽度，如果此宽度大于设定的参数，就认为他是一个完整的字符，添加进去，重置startIndex变量
        // 如果小于设定的参数，就认为他不是一个完整的字符，将startIndex赋值给lastKey，i赋值给lastValue，继续扫描，
        // 当扫描到第二个字符的起始黑色像素点时，标记为startIndex，继续扫描，碰到第一个白色像素，标记为i，
        // 如果第二个字符是完整的字符，即宽度超过设定值，那么第一个不完整的字符肯定是完整的字符，因为没有找到他的剩余部分
        // 如果第二个字符也不是完整的字符，那么计算第二个字符的宽度加上之前第一个不完整字符的宽度即i-lastKey，
        // 如果超过了设定的参数，就认为他们两个可以组成完整的字符，否则继续扫描剩余的部分
        // 总结起来就是，我从左往右扫描，当扫描到的黑色像素块超过我设定的阈值，那么我就认为这些黑色像素块是一个字符，以此类推
        // 找出起始像素点
        for (int i = 0; i < width; i++) {
            int value = map.get(i);

            // 字符起始下标
            if (startIndex == -1) {
                // 对于高度小于设定阈值的坐标，直接舍弃
                if (value >= threshold) {
                    startIndex = i;
                }
            } else {
                // x轴上像素个数小于3的都认为是空白
                if (value < threshold) {
                    whiteCount++;
                } else {
                    // 对于一个字符中间存在空白的情况，需要排除掉
                    whiteCount = 0;
                }

                if (whiteCount > interval) {
                    int charWidth = i-interval-startIndex;
                    // 防止一个字符被分割成两个部分
                    if (charWidth < minWidth) {
                        // 如果是类似“儿”这样的左右分开的字符
                        if (lastKey == -1) {
                            lastKey = startIndex;
                            lastValue = i-interval;
                        } else {
                            if (i-interval-lastKey > maxWidth) {
                                charsMap.put(lastKey, lastValue);
                                lastKey = startIndex;
                                lastValue = i-interval;
                            } else {
                                /*charsMap.put(lastKey, i-interval);
                                lastKey = -1;
                                lastValue = -1;*/

                                // 存在一个字符被分割成多个情况，这时需要多次合并
                                lastValue = i-interval;
                            }
                        }
                    } else {
                        if (lastKey != -1) {
                            charsMap.put(lastKey, lastValue);
                            lastKey = -1;
                            lastValue = -1;
                        }
                        charsMap.put(startIndex, i-interval);
                    }

                    startIndex = -1;
                    whiteCount = 0;
                }
            }
        }

        // 处理行尾的字符
        if (lastKey != -1 && startIndex != -1) {
            int i = width;

            // 后一个字符的宽度
            int charWidth = i-interval-startIndex;
            // 防止一个字符被分割成两个部分
            if (charWidth < minWidth) {
                // 如果是类似“儿”这样的左右分开的字符
                if (i-interval-lastKey > maxWidth) {
                    charsMap.put(lastKey, lastValue);
                    charsMap.put(startIndex, i-interval);
                } else {
                    // 存在一个字符被分割成多个情况，这时需要多次合并
                    charsMap.put(lastKey, i-interval);
                }
            } else {
                charsMap.put(lastKey, lastValue);
                charsMap.put(startIndex, i-interval);
            }
            lastKey = -1;
            lastValue = -1;
            startIndex = -1;
        }


        if (lastKey != -1 && startIndex == -1) {
            // 过滤噪点，暂时不考虑与最后一个字符合并
            if (lastValue - lastKey > 1) {
                charsMap.put(lastKey, lastValue);
                lastKey = -1;
                lastValue = -1;
            }
        }

        if (startIndex != -1 && lastKey == -1) {
            charsMap.put(startIndex, width);
            startIndex = -1;
        }

        // 根据上面已经标注好的字符下标，提取字符图片
        for (Integer key : charsMap.keySet()) {
            int value = charsMap.get(key);

            BufferedImage grayImage = bufferedImage.getSubimage(key,0,value-key,height);


            /*BufferedImage grayImage = new BufferedImage(value-key, bufferedImage.getHeight(), bufferedImage.getType());
            for (int i = 0; i < value-key; i++) {
                for (int j = 0; j < bufferedImage.getHeight(); j++) {
                    int color = bufferedImage.getRGB(key+i, j);
                    grayImage.setRGB(i, j, color);
                }
            }*/


            // 清除无效字符
            if (isInvalidChar(grayImage)) {
                continue;
            }

            // 清除字符上下空白部分
            BufferedImage cutUDImage = cutUD(grayImage);


            int maxHeight = getMaxHeight(bufferedImage);
            if (cutUDImage.getWidth() > maxHeight*splitThreshold) {
                // 如果两个字符相互重合不能使用一条直线分割，使用连通量进行分割
                List<BufferedImage> list2 = splitChar5(cutUDImage);
                charImages.addAll(list2);
            } else {
                charImages.add(cutUDImage);
            }

        }

        return charImages;
    }

    // 用于处理连在一起的字符串
    private int[] array2;

    /**
     * 根据连通分量来分割字符
     * 只处理含有两个连通分量的图片
     * @param image
     * @return
     */
    private List<BufferedImage> splitChar5(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int size = width * height;

        // 最小连通分量的面积，小于阈值的过滤掉
        int threshold = 20;

        // 用于存储每个单元格所属的集合
        array2 = new int[size];
        // 初始化各个单元格所属的集合
        for (int j = 0; j < size; j++) {
            array2[j] = -1;
        }

        //long startTime = System.currentTimeMillis();

        // 先合并左右单元格中间的竖着的墙
        for (int col=0; col<height; col++) {
            for (int row=0; row<width-1; row++) {
                int firstColor = image.getRGB(row, col);

                int secondColor = image.getRGB(row+1, col);

                int firstIndex = col*width+row;
                if ((firstColor & 0xff) == 0 && firstColor == secondColor) {
                    // 将两个单元格连通
                    array2[firstIndex] = firstIndex+1;
                }
            }
        }

        //System.out.println("startTime1：" + (System.currentTimeMillis()- startTime) + "ms");


        // 输出array
        /*for (int j=0; j<height; j++) {
            for (int i=0; i<width; i++) {
                int index = j * width + i;
                System.out.print(fillBlank(array[index]+",", 8));
            }

            System.out.println();
        }*/

        // 再拆除上下单元格之间的横着的墙，只拆除上下两行最右边的两个单元格就行了，能保证两行可以合并到同一个联通分量里面就行
        for (int col=height-1; col>0; col--) {
            for (int row=width-1; row>-1; row--) {
                int firstColor = image.getRGB(row, col);
                int secondColor = image.getRGB(row, col-1);

                // 上下两个单元格的右边如果都是黑色，就不合并
                if ((firstColor & 0xff) == 0 && firstColor == secondColor) {
                    // 第一个像素在第二个像素的下面
                    int firstIndex = col*width+row;
                    int secondIndex = firstIndex - width;

                    union2(firstIndex, secondIndex);
                }
            }
        }


        //System.out.println("startTime3：" + (System.currentTimeMillis()- startTime) + "ms");

        // 统计所有连通分量的大小
        Map<Integer, Integer> map = new HashMap<>();

        // 将联通分量里面的元素全部设置为统一编号，便于统计，用倒序比正序快得多
        for (int i=size-1; i>-1; i--) {
            int row = array2[i];
            if (row == -1) {
                continue;
            }

            int result1 = find2(i);

            Integer key = map.get(result1);
            if (key == null) {
                map.put(result1, 1);
            } else if (key < threshold) {
                map.put(result1, key+1);
            }

            array2[i] = result1;
        }

        // 存储连通量的上下两个极值在数组中的位置，key为连通量编号，也是连通量的上极值，value为连通量的下极值
        Map<Integer, Integer> map2 = new LinkedHashMap<>();
        for (int minY=0; minY<size; minY++) {
            int maxY = array2[minY];
            if (maxY == -1) {
                continue;
            }

            // 过滤掉面积小于1500像素的联通分量
            if (!map2.containsKey(maxY) && map.get(maxY) == threshold) {
                map2.put(maxY, minY);
            }
        }

        //System.out.println("startTime6：" + (System.currentTimeMillis()- startTime) + "ms");

        List<BufferedImage> list = new ArrayList<>();
        if (map2.size() != 2) {
            list.add(image);
            return list;
        }

        // 根据联通分量来抠图
        Map<Integer, BufferedImage> map5 = new LinkedHashMap<>();
        for (int row = 0; row < width; row++) {
            for (int col=0; col<height; col++) {
                int index = col * width + row;
                int maxY = array2[index];
                if (maxY == -1 || map2.get(maxY) == null) {
                    continue;
                }


                int minY = map2.get(maxY);

                if (map2.containsKey(maxY)) {
                    int newHeight = maxY/width - minY/width+1;

                    int blankHeight = minY/width;

                    if (map5.containsKey(maxY)) {
                        BufferedImage grayImage = map5.get(maxY);
                        int color = image.getRGB(row, col);

                        grayImage.setRGB(row, col-blankHeight, color);
                    } else {
                        // 找出能放下字符行的四边形的四个角
                        BufferedImage grayImage = new BufferedImage(width, newHeight, image.getType());
                        // 设置白色背景
                        for (int i=0; i<width; i++) {
                            for (int j=0; j<newHeight; j++) {
                                grayImage.setRGB(i, j, 0xFFFFFF);
                            }
                        }

                        int color = image.getRGB(row, col);

                        grayImage.setRGB(row, col-blankHeight, color);

                        map5.put(maxY, grayImage);
                    }


                }

            }
        }

        for (Integer key : map5.keySet()) {
            // 清除字符上下空白部分
            BufferedImage cutLR2 = cutLR2(map5.get(key));

            list.add(cutLR2);
        }

        return list;
    }

    private boolean isInvalidChar(BufferedImage bufferedImage) {
        int blackCount = 0;
        // 统计
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int b = color & 0xff;
                if (b == 0) {
                    blackCount++;
                }
            }
        }

        return blackCount < ignoreArea;
    }

    /**
     * 获取一行文字的最大有效高度
     * 用于处理倒S形的文字行
     * 从左到右扫描列，通过记录每一列的第一个和最后一个黑色像素，从而得到该列的有效高度，最后取所有列最大的有效高度，也即是字符的最大高度
     * @param bufferedImage
     * @return
     */
    private static int getMaxHeight(BufferedImage bufferedImage) {
        int maxHeight = 0;
        // 以图片左上角点为坐标原点
        for (int i = 0; i < bufferedImage.getWidth(); i++) {

            int start = bufferedImage.getHeight()-1;
            int end = 0;

            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int color = bufferedImage.getRGB(i, j);
                int red = color & 0xFF;
                if (red == 0) {
                    start = j;
                    break;
                }
            }

            for (int j = bufferedImage.getHeight()-1; j > -1; j--) {
                int color = bufferedImage.getRGB(i, j);
                int red = color & 0xFF;
                if (red == 0) {
                    end = j;
                    break;
                }
            }

            if ((end - start) > maxHeight) {
                maxHeight = end - start;
            }

        }

        return maxHeight + 1;
    }

    /**
     * 切分图片
     *
     * @param bufferedImage
     * @return
     */
    private static Map<Integer, BufferedImage> splitImage(BufferedImage bufferedImage) {
        Map<Integer, BufferedImage> map = new HashMap<>();

        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int type = bufferedImage.getType();

        int blockWidth = w / widthSplitNum;
        int blockHeight = h / heightSplitNum;

        for (int i = 0; i < widthSplitNum; i++) {
            for (int j = 0; j < heightSplitNum; j++) {
                BufferedImage grayImage = new BufferedImage(blockWidth, blockHeight, type);

                int mStart = i * blockWidth;
                int mEnd = (i + 1) * blockWidth;
                int nStart = j * blockHeight;
                int nEnd = (j + 1) * blockHeight;

                for (int m = mStart; m < mEnd; m++) {
                    for (int n = nStart; n < nEnd; n++) {
                        grayImage.setRGB(m - mStart, n - nStart, bufferedImage.getRGB(m, n));
                    }
                }

                map.put(i * heightSplitNum + j, grayImage);
            }
        }

        return map;
    }

    public static BufferedImage mergeImage(Map<Integer, BufferedImage> map) {
        if (map == null || map.get(0) == null) {
            return null;
        }

        int blockWidth = map.get(0).getWidth();
        int blockHeight = map.get(0).getHeight();

        BufferedImage destImage = new BufferedImage(blockWidth * widthSplitNum, blockHeight * heightSplitNum, BufferedImage.TYPE_BYTE_BINARY);


        for (int i = 0; i < widthSplitNum; i++) {
            for (int j = 0; j < heightSplitNum; j++) {
                BufferedImage grayImage = map.get(i * heightSplitNum + j);

                for (int m = 0; m < grayImage.getWidth(); m++) {
                    for (int n = 0; n < grayImage.getHeight(); n++) {
                        destImage.setRGB(i * blockWidth + m, j * blockHeight + n, grayImage.getRGB(m, n));
                    }
                }
            }
        }

        return destImage;
    }

    /**
     * 二值化图片
     *
     * @param bufferedImage 原图片
     * @return 二值化后的图片
     */
    private static BufferedImage binaryImage(BufferedImage bufferedImage) {
        BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        int threshold = getMeanThreshold(bufferedImage);

        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                int gray = (r*38 + g*75 + b*15) >> 7;
                if (gray > threshold) {
                    // 白色
                    grayImage.setRGB(i, j, 0xFFFFFF);
                } else {
                    // 黑色
                    grayImage.setRGB(i, j, 0);
                }
            }
        }

        return grayImage;
    }

    /**
     * 获取图片的阀值，采用基于灰度平均值的阈值
     *
     * @param bufferedImage 原图片
     * @return 二值化的阈值
     */
    private static int getMeanThreshold2(BufferedImage bufferedImage) {
        BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        Map<Integer, Integer> map = new HashMap<>();


        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                // getRGB()方法，根据手册，其返回的int型数据（32位）为ARGB格式，其中ARGB各占8bit
                int color = bufferedImage.getRGB(i, j);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                //int gray = (int) (0.2126 * r + 0.7152 * g + 0.0722 * b);
                int gray = (int) (0.33 * r + 0.34 * g + 0.33 * b);

                if (map.containsKey(gray)) {
                    map.put(gray, map.get(gray)+1);
                } else {
                    map.put(gray, 1);
                }
            }
        }

        // 起始像素值
        int index = 0;
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int rgb = getRgb(map, index);
                grayImage.setRGB(i, j, rgb);
            }
        }


        int sum = 0;

        int j = grayImage.getHeight()/2;
        for (int i = 0; i < grayImage.getWidth(); i++) {
            int color = grayImage.getRGB(i, j);
            int r = (color >> 16) & 0xff;
            sum += r;
            //System.out.println(r);
        }

        int threshold = sum / grayImage.getWidth();
        System.out.println(threshold);
        return threshold - 15;
    }

    /**
     * 递归
     * @param map
     * @param index
     * @return
     */
    private static int getRgb(Map<Integer, Integer> map, int index) {
        int rgb = 0;
        while (map.get(index) == null && index < 256) {
            index++;
        }

        Integer count = map.get(index);

        if (count != null) {
            if (count > 0) {
                map.put(index, count-1);
                rgb = (clamp(index) << 16) | (clamp(index) << 8) | clamp(index);
            } else {
                rgb = getRgb(map, index+1);
            }
        }

        return rgb;
    }

    /**
     * 获取图片的阀值，采用基于灰度平均值的阈值
     *
     * @param bufferedImage 原图片
     * @return 二值化的阈值
     */
    private static int getMeanThreshold(BufferedImage bufferedImage) {
        double aa = 0.9;
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int num = 0;
        long sum = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = bufferedImage.getRGB(i, j);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                int gray = (r*38 + g*75 + b*15) >> 7;
                sum += gray;
                num += 1;
            }
        }

        // 测试表明，阀值取平均值的1.2倍效果最好。
        // 越大越黑
        int threshold = (int) (sum / num);
        if (threshold * aa < 255) {
            threshold = (int) (aa * sum / num);
        }

        return threshold;
    }

    /**
     * 如果像素点的值超过了0-255的范围,予以调整
     *
     * @param value 输入值
     * @return 输出值
     */
    private static int clamp(int value) {
        return value > 255 ? 255 : (Math.max(value, 0));
    }

    // 用于存储每个单元格所属的集合
    private int[] array;

    // 映射到右边边界上,用于判断哪些行有黑色像素,对于没有黑色像素的行直接跳过
    private Map<Integer, Integer> map3;

    /**
     * 减少union的时间
     * @param image
     * @return
     * @throws java.io.IOException
     */
    private List<BufferedImage> getCharRow4(BufferedImage image, BufferedImage bufferedImage) {
        int width = image.getWidth();
        int height = image.getHeight();
        int size = width * height;

        // 最小连通分量的面积，小于阈值的过滤掉
        int threshold = 500;

        // 用于存储每个单元格所属的集合
        array = new int[size];
        // 初始化各个单元格所属的集合
        for (int j = 0; j < size; j++) {
            array[j] = -1;
        }

        //long startTime = System.currentTimeMillis();

        // 先合并左右单元格中间的竖着的墙
        for (int col=0; col<height; col++) {
            if (!map3.containsKey(col)) {
                continue;
            }

            for (int row=0; row<width-1; row++) {
                int firstColor = image.getRGB(row, col);

                int secondColor = image.getRGB(row+1, col);

                int firstIndex = col*width+row;
                if ((firstColor & 0xff) == 0 && firstColor == secondColor) {
                    // 将两个单元格连通
                    array[firstIndex] = firstIndex+1;
                }
            }
        }

        //System.out.println("startTime1：" + (System.currentTimeMillis()- startTime) + "ms");


        // 输出array
        /*for (int j=0; j<height; j++) {
            for (int i=0; i<width; i++) {
                int index = j * width + i;
                System.out.print(fillBlank(array[index]+",", 8));
            }

            System.out.println();
        }*/


        boolean flag = false;
        // 再拆除上下单元格之间的横着的墙，只拆除上下两行最右边的两个单元格就行了，能保证两行可以合并到同一个联通分量里面就行
        for (int col=height-1; col>0; col--) {
            if (!map3.containsKey(col)) {
                continue;
            }
            // 每次换行都要执行一次合并，防止因为两行的最右边是边界，没有白色像素
            flag = true;
            for (int row=width-1; row>-1; row--) {
                int firstColor = image.getRGB(row, col);
                int secondColor = image.getRGB(row, col-1);

                // 如果两个黑色像素前面是白色像素，那么就表示他们是各自行的末尾，那么就进行合并操作
                if (!flag && (firstColor == -1 || secondColor == -1)) {
                    flag = true;
                }

                // 上下两个单元格的右边如果都是黑色，就不合并
                if (flag && ((firstColor & 0xff) == 0 && firstColor == secondColor)) {
                    // 第一个像素在第二个像素的下面
                    int firstIndex = col*width+row;
                    int secondIndex = firstIndex - width;

                    union(firstIndex, secondIndex);
                    flag = false;
                }
            }
        }


        //System.out.println("startTime3：" + (System.currentTimeMillis()- startTime) + "ms");

        // 统计所有连通分量的大小
        Map<Integer, Integer> map = new HashMap<>();

        // 将联通分量里面的元素全部设置为统一编号，便于统计，用倒序比正序快得多
        for (int i=size-1; i>-1; i--) {
            int row = array[i];
            if (row == -1) {
                continue;
            }

            int result1 = find(i);

            Integer key = map.get(result1);
            if (key == null) {
                map.put(result1, 1);
            } else if (key < threshold) {
                map.put(result1, key+1);
            }

            array[i] = result1;
        }

        // 存储连通量的上下两个极值在数组中的位置，key为连通量编号，也是连通量的上极值，value为连通量的下极值
        Map<Integer, Integer> map2 = new LinkedHashMap<>();
        for (int minY=0; minY<size; minY++) {
            int maxY = array[minY];
            if (maxY == -1) {
                continue;
            }

            // 过滤掉面积小于1500像素的联通分量
            if (!map2.containsKey(maxY) && map.get(maxY) == threshold) {
                map2.put(maxY, minY);
            }
        }

        //System.out.println("startTime6：" + (System.currentTimeMillis()- startTime) + "ms");


        // 根据联通分量来抠图
        List<BufferedImage> list = new ArrayList<>();
        Map<Integer, BufferedImage> map5 = new LinkedHashMap<>();

        for (int col=0; col<height; col++) {
            if (!map3.containsKey(col)) {
                continue;
            }

            for (int row = 0; row < width; row++) {
                int index = col * width + row;
                int maxY = array[index];
                if (maxY == -1 || map2.get(maxY) == null) {
                    continue;
                }


                int minY = map2.get(maxY);

                if (map2.containsKey(maxY)) {
                    int newHeight = maxY/width - minY/width+1;

                    int blankHeight = minY/width;

                    if (map5.containsKey(maxY)) {
                        BufferedImage grayImage = map5.get(maxY);
                        int color = bufferedImage.getRGB(row, col);

                        grayImage.setRGB(row, col-blankHeight, color);
                    } else {
                        // 找出能放下字符行的四边形的四个角
                        BufferedImage grayImage = new BufferedImage(width, newHeight, image.getType());
                        // 设置白色背景
                        for (int i=0; i<width; i++) {
                            for (int j=0; j<newHeight; j++) {
                                grayImage.setRGB(i, j, 0xFFFFFF);
                            }
                        }

                        int color = bufferedImage.getRGB(row, col);

                        grayImage.setRGB(row, col-blankHeight, color);

                        map5.put(maxY, grayImage);
                    }


                }

            }
        }

        for (Integer key : map5.keySet()) {
            list.add(map5.get(key));
        }

        /*for (int maxY : map2.keySet()) {
            int minY = map2.get(maxY);

            int newHeight = maxY/width - minY/width+1;

            int blankHeight = minY/width;

            // 找出能放下字符行的四边形的四个角
            BufferedImage grayImage = new BufferedImage(width, newHeight, image.getType());
            // 设置白色背景
            for (int i=0; i<width; i++) {
                for (int j=0; j<newHeight; j++) {
                    grayImage.setRGB(i, j, 0xFFFFFF);
                }
            }

            for (int i=0; i<size; i++) {
                if (array[i] == -1) {
                    continue;
                }

                if (array[i] == maxY) {
                    int x = i % width;
                    int y = i / width;
                    int color = bufferedImage.getRGB(x, y);

                    grayImage.setRGB(x, y-blankHeight, color);
                }
            }

            list.add(grayImage);
        }*/

        //System.out.println("startTime7：" + (System.currentTimeMillis()- startTime) + "ms");

        int fileName = 0;
        for (Integer key: map5.keySet()) {
            File newFile = new File("d:\\data\\" + (fileName++) + ".png");
            try {
                ImageIO.write(map5.get(key), "png", newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private void printArray(int width, int height) {
        for (int j=0; j<height; j++) {
            for (int i=0; i<width; i++) {
                int index = j * width + i;
                System.out.print(fillBlank(array[index]+",", 8));
            }

            System.out.println();
        }
    }

    public String fillBlank(String str, int length) {
        int strLength = str.length();
        if (strLength < length) {
            for (int i=0; i<length-strLength; i++) {
                str += " ";
            }
        }
        return str;
    }

    /**
     * 返回 i 所在集合的最大值
     *
     * @param i 单元格编号
     * @return
     */
    public int find(int i) {
        int result = i;
        while (array[result] != -1) {
            result = array[result];
        }
        return result;
    }

    /**
     * 将 i 和 j 所在集合进行合并
     *
     * @param i 单元格编号
     * @param j 单元格编号
     */
    public void union(int i, int j) {
        int result1 = find(i);
        int result2 = find(j);
        if (result1 == result2){
            return;
        }
        if(result1 > result2) {
            array[result2] = result1;
        }
        else {
            array[result1] = result2;
        }
    }


    public int find2(int i) {
        int result = i;
        while (array2[result] != -1) {
            result = array2[result];
        }
        return result;
    }

    public void union2(int i, int j) {
        int result1 = find2(i);
        int result2 = find2(j);
        if (result1 == result2){
            return;
        }
        if(result1 > result2) {
            array2[result2] = result1;
        }
        else {
            array2[result1] = result2;
        }
    }

    private void setRecursion(int j, int value) {

        if (array[j] != 0) {
            if (j == array[j]) {
                return;
            }

            setRecursion(array[j], value);
        }

        array[j] = value;

    }

}

