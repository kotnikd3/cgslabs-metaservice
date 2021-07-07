/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.route.ReferencePointDescription;
import com.cgs.jt.rwis.route.ReferencedPoint;
import com.cgs.jt.rwis.route.WeatherConditionDescriptor;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;

/**
 * The implementation of the {@link ReferencePointDescriptionDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class ReferencePointDescriptionDAOimpl implements ReferencePointDescriptionDAO{


	//TODO: If input parameter ReferencePointDescription is null, or has null instance variables, or has instance variables that
	//are empty collections -this could lead to:
	//1) NullPointerException thrown in this function
	//2) null inserted into the database - (in the case of Cassandra creating tombstones) 
	//The ReferencePointDescription object does not have optional parameters (the object is meaningful only if fully 
	//initialized), therefore whoever calls this function should check that all it's fields are non-null. 
	//This is for example performed by the subscription service endpoint (Hibernate validation of ReferencePointDescription 
	//objects when they are constructed from the incoming JSON).
	@Override
	public void insert(ReferencePointDescription rpd) {		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforReferencePointDescriptionInsertion();		

		//TODO: since the table for reference points includes user defined types we need to pass UdtValue objects 
		//as bound values to prepared statement. See "Using UDTs as parameters" on 
		//https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/
		//In order to get the UdtValue object you need to get a reference to UserDefinedType object (and call newValue() on it).
		//You can get the reference to UserDefinedType object by three different ways. The preferred way of getting the UserDefinedType
		//is from the prepared statement - however the referencedPoints column contains a Set of UDT-s not the UDT itself - the 
		//UserDefinedType udt = (UserDefinedType) ps.getVariableDefinitions().get("referencedPoints").getType(); throws java.lang.IllegalArgumentException: No definition named referencedPoints.
		//So we need to get the UserDefinedType object from the session. HOWEVER YOU NEED TO PROVIDE THE KEYSPACE - but in our DropWizard 
		//services the "default" keyspace (which is rwis) is defined in the application.conf (and the value is not accessible to our code).				
		//This is why we need to get the keyspace from the session (however the session is not necessarily bound to any keyspace - which complicates
		//things further)...
		CqlSession session = DBmanager.INSTANCE.getSession();				
		Optional<CqlIdentifier> currentSessionKeyspaceOrNull = session.getKeyspace();
		CqlIdentifier currentSessionKeyspace = currentSessionKeyspaceOrNull.get(); //TODO: if the session is not bound to keyspace this will throw a runtime NoSuchElementException!

		//get the user defined type for the referenced point	
		//TODO: as described in the comments above it would be nice to get the udt from the prepared statement as shown 
		//here - https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/ however I do not know how to get 
		//it since the udt is inside a Set 
		UserDefinedType udtReferencedPoint = session.getMetadata()
				//.getKeyspace(CreateKeyspaceAndUsers.rwisDbKeyspace)					
				.getKeyspace(currentSessionKeyspace) 			
				.flatMap(ks -> ks.getUserDefinedType(DBconstants.UDT_REFERENCEDPOINT))
				.orElseThrow(() -> new IllegalArgumentException("Missing UDT definition for referencedpoint"));	

		//get the user defined type for the geographic location (NOTE: remember that the user defined type for reference point contains the user 
		//defined type for geo location - so when creating the reference point udt we need also the geo location utd)
		//TODO: as described in the comments above it would be nice to get the udt from the prepared statement as shown 
		//here - https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/ however I do not know how to get 
		//it since the udt is inside another udt that is inside a Set 
		UserDefinedType udtGeographicLocation = session.getMetadata()
				//.getKeyspace(CreateKeyspaceAndUsers.rwisDbKeyspace)					
				.getKeyspace(currentSessionKeyspace) 			
				.flatMap(ks -> ks.getUserDefinedType(DBconstants.UDT_GEOGRAPHICLOCATION))
				.orElseThrow(() -> new IllegalArgumentException("Missing UDT definition for geographiclocation"));	

		//transform the TreeSet<ReferencedPoint> (ReferencePointsDescription.referencedPoints) into a new Set that contains UdtValue objects 
		//which can then be inserted into the database.		
		//with Java8 streams - see https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
		//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
		//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
		HashSet<UdtValue> referencedpointsNew = rpd.getReferencedPoints().stream()
				.map(x -> udtReferencedPoint.newValue()
						.setUdtValue(0, udtGeographicLocation.newValue().setString(0, x.getGeoLocation().getId()).setDouble(1, x.getGeoLocation().getLatitude()).setDouble(2, x.getGeoLocation().getLongitude()))
						.setDouble(1, x.getDistanceToRefPoint())
						.setString(2, x.getRoadForecastModelId()))
				.collect(Collectors.toCollection(HashSet::new));

		//NOTE: for the weather condition description udt it is easy to get the udt from the prepared statement
		//as described here - https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/ 
		UserDefinedType udtWeatherConditionDescription = (UserDefinedType) ps.getVariableDefinitions().get("wcd").getType();

		//before constructing UDT values we need to transform the HashMap<ForecastedParameter, String> into HashMap<String, String>  
		//which can then be set as a value of UDT.	
		//with Java8 streams - see https://www.techiedelight.com/convert-hashmap-treemap-java/
		//see also https://www.geeksforgeeks.org/collectors-tomap-method-in-java-with-examples/ 
		//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
		//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
		HashMap<String, String> paramsNew = rpd.getWeatherConditionDescr().getParams().entrySet().stream()
				.collect(Collectors.toMap(x -> x.getKey().getLabel(), x -> x.getValue(), (oldValue, newValue) -> newValue, HashMap::new));

		//and we need to transform the HashSet<GeographicLocation> (WeatherConditionDescriptor.locations) into HashSet<UdtValue>
		//which can then be set as a value of weather condition descriptor UDT.		
		//with Java8 streams - see https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
		//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
		//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
		HashSet<UdtValue> locationsNew = rpd.getWeatherConditionDescr().getLocations().stream()
				.map(x -> udtGeographicLocation.newValue().setString(0, x.getId()).setDouble(1, x.getLatitude()).setDouble(2, x.getLongitude()))
				.collect(Collectors.toCollection(HashSet::new));

		UdtValue weatherConditionDescriptionAsUdtValue = udtWeatherConditionDescription.newValue()
				.setMap(0, paramsNew, String.class, String.class)
				.setSet(1, locationsNew, UdtValue.class);

		BoundStatement bs = ps.bind()
				.setString("reg", rpd.getRegion())
				.setDouble("lat", rpd.getGeoLocation().getLatitude())
				.setDouble("lon", rpd.getGeoLocation().getLongitude())											
				.setSet("rfps", referencedpointsNew, UdtValue.class)				
				.setUdtValue("wcd", weatherConditionDescriptionAsUdtValue);

		session.execute(bs);
	}


	@Override
	public HashSet<ReferencePointDescription> retrieve(String region){
		//TODO: If input parameter region is null there will be NullPointerExceptions. However we are 
		//assuming that the parameter is validated when constructed from the incoming query.		
		PreparedStatement ps = DBmanager.INSTANCE.getPSforReferencePointsDescriptionsRetrieval();
		BoundStatement bs = ps.bind().setString("reg", region);					
		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		//if the result set is empty return null 
		if(rs.getAvailableWithoutFetching() < 1) {
			return null;
		}
		else {
			HashSet<ReferencePointDescription> refPointsSet = new HashSet<ReferencePointDescription>();
			for (Row r : rs) {			
				//TODO: for now lets assume that the information in the database is "complete" so none of these variables will ever be null...			
				String reg = r.getString("region");
				Double lat = r.getDouble("locLat");
				Double lon = r.getDouble("locLon");			
				Set<UdtValue> referencedPointsAsUdts = r.getSet("referencedPoints", UdtValue.class);
				UdtValue weatherConditionDescriptionAsUdt = r.getUdtValue("weatherConditionDescription");

				//convert Set<UdtValue> to TreeSet<ReferencedPoint>				
				//with Java8 streams - see  https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
				//and https://dzone.com/articles/udts-in-cassandra-simplified-1
				//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
				//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
				TreeSet<ReferencedPoint> referencedPoints = referencedPointsAsUdts.stream()
						.map(x -> new ReferencedPoint(new GeographicLocation(x.getUdtValue(0).getDouble(1), x.getUdtValue(0).getDouble(2)),  x.getDouble(1), x.getString(2)))
						.collect(Collectors.toCollection(TreeSet::new));

				//convert UdtValue weatherConditionDescriptionAsUdt into WeatherConditionDescription object

				//need to get HashMap<ForecastedParameter, String> from Cassandra Map<String, String> 
				//with Java8 streams - see https://www.techiedelight.com/convert-hashmap-treemap-java/
				//see also https://www.geeksforgeeks.org/collectors-tomap-method-in-java-with-examples/ 
				//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
				//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
				HashMap<ForecastedParameter, String> params = weatherConditionDescriptionAsUdt.getMap(0, String.class, String.class).entrySet().stream()
						.collect(Collectors.toMap(x -> ForecastedParameter.get(x.getKey()), x -> x.getValue(), (oldValue, newValue) -> newValue, HashMap::new));

				//need to get HashSet<GeographicLocation> from Cassandra Set<UdtValue> 
				//with Java8 streams - see  https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
				//and https://dzone.com/articles/udts-in-cassandra-simplified-1
				//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
				//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
				HashSet<GeographicLocation> locations =  weatherConditionDescriptionAsUdt.getSet(1, UdtValue.class).stream()
						.map(x -> new GeographicLocation(x.getDouble(1), x.getDouble(2)))
						.collect(Collectors.toCollection(HashSet::new));			

				WeatherConditionDescriptor weatherConditionDescription = new WeatherConditionDescriptor(params, locations);			

				ReferencePointDescription refPoint = new ReferencePointDescription(reg, new GeographicLocation(lat, lon), referencedPoints, weatherConditionDescription);	
				refPointsSet.add(refPoint);
			}
			return refPointsSet;			
		}
	}
}




