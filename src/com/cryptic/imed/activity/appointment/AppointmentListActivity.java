package com.cryptic.imed.activity.appointment;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.cryptic.imed.R;
import com.cryptic.imed.fragment.appointment.AppointmentDetailsFragment;
import com.cryptic.imed.fragment.appointment.AppointmentListFragment;
import com.cryptic.imed.util.view.ViewUtils;
import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.annotation.Nullable;

/**
 * @author sharafat
 */
@ContentView(R.layout.list)
public class AppointmentListActivity extends RoboFragmentActivity {
    @Inject
    private AppointmentListFragment appointmentListFragment;
    @Inject
    private AppointmentDetailsFragment appointmentDetailsFragment;

    @InjectView(R.id.details_container)
    @Nullable
    private LinearLayout detailsContainer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.addListFragmentToLayout(this, detailsContainer, appointmentListFragment, appointmentDetailsFragment);
    }
}
