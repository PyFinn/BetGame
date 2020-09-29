package com.betgame.perhapps.specific_views;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.betgame.perhapps.Fragments.ScheduleFragment;
import com.betgame.perhapps.Game;
import com.betgame.perhapps.R;
import com.betgame.perhapps.recycler_view_adapters.ScheduleFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ScheduleSpecificSport extends Fragment implements ScheduleFragmentAdapter.ForecastAdapterOnClickHandler{
    private RecyclerView rv_explicit_sports_display;
    private ScheduleFragmentAdapter mRvAdapter;
    private static final String GameArrayKey = "GameArray";
    private static final String StringKey = "SelectedSport";
    private static final String ActiveBetsKey = "ActiveBets";
    private ArrayList<String> mActiveBets;
    private ArrayList<Parcelable> mGameArray;
    private String mSportType;
    private Map<String, Integer> mSportTypes;

    public static ScheduleSpecificSport newInstance(ArrayList<Parcelable> games, String selectedSports, ArrayList<String> activeBets) {
        ScheduleSpecificSport fragment = new ScheduleSpecificSport();
        Bundle initiativeBundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        Bundle bundle3 = new Bundle();
        bundle1.putParcelableArrayList(GameArrayKey, games);
        bundle2.putString(StringKey, selectedSports);
        bundle3.putStringArrayList(ActiveBetsKey, activeBets);
        initiativeBundle.putBundle("BundleGameArray", bundle1);
        initiativeBundle.putBundle("BundleSportsName", bundle2);
        initiativeBundle.putBundle("BundleActiveBets", bundle3 );
        fragment.setArguments(initiativeBundle);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.explicit_sports_display, container, false);
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) myView.findViewById(R.id.toolbar_back);
        toolbar.setVisibility(View.GONE);
        toolbar.setEnabled(false);
        mGameArray = getArguments() != null ? getArguments().getBundle("BundleGameArray").getParcelableArrayList(GameArrayKey) : null;
        mSportType = getArguments() != null ? getArguments().getBundle("BundleSportsName").getString(StringKey) : null;
        mActiveBets = getArguments() != null ? getArguments().getBundle("BundleActiveBets").getStringArrayList(ActiveBetsKey) : null;
        rv_explicit_sports_display = (RecyclerView) myView.findViewById(R.id.rv_explicit_sports_display);
        LinearLayoutManager schedule_fragment_layout_manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_explicit_sports_display.setLayoutManager(schedule_fragment_layout_manager);
        rv_explicit_sports_display.setHasFixedSize(true);
        mRvAdapter = new ScheduleFragmentAdapter(this);
        rv_explicit_sports_display.setAdapter(mRvAdapter);


        mSportTypes = new HashMap<>();

        if (mGameArray != null) {
            for (Parcelable game : mGameArray) {
                Game thisGame = (Game) game;
                String gameType = thisGame.getLeague();
                if (!thisGame.getStarted()) {
                    if (thisGame.getSports().equals(mSportType)) {
                        if (!ScheduleFragment.isAlreadyInList(gameType, mSportTypes)) {
                            mSportTypes.put(gameType, 0);
                        }
                        Integer newNum = 0;
                        try {
                            newNum = mSportTypes.get(gameType) + 1;
                        } catch (Exception e) {
                        }
                        mSportTypes.put(gameType, newNum);
                    }
                }
            }
        }

        mRvAdapter.setWeatherData(mSportTypes);
        return myView;
    }

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(getContext(), GamesForSport.class);
        intent.putParcelableArrayListExtra("Extra", mGameArray);
        intent.putExtra("SportType", mSportType);
        intent.putExtra("SelectedLeague", weatherForDay);
        intent.putExtra("ActiveBets", mActiveBets);
        startActivity(intent);
    }
}
