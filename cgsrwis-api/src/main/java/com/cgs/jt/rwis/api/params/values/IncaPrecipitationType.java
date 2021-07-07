/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.params.values;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines the possible values of the INCA precipitation type parameter (i.e. the possible output values
 * for precipitation type as reported by INCA forecasting model) and their mapping into 
 * Double number.
 * 
 * @author Jernej Trnkoczy
 */
public enum IncaPrecipitationType { 
	NOPRECIPITATION ("0", 0.0),
	RAIN ("1", 1.0), 
	RAINWITHSNOW ("2", 2.0), 
	SNOW ("3", 3.0), 
	FROZENRAIN ("4", 4.0);
	
	private final String stringValue;
	private final Double doubleValue;		
	
	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the Inca precipitation type.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */	
	private IncaPrecipitationType(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the INCA precipitation type mapped to double value.
	 * @return The INCA precipitation type double value.
	 */	
	public Double getDoubleValue() {
		return doubleValue;
	}
	


	/**
	 * Method to obtain the INCA precipitation type as a String-coded value.
	 * @return The INCA precipitation type string value.
	 */
	/*
	public String getStringValue() {
		return stringValue;
	}
	*/
	
	
	/**
	 * Stores a map of key-value pairs - where keys are string values (codes) representing precipitation type and the values are the enum 
	 * instances/constants representing the precipitation type .
	 */
	/*
	private static final Map<String,IncaPrecipitationType> ENUM_MAP;

	//Builds an immutable map of String codes to enum instance pairs.
	static {
		Map<String,IncaPrecipitationType> map = new ConcurrentHashMap<String, IncaPrecipitationType>();
		for (IncaPrecipitationType instance : IncaPrecipitationType.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getStringValue())) {
				map.put(instance.getStringValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The INCA precipitation type codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}
	*/
	
	
	/**
	 * To retrieve the enum instance by the given String code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */		
	/*
	public static IncaPrecipitationType get(String code) {
		return ENUM_MAP.get(code);
	}
	*/
	
}	