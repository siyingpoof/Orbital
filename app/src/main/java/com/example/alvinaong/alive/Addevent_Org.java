package com.example.alvinaong.alive;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import 	android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Addevent_Org extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etEventName;
    private EditText etEventDescription;
    private EditText etEventTime;
    private EditText etEventVenue;
    private EditText etEventQuota;
    private EditText etEventHours;
    private EditText etEventDate;
    private ImageView ivEventImage;

    private final Event event = new Event();

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDataBaseRef;
    private StorageTask mUploadTask;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_org);

//        // Calendar code**********
//        Date today = new Date();
//        Calendar nextYear = Calendar.getInstance();
//        nextYear.add(Calendar.YEAR, 1);
//
//        CalendarPickerView datePicker = findViewById(R.id.calendar);
//        datePicker.init(today, nextYear.getTime())
////                .inMode(CalendarPickerView.SelectionMode.RANGE)
//                .withSelectedDate(today);
//        // To store in firebase as a Date
//        eventDate = datePicker.getSelectedDate();


        etEventName = (EditText) findViewById(R.id.etEventName);
        etEventDescription = (EditText) findViewById(R.id.etEventDescription);
        etEventTime = (EditText) findViewById(R.id.etEventTime);
        etEventVenue = (EditText) findViewById(R.id.etEventVenue);
        etEventQuota = (EditText) findViewById(R.id.etEventQuota);
        etEventHours = (EditText) findViewById(R.id.etEventHours);
        etEventDate = (EditText) findViewById(R.id.etEventDate);
        ivEventImage = (ImageView) findViewById(R.id.ivEventImage);
        Button bUploadEventImage = (Button) findViewById(R.id.bUploadEventImage);
        Button bCreateEvent = (Button) findViewById(R.id.bCreateEvent);

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference("events"); // references in the database
        mDataBaseRef = FirebaseDatabase.getInstance().getReference("events");

        bUploadEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        
        bCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(Addevent_Org.this, "Creating your new event...",
                            Toast.LENGTH_LONG).show();
                } else {
                    updateEvent(event);
                }
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(ivEventImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    private void updateEvent(final Event event) {

        boolean toContinue = true;

        String name = etEventName.getText().toString().trim();
        event.setName(name);
        String description = etEventDescription.getText().toString().trim();
        event.setDescription(description);
        String date = etEventDate.getText().toString().trim();
        event.setDate(date);
        String time = etEventTime.getText().toString().trim();
        event.setTime(time);
        String venue = etEventVenue.getText().toString().trim();
        event.setVenue(venue);
        String quotaS = etEventQuota.getText().toString().trim();

        String hours = etEventHours.getText().toString().trim();

        event.setOrganiser(Uid);

        final String UploadId = mDataBaseRef.push().getKey();
        event.setEventID(UploadId);

        if (mImageUri == null) {
            Log.e("mytag", "image is freaking null");
            Toast.makeText(this, "Please select your event's picture", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your event's name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter your event's description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select your event dates", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please enter your event's time", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(venue)) {
            Toast.makeText(this, "Please enter your event's venue", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(quotaS)) {
            Toast.makeText(this, "Please enter your event's quota", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(hours)) {
            Toast.makeText(this, "Please enter your event's hours", Toast.LENGTH_SHORT).show();
        } else {

            // ensure quota entered is a digit
            try {
                event.setQuota(Integer.parseInt(quotaS));
            } catch (NumberFormatException e) {
                toContinue = false;
                Toast.makeText(Addevent_Org.this, "Please enter the quota in number", Toast.LENGTH_SHORT).show();
            }
            // ensure hours entered is a digit
            try {
                event.setHours(Integer.parseInt(hours));
            } catch (NumberFormatException e) {
                toContinue = false;
                Toast.makeText(Addevent_Org.this, "Please enter the hours in number", Toast.LENGTH_SHORT).show();
            }

            if (toContinue == true) {
                // adding new event to "events" child
                final StorageReference fileReference = mStorageRef.child(event.getEventID() + "."
                        + getFileExtension(mImageUri));
                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Addevent_Org.this, "Creating your new event...",
                                        Toast.LENGTH_LONG).show();


                                mDataBaseRef.child(UploadId).setValue(event);

                                // adding eventID under organiser
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("organisers/"+ Uid + "/events");
                                databaseReference.child(event.getEventID()).setValue(event.getEventID());

                                Intent intent = new Intent(Addevent_Org.this, Home_Org.class);
                                startActivity(intent);
                            };

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Addevent_Org.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
