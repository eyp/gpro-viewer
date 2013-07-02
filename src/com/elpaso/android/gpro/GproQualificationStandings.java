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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private static final Logger logger = LoggerFactory.getLogger("GproQualificationStandings");
    
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
                if (logger.isDebugEnabled()) {
                    logger.debug("Getting qualifications information...");
                }
                return GproDAO.findQualificationStandings(context);
            } catch (ParseException e) {
                logger.warn("Error parsing qualification information from GPRO", e);
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
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                } });
            } else {
                final Integer managerId = GproWidgetConfigure.loadManagerIdm(context);
                ArrayAdapter<Q12Position> ad = new ArrayAdapter<Q12Position>(context, R.layout.q_standings_line, drivers) {
                    @Override
                    public View getView(int row, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.q_standings_line, null);
                        }
                        Q12Position position = getItem(row);
                        if (position != null) {
                            final String FLAGS_URL = parent.getContext().getString(R.string.flags_url);
                            final String SUPPLIERS_URL = parent.getContext().getString(R.string.suppliers_url);
                            TextView rowNumberText = (TextView) v.findViewById(R.id.line_position);
                            if (position.getQ1Position() == null) {
                                rowNumberText.setText("--");
                            } else {
                                // For each row...
                                Position q1Pos = position.getQ1Position();
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
                                        q1Flag.setImageBitmap(NetHelper.loadImage(context.getResources(), FLAGS_URL, q1Pos.getFlagImageUrl()));
                                    } catch (MalformedURLException e) {
                                        logger.warn("Malformed URL for flag image: " + q1Pos.getFlagImageUrl() , e);
                                    } catch (IOException e) {
                                        logger.warn("Can't load flag image: " + q1Pos.getFlagImageUrl() , e);
                                    }
                                } else {
                                    logger.warn("ImageView for Q1 flag not found!");
                                }
                                
                                // Only for landscape mode
                                ImageView q1Tyres = (ImageView) v.findViewById(R.id.q1_tyres);
                                if (q1Tyres != null) {
                                    try {
                                        q1Tyres.setImageBitmap(NetHelper.loadImage(context.getResources(), SUPPLIERS_URL, q1Pos.getTyreSupplierImageUrl()));
                                    } catch (MalformedURLException e) {
                                        logger.warn("Malformed URL for tyres image: " + q1Pos.getTyreSupplierImageUrl() , e);
                                    } catch (IOException e) {
                                        logger.warn("Can't load tyres image: " + q1Pos.getTyreSupplierImageUrl() , e);
                                    }
                                } else {
                                    logger.warn("ImageView for Q1 tyres not found!");
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
                                    q2TimeText.setText(String.format("%s", q2Pos.getTime().toString()));
                                    q2NameText.setText(String.format("%s", q2Pos.getShortedName()));
                                    if (q2Flag != null) {
                                        q2Flag.setVisibility(ImageView.VISIBLE);
                                    }
                                    
                                    if (q2Tyres != null) {
                                        q2Tyres.setVisibility(ImageView.VISIBLE);
                                    }
                                
                                    if (q2Flag != null) { 
                                        try {
                                            q2Flag.setImageBitmap(NetHelper.loadImage(context.getResources(), FLAGS_URL, q2Pos.getFlagImageUrl()));
                                        } catch (MalformedURLException e) {
                                            logger.warn("Malformed URL for flag image: " + q2Pos.getFlagImageUrl() , e);
                                        } catch (IOException e) {
                                            logger.warn("Can't load flag image: " + q2Pos.getFlagImageUrl() , e);
                                        }
                                    } else {
                                        logger.warn("ImageView for Q2 flag not found!");
                                    }

                                    // Only for landscape mode
                                    if (q2Tyres != null) {
                                        try {
                                            q2Tyres.setImageBitmap(NetHelper.loadImage(context.getResources(), SUPPLIERS_URL, q2Pos.getTyreSupplierImageUrl()));
                                        } catch (MalformedURLException e) {
                                            logger.warn("Malformed URL for tyres image: " + q2Pos.getTyreSupplierImageUrl() , e);
                                        } catch (IOException e) {
                                            logger.warn("Can't load tyres image: " + q2Pos.getTyreSupplierImageUrl() , e);
                                        }
                                    } else {
                                        logger.warn("ImageView for Q2 tyres not found!");
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