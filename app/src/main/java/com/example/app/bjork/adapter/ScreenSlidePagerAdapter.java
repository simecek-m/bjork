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

    public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            default:
                return new ProductListFragment();
            case 1:
                return new FavouriteListFragment();
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
}
