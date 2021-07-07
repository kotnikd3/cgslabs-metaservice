/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */

package com.cgs.jt.rwis.metro;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Represents one of the directions that together form the visible horizon description. 
 * This object can be used as a representation of the individual <direction> XML tags in the station description XML (Metro model input). 
 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
 *
 * @author  Jernej Trnkoczy
 * 
 */

@JsonPropertyOrder({"azimuth", "elevation"})
public class VisibleHorizonDirection implements Comparable<VisibleHorizonDirection>{

	/**Represents the <azimuth> tag of the XML.*/
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	private Double azimuth;	

	/**Represents the <elevation> tag of the XML.*/
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	private Double elevation;



	/**
	 * Constructor with arguments. 
	 * 
	 * @param azimuth The azimuth of the horizon - i.e. value in the <azimuth> tag of the XML.
	 * @param elevation The elevation of the horizon - i.e. the value of the <elevation> tag of the XML.
	 * 		 
	 */	
	@JsonCreator
	public VisibleHorizonDirection(
			@JsonProperty("azimuth") Double azimuth, 
			@JsonProperty("elevation") Double elevation			
			) {		
		this.azimuth = azimuth;		
		this.elevation = elevation;		
	}





	/**
	 * Returns the value of {@link #azimuth} instance variable of the object.
	 * @return The {@link #azimuth} instance variable.
	 */	
	@JsonProperty("azimuth")//the name in the XML will be tha same as the name of variable (i.e. azimuth)
	public Double getAzimuth() {
		return this.azimuth;
	}
	

	/**
	 * Returns the value of {@link #elevation} instance variable of the object.
	 * @return The {@link #elevation} instance variable.
	 */
	@JsonProperty("elevation")//the name in the XML will be the same as the name of variable (i.e. elevation)
	public Double getElevation() {
		return this.elevation;	
	}



	//-------------------methods to determine when two objects are equal - in our case they are equal when they have the same azimuth--------
	//-------------------this is because two elevations at the same azimuth are meaningless - if you provide such data Metro will freak out-------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) --------------------------------------
	//-------------------if we make a list containing StationDescriptionXMLdirection objects - an object with azimuth that is -----------------
	//-------------------the same as the azimuth of another object that is already in a list - should not be added to the list (since it--------------
	//-------------------would produce invalid XML--------------------------------------------------------------------------------------------

	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. StationDescriptionXMLdirection objects are the same if they have the same azimuth, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this StationDescriptionXMLdirection instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same azimuth as this instance. 
	 * In other words, two StationDescriptionXMLdirection instances are
	 * considered to be equal if they have the same azimuth. 
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
		VisibleHorizonDirection other = (VisibleHorizonDirection) o;
		//compare the values (they should be numerically equal) 
		if (this.azimuth.compareTo(other.azimuth)!=0) {
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
		result = prime * result + ((this.azimuth == null) ? 0 : this.azimuth.hashCode());		
		return result;
	}




	//-------------------implementation of compareTo method - in order to be able to sort the objects----------------------------------------------------------
	//-------------------the objects should be sorted by their azimuth (the first <direction> tag is the one with the smallest azimuth----------------------
	//-------------------see also: https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) --------------------------------------
	//TODO: implementation of a null safe comparator should maybe be improved - see https://stackoverflow.com/questions/481813/how-to-simplify-a-null-safe-compareto-implementation
	@Override
	public int compareTo(VisibleHorizonDirection other) {
		if (this.azimuth == null ^ other.azimuth == null) {
			return (this.azimuth == null) ? -1 : 1;
		}
		//NOTE: if both are null they are considered equal!
		if (this.azimuth == null && other.azimuth == null) {
			return 0;
		}
		return this.azimuth.compareTo(other.azimuth);			
	}
}


