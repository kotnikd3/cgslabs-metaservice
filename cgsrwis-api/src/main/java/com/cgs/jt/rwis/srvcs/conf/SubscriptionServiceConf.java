/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.conf;

/**
 * Represents the constants related to the SubscriptionService that are used in different places in the code
 * (usually in the SubscriptionService resource classes as well as in the HTTP client of this service).
 * 
 * @author Jernej Trnkoczy
 *
 */
public class SubscriptionServiceConf {
	
	/**The root path of the service (the service will then be accessible at http://localhost:port/the-root-path).*/
	public static final String SUBSCR_ROOT_PATH="/subscriptions";
	
	/**The relative path to the endpoint for parameter forecast subscriptions by model retrieval.*/
	public static final String GET_FORECAST_PARAM_SUBSCR_BY_MODEL_PATH = "/parameterForecastSubscriptionsByModel";	
	
	/**The relative path to the endpoint for parameter visualizations subscriptions by model retrieval.*/
	public static final String GET_VISUALIZATION_SUBSCR_BY_MODEL_PATH = "/parameterVisualizationsSubscriptionsByModel";
	
	/**The name of a query parameter for parameter forecast subscriptions by model and parameter visualizations descriptions by model retrieval endpoints.*/
	public static final String QUERYPARAM_FORECAST_MODEL_ID = "forecastModelId";
		
	/**The relative path to the endpoint for parameter forecast subscriptions insertion.*/
	public static final String INSERT_FORECAST_PARAM_SUBSCRIPTIONS_PATH="/parameterForecastSubscription";	
	
	/**The relative path to the endpoint for Metro location description retrieval.*/
	public static final String GET_METRO_LOC_DESCR_PATH = "/getMetroLocationDescription"; 
	/**The name of a query parameter for Metro location description retrieval endpoint.*/
	public static final String QUERYPARAM_LOC_LAT = "locationLatitude";
	/**The name of a query parameter for Metro location description retrieval endpoint.*/
	public static final String QUERYPARAM_LOC_LON = "locationLongitude";	
	
	/**The relative path to the endpoint for Metro location description insertion.*/
	public static final String INSERT_METRO_LOC_DESCR_PATH = "/insertMetroLocationDescription";	
	
	/**The relative path to the endpoint for reference points descriptions retrieval.*/
	public static final String GET_REF_POINTS_DESCR_BY_REGION_PATH = "/getReferencePointsDescriptions"; 
	/**The name of a query parameter for reference points descriptions retrieval endpoint.*/
	public static final String QUERYPARAM_REGION = "region";
	
	/**The relative path to the endpoint for reference point description insertion.*/
	public static final String INSERT_REF_POINT_DESCR_PATH = "/insertReferencePointDescription";
	

}
