/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.time.Instant;
import java.util.HashSet;
import java.util.TreeMap;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an extension of the {@link ParameterMeasurements} with addition of the information defining
 * which customers want to see the measurements for certain parameter on certain location in the GUI on the 
 * geographical map (a map in the GUI with all the locations that the customer is interested in). If a set 
 * of customers is empty - this means that not a single customer wants to see these particular measurements 
 * on a geographical map.
 *
 * @author  Jernej Trnkoczy
 * 
 */
//NOTE: It would be nice to omit this class and live with ParameterMeasurements object only. However this class is necessary
//because we cannot implement "insert(ParameterMeasurements)" service endpoint - because in case the measurements should be 
//shown on the geographical map the service needs to know which customers want the measurement to be shown on the map.
//This is a consequence of a design decision - the measurements that are shown on the map are inserted in two Cassandra 
//database tables - one that stores the entire history of the measurements (and does not contain column "customer") and the 
//other that stores only the latest measurements on all the locations that need to be shown to a specific customer. 
//TODO: in the first place this is NOT A NICE DESIGN - the sensors should not care how the database is implemented!
//Anyhow - even if we go this way we have two options here:
//1) The sensors send ParameterMeasurements objects (without metadata) - and then the service for each measurement that needs
//   to be inserted queries the database first (to find out whether this particular measurement needs to be inserted in one table
//   or two tables - depending on the system metadata), and based on the retrieved metadata decides in which table to insert. The 
//   good side of this approach is that sensors are not aware of any metadata describing configuration. The downside is that for 
//   each insert there has to be an expensive retrieve operation first.
//2) The sensors send ParameterMeasurementsWithMetadata object (containing also the needed metadata). The good side is that insert
//   is very fast - because it really is only insert (without any additional expensive retrieve operation first). The downside is 
//   that the sensor must know about system metadata, also 3rd party sensors might "cheat" (because it is the sensor who decides 
//   which customers can see it's data - by inserting the right metadata).
//TODO: For now we choosed option 2) HOWEVER THIS NEEDS SOME FURTHER THINKING AND DECISIONS!!!
public class ParameterMeasurementsWithMetadata extends ParameterMeasurements{

	/**
	 * Represents the set of customer IDs that want to see the measurements on the geographical map in the GUI. If a set 
	 * of customers is empty - this means that not a single customer wants to see these particular measurements 
	 * on a geographical map. 
	 */	
	@NotNull
	@NotEmpty
	HashSet<String> customerIDs;



	/**
	 * Constructor. Creates ParameterMeasurements object populated with measured values.
	 * @param parameter The measured parameter (i.e. the parameter for which the measurements were taken).
	 * @param sensorNumber The number of the sensor (needed because there can be several sensors measuring the same parameter at the same geographic location).
	 * @param geographicLocation The geographic location (lat/lon) where measurements were taken.
	 * @param measuredValues The measured parameter values.
	 * @param customerIDs A set of customer IDs that are authorized to the measurements from this location (e.g. to view them in the graphical user interface). 
	 * 
	 */
	@JsonCreator
	public ParameterMeasurementsWithMetadata (
			@JsonProperty("parameter") MeasuredParameter parameter, 
			@JsonProperty("sensorId") String sensorId,
			@JsonProperty("geographicLocation") GeographicLocation geographicLocation,			 
			@JsonProperty("measuredValues") TreeMap<Instant, Double> measuredValues,
			@JsonProperty("customerIDs") HashSet<String> customerIDs){		
		super (parameter, sensorId, geographicLocation, measuredValues);
		this.customerIDs = customerIDs;
	}


	/**
	 * Returns the {@link #customerIDs} instance variable of this object.
	 * @return The {@link #customerIDs} instance variable.
	 *
	 */
	@JsonProperty("customerIDs")//the name in the JSON will be the same as the name of variable (i.e. customerIDs)
	public HashSet<String> getCustomerIDs() {
		return this.customerIDs;
	}


}
