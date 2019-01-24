package com.example.app.bjork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.app.bjork.R;
import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private Product product;

    private FirebaseAuth auth;
    private BottomSheetDialog loginBottomSheet;
    private BottomSheetDialog addToCartBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        auth = FirebaseAuth.getInstance();
        showToolbar();
        ImageView image = findViewById(R.id.image);
        TextView name = findViewById(R.id.name);
        ImageView typeIcon = findViewById(R.id.typeIcon);
        TextView size = findViewById(R.id.sizeText);
        TextView color = findViewById(R.id.colorText);
        TextView money = findViewById(R.id.moneyText);
        TextView description = findViewById(R.id.description);
        product = (Product) getIntent().getSerializableExtra("product");
        createAddToCartBottomSheet();
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.loading).error(R.drawable.error).fallback(R.drawable.error);
        Glide.with(this)
                .setDefaultRequestOptions(options)
                .load(product.getImageUrl())
                .into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this, ImageDetailActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }
        });
        name.setText(product.getName());
        int iconId = product.getTypeIconId();
        typeIcon.setImageResource(iconId);
        size.setText(product.getSize());
        color.setText(product.getAllColors());
        money.setText(product.getDiscountedPrice() + ",- Kč");
        description.setText(product.getDescription());
        createLoginBottomSheet();
        createAddToCartBottomSheet();
        cancelDiscountNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_cart:
                addToCart();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToCart(){
        if(auth.getUid() == null){
            loginBottomSheet.show();
        }else{
            addToCartBottomSheet.show();
        }
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

    public void createAddToCartBottomSheet(){
        addToCartBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        addToCartBottomSheet.setContentView(R.layout.add_to_cart_bottom_sheet);
        final TextView quantityText = addToCartBottomSheet.findViewById(R.id.quantityText);
        final Spinner colorText = addToCartBottomSheet.findViewById(R.id.colorText);
        List<String> colorsList = product.getColors();
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, colorsList);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorText.setAdapter(colorAdapter);
        View up = addToCartBottomSheet.findViewById(R.id.quantityUp);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity = Integer.parseInt(quantityText.getText().toString());
                quantity++;
                quantityText.setText(quantity.toString());
            }
        });
        View down = addToCartBottomSheet.findViewById(R.id.quantityDown);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity = Integer.parseInt(quantityText.getText().toString());
                if(quantity > 1){
                    quantity--;
                    quantityText.setText(quantity.toString());
                }
            }
        });
        View confirm = addToCartBottomSheet.findViewById(R.id.confirmButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String color = colorText.getSelectedItem().toString();
                Integer quantity = Integer.parseInt(quantityText.getText().toString());
                addToCartBottomSheet.dismiss();
                Database.addToCart(auth.getUid(), product, color, quantity);
                finish();
            }
        });
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.detail_product);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void cancelDiscountNotification(){
        boolean addToCartIntent = getIntent().getBooleanExtra("addToCart", false);
        if(addToCartIntent) {
            addToCartBottomSheet.show();
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.cancel(Integer.parseInt(product.getId()));
        }
    }
}
