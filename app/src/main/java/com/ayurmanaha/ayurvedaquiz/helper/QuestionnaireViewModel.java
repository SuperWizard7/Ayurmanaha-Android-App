package com.ayurmanaha.ayurvedaquiz.helper;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.util.Log;
import com.ayurmanaha.ayurvedaquiz.db.QRepository;
import com.ayurmanaha.ayurvedaquiz.db.Question;
import com.ayurmanaha.ayurvedaquiz.db.Score;
import com.ayurmanaha.ayurvedaquiz.db.SelectedAnswers;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class QuestionnaireViewModel extends AndroidViewModel {

    private String TAG = this.getClass().getSimpleName();
    private QRepository repository;
    private List<Question> allQuestions;

    public QuestionnaireViewModel(Application application) {
        super(application);
        repository = new QRepository(application);
        try{
            allQuestions = repository.getAllQuestions();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.i(TAG,"No questions found.");
        }
    }

    public void insertScore(Score score) {
        repository.insertScore(score);
    }

    public void insertSelAns(SelectedAnswers selAns) {
        repository.insertSelAns(selAns);
    }

    public Score getScore(String uId) throws ExecutionException, InterruptedException {
        return repository.getScore(uId);
    }

    public Integer getSelAns(String uId, int qId) throws ExecutionException, InterruptedException {
        return QRepository.getSelAns(uId, qId);
    }

    public List<Integer> getAllUserSelAns(String uId) throws ExecutionException, InterruptedException
    {
        return repository.getAllUserSelAns(uId);
    }

    public List<Question> getAllQuestions() {
        return allQuestions;
    }

    public void deleteSelectedAns(String uid){
        repository.deleteSelectedAnswer(uid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "ViewModel Destroyed");
    }
}