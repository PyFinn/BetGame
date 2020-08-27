package com.betgame.perhapps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class SplashScreen extends AppCompatActivity {

    Animation topAnimation, bottomAnimation;
    ImageView logo, text;
    TextView subText;
    AsyncTask<?,?,?> task;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;
    private DatabaseReference mUsersChild;
    private FirebaseAuth.AuthStateListener mStateListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logo = (ImageView) findViewById(R.id.icon);
        text = (ImageView) findViewById(R.id.slogan);
        subText = (TextView) findViewById(R.id.login_msg);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_top);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_bottom);

        logo.setAnimation(topAnimation);
        text.setAnimation(bottomAnimation);
        subText.setAnimation(bottomAnimation);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mGameDatabaseReference = mFirebaseDatabase.getReference();
        mUsersChild = mGameDatabaseReference.child("users");

        task = new CheckLogin();
        task.execute();

        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    final String uid = user.getUid();
                    mUsersChild.child(uid).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null){
                            } else {
                                mUsersChild.child(uid).child("balance").setValue(500);
                                mUsersChild.child(uid).child("spinned").setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.icon)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build()))
                                    .build(),
                            1);
                }
            }
        };
    }

    private final class CheckLogin extends AsyncTask<Object,Void,String> {
        @Override
        protected String doInBackground(Object... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            for (int i = 0; i < 25;i++) {
                if (FirebaseAuth.getInstance().getUid() != null) {
                    return "Start";
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    return "Failed";
                }
            }
            return "Failed";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Start")) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else if (s.equals("Failed")) {
                Toast.makeText(getApplicationContext(), "Failed to LogIn. Please check your Connection.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel(true);
        }
    }
}
