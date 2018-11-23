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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.models.City;
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.models.WorkshopAttendant;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.DatePickerFragment;
import uk.co.taniakolesnik.capstoneproject.ui_tools.TimePickerFragment;

public class WorkshopDetailsActivity extends AppCompatActivity
        implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener, AdapterView.OnItemSelectedListener {

    public static final String INTENT_OPEN_ADD_WORKSHOP_DETAILS = "add_workshop";
    public static final String INTENT_OPEN_UPDATE_WORKSHOP_DETAILS = "update_workshop";
    @BindView(R.id.ws_pick_date_bn) ImageButton pickDateButton;
    @BindView(R.id.workshop_date_tv) TextView dateTextView;
    @BindView(R.id.ws_pick_time_bn) ImageButton pickTimeButton;
    @BindView(R.id.workshop_time_tv) TextView timeTextView;
    @BindView(R.id.sing_in_or_out_ws_from_user) Button signToWorkshopButton;
    @BindView(R.id.ws_description_et) TextInputEditText descEditText;
    @BindView(R.id.ws_name_et) TextInputEditText nameEditText;
    @BindView(R.id.ws_address_et) TextInputEditText addressEditText;
    @BindView(R.id.cities_spinner_wsPage) Spinner mSpinner;
    @BindView(R.id.add_city_bt) Button mAddCityButton;

    private String id;
    private String date;
    private String time;
    private boolean isNew;
    private boolean isSigned;
    private ArrayAdapter<String> spinnerAdapter;
    private Workshop mWorkshop;
    private String city;
    private List<String> citiesList;
    private ArrayList<WorkshopAttendant> users;
    private WorkshopAttendant user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_workshop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String intentValue = intent.getExtras().getString(getString(R.string.open_workshop_details_intent_key));
        switch (intentValue) {
            case INTENT_OPEN_ADD_WORKSHOP_DETAILS:
                isNew = true;
                signToWorkshopButton.setVisibility(View.GONE);
                setTitle(getString(R.string.workshop_add_title));
                break;
            case INTENT_OPEN_UPDATE_WORKSHOP_DETAILS:
                id = intent.getExtras().getString(getString(R.string.current_workshop_id_key));
                loadExistentWorkshopDetails();
                Timber.i("night city after loadExistentWorkshopDetails is %s" , city);

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference()
                        .child(getString(R.string.firebase_root_name))
                        .child(getString(R.string.firebase_workshops_root_name))
                        .child(id)
                        .child("users");

                setTitle(getString(R.string.workshop_update_title));
                TinyDB tinyDB = new TinyDB(this);
                user = tinyDB.getObject(getString(R.string.firebase_user_tinyDb_key), WorkshopAttendant.class);
                if (user!=null){
                    String email = user.getEmail();
                    checkIfWorkshopAddedToUser(email);
                } else {
                    signToWorkshopButton.setVisibility(View.GONE);
                }
                break;
        }

        getCitiesSpinnerList();
        users = new ArrayList<>();
        setOnClickListeners();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workshop,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.save_ws :
                saveWorkshop();
                return true;
            case R.id.cancel_ws :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadExistentWorkshopDetails() {
        TinyDB tinydb = new TinyDB(this);
        mWorkshop = tinydb.getObject(getString(R.string.workshop_tinydb_key), Workshop.class);
        String description = mWorkshop.getDescription();
        String name = mWorkshop.getName();
        String address = mWorkshop.getAddress();
        city = mWorkshop.getCity();
        date = mWorkshop.getDate();
        time = mWorkshop.getTime();

        descEditText.setText(description);
        nameEditText.setText(name);
        addressEditText.setText(address);
        dateTextView.setText(getUserFriendlyDate(date));
        timeTextView.setText(time);

    }

    private void setOnClickListeners() {

        mAddCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.i("night mAddCityButton click done");
                loadAddNewCityDialogue();
            }
        });


        signToWorkshopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSigned){

                    signOutFromWorkshop();
                } else {

                    signInForWorkshop();
                }
            }
        });
    }

    private void updateUI(boolean isSigned){
        if (isSigned){
            signToWorkshopButton.setText("Sign Out");
        } else {
            signToWorkshopButton.setText("Sign in");
        }
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

        if (isNew) {
            users = new ArrayList<>();
            users.add(user);
        } else {
            //TODO load current list
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name));

        if (checkMandatoryFieldsSet()){
            Workshop updatedOrNewWorkshop = new Workshop(date, time, description, name, address, city);
            if (isNew) {
                databaseReference.push().setValue(updatedOrNewWorkshop);
            } else {
                databaseReference.child(id).setValue(updatedOrNewWorkshop);
            }
            finish();
        }

    }

    private boolean checkMandatoryFieldsSet(){
        boolean isAllMandatoryFieldsSet = true;

        if (TextUtils.isEmpty(dateTextView.getText())) {
            dateTextView.setError("Date is required!");
            isAllMandatoryFieldsSet = false;
        }

        if (TextUtils.isEmpty(timeTextView.getText())){
            timeTextView.setError("Time is required!");
            isAllMandatoryFieldsSet = false;
        }

        if (TextUtils.isEmpty(nameEditText.getText())){
            nameEditText.setError("Name is required!");
            isAllMandatoryFieldsSet = false;
        }

        if (TextUtils.isEmpty(addressEditText.getText())){
            addressEditText.setError("Address is required!");
            isAllMandatoryFieldsSet = false;
        }

        return isAllMandatoryFieldsSet;
    }

    public void signInForWorkshop() {
        databaseReference.push().setValue(user);
        isSigned = true;
        updateUI(isSigned);
    }


    public void signOutFromWorkshop() {
        databaseReference.orderByChild("email")
                .equalTo(user.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                            dataSnapshotItem.getRef().removeValue();
                            isSigned = false;
                            updateUI(isSigned);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkIfWorkshopAddedToUser(String email) {
        databaseReference.orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                           isSigned = true;
                           updateUI(isSigned);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadAddNewCityDialogue() {
        Timber.i("night loadAddNewCityDialogue started");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCity = input.getText().toString();
                if (TextUtils.isEmpty(newCity)){
                    Toast.makeText(getApplicationContext(), "Please add city name", Toast.LENGTH_SHORT).show();
                } else {
                    addCity(newCity);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void addCity(String newCity){
        if (newCity.isEmpty()||newCity==""){
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
            Timber.i("time picker time is %s", time);
            dialogFragment.setArguments(bundle);
        }
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
        ((TimePickerFragment) dialogFragment).setListener(this);
    }


    @Override
    public void setDate(int year, int month, int day) {
        date = day + "-" + month + "-" + year;
        dateTextView.setText(getUserFriendlyDate(date));
    }


    @Override
    public void setTime(int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
        timeTextView.setText(time);
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
        city = citiesList.get(position);
        Timber.i("night city is %s , %d", city, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
