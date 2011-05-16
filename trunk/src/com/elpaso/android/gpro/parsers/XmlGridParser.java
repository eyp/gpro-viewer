package com.elpaso.android.gpro.parsers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.elpaso.android.gpro.beans.GridPosition;

/**
 * Parsea las respuestas del servicio XML de GPRO que contiene la información de la parrilla de salida.
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
     * Called when tag starts <name>...
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
     * Called when tag closing </name>...
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentElement = false;
        if (qName.equalsIgnoreCase("position") || localName.equalsIgnoreCase("position")) {
            this.currentPosition.setPosition(Integer.valueOf(currentValue));
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
            this.currentPosition.setTime(currentValue);
        } else if (qName.equalsIgnoreCase("gap") || localName.equalsIgnoreCase("gap")) {
            this.currentPosition.setGap(currentValue);
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
