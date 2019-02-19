package com.example.alvinaong.alive;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class SignUp extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialise widgets
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPw = findViewById(R.id.editTextPassword);

        Button mBtnSetUpVol = findViewById(R.id.setUpVol);
        Button mBtnSetUpOrg = findViewById(R.id.setUpOrg);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mEditTextEmail.setText(extras.getString("email"));
        }

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();
        mBtnSetUpVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();
                // Check if email is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if password is alphanumeric
                if(!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])[a-zA-Z0-9]+$")){
                    Toast.makeText(SignUp.this, "Password must contain one upper and lower case letter and one number", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set your own additional constraints

                // Create a new user
                auth.createUserWithEmailAndPassword(email, password).
                        addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Sign up successful, go to set up page
                                    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    // create new Volunteer object
                                    Volunteer currVol = new Volunteer();
                                    currVol.setVolID(Uid);
                                    currVol.setEmail(email);

                                    // update database first
                                    DatabaseReference volDBRef = FirebaseDatabase.getInstance().getReference("volunteers").child(Uid);
                                    volDBRef.setValue(currVol);

                                    Intent intent = new Intent(SignUp.this, SetUp_Vol.class);
//                                    intent.putExtra("Uid", Uid);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    // End the activity
                                    finish();
                                }
                            }
                        });
            }
        });

        mBtnSetUpOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();
                // Check if email is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if password is alphanumeric
                if(!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])[a-zA-Z0-9]+$")){
                    Toast.makeText(SignUp.this, "Password must contain one upper and lower case letter and one number", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set your own additional constraints

                // Create a new user
                auth.createUserWithEmailAndPassword(email, password).
                        addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Sign up successful, go to set up page
                                    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    // create new Volunteer object
                                    Organiser currOrg = new Organiser();
                                    currOrg.setOrgID(Uid);
                                    currOrg.setEmail(email);

                                    // update database first
                                    DatabaseReference orgDBRef = FirebaseDatabase.getInstance().getReference("organisers").child(Uid);
                                    orgDBRef.setValue(currOrg);

                                    Intent intent = new Intent(SignUp.this, SetUp_Org.class);
//                                    intent.putExtra("Uid", Uid);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    // End the activity
                                    finish();
                                }
                            }
                        });
            }
        });

    }
}
