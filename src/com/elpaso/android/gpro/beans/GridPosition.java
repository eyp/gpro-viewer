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
 * Grid position.
 * 
 * @author eduardo.yanez
 */
public class GridPosition extends Manager {
    private String shortedName;
    private QualificationTime qualificationTimeGrid = new QualificationTime();
    private QualificationTime qualificationTimeOne;
    private QualificationTime qualificationTimeTwo;

    public String getShortedName() {
        return shortedName;
    }

    public void setShortedName(String shortedName) {
        this.shortedName = shortedName;
    }

    public QualificationTime getQualificationTimeGrid() {
        return qualificationTimeGrid;
    }

    public void setQualificationTimeGrid(QualificationTime qualificationTimeGrid) {
        this.qualificationTimeGrid = qualificationTimeGrid;
    }

    public QualificationTime getQualificationTimeOne() {
        return qualificationTimeOne;
    }

    public void setQualificationTimeOne(QualificationTime qualificationTimeOne) {
        this.qualificationTimeOne = qualificationTimeOne;
    }

    public QualificationTime getQualificationTimeTwo() {
        return qualificationTimeTwo;
    }

    public void setQualificationTimeTwo(QualificationTime qualificationTimeTwo) {
        this.qualificationTimeTwo = qualificationTimeTwo;
    }

    @Override
    public String toString() {
        return String.format("%d - %s, %s (%s)", qualificationTimeGrid.getPosition(), name, qualificationTimeGrid.getTime(),
                qualificationTimeGrid.getGap());
    }
}
