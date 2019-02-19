package com.example.alvinaong.alive;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;

public class Event_View_Vol extends AppCompatActivity {

    private Event currEvent;
    private String Uid;
    private int eventQuota;
    boolean signedUp = false;
    boolean attended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view_vol);

        final TextView tvEventName = (TextView) findViewById(R.id.txtName);
        final TextView tvEventDate = (TextView) findViewById(R.id.txtDate);
        final TextView tvEventTime = (TextView) findViewById(R.id.txtTime);
        final TextView tvEventQuota = (TextView) findViewById(R.id.txtQuota);
        final TextView tvEventVenue = (TextView) findViewById(R.id.txtVenue);
        final TextView tvEventDescription = (TextView) findViewById(R.id.txtDesc);
        ImageView img = (ImageView) findViewById(R.id.org_photo);
        final Button btnsignup = (Button) findViewById(R.id.sign_up_id);

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Receive data from EventAdapterVol (Home_Vol)
        Bundle extras = getIntent().getExtras();
        final String eventID = extras.getString("eventID");
        Log.e("mytag", eventID);

        // Receive Data from firebase
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Event checkEvent = dataSnapshot.getValue(Event.class);
                String checkID = checkEvent.getEventID();
                Log.e("mytag", "checkID is "+checkID);
                if (checkID.equals(eventID)) {
                    currEvent = checkEvent;
                    Log.e("mytag", "confirmed event ID is "+checkID);
                    tvEventName.setText(currEvent.getName());
                    tvEventDate.setText(currEvent.getDate());
                    tvEventTime.setText(currEvent.getTime());
                    tvEventQuota.setText(Integer.toString(currEvent.getQuota()));
                    eventQuota = currEvent.getQuota();
                    tvEventVenue.setText(currEvent.getVenue());
                    tvEventDescription.setText(currEvent.getDescription());

                    // determine if user has signed up for this event already or not
                    if (currEvent.getEventStatus()==false){
                        btnsignup.setText("Event is closed");
                    }

                    if (currEvent.getSignUpsList() != null) {
                        if (currEvent.getSignUpsList().contains(Uid) && currEvent.getEventStatus()==true) {
                            btnsignup.setText("Signed up");
                            signedUp = true;
                        } else if (currEvent.getSignUpsList().contains(Uid)) {
                            signedUp = true;
                        }
                    }

                    if (currEvent.getAttendanceList() != null) {
                        if (currEvent.getAttendanceList().contains(Uid)) {
                            btnsignup.setText("Attended");
                            attended = true;
                        }
                    }
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


        // loading image into imageview
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("events").child(eventID+".jpg");
        Glide.with(this).load(storageReference).into(img);


        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if this event quota != 0, add this volunteer to this event's signups list
                // if addition is successful
                // if (!event.addVolunteer(this volunteer in FirebaseUser form))
                // Toast.makeText(getApplicationContext(), "Error in signing up. Please contact us separately.", Toast.LENGTH_LONG).show();
                // else add this event's quota by -1
                // add this event to the volunteer's signedup events list (maybe add to calendar also)

                if (signedUp) {
                    Toast.makeText(Event_View_Vol.this, "You have signed up previously", Toast.LENGTH_LONG).show();
                } else if (attended) {
                    Toast.makeText(Event_View_Vol.this, "You have attended this event", Toast.LENGTH_LONG).show();
                } else if (currEvent.getEventStatus()==false) {
                    Toast.makeText(Event_View_Vol.this,"Event has closed.", Toast.LENGTH_LONG).show();
                } else if (eventQuota<=0) {
                    Toast.makeText(Event_View_Vol.this,"Quota for this event has been filled up.", Toast.LENGTH_LONG).show();
                } else {
                    final DatabaseReference eventDatabaseReference = FirebaseDatabase.getInstance().getReference("events");
                    eventDatabaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Event checkEvent = dataSnapshot.getValue(Event.class);
                            String checkID = checkEvent.getEventID();

                            if (checkID.equals(eventID)) {
                                // if successfully added this volunteer to list of signups
                                if (currEvent.canAddVolunteer(Uid)) {
                                    // update quota
                                    currEvent.reduceQuota();
//                                    eventDatabaseReference.child(eventID).child("quota").setValue(currEvent.getQuota());

                                    // upload updated event to database
                                    DatabaseReference uploadEventDBRef = FirebaseDatabase.getInstance().getReference("events");
                                    uploadEventDBRef.child(eventID).setValue(currEvent);

                                    // add signed up event under volunteer in database
                                    DatabaseReference addEventtoVolDBRef = FirebaseDatabase.getInstance().getReference("volunteers/" + Uid + "/events");
                                    addEventtoVolDBRef.child(eventID).setValue(eventID);

                                    Toast.makeText(Event_View_Vol.this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();
//                                    eventDatabaseReference.child(eventID).child("signups").child(Uid).setValue(Uid);

                                    btnsignup.setText("Signed up");
                                } else {
                                    Toast.makeText(Event_View_Vol.this, "You have signed up previously", Toast.LENGTH_LONG).show();
                                }
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


        });

    };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_vol_event, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHome:
                Intent intentHome = new Intent(Event_View_Vol.this, Home_Vol.class);
                startActivity(intentHome);
                return true;

            case R.id.itemLogPage:
                Intent intentLog = new Intent(Event_View_Vol.this, Log_Vol.class);
                startActivity(intentLog);
                return true;

            case R.id.itemLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Event_View_Vol.this, Login.class));
                finish();
                return true;
        }
        return true;
    }

}