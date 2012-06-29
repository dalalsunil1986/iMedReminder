package com.cryptic.imed.activity.prescription;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.cryptic.imed.R;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.util.*;
import com.cryptic.imed.util.adapter.Filterable;
import com.cryptic.imed.util.adapter.FilterableArrayAdapter;
import com.cryptic.imed.util.photo.util.ImageUtils;
import com.cryptic.imed.util.view.CompatibilityUtils;
import com.cryptic.imed.util.view.TwoLineListItemWithImageView;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import java.util.Comparator;

/**
 * @author sharafat
 */
@ContentView(R.layout.list_container)
public class AddMedicineActivity extends RoboActivity {
    public static final String KEY_SELECTED_MEDICINE = "selected_medicine";

    private static final int REQ_CODE_GET_DOSAGE_DETAILS = 1;

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;

    @InjectView(R.id.list_container)
    private LinearLayout linearLayout;
    @InjectView(R.id.filter_input)
    private EditText filterInput;

    @InjectResource(R.string.x_units_available)
    private String xUnitsAvailable;
    @InjectResource(R.drawable.ic_default_med)
    private Drawable defaultMedicinePhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutInflater.inflate(R.layout.list_view, linearLayout, true);

        final ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(new MedicineListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Medicine selectedMedicine = (Medicine) listView.getAdapter().getItem(position);

                Intent intent = new Intent(application, MedicineScheduleActivity.class);
                intent.putExtra(KEY_SELECTED_MEDICINE, selectedMedicine);
                startActivityForResult(intent, REQ_CODE_GET_DOSAGE_DETAILS);
            }
        });

        filterInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                ((MedicineListAdapter) listView.getAdapter()).getFilter().filter(s);
            }
        });

        CompatibilityUtils.setHomeButtonEnabled(true, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_GET_DOSAGE_DETAILS && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private class MedicineListAdapter extends FilterableArrayAdapter {
        MedicineListAdapter() {
            super(application, 0);

            RuntimeExceptionDao<Medicine, Integer> medicineDao =
                    DbHelper.getHelper().getRuntimeExceptionDao(Medicine.class);
            addAll(medicineDao.queryForEq("deleted", false));

            sort(new Comparator<Filterable>() {
                @Override
                public int compare(Filterable lhs, Filterable rhs) {
                    return ((Medicine) lhs).getName().compareTo(((Medicine) rhs).getName());
                }
            });

            DbHelper.release();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Medicine medicine = (Medicine) getItem(position);
            return TwoLineListItemWithImageView.getView(layoutInflater, convertView, parent, medicine.getName(),
                    String.format(xUnitsAvailable, StringUtils.dropDecimalIfRoundNumber(medicine.getCurrentStock()),
                            medicine.getMedicationUnit()),
                    ImageUtils.getNonEmptyImage(medicine.getPhoto(), defaultMedicinePhoto));
        }
    }
}
