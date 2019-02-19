package com.example.alvinaong.alive;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Home_Vol extends AppCompatActivity {

    private String Uid;
    private List<Event> eventList;
    private RecyclerView myRV;
    private EventAdapterVol myAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_vol);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(Home_Vol.this, Login.class);
            startActivity(intent);
            finish();
        }

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // check if database if completed first or not
        DatabaseReference checkDBRef = FirebaseDatabase.getInstance().getReference("volunteers").child(Uid);
        checkDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Volunteer currVol = dataSnapshot.getValue(Volunteer.class);

                // set up not complete yet
                if (currVol.getName().equals("")) {
                    Toast.makeText(Home_Vol.this, "Please set up your profile first", Toast.LENGTH_SHORT).show();
                    Intent setUpIntent = new Intent(Home_Vol.this, SetUp_Vol.class);
                    setUpIntent.putExtra("email", currVol.getEmail());
                    startActivity(setUpIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Toast message to welcome user
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("volunteers/" + Uid + "/name");
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String username = dataSnapshot.getValue().toString();
//                Toast.makeText(Home_Vol.this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


//         QR code************************
        FloatingActionButton qrCode = (FloatingActionButton) findViewById(R.id.qr_id);
        final Activity activity = this;
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home_Vol.this, QRCode.class));
            }
        });

        eventList = new ArrayList<>();

        // Recyclerview
        myRV = (RecyclerView) findViewById(R.id.recyclerview_id_vol);
        myRV.setHasFixedSize(true);
        myRV.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new EventAdapterVol(eventList);
        myRV.setAdapter(myAdapter);

        // Reading in events from Firebase
        readFirebase();

    }

    private void readFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Event eventToAdd = dataSnapshot.getValue(Event.class);
                eventList.add(eventToAdd);
                myAdapter.notifyItemInserted(eventList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Event eventToUpdate = dataSnapshot.getValue(Event.class);
                String toChangeID = eventToUpdate.getEventID();
                int index = -1;
                for (int i=0; i<eventList.size(); i++) {
                    if (toChangeID.equals(eventList.get(i).getEventID())) {
                        index = i;
                    }
                }
                eventList.set(index, eventToUpdate);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_vol_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.itemLogPage:
                Intent intent = new Intent(Home_Vol.this, Log_Vol.class);
                startActivity(intent);
                return true;

            case R.id.itemLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home_Vol.this, Login.class));
                finish();
                return true;
        }
        return true;
    }
}