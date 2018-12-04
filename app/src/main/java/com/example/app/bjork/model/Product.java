package com.example.app.bjork.model;

import android.graphics.drawable.Drawable;

import com.example.app.bjork.R;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private String id;
    private String name;
    private int discountPercentage;
    private List<String> colors;
    private int price;
    private String type;
    private String imageUrl;
    private String description;

    public Product() {
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

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
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

    public static int getTypeIconId(String type){
        switch (type){
            default:
                return R.drawable.ic_question_mark;
            case "bed":
                return R.drawable.ic_bed;
            case "closet":
                return R.drawable.ic_closet;
            case "couch":
                return R.drawable.ic_couch;
            case "lamp":
                return R.drawable.ic_lamp;
            case "spotlight":
                return R.drawable.ic_spotlight;
            case "table":
                return R.drawable.ic_table;
            case "toy":
                return R.drawable.ic_toy;
            case "decoration":
                return R.drawable.ic_decoration;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", colors=" + colors +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
