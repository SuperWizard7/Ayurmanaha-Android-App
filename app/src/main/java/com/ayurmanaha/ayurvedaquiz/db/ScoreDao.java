package com.ayurmanaha.ayurvedaquiz.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Score score);

    @Query("SELECT * FROM scores WHERE userid=:uID LIMIT 1")
    Score getScore(String uID);

    @Update
    void update(Score score);

}
