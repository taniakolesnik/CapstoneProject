package uk.co.taniakolesnik.capstoneproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.models.WorkshopAttendant;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.DatePickerFragment;
import uk.co.taniakolesnik.capstoneproject.ui_tools.TimePickerFragment;

public class WorkshopDetailsActivity extends AppCompatActivity
        implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener {

    public static final String INTENT_OPEN_ADD_WORKSHOP_DETAILS = "add_workshop";
    public static final String INTENT_OPEN_UPDATE_WORKSHOP_DETAILS = "update_workshop";
    @BindView(R.id.ws_pick_date_bn)
    Button pickDateButton;
    @BindView(R.id.ws_pick_time_bn)
    Button pickTimeButton;
    @BindView(R.id.sing_in_or_out_ws_from_user)
    Button signToWorkshopButton;
    @BindView(R.id.ws_description_et)
    EditText descEditText;
    @BindView(R.id.save_ws_bn)
    Button saveButton;
    @BindView(R.id.cancel_save_ws_bt)
    Button cancelButton;
    private String id;
    private String date;
    private String time;
    private boolean isNew;
    private boolean isSigned;
    private Workshop mWorkshop;
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
                loadExistentWorshopDetails();

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

        users = new ArrayList<>();
        setOnClickListeners();

    }

    private void loadExistentWorshopDetails() {
        TinyDB tinydb = new TinyDB(this);
        mWorkshop = tinydb.getObject(getString(R.string.workshop_tinydb_key), Workshop.class);
        String description = mWorkshop.getDescription();
        date = mWorkshop.getDate();
        time = mWorkshop.getTime();

        descEditText.setText(description);
        pickDateButton.setText(getUserFriendlyDate(date));
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

    private void saveWorkshop() {

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
        //get current user info
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

        Workshop updatedOrNewWorkshop = new Workshop(date, time, description, name,
                webaddress, building, street, city, country, postCode, directions, accessibilityInfo);

        if (isNew) {
            databaseReference.push().setValue(updatedOrNewWorkshop);
        } else {
            databaseReference.child(id).setValue(updatedOrNewWorkshop);
        }
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
        pickDateButton.setText(getUserFriendlyDate(date));
    }


    @Override
    public void setTime(int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
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

}
