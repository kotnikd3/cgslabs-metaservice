/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the triple:
 * 1) forecasted value absolute time (i.e. forecast reference time + offset)
 * 2) forecast reference time (the time when the forecast model was run)
 * 3) forecasted value  
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class ForecastedValue {
	/**
	 * The forecasted value absolute time.
	 */
	@NotNull
	private Instant absoluteTime;

	/**
	 * The forecast reference time (i.e. the time when the forecast model that produced this value has been run)
	 */
	@NotNull
	private Instant referenceTime;

	/**
	 * The actual forecasted value
	 */
	@NotNull
	private Double value;


	/**
	 * Constructor.
	 * 
	 * @param absoluteTime The forecasted value absolute time.
	 * @param referenceTime The forecast reference time (i.e. the time when the forecast model that produced this value has been run)
	 * @param value The actual forecasted value.
	 */
	@JsonCreator
	public ForecastedValue(
			@JsonProperty("absoluteTime") Instant absoluteTime, 
			@JsonProperty("referenceTime") Instant referenceTime,
			@JsonProperty("value") Double value) {
		this.absoluteTime = absoluteTime;		
		this.referenceTime = referenceTime;
		this.value = value;
	}


	/**
	 * Returns the value of {@link #absoluteTime} instance variable of the object.
	 * @return The {@link #absoluteTime} instance variable.
	 */
	@JsonProperty("absoluteTime")//the name in the JSON will be the same as the name of variable (i.e. absoluteTime)
	public Instant getAbsoluteTime() {
		return this.absoluteTime;	
	}


	/**
	 * Returns the value of {@link #referenceTime} instance variable of the object.
	 * @return The {@link #referenceTime} instance variable.
	 */
	@JsonProperty("referenceTime")//the name in the JSON will be the same as the name of variable (i.e. referenceTime)
	public Instant getReferenceTime() {
		return this.referenceTime;	
	}

	/**
	 * Returns the value of {@link #value} instance variable of the object.
	 * @return The {@link #value} instance variable.
	 */
	@JsonProperty("value")//the name in the JSON will be the same as the name of variable (i.e. value)
	public Double getValue() {
		return this.value;	
	}


	/*
	 * Implementation of Comparable's compareTo() method. Determines the ordering of the objects - i.e. which of the two 
	 * objects is "less than", "equal to" or "greater than" the other. The ordering is done by the value of the
	 * {@link #absoluteTime} variable - i.e. the absolute time of the forecasted value.
	 */
	//NOTE: you need to define "implements Comparable<ForecastedValue>" in class definition in order for this method to work :)
	/*
	public int compareTo(ForecastedValue b) {		
		return (b.getAbsoluteTime().compareTo(absoluteTime));
	}
	*/

}