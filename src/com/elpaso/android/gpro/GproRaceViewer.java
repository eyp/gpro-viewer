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

import android.app.Activity;
import android.app.AlertDialog;
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

import com.elpaso.android.gpro.exceptions.ConfigurationException;

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
        new DownloadRaceInfoTask(this.getParent()).execute();
	}

    /**
     * Class used to have an asynchronous connection and thread-safe.
     */
    private class DownloadRaceInfoTask extends AsyncTask<Void, Void, String> {
        private Context context;
        
        public DownloadRaceInfoTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Reads race's info from light page.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d(TAG, "Getting light race information from GPRO");
                return GproDAO.getLightRaceInfo(context);
            } catch (ConfigurationException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(UIHelper.makeErrorMessage(context, e.getLocalizedMessage()));
                alertDialog.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    return;
                } });
                Log.w(TAG, "Error reading light race information from GPRO", e);
                return null;
            }
        }

        /**
         * Updates the view with race info read at doInBackground.
         */
        @Override
        protected void onPostExecute(String race) {
            ScrollView scroll = new ScrollView(this.context);
            TableLayout tl = new TableLayout(this.context);
            Button refresh = new Button(context);
            refresh.setText(R.string.refresh);
            refresh.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramView) {
                    new DownloadRaceInfoTask(context).execute();
                }
            });
            TextView tv = new TextView(this.context);
            tv.setText(race);
            tv.setTextAppearance(context, R.style.boldText);
            tl.addView(refresh);
            tl.addView(tv);
            scroll.addView(tl);
            setContentView(scroll);
        }
    }
}