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
 * Represents template for storing measured value together with absolute time of measurement. It is a touple:
 * 1) measurement absolute time
 * 2) measured value
 * 
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class MeasuredValue {
	/**
	 * Identifies the absolute creation time of the value (e.g. when the measurement was taken) 
	 */
	@NotNull
	private Instant absoluteTime;

	/**
	 * Identifies the value (e.g. measured value)
	 */
	@NotNull
	private Double value;
	
	
	
	/**
	 * Constructor. 
	 * @param time The creation time of the value (e.g. when the measurement was taken) 
	 * @param value The value (e.g. measured value)	 * 
	 * 
	 */
	@JsonCreator
	public MeasuredValue (
			@JsonProperty("absoluteTime") Instant absoluteTime, 
			@JsonProperty("value") Double value
			) {		
		this.absoluteTime = absoluteTime;
		this.value = value;		
	}
	
	
	
	/**
	 * Returns the {@link #absoluteTime} instance variable of this object.
	 * @return The {@link #absoluteTime} instance variable.
	 *
	 */
	@JsonProperty("absoluteTime")//the name in the JSON will be the same as the name of variable (i.e. absoluteTime)
	public Instant getAbsoluteTime() {
		return this.absoluteTime;
	}

	/**
	 * Returns the {@link #value} instance variable of this object.
	 * @return The {@link #value} instance variable.
	 *
	 */
	@JsonProperty("value")//the name in the JSON will be the same as the name of variable (i.e. value)
	public Double getValue() {
		return this.value;
	}
	
}
