/*
 * Copyright 2011 Eduardo Yáñez Parareda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elpaso.android.gpro;

import java.util.List;

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.exceptions.ParseException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Gpro widget.
 * 
 * @author eduardo.yanez
 */
public class GproWidgetProvider extends AppWidgetProvider {
    // Log tag
    private static final String TAG = GproWidgetProvider.class.getName();
    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        Log.d(TAG, "onUpdate");
        final int N = widgetIds.length;

        // For each widget...
        for (int i = 0; i < N; i++) {
            int widgetId = widgetIds[i];
            try {
                setUpWidget(context, appWidgetManager, widgetId, GproWidgetConfigure.loadManagerIdm(context));
            } catch (ParseException e) {
                Log.e(TAG, "Error happened getting information from GPRO: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Initializes the widget, buttons, and updates the information shown.
     * Gets the qualification information, and shows the manager's position in the grid and the offset time related to the pole position.
     */
    static void setUpWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, Integer managerIdm) throws ParseException {
        Log.d(TAG, "Setting up GproWidget [" + widgetId + "]");
        // Getting widget's views
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gpro_widget);

        // Making an intent to launch the main activity 
        //Intent intent = new Intent(context, GproGridViewer.class);
        Intent intent = new Intent(context, GproViewer.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Linking the intent's call to the button's onclick event
        Log.d(TAG, "Configuring grid button");
        views.setOnClickPendingIntent(R.id.grid_button, pendingIntent);
        
        // Crear un intent para lanzar una activity 
//        Intent intentRace = new Intent(context, GproRaceViewer.class);
//        intentRace.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
//        PendingIntent pendingIntentRace = PendingIntent.getActivity(context, 0, intentRace, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asociamos la llamada al intent anterior en el onclick del botón
//        Log.d(TAG, "Configuring race button");
//        views.setOnClickPendingIntent(R.id.race_button, pendingIntentRace);
        
        // Updating the text shown in the widget
        Log.d(TAG, "Updating widget info");
        String info = "";
        if (managerIdm != null) {
            Log.d(TAG, "Getting grid position");
            Position driver = findGridManagerPosition(context, managerIdm);
            if (driver != null) {
                info = String.format("%02d - %s", driver.getPosition(), driver.getTime().toString());
            } else {
                info = context.getString(R.string.not_qualified);
            }
        } else {
            Log.w(TAG, "There isn't a manager IDM configured");
            info = context.getString(R.string.not_qualified);
        }
        views.setTextViewText(R.id.text, info);

        // Updating the widget
        appWidgetManager.updateAppWidget(widgetId, views);
        Log.d(TAG, "Set up finished");
    }

    /**
     * Gets the qualification information from a manager, reading the grid information firstly.
     * 
     * @param managerIdm Manager's ID.
     * @return Qualification information, or null if the manager isn't qualified yet.
     */
    private static Position findGridManagerPosition(Context context, Integer managerIdm) throws ParseException {
        List<Position> drivers = GproDAO.findGridPositions(context); 
        for (Position driver : drivers) {
            if (driver.getIdm().equals(managerIdm)) {
                return driver;
            }
        }
        return null;
    }
}