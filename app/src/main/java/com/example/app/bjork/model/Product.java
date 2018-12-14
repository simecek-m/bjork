package com.example.app.bjork.model;

import com.example.app.bjork.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product implements Serializable {

    private String id;
    private String name;
    private int discountPercentage;
    private List<String> colors;
    private String size;
    private int price;
    private String type;
    private String imageUrl;
    private String description;
    private List<String> likes = new ArrayList<>();

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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
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

    public String getAllColors(){
        String delimiter = " / ";
        StringBuilder allColors = new StringBuilder();
        for (String color: colors){
            allColors.append(color);
            allColors.append(delimiter);
        }
        allColors.delete(allColors.lastIndexOf(delimiter), allColors.length());
        return allColors.toString();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", colors=" + colors +
                ", size='" + size + '\'' +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", likes=" + likes +
                '}';
    }

    public boolean likedByUser(String userId){
        return likes.contains(userId);
    }

    public void likeProduct(String userId){
        if(likes.contains(userId)){
            likes.remove(userId);
        }else{
            likes.add(userId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
