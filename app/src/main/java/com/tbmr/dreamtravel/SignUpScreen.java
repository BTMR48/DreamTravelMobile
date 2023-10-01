package com.tbmr.dreamtravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpScreen extends AppCompatActivity {


    private EditText mEmail;

    private EditText phone;
    private EditText mPass;
    private TextView mTextView;
    private Button signUpBtn;

    private EditText  signUpNic;

    private EditText  msignUpUserName;
    private EditText  msignupPhone;
    private FirebaseFirestore db;  // Add this line for Firestore

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        mEmail = findViewById(R.id.signupEmail);
        mPass = findViewById(R.id.signupPass);
        mTextView = findViewById(R.id.loginpath);
        signUpBtn = findViewById(R.id.signupbtn);
         signUpNic = findViewById(R.id.signupNic);
        msignUpUserName = findViewById(R.id.signupUserName);
        msignupPhone = findViewById(R.id.signupPhone);
        mAuth = FirebaseAuth.getInstance();
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpScreen.this , LoginScreen.class));
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });


    }

    private void createUser() {
        final String email = mEmail.getText().toString();
        final String nic = signUpNic.getText().toString();
        final String pass = mPass.getText().toString();
        final String signUpUserName = msignUpUserName.getText().toString();
        final String signupPhone = msignupPhone.getText().toString();

        // First, check if NIC already exists in Firestore
        db.collection("users")
                .whereEqualTo("nic", nic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                Toast.makeText(SignUpScreen.this, "NIC already exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Register user in FirebaseAuth
                                mAuth.createUserWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Save to Firestore
                                                    DocumentReference newUser = db.collection("users").document(nic);

                                                    UserModel newUserModel = new UserModel(email, nic, signUpUserName, signupPhone, pass);

                                                    newUser.set(newUserModel)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(SignUpScreen.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(SignUpScreen.this, "Database Error!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(SignUpScreen.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpScreen.this, "Database Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private static class UserModel {
        public String email;
        public String nic;
        public String signUpUserName;
        public String signupPhone;
        public String pass;
        public UserModel(String email, String nic, String signUpUserName, String signupPhone, String pass) {
            this.email = email;
            this.nic = nic;
            this.signUpUserName = signUpUserName;
            this.pass = pass;
            this.signupPhone = signupPhone;
        }

        // Optional: Add getter methods
        public String getEmail() {
            return email;
        }

        public String getNic() {
            return nic;
        }

        public String getSignUpUserName() {
            return signUpUserName;
        }

        public String getSignupPhone() {
            return signupPhone;
        }
        public String getPass() {
            return pass;
        }
    }

}