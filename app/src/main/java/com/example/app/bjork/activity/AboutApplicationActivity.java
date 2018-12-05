package com.example.app.bjork.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;


import com.example.app.bjork.R;

public class AboutApplicationActivity extends AppCompatActivity {

    private static final String TAG = "AboutApplicationActivit";

    private TextView osVersionText;
    private TextView appVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_application);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        osVersionText = findViewById(R.id.os_version);
        appVersionText = findViewById(R.id.app_version);

        String osVersion = Build.VERSION.RELEASE;

        osVersionText.setText(getString(R.string.system_version) + ": " + osVersion);

        try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionText.setText(getString(R.string.version) + ": " + packageInfo.versionName);
        }catch (PackageManager.NameNotFoundException e){
            Log.e(TAG, "PackageName Not Found", e);
        }
    }
}
