package com.example.app.bjork.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    private FirebaseDatabase db;

    public Database() {
        db = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getAllProducts(){
        return db.getReference("products");
    }
}
