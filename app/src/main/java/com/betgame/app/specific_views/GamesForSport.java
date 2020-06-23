package com.betgame.app.specific_views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.betgame.app.Game;
import com.betgame.app.R;
import com.betgame.app.bet_logic.ModalBottomSheet;
import com.betgame.app.recycler_view_adapters.GameCardsActivityAdapter;

import java.util.ArrayList;

public class GamesForSport extends AppCompatActivity implements GameCardsActivityAdapter.ForecastAdapterOnClickHandler, ModalBottomSheet.BottomSheetListener {
    private RecyclerView rv_games;
    private GameCardsActivityAdapter mAdapterGames;
    String GameTypeQuery;
    String LeagueTypeQuery;
    private String[] oddsArray;
    ArrayList<Game> games;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        try {
            games = intent.getExtras().getParcelableArrayList("Extra");
        } catch (NullPointerException e){

        }
        GameTypeQuery = intent.getStringExtra("SportType");
        LeagueTypeQuery = intent.getStringExtra("SelectedLeague");
        setContentView(R.layout.explicit_sports_display);
        rv_games = (RecyclerView) findViewById(R.id.rv_explicit_sports_display);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_games.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        rv_games.addItemDecoration(dividerItemDecoration);
        rv_games.setLayoutManager(layoutManager);
        rv_games.setHasFixedSize(true);
        mAdapterGames = new GameCardsActivityAdapter(this);
        rv_games.setAdapter(mAdapterGames);

        mAdapterGames.setWeatherData(games, GameTypeQuery, LeagueTypeQuery);
    }

    @Override
    public void onClick(Game gameActual) {
        oddsArray = new String[3];
        oddsArray[0] = gameActual.getOdd_home_team();
        oddsArray[1] = gameActual.getOdd_away_team();
        if (gameActual.getOdd_draw() != null){
            oddsArray[2] = gameActual.getOdd_draw();
        }else{
            oddsArray[2] = "";
        }
        ModalBottomSheet bottomSheet = ModalBottomSheet.newInstance(2000, oddsArray);
        bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheetCreateBet");
    }

    @Override
    public void onSubmitted(String text) {
        
    }
}
