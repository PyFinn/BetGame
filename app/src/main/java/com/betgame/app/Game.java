package com.betgame.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {
    private String home_team;
    private String away_team;
    private String id;
    private String sports;
    private String league;
    private String date;
    private String time;
    private String odd_home_team;
    private String odd_away_team;
    private String odd_draw;

    public Game() {
    }

    public Game(String home_team, String away_team, String id,String sports, String league, String date, String time, String odd_home_team, String odd_away_team, String odd_draw) {
        this.home_team = home_team;
        this.away_team = away_team;
        this.id = id;
        this.sports = sports;
        this.league = league;
        this.date = date;
        this.time = time;
        this.odd_home_team = odd_home_team;
        this.odd_away_team = odd_away_team;
        this.odd_draw = odd_draw;
    }

    public String getHome_team() {
        return home_team;
    }

    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    public String getAway_team() {
        return away_team;
    }

    public void setAway_team(String away_team) {
        this.away_team = away_team;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports = sports;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOdd_home_team() {
        return odd_home_team;
    }

    public void setOdd_home_team(String odd_home_team) {
        this.odd_home_team = odd_home_team;
    }

    public String getOdd_away_team() {
        return odd_away_team;
    }

    public void setOdd_away_team(String odd_away_team) {
        this.odd_away_team = odd_away_team;
    }

    public String getOdd_draw() {
        return odd_draw;
    }

    public void setOdd_draw(String odd_draw) {
        this.odd_draw = odd_draw;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(home_team);
        dest.writeString(away_team);
        dest.writeString(id);
        dest.writeString(sports);
        dest.writeString(league);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(odd_home_team);
        dest.writeString(odd_away_team);
        dest.writeString(odd_draw);
    }

    private Game(Parcel parcel) {
        home_team = parcel.readString();
        away_team = parcel.readString();
        id = parcel.readString();
        sports = parcel.readString();
        league = parcel.readString();
        date = parcel.readString();
        time = parcel.readString();
        odd_home_team = parcel.readString();
        odd_away_team = parcel.readString();
        odd_draw = parcel.readString();
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {

        @Override
        public Game createFromParcel(Parcel source) {
            return new Game(source);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
}
