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
 * Defines the possible values of the Metro sunshadow method which is set by --sunshadow-method option. The possible values are:
 * <li>1 = Basic method (default). It sets solar flux to zero when Sun is below visible horizon</li>
 * <li>2 = Enhanced method. Replaces global solar flux with its diffuse component when Sun is below visible horizon.</li>
 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo) 
 * 
 * @author Jernej Trnkoczy
 */
public enum MetroSunshadowMethod { 
	BASICMETHOD ("1"),
	ENHANCEDMETHOD ("2");

	private final String stringValue;		

	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the Metro sunshadow methods
	 *  
	 */	
	private MetroSunshadowMethod(String stringValue) {
		this.stringValue = stringValue;		
	}



	/**
	 * Method to obtain the metro sunshadow method as a String-coded value.
	 * @return The Metro sunshadow method as string value.
	 */
	//annotation to get the label in JSON/XMl/YAML (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums	
	@JsonValue
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are string values (codes) representing metro sunshadow method and the values are the enum 
	 * instances/constants representing the metro sunshadow method.
	 */
	private static final Map<String,MetroSunshadowMethod> ENUM_MAP;

	//Builds an immutable map of String codes to enum instance pairs.
	static {
		Map<String, MetroSunshadowMethod> map = new ConcurrentHashMap<String, MetroSunshadowMethod>();
		for (MetroSunshadowMethod instance : MetroSunshadowMethod.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getStringValue())) {
				map.put(instance.getStringValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The sunshadow method codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}


	/**
	 * To retrieve the enum instance by the given String code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */	
	//when deserializing from JSON to enum - the label should be used to create enum instance. see:
	//https://www.libmonk.com/java-enum-serialization-deserialization-jackson-api/
	//https://medium.com/@ryanbrookepayne/deserializing-to-a-java-enum-type-975d8cf01ac4
	@JsonCreator
	public static MetroSunshadowMethod get(String code) {
		return ENUM_MAP.get(code);
	}
}


