package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.DatePickerFragment;
import uk.co.taniakolesnik.capstoneproject.ui_tools.TimePickerFragment;

public class WorkshopDetailsActivity extends AppCompatActivity implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener {

    private String date;
    private String time;
    private boolean isNew;
    private Workshop mWorkshop;
    public static final String INTENT_OPEN_ADD_WORKSHOP_DETAILS = "add_workshop";
    public static final String INTENT_OPEN_UPDATE_WORKSHOP_DETAILS = "update_workshop";

    @BindView(R.id.ws_pick_date_bn) Button pickDateButton;
    @BindView(R.id.ws_pick_time_bn) Button pickTimeButton;
    @BindView(R.id.add_ws_to_user_bt) Button addWorkshopToUser;
    @BindView(R.id.ws_description_et) EditText descEditText;
    @BindView(R.id.save_ws_bn) Button saveButton;
    @BindView(R.id.cancel_save_ws_bt) Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_workshop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String intentValue = intent.getExtras().getString(getString(R.string.open_workshop_details_intent_key));
        Timber.i("intentValue is %s", intentValue);
        switch (intentValue){
            case INTENT_OPEN_ADD_WORKSHOP_DETAILS:
                isNew = true;
                addWorkshopToUser.setVisibility(View.GONE);
                break;
            case INTENT_OPEN_UPDATE_WORKSHOP_DETAILS:
                loadWorkshopDetails();
                break;
        }
        setOnClickListeners();

    }

    private void loadWorkshopDetails() {
        TinyDB tinydb = new TinyDB(this);
        mWorkshop = tinydb.getObject(getString(R.string.workshop_tinydb_key), Workshop.class);
        String description = mWorkshop.getDescription();
        date = mWorkshop.getDate();
        time = mWorkshop.getTime();

        descEditText.setText(description);
        pickDateButton.setText(date);
        pickTimeButton.setText(time);
    }

    private void setOnClickListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkshop();
                finish();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveWorkshop() {

        String id;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name));

        if (isNew){
            id = databaseReference.push().getKey();
            Timber.i("new id is %s", id);
        }  else {
            id = mWorkshop.getId();
        }

        String description = descEditText.getText().toString();
        String name = getString(R.string.test_name);
        String webaddress = getString(R.string.test_webAddress);
        String building = getString(R.string.test_buildingName);
        String street = getString(R.string.test_street);
        String city = getString(R.string.test_city);
        String country = getString(R.string.test_country);
        String postCode = getString(R.string.test_postCode);
        String directions = getString(R.string.test_directions);
        String accessibilityInfo = getString(R.string.test_accessibilityInfo);
        Workshop updatedOrNewWorkshop = new Workshop(id, date, time, description, name,
                webaddress, building, street, city, country, postCode, directions, accessibilityInfo);
        databaseReference.child(id).setValue(updatedOrNewWorkshop);
    }


    public void addWorkshopToTestUser(View view) {
        Timber.i("addWorkshopToTestUser started");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_users_root_name)).child("-LOTNWv9b-oSw8SmiMgS");

        Map<String, String> map = new HashMap<>();
        map.put("status", "attending");
        map.put("date_signed", String.valueOf(System.currentTimeMillis()));
        databaseReference.child("workshops").child(mWorkshop.getId()).setValue(map);


    }
    public void updateWorkshopStatusToWaiting(View view) {
        Timber.i("addWorkshopToTestUser started");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_users_root_name))
                .child("-LOTNWv9b-oSw8SmiMgS")
                .child("workshops")
                .child(mWorkshop.getId());

        Timber.i(" workshop id is %s", mWorkshop.getId());
        databaseReference.child("status").setValue("waiting");
    }


    public void showDatePickerDialog(View v) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
        ((DatePickerFragment) dialogFragment).setListener(this);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
        ((TimePickerFragment) dialogFragment).setListener(this);
    }


    @Override
    public void setDate(int year, int month, int day) {
        date = day + "/" + month + "/" + year;
        pickDateButton.setText(date);
    }


    @Override
    public void setTime(int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
        pickTimeButton.setText(time);
    }

}
