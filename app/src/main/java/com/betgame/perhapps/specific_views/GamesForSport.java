package com.betgame.perhapps.specific_views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.betgame.perhapps.Bet;
import com.betgame.perhapps.Game;
import com.betgame.perhapps.R;
import com.betgame.perhapps.bet_logic.ModalBottomSheet;
import com.betgame.perhapps.recycler_view_adapters.GameCardsActivityAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.ArrayList;

public class GamesForSport extends AppCompatActivity implements GameCardsActivityAdapter.ForecastAdapterOnClickHandler, ModalBottomSheet.BottomSheetListener {
    private RecyclerView rv_games;
    private GameCardsActivityAdapter mAdapterGames;
    String GameTypeQuery;
    String LeagueTypeQuery;
    private String[] oddsArray;
    ArrayList<Game> games;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceActiveBets;
    private ArrayList<String> mActiveBets;
    private TextView mNoGamesTextView;
    private SlidrInterface slidr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidr = Slidr.attach(this);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
        mDatabaseReferenceActiveBets = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("active_bets");


        Intent intent = getIntent();
        try {
            games = intent.getExtras().getParcelableArrayList("Extra");
        } catch (NullPointerException e){

        }
        GameTypeQuery = intent.getStringExtra("SportType");
        LeagueTypeQuery = intent.getStringExtra("SelectedLeague");
        mActiveBets = intent.getStringArrayListExtra("ActiveBets");
        setContentView(R.layout.explicit_sports_display);
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        ImageView iv_go_back = (ImageView) toolbar.findViewById(R.id.iv_go_back);
        rv_games = (RecyclerView) findViewById(R.id.rv_explicit_sports_display);
        mNoGamesTextView = (TextView) findViewById(R.id.no_games_tv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_games.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rectangle));
        rv_games.addItemDecoration(dividerItemDecoration);
        rv_games.setLayoutManager(layoutManager);
        rv_games.setHasFixedSize(true);
        mAdapterGames = new GameCardsActivityAdapter(this);
        rv_games.setAdapter(mAdapterGames);
        iv_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAdapterGames.setWeatherData(games, GameTypeQuery, LeagueTypeQuery);
        if (mAdapterGames.getItemCount() == 0){
            mNoGamesTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(Game gameActual) {
        oddsArray = new String[3];
        oddsArray[0] = gameActual.getOdd_home_team();
        oddsArray[1] = gameActual.getOdd_away_team();
        if (Double.valueOf(gameActual.getOdd_draw()) != 0){
            oddsArray[2] = gameActual.getOdd_draw();
        }else{
            oddsArray[2] = "0";
        }
        ModalBottomSheet bottomSheet = ModalBottomSheet.newInstance(oddsArray, gameActual);
        bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheetCreateBet");
    }

    @Override
    public void onSubmitted(Bet bet, int balance) {
        mDatabaseReference.setValue(balance - bet.getAmount());
        String key = mDatabaseReferenceActiveBets.child(bet.getId()).push().getKey();
        mDatabaseReferenceActiveBets.child(bet.getId()).child(key).child("amount").setValue(bet.getAmount());
        mDatabaseReferenceActiveBets.child(bet.getId()).child(key).child("team").setValue(bet.getTeam());
        mDatabaseReferenceActiveBets.child(bet.getId()).child(key).child("odd").setValue(bet.getOdd());
        mActiveBets.add(bet.getId());
    }
}
