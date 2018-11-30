package com.example.app.bjork.model;

import java.util.List;

public class Product {

    private String name;
    private List<String> colors;
    private int price;
    private String type;
    private String imageUrl;
    private String description;

    public Product() {
    }

    public Product(String name, List<String> colors, int price, String type, String imageUrl, String description) {
        this.name = name;
        this.colors = colors;
        this.price = price;
        this.type = type;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
