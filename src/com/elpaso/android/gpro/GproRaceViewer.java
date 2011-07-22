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

import com.elpaso.android.gpro.exceptions.ConfigurationException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Activity which shows the current race's lap.
 * 
 * @author eduardo.yanez
 */
public class GproRaceViewer extends Activity {
    private static final String TAG = "GproRaceViewer";
    
	/** 
	 * Called when this activity is created. 
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
     * Class used to have an asynchronous connection and thread-safe.
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
         * Shows a progress dialog.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = UIHelper.makeProgressDialog(context, getString(R.string.loading));
            progressDialog.show();
        }

        /**
         * Reads race's info from light page.
         */
        @Override
        protected String doInBackground(Integer... appWidgets) {
            this.widgetId = appWidgets[0];
            try {
                return GproDAO.getLightRaceInfo(context, appWidgets[0]);
            } catch (ConfigurationException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(UIHelper.makeErrorMessage(context, e.getLocalizedMessage()));
                alertDialog.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    return;
                } });
                return null;
            }
        }

        /**
         * Updates the view with race info read at doInBackground.
         */
        @Override
        protected void onPostExecute(String race) {
            if (race == null) {
                progressDialog.dismiss();
                Log.w(TAG, "Race info can't be read");
                finish();
            } else {
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
}