package com.betgame.app.specific_views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.betgame.app.Fragments.ScheduleFragment;
import com.betgame.app.Game;
import com.betgame.app.R;
import com.betgame.app.recycler_view_adapters.ScheduleFragmentAdapter;

import java.util.ArrayList;


public class ScheduleSpecificSport extends Fragment implements ScheduleFragmentAdapter.ForecastAdapterOnClickHandler{
    private RecyclerView rv_explicit_sports_display;
    private ScheduleFragmentAdapter mRvAdapter;
    private static final String GameArrayKey = "GameArray";
    private static final String StringKey = "SelectedSport";
    private ArrayList<Parcelable> mGameArray;
    private String mSportType;

    public static ScheduleSpecificSport newInstance(ArrayList<Parcelable> games, String selectedSports) {
        ScheduleSpecificSport fragment = new ScheduleSpecificSport();
        Bundle initiativeBundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle1.putParcelableArrayList(GameArrayKey, games);
        bundle2.putString(StringKey, selectedSports);
        initiativeBundle.putBundle("BundleGameArray", bundle1);
        initiativeBundle.putBundle("BundleSportsName", bundle2);
        fragment.setArguments(initiativeBundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.explicit_sports_display, container, false);
        mGameArray = getArguments() != null ? getArguments().getBundle("BundleGameArray").getParcelableArrayList(GameArrayKey) : null;
        mSportType = getArguments() != null ? getArguments().getBundle("BundleSportsName").getString(StringKey) : null;
        rv_explicit_sports_display = (RecyclerView) myView.findViewById(R.id.rv_explicit_sports_display);
        LinearLayoutManager schedule_fragment_layout_manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_explicit_sports_display.setLayoutManager(schedule_fragment_layout_manager);
        rv_explicit_sports_display.setHasFixedSize(true);
        mRvAdapter = new ScheduleFragmentAdapter(this);
        rv_explicit_sports_display.setAdapter(mRvAdapter);
        String[] strings = {
                "Bundesliga",
                "Premier League",
                "La Liga",
                "Ligue 1",
                "Swiss League",
                "Liga NOS",
                "Serie A"
        };
        mRvAdapter.setWeatherData(strings);

        return myView;
    }

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(getContext(), GamesForSport.class);
        intent.putParcelableArrayListExtra("Extra", mGameArray);
        intent.putExtra("SportType", mSportType);
        startActivity(intent);
    }
}
