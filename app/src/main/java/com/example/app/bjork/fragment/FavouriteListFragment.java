package com.example.app.bjork.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app.bjork.R;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavouriteListFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private View emptyListLayout;
    private View favouritesListLayout;

    private List<Product> favouriteProducts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_list, container, false);
        emptyListLayout = view.findViewById(R.id.empty_list);
        System.out.println("empty list: " + emptyListLayout);
        favouritesListLayout = view.findViewById(R.id.favourite_list);
        System.out.println("fav list: " + favouritesListLayout);

        favouriteProducts = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        System.out.println("current user: " + auth.getCurrentUser());
        if(auth.getUid() != null) {
            System.out.println("auth not null: " + auth.getUid());
            BjorkAPI.loadFavouritesProducts(auth.getUid())
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            System.out.println("products: " + queryDocumentSnapshots.size());
                            Product product;
                            for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                product = snapshot.toObject(Product.class);
                                product.setId(snapshot.getId());
                                favouriteProducts.add(product);
                            }
                        }
                    });
        }
        return view;
    }

    public void addFavouriteProduct(Product product){
        System.out.println("add product: " + favouriteProducts.size());
        if(favouriteProducts.size() == 0){
            emptyListLayout.setVisibility(View.GONE);
            favouritesListLayout.setVisibility(View.VISIBLE);
        }
        favouriteProducts.add(product);
        TextView view = favouritesListLayout.findViewById(R.id.favText);
        view.setText("favs: " + favouriteProducts.size());
    }

    public void removeFavouriteProduct(Product product){
        favouriteProducts.remove(product);
        System.out.println("remove product: " + favouriteProducts.size());
        if(favouriteProducts.size() == 0){
            emptyListLayout.setVisibility(View.VISIBLE);
            favouritesListLayout.setVisibility(View.GONE);
        }

        TextView view = favouritesListLayout.findViewById(R.id.favText);
        view.setText("favs: " + favouriteProducts.size());
    }
}
