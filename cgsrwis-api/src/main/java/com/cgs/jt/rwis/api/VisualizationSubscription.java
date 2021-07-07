/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the subscription to the visualization of particular parameter from particular forecast
 * model. In other words an object from this class defines that the visualization of a certain  
 * weather parameter is created from the output data of certain forecasting model (defined by it's id). 
 * For example: From the output data of weather model 219:0:Aladin (i.e. Aladin running on ARSO) the 
 * visualization of the [Air temperature @ 2m above ground] needs to be created. 
 *
 * @author  Jernej Trnkoczy
 * 
 */
public class VisualizationSubscription {

	/**
	 * Represents the parameter. The possible parameters are defined in the {@link ForecastedParameter} enum.
	 * NOTE: a parameter visualization subscription of a new parameter for a certain forecasting model will cause that the module collecting 
	 * forecasts will from now on try to create visualization also for this parameter. However if appropriate PyNGL Python script (that 
	 * actually creates the visualization for this specific parameter) does not exist the visualization cannot be created. Therefore
	 * for visualizations of parameters that are not supported jet appropriate Python script needs to be created and the weather forecast
	 * collector module must implement the call of this script!
	 */
	@NotNull(message = "may not be null (the provided name in JSON is not expected or is empty/null)") //for the validator (packaged with Dropwizard - Hibernate) to check that the geographicLocation is not null when deserializing from JSON
	private ForecastedParameter parameter;	

	
	/**
	 * Uniquely identifies the forecasting model from which the visualizatoin of the {@link #parameter} parameter needs to be created.
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the forecastModel is not null when deserializing from JSON
	@NotEmpty //validator of the object needs to check that the forecastModelId is not empty
	private String forecastModelId;


	/**
	 * Constructor. 
	 * 
	 * @param parameterName The road/weather parameter the customer is subscribing to.  
	 * @param forecastModelId The id of the forecast model the customer is subscribing to.
	 * 
	 */
	@JsonCreator
	public VisualizationSubscription(			
			@JsonProperty("parameter") ForecastedParameter parameter, 
			@JsonProperty("forecastModelId") String forecastModelId) {		
		this.parameter = parameter;			
		this.forecastModelId = forecastModelId;		
	}



	/**
	 * Returns the value of {@link #parameter} instance variable.
	 * @return The {@link #parameter} instance variable.
	 */
	@JsonProperty("parameter")//the name in the JSON will be the same as the name of variable (i.e. parameter)
	public ForecastedParameter getParameter() {
		return this.parameter;	
	}	


	/**
	 * Returns the value of {@link #forecastModelId} instance variable.
	 * @return The {@link #forecastModelId} instance variable.
	 */
	@JsonProperty("forecastModelId")//the name in the JSON will be the same as the name of variable (i.e. forecastModel)
	public String getForecastModelId() {
		return this.forecastModelId;
	}
}
