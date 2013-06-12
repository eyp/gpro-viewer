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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.elpaso.android.gpro.exceptions.ConfigurationException;

/**
 * @author eduardo.yanez
 */
public class GproTextRaceViewer extends Activity {
    private static final Logger logger = LoggerFactory.getLogger("GproTextRaceViewer");

    /** 
     * Called when the activity is first created. 
     **/
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scroll = new ScrollView(this.getParent());
        WebView view = new WebView(this.getParent());
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setSupportZoom(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.setWebChromeClient(new WebChromeClient());
        try {
            String managerGroup = GproWidgetConfigure.loadGroupId(this.getParent());
            String url = getString(R.string.race_light_page) + URLEncoder.encode(managerGroup, "UTF-8");
            view.loadUrl(url);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error loading race page", e);
        } catch (ConfigurationException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(this.getParent()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(UIHelper.makeErrorMessage(this.getParent(), e.getLocalizedMessage()));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                } 
            });
            logger.warn("Error reading light race information from GPRO", e);
        }
        scroll.addView(view);
        setContentView(scroll);
    }
}
