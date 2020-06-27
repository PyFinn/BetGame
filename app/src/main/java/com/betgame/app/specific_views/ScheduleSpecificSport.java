package com.betgame.app.specific_views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private String[] leagues;

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
        LinearLayoutManager schedule_fragment_layout_manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_explicit_sports_display.setLayoutManager(schedule_fragment_layout_manager);
        rv_explicit_sports_display.setHasFixedSize(true);
        mRvAdapter = new ScheduleFragmentAdapter(this);
        rv_explicit_sports_display.setAdapter(mRvAdapter);

        switch (mSportType){
            case "Soccer":
                leagues = getResources().getStringArray(R.array.football_leagues);
                break;
            case "Basketball":
                leagues = getResources().getStringArray(R.array.basketball_leagues);
                break;
            case "American Football":
                leagues = getResources().getStringArray(R.array.american_football_leagues);
        }
        mRvAdapter.setWeatherData(leagues);
        return myView;
    }

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(getContext(), GamesForSport.class);
        intent.putParcelableArrayListExtra("Extra", mGameArray);
        intent.putExtra("SportType", mSportType);
        intent.putExtra("SelectedLeague", weatherForDay);
        startActivity(intent);
    }
}
