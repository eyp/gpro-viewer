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

import android.app.AlertDialog;
import android.app.ListActivity;
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

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.exceptions.ParseException;

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
        new DownloadGridInfoTask(this).execute();
	}

	/**
	 * With this class connection to GPRO site & recover of information is asynchronous and thread-safe
	 */
    private class DownloadGridInfoTask extends AsyncTask<Void, Void, List<Position>> {
        private Context context;
        
        public DownloadGridInfoTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Recovers qualification information for every manager qualified. 
         */
        protected List<Position> doInBackground(Void... params) {
            try {
                Log.d(TAG, "Getting grid information...");
                return GproDAO.findGridPositions(context);
            } catch (ParseException e) {
                Log.w(TAG, "Error parsing grid information from GPRO", e);
                return null;
            }
        }

        /**
         * Updates view.
         */
        protected void onPostExecute(List<Position> drivers) {
            if (drivers == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(UIHelper.makeErrorMessage(context, context.getString(R.string.error_100)));
                alertDialog.setButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    return;
                } }); 
            } else {
                final Integer managerId = GproWidgetConfigure.loadManagerIdm(context);
                ArrayAdapter<Position> ad = new ArrayAdapter<Position>(context, R.layout.grid_line, drivers) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.grid_line, null);
                        }
                        Position driver = getItem(position);
                        if (driver != null) {
                            TextView positionText = (TextView) v.findViewById(R.id.line_position);
                            TextView nameText = (TextView) v.findViewById(R.id.line_driver_name);
                            TextView timeText = (TextView) v.findViewById(R.id.line_time);
                            if (driver.getPosition() == null) {
                                // Not qualified yet
                                positionText.setText("--");
                            } else {
                                positionText.setText(String.format("%02d", driver.getPosition()));
                            }
                            timeText.setText(String.format("%s", driver.getTime().toString()));
                            nameText.setText(String.format("%s - %d %s", driver.getName(), driver.getPoints(), context.getString(R.string.points)));
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
            }
        }
    }
}