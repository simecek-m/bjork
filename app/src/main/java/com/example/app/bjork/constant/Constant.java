package com.example.app.bjork.constant;


import com.google.firebase.firestore.Query;

public class Constant {

    // sharedPreferences
    public static final String SORT_ATTRIBUTE = "sortAttribute";
    public static final String SORT_DIRECTION = "sortType";
    public static final String FILTER_TYPE = "filterType";


    public static final String[] SORT_ATTRIBUTES = {"name", "price"};
    public static final String[] SORT_DIRECTIONS = {"ASC", "DESC"};
    public static final String[] PRODUCT_TYPES = {"all", "bed", "table", "couch", "light", "lamp", "decoration", "toy"};
    public static final String[] GENDERS = {"man", "woman"};
}
