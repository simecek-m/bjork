package com.example.app.bjork.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ProductsListAdapter;
import com.example.app.bjork.adapter.ScreenSlidePagerAdapter;
import com.example.app.bjork.fragment.FavouriteListFragment;
import com.example.app.bjork.fragment.ProductListFragment;
import com.example.app.bjork.model.Product;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private String loggedUserId;

    private BottomSheetDialog loginBottomSheet;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar();
        createLoginBottomSheet();

        mAuth = FirebaseAuth.getInstance();
        loggedUserId = mAuth.getUid();
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        pagerAdapter.addOnLikeClickListener(new ProductsListAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(Product product) {
                if(mAuth.getUid() == null){
                    loginBottomSheet.show();
                }else{
                    FavouriteListFragment favouriteFragment = (FavouriteListFragment) pagerAdapter.getItem(1);
                    if(product.likedByUser(mAuth.getUid())){
                        favouriteFragment.addFavouriteProduct(product);
                    }else{
                        favouriteFragment.removeFavouriteProduct(product);
                    }

                }
            }
        });
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
        if(loggedUserChanged()){
            loggedUserId = mAuth.getUid();
            ProductListFragment productListFragment = (ProductListFragment) pagerAdapter.getItem(0);
            FavouriteListFragment favouriteListFragment = (FavouriteListFragment) pagerAdapter.getItem(1);
            productListFragment.loadList();
            favouriteListFragment.loadList();
        }
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
            case R.id.nav_profile:
                item.setChecked(true);
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                item.setChecked(true);
                intent = new Intent(this, AboutApplicationActivity.class);
                startActivity(intent);
                break;
        }
    }

    public boolean loggedUserChanged(){
        return this.loggedUserId != mAuth.getUid();
    }

    public void createToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Drawable hamburgerIcon = getDrawable(R.drawable.ic_menu);
        hamburgerIcon.setTint(getResources().getColor(R.color.white));
        actionBar.setHomeAsUpIndicator(hamburgerIcon);
    }

    public void createLoginBottomSheet(){
        loginBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        loginBottomSheet.setContentView(R.layout.login_bottom_sheet);
        View login = loginBottomSheet.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBottomSheet.dismiss();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
