package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button google_img;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        google_img = findViewById(R.id.button_google_signin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);


        google_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        editTextLoginEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextLoginPwd = findViewById(R.id.editTextTextPassword);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        //login user
        Button login_button = findViewById(R.id.button_login);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(MainActivity.this, "Please Enter your Email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(MainActivity.this, "Please Re-enter your Email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(MainActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("Password is Required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });

        // open register
        Button register_button = findViewById(R.id.button_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "You are Logged in now", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed, make sure your password is correct!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(e.toString());
            }
        }
    }

    private void HomeActivity() {
        finish();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        authProfile.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "You are logged in with Google", Toast.LENGTH_SHORT).show();

                            // Store the user's information in the Firebase Realtime Database
                            String userId = authProfile.getCurrentUser().getUid();
                            String userEmail = authProfile.getCurrentUser().getEmail();
                            String userName = authProfile.getCurrentUser().getDisplayName();
                            String userMobileNumber = "";  // Placeholder for mobile number

                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Registered Users")
                                    .child(userId);

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // User data already exists in the database
                                        String userGender = snapshot.child("gender").getValue(String.class);

                                        if (userGender != null && !userGender.isEmpty()) {
                                            // Gender already assigned, skip showing the dialog
                                            navigateToHomeActivity();
                                        } else {
                                            // Gender not assigned, show the dialog
                                            showGenderDialog(new GenderDialogCallback() {
                                                @Override
                                                public void onGenderSelected(String userGender) {
                                                    // Save the gender in the database
                                                    userRef.child("gender").setValue(userGender);

                                                    navigateToHomeActivity();
                                                }
                                            });
                                        }
                                    } else {
                                        // User data doesn't exist, create new data
                                        userRef.child("email").setValue(userEmail);
                                        userRef.child("name").setValue(userName);

                                        // Show the gender dialog
                                        showGenderDialog(new GenderDialogCallback() {
                                            @Override
                                            public void onGenderSelected(String userGender) {
                                                // Save the gender in the database
                                                userRef.child("gender").setValue(userGender);

                                                navigateToHomeActivity();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle database error
                                    Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }



    private void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();  // Optional: Finish the current activity to prevent returning to it with back button
    }

    private void showGenderDialog(GenderDialogCallback callback) {
        String userGender = "Unknown";  // Default value for gender

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Gender")
                .setItems(R.array.genders, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] genders = getResources().getStringArray(R.array.genders);
                        String selectedGender = genders[which];
                        callback.onGenderSelected(selectedGender);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void saveUserInfo(String userId, String userEmail, String userName, String userGender, String userMobileNumber) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        DatabaseReference userRef = usersRef.child(userId);

        userRef.child("email").setValue(userEmail);
        userRef.child("name").setValue(userName);
        userRef.child("gender").setValue(userGender);
        userRef.child("mobileNumber").setValue(userMobileNumber);
    }
    interface GenderDialogCallback {
        void onGenderSelected(String gender);
    }
}
