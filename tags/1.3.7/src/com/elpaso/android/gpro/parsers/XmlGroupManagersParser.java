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

import com.elpaso.android.gpro.beans.Manager;

/**
 * Parsea la respuesta del servicio XML de GPRO que contiene los miembros de un grupo.
 * 
 * <GROUP>
 * <MANAGER>
 * <FIRSTNAME>Jose</FIRSTNAME>
 * <LASTNAME>Antonio</LASTNAME>
 * <COUNTRY>Spain</COUNTRY>
 * <IDM>10985</IDM>
 * </MANAGER>
 * </GROUP>
 * 
 * @author eduardo.yanez
 */
public class XmlGroupManagersParser extends DefaultHandler {
    private Boolean currentElement = false;
    private String currentValue = "";
    private Manager currentManager = null;
    private List<Manager> managers;

    /**
     * Gets the group's managers.
     */
    public List<Manager> getManagers() {
        return this.managers;
    }

    /**
     * Called when tag starts <name>...
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = true;
        currentValue = "";
        if (localName.equalsIgnoreCase("group")) {
            this.managers = new ArrayList<Manager>();
        } else if (localName.equalsIgnoreCase("manager")) {
            this.currentManager = new Manager();
        }
    }

    /**
     * Called when tag closing </name>...
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentElement = false;
        if (qName.equalsIgnoreCase("firstname") || localName.equalsIgnoreCase("firstname")) {
            this.currentManager.setName(currentValue);
        } else if (qName.equalsIgnoreCase("lastname") || localName.equalsIgnoreCase("lastname")) {
            this.currentManager.setLastName(currentValue);
        } else if (qName.equalsIgnoreCase("country") || localName.equalsIgnoreCase("country")) {
            this.currentManager.setCountry(currentValue);
        } else if (qName.equalsIgnoreCase("idm") || localName.equalsIgnoreCase("idm")) {
            this.currentManager.setIdm(Integer.valueOf(currentValue));
        } else if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            this.managers.add(this.currentManager);
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
