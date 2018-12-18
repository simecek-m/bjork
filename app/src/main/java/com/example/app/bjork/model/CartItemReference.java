package com.example.app.bjork.model;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class CartItemReference implements Serializable {

    private String color;
    private int quantity;
    private DocumentReference ref;

    public CartItemReference() {
    }

    public CartItemReference(String color, int quantity, DocumentReference productRef) {
        this.color = color;
        this.quantity = quantity;
        this.ref = productRef;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public DocumentReference getRef() {
        return ref;
    }

    public void setRef(DocumentReference ref) {
        this.ref = ref;
    }
}
