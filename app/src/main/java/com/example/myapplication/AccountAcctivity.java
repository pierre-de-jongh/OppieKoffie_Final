package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AccountAcctivity extends AppCompatActivity {

    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editProfile, logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);
        logoutButton = findViewById(R.id.logoutButton);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not logged in, redirect to login activity
            startActivity(new Intent(AccountAcctivity.this, MainActivity.class));
            finish();
        } else {
            String usernameUser = currentUser.getUid(); // Use UID as the username or choose a unique identifier

            showAllUserData(usernameUser);

            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passUserData();
                }
            });

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    startActivity(new Intent(AccountAcctivity.this, MainActivity.class));
                    finish();
                }
            });
        }
    }

    public void showAllUserData(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query query = reference.child(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("fullname").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String mobile = dataSnapshot.child("mobile").getValue(String.class);

                    titleName.setText(name);
                    titleUsername.setText("");
                    profileName.setText(name);
                    profileEmail.setText(email);
                    profileUsername.setText(username);
                    profilePassword.setText(mobile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during data retrieval
            }
        });
    }

    public void passUserData(){
        String userUsername = profileUsername.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        // Additional logic to handle passing user data
    }
}
