package uk.co.taniakolesnik.capstoneproject.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;

public class AddWorkshop extends AppCompatActivity implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener {

    private String date;
    private String time;

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

        setOnClickListeners();

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
        String description = descEditText.getText().toString();
        String participant = getString(R.string.test_participant);
        String name = getString(R.string.test_name);
        String webaddress = getString(R.string.test_webAddress);
        String building = getString(R.string.test_buildingName);
        String street = getString(R.string.test_street);
        String city = getString(R.string.test_city);
        String country = getString(R.string.test_country);
        String postCode = getString(R.string.test_postCode);
        String directions = getString(R.string.test_directions);
        String accessibilityInfo = getString(R.string.test_accessibilityInfo);


        Workshop workshop = new Workshop(date, time, participant, description, name,
                webaddress, building, street, city, country, postCode, directions, accessibilityInfo);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(getString(R.string.firebase_workshops_root_name));
        databaseReference.push().setValue(workshop);

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
    }

    @Override
    public void setTime(int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
    }

}
