/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterMeasurements;
import com.cgs.jt.rwis.api.ParameterMeasurementsWithMetadata;
import com.cgs.jt.rwis.api.MeasuredValue;
import com.cgs.jt.rwis.api.params.MeasuredParameter;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;


/**
 * The implementation of the {@link ParameterMeasurementsDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class ParameterMeasurementsDAOimpl implements ParameterMeasurementsDAO {


	//NOTE: whoever calls this function should check that the input is valid (non-null, non-empty etc...).	
	//NOTE: our Cassandra implementation writes in two tables! One that stores the entire history of measurements
	//and the other that stores only the latest measurements for all locations that belong to certain customer.
	@Override
	public void insert(ParameterMeasurementsWithMetadata pm){
		PreparedStatement ps1 = DBmanager.INSTANCE.getPSforMeasurementInsertion();
		PreparedStatement ps2 = DBmanager.INSTANCE.getPSforLatestMeasurementInsertion();

		TreeMap<Instant, Double> measurements = pm.getMeasuredValues();
		for(Map.Entry<Instant, Double> entry : measurements.entrySet()) {
			Instant  key = entry.getKey();
			Double value = entry.getValue();

			//#############----insert into table that stores the entire history of measurements--------########
			BoundStatement bs1 = ps1.bind()
					.setString("pn", pm.getParameter().getLabel())
					.setString("sid", pm.getSensorId())
					.setDouble("lat", pm.getLocation().getLatitude())
					.setDouble("lon", pm.getLocation().getLongitude())				
					//Datastax DRIVER v.4.x NOW SUPPORTS java.time.Instant BY DEFAULT AND OUT OF THE BOX!
					.setInstant("mt", key)
					.setDouble("mv", value);
			DBmanager.INSTANCE.getSession().execute(bs1);


			//##########------insert into table that stores only the latest measurements--------############
			//insert only if at least one customer wants to see this measurement on the geographic map of GUI
			if(!pm.getCustomerIDs().isEmpty()) {
				//of course insert only the last value in the TreeMap (the "freshest" measurement)
				Map.Entry<Instant,Double> lastEntry = measurements.lastEntry();				
				//for every customer new row (even if the measured value is the same)
				for (String customerId : pm.getCustomerIDs()) {

					//NOTE: as long as we do not insert/upsert null values the tombstone is not created (of course only in case
					//we are dealing with columns that are not collections or UDTs) 
					BoundStatement bs2 = ps2.bind()
							.setString("cid", customerId)
							.setString("pn", pm.getParameter().getLabel())
							.setString("sid", pm.getSensorId())
							.setDouble("lat", pm.getLocation().getLatitude())
							.setDouble("lon", pm.getLocation().getLongitude())
							//Datastax DRIVER v.4.x NOW SUPPORTS java.time.Instant BY DEFAULT AND OUT OF THE BOX!
							.setInstant("mt", lastEntry.getKey())
							.setDouble("mv", lastEntry.getValue());
					DBmanager.INSTANCE.getSession().execute(bs2);				
				}
			}
		} 		
	}



	@Override
	public TreeMap<Instant, Double> retrieveLastNmeasurements(int n, MeasuredParameter parameter, String sensorId, GeographicLocation loc){

		//TODO: If input parameters n, parameter and geographicLocation are null then we will get
		//NullPointerExceptions. However we are assuming that they are validated when constructed from the incoming query.
		PreparedStatement ps = DBmanager.INSTANCE.getPSforLastNMeasurementsRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", parameter.getLabel())
				.setString("sid", sensorId)
				.setDouble("lat", loc.getLatitude())
				.setDouble("lon", loc.getLongitude())				
				.setInt("lim", n);//retrieve n measurements
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		//if the result set is empty return null - which will cause HTTP status 204 No Content to be returned...
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {
			TreeMap<Instant, Double> measurements = new TreeMap<Instant, Double>();
			for (Row r : rs) {			
				//NOTE: if data retrieved from the database (mt, mv, ...) can be null (or empty - i.e. values not bound) then NullPointerExceptions
				//will happen. However since it is only the services that insert data into database, and services do validate incoming data before 
				//they insert it into the database - we can assume that they will never be null.	
				Instant mt = r.getInstant("measurementTime");
				Double mv = r.getDouble("measuredVal");
				//NOTE: we do not check the retrieved measured value (if it is in the expected range defined for the parameter)
				//because it should have been checked when it was inserted into the database! 
				measurements.put(mt, mv);			
			}			 
			return measurements;				
		}
	}


	@Override
	public TreeMap<Instant, Double> retrieveMeasurementsFrom(Instant from, MeasuredParameter parameter, String sensorId, GeographicLocation loc){

		//NOTE: If input parameters from, parameter and loc are null then we will get NullPointerExceptions. 
		//However we are assuming that they are validated on the service endpoint.
		PreparedStatement ps = DBmanager.INSTANCE.getPSforMeasurementsFromRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", parameter.getLabel())
				.setString("sid", sensorId)
				.setDouble("lat", loc.getLatitude())
				.setDouble("lon", loc.getLongitude())				
				.setInstant("fr", from);//retrieve n measurements
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		//if the result set is empty return null 
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {			
			TreeMap<Instant, Double> measurements = new TreeMap<Instant, Double>();
			for (Row r : rs) {			
				//NOTE: if data retrieved from the database (mt, mv, ...) can be null (or empty - i.e. values not bound) then NullPointerExceptions
				//will happen. However since it is only the services that insert data into database, and services do validate incoming data before 
				//they insert it into the database - we can assume that they will never be null.	
				Instant mt = r.getInstant("measurementTime");
				Double mv = r.getDouble("measuredVal");
				//NOTE: we do not check the retrieved measured value (if it is in the expected range defined for the parameter)
				//because it should have been checked when it was inserted into the database! 
				measurements.put(mt, mv);			
			}			 
			return measurements;			
		}
	}




	@Override
	public TreeMap<Instant, Double> retrieveMeasurementsFromTo(Instant from, Instant to, MeasuredParameter parameter, String sensorId, GeographicLocation loc){

		//NOTE: If input parameters from, parameter and loc are null then we will get NullPointerExceptions. 
		//However we are assuming that they are validated on the service endpoint.
		PreparedStatement ps = DBmanager.INSTANCE.getPSforMeasurementsFromToRetrieval();
		BoundStatement bs = ps.bind()
				.setString("pn", parameter.getLabel())
				.setString("sid", sensorId)
				.setDouble("lat", loc.getLatitude())
				.setDouble("lon", loc.getLongitude())				
				.setInstant("ft", from)
				.setInstant("tt", to);
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		//if the result set is empty return null 
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {			
			TreeMap<Instant, Double> measurements = new TreeMap<Instant, Double>();
			for (Row r : rs) {			
				//NOTE: if data retrieved from the database (mt, mv, ...) can be null (or empty - i.e. values not bound) then NullPointerExceptions
				//will happen. However since it is only the services that insert data into database, and services do validate incoming data before 
				//they insert it into the database - we can assume that they will never be null.	
				Instant mt = r.getInstant("measurementTime");
				Double mv = r.getDouble("measuredVal");
				//NOTE: we do not check the retrieved measured value (if it is in the expected range defined for the parameter)
				//because it should have been checked when it was inserted into the database! 
				measurements.put(mt, mv);			
			}			 
			return measurements;			
		}
	}



	@Override
	public HashMap<GeographicLocation, HashMap<String, MeasuredValue>> retrieveLatestMeasurementsOfCustomer(MeasuredParameter param, String customerId){
		//retrieve from db - if no results return null
		PreparedStatement ps = DBmanager.INSTANCE.getPSforLatestMeasurementsRetrieval();
		BoundStatement bs = ps.bind()
				.setString("cid", customerId)
				.setString("pn", param.getLabel());	

		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);

		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {
			//TODO: in case if the returned result set is very large (e.g. customer is authorized to thousands and thousands of sensors)
			//then it might prove beneficial to implement some paging magic
			HashMap<GeographicLocation, HashMap<String, MeasuredValue>> latestMeasurements = new HashMap<GeographicLocation, HashMap<String, MeasuredValue>>();
			for (Row r : rs) {			
				//TODO: if there are no guarantees that all fields (sn, lat, lon, mt, mv...) retrieved from the database are non-null, 
				//non-empty (i.e. values not bound) then NullPointerExceptions will happen. However since the  data
				//is always inserted into the database through the service (and service does validate incoming data) - we can assume 
				//that they will never be null.			
				String sid = r.getString("sensorId");
				Double lat = r.getDouble("locLat");
				Double lon = r.getDouble("locLon");				
				Instant mt = r.getInstant("measurementTime");
				//NOTE: we do not check the retrieved measured value (if it is in the expected range defined for the parameter)
				//because it should have been checked when it was inserted into the database! 
				Double mv = r.getDouble("measuredVal");

				GeographicLocation gloc = new GeographicLocation(lat, lon);
				//find if key (i.e. location) already exists and if it does append to the value (hashmap of sensorId <-> TimeValue ),
				//otherwise create new key with value. 
				//using Java8 construction of multi-value maps - see https://www.baeldung.com/java-map-duplicate-keys
				latestMeasurements.computeIfAbsent(gloc, k -> new HashMap<String, MeasuredValue>()).put(sid , new MeasuredValue(mt, mv));
			}			
			return latestMeasurements;				
		}
	}
}

