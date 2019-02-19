package com.example.alvinaong.alive;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Event_View_Org extends AppCompatActivity {

    // Event that we are on currently
    private Event currEvent;
    private String eventID;
    private int hoursAwarded;

    private TextView tvName, tvDate, tvTime, tvQuota, tvVenue, tvDescription;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view_org);

        tvName = (TextView) findViewById(R.id.txtName);
        tvDate = (TextView) findViewById(R.id.txtDate);
        tvTime = (TextView) findViewById(R.id.txtTime);
        tvQuota = (TextView) findViewById(R.id.txtQuota);
        tvVenue = (TextView) findViewById(R.id.txtVenue);
        tvDescription = (TextView) findViewById(R.id.txtDesc);
        ivImage = (ImageView) findViewById(R.id.org_photo);

        final Button changeEventStatus = (Button) findViewById(R.id.changeEventStatus);

        // retrieving current event ID from previous activity
        eventID = getIntent().getExtras().get("eventID").toString();

        // retrieving event's details from firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Event checkEvent = dataSnapshot.getValue(Event.class);
                String checkID = checkEvent.getEventID();
                if (checkID.equals(eventID)) {
                    currEvent = checkEvent;

                    tvName.setText(currEvent.getName());
                    tvDate.setText(currEvent.getDate());
                    tvTime.setText(currEvent.getTime());
                    tvQuota.setText(Integer.toString(currEvent.getQuota()));
                    tvVenue.setText(currEvent.getVenue());
                    tvDescription.setText(currEvent.getDescription());
                    if (currEvent.getEventStatus()==true) {
                        changeEventStatus.setText("Close event");
                    } else {
                        changeEventStatus.setText("Open event");
                    }

                    hoursAwarded = currEvent.getHours();
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
        Glide.with(this).load(storageReference).into(ivImage);


        // Allow organiser to view the details of the list of volunteers who signed up.
        Button viewSignUps = (Button) findViewById(R.id.signupList);
        viewSignUps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Event_View_Org.this, SignUpsListView_Org.class);
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });

        // When organiser decides to close the signups to prevent any more changes in the signups

        changeEventStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close the event
                if (currEvent.getEventStatus()) {
                    currEvent.setEventStatus(false);
                    // update firebase
                    DatabaseReference eventStatusDBRef = FirebaseDatabase.getInstance().getReference("events/" + eventID);
                    eventStatusDBRef.setValue(currEvent);
                    // change text on the button
                    changeEventStatus.setText("Open event");

                    Toast.makeText(Event_View_Org.this, "Event has closed", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currEvent.getEventStatus()==false) {
                    currEvent.setEventStatus(true);
                    // update firebase
                    DatabaseReference eventStatusDBRef = FirebaseDatabase.getInstance().getReference("events/" + eventID);
                    eventStatusDBRef.setValue(currEvent);
                    // change text on the button
                    changeEventStatus.setText("Close event");

                    Toast.makeText(Event_View_Org.this, "Event has re-opened", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        //         QR Scanner code************************
        FloatingActionButton qrScanner = (FloatingActionButton) findViewById(R.id.qr_id);
        final Activity activity = this;
        qrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning" , Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Recording attendance..." , Toast.LENGTH_LONG).show();
                final String Uid = result.getContents();

                // search uid in the signupslist for this event

                if (currEvent.getSignUpsList().contains(Uid)) {
                    currEvent.updateAttendance(Uid);

                    // update database
                    DatabaseReference updateEventDBRef = FirebaseDatabase.getInstance().getReference("events");
                    updateEventDBRef.child(eventID).setValue(currEvent);

                    final DatabaseReference updateVolDBRef = FirebaseDatabase.getInstance().getReference("volunteers");
                    updateVolDBRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Volunteer checkVol = dataSnapshot.getValue(Volunteer.class);
                            if (checkVol.getVolID().equals(Uid)) {
                                Volunteer currVol = checkVol;
                                currVol.addHours(currEvent.getHours());
                                updateVolDBRef.child(Uid).setValue(currVol);
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

                } else {
                    Toast.makeText(Event_View_Org.this, "This volunteer did not sign up for this event.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
