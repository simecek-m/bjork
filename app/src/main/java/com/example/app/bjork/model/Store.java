package com.example.app.bjork.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

    public String getOpeningTimeByDay(String day){
        return openingTime.get(day);
    }

    public String getOpeningTimeToday(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        Date now = new Date();
        String today = sdf.format(now);
        return getOpeningTimeByDay(today);
    }
}
