package com.example.app.bjork.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.CartItem;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.viewmodel.ShoppingCartViewModel;
import com.example.app.bjork.wrapper.DataWrapper;

import java.util.Arrays;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = "ShoppingCartActivity";
    private static final int DISMISS_NEW_ORDER_DELAY = 2000;

    private ShoppingCartViewModel shoppingCartViewModel;
    private UserInfo currentUser;

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private LinearLayoutManager layoutManager;

    private Menu menu;
    private BottomSheetDialog orderBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        shoppingCartViewModel = ViewModelProviders.of(this).get(ShoppingCartViewModel.class);

        currentUser = ((UserInfo) getIntent().getSerializableExtra("currentUser"));
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        showToolbar();

        recyclerView = findViewById(R.id.cart_list);
        adapter = new ShoppingCartAdapter(this);
        layoutManager = new LinearLayoutManager(this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        createOrderBottomSheet();
        new ItemTouchHelper(new ShoppingCartTouchHelper()).attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        shoppingCartViewModel.getCartItemsList().observe(this, new Observer<DataWrapper<List<CartItem>>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<List<CartItem>> list) {
                if(list.getError() != null){
                    showError();
                }else{
                    if(list.getData().size() == 0){
                        showEmptyCart();
                    }else{
                        adapter.setList(list.getData());
                        adapter.notifyDataSetChanged();
                        showShoppingCart();
                    }

                }
            }
        });

        shoppingCartViewModel.getDeletedCartItem().observe(this, new Observer<DataWrapper<Pair<Integer, CartItem>>>() {
            @Override
            public void onChanged(@Nullable final DataWrapper<Pair<Integer, CartItem>> deletedItem) {
                System.out.println("deletedItem changed: " + deletedItem);
                final int position = deletedItem.getData().first;
                final CartItem deletedCartItem = deletedItem.getData().second;
                if(deletedItem.getError() == null){
                    String text = deletedCartItem.getName() + " " + getString(R.string.cart_item_removed);
                    Snackbar.make(recyclerView, text, Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(getResources().getColor(R.color.blue))
                            .setAction(R.string.undo, new View.OnClickListener() {
                                private boolean firstClick = true;
                                private View emptyCart = findViewById(R.id.empty_cart);
                                private View cartList = findViewById(R.id.cart_list);
                                @Override
                                public void onClick(View view) {
                                    if(firstClick){
                                        firstClick = false;
                                        Database.restoreCartItem(currentUser.getId(), deletedCartItem);
                                        adapter.addItemOnPosition(deletedCartItem, position);
                                        if(adapter.getItemCount() == 1){
                                            Animation.transitionViews(emptyCart, cartList);
                                            getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
                                        }
                                    }
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        shoppingCartViewModel.getCartItemsList().removeObservers(this);
        shoppingCartViewModel.getDeletedCartItem().removeObservers(this);
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
                showOrderBottomSheet();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOrderBottomSheet() {
        shoppingCartViewModel.getTotalPrice().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer totalPrice) {
                TextView  priceTextView = orderBottomSheet.findViewById(R.id.moneyText);
                priceTextView.setText(totalPrice + ",- Kč");
            }
        });
        orderBottomSheet.show();
    }

    public void createOrderBottomSheet() {
        orderBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        orderBottomSheet.setContentView(R.layout.order_bottom_sheet);
        fillBottomSheetData();
        orderBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                shoppingCartViewModel.getNewOrder().removeObservers(ShoppingCartActivity.this);
                shoppingCartViewModel.getTotalPrice().removeObservers(ShoppingCartActivity.this);
            }
        });


        shoppingCartViewModel.getNewOrder().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean orderFinished) {
                final ProgressBar progressBar = orderBottomSheet.findViewById(R.id.progressBar);
                final TextView progressText = orderBottomSheet.findViewById(R.id.progressText);
                if (orderFinished) {
                    progressBar.setVisibility(View.GONE);
                    progressText.setText(getString(R.string.order_complete));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            orderBottomSheet.dismiss();
                            finish();
                        }
                    }, DISMISS_NEW_ORDER_DELAY);
                }
            }
        });
    }

    public void showShoppingCart(){
        if(menu.findItem(R.id.menu_order) == null){
            getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
        }
        View cartList = findViewById(R.id.cart_list);
        if(cartList.getVisibility() != View.VISIBLE){
            View loadingAnimation = findViewById(R.id.loading_animation);
            Animation.transitionViews(loadingAnimation, cartList);
        }

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

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.cart);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    public void fillBottomSheetData(){
        final Button confirmButton = orderBottomSheet.findViewById(R.id.confirmButton);
        final LinearLayout progressLayout = orderBottomSheet.findViewById(R.id.progressLayout);
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
                orderInfo.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                shoppingCartViewModel.newOrder();
            }
        });
        TextView  user = orderBottomSheet.findViewById(R.id.userText);
        TextView  address = orderBottomSheet.findViewById(R.id.addressText);
        TextView uncompleteProfile = orderBottomSheet.findViewById(R.id.uncomplete_profile);

        if(currentUser != null){
            user.setText(currentUser.getFirstname() + " " + currentUser.getLastname());
            address.setText(currentUser.getAddress());
            if(currentUser.getFirstname() == null || currentUser.getLastname() == null || currentUser.getFirstname().isEmpty() || currentUser.getLastname().isEmpty()){
                user.setText(R.string.unknown);
                uncompleteProfile.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(false);
            }if(currentUser.getAddress() == null || currentUser.getAddress().isEmpty()){
                address.setText(R.string.unknown);
                confirmButton.setEnabled(false);
                uncompleteProfile.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(false);
            }
        }else{
            user.setText(R.string.unknown);
            address.setText(R.string.unknown);
            confirmButton.setEnabled(false);
            uncompleteProfile.setVisibility(View.VISIBLE);
        }
    }


    // helper for touch gestures callbacks
    private class ShoppingCartTouchHelper extends ItemTouchHelper.SimpleCallback{

        public ShoppingCartTouchHelper() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;

            final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.grey));
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
            shoppingCartViewModel.removeItemFromCart(new Pair<Integer, CartItem>(position, deletedCartItem));
            if(adapter.getItemCount() == 0){
                View cartListView = findViewById(R.id.cart_list);
                View emptyListView = findViewById(R.id.empty_cart);
                Animation.transitionViews(cartListView, emptyListView);
                menu.clear();
            }

        }
    }
}
