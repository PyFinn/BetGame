package com.betgame.app;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mStateListener;
    Fragment selectedFragment;
    BottomNavigationView bnbMain;
    private String[] games_bet_active = new String[5];
    long[] games_upcoming = new long[3];
    ArrayList<Long> gamesMS = new ArrayList<>();
    Game[] games;
    ArrayList<Game> game_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mGameDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference mUsersChild = mGameDatabaseReference.child("users");
        final DatabaseReference mGamesChild = mGameDatabaseReference.child("games");


        games_bet_active[0] = "00001";
        games_bet_active[1] = "00002";
        games_bet_active[2] = "00003";
        games_bet_active[3] = "00004";
        games_bet_active[4] = "00005";

        bnbMain = findViewById(R.id.bottom_navigation_bar);
        bnbMain.setOnNavigationItemSelectedListener(navListener);
        bnbMain.setSelectedItemId(R.id.nav_home);
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

        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.e("Tag", "I am executed right now");
                    final String uid = user.getUid();
                    mUsersChild.child(uid).child("balance").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null){
                                Log.e("Tag", "" + snapshot.getValue());
                            } else {
                                mUsersChild.child(uid).child("balance").setValue(500);
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build()))
                                    .build(),
                            1);
                }
            }
        };

        getSupportFragmentManager().beginTransaction().replace(R.id.sv_home_page, HomeFragment.newInstance(game_arr, games_bet_active, games_upcoming)).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            selectedFragment = null;
            String tag = "Home";
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment = HomeFragment.newInstance(game_arr, games_bet_active, games_upcoming);
                    tag = "Home";
                    break;
                case R.id.nav_schedule:
                    selectedFragment = ScheduleFragment.newInstance(game_arr);
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
        int counter = 0;


        // Create array of Game objects that stores data from the Input
        games = new Game[(int) snapshotToConvert.getChildrenCount()];

        for (DataSnapshot dSnap : snapshotToConvert.getChildren()){
            Log.e("Tag", dSnap.child("game").child("date").getValue().toString());
            games[counter] = new Game();

            games[counter].setHome_team(dSnap.child(RESULTS).child(HOME_TEAM).getValue().toString());
            games[counter].setAway_team(dSnap.child(RESULTS).child(AWAY_TEAM).getValue().toString());
            games[counter].setId(dSnap.getKey());
            games[counter].setSports(dSnap.child(RESULTS).child(SPORTS).getValue().toString());
            games[counter].setLeague(dSnap.child(RESULTS).child(LEAGUE).getValue().toString());
            games[counter].setDate(dSnap.child(RESULTS).child(DATE).getValue().toString());
            games[counter].setTime(dSnap.child(RESULTS).child(TIME).getValue().toString());
            games[counter].setYear(dSnap.child(RESULTS).child(YEAR).getValue().toString());
            games[counter].setOdd_home_team(dSnap.child(RESULTS).child(ODD_HOME_TEAM).getValue().toString());
            games[counter].setOdd_away_team(dSnap.child(RESULTS).child(ODD_AWAY_TEAM).getValue().toString());
            games[counter].setOdd_draw(dSnap.child(RESULTS).child(ODD_DRAW).getValue().toString());


            String dateToFormat = games[counter].getDate();
            String timeToFormat = games[counter].getTime();
            String yearToFormat = games[counter].getYear();
            String[] rawDateSplitted = dateToFormat.split("\\.", -1);
            String[] rawTimeSplitted = timeToFormat.split(":", -1);
            String convertMe = yearToFormat + "/" + rawDateSplitted[1] + "/" + rawDateSplitted[0] + "/" + rawTimeSplitted[0] + "/" + rawTimeSplitted[1];
            gamesMS.add(createDateFromString(convertMe));
            games[counter].setDateMS(createDateFromString(convertMe));

            counter++;
        }
        return games;
    }
}
