/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.cgs.jt.rwis.api.ForecastedValue;
import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterForecast;
import com.cgs.jt.rwis.api.ParameterForecastWithMetadata;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;


/**
 * The implementation of the {@link ParameterForecastsDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class ParameterForecastsDAOimpl implements ParameterForecastsDAO {


	//NOTE: whoever calls this function should check that the input is valid (non-null, non-empty etc...).	
	//NOTE: our Cassandra implementation writes in two tables! One that stores the entire history of forecasted values
	//and the other that stores only the latest forecasts (=forecasted values at all time offsets of the forecast)
	//for all locations and forecasting models that the customer is subscribed to.
	@Override
	public void insert(ParameterForecastWithMetadata pf){
		PreparedStatement ps1 = DBmanager.INSTANCE.getPSforForecastedValueInsertion();	
		PreparedStatement ps2 = DBmanager.INSTANCE.getPSforLatestForecastedValueInsertion();

		TreeMap<Instant, Double> forecastedVals = pf.getForecast();
		for(Map.Entry<Instant, Double> entry : forecastedVals.entrySet()) {
			Instant absTime = entry.getKey();
			Double value = entry.getValue();

			//#############----insert into table that stores the entire history of forecasted values--------########
			BoundStatement bs1 = ps1.bind()
					.setString("pn", pf.getParameter().getLabel())
					.setDouble("lat", pf.getLocation().getLatitude())
					.setDouble("lon", pf.getLocation().getLongitude())
					.setString("modid", pf.getForecastModelId())
					//Datastax DRIVER v.4.x NOW SUPPORTS java.time.Instant BY DEFAULT AND OUT OF THE BOX!
					.setInstant("fvt", absTime)
					.setInstant("frt", pf.getReferenceTime())
					.setDouble("val", value);
			DBmanager.INSTANCE.getSession().execute(bs1);


			//##########------insert into table that stores only the latest forecasts---------############			
			//insert only if at least one customer wants to see this forecast on the geographic map of GUI
			if(!pf.getCustomerIDs().isEmpty()) {
				//for every customer new row (even if the forecasted value is the same)
				for (String customerId : pf.getCustomerIDs()) {
					//NOTE: insert is the same as upsert in Cassandra - it will replace the old value
					//NOTE: as long as we do not insert/upsert null values the tombstone is not created (of course only in case
					//we are dealing with columns that are not collections or UDTs) 
					BoundStatement bs2 = ps2.bind()
							.setString("cid", customerId)
							.setString("pn", pf.getParameter().getLabel())
							.setDouble("lat", pf.getLocation().getLatitude())
							.setDouble("lon", pf.getLocation().getLongitude())
							.setString("modid", pf.getForecastModelId())	
							.setLong("off", ChronoUnit.MILLIS.between(pf.getReferenceTime(), absTime))									
							//Datastax DRIVER v.4.x NOW SUPPORTS java.time.Instant BY DEFAULT AND OUT OF THE BOX!
							.setInstant("fvt", absTime)
							.setInstant("frt", pf.getReferenceTime())
							.setDouble("val", value);
					DBmanager.INSTANCE.getSession().execute(bs2);
				}
			}
		}			
	}




	@Override
	public TreeMap<Instant, Double> retrieveLatestForecastedValsFrom(Instant from, ForecastedParameter param, GeographicLocation location, String forecastModelId) {
		//TODO: If input parameters from, param, location and forecastModelId are null then we will get
		//NullPointerExceptions. However we are assuming that they are validated when constructed from the incoming query.		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforOnlyTheLatestValsFromRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", param.getLabel())
				.setDouble("lat", location.getLatitude())
				.setDouble("lon", location.getLongitude())				
				.setString("modid", forecastModelId)
				.setInstant("fvt", from);//retrieve the forecasted values from the given "from" time...
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		//if the result set is empty return null 
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {			
			TreeMap<Instant, Double> forc = new TreeMap<Instant, Double>();
			for (Row r : rs) {			
				//TODO: if there are no guarantees that all fields retrieved from the database are non-null, non-empty (i.e. values not bound)
				//then NullPointerExceptions will happen. However since it is only the services that insert data into database, 
				//and services do validate incoming data before they insert it into the database - we can assume that they will never be null.
				Instant forecastedValAbsTime = r.getInstant("forecastedValTime");				
				//TODO: we do not check the forecasted values (if they are in the expected range defined for the parameter)
				//because they should have been checked when they were inserted into the database! 
				Double value = r.getDouble("value");
				forc.put(forecastedValAbsTime, value);				
			}

			return forc;
		}		
	}





	@Override
	public TreeMap<Instant, Double> retrieveLatestForecastedValsFromTo(Instant from, Instant to, ForecastedParameter param, GeographicLocation location, String forecastModelId) {
		//TODO: If input parameters from, to, param, location and forecastModelId are null then we will get
		//NullPointerExceptions. However we are assuming that they are validated when constructed from the incoming query.		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforOnlyTheLatestValsFromToRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", param.getLabel())
				.setDouble("lat", location.getLatitude())
				.setDouble("lon", location.getLongitude())				
				.setString("modid", forecastModelId)
				.setInstant("fvtfrom", from)//retrieve the forecasted values that have the absolute time bigger or equal to the  given "from" time...
				.setInstant("fvtto", to);//AND have the abslute time smaller or equal to the given "to" time.
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		//if the result set is empty return null 
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {			
			TreeMap<Instant, Double> forc = new TreeMap<Instant, Double>();
			for (Row r : rs) {			
				//TODO: if there are no guarantees that all fields retrieved from the database are non-null, non-empty (i.e. values not bound)
				//then NullPointerExceptions will happen. However since it is only the services that insert data into database, 
				//and services do validate incoming data before they insert it into the database - we can assume that they will never be null.
				Instant forecastedValAbsTime = r.getInstant("forecastedValTime");				
				//TODO: we do not check the forecasted values (if they are in the expected range defined for the parameter)
				//because they should have been checked when they were inserted into the database! 
				Double value = r.getDouble("value");
				forc.put(forecastedValAbsTime, value);				
			}

			return forc;
		}		
	}




	@Override
	public HashMap<GeographicLocation, HashMap<String, ForecastedValue>> retrieveLatestForecastedValuesOfCustomer(ForecastedParameter param, String customerId, Instant atTime){
		//retrieve from db - if no results return null
		PreparedStatement ps = DBmanager.INSTANCE.getPSforLatestForecastedValuesRetrieval();
		BoundStatement bs = ps.bind()
				.setString("cid", customerId)
				.setString("pn", param.getLabel())
				.setInstant("fvt", atTime);	

		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {
			//TODO: in case if the returned result set is very large (e.g. customer is authorized to thousands and thousands of location/forecast model pairs)
			//then it might prove beneficial to implement some paging magic
			HashMap<GeographicLocation, HashMap<String, ForecastedValue>> latestForecastedValsAtTime = new HashMap<GeographicLocation, HashMap<String, ForecastedValue>>();
			for (Row r : rs) {			
				//TODO: if there are no guarantees that all fields (modid, lat, lon, ...) retrieved from the database are non-null, 
				//non-empty (i.e. values not bound) then NullPointerExceptions will happen. The weather forecast data
				//is inserted into the database through forecast service (and this service does validate incoming data). The road
				//forecast data is inserted into the database directly (not through service). If the modules calculating road
				//forecasts insert "valid = non-null data" - we can assume that the retrieved data from database will never be null.			
				String modid = r.getString("modelId");
				Double lat = r.getDouble("locLat");
				Double lon = r.getDouble("locLon");				
				Instant frt = r.getInstant("forecastRefTime");
				Double v = r.getDouble("value");

				GeographicLocation gloc = new GeographicLocation(lat, lon);
				//find if key (i.e. location) already exists and if it does append to the value (hashmap of modelId <-> ForecastedValue ),
				//otherwise create new key with value. 
				//using Java8 construction of multi-value maps - see https://www.baeldung.com/java-map-duplicate-keys
				latestForecastedValsAtTime.computeIfAbsent(gloc, k -> new HashMap<String, ForecastedValue>()).put(modid , new ForecastedValue(atTime, frt, v));
			}			
			return latestForecastedValsAtTime;						
		}
	}


	//TODO:
	//#################################################################################################
	//#########TOLE JE KLELE ZACASNO - SAMO TOLIKO CASA DOKLER NE RAZMISLIMO KAKO BO TO NA KONCU - CUSTOMERS PA TO... GLEJ ZGORAJ insert(ParameterForecastWithMetadata pf)#######
	//#################################################################################################
	//NOTE: If input parameter ParameterForecasts is null, or has null instance variables, or has instance variables that
	//are empty collections -this could lead to:
	//1) NullPointerException thrown in this function
	//2) null inserted into the database - (in the case of Cassandra creating tombstones) 
	//The ParameterForecasts object does not have optional parameters (the object is meaningful only if fully 
	//initialized), therefore whoever calls this function should check that all it's fields are non-null. 
	//This is for example performed by the forecast service endpoint (Hibernate validation of ParameterForecasts 
	//objects when they are constructed from the incoming JSON).
	@Override
	public void insert(ParameterForecast pf){
		PreparedStatement ps1 = DBmanager.INSTANCE.getPSforForecastedValueInsertion();			

		TreeMap<Instant, Double> forecastedVals = pf.getForecast();
		for(Map.Entry<Instant, Double> entry : forecastedVals.entrySet()) {
			Instant absTime = entry.getKey();
			Double value = entry.getValue();

			//#############----insert into table that stores the entire history of forecasted values--------########
			BoundStatement bs1 = ps1.bind()
					.setString("pn", pf.getParameter().getLabel())
					.setDouble("lat", pf.getLocation().getLatitude())
					.setDouble("lon", pf.getLocation().getLongitude())
					.setString("modid", pf.getForecastModelId())
					//Datastax DRIVER v.4.x NOW SUPPORTS java.time.Instant BY DEFAULT AND OUT OF THE BOX!
					.setInstant("fvt", absTime)
					.setInstant("frt", pf.getReferenceTime())
					.setDouble("val", value);
			DBmanager.INSTANCE.getSession().execute(bs1);			
		}		
	}




	//NOTE: If input parameter ParameterForecasts is null, or has null instance variables, or has instance variables that
	//are empty collections -this could lead to:
	//1) NullPointerException thrown in this function
	//2) null inserted into the database - (in the case of Cassandra creating tombstones) 
	//The ParameterForecasts object does not have optional parameters (the object is meaningful only if fully 
	//initialized), therefore whoever calls this function should check that all it's fields are non-null. 
	//This is for example performed by the forecast service endpoint (Hibernate validation of ParameterForecasts 
	//objects when they are constructed from the incoming JSON).	
	/*
	@Override
	public void insert(ParameterForecasts pf){
		PreparedStatement ps = DBmanager.INSTANCE.getPSforForecastInsertion();

		TreeMap<Instant, TreeMap<Instant, Double>> forecasts = pf.getForecasts();
		for(Map.Entry<Instant, TreeMap<Instant, Double>> entry : forecasts.entrySet()) {
			Instant  k = entry.getKey();
			TreeMap<Instant, Double> v = entry.getValue();

			BoundStatement bs = ps.bind()
					.setString("pn", pf.getParameter().getLabel())
					.setDouble("lat", pf.getLocation().getLatitude())
					.setDouble("lon", pf.getLocation().getLongitude())
					.setString("modid", pf.getForecastModelId())
					//Datastax DRIVER v.4.x NOW SUPPORTS java.time.Instant BY DEFAULT AND OUT OF THE BOX!
					.setInstant("fr", k)
					.setMap("fv", v, Instant.class, Double.class);
			DBmanager.INSTANCE.getSession().execute(bs);
		}
	}
	 */


	/*
	@Override	
	public ParameterForecasts retrieveLastNforecasts(int n, ForecastedParameter parameter, GeographicLocation loc, String forecastModelId){

		//TODO: If input parameters n, parameter, geographicLocation and forecastModel are null then we will get
		//NullPointerExceptions. However we are assuming that they are validated when constructed from the incoming query.		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforLastNForecastsRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", parameter.getLabel())
				.setDouble("lat", loc.getLatitude())
				.setDouble("lon", loc.getLongitude())				
				.setString("modid", forecastModelId)
				.setInt("lim", n);//retrieve n forecasts
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		//if the result set is empty return null 
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {			
			TreeMap<Instant, TreeMap<Instant, Double>> forecasts = new TreeMap<Instant, TreeMap<Instant, Double>>();
			for (Row r : rs) {			
				//TODO: if there are no guarantees that all fields retrieved from the database are non-null, non-empty (i.e. values not bound)
				//then NullPointerExceptions will happen. However since it is only the services that insert data into database, 
				//and services do validate incoming data before they insert it into the database - we can assume that they will never be null.
				Instant frt = r.getInstant("forecastRefTime");
				//It is not possible to get a TreeMap from Cassandra - get the map and transform it into TreeMap
				//TODO: we do not check the forecasted values (if they are in the expected range defined for the parameter)
				//because they should have been checked when they were inserted into the database! 
				Map<Instant, Double> fv = r.getMap("forecastedVal", Instant.class, Double.class);

				//convert the returned LinkedHashMap into a TreeMap usign Java8
				//see https://www.techiedelight.com/convert-hashmap-treemap-java/
				//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
				//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
				forecasts.put(frt, fv.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,	(oldValue, newValue) -> newValue,TreeMap::new)));				
			}
			ParameterForecasts pf = new ParameterForecasts(
					parameter,
					loc, 
					forecastModelId,
					forecasts);

			return pf;
		}
	}
	 */


	/*
	@Override	
	public ParameterForecastedLatestVals retrieveLatestValsOfLastNforecasts(int n, ForecastedParameter parameter, GeographicLocation loc, String forecastModelId){

		ParameterForecastedLatestVals pflv = null;

		//TODO: If input parameters n, parameter, geographicLocation and forecastModel are null then we will get
		//NullPointerExceptions. However we are assuming that they are validated when constructed from the incoming query.		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforLastNForecastsRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", parameter.getLabel())
				.setDouble("lat", loc.getLatitude())
				.setDouble("lon", loc.getLongitude())				
				.setString("modid", forecastModelId)
				.setInt("lim", n);//retrieve n forecasts
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		//check if result set is empty - if the result set is empty the returned pflv will stay null - which will cause HTTP status 204 No Content to be returned...
		if(rs.getAvailableWithoutFetching() >= 1) {

			TreeMap<Instant, Double> latestVals = new TreeMap<Instant, Double>();

			//TODO: we need to take care that the "overlapping" values are replaced with the value taken from the last forecast 
			//(i.e. the one with the biggest forecast reference time). The database is implemented in such a way that the last
			//forecast will be retrieved first - but we need to add to the latestVals first from the "oldest" forecast - and then 
			//the values will get replaced by the values from the "newer" forecasts. However in order to be able to do that we 
			//need to fetch all results first - BUT: for queries that return a large number of rows calling rs.all() is not 
			//recommended - see rs.all() method documentation. By calling rs.all() all rows are fetched and stored in memory!
			//see also https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/paging/
			//FOR OUR PURPOSES HOWEVER IT WILL BE OK - SINCE WE EXPECT THAT THE n PARAMETER	IS SUCH THAT THE QUERY
			//ALWAYS RETURNS ONLY A FEW RESULTS.
			List<Row> allResults = rs.all();
			ListIterator<Row> listIter = allResults.listIterator(allResults.size());
			while (listIter.hasPrevious()) {
				//iterate backwards!
				Row r = listIter.previous();
				//TODO: if there are no guarantees that all fields retrieved from the database are non-null, non-empty (i.e. values not bound)
				//then NullPointerExceptions will happen. However since it is only the services that insert data into database, 
				//and services do validate incoming data before they insert it into the database - we can assume that they will never be null.

				//It is not possible to get a TreeMap from Cassandra - get the map and transform it into TreeMap
				//TODO: we do not check the forecasted values (if they are in the expected range defined for the parameter)
				//because they should have been checked when they were inserted into the database! 
				Map<Instant, Double> fv = r.getMap("forecastedVal", Instant.class, Double.class);

				//convert the returned LinkedHashMap into a TreeMap usign Java8
				//see https://www.techiedelight.com/convert-hashmap-treemap-java/
				//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
				//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
				TreeMap<Instant, Double> cur = fv.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,	(oldValue, newValue) -> newValue,TreeMap::new));
				latestVals.putAll(cur);
			}

			//TODO: I think that this checking is not needed - since if there is at least one Row in results - this Row should contain Map<Instant,Double>
			//that is not empty - because we checked this when inserting into the DB. Anyway it will not hurt much to check...
			if(!latestVals.isEmpty()) {	
				pflv = new ParameterForecastedLatestVals(
						parameter,
						loc, 
						forecastModelId,
						latestVals);
			}			
		}
		return pflv;
	}
	 */

	/*
	@Override
	public int retrieveNumberOfForecastsFrom(Instant from, ForecastedParameter param, GeographicLocation location, String forecastModelId) {

		//PreparedStatement ps = DBmanager.INSTANCE.getPSforForecastsFromRetrieval();
		//BoundStatement bs = ps.bind()
		//		.setString("pn", param.getLabel())
		//		.setDouble("lat", location.getLatitude())
		//		.setDouble("lon", location.getLongitude())
		//		.setString("modid", forecastModelId)
		//		.setInstant("fr", from);//retrieve the measurements from specified date/time given as java.time.Instant object	

		//ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		//List<Row> allResults = rs.all();
		//return allResults.size();

		//For queries that return a large number of rows calling rs.all() is not recommended - see method documentation.
		//This is because all the rows have to be fetched and stored in memory!
		//This consumes memory and is not very performant (fetching all the rows across network). If performance is an issue
		//then the rs.getAvailableWithoutFetching() should be used (it is of O(1) performance) - however this method returns only 
		//the size of the current page - which by default is 5000 rows - see https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/paging/
		//so if the query would return more than 5000 rows - then the function would return 5000 - which is not the total number of rows in result!
		//FOR OUR PURPOSES HOWEVER IT WOULD BE GOOD ENOUGH SINCE WE EXPECT THAT THE from PARAMETER	IS SUCH THAT THE QUERY
		//ALWAYS RETURNS ONLY A FEW RESULTS. FOR EXAMPLE - WE ARE VALIDATING THE from PARAMETER ON THE SERVICE ENDPOINT... BUT WHAT TO DO WITH MODULES THAT USE DAO DIRECTLY?
		//BOTTOM LINE - IF IT TURNS OUT THAT THE PERFORMANCE NEEDS TO BE BOOSTED - YOU CAN TRY WITH THE rs.getAvailableWithoutFetching() AND
		//RISK GETTING WRONG RESULT (although only in the case of more than 5000 returned rows)	

		//so the above design is a very bad one. First of all when the query gets executed on the side of
		//Cassandra cluster all the results will be loaded into memory, then all the results will be brought
		//over network to client (=datastax driver), load them into memory as List (so if DAO is used by
		//web service this means memory of web service), and get the size of a List.

		//better option is not to bring over network and load in memory of client, instead load results 
		//only into memory of the Cassandra cluster - and perform count there. See:
		//https://stackoverflow.com/questions/26620151/how-to-obtain-number-of-rows-in-cassandra-table and
		//http://www.redshots.com/cassandra-counting-without-using-counters/
		//check also https://www.datastax.com/blog/2013/04/counting-keys-cassandra - good explanation, it seems like you can set the limit to the COUNT!
		//However it seems that there is no default limit for count - see https://docs.datastax.com/en/cql-oss/3.3/cql/cql_reference/cqlSelect.html#:~:text=Cassandra%20provides%20standard%20built%2Din,aggregate%20values%20to%20SELECT%20statements.&text=A%20SELECT%20expression%20using%20COUNT,NULL%20values%20in%20a%20column.&text=A%20SELECT%20expression%20using%20COUNT,to%20get%20the%20same%20result.
		PreparedStatement ps = DBmanager.INSTANCE.getPSforNumberOfForecastsFromRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", param.getLabel())
				.setDouble("lat", location.getLatitude())
				.setDouble("lon", location.getLongitude())
				.setString("modid", forecastModelId)
				.setInstant("fr", from);//retrieve the measurements from specified date/time given as java.time.Instant object	

		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		Row r = rs.one();
		//TODO: the returned number of the prepared statement is 64bit BIGINT CQL type (Cassandra is big data remember ;).
		//So if the number will be bigger than it is possible to store in 32bit integer - then the casting below will
		//return wrong value (it will return only the low-order 32 bits). But for now - in our use case this is not going 
		//to cause much harm - because we are limiting the number of the forecasts that can actually be retrieved from the 
		//service and this number is much lower than the max number in 32bit integer...
		//However technically this is not correct and should be corrected in the future versions...
		return (int)r.getLong("count");

	}
	 */

}


