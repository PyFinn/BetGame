package com.betgame.app.Fragments;

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

import com.betgame.app.Game;
import com.betgame.app.R;
import com.betgame.app.recycler_view_adapters.ScheduleFragmentAdapter;
import com.betgame.app.specific_views.GamesForSport;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment implements ScheduleFragmentAdapter.ForecastAdapterOnClickHandler {
    private RecyclerView rv_schedule_fragment;
    private ScheduleFragmentAdapter mScheduleFragmentAdapter;
    private ArrayList<Parcelable> mGameArray;
    private static final String GameArrayKey = "GameArray";

    public static ScheduleFragment newInstance(ArrayList<Game> games) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(GameArrayKey, games);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_schedule, container, false);
        mGameArray = getArguments() != null ? getArguments().getParcelableArrayList(GameArrayKey) : null;
        rv_schedule_fragment = (RecyclerView) myView.findViewById(R.id.rv_fragment_schedule);
        LinearLayoutManager schedule_fragment_layout_manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_schedule_fragment.setLayoutManager(schedule_fragment_layout_manager);
        rv_schedule_fragment.setHasFixedSize(true);
        mScheduleFragmentAdapter = new ScheduleFragmentAdapter(this);
        rv_schedule_fragment.setAdapter(mScheduleFragmentAdapter);
        String[] strings = {
                "Soccer",
                "Basketball",
                "American Football",
                "Tennis",
                "Volleyball",
                "Baseball",
                "Icehockey"
        };
        mScheduleFragmentAdapter.setWeatherData(strings);

        return myView;
    }

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(getContext(), GamesForSport.class);
        intent.putParcelableArrayListExtra("Extra", mGameArray);
        startActivity(intent);
    }
}