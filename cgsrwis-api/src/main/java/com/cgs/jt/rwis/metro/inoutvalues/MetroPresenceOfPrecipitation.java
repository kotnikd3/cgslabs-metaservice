/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.metro.inoutvalues;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the possible values of the presence of precipitation (i.e. possible values of the <pi> XML tag in the input (observations) 
 * of Metro) and their mapping into Double number.
 * 
 * @author Jernej Trnkoczy
 */
public enum MetroPresenceOfPrecipitation { 
	PRECIPITATIONNOTPRESENT ("0", 0.0),
	PRECIPITATIONPRESENT ("1", 1.0),
	MISSINGVALUE("9999", 9999.0);	

	private final String stringValue;
	private final Double doubleValue;		

	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the three presence of precipitation possibilities.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */	
	private MetroPresenceOfPrecipitation(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the presence of precipitation  mapped to double value.
	 * @return The presence of precipitation double value.
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}


	/**
	 * Method to obtain the presence of precipitation  as a String-coded value.
	 * @return The presence of precipitation  string value.
	 */
	//annotation to get the label in JSON/XMl/YAML (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums	
	@JsonValue
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are Double values (codes) representing presence of precipitation  and the values are the enum 
	 * instances/constants representing the presence of precipitation.
	 */
	private static final Map<Double,MetroPresenceOfPrecipitation> ENUM_MAP;

	//Builds an immutable map of Double codes to enum instance pairs.
	static {
		Map<Double, MetroPresenceOfPrecipitation> map = new ConcurrentHashMap<Double, MetroPresenceOfPrecipitation>();
		for (MetroPresenceOfPrecipitation instance : MetroPresenceOfPrecipitation.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getDoubleValue())) {
				map.put(instance.getDoubleValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The presence of precipitation  codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}


	/**
	 * To retrieve the enum instance by the given Double  code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */		
	public static MetroPresenceOfPrecipitation get(Double code) {
		return ENUM_MAP.get(code);
	}
	
}


