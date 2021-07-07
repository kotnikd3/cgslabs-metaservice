/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.dbim;

import com.cgs.jt.rwis.route.MobileSample;

/**
 * This interface defines the methods for performing (some of the) CRUD database operations upon the 
 * {@link MobileSample} objects. 
 * 
 * @author  Jernej Trnkoczy
 *
 */
public interface MobileSampleDAO  {


	/**
	 * Stores information in the given {@link com.cgs.jt.rwis.route.MobileSample} object into the database.
	 *  
	 * NOTE: The implementing function must perform UPSERT rather than INSERT. It is a requirement that ONLY ONE 
	 * measurement exists in a database for a given vehicle ID and sampe date/time. In other words there 
	 * should not be two or more database entries with the same vehicle ID and sample date/time values. Two measurements 
	 * with the same vehicle ID and sample date/time are considered equal regardless if they have different measured values!
	 *  
	 * NOTE: It is mandatory that the input {@link MobileSample} instances are validated (for being null, containing null 
	 * instance variables, containing empty collections as instance variables, etc.) according to the annotations in 
	 * the {@link MobileSample} class before they are passed to this function. The implementations of this method
	 * therefore are not required to validate the input instances again.   
	 *   
	 * @param msd The mobile sample data to store in the database.
	 *  
	 */
	public void insert(MobileSample msd);
	

}




