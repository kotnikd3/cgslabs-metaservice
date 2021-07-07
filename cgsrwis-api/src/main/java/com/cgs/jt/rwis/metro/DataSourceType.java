package com.cgs.jt.rwis.metro;

public enum DataSourceType {
	MEASUREMENT("measurement"),
	FORECAST("forecast");

	String label;
	DataSourceType(String l) {
		label = l;
	}
	
	public String getLabel() {
		return label;
	} 
	
	public static DataSourceType valueOfLabel(String label) {
	    for (DataSourceType dt : values()) {
	        if (dt.label.equals(label)) {
	            return dt;
	        }
	    }
	    return null;
	}
}
