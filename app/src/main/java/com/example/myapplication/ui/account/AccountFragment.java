package com.example.myapplication.ui.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editProfile, logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePassword = view.findViewById(R.id.profilePassword);
        titleName = view.findViewById(R.id.titleName);
        titleUsername = view.findViewById(R.id.titleUsername);

        logoutButton = view.findViewById(R.id.logoutButton);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not logged in, redirect to login activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            String usernameUser = currentUser.getUid(); // Use UID as the username or choose a unique identifier

            showAllUserData(usernameUser);


            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }
            });
        }

        return view;
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

    public void passUserData() {
        String userUsername = profileUsername.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        // Additional logic to handle passing user data
    }
}
