package com.betgame.perhapps.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.betgame.perhapps.Bet;
import com.betgame.perhapps.Game;
import com.betgame.perhapps.R;
import com.betgame.perhapps.bet_logic.FinishedBetsDialog;
import com.betgame.perhapps.bet_logic.ModalBottomSheet;
import com.betgame.perhapps.gesture_detection.OnSwipeTouchListener;
import com.betgame.perhapps.recycler_view_adapters.ActiveBetsAdapter;
import com.betgame.perhapps.recycler_view_adapters.UpcomingGamesAdapter;
import com.betgame.perhapps.specific_views.ActiveBets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView rv_active_bets;
    private RecyclerView rv_upcoming_games;
    private ActiveBetsAdapter mActiveBetsAdapter;
    private UpcomingGamesAdapter mUpcomingGamesAdapter;
    private static final String GameArrayKey = "GameArray";
    private static final String ActiveBetsKey = "ActiveBets";
    private static final String DateMSKey = "dateMS";
    private static final String FinishedGamesKey = "FinishedGames";
    private ArrayList<Bet> mActiveBets;
    private ArrayList<Game> mGameArray;
    private ArrayList<String> mActiveBetsString = new ArrayList<>();
    private long[] dateMSList;
    private TextView mBalanceDisplay;
    private int mBalance = 0;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mBalanceEventListener;
    private ArrayList<Game> mFinishedGameArray;
    private ArrayList<Game> mActveFinishedGameArray = new ArrayList<>();
    private ArrayList<Bet> mActiveFinsihedGameArray2 = new ArrayList<>();
    private ProgressBar mProgressActiveBets;
    private TextView mNoBetsPlacedTextView;
    private TextView mSeeAllTextView;
    private boolean claimedAllRewards = false;

    public static HomeFragment newInstance(ArrayList<Game> games, ArrayList<Bet> activeBets, long[] dateMSList, ArrayList<Game> finishedGames) {
        HomeFragment fragment = new HomeFragment();
        Bundle parentBundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        Bundle bundle3 = new Bundle();
        Bundle bundle4 = new Bundle();
        bundle1.putParcelableArrayList(GameArrayKey, games);
        bundle2.putParcelableArrayList(ActiveBetsKey, activeBets);
        bundle3.putLongArray(DateMSKey, dateMSList);
        bundle4.putParcelableArrayList(FinishedGamesKey, finishedGames);

        parentBundle.putBundle(GameArrayKey,bundle1);
        parentBundle.putBundle(ActiveBetsKey, bundle2);
        parentBundle.putBundle(DateMSKey, bundle3);
        parentBundle.putBundle(FinishedGamesKey, bundle4);
        fragment.setArguments(parentBundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        mDatabase = FirebaseDatabase.getInstance();
        mBalanceEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long dataSnap = 0;
                try {
                    dataSnap = (long) snapshot.getValue();
                } catch (Exception e) {

                }
                mBalance = (int) dataSnap;
                mBalanceDisplay.setText(String.valueOf(mBalance));
                mProgressActiveBets.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        OnSwipeTouchListener tListener = new OnSwipeTouchListener(getContext()) {
            public void onSwipeTop() {
                Toast.makeText(getContext(), "Top", Toast.LENGTH_LONG).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(getContext(), "Left", Toast.LENGTH_LONG).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(getContext(), "Bottom", Toast.LENGTH_LONG).show();
            }
            public void onSwipeRight() {
                Toast.makeText(getContext(), "Right", Toast.LENGTH_LONG).show();
            }
            };

        myView.setOnTouchListener(tListener);

        try {
            mDatabaseReference = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
            mDatabaseReference.addListenerForSingleValueEvent(mBalanceEventListener);
        } catch (NullPointerException e) {
            Toast.makeText(getContext(), "You are currently not signed in.", Toast.LENGTH_LONG);
        }
        final Fragment actFrag = this;

        if (getArguments() != null)
            mGameArray = getArguments().getBundle(GameArrayKey).getParcelableArrayList(GameArrayKey);
        else mGameArray = null;
        mActiveBets = getArguments() != null ? getArguments().getBundle(ActiveBetsKey).<Bet>getParcelableArrayList(ActiveBetsKey) : null;
        dateMSList = getArguments() != null ? getArguments().getBundle(DateMSKey).getLongArray(DateMSKey) : null;
        for (Bet bet : mActiveBets){
            mActiveBetsString.add(bet.getId());
        }

        mFinishedGameArray = getArguments() != null ? getArguments().getBundle(FinishedGamesKey).<Game>getParcelableArrayList(FinishedGamesKey) : null;
        if (mFinishedGameArray != null && mFinishedGameArray.size() != 0 && mActiveBets != null && !claimedAllRewards){
            for (Game game : mFinishedGameArray){
                for (Bet activeGame : mActiveBets){
                    if (game.getId().equals(activeGame.getId())){
                        mActveFinishedGameArray.add(game);
                        mActiveFinsihedGameArray2.add(activeGame);
                    }
                }
            }
        }
        if (!claimedAllRewards && mActveFinishedGameArray != null){
            Game[] gamesToPass = mActveFinishedGameArray.toArray(new Game[mActveFinishedGameArray.size()]);
            Bet[] betToPass = mActiveFinsihedGameArray2.toArray(new Bet[mActiveFinsihedGameArray2.size()]);
            FinishedBetsDialog finishedBetsDialog = FinishedBetsDialog.newInstance(gamesToPass, betToPass, mBalance);
            if (getFragmentManager() != null){
                finishedBetsDialog.setTargetFragment(actFrag, 1);
                finishedBetsDialog.show(getFragmentManager(), "Finished Bet Dialog");
            }
        }

        mBalanceDisplay = (TextView) myView.findViewById(R.id.home_balance_display);
        mSeeAllTextView = (TextView) myView.findViewById(R.id.tv_see_all_home_fragment);

        // First RecyclerView ActiveBets

        rv_active_bets = (RecyclerView) myView.findViewById(R.id.rv_active_bets);
        mProgressActiveBets = (ProgressBar) myView.findViewById(R.id.progress_bar_active_bets);
        mNoBetsPlacedTextView = (TextView) myView.findViewById(R.id.no_bets_placed);
        LinearLayoutManager active_bets_layout_manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_active_bets.getContext(), active_bets_layout_manager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.black_rectangle));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        rv_active_bets.getLayoutParams().height = displayMetrics.heightPixels / 3;
        rv_active_bets.setLayoutManager(active_bets_layout_manager);
        rv_active_bets.setHasFixedSize(true);
        mActiveBetsAdapter = new ActiveBetsAdapter(new ActiveBetsAdapter.ForecastAdapterOnClickHandler() {
            @Override
            public void onClick(Game gameActual) {
                Intent intent_active_bets = new Intent(getActivity(), ActiveBets.class);
                intent_active_bets.putParcelableArrayListExtra("ActiveBets", mGameArray);
                intent_active_bets.putStringArrayListExtra("IdList", mActiveBetsString);
                startActivity(intent_active_bets);
            }
        });
        mSeeAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_active_bets2 = new Intent(getActivity(), ActiveBets.class);
                intent_active_bets2.putParcelableArrayListExtra("ActiveBets", mGameArray);
                intent_active_bets2.putStringArrayListExtra("IdList", mActiveBetsString);
                startActivity(intent_active_bets2);
            }
        });
        rv_active_bets.addItemDecoration(dividerItemDecoration);
        rv_active_bets.setAdapter(mActiveBetsAdapter);
        if (!claimedAllRewards){
            mActiveBetsAdapter.setWeatherData(mGameArray, mActiveBetsString, false);
        }
        int itemCount = mActiveBetsAdapter.getItemCount();
        if (itemCount > 0){
            mNoBetsPlacedTextView.setVisibility(View.INVISIBLE);
            rv_active_bets.setVisibility(View.VISIBLE);
        }
        // Second RecyclerView UpcomingGames

        rv_upcoming_games = (RecyclerView) myView.findViewById(R.id.rv_upcoming_games);
        LinearLayoutManager upcoming_games_layout_manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(rv_upcoming_games.getContext(), active_bets_layout_manager.getOrientation());
        dividerItemDecoration1.setDrawable(getResources().getDrawable(R.drawable.black_rectangle));
        rv_upcoming_games.setLayoutManager(upcoming_games_layout_manager);
        rv_upcoming_games.setHasFixedSize(true);
        rv_upcoming_games.addItemDecoration(dividerItemDecoration1);
        mUpcomingGamesAdapter = new UpcomingGamesAdapter(new UpcomingGamesAdapter.ForecastAdapterOnClickHandler() {
            @Override
            public void onClick(Game gameActual) {
                String[] oddsArray = new String[3];
                oddsArray[0] = gameActual.getOdd_home_team();
                oddsArray[1] = gameActual.getOdd_away_team();
                if (Double.valueOf(gameActual.getOdd_draw()) != 0){
                    oddsArray[2] = gameActual.getOdd_draw();
                }else{
                    oddsArray[2] = "0";
                }
                ModalBottomSheet bottomSheet = ModalBottomSheet.newInstance(oddsArray, gameActual);
                bottomSheet.show(getFragmentManager(), "ModalBottomSheetCreateBet");
            }
        });
        rv_upcoming_games.setAdapter(mUpcomingGamesAdapter);
        mUpcomingGamesAdapter.setWeatherData(mGameArray, dateMSList);

        CardView cv_active_bets = (CardView) myView.findViewById(R.id.cv_active_bets);
        CardView cv_upcoming_matches = (CardView) myView.findViewById(R.id.cv_upcoming_games);
        cv_active_bets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_active_bets2 = new Intent(getActivity(), ActiveBets.class);
                startActivity(intent_active_bets2);
            }
        });
        return myView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mDatabaseReference.removeEventListener(mBalanceEventListener);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1){
            claimedAllRewards = true;
            getActivity().getSupportFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
        }
    }
}
