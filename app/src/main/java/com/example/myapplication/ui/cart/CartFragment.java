package com.example.myapplication.ui.cart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CartItem;
import com.example.myapplication.CartViewModel;
import com.example.myapplication.Order;
import com.example.myapplication.R;
import com.example.myapplication.SharedViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtTotalPrice;
    private CartAdapter cartAdapter;
    private CartViewModel cartViewModel;
    private SharedViewModel sharedViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<CartItem> cartItems = cartViewModel.getCartItems();
        cartAdapter = new CartAdapter(getActivity(), cartItems);
        recyclerView.setAdapter(cartAdapter);

        Button checkoutButton = view.findViewById(R.id.btn_checkout);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);
        updateTotalPrice(cartItems);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write the cart items to Firebase database
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
                String orderId = ordersRef.push().getKey();
                for (CartItem item : cartItems) {
                    Order order = new Order(
                            item.getProductName(),
                            item.getProductPrice(),
                            item.getSize(),
                            item.getMilk(),
                            item.getSugar(),
                            item.getSugarQuantity()
                    );
                    ordersRef.child(orderId).push().setValue(order);
                }

                // Calculate the total price
                int totalPrice = calculateTotalPrice(cartItems);

                // Pass the total price as an argument to the CheckoutFragment
                Bundle bundle = new Bundle();
                bundle.putInt("totalPrice", totalPrice);

                // Clear the cart items and update the total price
                cartItems.clear();
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice(cartItems);

                // Set the total price in the shared view model
                sharedViewModel.setTotalPrice(totalPrice);

                // Navigate to the CheckoutFragment with the total price argument
                NavHostFragment.findNavController(CartFragment.this)
                        .navigate(R.id.action_CartFragment_to_CheckoutFragment, bundle);
            }
        });


        return view;
    }

    private void updateTotalPrice(List<CartItem> cartItems) {
        int totalPrice = calculateTotalPrice(cartItems);
        String formattedPrice = String.format("Total Amount Due: R%.2f", totalPrice / 100.0);
        txtTotalPrice.setText(formattedPrice);
    }


    private int calculateTotalPrice(List<CartItem> cartItems) {
        int totalPrice = 0;
        for (CartItem item : cartItems) {
            String priceString = item.getProductPrice().replaceAll("[^0-9]", "");
            int price = Integer.parseInt(priceString);
            totalPrice += price;
        }
        return totalPrice;
    }






    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private Context context;
        private List<CartItem> cartItems;

        public CartAdapter(Context context, List<CartItem> cartItems) {
            this.context = context;
            this.cartItems = cartItems;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            CartItem cartItem = cartItems.get(position);

            holder.txtProductName.setText(cartItem.getProductName());
            holder.txtProductPrice.setText(cartItem.getProductPrice());
            Picasso.get().load(cartItem.getProductImageURL()).into(holder.imgProductImage);

            holder.txtSelectedSize.setText("Size: " + cartItem.getSelectedSize());
            holder.txtSelectedSugar.setText("Sugar: " + cartItem.getSelectedSugar());
            holder.txtSugarAmount.setText("Sugar Amount: " + cartItem.getSugarAmount());
            holder.txtSelectedMilk.setText("Milk: " + cartItem.getSelectedMilk());

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = holder.getAdapterPosition();
                    cartItems.remove(clickedPosition);
                    notifyItemRemoved(clickedPosition);
                    updateTotalPrice(cartItems);
                }
            });
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public class CartViewHolder extends RecyclerView.ViewHolder {
            TextView txtProductName, txtProductPrice;
            ImageView imgProductImage;
            TextView txtSelectedSize, txtSelectedSugar, txtSugarAmount, txtSelectedMilk;
            Button btnRemove;

            public CartViewHolder(@NonNull View itemView) {
                super(itemView);
                txtProductName = itemView.findViewById(R.id.txt_product_name);
                txtProductPrice = itemView.findViewById(R.id.txt_product_price);
                imgProductImage = itemView.findViewById(R.id.img_product_image);
                txtSelectedSize = itemView.findViewById(R.id.txt_selected_size);
                txtSelectedSugar = itemView.findViewById(R.id.txt_selected_sugar);
                txtSugarAmount = itemView.findViewById(R.id.txt_sugar_amount);
                txtSelectedMilk = itemView.findViewById(R.id.txt_selected_milk);
                btnRemove = itemView.findViewById(R.id.btn_remove);
            }
        }
    }
}
