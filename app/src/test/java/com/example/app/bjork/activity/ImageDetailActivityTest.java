package com.example.app.bjork.activity;

import android.content.Context;

import com.example.app.bjork.model.Product;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.TestCase.*;

@RunWith(RobolectricTestRunner.class)
public class ImageDetailActivityTest {

    private ImageDetailActivity activity;
    private Context context;

    @Before
    public void setUp(){
        context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        activity = Robolectric.setupActivity(ImageDetailActivity.class);
    }

    @After
    public void tearDown(){
        FirebaseApp.getInstance().delete();
    }

    @Test
    public void activity_shouldBeNotNull(){
        assertNotNull(activity);
    }

    @Test
    public void clickingDownload_shouldDownloadImage(){

    }

    @Test
    public void clickingLike_shouldLikeProduct(){

    }

    @Test
    public void clickingShare_shouldOpenAppChooser(){

    }


}