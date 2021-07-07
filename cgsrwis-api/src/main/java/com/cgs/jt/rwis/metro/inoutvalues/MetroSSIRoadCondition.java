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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the possible values of the SSI road condition (i.e. possible values of the <sc> XML tag in the input (observations) 
 * of Metro) and their mapping into Double number.
 * 
 * @author Jernej Trnkoczy
 */
//NOTE: It is not entirely clear what these SSI codes are and how are they taken into account by Metro.
//For example on https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) one can read:
//All road condition values are considered wet except for 33 which is the SSI code for dry road. 
//This probably means that Metro can only distinguish between wet and dry - so SSI code 33 or anything else???
//Googling for SSI does not return meaningful results, the link on the Metro wep page is broken, from the ZapisiOpazovanjaXML.java class written by 
//Rok Krsmanc it can be seen that the possible values are the following:
//33=dry,34=wet,35=ice/snow,40=frost
//From https://framagit.org/metroprojects/metro/wikis/Road_condition_(METRo) you can see that there are other possible values (from 33 to 43)
//I made some tests and found out that Metro happily accepts whatever you provide (even if it is a string that cannot be converted to number).
//This makes me believe that Metro works as follows: if value 33 is provided this means dry road, anything else is considered as wet road
public enum MetroSSIRoadCondition { 
	DRY ("33", 33.0),
	WET ("34", 34.0),
	ICESNOW ("35", 35.0),
	FROST("40", 40.0),
	MISSINGVALUE("9999", 9999.0);	

	private final String stringValue;
	private final Double doubleValue;		

	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the SSI road conditions.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */	
	private MetroSSIRoadCondition(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the SSI road condition type mapped to double value.
	 * @return The SSI road condition as double value.
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}


	/**
	 * Method to obtain the SSI road condition as a String-coded value.
	 * @return The SSI road condition as string value.
	 */
	//annotation to get the label in JSON/XMl/YAML (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums	
	@JsonValue
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are double values (codes) representing SSI road condition and the values are the enum 
	 * instances/constants representing the SSI road condition.
	 */
	private static final Map<Double, MetroSSIRoadCondition> ENUM_MAP;

	//Builds an immutable map of double codes to enum instance pairs.
	static {
		Map<Double, MetroSSIRoadCondition> map = new ConcurrentHashMap<Double, MetroSSIRoadCondition>();
		for (MetroSSIRoadCondition instance : MetroSSIRoadCondition.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getDoubleValue())) {
				map.put(instance.getDoubleValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The SSI road condition codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}


	/**
	 * To retrieve the enum instance by the given double code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */		
	public static MetroSSIRoadCondition get(Double code) {
		return ENUM_MAP.get(code);
	}
}



