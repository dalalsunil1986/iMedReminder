package com.cryptic.imed.fragment.prescription;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.DashboardActivity;
import com.cryptic.imed.activity.prescription.AddEditPrescriptionActivity;
import com.cryptic.imed.activity.prescription.PrescriptionListActivity;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.common.Constants;
import com.cryptic.imed.domain.Doctor;
import com.cryptic.imed.domain.Prescription;
import com.cryptic.imed.domain.PrescriptionMedicine;
import com.cryptic.imed.util.photo.util.ImageUtils;
import com.cryptic.imed.util.view.CompatibilityUtils;
import com.cryptic.imed.util.view.DualPaneUtils;
import com.cryptic.imed.util.view.TwoLineListItemWithImageView;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.util.ArrayList;

/**
 * @author sharafat
 */
public class PrescriptionDetailsFragment extends RoboFragment {
    private final RuntimeExceptionDao<Prescription, Integer> prescriptionDao;
    private final RuntimeExceptionDao<Doctor, Integer> doctorDao;

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;

    @InjectView(R.id.prescription_details_view)
    private ScrollView prescriptionDetailsView;
    @InjectView(R.id.prescription_title)
    private TextView prescriptionTitleTextView;
    @InjectView(R.id.prescription_details)
    private TextView prescriptionDetailsTextView;
    @InjectView(R.id.issue_date)
    private TextView issueDateTextView;
    @InjectView(R.id.prescribed_by)
    private TextView prescribedByTextView;
    @InjectView(R.id.medicine_list_section)
    private LinearLayout medicineListSection;
    @InjectView(R.id.medicine_list)
    private LinearLayout medicineListView;

    @InjectResource(R.string.not_available)
    private String notAvailable;
    @InjectResource(R.string.medicine_list_item_details)
    private String medicineListItemDetails;
    @InjectResource(R.string.everyday)
    private String everyday;
    @InjectResource(R.string.every_x_days)
    private String everyXDays;
    @InjectResource(R.drawable.ic_default_med)
    private Drawable defaultMedicinePhoto;

    private boolean dualPanel;
    private PrescriptionListFragment prescriptionListFragment;
    private Prescription prescription;

    public PrescriptionDetailsFragment() {
        OrmLiteSqliteOpenHelper dbHelper = DbHelper.getHelper();

        prescriptionDao = dbHelper.getRuntimeExceptionDao(Prescription.class);
        doctorDao = dbHelper.getRuntimeExceptionDao(Doctor.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CompatibilityUtils.setHomeButtonEnabled(true, getActivity());

        dualPanel = DualPaneUtils.isDualPane(getActivity(), R.id.list_container);
        if (dualPanel) {
            prescriptionListFragment = (PrescriptionListFragment)
                    getFragmentManager().findFragmentByTag(PrescriptionListActivity.TAG_PRESCRIPTION_LIST_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.prescription_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView();
    }

    public void updateView() {
        if (prescription != null) {
            prescriptionTitleTextView.setText(prescription.getTitle());
            prescriptionDetailsTextView.setText(prescription.getDetails());
            issueDateTextView.setText(
                    DateFormat.format(Constants.PRESCRIPTION_DETAILS_DATE_FORMAT, prescription.getIssueDate()));

            if (prescription.getPrescribedBy() != null) {
                doctorDao.refresh(prescription.getPrescribedBy());
                prescribedByTextView.setText(prescription.getPrescribedBy().getName());
                prescribedByTextView.setVisibility(View.VISIBLE);
            } else {
                prescribedByTextView.setVisibility(View.GONE);
            }

            prescriptionDao.refresh(prescription);
            prescription.setMedicines(new ArrayList<PrescriptionMedicine>(prescription.getMedicines()));

            if (prescription.getMedicines().size() > 0) {
                for (PrescriptionMedicine prescriptionMedicine : prescription.getMedicines()) {
                    medicineListView.addView(TwoLineListItemWithImageView.getView(
                            layoutInflater, null, medicineListView,
                            prescriptionMedicine.getMedicine().getName(),
                            String.format(medicineListItemDetails,
                                    prescriptionMedicine.getTotalDaysToTake(),
                                    prescriptionMedicine.getDosesToTake(),
                                    prescriptionMedicine.getDayInterval() == 0
                                            ? everyday
                                            : String.format(everyXDays, prescriptionMedicine.getDayInterval()),
                                    DateFormat.format(Constants.GENERAL_DATE_FORMAT, prescriptionMedicine.getStartDate())),
                            ImageUtils.getNonEmptyImage(prescriptionMedicine.getMedicine().getPhoto(), defaultMedicinePhoto)));
                }

                medicineListSection.setVisibility(View.VISIBLE);
            } else {
                medicineListSection.setVisibility(View.GONE);
            }

            prescriptionDetailsView.setVisibility(View.VISIBLE);
            setHasOptionsMenu(true);
        } else {
            prescriptionDetailsView.setVisibility(View.GONE);
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
                Intent intent = new Intent(application, AddEditPrescriptionActivity.class);
                intent.putExtra(PrescriptionListFragment.KEY_PRESCRIPTION, prescription);
                startActivity(intent);
                break;
            case R.id.menu_delete:
                if (dualPanel) {
                    prescriptionListFragment.deletePrescriptionAndUpdateView(prescription);
                } else {
                    prescriptionDao.delete(prescription);

                    Intent prescriptionListActivityIntent = new Intent(application, PrescriptionListActivity.class);
                    startActivity(prescriptionListActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }
}
