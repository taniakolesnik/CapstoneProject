package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.User;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;

public class UserDetailsActivity extends AppCompatActivity {


    private boolean isNew;
    private User mUser;
    public static final String INTENT_OPEN_ADD_USER_DETAILS = "add_user";
    public static final String INTENT_OPEN_UPDATE_USER_DETAILS = "update_user";

    @BindView(R.id.user_first_name_et) EditText firstNameEditText;
    @BindView(R.id.user_last_name_et) EditText lastNameEditText;
    @BindView(R.id.save_user_bn) Button saveButton;
    @BindView(R.id.cancel_save_user_bt) Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_user);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String intentValue = intent.getExtras().getString(getString(R.string.open_user_details_intent_key));
        Timber.i("intentValue is %s", intentValue);
        switch (intentValue){
            case INTENT_OPEN_ADD_USER_DETAILS:
                isNew = true;
                break;
            case INTENT_OPEN_UPDATE_USER_DETAILS:
                loadUserDetails();
                break;
        }
        setOnClickListeners();

    }

    private void loadUserDetails() {
        String firstName;
        String lastName;
        TinyDB tinydb = new TinyDB(this);
        mUser = tinydb.getObject(getString(R.string.user_tinydb_key), User.class);
        firstName = mUser.getFirstName();
        lastName = mUser.getLastName();
        firstNameEditText.setText(firstName);
        lastNameEditText.setText(lastName);
    }

    private void setOnClickListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
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

    private void saveUser() {

        String id;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_users_root_name));

        if (isNew){
            id = databaseReference.push().getKey();
            Timber.i("new user id is %s ", id);
        }  else {
            id = mUser.getId();
            Timber.i("existent user id is %s ", id);
        }

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String pronouns = getString(R.string.test_pronouns);
        String email = getString(R.string.test_email);
        int userType = 4;
        User updatedOrNewUser = new User(id, firstName, lastName, pronouns, email, userType);
        databaseReference.child(id).setValue(updatedOrNewUser);
    }

}
