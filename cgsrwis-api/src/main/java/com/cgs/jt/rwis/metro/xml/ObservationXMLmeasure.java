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

import com.cgs.jt.rwis.metro.MetroDoubleSerializer;
import com.cgs.jt.rwis.metro.inoutvalues.MetroPresenceOfPrecipitation;
import com.cgs.jt.rwis.metro.inoutvalues.MetroSSIRoadCondition;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Java bean that represents an individual <measure> XML tags in the observation XML - i.e. XML containing RWS's measurements (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"observation-time", "at", "td", "pi", "ws", "sc", "st", "sst"})
public class ObservationXMLmeasure implements Comparable<ObservationXMLmeasure>{

	/**Represents the <observation-time> tag of the XML. The time should be in ISO 8601 format and should contain 
	 * only hours and minutes (e.g. 2003-11-17T23:00Z)*/
	//NOTE: Metro uses ISO 8601 date/time format which should be parsable into java.time.Instant object
	//without problems (e.g. Instant instant = Instant.parse("2018-07-17T09:59:00Z")). However if the seconds
	//are missing in the string (e.g. Instant instant = Instant.parse("2018-07-17T09:59Z")) an exception is thrown.
	//Since metro uses only hh:mm (without seconds, milliseconds, nanoseconds...) format we need to serialize Instant
	//into XML in 2019-10-23T13:18Z format, and we need to be able to deserialize from this format into Instant.
	//This problem is solved by specifying the pattern with @JsonFormat. 
	//see also: https://stackoverflow.com/questions/36252556/jackson-deserialize-iso8601-fromatted-date-time-into-java8-instant
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX", timezone="UTC")
	private Instant observationTime;

	/**Represents the <at> tag of the XML - i.e. air temperature 1.5m above ground in degrees Celsius*/
	@JsonSerialize(using = MetroDoubleSerializer.class)
	private Double at;

	/**Represents the <td> tag of the XML - i.e. dew point temperature 1.5m above ground in degrees Celsius*/
	@JsonSerialize(using = MetroDoubleSerializer.class)
	private Double td;

	/**Represents the <pi> tag of the XML - i.e. presence of the precipitation (0=No , 1=Yes)*/
	private MetroPresenceOfPrecipitation pi;

	/**Represents the <ws> tag of the XML - i.e. Wind speed 10 m above ground in km/h */
	@JsonSerialize(using = MetroDoubleSerializer.class)	
	private Double ws;

	/**Represents the <sc> tag of the XML - i.e. road condition expressed as SSI code
	 * @see https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo)
	 * @see https://framagit.org/metroprojects/metro/wikis/Road_condition_(METRo)
	 */
	private MetroSSIRoadCondition sc;


	/**Represents the <st> tag of the XML - i.e. road surface temperature in degrees Celsius */
	@JsonSerialize(using = MetroDoubleSerializer.class)
	private Double st;

	/**Represents the <sst> tag of the XML - i.e. road subsurface temperature in degrees Celsius - road subsurface sensor is typically
	 * 40cm below ground level, however other depths are possible (provided in the <sst-sensor-depth> tag  of the station configuration XML */
	@JsonSerialize(using = MetroDoubleSerializer.class)
	private Double sst;







	/**
	 * Constructor with arguments. 
	 * 
	 * @param observationTime The time in the <observation-time> tag of the XML.
	 * @param at The value of the <at> tag of the XML - i.e. the air temperature 1.5m above ground in degrees Celsius
	 * @param td The value of the <td> tag of the XML - i.e. the dew point temperature 1.5m above ground in degrees Celsius
	 * @param pi The value of the <pi> tag of the XML - i.e. presence of the precipitation (0=No , 1=Yes)
	 * @param ws The value of the <ws> tag of the XML - i.e. the wind speed 10 m above ground in km/h 
	 * @param sc The value of the <sc> tag of the XML - i.e. road condition expressed as SSI code
	 * @param st The value of the <st> tag of the XML - i.e. road surface temperature in degrees Celsius 
	 * @param sst The value of the <sst> tag of the XML - i.e. road subsurface temperature in degrees Celsius			 
	 */	
	@JsonCreator
	public ObservationXMLmeasure(
			@JsonProperty("observation-time") Instant observationTime, 
			@JsonProperty("at") Double at, 
			@JsonProperty("td") Double td,
			@JsonProperty("pi") MetroPresenceOfPrecipitation pi,
			@JsonProperty("ws") Double ws,
			@JsonProperty("sc") MetroSSIRoadCondition sc,			
			@JsonProperty("st") Double st,
			@JsonProperty("sst") Double sst
			) {		
		this.observationTime= observationTime;
		this.at = at;
		this.td = td;
		this.pi = pi;		
		this.ws = ws;
		this.sc = sc;		
		this.st = st;
		this.sst = sst;
	}





	/**
	 * Returns the value of {@link #observationTime} instance variable of the object.
	 * @return The {@link #observationTime} instance variable.
	 */	
	@JsonProperty("observation-time")//the name in the XML will not be the same as the name of variable (it will be observation-time instead observationTime)
	public Instant getObservationTime() {
		return this.observationTime;
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
	 * Returns the value of {@link #pi} instance variable of the object.
	 * @return The {@link #pi} instance variable.
	 */
	@JsonProperty("pi")//the name in the XML will be the same as the name of variable (i.e. pi)
	public MetroPresenceOfPrecipitation getPi() {
		return this.pi;	
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
	 * Returns the value of {@link #sc} instance variable of the object.
	 * @return The {@link #sc} instance variable.
	 */
	@JsonProperty("sc")//the name in the XML will be the same as the name of variable (i.e. sc)
	public MetroSSIRoadCondition getSc() {
		return this.sc;	
	}


	/**
	 * Returns the value of {@link #st} instance variable of the object.
	 * @return The {@link #st} instance variable.
	 */
	@JsonProperty("st")//the name in the XML will be the same as the name of variable (i.e. st)
	public Double getSt() {
		return this.st;	
	}

	/**
	 * Returns the value of {@link #sst} instance variable of the object.
	 * @return The {@link #sst} instance variable.
	 */
	@JsonProperty("sst")//the name in the XML will be the same as the name of variable (i.e. sst)
	public Double getSst() {
		return this.sst;
	}





	//-------------------methods to determine when two objects are equal - in our case they are equal when they have the same observation---------
	//-------------------time - this is because two "measures" cannot be at the same time - if you provide such data Metro will freak out---------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) --------------------------------------
	//-------------------and: https://framagit.org/metroprojects/metro/wikis/Input_observation_QA/QC_(METRo) -------------------------------------
	//-------------------if we make a list containing ObservationXMLmeasure objects - an observation with the same observation time---------------
	//-------------------as is the observation time of object that is already in a list - should not be added to the list (since it---------------
	//-------------------would produce invalid XML------------------------------------------------------------------------------------------------

	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. ObservationXMLmeasure objects are the same if they have the same observation time, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this ObservationXMLmeasure instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same observation time as this instance. 
	 * In other words, two ObservationXMLmeasure instances are
	 * considered to be equal if they have the same observation time. 
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
		ObservationXMLmeasure other = (ObservationXMLmeasure) o;
		//compare the values (they should be numerically equal) 
		if (this.getObservationTime().compareTo(other.getObservationTime())!=0) {
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
		result = prime * result + ((this.observationTime == null) ? 0 : this.observationTime.hashCode());		
		return result;
	}




	//-------------------implementation of compareTo method - in order to be able to sort the observations----------------------------------------------------------
	//-------------------the observations should be sorted by their observation time (the oldest observation first)----------------------
	//-------------------see: https://framagit.org/metroprojects/metro/wikis/Input_observation_QA/QC_(METRo) ------------------------------------- --------------------------------------
	//TODO: implementation of a null safe comparator should maybe be improved - see https://stackoverflow.com/questions/481813/how-to-simplify-a-null-safe-compareto-implementation
	@Override
	public int compareTo(ObservationXMLmeasure other) {
		if (this.observationTime == null ^ other.observationTime == null) {
			return (this.observationTime == null) ? -1 : 1;
		}
		//NOTE: if both are null they are considered equal!
		if (this.observationTime == null && other.observationTime == null) {
			return 0;
		}
		
		return this.observationTime.compareTo(other.observationTime);			
	}
}
