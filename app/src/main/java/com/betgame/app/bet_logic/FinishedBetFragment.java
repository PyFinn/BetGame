package com.betgame.app.bet_logic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.betgame.app.Bet;
import com.betgame.app.Game;
import com.betgame.app.R;

public class FinishedBetFragment extends Fragment {

    private static final String mActualGameKey = "ActualGame";
    private static final String mActualBetKey = "ActualBet";
    private Button mButtonClaim;
    private TextView mHomeTeamName;
    private TextView mAwayTeamName;
    private Game thisGame;
    private Bet thisBet;
    private TextView mHomeTeamScore;
    private TextView mAwayTeamScore;
    private TextView mRewardDisplay;

    public static FinishedBetFragment newInstance(Game game, Bet bet) {
        FinishedBetFragment finishedBetFragment = new FinishedBetFragment();
        Bundle parentBundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putParcelable(mActualGameKey, game);
        bundle2.putParcelable(mActualBetKey, bet);
        parentBundle.putBundle(mActualGameKey, bundle1);
        parentBundle.putBundle(mActualBetKey, bundle2);

        finishedBetFragment.setArguments(parentBundle);
        return finishedBetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.finished_bet, container, false);

        thisGame = getArguments().getBundle(mActualGameKey).getParcelable(mActualGameKey);
        thisBet = getArguments().getBundle(mActualBetKey).getParcelable(mActualBetKey);

        mHomeTeamScore = (TextView) myView.findViewById(R.id.home_team_score_display);
        mAwayTeamScore = (TextView) myView.findViewById(R.id.away_team_score_display);
        mButtonClaim = (Button) myView.findViewById(R.id.claim_reward_button);
        mHomeTeamName = (TextView) myView.findViewById(R.id.home_team_name_reward_sheet);
        mAwayTeamName = (TextView) myView.findViewById(R.id.away_team_name_reward_sheet);
        mRewardDisplay = (TextView) myView.findViewById(R.id.reward_text_view_reward_sheet);

        mHomeTeamName.setText(thisGame.getHome_team());
        mAwayTeamName.setText(thisGame.getAway_team());
        mHomeTeamScore.setText(String.valueOf(thisGame.getHome_team_score()));
        mAwayTeamScore.setText(String.valueOf(thisGame.getAway_team_score()));
        mButtonClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClaimButtonClicked();
            }
        });
        mButtonClaim.setBackgroundResource(R.drawable.black_rectangle);
        if (thisBet.getTeam().equals(thisGame.getHome_team())){
            mHomeTeamName.setBackgroundColor(getResources().getColor(R.color.oddDrawBackground));
        } else if (thisBet.getTeam().equals(thisGame.getAway_team())){
            mAwayTeamName.setBackgroundColor(getResources().getColor(R.color.oddDrawBackground));
        } else {
            mHomeTeamName.setBackgroundColor(getResources().getColor(R.color.oddBackground));
            mAwayTeamName.setBackgroundColor(getResources().getColor(R.color.oddBackground));
        }

        if (FinishedBetsDialog.determineWinner(thisGame).equals(thisBet.getTeam())){
            mRewardDisplay.setText(String.valueOf(thisBet.getAmount() * thisBet.getOdd()));
        } else {
            mRewardDisplay.setText("0");
        }

        return myView;
    }

    interface ClaimButtonclickedListener {
        void onClaimButtonClicked();
    }

    private void onClaimButtonClicked() {
        ClaimButtonclickedListener buttonclickedListener = (ClaimButtonclickedListener) getParentFragment();
        assert buttonclickedListener != null;
        buttonclickedListener.onClaimButtonClicked();
    }
}
