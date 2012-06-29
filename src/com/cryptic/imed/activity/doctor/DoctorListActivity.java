package com.cryptic.imed.activity.doctor;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import com.cryptic.imed.R;
import com.cryptic.imed.fragment.doctor.DoctorDetailsFragment;
import com.cryptic.imed.fragment.doctor.DoctorListFragment;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.annotation.Nullable;

/**
 * @author sharafat
 */
@ContentView(R.layout.list)
public class DoctorListActivity extends RoboFragmentActivity {
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
            DoctorDetailsFragment doctorDetailsFragment = new DoctorDetailsFragment();
            fragmentTransaction.add(R.id.details_container, doctorDetailsFragment);
        }

        DoctorListFragment doctorListFragment = new DoctorListFragment();
        fragmentTransaction.add(R.id.list_container, doctorListFragment);

        fragmentTransaction.commit();
    }
}
