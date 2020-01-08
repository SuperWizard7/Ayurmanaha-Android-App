package com.ayurmanaha.ayurvedaquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_VSCORE = "extraVscore";
    public static final String EXTRA_PSCORE = "extraPscore";
    public static final String EXTRA_KSCORE = "extraKscore";

    private static final String KEY_VSCORE = "keyVscore";
    private static final String KEY_KSCORE = "keyKscore";
    private static final String KEY_PSCORE = "keyPscore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView textViewQuestion;
    //private TextView textViewScore;
    private TextView textViewQuestionCount;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private Button buttonBack;

    private ColorStateList textColorDefaultRb;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int kScore;
    private int vScore;
    private int pScore;
    private boolean answered;
    private long backPressedTime;

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
        buttonBack = findViewById(R.id.button_back);

        textColorDefaultRb = rb1.getTextColors();
        questionViewModel model = ViewModelProviders.of(this).get(questionViewModel.class);
        questionList = model.getQuestionInformation();
        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);
        showNextQuestion();3
//        if (savedInstanceState == null) {
//            QuizDbHelper dbHelper = new QuizDbHelper(this);
//            questionList = dbHelper.getAllQuestions();
//            questionCountTotal = questionList.size();
//            Collections.shuffle(questionList);
//
//            showNextQuestion();
//        } else {
//            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
//            questionCountTotal = questionList.size();
//            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
//            currentQuestion = questionList.get(questionCounter - 1);
//            kScore = savedInstanceState.getInt(KEY_KSCORE);
//            vScore = savedInstanceState.getInt(KEY_VSCORE);
//            pScore = savedInstanceState.getInt(KEY_PSCORE);
//            answered = savedInstanceState.getBoolean(KEY_ANSWERED);
//            if (answered) {
//                showSolution();
//            }
//        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousQuestion();
            }
        });
    }

    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            //buttonConfirmNext.setText("Confirm");
        } else {
            finishQuiz();
        }
    }

    private void showPreviousQuestion(){
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();
        if(questionCounter>0) {
            questionCounter--;
        }
        currentQuestion = questionList.get(questionCounter);
        textViewQuestion.setText(currentQuestion.getQuestion());
        rb1.setText(currentQuestion.getOption1());
        rb2.setText(currentQuestion.getOption2());
        rb3.setText(currentQuestion.getOption3());
        textViewQuestionCount.setText("Question: " + (questionCounter+1) + "/" + questionCountTotal);
//      else {
//            Toast.makeText(QuizActivity.this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
//        }
    }

    private void checkAnswer() {
        answered = true;

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQuestion.getSelectedAnswer()) {
            score++;
            textViewScore.setText("Score: " + score);
        }
        showSolution();
    }

    private void showSolution() {
//        rb1.setTextColor(Color.RED);
//        rb2.setTextColor(Color.RED);
//        rb3.setTextColor(Color.RED);

        switch (currentQuestion.getSelectedAnswer()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is correct");
                break;
        }

        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        resultIntent.putExtra();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}