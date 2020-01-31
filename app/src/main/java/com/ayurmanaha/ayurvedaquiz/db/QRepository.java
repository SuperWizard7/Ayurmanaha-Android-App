package com.ayurmanaha.ayurvedaquiz.db;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class QRepository {
    private ScoreDao scoreDao;
    private SelectedAnswersDao selectedAnswersDao;
    private LiveData<List<Question>> allQuestions;
    private Score scoreObtained;
    private Integer selAnsObtained;
    private List<Integer> selAnsObtainedList;

    public QRepository(Application application) {
        QRoomDatabase questionDB = QRoomDatabase.getDatabase(application);
        QuestionDao questionDao = questionDB.questionDao();
        scoreDao = questionDB.scoreDao();
        selectedAnswersDao = questionDB.selectedAnswersDao();
        allQuestions = questionDao.getAllQuestions();
    }

//    public long getScoreInsertId() {
//        return scoreInsertID;
//    }
//
//    private static void onScoreObtained(Score score) {
//        scoreObtained = score;
//    }

    public void insertScore(Score score) {
        new InsertAsyncTaskScore(scoreDao).execute(score);
    }

    public void insertSelAns(SelectedAnswers selAns) {
        new InsertAsyncTaskSelAns(selectedAnswersDao).execute(selAns);
    }

//    public void updateScore(Score score) {
//        new UpdateAsyncTaskScore(scoreDao).execute(score);
//    }
//
//    public void updateSelectedAnswer(SelectedAnswers selAns) {
//        new UpdateAsyncTaskSelAns(selectedAnswersDao).execute(selAns);
//    }

    public LiveData<List<Question>> getAllQuestions() {
        return allQuestions;
    }

    /*Question getQuestion(int qId) {
        return questionDao.getQuestion(qId);
    }*/

    public Score getScore(String uId) {
        new GetAsyncTaskScore((Score output) -> scoreObtained = output,scoreDao).execute(uId);
        return scoreObtained;
    }

    public Integer getSelAns(String uId, int qId) {

        new GetAsyncTaskSelAns((Integer result) -> selAnsObtained = result,selectedAnswersDao,uId,qId).execute();
        return selAnsObtained;
    }

    public List<Integer> getAllUserSelAns(String uId)
    {
        new GetAsyncTaskSelAnsList((List<Integer> resultList) -> selAnsObtainedList = resultList,selectedAnswersDao).execute(uId);
        return selAnsObtainedList;
    }

    /*public void deleteSelectedAnswer(String uId) {
        new DeleteAsyncTaskSelAns(selectedAnswersDao).execute(uId);
    }*/

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

        //private long insertID = -1;
        InsertAsyncTaskScore(ScoreDao scDao) {
            super(scDao);
        }
        @Override
        protected Void doInBackground(Score... scores) {
            mAsyncTaskDao.insert(scores[0]);
            //insertID = mAsyncTaskDao.insert(scores[0]);
            return null;
        }
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            onScoreInserted(insertID);
//        }
    }

//    private class UpdateAsyncTaskScore extends OperationsAsyncTaskScore {
//
//        UpdateAsyncTaskScore(ScoreDao scDao) {
//            super(scDao);
//        }
//        @Override
//        protected Void doInBackground(Score... scores) {
//            mAsyncTaskDao.update(scores[0]);
//            return null;
//        }
//    }

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

//    private static class GetAsyncTaskScore extends AsyncTask<String,Void,Score> {
//        ScoreDao mAsyncTaskDao;
//        GetAsyncTaskScore(ScoreDao dao) {
//            this.mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Score doInBackground(String... string) {
//            return mAsyncTaskDao.getScore(string[0]);
//            //insertID = mAsyncTaskDao.insert(scores[0]);
//        }
//        @Override
//        protected void onPostExecute(Score scores) {
//            super.onPostExecute(scores);
//            onScoreObtained(scores);
//        }
//    }

    private static class GetAsyncTaskScore extends AsyncTask<String,Void,Score> {

        ScoreDao mAsyncTaskDao;

        private AsyncResponse1 delegate;

        private GetAsyncTaskScore(AsyncResponse1 delegate, ScoreDao dao){
            this.mAsyncTaskDao = dao;
            this.delegate = delegate;
        }

        @Override
        protected Score doInBackground(String... string) {
            return mAsyncTaskDao.getScore(string[0]);
        }

        @Override
        protected void onPostExecute(Score score) {
            delegate.processFinish(score);
        }
    }

    private static class GetAsyncTaskSelAns extends AsyncTask<Void, Void, Integer>{
        SelectedAnswersDao mAsyncTaskDao;
        String uid;
        int qid;
        AsyncResponse2 delegate;

        GetAsyncTaskSelAns(AsyncResponse2 delegate, SelectedAnswersDao dao, String uid, Integer qid){
            this.mAsyncTaskDao = dao;
            this.uid = uid;
            this.qid = qid;
            this.delegate = delegate;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return mAsyncTaskDao.getSelectedAnswer(uid,qid);
        }

        @Override
        protected void onPostExecute(Integer result) {
            delegate.processFinish(result);
        }
    }

    private static class GetAsyncTaskSelAnsList extends AsyncTask<String,Void,List<Integer>> {

        SelectedAnswersDao mAsyncTaskDao;

        private AsyncResponse3 delegate;

        private GetAsyncTaskSelAnsList(AsyncResponse3 delegate, SelectedAnswersDao dao){
            this.mAsyncTaskDao = dao;
            this.delegate = delegate;
        }

        @Override
        protected List<Integer> doInBackground(String... string) {
            return mAsyncTaskDao.getAllUserSelectedAnswers(string[0]);
        }

        @Override
        protected void onPostExecute(List<Integer> resultList) {
            delegate.processFinish(resultList);
        }
    }

//    private class UpdateAsyncTaskSelAns extends OperationsAsyncTaskSelAns {
//
//        UpdateAsyncTaskSelAns(SelectedAnswersDao saDao) {
//            super(saDao);
//        }
//        @Override
//        protected Void doInBackground(SelectedAnswers... selectedAnswers) {
//            mAsyncTaskDao.update(selectedAnswers[0]);
//            return null;
//        }
//    }

    /*private class DeleteAsyncTaskSelAns extends OperationsAsyncTaskSelAns {

        public DeleteAsyncTaskSelAns(SelectedAnswersDao saDao) {
            super(saDao);
        }

        @Override
        protected Void doInBackground(String... uID) {
            mAsyncTaskDao.delete(uID[0]);
            return null;
        }
    }*/
    public interface AsyncResponse1 {
        void processFinish(Score output);
    }
    public interface AsyncResponse2 {
        void processFinish(Integer result);
    }
    public interface AsyncResponse3 {
        void processFinish(List<Integer> resultList);
    }
}