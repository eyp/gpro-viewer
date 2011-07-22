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
package com.elpaso.android.gpro.parsers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.elpaso.android.gpro.beans.GridPosition;

/**
 * Parses the response from the service StartingGridXML which contains the current grid qualification status.
 * 
 * @author eduardo.yanez
 */
public class XmlGridParser extends DefaultHandler {
    private Boolean currentElement = false;
    private String currentValue = null;
    private GridPosition currentPosition = null;
    private List<GridPosition> grid;

    /**
     * Gets the grid information.
     */
    public List<GridPosition> getGrid() {
        return grid;
    }

    /**
     * Called when tag starts <manager>...
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = true;
        if (qName.equalsIgnoreCase("grid") || localName.equalsIgnoreCase("grid")) {
            this.grid = new ArrayList<GridPosition>();
        } else if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            this.currentPosition = new GridPosition();
        }
    }

    /**
     * Called when tag closing </manager>...
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentElement = false;
        if (qName.equalsIgnoreCase("position") || localName.equalsIgnoreCase("position")) {
            this.currentPosition.getQualificationTimeGrid().setPosition(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("name") || localName.equalsIgnoreCase("name")) {
            this.currentPosition.setName(currentValue);
        } else if (qName.equalsIgnoreCase("shortedname") || localName.equalsIgnoreCase("shortedname")) {
            this.currentPosition.setShortedName(currentValue);
        } else if (qName.equalsIgnoreCase("country") || localName.equalsIgnoreCase("country")) {
            this.currentPosition.setCountry(currentValue);
        } else if (qName.equalsIgnoreCase("idm") || localName.equalsIgnoreCase("idm")) {
            this.currentPosition.setIdm(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("tyresupplier") || localName.equalsIgnoreCase("tyresupplier")) {
            this.currentPosition.setTyreSupplier(currentValue);
        } else if (qName.equalsIgnoreCase("points") || localName.equalsIgnoreCase("points")) {
            this.currentPosition.setPoints(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("time") || localName.equalsIgnoreCase("time")) {
            this.currentPosition.getQualificationTimeGrid().setTime(currentValue);
        } else if (qName.equalsIgnoreCase("gap") || localName.equalsIgnoreCase("gap")) {
            this.currentPosition.getQualificationTimeGrid().setGap(currentValue);
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