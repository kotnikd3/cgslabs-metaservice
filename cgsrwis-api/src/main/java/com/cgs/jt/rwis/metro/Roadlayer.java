/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */

package com.cgs.jt.rwis.metro;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.cgs.jt.rwis.metro.inoutvalues.MetroRoadlayerType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Represents a road layer description. 
 * This object can be used as a representation of the individual <roadlayer> XML tags in the station description XML (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"position", "type", "thickness"})
public class Roadlayer implements Comparable<Roadlayer>{

	/**Represents the value of the <position> tag of the XML.
	 * NOTE: The position starts with number 1 (the road surface layer)
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the road layer position is not null 
	@Min(value = 1, message = "The position of road layer is an integer bigger than zero")
	private Integer position;

	/**Represents the value of the <type> tag of the XML.*/
	//for the validator (packaged with Dropwizard - Hibernate) to check that the road layer type is not null
	//NOTE: due to the implementation (see @JsonCreator in MetroRoadlayerType) if in JSON there is "unknown" String
	//this will result in null MetroRoadlayerType - and since it must not be null - the @NotNull validator will report
	//object not valid. This is why we added the "and must be one of the supported types" in the message.
	@NotNull(message = "may not be null and must be one of the supported types")  
	private MetroRoadlayerType type;

	/**Represents the value of the <thickness> tag of the XML.
	 * NOTE: the value is expressed in meters
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the road layer thickness is not null 
	private Double thickness;



	/**
	 * Constructor with arguments. 
	 * 
	 * @param position The position of the layer - i.e. value in the <position> tag of the XML.
	 * @param type The type of the layer - i.e. the value of the <type> tag of the XML.
	 * @param thickness The thickness of the layer in meters - i.e. the value of the <thickenss> tag of the XML.
	 * 		 
	 */	
	@JsonCreator
	public Roadlayer(
			@JsonProperty("position") Integer position, 
			@JsonProperty("type") MetroRoadlayerType type, 
			@JsonProperty("thickness") Double thickness			
			) {		
		this.position = position;
		this.type = type;
		this.thickness = thickness;		
	}





	/**
	 * Returns the value of {@link #position} instance variable of the object.
	 * @return The {@link #position} instance variable.
	 */	
	@JsonProperty("position")//the name in the XML will be tha same as the name of variable (i.e. position)
	public Integer getPosition() {
		return this.position;
	}

	/**
	 * Returns the value of {@link #type} instance variable of the object.
	 * @return The {@link #type} instance variable.
	 */
	@JsonProperty("type")//the name in the XML will be the same as the name of variable (i.e. type)
	public MetroRoadlayerType getType() {
		return this.type;	
	}

	/**
	 * Returns the value of {@link #thickness} instance variable of the object.
	 * @return The {@link #thickness} instance variable.
	 */
	@JsonProperty("thickness")//the name in the XML will be the same as the name of variable (i.e. thickness)
	public Double getThickness() {
		return this.thickness;	
	}



	//-------------------methods to determine when two objects are equal - in our case they are equal when they have the same position--------
	//-------------------this is because two road layers cannot be at the same position - if you provide such data Metro will freak out-------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) --------------------------------------
	//-------------------if we make a list containing StationDescriptionXMLroadlayer objects - a layer with position that is -----------------
	//-------------------the same as the position of object that is already in a list - should not be added to the list (since it--------------
	//-------------------would produce invalid XML--------------------------------------------------------------------------------------------

	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. StationDescriptionXMLroadlayer objects are the same if they have the same position, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this StationDescriptionXMLroadlayer instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same layer position as this instance. 
	 * In other words, two StationDescriptionXMLroadlayer instances are
	 * considered to be equal if they have the same position. 
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
		Roadlayer other = (Roadlayer) o;
		//compare the values (they should be numerically equal) 
		if (this.getPosition().compareTo(other.getPosition())!=0) {
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
		result = prime * result + ((this.position == null) ? 0 : this.position.hashCode());		
		return result;
	}




	//-------------------implementation of compareTo method - in order to be able to sort the layers----------------------------------------------------------
	//-------------------the layers should be sorted by their position (the top layer is the road surface layer and should be the first)----------------------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) --------------------------------------
	//TODO: implementation of a null safe comparator should maybe be improved - see https://stackoverflow.com/questions/481813/how-to-simplify-a-null-safe-compareto-implementation
	@Override
	public int compareTo(Roadlayer other) {			 
		if (this.position == null ^ other.position == null) {
			return (this.position == null) ? -1 : 1;
		}
		//NOTE: if both are null they are considered equal!
		if (this.position == null && other.position == null) {
			return 0;
		}
		return this.position.compareTo(other.position);

	}
}

