package com.example.rapidbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText mEmail, mpassword;
    Button mLoginBtn;
    TextView mCreateBtn, mForgot;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    boolean pass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.Email);
        mpassword = findViewById(R.id.Password);
        mForgot = findViewById(R.id.forgotpassword);
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.LoginBtn);
        mCreateBtn = findViewById(R.id.createtext);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already authenticated, redirect to main activity
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        mpassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= mpassword.getRight() - mpassword.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = mpassword.getSelectionEnd();
                        if (pass) {
                            mpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibilityoff, 0);
                            mpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            pass = false;
                        } else {
                            mpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.removepasswordhide, 0);
                            mpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            pass = true;
                        }
                        mpassword.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validatePassword()) {
                    // Do nothing if email or password is invalid
                } else {
                    loginUser();
                }
            }
        });

        mForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    public Boolean validateEmail() {
        String val = mEmail.getText().toString().trim();
        if (val.isEmpty()) {
            mEmail.setError("Email cannot be Empty");
            return false;
        } else {
            mEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = mpassword.getText().toString().trim();
        if (val.isEmpty()) {
            mpassword.setError("Password cannot be Empty");
            return false;
        } else {
            mpassword.setError(null);
            return true;
        }
    }

    private void loginUser() {
        final String userEmail = mEmail.getText().toString().trim();
        String userPassword = mpassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                showToast("Login successful!");
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Failed to retrieve user information");
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage.contains("no user record")) {
                                showToast("Email does not exist. Please register.");
                            } else {
                                showToast("Login failed. " + errorMessage);
                            }
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }
}