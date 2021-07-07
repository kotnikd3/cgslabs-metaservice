/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.db;

import java.util.HashMap;
import java.util.HashSet;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.api.ParameterForecastSubscription;
import com.cgs.jt.rwis.api.params.ForecastedParameter;

/**
 * Defines the methods for database operations (insertion, retrieval,...) related to the parameter forecast subscriptions - i.e. 
 * which forecast model provides forecasts for which parameters at which locations.
 * 
 * @author  Jernej Trnkoczy
 *
 */
//Defines the methods for performing (some of the) CRUD database operations upon the {@link ParameterForecastSubscription} 
//objects. NOTE: Due to the application design (and non-relational nature of some databases) some of the methods 
//do not return "whole" {@link ParameterForecastSubscription} object(s), instead they return only a "part" (i.e. subset 
//of data) of the object(s). For example {@link #retrieveParameterSubscriptionsByModel()}
//method does not return a list of {@link ParameterSubscription} objects. This is therefore not a clean DAO pattern!
//TODO: Maybe even use some other pattern (instead of DAO) - e.g. https://thinkinginobjects.com/2012/08/26/dont-use-dao-use-repository/
public interface ParameterForecastSubscriptionDAO {


	/**
	 * Stores information in the given {@link com.cgs.jt.ParameterForecastSubscription} instance into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * parameter forecast subscription exists in a database for a given forecast model, location and parameter. In other 
	 * words there should not be two or more database entries with the same values for the forecast model id,
	 * location (latitude and longitude), and forecasted parameter. 
	 *  
	 * NOTE: It is mandatory that the input {@link ParameterForecastSubscription} instances are validated (for being null, 
	 * containing null instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link ParameterForecastSubscription} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again .  
	 *   
	 * @param pfs The subscription to store in the database.	 
	 * 
	 */
	public void insert(ParameterForecastSubscription psub);


	/**
	 * Retrieves all the earth surface points and parameters (in each earth surface point a list of forecast 
	 * parameters that customers are interested in this earth surface point) that are associated with a given 
	 * forecast model (defined by forecastModelId). NOTE: earth surface point is essentially a 
	 * geographic location (latitude/longitude pair) together with the elevation of ground or water surface at
	 * this geographic location (the elevation is needed for example for recalculating MSL atmospheric pressure
	 * into atmospheric pressure at the earth or water surface).
	 * The returned data structure is like the following example: 
	 * earthSurfacePoint1:(param1, param7, param9); earthSurfacePoint2:(param2, param3); earthSurfacePoint3:(param1, param9, param33) etc.
	 * The returned data is encapsulated in a HashMap with EarthSurfacePoint objects as keys and HashSet of 
	 * parameter names (Strings) as value. The returned data does not contain duplicated geographic locations 
	 * (i.e. two EarthSurfacePoint object instances with the same latitude and longitude) and the set of parameters 
	 * associated with an earth surface point does not contain duplicated parameter names.
	 * 	 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 *  	
	 * @param forecastModelId The identification of the forecast model for which we would like to find associated earth surface points/parameters data.
	 * @return All of the earth surface points, each with a set of parameters of interest (at the earth surface point) - 
	 * that are associated with the specified forecast model. In other words the model which is specified as input 
	 * parameter supplies forecast data for all of the earth surface points (and for all surface-point-associated parameters)
	 * If no surface points/parameters are associated with the specified forecast model then null is returned.
	 * 
	 */
	public HashMap<EarthSurfacePoint, HashSet<ForecastedParameter>> retrieveParameterSubscriptionsByModel(String forecastModelId);




}
