package com.betgame.app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.betgame.app.Game;
import com.betgame.app.R;
import com.betgame.app.recycler_view_adapters.ActiveBetsAdapter;
import com.betgame.app.specific_views.ActiveBets;
import com.betgame.app.specific_views.UpcomingGames;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener, ActiveBetsAdapter.ForecastAdapterOnClickHandler {
    private RecyclerView rv_active_bets;
    private ActiveBetsAdapter mActiveBetsAdapter;
    private static final String GameArrayKey = "GameArray";
    private ArrayList<Parcelable> mGameArray;


    public static HomeFragment newInstance(ArrayList<Game> games) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(GameArrayKey, games);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        mGameArray = getArguments() != null ? getArguments().getParcelableArrayList(GameArrayKey) : null;
        rv_active_bets = (RecyclerView) myView.findViewById(R.id.rv_active_bets);
        LinearLayoutManager active_bets_layout_manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        rv_active_bets.setLayoutManager(active_bets_layout_manager);
        rv_active_bets.setHasFixedSize(true);
        mActiveBetsAdapter = new ActiveBetsAdapter(this);
        rv_active_bets.setAdapter(mActiveBetsAdapter);
        String[] strings = {
                "Was geht ab",
                "Hola",
                "Moin",
                "Liverpool",
                "Arsenal",
                "ManU"
        };
        mActiveBetsAdapter.setWeatherData(strings);


        CardView cv_active_bets = (CardView) myView.findViewById(R.id.cv_active_bets);
        CardView cv_upcoming_matches = (CardView) myView.findViewById(R.id.cv_upcoming_games);
        cv_upcoming_matches.setOnClickListener(this);
        cv_active_bets.setOnClickListener(this);
        return myView;
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
    public void onClick(String weatherForDay) {
        Toast.makeText(getContext(), weatherForDay, Toast.LENGTH_SHORT)
                .show();
    }
}
