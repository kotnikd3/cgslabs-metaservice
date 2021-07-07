/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */
package com.cgs.jt.rwis.route;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single mobile sample - i.e. a sampe made by a vehicle equipped with sensor(s). The sample has an location 
 *(where the sample was taken) expressed with longitude and latitude and the data (given as {@link MobileSampleData} object)
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class MobileSample {

	/**
	 * Defines the geographic location (longitude/latitude) where the measurement was taken. 
	 */	
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the geographicLocation is not null when deserializing from JSON
	@Valid //validator of this object needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then 
	//MobileSample object is also not valid)
	private GeographicLocation geographicLocation;
	
	@NotNull
	@Valid
	private MobileSampleData data;

	@JsonCreator
	public MobileSample(
			@JsonProperty("geographicLocation") GeographicLocation geographicLocation, 
			@JsonProperty("sampleData") MobileSampleData data) {
		this.geographicLocation = geographicLocation;		
		this.data = data;		
	}		


	@JsonProperty("geographicLocation")
	public GeographicLocation getGeographicLocation() {
		return this.geographicLocation;
	}
	

	@JsonProperty("sampleData")
	public MobileSampleData getMobileSampleData() {
		return this.data;
	}	

}
