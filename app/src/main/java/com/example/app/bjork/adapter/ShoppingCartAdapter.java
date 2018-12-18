package com.example.app.bjork.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.model.CartItem;

import java.util.List;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public ShoppingCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartAdapter.ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.image);

        holder.name.setText(item.getName());
        holder.description.setText(item.getQuantity() + "x" + ", " + item.getColor());
        holder.prize.setText(item.getPrice() + ",- Kč");
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public void setList(List<CartItem> list){
        this.cartItemList = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView description;
        private TextView prize;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            prize = itemView.findViewById(R.id.prize);
        }
    }
}
