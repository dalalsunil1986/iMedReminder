package com.cryptic.imed.fragment.medicine;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.medicine.AddEditMedicineActivity;
import com.cryptic.imed.activity.DashboardActivity;
import com.cryptic.imed.activity.medicine.MedicineListActivity;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.photo.util.BitmapByteArrayConverter;
import com.cryptic.imed.util.CompatibilityUtils;
import com.cryptic.imed.util.DualPaneUtils;
import com.cryptic.imed.util.StringUtils;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * @author sharafat
 */
public class MedicineDetailsFragment extends RoboFragment {
    private final RuntimeExceptionDao<Medicine, Integer> medicineDao;

    @Inject
    private Application application;

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

    private boolean dualPanel;
    private MedicineListFragment medicineListFragment;
    private Medicine medicine;

    public MedicineDetailsFragment() {
        medicineDao = DbHelper.getHelper().getRuntimeExceptionDao(Medicine.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CompatibilityUtils.setHomeButtonEnabled(true, getActivity());

        dualPanel = DualPaneUtils.isDualPane(getActivity(), R.id.list_container);
        if (dualPanel) {
            medicineListFragment = (MedicineListFragment)
                    getFragmentManager().findFragmentByTag(MedicineListActivity.TAG_MEDICINE_LIST_FRAGMENT);
        }
    }

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

            setHasOptionsMenu(true);
        } else {
            medNameTextView.setText("");
            medDetailsTextView.setText("");
            currentStockTextView.setText("");
            medPhotoImageView.setImageDrawable(null);

            detailsHeading.setVisibility(View.GONE);
            currentStockHeading.setVisibility(View.GONE);

            setHasOptionsMenu(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_delete_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(application, AddEditMedicineActivity.class);
                intent.putExtra(MedicineListFragment.KEY_MEDICINE, medicine);
                startActivity(intent);
                break;
            case R.id.menu_delete:
                if (dualPanel) {
                    medicineListFragment.deleteMedicineAndUpdateView(medicine);
                } else {
                    medicine.setDeleted(true);
                    medicineDao.update(medicine);

                    Intent medicineListActivityIntent = new Intent(application, MedicineListActivity.class);
                    startActivity(medicineListActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                }
                break;
            case android.R.id.home:
                startActivity(new Intent(application, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }

        return false;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
}
