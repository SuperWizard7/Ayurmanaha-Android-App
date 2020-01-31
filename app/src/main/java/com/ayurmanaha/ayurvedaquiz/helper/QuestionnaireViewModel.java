package com.ayurmanaha.ayurvedaquiz.helper;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.util.Log;
import com.ayurmanaha.ayurvedaquiz.db.QRepository;
import com.ayurmanaha.ayurvedaquiz.db.Question;
import com.ayurmanaha.ayurvedaquiz.db.Score;
import com.ayurmanaha.ayurvedaquiz.db.SelectedAnswers;

import java.util.List;

public class QuestionnaireViewModel extends AndroidViewModel {

    private String TAG = this.getClass().getSimpleName();
    private QRepository repository;
    private LiveData<List<Question>> allQuestions;
    //private long scoreInsertID = -1;

    public QuestionnaireViewModel(Application application) {
        super(application);
        repository = new QRepository(application);
        allQuestions = repository.getAllQuestions();
    }

    public void insertScore(Score score) {
        repository.insertScore(score);
    }

    public void insertSelAns(SelectedAnswers selAns) {
        repository.insertSelAns(selAns);
    }

    public Score getScore(String uId) {
        return repository.getScore(uId);
    }

    public Integer getSelAns(String uId, int qId) {
        return repository.getSelAns(uId, qId);
    }

    public List<Integer> getAllUserSelAns(String uId)
    {
        return repository.getAllUserSelAns(uId);
    }

    public LiveData<List<Question>> getAllQuestions() {
        return allQuestions;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "ViewModel Destroyed");
    }
}