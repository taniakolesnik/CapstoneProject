package uk.co.taniakolesnik.capstoneproject.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.models.City;
import uk.co.taniakolesnik.capstoneproject.models.User;
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.DatePickerFragment;
import uk.co.taniakolesnik.capstoneproject.ui_tools.TimePickerFragment;

public class WorkshopDetailsActivity extends AppCompatActivity
        implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener, AdapterView.OnItemSelectedListener {

    public static final String INTENT_OPEN_ADD_WORKSHOP_DETAILS = "add_workshop";
    public static final String INTENT_OPEN_UPDATE_WORKSHOP_DETAILS = "update_workshop";
    @BindView(R.id.ws_pick_date_bn)
    Button pickDateButton;
    @BindView(R.id.ws_pick_time_bn)
    Button pickTimeButton;
    @BindView(R.id.sing_in_or_out_ws_from_user)
    Button signToWorkshopButton;
    @BindView(R.id.ws_description_et)
    TextInputEditText descEditText;
    @BindView(R.id.ws_name_et)
    TextInputEditText nameEditText;
    @BindView(R.id.ws_address_et)
    TextInputEditText addressEditText;
    @BindView(R.id.cities_spinner_wsPage)
    Spinner mSpinner;
    @BindView(R.id.users_list)
    ListView mListView;

    private String id;
    private String date;
    private String time;
    private boolean isNew;
    private ArrayAdapter<String> spinnerAdapter;
    private String city;
    private List<String> citiesList;
    private ArrayList<String> usersSigned;
    private User currentUser;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_workshop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String intentValue = intent.getExtras().getString(getString(R.string.open_workshop_details_intent_key));
            if (intentValue != null) {
                switch (intentValue) {
                    case INTENT_OPEN_ADD_WORKSHOP_DETAILS:
                        isNew = true;
                        signToWorkshopButton.setVisibility(View.GONE);
                        setTitle(getString(R.string.workshop_add_title));
                        break;
                    case INTENT_OPEN_UPDATE_WORKSHOP_DETAILS:
                        id = intent.getExtras().getString(getString(R.string.current_workshop_id_key));
                        updateUI(false);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference()
                                .child(getString(R.string.firebase_root_name))
                                .child(getString(R.string.firebase_workshops_root_name))
                                .child(id)
                                .child(getString(R.string.firebase_user_workshop_user_list));
                        loadWorkshopDetails();
                        loadWorkshopAttendants();
                        setTitle(getString(R.string.workshop_update_title));
                        usersSigned = new ArrayList<>();
                        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersSigned);
                        mListView.setAdapter(itemsAdapter);
                        break;
                }
            }

        getCitiesSpinnerList();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workshop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save_ws:
                saveWorkshop();
                return true;
            case R.id.cancel_ws:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadWorkshopDetails() {
        TinyDB tinydb = new TinyDB(this);
        Workshop mWorkshop = tinydb.getObject(getString(R.string.workshop_tinydb_key), Workshop.class);
        String description = mWorkshop.getDescription();
        String name = mWorkshop.getName();
        String address = mWorkshop.getAddress();
        city = mWorkshop.getCity();
        date = mWorkshop.getDate();
        time = mWorkshop.getTime();

        descEditText.setText(description);
        nameEditText.setText(name);
        addressEditText.setText(address);
        pickDateButton.setText(getUserFriendlyDate(date));
        pickTimeButton.setText(time);

    }


    private void updateUI(final boolean isSigned) {
        if (isSigned) {
            signToWorkshopButton.setText(getString(R.string.sign_out_menu_text));
        } else {
            signToWorkshopButton.setText(getString(R.string.sign_in_workshop_text));
        }

        signToWorkshopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSigned) {
                    signOutFromWorkshop();
                } else {
                    signInForWorkshop();
                }
            }
        });
    }

    private void getCitiesSpinnerList() {
        citiesList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, citiesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_cities_root_name));
        Query query = databaseReference.orderByChild(getString(R.string.firebase_cities_name_key));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                citiesList.clear();
                for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                    City citySpinner = dataSnapshotItem.getValue(City.class);
                    String name = citySpinner.getName();
                    citiesList.add(name);
                }
                if (city != null) {
                    int spinnerPosition = spinnerAdapter.getPosition(city);
                    mSpinner.setSelection(spinnerPosition);
                }
                citiesList.add(getString(R.string.spinner_add_city_value));
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveWorkshop() {

        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        String description = Objects.requireNonNull(descEditText.getText()).toString();
        String address = Objects.requireNonNull(addressEditText.getText()).toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name));

        if (checkMandatoryFieldsSet()) {
            Workshop workshop = new Workshop(date, time, description, name, address, city);
            if (isNew) {
                databaseReference.push().setValue(workshop);
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("address", address);
                map.put("date", date);
                map.put("time", time);
                map.put("description", description);
                map.put("name", name);
                map.put("city", city);
                databaseReference.child(id).updateChildren(map);
            }
            finish();
        }

    }

    private void loadWorkshopAttendants() {
        TinyDB tinyDB = new TinyDB(this);
        try {
            currentUser = tinyDB.getObject(getString(R.string.firebase_user_tinyDb_key), User.class);

        } catch (NullPointerException e) {
            signToWorkshopButton.setVisibility(View.GONE);
            Timber.i(e);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usersSigned.clear();
                    for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                        User user = dataSnapshotItem.getValue(User.class);
                        usersSigned.add(user.getDisplayName() + ", (" + user.getEmail() + ")");
                        if (user.getEmail().equals(currentUser.getEmail())) {
                            updateUI(true);
                        }
                    }
                    itemsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkMandatoryFieldsSet() {
        boolean isAllMandatoryFieldsSet = true;

        if (TextUtils.isEmpty(date)) {
            pickDateButton.setText(getString(R.string.date_not_set_message));
            pickDateButton.setBackgroundColor(getColor(R.color.colorRed));
            isAllMandatoryFieldsSet = false;
        }

        if (TextUtils.isEmpty(time)) {
            pickTimeButton.setText(getString(R.string.time_not_set_message));
            pickTimeButton.setBackgroundColor(getColor(R.color.colorRed));
            isAllMandatoryFieldsSet = false;
        }

        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameEditText.setError(getString(R.string.name_not_set_message));
            isAllMandatoryFieldsSet = false;
        }

        if (TextUtils.isEmpty(addressEditText.getText())) {
            addressEditText.setError(getString(R.string.address_not_set_message));
            isAllMandatoryFieldsSet = false;
        }

        return isAllMandatoryFieldsSet;
    }

    public void signInForWorkshop() {
        databaseReference.push().setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Timber.i(task.getException());
                }
            }
        });

    }


    public void signOutFromWorkshop() {
        databaseReference.orderByChild(getString(R.string.firebase_email_order_by))
                .equalTo(currentUser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        updateUI(false);
                        for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                            dataSnapshotItem.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadAddNewCityDialogue() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newCity = input.getText().toString();
                        if (TextUtils.isEmpty(newCity)) {
                            dialog.setTitle(getString(R.string.add_city_error_empty));
                        } else if (citiesList.contains(newCity)) {
                            dialog.setTitle(getString(R.string.add_city_error_already_exists));
                        } else {
                            addCity(newCity);
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void addCity(String newCity) {
        city = newCity;
        if (newCity.isEmpty() || newCity == "") {
            return;
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.firebase_root_name))
                    .child(getString(R.string.firebase_cities_root_name));
            City city = new City();
            city.setName(newCity);
            databaseReference.push().setValue(city);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment dialogFragment = new DatePickerFragment();
        if (!isNew) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.workshop_date_dialog_args_key), date);
            dialogFragment.setArguments(bundle);
        }
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
        ((DatePickerFragment) dialogFragment).setListener(this);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment dialogFragment = new TimePickerFragment();
        if (!isNew) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.workshop_time_dialog_args_key), time);
            dialogFragment.setArguments(bundle);
        }
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
        ((TimePickerFragment) dialogFragment).setListener(this);
    }


    @Override
    public void setDate(int year, int month, int day) {
        date = day + "-" + month + "-" + year;
        pickDateButton.setText(getUserFriendlyDate(date));
    }


    @Override
    public void setTime(int hourOfDay, int minute) {
        time = String.format("%02d:%02d", hourOfDay, minute);
        pickTimeButton.setText(time);
    }

    private String getUserFriendlyDate(String dateOld) {
        Date date = new Date();
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            date = oldDateFormat.parse(dateOld);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
        return newDateFormat.format(date);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (citiesList.get(position).equals("add new city")) {
            loadAddNewCityDialogue();
        } else {
            city = citiesList.get(position);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
