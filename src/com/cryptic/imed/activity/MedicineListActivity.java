package com.cryptic.imed.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.cryptic.imed.R;
import com.cryptic.imed.fragment.MedicineListFragment;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;

/**
 * @author sharafat
 */
@ContentView(R.layout.list_container)
public class MedicineListActivity extends RoboFragmentActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMedicineListFragmentToLayout();
    }

    private void addMedicineListFragmentToLayout() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MedicineListFragment medicineListFragment = new MedicineListFragment();
        fragmentTransaction.add(R.id.list_container, medicineListFragment);
        fragmentTransaction.commit();
    }
}
