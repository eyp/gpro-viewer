package com.elpaso.android.gpro;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Activity para mostrar el estado de la carrera actual.
 * 
 * @author eduardo.yanez
 */
public class GproRaceViewer extends Activity {
    private static final String TAG = "GproRaceViewer";
    
	/** 
	 * Se llama cuando la activity es creada. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Recuperamos el identificador del widget que ha llamado, y si no es válido, terminamos.
		int appWidgetId = GproUtils.getWidgetId(this.getIntent());
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.w(TAG, "El identificador del widget no es válido");
            finish();
        } else {
            new DownloadRaceInfoTask(this).execute(appWidgetId);
        }
        
	}

    /**
     * Clase para que la conexión a la web, y la carga de los datos sea asíncrona y thread-safe.
     */
    private class DownloadRaceInfoTask extends AsyncTask<Integer, Void, String> {
        private Context context;
        
        public DownloadRaceInfoTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Leemos de la web la información de la carrera.
         */
        protected String doInBackground(Integer... appWidgets) {
            return GproUtils.getLightRaceInfo(context, appWidgets[0]);
        }

        /**
         * Actualizamos la vista con la información de la carrera.
         */
        protected void onPostExecute(String race) {
            ScrollView scroll = new ScrollView(this.context);
            TableLayout tl = new TableLayout(this.context);
            TextView tv = new TextView(this.context);
            tv.setText(race);
            tl.addView(tv);
            scroll.addView(tl);
            setContentView(scroll);
        }
    }
}