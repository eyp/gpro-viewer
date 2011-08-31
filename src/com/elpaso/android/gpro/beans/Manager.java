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
 * Represents a GPRO manager.
 * 
 * @author eduardo.yanez
 */
public class Manager {
    protected Integer idm = 0;
    protected String name;
    private String firstName;
    private String lastName;
    private String country;
    private Integer championships;
    private String tyreSupplier;
    private Integer points = 0;
    private String tyreSupplierImageUrl;
    private String liveryImageUrl;
    private String flagImageUrl;

    public Manager() {
    }
    
    public Manager(Manager source) {
        this.idm = source.idm;
        this.name = source.name;
        this.firstName = source.firstName;
        this.lastName = source.lastName;
        this.country = source.country;
        this.championships = source.championships;
        this.tyreSupplier = source.tyreSupplier;
        this.points = source.points;
        this.tyreSupplierImageUrl = source.tyreSupplierImageUrl;
        this.liveryImageUrl = source.liveryImageUrl;
        this.flagImageUrl = source.flagImageUrl;
    }
    
    public Integer getIdm() {
        return idm;
    }

    public void setIdm(Integer idm) {
        this.idm = idm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getChampionships() {
        return championships;
    }

    public void setChampionships(Integer championships) {
        this.championships = championships;
    }

    public String getTyreSupplier() {
        return tyreSupplier;
    }

    public void setTyreSupplier(String tyreSupplier) {
        this.tyreSupplier = tyreSupplier;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public String getTyreSupplierImageUrl() {
        return tyreSupplierImageUrl;
    }

    public void setTyreSupplierImageUrl(String tyreSupplierImageUrl) {
        this.tyreSupplierImageUrl = tyreSupplierImageUrl;
    }

    public String getLiveryImageUrl() {
        return liveryImageUrl;
    }

    public void setLiveryImageUrl(String liveryImageUrl) {
        this.liveryImageUrl = liveryImageUrl;
    }

    public String getFlagImageUrl() {
        return flagImageUrl;
    }

    public void setFlagImageUrl(String flagImageUrl) {
        this.flagImageUrl = flagImageUrl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idm == null) ? 0 : idm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Manager other = (Manager) obj;
        if (idm == null) {
            if (other.idm != null) {
                return false;
            }
        } else if (!idm.equals(other.idm)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, lastName);
    }
}
