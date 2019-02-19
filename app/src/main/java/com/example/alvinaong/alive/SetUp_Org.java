package com.example.alvinaong.alive;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;

public class SetUp_Org extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button bChooseImage;
    private Button bCreateProfile;
    private ImageView ivImage;
    private EditText etName;
    private EditText etContact;
    private EditText etDescription;
    private EditText etEmail;

    private final Organiser organiser = new Organiser();

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDataBaseRef;
    private StorageTask mUploadTask;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_org);

        bChooseImage = (Button) findViewById(R.id.bChooseImage);
        bCreateProfile = (Button) findViewById(R.id.bCreate);
        ivImage = (ImageView) findViewById(R.id.ivOrgImage);
        etName = (EditText) findViewById(R.id.etName);
        etContact = (EditText) findViewById(R.id.etMobile);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etDescription = (EditText) findViewById(R.id.etDescription);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(SetUp_Org.this, Login.class);
            startActivity(intent);
            finish();
        }

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            etEmail.setText(extras.getString("email"));
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("organisers"); // references in the database
        mDataBaseRef = FirebaseDatabase.getInstance().getReference("organisers");

        bChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        bCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(SetUp_Org.this, "Creating your profile...", Toast.LENGTH_LONG).show();
                } else {
                    updateUser(organiser);
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
            Glide.with(this).load(mImageUri).into(ivImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void updateUser(final Organiser organiser) {

        String name = etName.getText().toString().trim();
        organiser.setName(name);
        String description = etDescription.getText().toString().trim();
        organiser.setDescription(description);
        String email = etEmail.getText().toString().trim();
        organiser.setEmail(email);
        String mobile = etContact.getText().toString().trim();
        organiser.setContact(mobile);

        if (mImageUri == null) {
            Toast.makeText(this, "Please select your team's picture", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your team's name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter your team's description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your team's email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mobile)){
            Toast.makeText(this, "Please enter your team's contact number", Toast.LENGTH_SHORT).show();
        } else {

            final StorageReference fileReference = mStorageRef.child(Uid + "."
                    + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SetUp_Org.this, "Creating your profile...",
                                    Toast.LENGTH_LONG).show();
//                            UploadImage upload = new UploadImage(etName.getText().toString().trim(),
//                                    taskSnapshot.getDownloadUrl().toString());
//
                            organiser.setOrgID(Uid);
                            mDataBaseRef.child(Uid).setValue(organiser);

                            Intent intent = new Intent(SetUp_Org.this, Home_Org.class);
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SetUp_Org.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}