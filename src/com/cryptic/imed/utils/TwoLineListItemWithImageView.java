package com.cryptic.imed.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cryptic.imed.R;

/**
 * @author sharafat
 */
public class TwoLineListItemWithImageView {

    public static View getView(LayoutInflater layoutInflater, View convertView, ViewGroup parent,
                               String primaryText, String secondaryText, int imageResId) {
        convertView = prepareConvertViewWithPrimaryAndSecondaryTexts(layoutInflater, convertView, parent,
                primaryText, secondaryText);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        if (imageResId != 0) {
            imageView.setImageResource(imageResId);
        }

        return convertView;
    }

    public static View getView(LayoutInflater layoutInflater, View convertView, ViewGroup parent,
                               String primaryText, String secondaryText, byte[] image) {
        return getView(layoutInflater, convertView, parent, primaryText, secondaryText,
                image == null ? null : BitmapFactory.decodeByteArray(image, 0, image.length));
    }

    public static View getView(LayoutInflater layoutInflater, View convertView, ViewGroup parent,
                               String primaryText, String secondaryText, Bitmap image) {
        convertView = prepareConvertViewWithPrimaryAndSecondaryTexts(layoutInflater, convertView, parent,
                primaryText, secondaryText);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        if (image != null) {
            imageView.setImageBitmap(image);
        }

        return convertView;
    }

    private static View prepareConvertViewWithPrimaryAndSecondaryTexts(LayoutInflater layoutInflater,
                                                                       View convertView, ViewGroup parent,
                                                                       String primaryText, String secondaryText) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    Build.VERSION.SDK_INT < 11
                            ? R.layout.list_item_with_image
                            : R.layout.list_item_with_image_activated,
                    parent, false);
        }

        TextView primaryTextView = (TextView) convertView.findViewById(R.id.primaryText);
        TextView secondaryTextView = (TextView) convertView.findViewById(R.id.secondaryText);

        primaryTextView.setText(primaryText);
        secondaryTextView.setText(secondaryText);

        return convertView;
    }
}
