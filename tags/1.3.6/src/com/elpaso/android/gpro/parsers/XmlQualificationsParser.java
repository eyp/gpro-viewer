/*
 * Copyright 2011 Eduardo Yáñez Parareda
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elpaso.android.gpro.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.elpaso.android.gpro.beans.Position;
import com.elpaso.android.gpro.beans.Q12Position;

/**
 * Parser for Qualifying12StandingsXML service.<br>
 * <GRID>
 * <QUALIFICATION1>
 * <MANAGER>
 * <POSITION>1</POSITION>
 * <NAME>Kamil Wojtaszek</NAME>
 * <SHORTEDNAME>K. Wojtaszek</SHORTEDNAME>
 * <COUNTRY>Poland</COUNTRY>
 * <IDM>24790</IDM>
 * <CHAMPIONSHIPS>0</CHAMPIONSHIPS>
 * <TYRESUPPLIER>Pipirelli</TYRESUPPLIER>
 * <POINTS>73</POINTS>
 * <TIME>1:28.219</TIME>
 * <GAP>+0.000</GAP>
 * <FLAG_URL>/images/country/pl.gif</FLAG_URL>
 * <LIVERY_URL>/images/245/car.gif</LIVERY_URL>
 * <TYRESUPPLIER_URL>/images/suppliers/initials/pipirelli.gif</TYRESUPPLIER_URL>
 * </QUALIFICATION1>
 * <QUALIFICATION2>
 * <MANAGER>
 * <POSITION>1</POSITION>
 * <NAME>Zdenek Hladik</NAME>
 * <SHORTEDNAME>Z. Hladik</SHORTEDNAME>
 * <COUNTRY>Czech Republic</COUNTRY>
 * <IDM>52355</IDM>
 * <CHAMPIONSHIPS>2</CHAMPIONSHIPS>
 * <TYRESUPPLIER>Bridgerock</TYRESUPPLIER>
 * <POINTS>68</POINTS>
 * <TIME>1:20.706</TIME>
 * <GAP>+0.000</GAP>
 * <FLAG_URL>/images/country/pl.gif</FLAG_URL>
 * <LIVERY_URL>/images/245/car.gif</LIVERY_URL>
 * <TYRESUPPLIER_URL>/images/suppliers/initials/pipirelli.gif</TYRESUPPLIER_URL>
 * </MANAGER>
 * </QUALIFICATION2>
 * </GRID>
 * 
 * @author eduardo.yanez
 */
public class XmlQualificationsParser extends DefaultHandler {
    private static final String TAG = "XmlQualificationsParser";
    
    private List<Position> q1;
    private List<Position> q2;
    private List<Q12Position> q12;
    private Map<String, Position> auxPositions = new HashMap<String, Position>();
    private Boolean isQ1 = false;
    private Boolean isQ2 = false;
    private Boolean currentElement = false;
    private String currentValue = null;
    private Position currentPosition = null;
    private List<Position> grid;

    /**
     * After parsing the XML you can call this method in order to get the information for the grid standings.
     */
    public List<Position> getGrid() {
        return grid;
    }

    /**
     * After parsing the XML you can call this method in order to get the information for each row of Q1 & Q2 standings.
     *  
     * @return A list of {@link Q12Position}. Each one contains two {@link Position} objects, one for position N in Q1, and 
     * another for the same N position in Q2. If there isn't a Q2 position, then the object is will be null.
     */
    public List<Q12Position> getQualificationStandings() {
        return q12;
    }

    /**
     * After parsing the XML you can call this method in order to get the information for Q1 standings.
     */
    public List<Position> getQ1Standings() {
        return q1;
    }

    /**
     * After parsing the XML you can call this method in order to get the information for Q2 standings.
     */
    public List<Position> getQ2Standings() {
        return q2;
    }

    /**
     * Called when tag starts &lt;manager&gt;, &lt;grid&gt;, etc.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = true;
        currentValue = "";
        if (qName.equalsIgnoreCase("grid") || localName.equalsIgnoreCase("grid")) {
            this.q12 = new ArrayList<Q12Position>();
            this.grid = new ArrayList<Position>();
        } else if (qName.equalsIgnoreCase("qualification1") || localName.equalsIgnoreCase("qualification1")) {
            this.q1 = new ArrayList<Position>();
            this.isQ1 = true;
            this.isQ2 = false;
        } else if (qName.equalsIgnoreCase("qualification2") || localName.equalsIgnoreCase("qualification2")) {
            this.q2 = new ArrayList<Position>();
            this.isQ1 = false;
            this.isQ2 = true;
        } else if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            this.currentPosition = new Position();
        }
    }

    /**
     * Called when tag is closed &lt;/manager&gt;, &lt;/grid&gt;, etc.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentElement = false;
        if (qName.equalsIgnoreCase("position") || localName.equalsIgnoreCase("position")) {
            if (!"".equals(currentValue.trim())) {
                this.currentPosition.setPosition(Integer.valueOf(currentValue));
            }
        } else if (qName.equalsIgnoreCase("name") || localName.equalsIgnoreCase("name")) {
            this.currentPosition.setName(currentValue);
        } else if (qName.equalsIgnoreCase("shortedname") || localName.equalsIgnoreCase("shortedname")) {
            // Since very long names Removes the second surname of some shorted names in order to show right the information on the screen
            String shortedName = this.currentValue; 
            if (this.currentValue.length() >= 12 && this.currentValue.substring(3).lastIndexOf(" ") > -1) {
                shortedName = this.currentValue.substring(0, 2) + this.currentValue.substring(3, this.currentValue.substring(3).lastIndexOf(" ") + 3);
            }
            this.currentPosition.setShortedName(shortedName);
        } else if (qName.equalsIgnoreCase("country") || localName.equalsIgnoreCase("country")) {
            this.currentPosition.setCountry(currentValue);
        } else if (qName.equalsIgnoreCase("idm") || localName.equalsIgnoreCase("idm")) {
            this.currentPosition.setIdm(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("championships") || localName.equalsIgnoreCase("championships")) {
            this.currentPosition.setChampionships(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("tyresupplier") || localName.equalsIgnoreCase("tyresupplier")) {
            this.currentPosition.setTyreSupplier(currentValue);
        } else if (qName.equalsIgnoreCase("points") || localName.equalsIgnoreCase("points")) {
            this.currentPosition.setPoints(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("time") || localName.equalsIgnoreCase("time")) {
            if (!"".equals(currentValue.trim())) {
                this.currentPosition.getTime().setTime(currentValue);
            }
        } else if (qName.equalsIgnoreCase("gap") || localName.equalsIgnoreCase("gap")) {
            if (!"".equals(currentValue.trim())) {
                this.currentPosition.getTime().setGap(currentValue);
            }
        } else if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            if (this.isQ1) {
                this.q1.add(this.currentPosition);
                this.auxPositions.put("Q1-" + this.currentPosition.getPosition(), this.currentPosition);
            } else if (this.isQ2) {
                this.q2.add(this.currentPosition);
                this.auxPositions.put("Q2-" + this.currentPosition.getPosition(), this.currentPosition);
            }
        } else if (qName.equalsIgnoreCase("grid") || localName.equalsIgnoreCase("grid")) {
            // Makes the list of Q12Positions
            this.buildQ12ListPositions();
            this.buildGrid();
            this.auxPositions.clear();
        } else if (qName.equalsIgnoreCase("flag_url") || localName.equalsIgnoreCase("flag_url")) {
            this.currentPosition.setFlagImageUrl(this.currentValue);
        } else if (qName.equalsIgnoreCase("LIVERY_URL") || localName.equalsIgnoreCase("LIVERY_URL")) {
            this.currentPosition.setLiveryImageUrl(this.currentValue);
            this.currentPosition.setLandscapeLiveryImageUrl(this.currentValue.replace("car.gif", "car_horiz.gif"));
        } else if (qName.equalsIgnoreCase("TYRESUPPLIER_URL") || localName.equalsIgnoreCase("TYRESUPPLIER_URL")) {
            this.currentPosition.setTyreSupplierImageUrl(this.currentValue);
        }
    }

    /**
     * Called to get tag characters ( ex:- <name>AndroidPeople</name>
     * -- to get AndroidPeople Character )
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentElement) {
            currentValue = new String(ch, start, length);
            currentElement = false;
        }
    }

    /**
     * For each row builds a pair with Q1 position i & Q2 position i
     */
    private void buildQ12ListPositions() {
        for (int i = 1; i <= 40; i++) {
            Position qPos = this.auxPositions.get("Q1-" + i);
            // If there is a manager qualified in this Q1's position, builds a new pair
            if (qPos != null) {
                Q12Position q12Pos = new Q12Position();
                q12Pos.setQ1Position(qPos);
                qPos = this.auxPositions.get("Q2-" + i);
                if (qPos != null) {
                    q12Pos.setQ2Position(qPos);
                }
                this.q12.add(q12Pos);
            }
        }
    }
    
    /**
     * For each manager qualified in Q1, look for his Q2's time and add it to his Q1's time. Then
     * order the list by total ascending time.
     */
    private void buildGrid() {
        DateTimeFormatter dtfTime = new DateTimeFormatterBuilder()
            .appendMinuteOfHour(1).appendLiteral(":")
            .appendSecondOfMinute(2).appendLiteral(".")
            .appendMillisOfSecond(3).toFormatter();
        for (Position q1Pos : this.q1) {
            Position q2Pos = this.q2.get(this.q2.indexOf(q1Pos));
            if (q2Pos.getTime().getTime() != null) {
                DateTime q1Time = DateTime.parse(q1Pos.getTime().getTime(), dtfTime);
                DateTime gridTime = q1Time.plus(DateTime.parse(q2Pos.getTime().getTime(), dtfTime).getMillis());
                Position gridPos = new Position(q1Pos);
                gridPos.setTime(gridTime.toString(dtfTime));
                this.grid.add(gridPos);
            }
        }
        
        // Sorts the list through the total time calculated in the previous step
        Collections.sort(this.grid, new Comparator<Position>() {
            public int compare(Position pos1, Position pos2) {
                return pos1.getTime().getTime().compareTo(pos2.getTime().getTime());
            }
        });
        
        DateTimeFormatter dtfGap = new DateTimeFormatterBuilder()
            .appendLiteral("+")
            .appendSecondOfMinute(1).appendLiteral(".")
            .appendMillisOfSecond(3).toFormatter();

        // Updates the position because positions have Q1 position number. Also 
        // calculates gap times.
        Position polePosition = this.grid.get(0);
        DateTime poleTime = DateTime.parse(polePosition.getTime().getTime(), dtfTime);
        for (int i = 0; i < this.grid.size(); i++) {
            Position pos = this.grid.get(i);
            pos.setPosition(i + 1);
            DateTime time = DateTime.parse(pos.getTime().getTime(), dtfTime);
            DateTime gap = time.minus(poleTime.getMillis());
            pos.setGap(gap.toString(dtfGap));
        }
    }
}
