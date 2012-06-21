package com.cryptic.imed.fragment;

import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ListView;
import com.cryptic.imed.R;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.utils.FilterableArrayAdapter;
import com.cryptic.imed.utils.TwoLineListItemWithImageView;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

/**
 * @author sharafat
 */
public class MedicineListFragment extends RoboListFragment {
    private static final Logger log = LoggerFactory.getLogger(MedicineListFragment.class);

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;

    @InjectResource(R.string.x_units_available)
    private String xUnitsAvailable;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new MedicineListAdapter());
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        EditText filterInput = (EditText) getActivity().findViewById(R.id.filter_input);
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
                ((MedicineListAdapter) getListAdapter()).getFilter().filter(s);
            }
        });

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.medicine_list_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_medicine:
                log.debug("New Medicine menu selected");
                break;
        }

        return true;
    }


    private class MedicineListAdapter extends FilterableArrayAdapter {
        @SuppressWarnings("unchecked")
        MedicineListAdapter() {
            super(application, 0);
            addAll(DbHelper.getHelper().getRuntimeExceptionDao(Medicine.class).queryForAll());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Medicine medicine = (Medicine) getItem(position);
            return TwoLineListItemWithImageView.getView(layoutInflater, convertView, parent, medicine.getName(),
                    String.format(xUnitsAvailable, medicine.getCurrentStock() == 0 ? "0" : medicine.getCurrentStock(),
                            medicine.getMedicationUnit()), medicine.getPhoto());
        }
    }
}
