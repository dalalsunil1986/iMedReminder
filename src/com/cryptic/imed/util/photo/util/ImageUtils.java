package com.cryptic.imed.util.photo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

/**
 * @author sharafat
 */
public class ImageUtils {

    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    public static Bitmap byteArray2Bitmap(byte[] byteArray) {
        return byteArray == null ? null : BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static byte[] drawable2ByteArray(Drawable drawable) {
        return bitmap2ByteArray(drawable2Bitmap(drawable));
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    public static Bitmap getNonEmptyImage(Bitmap image, Bitmap defaultImage) {
        return image != null? image : defaultImage;
    }

    public static Bitmap getNonEmptyImage(Bitmap image, byte[] defaultImage) {
        return image != null? image : byteArray2Bitmap(defaultImage);
    }

    public static Bitmap getNonEmptyImage(Bitmap image, Drawable defaultImage) {
        return image != null? image : drawable2Bitmap(defaultImage);
    }

    public static Bitmap getNonEmptyImage(byte[] image, byte[] defaultImage) {
        return image != null? byteArray2Bitmap(image) : byteArray2Bitmap(defaultImage);
    }

    public static Bitmap getNonEmptyImage(byte[] image, Bitmap defaultImage) {
        return image != null? byteArray2Bitmap(image) : defaultImage;
    }

    public static Bitmap getNonEmptyImage(byte[] image, Drawable defaultImage) {
        return image != null? byteArray2Bitmap(image) : drawable2Bitmap(defaultImage);
    }

    public static Bitmap getNonEmptyImage(Drawable image, Drawable defaultImage) {
        return image != null? drawable2Bitmap(image) : drawable2Bitmap(defaultImage);
    }

    public static Bitmap getNonEmptyImage(Drawable image, byte[] defaultImage) {
        return image != null? drawable2Bitmap(image) : byteArray2Bitmap(defaultImage);
    }

    public static Bitmap getNonEmptyImage(Drawable image, Bitmap defaultImage) {
        return image != null? drawable2Bitmap(image) : defaultImage;
    }
}
