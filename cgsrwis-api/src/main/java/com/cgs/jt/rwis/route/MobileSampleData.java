/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */
package com.cgs.jt.rwis.route;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.srvcs.validation.MobileSampleDataValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a the data of the mobile sample - i.e. the data recorded by the vehicle equipped with sensor(s) - but
 * without the location information. 
 * 
 * @author  Jernej Trnkoczy
 * 
 */
@MobileSampleDataValidation //our custom validation that checks the values in HashMap<MeasuredParameter, Double>
public class MobileSampleData implements Serializable{

	//NOTE: the vehicleId and measurementTime together represent a "unique ID" - this means that a certain vehicle can produce only
	//one MobileSampleData object at certain measurement time. This requirement is related to the database (Redis) storage in 
	//GEOset 
	/** the ID of the vehicle that produced the sample*/
	@NotNull
	@NotEmpty
	private String vehicleID;
	/** the time when the sample was taken */
	@NotNull
	@Valid
	private Instant measurementTime;	
	/**
	 * the level of trust for this sensor - a number between 0 and 100 - where 100 is fully reliable and 0 is totally unreliable
	 */
	@NotNull
	private Integer sensorTrustLevel; 

	/**
	 * a map of measuter parameters and their corresponding measured values
	 */
	@NotEmpty //checks that the HashMap is not null and is not empty
	//We also need to assure that the elements inside HashMap are not null
	//this can be handled in two ways:
	//1) write custom validation - see https://stackoverflow.com/questions/27984137/java-beans-validation-collection-map-does-not-contain-nulls
	//2) use the latest Bean Validation API which supports this kind of validation out-of-the-box
	//The problem with 2) is that the latest Dropwizard does not include the latest JSR-303 implementation. The
	//the default Maven dependencies of Dropwizard 1.3.12 are: validation-api-1.1.0.Final.jar. and hibernate-validator-5.4.3.Final.jar 
	//However we need validation-api-2.0.0.Final.jar and hibernate-validator-6.0.2.Final.jar - see: https://www.baeldung.com/javax-validation
	//I've checked and it works (if you add the dependencies for newer version into pom.xml). However then you have dependency
	//conflict (at least shade plugin is complaining). This can be resolved with proper pom.xml but is troublesome. Also you cannot
	//check if the forecasted values are inside the expected range. Therefore I implemented custom validator 
	//(com.cgs.jt.rwis.srvcs.validation.MobileSampleDataValidator) and annotation (com.cgs.jt.rwis.srvcs.validation.MobileSampleDataValidation)
	//and annotated this class with it (above - just before class declaration)
	//private HashMap<@NotNull @Valid MeasuredParameter, @NotNull Double> data;	//for Bean Validation API from version 2.0 on	
	private HashMap<MeasuredParameter, Double> data;     

	@JsonCreator
	public MobileSampleData(
			@JsonProperty("vehicleID") String vehicleID, 
			@JsonProperty("measurementTime") Instant measurementTime, 
			@JsonProperty("sensorTrustLevel") Integer sensorTrustLevel, 
			@JsonProperty("data") HashMap<MeasuredParameter, Double> data) {
		this.vehicleID = vehicleID;
		this.measurementTime = measurementTime;
		this.sensorTrustLevel = sensorTrustLevel;
		this.data = data;		
	}	

	@JsonProperty("vehicleID")
	public String getVehicleID() { 
		return this.vehicleID; 
	}

	@JsonProperty("measurementTime")
	public Instant getMeasurementTime() {
		return this.measurementTime;
	}
	
	@JsonProperty("sensorTrustLevel")
	public Integer getSensorTrustLevel() {
		return this.sensorTrustLevel;
	}

	@JsonProperty("data")
	public HashMap<MeasuredParameter, Double> getSampledData() {
		return this.data;
	}	
}