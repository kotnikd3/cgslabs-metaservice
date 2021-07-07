/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */
package com.cgs.jt.rwis.metro.xml;

import java.util.ArrayList;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * Java bean that represents the weather forecasts XML (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"header", "prediction-list"})
@JsonRootName("forecast")
public class WeatherForecastXML {

	/** Represents the <header> tag of the XML */
	@JsonProperty("header")//the name in the XML will be the same as the name of variable (i.e. header)
	private WeatherForecastXMLheader header;

	/** Represents the list of forecasts (i.e. <prediction-list> tag) in the XML. */
	//NOTE: Metro uses ISO 8601 date/time format which should be parsable into java.time.Instant object
	//without problems (e.g. Instant instant = Instant.parse("2018-07-17T09:59:00Z")). However if the seconds
	//are missing in the string (e.g. Instant instant = Instant.parse("2018-07-17T09:59Z")) an exception is thrown.
	//Since metro uses only hh:mm (without seconds, milliseconds, nanoseconds...) format we need to serialize Instant
	//into XML in 2019-10-23T13:18Z format, and we need to be able to deserialize from this format into Instant.
	//This problem is solved by specifying the pattern with @JsonFormat. 
	//see also: https://stackoverflow.com/questions/36252556/jackson-deserialize-iso8601-fromatted-date-time-into-java8-instant
	@JacksonXmlElementWrapper(localName = "prediction-list")
	@JsonProperty("prediction")
	private TreeSet<WeatherForecastXMLprediction> forecasts;



	/**
	 * Constructor with arguments. 
	 * 
	 * @param header The information included in the header part of the XML. 
	 * @param forecasts The list of weather forecasts included in the <prediction-list> tag of XML.	 
	 */	
	//NOTE: this constructor is not used by Jackson when deserializing from XML
	@JsonIgnore
	public WeatherForecastXML(			
			WeatherForecastXMLheader header, 
			TreeSet<WeatherForecastXMLprediction> forecasts
			) {		
		this.header = header;
		this.forecasts = forecasts;	
	}

	/**Default constructor**/
	//NOTE: this constructor is used by Jackson when deserializing from XML
	//NOTE: we could have used the above constructor with arguments also for Jackson deserialization (so we would then have only 
	//one constructor in ForecastXML class and creating ForecastXML object with instance variables un-set would be impossible). 
	//An example of such constructor would be:
	/*
	@JsonCreator
	public ForecastXML(			
			@JsonProperty("header") ForecastXMLheader header, 			
			@JsonProperty("XXXXXXX") ArrayList<ForecastXMLprediction> forecasts
			) {		
		this.header = header;
		this.forecasts = forecasts;			

	}*/	
	//However there is a bug in jackson XML handling of wrappers/wrapped - https://github.com/FasterXML/jackson-dataformat-xml/issues/188
	//therefore you cannot use the correct "prediction-list" name (e.g. @JsonProperty("prediction-list"). To circumvent this you can 
	//put an arbitrary name (e.g. XXXXXXXX) and it will work - however this is a dirty hack - so we will rather have two constructors 
	//in the class (and risk that somebody will call the default constructor - which will lead to un-set instance variables...)
	@JsonCreator
	public WeatherForecastXML() {};




	/**
	 * Returns the value of {@link #header} instance variable of the object.
	 * @return The {@link #header} instance variable.
	 */		
	public WeatherForecastXMLheader getHeader() {
		return this.header;
	}

	/**
	 * Returns the value of {@link #forecasts} instance variable of the object.
	 * @return The {@link #forecasts} instance variable.
	 */	
	public TreeSet<WeatherForecastXMLprediction> getForecasts() {
		return this.forecasts;	
	}

}
