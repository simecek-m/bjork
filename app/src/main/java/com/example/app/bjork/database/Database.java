package com.example.app.bjork.database;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.app.bjork.constant.Constant;
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

public class Database {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String PRODUCTS_COLLECTION = "products";
    private static final String USER_INFO_COLLECTION = "user_info";
    private static final String CARTS_COLLECTION = "carts";
    private static final String CART_ITEMS_COLLECTION = "cart_items";
    private static final String FEEDBACKS_COLLECTION = "feedbacks";

    private static final String LIKES_FIELD = "likes";
    private static final String TYPE_FIELD = "type";
    private static final String NAME_FIELD = "name";
    private static final String MESSAGING_TOKEN_FIELD = "messagingToken";

    private static final String GET_SHOPPING_CART_FUNCTION = "getShoppingCart";
    private static final String DELETE_CART_FUNCTION = "deleteCart";

    public static void updateLikes(Product product){
        List<String> likes = product.getLikes();
        db.collection(PRODUCTS_COLLECTION)
                .document(product.getId())
                .update(LIKES_FIELD, likes);

    }

    public static Task<QuerySnapshot> loadProducts(){
        return db.collection(PRODUCTS_COLLECTION)
                .get();
    }

    public static Task<QuerySnapshot> loadProducts(String filter){
        return db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo(TYPE_FIELD, filter)
                .get();
    }

    public static Task<QuerySnapshot> loadFavouritesProducts(String userId){
        return db.collection(PRODUCTS_COLLECTION)
                .whereArrayContains(LIKES_FIELD, userId)
                .get();
    }

    public static Task<QuerySnapshot> loadFavouritesProducts(String userId, String filterType){
        return db.collection(PRODUCTS_COLLECTION)
                .whereArrayContains(LIKES_FIELD, userId)
                .whereEqualTo(TYPE_FIELD, filterType)
                .get();
    }

    public static Task<DocumentSnapshot> loadUserInfo(String userId){
        return db.collection(USER_INFO_COLLECTION)
                .document(userId)
                .get();
    }

    public static Task addUserInfo(UserInfo userInfo){
        return db.collection(USER_INFO_COLLECTION)
                .document(userInfo.getId())
                .set(userInfo);
    }

    public static void addToCart(String userId, Product product, String color, int quantity){

        DocumentReference docRef = db.collection(PRODUCTS_COLLECTION)
                .document(product.getId());

        CartItemReference item = new CartItemReference(color, quantity, docRef);

        db.collection(CARTS_COLLECTION)
                .document(userId)
                .collection(CART_ITEMS_COLLECTION)
                .add(item);
    }

    public static Task<HttpsCallableResult> getShoppingCart(){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable(GET_SHOPPING_CART_FUNCTION)
                .call();
    }

    public static Task<HttpsCallableResult> newOrder(){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable(DELETE_CART_FUNCTION)
                .call();
    }

    public static void sendFeedback(Feedback feedback){
        db.collection(FEEDBACKS_COLLECTION)
                .add(feedback);
    }

    public static Task<Void> removeItemFromCart(String userId, String cartItemId){
        return db.collection(CARTS_COLLECTION)
                .document(userId)
                .collection(CART_ITEMS_COLLECTION)
                .document(cartItemId)
                .delete();
    }

    public static void restoreCartItem(String userId, CartItem restoreItem){

        DocumentReference docRef = db.document(restoreItem.getDocRef());

        CartItemReference item = new CartItemReference(restoreItem.getColor(), restoreItem.getQuantity(), docRef);

        db.collection(CARTS_COLLECTION)
                .document(userId)
                .collection(CART_ITEMS_COLLECTION)
                .document(restoreItem.getId())
                .set(item);
    }

    public static void searchProducts(String query, CompletionHandler resultHandler){
        Client client = new Client(Constant.ALGOLIA_APPLICATION_ID, Constant.ALGOLIA_API_KEY);
        Index index = client.getIndex(Constant.ALGOLIA_PRODUCT_INDICES);
        index.searchAsync(new Query(query), null, resultHandler);
    }

    public static Task<DocumentSnapshot> getDocument(String productId){
        return db.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .get();
    }

    public static Task updateMessagingToken(String userId, String token){
        return db.collection(USER_INFO_COLLECTION)
                .document(userId)
                .update(MESSAGING_TOKEN_FIELD, token);
    }

    public static Task<QuerySnapshot> getNearestStore(){
        return db.collection("nearest_store")
                .get();
    }
}
