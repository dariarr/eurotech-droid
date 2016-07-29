package com.martoff.esmart.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by amanjham on 31/03/16.
 */
public class RobotoButtonMedium extends Button {
    public RobotoButtonMedium(Context context) {
        super(context);
        createFont();
    }

    public RobotoButtonMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public RobotoButtonMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Medium.ttf");
        setTypeface(font);
    }
}
