package com.betgame.app;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.betgame.app.Fragments.CashFragment;
import com.betgame.app.Fragments.HomeFragment;
import com.betgame.app.Fragments.ScheduleFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;
    Fragment selectedFragment;
    BottomNavigationView bnbMain;
    String makeMeToJSON = "{\n" +
            "    \"page\": 1,\n" +
            "    \"total_results\": 10000,\n" +
            "    \"total_pages\": 500,\n" +
            "    \"results\": [\n" +
            "        {\n" +
            "            \"home_team\": \"FC Bayern München\",\n" +
            "            \"away_team\": \"Borussia Dortmund\",\n" +
            "            \"id\": \"00001\",\n" +
            "            \"sports\": Soccer,\n" +
            "            \"league\": \"Bundesliga\",\n" +
            "            \"date\": \"24.07\",\n" +
            "            \"time\": \"15:30\",\n" +
            "            \"year\": \"2020\",\n" +
            "            \"odd_home_team\": \"1.57\",\n" +
            "            \"odd_away_team\": \"2.41\",\n" +
            "            \"odd_draw\": \"3.95\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"home_team\": \"Los Angeles Lakers\",\n" +
            "            \"away_team\": \"Golden State Warriors\",\n" +
            "            \"id\": \"00002\",\n" +
            "            \"sports\": Basketball,\n" +
            "            \"league\": \"NBA\",\n" +
            "            \"date\": \"24.07\",\n" +
            "            \"time\": \"15:30\",\n" +
            "            \"year\": \"2020\",\n" +
            "            \"odd_home_team\": \"1.57\",\n" +
            "            \"odd_away_team\": \"2.41\",\n" +
            "            \"odd_draw\": \"3.95\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"home_team\": \"Chelsea\",\n" +
            "            \"away_team\": \"Arsenal\",\n" +
            "            \"id\": \"00003\",\n" +
            "            \"sports\": Soccer,\n" +
            "            \"league\": \"Premier League\",\n" +
            "            \"date\": \"23.06\",\n" +
            "            \"time\": \"15:30\",\n" +
            "            \"year\": \"2020\",\n" +
            "            \"odd_home_team\": \"1.57\",\n" +
            "            \"odd_away_team\": \"2.41\",\n" +
            "            \"odd_draw\": \"3.95\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"home_team\": \"Paderborn\",\n" +
            "            \"away_team\": \"Düsseldorf\",\n" +
            "            \"id\": \"00004\",\n" +
            "            \"sports\": Soccer,\n" +
            "            \"league\": \"Bundesliga\",\n" +
            "            \"date\": \"23.06\",\n" +
            "            \"time\": \"15:30\",\n" +
            "            \"year\": \"2020\",\n" +
            "            \"odd_home_team\": \"1.57\",\n" +
            "            \"odd_away_team\": \"2.41\",\n" +
            "            \"odd_draw\": \"3.95\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"home_team\": \"Hoffenheim\",\n" +
            "            \"away_team\": \"Freiburg\",\n" +
            "            \"id\": \"00005\",\n" +
            "            \"sports\": Soccer,\n" +
            "            \"league\": \"Bundesliga\",\n" +
            "            \"date\": \"23.07\",\n" +
            "            \"time\": \"15:30\",\n" +
            "            \"year\": \"2020\",\n" +
            "            \"odd_home_team\": \"1.57\",\n" +
            "            \"odd_away_team\": \"2.41\",\n" +
            "            \"odd_draw\": \"3.95\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"home_team\": \"FC Bayern München\",\n" +
            "            \"away_team\": \"Borussia Dortmund\",\n" +
            "            \"id\": \"00006\",\n" +
            "            \"sports\": Soccer,\n" +
            "            \"league\": \"Bundesliga\",\n" +
            "            \"date\": \"24.06\",\n" +
            "            \"time\": \"14:30\",\n" +
            "            \"year\": \"2020\",\n" +
            "            \"odd_home_team\": \"1.57\",\n" +
            "            \"odd_away_team\": \"2.41\",\n" +
            "            \"odd_draw\": \"3.95\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private String[] games_bet_active = new String[5];
    long[] games_upcoming = new long[3];
    ArrayList<Long> gamesMS = new ArrayList<>();
    Game[] games;
    ArrayList<Game> game_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDatabase.getInstance();
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("games");


        games_bet_active[0] = "00001";
        games_bet_active[1] = "00002";
        games_bet_active[2] = "00003";
        games_bet_active[3] = "00004";
        games_bet_active[4] = "00005";

        bnbMain = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        bnbMain.setOnNavigationItemSelectedListener(navListener);
        bnbMain.setSelectedItemId(R.id.nav_home);
        try {
            game_arr = new ArrayList<Game>(Arrays.asList(makeGamesDataToArray(makeMeToJSON)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    public Game[] makeGamesDataToArray(String moviesJsonResults) throws JSONException {
        // JSON filters
        final String RESULTS = "results";
        final String HOME_TEAM = "home_team";
        final String AWAY_TEAM = "away_team";
        final String ID = "id";
        final String SPORTS = "sports";
        final String LEAGUE = "league";
        final String DATE = "date";
        final String TIME = "time";
        final String YEAR = "year";
        final String ODD_HOME_TEAM = "odd_home_team";
        final String ODD_AWAY_TEAM = "odd_away_team";
        final String ODD_DRAW = "odd_draw";

        // Get results as an array
        JSONObject moviesJson = new JSONObject(moviesJsonResults);
        JSONArray resultsArray = moviesJson.getJSONArray(RESULTS);

        // Create array of Movie objects that stores data from the JSON string
        games = new Game[resultsArray.length()];

        // Go through movies one by one and get data
        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            games[i] = new Game();

            // Object contains all tags we're looking for
            JSONObject gameInfo = resultsArray.getJSONObject(i);
            // Store data in game object
            games[i].setHome_team(gameInfo.getString(HOME_TEAM));
            games[i].setAway_team(gameInfo.getString(AWAY_TEAM));
            games[i].setId(gameInfo.getString(ID));
            games[i].setSports(gameInfo.getString(SPORTS));
            games[i].setLeague(gameInfo.getString(LEAGUE));
            games[i].setDate(gameInfo.getString(DATE));
            games[i].setTime(gameInfo.getString(TIME));
            games[i].setYear(gameInfo.getString(YEAR));
            games[i].setOdd_home_team(gameInfo.getString(ODD_HOME_TEAM));
            games[i].setOdd_away_team(gameInfo.getString(ODD_AWAY_TEAM));
            games[i].setOdd_draw(gameInfo.getString(ODD_DRAW));

            String dateToFormat = games[i].getDate();
            String timeToFormat = games[i].getTime();
            String yearToFormat = games[i].getYear();
            String[] rawDateSplitted = dateToFormat.split("\\.", -1);
            String[] rawTimeSplitted = timeToFormat.split(":", -1);
            String convertMe = yearToFormat + "/" + rawDateSplitted[1] + "/" + rawDateSplitted[0] + "/" + rawTimeSplitted[0] + "/" + rawTimeSplitted[1];
            gamesMS.add(createDateFromString(convertMe));
            games[i].setDateMS(createDateFromString(convertMe));
        }
        return games;
    }
}
