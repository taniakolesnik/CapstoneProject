package uk.co.taniakolesnik.capstoneproject.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import uk.co.taniakolesnik.capstoneproject.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

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

    //    public void addNewCity(View view){
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child(getString(R.string.firebase_root_name))
//                .child(getString(R.string.firebase_cities_root_name));
//        City city = new City();
//        city.setName("San Diego");
//        databaseReference.push().setValue(city);
//    }
}
