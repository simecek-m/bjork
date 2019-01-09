package com.example.app.bjork.constant;


public class Constant {

    // sharedPreferences
    public static final String SORT_ATTRIBUTE = "sortAttribute";
    public static final String SORT_DIRECTION = "sortType";
    public static final String FILTER_TYPE = "filterType";

    public static final String[] SORT_ATTRIBUTES = {"name", "price"};
    public static final String[] SORT_DIRECTIONS = {"ASC", "DESC"};
    public static final String[] PRODUCT_TYPES = {"all", "bed", "table", "couch", "light", "lamp", "decoration", "toy"};
    public static final String[] GENDERS = {"man", "woman"};

    // Algolia
    public static final String ALGOLIA_APPLICATION_ID = "JHP748HQRB";
    public static final String ALGOLIA_API_KEY = "ad297db462fb1fafc5a831a1b39a715e";
    public static final String ALGOLIA_PRODUCT_INDICES = "bjork_products";


}
