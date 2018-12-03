package com.example.app.bjork.model;

import java.util.HashMap;

public class Store {

    private String name;
    private float latitude;
    private float longitude;
    private String imageUrl;
    private HashMap<String, String> openingTime;

    public Store() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HashMap<String, String> getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(HashMap<String, String> openingTime) {
        this.openingTime = openingTime;
    }
}
