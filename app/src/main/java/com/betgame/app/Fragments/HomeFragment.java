package com.betgame.app.Fragments;

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

import com.betgame.app.Bet;
import com.betgame.app.Game;
import com.betgame.app.R;
import com.betgame.app.bet_logic.FinishedBetsDialog;
import com.betgame.app.recycler_view_adapters.ActiveBetsAdapter;
import com.betgame.app.recycler_view_adapters.UpcomingGamesAdapter;
import com.betgame.app.specific_views.ActiveBets;
import com.betgame.app.specific_views.UpcomingGames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

        public class HomeFragment extends Fragment implements View.OnClickListener, ActiveBetsAdapter.ForecastAdapterOnClickHandler, UpcomingGamesAdapter.ForecastAdapterOnClickHandler {
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
        mDatabaseReference = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
        mBalanceEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long dataSnap = (long) snapshot.getValue();
                mBalance = (int) dataSnap;
                mBalanceDisplay.setText(String.valueOf(mBalance));
                mProgressActiveBets.setVisibility(View.INVISIBLE);
                mFinishedGameArray = getArguments() != null ? getArguments().getBundle(FinishedGamesKey).<Game>getParcelableArrayList(FinishedGamesKey) : null;
                if (mFinishedGameArray != null){
                    for (Game game : mFinishedGameArray){
                        for (Bet activeGame : mActiveBets){
                            if (game.getId().equals(activeGame.getId())){
                                mActveFinishedGameArray.add(game);
                                mActiveFinsihedGameArray2.add(activeGame);
                            }
                        }
                    }
                }
                if (mActveFinishedGameArray != null){
                    Game[] gamesToPass = mActveFinishedGameArray.toArray(new Game[mActveFinishedGameArray.size()]);
                    Bet[] betToPass = mActiveFinsihedGameArray2.toArray(new Bet[mActiveFinsihedGameArray2.size()]);
                    FinishedBetsDialog finishedBetsDialog = FinishedBetsDialog.newInstance(gamesToPass, betToPass, mBalance);
//                    finishedBetsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            getActivity().getSupportFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
//                        }
//                    });
                    if (getFragmentManager() != null){
                        finishedBetsDialog.show(getFragmentManager(), "Finished Bet Dialog");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addListenerForSingleValueEvent(mBalanceEventListener);

        if (getArguments() != null)
            mGameArray = getArguments().getBundle(GameArrayKey).getParcelableArrayList(GameArrayKey);
        else mGameArray = null;
        mActiveBets = getArguments() != null ? getArguments().getBundle(ActiveBetsKey).<Bet>getParcelableArrayList(ActiveBetsKey) : null;
        dateMSList = getArguments() != null ? getArguments().getBundle(DateMSKey).getLongArray(DateMSKey) : null;
        for (Bet bet : mActiveBets){
            mActiveBetsString.add(bet.getId());
        }

        mBalanceDisplay = (TextView) myView.findViewById(R.id.home_balance_display);

        // First RecyclerView ActiveBets

        rv_active_bets = (RecyclerView) myView.findViewById(R.id.rv_active_bets);
        mProgressActiveBets = (ProgressBar) myView.findViewById(R.id.progress_bar_active_bets);
        mNoBetsPlacedTextView = (TextView) myView.findViewById(R.id.no_bets_placed);
        LinearLayoutManager active_bets_layout_manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_active_bets.getContext(), active_bets_layout_manager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.black_rectangle));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceheight = displayMetrics.heightPixels / 3;
        rv_active_bets.getLayoutParams().height = deviceheight;
        rv_active_bets.setLayoutManager(active_bets_layout_manager);
        rv_active_bets.setHasFixedSize(true);
        mActiveBetsAdapter = new ActiveBetsAdapter(this);
        rv_active_bets.addItemDecoration(dividerItemDecoration);
        rv_active_bets.setAdapter(mActiveBetsAdapter);
        mActiveBetsAdapter.setWeatherData(mGameArray, mActiveBetsString);
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
        mUpcomingGamesAdapter = new UpcomingGamesAdapter(this);
        rv_upcoming_games.setAdapter(mUpcomingGamesAdapter);
        mUpcomingGamesAdapter.setWeatherData(mGameArray, dateMSList);

        CardView cv_active_bets = (CardView) myView.findViewById(R.id.cv_active_bets);
        CardView cv_upcoming_matches = (CardView) myView.findViewById(R.id.cv_upcoming_games);
        cv_upcoming_matches.setOnClickListener(this);
        cv_active_bets.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDatabaseReference.removeEventListener(mBalanceEventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_active_bets:
                Intent intent_active_bets = new Intent(getActivity(), ActiveBets.class);
                startActivity(intent_active_bets);
                break;
            case R.id.cv_upcoming_games:
                Intent intent_upcoming_games = new Intent(getActivity(), UpcomingGames.class);
                startActivity(intent_upcoming_games);
                break;
            case R.id.tv_see_all_home_fragment:
                Intent intent_active_bets2 = new Intent(getActivity(), ActiveBets.class);
                startActivity(intent_active_bets2);
        }
    }

    @Override
    public void onClick(Game gameActual) {
        boolean isActive = false;
        for (int i = 0;i < mActiveBets.size();i++){
            if (gameActual.getId().equals(mActiveBets.get(i))){
                isActive = true;
            }
        }
        if (isActive){
            Intent intent = new Intent(getContext(), ActiveBets.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), UpcomingGames.class);
            startActivity(intent);
        }
    }
}
