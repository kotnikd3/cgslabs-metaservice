/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;


/**
 * Defines the constants related to the Cassandra database.
 * NOTE: these are only constants that are not included in the Cassandra configuration file.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class DBconstants {
	/**
	 * Represents the name of the table for queries "Give me all the parameter subscriptions
	 * (i.e. parameter name and location of point of interest) that are associated
	 * with certain forecast model. "
	 */
	public static final String TABLE_PARAMETER_SUBSCRIPTIONS_BY_MODEL = "parameter_subscriptions_by_model";
	
		
	/**
	 * Represents the name of the table for queries "give me the forecasted values 
	 * for specific parameter name, location and forecast model that have absolute forecast time in the given time range.
	 */
	public static final String TABLE_FORECASTED_VALUES_BY_PARAMETER_LOCATION_AND_MODEL = "forecasted_values_by_param_loc_and_model";
	
	/**
	 * Represents the name of the table for queries "Give me the latest forecasted values (i.e. the ones with the biggest 
	 * reference time) of specified parameter on all locations that are associated with specified customer 
	 * and from all the forecasting models that cover these locations and have absolute time of forecasted value equal to the
	 * specified time. 
	 */
	public static final String TABLE_FORECASTED_VALUES_BY_CUSTOMER_PARAMETER_AND_TIME = "forecasted_values_by_customer_param_and_time";
		
	
	/**
	 * Represents the name of the table for queries "give me the N most recent (freshest) measurements 
	 * for specific parameter name and location.
	 */
	public static final String TABLE_MEASUREMENTS_BY_PARAMETER_SENSORID_AND_LOCATION = "measurements_by_param_sensorid_and_loc";
	
	
	
	/**
	 * Represents the name of the table for queries "give me the latest (freshest) measurements of certain parameter for
	 * all locations that are associated with certain customer.
	 */
	public static final String TABLE_MEASUREMENTS_BY_CUSTOMER_AND_PARAMETER = "measurements_by_customer_and_param";	
	
	
	/**
	 * Represents the name of the table for queries "Give me all the location description data ({@link MetroLocationDescription} 
	 * (that is needed to run Metro model) for specific location and model (e.g. Metro01 model, or Metro02 model, ...)"
	 */
	public static final String TABLE_METRO_LOCATION_DESCRIPTION_BY_LOCATION_AND_MODEL = "metro_loc_description_by_loc_mod";	
	/**
	 * Represents the user-defined type that will store the data representing a single road layer  
	 */
	public static final String UDT_ROADLAYER = "udtroadlayer";	
	/**
	 * Represents the user-defined type that will store the data representing a single visible horizon direction 
	 */
	public static final String UDT_VISIBLEHORIZONDIRECTION = "udtvisiblehorizondirection";	
	/**
	 * Represents the user-defined type that will store the data representing a GeographicLocation (i.e. a location with
	 * lat/lon and ID).  
	 */
	public static final String UDT_GEOGRAPHICLOCATION = "udtgeographiclocation";
	/**
	 * Represents the user-defined type that will store the data representing a {@link DataSource} instance. 
	 */
	public static final String UDT_DATASOURCE = "udtdatasource";	
		
	
	
	
	/**
	 * Represents the name of the table for queries "Give me all the reference points that belong to certain region"
	 */
	public static final String TABLE_REFERENCE_POINTS_BY_REGION = "reference_points_by_region";	
	/**
	 * Represents the user-defined type that will store the data representing a ReferencedPoint (i.e. a point that is 
	 * referenced by a reference point. 
	 */
	public static final String UDT_REFERENCEDPOINT = "udtreferencedpoint";	
	/**
	 * Represents the user-defined type that will store the data describing what is considered a "weather condition"
	 * for a certain reference point 
	 */
	public static final String UDT_WEATHERCONDITIONDESCRIPTION = "udtweatherconditiondescription";
	


}
