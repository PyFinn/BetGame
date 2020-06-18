package com.betgame.app.specific_views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.betgame.app.Game;
import com.betgame.app.R;
import com.betgame.app.recycler_view_adapters.GameCardsActivityAdapter;

import java.util.ArrayList;

public class GamesForSport extends AppCompatActivity implements GameCardsActivityAdapter.ForecastAdapterOnClickHandler{
    private RecyclerView rv_games;
    private GameCardsActivityAdapter mAdapterGames;
    ArrayList<Game> games;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        games = intent.getExtras().getParcelableArrayList("Extra");
        Log.e("M", games.toString());
        setContentView(R.layout.explicit_sports_display);
        rv_games = (RecyclerView) findViewById(R.id.rv_explicit_sports_display);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_games.setLayoutManager(layoutManager);
        rv_games.setHasFixedSize(true);
        mAdapterGames = new GameCardsActivityAdapter(this);
        rv_games.setAdapter(mAdapterGames);
        String[] strings = {
                "Bundesliga",
                "Premier League",
                "Liga BBVA",
                "Ligue 1",
                "ÖPN",
                "Alaskan",
                "MLS"
        };
//        mAdapterGames.setWeatherData(games);
    }

    @Override
    public void onClick(Game gameActual) {

    }
}
