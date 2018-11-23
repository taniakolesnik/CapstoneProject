package uk.co.taniakolesnik.capstoneproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
    @BindView(R.id.ws_pick_date_bn) ImageButton pickDateButton;
    @BindView(R.id.workshop_date_tv) TextView dateTextView;
    @BindView(R.id.ws_pick_time_bn) ImageButton pickTimeButton;
    @BindView(R.id.workshop_time_tv) TextView timeTextView;
    @BindView(R.id.sing_in_or_out_ws_from_user) Button signToWorkshopButton;
    @BindView(R.id.ws_description_et) TextInputEditText descEditText;
    @BindView(R.id.ws_name_et) TextInputEditText nameEditText;
    @BindView(R.id.ws_address_et) TextInputEditText addressEditText;
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

    private void loadExistentWorshopDetails() {
        TinyDB tinydb = new TinyDB(this);
        mWorkshop = tinydb.getObject(getString(R.string.workshop_tinydb_key), Workshop.class);
        String description = mWorkshop.getDescription();
        String name = mWorkshop.getName();
        String address = mWorkshop.getAddress();
        date = mWorkshop.getDate();
        time = mWorkshop.getTime();

        descEditText.setText(description);
        nameEditText.setText(name);
        addressEditText.setText(address);
        dateTextView.setText(getUserFriendlyDate(date));
        timeTextView.setText(time);
    }

    private void setOnClickListeners() {

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

        String name = nameEditText.getText().toString();
        String description = descEditText.getText().toString();
        String address = addressEditText.getText().toString();

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
            Workshop updatedOrNewWorkshop = new Workshop(date, time, description, name, address, "London");
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

}
