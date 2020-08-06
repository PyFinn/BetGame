package com.betgame.app.specific_views;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.betgame.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class LuckyWheel extends AppCompatActivity {
    boolean mButtonRotation = true;
    private static final float number = 30f;
    int mDegrees = 0, mOldDegrees = 0;
    Button mButtonStartWheel;
    ImageView mImageWheel;
    Random r;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    int currentBalance;
    DatabaseReference mDbReference = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    DatabaseReference mDbBalanceReference = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(1024);
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lucky_wheel);

        mDbBalanceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentBalance = (int) ((long)snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mButtonStartWheel = (Button) findViewById(R.id.button_start_spin);
        mImageWheel = (ImageView) findViewById(R.id.lucky_wheel);

        mDbReference.child("spinned").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(boolean) snapshot.getValue()){
                    mButtonStartWheel.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        r = new Random();
        mButtonStartWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonRotation){
                    mOldDegrees = mDegrees % 360;
                    mDegrees = r.nextInt(3600) + 720;
                    RotateAnimation rotateAnimation = new RotateAnimation(mOldDegrees, mDegrees,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(9000);
                    rotateAnimation.setFillAfter(true);
                    rotateAnimation.setInterpolator(new DecelerateInterpolator());
                    rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mButtonRotation = false;
                            mButtonStartWheel.setEnabled(false);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mButtonRotation = true;
                            mButtonStartWheel.setEnabled(false);
                            currentNumber(360 - (mDegrees % 360));
                            mDbReference.child("spinned").setValue(true);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    mImageWheel.startAnimation(rotateAnimation);
                }
            }
        });
    }
    private void currentNumber(int degrees) {
        int amount = 0;

        if (degrees >= (number * 0) && degrees < (number * 1)){
            amount = 20;
        }
        if (degrees >= (number * 1) && degrees < (number * 2)){
            amount = 10;
        }
        if (degrees >= (number * 2) && degrees < (number * 3)){
            amount = 50;
        }
        if (degrees >= (number * 3) && degrees < (number * 4)){
            amount = 100;
        }
        if (degrees >= (number * 4) && degrees < (number * 5)){
            amount = 10;
        }
        if (degrees >= (number * 5) && degrees < (number * 6)){
            amount = 150;
        }
        if (degrees >= (number * 6) && degrees < (number * 7)){
            amount = 50;
        }
        if (degrees >= (number * 7) && degrees < (number * 8)){
            amount = 20;
        }
        if (degrees >= (number * 8) && degrees < (number * 9)){
            amount = 100;
        }
        if (degrees >= (number * 9) && degrees < (number * 10)){
            amount = 10;
        }
        if (degrees >= (number * 10) && degrees < (number * 11)){
            amount = 250;
        }
        if (degrees >= (number * 11) && degrees < (number * 12)){
            amount = 1400;
        }
        setBalance(amount);
        Toast toast = Toast.makeText(this, "You won: " + (amount) + "$", Toast.LENGTH_LONG);
        toast.show();
    }
    void setBalance(int amount) {
        mDbBalanceReference.setValue(currentBalance + amount);
    }
}

