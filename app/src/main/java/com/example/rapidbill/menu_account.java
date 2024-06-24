package com.example.rapidbill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class menu_account extends AppCompatActivity {

    TextView mfull,memail,mphone;
    FirebaseAuth mauth;
    FirebaseUser user;
    DatabaseReference userReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_account);

        mfull = findViewById(R.id.name);
        memail = findViewById(R.id.userID);
        mphone = findViewById(R.id.phone);
        mauth = FirebaseAuth.getInstance();
        user = mauth.getCurrentUser();

        if (user != null) {
            userReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            getUserDetails();
        }

        ImageView imageView = findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the action you want when the image is clicked
                // For example, start a new activity
                startActivity(new Intent(menu_account.this, MainActivity.class));
            }
        });
    }
    private void getUserDetails() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    // Display user details in TextView
                    mfull.setText(user.getName());
                    memail.setText(user.getEmail());
                    mphone.setText(user.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

}