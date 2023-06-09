package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.ui.account.AccountFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail,editTextRegisterDoB,editTextRegisterMobile,
    editTextRegisterPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private static final String TAG="RegisterActivity";
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toast.makeText(RegisterActivity.this,"You can Register now",Toast.LENGTH_LONG).show();

        //Entries from application
        progressBar=findViewById(R.id.progressBar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDoB=findViewById(R.id.editText_register_dob);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd=findViewById(R.id.editText_register_password);

        // RadioButton Entries
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //setting up datepicker
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calender = Calendar.getInstance();
                int day=calender.get(Calendar.DAY_OF_MONTH);
                int month=calender.get(Calendar.MONTH);
                int year=calender.get(Calendar.YEAR);

                picker=new DatePickerDialog(RegisterActivity.this, R.style.DatePickerTheme,  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year, month,day);
                picker.show();
            }
        });

        Button registerButton = findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting radio button selected option
                int selectedGenderID=radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderID);

                //Retrieving Entries data
                String textFullName=editTextRegisterFullName.getText().toString();
                String textEmail=editTextRegisterEmail.getText().toString();
                String textDoB=editTextRegisterDoB.getText().toString();
                String textMobile=editTextRegisterMobile.getText().toString();
                String textPwd=editTextRegisterPwd.getText().toString();
                String textGender;


                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this,"Please Enter your Full Name",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is Required");
                    editTextRegisterFullName.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this,"Please Enter your Email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is Required");
                    editTextRegisterEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this,"Please Enter a Valid Email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid Email is Required");
                    editTextRegisterEmail.requestFocus();
                }else if(TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter your Date Of Birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date Of Birth is Required");
                    editTextRegisterDoB.requestFocus();
                }else if(radioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegisterActivity.this, "Please Select your Gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is Required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter your Mobile Number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number is Required");
                    editTextRegisterMobile.requestFocus();
                }else if(textMobile.length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Please Re-Enter your Mobile Number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number is expected to be 10 Digits");
                    editTextRegisterMobile.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter a Password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is Required");
                    editTextRegisterPwd.requestFocus();
                }else if(textPwd.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password needs to be at least 6 Digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Stronger Password is Required");
                    editTextRegisterPwd.requestFocus();
                }else{
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPwd);

                }

            }
        });


    }
    //Connecting And Writing to FireBase Database
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //create user profile
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser=auth.getCurrentUser();

                            //Update Display Name
                            UserProfileChangeRequest profileChangeRequest =new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            //Save user to Database

                            ReadWriteUserDetails writeUserDetails= new ReadWriteUserDetails(textFullName,textDoB,textGender,textMobile,textEmail);

                            //Database Instance
                            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //send verification email
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this,"User Registered Successfully. Please Verify Your Email",Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(RegisterActivity.this, AccountFragment.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                |Intent.FLAG_ACTIVITY_NEW_TASK );
                                        startActivity(intent);
                                        finish();


                                    }else {
                                        Toast.makeText(RegisterActivity.this,"User Registered Fail. Please Try Again",Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });
                        }else{
                            try {
                                throw task.getException();
                            }catch ( FirebaseAuthWeakPasswordException e ){
                                editTextRegisterPwd.setError("Your Password is too Weak");
                                editTextRegisterPwd.requestFocus();
                            }catch (FirebaseAuthUserCollisionException e){
                                editTextRegisterEmail.setError("This email is already in Use! ");
                                editTextRegisterEmail.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}