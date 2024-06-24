package com.example.rapidbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleBtn;

    EditText mFullName, mEmail, mPassword, mPhone;
    Button mRegisterBtn;
    ProgressBar progressBar;
    boolean passwordVisible;
    TextView mloginBtn;

    FirebaseAuth mAuth;
    DatabaseReference reference;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        googleBtn = findViewById(R.id.google_btn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.registerBn);
        mloginBtn = findViewById(R.id.createtext);
        progressBar = findViewById(R.id.progressbar);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already authenticated, redirect to main activity
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        }

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= mPassword.getRight() - mPassword.getCompoundDrawables()[right].getBounds().width()) {
                        int selection = mPassword.getSelectionEnd();
                        if (passwordVisible) {
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibilityoff, 0);
                            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        } else {
                            mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.removepasswordhide, 0);
                            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        mPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mFullName.getText().toString();
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString();
                final String phone = mPhone.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(phone) || password.length() < 8 || phone.length() != 10) {
                    showToast("Invalid input. Please check all fields.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Create user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // User created successfully in Firebase Authentication
                                    showToast("Registration successful!");
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();

                                        // Create User object and store in Realtime Database
                                        User user = new User(userId, name, email,password, phone);
                                        reference.child(userId).setValue(user);

                                        // Continue with your app logic, e.g., navigate to the next activity
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    // If registration fails, display a message to the user.
                                    showToast("Registration failed. " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }
}