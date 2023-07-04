package com.example.myapplication.ui.checkout;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.R;
import com.example.myapplication.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckoutFragment extends Fragment {

    private TextView name, email, orderIDTextView;
    private EditText pickupTimeTextView;
    private Button confirmOrderButton, pickupTimeButton;
    private SharedViewModel sharedViewModel;
    private DatabaseReference ordersRef;
    private DatabaseReference usersRef;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        // Initialize views
        name = view.findViewById(R.id.txt_user_name);
        email = view.findViewById(R.id.txt_email);

        orderIDTextView = view.findViewById(R.id.txt_order_id);
        confirmOrderButton = view.findViewById(R.id.confirmOrderButton);

        pickupTimeButton = view.findViewById(R.id.btn_pickupTime);
        pickupTimeTextView = view.findViewById(R.id.txt_pickup_time);

        mAuth = FirebaseAuth.getInstance();

        // Set user name and email if available
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name.setText("Name: " + user.getDisplayName());
            email.setText("Email: " + user.getEmail());
        }

        // Get the SharedViewModel instance
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Get the total price from the SharedViewModel
        sharedViewModel.getTotalPrice().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalPrice) {
                String formattedPrice = String.format("Pay: R%.2f", totalPrice / 100.0);
                confirmOrderButton.setText(formattedPrice);
            }
        });

        // Get a reference to the "orders" node in Firebase database
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Generate and display the current order ID
        // Get the latest order ID from the Firebase database
        ordersRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String orderId = childSnapshot.getKey();
                    orderIDTextView.setText("Order ID: " + orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        // Set the click listener for the confirm order button
        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                if (currentUser != null) {
//                    usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
//                    String path = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    DatabaseReference users1Ref = usersRef.child(path);
//
//                    users1Ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.hasChild("lp")) {
//                                int count = (int) snapshot.child("lp").getValue();
//                                usersRef.child("lp").setValue(count++);
//                            } else {
//                                usersRef.child("lp").setValue(1);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

                    NavController navController;
                    navController = NavHostFragment.findNavController(CheckoutFragment.this);
                    navController.navigate(R.id.action_CheckoutFragment_to_HomeFragment);

                    Toast.makeText(getActivity(), "Your purchase was successful", Toast.LENGTH_LONG).show();
                }
//            }
        });

        pickupTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        pickupTimeTextView.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });

        return view;
    }
}