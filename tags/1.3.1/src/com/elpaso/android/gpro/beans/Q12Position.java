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
 * Represents the position for Q1 & Q2 standings, i.e., an object of this class will have 2 {@link Position} objects, one 
 * represents the Q1 position for row N, and the other represents the Q2 position form the same row N.<br>
 * Sometimes there isn't a Q2 position because not every manager that qualifies in Q1 does in Q2.<br>
 * 
 * @author eduardo.yanez
 */
public class Q12Position {
    private Position q1Position = null;
    private Position q2Position = null;
    
    public Position getQ1Position() {
        return q1Position;
    }
    public  void setQ1Position(Position q1Position) {
        this.q1Position = q1Position;
    }
    public  Position getQ2Position() {
        return q2Position;
    }
    public void setQ2Position(Position q2Position) {
        this.q2Position = q2Position;
    }
}
