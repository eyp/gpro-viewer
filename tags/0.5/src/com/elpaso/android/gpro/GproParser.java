/**
 * 
 */
package com.elpaso.android.gpro;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parsea las páginas en busca de información.
 * 
 * @author eduardo.yanez
 */
class GproParser {

    int currentIdx = 0;

    /**
     * Parsea la página de clasificación y devuelve la lista de pilotos y la información de su clasificación.
     * 
     * @param gridPage Página con la parrilla de clasificación que se quiere parsear.
     * @return La lista de pilotos, o una lista vacía si no hay ninguno clasificado.
     */
    List<Driver> parseGridPage(String gridPage) {
        this.currentIdx = 0;
        List<Driver> grid = new ArrayList<Driver>();
        Driver driver = this.nextDriver(gridPage);
        int position = 0;
        while (driver != null) {
            driver.setPosition(++position);
            grid.add(driver);
            driver = this.nextDriver(gridPage);
        }
        return grid;
    }

    /**
     * Parsea la página ligera de la carrera y devuelve la lista de pilotos y la información de su clasificación.
     * 
     * @param racePage Página con la página de la carrera en formato ligero.
     * @return La información de la carrera.
     */
    String parseLightRacePage(String racePage) {
        int idxStart = racePage.lastIndexOf("form");
        int idxEnd = -1;
        String race = "";
        if (idxStart > 0) {
            idxEnd = racePage.indexOf("/div", idxStart) + 6;
            race = racePage.substring(idxStart+7, idxEnd).trim();
            race = race.replaceAll("<br>", "");
            race = race.replaceAll("<Br>", "");
            race = race.replaceAll("</div>", "");
            race = race.replaceAll("<!--NEXTLAPIN--><!--TIRESFUELINFO-->", "\n");
        }
        return unescape(race);
    }

    /**
     * Recupera la información del siguiente piloto de la parrilla.
     * 
     * @param gridPage Página de la parrilla de clasificación.
     * @return
     */
    private Driver nextDriver(String gridPage) {
        Driver driver = null;
        if (this.currentIdx >= 0) {
            int idxStart = gridPage.indexOf("ManagerProfile", this.currentIdx);
            int idxEnd = -1;
            if (idxStart > 0) {
                idxStart = gridPage.indexOf(">", idxStart) + 1;
                idxEnd = gridPage.indexOf("</a", idxStart);
                String name = unescape(gridPage.substring(idxStart, idxEnd).trim());
                idxStart = gridPage.indexOf("<font", idxEnd);
                idxStart = gridPage.indexOf(">", idxStart) + 1;
                idxEnd = gridPage.indexOf("<", idxStart);
                String time = gridPage.substring(idxStart, idxEnd).trim();
                idxStart = gridPage.indexOf("(", idxEnd) + 1;
                idxEnd = gridPage.indexOf(")", idxStart);
                String offset = gridPage.substring(idxStart, idxEnd).trim();
                driver = new Driver();
                driver.setName(name);
                driver.setTime(time);
                driver.setOffset(offset);
            }
            this.currentIdx = idxEnd;
        }
        return driver;
    }

    /**
     * Convierte entidades XML que representan letras acentuadas, eñes, etc... a caracteres UTF.
     * 
     * @param str La cadena que se quiere decodificar.
     * @return la cadena decodificada.
     */
    private String unescape(String str) {
        StringBuffer buf = null;
        String entityName = null;
        char ch = ' ';
        char charAt1 = ' ';
        int entityValue = 0;
        buf = new StringBuffer(str.length());
        for (int i = 0, l = str.length(); i < l; ++i) {
            ch = str.charAt(i);
            if (ch == '&') {
                int semi = str.indexOf(';', i + 1);

                if (semi == -1) {
                    buf.append(ch);
                    continue;
                }
                entityName = str.substring(i + 1, semi);

                if (entityName.charAt(0) == '#') {
                    charAt1 = entityName.charAt(1);
                    if (charAt1 == 'x' || charAt1 == 'X') {
                        entityValue = Integer.valueOf(entityName.substring(2), 16).intValue();
                    } else {
                        entityValue = Integer.parseInt(entityName.substring(1));
                    }
                }
                if (entityValue == -1) {
                    buf.append('&');
                    buf.append(entityName);
                    buf.append(';');
                } else {
                    buf.append((char) (entityValue));
                }
                i = semi;
            } else {
                buf.append(ch);
            }
        }
        try {
            return new String(buf.toString().getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return buf.toString();
        }
    }
}
