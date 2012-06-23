package com.cryptic.imed.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cryptic.imed.R;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.photo.util.BitmapByteArrayConverter;
import com.cryptic.imed.util.StringUtils;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * @author sharafat
 */
public class MedicineDetailsFragment extends RoboFragment {
    @InjectView(R.id.med_name)
    private TextView medNameTextView;

    @InjectView(R.id.med_details)
    private TextView medDetailsTextView;
    @InjectView(R.id.current_stock)
    private TextView currentStockTextView;
    @InjectView(R.id.med_photo)
    private ImageView medPhotoImageView;
    @InjectView(R.id.details_heading)
    private TextView detailsHeading;
    @InjectView(R.id.current_stock_heading)
    private TextView currentStockHeading;


    @InjectResource(R.string.x_units_available)
    private String xUnitsAvailable;
    @InjectResource(R.string.not_available)
    private String notAvailable;

    private Medicine medicine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.medicine_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView();
    }

    public void updateView() {
        if (medicine != null) {
            medNameTextView.setText(medicine.getName());
            medDetailsTextView.setText(StringUtils.getNonEmptyString(medicine.getDetails(), notAvailable));
            currentStockTextView.setText(String.format(xUnitsAvailable,
                    StringUtils.dropDecimalIfRoundNumber(medicine.getCurrentStock()), medicine.getMedicationUnit()));
            if (medicine.getPhoto() != null) {
                medPhotoImageView.setImageBitmap(BitmapByteArrayConverter.byteArray2Bitmap(medicine.getPhoto()));
            }

            detailsHeading.setVisibility(View.VISIBLE);
            currentStockHeading.setVisibility(View.VISIBLE);
        } else {
            medNameTextView.setText("");
            medDetailsTextView.setText("");
            currentStockTextView.setText("");
            medPhotoImageView.setImageDrawable(null);

            detailsHeading.setVisibility(View.GONE);
            currentStockHeading.setVisibility(View.GONE);
        }
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
}
