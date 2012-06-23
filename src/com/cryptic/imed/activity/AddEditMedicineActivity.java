package com.cryptic.imed.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.cryptic.imed.R;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.domain.MedicationUnit;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.fragment.MedicineListFragment;
import com.cryptic.imed.photo.camera.CameraUnavailableException;
import com.cryptic.imed.photo.camera.OnPhotoTakeListener;
import com.cryptic.imed.photo.camera.PhotoTaker;
import com.cryptic.imed.photo.utils.BitmapByteArrayConverter;
import com.cryptic.imed.util.StringUtils;
import com.cryptic.imed.util.Validation;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.io.Serializable;

/**
 * @author sharafat
 */
@ContentView(R.layout.new_medicine)
public class AddEditMedicineActivity extends RoboActivity {
    private static final int PHOTO_SIZE = 64;

    @Inject
    private PhotoTaker photoTaker;

    @InjectView(R.id.med_name_input)
    private EditText medNameInput;
    @InjectView(R.id.take_photo_btn)
    private ImageButton takePhotoButton;
    @InjectView(R.id.details_input)
    private EditText detailsInput;
    @InjectView(R.id.current_stock_input)
    private EditText currentStockInput;
    @InjectView(R.id.medication_unit_spinner)
    private Spinner medicationUnitSpinner;

    @InjectResource(R.array.photo_taking_options)
    private String[] photoTakingOptions;
    @InjectResource(R.string.add_medicine_photo)
    private String addMedicinePhoto;
    @InjectResource(R.string.remove_photo)
    private String removePhoto;
    @InjectResource(R.string.required)
    private String required;

    private AlertDialog addMedicinePhotoDialog;
    private OnPhotoTakeListener onPhotoTakeListener;

    private Medicine medicine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMedicationUnitSpinnerAdapter();
        setPhotoTakerOptions();
        createAddMedicinePhotoDialog();
        setOnPhotoTakeListener();
        registerForContextMenu(takePhotoButton);
        prepareMedicine(getIntent().getSerializableExtra(MedicineListFragment.KEY_MEDICINE_TO_BE_EDITED));
    }

    private void prepareMedicine(Serializable medicineToBeEdited) {
        if (medicineToBeEdited == null) {
            medicine = new Medicine();
        } else {
            medicine = (Medicine) medicineToBeEdited;
            updateViewWithMedicineDetails();
        }
    }

    private void updateViewWithMedicineDetails() {
        medNameInput.setText(medicine.getName());
        detailsInput.setText(medicine.getDetails());
        currentStockInput.setText(StringUtils.dropDecimalIfRoundNumber(medicine.getCurrentStock()));
        for (int i = 0; i < medicationUnitSpinner.getAdapter().getCount(); i++) {
            if (medicationUnitSpinner.getAdapter().getItem(i) == medicine.getMedicationUnit()) {
                medicationUnitSpinner.setSelection(i);
                break;
            }
        }
        if (medicine.getPhoto() != null) {
            takePhotoButton.setImageBitmap(BitmapByteArrayConverter.byteArray2Bitmap(medicine.getPhoto()));
        }
    }

    private void setOnPhotoTakeListener() {
        onPhotoTakeListener = new OnPhotoTakeListener() {
            @Override
            public void onPhotoTaken(Bitmap photo) {
                if (photo != null) {
                    medicine.setPhoto(BitmapByteArrayConverter.bitmap2ByteArray(photo));
                    takePhotoButton.setImageBitmap(photo);
                }
            }
        };
    }

    private void setMedicationUnitSpinnerAdapter() {
        ArrayAdapter<MedicationUnit> medicationUnitSpinnerAdapter =
                new ArrayAdapter<MedicationUnit>(this, android.R.layout.simple_spinner_item, MedicationUnit.values());
        medicationUnitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicationUnitSpinner.setAdapter(medicationUnitSpinnerAdapter);
    }

    private void setPhotoTakerOptions() {
        photoTaker.setAspectXForCropping(1);
        photoTaker.setAspectYForCropping(1);
        photoTaker.setImageWidthAfterCropping(PHOTO_SIZE);
        photoTaker.setImageHeightAfterCropping(PHOTO_SIZE);
    }

    private void createAddMedicinePhotoDialog() {
        addMedicinePhotoDialog = new AlertDialog.Builder(this).setTitle(addMedicinePhoto)
                .setItems(photoTakingOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedOptionIndex) {
                        switch (selectedOptionIndex) {
                            case 0:
                                try {
                                    photoTaker.takePhotoFromCamera();
                                } catch (CameraUnavailableException e) {
                                    new AlertDialog.Builder(AddEditMedicineActivity.this)
                                            .setMessage(R.string.camera_unavailable).create().show();
                                    takePhotoButton.setEnabled(false);
                                }
                                break;
                            case 1:
                                photoTaker.pickImageFromGallery();
                                break;
                        }
                    }
                }).create();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v == takePhotoButton && medicine.getPhoto() != null) {
            menu.add(removePhoto);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(removePhoto)) {
            medicine.setPhoto(null);
            takePhotoButton.setImageResource(R.drawable.take_photo);
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoTaker.onActivityResult(requestCode, resultCode, data, onPhotoTakeListener);
    }

    public void onTakePhotoButtonClicked(View view) {
        addMedicinePhotoDialog.show();
    }

    public void onSaveButtonClicked(View view) {
        if (!Validation.validateRequired(medNameInput, required)) {
            return;
        }

        saveMedicine();

        startActivity(new Intent(this, MedicineListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private void saveMedicine() {
        medicine.setName(medNameInput.getText().toString());
        medicine.setDetails(detailsInput.getText().toString());
        medicine.setCurrentStock(getCurrentStockFromUserInput());
        medicine.setMedicationUnit((MedicationUnit) medicationUnitSpinner.getSelectedItem());

        RuntimeExceptionDao<Medicine, Integer> runtimeExceptionDao =
                DbHelper.getHelper().getRuntimeExceptionDao(Medicine.class);
        if (medicine.getId() == 0) {
            runtimeExceptionDao.create(medicine);
        } else {
            runtimeExceptionDao.update(medicine);
        }
    }

    private float getCurrentStockFromUserInput() {
        String currentStock = currentStockInput.getText().toString();
        if ("".equals(currentStock)) {  //user hasn't provided any input
            return 0;
        } else {
            return Float.parseFloat(currentStock);
        }
    }

    public void onCancelButtonClicked(View view) {
        finish();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
    }
}
