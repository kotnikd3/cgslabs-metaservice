/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import java.util.HashMap;
import java.util.HashSet;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterForecastSubscription;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

/**
 * The implementation of the {@link ParameterForecastSubscriptionDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class ParameterForecastSubscriptionDAOimpl implements ParameterForecastSubscriptionDAO{


	//TODO: If input parameter ParameterForecastSubscription is null, or has null instance variables, or has instance variables that
	//are empty collections -this could lead to:
	//1) NullPointerException thrown in this function
	//2) null inserted into the database - (in the case of Cassandra creating tombstones) 
	//The ParameterForecastSubscription object does not have optional parameters (the object is meaningful only if fully 
	//initialized), therefore whoever calls this function should check that all it's fields are non-null. 
	//This is for example performed by the subscription service endpoint (Hibernate validation of ParameterForecastSubscription 
	//objects when they are constructed from the incoming JSON).
	@Override
	public void insert(ParameterForecastSubscription psub) {		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforParameterSubscriptionByModelInsertion();
		BoundStatement bs = ps.bind()
				.setDouble("lat", psub.getSubscriptionPoint().getGeoLocation().getLatitude())
				.setDouble("lon", psub.getSubscriptionPoint().getGeoLocation().getLongitude())
				.setDouble("ele", psub.getSubscriptionPoint().getElevation())
				.setString("pname", psub.getParameter().getLabel())				
				.setString("modid", psub.getForecastModelId());
		DBmanager.INSTANCE.getSession().execute(bs);

	}


	@Override
	public HashMap<EarthSurfacePoint, HashSet<ForecastedParameter>> retrieveParameterSubscriptionsByModel(String forecastModelId){
		//TODO: If input parameter forecastModelId is null there will be NullPointerExceptions. However we are 
		//assuming that the parameter is validated when constructed from the incoming query.
		PreparedStatement ps = DBmanager.INSTANCE.getPSforParameterSubscriptionByModelRetrieval();
		BoundStatement bs = ps.bind().setString("modid", forecastModelId);					
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		//if the result set is empty return null
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {
			HashMap<EarthSurfacePoint, HashSet<ForecastedParameter>> map = new HashMap<EarthSurfacePoint, HashSet<ForecastedParameter>>();
			for (Row r : rs) {			
				//TODO: if there are no guarantees that all fields (lat, lon, elev, paramn) retrieved from the database are non-null, 
				//non-empty (i.e. values not bound) then NullPointerExceptions will happen. However since the parameter subscription data
				//is always inserted into the database through subscription service (and service does validate incoming data) - we can assume 
				//that they will never be null.			
				Double lat = r.getDouble("locLat");
				Double lon = r.getDouble("locLon");
				Double elev = r.getDouble("elevation");
				String paramname = r.getString("paramName");

				//TODO: the param retrieved by paramname (using get()) can be null - if the given paramname is "wrong".
				//We assume that the names in the database are "not wrong" since they were inserted by the service
				//which validated the user-submitted input - so the value is not checked here! 
				ForecastedParameter param = ForecastedParameter.get(paramname);
				EarthSurfacePoint sPoint = new EarthSurfacePoint(new GeographicLocation(lat, lon), elev);
				//find if key (i.e. location) already exists and if it does append to the value (list of parameters),
				//otherwise create new key with value. 
				//using Java8 construction of multi-value maps - see https://www.baeldung.com/java-map-duplicate-keys
				map.computeIfAbsent(sPoint, k -> new HashSet<ForecastedParameter>()).add(param);
			}
			return map;
		}
	}
}



