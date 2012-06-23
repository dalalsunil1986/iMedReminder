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
import com.cryptic.imed.activity.MedicineDetailsActivity;
import com.cryptic.imed.activity.MedicineListActivity;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.domain.Medicine;
import com.cryptic.imed.util.*;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import java.util.Comparator;

/**
 * @author sharafat
 */
public class MedicineListFragment extends RoboListFragment {
    public static final String KEY_MEDICINE = "medicine";

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

    private boolean dualPane;
    private MedicineDetailsFragment medicineDetailsFragment;

    public MedicineListFragment() {
        medicineDao = DbHelper.getHelper().getRuntimeExceptionDao(Medicine.class);
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

        dualPane = DualPaneUtils.isDualPane(getActivity(), R.id.details_container);
        if (dualPane) {
            medicineDetailsFragment = (MedicineDetailsFragment)
                    getFragmentManager().findFragmentByTag(MedicineListActivity.TAG_MEDICINE_DETAILS_FRAGMENT);
            selectFirstItemInList();
        }
    }

    private void selectFirstItemInList() {
        if (getListAdapter().getCount() > 0) {
            getListView().setItemChecked(0, true);
            updateDetailsFragment(getListAdapter().getItem(0));
        } else {
            updateDetailsFragment(null);
        }
    }

    private void updateDetailsFragment(Object selectedItem) {
        medicineDetailsFragment.setMedicine(selectedItem == null ? null : (Medicine) selectedItem);
        medicineDetailsFragment.updateView();
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
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE, 1, delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Medicine selectedMedicine = (Medicine) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                Intent intent = new Intent(application, AddEditMedicineActivity.class);
                intent.putExtra(KEY_MEDICINE, selectedMedicine);
                startActivity(intent);
                return true;
            case CONTEXT_MENU_DELETE:
                deleteMedicineAndUpdateList(selectedMedicine);
                return true;
        }

        return false;
    }

    public void deleteMedicineAndUpdateList(Medicine selectedMedicine) {
        deleteMedicine(selectedMedicine);
        updateMedicineList(selectedMedicine);
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Medicine selectedMedicine = (Medicine) getListAdapter().getItem(position);

        if (dualPane) {
            getListView().setItemChecked(position, true);
            updateDetailsFragment(selectedMedicine);
        } else {
            Intent intent = new Intent(application, MedicineDetailsActivity.class);
            intent.putExtra(KEY_MEDICINE, selectedMedicine);
            startActivity(intent);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
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
