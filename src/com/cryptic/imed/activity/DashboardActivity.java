package com.cryptic.imed.activity;

import android.content.Intent;
import android.view.View;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.doctor.DoctorListActivity;
import com.cryptic.imed.activity.medicine.MedicineListActivity;
import com.cryptic.imed.activity.prescription.PrescriptionListActivity;
import com.cryptic.imed.util.view.IndefinitelyProgressingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;

@ContentView(R.layout.dashboard)
public class DashboardActivity extends RoboActivity {
    private static final Logger log = LoggerFactory.getLogger(DashboardActivity.class);

    @InjectResource(R.string.loading)
    private String loadingMessage;

    public void onPrescriptionsClicked(View view) {
        startActivity(PrescriptionListActivity.class);
    }

    public void onMedicinesClicked(View view) {
        startActivity(MedicineListActivity.class);
    }

    public void onDoctorsClicked(View view) {
        startActivity(DoctorListActivity.class);
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
        new IndefinitelyProgressingTask<Void>(this, loadingMessage,
                new IndefinitelyProgressingTask.OnTaskExecutionListener<Void>() {
                    @Override
                    public Void execute() {
                        startActivity(new Intent(DashboardActivity.this, clazz));
                        return null;
                    }

                    @Override
                    public void onSuccess(Void result) {
                        //ignore
                    }

                    @Override
                    public void onException(Exception e) {
                        //ignore
                    }
                }).execute();
    }
}
