package com.betgame.perhapps.specific_views;

import android.animation.TimeAnimator;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.betgame.perhapps.R;
import com.betgame.perhapps.custom_views.CustomViewButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Random;


public class Profile extends AppCompatActivity implements TimeAnimator.TimeListener {

    private ImageView mBackImageView;
    private TextView mNameTextView;
    private TextView mMailTextView;
    private TextView mBalanceTextView;
    private TextView mGuestText;
    private Button mCreateAccount;
    private CustomViewButton mSignOut;
    private TimeAnimator mTimeAnimator;
    private ClipDrawable mClipDrawable;
    private int mCurrentLevel = 0;
    private LayerDrawable layerDrawable;
    private static final int LEVEL_INCREMENT = 200;
    private static final int MAX_LEVEL = 10000;
    private boolean first_time = true;
    private UserProfileChangeRequest userProfileChangeRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Intent initialIntent = getIntent();
        String name = initialIntent.getStringExtra("Name");
        String mail = initialIntent.getStringExtra("Mail");
        Integer balance = initialIntent.getIntExtra("Balance", 0);

        mBackImageView = (ImageView) findViewById(R.id.iv_go_back);
        mNameTextView = (TextView) findViewById(R.id.tv_username);
        mMailTextView = (TextView) findViewById(R.id.tv_email);
        mSignOut = (CustomViewButton) findViewById(R.id.sign_out_button);
        mBalanceTextView = (TextView) findViewById(R.id.tv_balance);
        layerDrawable = (LayerDrawable) mSignOut.getBackground();
        mClipDrawable = (ClipDrawable) layerDrawable.findDrawableByLayerId(R.id.clip_drawable);
        mGuestText = (TextView) findViewById(R.id.tv_guest_message);
        mCreateAccount = (Button) findViewById(R.id.create_account_button);

        mTimeAnimator = new TimeAnimator();
        mTimeAnimator.setTimeListener(this);

        if (name != null && !name.equals("")) {
            mNameTextView.setText(name);
        } else {
            long r = new Random().nextInt(100000000) + 100;
            String userName = "Guest" + r;
            mNameTextView.setText(userName);
            userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
            try {
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest);
            } catch (NullPointerException e) {

            }
        }
        if (mail != null && !mail.equals("")) {
            mMailTextView.setText(mail);
        } else {
            isGuest();
        }
        if (balance != null) {
            mBalanceTextView.setText(String.valueOf(balance) + "$");
        }

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new CreateAccount();
                newFragment.show(getSupportFragmentManager(), "Create Account");
            }
        });

        mSignOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animateButton(mSignOut);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (mTimeAnimator.isRunning() && mCurrentLevel != 10000) {
                            mCurrentLevel = 0;
                            mTimeAnimator.cancel();
                            mClipDrawable.setLevel(mCurrentLevel);
                            mSignOut.setTextColor(getResources().getColor(R.color.colorAccent));
                            return true;
                        } else if (mCurrentLevel == 10000) {
                            signOut();
                            finish();
                            return true;
                        }
                }
                return false;
            }
        });

        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        mClipDrawable.setLevel(mCurrentLevel);
        if (mCurrentLevel >= MAX_LEVEL) {
            mTimeAnimator.cancel();
            signOut();
            finish();
        } else {
            mCurrentLevel = Math.min(MAX_LEVEL, mCurrentLevel + LEVEL_INCREMENT);
        }
        if (mCurrentLevel >= MAX_LEVEL / 2 && first_time) {
            first_time = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSignOut.setTextColor(getColor(R.color.colorInnerCards));
            }
        }
    }

    public void animateButton(View view) {
        if (!mTimeAnimator.isRunning()) {
            mCurrentLevel = 0;
            mTimeAnimator.start();
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void isGuest() {
        mGuestText.setVisibility(View.VISIBLE);
        mCreateAccount.setVisibility(View.VISIBLE);
        mGuestText.setEnabled(true);
        mCreateAccount.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeAnimator.cancel();
        mClipDrawable.setLevel(0);
    }
}
