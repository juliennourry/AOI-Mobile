package fr.java.aoitechnicien;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {

    private Activity activity;
    private String session;
    private String param;
    private String value;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public SharedHelper(Activity act, String ses) {
        activity = act;
        session = ses;
    }

    public SharedPreferences getShared() {
        sharedpreferences = activity.getSharedPreferences(session, Context.MODE_PRIVATE);
        return sharedpreferences;
    }

    public SharedPreferences.Editor getEditor(SharedPreferences sharedpreferences) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        return editor;
    }

    public void stockParam(String param, String value) {
        editor = getEditor(getShared());
        editor.putString(param, value);
        editor.apply();
    }

    public String getParam(String param) {
        value = getShared().getString(param, "");
        return value;
    }
}
