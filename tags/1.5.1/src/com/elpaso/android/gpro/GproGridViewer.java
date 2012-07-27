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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.exceptions.ParseException;

/**
 * This screen shows the grid updated. List of managers who are qualified and their qualification times.
 * 
 * @author eduardo.yanez
 */
public class GproGridViewer extends ListActivity {
    private static final Logger logger = LoggerFactory.getLogger(GproQualificationStandings.class);
    
	/** 
	 * Called on activity creation. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        new DownloadGridInfoTask(this).execute();
	}

    @Override
    protected void onResume() {
        super.onResume();
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
                if (logger.isDebugEnabled()) {
                    logger.debug("Getting grid information...");
                }
                return GproDAO.findGridPositions(context);
            } catch (ParseException e) {
                logger.warn("Error parsing grid information from GPRO", e);
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
                            final String FLAGS_URL = parent.getContext().getString(R.string.flags_url);
                            final String SUPPLIERS_URL = parent.getContext().getString(R.string.suppliers_url);
                            final String LIVERIES_URL = parent.getContext().getString(R.string.liveries_url);
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

                            ImageView livery = (ImageView) v.findViewById(R.id.livery);
                            if (livery != null) {
                                try {
                                    Bitmap bmp = NetHelper.loadImage(LIVERIES_URL, driver.getLandscapeLiveryImageUrl());
                                    livery.setImageBitmap(bmp);
                                } catch (MalformedURLException e) {
                                    logger.warn("Malformed URL for livery image: " + driver.getLandscapeLiveryImageUrl() , e);
                                } catch (IOException e) {
                                    logger.warn("Can't load livery image: " + driver.getLandscapeLiveryImageUrl() , e);
                                }
                            }
                            
                            ImageView flag = (ImageView) v.findViewById(R.id.flag);
                            if (flag != null) {
                                try {
                                    flag.setImageBitmap(NetHelper.loadImage(FLAGS_URL, driver.getFlagImageUrl()));
                                } catch (MalformedURLException e) {
                                    logger.warn("Malformed URL for flag image: " + driver.getFlagImageUrl() , e);
                                } catch (IOException e) {
                                    logger.warn("Can't load flag image: " + driver.getLiveryImageUrl() , e);
                                }
                            }
                            
                            // Only for landscape mode
                            ImageView tyres = (ImageView) v.findViewById(R.id.tyres);
                            if (tyres != null) {
                                try {
                                    tyres.setImageBitmap(NetHelper.loadImage(SUPPLIERS_URL, driver.getTyreSupplierImageUrl()));
                                } catch (MalformedURLException e) {
                                    logger.warn("Malformed URL for tyres image: " + driver.getTyreSupplierImageUrl() , e);
                                } catch (IOException e) {
                                    logger.warn("Can't load tyres image: " + driver.getTyreSupplierImageUrl() , e);
                                }
                            }
                            
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