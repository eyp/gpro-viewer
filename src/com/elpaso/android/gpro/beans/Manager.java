package com.elpaso.android.gpro.beans;

/**
 * Rpresents a GPRO manager.
 * 
 * @author eduardo.yanez
 */
public class Manager {
    private Integer idm = 0;
    protected String name;
    private String firstName;
    private String lastName;
    private String country;
    
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
        return name + " " + lastName;
    }
}
