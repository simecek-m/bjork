package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.Feedback;
import com.google.firebase.auth.FirebaseAuth;

public class FeedbackViewModel extends ViewModel {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    public void sendFeedback(String feedbackMessage){
        System.out.println("sending feedback from viewModel!");
        Feedback feedback = new Feedback(auth.getUid(), feedbackMessage);
        Database.sendFeedback(feedback);
    }
}
