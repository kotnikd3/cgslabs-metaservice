/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the container for the metadata describing the location on Earth's surface which is defined 
 * by a geographic position(latitude/longitude) and elevation (in meters above mean sea level). This 
 * information can be used to describe position of the RWIS station for example.
 * NOTE: we are only considering the locations on the Earth's surface here (and not arbitrary location in
 * 3D space). The elevation is actually defined by the geographic location and the Earth's profile. 
 * In other words the value of {@link #heightOfSurface} variable for a given lat/lon pair is determined
 * by the Earth's relief. 
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class EarthSurfacePoint{


	/**
	 * Represents the geographic location of this point.	 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the geoLocation is not null when deserializing from JSON
	@Valid //validator of the EarthSurfacePoint needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then EarthSurfacePoint is also not valid)
	private GeographicLocation geoLocation;


	/**
	 * Represents the elevation (i.e. meters above mean sea level) of the ground or water surface for this 
	 * point. The unit is [m]. The value is needed mainly for presentation in the GUI, 
	 * re-calculation of barometric pressure MSL to pressure @ Ground or water surface, etc.
	 * The precision should be around +/-1 meter. 
	 * NOTE: we are only considering the locations on the Earth's surface here (and not arbitrary location in
	 * 3D space). The elevation is actually defined by the geographic location and the Earth's relief.
	 * 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the elevation is not null when deserializing from JSON
	@Min(value = -433, message = "Elevation value cannot be less than -433")
    @Max(value = +8848, message = "Elevation value cannot be greater than +8848")	
	private Double elevation;



	/**
	 * Constructor with arguments.
	 * 
	 * @param geoLocation The geographic location of this point.
	 * @param elevation The elevation (i.e. meters above mean sea level) of the ground or water surface at the geographic
	 * location. The unit is [m]. The precision should be around +/-1 meter. 
	 */
	@JsonCreator
	public EarthSurfacePoint(
			@JsonProperty("geoLocation") GeographicLocation geoLocation, 
			@JsonProperty("elevation") Double elevation
			) {		
		this.geoLocation = geoLocation;
		this.elevation = elevation;		
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
	 * Returns the value of {@link #elevation} instance variable of the object.
	 * @return The {@link #elevation} instance variable.
	 */
	@JsonProperty("elevation")//the name in the JSON will be the same as the name of variable (i.e. elevation)
	public Double getElevation() {
		return this.elevation;	
	}	
	
	

	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. two Earth's surface points are the same if they have the same 
	//geographic location, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this EarthSurfacePoint instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same geographic 
	 * location as this instance. In other words, two EarthSurfacePoint instances are considered to be
	 * equal if they have the same geographic position (latitude and longitude). 
	 * NOTE: the value of the {@link #elevation} instance variable is (regarding equality) irrelevant since 
	 * it is determined by geographic position (lat/lon) and the Earth's relief - so it is always
	 * the same if lat/lon pair is the same.
	 * 
	 *
	 * @param o the object to be compared for equality with this Location instance
	 * @return <code>true</code> if the specified object is equal to this Location instance
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
		EarthSurfacePoint other = (EarthSurfacePoint) o;
		//compare the values (they should be numerically equal) 
		if (this.getGeoLocation().getLatitude().compareTo(other.getGeoLocation().getLatitude())!=0) {
			return false;
		}
		if(this.getGeoLocation().getLongitude().compareTo(other.getGeoLocation().getLongitude())!=0) {
			return false;
		}
		return true;
	}

	//Override the hashCode() method of Java.lang.Object according to the rules described below.
	//The only requirement for the hashCode() implementation is that equal objects (by their equals method)
	//should have the same hash code - see https://www.sitepoint.com/how-to-implement-javas-hashcode-correctly/	
	/**
	 * Returns the hash code value for this Location instance.     
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

}
