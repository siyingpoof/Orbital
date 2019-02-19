package com.example.alvinaong.alive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.util.Log;

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

public class SetUp_Vol extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etContact, etEmail;
    private Spinner spinner;
    private ImageView ivImage;
    private Button bChooseImage;
    private Button bCreateProfile;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private String Uid;
    private final Volunteer volunteer = new Volunteer();

    private Uri mImageUri;
    private StorageTask mUploadTask;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_vol);

        etName = findViewById(R.id.etName);
        radioGroup = findViewById(R.id.radioGender);
        spinner = findViewById(R.id.spinSchool);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        ivImage = findViewById(R.id.ivUploadedImage);
        bChooseImage = findViewById(R.id.bUploadImage);
        bCreateProfile = findViewById(R.id.bCreate);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(SetUp_Vol.this, Login.class);
            startActivity(intent);
            finish();
        }

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            etEmail.setText(extras.getString("email"));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("volunteers");
        storageReference = FirebaseStorage.getInstance().getReference("volunteers");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.schoolChoices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


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
                    Toast.makeText(SetUp_Vol.this, "Creating your profile...", Toast.LENGTH_LONG).show();
                } else {
                    updateUser(volunteer);
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

    private void updateUser(final Volunteer volunteer) {

        String name = etName.getText().toString().trim();
        volunteer.setName(name);

        // get selected radio button from radioGroup
        int selectedGender = radioGroup.getCheckedRadioButtonId();
        // find radioButton by returned id
        radioButton = (RadioButton) findViewById(selectedGender);
        String gender = radioButton.getText().toString();
        volunteer.setGender(gender);

        String school = spinner.getSelectedItem().toString();
        volunteer.setSchool(school);

        String contactNumber = etContact.getText().toString().trim();
        volunteer.setContactNumber(contactNumber);

        String email = etEmail.getText().toString().trim();
        volunteer.setEmail(email);

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please choose your gender", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(school)) {
            Toast.makeText(this, "Please choose your school", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(contactNumber)) {
            Toast.makeText(this, "Please enter your contact number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } else if (mImageUri == null) {
            Toast.makeText(this, "Please select your profile picture", Toast.LENGTH_SHORT).show();
        } else {
            final StorageReference fileReference = storageReference.child(Uid + "."
                    + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SetUp_Vol.this, "Creating your profile...",
                                    Toast.LENGTH_LONG).show();
//                            UploadImage upload = new UploadImage(etName.getText().toString().trim(),
//                                    taskSnapshot.getDownloadUrl().toString());

                            volunteer.setVolID(Uid);
                            databaseReference.child(Uid).setValue(volunteer);

                            Intent intent = new Intent(SetUp_Vol.this, Home_Vol.class);
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SetUp_Vol.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
