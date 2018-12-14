package com.example.app.bjork.model;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String userId;
    private String color;
    private int count;
    private DocumentReference ref;

    public CartItem() {
    }

    public CartItem(String userId, String color, int number, DocumentReference productRef) {
        this.userId = userId;
        this.color = color;
        this.count = number;
        this.ref = productRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int number) {
        this.count = number;
    }

    public DocumentReference getRef() {
        return ref;
    }

    public void setRef(DocumentReference ref) {
        this.ref = ref;
    }
}
