package com.example.app.bjork.model;


public class CartItem {

    private String name;
    private String imageUrl;
    private int pricePerUnit;
    private String color;
    private int quantity;

    public CartItem(Product product, String color, int quantity) {
        this.name = product.getName();
        this.imageUrl = product.getImageUrl();
        this.color = color;
        this.quantity = quantity;
        float defaultPrice = product.getPrice();
        float discount = (defaultPrice/100)*product.getDiscountPercentage();
        this.pricePerUnit = Math.round(defaultPrice) - Math.round(discount);
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

    @Override
    public String toString() {
        return "CartItem{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", color='" + color + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    public int getPrice(){
        return quantity*pricePerUnit;
    }
}
