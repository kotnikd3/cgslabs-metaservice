/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.db;

import java.util.HashMap;
import java.util.HashSet;

import com.cgs.jt.rwis.api.VisualizationSubscription;
import com.cgs.jt.rwis.api.params.ForecastedParameter;

/**
 * Defines the methods for database operations (insertion, retrieval,...) related to the visualization subscriptions - i.e. 
 * which forecast model provides visualizations for which parameters.
 * 
 * @author  Jernej Trnkoczy
 *
 */
//Defines the methods for performing (some of the) CRUD database operations upon the {@link VisualizationSubscription} 
//objects. NOTE: Due to the application design (and non-relational nature of some databases) some of the methods 
//do not return "whole" {@link VisualizationSubscription} object(s), instead they return only a "part" (i.e. subset 
//of data) of the object(s). This is therefore not a clean DAO pattern!
//TODO: Maybe even use some other pattern (instead of DAO) - e.g. https://thinkinginobjects.com/2012/08/26/dont-use-dao-use-repository/
public interface VisualizationSubscriptionDAO {


	/**
	 * Stores information in the given {@link com.cgs.jt.VisualizationSubscription} instance into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * visualization subscription exists in a database for a given forecast model, and parameter. In other 
	 * words there should not be two or more database entries with the same values for the forecast model id,
	 * and visualized forecasted parameter. 
	 *  
	 * NOTE: It is mandatory that the input {@link VisualizationSubscription} instances are validated (for being null, 
	 * containing null instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link VisualizationSubscription} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again .  
	 *   
	 * @param pfs The subscription to store in the database.	 
	 * 
	 */
	public void insert(VisualizationSubscription vs);


	/**
	 * Retrieves all the parameters that are associated with a given forecast model (defined by forecastModelId). 
	 *  	 
	 * NOTE: The implementations of this method do not need to validate the returned objects, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 *  	
	 * @param forecastModelId The identification of the forecast model for which we would like to find which parameters from this model should be 
	 * visualized.
	 * @return All of the parameters that need to be visualized from the output data of the given model. In other words from the model which is specified as input 
	 * parameter all of the returned parameters must be visalized. If not a single parameter is associated with the specified forecast model then null is returned.
	 * 
	 */
	public HashSet<ForecastedParameter> retrieveVisualizationSubscriptionsByModel(String forecastModelId);




}

