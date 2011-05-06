package com.elpaso.android.gpro.parsers;

import java.util.ArrayList;
import java.util.List;

import com.elpaso.android.gpro.beans.GridPosition;

/**
 * Parsea las páginas HTML de GPRO en busca de información.
 * 
 * @author eduardo.yanez
 */
public class HtmlParser {

    int currentIdx = 0;

    /**
     * Parsea la página de clasificación y devuelve la lista de pilotos y la información de su clasificación.
     * 
     * @param gridPage Página con la parrilla de clasificación que se quiere parsear.
     * @return La lista de pilotos, o una lista vacía si no hay ninguno clasificado.
     */
    public List<GridPosition> parseGridPage(String gridPage) {
        this.currentIdx = 0;
        List<GridPosition> grid = new ArrayList<GridPosition>();
        GridPosition position = this.nextPosition(gridPage);
        int place = 0;
        while (position != null) {
            position.setPosition(++place);
            grid.add(position);
            position = this.nextPosition(gridPage);
        }
        return grid;
    }

    /**
     * Parsea la página ligera de la carrera y devuelve la lista de pilotos y la información de su clasificación.
     * 
     * @param racePage Página con la página de la carrera en formato ligero.
     * @return La información de la carrera.
     */
    public String parseLightRacePage(String racePage) {
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
        return ParserHelper.unescape(race);
    }

    /**
     * Recupera la información del siguiente piloto de la parrilla.
     * 
     * @param gridPage Página de la parrilla de clasificación.
     * @return
     */
    private GridPosition nextPosition(String gridPage) {
        GridPosition driver = null;
        if (this.currentIdx >= 0) {
            int idxStart = gridPage.indexOf("ManagerProfile", this.currentIdx);
            int idxEnd = -1;
            if (idxStart > 0) {
                idxStart = gridPage.indexOf(">", idxStart) + 1;
                idxEnd = gridPage.indexOf("</a", idxStart);
                String name = ParserHelper.unescape(gridPage.substring(idxStart, idxEnd).trim());
                idxStart = gridPage.indexOf("<font", idxEnd);
                idxStart = gridPage.indexOf(">", idxStart) + 1;
                idxEnd = gridPage.indexOf("<", idxStart);
                String time = gridPage.substring(idxStart, idxEnd).trim();
                idxStart = gridPage.indexOf("(", idxEnd) + 1;
                idxEnd = gridPage.indexOf(")", idxStart);
                String offset = gridPage.substring(idxStart, idxEnd).trim();
                driver = new GridPosition();
                driver.setName(name);
                driver.setTime(time);
                driver.setGap(offset);
            }
            this.currentIdx = idxEnd;
        }
        return driver;
    }
}
