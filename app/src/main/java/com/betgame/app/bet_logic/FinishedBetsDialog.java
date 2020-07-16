package com.betgame.app.bet_logic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.betgame.app.Bet;
import com.betgame.app.Game;
import com.betgame.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FinishedBetsDialog extends AppCompatDialogFragment implements FinishedBetFragment.ClaimButtonclickedListener{

    private static final String mActualGameKey = "ActualGame";
    private static final String mActualBetKey = "ActualBet";
    private static final String mActualBalanceKey = "ActualBalance";
    private Game[] games;
    private Bet[] bets;
    private Integer mBalance;
    private static Integer counter = 0;
    private static Integer gamesLength;

    public static FinishedBetsDialog newInstance(Game[] game, Bet[] bet, Integer currentBalance) {
        FinishedBetsDialog finishedBetFragment = new FinishedBetsDialog();
        Bundle parentBundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        Bundle bundle3 = new Bundle();
        bundle1.putParcelableArray(mActualGameKey, game);
        bundle2.putParcelableArray(mActualBetKey, bet);
        bundle3.putInt(mActualBalanceKey, currentBalance);
        parentBundle.putBundle(mActualGameKey, bundle1);
        parentBundle.putBundle(mActualBetKey, bundle2);
        parentBundle.putBundle(mActualBalanceKey, bundle3);

        finishedBetFragment.setArguments(parentBundle);
        return finishedBetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bet_payout_dialog, container, false);

        getDialog().setTitle("Claim Reward");
        getDialog().setCanceledOnTouchOutside(false);
        counter = 0;
        games = (Game[]) getArguments().getBundle(mActualGameKey).getParcelableArray(mActualGameKey);
        bets = (Bet[]) getArguments().getBundle(mActualBetKey).getParcelableArray(mActualBetKey);
        mBalance = getArguments().getBundle(mActualBalanceKey).getInt(mActualBalanceKey);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        try {
            FinishedBetFragment finishedBetFragment = FinishedBetFragment.newInstance(games[counter], bets[counter]);
            counter++;
            gamesLength = games.length;
            transaction.replace(R.id.bet_payout_container, finishedBetFragment).commit();
        } catch (ArrayIndexOutOfBoundsException e){
            dismiss();
        }
        return view;
    }

    @Override
    public void onClaimButtonClicked() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (betWon(determineWinner(games[counter - 1]), bets[counter - 1])){
            dbRef.child("balance").setValue(mBalance + amountToAddOnBalance(bets[counter - 1]));
        }
//        dbRef.child("active_bets").child(bets[counter - 1].getId()).child(bets[counter - 1].getBetId()).removeValue();
        if (counter < gamesLength){
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            FinishedBetFragment finishedBetFragment = FinishedBetFragment.newInstance(games[counter], bets[counter]);
            transaction.replace(R.id.bet_payout_container, finishedBetFragment).commit();
            counter++;
        }
        else {
            dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);
    }
    public boolean betWon(String winner, Bet actBet) {
        if (winner.equals(actBet.getTeam())){
            return true;
        }
        return false;
    }
    public String determineWinner(Game actGame) {
        int scoreHome = actGame.getHome_team_score();
        int scoreAway = actGame.getAway_team_score();
        if (scoreHome > scoreAway){
            return actGame.getHome_team();
        } else if (scoreAway > scoreHome){
            return actGame.getAway_team();
        } else {
            return "Draw";
        }
    }
    public int amountToAddOnBalance(Bet bet) {
        double dbResult = (double) bet.getAmount() * bet.getOdd();
        int intResult = (int) dbResult;
        return intResult;
    }
}
