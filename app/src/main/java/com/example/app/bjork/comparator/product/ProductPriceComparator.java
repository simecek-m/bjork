package com.example.app.bjork.comparator.product;

import com.example.app.bjork.model.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o1.getDiscountedPrice() < o2.getDiscountedPrice()? -1:1;
    }
}
