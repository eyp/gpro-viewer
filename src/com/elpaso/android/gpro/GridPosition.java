package com.elpaso.android.gpro;

/**
 * Información de cada piloto.
 * 
 * @author eduardo.yanez
 */
public class GridPosition {
	private String managerName;
	private int place;
	private String time;
	private String offset;

	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	
    public String shortToString() {
        return String.format("%d - %s (%s)", place, time, offset);
    }
    
    @Override
    public String toString() {
        return String.format("%d - %s, %s (%s)", place, managerName, time, offset);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((managerName == null) ? 0 : managerName.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GridPosition other = (GridPosition) obj;
        if (managerName == null) {
            if (other.managerName != null)
                return false;
        } else if (!managerName.equals(other.managerName))
            return false;
        return true;
    }
}
