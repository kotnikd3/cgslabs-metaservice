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
 * Defines the possible values of the STATE_OF_COUPLING_LUFFT_IRS31PRO measured parameter (i.e. the possible output values
 * for state of coupling of the Lufft IRS31PRO sensor as configured in CGS RWIS road weather stations) and their mapping into 
 * Double number. Used in the checkValue() and getDoubleFromString() method implementations of the 
 * STATE_OF_COUPLING_LUFFT_IRS31PRO enum instance (see {@link MeasuredParameter} enum).
 * 
 * NOTE: if the sensor is configured in some other way then the possible output values may be different so you will need different
 * enumeration!
 * 
 * @author Jernej Trnkoczy
 */
public enum LufftIRS31ProStateOfCoupling {
	OFF("0", 0.0),
	ON_FREEZING_TEMPERATURE_ARS31PRO_RECEIVED("1", 1.0),
	ON_FREEZING_TEMPERATURE_ARS31PRO_NOT_RECEIVED("2", 2.0);
	
	private final String stringValue;
	private final Double doubleValue;
	
	/**
	 * Constructor of enumeration
	 * @param stringValue The string value (usually called "code") that represents one of the Lufft IRS30pro sensor outputs.
	 * @param doubleValue The mapping of the string value (code) to the numeric (Double) representation. 
	 */
	private LufftIRS31ProStateOfCoupling(String stringValue, Double doubleValue) {
		this.stringValue = stringValue;
		this.doubleValue = doubleValue;
	}

	/**
	 * Method to obtain the state of coupling (sensor output) mapped to double value.
	 * @return The state of coupling double value.
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}


	/**
	 * Method to obtain the state of coupling (sensor output) as a String-coded value.
	 * @return The state of coupling string value.
	 */
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Stores a map of key-value pairs - where keys are string values (codes) of the sensor output and the values are the enum 
	 * instances/constants associated with this sensor output.
	 */
	private static final Map<String,LufftIRS31ProStateOfCoupling> ENUM_MAP;

	//Builds an immutable map of String codes to enum instance pairs.
	static {
		Map<String,LufftIRS31ProStateOfCoupling> map = new ConcurrentHashMap<String, LufftIRS31ProStateOfCoupling>();
		for (LufftIRS31ProStateOfCoupling instance : LufftIRS31ProStateOfCoupling.values()) {
			//we should not allow two instances to have the same code!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getStringValue())) {
				map.put(instance.getStringValue(),instance);
			}
			else {
				throw new IllegalArgumentException("The state of coupling codes should all be different!");
			}

		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}
	
	
	/**
	 * To retrieve the enum instance by the given String code...
	 * @param code The code for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given code or null if no enum instance is associated with the given label.
	 */		
	public static LufftIRS31ProStateOfCoupling get(String code) {
		return ENUM_MAP.get(code);
	}
	

}
