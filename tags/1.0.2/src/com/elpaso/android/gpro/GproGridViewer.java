package com.elpaso.android.gpro;

import java.util.List;

import com.elpaso.android.gpro.beans.GridPosition;
import com.elpaso.android.gpro.exceptions.ParseException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Activity para mostrar la parrilla de salida completa. Es una lista con los pilotos, y sus tiempos de clasificación.
 * 
 * @author eduardo.yanez
 */
public class GproGridViewer extends ListActivity {
    private static final String TAG = GproGridViewer.class.getName();
    
	/** 
	 * Se llama cuando la activity es creada. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Recuperamos el identificador del widget que ha llamado, y si no es válido, terminamos.
		int appWidgetId = UtilHelper.getWidgetId(this.getIntent());
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.w(TAG, "Widget identifier is invalid");
            finish();
        } else {
            getListView().setDividerHeight(2);
            new DownloadGridInfoTask(this).execute(appWidgetId);
        }
	}

	/**
	 * Clase para que la conexión a la web, y la carga de los datos sea asíncrona y thread-safe.
	 */
    private class DownloadGridInfoTask extends AsyncTask<Integer, Void, List<GridPosition>> {
        private Context context;
        private Integer widgetId;
        private ProgressDialog progressDialog;
        
        public DownloadGridInfoTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Mostramos un diálogo de 'Cargando...' o con el mensaje que sea.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = UIHelper.makeProgressDialog(context, getString(R.string.loading));
            progressDialog.show();
        }

        /**
         * Leemos de la web los pilotos que están en la parrilla de clasificación
         */
        protected List<GridPosition> doInBackground(Integer... appWidgets) {
            this.widgetId = appWidgets[0];
            try {
                return GproDAO.findGridPositions(context, widgetId);
            } catch (ParseException e) {
                return null;
            }
        }

        /**
         * Actualizamos la lista de pilotos.
         */
        protected void onPostExecute(List<GridPosition> drivers) {
            if (drivers == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(UIHelper.makeErrorMessage(context, context.getString(R.string.error_100)));
                alertDialog.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    return;
                } }); 
            } else {
                final Integer managerId = GproWidgetConfigure.loadManagerIdm(context, widgetId);
                ArrayAdapter<GridPosition> ad = new ArrayAdapter<GridPosition>(context, R.layout.grid_line, drivers) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.grid_line, null);
                        }
                        GridPosition driver = getItem(position);
                        if (driver != null) {
                            TextView positionText = (TextView) v.findViewById(R.id.line_position);
                            TextView nameText = (TextView) v.findViewById(R.id.line_driver_name);
                            TextView timeText = (TextView) v.findViewById(R.id.line_time);
                            positionText.setText(String.valueOf(driver.getPosition()));
                            timeText.setText(driver.getTime() + " (" + driver.getGap() + ")");
                            nameText.setText(driver.getName());
                            if (driver.getIdm().equals(managerId)) {
                                nameText.setTextAppearance(context, R.style.highlightedText);
                                positionText.setTextAppearance(context, R.style.highlightedText);
                                timeText.setTextAppearance(context, R.style.highlightedText);
                            } else {
                                nameText.setTextAppearance(context, R.style.normalText);
                                positionText.setTextAppearance(context, R.style.normalText);
                                timeText.setTextAppearance(context, R.style.normalText);
                            }
                        }
                        return v;
                    }
                };
                setListAdapter(ad);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                try {
                    GproWidgetProvider.setUpWidget(context, appWidgetManager, widgetId, managerId);
                } catch (ParseException e) {
                    Log.e(TAG, "Error happened getting information from GPRO: " + e.getLocalizedMessage());
                }
            }
            progressDialog.dismiss();
        }
    }
}