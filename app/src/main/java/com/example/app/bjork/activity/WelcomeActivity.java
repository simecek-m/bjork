package com.example.app.bjork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.bjork.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final int SPLASH_TIMER = 4000;
    private static final int TRANSLATION_ANIMATION_TIMER = 2000;
    private static final int ALPHA_ANIMATION_TIMER = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final ImageView icon = findViewById(R.id.icon);
        final TextView appName = findViewById(R.id.app_name);
        final TextView motto = findViewById(R.id.motto);

        icon.animate()
                .setDuration(TRANSLATION_ANIMATION_TIMER)
                .translationY(0f)
                .start();

        appName.animate()
                .setDuration(TRANSLATION_ANIMATION_TIMER)
                .translationY(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        motto.animate()
                                .setDuration(ALPHA_ANIMATION_TIMER)
                                .alpha(1f)
                                .start();
                    }
                })
                .start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);
    }
}
