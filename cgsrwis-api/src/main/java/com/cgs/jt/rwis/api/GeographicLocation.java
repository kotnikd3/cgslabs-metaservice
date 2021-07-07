/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the container for the metadata describing the geographic location which is defined by
 * latitude and longitude pair. The lat/lon pair is in the EPSG:900913 / EPSG:3785 / OSGEO:41001 Web 
 * Mercator projection.  
 * 
 * @author  Jernej Trnkoczy
 * 
 */

//NOTE: The lat/lon pair is in the EPSG:900913 / EPSG:3785 / OSGEO:41001 Web Mercator projection - https://en.wikipedia.org/wiki/Web_Mercator_projection, 
//see also https://wiki.openstreetmap.org/wiki/EPSG:3857 . This projection assumes that the Earth is a perfect sphere (instead of ellipsoid). 
//Therefore we can calculate the distance between two lat/lon points using Haversine formula - https://en.wikipedia.org/wiki/Haversine_formula
//The same coordinate system is used by Redis database for GEO functions - see https://redis.io/commands/geoadd - therefore we can use these 
//lat/lon pairs directly (when inserting into the Redis database). Since the projection used is "simplified" Mercator - and since the ordinate 
//y of the Mercator projection becomes infinite at the poles - the map must be truncated at some latitude less than ninety degrees. For Web 
//Mercator projection the valid latitudes are from -85.05112878 to 85.05112878 degrees - therefore our implementation expects values in the 
//range from -85 to +85.
public class GeographicLocation {


	/**
	 * Represents the location_id which is composed from the latitude and longitude information (the location is 
	 * uniquely identified by it's lat/lon pair). 
	 * The format of the location_id should be the following:
	 * 1)the numbers in the id represent location's latitude/longitude in the normal lat/lon projection.
	 * 2)the latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!)
	 * 3)the number of digits after the decimal point should be at least 6 (this gives enough precision to differentiate 
	 * locations that are very close to each other. 
	 * 4)the first number in a string represents latitude and should end with "lat" (for human readability)
	 * 5)the second number in a string represents longitude and should end with "lon" (for human readability)
	 * 6)the lat and long parts are separated by underscore _ 
	 * An example of such string is:
	 * 46.228600lat_14.363200lon 
	 */
	//TODO: if we found out that the id is not needed then delete the id instance variable from this class!
	//TODO: however if the id is needed then replace it with geohash - just like Redis does it - see https://redis.io/commands/geoadd and https://en.wikipedia.org/wiki/Geohash
	@NotEmpty //for the Hibernate validator (packaged with Dropwizard) to check that the id is not null or empty string - when deserializing from JSON
	private String id;


	/**
	 * Represents the latitude of the location in the "normal" lat/lon projection.
	 * The latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!) 
	 * The precision has to be at least 6 fractional digits (i.e. the number of digits following the decimal point)
	 * - or in other words it should be precise to 1/1000000 of degree.  This is needed to differentiate locations 
	 * that are very close to each other (e.g. a couple of meters).
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the latitude is not null when deserializing from JSON
	@Min(value = -85, message = "Latitude value cannot be less than -85")
	@Max(value = +85, message = "Latitude value cannot be greater than +85")
	private Double latitude;

	/**
	 * Represents the longitude of the location in the "normal" lat/lon projection.
	 * The latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!) 
	 * The precision has to be at least 6 fractional digits (i.e. the number of digits following the decimal point)
	 * - or in other words it should be precise to 1/1000000 of degree. This is needed to differentiate locations 
	 * that are extremely close to each other (e.g. a couple of meters).
	 * 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the longitude is not null when deserializing from JSON
	@Min(value = -180, message = "Longitude value cannot be less than -180")
	@Max(value = +180, message = "Longitude value cannot be greater than +180")
	private Double longitude;	


	/**
	 * Constructor with arguments. NOTE: the id of the location is calculated from given latitude and longitude automatically.
	 * 
	 * @param latitude The latitude of the location in the "normal" lat/lon projection. The latitude 
	 * is expressed in degrees with decimal fraction (not minutes and seconds!). The
	 * precision has to be at least 6 fractional digits (i.e. the number of digits following the decimal point)
	 * - or in other words it should be precise to 1/1000000 of degree. 
	 * @param longitude The longitude of the location in the "normal" lat/lon projection. The longitude is
	 * expressed in degrees with decimal fraction (not minutes and seconds!). The latitude 
	 * is expressed in degrees with decimal fraction (not minutes and seconds!). The
	 * precision has to be at least 6 fractional digits (i.e. the number of digits following the decimal point)
	 * - or in other words it should be precise to 1/1000000 of degree. 	 
	 */
	@JsonCreator
	public GeographicLocation(
			@JsonProperty("latitude") Double latitude, 
			@JsonProperty("longitude") Double longitude
			) {		
		this.latitude = latitude;
		this.longitude = longitude;	
		if(latitude != null && longitude != null) {
			this.id = this.getIdFromLatLon(latitude, longitude);
		}
		else {
			this.id = null;
		}

	}



	/**
	 * Constructor. NOTE: the longitude and latitude are obtained from the given location id.
	 * 
	 * @param id The location id. The format of this is should be the following:
	 * 1)the numbers in the id represent location's latitude/longitude in the normal lat/lon projection.
	 * 2)the latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!)
	 * 3)the number of digits after the decimal point should be at least 6 (this gives enough precision to 
	 * differentiate geographic locations that are very close to each other) 
	 * 4)the first number in a string represents latitude and should end with "lat" (for human readability)
	 * 5)the second number in a string represents longitude and should end with "lon" (for human readability)
	 * 6)the lat and long parts are separated by underscore _ 
	 * An example of such string is:
	 * 46.228600lat_14.363200lon 	 
	 * 
	 */	
	public GeographicLocation(String id) {
		this.id = id;
		this.latitude = this.getLocationLatFromId(id);
		this.longitude = this.getLocationLonFromId(id);		
	}





	/**
	 * Returns the value of {@link #id} instance variable of the object.
	 * @return The {@link #id} instance variable.
	 */
	@JsonIgnore//we will not send the ID in the JSON
	public String getId() {
		return this.id;
	}

	/**
	 * Returns the value of {@link #latitude} instance variable of the object.
	 * @return The {@link #latitude} instance variable.
	 */
	@JsonProperty("latitude")//the name in the JSON will be the same as the name of variable (i.e. latitude)
	public Double getLatitude() {
		return this.latitude;	
	}

	/**
	 * Returns the value of {@link #longitude} instance variable of the object.
	 * @return The {@link #longitude} instance variable.
	 */
	@JsonProperty("longitude")//the name in the JSON will be the same as the name of variable (i.e. longitude)
	public Double getLongitude() {
		return this.longitude;	
	}	




	/**
	 * Utility method to convert latitude and longitude into the location ID String. 
	 * @param latitude The latitude of the location. 
	 * @param longitude The longitude of the location.
	 * 
	 * 
	 * @return The location ID String. The format of the returned id String is the following:
	 * 1)the latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!)
	 * 2)the number of digits after the decimal point is 6 (this gives us enough precision to differentiate stations that are only centimeters apart ;) 
	 * 3)the first number in a string represents latitude and ends with "lat" (for human readability)
	 * 4)the second number in a string represents longitude and ends with "lon" (for human readability)
	 * 5)the lat and long parts are separated by underscore _ 
	 * An example of such a string is:
	 * 46.228600lat_14.363000lon 
	 */
	private String getIdFromLatLon(Double latitude, Double longitude) {
		DecimalFormat df = new DecimalFormat("#.000000");
		df.setRoundingMode(RoundingMode.DOWN);
		String lat = df.format(latitude);
		String lon = df.format(longitude);
		return lat+"lat_"+lon+"lon";		
	}




	/**
	 * Utility method that calculates latitude by parsing location_id String
	 * @param the location_id String which contains the latitude/longitude information. The format of this string should be the following:
	 * 1)the latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!)
	 * 2)the number of digits after the decimal point should be 6 (this gives us enough precision to differentiate stations that are only centimeters apart ;) 
	 * 3)the first number in a string represents latitude and should end with "lat" (for human readability)
	 * 4)the second number in a string represents longitude and should end with "lon" (for human readability)
	 * 5)the lat and long parts are separated by underscore _ 
	 * An example of such string is:
	 * 46.228600lat_14.363200lon 
	 * 
	 * @return The latitude obtained from location_id expressed as Double value (latitude in degrees with decimal fraction - not minutes!). 
	 * null if the latitude could not be parsed from the given input String (because the location_id has wrong format).
	 */
	private Double getLocationLatFromId(String location_id) {
		String[] parts = location_id.split("_");
		String latString = parts[0];
		if(latString!=null && latString.endsWith("lat")) {
			String latNumber = latString.replaceAll("lat", "");
			try {
				return Double.parseDouble(latNumber);
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
		else {
			return null;
		}
	}






	/**
	 * Utility method that calculates longitude by parsing location_id String
	 * @param the location_id String which contains the latitude/longitude information. The format of this string should be the following:
	 * 1)the latitude and longitude are expressed in degrees with decimal fraction (not minutes and seconds!)
	 * 2)the number of digits after the decimal point should be 6 (this gives us enough precision to differentiate stations that are only centimeters apart ;) 
	 * 3)the first number in a string represents latitude and should end with "lat" (for human readability)
	 * 4)the second number in a string represents longitude and should end with "lon" (for human readability)
	 * 5)the lat and long parts are separated by underscore _ 
	 * An example of such string is:
	 * 46.228600lat_14.363200lon 
	 * 
	 * @return The longitude obtained from location_id expressed as Double value (latitude in degrees with decimal fraction - not minutes!).
	 * null if the latitude could not be parsed from the given input String (because the location_id has wrong format).
	 */
	private Double getLocationLonFromId(String location_id) {
		String[] parts = location_id.split("_");
		String lonString = parts[1];
		if(lonString!=null && lonString.endsWith("lon")) {			
			String lonNumber = lonString.replaceAll("lon", "");			
			try {
				return Double.parseDouble(lonNumber);
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
		else {
			return null;
		}
	}

	//Overrides the shallow comparison method of the Java.lang.Object 
	//(see https://www.geeksforgeeks.org/equals-hashcode-methods-java/)
	//with a Deep comparison according to the below defined rules 
	//(i.e. GeographicLocation objects are the same if they have the same lat and lon 
	//coordinates, are non-null, and belong to the same class).
	//Inspired by example here: https://www.codexpedia.com/java/java-set-and-hashset-with-custom-class/
	/**
	 * Compares the specified object with this Location instance for equality. The method 
	 * returns <code>true</code> if and only if the specified object has the same latitude
	 * and longitude as this instance. In other words, two GeographicLocations instances are
	 * considered to be equal if they have the same value of latitude and longitude. 
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
		GeographicLocation other = (GeographicLocation) o;
		//compare the values (they should be numerically equal) 
		if (this.getLatitude().compareTo(other.getLatitude())!=0) {
			return false;
		}
		if(this.getLongitude().compareTo(other.getLongitude())!=0) {
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
		result = prime * result + ((this.latitude == null) ? 0 : this.latitude.hashCode());
		result = prime * result + ((this.longitude == null) ? 0 : this.longitude.hashCode());
		return result;
	}

}