package com.example.app.bjork.model;


public class CartItem {

    private String id;
    private String name;
    private String imageUrl;
    private int pricePerUnit;
    private String color;
    private int quantity;
    private String docRef;

    public CartItem(String id, Product product, String color, int quantity, String docRef) {
        this.id = id;
        this.name = product.getName();
        this.imageUrl = product.getImageUrl();
        this.color = color;
        this.quantity = quantity;
        float defaultPrice = product.getPrice();
        float discount = (defaultPrice/100)*product.getDiscountPercentage();
        this.pricePerUnit = Math.round(defaultPrice) - Math.round(discount);
        this.docRef = docRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(int pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", color='" + color + '\'' +
                ", quantity=" + quantity +
                ", docRef='" + docRef + '\'' +
                '}';
    }

    public int getPrice(){
        return quantity*pricePerUnit;
    }
}
