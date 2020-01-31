package com.ayurmanaha.ayurvedaquiz.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ayurmanaha.ayurvedaquiz.R;
import com.ayurmanaha.ayurvedaquiz.app.AppConfig;
import com.ayurmanaha.ayurvedaquiz.db.Question;
import com.ayurmanaha.ayurvedaquiz.helper.QuestionnaireViewModel;
import com.ayurmanaha.ayurvedaquiz.db.Score;
import com.ayurmanaha.ayurvedaquiz.db.SelectedAnswers;
import com.ayurmanaha.ayurvedaquiz.helper.SessionManager;
import com.ayurmanaha.ayurvedaquiz.helper.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
//    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
//    private static final String KEY_SELECTED_OPTION = "keySelectedOption";
//    private SharedPreferences preferences;
//    private String sharedPrefFile = "com.ayurmanaha.ayurvedaquiz";
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private QuestionnaireViewModel questionnaireViewModel;
    private SessionManager session;

    private TextView textViewQuestion;
    private TextView textViewQuestionCount;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private ProgressDialog pDialog;
    private ColorStateList textColorDefaultRb;

    private List<Question> questionList;
    private int questionCount;
    private int currQuestionNo;
    private int selectedOption;
    private Question currentQuestion;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        rbGroup = findViewById(R.id.answer_options);
        rb1 = findViewById(R.id.option1);
        rb2 = findViewById(R.id.option2);
        rb3 = findViewById(R.id.option3);
        buttonConfirmNext = findViewById(R.id.button_next);
        Button buttonBack = findViewById(R.id.button_back);
        textColorDefaultRb = rb1.getTextColors();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(this);

        if (!session.isQuizStarted()) {
            session.clearQuizSession();
            Intent intent = new Intent(QuizActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
//        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
//        currQuestionNo = preferences.getInt(KEY_QUESTION_COUNT,0);
//        selectedOption = preferences.getInt(KEY_SELECTED_OPTION,-1);
        uid = session.getUserID();
        //add error code for if uid is null
        currQuestionNo = session.getQuestionCount();
        selectedOption = session.getSelectedOption();
        questionnaireViewModel = ViewModelProviders.of(this).get(QuestionnaireViewModel.class);
        questionnaireViewModel.getAllQuestions().observe(this, (List<Question> questions) -> {
            questionList = questions;
            questionCount = questionList.size();
        });
        showNextQuestion();

        buttonConfirmNext.setOnClickListener((View v) -> {
            if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                recordAnswer();
                currQuestionNo++;
                showNextQuestion();
            } else {
                Toast.makeText(QuizActivity.this, "Please select an answer.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBack.setOnClickListener((View v) -> {
            if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                recordAnswer();
            }
            if (currQuestionNo > 0)
                currQuestionNo--;
            showPreviousQuestion();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rb1.isChecked()) {
            selectedOption = 0;
        } else if (rb2.isChecked()) {
            selectedOption = 1;
        } else if (rb3.isChecked()) {
            selectedOption = 2;
        } else {
            selectedOption = -1;
        }
//        SharedPreferences.Editor preferencesEditor = preferences.edit();
//        preferencesEditor.putInt(KEY_QUESTION_COUNT, currQuestionNo);
//        preferencesEditor.putInt(KEY_SELECTED_OPTION, selectedOption);
//        preferencesEditor.apply();
        session.setQuestionCount(currQuestionNo);
        session.setSelectedOption(selectedOption);
    }

    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        switch (selectedOption) {
            case 0:
                rbGroup.check(R.id.option1);
                break;
            case 1:
                rbGroup.check(R.id.option2);
                break;
            case 2:
                rbGroup.check(R.id.option3);
                break;
            case -1:
                rbGroup.clearCheck();
                break;
        }
        if (currQuestionNo < questionCount) {
            currentQuestion = questionList.get(currQuestionNo);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            textViewQuestionCount.setText("Question: " + (currQuestionNo + 1) + "/" + questionCount);
            //currQuestionNo++;
            //answered = false;
            //buttonConfirmNext.setText("Confirm");
        } else {
            sendScoreToServer();
        }
    }

    private void showPreviousQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);

        currentQuestion = questionList.get(currQuestionNo);
        textViewQuestion.setText(currentQuestion.getQuestion());
        rb1.setText(currentQuestion.getOption1());
        rb2.setText(currentQuestion.getOption2());
        rb3.setText(currentQuestion.getOption3());
        textViewQuestionCount.setText("Question: " + (currQuestionNo + 1) + "/" + questionCount);

        selectedOption = questionnaireViewModel.getSelAns(uid, currQuestionNo);
        switch (selectedOption) {
            case 0:
                rbGroup.check(R.id.option1);
                break;
            case 1:
                rbGroup.check(R.id.option2);
                break;
            case 2:
                rbGroup.check(R.id.option3);
                break;
            case -1:
                rbGroup.clearCheck();
                break;
        }
    }

    private void recordAnswer() {
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        selectedOption = rbGroup.indexOfChild(rbSelected);
        SelectedAnswers selAns = new SelectedAnswers(currQuestionNo, uid, selectedOption);
        questionnaireViewModel.insertSelAns(selAns);
        if (currQuestionNo < questionCount) {
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {
//        Intent resultIntent = new Intent();
//        setResult(RESULT_OK, resultIntent);
        session.setQuizStarted(false);
        session.clearQuizSession();

        // Go back to main activity
        Intent intent = new Intent(QuizActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendScoreToServer() {
        String tag_send_score = "send_score";
        int p = 0, k = 0, v = 0;
        List<Integer> allSelectedAnswers = questionnaireViewModel.getAllUserSelAns(uid);
        for (Integer i : allSelectedAnswers) {
            switch (i) {
                case 0:
                    v++;
                    break;
                case 1:
                    p++;
                    break;
                case 2:
                    k++;
                    break;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        Score score = new Score(uid, p, k, v, currentDateTime);
        questionnaireViewModel.insertScore(score);

        pDialog.setMessage("Finishing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SCORE_UPDATE, (String response) -> {
            Log.d(TAG, "Sending score response: " + response);
            hideDialog();
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    Toast.makeText(this, "New score updated!", Toast.LENGTH_LONG).show();
                    finishQuiz();
                } else {
                    // Error occurred in updating score. Get the error
                    // message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, (VolleyError error) -> {
            Log.e(TAG, "Score Update Error: " + error.getMessage());
            Toast.makeText(this, "Could not update score to server. Please check your network connection and try submitting again.", Toast.LENGTH_LONG).show();
            hideDialog();
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("vScore", String.valueOf(score.getVScore()));
                params.put("pScore", String.valueOf(score.getPScore()));
                params.put("kScore", String.valueOf(score.getKScore()));
                params.put("timeUpdated", score.getTimeUpdated());

                return params;
            }
        };
        // Adding request to request queue
        VolleyHelper.getInstance().addToRequestQueue(strReq, tag_send_score);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(QuizActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leave Quiz")
                .setMessage("Are you sure you want to leave the quiz? All progress will be lost.")
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> finishQuiz())
                .setNegativeButton("No", null).show();
    }
}