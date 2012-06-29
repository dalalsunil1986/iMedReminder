package com.cryptic.imed.fragment.prescription;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.*;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.DashboardActivity;
import com.cryptic.imed.activity.prescription.AddEditPrescriptionActivity;
import com.cryptic.imed.activity.prescription.PrescriptionDetailsActivity;
import com.cryptic.imed.activity.prescription.PrescriptionListActivity;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.common.Constants;
import com.cryptic.imed.domain.Prescription;
import com.cryptic.imed.util.*;
import com.cryptic.imed.util.Filterable;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import java.util.Comparator;

/**
 * @author sharafat
 */
public class PrescriptionListFragment extends RoboListFragment {
    public static final String KEY_PRESCRIPTION = "prescription";

    private final RuntimeExceptionDao<Prescription, Integer> prescriptionDao;

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;

    @InjectResource(R.string.edit)
    private String edit;
    @InjectResource(R.string.delete)
    private String delete;
    @InjectResource(R.string.added_on)
    private String addedOn;

    private boolean dualPane;
    private PrescriptionDetailsFragment prescriptionDetailsFragment;

    public PrescriptionListFragment() {
        prescriptionDao = DbHelper.getHelper().getRuntimeExceptionDao(Prescription.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new PrescriptionListAdapter());
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        EditText filterInput = (EditText) getActivity().findViewById(R.id.filter_input);
        filterInput.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                ((PrescriptionListAdapter) getListAdapter()).getFilter().filter(s, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (dualPane) {
                            selectFirstItemInList();
                        }
                    }
                });
            }
        });

        registerForContextMenu(getListView());
        setHasOptionsMenu(true);
        CompatibilityUtils.setHomeButtonEnabled(true, getActivity());

        dualPane = DualPaneUtils.isDualPane(getActivity(), R.id.details_container);
        if (dualPane) {
            prescriptionDetailsFragment = (PrescriptionDetailsFragment)
                    getFragmentManager().findFragmentByTag(PrescriptionListActivity.TAG_PRESCRIPTION_DETAILS_FRAGMENT);
            selectFirstItemInList();
        }
    }

    private void selectFirstItemInList() {
        selectItemInList(0);
    }

    private void selectItemInList(int position) {
        int itemCount = getListAdapter().getCount();
        if (itemCount > 0) {
            if (position < itemCount) {
                getListView().setItemChecked(position, true);
                updateDetailsFragment(getListAdapter().getItem(position));
            } else {
                throw new ArrayIndexOutOfBoundsException("list item count = " + itemCount
                        + ", but position given = " + position);
            }
        } else {
            updateDetailsFragment(null);
        }
    }

    private void updateDetailsFragment(Object selectedItem) {
        prescriptionDetailsFragment.setPrescription(selectedItem == null ? null : (Prescription) selectedItem);
        prescriptionDetailsFragment.updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Prescription selectedPrescription = (Prescription) getListAdapter().getItem(info.position);

        menu.setHeaderTitle(selectedPrescription.getTitle());
        menu.add(Menu.NONE, Constants.CONTEXT_MENU_EDIT, 0, edit);
        menu.add(Menu.NONE, Constants.CONTEXT_MENU_DELETE, 1, delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Prescription selectedPrescription = (Prescription) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case Constants.CONTEXT_MENU_EDIT:
                Intent intent = new Intent(application, AddEditPrescriptionActivity.class);
                intent.putExtra(KEY_PRESCRIPTION, selectedPrescription);
                startActivity(intent);
                return true;
            case Constants.CONTEXT_MENU_DELETE:
                deletePrescriptionAndUpdateView(selectedPrescription);
                return true;
        }

        return false;
    }

    public void deletePrescriptionAndUpdateView(Prescription prescription) {
        deletePrescription(prescription);
        int selectedPrescriptionIndex = updatePrescriptionList(prescription);
        if (dualPane) {
            try {
                selectItemInList(selectedPrescriptionIndex);
            } catch (ArrayIndexOutOfBoundsException e) {
                selectItemInList(selectedPrescriptionIndex - 1);
            }
        }
    }

    private void deletePrescription(Prescription prescription) {
        prescriptionDao.delete(prescription);
    }

    private int updatePrescriptionList(Prescription selectedPrescription) {
        PrescriptionListAdapter prescriptionListAdapter = (PrescriptionListAdapter) getListAdapter();

        int selectedPrescriptionIndex = prescriptionListAdapter.getPosition(selectedPrescription);

        prescriptionListAdapter.remove(selectedPrescription);
        prescriptionListAdapter.notifyDataSetInvalidated();

        return selectedPrescriptionIndex;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.prescription_list_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_prescription:
                startActivity(new Intent(application, AddEditPrescriptionActivity.class));
                break;
            case android.R.id.home:
                startActivity(new Intent(application, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }

        return false;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Prescription selectedPrescription = (Prescription) getListAdapter().getItem(position);

        if (dualPane) {
            getListView().setItemChecked(position, true);
            updateDetailsFragment(selectedPrescription);
        } else {
            Intent intent = new Intent(application, PrescriptionDetailsActivity.class);
            intent.putExtra(KEY_PRESCRIPTION, selectedPrescription);
            startActivity(intent);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
    }


    private class PrescriptionListAdapter extends FilterableArrayAdapter {
        PrescriptionListAdapter() {
            super(application, 0);

            addAll(prescriptionDao.queryForAll());

            sort(new Comparator<Filterable>() {
                @Override
                public int compare(Filterable lhs, Filterable rhs) {
                    return ((Prescription) lhs).getTitle().compareTo(((Prescription) rhs).getTitle());
                }
            });
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        Build.VERSION.SDK_INT < 11
                                ? android.R.layout.simple_list_item_2
                                : android.R.layout.simple_list_item_activated_2,
                        parent, false);
            }

            TextView primaryTextView = (TextView) convertView.findViewById(android.R.id.text1);
            TextView secondaryTextView = (TextView) convertView.findViewById(android.R.id.text2);

            Prescription prescription = (Prescription) getItem(position);

            primaryTextView.setText(prescription.getTitle());
            secondaryTextView.setText(String.format(addedOn,
                    DateFormat.format(Constants.PRESCRIPTION_DETAILS_DATE_FORMAT, prescription.getIssueDate())));

            return convertView;
        }
    }
}
