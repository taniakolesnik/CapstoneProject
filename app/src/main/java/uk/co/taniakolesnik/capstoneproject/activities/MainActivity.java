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
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.models.WorkshopAttendant;
import uk.co.taniakolesnik.capstoneproject.tools.ReleaseTree;
import uk.co.taniakolesnik.capstoneproject.tools.TinyDB;
import uk.co.taniakolesnik.capstoneproject.ui_tools.WorkshopsFirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String CLIENT_ID = "3d07ee7ebfec179b882d";
    private static final String CLIENT_SECRET = "35c07c31e0d4608f524fdc27d319b065904345aa";
    private static final String REDIRECT_URI = "capstoneproject://callback";

    private WorkshopsFirebaseRecyclerAdapter adapter;
    private List<String> citiesList;

    private String mAcceessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @BindView(R.id.workshop_rv) RecyclerView mRecyclerView;

    @BindView(R.id.appBarLayout) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolBar) Toolbar mToolbar;
    @BindView(R.id.collapsingBarLayout) CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.cities_spinner_homePage) Spinner mSpinner;
    @BindView(R.id.login_bn) Button mLoginButton;
    @BindView(R.id.signed_header) TextView headerSigned;
    @BindView(R.id.not_signed_header) RelativeLayout headerNotSigned;

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

        mAuth = FirebaseAuth.getInstance();
        getCitiesSpinnerList();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    TinyDB tinyDB = new TinyDB(getApplicationContext());
                    WorkshopAttendant currentUser = new WorkshopAttendant(user.getEmail(), 1); //Roles will added later
                    tinyDB.putObject(getString(R.string.firebase_user_tinyDb_key), currentUser);
                }
                updateUI(user);
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null){
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
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)){
            String code = uri.getQueryParameter("code");
            String error = uri.getQueryParameter("error");
            if (code != null) {
                getAccessToken(code);
            } else if (error != null) {
            }
        }

        mCollapsingToolbarLayout.requestLayout();
    }

    private void updateUI(final FirebaseUser user) {
        if (user!=null) {
            headerNotSigned.setVisibility(View.GONE);
            headerSigned.setVisibility(View.VISIBLE);
        } else {
            mLoginButton.setText("Log in");
            headerNotSigned.setVisibility(View.VISIBLE);
            headerSigned.setVisibility(View.GONE);
        }

        mCollapsingToolbarLayout.requestLayout();

        invalidateOptionsMenu();
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user!=null){
                    signOut();
                } else {
                    startGitHubLoginIntent();
                }
            }
        });

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

    private void getAccessToken(String code){
        GitHubClient client = ServiceGenerator.createService(GitHubClient.class);
        Call<AccessToken> call = client.getAccessToken(CLIENT_ID, CLIENT_SECRET, code);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.body() != null) {
                    mAcceessToken = response.body().getAccessToken();
                    //getUserInfo();
                    authFirebase(mAcceessToken);
                }

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                mAcceessToken = null;
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
                            Timber.i("night authFirebase failed getException is %s", task.getException().toString());
                        } else {
                            Timber.i("night authFirebase successmAcceessToken is %s", mAcceessToken);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        }
                    }
                });
    }
//
//
//    private void getUserInfo() {
//        if (mAcceessToken!=null){
//            GitHubClient client = ServiceGenerator.createApiService(GitHubClient.class);
//            Call<GitHubUser> call = client.getUserInfo("Bearer " + mAcceessToken);
//            call.enqueue(new Callback<GitHubUser>() {
//                @Override
//                public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
//                    GitHubUser user = response.body();
//                    String email = user.getEmail();
//                    String login = user.getLogin();
//                    String avatarUrl = user.getAvatarUrl();
//                    String userName = user.getUserName();
//                    Toast.makeText(getApplicationContext(), email
//                                    + "\n"
//                                    + login
//                                    + "\n"
//                                    + avatarUrl
//                                    + "\n"
//                                    + userName,
//                            Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onFailure(Call<GitHubUser> call, Throwable t) {
//                }
//            });
//        }
//    }

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
                    String name = city.getName();
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
        if (city == getString(R.string.spinner_cities_all_value)) {
            loadWorkshopsForCity(getString(R.string.spinner_cities_all_value));
        } else {
            loadWorkshopsForCity(city);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadWorkshopsForCity(String city) {
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
                    .equalTo(city);
        }

        FirebaseRecyclerOptions<Workshop> options =
                new FirebaseRecyclerOptions.Builder<Workshop>()
                        .setQuery(query, Workshop.class)
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
        if (adapter != null){
            adapter.stopListening();
        }
        mAuth.removeAuthStateListener(mAuthStateListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser()!=null){
            getMenuInflater().inflate(R.menu.main_admin, menu);
        } else{
           // getMenuInflater().inflate(R.menu.main_user, menu); no menu for non auth users for now
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
           case R.id.addWorkshop :
               Intent intent = new Intent(getApplicationContext(), WorkshopDetailsActivity.class);
               intent.putExtra(getString(R.string.open_workshop_details_intent_key),
                       WorkshopDetailsActivity.INTENT_OPEN_ADD_WORKSHOP_DETAILS);
               startActivity(intent);
            return true;

            case R.id.signOutMenu :
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
