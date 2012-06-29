package com.cryptic.imed.activity.prescription;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import com.cryptic.imed.R;
import com.cryptic.imed.fragment.prescription.PrescriptionDetailsFragment;
import com.cryptic.imed.fragment.prescription.PrescriptionListFragment;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.annotation.Nullable;

/**
 * @author sharafat
 */
@ContentView(R.layout.list)
public class PrescriptionListActivity extends RoboFragmentActivity {
    public static final String TAG_PRESCRIPTION_LIST_FRAGMENT = "prescriptionListFragment";
    public static final String TAG_PRESCRIPTION_DETAILS_FRAGMENT = "prescriptionDetailsFragment";

    @InjectView(R.id.details_container)
    @Nullable
    private LinearLayout detailsContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addDoctorListFragmentToLayout();
    }

    private void addDoctorListFragmentToLayout() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (detailsContainer != null) {     //dual pane
            PrescriptionDetailsFragment prescriptionDetailsFragment = new PrescriptionDetailsFragment();
            fragmentTransaction.add(R.id.details_container, prescriptionDetailsFragment, TAG_PRESCRIPTION_DETAILS_FRAGMENT);
        }

        PrescriptionListFragment prescriptionListFragment = new PrescriptionListFragment();
        fragmentTransaction.add(R.id.list_container, prescriptionListFragment, TAG_PRESCRIPTION_LIST_FRAGMENT);

        fragmentTransaction.commit();
    }
}
