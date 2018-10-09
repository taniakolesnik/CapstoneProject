package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.DatePickerFragment;
import uk.co.taniakolesnik.capstoneproject.ui_tools.TimePickerFragment;

public class WorkshopDetailsActivity extends AppCompatActivity implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener {

    private String date;
    private String time;
    private boolean isNew;
    private Workshop workshop;
    public static final String INTENT_OPEN_ADD_WORKSHOP_DETAILS = "add";
    public static final String INTENT_OPEN_UPDATE_WORKSHOP_DETAILS = "update";

    @BindView(R.id.ws_pick_date_bn) Button pickDateButton;
    @BindView(R.id.ws_pick_time_bn) Button pickTimeButton;
    @BindView(R.id.ws_description_et) EditText descEditText;
    @BindView(R.id.save_ws_bn) Button saveButton;
    @BindView(R.id.cancel_save_ws_bt) Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workshop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String intentValue = intent.getExtras().getString(getString(R.string.open_workshop_details_intent_key));
        switch (intentValue){
            case INTENT_OPEN_ADD_WORKSHOP_DETAILS:
                isNew = true;
                Toast.makeText(this, "please add new workshop", Toast.LENGTH_SHORT).show();
            case INTENT_OPEN_UPDATE_WORKSHOP_DETAILS:
                loadWorkshopDetails();
                Toast.makeText(this, "please updated existent workshop", Toast.LENGTH_SHORT).show();
        }
        setOnClickListeners();

    }

    private void loadWorkshopDetails() {
        TinyDB tinydb = new TinyDB(this);
        workshop = tinydb.getObject(getString(R.string.workshop_tinydb_key), Workshop.class);
        descEditText.setText(workshop.getDescription());
        pickDateButton.setText(workshop.getDate());
        pickTimeButton.setText(workshop.getTime());
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
        }  else {
            id = workshop.getId();
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
