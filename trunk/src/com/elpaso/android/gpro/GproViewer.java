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

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Main view... This view is a tabbed panel with several tabs each one will show an activity, i.e. the grid,
 * qualification times and so on.
 * 
 * @author eduardo.yanez
 */
public class GproViewer extends TabActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Resource object to get Drawables
        Resources res = getResources(); 
        // The activity TabHost
        TabHost tabHost = getTabHost();  

        // Initialize a TabSpec for each tab and add it to the TabHost
        Intent intent = null;
        TabHost.TabSpec spec = null;
        
        intent = new Intent().setClass(this, GproGridViewer.class);
        spec = tabHost.newTabSpec("grid").setIndicator(this.getString(R.string.view_grid), res.getDrawable(R.drawable.ic_tab_timer)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, GproQualificationStandings.class);
        spec = tabHost.newTabSpec("qualification_times").setIndicator(this.getString(R.string.qualification12), res.getDrawable(R.drawable.ic_tab_timer)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, GproRaceViewer.class);
        spec = tabHost.newTabSpec("race").setIndicator(this.getString(R.string.race), res.getDrawable(R.drawable.ic_tab_flag)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, GproTextRaceViewer.class);
        spec = tabHost.newTabSpec("light_race").setIndicator(this.getString(R.string.light_race), res.getDrawable(R.drawable.ic_tab_flag)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}
