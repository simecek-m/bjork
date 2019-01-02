package com.example.app.bjork.listener;

import android.view.View;

import com.example.app.bjork.Animation;
import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ShoppingCartAdapter;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.CartItem;

public class UndoDeleteCartItemListener implements View.OnClickListener {

    private boolean firstClick = true;
    private String userId;
    private ShoppingCartAdapter adapter;
    private CartItem item;
    private int position;
    private View emptyCart;
    private View cartList;

    public UndoDeleteCartItemListener(String userId, ShoppingCartAdapter adapter, CartItem item, int position, View layout) {
        this.userId = userId;
        this.adapter = adapter;
        this.item = item;
        this.position = position;
        emptyCart = layout.findViewById(R.id.empty_cart);
        cartList = layout.findViewById(R.id.cart_list);
    }

    @Override
    public void onClick(View view) {
        if(firstClick){
            firstClick = false;
            BjorkAPI.restoreCartItem(userId, item);
            adapter.addItemOnPosition(item, position);
            if(adapter.getItemCount() == 1){
                Animation.transitionViews(emptyCart, cartList);
            }
        }
    }
}
