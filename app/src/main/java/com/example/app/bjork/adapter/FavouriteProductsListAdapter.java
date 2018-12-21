package com.example.app.bjork.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.app.bjork.R;
import com.example.app.bjork.activity.ProductDetailActivity;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FavouriteProductsListAdapter extends RecyclerView.Adapter<FavouriteProductsListAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> favouriteProductsList;
    private FirebaseAuth mAuth;

    private OnLikeClickListener onLikeClickListener;

    public FavouriteProductsListAdapter(Context context, List<Product> favouriteProductsList) {
        this.context = context;
        this.favouriteProductsList = favouriteProductsList;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_element, parent,false);
        ProductViewHolder viewHolder = new ProductViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        final Product product = favouriteProductsList.get(position);
        holder.name.setText(product.getName());
        int iconId = Product.getTypeIconId(product.getType());
        holder.icon.setImageResource(iconId);
        holder.defaultPrice.setText(product.getPrice() + ",- Kč");
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.loading).error(R.drawable.error).fallback(R.drawable.error);
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(product.getImageUrl())
                .into(holder.image);
        int discount = product.getDiscountPercentage();
        if(product.likedByUser(mAuth.getUid())){
            Drawable heartIcon = context.getDrawable(R.drawable.ic_favorite_heart);
            heartIcon.setTint(context.getResources().getColor(R.color.colorPrimary));
            holder.like.setImageDrawable(heartIcon);
        }else{
            holder.like.setImageDrawable(context.getDrawable(R.drawable.ic_heart));
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getUid() != null){
                    product.getLikes().remove(mAuth.getUid());
                    BjorkAPI.likeProduct(product);
                }
                if(onLikeClickListener != null){
                    onLikeClickListener.onLikeClick(product);
                }
            }
        });
        if(discount != 0){
            styleDiscountItem(holder, product);
        }else {
            styleNormalItem(holder, product);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
    }

    public void styleDiscountItem(ProductViewHolder holder, Product product){
        float defaultPrice = product.getPrice();
        float discount = (defaultPrice/100)*product.getDiscountPercentage();
        int newPrice = Math.round(defaultPrice) - Math.round(discount);
        holder.defaultPrice.setPaintFlags(holder.defaultPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.defaultPrice.setTextColor(context.getResources().getColor(R.color.greyDarker));
        holder.defaultPrice.setTextAppearance(context, R.style.RobotoItalicFont);
        holder.discountIcon.setVisibility(View.VISIBLE);
        holder.discountText.setVisibility(View.VISIBLE);
        holder.discountPrice.setVisibility(View.VISIBLE);
        holder.discountPrice.setText(newPrice + ",- Kč");
        holder.discountText.setText(product.getDiscountPercentage()+"%");
    }

    public void styleNormalItem(ProductViewHolder holder, Product product){
        holder.defaultPrice.setPaintFlags(0);
        holder.defaultPrice.setTextColor(context.getResources().getColor(R.color.black));
        holder.defaultPrice.setTextAppearance(context, R.style.RobotoBoldFont);
        holder.discountIcon.setVisibility(View.GONE);
        holder.discountText.setVisibility(View.GONE);
        holder.discountPrice.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return favouriteProductsList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView icon;
        private ImageView image;
        private ImageView like;
        private TextView defaultPrice;
        private TextView discountPrice;
        private ImageView discountIcon;
        private TextView discountText;


        public ProductViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.typeIcon);
            image = itemView.findViewById(R.id.image);
            like = itemView.findViewById(R.id.like);
            defaultPrice = (TextView) itemView.findViewById(R.id.defaultPrice);
            discountPrice = itemView.findViewById(R.id.discountPrice);
            discountIcon = itemView.findViewById(R.id.discountIcon);
            discountText = itemView.findViewById(R.id.discountText);
        }
    }

    public void setList(List<Product> productsList){
        if(!productsList.equals(this.favouriteProductsList)){
            this.favouriteProductsList = productsList;
        }
    }

    public void addOnLikeClickListener(OnLikeClickListener onLikeClickListener){
        this.onLikeClickListener = onLikeClickListener;
    }

    public interface OnLikeClickListener{
        void onLikeClick(Product product);
    }
}
