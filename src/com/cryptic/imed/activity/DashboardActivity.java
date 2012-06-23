package com.cryptic.imed.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.cryptic.imed.R;
import com.cryptic.imed.utils.IndefinitelyProgressingTask;
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
        startActivity(MedicineListActivity.class);
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

    private void startActivity(final Class<?> clazz) {
        new IndefinitelyProgressingTask<Void>(this, "Loading...",
                new IndefinitelyProgressingTask.OnTaskExecutionListener<Void>() {
                    @Override
                    public Void execute() {
                        startActivity(new Intent(DashboardActivity.this, clazz));
                        return null;
                    }

                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onException(Exception e) {
                    }
                }).execute();
    }
}
