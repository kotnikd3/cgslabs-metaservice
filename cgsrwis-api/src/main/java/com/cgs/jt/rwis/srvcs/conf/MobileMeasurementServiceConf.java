/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.conf;

/**
 * Represents the constants related to the MobileMeasurementService that are used in different places in the code
 * (usually in the MobileMeasurementService resource classes as well as in the HTTP client of this service).
 * 
 * @author Jernej Trnkoczy
 *
 */
public class MobileMeasurementServiceConf {
	
	/**The root path of the service (the service will then be accessible at http://localhost:port/the-root-path).*/
	public static final String MOBMSRMNTS_ROOT_PATH="/mobilemeasurements";
	
	/**The relative path to the endpoint for mobile measurements insertion.*/
	public static final String INSERT_MOBILEMEASUREMENTS_PATH="/";	

}


