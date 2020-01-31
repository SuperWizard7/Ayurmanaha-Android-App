package com.ayurmanaha.ayurvedaquiz.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.ayurmanaha.ayurvedaquiz.db.Score;
import com.ayurmanaha.ayurvedaquiz.helper.QuestionnaireViewModel;
import com.ayurmanaha.ayurvedaquiz.helper.SessionManager;
import com.ayurmanaha.ayurvedaquiz.helper.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    //private static final int REQUEST_CODE_QUIZ = 1;
    //public static final String SHARED_PREFS = "sharedPrefs";

    private TextView txt_vScore;
    private TextView txt_pScore;
    private TextView txt_kScore;
    private QuestionnaireViewModel questionnaireViewModel;
    private SessionManager session;

    private String uid;
    private String Vscore;
    private String Kscore;
    private String Pscore;
    private long timeUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt_Name = findViewById(R.id.name);
        txt_vScore = findViewById(R.id.vatha_score);
        txt_pScore = findViewById(R.id.pittha_score);
        txt_kScore = findViewById(R.id.kapha_score);
        Button buttonStartQuiz = findViewById(R.id.buttonStartQuiz);
        Button btnLogout = findViewById(R.id.btnLogout);
        session = new SessionManager(this);
        questionnaireViewModel = ViewModelProviders.of(this).get(QuestionnaireViewModel.class);
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        if(session.isQuizStarted())
        {
            Log.d(TAG,"Quiz activity resumed from main activity");
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
        }

        String name = session.getUserName();
        txt_Name.setText(name);
        uid = session.getUserID();

        //Set scores to text views
        setScore();

        buttonStartQuiz.setOnClickListener((View v)-> startQuiz());
        btnLogout.setOnClickListener((View v)-> logoutUser());
    }

    private void setScore() {
        Score score = questionnaireViewModel.getScore(uid);
        String tag_get_score = "getScore";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GET_SCORE,(String response) -> {
            Log.d(TAG, "Get Score Response: " + response);
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    Vscore = jObj.getString("vScore");
                    Pscore = jObj.getString("pScore");
                    Kscore = jObj.getString("kScore");
                    timeUpdated = Long.parseLong(jObj.getString("time"));

                    if(score==null)
                    {
                        txt_vScore.setText(Vscore);
                        txt_pScore.setText(Pscore);
                        txt_kScore.setText(Kscore);
                        questionnaireViewModel.insertScore(new Score(uid,Integer.parseInt(Pscore),Integer.parseInt(Kscore),Integer.parseInt(Vscore),String.valueOf(timeUpdated)));
                    }
                    else
                    {
                        if(timeUpdated==Long.parseLong(score.getTimeUpdated()))
                        {
                            txt_vScore.setText(Vscore);
                            txt_pScore.setText(Pscore);
                            txt_kScore.setText(Kscore);
                        }
                        else if(timeUpdated>=Long.parseLong(score.getTimeUpdated()))
                        {
                            txt_vScore.setText(Vscore);
                            txt_pScore.setText(Pscore);
                            txt_kScore.setText(Kscore);
                            questionnaireViewModel.insertScore(new Score(uid,Integer.parseInt(Pscore),Integer.parseInt(Kscore),Integer.parseInt(Vscore),String.valueOf(timeUpdated)));
                        }
                        else
                        {
                            retrySendScoreToServer(score);
                        }
                    }
                } else {
                    if(score==null)
                    {
                        setScoreViews(0,0,0);
                    }
                    else
                    {
                        retrySendScoreToServer(score);
                    }
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Log.e(TAG, "Json error: " + e.getMessage());
                Toast.makeText(this,"Unexpected JSON error has occurred. Please restart the application.",Toast.LENGTH_LONG).show();
                setScoreViews(0,0,0);
            }},(VolleyError error) ->{
                Log.e(TAG, "Score retrieve error: " + error.getMessage());
                Toast.makeText(this,"Could not update score to server. Please check your network connection and restart the application to try again.", Toast.LENGTH_LONG).show();
                setScoreViews(0,0,0);
            }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to  url
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };
        // Adding request to request queue
        VolleyHelper.getInstance().addToRequestQueue(strReq, tag_get_score);
    }

    private void startQuiz() {
        session.setQuizStarted(true);
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        startActivity(intent);

    }

    private void logoutUser() {
        session.setLogin(false);
        session.clearUserSession();
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    // Launching the login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null).show();
    }

    private void retrySendScoreToServer(Score score){
        String tag_send_score = "send_score";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SCORE_UPDATE, (String response) -> {
            Log.d(TAG, "Sending score response: " + response);
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setScoreViews(score.getVScore(),score.getPScore(),score.getKScore());
                } else {
                    // Error occurred in updating score. Get the error
                    // message
                    String errorMsg = jObj.getString("error_msg");
                    Log.e(TAG,errorMsg+" Failed to update score during retry (Main Activity)");
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    setScoreViews(0,0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Json error: " + e.getMessage());
                Toast.makeText(this,"Unexpected JSON error has occurred. Please restart the application.",Toast.LENGTH_LONG).show();
                setScoreViews(0,0,0);
            }
        }, (VolleyError error) -> {
            Log.e(TAG, "Score Update Error: " + error.getMessage());
            Toast.makeText(this, "Could not update score to server. Please check your network connection and restart the application to try again.", Toast.LENGTH_LONG).show();
            setScoreViews(0,0,0);
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

    private void setScoreViews(int v,int p,int k){
        Vscore = String.valueOf(v);
        Pscore = String.valueOf(p);
        Kscore = String.valueOf(k);
        txt_kScore.setText(Kscore);
        txt_vScore.setText(Vscore);
        txt_pScore.setText(Pscore);
    }
}