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

import com.elpaso.android.gpro.beans.Position;

/**
 * Parser for Qualifying12StandingsXML service.<br>
 * <GRID>
 * <QUALIFICATION1>
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
 * </MANAGER>
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
 * </MANAGER>
 * </QUALIFICATION2>
 * </GRID>
 * 
 * @author eduardo.yanez
 */
public class XmlQualificationsParser extends XmlGridParser {
    
    private List<Position> q1;
    private List<Position> q2;
    private Boolean isQ1 = false;
    private Boolean isQ2 = false;

    public List<Position> getQ1Standings() {
        return q1;
    }

    public List<Position> getQ2Standings() {
        return q2;
    }

    /**
     * Called when tag starts <manager>...
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equalsIgnoreCase("qualification1") || localName.equalsIgnoreCase("qualification1")) {
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
     * Called when tag closing </manager>...
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        
        if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            if (this.isQ1) {
                this.q1.add(this.currentPosition);
            } else if (this.isQ2) {
                this.q2.add(this.currentPosition);
            }
        }
    }
}
