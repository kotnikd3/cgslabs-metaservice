/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */
package com.cgs.jt.rwis.metro.xml;

import java.util.TreeSet;

import com.cgs.jt.rwis.metro.Roadlayer;
import com.cgs.jt.rwis.metro.VisibleHorizonDirection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * Java bean that represents the station description XML - i.e. XML containing the description of the road weather station (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"header", "roadlayer-list"})
@JsonRootName("station")
public class StationDescriptionXML {
	
	/** Represents the <header> tag of the XML */
	@JsonProperty("header")//the name in the XML will be the same as the name of variable (i.e. header)
	private StationDescriptionXMLheader header;

	/** Represents the list of different layers of the road (i.e. <roadlayer-list> tag) in the XML. */
	//NOTE: this is a bit counterintuitive - but the name of the variable (or the name in the @JsonProperty("roadlayer") annotation - if 
	//provided) will represent the names of each element of the list - see https://stackabuse.com/serialize-and-deserialize-xml-in-java-with-jackson/
	//the wrapper element (that will include all elements of the list) will by default have the same name. However we can 
	//turn off the wrapper element, or we can set the name of the wrapper element by using @JacksonXmlElementWrapper annotation
	//@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlElementWrapper(localName = "roadlayer-list")
	@JsonProperty("roadlayer")
	private TreeSet<Roadlayer> roadlayers;
	
	/** Represents the list of directions (azimuth/elevation pairs) which describes the visible horizon (for the sunshadow algorithm
	 * of Metro (i.e. <visible-horizon> tag) in the XML. */
	//NOTE: this is a bit counterintuitive - but the name of the variable (or the name in the @JsonProperty("direction") annotation - if 
	//provided) will represent the names of each element of the list - see https://stackabuse.com/serialize-and-deserialize-xml-in-java-with-jackson/
	//the wrapper element (that will include all elements of the list) will by default have the same name. However we can 
	//turn off the wrapper element, or we can set the name of the wrapper element by using @JacksonXmlElementWrapper annotation
	//@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlElementWrapper(localName = "visible-horizon")
	@JsonProperty("direction")
	private TreeSet<VisibleHorizonDirection> directions;



	/**
	 * Constructor with arguments. 
	 * 
	 * @param header The information included in the header part of the XML. 
	 * @param roadlayers The list of road layers included in the <roadlayer-list> tag of XML.
	 * @param directions The list of directions (azimuth/elevation pairs) included in the <visible-horizon> tag of XML.	 
	 */	
	//NOTE: this constructor is not used by Jackson when deserializing from XML
	@JsonIgnore
	public StationDescriptionXML(			
			StationDescriptionXMLheader header, 
			TreeSet<Roadlayer> roadlayers,
			TreeSet<VisibleHorizonDirection> directions
			) {		
		this.header = header;
		this.roadlayers = roadlayers;
		this.directions = directions;
	}
	
	
	/**
	 * Constructor with arguments. 
	 * 
	 * @param header The information included in the header part of the XML. 
	 * @param roadlayers The list of road layers included in the <roadlayer-list> tag of XML.	 
	 */	
	//NOTE: this constructor is not used by Jackson when deserializing from XML
	//NOTE: this constructor constructs object without setting "directions" instance variable - and in intended for the case
	//when we do not want the <visible-horizon> tag in the XML document.
	@JsonIgnore
	public StationDescriptionXML(			
			StationDescriptionXMLheader header, 
			TreeSet<Roadlayer> roadlayers			
			) {		
		this.header = header;
		this.roadlayers = roadlayers;		
	}
	
	/**Default constructor**/
	//NOTE: this constructor is used by Jackson when deserializing from XML
	//NOTE: we could have used the above constructor with arguments also for Jackson deserialization (so we would then have only 
	//one constructor in StationDescriptionXML class and creating StationDescriptionXML object with instance variables un-set would be impossible). 
	//An example of such constructor would be:
	/*
	@JsonCreator
	public StationDescriptionXML(			
			@JsonProperty("header") StationDescriptionXMLheader header, 			
			@JsonProperty("XXXXXXX") ArrayList<StationDescriptionXMLroadlayer> roadlayers
			) {		
		this.header = header;
		this.roadlayers = roadlayers;			

	}*/	
	//However there is a bug in jackson XML handling of wrappers/wrapped - https://github.com/FasterXML/jackson-dataformat-xml/issues/188
	//therefore you cannot use the correct "roadlayer-list" name (e.g. @JsonProperty("roadlayer-list"). To circumvent this you can 
	//put an arbitrary name (e.g. @JsonProperty("XXXXXXX")) and it will work - however this is a dirty hack - so we will rather have two constructors 
	//in the class (and risk that somebody will call the default constructor - which will lead to un-set instance variables...)
	@JsonCreator
	public StationDescriptionXML() {};




	/**
	 * Returns the value of {@link #header} instance variable of the object.
	 * @return The {@link #header} instance variable.
	 */		
	public StationDescriptionXMLheader getHeader() {
		return this.header;
	}

	/**
	 * Returns the value of {@link #roadlayers} instance variable of the object.
	 * @return The {@link #roadlayers} instance variable.
	 */	
	public TreeSet<Roadlayer> getRoadlayers() {
		return this.roadlayers;	
	}
	
	/**
	 * Returns the value of {@link #directions} instance variable of the object.
	 * @return The {@link #directions} instance variable.
	 */	
	public TreeSet<VisibleHorizonDirection> getDirections() {
		return this.directions;	
	}
	
}
