package com.cryptic.imed.utils;

/**
 * @author sharafat
 */
public class StringUtils {

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
