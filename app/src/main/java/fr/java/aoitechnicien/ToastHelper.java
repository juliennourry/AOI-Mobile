package fr.java.aoitechnicien;

import android.app.Activity;
import android.widget.Toast;

public class ToastHelper {
    private Activity activity;
    private String text;

    public ToastHelper(Activity act) {
        activity = act;

    }

    public void LoadToasted(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();

    }

}
