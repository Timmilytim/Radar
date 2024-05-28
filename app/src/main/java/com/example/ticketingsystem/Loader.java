package com.example.ticketingsystem;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

public class Loader {

    private static ProgressBar progressBar;
    private static View overlay;

    public static void showLoader(Activity activity) {
        progressBar = activity.findViewById(R.id.progressBar);
        overlay = activity.findViewById(R.id.overlay);

        if (progressBar != null && overlay != null) {
            progressBar.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
        }
    }

    public static void hideLoader(Activity activity) {
        progressBar = activity.findViewById(R.id.progressBar);
        overlay = activity.findViewById(R.id.overlay);

        if (progressBar != null && overlay != null) {
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        }
    }
}
