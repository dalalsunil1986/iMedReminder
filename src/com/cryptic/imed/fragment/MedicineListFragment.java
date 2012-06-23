package com.cryptic.imed.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.AddEditMedicineActivity;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.utils.Filterable;
import com.cryptic.imed.utils.FilterableArrayAdapter;
import com.cryptic.imed.utils.StringUtils;
import com.cryptic.imed.utils.TwoLineListItemWithImageView;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import java.util.Comparator;

/**
 * @author sharafat
 */
public class MedicineListFragment extends RoboListFragment {
    public static final String KEY_MEDICINE_TO_BE_EDITED = "medicine_to_be_edited";

    private static final int CONTEXT_MENU_EDIT = 0;
    private static final int CONTEXT_MENU_DELETE = 1;

    private final RuntimeExceptionDao<Medicine, Integer> medicineDao;

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;

    @InjectResource(R.string.x_units_available)
    private String xUnitsAvailable;
    @InjectResource(R.string.edit)
    private String edit;
    @InjectResource(R.string.delete)
    private String delete;

    public MedicineListFragment() {
        medicineDao = DbHelper.getHelper().getRuntimeExceptionDao(Medicine.class);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
    }

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

        registerForContextMenu(getListView());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Medicine selectedMedicine = (Medicine) getListAdapter().getItem(info.position);

        menu.setHeaderTitle(selectedMedicine.getName());
        menu.add(Menu.NONE, CONTEXT_MENU_EDIT, 0, edit);
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE, 0, delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Medicine selectedMedicine = (Medicine) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                Intent intent = new Intent(application, AddEditMedicineActivity.class);
                intent.putExtra(KEY_MEDICINE_TO_BE_EDITED, selectedMedicine);
                startActivity(intent);
                return true;
            case CONTEXT_MENU_DELETE:
                deleteMedicine(selectedMedicine);
                updateMedicineList(selectedMedicine);
                return true;
        }

        return false;
    }

    private void deleteMedicine(Medicine selectedMedicine) {
        selectedMedicine.setDeleted(true);
        medicineDao.update(selectedMedicine);
    }

    private void updateMedicineList(Medicine selectedMedicine) {
        MedicineListAdapter medicineListAdapter = (MedicineListAdapter) getListAdapter();
        medicineListAdapter.remove(selectedMedicine);
        medicineListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.medicine_list_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_medicine:
                startActivity(new Intent(application, AddEditMedicineActivity.class));
                break;
        }

        return true;
    }


    private class MedicineListAdapter extends FilterableArrayAdapter {
        @SuppressWarnings("unchecked")
        MedicineListAdapter() {
            super(application, 0);

            addAll(medicineDao.queryForEq("deleted", false));

            sort(new Comparator<Filterable>() {
                @Override
                public int compare(Filterable lhs, Filterable rhs) {
                    return ((Medicine) lhs).getName().compareTo(((Medicine) rhs).getName());
                }
            });
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Medicine medicine = (Medicine) getItem(position);
            return TwoLineListItemWithImageView.getView(layoutInflater, convertView, parent, medicine.getName(),
                    String.format(xUnitsAvailable, StringUtils.dropDecimalIfRoundNumber(medicine.getCurrentStock()),
                            medicine.getMedicationUnit()), medicine.getPhoto());
        }
    }
}
