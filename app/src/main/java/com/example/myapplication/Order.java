package com.example.myapplication;

public class Order {
    private String productName;
    private String productPrice;
    private String size;
    private String milk;
    private String sugar;
    private String sugarQuantity;

    public Order() {
        // Empty constructor required for Firebase
    }

    public Order(String productName, String productPrice, String size, String milk, String sugar, String sugarQuantity) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.size = size;
        this.milk = milk;
        this.sugar = sugar;
        this.sugarQuantity = sugarQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMilk() {
        return milk;
    }

    public void setMilk(String milk) {
        this.milk = milk;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getSugarQuantity() {
        return sugarQuantity;
    }

    public void setSugarQuantity(String sugarQuantity) {
        this.sugarQuantity = sugarQuantity;
    }
}
