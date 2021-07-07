/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the container for the metadata describing the point of interest (i.e. a
 * named geographic location of interest). A certain point of interest has a human readable name, and associated 
 * earth surface point (i.e. geographic position + elevation - see (@link #earthSurfacePoint) instance variable. 
 * The human readable name of point of interest is a property given by the customer and is used for human 
 * convenience - mainly for it's display in the GUI. It is therefore perfectly possible that there will be two 
 * different points of interest on the same geographical location 
 * (e.g. DARS is interested in geographic location lat-46.1841, lon-14.3792 and names it Zbilje, while DRSI is 
 * also interested in geographic location lat-46.1841, lon-14.3792 and names it Jeprca).
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class PointOfInterest {

	/**
	 * Represents the human readable name of the point of interest. The name is mainly used for human convenience - i.e. 
	 * for it's display in the GUI.
	 */
	@NotEmpty //for the Hibernate validator (packaged with Dropwizard) to check that the name is not null or empty string - when deserializing from JSON
	private String name;

	/**
	 * Represents the earth surface point (i.e. geographic location together with elevation of ground or water surface 
	 * in meters above mean sea level at this geographic location). 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the earthSurfacePoint is not null when deserializing from JSON
	@Valid //validator of the SubscriptionPoint needs to check the validness of EarthSurfacePoint too (if EarthSurfacePoint is not valid then SubscriptionPoint is also not valid)
	private EarthSurfacePoint earthSurfacePoint;



	/**
	 * The constructor of the {@code PointOfInterest} class. 
	 * @param name The human readable name of the point of interest.
	 * @param earthSurfacePoint The geographic location (latitude/longitude pair) together with elevation of ground
	 * or water surface in meters above mean sea level (at this geographic location). 
	 */
	@JsonCreator
	public PointOfInterest(
			@JsonProperty("name") String name, 
			@JsonProperty("earthSurfacePoint") EarthSurfacePoint earthSurfacePoint
			) {
		this.name = name;
		this.earthSurfacePoint = earthSurfacePoint;			
	}

	/**
	 * The constructor of the {@code PointOfInterest} class. 
	 * @param name The human readable name of the point of interest.
	 * @param lat The latitude of the geographic location of the point of interest.
	 * @param lon The longitude of the geographic location of the point of interest.
	 * @param elevation The elevation of ground or water surface in meters above mean sea level of the point of interest.    
	 */
	public PointOfInterest(String name, Double lat, Double lon, Double elevation) {
		this.name = name;
		this.earthSurfacePoint = new EarthSurfacePoint(new GeographicLocation(lat, lon), elevation);			
	}



	/**
	 * Returns the value of {@link #name} instance variable of the object.
	 * @return The {@link #name} instance variable.
	 */
	@JsonProperty("name")//the name in the JSON will be the same as the name of variable (i.e. name)
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the value of {@link #earthSurfacePoint} instance variable of the object.
	 * @return The {@link #earthSurfacePoint} instance variable.
	 */
	@JsonProperty("earthSurfacePoint")//the name in the JSON will be the same as the name of variable (i.e. earthSurfacePoint)
	public EarthSurfacePoint getEarthSurfacePoint() {
		return this.earthSurfacePoint;
	}

}
