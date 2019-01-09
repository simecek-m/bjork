package com.example.app.bjork.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ShoppingCartAdapter;
import com.example.app.bjork.animation.Animation;
import com.example.app.bjork.api.BjorkAPI;
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
import java.util.Arrays;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = "ShoppingCartActivity";
    private static final int ORDER_DELAY = 2000;
    private static final int MINIMAL_ANIMATION_TIMER = 1000;

    private FirebaseAuth auth;
    private List<CartItem> list;

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private LinearLayoutManager layoutManager;

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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
        final ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));
                final Drawable icon = getDrawable(R.drawable.ic_delete);
                icon.setTint(Color.WHITE);

                int iconWidth = 100;
                int iconHeight = 100;
                int horizontalMargin = 50;
                int verticalMargin = (itemView.getHeight()-iconHeight) / 2;

                //swipe right
                if(dX > 0){
                    background.setBounds(0, itemView.getTop(),   itemView.getLeft() + (int)dX, itemView.getBottom());
                    background.draw(c);
                    if(dX > horizontalMargin*2+iconWidth){
                        icon.setBounds(horizontalMargin, itemView.getTop()+verticalMargin, horizontalMargin+iconWidth, itemView.getBottom()-verticalMargin);
                        icon.draw(c);
                    }
                }else{
                    background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    background.draw(c);
                    if(-dX > horizontalMargin*2+iconWidth){
                        icon.setBounds(itemView.getRight()-horizontalMargin-iconWidth, itemView.getTop()+verticalMargin, itemView.getRight()-horizontalMargin, itemView.getBottom()-verticalMargin);
                        icon.draw(c);
                    }
                }
            }

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
                BjorkAPI.removeItemFromCart(auth.getUid(), deletedCartItem.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete() && task.isSuccessful()){
                            updateOrderBottomSheetPrice();
                            String text = deletedCartItem.getName() + " " + getString(R.string.cart_item_removed);
                            Snackbar.make(recyclerView, text, Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getResources().getColor(R.color.blue))
                                    .setAction(R.string.undo, new View.OnClickListener() {

                                        private boolean firstClick = true;
                                        private View emptyCart = layout.findViewById(R.id.empty_cart);
                                        private View cartList = layout.findViewById(R.id.cart_list);

                                        @Override
                                        public void onClick(View view) {
                                            if(firstClick){
                                                firstClick = false;
                                                BjorkAPI.restoreCartItem(currentUser.getId(), deletedCartItem);
                                                adapter.addItemOnPosition(deletedCartItem, position);
                                                updateOrderBottomSheetPrice();
                                                if(adapter.getItemCount() == 1){
                                                    Animation.transitionViews(emptyCart, cartList);
                                                }
                                            }
                                        }
                                    })
                                    .show();
                        }
                    }
                });
                if(adapter.getItemCount() == 0){
                    View cartListView = findViewById(R.id.cart_list);
                    View emptyListView = findViewById(R.id.empty_cart);
                    Animation.transitionViews(cartListView, emptyListView);
                    menu = null;
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
            case R.id.menu_order:
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showError();
                            }
                        }, MINIMAL_ANIMATION_TIMER);
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

        final Spinner deliveryList = orderBottomSheet.findViewById(R.id.deliveryText);
        List<String> methods = Arrays.asList(getResources().getStringArray(R.array.delivery_methods));
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, methods);
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deliveryList.setAdapter(deliveryAdapter);

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
        View loadingAnimation = findViewById(R.id.loading_animation);
        View cartList = findViewById(R.id.cart_list);
        Animation.transitionViews(loadingAnimation, cartList);
    }

    public void showEmptyCart(){
        View loadingAnimation = findViewById(R.id.loading_animation);
        View emptyCartView = findViewById(R.id.empty_cart);
        Animation.transitionViews(loadingAnimation, emptyCartView);
    }

    public void showError(){
        LottieAnimationView animationView = findViewById(R.id.lottie_animation_view);
        TextView error = findViewById(R.id.error_text);
        TextView errorDescription = findViewById(R.id.error_text_description);
        animationView.setAnimation(R.raw.error);
        animationView.playAnimation();
        error.setText(R.string.connection_error);
        errorDescription.setVisibility(View.VISIBLE);
        errorDescription.setText(R.string.connection_error_description);

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
