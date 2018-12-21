package com.example.app.bjork.comparator;

import com.example.app.bjork.comparator.product.ProductNameComparator;
import com.example.app.bjork.comparator.product.ProductPriceComparator;
import com.example.app.bjork.comparator.product.ReversedProductNameComparator;
import com.example.app.bjork.comparator.product.ReversedProductPriceComparator;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.Product;

import java.util.Comparator;

public class ProductComparator {

    private String sortAttribute;
    private String sortDirection;

    public ProductComparator(String attribute, String direction) {
        this.sortAttribute = attribute;
        this.sortDirection = direction;
    }

    public Comparator<Product> getComparator(){
        if(sortAttribute == Constant.SORT_ATTRIBUTES[0]){
            if(sortDirection == Constant.SORT_DIRECTIONS[0]){
                return new ProductNameComparator();
            }else{
                return new ReversedProductNameComparator();
            }
        }else{
            if(sortDirection == Constant.SORT_DIRECTIONS[0]){
                return new ProductPriceComparator();
            }else{
                return new ReversedProductPriceComparator();
            }
        }
    }
}
