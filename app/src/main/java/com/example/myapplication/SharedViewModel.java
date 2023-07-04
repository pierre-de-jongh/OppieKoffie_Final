package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Integer> totalPrice;

    public SharedViewModel() {
        totalPrice = new MutableLiveData<>();
        totalPrice.setValue(0);
    }

    public void setTotalPrice(int price) {
        totalPrice.setValue(price);
    }

    public LiveData<Integer> getTotalPrice() {
        return totalPrice;
    }
}
