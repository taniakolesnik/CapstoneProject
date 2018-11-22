package uk.co.taniakolesnik.capstoneproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.taniakolesnik.capstoneproject.R;
import uk.co.taniakolesnik.capstoneproject.models.City;

public class AdminActivity extends AppCompatActivity {

    @BindView(R.id.add_workshop_bn) Button addWorkshopButton;
    @BindView(R.id.add_user_bn) Button addUserButton;
    @BindView(R.id.add_new_city_bn) Button addNewCityButton;
    @BindView(R.id.retrieve_ws_from_user) Button getUserWorkshopsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

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


        addNewCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.firebase_root_name))
                        .child(getString(R.string.firebase_cities_root_name));
                City city = new City();
                city.setName("San Diego");
                databaseReference.push().setValue(city);
            }
        });

        getUserWorkshopsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference()
                        .child(getString(R.string.firebase_root_name))
                        .child(getString(R.string.firebase_workshops_root_name))
                        .child(getString(R.string.firebase_workshops_root_name));

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
        });


    }
}
