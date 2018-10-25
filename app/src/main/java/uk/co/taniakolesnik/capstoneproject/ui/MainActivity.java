package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.BuildConfig;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.City;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.ReleaseTree;
import uk.co.taniakolesnik.capstoneproject.ui_tools.WorkshopsFirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.workshop_rv)
    RecyclerView mRecyclerView;
//    @BindView(R.id.add_workshop_bn)
//    Button addWorkshopButton;
//    @BindView(R.id.add_user_bn)
//    Button addUserButton;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.cities_spinner_homePage)
    Spinner mSpinner;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private WorkshopsFirebaseRecyclerAdapter adapter;
    private List<String> citiesList;
    private static final int REQUEST_CODE = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Toast.makeText(getApplicationContext(), "User is signed in", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "User signed out", Toast.LENGTH_LONG).show();
                }
                //TODO set ui changes on user auth status
            }
        };

        getCitiesSpinnerList();
//
//        addWorkshopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), WorkshopDetailsActivity.class);
//                intent.putExtra(getString(R.string.open_workshop_details_intent_key),
//                        WorkshopDetailsActivity.INTENT_OPEN_ADD_WORKSHOP_DETAILS);
//                startActivity(intent);
//
//            }
//        });

//        addUserButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
//                intent.putExtra(getString(R.string.open_user_details_intent_key),
//                        UserDetailsActivity.INTENT_OPEN_ADD_USER_DETAILS);
//                startActivity(intent);
//            }
//        });
//
    }

    private void getAllWorkshopsList() {
        Query query = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name))
                .limitToLast(50);

        FirebaseRecyclerOptions<Workshop> options =
                new FirebaseRecyclerOptions.Builder<Workshop>()
                        .setQuery(query, Workshop.class)
                        .build();

        if (adapter!=null){
            adapter.stopListening();
        }
        adapter = new WorkshopsFirebaseRecyclerAdapter(options, this, progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void getCitiesSpinnerList() {

        citiesList = new ArrayList<>();
        citiesList.add(getString(R.string.spinner_cities_all_value));
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, citiesList);
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
                    City city = dataSnapshotItem.getValue(City.class);
                    String name = city.getName();
                    citiesList.add(name);
                    Timber.i("new city found and added %s", name);
                }
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null){
            adapter.startListening();
        }
        mAuth.addAuthStateListener(mAuthListener);
     //   FirebaseUser currentUser = mAuth.getCurrentUser(); // TODO why?\
      //  Timber.i("get current user %s", currentUser.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    public void getWorkshopListFromTestUser(View view) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_users_root_name)).child("-LOTNWv9b-oSw8SmiMgS")
                .child(getString(R.string.firebase_users_workshops_name));

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> workshoprsIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String workshopid = snapshot.getKey();
                    workshoprsIds.add(workshopid);
                }
                Toast.makeText(getApplicationContext(), workshoprsIds.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String city = citiesList.get(position);
        if (city == getString(R.string.spinner_cities_all_value)) {
            getAllWorkshopsList();
        } else {
            loadWorkshopsForCity(city);
        }

    }

    private void loadWorkshopsForCity(String city) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name))
                .orderByChild(getString(R.string.firebase_workshop_city_name_key))
                .equalTo(city);

        FirebaseRecyclerOptions<Workshop> options =
                new FirebaseRecyclerOptions.Builder<Workshop>()
                        .setQuery(query, Workshop.class)
                        .build();
        adapter.stopListening();
        adapter = new WorkshopsFirebaseRecyclerAdapter(options, this, progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addNewCity(View view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_cities_root_name));
        City city = new City();
        city.setName("San Diego");
        databaseReference.push().setValue(city);
    }

    public void loginGitHub(View v){

        requestGitHubIdentity();

    }

    private void requestGitHubIdentity() {
        // GET https://github.com/login/oauth/authorize
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", getString(R.string.github_client_id))
                .appendQueryParameter("redirect_uri", getString(R.string.github_redirect))
                .appendQueryParameter("state", getRandomNumber())
                .appendQueryParameter("scope", "user:email");
        Uri uri = builder.build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public static String getRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(999999999);
        Timber.i("random number is %d", number);
        return String.format(Locale.ENGLISH, "%06d", number);
    }
}
