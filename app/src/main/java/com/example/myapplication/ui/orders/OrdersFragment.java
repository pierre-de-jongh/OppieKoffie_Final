package com.example.myapplication.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.ui.OrderDetailsActivity;
import com.example.myapplication.ui.OrderDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private ListView listViewOrders;
    private ArrayList<String> orderIdsList;
    private ArrayAdapter<String> ordersAdapter;

    private DatabaseReference ordersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        listViewOrders = view.findViewById(R.id.list_view_orders);
        orderIdsList = new ArrayList<>();
        ordersAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, orderIdsList);
        listViewOrders.setAdapter(ordersAdapter);

        // Get a reference to the "orders" node in your Firebase database
        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

        // Retrieve order IDs from Firebase
        retrieveOrderIds();

        // Set item click listener for the ListView
        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOrderId = orderIdsList.get(position);

                // Start the OrderDetailsActivity and pass the selected order ID
                Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                intent.putExtra("order_id", selectedOrderId);
                startActivity(intent);
            }
        });

        return view;
    }

    private void retrieveOrderIds() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderIdsList.clear(); // Clear the existing order IDs

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    orderIdsList.add(orderId);
                }

                ordersAdapter.notifyDataSetChanged(); // Update the ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database retrieval
                // For simplicity, you can leave this empty or display an error message
            }
        });
    }
}
