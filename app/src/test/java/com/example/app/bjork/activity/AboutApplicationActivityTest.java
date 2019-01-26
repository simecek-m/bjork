package com.example.app.bjork.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.app.bjork.R;
import com.example.app.bjork.activity.AboutApplicationActivity;
import com.example.app.bjork.activity.FeedbackActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.test.core.app.ApplicationProvider;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class AboutApplicationActivityTest {

    private AboutApplicationActivity activity;
    private Context context;

    @Before
    public void setUp(){
        activity = Robolectric.setupActivity(AboutApplicationActivity.class);
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void activity_shouldNotBeNull(){
        assertNotNull(activity);
        assertNotNull(context);
    }

    @Test
    public void clickingFeedback_shouldStartFeedbackActivity(){
        activity.findViewById(R.id.feedback_button).performClick();
        Intent expectedIntent = new Intent(activity, FeedbackActivity.class);
        Intent actual = shadowOf((Application) context).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void toolbar_isVisible(){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        assertEquals(toolbar.getVisibility(), View.VISIBLE);
        assertEquals(toolbar.getTitle(), context.getString(R.string.about));
    }
}
