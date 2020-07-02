package com.betgame.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Bet implements Parcelable {

    private String id;
    private int amount;
    private double odd;
    private String team;

    public Bet() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getOdd() {
        return odd;
    }

    public void setOdd(double odd) {
        this.odd = odd;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Bet(Parcel in) {
        id = in.readString();
        amount = in.readInt();
        odd = in.readDouble();
        team = in.readString();
    }

    public static final Creator<Bet> CREATOR = new Creator<Bet>() {
        @Override
        public Bet createFromParcel(Parcel in) {
            return new Bet(in);
        }

        @Override
        public Bet[] newArray(int size) {
            return new Bet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(amount);
        dest.writeDouble(odd);
        dest.writeString(team);
    }
}
