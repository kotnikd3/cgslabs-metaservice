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
 * Defines the possible values of the Metro model road condition (i.e. possible values of the <rc> XML tag in the output of Metro) and
 * their mapping into Double number.
 * @see https://framagit.org/metroprojects/metro/wikis/Road_condition_(METRo)#Criteria_for_determination_of_the_road_condition
 * 
 * @author Jernej Trnkoczy
 */
public enum MetroModelRoadCondition { 
	DRYROAD ("1", 1.0),
	WETROAD ("2", 2.0),
	ICESNOWONTHEROAD ("3", 3.0),
	MIXWATERSNOWONTHEROAD("4", 4.0),
	DEW("5", 5.0),	
	MELTINGSNOW("6", 6.0),
	FROST("7", 7.0),
	ICINGRAIN("8", 8.0);

	private final String stringValue;
	private final Double doubleValue;		

	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the metro model road conditions.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */	
	private MetroModelRoadCondition(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the metro model road condition type mapped to double value.
	 * @return The metro model road condition as double value.
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}


	/**
	 * Method to obtain the metro model road condition as a String-coded value.
	 * @return The metro model road condition as string value.
	 */
	//annotation to get the label in JSON/XMl/YAML (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums	
	@JsonValue
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are string values (codes) representing metro model road condition and the values are the enum 
	 * instances/constants representing the metro model road condition.
	 */
	private static final Map<String,MetroModelRoadCondition> ENUM_MAP;

	//Builds an immutable map of String codes to enum instance pairs.
	static {
		Map<String,MetroModelRoadCondition> map = new ConcurrentHashMap<String, MetroModelRoadCondition>();
		for (MetroModelRoadCondition instance : MetroModelRoadCondition.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getStringValue())) {
				map.put(instance.getStringValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The metro model road condition codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}


	/**
	 * To retrieve the enum instance by the given String code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */		
	public static MetroModelRoadCondition get(String code) {
		return ENUM_MAP.get(code);
	}
}




