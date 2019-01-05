package com.example.app.bjork.adapter;

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

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Product> productsList = new ArrayList<>();

    public SearchAdapter() {
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent,false);
        SearchViewHolder viewHolder = new SearchViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {
        Product product = productsList.get(i);

        searchViewHolder.name.setText(product.getName());
        searchViewHolder.price.setText(product.getPrice() + " ,- Kč");
        Glide.with(searchViewHolder.itemView)
                .load(product.getImageUrl())
                .into(searchViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public void setList(List<Product> products){
        productsList = products;
        notifyDataSetChanged();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView image;
        private TextView price;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            price = itemView.findViewById(R.id.price);
        }
    }
}
