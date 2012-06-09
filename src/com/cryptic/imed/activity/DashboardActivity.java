package com.cryptic.imed.activity;

import android.os.Bundle;
import android.view.View;
import com.cryptic.imed.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.dashboard)
public class DashboardActivity extends RoboActivity {
    private static final Logger log = LoggerFactory.getLogger(DashboardActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onPrescriptionsClicked(View view) {
        log.debug("Prescriptions clicked");
    }

    public void onMedicinesClicked(View view) {
        log.debug("Medicines clicked");
    }

    public void onDoctorsClicked(View view) {
        log.debug("Doctors clicked");
    }

    public void onPharmaciesClicked(View view) {
        log.debug("Pharmacies clicked");
    }

    public void onAppointmentsClicked(View view) {
        log.debug("Appointments clicked");
    }

    public void onSchedulesClicked(View view) {
        log.debug("Schedules clicked");
    }
}
