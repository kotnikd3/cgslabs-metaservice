/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.conf;

/**
 * Represents the constants related to the MeasurementService that are used in different places in the code
 * (usually in the MeasurementService resource classes as well as in the HTTP client of this service).
 * 
 * @author Jernej Trnkoczy
 *
 */
public class MeasurementServiceConf {
	
	/**The root path of the service (the service will then be accessible at http://localhost:port/the-root-path).*/
	public static final String MSRMNTS_ROOT_PATH="/measurements";
	
	/**The relative path to the {@link MeasurementServiceResource.insertMeasurements()} endpoint.*/
	public static final String INSERT_MEASUREMENTS_PATH="/";	
	
	/**The relative path to the {@link MeasurementServiceResource.getLastNmeasurements()} endpoint.*/
	public static final String GET_LAST_N_MSRMNTS_PATH="/lastNmeasurements";
	
	/**The relative path to the {@link MeasurementServiceResource.getMeasurementsFrom()} endpoint.*/
	public static final String GET_MSRMNTS_FROM_PATH="/measurementsFrom";	
	
	/**The relative path to the {@link MeasurementServiceResource.getMeasurementsFromTo()} endpoint.*/
	public static final String GET_MSRMNTS_FROM_TO_PATH="/measurementsFromTo";	
	
	/**The relative path to the {@link MeasurementServiceResource.getLatestMeasurements()} endpoint.*/
	public static final String GET_LATEST_MSRMNTS="/latestMeasurements";	
	
	
	
	/**The definition of the name of a query parameter used in {@link MeasurementServiceResource.getLastNmeasurements()} endpoint.*/
	public static final String QUERYPARAM_NUM_MEASUREMENTS = "n";
	
	/**The definition of the name of a query parameter used in the {@link MeasurementServiceResource.getMeasurementsFrom()} and 
	 * {@link MeasurementServiceResource.getMeasurementsFromTo()} endpoints.*/
	public static final String QUERYPARAM_FROM = "from";
	
	/**The definition of the name of a query parameter used in the {@link MeasurementServiceResource.getMeasurementsFromTo()} endpoint.*/
	public static final String QUERYPARAM_TO = "to";
	
	/**The name of a query parameter used in {@link MeasurementServiceResource.getLastNmeasurements()} , 
	 * {@link MeasurementServiceResource.getMeasurementsFrom()} , and {@link MeasurementServiceResource.getMeasurementsFromTo(}) endpoints.*/
	public static final String QUERYPARAM_MSRMNTS_PARAM_NAME = "parameterName";
	
	/**The name of a query parameter used in {@link MeasurementServiceResource.getLastNmeasurements()} , 
	 * {@link MeasurementServiceResource.getMeasurementsFrom()} , and {@link MeasurementServiceResource.getMeasurementsFromTo(}) endpoints.*/
	public static final String QUERYPARAM_SENSOR_ID = "sensorId";
	
	/**The name of a query parameter used in {@link MeasurementServiceResource.getLastNmeasurements()} , 
	 * {@link MeasurementServiceResource.getMeasurementsFrom()} , and {@link MeasurementServiceResource.getMeasurementsFromTo(}) endpoints.*/
	public static final String QUERYPARAM_LOC_LAT = "locationLatitude";
	
	/**The name of a query parameter used in {@link MeasurementServiceResource.getLastNmeasurements()} , 
	 * {@link MeasurementServiceResource.getMeasurementsFrom()} , and {@link MeasurementServiceResource.getMeasurementsFromTo(}) endpoints.*/
	public static final String QUERYPARAM_LOC_LON = "locationLongitude";	
	
	/**The name of a query parameter used in {@link MeasurementServiceResource.getLatestMeasurements()}*/
	public static final String QUERYPARAM_CUSTOMER = "customerId";
	

}

