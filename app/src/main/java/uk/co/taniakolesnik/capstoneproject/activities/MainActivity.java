package uk.co.taniakolesnik.capstoneproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
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
import uk.co.taniakolesnik.capstoneproject.auth.GitHubUser;
import uk.co.taniakolesnik.capstoneproject.auth.ServiceGenerator;
import uk.co.taniakolesnik.capstoneproject.models.City;
import uk.co.taniakolesnik.capstoneproject.models.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.ReleaseTree;
import uk.co.taniakolesnik.capstoneproject.ui_tools.WorkshopsFirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String CLIENT_ID = "3d07ee7ebfec179b882d";
    private String CLIENT_SECRET = "f1948fd03a79b547cad30ebeaaad4b36084d0f3f";
    private String REDIRECT_URI = "capstoneproject://callback";

    private WorkshopsFirebaseRecyclerAdapter adapter;
    private List<String> citiesList;

    @BindView(R.id.workshop_rv) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.cities_spinner_homePage) Spinner mSpinner;
    @BindView(R.id.login_bn) Button mLoginButton;

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

        //getCitiesSpinnerList();
        
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGitHub();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)){
            String code = uri.getQueryParameter("code");
            Log.i("night", "onResume code is " + code);
            GitHubClient client = ServiceGenerator.createService(GitHubClient.class);
            Call<AccessToken> call = client.getAccessToken(CLIENT_ID, CLIENT_SECRET, code);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.i("night", "onResume onResponse success");
//                    String accessToken = response.body().getAccessToken();
//                    getUserInfo(accessToken);
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.i("night", "onResume onFailure " + t.toString());
                }
            });

        }
    }

    private void loginGitHub() {
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
        Log.i("night", "loginGitHub " + uri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    private void getUserInfo(String accessToken) {
        Log.i("night", " getUserInfo started");
        GitHubClient client = ServiceGenerator.createApiService(GitHubClient.class);
        Call<GitHubUser> call = client.getUserInfo("Bearer " + accessToken);
        call.enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
                GitHubUser user = response.body();
                String email = user.getEmail();
                String login = user.getLogin();
                String avatarUrl = user.getAvatarUrl();
                String userName = user.getUserName();
                Toast.makeText(getApplicationContext(), email
                                + "\n"
                                + login
                                + "\n"
                                + avatarUrl
                                + "\n"
                                + userName,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Epic fail, Tanushka",
                        Toast.LENGTH_LONG).show();
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

//    public void getWorkshopListFromTestUser(View view) {
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference databaseReference = firebaseDatabase.getReference()
//                .child(getString(R.string.firebase_root_name))
//                .child(getString(R.string.firebase_users_root_name)).child("-LOTNWv9b-oSw8SmiMgS")
//                .child(getString(R.string.firebase_users_workshops_name));
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<String> workshoprsIds = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String workshopid = snapshot.getKey();
//                    workshoprsIds.add(workshopid);
//                }
//                Toast.makeText(getApplicationContext(), workshoprsIds.toString(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

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

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            mLoginButton.setText("sigh out as " + currentUser.getDisplayName());
        } else {
            mLoginButton.setText("Log in");
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.stopListening();
        }
    }
}
