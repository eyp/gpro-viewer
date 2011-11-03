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


/**
 * Parsea las páginas HTML de GPRO en busca de información.
 * 
 * @author eduardo.yanez
 */
public class HtmlParser {

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
}
