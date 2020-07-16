package com.betgame.app.bet_logic;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.betgame.app.Bet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.betgame.app.Game;
import com.betgame.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ModalBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener{
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceDatabase;
    private FirebaseUser mUser;
    private TextView mTitlePlaceBet;
    private TextView mTitleConfirmBet;
    private TextView mAmountToBet;
    private TextView mBackButton;
    private TextView mTeamBettedOn;
    private TextView mPossibleRevenue;
    private BottomSheetListener mListener;
    private SeekBar mSeekBar;
    private EditText mCurrentWage;
    private TextView mOddHomeTeam;
    private TextView mOddAwayTeam;
    private TextView mSelectedTeam;
    private TextView mOddDraw;
    private TextView mHomeTeamTitle;
    private TextView mAwayTeamTitle;
    private TextView mDrawTitle;
    private Button mButtonSubmit;
    private Button mConfirmButton;
    private int actualBalanceUser;
    private String[] thisGameOdds;
    private static boolean hasDraw = true;
    private Game game;
    private ValueEventListener mValueEventListener;
    private static final String bundleOddsKey = "BUNDLEODDS";
    private static final String bundleBalanceKey = "BUNDLEBALANCE";
    private static final String bundleGameKey = "BUNDLEGAME";
    private String userID;

    public static ModalBottomSheet newInstance(int currentBalance, String[] odds, Game currentGame){
        ModalBottomSheet bottomSheet = new ModalBottomSheet();
        Bundle parentBundle = new Bundle();
        Bundle currentGameBundle = new Bundle();
        Bundle argumentBundleOdds = new Bundle();
        Bundle argumentBundleBalance = new Bundle();
        currentGameBundle.putParcelable("ThisGame", currentGame);
        argumentBundleOdds.putStringArray("OddsExtra", odds);
        argumentBundleBalance.putInt("BalanceExtra", currentBalance);
        parentBundle.putBundle(bundleOddsKey, argumentBundleOdds);
        parentBundle.putBundle(bundleBalanceKey, argumentBundleBalance);
        parentBundle.putBundle(bundleGameKey, currentGameBundle);
        bottomSheet.setArguments(parentBundle);
        return bottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_bottom_sheet_place_bet, container, false);
        mSeekBar = v.findViewById(R.id.place_new_bet_seek_bar);
        mCurrentWage = v.findViewById(R.id.tv_current_wage);
        mOddHomeTeam = v.findViewById(R.id.home_team_odd_bet_fragment);
        mOddAwayTeam = v.findViewById(R.id.away_team_odd_bet_fragment);
        mOddDraw = v.findViewById(R.id.draw_odd_bet_fragment);
        mButtonSubmit = v.findViewById(R.id.buttonSumbit);
        mConfirmButton = v.findViewById(R.id.buttonFinalConfirm);
        mTitleConfirmBet = v.findViewById(R.id.title_confirm_bet);
        mTitlePlaceBet = v.findViewById(R.id.place_new_bet_title);
        mAmountToBet = v.findViewById(R.id.tv_amount_to_bet);
        mBackButton = v.findViewById(R.id.back_button_bet_fragment);
        mTeamBettedOn = v.findViewById(R.id.team_betted_on);
        mPossibleRevenue = v.findViewById(R.id.possible_revenue);
        mHomeTeamTitle = v.findViewById(R.id.home_team_name_bet_sheet);
        mAwayTeamTitle = v.findViewById(R.id.away_team_name_bet_sheet);
        mDrawTitle = v.findViewById(R.id.draw_name_bet_sheet);

        mDatabase = FirebaseDatabase.getInstance();
        mReferenceDatabase = mDatabase.getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mUser.getUid();
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long longActualBalanceUser = (long) snapshot.getValue();
                actualBalanceUser = (int) longActualBalanceUser;
                mSeekBar.setMax(actualBalanceUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mReferenceDatabase.child("users").child(userID).child("balance").addListenerForSingleValueEvent(mValueEventListener);

        thisGameOdds = getArguments().getBundle(bundleOddsKey).getStringArray("OddsExtra");
        game = getArguments().getBundle(bundleGameKey).getParcelable("ThisGame");
        mSeekBar.setMax(actualBalanceUser);

        mHomeTeamTitle.setText(game.getHome_team());
        mAwayTeamTitle.setText(game.getAway_team());
        mDrawTitle.setText(R.string.draw);
        mOddHomeTeam.setText(thisGameOdds[0]);
        mOddAwayTeam.setText(thisGameOdds[1]);
        if (Double.valueOf(thisGameOdds[2]) != 0){
            mOddDraw.setText(thisGameOdds[2]);
        } else {
            mOddDraw.setVisibility(View.INVISIBLE);
            mOddDraw.setEnabled(false);
            mDrawTitle.setVisibility(View.INVISIBLE);
            hasDraw = false;
        }

        mOddHomeTeam.setOnClickListener(this);
        mOddAwayTeam.setOnClickListener(this);
        mOddDraw.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    progress = progress / 10;
                    progress = progress * 10;
                    mCurrentWage.setText(String.valueOf(progress));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCurrentWage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String typedIn = String.valueOf(s);
                try {
                    int mTypedAmount = Integer.parseInt(typedIn);
                    if (mTypedAmount > actualBalanceUser){
                        mCurrentWage.setText(String.valueOf(actualBalanceUser));
                        mCurrentWage.clearFocus();
                    }
                    mSeekBar.setProgress(Integer.parseInt(String.valueOf(mCurrentWage.getText())));
                    if (mCurrentWage.length() > String.valueOf(actualBalanceUser).length()){
                        mCurrentWage.getText().clear();
                    }
                } catch (Exception e){
                    mCurrentWage.getText().clear();
                }
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mReferenceDatabase.child("users").child(userID).child("balance").removeEventListener(mValueEventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_team_odd_bet_fragment:
                mOddHomeTeam.setBackgroundColor(getResources().getColor(R.color.oddBackground));
                mOddAwayTeam.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_bet_border));
                mOddDraw.setBackgroundDrawable(getResources().getDrawable(R.drawable.draw_bet_border));
                mSelectedTeam = mOddHomeTeam;
                break;
            case R.id.away_team_odd_bet_fragment:
                mOddAwayTeam.setBackgroundColor(getResources().getColor(R.color.oddBackground));
                mOddHomeTeam.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_bet_border));
                mOddDraw.setBackgroundDrawable(getResources().getDrawable(R.drawable.draw_bet_border));
                mSelectedTeam = mOddAwayTeam;
                break;
            case R.id.draw_odd_bet_fragment:
                mOddDraw.setBackgroundColor(getResources().getColor(R.color.oddDrawBackground));
                mOddAwayTeam.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_bet_border));
                mOddHomeTeam.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_bet_border));
                mSelectedTeam = mOddDraw;
                break;
            case R.id.back_button_bet_fragment:
                fragmentOneVisible(hasDraw);
                break;
            case R.id.buttonFinalConfirm:
                Bet bet = new Bet();
                bet.setId(game.getId());
                bet.setAmount(Integer.parseInt(mCurrentWage.getText().toString()));
                bet.setOdd(oddsForBet(bettedOn(mSelectedTeam), game));
                bet.setTeam(nameForBettedOn(bettedOn(mSelectedTeam), game));
                mListener.onSubmitted(bet, actualBalanceUser);
                dismiss();
                Toast.makeText(getContext(), R.string.bet_placed, Toast.LENGTH_SHORT).show();
            case R.id.buttonSumbit:
                try{
                    if (mSelectedTeam.getText().equals("")){
                        return;
                    }else if(Integer.parseInt(mCurrentWage.getText().toString()) <= 0 ){
                        return;
                    } else {
                        fragmentTwoVisible();
                        mTeamBettedOn.setText("You are about to bet on " + nameForBettedOn(bettedOn(mSelectedTeam), game) + " wins.");
                        mPossibleRevenue.setText("Possible Revenue: " + String.valueOf(Math.round(Integer.valueOf(mCurrentWage.getText().toString()) * Double.valueOf(mSelectedTeam.getText().toString()))) + "$");
                        break;
                    }
                }catch (Exception e){
                }
                break;
        }
    }

    public interface BottomSheetListener {
        void onSubmitted(Bet bet, int balance);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public void fragmentOneVisible(boolean hasDraw){
        mTitlePlaceBet.setVisibility(View.VISIBLE);
        mOddHomeTeam.setVisibility(View.VISIBLE);
        mOddHomeTeam.setEnabled(true);
        if (hasDraw){
            mOddDraw.setVisibility(View.VISIBLE);
            mOddDraw.setEnabled(true);
            mDrawTitle.setVisibility(View.VISIBLE);
            mDrawTitle.setEnabled(true);
        }
        mOddAwayTeam.setVisibility(View.VISIBLE);
        mOddAwayTeam.setEnabled(true);
        mSeekBar.setVisibility(View.VISIBLE);
        mSeekBar.setEnabled(true);
        mButtonSubmit.setVisibility(View.VISIBLE);
        mButtonSubmit.setEnabled(true);
        mCurrentWage.setVisibility(View.VISIBLE);
        mCurrentWage.setEnabled(true);
        mAmountToBet.setVisibility(View.VISIBLE);
        mAmountToBet.setEnabled(true);
        mHomeTeamTitle.setVisibility(View.VISIBLE);
        mHomeTeamTitle.setEnabled(true);
        mAwayTeamTitle.setVisibility(View.VISIBLE);
        mAwayTeamTitle.setEnabled(true);

        mBackButton.setVisibility(View.INVISIBLE);
        mBackButton.setEnabled(false);
        mTitleConfirmBet.setVisibility(View.INVISIBLE);
        mTitleConfirmBet.setEnabled(false);
        mConfirmButton.setVisibility(View.INVISIBLE);
        mConfirmButton.setEnabled(false);
        mPossibleRevenue.setVisibility(View.INVISIBLE);
        mPossibleRevenue.setEnabled(false);
        mTeamBettedOn.setVisibility(View.INVISIBLE);
        mTeamBettedOn.setEnabled(false);
    }

    public void fragmentTwoVisible(){
        mTitlePlaceBet.setVisibility(View.INVISIBLE);
        mOddHomeTeam.setVisibility(View.INVISIBLE);
        mOddHomeTeam.setEnabled(false);
        mOddDraw.setVisibility(View.INVISIBLE);
        mOddDraw.setEnabled(false);
        mOddAwayTeam.setVisibility(View.INVISIBLE);
        mOddAwayTeam.setEnabled(false);
        mSeekBar.setVisibility(View.INVISIBLE);
        mSeekBar.setEnabled(false);
        mButtonSubmit.setVisibility(View.INVISIBLE);
        mButtonSubmit.setEnabled(false);
        mCurrentWage.setVisibility(View.INVISIBLE);
        mCurrentWage.setEnabled(false);
        mAmountToBet.setVisibility(View.INVISIBLE);
        mAmountToBet.setEnabled(false);
        mHomeTeamTitle.setVisibility(View.INVISIBLE);
        mAwayTeamTitle.setVisibility(View.INVISIBLE);
        mDrawTitle.setVisibility(View.INVISIBLE);

        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.setEnabled(true);
        mTitleConfirmBet.setVisibility(View.VISIBLE);
        mTitleConfirmBet.setEnabled(true);
        mConfirmButton.setVisibility(View.VISIBLE);
        mConfirmButton.setEnabled(true);
        mPossibleRevenue.setVisibility(View.VISIBLE);
        mPossibleRevenue.setEnabled(true);
        mTeamBettedOn.setVisibility(View.VISIBLE);
        mTeamBettedOn.setEnabled(true);
    }

    private String bettedOn(TextView tv) {
        switch (tv.getId()){
            case R.id.home_team_odd_bet_fragment:
                return "Home Team";
            case R.id.away_team_odd_bet_fragment:
                return "Away Team";
            case R.id.draw_odd_bet_fragment:
                return "Draw";
            default:
                return null;
        }
    }

    private double oddsForBet(String bettedOn, Game game) {
        switch (bettedOn){
            case "Home Team":
                return Double.parseDouble(game.getOdd_home_team());
            case "Away Team":
                return Double.parseDouble(game.getOdd_away_team());
            case "Draw":
                return Double.parseDouble(game.getOdd_draw());
            default:
                return 0;
        }
    }

    private String nameForBettedOn(String bettedOn, Game game){
        switch (bettedOn){
            case "Home Team":
                return game.getHome_team();
            case "Away Team":
                return game.getAway_team();
            case "Draw":
                return "Draw";
            default:
                return "Unregular Bet";
        }
    }
}
