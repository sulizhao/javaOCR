package tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * <pre>
 * Modify Information:
 * Author       Date          Description
 * ============ ============= ============================
 * sulizhao      2021年09月03日
 * </pre>
 */
public class BigDecimalCaculateUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##,###,###,##0.00");
    private static final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("##########0.00#");
    private static final DecimalFormat DECIMAL_FORMAT3 = new DecimalFormat("###########.#");


    /**
     * 加
     * @param arg1
     * @param arg2
     * @return
     */
    public static String add(String arg1, String arg2) {
        arg1 = isEmpty(arg1)?"0":arg1;
        arg2 = isEmpty(arg2)?"0":arg2;
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal add = bigDecimal1.add(bigDecimal2);
        return add.toString();
    }

    /**
     * 减
     * @param arg1
     * @param arg2
     * @return
     */
    public static String subtract(String arg1, String arg2) {
        arg1 = isEmpty(arg1)?"0":arg1;
        arg2 = isEmpty(arg2)?"0":arg2;
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal add = bigDecimal1.subtract(bigDecimal2);
        return add.toString();
    }

    /**
     * 乘
     * @param arg1
     * @param arg2
     * @return
     */
    public static String multiply(String arg1, String arg2) {
        arg1 = isEmpty(arg1)?"0":arg1;
        arg2 = isEmpty(arg2)?"0":arg2;
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal add = bigDecimal1.multiply(bigDecimal2);
        return add.toString();
    }

    /**
     * 除
     * @param arg1
     * @param arg2
     * @return
     */
    public static String divide(String arg1, String arg2, int scale) {
        arg1 = isEmpty(arg1)?"0":arg1;
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal add = bigDecimal1.divide(bigDecimal2, scale, RoundingMode.CEILING);
        return add.toString();
    }


    /**
     * 四舍五入除
     * @param arg1
     * @param arg2
     * @return
     */
    public static String halfUpDivide(String arg1, String arg2, int scale) {
        arg1 = isEmpty(arg1)?"0":arg1;
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        BigDecimal add = bigDecimal1.divide(bigDecimal2, scale, RoundingMode.HALF_UP);
        return add.toString();
    }

    /**
     * 比较
     * @param arg1
     * @param arg2
     * @return
     */
    public static int compare(String arg1, String arg2) {
        arg1 = isEmpty(arg1)?"0":arg1;
        arg2 = isEmpty(arg2)?"0":arg2;
        BigDecimal bigDecimal1 = new BigDecimal(arg1);
        BigDecimal bigDecimal2 = new BigDecimal(arg2);
        return bigDecimal1.compareTo(bigDecimal2);
    }

    /**
     * 取整
     * @param arg1
     * @return
     */
    public static Long getLongValue(String arg1) {
        return (long) Math.floor(new BigDecimal(arg1).doubleValue());
    }

    /**
     * 取整
     * @param arg1
     * @return
     */
    public static String yuan2fen(String arg1) {
        if (isEmpty(arg1)) {
            return "0";
        } else {
            return DECIMAL_FORMAT3.format(new BigDecimal(arg1).multiply(new BigDecimal("100")));
        }
    }

    /**
     * 取整
     * @param arg1
     * @return
     */
    public static String fen2yuan(String arg1) {
        if (isEmpty(arg1)) {
            return "0";
        } else {
            return DECIMAL_FORMAT2.format(new BigDecimal(arg1).divide(new BigDecimal("100"), 3, RoundingMode.CEILING));
        }
    }

    public static void main(String[] args) {
        Pattern patternIdValue = Pattern.compile("\\d{17}([0-9]{1}|X)");
        System.out.println(patternIdValue.matcher("2008.11.21-长期").find());
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return null == str || "".equals(str.trim());
    }


}
