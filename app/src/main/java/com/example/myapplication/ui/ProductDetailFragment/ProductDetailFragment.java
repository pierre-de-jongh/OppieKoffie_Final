package com.example.myapplication.ui.ProductDetailFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.CartItem;
import com.example.myapplication.CartViewModel;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProductDetailFragment extends Fragment {

    private DatabaseReference ProductsRef;
    private TextView txtProductName, txtProductDescription, txtProductPrice;
    private ImageView imageView;
    private CartViewModel cartViewModel;
    private String productImageURL;

    private Spinner spinnerSize;
    private Spinner spinnerMilk;
    private Spinner spinnerSugar;

    private String selectedSize;
    private String selectedMilk;
    private String selectedSugar;

    private Spinner spinnerSugarQuantity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        String productId = "";
        Bundle args = getArguments();
        if (args != null) {
            productId = args.getString("productId", "");
        }

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Coffees").child(productId);

        txtProductName = view.findViewById(R.id.txt_product_name);
        txtProductDescription = view.findViewById(R.id.txt_product_description);
        txtProductPrice = view.findViewById(R.id.txt_product_price);
        imageView = view.findViewById(R.id.product_image);
        spinnerSugarQuantity = view.findViewById(R.id.spinner_sugar_quantity);

        Button btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        String finalProductId = productId;

        spinnerSize = view.findViewById(R.id.spinner_size);
        spinnerMilk = view.findViewById(R.id.spinner_milk);
        spinnerSugar = view.findViewById(R.id.spinner_sugar);

        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sizes_array, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);

        ArrayAdapter<CharSequence> milkAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.milk_array, android.R.layout.simple_spinner_item);
        milkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMilk.setAdapter(milkAdapter);

        ArrayAdapter<CharSequence> sugarAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sugar_array, android.R.layout.simple_spinner_item);
        sugarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSugar.setAdapter(sugarAdapter);
        ArrayAdapter<CharSequence> sugarQuantityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sugar_quantity_array, android.R.layout.simple_spinner_item);
        sugarQuantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSugarQuantity.setAdapter(sugarQuantityAdapter);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSize = spinnerSize.getSelectedItem().toString();
                selectedMilk = spinnerMilk.getSelectedItem().toString();
                selectedSugar = spinnerSugar.getSelectedItem().toString();
                String sugarQuantity = spinnerSugarQuantity.getSelectedItem().toString();

                double originalPrice = Double.parseDouble(txtProductPrice.getTag().toString());
                double updatedPrice = originalPrice;

                // Apply additional cost based on the selected size
                if (selectedSize.equals("Medium")) {
                    updatedPrice += 10.00;
                } else if (selectedSize.equals("Large")) {
                    updatedPrice += 20.00;
                }

                // Format the price to display two decimal places
                String formattedPrice = String.format("%.2f", updatedPrice);

                CartItem cartItem = new CartItem();
                cartItem.setProductId(finalProductId);
                cartItem.setProductName(txtProductName.getText().toString());
                cartItem.setProductPrice("Price: R" + formattedPrice);
                cartItem.setProductImageURL(productImageURL);
                cartItem.setSize(selectedSize);
                cartItem.setMilk(selectedMilk);
                cartItem.setSugar(selectedSugar);
                cartItem.setSugarQuantity(sugarQuantity);

                cartViewModel.addToCart(cartItem);

                Toast.makeText(getActivity(), "Item added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = parent.getItemAtPosition(position).toString();
                Double tagValue = (Double) txtProductPrice.getTag();
                double originalPrice = tagValue != null ? tagValue : 0.0;
                double updatedPrice = originalPrice;

                if (selectedSize.equals("Medium")) {
                    updatedPrice += 10.00;
                } else if (selectedSize.equals("Large")) {
                    updatedPrice += 20.00;
                }

                // Format the price to display two decimal places
                String formattedPrice = String.format("%.2f", updatedPrice);

                txtProductPrice.setText("Price: R" + formattedPrice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String productName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String productDescription = Objects.requireNonNull(snapshot.child("description").getValue()).toString();
                    productImageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();

                    txtProductName.setText(productName);
                    txtProductDescription.setText(productDescription);

                    // Check if "price" exists in the database snapshot
                    if (snapshot.hasChild("price")) {
                        String productPrice = snapshot.child("price").getValue().toString();
                        txtProductPrice.setText("Price: R" + productPrice);
                        txtProductPrice.setTag(Double.parseDouble(productPrice));
                    } else {
                        txtProductPrice.setText("Price: N/A");
                        txtProductPrice.setTag(0.0);
                    }

                    Picasso.get().load(productImageURL).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
