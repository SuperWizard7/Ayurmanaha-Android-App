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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView txt_vScore;
    private TextView txt_pScore;
    private TextView txt_kScore;
    private TextView vatha_label;
    private TextView pittha_label;
    private TextView kapha_label;
    private TextView score_desc_label;
    private Score score;
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
        vatha_label = findViewById(R.id.vatha_lbl);
        pittha_label = findViewById(R.id.pittha_lbl);
        kapha_label = findViewById(R.id.kapha_lbl);
        score_desc_label = findViewById(R.id.current_score_label);

        vatha_label.setText(R.string.vScore_str);
        pittha_label.setText(R.string.pScore_str);
        kapha_label.setText(R.string.kScore_str);
        score_desc_label.setText(R.string.current_score_label);

        Button buttonStartQuiz = findViewById(R.id.buttonStartQuiz);
        Button btnLogout = findViewById(R.id.btnLogout);
        session = new SessionManager(this);

        try{
            questionnaireViewModel = ViewModelProviders.of(this).get(QuestionnaireViewModel.class);
        }
        catch(RuntimeException e){
            Log.e(TAG,"first run db crash");
            questionnaireViewModel = ViewModelProviders.of(this).get(QuestionnaireViewModel.class);
        }

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
            finish();
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
        try{
            score = questionnaireViewModel.getScore(uid);
        }
        catch(InterruptedException|ExecutionException e)
        {
            e.printStackTrace();
        }
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
                        if(Integer.parseInt(Pscore)==0 && Integer.parseInt(Vscore)==0 && Integer.parseInt(Kscore)==0){
                            setNoScoreViews(Integer.parseInt(Vscore),Integer.parseInt(Pscore),Integer.parseInt(Kscore));
                        }
                        else
                        {
                            txt_vScore.setText(Vscore);
                            txt_pScore.setText(Pscore);
                            txt_kScore.setText(Kscore);
                        }
                        questionnaireViewModel.insertScore(new Score(uid,Integer.parseInt(Pscore),Integer.parseInt(Kscore),Integer.parseInt(Vscore),String.valueOf(timeUpdated)));
                    }
                    else
                    {
                        if(timeUpdated==Long.parseLong(score.getTimeUpdated()))
                        {
                            if(Integer.parseInt(Pscore)==0 && Integer.parseInt(Vscore)==0 && Integer.parseInt(Kscore)==0){
                                setNoScoreViews(Integer.parseInt(Vscore),Integer.parseInt(Pscore),Integer.parseInt(Kscore));
                            }
                            else
                            {
                                txt_vScore.setText(Vscore);
                                txt_pScore.setText(Pscore);
                                txt_kScore.setText(Kscore);
                            }
                        }
                        else if(timeUpdated>=Long.parseLong(score.getTimeUpdated()))
                        {
                            if(Integer.parseInt(Pscore)==0 && Integer.parseInt(Vscore)==0 && Integer.parseInt(Kscore)==0){
                                setNoScoreViews(Integer.parseInt(Vscore),Integer.parseInt(Pscore),Integer.parseInt(Kscore));
                            }
                            else {
                                txt_vScore.setText(Vscore);
                                txt_pScore.setText(Pscore);
                                txt_kScore.setText(Kscore);
                                questionnaireViewModel.insertScore(new Score(uid, Integer.parseInt(Pscore), Integer.parseInt(Kscore), Integer.parseInt(Vscore), String.valueOf(timeUpdated)));
                            }
                        }
                        else
                        {
                            retrySendScoreToServer(score);
                        }
                    }
                } else {
                    if(score==null)
                    {
                        setNoScoreViews(0,0,0);
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
                        String currentDateTime = sdf.format(new Date());
                        questionnaireViewModel.insertScore(new Score(uid,0,0,0,currentDateTime));
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
                setNoScoreViews(0,0,0);
            }},(VolleyError error) ->{
                Log.e(TAG, "Score retrieve error: " + error.getMessage());
                Toast.makeText(this,"Could not update score to server. Please check your network connection and restart the application to try again.", Toast.LENGTH_LONG).show();
                setNoScoreViews(0,0,0);
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
        session.setQuizFinished(false);
        Log.i(TAG,"Started quiz");
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                    // Launching the login activity
                    session.setLogin(false);
                    session.clearUserSession();
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
                    setNoScoreViews(score.getVScore(),score.getPScore(),score.getKScore());
                } else {
                    // Error occurred in updating score. Get the error
                    // message
                    String errorMsg = jObj.getString("error_msg");
                    Log.e(TAG,errorMsg+" Failed to update score during retry (Main Activity)");
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    setNoScoreViews(0,0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Json error: " + e.getMessage());
                Toast.makeText(this,"Unexpected JSON error has occurred. Please restart the application.",Toast.LENGTH_LONG).show();
                setNoScoreViews(0,0,0);
            }
        }, (VolleyError error) -> {
            Log.e(TAG, "Score Update Error: " + error.getMessage());
            Toast.makeText(this, "Could not update score to server. Please check your network connection and restart the application to try again.", Toast.LENGTH_LONG).show();
            setNoScoreViews(0,0,0);
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

    private void setNoScoreViews(int v, int p, int k){
        Vscore = String.valueOf(v);
        Pscore = String.valueOf(p);
        Kscore = String.valueOf(k);
        txt_kScore.setText("");
        txt_vScore.setText("");
        txt_pScore.setText("");
        vatha_label.setText("");
        kapha_label.setText("");
        pittha_label.setText("");
        score_desc_label.setText(R.string.no_score_label);
    }
}