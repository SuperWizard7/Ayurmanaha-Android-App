package com.ayurmanaha.ayurvedaquiz.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "questions")
public class Question {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id", index = true)
    private int id = 0;

    @NonNull
    @ColumnInfo(name = "question")
    private String question;

    @NonNull
    @ColumnInfo(name = "option1")
    private String option1;

    @NonNull
    @ColumnInfo(name = "option2")
    private String option2;

    @NonNull
    @ColumnInfo(name = "option3")
    private String option3;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    @NonNull
    public String getQuestion() {
        return this.question;
    }

    @NonNull
    public String getOption1() {
        return this.option1;
    }

    @NonNull
    public String getOption2() {
        return this.option2;
    }

    @NonNull
    public String getOption3() { return this.option3; }

    public Question(String question, String option1, String option2, String option3) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
    }

}

