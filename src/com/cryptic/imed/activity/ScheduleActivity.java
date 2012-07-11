package com.cryptic.imed.activity;

import android.os.Bundle;
import com.cryptic.imed.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * @author sharafat
 */
@ContentView(R.layout.schedules)
public class ScheduleActivity extends RoboActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onDateClicked(int year, int month, int dayOfMonth) {
        //TODO
    }

    public void onYearMonthChanged(int newYear, int newMonth) {
        //TODO
    }
}
