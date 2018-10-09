package uk.co.taniakolesnik.capstoneproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

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
    @BindView(R.id.add_workshop_bn) Button mButton;
    private List<Workshop> workshops;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
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

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(getString(R.string.firebase_root_name));

        Query query = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_root_name))
                .child(getString(R.string.firebase_workshops_root_name))
                .limitToLast(50);



        FirebaseRecyclerOptions<Workshop> options =
                new FirebaseRecyclerOptions.Builder<Workshop>()
                        .setQuery(query, Workshop.class)
                        .build();

        adapter = new WorkshopsFirebaseRecyclerAdapter(options);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkshopDetailsActivity.class);
                intent.putExtra(getString(R.string.open_workshop_details_intent_key),
                        WorkshopDetailsActivity.INTENT_OPEN_ADD_WORKSHOP_DETAILS);
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

//    private void geTestUser() {
//        Timber.i("geTestUsers started");
//        mDatabaseReference.child(getString(R.string.firebase_users_root_name)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int testUserTypeId = 4;
//                if (!dataSnapshot.child(String.valueOf(testUserTypeId)).exists()){
//                    String id = mDatabaseReference.push().getKey();
//                    Timber.i("geTestUsers id " + id);
//                    String name = getString(R.string.test_firstName);
//                    String lastName = getString(R.string.test_lasttName);
//                    String pronouns = getString(R.string.test_pronouns);
//                    String email = getString(R.string.test_email);
//                    int userType = testUserTypeId;
//                    User user = new User(id, name, lastName, pronouns, email, userType);
//                    mDatabaseReference.setValue(user);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


}
