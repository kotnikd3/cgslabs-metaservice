/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.db;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.metro.MetroLocationDescription;

/**
 * Defines the methods for database operations (insertion, retrieval,...) related to metro location description.
 * 
 * @author  Jernej Trnkoczy
 *
 */
//the interface provides (some of the) CRUD database operations upon the {@link MetroLocationDescription} objects. 
//NOTE: Due to the application design some of the operations may operate upon subset of data and not upon "complete" 
//object (in other words the design may not strictly follow ORM patterns)
public interface MetroLocationDescriptionDAO {


	/**
	 * Stores information in the given {@link com.cgs.jt.rwis.metro.MetroLocationDescription} instance into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * description exists in a database for a given location (lat/lon pair) at any given time. In other words there 
	 * should not be two or more database entries with the same values for the location latitude and longitude. Two 
	 * location descriptions for the same location are considered equal regardless if they contain different description 
	 * data!
	 * 
	 * NOTE: It is mandatory that the input {@link MetroLocationDescription} instances are validated (for being null, containing null 
	 * instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link MetroLocationDescription} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again . 
	 * HOWEVER THE {@link MetroLocationDescription.visibleHorizonDescription} variable represents an EXCEPTION. This field 
	 * is optional and may thus be null or empty {@link TreeSet} even after the validation. If the database technology used 
	 * by the implementing function does not tolerate null insertion (e.g. Cassandra creates tombstones) this field
	 * must be checked by the implementing method. 
	 * 
	 * 
	 * @param mld The Metro location description to store in the database.
	 *  
	 */
	public void insert(MetroLocationDescription mld);



	/**
	 * Retrieve the (one and only one) Metro description that is describing certain location and is processed by a certain Metro model.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned object, meaning it can contain null or "wrong" values - the object
	 * is populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param geoLocation specifies the location for which to retrieve the description.
	 *  
	 * @return A Metro description of the location. null if there is no such description.
	 */		
	public MetroLocationDescription retrieve(GeographicLocation geoLocation, String modelId);

}

