package com.elpaso.android.gpro;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
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
            showRace(appWidgetId);
        }
        
	}

    private void showRace(int appWidgetId) {
        String race = GproUtils.getLightRaceInfo(getBaseContext(), appWidgetId);
        ScrollView scroll = new ScrollView(this);
        TableLayout tl = new TableLayout(this);
        TextView tv = new TextView(this);
        tv.setText(race);
        tl.addView(tv);
        scroll.addView(tl);
        setContentView(scroll);
    }
}