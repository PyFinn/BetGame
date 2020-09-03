package com.betgame.perhapps.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class CustomViewButton extends AppCompatButton {

    public CustomViewButton(@NonNull Context context) {
        super(context);
    }

    public CustomViewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
