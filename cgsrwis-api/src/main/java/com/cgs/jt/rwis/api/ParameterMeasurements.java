/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.time.Instant;
import java.util.TreeMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.srvcs.validation.ParameterMeasurementsValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a template for storing multiple measured values (with their measurement times) of a single sensor. The sensor is 
 * identified by parameter it is measuring, the sensor number (needed because there can be several sensors measuring the same parameter
 * at the same geographic location) and geographic location (of the road weather station to which the sensor is connected).    
 *
 * @author  Jernej Trnkoczy
 * 
 */
//TODO: As you can see by the annotations below the measured values are validated by Hybernate validation (on the measurement service). 
//However if you check the forecasted values - they are not annotated and not validated by Hybernate validation (on the
//forecast service)! So the question is if this is OK? Maybe the measurements are coming from 3rd party organizations and need to
//be validated, while the forecasts are always extracted by Vedra itself (and can perform validation before calling the service)?
//Or maybe this needs to be consolidated (both validated on service? or both validated before calling service?).
@ParameterMeasurementsValidation //our custom validation that checks the values inside the TreeMap
public class ParameterMeasurements {

	/**
	 * Identifies the measured parameter. 
	 */
	@NotNull (message = "may not be null (the provided name in JSON is not expected or is empty/null)") //for the validator (packaged with Dropwizard - Hibernate) to check if not null when deserializing from JSON
	private MeasuredParameter parameter;
	
	/**
	 * Identifies the id of the sensor (needed because there can be several sensors measuring the same parameter at the same geographic location). 
	 */
	@NotEmpty (message = "may not be null or empty (the provided name in JSON is not expected or is empty/null)") //for the validator (packaged with Dropwizard - Hibernate) to check if not null when deserializing from JSON
	private String sensorId;

	/**
	 * Defines the geographic location (longitude/latitude) where the measurement was taken. Since the parameterName
	 * defines also the "layer" (i.e. what and where is measured at this location - e.g. road temperature @ 5cm below surface)
	 * the geographic location together with parameter name fully defines the parameter (i.e. all three dimensions).
	 */	
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the geographicLocation is not null when deserializing from JSON
	@Valid //validator of the ParameterMeasurement needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then 
	//ParameterMeasurement is also not valid)
	private GeographicLocation geographicLocation;	



	/**
	 * Represents measured values in a form of {@link java.util.TreeMap} of key-value pairs. The TreeMap 
	 * is always sorted on keys. The keys are the times of the measured values as Instant objects (millis after epoch). 
	 * The values belonging to the keys represent the actual measured values.
	 * 
	 * NOTE: Since the measurements are produced by sensors we can assume that the measured values  
	 * will always be "numeric" - i.e. they can always be expressed as a number even if they have categorical values.
	 * The data type of the values is therefore Double. 
	 * NOTE: sensors producing data arrays etc. (e.g. image cameras, thermal maps etc...) are outside the scope and are not considered here.	 
	 */
	//NOTE: The java.time.Instant class equals() method is based on comparison of value (not reference) - so two
	//different objects representing the same moment on the time line are considered equal. The thing with Maps is
	//that if we have a key1-value1 in the Map and then we add a new pair key1-value2 which has the same key
	//(the keys "equality" is checked by the hashCode() and equals() methods) then value2 WILL REPLACE value1.
	//Because two Instant objects are considered equal if they represent the same point in time - if we try to add
	//a new key-value pair with the same "time" (=key) the new value will replace the old one.
	@NotEmpty //checks that the TreeMap is not null and is not empty
	//We also need to assure that the elements inside TreeMap are not null
	//this can be handled in two ways:
	//1) write custom validation - see https://stackoverflow.com/questions/27984137/java-beans-validation-collection-map-does-not-contain-nulls
	//2) use the latest Bean Validation API which supports this kind of validation out-of-the-box
	//The problem with 2) is that the latest Dropwizard does not include the latest JSR-303 implementation. The
	//default Maven dependencies of Dropwizard 1.3.12 are: validation-api-1.1.0.Final.jar. and hibernate-validator-5.4.3.Final.jar 
	//However we need validation-api-2.0.0.Final.jar and hibernate-validator-6.0.2.Final.jar - see: https://www.baeldung.com/javax-validation
	//I've checked and it works (if you add the dependencies for newer version into pom.xml). However then you have dependency
	//conflict (at least shade plugin is complaining). This can be resolved with proper pom.xml but is troublesome. Also you cannot
	//check if the forecasted values are inside the expected range. Therefore I implemented custom validator 
	//(com.cgs.jt.rwis.srvcs.validation.ParameterMeasurementsValidator) and annotation (com.cgs.jt.rwis.srvcs.validation.ParameterMeasurementsValidation)
	//and annotated this class with it (above - just before class declaration)
	//private TreeMap<@NotNull Instant, @NotNull Double> measuredValues;	//for Bean Validation API from version 2.0 on
	private TreeMap<Instant, Double> measuredValues;


	/**
	 * Constructor. Creates ParameterMeasurements object populated with measured values.
	 * @param parameter The measured parameter (i.e. the parameter for which the measurements were taken).
	 * @param sensorNumber The number of the sensor (needed because there can be several sensors measuring the same parameter at the same geographic location).
	 * @param geographicLocation The geographic location (lat/lon) where measurements were taken.
	 * @param customerIDs A set of customer IDs that are authorized to the measurements from this location (e.g. to view them in the graphical user interface). 
	 * @param measuredValues The measured parameter values.
	 */
	@JsonCreator
	public ParameterMeasurements (
			@JsonProperty("parameter") MeasuredParameter parameter, 
			@JsonProperty("sensorId") String sensorId,
			@JsonProperty("geographicLocation") GeographicLocation geographicLocation,			 
			@JsonProperty("measuredValues") TreeMap<Instant, Double> measuredValues) {		
		this.parameter = parameter;
		this.sensorId = sensorId;
		this.geographicLocation = geographicLocation;		
		this.measuredValues = measuredValues;
	}




	/**
	 * Returns the {@link #parameter} instance variable of this object.
	 * @return The {@link #parameter} instance variable.
	 */
	@JsonProperty("parameter")//the name in the JSON will be the same as the name of variable (i.e. parameter)
	public MeasuredParameter getParameter() {
		return this.parameter;
	}
	
	
	/**
	 * Returns the {@link #sensorId} instance variable of this object.
	 * @return The {@link #sensorId} instance variable.
	 */
	@JsonProperty("sensorId")//the name in the JSON will be the same as the name of variable (i.e. sensorId)
	public String getSensorId() {
		return this.sensorId;
	}


	/**
	 * Returns the {@link #geographicLocation} instance variable of this object.
	 * @return The {@link #geographicLocation} instance variable.
	 *
	 */
	@JsonProperty("geographicLocation")//the name in the JSON will be the same as the name of variable (i.e. geographicLocation)
	public GeographicLocation getLocation() {
		return this.geographicLocation;
	}	


	/**
	 * Returns the {@link #measuredValue} instance variable of this object.
	 * @return The {@link #measuredValue} instance variable.
	 *
	 */
	@JsonProperty("measuredValues")//the name in the JSON will be the same as the name of variable (i.e. measuredValue)
	public TreeMap<Instant, Double> getMeasuredValues() {
		return this.measuredValues;
	}
	
}


