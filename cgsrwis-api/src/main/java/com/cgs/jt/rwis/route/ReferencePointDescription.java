/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.route;

import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the container for the metadata describing a specific RWIS reference point (i.e. a point on the road where
 * there is no RWS - however the system still provides the current and forecasted state (temperature, road condition etc.) 
 * on this location - based on route based forecasting and thermal mapping). This information is retrieved from the database 
 * and is needed by the modules that perform mapping of raw mobile measurements to reference points, select appropriate thermal mapping,
 * calculate road forecast on reference points etc... Part of the metadata ({@link #weatherCondition} defines what is considered
 * as a "weather condition" for a certain reference point. The other part of the metadata {@link #referencedPoints} describes
 * the "referenced points" that are associated with a reference point (the referenced points are nearby RWSs - from which
 * to calculate the interpolation).
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class ReferencePointDescription {

	/**
	 * Represents the geographic region to which this reference point belongs to.	 
	 */
	//NOTE: this is needed to allow for the paralelization - each reference points processing module will process only reference points 
	//that belong to certain region
	//TODO: not sure if this is needed at all?
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the string is not empty
	private String region;
	
	/**
	 * Represents the geographic location of this reference point.	 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@Valid //validator of the ReferencePointDescription needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then ReferencePointDescription is not valid either)
	private GeographicLocation geoLocation;
	

	/**
	 * Represents the set of referenced points (i.e. points that are associated with this reference point, usually nearby RWSs - from 
	 * which to calculate the interpolation)  
	 */
	//NOTE: the ReferencedPoint objects will be sorted according to their compareTo() method - we overrided it so that they are sorted by the distance to the reference point
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the set contains at least one element - the field is mandatory - see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) 
	@Valid //for the validator (packaged with Dropwizard - Hibernate) to check the validness of ReferencedPoint objects (if one of the objects in the set is not valid then ReferencePointDescription is not valid either)
	private TreeSet<ReferencedPoint> referencedPoints;	

	

	/**
	 * Represents the description of what is supposed to be "weather condition" for this reference point.
	 * The "weather condition" is defined by a weather in several geographic locations, and the weather 
	 * is represented by several weather parameters. Defining these parameters and locations means defining
	 * the "weather condition" 
	 */		
	@NotNull
	@Valid
	private WeatherConditionDescriptor weatherConditionDescr;


	

	






	/**
	 * Constructor with arguments.
	 * 
	 * @param region The geographic regions to which this reference point belongs to.
	 * @param geoLocation The geographic location of this reference point.
	 * @param referencedPoints The points that are referenced by this reference point (and from which to calculate interpolations)
	 * @param weatherConditionDescr The description of what is supposed to represent "weather condition" for this reference point.
	 */
	@JsonCreator
	public ReferencePointDescription(
			@JsonProperty("region") String region,
			@JsonProperty("geoLocation") GeographicLocation geoLocation, 
			@JsonProperty("referencedPoints") TreeSet<ReferencedPoint> referencedPoints,
			@JsonProperty("weatherConditionDescr") WeatherConditionDescriptor weatherConditionDescr			
			) {		
		this.region = region;
		this.geoLocation = geoLocation;
		this.referencedPoints = referencedPoints;
		this.weatherConditionDescr = weatherConditionDescr;	
	}



	/**
	 * Returns the value of {@link #region} instance variable of the object.
	 * @return The {@link #region} instance variable.
	 */
	@JsonProperty("region")//the name in the JSON will be the same as the name of variable (i.e. region)
	public String getRegion() {
		return this.region;
	}
	
	/**
	 * Returns the value of {@link #geoLocation} instance variable of the object.
	 * @return The {@link #geoLocation} instance variable.
	 */
	@JsonProperty("geoLocation")//the name in the JSON will be the same as the name of variable (i.e. geoLocation)
	public GeographicLocation getGeoLocation() {
		return this.geoLocation;
	}

	/**
	 * Returns the value of {@link #referencedPoints} instance variable of the object.
	 * @return The {@link #referencedPoints} instance variable.
	 */
	@JsonProperty("referencedPoints")//the name in the JSON will be the same as the name of variable (i.e. referencedPoints)
	public TreeSet<ReferencedPoint> getReferencedPoints() {
		return this.referencedPoints;	
	}


	/**
	 * Returns the value of {@link #weatherConditionDescr} instance variable of the object.
	 * @return The {@link #weatherConditionDescr} instance variable.
	 */
	@JsonProperty("weatherConditionDescr")//the name in the JSON will be the same as the name of variable (i.e. weatherConditionDescr)
	public WeatherConditionDescriptor getWeatherConditionDescr() {
		return this.weatherConditionDescr;	
	}	
}
