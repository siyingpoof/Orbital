package com.example.alvinaong.alive;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Log_Vol extends AppCompatActivity {

    private TextView tvName, tvGender, tvSchool, tvContact, tvEmail, tvAccHours;
    private ImageView ivImage;
    private ProgressBar progressBar;
    private String Uid;
    private Volunteer currUser;
    private int accHours;

    private RecyclerView myRV;
    private EventAdapterVolLog myAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_vol);

        tvName = findViewById(R.id.tvName);
        tvGender = findViewById(R.id.tvGender);
        tvSchool = findViewById(R.id.tvSchool);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        tvAccHours = findViewById(R.id.tvAccHours);
        ivImage = findViewById(R.id.ivVolImage);
        progressBar = findViewById(R.id.progressBar);
        tvAccHours = findViewById(R.id.tvAccHours);

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("volunteers").child(Uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currUser = dataSnapshot.getValue(Volunteer.class);
                tvName.setText(currUser.getName());
                tvGender.setText(currUser.getGender());
                tvSchool.setText(currUser.getSchool());
                tvEmail.setText(currUser.getEmail());
                tvContact.setText(currUser.getContactNumber());

                // Progress bar
                accHours = currUser.getHours();
                tvAccHours.setText(accHours + " hours");

                progressBar.setMax(100);
//                progressBar.setIndeterminate(false);
                progressBar.setProgress(accHours);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("volunteers").child(Uid+".jpg");
                Glide.with(Log_Vol.this).load(storageReference).into(ivImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        eventList = new ArrayList<>();

        // Recycler view
        myRV = (RecyclerView) findViewById(R.id.recyclerview_id_vollog);
        myRV.setHasFixedSize(true);
        myRV.setLayoutManager(new GridLayoutManager(this, 3));
        myAdapter = new EventAdapterVolLog(eventList);
        myRV.setAdapter(myAdapter);

        // read in data from firebase
        readFirebase();

    }

    private void readFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("volunteers").child(Uid).child("events");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final String currEventID = dataSnapshot.getValue().toString();

                DatabaseReference myEventRef = FirebaseDatabase.getInstance().getReference("events");
                myEventRef.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Event eventToCheck = dataSnapshot.getValue(Event.class);
                        String checkID = eventToCheck.getEventID();
                        if (checkID.equals(currEventID)) {
                            eventList.add(eventToCheck);
                            myAdapter.notifyItemInserted(eventList.size() - 1);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        getMenuInflater().inflate(R.menu.actionbar_vol_log, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.volGuide:
                startActivity(new Intent(Log_Vol.this, Help_Vol.class));
                return true;

            case R.id.itemHome:
                startActivity(new Intent(Log_Vol.this, Home_Vol.class));
                return true;

            case R.id.itemLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Log_Vol.this, Login.class));
                finish();
                return true;
        }
        return true;
    }
}
