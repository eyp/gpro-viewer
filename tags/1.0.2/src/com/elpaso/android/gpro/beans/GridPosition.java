package com.elpaso.android.gpro.beans;

/**
 * Grid position.
 * 
 * @author eduardo.yanez
 */
public class GridPosition extends Manager {
	private String shortedName;
	private Integer position;
	private String time;
	private String gap;
	private String tyreSupplier;
	private Integer points = 0;

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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getGap() {
		return gap;
	}
	public void setGap(String gap) {
		this.gap = gap;
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
    public String shortToString() {
        return String.format("%d - %s (%s)", position, time, gap);
    }
    
    @Override
    public String toString() {
        return String.format("%d - %s, %s (%s)", position, name, time, gap);
    }
}
