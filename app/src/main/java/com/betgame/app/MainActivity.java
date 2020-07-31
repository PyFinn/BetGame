package com.betgame.app;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.betgame.app.Fragments.CashFragment;
import com.betgame.app.Fragments.HomeFragment;
import com.betgame.app.Fragments.ScheduleFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;
    private DatabaseReference mUserActiveBetsReference;
    private DatabaseReference mUsersChild;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mStateListener;
    Fragment selectedFragment;
    BottomNavigationView bnbMain;
    private ArrayList<String> games_bet_active;
    long[] games_upcoming = new long[3];
    ArrayList<Long> gamesMS = new ArrayList<>();
    Game[] games;
    ArrayList<Game> mFinishedGames;
    ArrayList<Game> game_arr;
    private ArrayList<Bet> mBetArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mGameDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsersChild = mGameDatabaseReference.child("users");
        mUserActiveBetsReference = mGameDatabaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("active_bets");
        final DatabaseReference mGamesChild = mGameDatabaseReference.child("games");


        mUserActiveBetsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                games_bet_active = new ArrayList<>();
                mBetArray = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot : shot.getChildren()){
                        Bet newBet = new Bet();
                        long sipsnap = (long) dataSnapshot.child("amount").getValue();
                        Double oddToSet;
                        try {
                            oddToSet = (Double) dataSnapshot.child("odd").getValue();
                        } catch (ClassCastException e) {
                            long dblOddToSet = (long) dataSnapshot.child("odd").getValue();
                            oddToSet = (double) dblOddToSet;
                        }
                        newBet.setId(shot.getKey());
                        newBet.setTeam((String) dataSnapshot.child("team").getValue());
                        if (oddToSet != null){
                            newBet.setOdd(oddToSet);
                        }
                        newBet.setAmount((int) sipsnap);
                        newBet.setBetId(dataSnapshot.getKey());
                        mBetArray.add(newBet);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mGamesChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game_arr = new ArrayList<Game>(Arrays.asList(makeGamesDataToArray(snapshot)));
                mFinishedGames = gameArrayToFinishedGameArray(game_arr);
                long largestA = Long.MAX_VALUE, largestB = Long.MAX_VALUE, largestC = Long.MAX_VALUE;
                for(long value : gamesMS) {
                    if(value < largestA) {
                        largestB = largestA;
                        largestA = value;
                    } else if (value < largestB) {
                        largestC = largestB;
                        largestB = value;
                    } else if (value < largestC) {
                        largestC = value;
                    }
                }
                games_upcoming[0] = largestA;
                games_upcoming[1] = largestB;
                games_upcoming[2] = largestC;
                getSupportFragmentManager().beginTransaction().replace(R.id.sv_home_page, HomeFragment.newInstance(game_arr, mBetArray, games_upcoming, mFinishedGames)).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bnbMain = findViewById(R.id.bottom_navigation_bar);
        bnbMain.setOnNavigationItemSelectedListener(navListener);
        bnbMain.setSelectedItemId(R.id.nav_home);

        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.e("Tag", "I am executed right now");
                    final String uid = user.getUid();
                    mUsersChild.child(uid).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null){
                            } else {
                                mUsersChild.child(uid).child("balance").setValue(500);
                                mUsersChild.child(uid).child("spinned").setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.icon)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build()))
                                    .build(),
                            1);
                }
            }
        };

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            selectedFragment = null;
            String tag = "Home";
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment = HomeFragment.newInstance(game_arr, mBetArray, games_upcoming, mFinishedGames);
                    tag = "Home";
                    break;
                case R.id.nav_schedule:
                    selectedFragment = ScheduleFragment.newInstance(game_arr, games_bet_active);
                    tag = "Schedule";
                    break;
                case R.id.nav_money:
                    selectedFragment = new CashFragment();
                    tag = "Cash";
                    break;
            }
            if (!tag.equals("Cash")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.sv_home_page, selectedFragment);
                ft.commit();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.sv_home_page, selectedFragment, tag).commit();
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mStateListener);
    }

    public long createDateFromString(String strToConvert){
        long timeInMilliseconds = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        format.setLenient(false);
        try {
            Date date = format.parse(strToConvert);
            timeInMilliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public ArrayList<Game> gameArrayToFinishedGameArray(ArrayList<Game> games){
        ArrayList<Game> mFinishedGameArray = new ArrayList<>();
        for (Game game : games){
            if (game.getFinished()){
                mFinishedGameArray.add(game);
            }
        }
        return mFinishedGameArray;
    }


    public Game[] makeGamesDataToArray(DataSnapshot snapshotToConvert) {
        // filters
        final String RESULTS = "game";
        final String HOME_TEAM = "home_team_name";
        final String AWAY_TEAM = "away_team_name";
        final String SPORTS = "sports";
        final String LEAGUE = "league";
        final String DATE = "date";
        final String TIME = "time";
        final String YEAR = "year";
        final String ODD_HOME_TEAM = "odd_home_team";
        final String ODD_AWAY_TEAM = "odd_away_team";
        final String ODD_DRAW = "odd_draw";
        final String STARTED = "started";
        final String FINSIHED = "finished";
        final String HOME_TEAM_SCORE = "home_team_score";
        final String AWAY_TEAM_SCORE = "away_team_score";
        int counter = 0;


        // Create array of Game objects that stores data from the Input
        games = new Game[(int) snapshotToConvert.getChildrenCount()];

        for (DataSnapshot dSnap : snapshotToConvert.getChildren()){
            Log.e("Tag", dSnap.child("game").child("date").getValue().toString());
            games[counter] = new Game();
            DataSnapshot dbRef = dSnap.child(RESULTS);

            if (dbRef.hasChild(HOME_TEAM)){
                games[counter].setHome_team((String) dbRef.child(HOME_TEAM).getValue());
            }

            if (dbRef.hasChild(AWAY_TEAM)){
                games[counter].setAway_team((String) dbRef.child(AWAY_TEAM).getValue());
            }

            if (dbRef.hasChild(SPORTS)){
                games[counter].setSports((String) dbRef.child(SPORTS).getValue());
            }

            if (dbRef.hasChild(LEAGUE)){
                games[counter].setLeague((String) dbRef.child(LEAGUE).getValue());
            }

            if (dbRef.hasChild(DATE)){
                games[counter].setDate((String) dbRef.child(DATE).getValue());
            }

            if (dbRef.hasChild(TIME)){
                games[counter].setTime((String) dbRef.child(TIME).getValue());
            }

            if (dbRef.hasChild(YEAR)){
                games[counter].setYear((String) dbRef.child(YEAR).getValue());
            }

            if (dbRef.hasChild(ODD_HOME_TEAM)){
                games[counter].setOdd_home_team((String) dbRef.child(ODD_HOME_TEAM).getValue());
            }

            if (dbRef.hasChild(ODD_AWAY_TEAM)){
                games[counter].setOdd_away_team((String) dbRef.child(ODD_AWAY_TEAM).getValue());
            }

            if (dbRef.hasChild(ODD_DRAW)){
                games[counter].setOdd_draw((String) dbRef.child(ODD_DRAW).getValue());
            } else {
                games[counter].setOdd_draw("0");
            }

            if (dbRef.hasChild(STARTED)){
                games[counter].setStarted((Boolean) dbRef.child(STARTED).getValue());
            }

            if (dbRef.hasChild(FINSIHED)){
                games[counter].setFinished((Boolean) dbRef.child(FINSIHED).getValue());
            }

            if (dbRef.hasChild(HOME_TEAM_SCORE)){
                long home_team_score = (long) dbRef.child(HOME_TEAM_SCORE).getValue();
                games[counter].setHome_team_score((int) home_team_score);
            }else {
                games[counter].setHome_team_score(0);
            }

            if (dbRef.hasChild(AWAY_TEAM_SCORE)){
                games[counter].setAway_team_score((int) ((long) dbRef.child(AWAY_TEAM_SCORE).getValue()));
            } else {
                games[counter].setAway_team_score(0);
            }

            games[counter].setId(dSnap.getKey());


            String dateToFormat = games[counter].getDate();
            String timeToFormat = games[counter].getTime();
            String yearToFormat = games[counter].getYear();
            String[] rawDateSplitted = dateToFormat.split("\\.", -1);
            String[] rawTimeSplitted = timeToFormat.split(":", -1);
            String convertMe = yearToFormat + "/" + rawDateSplitted[1] + "/" + rawDateSplitted[0] + "/" + rawTimeSplitted[0] + "/" + rawTimeSplitted[1];
            if (!games[counter].getFinished()){
                gamesMS.add(createDateFromString(convertMe));
            }
            games[counter].setDateMS(createDateFromString(convertMe));

            counter++;
        }
        return games;
    }
}
