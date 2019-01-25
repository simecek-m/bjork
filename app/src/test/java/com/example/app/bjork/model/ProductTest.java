package com.example.app.bjork.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProductTest {

    private Product product;

    @Before
    public void setUp(){
        product = new Product();
    }

    @Test
    public void getAllColors() {
        List<String> colors = Arrays.asList("black", "white", "blue", "red", "green");
        product.setColors(colors);
        assertEquals(product.getAllColors(), "black / white / blue / red / green");
    }

    @Test
    public void getDiscountedPrice() {
        product.setPrice(1000);
        product.setDiscountPercentage(10);
        assertEquals(product.getDiscountedPrice(), 900);

        product.setPrice(0);
        product.setDiscountPercentage(50);
        assertEquals(product.getDiscountedPrice(), 0);

        product.setPrice(1000);
        product.setDiscountPercentage(100);
        assertEquals(product.getDiscountedPrice(), 0);

        product.setPrice(1000);
        product.setDiscountPercentage(200);
        assertEquals(product.getDiscountedPrice(), 0);
    }

    @Test
    public void likeProduct() {
        String firstUserId = "bPyhcCOYE44UTFDLyFOXM25IM5Rf";
        String secondUserId = "pKLXjEPkbi7x5OYx6J5yqSmgbLft";
        product.likeProduct(firstUserId);
        assertTrue(product.getLikes().contains(firstUserId));
        product.likeProduct(secondUserId);
        assertTrue(product.getLikes().contains(secondUserId));
        assertEquals(product.getLikes().size(), 2);
        product.likeProduct(firstUserId);
        assertFalse(product.getLikes().contains(firstUserId));
    }
}