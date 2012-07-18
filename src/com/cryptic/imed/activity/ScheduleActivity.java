package com.cryptic.imed.activity;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.cryptic.android.widget.calendar.CellBackgroundImage;
import com.cryptic.android.widget.calendar.SimpleCalendarView;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.prescription.PrescriptionDetailsActivity;
import com.cryptic.imed.controller.ScheduleController;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.domain.Prescription;
import com.cryptic.imed.domain.PrescriptionMedicine;
import com.cryptic.imed.fragment.prescription.PrescriptionListFragment;
import com.cryptic.imed.util.DateWithoutTime;
import com.cryptic.imed.util.photo.util.ImageUtils;
import com.cryptic.imed.util.view.TwoLineListItemWithImageView;
import com.cryptic.imed.util.view.ViewUtils;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sharafat
 */
@ContentView(R.layout.schedules)
public class ScheduleActivity extends RoboActivity {
    private static final Logger log = LoggerFactory.getLogger(ScheduleActivity.class);

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;
    @Inject
    private ScheduleController scheduleController;

    @InjectView(R.id.calendar_view)
    private SimpleCalendarView calendarView;
    @InjectView(android.R.id.list)
    private ListView listView;

    @InjectResource(R.drawable.ic_default_med)
    private Drawable defaultMedicinePhoto;
    @InjectResource(R.string.x_doses)
    private String xDoses;

    private Map<DateWithoutTime, List<PrescriptionMedicine>> schedule;
    private ScheduleActivity.MedicineScheduleListAdapter medicineScheduleListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        medicineScheduleListAdapter = new MedicineScheduleListAdapter();
        listView.setAdapter(medicineScheduleListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prescription prescription = medicineScheduleListAdapter.getItem(position).getPrescription();
                scheduleController.refresh(prescription);

                Intent intent = new Intent(application, PrescriptionDetailsActivity.class);
                intent.putExtra(PrescriptionListFragment.KEY_PRESCRIPTION, prescription);
                startActivity(intent);
            }
        });

        markScheduledCalendarDates();
    }

    private void markScheduledCalendarDates() {
        schedule = scheduleController.list(calendarView.getYear(), calendarView.getMonth(), calendarView.getWeekStartDay());

        Map<Integer, CellBackgroundImage> cellBackgroundImages = new HashMap<Integer, CellBackgroundImage>(schedule.size());

        CellBackgroundImage cellBackgroundImage = new CellBackgroundImage(
                getResources().getDrawable(R.drawable.bg_schedule_within_month),
                getResources().getDrawable(R.drawable.bg_schedule_outside_month));

        log.debug("Schedules...");
        for (DateWithoutTime date : schedule.keySet()) {
            log.debug("Date: {}, presMed count: {} schedules: {}",
                    new Object[]{date, schedule.get(date).size(), schedule.get(date)});

            cellBackgroundImages.put(calendarView.getCellPositionInCalendarView(date.getMonth(), date.getDate()),
                    cellBackgroundImage);
        }

        calendarView.setCellBackgroundImages(cellBackgroundImages);
    }

    public void onDateClicked(View view, int year, int month, int dayOfMonth) {
        DateWithoutTime clickedDate = new DateWithoutTime(year, month, dayOfMonth);
        preparePrescriptionMedicineList(schedule.containsKey(clickedDate) ? schedule.get(clickedDate) : null);
    }

    private void preparePrescriptionMedicineList(List<PrescriptionMedicine> prescriptionMedicineList) {
        medicineScheduleListAdapter.setPrescriptionMedicineList(
                prescriptionMedicineList != null ? prescriptionMedicineList : new ArrayList<PrescriptionMedicine>(0));
        ViewUtils.setListViewHeightBasedOnChildren(listView);
    }

    public void onYearMonthChanged(View view, int newYear, int newMonth) {
        markScheduledCalendarDates();
        preparePrescriptionMedicineList(null);
    }


    private class MedicineScheduleListAdapter extends ArrayAdapter<PrescriptionMedicine> {
        public MedicineScheduleListAdapter() {
            super(ScheduleActivity.this, 0);
        }

        public void setPrescriptionMedicineList(List<PrescriptionMedicine> prescriptionMedicineList) {
            clear();

            for (PrescriptionMedicine prescriptionMedicine : prescriptionMedicineList) {
                add(prescriptionMedicine);
            }

            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PrescriptionMedicine prescriptionMedicine = (PrescriptionMedicine) getItem(position);
            Medicine medicine = prescriptionMedicine.getMedicine();

            return TwoLineListItemWithImageView.getView(layoutInflater, convertView, parent,
                    medicine.getName(),
                    String.format(xDoses, prescriptionMedicine.getDosesToTake(), medicine.getMedicationUnit()),
                    ImageUtils.getNonEmptyImage(medicine.getPhoto(), defaultMedicinePhoto));
        }
    }
}
