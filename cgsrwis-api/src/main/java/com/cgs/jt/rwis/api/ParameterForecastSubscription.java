/*
 * Copyright (c) 1990, 2018, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the subscription to the forecast of particular parameter. In other words an object from 
 * this class defines that the forecast of a certain  weather/road parameter for a particular 
 * subscription point (lat, lon, elevation) is retrieved from certain forecasting model (defined by it's id)
 * and can be accessed by certain customer. 
 * For example: the forecasts for parameter [Air temperature @ 2m above ground] on subscription point 
 * (e.g. lat=46.1841, lon=14.3792, elevation=360.0 ) are provided by weather model 
 * 219:0:Aladin (i.e. Aladin running on ARSO) and can be accessed by customer DRSI:Grosuplje:Miha Novak. 
 *
 * @author  Jernej Trnkoczy
 * 
 */
public class ParameterForecastSubscription {

	/**
	 * Represents the parameter. The possible parameters are defined in the {@link ForecastedParameter} enum.
	 * NOTE: a parameter subscription of an existing parameter (that is already supported by system) for a new subscription 
	 * point with a certain forecasting model will cause that the module collecting forecasts will from now on collect also 
	 * for this location and parameter. However registering a parameter subscription for a NEW PARAMETER (i.e. a parameter
	 * that is still not supported by the system/collector module) has absolutely no effect. To start populating the database
	 * with the data for this "new" parameter the appropriate forecast collector module must implement appropriate methods 
	 * to start populating the database!
	 */
	@NotNull(message = "may not be null (the provided name in JSON is not expected or is empty/null)") //for the validator (packaged with Dropwizard - Hibernate) to check that the variable is not null when deserializing from JSON
	private ForecastedParameter parameter;	

	/**
	 * Represents the subscription point (i.e. a human readable name plus earth surface point = lat/lon/elevation of location).
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the subscriptionPoint is not null when deserializing from JSON
	@Valid //validator of the ParameterForecastSubscription needs to check the validness of EarthSurfacePoint too (if it is not valid then ParameterSubscription is also not valid)
	private EarthSurfacePoint subscriptionPoint;


	/**
	 * Uniquely identifies the forecasting model from which the forecast for this parameter at this location is retrieved.
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the forecastModel is not null when deserializing from JSON
	@NotEmpty //validator of the ParameterSubscription needs to check that the forecastModelId is not empty
	private String forecastModelId;

	/**
	 * Defines the customer that can access the actual data of this parameter/model/location triplet.
	 */
	@NotEmpty //validator of the ParameterSubscription needs to check that the forecastModelId is not empty
	private String customerId;
	
	

	/**
	 * Constructor. 
	 * 
	 * @param parameterName The road/weather parameter the customer is subscribing to. 
	 * @param subscriptionPoint The subscription point (i.e. named location) the customer is subscribing to. 
	 * @param forecastModelId The id of the forecast model the customer is subscribing to.
	 * @param customerId The id of the customer that can access the actual data of this parameter/model/location triplet.
	 * 
	 */
	@JsonCreator
	public ParameterForecastSubscription(			
			@JsonProperty("parameter") ForecastedParameter parameter, 
			@JsonProperty("subscriptionPoint") EarthSurfacePoint subscriptionPoint, 
			@JsonProperty("forecastModelId") String forecastModelId,
			@JsonProperty("customerId") String customerId) {		
		this.parameter = parameter;
		this.subscriptionPoint = subscriptionPoint;		
		this.forecastModelId = forecastModelId;
		this.customerId = customerId;
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
	 * Returns the value of {@link #subscriptionPoint} instance variable.
	 * @return The {@link #subscriptionPoint} instance variable.
	 */
	@JsonProperty("subscriptionPoint")//the name in the JSON will be the same as the name of variable (i.e. subscriptionPoint)
	public EarthSurfacePoint getSubscriptionPoint() {
		return this.subscriptionPoint;	
	}	


	/**
	 * Returns the value of {@link #forecastModelId} instance variable.
	 * @return The {@link #forecastModelId} instance variable.
	 */
	@JsonProperty("forecastModelId")//the name in the JSON will be the same as the name of variable (i.e. forecastModel)
	public String getForecastModelId() {
		return this.forecastModelId;
	}
	
	
	/**
	 * Returns the value of {@link #customerId} instance variable.
	 * @return The {@link #customerId} instance variable.
	 */
	@JsonProperty("customerId")//the name in the JSON will be the same as the name of variable (i.e. customerId)
	public String getCustomerId() {
		return this.customerId;
	}
}
