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
package com.elpaso.android.gpro.beans;

/**
 * Represents a position in a grid, in qualification sessions or in a race.
 * 
 * @author eduardo.yanez
 */
public class Position extends Manager {
    private Integer position = null;
    private String shortedName;
    private Time time = new Time();

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getShortedName() {
        return shortedName;
    }

    public void setShortedName(String shortedName) {
        this.shortedName = shortedName;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        if (this.position != null) {
            return String.format("%d - %s, %s (%s)", position, name, time.getTime(), time.getGap());
        } else {
            return String.format("%s, %s (%s)", name, time.getTime(), time.getGap());
        }
    }
}
