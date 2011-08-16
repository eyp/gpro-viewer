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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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
public class XmlQualificationsParser extends XmlGridParser {
    
    private List<Position> q1;
    private List<Position> q2;
    private List<Q12Position> q12;
    private Map<String, Position> auxPositions = new HashMap<String, Position>();
    private Boolean isQ1 = false;
    private Boolean isQ2 = false;

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
        super.startElement(uri, localName, qName, attributes);

        if (qName.equalsIgnoreCase("grid") || localName.equalsIgnoreCase("grid")) {
            this.q12 = new ArrayList<Q12Position>();
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
        super.endElement(uri, localName, qName);
        
        if (qName.equalsIgnoreCase("manager") || localName.equalsIgnoreCase("manager")) {
            if (this.isQ1) {
                this.q1.add(this.currentPosition);
                this.auxPositions.put("Q1-" + this.currentPosition.getPosition(), this.currentPosition);
            } else if (this.isQ2) {
                this.q2.add(this.currentPosition);
                this.auxPositions.put("Q2-" + this.currentPosition.getPosition(), this.currentPosition);
            }
        } else if (qName.equalsIgnoreCase("grid") || localName.equalsIgnoreCase("grid")) {
            // Makes the list of Q12Positions
            for (int i = 1; i <= 40; i++) {
                Position qPos = this.auxPositions.get("Q1-" + i);
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
            this.auxPositions.clear();
        } else if (qName.equalsIgnoreCase("flag_url") || localName.equalsIgnoreCase("flag_url")) {
            this.currentPosition.setTyreSupplierImageUrl(this.currentValue);
        } else if (qName.equalsIgnoreCase("LIVERY_URL") || localName.equalsIgnoreCase("LIVERY_URL")) {
            this.currentPosition.setLiveryImageUrl(this.currentValue);
        } else if (qName.equalsIgnoreCase("TYRESUPPLIER_URL") || localName.equalsIgnoreCase("TYRESUPPLIER_URL")) {
            this.currentPosition.setTyreSupplierImageUrl(this.currentValue);
        }
    }
}
