/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.conf;

/**
 * Represents the constants related to the ForecastService that are used in different places in the code
 * (usually in the ForecastService resource classes as well as in the HTTP client of this service).
 * 
 * @author Jernej Trnkoczy
 *
 */
public class ForecastServiceConf {
	
	/**The root path of the service (the service will then be accessible at http://localhost:port/the-root-path).*/
	public static final String FORCST_ROOT_PATH="/forecasts";
	
	/**The relative path to the endpoint for forecast insertion.*/
	public static final String INSERT_FORECASTS_PATH="/";	
	
	/**The relative path to the endpoint for retrieving the latest forecasted values from the given "from" date/time.*/
	public static final String GET_LATEST_VALS_FROM="/latestValsFrom";
	
	/**The relative path to the endpoint for retrieving the latest forecasted values that have absolute time on the mathematically
	 * closed interval between given {@code from} and {@code to} dates/times.
	 */
	public static final String GET_LATEST_VALS_FROM_TO="/latestValsFromTo";
	
	/*The relative path to the endpoint for last N forecasts retrieval.*/
	//public static final String GET_LAST_N_FORCST_PATH="/lastNforecasts";
	
	/*The relative path to the endpoint for retrieving the latest forecasted values of the last N forecasts.*/
	//public static final String GET_LATEST_FROM_LAST_N_FORCST_PATH="/latestValsOfLastNforecasts";
	
	/*The relative path to the endpoint for retrieveing the number of forecasts with reference time newer than given date/time.*/
	//public static final String GET_NUM_FORCST_FROM_PATH="/numForecastsFrom";	
	
	
	/*The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	//public static final String QUERYPARAM_NUM_FORECASTS = "n";
	/**The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	public static final String QUERYPARAM_FROM = "from";
	/**The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	public static final String QUERYPARAM_TO = "to";
	/**The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	public static final String QUERYPARAM_FORCST_PARAM_NAME = "parameterName";
	/**The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	public static final String QUERYPARAM_LOC_LAT = "locationLatitude";
	/**The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	public static final String QUERYPARAM_LOC_LON = "locationLongitude";
	/**The definition of the name of a query parameter (NOTE: can be used by multiple endpoints of the forecast service).*/
	public static final String QUERYPARAM_FORECAST_MODEL_ID = "forecastModelId";
}
