package com.example.alvinaong.alive;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;


public class Login extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_login);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        // Initialise widgets
        Button mButtonLogin = findViewById(R.id.buttonLogin);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPw = findViewById(R.id.editTextPassword);
        TextView mTextViewSignup = findViewById(R.id.textViewSignup);

        // Direct to sign up page
        mTextViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                if (!TextUtils.isEmpty(mEditTextEmail.getText().toString().trim())) {
                    intent.putExtra("email", mEditTextEmail.getText().toString().trim());
                }
                startActivity(intent);
            }
        });

        // Check user type and login accordingly.
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEditTextPw.getText().toString().trim();
                // Check if email is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                // check if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // error occurred
                                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    // *** TO DO ***
                                    // check based on email if user is instanceOf volunteer or organiser
                                    // direct accordingly
                                    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                                    final String RegisteredUserID = currUser.getUid();
                                    //    final Organiser registeredUser;

                                    DatabaseReference loginOrgDatabase = FirebaseDatabase.getInstance().getReference("organisers")/*.child(RegisteredUserID)*/;
                                    loginOrgDatabase.addChildEventListener(new ChildEventListener() {

                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Organiser checkOrg = dataSnapshot.getValue(Organiser.class);
                                            String checkID = checkOrg.getOrgID();
                                            if (checkID.equals(RegisteredUserID)) {
                                                Organiser registeredUser = checkOrg;
                                                Intent intent = new Intent(Login.this, Home_Org.class);
                                                intent.putExtra("Uid", registeredUser.getOrgID());
                                                startActivity(intent);
                                                finish();
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

                                    DatabaseReference loginVolDatabase = FirebaseDatabase.getInstance().getReference("volunteers")/*.child(RegisteredUserID)*/;
                                    loginVolDatabase.addChildEventListener(new ChildEventListener() {

                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Volunteer checkVol = dataSnapshot.getValue(Volunteer.class);
                                            String checkID = checkVol.getVolID();
                                            if (checkID.equals(RegisteredUserID)) {
                                                Volunteer registeredUser = checkVol;
                                                Intent intent = new Intent(Login.this, Home_Vol.class);
                                                intent.putExtra("Uid", registeredUser.getVolID());
                                                startActivity(intent);
                                                finish();
                                                return;
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

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Please sign up below.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
}
