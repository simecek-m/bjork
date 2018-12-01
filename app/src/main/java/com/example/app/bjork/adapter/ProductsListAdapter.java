package com.example.app.bjork.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.model.Product;

import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productsList;

    public ProductsListAdapter(Context context, List<Product> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_element, parent,false);
        ProductViewHolder viewHolder = new ProductViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productsList.get(position);
        holder.name.setText(product.getName());
        Drawable icon = getTypeIcon(product.getType());
        holder.icon.setBackground(icon);
        holder.price.setText(product.getPrice() + ",- Kč");
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView icon;
        private ImageView image;
        private ImageView like;
        private TextView price;

        public ProductViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.typeIcon);
            image = itemView.findViewById(R.id.image);
            like = itemView.findViewById(R.id.like);
            price = itemView.findViewById(R.id.price);
        }
    }

    public Drawable getTypeIcon(String type){
        switch (type){
            default:
                return context.getDrawable(R.drawable.ic_question_mark);
            case "bed":
                return context.getDrawable(R.drawable.ic_bed);
            case "closet":
                return context.getDrawable(R.drawable.ic_closet);
            case "couch":
                return context.getDrawable(R.drawable.ic_couch);
            case "lamp":
                return context.getDrawable(R.drawable.ic_lamp);
            case "spotlight":
                return context.getDrawable(R.drawable.ic_spotlight);
            case "table":
                return context.getDrawable(R.drawable.ic_table);
            case "toy":
                return context.getDrawable(R.drawable.ic_toy);
            case "decoration":
                return context.getDrawable(R.drawable.ic_decoration);
        }
    }

    public void setList(List<Product> productsList){
        if(!productsList.equals(this.productsList)){
            this.productsList = productsList;
        }
    }
}
