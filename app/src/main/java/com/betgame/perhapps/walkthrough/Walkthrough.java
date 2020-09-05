package com.betgame.perhapps.walkthrough;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.betgame.perhapps.R;

public class Walkthrough extends AppCompatActivity {

    private ViewPager mViewPager;
    private LinearLayout mLinearLayout;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkthrough);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mViewPager = findViewById(R.id.vp_walkthrough);
        mLinearLayout = findViewById(R.id.ll_dots_walkthrough);

        viewPagerAdapter = new ViewPagerAdapter(this);

        mViewPager.setAdapter(viewPagerAdapter);
    }
}
