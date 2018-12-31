package com.example.app.bjork;

import android.view.View;

public class Animation {

    public static void transitionViews(final View hideView, final View showView){
        hideView.setAlpha(1f);
        showView.setAlpha(0f);
        showView.setVisibility(View.VISIBLE);

        hideView.animate()
                .setDuration(500)
                .alpha(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        hideView.setVisibility(View.GONE);
                        showView.animate()
                                .setDuration(500)
                                .alpha(1f)
                                .start();
                    }
                })
                .start();
    }
}
