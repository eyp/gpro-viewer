package com.elpaso.android.gpro;

import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Activity para mostrar la parrilla de salida completa.
 * 
 * @author eduardo.yanez
 */
public class GproGridViewer extends Activity {
    private static final String TAG = "GproGridViewer";
    
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
            showGrid(appWidgetId);
        }
        
	}

    private void showGrid(int appWidgetId) {
        ScrollView scroll = new ScrollView(this);
        TableLayout tl = new TableLayout(this);
        List<Driver> drivers = GproUtils.findGridDrivers(getBaseContext(), appWidgetId);
        for (Driver driver : drivers) {
            TextView tv = new TextView(this);
            String row = String.format("%2d: %s, %s (%s)", driver.getPosition(), driver.getName(), driver.getTime(), driver.getOffset()); 
            tv.setText(row);
            tl.addView(tv);
        }
        scroll.addView(tl);
        setContentView(scroll);
    }
}