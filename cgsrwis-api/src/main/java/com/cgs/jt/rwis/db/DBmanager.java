/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import io.dropwizard.lifecycle.Managed;

/**
 * The {@code DBmanager} enum represents means to connect and disconnect to Cassandra cluster and
 * obtain a singleton Cassandra {@link com.datastax.driver.core.Session} object needed to execute CQL
 * queries. Additionally it provides methods returning prepared statements used in the system. 
 * 
 * Since the class implements {@link io.dropwizard.lifecycle.Managed} and overrides it's start()
 * and stop() methods it can be registered in the Dropwizard environment in order to establish Cassandra session
 * when Dropwizard service starts and release the session when service stops. 
 *
 * @author  Jernej Trnkoczy
 * 
 */
//why we need implements Managed -> see: https://dropwizard.readthedocs.io/en/stable/manual/core.html#managed-objects
public enum DBmanager implements Managed{


	/**Represents the only enumeration value of this enum - the singleton instance.*/
	INSTANCE;


	/**Represents the Cassandra session.*/
	private CqlSession session;

	//---------------------------PREPARED STATEMENTS FOR parameter_subscriptions_by_model TABLE------------------------------------------
	/**
	 * Represents the prepared statement for inserting the "parameter subscriptions" data.
	 */
	private PreparedStatement preparedStatementForParameterSubscriptionByModelInsertion; 

	/**
	 * Represents the prepared statement for retrieving the "parameter subscriptions" data (i.e. give me all the parameters
	 * and locations associated with a given forecasting model).  
	 */
	private PreparedStatement preparedStatementForParameterSubscriptionByModelRetrieval;



	//-------------------------PREPARED STATEMENTS FOR THE forecasted_values_by_param_loc_and_model TABLE--------------------------------
	/**
	 * Represents the prepared statement for inserting the forecasted values.
	 */
	private PreparedStatement preparedStatementForForecastedValueInsertion;

	/**
	 * Represents the prepared statement for retrieving only the "latest" forecasted values from certain date/time.
	 * Latest here means the values that were produced by the last run of the forecasting model.
	 */
	private PreparedStatement preparedStatementForOnlyTheLatestValsFromRetrieval;

	/**
	 * Represents the prepared statement for retrieving only the "latest" forecasted values that are on the given mathematically closed 
	 * time interval (specified by "from" and "to" times). Latest here means the values that were produced by the last run of the 
	 * forecasting model.
	 */
	private PreparedStatement preparedStatementForOnlyTheLatestValsFromToRetrieval;



	//------------------------------PREPARED STATEMENTS FOR forecasted_values_by_customer_param_and_time TABLE------------------------------------------------------
	/**
	 * Represents the prepared statement for inserting a single forecasted value - into table that stores only the latest forecasts.
	 */
	private PreparedStatement preparedStatementForLatestForecastedValueInsertion;

	/**
	 * Represents the prepared statement for retrieving the latest forecasted values for specified customer, parameter and at given time.
	 */
	private PreparedStatement preparedStatementForLatestForecastedValuesRetrieval;



	//------------------------------PREPARED STATEMENTS FOR measurements_by_param_sensornum_and_loc TABLE------------------------------------
	/**
	 * Represents the prepared statement for inserting the "measurements" data.
	 */
	private PreparedStatement preparedStatementForMeasurementInsertion;	

	/**
	 * Represents the prepared statement for retrieving the last N "measurements".
	 */
	private PreparedStatement preparedStatementForLastNMeasurementsRetrieval;	

	/**
	 * Represents the prepared statement for retrieving the "measurements" that are newer than given date/time.
	 */
	private PreparedStatement preparedStatementForMeasurementsFromRetrieval;	

	/**
	 * Represents the prepared statement for retrieving the "measurements" that are on the mathematically
	 * closed time interval between given {@code from} and {@code to} dates/times.
	 */
	private PreparedStatement preparedStatementForMeasurementsFromToRetrieval;



	//-----------------------PREPARED STATEMENTS FOR measurements_by_customer_and_param TABLE------------------------------------------
	/**
	 * Represents the prepared statement for inserting the "measurements" data.
	 */
	private PreparedStatement preparedStatementForLatestMeasurementInsertion;

	/**
	 * Represents the prepared statement for retrieving the latest measurements of given parameter on all sensors that
	 * belong to the specified customer.
	 */
	private PreparedStatement preparedStatementForLatestMeasurementsRetrieval;




	//----------------------PREPARED STATEMENTS FOR metro_loc_description_by_loc TABLE--------------------------------------------------
	/**
	 * Represents the prepared statement for inserting the "Metro location description" data.
	 */
	private PreparedStatement preparedStatementForMetroLocationDescriptionInsertion;

	/**
	 * Represents the prepared statement for retrieving the "Metro location description" for certain geographic location.
	 */
	private PreparedStatement preparedStatementForMetroLocationDescriptionRetrieval;



	//----------------------PREPARED STATEMENTS FOR reference_points_by_region TABLE----------------------------------------------------
	/**
	 * Represents the prepared statement for inserting the "reference point description".
	 */
	private PreparedStatement preparedStatementForReferencePointDescriptionInsertion;	

	/**
	 * Represents the prepared statement for retrieving the reference point descriptions (that belong to certain region).
	 */
	private PreparedStatement preparedStatementForReferencePointsDescriptionsRetrieval;



	/**
	 * The override of the {@link io.dropwizard.lifecycle.Managed#start()} method.
	 */
	@Override
	public void start() throws Exception {
		DBmanager.INSTANCE.connect();		
	}


	/**
	 * The override of the {@link io.dropwizard.lifecycle.Managed#stop()} method.
	 * NOTE: This method is called only if the Dropwizard service is shut down properly. The recommended
	 * procedure to shut down is to use {@code kill -SIGINT <pidNumber>}. Using system exit (Ctrl+C) is 
	 * NOT RECOMMENDED! There is no guarantee how the jetty server terminates in this case.
	 * @see https://stackoverflow.com/questions/25812816/how-to-shutdown-dropwizard-application
	 * @see https://stackoverflow.com/questions/31288721/dropwizard-shutdown-hook
	 * @see https://github.com/HubSpot/dropwizard-guice/issues/77		
	 */
	@Override
	public void stop() throws Exception {		
		DBmanager.INSTANCE.disconnect();		
	}


	/**
	 * Establishes Cassandra session in accordance with the provided configuration file(s) and assigns 
	 * the session to the {@link #session} instance variable. Multiple calls to this method will have 
	 * no effects (once a connection is established).
	 * 
	 * NOTE: There can be multiple configuration files in configuration inheritance tree.
	 * @see https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/configuration/ 
	 * 
	 */
	public void connect() {
		if (session == null) {
			//TODO: there is a bunch of possible configurations that need to be set in production for long-living db applications:
			// withReconnectionPolicy
			// seed nodes,
			// load balancing, 
			// connection timeout, 
			// etc, etc.
			// from v.4.x of driver all these can be set as a configuration file 
			//see https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/configuration/

			//also the contact points, datacenter, username, password etc. are all obtained from config file		
			//regarding the datacenter --> see https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/load_balancing/
			//the thing is that in the default configuration file (=reference.conf included in the .jar file of the java-driver-core) it is 
			//stated that load balancing has DefaultLoadBalancingPolicy --> in this case if you provided the contactNodes then you must provide 
			//also a local datacenter name - see https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/load_balancing/

			//TODO: maybe implement some retry logic - if database is not ready yet - however since we will have
			//a bare-metal installation of Cassandra this is not really needed (if we would run in Docker then it
			//would be another matter...)
			session = CqlSession.builder().build();

			//if you want you can log some info to inform the user....
			/*
			Metadata metadata = session.getMetadata();
			System.out.println("Connected to Cassandra cluster with nodes:");
			Map<UUID, Node> nodes=metadata.getNodes();
			Iterator<Entry<UUID, Node>> it = nodes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<UUID, Node> pair = (Map.Entry<UUID, Node>)it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue());				
			}
			 */			
		}		
	}






	/**
	 * Invalidates and closes the session and connection to the Cassandra database
	 */
	public void disconnect() {		
		if (session != null) {			
			session.close();
		}		
	}


	/**
	 * Returns the Cassandra session - the value of the {@link #session} instance variable. It the 
	 * value of the {@link #session} instance variable is null the method tries to establish the
	 * Cassandra session.
	 *  
	 * @return Cassandra session or null if the session has not been created yet.
	 * 
	 */
	public CqlSession getSession() {
		if(session == null) {
			DBmanager.INSTANCE.connect();
		}		
		return session;
	}





	//---------------------------PREPARED STATEMENTS FOR parameter_subscriptions_by_model TABLE------------------------------------------
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting the forecast parameter subscription.
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforParameterSubscriptionByModelInsertion() {
		if(preparedStatementForParameterSubscriptionByModelInsertion == null) {
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_PARAMETER_SUBSCRIPTIONS_BY_MODEL)
					.append("(locLat, locLon, elevation, paramName, modelId) ")
					.append("VALUES (:lat, :lon, :ele, :pname, :modid);");				
			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForParameterSubscriptionByModelInsertion = ps;
		}
		return preparedStatementForParameterSubscriptionByModelInsertion;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the parameter
	 * subscription by model (i.e. give me all locations and all parameters that are associated with a certain
	 * forecast model). 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforParameterSubscriptionByModelRetrieval() {
		//TODO: Prepared statements for SELECT are not advised it the database schema changes but the app is not restarted...
		if(preparedStatementForParameterSubscriptionByModelRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(DBconstants.TABLE_PARAMETER_SUBSCRIPTIONS_BY_MODEL)
					.append(" WHERE modelId = :modid ;");				
			final String query = sb.toString();

			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = DBmanager.INSTANCE.getSession().prepare(query);
			preparedStatementForParameterSubscriptionByModelRetrieval = ps;
		}
		return preparedStatementForParameterSubscriptionByModelRetrieval;
	}





	//-------------------------PREPARED STATEMENTS FOR THE forecasted_values_by_param_loc_and_model TABLE--------------------------------	
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting the forecasted values. 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforForecastedValueInsertion() {
		if(preparedStatementForForecastedValueInsertion == null) {			
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_FORECASTED_VALUES_BY_PARAMETER_LOCATION_AND_MODEL)
					.append("(paramName, locLat, locLon, modelId, forecastedValTime, forecastRefTime, value) ")
					.append("VALUES (:pn, :lat, :lon, :modid, :fvt, :frt, :val);");	

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForForecastedValueInsertion  = ps;
		}
		return preparedStatementForForecastedValueInsertion ;
	}	

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the forecasted values that have absolute time
	 * newer (or at the same time) than the given date/time and belong to the latest run of the forecasting model.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforOnlyTheLatestValsFromRetrieval() {
		if(preparedStatementForOnlyTheLatestValsFromRetrieval == null) {			
			StringBuilder sb = new StringBuilder("SELECT forecastedValTime, value FROM ")
					.append(DBconstants.TABLE_FORECASTED_VALUES_BY_PARAMETER_LOCATION_AND_MODEL)
					.append(" WHERE paramName = :pn AND locLat = :lat AND locLon = :lon AND modelId = :modid AND forecastedValTime >= :fvt")
					.append(" GROUP BY forecastedValTime;");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForOnlyTheLatestValsFromRetrieval = ps;
		}
		return preparedStatementForOnlyTheLatestValsFromRetrieval;
	}	

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the forecasted values that have absolute time
	 * on a mathematically closed interval between given "from" and "to" times.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforOnlyTheLatestValsFromToRetrieval() {
		if(preparedStatementForOnlyTheLatestValsFromToRetrieval == null) {			
			StringBuilder sb = new StringBuilder("SELECT forecastedValTime, value FROM ")
					.append(DBconstants.TABLE_FORECASTED_VALUES_BY_PARAMETER_LOCATION_AND_MODEL)
					.append(" WHERE paramName = :pn AND locLat = :lat AND locLon = :lon AND modelId = :modid AND forecastedValTime >= :fvtfrom AND forecastedValTime <= :fvtto")
					.append(" GROUP BY forecastedValTime;");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForOnlyTheLatestValsFromToRetrieval = ps;
		}
		return preparedStatementForOnlyTheLatestValsFromToRetrieval;
	}





	//------------------------------PREPARED STATEMENTS FOR forecasted_values_by_customer_param_and_time TABLE------------------------------------------------------
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting forecasted values
	 * into table that stores only the latest forecasts (i.e. the latest run of the forecasting model). 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object that can be used to execute CQL queries.
	 * 
	 */	
	public PreparedStatement getPSforLatestForecastedValueInsertion() {
		//Prepared Statements --> https://docs.datastax.com/en/developer/java-driver/3.1/manual/statements/prepared/ 
		//And example is here: https://github.com/DataStaxDocs/playlist/blob/master/src/main/java/playlist/model/TracksDAO.java 
		//The PreparedStatement OBJECT SHOULD BE CREATED ONLY ONCE per app!
		if(preparedStatementForLatestForecastedValueInsertion == null) {
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_FORECASTED_VALUES_BY_CUSTOMER_PARAMETER_AND_TIME)
					.append("(customerId, paramName, modelId, locLat, locLon, offsetInMillis, forecastedValTime, forecastRefTime, value) ")
					.append("VALUES (:cid, :pn, :modid, :lat, :lon, :off, :fvt, :frt, :val);");				
			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare(). As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForLatestForecastedValueInsertion = ps;
		}
		return preparedStatementForLatestForecastedValueInsertion;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting forecasted values
	 * into table that stores only the latest forecasts (i.e. the latest run of the forecasting model). 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object that can be used to execute CQL queries.
	 * 
	 */	
	public PreparedStatement getPSforLatestForecastedValuesRetrieval() {
		//Prepared Statements --> https://docs.datastax.com/en/developer/java-driver/3.1/manual/statements/prepared/ 
		//And example is here: https://github.com/DataStaxDocs/playlist/blob/master/src/main/java/playlist/model/TracksDAO.java 
		//The PreparedStatement OBJECT SHOULD BE CREATED ONLY ONCE per app!
		if(preparedStatementForLatestForecastedValuesRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT modelId, locLat, locLon, forecastRefTime, value FROM ")
					.append(DBconstants.TABLE_FORECASTED_VALUES_BY_CUSTOMER_PARAMETER_AND_TIME)
					.append(" WHERE customerId = :cid AND paramName = :pn AND forecastedValTime = :fvt")
					//NOTE: because forecastedValTime is not part of PRIMARY KEY - when we perform query like 
					//SELECT * FROM forecasted_values_by_customer_param_and_time WHERE customerId = 'ObcinaKranj' AND paramName = 'Wind in [km h^-1] @ 10m above ground' AND forecastedValTime = '2019-12-17 17:00:00';
					//Cassandra complains with: InvalidRequest: Error from server: code=2200 [Invalid query] message="Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. If you want to execute this query despite the performance unpredictability, use ALLOW FILTERING"
					//I believe that in this case ALLOW FILTERING is acceptable -> see:
					//https://www.datastax.com/blog/allow-filtering-explained
					//and https://www.datastax.com/blog/allow-filtering-explained (Scylla works similar than Cassandra)
					//Because Cassandra will have to do filtering on all results WHERE customerId = 'ObcinaKranj' AND paramName = 'Wind in [km h^-1] @ 10m above ground'
					//it will have to perform filtering on dataset consisting of (all locations of customer * all forecasting models * all possible forecast time offsets) records.
					//However on the other hand:
					//1) filtering will not be performed across different partitions
					//2) our query is somewhere in between low selectivity query and high selectivity query. Low selectivity queries for example from 10000 unfiltered results 
					//filter out only 100 - and 9900 are in the final result. High selectivity queries fore example from 10000 unfiltered results filter out 9999 - and only 1 is
					//in the final result. ALLOW FILTERING is meaningful with low selectivity queries, in such cases it might even be more efficient than creating secondary index 
					//since a sequential scan of all of the values is faster than a huge set of random index-based seeks. In case of our query only 1/n (where n is the number of possible forecast offsets)
					//will be in the final result (e.g. 1/12 of unfiltered results will be in the final result if we have 12 offsets (+1h, +2h, +3h, ....,+12h) in the forecast).
					//THEREFORE I WILL ASSUME THAT ALLOW FILTERING IS ACCEPTABLE IN THIS CASE - AND PROBABLY INTRODUCING SECONDARY INDEX WOULD NOT INCREASE PERFORMANCE DRASTICALLY
					//TODO: if it turns out this is not true - then implement secondary index on forecastedValTime column!
					.append(" ALLOW FILTERING;");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare(). As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForLatestForecastedValuesRetrieval = ps;
		}
		return preparedStatementForLatestForecastedValuesRetrieval;
	}






	//------------------------------PREPARED STATEMENTS FOR measurements_by_param_sensornum_and_loc TABLE------------------------------------
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting the measurements data
	 * into table that stores the entire history of measurements. 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforMeasurementInsertion() {
		if(preparedStatementForMeasurementInsertion == null) {
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_MEASUREMENTS_BY_PARAMETER_SENSORID_AND_LOCATION)
					.append("(paramName, sensorId, locLat, locLon, measurementTime, measuredVal) ")
					.append("VALUES (:pn, :sid, :lat, :lon, :mt, :mv);");				
			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForMeasurementInsertion = ps;
		}
		return preparedStatementForMeasurementInsertion;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the measurements data.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforLastNMeasurementsRetrieval() {
		if(preparedStatementForLastNMeasurementsRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(DBconstants.TABLE_MEASUREMENTS_BY_PARAMETER_SENSORID_AND_LOCATION)
					.append(" WHERE paramName = :pn AND sensorId = :sid AND locLat = :lat AND locLon = :lon")
					//setting LIMIT will return only "the last N" rows. The rows are sorted by the clustering key - as
					//defined when table was created! If there are fewer rows than N in the table Cassandra will return 
					//rows that exist.
					.append(" LIMIT :lim")
					.append(";");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForLastNMeasurementsRetrieval = ps;
		}
		return preparedStatementForLastNMeasurementsRetrieval;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the measurements newer than
	 * given date/time data.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforMeasurementsFromRetrieval() {
		if(preparedStatementForMeasurementsFromRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(DBconstants.TABLE_MEASUREMENTS_BY_PARAMETER_SENSORID_AND_LOCATION)
					.append(" WHERE paramName = :pn AND sensorId = :sid AND locLat = :lat AND locLon = :lon AND measurementTime >= :fr")
					.append(";");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForMeasurementsFromRetrieval = ps;
		}
		return preparedStatementForMeasurementsFromRetrieval;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the measurements on the mathematically
	 * closed interval between provided {@code from} and {@code to} dates/times.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforMeasurementsFromToRetrieval() {
		if(preparedStatementForMeasurementsFromToRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(DBconstants.TABLE_MEASUREMENTS_BY_PARAMETER_SENSORID_AND_LOCATION)
					.append(" WHERE paramName = :pn AND sensorId = :sid AND locLat = :lat AND locLon = :lon AND measurementTime >= :ft AND measurementTime <= :tt")
					.append(";");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForMeasurementsFromToRetrieval = ps;
		}
		return preparedStatementForMeasurementsFromToRetrieval;
	}






	//-----------------------PREPARED STATEMENTS FOR measurements_by_customer_and_param TABLE------------------------------------------
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting the measurements data
	 * into table that stores only the latest measurements. 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforLatestMeasurementInsertion() {		
		if(preparedStatementForLatestMeasurementInsertion == null) {
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_MEASUREMENTS_BY_CUSTOMER_AND_PARAMETER)
					.append("(customerId, paramName, sensorId, locLat, locLon, measurementTime, measuredVal) ")
					.append("VALUES (:cid, :pn, :sid, :lat, :lon, :mt, :mv);");				
			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForLatestMeasurementInsertion = ps;
		}
		return preparedStatementForLatestMeasurementInsertion;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the latest measurements 
	 * for a specified parameter on all sensors that belong to the specified customer.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforLatestMeasurementsRetrieval() {
		if(preparedStatementForLatestMeasurementsRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT sensorId, locLat, locLon, measurementTime, measuredVal FROM ")
					.append(DBconstants.TABLE_MEASUREMENTS_BY_CUSTOMER_AND_PARAMETER)
					.append(" WHERE customerId = :cid AND paramName = :pn")
					.append(";");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForLatestMeasurementsRetrieval = ps;
		}
		return preparedStatementForLatestMeasurementsRetrieval;
	}


	




	//----------------------PREPARED STATEMENTS FOR metro_loc_description_by_loc_mod TABLE--------------------------------------------------
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting the Metro location description data. 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforMetroLocationDescriptionInsertion() {
		if(preparedStatementForMetroLocationDescriptionInsertion == null) {
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_METRO_LOCATION_DESCRIPTION_BY_LOCATION_AND_MODEL)
					.append("(locLat, locLon, modelId, type, subSurfaceSensorDepth, roadLayers, visibleHorizonDirections, measurementsMappings, weatherForecastMappings, solarFluxFactor, infraredFluxFactor, anthropogenicFluxFactor, enableSunshadow, sunshadowMethod, useAnthropogenicFlux, useInfraredFlux, useSolarFlux, verbosityLevel) ")
					.append("VALUES (:lat, :lon, :modid, :ty, :sssd, :rls, :vhd, :mm, :wfm, :sff, :iff, :aff, :es, :sm, :uaf, :uif, :usf, :vl);");				
			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForMetroLocationDescriptionInsertion = ps;
		}
		return preparedStatementForMetroLocationDescriptionInsertion;
	}	
	
	
	
	

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieving the Metro location description.	
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforMetroLocationDescriptionRetrieval() {
		if(preparedStatementForMetroLocationDescriptionRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(DBconstants.TABLE_METRO_LOCATION_DESCRIPTION_BY_LOCATION_AND_MODEL)
					.append(" WHERE locLat = :lat AND locLon = :lon AND modelId = :modid")					
					.append(";");

			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForMetroLocationDescriptionRetrieval = ps;
		}
		return preparedStatementForMetroLocationDescriptionRetrieval;
	}


	





	//----------------------PREPARED STATEMENTS FOR reference_points_by_region TABLE----------------------------------------------------
	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for inserting the reference point description data. 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforReferencePointDescriptionInsertion() {
		if(preparedStatementForReferencePointDescriptionInsertion == null) {
			StringBuilder sb = new StringBuilder("INSERT INTO ")
					.append(DBconstants.TABLE_REFERENCE_POINTS_BY_REGION)
					.append("(region, locLat, locLon, referencedPoints, weatherConditionDescription) ")
					.append("VALUES (:reg, :lat, :lon, :rfps, :wcd);");				
			final String query = sb.toString();
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForReferencePointDescriptionInsertion = ps;
		}
		return preparedStatementForReferencePointDescriptionInsertion;
	}

	/**
	 * Used to obtain the singleton instance of Cassandra prepared statement for retrieval of reference point descriptions (that belong to certain region). 
	 * 
	 * @return A singleton {@link com.datastax.driver.core.PreparedStatement} object.
	 * 
	 */
	public PreparedStatement getPSforReferencePointsDescriptionsRetrieval() {
		if(preparedStatementForReferencePointsDescriptionsRetrieval == null) {
			StringBuilder sb = new StringBuilder("SELECT * FROM ")
					.append(DBconstants.TABLE_REFERENCE_POINTS_BY_REGION)
					.append(" WHERE region = :reg")					
					.append(";");

			final String query = sb.toString();			
			//TODO: It would probably be better to use SimpleStatement as input to session.prepare().
			//As explained here https://docs.datastax.com/en/developer/java-driver/4.0/manual/core/statements/prepared/
			//Session.prepare() accepts either a plain query string, or a SimpleStatement object. If you use a SimpleStatement, its execution parameters will propagate to bound statements. So we could use:
			//SimpleStatement simpleStatement = SimpleStatement.builder(query).setConsistencyLevel(DefaultConsistencyLevel.QUORUM).build();
			//PreparedStatement preparedStatement = session.prepare(simpleStatement);
			PreparedStatement ps = getSession().prepare(query);
			preparedStatementForReferencePointsDescriptionsRetrieval = ps;
		}
		return preparedStatementForReferencePointsDescriptionsRetrieval;
	}

}
