/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.db;

import java.util.HashSet;

import com.cgs.jt.rwis.route.ReferencePointDescription;

/**
 * Defines the methods for database operations (insertion, retrieval,...) related to description of reference points.
 * 
 * @author  Jernej Trnkoczy
 *
 */
//the interface provides (some of the) CRUD database operations upon the {@link ReferencePointDescription} objects. 
//NOTE: Due to the application design some of the operations may operate upon subset of data and not upon "complete" 
//object (in other words the design may not strictly follow ORM patterns)
public interface ReferencePointDescriptionDAO {


	/**
	 * Stores information in the given {@link com.cgs.jt.rwis.route.ReferencePointDescription} instance into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * description exists in a database for a given region and location (region/lat/lon triple) at any given time. In other words there 
	 * should not be two or more database entries with the same values in region/latitude/longitude columns. 
	 * 
	 * NOTE: It is mandatory that the input {@link ReferencePointDescription} instances are validated (for being null, containing null 
	 * instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link ReferencePointDescription} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again . 
	 *  
	 * 
	 * @param rpd The reference point description to store in the database.
	 *  
	 */
	public void insert(ReferencePointDescription rpd);



	/**
	 * Retrieve a set of {@link ReferencePointDescription} instances that are associated with the given region.
	 * 
	 * NOTE: The implementations of this method do not need to validate the returned object, meaning they can contain null or "wrong" values - the objects
	 * are populated with whatever data is found in the database. The validation should be performed by the caller of this method.
	 * 
	 * @param region specifies the region for which to retrieve the reference points descriptions.
	 *  
	 * @return A set of reference points descriptions associated with the given region. If no reference point is associated 
	 * with the specified region null should be returned.
	 * 
	 */		
	public HashSet<ReferencePointDescription> retrieve(String region);

}


