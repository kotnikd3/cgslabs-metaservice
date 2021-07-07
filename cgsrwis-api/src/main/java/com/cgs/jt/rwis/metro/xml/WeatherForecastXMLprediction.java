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

import com.cgs.jt.rwis.metro.inoutvalues.MetroOctalCloudCoverage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Java bean that represents an individual <prediction> XML tag in the weather forecasts XML (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"forecast-time", "at", "td", "ra", "sn", "ws", "ap", "cc", "ir", "sf"})
public class WeatherForecastXMLprediction implements Comparable<WeatherForecastXMLprediction>{

	/**Represents the <forecast-time> tag of the XML. The time should be in ISO 8601 format and should contain 
	 * only hours and minutes(e.g. 2003-11-17T23:00Z)*/
	//NOTE: Metro uses ISO 8601 date/time format which should be parsable into java.time.Instant object
	//without problems (e.g. Instant instant = Instant.parse("2018-07-17T09:59:00Z")). However if the seconds
	//are missing in the string (e.g. Instant instant = Instant.parse("2018-07-17T09:59Z")) an exception is thrown.
	//Since metro uses only hh:mm (without seconds, milliseconds, nanoseconds...) format we need to serialize Instant
	//into XML in 2019-10-23T13:18Z format, and we need to be able to deserialize from this format into Instant.
	//This problem is solved by specifying the pattern with @JsonFormat. 
	//see also: https://stackoverflow.com/questions/36252556/jackson-deserialize-iso8601-fromatted-date-time-into-java8-instant
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX", timezone="UTC")
	private Instant forecastTime;

	/**Represents the <at> tag of the XML - i.e. air temperature 1.5m above ground in degrees Celsius
	 * NOTE: the <at> tag is mandatory.
	 */
	private Double at;

	/**Represents the <td> tag of the XML - i.e. dew point temperature 1.5m above ground in degrees Celsius
	 * NOTE: the <td> tag is mandatory.
	 */
	private Double td;

	/**Represents the <ra> tag of the XML - i.e. rain precipitation quantity since the beginning of the forecast in mm
	 * NOTE: the <ra> tag is mandatory.
	 */
	private Double ra;

	/**Represents the <sn> tag of the XML - i.e. snow precipitation quantity since the beginning of the forecast in cm
	 * NOTE: the <sn> tag is mandatory.
	 */
	private Double sn;

	/**Represents the <ws> tag of the XML - i.e. Wind speed 10 m above ground in km/h 
	 * NOTE: the <ws> tag is mandatory.
	 */
	private Double ws;

	/**Represents the <ap> tag of the XML - i.e. surface air pressure (at location elevation) 
	 * NOTE: the <ap> tag is mandatory.
	 */
	private Double ap;

	/**Represents the <cc> tag of the XML - i.e. octal cloud coverage (0-8) 
	 * NOTE: if both fluxes (sf and ir) are provided and Metro is configured to use them (with --use-solarflux-forecast and 
	 * --use-infrared-forecast options) - then the cloud coverage is ignored by Metro and the <cc> tag is not 
	 * necessarily included in the XML file. 
	 */	
	@JsonInclude(JsonInclude.Include.NON_NULL)//if the value is null then it is not included in JSON
	private MetroOctalCloudCoverage cc;

	/**Represents the <sf> tag of the XML - i.e. downwards solar radiation flux at ground surface in W/m2 
	 * NOTE: the <sf> tag is not mandatory.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)//if the value is null then it is not included in JSON
	private Double sf;
	
	/**Represents the <ir> tag of the XML - i.e. downwards infrared radiation flux at ground surface in W/m2
	 * NOTE: the <ir> tag is not mandatory.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)//if the value is null then it is not included in JSON
	private Double ir;
	
	/**Represents the <fa> tag of the XML - i.e. the "incoming" anthropogenic radiation flux at ground surface in W/m2 
	 * NOTE: the <fa> tag is not mandatory.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)//if the value is null then it is not included in JSON
	private Double fa;






	/**
	 * Constructor with arguments. 
	 * 
	 * @param forecastTime The time of the forecast.
	 * @param at The air temperature 1.5m above ground in degrees Celsius
	 * @param td The dew point temperature 1.5m above ground in degrees Celsius
	 * @param ra The rain precipitation quantity since the beginning of the forecast in mm
	 * @param sn The snow precipitation quantity since the beginning of the forecast in cm
	 * @param ws The wind speed 10 m above ground in km/h 
	 * @param ap The surface air pressure (at location elevation)
	 * @param cc The octal cloud coverage (0-8) 
	 * @param sf The downwards solar radiation flux at ground surface in W/m2 
	 * @param ir The downwards infrared radiation flux at ground surface in W/m2 		 
	 * @param fa The incoming anthropogenic radiation flux at ground surface in W/m2
	 */	
	@JsonCreator
	public WeatherForecastXMLprediction(
			@JsonProperty("forecast-time") Instant forecastTime, 
			@JsonProperty("at") Double at, 
			@JsonProperty("td") Double td,
			@JsonProperty("ra") Double ra,
			@JsonProperty("sn") Double sn,
			@JsonProperty("ws") Double ws,
			@JsonProperty("ap") Double ap,
			@JsonProperty("cc") MetroOctalCloudCoverage cc,
			@JsonProperty("sf") Double sf,
			@JsonProperty("ir") Double ir,
			@JsonProperty("fa") Double fa
			) {		
		this.forecastTime= forecastTime;
		this.at = at;
		this.td = td;
		this.ra = ra;
		this.sn = sn;
		this.ws = ws;
		this.ap = ap;
		this.cc = cc;
		this.sf = sf;
		this.ir = ir;
		this.fa = fa;
	}





	/**
	 * Returns the value of {@link #forecastTime} instance variable of the object.
	 * @return The {@link #forecastTime} instance variable.
	 */	
	@JsonProperty("forecast-time")//the name in the XML will not be the same as the name of variable (it will be forecast-time instead forecastTime)
	public Instant getForecastTime() {
		return this.forecastTime;
	}

	/**
	 * Returns the value of {@link #at} instance variable of the object.
	 * @return The {@link #at} instance variable.
	 */
	@JsonProperty("at")//the name in the XML will be the same as the name of variable (i.e. at)
	public Double getAt() {
		return this.at;	
	}

	/**
	 * Returns the value of {@link #td} instance variable of the object.
	 * @return The {@link #td} instance variable.
	 */
	@JsonProperty("td")//the name in the XML will be the same as the name of variable (i.e. td)
	public Double getTd() {
		return this.td;	
	}

	/**
	 * Returns the value of {@link #ra} instance variable of the object.
	 * @return The {@link #ra} instance variable.
	 */
	@JsonProperty("ra")//the name in the XML will be the same as the name of variable (i.e. ra)
	public Double getRa() {
		return this.ra;	
	}


	/**
	 * Returns the value of {@link #sn} instance variable of the object.
	 * @return The {@link #sn} instance variable.
	 */
	@JsonProperty("sn")//the name in the XML will be the same as the name of variable (i.e. sn)
	public Double getSn() {
		return this.sn;	
	}

	/**
	 * Returns the value of {@link #ws} instance variable of the object.
	 * @return The {@link #ws} instance variable.
	 */
	@JsonProperty("ws")//the name in the XML will be the same as the name of variable (i.e. ws)
	public Double getWs() {
		return this.ws;	
	}

	/**
	 * Returns the value of {@link #ap} instance variable of the object.
	 * @return The {@link #ap} instance variable.
	 */
	@JsonProperty("ap")//the name in the XML will be the same as the name of variable (i.e. ap)
	public Double getAp() {
		return this.ap;	
	}


	/**
	 * Returns the value of {@link #cc} instance variable of the object.
	 * @return The {@link #cc} instance variable.
	 */
	@JsonProperty("cc")//the name in the XML will be the same as the name of variable (i.e. cc)
	public MetroOctalCloudCoverage getCc() {
		return this.cc;	
	}

	/**
	 * Returns the value of {@link #sf} instance variable of the object.
	 * @return The {@link #sf} instance variable.
	 */
	@JsonProperty("sf")//the name in the XML will be the same as the name of variable (i.e. sf)
	public Double getSf() {
		return this.sf;	
	}

	/**
	 * Returns the value of {@link #ir} instance variable of the object.
	 * @return The {@link #ir} instance variable.
	 */
	@JsonProperty("ir")//the name in the XML will be the same as the name of variable (i.e. ir)
	public Double getIr() {
		return this.ir;
	}
	
	/**
	 * Returns the value of {@link #fa} instance variable of the object.
	 * @return The {@link #fa} instance variable.
	 */
	@JsonProperty("fa")//the name in the XML will be the same as the name of variable (i.e. fa)
	public Double getFa() {
		return this.fa;
	}



	//-------------------methods to determine when two objects are equal - in our case they are equal when they have the same forecast---------
	//-------------------time - this is because two "forecasts" cannot be at the same time - if you provide such data Metro will freak out---------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo) --------------------------------------
	//-------------------if we make a list containing WeatherForecastXMLprediction objects - an prediction with the same forecast time---------------
	//-------------------as is the forecast time of another object that is already in a list - should not be added to the list (since it---------------
	//-------------------would produce invalid XML------------------------------------------------------------------------------------------------

	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. WeatherForecastXMLprediction objects are the same if they have the same forecast time, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this WeatherForecastXMLprediction instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same forecast time as this instance. 
	 * In other words, two WeatherForecastXMLprediction instances are
	 * considered to be equal if they have the same forecast time. 
	 *
	 * @param o the object to be compared for equality with this instance
	 * @return <code>true</code> if the specified object is equal to this instance
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (this.getClass() != o.getClass()) {
			return false;
		}			
		WeatherForecastXMLprediction other = (WeatherForecastXMLprediction) o;
		//compare the values (they should be numerically equal) 
		if (this.forecastTime.compareTo(other.forecastTime)!=0) {
			return false;
		}		
		return true;
	}

	//Override the hashCode() method of Java.lang.Object according to the rules described below.
	//The only requirement for the hashCode() implementation is that equal objects (by their equals method)
	//should have the same hash code - see https://www.sitepoint.com/how-to-implement-javas-hashcode-correctly/	
	/**
	 * Returns the hash code value for this instance.     
	 *
	 * @return the hash code value for this instance
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((this.forecastTime == null) ? 0 : this.forecastTime.hashCode());		
		return result;
	}




	//-------------------implementation of compareTo method - in order to be able to sort the weather forecasts----------------------------------------------------------
	//-------------------the forecasts should be sorted by their forecast time (the oldest forecast first)----------------------
	//-------------------see: https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo)------------------------------------- --------------------------------------
	//TODO: implementation of a null safe comparator should maybe be improved - see https://stackoverflow.com/questions/481813/how-to-simplify-a-null-safe-compareto-implementation
	@Override
	public int compareTo(WeatherForecastXMLprediction other) {
		if (this.forecastTime == null ^ other.forecastTime == null) {
			return (this.forecastTime == null) ? -1 : 1;
		}
		//NOTE: if both are null they are considered equal!
		if (this.forecastTime == null && other.forecastTime == null) {
			return 0;
		}
		return this.forecastTime.compareTo(other.forecastTime);			
	}
}
