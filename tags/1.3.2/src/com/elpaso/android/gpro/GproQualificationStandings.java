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

import java.net.MalformedURLException;
import java.net.URL;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.beans.Q12Position;
import com.elpaso.android.gpro.exceptions.ParseException;

/**
 * This screen shows the Q1 standings. List of managers who are qualified and their qualification times.
 * 
 * @author eduardo.yanez
 */
public class GproQualificationStandings extends ListActivity {
    private static final String TAG = GproQualificationStandings.class.getName();
    
	/** 
	 * Called on activity creation. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        new DownloadQualificationStandingsTask(this).execute();
	}

	
	@Override
    protected void onResume() {
        super.onResume();
        new DownloadQualificationStandingsTask(this).execute();
    }

    /**
	 * With this class connection to GPRO site & recover of information is asynchronous and thread-safe
	 */
    private class DownloadQualificationStandingsTask extends AsyncTask<Void, Void, List<Q12Position>> {
        private Context context;
        
        public DownloadQualificationStandingsTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Recovers qualification information for every manager qualified. 
         */
        protected List<Q12Position> doInBackground(Void... params) {
            try {
                Log.d(TAG, "Getting qualifications information...");
                return GproDAO.findQualificationStandings(context);
            } catch (ParseException e) {
                Log.w(TAG, "Error parsing qualification information from GPRO", e);
                return null;
            }
        }

        /**
         * Updates view.
         */
        protected void onPostExecute(List<Q12Position> drivers) {
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
                ArrayAdapter<Q12Position> ad = new ArrayAdapter<Q12Position>(context, R.layout.q_standings_line, drivers) {
                    @Override
                    public View getView(int row, View convertView, ViewGroup parent) {
                        Log.d(TAG, "Drawing row " + row);
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.q_standings_line, null);
                        }
                        Q12Position position = getItem(row);
                        if (position != null) {
                            Log.d(TAG, "There are drivers at row " + row);
                            TextView rowNumberText = (TextView) v.findViewById(R.id.line_position);
                            if (position.getQ1Position() == null) {
                                rowNumberText.setText("--");
                            } else {
                                // For each row...
                                Position q1Pos = position.getQ1Position();
                                Log.d(TAG, "Q1 cell: " + q1Pos);
                                // Position number
                                rowNumberText.setText(String.format("%02d", q1Pos.getPosition()));
                                
                                // Q1 cell
                                TextView q1NameText = (TextView) v.findViewById(R.id.q1_line_driver_name);
                                TextView q1TimeText = (TextView) v.findViewById(R.id.q1_line_time);
                                q1TimeText.setText(String.format("%s", q1Pos.getTime().toString()));
                                q1NameText.setText(String.format("%s", q1Pos.getShortedName()));

                                ImageView q1Flag = (ImageView) v.findViewById(R.id.q1_flag);
                                if (q1Flag != null) {
                                    try {
                                        q1Flag.setImageBitmap(UtilHelper.loadImage(new URL(parent.getContext().getString(R.string.site_url) + q1Pos.getFlagImageUrl())));
                                    } catch (MalformedURLException e) {
                                        Log.w(TAG, "Malformed URL for flag image: " + q1Pos.getFlagImageUrl() , e);
                                    }
                                }
                                
                                // Only for landscape mode
                                ImageView q1Tyres = (ImageView) v.findViewById(R.id.q1_tyres);
                                if (q1Tyres != null) {
                                    try {
                                        q1Tyres.setImageBitmap(UtilHelper.loadImage(new URL(parent.getContext().getString(R.string.site_url) + q1Pos.getTyreSupplierImageUrl())));
                                    } catch (MalformedURLException e) {
                                        Log.w(TAG, "Malformed URL for tyres image: " + q1Pos.getTyreSupplierImageUrl() , e);
                                    }
                                }
                                
                                // Highlight manager in Q1 cell
                                if (q1Pos.getIdm().equals(managerId)) {
                                    q1NameText.setTextAppearance(context, R.style.highlightedText);
                                    q1TimeText.setTextAppearance(context, R.style.highlightedText);
                                } else {
                                    q1NameText.setTextAppearance(context, R.style.normalText);
                                    q1TimeText.setTextAppearance(context, R.style.normalText);
                                }

                                // Q2 cell
                                TextView q2NameText = (TextView) v.findViewById(R.id.q2_line_driver_name);
                                TextView q2TimeText = (TextView) v.findViewById(R.id.q2_line_time);
                                ImageView q2Flag = (ImageView) v.findViewById(R.id.q2_flag);
                                ImageView q2Tyres = (ImageView) v.findViewById(R.id.q2_tyres);
                                if (position.getQ2Position() == null) {
                                    q2NameText.setText("----------");
                                    q2TimeText.setText("--:--.---");
                                    if (q2Flag != null) {
                                        q2Flag.setVisibility(ImageView.INVISIBLE);
                                    }
                                    
                                    if (q2Tyres != null) {
                                        q2Tyres.setVisibility(ImageView.INVISIBLE);
                                    }
                                } else {
                                    Position q2Pos = position.getQ2Position();
                                    Log.d(TAG, "Q2 cell: " + q2Pos);
                                    
                                    q2TimeText.setText(String.format("%s", q2Pos.getTime().toString()));
                                    q2NameText.setText(String.format("%s", q2Pos.getShortedName()));
                                
                                    if (q2Flag != null) { 
                                        try {
                                            q2Flag.setImageBitmap(UtilHelper.loadImage(new URL(parent.getContext().getString(R.string.site_url) + q2Pos.getFlagImageUrl())));
                                        } catch (MalformedURLException e) {
                                            Log.w(TAG, "Malformed URL for flag image: " + q2Pos.getFlagImageUrl() , e);
                                        }
                                    }

                                    // Only for landscape mode
                                    if (q2Tyres != null) {
                                        try {
                                            q2Tyres.setImageBitmap(UtilHelper.loadImage(new URL(parent.getContext().getString(R.string.site_url) + q2Pos.getTyreSupplierImageUrl())));
                                        } catch (MalformedURLException e) {
                                            Log.w(TAG, "Malformed URL for tyres image: " + q2Pos.getTyreSupplierImageUrl() , e);
                                        }
                                    }
                                    
                                    // Highlight manager in Q2 cell
                                    if (q2Pos.getIdm().equals(managerId)) {
                                        q2NameText.setTextAppearance(context, R.style.highlightedText);
                                        q2TimeText.setTextAppearance(context, R.style.highlightedText);
                                    } else {
                                        q2NameText.setTextAppearance(context, R.style.normalText);
                                        q2TimeText.setTextAppearance(context, R.style.normalText);
                                    }
                                }
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