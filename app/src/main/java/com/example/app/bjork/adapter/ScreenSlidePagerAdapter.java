package com.example.app.bjork.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.app.bjork.R;
import com.example.app.bjork.fragment.FavouriteListFragment;
import com.example.app.bjork.fragment.ProductListFragment;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 2;
    private Context context;

    private ProductListFragment productListFragment;
    private FavouriteListFragment favouriteListFragment;

    public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        productListFragment = new ProductListFragment();
        favouriteListFragment = new FavouriteListFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            default:
                return productListFragment;
            case 1:
                return favouriteListFragment;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            default:
                return context.getString(R.string.all);
            case 1:
                return context.getString(R.string.favourite);
        }
    }

    public void addOnLikeClickListener(ProductsListAdapter.OnLikeClickListener listener){
        productListFragment.addOnLikeClickListener(listener);
    }

    public void addOnLikeClickListener(FavouriteProductsListAdapter.OnLikeClickListener listener){
        favouriteListFragment.addOnLikeClickListener(listener);
    }
}
