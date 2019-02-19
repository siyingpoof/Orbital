package com.example.alvinaong.alive;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignUpsListView_Org extends AppCompatActivity {

    private String eventID;
    private List<String> userUids;
    private List<Volunteer> signupsList;
    private List<Volunteer> attendeesList;

    private RecyclerView signUpsRV;
    private RecyclerView attendeesRV;
    private SignUpsAdapter signUpsAdapter;
    private SignUpsAdapter attendeesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_ups_list_org);

        Bundle extras = getIntent().getExtras();
        eventID = extras.getString("eventID");

        signupsList = new ArrayList<>();
        attendeesList = new ArrayList<>();

        // Recycler View for signups
        signUpsRV = (RecyclerView) findViewById(R.id.sign_ups_recyclerview);
        signUpsRV.setHasFixedSize(true);
        signUpsRV.setLayoutManager(new LinearLayoutManager(this));
        signUpsAdapter = new SignUpsAdapter(signupsList);
        signUpsRV.setAdapter(signUpsAdapter);

        // Recycler View for attendees
        attendeesRV = (RecyclerView) findViewById(R.id.attendees_recyclerview);
        attendeesRV.setHasFixedSize(true);
        attendeesRV.setLayoutManager(new LinearLayoutManager(this));
        attendeesAdapter = new SignUpsAdapter(attendeesList);
        attendeesRV.setAdapter(attendeesAdapter);


        // to get Uid of the people who signed up
        DatabaseReference eventDB = FirebaseDatabase.getInstance().getReference("events").child(eventID);
        eventDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event currEvent = dataSnapshot.getValue(Event.class);
                if (currEvent.getSignUpsList() != null) {
                    for(String currUser : currEvent.getSignUpsList()) {
                        final String currUserUid = currUser;
                        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("volunteers");
                        userDB.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.getValue(Volunteer.class).getVolID().equals(currUserUid)) {
                                    Volunteer currVol = dataSnapshot.getValue(Volunteer.class);
                                    signupsList.add(currVol);
                                    signUpsAdapter.notifyItemInserted(signupsList.size() - 1);
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

                }

                if (currEvent.getAttendanceList() != null) {
                    for(String attendee : currEvent.getAttendanceList()) {
                        final String currUserUid = attendee;
                        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("volunteers");
                        userDB.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.getValue(Volunteer.class).getVolID().equals(currUserUid)) {
                                    Volunteer currVol = dataSnapshot.getValue(Volunteer.class);
                                    attendeesList.add(currVol);
                                    attendeesAdapter.notifyItemInserted(attendeesList.size() - 1);
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

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // to get the Volunteer.class for the vols who signed up


    }
}
