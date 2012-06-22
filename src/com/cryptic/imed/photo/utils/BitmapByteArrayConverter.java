package com.cryptic.imed.photo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * @author sharafat
 */
public class BitmapByteArrayConverter {

    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    public static Bitmap byteArray2Bitmap(byte[] byteArray) {
        return byteArray == null ? null : BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

}
