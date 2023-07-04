package com.example.myapplication;

public class CartItem {
    private String productId;
    private String productName;
    private String productPrice;
    private String productImageURL;
    private String size;
    private String milk;
    private String sugar;
    private String sugarQuantity;

    public CartItem() {
        // Empty constructor required for Firebase
    }

    public CartItem(String productId, String productName, String productPrice, String productImageURL) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageURL = productImageURL;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
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

    public String getSelectedSize() {

        return size;
    }

    public String getSelectedSugar() {

        return sugar;
    }

    public String getSugarAmount() {

        return sugarQuantity;
    }

    public String getSelectedMilk() {

        return milk;
    }
}
