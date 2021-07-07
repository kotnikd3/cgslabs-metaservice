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
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.GeographicLocation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a description of a referenced point (a point that is referenced by a reference point :).
 *  
 * @author  Jernej Trnkoczy
 * 
 */
public class ReferencedPoint implements Comparable<ReferencedPoint>{

	/**
	 * Represents the geographic location of this referenced point.	 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@Valid //validator of the ReferencedPoint needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then ReferencedPoint is not valid either)
	private GeographicLocation geoLocation;

	/**
	 * Represents the distance from the reference point (that is referring to this ReferencedPoint) to this referenced point.
	 * The distance unit is [m].	
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@DecimalMin(value = "5.0", message = "Minimum distance from the reference point to the referenced point is 5.0 m")
	private Double distanceToRefPoint;

	/**
	 * Represents the forecast model from which it is possible to obtain road forecast (Metro output) at this location. 
	 */
	@NotEmpty //for the Hibernate validator (packaged with Dropwizard) to check that the model is not null or empty string - when deserializing from JSON
	//TODO: this needs to be modified. ReferencedPoint is used for calculating both 1) semimeasurements and 2) semiforecasts
	//The module calculating semimearuements does not need information about roadForecastModelId at all... 
	//TODO: The ReferencePointDescription.java needs to be modified too - since for example the module calculating 
	//semimeasurements might have differente "ReferencedPoints" than module calculating semiforecasts! 
	private String roadForecastModelId;




	/**
	 * Constructor with arguments. 
	 * 
	 * @param geoLocation The location of the referenced point.
	 * @param distanceToRefPoint The distance from the reference point (that is referring to this ReferencedPoint) to this referenced point.
	 * @param roadForecastModelId The id of the forecast model from which it is possible to obtain road forecast (Metro output) at this location..
	 * 		 
	 */	
	@JsonCreator
	public ReferencedPoint(
			@JsonProperty("geoLocation") GeographicLocation geoLocation, 
			@JsonProperty("distanceToRefPoint") Double distanceToRefPoint, 
			@JsonProperty("roadForecastModelId") String roadForecastModelId			
			) {		
		this.geoLocation = geoLocation;
		this.distanceToRefPoint = distanceToRefPoint;
		this.roadForecastModelId = roadForecastModelId;		
	}





	/**
	 * Returns the value of {@link #geoLocation} instance variable of the object.
	 * @return The {@link #geoLocation} instance variable.
	 */	
	@JsonProperty("geoLocation")//the name in the JSON will be the same as the name of variable (i.e. geoLocation)
	public GeographicLocation getGeoLocation() {
		return this.geoLocation;
	}

	/**
	 * Returns the value of {@link #distanceToRefPoint} instance variable of the object.
	 * @return The {@link #distanceToRefPoint} instance variable.
	 */
	@JsonProperty("distanceToRefPoint")//the name in the JSON will be the same as the name of variable (i.e. distanceToRefPoint)
	public Double getDistanceToRefPoint() {
		return this.distanceToRefPoint;	
	}

	/**
	 * Returns the value of {@link #roadForecastModelId} instance variable of the object.
	 * @return The {@link #roadForecastModelId} instance variable.
	 */
	@JsonProperty("roadForecastModelId")//the name in the JSON will be the same as the name of variable (i.e. roadForecastModelId)
	public String getRoadForecastModelId() {
		return this.roadForecastModelId;	
	}




	//-------------------methods to determine when two objects are equal - in our case they are equal when they both have the same location--------
	//-------------------this is because two referenced points with the same location cannot exist -------
	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. ReferencedPoint objects are the same if they have the same location, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this ReferencedPoint instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same location (lat/lon pair) as this instance. 
	 * In other words, two ReferencedPoint instances are
	 * considered to be equal if they have the same location (lat/lon pair). 
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
		ReferencedPoint other = (ReferencedPoint) o;
		//compare the values (they should be numerically equal) 
		if (this.getGeoLocation().getLatitude().compareTo(other.getGeoLocation().getLatitude())!=0 || 
				this.getGeoLocation().getLongitude().compareTo(other.getGeoLocation().getLongitude())!=0) {
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
		result = prime * result + ((this.getGeoLocation().getLatitude() == null) ? 0 : this.getGeoLocation().getLatitude().hashCode());
		result = prime * result + ((this.getGeoLocation().getLongitude() == null) ? 0 : this.getGeoLocation().getLongitude().hashCode());
		return result;
	}




	//-------------------implementation of compareTo method - in order to be able to sort the referenced points----------------------------------------------------------
	//-------------------the referenced points should be sorted by their distance to the reference point (the nearest should be the first)----------------------
	//TODO: implementation of a null safe comparator should maybe be improved - see https://stackoverflow.com/questions/481813/how-to-simplify-a-null-safe-compareto-implementation
	@Override
	public int compareTo(ReferencedPoint other) {			 
		if (this.distanceToRefPoint == null ^ other.distanceToRefPoint == null) {
			return (this.distanceToRefPoint == null) ? -1 : 1;
		}
		//NOTE: if both are null they are considered equal!
		if (this.distanceToRefPoint == null && other.distanceToRefPoint == null) {
			return 0;
		}
		return this.distanceToRefPoint.compareTo(other.distanceToRefPoint);

	}


}
