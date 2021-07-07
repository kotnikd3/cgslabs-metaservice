/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.util.Objects;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the the metadata describing the weather forecast model.
 *
 * @author  Jernej Trnkoczy
 * 
 */
//TODO: not sure if this class is used anywhere at all... Check it and if not - then delete it...
public class WeatherForecastModel {
	
	/**
	 * A model is uniquely identified by it's ID. Although the ID can be an arbitrary string (as long as it is different from
	 * the IDs of all other models that are in the database) it is best to follow the following naming conventions:
	 * 1) The ID for weather forecast models is composed from weather provider (identified by centre/subcentre) and the name of the model (many different models could be running in a single forecast provider organization).
	 * 2) The centre subcentre and model name are separated by semicolon
	 * 3)  The centre/subcentre identification is adopted from GRIB standard. The codes for centre and subcentre should be according to the WMO standard and table COMMON CODE TABLE C-1: Identification of originating/generating centre.
	 * An example of such model ID is 219:0:Aladin (where 219 identifies the centre - ARSO from Slovenia, subcentre is 0 (since ARSO does not
	 * have subcentres) and model name is Aladin.
	 */
	private String modelId;
	
	/**
	 * Represents the geographic location (in lat/lon projection) of the west-south corner of the geospatial grid covered by the
	 * model.
	 */
	private GeographicLocation westSouthCorner;
	
	/**
	 * Represents the geographic location (in lat/lon projection) of the east-north corner of the geospatial grid covered by the
	 * model.
	 */
	private GeographicLocation eastNorthCorner;


	



	

	/**
	 * The constructor of the {@code WeatherForecastModel} class. 
	 * 
	 * @param modelId The id that uniquely identifies the weather forecast model. 
	 */
	@JsonCreator
	public WeatherForecastModel(
			@JsonProperty("modelId") String modelId, 
			@JsonProperty("westSouthCorner") GeographicLocation westSouthCorner, 
			@JsonProperty("eastNorthCorner") GeographicLocation eastNorthCorner) {
		this.modelId = modelId;
		this.westSouthCorner = westSouthCorner;
		this.eastNorthCorner = eastNorthCorner;				
	}



	/**
	 * Returns the value of {@link #modelId} instance variable of the object.
	 * @return The {@link #modelId} instance variable.
	 */
	@JsonProperty("modelId")//the name in the JSON will be the same as the name of variable (i.e. modelId)
	public String getModelId() {
		return this.modelId;
	}
	
	
	/**
	 * Returns the value of {@link #westSouthCorner} instance variable of the object.
	 * @return The {@link #westSouthCorner} instance variable.
	 */
	@JsonProperty("westSouthCorner")//the name in the JSON will be the same as the name of variable (i.e. westSouthCorner)
	public GeographicLocation getWestSouthCorner() {
		return this.westSouthCorner;
	}
	
	
	
	/**
	 * Returns the value of {@link #eastNorthCorner} instance variable of the object.
	 * @return The {@link #eastNorthCorner} instance variable.
	 */
	@JsonProperty("eastNorthCorner")//the name in the JSON will be the same as the name of variable (i.e. eastNorthCorner)
	public GeographicLocation getEastNorthCorner() {
		return this.eastNorthCorner;
	}

	

}