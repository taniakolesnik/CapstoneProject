package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.taniakolesnik.capstoneproject.BuildConfig;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.objects.Workshop;
import uk.co.taniakolesnik.capstoneproject.tools.ReleaseTree;
import uk.co.taniakolesnik.capstoneproject.ui_tools.WorkshopsFirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.workshop_rv) RecyclerView mRecyclerView;
    @BindView(R.id.add_workshop_bn) Button addWorkshopButton;
    @BindView(R.id.add_user_bn) Button addUserButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    private WorkshopsFirebaseRecyclerAdapter adapter;

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

        Query query = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name))
                .limitToLast(50);

        FirebaseRecyclerOptions<Workshop> options =
                new FirebaseRecyclerOptions.Builder<Workshop>()
                        .setQuery(query, Workshop.class)
                        .build();

        adapter = new WorkshopsFirebaseRecyclerAdapter(options, this, progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        addWorkshopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkshopDetailsActivity.class);
                intent.putExtra(getString(R.string.open_workshop_details_intent_key),
                        WorkshopDetailsActivity.INTENT_OPEN_ADD_WORKSHOP_DETAILS);
                startActivity(intent);

            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                intent.putExtra(getString(R.string.open_user_details_intent_key),
                        UserDetailsActivity.INTENT_OPEN_ADD_USER_DETAILS);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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

}
