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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.beans.Manager;
import com.elpaso.android.gpro.exceptions.ConfigurationException;
import com.elpaso.android.gpro.exceptions.ParseException;
import com.elpaso.android.gpro.parsers.HtmlParser;
import com.elpaso.android.gpro.parsers.ParserHelper;
import com.elpaso.android.gpro.parsers.XmlGridParser;
import com.elpaso.android.gpro.parsers.XmlGroupManagersParser;
import com.elpaso.android.gpro.parsers.XmlQualificationsParser;

/**
 * Utility methods for recovering information from the GRPO site.
 * 
 * @author eduardo.yanez
 */
public class GproDAO {
    private static final String TAG = GproDAO.class.getName();
    private static final String ENCODING = "UTF-8";
    
    /**
     * Reads light race page content.
     *  
     * @param context Application context.
     * @param widgetId Widget's identifier.
     * @throws ConfigurationException if group information can't be calculated.
     */
    public static String getLightRaceInfo(Context context, int widgetId) throws ConfigurationException {
        HtmlParser parser = new HtmlParser();
        String group = GproWidgetConfigure.loadGroupId(context, widgetId);
        String raceInfo = parser.parseLightRacePage(getLightRacePageContent(group, context));
        return raceInfo;
    }
    
    /**
     * Gets a managers list who are already qualified, or an empty list if nobody has qualified yet.
     * 
     * @param context Application context.
     * @param widgetId Widget's identifier.
     */
    public static List<Position> findGridPositions(Context context, int widgetId) throws ParseException {
        List<Position> drivers = null;
        try {
            Log.d(TAG, "Parsing XML response for grid positions");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            String group = GproWidgetConfigure.loadGroupId(context, widgetId);
            if (group == null) {
                Log.d(TAG, "Group is null, do nothing");
                return new ArrayList<Position>();
            }
            Log.d(TAG, "Group ID: " + group);
            URL sourceUrl = new URL(getGridPage(group, context));

            XmlGridParser parser = new XmlGridParser();
            xr.setContentHandler(parser);
            xr.parse(new InputSource(ParserHelper.unscapeStream(sourceUrl.openStream())));
            drivers = parser.getGrid();
            Log.d(TAG, drivers.size() + " drivers are already qualified");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML grid service response", e);
            throw new ParseException("Error parsing XML grid service response");
        }
        return drivers;
    }
    
    /**
     * Reads standings for Q1 session.
     * 
     * @param context Application context.
     * @param widgetId Widget's identifier.
     */
    public static List<Position> findQualification1Standings(Context context, int widgetId) throws ParseException {
        List<Position> positions = new ArrayList<Position>();
        XmlQualificationsParser parser = parseQualificationsPage(context, widgetId);
        if (parser != null) {
            positions = parser.getQ1Standings();
        }
        return positions;
    }
    
    /**
     * Reads standings for Q2 session.
     * 
     * @param context Application context.
     * @param widgetId Widget's identifier.
     */
    public static List<Position> findQualification2Standings(Context context, int widgetId) throws ParseException {
        List<Position> positions = new ArrayList<Position>();
        XmlQualificationsParser parser = parseQualificationsPage(context, widgetId);
        if (parser != null) {
            positions = parser.getQ2Standings();
        }
        return positions;
    }
    
    /**
     * Reads standings for Q1 & Q2
     * 
     * @param context Application context.
     * @param widgetId Widget's identifier.
     */
    private static XmlQualificationsParser parseQualificationsPage(Context context, int widgetId) throws ParseException {
        XmlQualificationsParser parser = null;
        try {
            Log.d(TAG, "Parsing XML response for qualifications standings");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            String group = GproWidgetConfigure.loadGroupId(context, widgetId);
            if (group == null) {
                return null;
            }
            Log.d(TAG, "Group ID: " + group);
            URL sourceUrl = new URL(getQualificationsPage(group, context));

            parser = new XmlQualificationsParser();
            xr.setContentHandler(parser);
            xr.parse(new InputSource(ParserHelper.unscapeStream(sourceUrl.openStream())));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML qualifications service response", e);
            throw new ParseException("Error parsing XML qualifications service response");
        }
        return parser;
    }
    
    /**
     * Reads managers from the configured group.
     *  
     * @param context Application context.
     * @param widgetId Widget's identifier.
     */
    public static List<Manager> findGroupMembers(Context context, int widgetId) throws ParseException {
        List<Manager> managers = null;
        try {
            Log.d(TAG, "Parsing XML response for group members");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            String group = GproWidgetConfigure.loadGroupId(context, widgetId);
            Log.d(TAG, "Group ID: " + group);
            URL sourceUrl = new URL(getGroupMembersPage(group, context));

            XmlGroupManagersParser parser = new XmlGroupManagersParser();
            xr.setContentHandler(parser);
            xr.parse(new InputSource(ParserHelper.unscapeStream(sourceUrl.openStream())));
            managers = parser.getManagers();
            Log.d(TAG, managers.size() + " managers in group " + group);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML group members service response", e);
            throw new ParseException("Error parsing XML group members service response");
        }
        return managers;
    }
    
    /**
     * Reads managers from a group.
     * 
     * @param context Application context.
     * @param widgetId Widget's identifier.
     * @param groupType Group type (Rookie, Amateur, Pro, Master, Elite).
     * @param groupNumber For Elite this must empty.
     */
    public static List<Manager> findGroupMembers(Context context, int widgetId, String groupType, String groupNumber) throws ParseException {
        List<Manager> managers = null;
        try {
            Log.d(TAG, "Parsing XML response for group members");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            String group = String.format("%s - %s", groupType, groupNumber);
            Log.d(TAG, "Group ID: " + group);
            URL sourceUrl = new URL(getGroupMembersPage(group, context));

            XmlGroupManagersParser parser = new XmlGroupManagersParser();
            xr.setContentHandler(parser);
            xr.parse(new InputSource(ParserHelper.unscapeStream(sourceUrl.openStream())));
            managers = parser.getManagers();
            Log.d(TAG, managers.size() + " managers in group " + group);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML group members service response", e);
            throw new ParseException("Error parsing XML group members service response");
        }
        return managers;
    }
    
    /**
     * Lee el contenido de la página ligera de la carrera.
     * 
     * @param group String con el tipo de grupo y el número: <em>Rookie - 217</em>
     * @param context Contexto de la aplicación
     * @return El contenido de la página.
     */
    public static String getLightRacePageContent(String group, Context context) {
        String racePage = "";
        try {
            String request = "";
            if (context.getString(R.string.test).equals("on")) {
                request = context.getString(R.string.test_race_light_page);
            } else {
                request = context.getString(R.string.race_light_page) + URLEncoder.encode(group, ENCODING);
            }
            racePage = getData(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return racePage;
    }
    
    private static String getGridPage(String group, Context context) {
        String request = "";
        try {
            if (context.getString(R.string.test).equals("on")) {
                request = context.getString(R.string.test_service_grid_page);
            } else {
                request = context.getString(R.string.service_grid_page) + URLEncoder.encode(group, ENCODING);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return request;
    }
    
    private static String getQualificationsPage(String group, Context context) {
        String request = "";
        try {
            if (context.getString(R.string.test).equals("on")) {
                request = context.getString(R.string.test_service_qualifications_page);
            } else {
                request = context.getString(R.string.service_qualifications_page) + URLEncoder.encode(group, ENCODING);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return request;
    }
    
    private static String getGroupMembersPage(String group, Context context) {
        String request = "";
        try {
            if (context.getString(R.string.test).equals("on")) {
                request = context.getString(R.string.test_service_group_members_page);
            } else {
                request = context.getString(R.string.service_group_members_page) + URLEncoder.encode(group, ENCODING);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return request;
    }
    
    /**
     * Reads an URL content.
     * 
     * @param url URL to read.
     * @return a String with the page's content.
     */
    public static String getData(String url) {
        String pageContent = "";
        try {
            HttpGet httpRequest = new HttpGet(new URL(url).toURI());

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream input = bufHttpEntity.getContent();
            StringBuffer content = new StringBuffer();
            int b;
            while ((b = input.read()) != -1) {
                content.append((char) b);
            }
            pageContent = content.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageContent;
    }
}