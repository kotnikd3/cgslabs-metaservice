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

import com.cgs.jt.rwis.api.params.MeasuredParameter;

/**
 * Defines the possible values of the ROAD_CONDITION_LUFFT_IRS31PRO measured parameter (i.e. the possible output values
 * for road condition of the Lufft IRS31PRO sensor as configured in CGS RWIS road weather stations.) and their mapping into 
 * Double number. Used in the checkValue() and getDoubleFromString() method implementations of the 
 * ROAD_CONDITION_LUFFT_IRS31PRO enum instance (see {@link MeasuredParameter} enum).
 * 
 * NOTE: These road conditions are the output of the Lufft IRS31PRO sensor operating in the IRS31-UMB Compatibility mode. 
 * If the sensor is configured to operate in some other mode then the output values may be different!
 * 
 * @author Jernej Trnkoczy
 */
public enum LufftIRS31ProRoadCondition {
	DRY("0", 0.0),
	MOIST("1", 1.0),
	WET("2", 2.0),
	ICE("3", 3.0),
	SNOW("4", 4.0),
	RESIDUAL_SALT("5", 5.0),
	FREEZING_WET("6", 6.0),
	CRITICAL("7", 7.0),
	UNDEFINED1("98", 98.0),//NOTE: in the IRS31Pro-UMB user manual you can see that UNDEFINED are all values >90 - it is not possible to make such enumeration - however fortunately it seems like the sensor (if it cennot decide on road condition) can output only 98 or 99!
	UNDEFINED2("99", 99.0);//NOTE: in the IRS31Pro-UMB user manual you can see that UNDEFINED are all values >90 - it is not possible to make such enumeration - however fortunately it seems like the sensor (if it cennot decide on road condition) can output only 98 or 99! 
	
	
	private final String stringValue;
	private final Double doubleValue;		
	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the Lufft IRS30pro sensor outputs.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */
	private LufftIRS31ProRoadCondition(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the road condition (sensor output) mapped to double value.
	 * @return The road condition double value.
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}


	/**
	 * Method to obtain the road condition (sensor output) as a String-coded value.
	 * @return The road condition string value.
	 */
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are string values (codes) of the sensor output and the values are the enum 
	 * instances/constants associated with this sensor output.
	 */
	private static final Map<String,LufftIRS31ProRoadCondition> ENUM_MAP;

	//Builds an immutable map of String codes to enum instance pairs.
	static {
		Map<String,LufftIRS31ProRoadCondition> map = new ConcurrentHashMap<String, LufftIRS31ProRoadCondition>();
		for (LufftIRS31ProRoadCondition instance : LufftIRS31ProRoadCondition.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getStringValue())) {
				map.put(instance.getStringValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The road condition codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}
	
	
	/**
	 * To retrieve the enum instance by the given String code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */	
	public static LufftIRS31ProRoadCondition get(String code) {
		return ENUM_MAP.get(code);
	}
	
	

}
