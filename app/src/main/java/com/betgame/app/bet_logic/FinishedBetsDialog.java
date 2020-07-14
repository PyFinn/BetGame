package com.betgame.app.bet_logic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.betgame.app.Bet;
import com.betgame.app.Game;
import com.betgame.app.R;

public class FinishedBetsDialog extends AppCompatDialogFragment implements FinishedBetFragment.ClaimButtonclickedListener{

    private static final String mActualGameKey = "ActualGame";
    private static final String mActualBetKey = "ActualBet";
    private Game[] games;
    private Bet[] bets;
    private static Integer counter = 0;
    private static Integer gamesLength;

    public static FinishedBetsDialog newInstance(Game[] game, Bet[] bet) {
        FinishedBetsDialog finishedBetFragment = new FinishedBetsDialog();
        Bundle parentBundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putParcelableArray(mActualGameKey, game);
        bundle2.putParcelableArray(mActualBetKey, bet);
        parentBundle.putBundle(mActualGameKey, bundle1);
        parentBundle.putBundle(mActualBetKey, bundle2);

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
}
