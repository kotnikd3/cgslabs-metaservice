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
 * Java bean that represents the road forecast XML - i.e. XML containing the produced road forecasts (Metro model output). 
 * @see https://framagit.org/metroprojects/metro/wikis/Output_roadcast_(METRo)
 * NOTE: it is assumed that Metro is run without --output-subsurface-levels  flag, therefote the output XML does not contain
 * the sub surface temperature levels.
 * @see  https://framagit.org/metroprojects/metro/wikis/Vertical_levels_(METRo) for more on sub surface temperature levels.
 *
 * @author  Jernej Trnkoczy
 * 
 */
@JsonPropertyOrder({"header", "prediction-list"})
@JsonRootName("roadcast")
public class RoadForecastXML {
	
	/** Represents the <header> tag of the XML */
	@JsonProperty("header")//the name in the XML will be the same as the name of variable (i.e. header)
	private RoadForecastXMLheader header;


	/** Represents the list of road forecasts (i.e. <prediction-list> tag) in the XML. */
	//NOTE: this is a bit counterintuitive - but the name of the variable (or the name in the @JsonProperty("prediction") annotation - if 
	//provided) will represent the names of each element of the list - see https://stackabuse.com/serialize-and-deserialize-xml-in-java-with-jackson/
	//the wrapper element (that will include all elements of the list) will by default have the same name. However we can 
	//turn off the wrapper element, or we can set the name of the wrapper element by using @JacksonXmlElementWrapper annotation
	//@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlElementWrapper(localName = "prediction-list")
	@JsonProperty("prediction")
	private TreeSet<RoadForecastXMLprediction> predictions;



	/**
	 * Constructor with arguments. 
	 * 
	 * @param header The information included in the header part of the XML. 
	 * @param predictions The list of the road forecasts in the <prediction-list> tag of XML.	 
	 */	
	//NOTE: this constructor is not used by Jackson when deserializing from XML
	@JsonIgnore
	public RoadForecastXML(			
			RoadForecastXMLheader header, 
			TreeSet<RoadForecastXMLprediction> predictions
			) {		
		this.header = header;
		this.predictions = predictions;	
	}
	
	/**Default constructor**/
	//NOTE: this constructor is used by Jackson when deserializing from XML
	//NOTE: we could have used the above constructor with arguments also for Jackson deserialization (so we would then have only 
	//one constructor in RoadForecastXML class and creating RoadForecastXML object with instance variables un-set would be impossible). 
	//An example of such constructor would be:
	/*
	@JsonCreator
	public RoadForecastXML(			
			@JsonProperty("header") RoadForecastXMLheader header, 			
			@JsonProperty("XXXXXXX") ArrayList<RoadForecastXMLprediction> predictions
			) {		
		this.header = header;
		this.predictions = predictions;			

	}*/	
	//However there is a bug in jackson XML handling of wrappers/wrapped - https://github.com/FasterXML/jackson-dataformat-xml/issues/188
	//therefore you cannot use the correct "predictions-list" name (e.g. @JsonProperty("predictions-list"). To circumvent this you can 
	//put an arbitrary name (e.g. @JsonProperty("XXXXXXX")) and it will work - however this is a dirty hack - so we will rather have two constructors 
	//in the class (and risk that somebody will call the default constructor - which will lead to un-set instance variables...)
	@JsonCreator
	public RoadForecastXML() {};




	/**
	 * Returns the value of {@link #header} instance variable of the object.
	 * @return The {@link #header} instance variable.
	 */		
	public RoadForecastXMLheader getHeader() {
		return this.header;
	}

	/**
	 * Returns the value of {@link #predictions} instance variable of the object.
	 * @return The {@link #predictions} instance variable.
	 */	
	public TreeSet<RoadForecastXMLprediction> getPredictions() {
		return this.predictions;	
	}
	
}
