package com.elpaso.android.gpro;

import android.app.ProgressDialog;
import android.content.Context;

public class UIHelper {
    public static ProgressDialog makeProgressDialog(Context context, CharSequence message) { 
        ProgressDialog progress = new ProgressDialog(context); 
        progress.setIndeterminate(true); 
        progress.setMessage(message); 
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progress;
    }
}
