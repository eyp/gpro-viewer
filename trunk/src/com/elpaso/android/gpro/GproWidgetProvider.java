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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.exceptions.ParseException;

/**
 * Gpro widget.
 * 
 * @author eduardo.yanez
 */
public class GproWidgetProvider extends AppWidgetProvider {
    private static final Logger logger = LoggerFactory.getLogger("GproWidgetProvider");
    private List<Position> drivers;
    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        logger.debug("onUpdate");
        final int N = widgetIds.length;

        // For each widget...
        for (int i = 0; i < N; i++) {
            int widgetId = widgetIds[i];
            try {
                setUpWidget(context, appWidgetManager, widgetId);
            } catch (ParseException e) {
                logger.error("Error happened getting information from GPRO", e);
            }
        }
    }

    /**
     * Initializes the widget, buttons, and updates the information shown.
     * Gets the qualification information, and shows the manager's position in the grid and the offset time related to the pole position.
     */
    private void setUpWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) throws ParseException {
        logger.debug("Setting up GproWidget [{}]", widgetId);
        // Getting widget's views
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gpro_widget);

        // Making an intent to launch the main activity 
        logger.debug("Configuring widget arrow button");
        Intent intent = new Intent(context, GproViewer.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Linking the intent's call to the button's onclick event
        views.setOnClickPendingIntent(R.id.grid_button, pendingIntent);
        
        // Updating the text shown in the widget
        logger.debug("Updating widget info");
        String info = "";
        String managerName = "";
        Integer managerIdm = GproWidgetConfigure.loadManagerIdm(context);
        if (managerIdm != null) {
            logger.debug("Getting grid position");
            Position driver = this.findGridManagerPosition(context, managerIdm);
            Integer totalDriversQualified = this.drivers.size();
            if (driver != null) {
                info = String.format("%1$d (%3$d) - %2$s", driver.getPosition(), driver.getTime().toString(), totalDriversQualified);
                managerName = " - " + driver.getShortedName();
                logger.debug("Creating livery image");
                try {
                    logger.debug("Loading bitmap for livery image");
                    final String LIVERIES_URL = context.getString(R.string.liveries_url);
                    Bitmap bmp = NetHelper.loadImage(context.getResources(), LIVERIES_URL, driver.getLiveryImageUrl());
                    logger.debug("Setting livery image");
                    views.setImageViewBitmap(R.id.livery, bmp);
                } catch (MalformedURLException e) {
                    logger.warn("Malformed URL for livery image " + driver.getLiveryImageUrl() , e);
                } catch (IOException e) {
                    logger.warn("Can't load livery image: " + driver.getLiveryImageUrl() , e);
                }
            } else {
                info = context.getString(R.string.not_qualified);
            }
        } else {
            logger.debug("There isn't a manager IDM configured");
            info = context.getString(R.string.not_qualified);
        }
        views.setTextViewText(R.id.text, info);
        views.setTextViewText(R.id.title, String.format("%1$s%2$s", context.getString(R.string.app_name), managerName));

        // Updating the widget
        appWidgetManager.updateAppWidget(widgetId, views);
        logger.debug("Set up finished");
    }

    /**
     * Gets the qualification information from a manager, reading the grid information firstly.
     * 
     * @param managerIdm Manager's ID.
     * @return Qualification information, or null if the manager isn't qualified yet.
     */
    private Position findGridManagerPosition(Context context, Integer managerIdm) throws ParseException {
        this.drivers = GproDAO.findGridPositions(context);
        for (Position driver : drivers) {
            if (driver.getIdm().equals(managerIdm)) {
                return driver;
            }
        }
        return null;
    }
}