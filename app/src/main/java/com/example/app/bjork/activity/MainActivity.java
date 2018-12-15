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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.FavouriteProductsListAdapter;
import com.example.app.bjork.adapter.ProductsListAdapter;
import com.example.app.bjork.adapter.ScreenSlidePagerAdapter;
import com.example.app.bjork.fragment.FavouriteListFragment;
import com.example.app.bjork.fragment.ProductListFragment;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private static final int CHANGE_PROFILE_INFO_REQUEST = 1;
    private static final String GENDER_MALE = "Muž";

    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private String loggedUserId;

    private BottomSheetDialog loginBottomSheet;
    private Toolbar toolbar;

    private TextView name;
    private TextView email;
    private ImageView profileImage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar();
        createLoginBottomSheet();

        db = FirebaseFirestore.getInstance();

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

        pagerAdapter.addOnLikeClickListener(new FavouriteProductsListAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(Product product) {
                    ProductListFragment listFragment = (ProductListFragment) pagerAdapter.getItem(0);
                    listFragment.removeFavouriteProduct(product, mAuth.getUid());
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

        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.name);
        email = header.findViewById(R.id.email);
        profileImage = header.findViewById(R.id.profileImage);

        updateUserInfo();
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
            updateUserInfo();
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
                startActivityForResult(intent, CHANGE_PROFILE_INFO_REQUEST);
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

    public void updateUserInfo(){
        if(mAuth.getUid() != null){
            db.collection("user_info")
                    .document(mAuth.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserInfo user = documentSnapshot.toObject(UserInfo.class);
                            name.setText(user.getFirstname() + " " + user.getLastname());
                            email.setText(user.getEmail());
                            if(user.getGender().equals(GENDER_MALE)){
                                profileImage.setImageResource(R.drawable.avatar_man);
                            }else{
                                profileImage.setImageResource(R.drawable.avatar_woman);
                            }
                        }
                    });
        }else{
            name.setText(null);
            email.setText(R.string.unlogged_user);
            profileImage.setImageDrawable(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHANGE_PROFILE_INFO_REQUEST && resultCode == RESULT_OK){
            UserInfo userInfo = (UserInfo) data.getSerializableExtra("userInfo");
            name.setText(userInfo.getFirstname() + " " + userInfo.getLastname());
            email.setText(userInfo.getEmail());
            if(userInfo.getGender().equals(GENDER_MALE)){
                profileImage.setImageResource(R.drawable.avatar_man);
            }else{
                profileImage.setImageResource(R.drawable.avatar_woman);
            }
        }
    }
}
