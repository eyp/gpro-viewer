package com.elpaso.android.gpro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
		int appWidgetId = UtilHelper.getWidgetId(this.getIntent());
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
        private ProgressDialog progressDialog;
        private Integer widgetId;
        
        public DownloadRaceInfoTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Mostramos un diálogo de 'Cargando...' o con el mensaje que sea.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = UIHelper.makeProgressDialog(context, getText(R.string.loading));
            progressDialog.show();
        }

        /**
         * Leemos de la web la información de la carrera.
         */
        @Override
        protected String doInBackground(Integer... appWidgets) {
            this.widgetId = appWidgets[0];
            return GproDAO.getLightRaceInfo(context, appWidgets[0]);
        }

        /**
         * Actualizamos la vista con la información de la carrera.
         */
        @Override
        protected void onPostExecute(String race) {
            ScrollView scroll = new ScrollView(this.context);
            TableLayout tl = new TableLayout(this.context);
            Button refresh = new Button(context);
            refresh.setText(R.string.refresh);
            refresh.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramView) {
                    new DownloadRaceInfoTask(context).execute(widgetId);
                }
            });
            TextView tv = new TextView(this.context);
            tv.setText(race);
            tv.setTextAppearance(context, R.style.boldText);
            tl.addView(refresh);
            tl.addView(tv);
            scroll.addView(tl);
            progressDialog.dismiss();
            setContentView(scroll);
        }
    }
}