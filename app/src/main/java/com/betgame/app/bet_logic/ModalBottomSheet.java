package com.betgame.app.bet_logic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.betgame.app.R;



public class ModalBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener{
    private BottomSheetListener mListener;
    private SeekBar mSeekBar;
    private EditText mCurrentWage;
    private TextView mOddHomeTeam;
    private TextView mOddAwayTeam;
    private TextView mSelectedTeam;
    private TextView mOddDraw;
    private Button mButtonSubmit;
    private int actualBalanceUser;
    private String[] thisGameOdds;
    private static final String bundleOddsKey = "BUNDLEODDS";
    private static final String bundleBalanceKey = "BUNDLEBALANCE";

    public static ModalBottomSheet newInstance(int currentBalance, String[] odds){
        ModalBottomSheet bottomSheet = new ModalBottomSheet();
        Bundle parentBundle = new Bundle();
        Bundle argumentBundleOdds = new Bundle();
        Bundle argumentBundleBalance = new Bundle();
        argumentBundleOdds.putStringArray("OddsExtra", odds);
        argumentBundleBalance.putInt("BalanceExtra", currentBalance);
        parentBundle.putBundle(bundleOddsKey, argumentBundleOdds);
        parentBundle.putBundle(bundleBalanceKey, argumentBundleBalance);
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

        thisGameOdds = getArguments().getBundle(bundleOddsKey).getStringArray("OddsExtra");
        actualBalanceUser = getArguments().getBundle(bundleBalanceKey).getInt("BalanceExtra");
        mSeekBar.setMax(actualBalanceUser);

        mOddHomeTeam.setText(thisGameOdds[0]);
        mOddAwayTeam.setText(thisGameOdds[1]);
        mOddDraw.setText(thisGameOdds[2]);

        mOddHomeTeam.setOnClickListener(this);
        mOddAwayTeam.setOnClickListener(this);
        mOddDraw.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);

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
            case R.id.buttonSumbit:
                try{
                    if (mSelectedTeam.getText().equals("")){
                        return;
                    }else if(Integer.parseInt(mCurrentWage.getText().toString()) <= 0 ){
                        return;
                    } else {
                        mListener.onSubmitted("clicked");
                    }
                }catch (Exception e){
                }
                break;
        }
    }

    public interface BottomSheetListener {
        void onSubmitted(String text);
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
}
