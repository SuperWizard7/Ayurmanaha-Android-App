package com.ayurmanaha.ayurvedaquiz.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SelectedAnswersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SelectedAnswers selectedAnswers);

    @Query("SELECT selectedAns FROM selected_answers WHERE userid=:uID and questionID=:qID")
    Integer getSelectedAnswer(String uID, int qID);

    @Query("SELECT selectedAns FROM selected_answers WHERE userid=:uID")
    List<Integer> getAllUserSelectedAnswers(String uID);

    @Update
    void update(SelectedAnswers selectedAnswers);

    @Query("DELETE FROM selected_answers WHERE userID = :userId")
    void delete(String userId);
}

