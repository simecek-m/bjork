package com.example.app.bjork.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ScreenSlidePagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable hamburgerIcon = getDrawable(R.drawable.ic_menu);
        hamburgerIcon.setTint(getResources().getColor(R.color.white));
        actionBar.setHomeAsUpIndicator(hamburgerIcon);

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.nav_products);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean animation = item.getItemId() == R.id.nav_products;
                drawerLayout.closeDrawer(Gravity.START, animation);
                openItemActivity(item);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_products);
    }

    public void openItemActivity(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            default:
                break;
            case R.id.nav_store:
                item.setChecked(true);
                intent = new Intent(this, NearestStoreActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                item.setChecked(true);
                intent = new Intent(this, AboutApplicationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
