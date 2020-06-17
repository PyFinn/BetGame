package com.betgame.app.specific_views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.betgame.app.R;
import com.betgame.app.recycler_view_adapters.ScheduleFragmentAdapter;

public class ScheduleSpecificSport extends AppCompatActivity implements ScheduleFragmentAdapter.ForecastAdapterOnClickHandler{
    private RecyclerView rv_explicit_sports_display;
    private ScheduleFragmentAdapter mRvAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explicit_sports_display);
        rv_explicit_sports_display = (RecyclerView) findViewById(R.id.rv_explicit_sports_display);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_explicit_sports_display.setLayoutManager(layoutManager);
        rv_explicit_sports_display.setHasFixedSize(true);
        mRvAdapter = new ScheduleFragmentAdapter(this);
        rv_explicit_sports_display.setAdapter(mRvAdapter);
        String[] strings = {
                "Bundesliga",
                "Premier League",
                "Liga BBVA",
                "Ligue 1",
                "ÖPN",
                "Alaskan",
                "MLS"
        };
        mRvAdapter.setWeatherData(strings);
    }

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(this, GamesForSport.class);
        startActivity(intent);
    }
}
