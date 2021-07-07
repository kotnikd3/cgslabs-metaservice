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

import com.cgs.jt.rwis.metro.inoutvalues.MetroModelRoadCondition;
import com.cgs.jt.rwis.metro.inoutvalues.MetroOctalCloudCoverage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Java bean that represents an individual <prediction> XML tag in the weather forecasts XML (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"roadcast-time", "hh", "at", "td", "ws", "sn", "ra", "qp-sn", "qp-ra", "cc", "sf", "ir", "fv", "fc", "fa", "fg", "bb", "fp", "rc", "st", "sst"})
public class RoadForecastXMLprediction implements Comparable<RoadForecastXMLprediction>{

	/**Represents the <roadcast-time> tag of the XML. The time should be in ISO 8601 format and should contain 
	 * only hours and minutes(e.g. 2003-11-17T23:00Z)*/
	//NOTE: Metro uses ISO 8601 date/time format which should be parsable into java.time.Instant object
	//without problems (e.g. Instant instant = Instant.parse("2018-07-17T09:59:00Z")). However if the seconds
	//are missing in the string (e.g. Instant instant = Instant.parse("2018-07-17T09:59Z")) an exception is thrown.
	//Since metro uses only hh:mm (without seconds, milliseconds, nanoseconds...) format we need to serialize Instant
	//into XML in 2019-10-23T13:18Z format, and we need to be able to deserialize from this format into Instant.
	//This problem is solved by specifying the pattern with @JsonFormat. 
	//see also: https://stackoverflow.com/questions/36252556/jackson-deserialize-iso8601-fromatted-date-time-into-java8-instant
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX", timezone="UTC")
	private Instant roadcastTime;

	/**Represents the <hh> tag of the XML - i.e. time since the beginning of the road forecast in hours with decimal precision*/
	private Double hh;

	/**Represents the <at> tag of the XML - i.e. air temperature 1.5m above ground in degrees Celsius*/
	private Double at;

	/**Represents the <td> tag of the XML - i.e. dew point temperature 1.5m above ground in degrees Celsius*/
	private Double td;

	/**Represents the <ws> tag of the XML - i.e. Wind speed 10 m above ground in km/h */
	private Double ws;

	/**Represents the <sn> tag of the XML - i.e. quantity of snow or ice on the road in cm*/
	private Double sn;

	/**Represents the <ra> tag of the XML - i.e. quantity of rain on the road in mm*/
	private Double ra;

	/**Represents the <qp-sn> tag of the XML - i.e. Total (1 hr) snow precipitation in cm*/
	private Double qpsn;

	/**Represents the <qp-ra> tag of the XML - i.e. Total (1 hr) rain precipitation in mm*/
	private Double qpra;

	/**Represents the <cc> tag of the XML - i.e. octal cloud coverage (0-8) 
	 * NOTE: if both fluxes (sf and ir) are provided and Metro is configured to use them (with --use-solarflux-forecast and 
	 * --use-infrared-forecast) - then the computed cloud cover value in the roadcast is -1. 
	 */	
	private MetroOctalCloudCoverage cc;

	/**Represents the <sf> tag of the XML - i.e. downwards solar radiation flux at ground surface in W/m2 */
	private Double sf;

	/**Represents the <ir> tag of the XML - i.e. downwards infrared radiation flux at ground surface in W/m2 */
	private Double ir;

	/**Represents the <fv> tag of the XML - i.e. vapor flux in W/m2 */
	private Double fv;

	/**Represents the <fc> tag of the XML - i.e. sensible heat in W/m2 */
	private Double fc;

	/**Represents the <fa> tag of the XML - i.e. anthropogenic flux at ground surface in W/m2. NOTE: if anthropogenic flux is not
	 * provided as input to Metro then Metro uses value of 10 W/m2 by default - and this same value is also in the output roadcast.*/
	private Double fa;

	/**Represents the <fg> tag of the XML - i.e. ground exchange flux in W/m2 */
	private Double fg;

	/**Represents the <bb> tag of the XML - i.e. blackbody effect in W/m2 */
	private Double bb;

	/**Represents the <fp> tag of the XML - i.e. phase change in W/m2 */
	private Double fp;

	/**Represents the <rc> tag of the XML - i.e. road condition expressed in Metro code (possible values 1,2,3,4,5,6,7,8)
	 * @see  https://framagit.org/metroprojects/metro/wikis/Road_condition_(METRo)#Criteria_for_determination_of_the_road_condition*/
	private MetroModelRoadCondition rc;

	/**Represents the <st> tag of the XML - i.e. road surface temperature in degrees Celsius */
	private Double st;

	/**Represents the <sst> tag of the XML - i.e. road subsurface temperature in degrees Celsius. This is typically
	 * 40cm below ground level, however other depths are possible (NOTE: with new versions of Metro you can provide the
	 * depth at which your sensor sa are measuring temperature as Metro input). For details also check 
	 * https://framagit.org/metroprojects/metro/wikis/Output_roadcast_(METRo) and 
	 * https://framagit.org/metroprojects/metro/wikis/Vertical_levels_(METRo) */
	private Double sst;





	/**
	 * Constructor with arguments. 
	 * 
	 * @param roadcastTime The value of the <roadcast-time> tag of the XML. The time should be in ISO 8601 format and should contain 
	 * only hours and minutes(e.g. 2003-11-17T23:00Z)
	 * @param hh The value of the <hh> tag of the XML - i.e. time since the beginning of the road forecast in hours with decimal precision
	 * @param at The value of the <at> tag of the XML - i.e. air temperature 1.5m above ground in degrees Celsius
	 * @param td The value of the <td> tag of the XML - i.e. dew point temperature 1.5m above ground in degrees Celsius
	 * @param ws The value of the <ws> tag of the XML - i.e. Wind speed 10 m above ground in km/h 
	 * @param sn The value of the <sn> tag of the XML - i.e. quantity of snow or ice on the road in cm
	 * @param ra The value of the <ra> tag of the XML - i.e. quantity of rain on the road in mm
	 * @param qpsn The value of the <qp-sn> tag of the XML - i.e. Total (1 hr) snow precipitation in cm
	 * @param qpra The value of the <qp-ra> tag of the XML - i.e. Total (1 hr) rain precipitation in mm
	 * @param cc The value of the <cc> tag of the XML - i.e. octal cloud coverage (0-8)
	 * @param sf The value of the <sf> tag of the XML - i.e. downwards solar radiation flux at ground surface in W/m2 
	 * @param ir The value of the <ir> tag of the XML - i.e. downwards infrared radiation flux at ground surface in W/m2 
	 * @param fv The value of the <fv> tag of the XML - i.e. vapor flux in W/m2
	 * @param fc The value of the <fc> tag of the XML - i.e. sensible heat in W/m2
	 * @param fa The value of the <fa> tag of the XML - i.e. anthropogenic flux at ground surface in W/m2. 
	 * @param fq The value of the <fg> tag of the XML - i.e. ground exchange flux in W/m2 
	 * @param bb The value of the <bb> tag of the XML - i.e. blackbody effect in W/m2 
	 * @param fp The value of the <fp> tag of the XML - i.e. phase change in W/m2
	 * @param rc The value of the <rc> tag of the XML - i.e. road condition expressed in Metro code (possible values 1,2,3,4,5,6,7,8)
	 * @param st The value of the <st> tag of the XML - i.e. road surface temperature in degrees Celsius
	 * @param sst The value of the <sst> tag of the XML - i.e. road subsurface temperature in degrees Celsius.	 * 		 
	 */	
	@JsonCreator
	public RoadForecastXMLprediction(
			@JsonProperty("roadcast-time") Instant roadcastTime, 
			@JsonProperty("hh") Double hh,
			@JsonProperty("at") Double at,
			@JsonProperty("td") Double td,
			@JsonProperty("ws") Double ws,
			@JsonProperty("sn") Double sn,
			@JsonProperty("ra") Double ra,
			@JsonProperty("qp-sn") Double qpsn,
			@JsonProperty("qp-ra") Double qpra,
			@JsonProperty("cc") MetroOctalCloudCoverage cc,
			@JsonProperty("sf") Double sf,
			@JsonProperty("ir") Double ir,
			@JsonProperty("fv") Double fv,
			@JsonProperty("fc") Double fc,
			@JsonProperty("fa") Double fa,
			@JsonProperty("fg") Double fg,
			@JsonProperty("bb") Double bb,
			@JsonProperty("fp") Double fp,
			@JsonProperty("rc") MetroModelRoadCondition rc,
			@JsonProperty("st") Double st,
			@JsonProperty("sst") Double sst	
			) {		
		this.roadcastTime = roadcastTime; 
		this.hh = hh;
		this.at = at;
		this.td = td;
		this.ws = ws;
		this.sn = sn;
		this.ra = ra;
		this.qpsn = qpsn;
		this.qpra = qpra;
		this.cc = cc;
		this.sf = sf;
		this.ir = ir;
		this.fv = fv;
		this.fc = fc;
		this.fa = fa;
		this.fg = fg; 
		this.bb = bb;
		this.fp = fp;
		this.rc = rc;
		this.st = st;
		this.sst = sst;	
	}





	/**
	 * Returns the value of {@link #roadcastTime} instance variable of the object.
	 * @return The {@link #roadcastTime} instance variable.
	 */	
	@JsonProperty("roadcast-time")//the name in the XML will not be the same as the name of variable (it will be roadcast-time instead roadcastTime)
	public Instant getRoadcastTime() {
		return this.roadcastTime;
	}

	/**
	 * Returns the value of {@link #hh} instance variable of the object.
	 * @return The {@link #hh} instance variable.
	 */
	@JsonProperty("hh")//the name in the XML will be the same as the name of variable (i.e. hh)
	public Double getHh() {
		return this.hh;	
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
	 * Returns the value of {@link #ws} instance variable of the object.
	 * @return The {@link #ws} instance variable.
	 */
	@JsonProperty("ws")//the name in the XML will be the same as the name of variable (i.e. ws)
	public Double getWs() {
		return this.ws;	
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
	 * Returns the value of {@link #ra} instance variable of the object.
	 * @return The {@link #ra} instance variable.
	 */
	@JsonProperty("ra")//the name in the XML will be the same as the name of variable (i.e. ra)
	public Double getRa() {
		return this.ra;	
	}

	/**
	 * Returns the value of {@link #qpsn} instance variable of the object.
	 * @return The {@link #qpsn} instance variable.
	 */
	@JsonProperty("qp-sn")//the name in the XML will not be the same as the name of variable (it will be qp-sn instead of qpsn)
	public Double getQpsn() {
		return this.qpsn;	
	}

	/**
	 * Returns the value of {@link #qpra} instance variable of the object.
	 * @return The {@link #qpra} instance variable.
	 */
	@JsonProperty("qp-ra")//the name in the XML will not be the same as the name of variable (it will be qp-ra instead of qpra)
	public Double getQpra() {
		return this.qpra;	
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
	 * Returns the value of {@link #fv} instance variable of the object.
	 * @return The {@link #fv} instance variable.
	 */
	@JsonProperty("fv")//the name in the XML will be the same as the name of variable (i.e. fv)
	public Double getFv() {
		return this.fv;
	}

	/**
	 * Returns the value of {@link #fc} instance variable of the object.
	 * @return The {@link #fc} instance variable.
	 */
	@JsonProperty("fc")//the name in the XML will be the same as the name of variable (i.e. fc)
	public Double getFc() {
		return this.fc;
	}

	/**
	 * Returns the value of {@link #fa} instance variable of the object.
	 * @return The {@link #fa} instance variable.
	 */
	@JsonProperty("fa")//the name in the XML will be the same as the name of variable (i.e. fa)
	public Double getFa() {
		return this.fa;
	}

	/**
	 * Returns the value of {@link #fg} instance variable of the object.
	 * @return The {@link #fg} instance variable.
	 */
	@JsonProperty("fg")//the name in the XML will be the same as the name of variable (i.e. fg)
	public Double getFg() {
		return this.fg;
	}

	/**
	 * Returns the value of {@link #bb} instance variable of the object.
	 * @return The {@link #bb} instance variable.
	 */
	@JsonProperty("bb")//the name in the XML will be the same as the name of variable (i.e. bb)
	public Double getBb() {
		return this.bb;
	}

	/**
	 * Returns the value of {@link #fp} instance variable of the object.
	 * @return The {@link #fp} instance variable.
	 */
	@JsonProperty("fp")//the name in the XML will be the same as the name of variable (i.e. fp)
	public Double getFp() {
		return this.fp;
	} 

	/**
	 * Returns the value of {@link #rc} instance variable of the object.
	 * @return The {@link #rc} instance variable.
	 */
	@JsonProperty("rc")//the name in the XML will be the same as the name of variable (i.e. rc)
	public MetroModelRoadCondition getRc() {
		return this.rc;
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


	//-------------------methods to determine when two objects are equal - in our case they are equal when they have the same roadcast---------
	//-------------------time - this is because two "roadcasts" cannot be at the same time ---------------------------------------------------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Output_roadcast_(METRo) --------------------------------------
	//-------------------if we make a list containing RoadForecastXMLprediction objects - an roadcast with the same roadcast time---------------
	//-------------------as is the roadcast time of object that is already in a list - should not be added to the list 
	
	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. RoadForecastXMLprediction objects are the same if they have the same roadcast time, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this RoadForecastXMLprediction instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same roadcast time as this instance. 
	 * In other words, two RoadForecastXMLprediction instances are
	 * considered to be equal if they have the same roadcast time. 
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
		RoadForecastXMLprediction other = (RoadForecastXMLprediction) o;
		//compare the values (they should be numerically equal) 
		if (this.getRoadcastTime().compareTo(other.getRoadcastTime())!=0) {
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
		result = prime * result + ((this.roadcastTime == null) ? 0 : this.roadcastTime.hashCode());		
		return result;
	}




	//-------------------implementation of compareTo method - in order to be able to sort the observations----------------------------------------------------------
	//-------------------the observations should be sorted by their observation time (the oldest observation first)----------------------
	//-------------------see: https://framagit.org/metroprojects/metro/wikis/Input_observation_QA/QC_(METRo) ------------------------------------- --------------------------------------
	//TODO: implementation of a null safe comparator should maybe be improved - see https://stackoverflow.com/questions/481813/how-to-simplify-a-null-safe-compareto-implementation
	@Override
	public int compareTo(RoadForecastXMLprediction other) {
		if (this.roadcastTime == null ^ other.roadcastTime == null) {
			return (this.roadcastTime == null) ? -1 : 1;
		}
		//NOTE: if both are null they are considered equal!
		if (this.roadcastTime == null && other.roadcastTime == null) {
			return 0;
		}
		return this.roadcastTime.compareTo(other.roadcastTime);			
	}
}

