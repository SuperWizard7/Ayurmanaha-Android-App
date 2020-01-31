package com.ayurmanaha.ayurvedaquiz.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "selected_answers",
        indices = {@Index(value = {"userID","questionID"}, unique = true)},
        primaryKeys = {"questionID","userID"},
        foreignKeys = {@ForeignKey(entity = Question.class,
        parentColumns = "id",
        childColumns = "questionID",
        onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Score.class,
        parentColumns = "userID",
        childColumns = "userID",
        onDelete = ForeignKey.CASCADE)})
public class SelectedAnswers {

    @ColumnInfo(name = "questionID")
    @NonNull
    private int qId;

    @ColumnInfo(name = "userID")
    @NonNull
    private String userId;

    @ColumnInfo(name = "selectedAns")
    private int selectedAns;

    public SelectedAnswers(int qId, String userId, int selectedAns) {
        this.qId = qId;
        this.userId = userId;
        this.selectedAns = selectedAns;
    }

    public int getQId() {
        return qId;
    }

    public String getUserId() {
        return userId;
    }

    public int getSelectedAns() {
        return selectedAns;
    }

}
