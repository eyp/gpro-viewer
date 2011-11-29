package com.elpaso.android.gpro;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Widget para Gpro.
 * 
 * @author eduardo.yanez
 */
public class GproWidgetProvider extends AppWidgetProvider {
    // Log tag
    private static final String TAG = "GproWidgetProvider";

    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        Log.d(TAG, "onUpdate");
        final int N = widgetIds.length;

        // Por cada widget asociado al provider
        for (int i = 0; i < N; i++) {
            int widgetId = widgetIds[i];
            setUpWidget(context, appWidgetManager, widgetId, GproWidgetConfigure.loadManagerName(context, widgetId));
        }
    }

    /**
     * Inicializa el widget, configura los botones, y actualiza la información de la pantalla del widget. 
     * Consulta la clasificación, y muestra la posición, el tiempo y la diferencia con el primer clasificado.
     */
    static void setUpWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, String manager) {
        Log.d(TAG, "Setting up GproWidget [" + widgetId + "]");
        // Obtenemos las vistas del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gpro_widget);

        // Crear un intent para lanzar una activity 
        Intent intent = new Intent(context, GproGridViewer.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asociamos la llamada al intent anterior en el onclick del botón
        views.setOnClickPendingIntent(R.id.grid_button, pendingIntent);
        
        // Crear un intent para lanzar una activity 
        Intent intentRace = new Intent(context, GproRaceViewer.class);
        intentRace.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntentRace = PendingIntent.getActivity(context, 0, intentRace, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asociamos la llamada al intent anterior en el onclick del botón
        views.setOnClickPendingIntent(R.id.race_button, pendingIntentRace);
        
        // Actualizamos el texto del widget
        Driver driver = GproUtils.getDriver(context, widgetId, manager);
        String info = "";
        if (driver != null) {
            info = driver.shortToString();
        } else {
            info = context.getString(R.string.not_qualified);
        }
        views.setTextViewText(R.id.text, info);

        // Actualizar el widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * Actualiza la información de la pantalla del widget con los datos del piloto recibido. No actualiza ni los botones 
     * ni nada más.
     * @deprecated De momento no se debe usar.
     */
    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, Driver driver) {
        Log.d(TAG, "Updating driver info for GproWidget [" + widgetId + "]");
        // Obtenemos las vistas del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gpro_widget);

        String info = "";
        if (driver != null) {
            info = driver.shortToString();
        } else {
            info = context.getString(R.string.not_qualified);
        }
        views.setTextViewText(R.id.text, info);

        // Actualizar el widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }
}