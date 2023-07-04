package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.checkout.CheckoutFragment;
import com.example.myapplication.ui.shop.ShopFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderDetailsActivity extends AppCompatActivity {

    private ListView listViewOrderItems;
    private ArrayList<String> orderItemsList;
    private ArrayAdapter<String> orderItemsAdapter;

    private DatabaseReference coffeesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        listViewOrderItems = findViewById(R.id.list_view_order_items);
        orderItemsList = new ArrayList<>();
        orderItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, orderItemsList);
        listViewOrderItems.setAdapter(orderItemsAdapter);

        coffeesRef = FirebaseDatabase.getInstance().getReference().child("Coffees");

        // Retrieve two random coffees from Firebase
        retrieveRandomCoffees();

        Button orderAgainButton = findViewById(R.id.button_order_again);
        orderAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Order Again" button click
                Intent intent = new Intent(OrderDetailsActivity.this, ShopFragment.class);
                startActivity(intent);
                finish();
            }
        });

        Button homeButton = findViewById(R.id.button_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the HomeActivity
                Intent intent = new Intent(OrderDetailsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish the current activity if you don't want to return to it
            }
        });
    }

    private void retrieveRandomCoffees() {
        coffeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> coffeeNames = new ArrayList<>();
                List<Double> coffeePrices = new ArrayList<>();

                for (DataSnapshot coffeeSnapshot : dataSnapshot.getChildren()) {
                    String coffeeName = coffeeSnapshot.child("name").getValue(String.class);
                    double coffeePrice = coffeeSnapshot.child("price").getValue(Double.class);

                    coffeeNames.add(coffeeName);
                    coffeePrices.add(coffeePrice);
                }

                Random random = new Random();
                int coffeeCount = coffeeNames.size();

                if (coffeeCount >= 2) {
                    int randomIndex1 = random.nextInt(coffeeCount);
                    int randomIndex2;

                    String coffee1 = coffeeNames.get(randomIndex1);
                    double price1 = coffeePrices.get(randomIndex1);

                    do {
                        randomIndex2 = random.nextInt(coffeeCount);
                    } while (randomIndex2 == randomIndex1);

                    String coffee2 = coffeeNames.get(randomIndex2);
                    double price2 = coffeePrices.get(randomIndex2);

                    String orderItem1 = coffee1 + " - $" + price1;
                    String orderItem2 = coffee2 + " - $" + price2;

                    orderItemsList.add(orderItem1);
                    orderItemsList.add(orderItem2);

                    orderItemsAdapter.notifyDataSetChanged(); // Update the ListView
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur during the database retrieval
                // For simplicity, you can leave this empty or display an error message
            }
        });
    }

    }

