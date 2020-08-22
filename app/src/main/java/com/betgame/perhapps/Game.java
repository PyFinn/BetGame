package com.betgame.perhapps;

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
    private String year;
    private Long dateMS;
    private String odd_home_team;
    private String odd_away_team;
    private String odd_draw;
    private Boolean finished;
    private Boolean started;
    private Integer home_team_score;
    private Integer away_team_score;

    public Game() {
    }

    public Game(String home_team, String away_team, String id,String sports, String league, String date, String time, String year, Long dateMS, String odd_home_team, String odd_away_team, String odd_draw, Boolean finished, Boolean started, Integer home_team_score, Integer away_team_score) {
        this.home_team = home_team;
        this.away_team = away_team;
        this.id = id;
        this.sports = sports;
        this.league = league;
        this.date = date;
        this.time = time;
        this.year = year;
        this.dateMS = dateMS;
        this.odd_home_team = odd_home_team;
        this.odd_away_team = odd_away_team;
        this.odd_draw = odd_draw;
        this.finished = finished;
        this.started = started;
        this.home_team_score = home_team_score;
        this.away_team_score = away_team_score;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getDateMS() {
        return dateMS;
    }

    public void setDateMS(Long dateMS) {
        this.dateMS = dateMS;
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

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public Integer getHome_team_score() {
        return home_team_score;
    }

    public void setHome_team_score(Integer home_team_score) {
        this.home_team_score = home_team_score;
    }

    public Integer getAway_team_score() {
        return away_team_score;
    }

    public void setAway_team_score(Integer away_team_score) {
        this.away_team_score = away_team_score;
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
        dest.writeString(year);
        dest.writeLong(dateMS);
        dest.writeString(odd_home_team);
        dest.writeString(odd_away_team);
        dest.writeString(odd_draw);
        dest.writeValue(finished);
        dest.writeValue(started);
        dest.writeInt(home_team_score);
        dest.writeInt(away_team_score);

    }

    private Game(Parcel parcel) {
        home_team = parcel.readString();
        away_team = parcel.readString();
        id = parcel.readString();
        sports = parcel.readString();
        league = parcel.readString();
        date = parcel.readString();
        time = parcel.readString();
        year = parcel.readString();
        dateMS = parcel.readLong();
        odd_home_team = parcel.readString();
        odd_away_team = parcel.readString();
        odd_draw = parcel.readString();
        finished = (Boolean) parcel.readValue(null);
        started = (Boolean) parcel.readValue(null);
        home_team_score = parcel.readInt();
        away_team_score = parcel.readInt();
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
