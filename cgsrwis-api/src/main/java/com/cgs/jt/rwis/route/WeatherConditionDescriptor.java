/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */
package com.cgs.jt.rwis.route;

import java.util.HashMap;
import java.util.HashSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.srvcs.validation.WeatherConditionDescriptorParamsValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the metadata defining what is supposed to be "weather condition" for certain reference point.
 * The "weather condition" is defined by a weather in several geographic locations, and the weather 
 * is represented by several weather parameters. Defining these parameters and locations means defining
 * what is "weather condition" for a certain reference point. 
 *  
 * @author  Jernej Trnkoczy
 * 
 */
//TODO: weather condition description in certain reference point might be described by forecasted parameters and by 
//MEASURED PARAMETERS AS WELL (e.g. weather condition in certain reference point is described by wind speed measurements
//in the nearby CVPs)!
@WeatherConditionDescriptorParamsValidation
public class WeatherConditionDescriptor{

	/**
	 * Represents a map of weather parameter (=key) and weather model providing this parameter (=value). This map defines
	 * which weather parameters define the weather condition. The weather condition for a certain reference point is 
	 * defined by these weather parameters on all of the defined locations in {@link locations} instance variable. 
	 */	
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the set contains at least one element  
	//NOTE: the validation for non-null and non-empty values of HashMap is done by
	//our custom validator - WeatherConditionDescriptorParamsValidator - and is triggered by the 
	//@WeatherConditionDescriptorParamsValidation annotation (above - just before class declaration begins)
	//TODO: the keys inside the HashMap are enum instances - however JSON representation will contain String (i.e. the label of the 
	//enum instance) - if in JSON there will be a String (label) that does not correspond to any enum instance - then the @JsonCreator in the
	//ForecastedParameter will return null. However null key in a map is a problem - so even before ForecastedParameter is deserialized 
	//Jackson will complain with JSON processing exception. Because this happens before validation - the user will not get validation error, 
	//instead he will get JSON processing error - and this is not very user friendly...
	private HashMap<ForecastedParameter, String> params;


	/**
	 * Represents the set of geographic locations that (together with the weather parameters defined in the {@link params}
	 * variable) define what is supposed to be "weather condition" for certain reference point. 
	 *  
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the set contains at least one element  
	@Valid //for the validator (packaged with Dropwizard - Hibernate) to check the validness of GeographicLocation (if one fo the GeographicLocation objects in the set is not valid then WeatherConditionDescription is not valid either)
	private HashSet<GeographicLocation> locations;


	/**
	 * Constructor with arguments. 
	 * 
	 * @param params The weather parameters that need to be considered to describe "weather condition"
	 * @param locations The geographic locations where the {@link params} weather parameters are monitored to describe "weather condition"
	 * 	 
	 */	
	@JsonCreator
	public WeatherConditionDescriptor(
			@JsonProperty("params") HashMap<ForecastedParameter, String> params, 
			@JsonProperty("locations") HashSet<GeographicLocation> locations					
			) {		
		this.params = params;
		this.locations = locations;				
	}





	/**
	 * Returns the value of {@link #params} instance variable of the object.
	 * @return The {@link #params} instance variable.
	 */	
	@JsonProperty("params")//the name in the JSON will be the same as the name of variable (i.e. params)
	public HashMap<ForecastedParameter, String> getParams() {
		return this.params;
	}

	/**
	 * Returns the value of {@link #locations} instance variable of the object.
	 * @return The {@link #locations} instance variable.
	 */
	@JsonProperty("locations")//the name in the JSON will be the same as the name of variable (i.e. locations)
	public HashSet<GeographicLocation> getLocations() {
		return this.locations;	
	}

}

