package com.betgame.perhapps.specific_views;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.betgame.perhapps.Game;
import com.betgame.perhapps.R;
import com.betgame.perhapps.recycler_view_adapters.ActiveBetsAdapter;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.ArrayList;

public class ActiveBets extends AppCompatActivity implements ActiveBetsAdapter.ForecastAdapterOnClickHandler {
    private RecyclerView rv_main;
    private ActiveBetsAdapter mActiveBetsAdapter;
    private ArrayList<Game> games;
    private ArrayList<String> idList;
    private SlidrInterface slidr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explicit_view_active_bets);

        slidr = Slidr.attach(this);
        games = getIntent().getParcelableArrayListExtra("ActiveBets");
        idList = getIntent().getStringArrayListExtra("IdList");
        rv_main = (RecyclerView) findViewById(R.id.act_bets_rv_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.black_rectangle));
        rv_main.setLayoutManager(layoutManager);
        rv_main.setHasFixedSize(true);
        mActiveBetsAdapter = new ActiveBetsAdapter(this);
        rv_main.setAdapter(mActiveBetsAdapter);
        mActiveBetsAdapter.setWeatherData(games, idList, true);

    }

    @Override
    public void onClick(Game gameActual) {

    }
}
