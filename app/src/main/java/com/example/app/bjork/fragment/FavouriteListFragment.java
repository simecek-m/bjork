package com.example.app.bjork.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.FavouriteProductsListAdapter;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.comparator.ProductComparator;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.app.bjork.constant.Constant.FILTER_TYPE;
import static com.example.app.bjork.constant.Constant.SORT_ATTRIBUTE;
import static com.example.app.bjork.constant.Constant.SORT_DIRECTION;

public class FavouriteListFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private View emptyListLayout;
    private View favouritesListLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FavouriteProductsListAdapter adapter;

    private FavouriteProductsListAdapter.OnLikeClickListener onLikeClickListener;


    private List<Product> favouriteProducts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_list, container, false);
        emptyListLayout = view.findViewById(R.id.empty_list);
        favouritesListLayout = view.findViewById(R.id.favourite_list);

        favouriteProducts = new ArrayList<>();

        recyclerView = view.findViewById(R.id.favouriteProductsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FavouriteProductsListAdapter(getContext(), favouriteProducts);
        adapter.addOnLikeClickListener(onLikeClickListener);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadList();
        return view;
    }

    public void addFavouriteProduct(Product product){
        if(favouriteProducts.size() == 0){
            emptyListLayout.setVisibility(View.GONE);
            favouritesListLayout.setVisibility(View.VISIBLE);
        }
        favouriteProducts.add(product);
        adapter.notifyDataSetChanged();
    }

    public void removeFavouriteProduct(Product product){
        favouriteProducts.remove(product);
        adapter.notifyDataSetChanged();
        if(favouriteProducts.size() == 0){
            emptyListLayout.setVisibility(View.VISIBLE);
            favouritesListLayout.setVisibility(View.GONE);
        }
    }

    public void loadList(){
        if(auth.getUid() != null) {
            SharedPreferences settings = getActivity().getPreferences(MODE_PRIVATE);
            final String attribute = Constant.SORT_ATTRIBUTES[settings.getInt(SORT_ATTRIBUTE, 0)];
            final String direction = Constant.SORT_DIRECTIONS[settings.getInt(SORT_DIRECTION, 0)];
            final String filterType = Constant.PRODUCT_TYPES[settings.getInt(FILTER_TYPE, 0)];

            Task<QuerySnapshot> task;
            if(filterType == Constant.PRODUCT_TYPES[0]){
                task = BjorkAPI.loadFavouritesProducts(auth.getUid());
            }else{
                task = BjorkAPI.loadFavouritesProducts(auth.getUid(), filterType);
            }
            task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            favouriteProducts.clear();
                            Product product;
                            for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                product = snapshot.toObject(Product.class);
                                product.setId(snapshot.getId());
                                favouriteProducts.add(product);
                            }
                            Comparator<Product> comparator = new ProductComparator(attribute, direction).getComparator();
                            Collections.sort(favouriteProducts, comparator);
                            adapter.notifyDataSetChanged();
                            if(favouriteProducts.size() != 0){
                                emptyListLayout.setVisibility(View.GONE);
                                favouritesListLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }else{
            emptyListLayout.setVisibility(View.VISIBLE);
        }
    }

    public void addOnLikeClickListener(FavouriteProductsListAdapter.OnLikeClickListener onLikeClickListener){
        this.onLikeClickListener = onLikeClickListener;
    }
}
