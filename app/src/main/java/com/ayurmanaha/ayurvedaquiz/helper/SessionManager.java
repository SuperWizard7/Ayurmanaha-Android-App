package com.ayurmanaha.ayurvedaquiz.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared preferences file name
	private static final String PREF_NAME = "com.ayurmanaha.ayurvedaquiz";
	private static final String KEY_CURRENT_USER_ID = "keyCurrentUserID";
	private static final String KEY_CURRENT_USER_NAME = "keyCurrentUserName";
	private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
	private static final String KEY_SELECTED_OPTION = "keySelectedOption";
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	private static final String KEY_IS_QUIZ_STARTED = "isQuizStarted";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		editor = pref.edit();
		editor.apply();
	}

	public void setLogin(boolean isLoggedIn) {
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
		editor.apply();
		Log.d(TAG, "User login session modified!");
	}

	public void setQuizStarted(boolean isQuizStarted)
    {
        editor.putBoolean(KEY_IS_QUIZ_STARTED, isQuizStarted);
        editor.apply();
        Log.d(TAG, "Is quiz started? = "+isQuizStarted);
    }

	public void setUserID(String userID) {
		editor.putString(KEY_CURRENT_USER_ID, userID);
		editor.apply();
		Log.d(TAG, "Current User ID: "+userID);
	}

	public void setUserName(String userName) {
		editor.putString(KEY_CURRENT_USER_NAME, userName);
		editor.apply();
		Log.d(TAG, "Current User Name: "+userName);
	}

	public void setQuestionCount(int qNo) {
		editor.putInt(KEY_QUESTION_COUNT, qNo);
		editor.apply();
		Log.d(TAG, "Quiz resumed/stopped at question number "+(qNo+1));
	}

	public void setSelectedOption(int option) {
		editor.putInt(KEY_SELECTED_OPTION, option);
		editor.apply();
	}

	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}

	public boolean isQuizStarted() {return pref.getBoolean(KEY_IS_QUIZ_STARTED,false);}

	public String getUserID() {return pref.getString(KEY_CURRENT_USER_ID,null);}

	public String getUserName() {return pref.getString(KEY_CURRENT_USER_NAME,null);}

	public int getQuestionCount() {return pref.getInt(KEY_QUESTION_COUNT,0);}

	public int getSelectedOption() {return pref.getInt(KEY_SELECTED_OPTION,-1);}

	public void clearQuizSession()
	{
		editor.remove(KEY_QUESTION_COUNT);
		editor.remove(KEY_SELECTED_OPTION);
		editor.apply();
		Log.d(TAG, "Quiz session shared preferences cleared.");
	}

	public void clearUserSession()
	{
		editor.remove(KEY_CURRENT_USER_ID);
		editor.remove(KEY_CURRENT_USER_NAME);
		editor.apply();
		Log.d(TAG, "User session shared preferences cleared.");
	}

}
