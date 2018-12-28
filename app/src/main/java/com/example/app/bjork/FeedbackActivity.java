package com.example.app.bjork;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Feedback;
import com.google.firebase.auth.FirebaseAuth;

public class FeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button sendButton;
    private TextInputEditText feedbackView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        auth = FirebaseAuth.getInstance();
        feedbackView = findViewById(R.id.feedbackText);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.feedback);
        ab.setDisplayHomeAsUpEnabled(true);

        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feedback feedback = new Feedback(auth.getUid(), feedbackView.getText().toString());
                BjorkAPI.addFeedback(feedback);
                finish();
            }
        });
    }
}
