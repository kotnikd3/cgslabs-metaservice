/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cgs.jt.rwis.api.VisualizationSubscription;
import com.cgs.jt.rwis.api.params.ForecastedParameter;


/**
 * The implementation of the {@link VisualizationSubscriptionDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class VisualizationSubscriptionDAOimpl implements VisualizationSubscriptionDAO{


	//TODO: If input parameter VisualizationSubscription is null, or has null instance variables -this could lead to:
	//1) NullPointerException thrown in this function
	//2) null inserted into the database - (in the case of Cassandra creating tombstones) 
	//The VisualizationSubscription object does not have optional parameters (the object is meaningful only if fully 
	//initialized), therefore whoever calls this function should check that all it's fields are non-null. 
	//This is for example performed by the subscription service endpoint (Hibernate validation of  
	//objects when they are constructed from the incoming JSON).
	@Override
	public void insert(VisualizationSubscription psub) {		
		//TODO: needs actual implementation :)
	}


	@Override
	public HashSet<ForecastedParameter> retrieveVisualizationSubscriptionsByModel(String modelId){
		//TODO: If input parameter forecastModelId is null there will be NullPointerExceptions. However we are 
		//assuming that the parameter is validated when constructed from the incoming query.

		//TODO: needs actual implementation of retrieval from database :)
		if(modelId.equals("219:0:Aladin")) {
			return Stream.of(ForecastedParameter.GROUNDTEMPERATURE).collect(Collectors.toCollection(HashSet::new));
		}
		else if(modelId.equals("219:0:Inca30")) {
			return Stream.of(ForecastedParameter.TOTALPRECIPITATIONSURFACE).collect(Collectors.toCollection(HashSet::new));
		}
		else if(modelId.equals("219:0:Inca60")) {
			return Stream.of(ForecastedParameter.AIRTEMPERATURE150CM, ForecastedParameter.GROUNDTEMPERATURE, ForecastedParameter.RELATIVEHUMIDITY200CM, ForecastedParameter.WINDSPEED10M).collect(Collectors.toCollection(HashSet::new));
		}
		else {
			return null;
		}

	}
}




