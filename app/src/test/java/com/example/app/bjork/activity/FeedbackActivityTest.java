package com.example.app.bjork.activity;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.app.bjork.R;
import com.google.firebase.FirebaseApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class FeedbackActivityTest {

    private FeedbackActivity activity;
    private Context context;

    @Before
    public void setUp(){
        context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        activity = Robolectric.setupActivity(FeedbackActivity.class);

    }

    @After
    public void tearDown(){
        FirebaseApp.getInstance().delete();
    }

    @Test
    public void activity_shouldNotBeNull(){
        assertNotNull(activity);
    }

    @Test
    public void toolbar_isVisible(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        assertEquals(toolbar.getVisibility(), View.VISIBLE);
        assertEquals(toolbar.getTitle(), context.getString(R.string.feedback));
    }

    @Test
    public void messageInput_isVisible(){
        TextInputLayout feedbackInput = activity.findViewById(R.id.feedback_input);
        assertEquals(feedbackInput.getVisibility(), View.VISIBLE);
        assertFalse(feedbackInput.isFocused());
        assertTrue(feedbackInput.isFocusableInTouchMode());
    }

    @Test
    public void clickingSend_shouldFinishActivity(){
        activity.findViewById(R.id.send_button).performClick();
        assertTrue(activity.isFinishing());
    }
}