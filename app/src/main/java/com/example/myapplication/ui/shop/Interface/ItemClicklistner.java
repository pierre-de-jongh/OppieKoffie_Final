package com.example.myapplication.ui.shop.Interface;

import com.google.firebase.database.core.view.View;

public interface ItemClicklistner {
    void onClick(View view, int position, boolean isLongClick);

    void onClick(android.view.View view, int adapterPosition, boolean isLongClick);
}
