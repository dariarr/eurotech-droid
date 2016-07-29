package com.martoff.esmart;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class ScreenUtility {

    private Activity activity;
    private float dpWidth;
    private float dpHeight;

    public ScreenUtility(Activity activity) {
        this.activity = activity;

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = activity.getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth = outMetrics.widthPixels / density;

        Log.e("ScreenUtility", "density :: " + density);
    }

    public float getWidth() {
        return dpWidth;
    }

    public float getHeight() {
        return dpHeight;
    }
}