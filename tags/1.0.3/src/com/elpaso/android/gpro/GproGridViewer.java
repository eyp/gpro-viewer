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

import com.elpaso.android.gpro.beans.GridPosition;
import com.elpaso.android.gpro.beans.Manager;
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
 * This screen shows the grid updated. List of managers who are qualified and their qualification times.
 * 
 * @author eduardo.yanez
 */
public class GproGridViewer extends ListActivity {
    private static final String TAG = GproGridViewer.class.getName();
    
	/** 
	 * Called on activity creation. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get caller widget's identifier, if it isn't valid then finish app
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
	 * With this class connection to GPRO site & recover of information is asynchronous and thread-safe
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
         * Shows a progress dialog while this task is recovering information.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = UIHelper.makeProgressDialog(context, getString(R.string.loading));
            progressDialog.show();
        }

        /**
         * Recovers qualification information for every manager qualified. 
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
         * Updates view.
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
                            positionText.setText(String.valueOf(driver.getQualificationTimeGrid().getPosition()));
                            timeText.setText(String.format("%s (%s)", driver.getQualificationTimeGrid().getTime(), driver.getQualificationTimeGrid().getGap()));
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