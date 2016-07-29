package com.martoff.esmart.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by amanjham on 31/03/16.
 */
public class RobotoTextViewBold extends TextView {
    public RobotoTextViewBold(Context context) {
        super(context);
        createFont();
    }

    public RobotoTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public RobotoTextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"Roboto-Bold.ttf");
        setTypeface(font);
    }
}
