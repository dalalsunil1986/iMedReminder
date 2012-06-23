package com.cryptic.imed.util;

/**
 * @author sharafat
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static String getNonEmptyString(String str, String defaultStringIfStrIsEmpty) {
        return isEmpty(str) ? defaultStringIfStrIsEmpty : str;
    }

    public static String dropDecimalIfRoundNumber(float val) {
        if (val == 0) {
            return "0";
        } else if (val % (int) val == 0) {
            return Integer.toString((int) val);
        } else {
            return Float.toString(val);
        }
    }
}
