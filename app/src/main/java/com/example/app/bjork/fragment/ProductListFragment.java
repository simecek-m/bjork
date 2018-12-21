package com.example.app.bjork.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ProductsListAdapter;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.comparator.ProductComparator;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.app.bjork.constant.Constant.*;


public class ProductListFragment extends Fragment {

    private static final String TAG = "ProductListFragment";

    private List<Product> productsList = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProductsListAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProductsListAdapter.OnLikeClickListener onLikeClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.productsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProductsListAdapter(getContext(), productsList);
        adapter.addOnLikeClickListener(onLikeClickListener);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        loadList();
        return view;
    }

    public void loadList(){
        SharedPreferences settings = getActivity().getPreferences(MODE_PRIVATE);
        final String attribute = Constant.SORT_ATTRIBUTES[settings.getInt(SORT_ATTRIBUTE, 0)];
        final String direction = Constant.SORT_DIRECTIONS[settings.getInt(SORT_DIRECTION, 0)];
        final String filterType = Constant.PRODUCT_TYPES[settings.getInt(FILTER_TYPE, 0)];
        Task<QuerySnapshot> task;
        if(filterType == Constant.PRODUCT_TYPES[0]){
            task = BjorkAPI.loadProducts();
        }else{
            task = BjorkAPI.loadProducts(filterType);
        }
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productsList.clear();
                        Product product;
                        for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                            product = snapshot.toObject(Product.class);
                            product.setId(snapshot.getId());
                            productsList.add(product);
                        }
                        Comparator<Product> comparator = new ProductComparator(attribute, direction).getComparator();
                        Collections.sort(productsList, comparator);
                        adapter.setList(productsList);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

    }

    public void addOnLikeClickListener(ProductsListAdapter.OnLikeClickListener onLikeClickListener){
        this.onLikeClickListener = onLikeClickListener;
    }

    public void removeFavouriteProduct(Product product, String userId){
        int position = productsList.indexOf(product);
        List<String> likes = productsList.get(position).getLikes();
        likes.remove(userId);
        adapter.notifyDataSetChanged();
    }
}


