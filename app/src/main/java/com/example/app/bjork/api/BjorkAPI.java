package com.example.app.bjork.api;

import com.example.app.bjork.model.CartItem;
import com.example.app.bjork.model.CartItemReference;
import com.example.app.bjork.model.Feedback;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.List;

public class BjorkAPI {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void likeProduct(Product product){
        List<String> likes = product.getLikes();
        db.collection("products")
                .document(product.getId())
                .update("likes", likes);

    }

    public static Task<QuerySnapshot> loadProducts(){
        return db.collection("products")
                .get();
    }

    public static Task<QuerySnapshot> loadProducts(String filter){
        return db.collection("products")
                .whereEqualTo("type", filter)
                .get();
    }

    public static Task<QuerySnapshot> loadFavouritesProducts(String userId){
        return db.collection("products")
                .whereArrayContains("likes", userId)
                .get();
    }

    public static Task<QuerySnapshot> loadFavouritesProducts(String userId, String filterType){
        return db.collection("products")
                .whereArrayContains("likes", userId)
                .whereEqualTo("type", filterType)
                .get();
    }

    public static Task<DocumentSnapshot> loadUserInfo(String userId){
        return db.collection("user_info")
                .document(userId)
                .get();
    }

    public static void addUserInfo(UserInfo userInfo){
        db.collection("user_info")
                .document(userInfo.getId())
                .set(userInfo);
    }

    public static void addToCart(String userId, Product product, String color, int quantity){

        DocumentReference docRef = db.collection("products")
                .document(product.getId());

        CartItemReference item = new CartItemReference(color, quantity, docRef);

        db.collection("carts")
                .document(userId)
                .collection("cart_items")
                .add(item);
    }

    public static Task<HttpsCallableResult> getShoppingCart(){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable("getShoppingCart")
                .call();
    }

    public static Task<HttpsCallableResult> newOrder(){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable("deleteCart")
                .call();
    }

    public static void addFeedback(Feedback feedback){
        db.collection("feedbacks")
                .add(feedback);
    }

    public static Task<Void> removeItemFromCart(String userId, String cartItemId){
        return db.collection("carts")
                .document(userId)
                .collection("cart_items")
                .document(cartItemId)
                .delete();
    }

    public static void restoreCartItem(String userId, CartItem restoreItem){

        DocumentReference docRef = db.document(restoreItem.getDocRef());

        CartItemReference item = new CartItemReference(restoreItem.getColor(), restoreItem.getQuantity(), docRef);

        db.collection("carts")
                .document(userId)
                .collection("cart_items")
                .document(restoreItem.getId())
                .set(item);
    }
}
