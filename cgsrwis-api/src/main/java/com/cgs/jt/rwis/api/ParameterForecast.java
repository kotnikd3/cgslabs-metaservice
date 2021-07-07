/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
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

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.srvcs.validation.ParameterForecastValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a generic template for storing a single forecast for a specific parameter on specific location and
 * produced by specific forecasting model (NOTE: the same parameter in the same location might be forecasted by 
 * several different forecasting models).   
 *
 * @author  Jernej Trnkoczy
 * 
 */

@ParameterForecastValidation //our custom validation that checks the values in TreeMap 
public class ParameterForecast {

	/**
	 * Identifies the forecasted parameter. 
	 */
	//for the validator (packaged with Dropwizard - Hibernate) to check that the parameter is not null when deserializing from JSON
	//NOTE: due to the implementation (see @JsonCreator in ForecastedParameter) if in JSON there is "unknown" String
	//this will result in null ForecastedParameter - and since it must not be null - the @NotNull validator will report
	//object not valid. This is why we added the "and must be one of the supported weather parameter names" in the message.
	@NotNull(message = "may not be null and must be one of the supported weather parameter names")
	private ForecastedParameter parameter;	

	
	/**
	 * Defines the geographic location (longitude/latitude).
	 * NOTE: Since the parameterName defines also the "layer" (i.e. the vertical dimension of the parameter - for example 150cm above ground,
	 * at ground or water surface, etc...) the geographic location (without layer information) together with parameter
	 * name fully defines the parameter (i.e. all three dimensions - latitude, longitude and layer).
	 */	
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the geographicLocation is not null when deserializing from JSON
	@Valid //validator of the ParameterForecast needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then ParameterForecast is also not valid)
	private GeographicLocation geographicLocation;

	/**
	 * Uniquely identifies the forecast model that produced the forecast.
	 */	
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the forecastModelId is not null when deserializing from JSON
	@NotEmpty //validator of the ParameterForecast needs to check that the forecastModelId is not empty
	private String forecastModelId;


	/**
	 * Identifies the reference time of the forecast 
	 */
	@NotNull
	private Instant referenceTime;

	/**
	 * A TreeMap representing the forecast (i.e. keys which are the absolue times of the forecasted
	 * values - this means forecastReferenceTime + offset (e.g. +1h, +2h, +3h...) - and values which represent
	 * the actual forecasted values). 
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
	//the default Maven dependencies of Dropwizard 1.3.12 are: validation-api-1.1.0.Final.jar. and hibernate-validator-5.4.3.Final.jar 
	//However we need validation-api-2.0.0.Final.jar and hibernate-validator-6.0.2.Final.jar - see: https://www.baeldung.com/javax-validation
	//I've checked and it works (if you add the dependencies for newer version into pom.xml). However then you have dependency
	//conflict (at least shade plugin is complaining). This can be resolved with proper pom.xml but is troublesome. Also you cannot
	//check if the forecasted values are inside the expected range. Therefore I implemented custom validator 
	//(com.cgs.jt.rwis.srvcs.validation.ParameterForecastValidator) and annotation (com.cgs.jt.rwis.srvcs.validation.ParameterForecastValidation)
	//and annotated this class with it (above - just before class declaration)
	//private TreeMap<@NotNull Instant, @NotNull TreeMap<@NotNull Instant, @NotNull Double>> forecasts;	//for Bean Validation API from version 2.0 on
	private TreeMap<Instant, Double> forecast;	
	
	

	/**
	 * Constructor. Creates ParameterForecast object.
	 * @param parameter The forecasted parameter (i.e. parameter for which this forecast is made).
	 * The forecasted parameter instances are defined in {@link ForecastedParameter} enum. 
	 * @param geographicLocation The geographic location (lat/lon) for which this forecast is made.
	 * @param forecastModelId The forecast model id (uniquely identifies the forecast model) that produced this forecast.
	 * @param referenceTime The reference time of the forecast.
	 * @param forecast The forecasted values (one or multiple) at different forecast absolute times.
	 */
	@JsonCreator
	public ParameterForecast (
			@JsonProperty("parameter") ForecastedParameter parameter, 
			@JsonProperty("geographicLocation") GeographicLocation geographicLocation, 
			@JsonProperty("forecastModelId") String forecastModelId,
			@JsonProperty("referenceTime") Instant referenceTime, 
			@JsonProperty("forecast") TreeMap<Instant, Double> forecast) {		
		this.parameter = parameter;		
		this.geographicLocation = geographicLocation;
		this.forecastModelId = forecastModelId;	
		this.referenceTime = referenceTime;
		this.forecast = forecast;
	}
	




	/**
	 * Returns the {@link #parameter} instance variable of this object.
	 * @return The {@link #parameter} instance variable.
	 */
	@JsonProperty("parameter")//the name in the JSON will be the same as the name of variable (i.e. parameterName)
	public ForecastedParameter getParameter() {
		return this.parameter;
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
	 * Returns the {@link #forecastModel} instance variable of this object.
	 * @return The {@link #forecastModel} instance variable.
	 *
	 */
	@JsonProperty("forecastModelId")//the name in the JSON will be the same as the name of variable (i.e. forecastModel)
	public String getForecastModelId() {
		return this.forecastModelId;
	}
	
	
	/**
	 * Returns the {@link #referenceTime} instance variable of this object.
	 * @return The {@link #referenceTime} instance variable.
	 *
	 */
	@JsonProperty("referenceTime")//the name in the JSON will be the same as the name of variable (i.e. referenceTime)
	public Instant getReferenceTime() {
		return this.referenceTime;
	}
	

	/**
	 * Returns the {@link #forecast} instance variable of this object.
	 * @return The {@link #forecast} instance variable.
	 *
	 */
	@JsonProperty("forecast")//the name in the JSON will be the same as the name of variable (i.e. forecast)
	public TreeMap<Instant, Double> getForecast() {
		return this.forecast;
	}
}


