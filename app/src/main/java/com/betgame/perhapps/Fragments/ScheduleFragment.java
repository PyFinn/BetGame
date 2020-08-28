package com.betgame.perhapps.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.betgame.perhapps.Game;
import com.betgame.perhapps.R;
import com.betgame.perhapps.recycler_view_adapters.ScheduleFragmentAdapter;
import com.betgame.perhapps.specific_views.ScheduleSpecificSport;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment implements ScheduleFragmentAdapter.ForecastAdapterOnClickHandler {
    private RecyclerView rv_schedule_fragment;
    private ScheduleFragmentAdapter mScheduleFragmentAdapter;
    private ArrayList<Parcelable> mGameArray;
    private ArrayList<String> mActiveBets;
    private static final String GameArrayKey = "GameArray";
    private static final String ActiveBetsKey = "ActiveBets";
    Fragment LeaguesFragment;

    public static ScheduleFragment newInstance(ArrayList<Game> games, ArrayList<String> activeBets) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ActiveBetsKey ,activeBets);
        bundle.putParcelableArrayList(GameArrayKey, games);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_schedule, container, false);
        mGameArray = getArguments() != null ? getArguments().getParcelableArrayList(GameArrayKey) : null;
        mActiveBets = getArguments() != null ? getArguments().getStringArrayList(ActiveBetsKey) : null;
        rv_schedule_fragment = (RecyclerView) myView.findViewById(R.id.rv_fragment_schedule);
        LinearLayoutManager schedule_fragment_layout_manager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
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
        LeaguesFragment = ScheduleSpecificSport.newInstance(mGameArray, weatherForDay, mActiveBets);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.sv_home_page, LeaguesFragment);
        ft.commitAllowingStateLoss();
    }
}