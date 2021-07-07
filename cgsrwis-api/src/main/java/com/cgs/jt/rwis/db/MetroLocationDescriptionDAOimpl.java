/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.metro.DataSource;
import com.cgs.jt.rwis.metro.DataSourceType;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.cgs.jt.rwis.metro.MetroObservationParameter;
import com.cgs.jt.rwis.metro.MetroSunshadowMethod;
import com.cgs.jt.rwis.metro.MetroVerbosityLevel;
import com.cgs.jt.rwis.metro.MetroWeatherForecastParameter;
import com.cgs.jt.rwis.metro.Roadlayer;
import com.cgs.jt.rwis.metro.VisibleHorizonDirection;
import com.cgs.jt.rwis.metro.inoutvalues.MetroRoadlayerType;
import com.cgs.jt.rwis.metro.inoutvalues.MetroStationType;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;

/**
 * The implementation of the {@link MetroLocationDescriptionDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class MetroLocationDescriptionDAOimpl implements MetroLocationDescriptionDAO {


	//TODO: If input parameter MetroLocationDescritpion is null, or has null instance variables, or has instance variables that
	//are empty collections -this could lead to:
	//1) NullPointerException thrown in this function
	//2) null inserted into the database - (in the case of Cassandra creating tombstones) 
	//The MetroLocationDescription has one optional parameter (MetroLocationDescription.visibleHorizonDescription), all the 
	//other parameters are mandatory (meaning that the object is meaningful only if they are fully initialized), therefore whoever 
	//calls this function should check that all fields (except MetroLocationDescription.visibleHorizonDescription) are non-null. 
	//This is for example performed by the subscription service endpoint (Hibernate validation of MetroLocationDescritpion 
	//objects when they are constructed from the incoming JSON).
	//The optional field MetroLocationDescription.visibleHorizonDescription is validated by this method!
	@Override
	public void insert(MetroLocationDescription mld){					
		PreparedStatement ps = DBmanager.INSTANCE.getPSforMetroLocationDescriptionInsertion();

		//TODO: since the table for metro location descriptions includes user defined types we need to pass UdtValue objects 
		//as bound values to prepared statement. See "Using UDTs as parameters" on 
		//https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/
		//In order to get the UdtValue object you need to get a reference to UserDefinedType object (and call newValue() on it).
		//You can get the reference to UserDefinedType object by three different ways. The preferred way of getting the UserDefinedType
		//is from the prepared statement - however the roadLayers column contains a Set of UDT-s not the UDT itself - the 
		//UserDefinedType udt = (UserDefinedType) ps.getVariableDefinitions().get("roadLayers").getType(); throws java.lang.IllegalArgumentException: No definition named roadLayers.
		//So we need to get the UserDefinedType object from the session. HOWEVER YOU NEED TO PROVIDE THE KEYSPACE - but in our DropWizard 
		//services the "default" keyspace (which is rwis) is defined in the application.conf (and the value is not accessible to our code).				
		//This is why we need to get the keyspace from the session (however the session is not necessarily bound to any keyspace - which complicates
		//things further)...
		//TODO: it would be nice to get the udt from the prepared statement as shown here - https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/
		//however I do not know how to get it since the udt is inside a Set 
		CqlSession session = DBmanager.INSTANCE.getSession();				
		Optional<CqlIdentifier> currentSessionKeyspaceOrNull = session.getKeyspace();
		CqlIdentifier currentSessionKeyspace = currentSessionKeyspaceOrNull.get(); //TODO: if the session is not bound to keyspace this will throw a runtime NoSuchElementException! Since our services bind to keyspace by default (it is defined in the application.conf file) this exception should not occur!

		//transform the TreeSet<Roadlayer> (MetroLocationDescription.roadLayers) into a new Set that contains UdtValue objects 
		//which can then be inserted into the database.		
		UserDefinedType udtRoadLayer = session.getMetadata()
				//.getKeyspace(CreateKeyspaceAndUsers.rwisDbKeyspace)					
				.getKeyspace(currentSessionKeyspace) 				
				.flatMap(ks -> ks.getUserDefinedType(DBconstants.UDT_ROADLAYER))
				.orElseThrow(() -> new IllegalArgumentException("Missing UDT definition for roadlayer"));				
		//with Java8 streams - see https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
		//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
		//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
		HashSet<UdtValue> roadlayersNew = mld.getRoadLayers().stream()
				.map(x -> udtRoadLayer.newValue().setInt(0, x.getPosition()).setString(1, x.getType().getStringValue()).setDouble(2, x.getThickness()))
				.collect(Collectors.toCollection(HashSet::new));


		//transform also the TreeSet<VisibleHorizonDirection> into HashSet<UdtValue>  which can then be inserted into the database.	
		//the field MetroLocationDescription.visibleHorizonDirection is however OPTIONAL - and we need to check for null...
		//NOTE: also pay attention that if MetroLocationDescription.visibleHorizonDirection is an empty TreeSet - transforming into an
		//empty HashSet<UdtValue> and inserting in Cassandra will create tombstone! Therefore if the TreeSet<VisibleHorizonDirection>
		//is empty we will set the HashSet<UdtValue> to null - which will be checked while inserting and the variable left unset/unbound!
		HashSet<UdtValue> visibleHorizonDirectionsNew = null;
		if(mld.getVisibleHorizonDirections() != null && !mld.getVisibleHorizonDirections().isEmpty()) {							
			UserDefinedType udtVisibleHorizonDirection = session.getMetadata()
					//.getKeyspace(CreateKeyspaceAndUsers.rwisDbKeyspace)					
					.getKeyspace(currentSessionKeyspace) 				
					.flatMap(ks -> ks.getUserDefinedType(DBconstants.UDT_VISIBLEHORIZONDIRECTION))
					.orElseThrow(() -> new IllegalArgumentException("Missing UDT definition for visible horizon direction"));				
			//with Java8 streams - see https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
			//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
			//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
			visibleHorizonDirectionsNew = mld.getVisibleHorizonDirections().stream()
					.map(x -> udtVisibleHorizonDirection.newValue().setDouble(0, x.getAzimuth()).setDouble(1, x.getElevation()))
					.collect(Collectors.toCollection(HashSet::new));
		}


		//transform also the HashMap<MetroObservationParameter, ArrayList<DataSource>> into HashMap<String, ArrayList<UdtValue>>  
		//which can then be inserted into the database.
		//NOTE: we need udtDataSource as well as udtGeographicLocation (since udtDataSource contains udtGeographicLocation(s))

		//get the user defined type for the geographic location 
		//TODO: as described in the comments above it would be nice to get the udt from the prepared statement as shown 
		//here - https://docs.datastax.com/en/developer/java-driver/4.3/manual/core/udts/ however I do not know how to get 
		//it since the udt is inside another udt that is inside a HashMap 
		UserDefinedType udtGeographicLocation = session.getMetadata()
				//.getKeyspace(CreateKeyspaceAndUsers.rwisDbKeyspace)					
				.getKeyspace(currentSessionKeyspace) 			
				.flatMap(ks -> ks.getUserDefinedType(DBconstants.UDT_GEOGRAPHICLOCATION))
				.orElseThrow(() -> new IllegalArgumentException("Missing UDT definition for GeographicLocation"));


		//get the user defined type for the data source
		UserDefinedType udtDataSource = session.getMetadata()
				//.getKeyspace(CreateKeyspaceAndUsers.rwisDbKeyspace)					
				.getKeyspace(currentSessionKeyspace) 			
				.flatMap(ks -> ks.getUserDefinedType(DBconstants.UDT_DATASOURCE))
				.orElseThrow(() -> new IllegalArgumentException("Missing UDT definition for DataSource"));

		//with Java8 streams - see https://www.techiedelight.com/convert-hashmap-treemap-java/
		//see also https://www.geeksforgeeks.org/collectors-tomap-method-in-java-with-examples/ 
		//and https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html#toMap-java.util.function.Function-java.util.function.Function-
		//and maybe https://stackoverflow.com/questions/47800134/java-8-convert-hashmap-program-in-lambda
		//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
		//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
		HashMap<String, ArrayList<UdtValue>> measurementsMappingsNew = mld.getMeasurementsMappings().entrySet().stream()
				.collect(Collectors.toMap(
						x -> x.getKey().getLabel(), 
						x -> x.getValue().stream()
						.map(y -> udtDataSource.newValue()
								.setString(0, y.getType().getLabel())
								.setUdtValue(1, udtGeographicLocation.newValue().setString(0, y.getGeographicLocation().getId()).setDouble(1, y.getGeographicLocation().getLatitude()).setDouble(2, y.getGeographicLocation().getLongitude()))
								.setString(2, y.getParameterLabel())
								.setString(3, y.getDataSourceId()))
						.collect(Collectors.toCollection(ArrayList::new)), 
						(oldValue, newValue) -> newValue, 
						HashMap::new));


		//transform also the HashMap<MetroWeatherForecastParameter, String> into HashMap<String, String>  
		//which can then be inserted into the database.	
		//with Java8 streams - see https://www.techiedelight.com/convert-hashmap-treemap-java/
		//see also https://www.geeksforgeeks.org/collectors-tomap-method-in-java-with-examples/ 
		//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
		//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
		HashMap<String, ArrayList<UdtValue>> weatherForecastMappingsNew = mld.getWeatherForecastMappings().entrySet().stream()
				.collect(Collectors.toMap(
						x -> x.getKey().getLabel(), 
						x -> x.getValue().stream()
						.map(y -> udtDataSource.newValue()
								.setString(0, y.getType().getLabel())
								.setUdtValue(1, udtGeographicLocation.newValue().setString(0, y.getGeographicLocation().getId()).setDouble(1, y.getGeographicLocation().getLatitude()).setDouble(2, y.getGeographicLocation().getLongitude()))
								.setString(2, y.getParameterLabel())
								.setString(3, y.getDataSourceId()))
						.collect(Collectors.toCollection(ArrayList::new)), 
						(oldValue, newValue) -> newValue, 
						HashMap::new));


		BoundStatement bs = ps.bind()
				.setDouble("lat", mld.getGeoLocation().getLatitude())
				.setDouble("lon", mld.getGeoLocation().getLongitude())
				.setString("modid", mld.getForecastModelId())
				.setString("ty", mld.getType().getStringValue())					
				.setDouble("sssd", mld.getSubSurfaceSensorDepth())								
				.setSet("rls", roadlayersNew, UdtValue.class)
				//NOTE: we will set/bound the visible horizon direction even if it is null (and will later unset it if null - see below)
				.setSet("vhd", visibleHorizonDirectionsNew, UdtValue.class)					
				//we are using datastax driver core 4.0.1 - here you can find the setMap() method description - https://docs.datastax.com/en/drivers/java/4.0/com/datastax/oss/driver/api/core/data/SettableByName.html#setMap-java.lang.String-java.util.Map-java.lang.Class-java.lang.Class-
				//it says - This method is provided for convenience when the element type is a non-generic type. For more complex map types, use SettableByIndex.set(int, Object, GenericType).
				//looks like we need to use set(name, object, GenericType) method...
				//.setMap("mm", measurementsMappingsNew, TypeCodecs.TEXT, TypeCodecs.listOf(UdtValue.class))
				.set("mm", measurementsMappingsNew, HashMap.class)				
				.set("wfm", weatherForecastMappingsNew, HashMap.class)
				.setDouble("sff", mld.getSolarFluxFactor())
				.setDouble("iff", mld.getInfraredFluxFactor())
				.setDouble("aff", mld.getAnthropogenicFluxFactor())
				.setDouble("lat", mld.getGeoLocation().getLatitude())
				.setBoolean("es", mld.getEnableSunshadow())
				.setString("sm", mld.getSunshadowMethod().getStringValue())
				.setBoolean("uaf", mld.getUseAnthropogenicFlux())
				.setBoolean("uif", mld.getUseInfraredFlux())
				.setBoolean("usf", mld.getUseSolarFlux())
				.setString("vl", mld.getVerbosityLevel().getStringValue());

		//NOTE: if optional value MetroLocationDescription.visibleHorizonDirections is null or empty TreeSet then we do not want to create Cassandra 
		//tombstone. We could of course use two different bound statements (for each case - if null or empty and if not null and not empty)
		//however the code looks nicer if we just unset a previously set (to null) variable.
		//inspired by https://stackoverflow.com/questions/47444057/cassandra-multiple-prepared-statements-for-nullable-columns
		//and https://stackoverflow.com/questions/36838333/cassandra-prepared-statements-with-empty-columns
		//see also //https://thelastpickle.com/blog/2016/09/15/Null-bindings-on-prepared-statements-and-undesired-tombstone-creation.html 

		if(visibleHorizonDirectionsNew == null) {				
			bs.unset("vhd");
		}
		session.execute(bs);
	}



	@Override	
	public MetroLocationDescription retrieve(GeographicLocation geoLoc, String modelId){

		PreparedStatement ps = DBmanager.INSTANCE.getPSforMetroLocationDescriptionRetrieval();
		BoundStatement bs = ps.bind()
				.setDouble("lat", geoLoc.getLatitude())
				.setDouble("lon", geoLoc.getLongitude())
				.setString("modid", modelId);			


		ResultSet rs = DBmanager.INSTANCE.getSession().execute(bs);
		//we are expecting only one description per station/location
		Row r = rs.one();
		if(r != null) {
			//TODO: for now lets assume that the information in the database is "complete" (since it was validate when inserted).
			//Therefore none of the above variables will be null (except visibleHorizonDirectionsAsUdts - which is optional - but 
			//due to datastax driver handling of CQL NULL the visibleHorizonDirectionsAsUdts will never be null either - see below comments for more...)

			Double latitude = r.getDouble("locLat");
			Double longitude = r.getDouble("locLon");
			String modId = r.getString("modelId");
			String stationTypeAsString = r.getString("type");
			Double subSurfaceSensorDepth = r.getDouble("subSurfaceSensorDepth");
			Set<UdtValue> roadLayersAsUdts = r.getSet("roadLayers", UdtValue.class);
			Set<UdtValue> visibleHorizonDirectionsAsUdts = r.getSet("visibleHorizonDirections", UdtValue.class);
			//It is not possible to get a TreeMap, HashMap etc... from Cassandra - get the map and later transform if needed...
			Map<String, List<UdtValue>> measurementMappingsWithUdts = r.get("measurementsMappings", Map.class);			
			Map<String, List<UdtValue>> weatherForecastMappingsWithUdts = r.get("weatherForecastMappings", Map.class);
			Double solarFluxFactor = r.getDouble("solarFluxFactor");
			Double infraredFluxFactor = r.getDouble("infraredFluxFactor");
			Double anthropogenicFluxFactor = r.getDouble("anthropogenicFluxFactor");
			Boolean enableSunshadow = r.getBoolean("enableSunshadow");
			String sunshadowMethodAsString = r.getString("sunshadowMethod");
			Boolean useAnthropogenicFlux = r.getBoolean("useAnthropogenicFlux");
			Boolean useInfraredFlux = r.getBoolean("useInfraredFlux");
			Boolean useSolarFlux = r.getBoolean("useSolarFlux");
			String verbosityLevelAsString = r.getString("verbosityLevel");			

			//convert Set<UdtValue> to TreeSet<Roadlayer>				
			//with Java8 streams - see  https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
			//and https://dzone.com/articles/udts-in-cassandra-simplified-1
			//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
			//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
			TreeSet<Roadlayer> roadLayers = roadLayersAsUdts.stream()
					.map(x -> new Roadlayer(x.getInt(0), MetroRoadlayerType.get(x.getString(1)), x.getDouble(2)))
					.collect(Collectors.toCollection(TreeSet::new));

			//TODO: the visible horizon description is optional - so it could be NULL CQL value...
			//HOWEVER it seems that NULL CQL values map to empty Java collections - datastax driver behavior. However it is not
			//clear to me if this is only with collections that contain UdtValues or collections in general - see 
			//https://stackoverflow.com/questions/47142993/cassandra-cql-java-driver-returns-a-empty-list-instead-of-null
			//ANYWAY checking for null and returning an empty TreeSet in this case will not hurt...
			TreeSet<VisibleHorizonDirection> visibleHorizonDirections;
			if(visibleHorizonDirectionsAsUdts !=null) {				
				//convert Set<UdtValue> to TreeSet<VisibleHorizonDirection>				
				//with Java8 streams - see  https://stackoverflow.com/questions/46863220/process-elements-of-setfoo-and-create-setbar-using-streams
				//and https://dzone.com/articles/udts-in-cassandra-simplified-1
				//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
				//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
				visibleHorizonDirections = visibleHorizonDirectionsAsUdts.stream()
						.map(x -> new VisibleHorizonDirection(x.getDouble(0), x.getDouble(1)))
						.collect(Collectors.toCollection(TreeSet::new));
			}
			else {
				visibleHorizonDirections = new TreeSet<VisibleHorizonDirection>();
			}


			//convert the returned Map<String, List<UdtValue>> obtained from cassandra into HashMap<MetroObservationParameter, ArrayList<DataSource>>
			//with Java8 streams - see https://www.techiedelight.com/convert-hashmap-treemap-java/
			//see also https://www.geeksforgeeks.org/collectors-tomap-method-in-java-with-examples/ 
			//and https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html#toMap-java.util.function.Function-java.util.function.Function-
			//and maybe https://stackoverflow.com/questions/47800134/java-8-convert-hashmap-program-in-lambda
			//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
			//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
			HashMap<MetroObservationParameter, ArrayList<DataSource>> measurementsMappings = measurementMappingsWithUdts.entrySet().stream()
					.collect(Collectors.toMap(
							x -> MetroObservationParameter.get(x.getKey()),							
							x -> x.getValue().stream().map(y -> new DataSource(
									DataSourceType.valueOfLabel(y.getString(0)), 
									new GeographicLocation(y.getUdtValue(1).getDouble(1), y.getUdtValue(1).getDouble(2)),
									y.getString(2),
									y.getString(3)
									)).collect(Collectors.toCollection(ArrayList::new)),
							(oldValue, newValue) -> newValue,
							HashMap::new));

			//convert the returned Map<String, List<UdtValue>> obtained from cassandra into HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> 
			//with Java8 streams - see https://www.techiedelight.com/convert-hashmap-treemap-java/
			//see also https://www.geeksforgeeks.org/collectors-tomap-method-in-java-with-examples/ 
			//and https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html#toMap-java.util.function.Function-java.util.function.Function-
			//and maybe https://stackoverflow.com/questions/47800134/java-8-convert-hashmap-program-in-lambda
			//TODO: not sure about this conversion. Would be nice to skip it but how? In the past Achilles https://github.com/doanduyhai/Achilles
			//was the solution, but nowdays it seems we could do it with datastax driver object mapper - https://docs.datastax.com/en/developer/java-driver/4.9/manual/mapper/
			HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> weatherForecastMappings = weatherForecastMappingsWithUdts.entrySet().stream()
					.collect(Collectors.toMap(
							x -> MetroWeatherForecastParameter.get(x.getKey()),							
							x -> x.getValue().stream().map(y -> new DataSource(
									DataSourceType.valueOfLabel(y.getString(0)), 
									new GeographicLocation(y.getUdtValue(1).getDouble(1), y.getUdtValue(1).getDouble(2)),
									y.getString(2),
									y.getString(3)
									)).collect(Collectors.toCollection(ArrayList::new)),
							(oldValue, newValue) -> newValue,
							HashMap::new));

			return new MetroLocationDescription(
					new GeographicLocation(latitude, longitude),
					modId,
					MetroStationType.get(stationTypeAsString),
					subSurfaceSensorDepth,
					roadLayers,
					visibleHorizonDirections,
					measurementsMappings,
					weatherForecastMappings,
					solarFluxFactor,
					infraredFluxFactor,
					anthropogenicFluxFactor,
					enableSunshadow,
					MetroSunshadowMethod.get(sunshadowMethodAsString),
					useAnthropogenicFlux,
					useInfraredFlux,
					useSolarFlux,
					MetroVerbosityLevel.get(verbosityLevelAsString)
					);
		}
		else {
			return null;
		}
	}

}
