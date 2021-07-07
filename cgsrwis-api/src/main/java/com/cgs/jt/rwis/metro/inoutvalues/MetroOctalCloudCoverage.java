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
 * Defines the possible values of the Octal cloud coverage (i.e. possible values of the <cc> XML tag in the input (forecasts) and
 * output of Metro) and their mapping into Double number.
 * 
 * @author Jernej Trnkoczy
 */
public enum MetroOctalCloudCoverage { 
	NOTNEEDED("-1", -1.0),//NOTE: if both fluxes (sf and ir) are provided as input and Metro is configured to use them (with --use-solarflux-forecast and --use-infrared-forecast options) - then the cloud coverage is irrelevant (put -1 as input, get -1 in output).
	NOCLOUDS ("0", 0.0),
	ONEEIGHTCLOUDY ("1", 1.0), 
	TWOEIGHTSCLOUDY ("2", 2.0), 
	THREEEIGHTSCLOUDY ("3", 3.0), 
	FOUREIGHTSCLOUDY ("4", 4.0),
	FIVEEIGHTSCLOUDY ("5", 5.0),
	SIXEIGHTSCLOUDY ("6", 6.0),
	SEVENEIGHTSCLOUDY ("7", 7.0),
	TOTALLYCLOUDY ("8", 8.0);

	private final String stringValue;
	private final Double doubleValue;		

	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the eight octal cloud coverage possibilities.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */	
	private MetroOctalCloudCoverage(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the octal cloud coverage type mapped to double value.
	 * @return The octal cloud coverage double value.
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}


	/**
	 * Method to obtain the octal cloud coverage type as a String-coded value.
	 * @return The octal cloud coverage type string value.
	 */
	//annotation to get the label in JSON/XML/YAML (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums	
	@JsonValue
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are string values (codes) representing octal cloud coverage type and the values are the enum 
	 * instances/constants representing the octal cloud coverage.
	 */
	private static final Map<String,MetroOctalCloudCoverage> ENUM_MAP;

	//Builds an immutable map of String codes to enum instance pairs.
	static {
		Map<String,MetroOctalCloudCoverage> map = new ConcurrentHashMap<String, MetroOctalCloudCoverage>();
		for (MetroOctalCloudCoverage instance : MetroOctalCloudCoverage.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getStringValue())) {
				map.put(instance.getStringValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The octal cloud coverage type codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}


	/**
	 * To retrieve the enum instance by the given String code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */		
	public static MetroOctalCloudCoverage get(String code) {
		return ENUM_MAP.get(code);
	}
}

