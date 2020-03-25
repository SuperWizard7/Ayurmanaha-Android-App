package com.ayurmanaha.ayurvedaquiz.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "scores")
public class Score
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userID", index = true)
    private String userid;

    @NonNull
    @ColumnInfo(name = "pScore")
    private int pScore;

    @NonNull
    @ColumnInfo(name = "kScore")
    private int kScore;

    @NonNull
    @ColumnInfo(name = "vScore")
    private int vScore;

    @NonNull
    private String timeUpdated;

    public Score(String userid, int pScore, int kScore, int vScore, String timeUpdated) {
        this.userid = userid;
        this.pScore = pScore;
        this.kScore = kScore;
        this.vScore = vScore;
        this.timeUpdated = timeUpdated;
    }

    @NonNull
    public String getUserid() {
        return userid;
    }

    @NonNull
    public int getPScore() {
        return this.pScore;
    }

    @NonNull
    public int getKScore() {
        return this.kScore;
    }

    @NonNull
    public int getVScore() {
        return this.vScore;
    }

    @NonNull
    public String getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(@NonNull String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public void setUserid(@NonNull String userid) {
        this.userid = userid;
    }
}
