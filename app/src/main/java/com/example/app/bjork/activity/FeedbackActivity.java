package com.example.app.bjork.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.app.bjork.R;
import com.example.app.bjork.viewmodel.FeedbackViewModel;

public class FeedbackActivity extends AppCompatActivity {

    private FeedbackViewModel feedbackViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        showToolbar();
        feedbackViewModel = ViewModelProviders.of(this).get(FeedbackViewModel.class);
        final TextInputEditText feedbackTextView =  findViewById(R.id.feedback_text);
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedbackMessage = feedbackTextView.getText().toString();
                feedbackViewModel.sendFeedback(feedbackMessage);
                finish();
            }
        });
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.feedback);
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
