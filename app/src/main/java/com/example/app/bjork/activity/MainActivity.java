package com.example.app.bjork.activity;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.FavouriteProductsListAdapter;
import com.example.app.bjork.adapter.ProductsListAdapter;
import com.example.app.bjork.adapter.ScreenSlidePagerAdapter;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.fragment.FavouriteListFragment;
import com.example.app.bjork.fragment.ProductListFragment;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.viewmodel.MainViewModel;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.firebase.auth.FirebaseUser;

import static com.example.app.bjork.constant.Constant.BROADCAST_LIKE_PRODUCT;
import static com.example.app.bjork.constant.Constant.BROADCAST_USER_LOG_IN;
import static com.example.app.bjork.constant.Constant.BROADCAST_USER_LOG_OUT;
import static com.example.app.bjork.constant.Constant.FILTER_TYPE;
import static com.example.app.bjork.constant.Constant.SORT_ATTRIBUTE;
import static com.example.app.bjork.constant.Constant.SORT_DIRECTION;

public class MainActivity extends AppCompatActivity {

    private String GENDER_MALE = Constant.GENDERS[0];

    private MainViewModel mainViewModel;
    private Menu menu;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FavouriteListFragment favouriteListFragment;
    private ProductListFragment productListFragment;
    private BottomSheetDialog loginBottomSheet;
    private BottomSheetDialog sortBottomSheet;
    private TextView name;
    private TextView email;
    private ImageView profileImage;
    private LocalBroadcastManager localBroadcastManager;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showToolbar();
        createLoginBottomSheet();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        final FirebaseUser currentUser = mainViewModel.getCurrentUser();
        pagerAdapter.addOnLikeClickListener(new ProductsListAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(Product product) {
                if(currentUser == null){
                    loginBottomSheet.show();
                }else{
                    if(product.likedByUser(currentUser.getUid())){
                        favouriteListFragment.addFavouriteProduct(product);
                    }else{
                        favouriteListFragment.removeFavouriteProduct(product);
                    }

                }
            }
        });

        pagerAdapter.addOnLikeClickListener(new FavouriteProductsListAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(Product product) {
                    ProductListFragment listFragment = (ProductListFragment) pagerAdapter.getItem(0);
                    listFragment.removeFavouriteProduct(product, currentUser.getUid());
                    favouriteListFragment.removeFavouriteProduct(product);
            }
        });

        productListFragment = (ProductListFragment) pagerAdapter.getItem(0);
        favouriteListFragment = (FavouriteListFragment) pagerAdapter.getItem(1);
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

        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.name);
        email = header.findViewById(R.id.email);
        profileImage = header.findViewById(R.id.profileImage);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(likeBroadcastReceiver, new IntentFilter(BROADCAST_LIKE_PRODUCT));
        localBroadcastManager.registerReceiver(logoutReceiver, new IntentFilter(BROADCAST_USER_LOG_OUT));
        localBroadcastManager.registerReceiver(loginReceiver, new IntentFilter(BROADCAST_USER_LOG_IN));

        mainViewModel.getUserInfo().observe(this, new Observer<DataWrapper<UserInfo>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<UserInfo> userInfoDataWrapper) {
                System.out.println("logged user info changed");
                if(userInfoDataWrapper != null && userInfoDataWrapper.getData() != null){
                    userInfo = userInfoDataWrapper.getData();
                    updateNavigationHeader(userInfo);
                }else{
                    updateNavigationHeader(null);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_filter:
                createSortBottomSheet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(menu != null){
            MenuItem item = menu.findItem(R.id.menu_search);
            item.collapseActionView();
        }
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
            case R.id.nav_cart:
                item.setChecked(true);
                intent = new Intent(this, ShoppingCartActivity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                item.setChecked(true);
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                break;
            case R.id.nav_about:
                item.setChecked(true);
                intent = new Intent(this, AboutApplicationActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.app_name);
        actionBar.setElevation(0);
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

    public void updateNavigationHeader(UserInfo currentUserInfo){
        FirebaseUser currentUser = mainViewModel.getCurrentUser();
        if(currentUser != null && currentUserInfo != null){
            String userGender = currentUserInfo.getGender();
            if(currentUserInfo.getFirstname() != null && currentUserInfo.getLastname() != null) {
                name.setText(currentUserInfo.getFirstname() + " " + currentUserInfo.getLastname());
            }if(userGender == null){
                profileImage.setImageDrawable(null);
            }else if(userGender.equals(GENDER_MALE)){
                profileImage.setImageDrawable(getDrawable(R.drawable.avatar_man));
            }else{
                profileImage.setImageDrawable(getDrawable(R.drawable.avatar_woman));
            }
            email.setText(currentUser.getEmail());
        }else{
            name.setText(null);
            email.setText(R.string.unlogged_user);
            profileImage.setImageDrawable(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_filter_menu, menu);
        this.menu = menu;
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchViewMenuIcons(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchViewMenuIcons(true);
                return true;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultActivity.class)));
        searchView.setSubmitButtonEnabled(true);
        return true;
    }

    public void createSortBottomSheet(){
        sortBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        sortBottomSheet.setContentView(R.layout.sort_bottom_sheet);
        final Spinner sortAttributeText = sortBottomSheet.findViewById(R.id.sortAttributeText);
        final Spinner sortTypeText = sortBottomSheet.findViewById(R.id.sortTypeText);
        final Spinner filterTypeText = sortBottomSheet.findViewById(R.id.filterTypeText);

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        sortAttributeText.setSelection(settings.getInt(SORT_ATTRIBUTE, 0));
        sortTypeText.setSelection(settings.getInt(SORT_DIRECTION, 0));
        filterTypeText.setSelection(settings.getInt(FILTER_TYPE, 0));

        Button button = sortBottomSheet.findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedAttributeIndex  = sortAttributeText.getSelectedItemPosition();
                int selectedDirectionIndex = sortTypeText.getSelectedItemPosition();
                int selectedFilterIndex = filterTypeText.getSelectedItemPosition();
                saveFilterSettings(selectedAttributeIndex, selectedDirectionIndex, selectedFilterIndex);
            }
        });
        sortBottomSheet.show();
    }

    public void saveFilterSettings(int selectedAttributeIndex, int selectedDirectionIndex, int selectedFilterIndex){
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt(SORT_ATTRIBUTE, selectedAttributeIndex);
        editor.putInt(SORT_DIRECTION, selectedDirectionIndex);
        editor.putInt(FILTER_TYPE, selectedFilterIndex);
        editor.commit();
        productListFragment.loadList();
        favouriteListFragment.loadList();
        sortBottomSheet.dismiss();
    }

    public void searchViewMenuIcons(boolean visibility){
        MenuItem searchMenu = menu.findItem(R.id.menu_search);
        for(int i=0; i<menu.size(); i++){
            MenuItem item = menu.getItem(i);
            if(item != searchMenu){
                item.setVisible(visibility);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainViewModel.getUserInfo().removeObservers(this);
        localBroadcastManager.unregisterReceiver(likeBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(loginReceiver);
        localBroadcastManager.unregisterReceiver(logoutReceiver);
    }

    private BroadcastReceiver likeBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Product product = (Product) intent.getSerializableExtra("product");
            productListFragment.updateList(product);
            favouriteListFragment.updateList(product);
        }
    };

    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            productListFragment.loadList();
            favouriteListFragment.loadList();
        }
    };

    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            productListFragment.loadList();
            favouriteListFragment.loadList();
        }
    };
}
