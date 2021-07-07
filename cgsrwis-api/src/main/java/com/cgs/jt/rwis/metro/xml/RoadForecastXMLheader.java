/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */

package com.cgs.jt.rwis.metro.xml;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Java bean that represents the <header> XML tag in the road forecast XML - i.e. XML containing road forecasts (Metro model output). 
 * @see https://framagit.org/metroprojects/metro/wikis/Output_roadcast_(METRo)
 * 
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"version", "production-date", "road-station", "latitude", "longitude", "filetype", "first-roadcast"})
public class RoadForecastXMLheader {
	/**Represents the <version> tag of the XML.*/
	private String version;
	
	/**Represents the <production-date> tag of the XML. The time should be in ISO 8601 format and should contain 
	 * only hours and minutes(e.g. 2003-11-17T23:00Z)*/
	//NOTE: Metro uses ISO 8601 date/time format which should be parsable into java.time.Instant object
	//without problems (e.g. Instant instant = Instant.parse("2018-07-17T09:59:00Z")). However if the seconds
	//are missing in the string (e.g. Instant instant = Instant.parse("2018-07-17T09:59Z")) an exception is thrown.
	//Since Metro uses only hh:mm (without seconds, milliseconds, nanoseconds...) format we need to serialize Instant
	//into XML in 2019-10-23T13:18Z format, and we need to be able to deserialize from this format into Instant.
	//This problem is solved by specifying the pattern with @JsonFormat. 
	//see also: https://stackoverflow.com/questions/36252556/jackson-deserialize-iso8601-fromatted-date-time-into-java8-instant
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX", timezone="UTC")
	private Instant productionDate;
	
	/**Represents the <road-station> tag of the XML.*/
	private String roadStation;
	
	/**Represents the <latitude> tag of the XML.*/
	private Double latitude;
	
	/**Represents the <longitude> tag of the XML.*/
	private Double longitude;
	
	/**Represents the <filetype> tag of the XML.*/
	private String filetype;
	
	/**Represents the <first-roadcast> tag of the XML.*/
	//NOTE: Metro uses ISO 8601 date/time format which should be parsable into java.time.Instant object
	//without problems (e.g. Instant instant = Instant.parse("2018-07-17T09:59:00Z")). However if the seconds
	//are missing in the string (e.g. Instant instant = Instant.parse("2018-07-17T09:59Z")) an exception is thrown.
	//Since Metro uses only hh:mm (without seconds, milliseconds, nanoseconds...) format we need to serialize Instant
	//into XML in 2019-10-23T13:18Z format, and we need to be able to deserialize from this format into Instant.
	//This problem is solved by specifying the pattern with @JsonFormat. 
	//see also: https://stackoverflow.com/questions/36252556/jackson-deserialize-iso8601-fromatted-date-time-into-java8-instant
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX", timezone="UTC")
	private Instant firstRoadcast;



	/**
	 * Constructor with arguments. 
	 * 
	 * @param version The version in the header of the XML. 
	 * @param productionDate The production date in the header of the XML.
	 * @param roadStation The name of the road station for which the forecast is made (None if unknown) - information in the header of the XML.  
	 * @param latitude The latitude of the station - information in the header of the XML.	 
	 * @param longitude The longitude of the station - information in the header of the XML.
	 * @param filetype The filetype (e.g. roadcast) - information in the header of the XML.
	 * @param firstRoadcast The first roadcast - information in the header of the XML.
	 */	
	@JsonCreator
	public RoadForecastXMLheader(
			@JsonProperty("version") String version, 
			@JsonProperty("production-date") Instant productionDate,
			@JsonProperty("road-station") String roadStation,
			@JsonProperty("latitude") Double latitude,
			@JsonProperty("longitude") Double longitude,
			@JsonProperty("filetype") String filetype,
			@JsonProperty("first-roadcast") Instant firstRoadcast			
			) {		
		this.version = version;
		this.productionDate = productionDate;
		this.roadStation = roadStation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.filetype = filetype;
		this.firstRoadcast = firstRoadcast;
	}


	/**
	 * Returns the value of {@link #version} instance variable of the object.
	 * @return The {@link #version} instance variable.
	 */	
	@JsonProperty("version")//the name in the XML will be the same as the name of variable (i.e. version)
	public String getVersion() {
		return this.version;
	}

	/**
	 * Returns the value of {@link #productionDate} instance variable of the object.
	 * @return The {@link #productionDate} instance variable.
	 */
	@JsonProperty("production-date")//the name of the XML tag will not be the same as the name of variable (it will be production-date instead of productionDate)
	public Instant getProductionDate() {
		return this.productionDate;	
	}

	/**
	 * Returns the value of {@link #roadStation} instance variable of the object.
	 * @return The {@link #roadStation} instance variable.
	 */	
	@JsonProperty("road-station")//the name in the XML will not be the same as the name of variable (it will be road-station instead of roadStation)
	public String getRoadStation() {
		return this.roadStation;
	}

	/**
	 * Returns the value of {@link #latitude} instance variable of the object.
	 * @return The {@link #latitude} instance variable.
	 */	
	@JsonProperty("latitude")//the name in the XML will be the same as the name of variable (i.e. latitude)
	public Double getLatitude() {
		return this.latitude;
	}
	
	/**
	 * Returns the value of {@link #longitude} instance variable of the object.
	 * @return The {@link #longitude} instance variable.
	 */	
	@JsonProperty("longitude")//the name in the XML will be the same as the name of variable (i.e. longitude)
	public Double getLongitude() {
		return this.longitude;
	}


	/**
	 * Returns the value of {@link #filetype} instance variable of the object.
	 * @return The {@link #filetype} instance variable.
	 */	
	@JsonProperty("filetype")//the name in the XML will be the same as the name of variable (i.e. filetype)
	public String getFileType() {
		return this.filetype;
	}
	
	/**
	 * Returns the value of {@link #firstRoadcast} instance variable of the object.
	 * @return The {@link #firstRoadcast} instance variable.
	 */
	@JsonProperty("first-roadcast")//the name of the XML tag will not be the same as the name of variable (it will be first-roadcast instead of firstRoadcast)
	public Instant getFirstRoadcast() {
		return this.firstRoadcast;	
	}

}



