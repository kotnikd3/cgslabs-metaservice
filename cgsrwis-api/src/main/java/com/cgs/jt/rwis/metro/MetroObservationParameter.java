/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.metro;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the possible Metro observation parameters (which are at, td, pi, ws, sc, st, sst)  along with their human readable names.
 * @see https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo)
 *   
 * @author Jernej Trnkoczy
 */
public enum MetroObservationParameter { 
	/**
	 * Enum instance representing the <at> tag in Metro observations XML. This tag should be populated with values representing
	 * "Air temperature in [deg. C] @ road weather station's height" parameter at the time of measurement (not a long-term average).
	 */
	AT("Metro observation <at> = Air temperature in [deg. C] @ Road weather station's height"),	


	/**
	 * Enum instance representing the <td> tag in Metro observations XML. This tag should be populated with values representing
	 * "Dew point temperature in [deg. C] @ Road weather station's height" parameter at the time of measurement (not a long-term average).
	 */
	TD("Metro observation <td> = Dew point temperature in [deg. C] @ Road weather station's height"),


	/**
	 * Enum instance representing the <pi> tag in Metro observations XML. This tag should be populated with values representing
	 * "Presence of precipitation in [0:No 1:Yes] @ Road weather station's height" parameter at the time of measurement (not a long-term average).
	 * @see com.cgs.jt.rwis.metro.inoutvalues.MetroPresenceOfPrecipitation class.
	 */
	PI("Metro observation <pi> = Presence of precipitation in [0:No 1:Yes] @ Road weather station's height"),


	/**
	 * Enum instance representing the <ws> tag in Metro observations XML. This tag should be populated with values representing
	 * "Wind speed in [km h^-1] @ Road weather station's height" parameter at the time of measurement (not a long-term average).
	 */
	WS("Metro observation <ws> = Wind speed in [km h^-1] @ Road weather station's height"),


	/**
	 * Enum instance representing the <sc> tag in Metro observations XML. This tag should be populated with values representing
	 * "Road condition in [categorical - SSI code] @ Road surface" parameter at the time of measurement (not a long-term average).
	 * Possible values are 33:Dry 34:Wet 35:Ice/Snow 40:Frost
	 * @see com.cgs.jt.rwis.metro.inoutvalues.MetroSSIRoadCondition class.
	 */
	SC("Metro observation <sc> = Road condition in [33:Dry 34:Wet 35:Ice/Snow 40:Frost] @ Road surface"),



	/**
	 * Enum instance representing the <st> tag in Metro observations XML. This tag should be populated with values representing
	 * "Road temperature in [deg. C] @ Ground surface" parameter at the time of measurement (not a long-term average).
	 */
	ST("Metro observation <st> = Road temperature in [deg. C] @ Ground surface"),




	/**
	 * Enum instance representing the <sst> tag in Metro observations XML. This tag should be populated with values representing
	 * "Road sub-surface temperature in [deg. C] @ sub-surface sensor depth below ground surface" parameter at the time of measurement 
	 * (not a long-term average). 
	 * NOTE: The depth of the sub-surface sensor can vary from station to station and needs to be given as Metro input in the 
	 * header of the station description XML file.
	 */
	SST("Metro observation <sst> = Road sub-surface temperature in [deg. C] @ sub-surface sensor depth below ground surface");





	/**
	 * Represents a label that is given to each of the enum instances - as to be able to retrieve the enum instance
	 * by the label.
	 */
	private final String  label;

	/**
	 * Stores a map of key-value pairs - where keys are label strings and values are the enum instances/constants 
	 * associated with this name
	 */
	private static final Map<String,MetroObservationParameter> ENUM_MAP;

	/**
	 * Constructor of enumeration
	 * @param The label associated with the enum instance.
	 */
	private MetroObservationParameter(String label) {
		this.label = label;	
	}

	/**
	 * Get the label of the enum instance (i.e. the value of {@link #label} instance variable).
	 * @return The label of the enum instance (i.e. the value of {@link #label} instance variable)
	 */
	//annotation to get the label in JSON (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums
	//
	@JsonValue
	public String getLabel() {
		return this.label;
	}


	// Build an immutable map of String label to enum instance pairs.
	static {
		Map<String,MetroObservationParameter> map = new ConcurrentHashMap<String, MetroObservationParameter>();
		for (MetroObservationParameter instance : MetroObservationParameter.values()) {
			//we should not allow two instances to have the same label!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getLabel())) {
				map.put(instance.getLabel(),instance);
			}
			else {
				throw new IllegalArgumentException("The labels (i.e. names) of the Metro observation parameters should all be different!");
			}			
		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * To retrieve the enum instance by the given label...
	 * @param label The label for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given label or null if no enum instance is associated with the given label.
	 */
	//when deserializing from JSON to enum - the label should be used to create enum instance. see:
	//https://www.libmonk.com/java-enum-serialization-deserialization-jackson-api/
	//https://medium.com/@ryanbrookepayne/deserializing-to-a-java-enum-type-975d8cf01ac4
	@JsonCreator
	public static MetroObservationParameter get(String label) {
		return ENUM_MAP.get(label);
	}
}




