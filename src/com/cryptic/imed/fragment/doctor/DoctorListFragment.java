package com.cryptic.imed.fragment.doctor;

import android.app.Application;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import com.cryptic.imed.R;
import com.cryptic.imed.activity.DashboardActivity;
import com.cryptic.imed.activity.doctor.AddEditDoctorActivity;
import com.cryptic.imed.activity.doctor.DoctorDetailsActivity;
import com.cryptic.imed.activity.doctor.DoctorListActivity;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.common.Constants;
import com.cryptic.imed.domain.Doctor;
import com.cryptic.imed.util.adapter.Filterable;
import com.cryptic.imed.util.adapter.FilterableArrayAdapter;
import com.cryptic.imed.util.photo.util.ImageUtils;
import com.cryptic.imed.util.view.CompatibilityUtils;
import com.cryptic.imed.util.view.DualPaneUtils;
import com.cryptic.imed.util.view.TwoLineListItemWithImageView;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectResource;

import java.util.Comparator;

/**
 * @author sharafat
 */
public class DoctorListFragment extends RoboListFragment {
    public static final String KEY_DOCTOR = "doctor";

    private final RuntimeExceptionDao<Doctor, Integer> doctorDao;

    @Inject
    private Application application;
    @Inject
    private LayoutInflater layoutInflater;

    @InjectResource(R.string.edit)
    private String edit;
    @InjectResource(R.string.delete)
    private String delete;

    private boolean dualPane;
    private DoctorDetailsFragment doctorDetailsFragment;

    public DoctorListFragment() {
        doctorDao = DbHelper.getHelper().getRuntimeExceptionDao(Doctor.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new DoctorListAdapter());
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
                ((DoctorListAdapter) getListAdapter()).getFilter().filter(s, new Filter.FilterListener() {
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
            doctorDetailsFragment = (DoctorDetailsFragment)
                    getFragmentManager().findFragmentByTag(DoctorListActivity.TAG_DOCTOR_DETAILS_FRAGMENT);
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
        doctorDetailsFragment.setDoctor(selectedItem == null ? null : (Doctor) selectedItem);
        doctorDetailsFragment.updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Doctor selectedDoctor = (Doctor) getListAdapter().getItem(info.position);

        menu.setHeaderTitle(selectedDoctor.getName());
        menu.add(Menu.NONE, Constants.CONTEXT_MENU_EDIT, 0, edit);
        menu.add(Menu.NONE, Constants.CONTEXT_MENU_DELETE, 1, delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Doctor selectedDoctor = (Doctor) getListAdapter().getItem(info.position);

        switch (item.getItemId()) {
            case Constants.CONTEXT_MENU_EDIT:
                Intent intent = new Intent(application, AddEditDoctorActivity.class);
                intent.putExtra(KEY_DOCTOR, selectedDoctor);
                startActivity(intent);
                return true;
            case Constants.CONTEXT_MENU_DELETE:
                deleteDoctorAndUpdateView(selectedDoctor);
                return true;
        }

        return false;
    }

    public void deleteDoctorAndUpdateView(Doctor selectedDoctor) {
        deleteDoctor(selectedDoctor);
        int selectedDoctorIndex = updateDoctorList(selectedDoctor);
        if (dualPane) {
            try {
                selectItemInList(selectedDoctorIndex);
            } catch (ArrayIndexOutOfBoundsException e) {
                selectItemInList(selectedDoctorIndex - 1);
            }
        }
    }

    private void deleteDoctor(Doctor selectedDoctor) {
        selectedDoctor.setDeleted(true);
        doctorDao.update(selectedDoctor);
    }

    private int updateDoctorList(Doctor selectedDoctor) {
        DoctorListAdapter doctorListAdapter = (DoctorListAdapter) getListAdapter();

        int selectedDoctorIndex = doctorListAdapter.getPosition(selectedDoctor);

        doctorListAdapter.remove(selectedDoctor);
        doctorListAdapter.notifyDataSetInvalidated();

        return selectedDoctorIndex;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.doctor_list_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_doctor:
                startActivity(new Intent(application, AddEditDoctorActivity.class));
                break;
            case android.R.id.home:
                startActivity(new Intent(application, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }

        return false;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Doctor selectedDoctor = (Doctor) getListAdapter().getItem(position);

        if (dualPane) {
            getListView().setItemChecked(position, true);
            updateDetailsFragment(selectedDoctor);
        } else {
            Intent intent = new Intent(application, DoctorDetailsActivity.class);
            intent.putExtra(KEY_DOCTOR, selectedDoctor);
            startActivity(intent);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
    }


    private class DoctorListAdapter extends FilterableArrayAdapter {
        DoctorListAdapter() {
            super(application, 0);

            addAll(doctorDao.queryForEq("deleted", false));

            sort(new Comparator<Filterable>() {
                @Override
                public int compare(Filterable lhs, Filterable rhs) {
                    return ((Doctor) lhs).getName().compareTo(((Doctor) rhs).getName());
                }
            });
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Doctor doctor = (Doctor) getItem(position);
            return TwoLineListItemWithImageView.getView(layoutInflater, convertView, parent,
                    doctor.getName(), doctor.getAddress(),
                    doctor.getPhoto() != null ? doctor.getPhoto() : ImageUtils.getByteArray(
                            BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_photo)));
        }
    }
}
