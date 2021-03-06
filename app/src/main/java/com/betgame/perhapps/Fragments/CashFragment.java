package com.betgame.perhapps.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.betgame.perhapps.R;
import com.betgame.perhapps.specific_views.LuckyWheel;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CashFragment extends Fragment implements View.OnClickListener {
    private RewardedAd rewardedVideoAd;
    private DatabaseReference mDatabaseReference;
    private TextView tvLogout;

    private ConstraintLayout mWacthAdForReward;
    private ConstraintLayout mSpinWheel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_cash, container, false);
        mWacthAdForReward = (ConstraintLayout) myView.findViewById(R.id.card_view_watch_ad);
        mSpinWheel = (ConstraintLayout) myView.findViewById(R.id.card_view_daily_spin);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("balance");
        mWacthAdForReward.setOnClickListener(this);
        mSpinWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LuckyWheel.class);
                startActivity(intent);
            }
        });
        if (getContext() != null){
            rewardedVideoAd = new RewardedAd(getContext(), "ca-app-pub-8505961705640879/6973071148");
        }
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
            }

            @Override
            public void onRewardedAdFailedToLoad(int i) {
            }
        };
        rewardedVideoAd.loadAd(new PublisherAdRequest.Builder().build(), adLoadCallback);
        return myView;
    }

    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(getContext(),
                "ca-app-pub-8505961705640879/6973071148");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new PublisherAdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }

    @Override
    public void onClick(View v) {
        if (rewardedVideoAd.isLoaded()){
            RewardedAdCallback callback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer currentBalance = snapshot.getValue(Integer.class);
                            if (currentBalance != null){
                                mDatabaseReference.setValue(currentBalance + 100);
                            } else {
                                Toast.makeText(getContext(), "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    Toast.makeText(getContext(), "Failed to show Ad", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRewardedAdClosed() {
                    rewardedVideoAd = createAndLoadRewardedAd();
                }
            };
            rewardedVideoAd.show(getActivity(), callback);
        } else {
            Toast.makeText(getContext(), "Video currently loading", Toast.LENGTH_LONG).show();
        }
    }
}

