/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.db;

import java.time.Instant;
import java.util.HashMap;
import java.util.TreeMap;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterMeasurements;
import com.cgs.jt.rwis.api.ParameterMeasurementsWithMetadata;
import com.cgs.jt.rwis.api.MeasuredValue;
import com.cgs.jt.rwis.api.params.MeasuredParameter;

/**
 * Defines the methods for database operations (insertion, retrieval,...) related to measurements.
 * 
 * @author  Jernej Trnkoczy
 *
 */
//Defines the methods for performing (some of the) CRUD database operations upon the {@link ParameterMeasurements} and 
//{@link ParameterMeasurementsOwned} objects. 
//NOTE: Due to the application design some of the operations may operate upon subset of data and not upon "complete" 
//object (in other words the design may not strictly following ORM patterns)
public interface ParameterMeasurementsDAO {


	/**
	 * Stores information in the given {@link com.cgs.jt.ParameterMeasurementsWithMetadata.ParameterMeasurementsOwned} object into the database.
	 *  
	 * NOTE: It is mandatory that the input {@link ParameterMeasurementsWithMetadata} instances are validated (for being null, containing null 
	 * instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link ParameterMeasurements} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again. 
	 * 
	 * @param pm The measurements to store in the database.
	 *  
	 */
	//NOTE: The implementation must be such that support all the "retrieve" functions that are defined below. It is upon the implementer
	//to decide whether the information will be in one table only, or maybe two tables (as is the case in Cassandra scenario).
	public void insert(ParameterMeasurementsWithMetadata pm);



	/**
	 * Retrieve the latest N measurements for a specified parameter name, sensor number and location.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param n specifies the number of the measurements to retrieve.
	 * @param param The parameter for which the measurements are retrieved.
	 * @param sensorId The id of the sensor (there can be multiple sensors measuring the same parameter on the same location).
	 * @param location The location for which the measurements are retrieved.	
	 * 
	 * @return The latest measurements retrieved from the database. null if not a single measurement can be retrieved.
	 */
	public TreeMap<Instant, Double> retrieveLastNmeasurements(int n, MeasuredParameter param, String sensorId, GeographicLocation location);
	
	
	
	/**
	 * Retrieve the measurements (for a specified parameter name, sensor number and location) that were measured later or at the same time as
	 * the provided date/time.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param from specifies the date/time - only the measurements measured after or at this date/time are retrieved.
	 * @param param The parameter for which the measurements are retrieved.
	 * @param sensorId The id of the sensor (there can be multiple sensors measuring the same parameter on the same location).
	 * @param location The location for which the measurements are retrieved.	
	 * 
	 * @return Measurements retrieved from the database. null if not a single measurement can be retrieved.
	 */
	public TreeMap<Instant, Double> retrieveMeasurementsFrom(Instant from, MeasuredParameter param, String seensorId, GeographicLocation location);
	
	
	
	
	/**
	 * Retrieve the measurements (for a specified parameter name, sensor number and location) that were measured on the mathematically closed
	 * time interval between provided {@code from} and {@code to} dates/times.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param from Only the measurements measured after or at the same date/time are returned.
	 * @param to Only the measurements measured before or at the same date/time are returned.
	 * @param param The parameter for which the measurements are retrieved.
	 * @param sensorId The id of the sensor (there can be multiple sensors measuring the same parameter on the same location).
	 * @param location The location for which the measurements are retrieved.	
	 * 
	 * @return Measurements retrieved from the database. null if not a single measurement can be retrieved.
	 */
	public TreeMap<Instant, Double> retrieveMeasurementsFromTo(Instant from, Instant to, MeasuredParameter param, String sensorId, GeographicLocation location);

	
	
	
	/**
	 * Retrieve the latest measurements for a specified parameter from sensors that are associated with the given 
	 * customer.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param param The parameter for which the measurements are retrieved.
	 * @param customerId The ID of customer. 
	 * 
	 * @return Latest measurements for this particular parameter and customer retrieved from the database. null if not a single measurement can be retrieved.
	 */
	public HashMap<GeographicLocation, HashMap<String, MeasuredValue>> retrieveLatestMeasurementsOfCustomer(MeasuredParameter param, String customerId);

}



