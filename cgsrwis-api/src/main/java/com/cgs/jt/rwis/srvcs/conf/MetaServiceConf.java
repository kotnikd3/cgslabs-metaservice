/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.conf;


/**
 * Represents the constants related to the Metaservice that are used in different places in the code
 * (usually in the Metaservice resource classes as well as in the HTTP client of this service).
 * 
 * @author Jernej Trnkoczy
 *
 */
public class MetaServiceConf {

	
	/**The root path of the service (the service will then be accessible at http://localhost:port/the-root-path).*/
	public static final String META_ROOT_PATH="";	
	
	
	/**The relative path to the endpoint for subscriptions manipulation.*/
	public static final String SUBSCR_PATH = "/subscriptions";
	
	/**The relative path to the endpoint for metroconfig manipulation.*/
	public static final String METROCONFIG_PATH = "/metroconfig";
	
	
}
