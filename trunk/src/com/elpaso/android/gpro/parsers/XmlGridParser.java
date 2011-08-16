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
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.elpaso.android.gpro.beans.Position;

/**
 * Parses the response from the service StartingGridXML which contains the current grid qualification status.
 * 
 * @author eduardo.yanez
 */
public class XmlGridParser extends DefaultHandler {
    private static final String TAG = XmlGridParser.class.getName();
    
    protected Boolean currentElement = false;
    protected String currentValue = null;
    protected Position currentPosition = null;
    private List<Position> grid;

    /**
     * Gets the grid information.
     */
    public List<Position> getGrid() {
        return grid;
    }

    /**
     * Called when tag starts <manager>...
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = true;
        currentValue = "";
        if (qName.equalsIgnoreCase("grid") || localName.equalsIgnoreCase("grid")) {
            this.grid = new ArrayList<Position>();
        } else if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            this.currentPosition = new Position();
        }
    }

    /**
     * Called when tag closing </manager>...
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
            if (this.currentValue.substring(3).lastIndexOf(" ") > -1) {
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
            this.grid.add(this.currentPosition);
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
}
