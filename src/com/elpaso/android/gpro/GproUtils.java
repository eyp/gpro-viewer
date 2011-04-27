package com.elpaso.android.gpro;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Utilidades varias para el widget y la aplicación.
 * 
 * @author eduardo.yanez
 */
public class GproUtils {
    
    /**
     * Devuelve el identificador del widget que ha llamado a la actividad.
     * 
     * @return EL identificador del widget, o AppWidgetManager.INVALID_APPWIDGET_ID si no se encuentra.
     */
    static int getWidgetId(Intent intent) {
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return appWidgetId;
    }
    
    
    /**
     * Recupera la información de la carrera en formato ligero. 
     */
    static String getLightRaceInfo(Context context, int widgetId) {
        GproParser parser = new GproParser();
        String group = GproWidgetConfigure.loadGroupId(context, widgetId);
        String raceInfo = parser.parseLightRacePage(getLightRacePageContent(group, context));
        return raceInfo;
    }
    
    /**
     * Recupera la lista de pilotos que están clasificados en la parrilla de salida, o una lista
     * vacía si no hay ninguno. 
     */
    static List<GridPosition> findGridPositions(Context context, int widgetId) {
        GproParser parser = new GproParser();
        String group = GproWidgetConfigure.loadGroupId(context, widgetId);
        Log.d(GproUtils.class.getName(), "ID Grupo: " + group);
        List<GridPosition> drivers = parser.parseGridPage(getQualificationPage(group, context));
        return drivers;
    }
    
    /**
     * Obtiene la información de la clasificación de un piloto, consultando primero la parrilla de clasificación.
     * 
     * @param managerName El nombre del manager tal y como está en Gpro, del que queremos obtener la información.
     * @return La información de clasificación del piloto, o null si no se ha clasificado.
     */
    static GridPosition findGridManagerPosition(Context context, int widgetId, String managerName) {
        List<GridPosition> drivers = findGridPositions(context, widgetId); 
        for (GridPosition driver : drivers) {
            if (driver.getManagerName().equals(managerName)) {
                return driver;
            }
        }
        return null;
    }

    /**
     * Obtiene la información de la clasificación de un piloto.
     * 
     * @param drivers a lista de pilotos clasificados hasta el momento.
     * @param managerName El nombre del manager tal y como está en Gpro, del que queremos obtener la información.
     * @return La información de clasificación del piloto, o null si no se ha clasificado.
     */
    static GridPosition findGridManagerPosition(List<GridPosition> drivers, String managerName) {
        for (GridPosition driver : drivers) {
            if (driver.getManagerName().equals(managerName)) {
                return driver;
            }
        }
        return null;
    }

    /**
     * Lee el contenido de la página ligera de la carrera.
     * 
     * @param group String con el tipo de grupo y el número: <em>Rookie - 217</em>
     * @param context Contexto de la aplicación
     * @return El contenido de la página.
     */
    static String getLightRacePageContent(String group, Context context) {
        String racePage = "";
        try {
            String request = "";
            if (context.getString(R.string.test).equals("on")) {
                request = context.getString(R.string.test_race_light_page);
            } else {
                request = context.getString(R.string.actual_race_light_page) + URLEncoder.encode(group, "UTF-8");
            }
            racePage = getData(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return racePage;
    }
    
    private static String getQualificationPage(String group, Context context) {
        String gridPage = "";
        try {
            String request = "";
            if (context.getString(R.string.test).equals("on")) {
                request = context.getString(R.string.test_grid_page);
            } else {
                request = context.getString(R.string.actual_grid_page) + URLEncoder.encode(group, "UTF-8");
            }
            gridPage = getData(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return gridPage;
    }
    
    static ProgressDialog makeProgressDialog(Context context, CharSequence message) { 
        ProgressDialog progress = new ProgressDialog(context); 
        progress.setIndeterminate(true); 
        progress.setMessage(message); 
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progress;
    }
    
    static String getData(String url) {
        String pageContent = "";
        try {
            HttpGet httpRequest = new HttpGet(new URL(url).toURI());

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream input = bufHttpEntity.getContent();
            StringBuffer content = new StringBuffer();
            int b;
            while ((b = input.read()) != -1) {
                content.append((char) b);
            }
            pageContent = content.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageContent;
    }
    

    /*
    private void updateWidget() {
        dialog = GproUtils.makeProgressDialog(context, "Hooooooooooola");
        dialog.show();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    break;
                default:
                    break;
                }
            }
        };
        thread = new Thread() {
            public void run() {
                handler.sendEmptyMessage(1);
            };
        };
        thread.start();
    }*/
}
