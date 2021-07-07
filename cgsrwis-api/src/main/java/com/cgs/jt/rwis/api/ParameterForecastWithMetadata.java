/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import java.time.Instant;
import java.util.HashSet;
import java.util.TreeMap;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * Represents an extension of the {@link ParameterForecast} with addition of the information defining
 * which customers want to see the forecasts for certain parameter on certain location in the GUI on the 
 * geographical map (a map in the GUI with all the locations that the customer is interested in). If a set 
 * of customers is empty - this means that not a single customer wants to see these particular forecast 
 * on a geographical map.
 * 
 *
 * @author  Jernej Trnkoczy
 * 
 */
//NOTE: It would be nice to omit this class and live with ParameterForecast object only. However this class is necessary
//because we cannot implement "insert()" forecast service endpoint in a way that the customerId would be sent
//as a query param (instead of being incorporated into JSON). This is not possible because sometimes certain forecast is "owned"
//by several customers - and in this case we would need to implement a query param whose content would be a set of customers - it 
//would be possible however I believe it would not be very nice!



//NOTE: It would be nice to omit this class and live with ParameterForecast object only. However this class is necessary
//because we cannot implement "insert(ParameterForecast)" service endpoint - because in case the forecasts should be 
//shown on the geographical map the service needs to know which customers want the forecasts to be shown on the map.
//This is a consequence of a design decision - the forecasts that are shown on the map are inserted in two Cassandra 
//database tables - one that stores the entire history of the forecasts (and does not contain column "customer") and the 
//other that stores only the latest measurements on all the locations that need to be shown to a specific customer. 
//TODO: in the first place this is NOT A NICE DESIGN - the sensors should not care how the database is implemented!
//Anyhow - even if we go this way we have two options here:
//1) The modules producing forecasts produce ParameterForecast objects (without metadata) - and then the service for 
//   each forecast that needs to be inserted queries the database first (to find out whether this particular forecast needs 
//   to be inserted in one table or two tables - depending on the system metadata), and based on the retrieved metadata 
//   decides in which table to insert. The good side of this approach is that modules producing forecasts are not aware 
//   of any metadata describing configuration. The downside is that for each insert there has to be an expensive retrieve 
//   operation first.
//2) The modules producing forecasts produce ParameterForecastWithMetadata object (containing also the needed metadata). 
//   The good side is that insert is very fast - because it really is only insert (without any additional expensive retrieve 
//   operation first). The downside is that the modules producing forecasts must know about system metadata, also 3rd party 
//   modules might "cheat" (because it is the module who decides which customers can see it's data - by inserting the right metadata).
//TODO: For now we choosed option 2) HOWEVER THIS NEEDS SOME FURTHER THINKING AND DECISIONS!!!
public class ParameterForecastWithMetadata extends ParameterForecast{

	/**
	 * Represents the set of customer IDs that want to see the forecasts on the geographical map in the GUI. If a set 
	 * of customers is empty - this means that not a single customer wants to see these particular forecasts 
	 * on a geographical map. 
	 */	
	@NotNull
	@NotEmpty
	HashSet<String> customerIDs;



	/**
	 * Constructor.
	 * @param parameter The forecasted parameter (i.e. parameter for which this forecast is made).
	 * The forecasted parameter instances are defined in {@link ForecastedParameter} enum. 
	 * @param geographicLocation The geographic location (lat/lon) for which this forecast is made.
	 * @param forecastModelId The forecast model id (uniquely identifies the forecast model) that produced this forecast.
	 * @param referenceTime The reference time of the forecast.
	 * @param forecast The forecasted values (one or multiple) at different forecast absolute times.
	 * @param customerIDs A set of customer IDs that are authorized to the measurements from this location (e.g. to view them in the graphical user interface).
	 */	
	@JsonCreator
	public ParameterForecastWithMetadata (
			@JsonProperty("parameter") ForecastedParameter parameter, 
			@JsonProperty("geographicLocation") GeographicLocation geographicLocation, 
			@JsonProperty("forecastModelId") String forecastModelId,
			@JsonProperty("referenceTime") Instant referenceTime, 
			@JsonProperty("forecast") TreeMap<Instant, Double> forecast,		
			@JsonProperty("customerIDs") HashSet<String> customerIDs){		
		super (parameter, geographicLocation, forecastModelId, referenceTime, forecast);
		this.customerIDs = customerIDs;
	}
	
	
	/**
	 * Constructor. 
	 * @param pf The superclass instance. 
	 * @param customerIDs A set of customer IDs that are authorized to the measurements from this location (e.g. to view them in the graphical user interface).
	 */	
	public ParameterForecastWithMetadata (ParameterForecast pf, HashSet<String> customerIDs){		
		super (pf.getParameter(), pf.getLocation(), pf.getForecastModelId(), pf.getReferenceTime(), pf.getForecast());
		this.customerIDs = customerIDs;
	}


	/**
	 * Returns the {@link #customerIDs} instance variable of this object.
	 * @return The {@link #customerIDs} instance variable.
	 *
	 */
	@JsonProperty("customerIDs")//the name in the JSON will be the same as the name of variable (i.e. customerIDs)
	public HashSet<String> getCustomerIDs() {
		return this.customerIDs;
	}


}
