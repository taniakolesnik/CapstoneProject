package uk.co.taniakolesnik.capstoneproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.BuildConfig;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.auth.AccessToken;
import uk.co.taniakolesnik.capstoneproject.auth.GitHubClient;
import uk.co.taniakolesnik.capstoneproject.auth.ServiceGenerator;
import uk.co.taniakolesnik.capstoneproject.models.City;
import uk.co.taniakolesnik.capstoneproject.models.User;
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.WorkshopsFirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String CLIENT_ID = "3e518903d1e2a128f2f0";
    private static final String CLIENT_SECRET = "6209b7dc86a0e01ca118205a736201b3db118438";
    private static final String REDIRECT_URI = "capstoneproject://callback";

    private WorkshopsFirebaseRecyclerAdapter adapter;
    private List<String> citiesList;
    private String mAccessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @BindView(R.id.workshop_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolBar)
    Toolbar mToolbar;
    @BindView(R.id.collapsingBarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.cities_spinner_homePage)
    Spinner mSpinner;
    @BindView(R.id.login_bn)
    Button mLoginButton;
    @BindView(R.id.signed_header)
    TextView headerSigned;
    @BindView(R.id.not_signed_header)
    RelativeLayout headerNotSigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.DebugTree());
        }

        mAuth = FirebaseAuth.getInstance();
        getCitiesSpinnerList();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    TinyDB tinyDB = new TinyDB(getApplicationContext());
                    User currentUser = new User(user.getEmail(), user.getDisplayName(), String.valueOf(user.getPhotoUrl())); //Roles will added later
                    tinyDB.putObject(getString(R.string.firebase_user_tinyDb_key), currentUser);
                }
                updateUI(user);
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
        mAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            String code = uri.getQueryParameter(getString(R.string.code_query_parameter));
            String error = uri.getQueryParameter(getString(R.string.error_query_parameter));
            if (code != null) {
                getAccessToken(code);
            } else if (error != null) {
                Timber.i(getString(R.string.callback_error_return_message), error);
            }
        }

        mCollapsingToolbarLayout.requestLayout();
    }

    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            headerNotSigned.setVisibility(View.GONE);
            headerSigned.setVisibility(View.VISIBLE);
        } else {
            mLoginButton.setText(getString(R.string.login_button_text));
            headerNotSigned.setVisibility(View.VISIBLE);
            headerSigned.setVisibility(View.GONE);
        }

        mCollapsingToolbarLayout.requestLayout();
        invalidateOptionsMenu();
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    signOut();
                } else {
                    startGitHubLoginIntent();
                }
            }
        });

        loadWorkshopsForCity(citiesList.get(0)); // default

    }

    public void signOut() {
        mAuth.signOut();
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putObject(getString(R.string.firebase_user_tinyDb_key), null);
    }

    private void startGitHubLoginIntent() {
        // https://github.com/login/oauth/authorize
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", "user")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    private void getAccessToken(String code) {
        GitHubClient client = ServiceGenerator.createService(GitHubClient.class);
        Call<AccessToken> call = client.getAccessToken(CLIENT_ID, CLIENT_SECRET, code);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.body() != null) {
                    mAccessToken = response.body().getAccessToken();
                    if (mAccessToken!=null){
                        TinyDB tinyDB = new TinyDB(getApplicationContext());
                        tinyDB.putString("token", mAccessToken);
                    }
                    authFirebase(mAccessToken);
                }

            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, Throwable t) {
                mAccessToken = null;
            }
        });
    }


    private void authFirebase(String token) {
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Timber.i(task.getException(), "Monday authFirebase failed");
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Timber.i(task.getException(), "Monday authFirebase succeeded %s" , user.getEmail());
                        }
                    }
                });
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
                citiesList.add(getString(R.string.spinner_cities_all_value));
                for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
                    City city = dataSnapshotItem.getValue(City.class);
                    String name = null;
                    if (city != null) {
                        name = city.getName();
                    }
                    citiesList.add(name);
                }
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String city = citiesList.get(position);
        if (city.equals(getString(R.string.spinner_cities_all_value))) {
            loadWorkshopsForCity(getString(R.string.spinner_cities_all_value));
        } else {
            loadWorkshopsForCity(city);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadWorkshopsForCity(String city) {
        Timber.i("Monday late loadWorkshopsForCity started");

        Query query;
        if (city.equals(getString(R.string.spinner_cities_all_value))) {
            query = FirebaseDatabase.getInstance()
                    .getReference(getString(R.string.firebase_root_name))
                    .child(getString(R.string.firebase_workshops_root_name))
                    .limitToLast(50);
        } else {
            query = FirebaseDatabase.getInstance()
                    .getReference(getString(R.string.firebase_root_name))
                    .child(getString(R.string.firebase_workshops_root_name))
                    .orderByChild(getString(R.string.firebase_workshop_city_name_key))
                    .equalTo(city).limitToLast(50);

        }

        FirebaseRecyclerOptions<Workshop> options =
                new FirebaseRecyclerOptions.Builder<Workshop>()
                        .setQuery(query, new SnapshotParser<Workshop>() {
                            @NonNull
                            @Override
                            public Workshop parseSnapshot(@NonNull DataSnapshot snapshot) {
                                GenericTypeIndicator<Workshop> t = new GenericTypeIndicator<Workshop>() {
                                };
                                Workshop workshop = snapshot.getValue(t);
                                return workshop;
                            }
                        })
                        .build();
        if (adapter != null) {
            adapter.stopListening();
        }
        adapter = new WorkshopsFirebaseRecyclerAdapter(options, this, progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
        mAuth.removeAuthStateListener(mAuthStateListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.main_admin, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addWorkshop:
                Intent addWorkshopIntent = new Intent(getApplicationContext(), WorkshopDetailsActivity.class);
                addWorkshopIntent.putExtra(getString(R.string.open_workshop_details_intent_key),
                        WorkshopDetailsActivity.INTENT_OPEN_ADD_WORKSHOP_DETAILS);
                startActivity(addWorkshopIntent);
                return true;

            case R.id.userInfo:
                Intent userInfoIntent = new Intent(getApplicationContext(), UserInfoActivity.class);
                startActivity(userInfoIntent);
                return true;

            case R.id.signOutMenu:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
