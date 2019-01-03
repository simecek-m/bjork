package com.example.app.bjork.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.app.bjork.animation.Animation;
import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ShoppingCartAdapter;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.listener.UndoDeleteCartItemListener;
import com.example.app.bjork.model.CartItem;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = "ShoppingCartActivity";
    private static final int ORDER_DELAY = 2000;
    private static final int MINIMAL_ANIMATION_TIMER = 1000;

    private FirebaseAuth auth;
    private List<CartItem> list;

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Menu menu;
    private BottomSheetDialog orderBottomSheet;
    private UserInfo currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.cart);
        ab.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.hasFixedSize();
        adapter = new ShoppingCartAdapter(this, list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        currentUser = (UserInfo) getIntent().getSerializableExtra("currentUser");

        if(auth.getUid() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            createOrderBottomSheet();
            loadData();
        }

        // swipe gesture - remove product from shopping cart
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final CartItem deletedCartItem = adapter.getCartItem(position);
                final View layout = findViewById(R.id.layout);
                adapter.removeItem(position);
                BjorkAPI.removeItemFromCart(currentUser.getId(), deletedCartItem.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete() && task.isSuccessful()){
                            String text = deletedCartItem.getName() + " " + getString(R.string.cart_item_removed);
                            Snackbar.make(recyclerView, text, Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getColor(R.color.blue))
                                    .setAction(R.string.undo, new UndoDeleteCartItemListener(currentUser.getId(), adapter, deletedCartItem, position, layout))
                                    .show();
                        }
                    }
                });
                if(adapter.getItemCount() == 0){
                    View cartListView = findViewById(R.id.cart_list);
                    View emptyListView = findViewById(R.id.empty_cart);
                    Animation.transitionViews(cartListView, emptyListView);
                }

            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    public int getPrice(){
        int result = 0;
        for(CartItem item: list){
            result += item.getPrice();
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_money:
                orderBottomSheet.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData(){
        BjorkAPI.getShoppingCart().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Object> result = (List<Object>) httpsCallableResult.getData();
                        for(Object obj: result) {
                            Gson gson = new Gson();
                            JsonElement element = gson.toJsonTree(obj);
                            CartItem item = gson.fromJson(element, CartItem.class);
                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(list.size() > 0){
                                    showShoppingCart();
                                }else{
                                    showEmptyCart();
                                }
                            }
                        }, MINIMAL_ANIMATION_TIMER);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "load getShoppingCart failed: ", e);
                    }
                });
    }

    public void createOrderBottomSheet() {
        orderBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        orderBottomSheet.setContentView(R.layout.order_bottom_sheet);
        final Button confirmButton = orderBottomSheet.findViewById(R.id.confirmButton);
        final LinearLayout progressLayout = orderBottomSheet.findViewById(R.id.progressLayout);
        final ProgressBar progressBar = orderBottomSheet.findViewById(R.id.progressBar);
        final TextView progressText = orderBottomSheet.findViewById(R.id.progressText);
        final ConstraintLayout orderInfo = orderBottomSheet.findViewById(R.id.orderInfo);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmButton.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                orderInfo.setVisibility(View.GONE);
                BjorkAPI.newOrder().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        progressBar.setVisibility(View.GONE);
                        progressText.setText(getString(R.string.order_complete));
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                orderBottomSheet.dismiss();
                                finish();
                            }
                        }, ORDER_DELAY);
                    }
                });
            }
        });
        TextView  user = orderBottomSheet.findViewById(R.id.userText);
        TextView  address = orderBottomSheet.findViewById(R.id.addressText);
        TextView uncompleteProfile = orderBottomSheet.findViewById(R.id.uncomplete_profile);
        if(currentUser != null){
            user.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
            address.setText(currentUser.getAddress());
            if(currentUser.getFirstname().isEmpty() || currentUser.getLastname().isEmpty()){
                user.setText(R.string.unknown);
                confirmButton.setEnabled(false);
                uncompleteProfile.setVisibility(View.VISIBLE);
            }if(currentUser.getAddress().isEmpty()){
                address.setText(R.string.unknown);
                confirmButton.setEnabled(false);
                uncompleteProfile.setVisibility(View.VISIBLE);
            }
        }else{
            user.setText(R.string.unknown);
            address.setText(R.string.unknown);
            confirmButton.setEnabled(false);
            uncompleteProfile.setVisibility(View.VISIBLE);
        }
    }

    public void updateOrderBottomSheetPrice(){
        TextView  money = orderBottomSheet.findViewById(R.id.moneyText);
        money.setText(getPrice() + ",- Kč");
    }

    public void showShoppingCart(){
        getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
        updateOrderBottomSheetPrice();
        View loadingAnimation = findViewById(R.id.loadingAnimation);
        View cartList = findViewById(R.id.cart_list);
        Animation.transitionViews(loadingAnimation, cartList);
    }

    public void showEmptyCart(){
        View loadingAnimation = findViewById(R.id.loadingAnimation);
        View emptyCartView = findViewById(R.id.empty_cart);
        Animation.transitionViews(loadingAnimation, emptyCartView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BjorkAPI.loadUserInfo(auth.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(UserInfo.class);
                createOrderBottomSheet();
            }
        });
    }
}
