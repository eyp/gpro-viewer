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

import com.elpaso.android.gpro.beans.GridPosition;
import com.elpaso.android.gpro.beans.Manager;
import com.elpaso.android.gpro.exceptions.ParseException;
import com.elpaso.android.gpro.parsers.HtmlParser;
import com.elpaso.android.gpro.parsers.ParserHelper;
import com.elpaso.android.gpro.parsers.XmlGridParser;
import com.elpaso.android.gpro.parsers.XmlGroupManagersParser;

/**
 * Utilidades varias para el widget y la aplicación.
 * 
 * @author eduardo.yanez
 */
public class GproDAO {
    
    private static final String TAG = GproDAO.class.getName();
    
    /**
     * Recupera la información de la carrera en formato ligero. 
     */
    public static String getLightRaceInfo(Context context, int widgetId) {
        HtmlParser parser = new HtmlParser();
        String group = GproWidgetConfigure.loadGroupId(context, widgetId);
        String raceInfo = parser.parseLightRacePage(getLightRacePageContent(group, context));
        return raceInfo;
    }
    
    /**
     * Recupera la lista de pilotos que están clasificados en la parrilla de salida, o una lista
     * vacía si no hay ninguno. 
     */
    public static List<GridPosition> findGridPositions(Context context, int widgetId) throws ParseException {
        List<GridPosition> drivers = null;
        if (context.getString(R.string.services).equals("on")) {
            try {
                Log.d(TAG, "Parsing XML response for grid positions");
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();
    
                String group = GproWidgetConfigure.loadGroupId(context, widgetId);
                if (group == null) {
                    Log.d(TAG, "Group is null, do nothing");
                    return new ArrayList<GridPosition>();
                }
                Log.d(TAG, "Group ID: " + group);
                URL sourceUrl = new URL(getQualificationPage(group, context));
    
                XmlGridParser parser = new XmlGridParser();
                xr.setContentHandler(parser);
                xr.parse(new InputSource(ParserHelper.unscapeStream(sourceUrl.openStream())));
                drivers = parser.getGrid();
                Log.d(TAG, drivers.size() + " drivers are already qualified");
            } catch (Exception e) {
                Log.e(TAG, "Error parsing XML grid service response", e);
                throw new ParseException("Error parsing XML grid service response");
            }
        } else {
            Log.d(TAG, "Parsing HTML response for grid positions");
            HtmlParser parser = new HtmlParser();
            String group = GproWidgetConfigure.loadGroupId(context, widgetId);
            Log.d(TAG, "Group ID: " + group);
            String gridPageContent = getData(getQualificationPage(group, context));
            drivers = parser.parseGridPage(gridPageContent);
            Log.d(TAG, drivers.size() + " drivers are already qualified");
        }
        return drivers;
    }
    
    /**
     * Recupera la lista de pilotos del grupo configurado. 
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
     * Recupera la lista de pilotos del grupo configurado. 
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
     * Obtiene la información de la clasificación de un piloto.
     * 
     * @param drivers a lista de pilotos clasificados hasta el momento.
     * @param managerName El nombre del manager tal y como está en Gpro, del que queremos obtener la información.
     * @return La información de clasificación del piloto, o null si no se ha clasificado.
     */
    public static GridPosition findGridManagerPosition(List<GridPosition> drivers, String managerName) {
        for (GridPosition driver : drivers) {
            if (driver.getName().equals(managerName)) {
                return driver;
            }
        }
        return null;
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
                request = context.getString(R.string.race_light_page) + URLEncoder.encode(group, "UTF-8");
            }
            racePage = getData(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return racePage;
    }
    
    private static String getQualificationPage(String group, Context context) {
        String request = "";
        try {
            if (context.getString(R.string.test).equals("on")) {
                if (context.getString(R.string.services).equals("on")) {
                    request = context.getString(R.string.test_service_grid_page);
                } else {
                    request = context.getString(R.string.test_grid_page);
                }
            } else {
                if (context.getString(R.string.services).equals("on")) {
                    request = context.getString(R.string.service_grid_page) + URLEncoder.encode(group, "UTF-8");
                } else {
                    request = context.getString(R.string.grid_page) + URLEncoder.encode(group, "UTF-8");
                }
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
                request = context.getString(R.string.service_group_members_page) + URLEncoder.encode(group, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return request;
    }
    
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
    
    /*
    private void updateWidget() {
        dialog = GproUtils.makeProgressDialog(context, "Hooooooooooola");
        dialog.show();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    break;
                default:
                    break;
                }
            }
        };
        thread = new Thread() {
            public void run() {
                handler.sendEmptyMessage(1);
            };
        };
        thread.start();
    }*/
}
