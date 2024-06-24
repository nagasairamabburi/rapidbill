package com.example.rapidbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText txtEmail;
    Button mreset;
    String email;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.forEmail);
        mreset = findViewById(R.id.reset);

        mreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    void validateData(){
        email = txtEmail.getText().toString();
        if (email.isEmpty()){
            txtEmail.setError("Required");
        }else {
            forgetPass();
        }
    }

    void forgetPass(){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"Check your Email",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }else {
                    Toast.makeText(ForgotPassword.this,"Error : " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}