package com.martoff.esmart.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by amanjham on 31/03/16.
 */
public class RobotoEditTextRegular extends EditText {
    public RobotoEditTextRegular(Context context) {
        super(context);
        createFont();
    }

    public RobotoEditTextRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public RobotoEditTextRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"Roboto-Regular.ttf");
        setTypeface(font);
    }
}
