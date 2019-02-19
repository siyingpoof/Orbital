package com.example.alvinaong.alive;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home_Org extends AppCompatActivity {

    private TextView tv_OrgName, tv_OrgDes;
    private ImageView iv_OrgImage;

    private List<Event> eventList;
    private RecyclerView myRV;
    private EventAdapterOrg myAdapter;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String Uid;

    Organiser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_org_layout);

        tv_OrgName = (TextView) findViewById(R.id.tvOrgName);
        tv_OrgDes = (TextView) findViewById(R.id.tvOrgDes);
        iv_OrgImage = (ImageView) findViewById(R.id.ivOrgImage);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(Home_Org.this, Login.class);
            startActivity(intent);
            finish();
        }

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // check if database if completed first or not
        DatabaseReference checkDBRef = FirebaseDatabase.getInstance().getReference("organisers").child(Uid);
        checkDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Organiser currOrg = dataSnapshot.getValue(Organiser.class);

                // set up not complete yet
                if (currOrg.getName().equals("")) {
                    Toast.makeText(Home_Org.this, "Please set up your profile first", Toast.LENGTH_SHORT).show();
                    Intent setUpIntent = new Intent(Home_Org.this, SetUp_Org.class);
                    setUpIntent.putExtra("email", currOrg.getEmail());
                    startActivity(setUpIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("organisers").child(Uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Organiser.class);
                tv_OrgName.setText(user.getName());
                tv_OrgDes.setText(user.getDescription());
                storageReference = FirebaseStorage.getInstance().getReference("organisers").child(Uid+".jpg");
                Glide.with(Home_Org.this).load(storageReference).into(iv_OrgImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        eventList = new ArrayList<>();

        // Recycler View
        myRV = (RecyclerView) findViewById(R.id.recyclerview_id_org);
        myRV.setHasFixedSize(true);
        myRV.setLayoutManager(new GridLayoutManager(this, 3));
        myAdapter = new EventAdapterOrg(eventList);
        myRV.setAdapter(myAdapter);
//        myRV.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        // Firebase finding for events under current user
        readFirebase();
    }


    private void readFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("organisers").child(Uid).child("events");
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

        Log.e("mytag", "adapter size of home_org in the end is "+myAdapter.getItemCount());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_org, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemAddEvent:
                startActivity(new Intent(Home_Org.this, Addevent_Org.class));
                return true;

            case R.id.orgGuide:
                startActivity(new Intent(Home_Org.this, Help_Org.class));
                return true;

            case R.id.itemLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home_Org.this, Login.class));
                finish();
                return true;
        }
        return true;
    }


}
