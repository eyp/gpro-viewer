package com.elpaso.android.gpro;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

public class UtilHelper {
    /**
     * Devuelve el identificador del widget que ha llamado a la actividad.
     * 
     * @return EL identificador del widget, o AppWidgetManager.INVALID_APPWIDGET_ID si no se encuentra.
     */
    public static int getWidgetId(Intent intent) {
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return appWidgetId;
    }
}
