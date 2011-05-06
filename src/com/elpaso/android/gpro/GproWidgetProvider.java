package com.elpaso.android.gpro;

import java.util.List;

import com.elpaso.android.gpro.beans.GridPosition;
import com.elpaso.android.gpro.exceptions.ParseException;

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
    private static final String TAG = GproWidgetProvider.class.getName();
    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        Log.d(TAG, "onUpdate");
        final int N = widgetIds.length;

        // Por cada widget asociado al provider
        for (int i = 0; i < N; i++) {
            int widgetId = widgetIds[i];
            try {
                setUpWidget(context, appWidgetManager, widgetId, GproWidgetConfigure.loadManagerIdm(context, widgetId));
            } catch (ParseException e) {
                Log.e(TAG, "Error happened getting information from GPRO: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Inicializa el widget, configura los botones, y actualiza la información de la pantalla del widget. 
     * Consulta la clasificación, y muestra la posición, el tiempo y la diferencia con el primer clasificado.
     */
    static void setUpWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, Integer managerIdm) throws ParseException {
        Log.d(TAG, "Setting up GproWidget [" + widgetId + "]");
        // Obtenemos las vistas del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gpro_widget);

        // Crear un intent para lanzar una activity 
        Intent intent = new Intent(context, GproGridViewer.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asociamos la llamada al intent anterior en el onclick del botón
        Log.d(TAG, "Configuring grid button");
        views.setOnClickPendingIntent(R.id.grid_button, pendingIntent);
        
        // Crear un intent para lanzar una activity 
        Intent intentRace = new Intent(context, GproRaceViewer.class);
        intentRace.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntentRace = PendingIntent.getActivity(context, 0, intentRace, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asociamos la llamada al intent anterior en el onclick del botón
        Log.d(TAG, "Configuring race button");
        views.setOnClickPendingIntent(R.id.race_button, pendingIntentRace);
        
        // Actualizamos el texto del widget
        Log.d(TAG, "Updating widget info");
        String info = "";
        if (managerIdm != null) {
            Log.d(TAG, "Getting grid position");
            GridPosition driver = findGridManagerPosition(context, widgetId, managerIdm);
            if (driver != null) {
                info = driver.shortToString();
            } else {
                info = context.getString(R.string.not_qualified);
            }
        } else {
            Log.w(TAG, "There isn't manager IDM configured");
            info = context.getString(R.string.not_qualified);
        }
        views.setTextViewText(R.id.text, info);

        // Actualizar el widget
        appWidgetManager.updateAppWidget(widgetId, views);
        Log.d(TAG, "Set up finished");
    }

    /**
     * Obtiene la información de la clasificación de un piloto, consultando primero la parrilla de clasificación.
     * 
     * @param managerName El nombre del manager tal y como está en Gpro, del que queremos obtener la información.
     * @return La información de clasificación del piloto, o null si no se ha clasificado.
     */
    private static GridPosition findGridManagerPosition(Context context, int widgetId, Integer managerIdm) throws ParseException {
        List<GridPosition> drivers = GproDAO.findGridPositions(context, widgetId); 
        for (GridPosition driver : drivers) {
            if (driver.getIdm().equals(managerIdm)) {
                return driver;
            }
        }
        return null;
    }

    /**
     * Actualiza la información de la pantalla del widget con los datos del piloto recibido. No actualiza ni los botones 
     * ni nada más.
     * @deprecated De momento no se debe usar.
     */
    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, GridPosition gridPosition) {
        Log.d(TAG, "Updating driver info for GproWidget [" + widgetId + "]");
        // Obtenemos las vistas del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gpro_widget);

        String info = "";
        if (gridPosition != null) {
            info = gridPosition.shortToString();
        } else {
            info = context.getString(R.string.not_qualified);
        }
        views.setTextViewText(R.id.text, info);

        // Actualizar el widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }
}