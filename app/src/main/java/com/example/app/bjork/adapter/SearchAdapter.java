package com.example.app.bjork.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.activity.ProductDetailActivity;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<Product> productsList = new ArrayList<>();

    public SearchAdapter(Context context) {
        this.context = context;
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
        searchViewHolder.price.setText(product.getDiscountedPrice() + " ,- Kč");
        Glide.with(searchViewHolder.itemView)
                .load(product.getImageUrl())
                .into(searchViewHolder.image);
        searchViewHolder.icon.setImageResource(product.getTypeIconId());
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
        private ImageView icon;

        public SearchViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            price = itemView.findViewById(R.id.price);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product algoliaProduct = productsList.get(getAdapterPosition());
                    BjorkAPI.getDocument(algoliaProduct.getId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent intent = new Intent(context, ProductDetailActivity.class);
                            Product product = documentSnapshot.toObject(Product.class);
                            product.setId(documentSnapshot.getId());
                            intent.putExtra("product", product);
                            context.startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
