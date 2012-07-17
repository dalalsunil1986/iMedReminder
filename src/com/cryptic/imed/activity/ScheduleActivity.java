package com.cryptic.imed.activity;

import android.os.Bundle;
import android.view.View;
import com.cryptic.android.widget.calendar.CellBackgroundImage;
import com.cryptic.android.widget.calendar.SimpleCalendarView;
import com.cryptic.imed.R;
import com.cryptic.imed.controller.ScheduleController;
import com.cryptic.imed.domain.PrescriptionMedicine;
import com.cryptic.imed.util.DateWithoutTime;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

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
    private ScheduleController scheduleController;

    @InjectView(R.id.calendar_view)
    private SimpleCalendarView calendarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markScheduledCalendarDates();
    }

    private void markScheduledCalendarDates() {
        Map<DateWithoutTime, List<PrescriptionMedicine>> schedule =
                scheduleController.list(calendarView.getYear(), calendarView.getMonth(), calendarView.getWeekStartDay());

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
        //TODO
    }

    public void onYearMonthChanged(View view, int newYear, int newMonth) {
        markScheduledCalendarDates();
    }
}
