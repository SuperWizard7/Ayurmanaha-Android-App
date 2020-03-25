package com.ayurmanaha.ayurvedaquiz.db;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QRepository {
    private static final String TAG = "QRepository";
    private static QuestionDao questionDao;
    private static ScoreDao scoreDao;
    private static SelectedAnswersDao selectedAnswersDao;

    public QRepository(Application application) {
        QRoomDatabase questionDB = QRoomDatabase.getDatabase(application);
        questionDao = questionDB.questionDao();
        scoreDao = questionDB.scoreDao();
        selectedAnswersDao = questionDB.selectedAnswersDao();
    }

    public void insertScore(Score score) {
        new InsertAsyncTaskScore(scoreDao).execute(score);
    }

    public void insertSelAns(SelectedAnswers selAns) {
        new InsertAsyncTaskSelAns(selectedAnswersDao).execute(selAns);
    }

    public List<Question> getAllQuestions() throws ExecutionException, InterruptedException{
        List<Question> allQuestions = new GetAsyncTaskQuestions(questionDao).execute().get();
        Log.d(TAG,"question 1: "+allQuestions.get(0));
        return allQuestions;
    }

    public Score getScore(String uId) throws ExecutionException, InterruptedException{
        return new GetAsyncTaskScore(scoreDao).execute(uId).get();
    }

    public static Integer getSelAns(String uId, int qId) throws ExecutionException, InterruptedException  {
        return new GetAsyncTaskSelAns(selectedAnswersDao,uId,qId).execute().get();
    }

    public List<Integer> getAllUserSelAns(String uId) throws ExecutionException, InterruptedException
    {
        return new GetAsyncTaskSelAnsList(selectedAnswersDao).execute(uId).get();
    }

    public void deleteSelectedAnswer(String uId) {
        new DeleteAsyncTaskSelAns(selectedAnswersDao).execute(uId);
    }

    private static class OperationsAsyncTaskScore extends AsyncTask<Score, Void, Void> {
        ScoreDao mAsyncTaskDao;

        OperationsAsyncTaskScore(ScoreDao dao) {
            this.mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(Score... scores) {
            return null;
        }
    }

    private static class OperationsAsyncTaskSelAns extends AsyncTask<SelectedAnswers, Void, Void> {
        SelectedAnswersDao mAsyncTaskDao;

        OperationsAsyncTaskSelAns(SelectedAnswersDao dao) {
            this.mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(SelectedAnswers... selectedAnswers) {
            return null;
        }
    }

    private static class InsertAsyncTaskScore extends OperationsAsyncTaskScore {

        InsertAsyncTaskScore(ScoreDao scDao) {
            super(scDao);
        }
        @Override
        protected Void doInBackground(Score... scores) {
            mAsyncTaskDao.insert(scores[0]);
            return null;
        }
    }

    private static class InsertAsyncTaskSelAns extends OperationsAsyncTaskSelAns {

        InsertAsyncTaskSelAns(SelectedAnswersDao saDao) {
            super(saDao);
        }
        @Override
        protected Void doInBackground(SelectedAnswers... selectedAnswers) {
            mAsyncTaskDao.insert(selectedAnswers[0]);
            return null;
        }
    }

    private static class GetAsyncTaskScore extends AsyncTask<String,Void,Score>{
        ScoreDao mAsyncTaskDao;

        private GetAsyncTaskScore(ScoreDao dao){
            this.mAsyncTaskDao = dao;
        }

        @Override
        protected Score doInBackground(String... string) {
            return mAsyncTaskDao.getScore(string[0]);
        }
    }

    private static class GetAsyncTaskSelAns extends AsyncTask<Void, Void, Integer>{
        SelectedAnswersDao mAsyncTaskDao;
        String uid;
        int qid;

        GetAsyncTaskSelAns(SelectedAnswersDao dao, String uid, Integer qid){
            this.mAsyncTaskDao = dao;
            this.uid = uid;
            this.qid = qid;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return mAsyncTaskDao.getSelectedAnswer(uid,qid);
        }
    }

    private static class GetAsyncTaskSelAnsList extends AsyncTask<String,Void,List<Integer>>{

        SelectedAnswersDao mAsyncTaskDao;

        private GetAsyncTaskSelAnsList(SelectedAnswersDao dao){
            this.mAsyncTaskDao = dao;
        }

        @Override
        protected List<Integer> doInBackground(String... string) {
            return mAsyncTaskDao.getAllUserSelectedAnswers(string[0]);
        }
    }

    private static class GetAsyncTaskQuestions extends AsyncTask<Void,Void,List<Question>>{

        QuestionDao mAsyncTaskDao;

        private GetAsyncTaskQuestions(QuestionDao dao){
            this.mAsyncTaskDao = dao;
        }

        @Override
        protected List<Question> doInBackground(Void... params) {
            return mAsyncTaskDao.getAllQuestions();
        }

    }

    private static class DeleteAsyncTaskSelAns extends AsyncTask<String,Void,Void> {
        SelectedAnswersDao mAsyncTaskDao;
        DeleteAsyncTaskSelAns(SelectedAnswersDao dao) {
            this.mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... uID) {
            mAsyncTaskDao.delete(uID[0]);
            return null;
        }
    }
}