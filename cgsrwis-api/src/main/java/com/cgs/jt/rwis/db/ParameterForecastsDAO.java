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

import com.cgs.jt.rwis.api.ForecastedValue;
import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterForecast;
import com.cgs.jt.rwis.api.ParameterForecastWithMetadata;
import com.cgs.jt.rwis.api.params.ForecastedParameter;


/**
 * Defines the methods for database operations (insertion, retrieval,...) related to forecasts (weather, road,...).
 * 
 * @author  Jernej Trnkoczy
 *
 */
//Defines the methods for performing (some of the) CRUD database operations upon the {@link ParameterForecasts} and 
//{@link ParameterForecastOwned} objects.
//NOTE: Due to the application design some of the operations operate upon subset of data and not upon "complete" object.
//Also some methods write/read into one table and the others write/read from another table.
//(in other words the design is not strictly following ORM patterns). 
public interface ParameterForecastsDAO {

	/**
	 * Stores information in the given {@link com.cgs.jt.rwis.api.ParameterForecastWithMetadata} instance into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * forecasted value for a given forecast model, location, parameter, forecast reference time and forecasted 
	 * value absolute time exists in the database.
	 * In other words there should not be two or more database entries with the same values for the forecast model,
	 * location (latitude and longitude), forecasted parameter, forecast reference time, and absolute time of the forecasted
	 * value. 
	 *  
	 * NOTE: It is mandatory that the input {@link ParameterForecastWithMetadata} instances are validated (for being null, containing null 
	 * instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link ParameterForecastWithMetadata} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again . 
	 * 
	 * @param pfv The forecast to store in the database.
	 *  
	 */
	//NOTE: The implementation must be such that support all the "retrieve" functions that are defined below. It is upon the implementer
	//to decide whether the information will be in one table only, or maybe two tables (as is the case in Cassandra scenario).
	public void insert(ParameterForecastWithMetadata pf);


	/**
	 * Retrieve the latest forecasted values (i.e. those that were calculated by the latest forecast model run = the run with the
	 * biggest forecast reference time) for a specified parameter name, location and forecast model whose forecasted value absolute
	 * time is the same or bigger then the given date/time .
	 * 
	 * 
	 * @param from Only the forecasted values with absolute time bigger or equal to this date/time are retrieved.
	 * @param param The parameter for which the forecasted values are retrieved.
	 * @param location The location for which the forecasted values are retrieved.
	 * @param forecastModelId The id of the forecast model that produced the forecasted values.
	 * 
	 * @return The latest forecasted values (i.e. those that were calculated by the latest forecast model run = the run with the
	 * biggest forecast reference time) for a specified parameter name, location and forecast model whose forecasted value absolute
	 * time is the same or bigger then the given date/time .
	 */
	public TreeMap<Instant, Double> retrieveLatestForecastedValsFrom(Instant from, ForecastedParameter param, GeographicLocation location, String forecastModelId);


	/**
	 * Retrieve the latest forecasted values (i.e. those that were calculated by the latest forecast model run = the run with the
	 * biggest forecast reference time) for a specified parameter name, location and forecast model whose forecasted value absolute
	 * time is on the mathematically closed time interval between {@code from} and {@code to}.
	 * 
	 * 
	 * @param from Only the forecasted values with absolute time bigger or equal to this date/time are retrieved.
	 * @param to Only the forecasted values with absolute time smaller or equal to this date/time are retrieved.
	 * @param param The parameter for which the forecasted values are retrieved.
	 * @param location The location for which the forecasted values are retrieved.
	 * @param forecastModelId The id of the forecast model that produced the forecasted values.
	 * 
	 * @return The latest forecasted values (i.e. those that were calculated by the latest forecast model run = the run with the
	 * biggest forecast reference time) for a specified parameter name, location and forecast model whose forecasted value absolute
	 * time is on the mathematically closed time interval between {@code from} and {@code to}.
	 */
	public TreeMap<Instant, Double> retrieveLatestForecastedValsFromTo(Instant from, Instant to, ForecastedParameter param, GeographicLocation location, String forecastModelId);



	/**
	 * Retrieve the latest forecasted values for a specified parameter on all locations and from all forecasting models that are associated with the given 
	 * customer and have absolute forecast time the same as is the given time.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param param The parameter for which the forecasted values are retrieved.
	 * @param customerId The ID of customer. 
	 * @param atTime The retrieved forecasted values should have absolute forecast time the same as this time
	 * 
	 * @return Latest forecasted values for this particular parameter, customer and time on all locations and produced on all forecasting models (to which customer
	 * is "subscribed". null if not a single forecasted value can be retrieved.
	 */
	public HashMap<GeographicLocation, HashMap<String, ForecastedValue>> retrieveLatestForecastedValuesOfCustomer(ForecastedParameter param, String customerId, Instant atTime);




	//TODO:
	//#################################################################################################
	//#########TOLE JE KLELE ZACASNO - SAMO TOLIKO CASA DOKLER NE RAZMISLIMO KAKO BO TO NA KONCU - CUSTOMERS PA TO... - GLEJ ZGORAJ insert(ParameterForecastWithMetadata)#######
	//#################################################################################################
	public void insert(ParameterForecast pf);



	/*
	 * Stores information in the given {@link com.cgs.jt.wdm.ParameterForecasts} instance into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * forecast exists in a database for a given forecast model, location, parameter and forecast reference time.
	 * In other words there should not be two or more database entries with the same values for the forecast model,
	 * location (latitude and longitude), forecasted parameter and reference time of the forecast. Two forecasts 
	 * produced by the same forecast model, for a given parameter name, on a given geographic location, and 
	 * at given forecast reference time are considered equal regardless if they have different forecasted values!
	 *  
	 * NOTE: It is mandatory that the input {@link ParameterForecasts} instances are validated (for being null, containing null 
	 * instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link ParameterForecasts} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again . 
	 * 
	 * @param pf The forecasts to store in the database.
	 *  
	 */
	//public void insert(ParameterForecasts pf);



	/*
	 * Retrieves the last N forecasts for a specified parameter name, location and 
	 * forecast model.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.	 
	 * 
	 * @param n specifies the number of the forecasts to retrieve.
	 * @param param The parameter for which the forecasts are retrieved.
	 * @param location The location for which the forecasts are retrieved.
	 * @param forecastModelId The id of the forecast model that produced the forecasts.
	 * 
	 * @return Latest N forecasts retrieved from the database. null if not a single forecast can be retrieved.
	 */		 
	//public ParameterForecasts retrieveLastNforecasts(int n, ForecastedParameter param, GeographicLocation location, String forecastModelId);





	/*
	 * Retrieves the latest forecasted values of the last N (possibly overlapping) forecasts for a specified parameter name, location and 
	 * forecast model.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.	 
	 * 
	 * @param n specifies the number of the forecasts to retrieve.
	 * @param param The parameter for which the forecasts are retrieved.
	 * @param location The location for which the forecasts are retrieved.
	 * @param forecastModelId The id of the forecast model that produced the forecasts.
	 * 
	 * @return Latest forecasted values obtained form the last N (possibly overlapping) forecasts retrieved from the database. null if 
	 * not a single forecasted value can be retrieved.
	 */		 
	//public ParameterForecastedLatestVals retrieveLatestValsOfLastNforecasts(int n, ForecastedParameter param, GeographicLocation location, String forecastModelId);






	/*
	 * Retrieve the number of forecasts (for a specified parameter name, location and forecast model) whose forecast reference time is the same or
	 * bigger then the given date/time .
	 * 
	 * 
	 * @param from Only the forecasts with forecast reference time bigger or equal to this date/time are retrieved.
	 * @param param The parameter for which the forecasts are retrieved.
	 * @param location The location for which the forecasts are retrieved.
	 * @param forecastModelId The id of the forecast model that produced the forecasts.
	 * 
	 * @return The number of the forecasts that are "newer" (or at the same time) than a given date/time.
	 */
	//public int retrieveNumberOfForecastsFrom(Instant from, ForecastedParameter param, GeographicLocation location, String forecastModelId);

}



