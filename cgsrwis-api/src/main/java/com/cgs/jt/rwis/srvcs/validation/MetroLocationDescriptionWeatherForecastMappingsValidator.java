/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.metro.DataSource;
import com.cgs.jt.rwis.metro.DataSourceType;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.cgs.jt.rwis.metro.MetroObservationParameter;
import com.cgs.jt.rwis.metro.MetroWeatherForecastParameter;

/**
 * A custom Hibernate validator used to (partially!) validate the MetroLocationDescription object.
 * This custom validator only checks:
 * 1) that the HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> weatherForecastMappings
 * contains weather forecast mappings for the mandatory parameters (which by Metro input specifications are 
 * <at>, <td>, <ra>, <sn>, <ws> and <ap>).
 * @see https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo) - note that if both 
 * --use-solarflux-forecast and --use-infrared-forecast are used, the cc field is not mandatory!
 * 
 * 2) that all the values in the HashMap (i.e. ArrayLists) are non-null (NOTE: checking for HashMap keys for being non-null is not needed
 * since Jackson will complain when serializing/deserializing - that null keys in map are not supported - before 
 * NullPointerExceptions are thrown at server side).
 * 
 * 3) that all of the DataSource objects in all ArrayLists are non-null
 * 
 * 4) that the DataSource objects are of type DataSourceType.FORECAST (this is because weather forecast input for Metro cannot be in any possible
 * way retrieved from measurements!), and 
 * 
 * 5) that for each mapping the correct parameter (label) is used - the mappings are fixed and should be as follows: 
 *    <at> --> ForecastedParameter.AIRTEMPERATURE150CM
 *    <td> --> ForecastedParameter.DEWPOINT150CM
 *    <ra> --> ForecastedParameter.RAINPRECIPITATIONSURFACE
 *    <sn> --> ForecastedParameter.SNOWPRECIPITATIONSURFACE
 *    <ws> --> ForecastedParameter.WINDSPEED10M
 *    <ap> --> ForecastedParameter.PRESSURESURFACE
 *    <cc> --> ForecastedParameter.CLOUDCOVERAGE
 *    <sf> --> ForecastedParameter.SOLARFLUXSURFACE
 *    <ir> --> ForecastedParameter.INFRAREDFLUXSURFACE
 *    <fa> --> ForecastedParameter.ANTHROPOGENICFLUXSURFACE
 *
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
//TODO: check that this validator indeed works as expected!
public class MetroLocationDescriptionWeatherForecastMappingsValidator implements ConstraintValidator<MetroLocationDescriptionWeatherForecastMappingsValidation, MetroLocationDescription> {

	@Override
	public void initialize(MetroLocationDescriptionWeatherForecastMappingsValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(MetroLocationDescription mld, ConstraintValidatorContext context) {

		//for the MetroLocationDescription.weatherForecastMappings - the reqirement is
		//not to be null and not to be empty - however @NotNull and @NotEmpty validatoin is performed by non-custom validator, so 
		//here we need to return true in this case (as if the object is valid) and let the non-custom validator handle it...
		if(mld.getWeatherForecastMappings() == null || 
				mld.getWeatherForecastMappings().isEmpty()) {
			return true;
		}
		else {
			HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> wfm = mld.getWeatherForecastMappings();
			//the mappings for <at>, <td>, <ra>, <sn>, <ws> and <ap> are mandatory 
			if(!wfm.containsKey(MetroWeatherForecastParameter.AT)) {
				return false;
			}
			if(!wfm.containsKey(MetroWeatherForecastParameter.TD)) {
				return false;
			}
			if(!wfm.containsKey(MetroWeatherForecastParameter.RA)) {
				return false;
			}
			if(!wfm.containsKey(MetroWeatherForecastParameter.SN)) {
				return false;
			}
			if(!wfm.containsKey(MetroWeatherForecastParameter.WS)) {
				return false;
			}
			if(!wfm.containsKey(MetroWeatherForecastParameter.AP)) {
				return false;
			}
			//the <cc>, <sf>, <ir>, <fa> are not mandatory - however they need to be present under the following conditions:
			if(mld.getUseAnthropogenicFlux() && !wfm.containsKey(MetroWeatherForecastParameter.FA)) {
				return false;
			}
			if(mld.getUseInfraredFlux() && !wfm.containsKey(MetroWeatherForecastParameter.IR)) {
				return false;
			}
			if(mld.getUseSolarFlux() && !wfm.containsKey(MetroWeatherForecastParameter.SF)) {
				return false;
			}
			if((!mld.getUseInfraredFlux() || !mld.getUseSolarFlux()) && !wfm.containsKey(MetroWeatherForecastParameter.CC)){
				return false;
			}			


			//now check also for null or empty ArrayLists in HashMap (i.e. values of HashMap), and null values in these ArrayLists
			for(Map.Entry<MetroWeatherForecastParameter, ArrayList<DataSource>> entry : mld.getWeatherForecastMappings().entrySet()) {
				MetroWeatherForecastParameter mparam = entry.getKey();
				ArrayList<DataSource> value = entry.getValue();				
				if(value == null  || value.isEmpty() || value.contains(null)) {
					return false;
				}				
				//TODO: I believe this is a bit hacky (or maybe not). We are checking that all the DataSource objects inside ArrayList are of
				//type DataSourceType.FORECAST (this is because weather forecast input for Metro cannot be in any possible way retrieved from measurements!)
				else {
					for (DataSource temp : value) {
						//if any DataSource object in any of the ArrayLists contains null or empty instance variables - we should let the non-custom validatr
						//handle the problem - so we should return true here (and then non-custom validator will return false - letting know something is wrong)
						//BUT! THE PROBLEM IS THAT NON-CUSTOM VALIDATOR IS NEVER EXECUTED - SINCE WE HAVE AN OLD VERSION OF HYBERNATE THE OBJECTS IN Map COLLECTION
						//ARE NOT VALIDATED - SEE COMMENTS IN THE MetroLocationDescription class - the  weatherForecastMappings variable...
						/*
						if(temp.getType() == null || 
								temp.getGeographicLocation() == null || 
								temp.getParameterLabel() == null ||
								temp.getParameterLabel().isEmpty()|| 
								temp.getDataSourceId() == null ||
								temp.getDataSourceId().isEmpty()
								) {
							return true;
						}
						*/
						//so we will perform validation of the DataSource object here...
						ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
						Validator validator = factory.getValidator();
						if (!validator.validate(temp).isEmpty()) {
							return false;
						}						
						//check that:
						//1) the type of the data source is forecast (remember - the metro weather forecast input cannot be in any possible way 
						//retrieved from measurements - we dont have measurements in the future!), and
						//2) that the correct parameter is mapped to the Metro forecast input XML tag. The mapping should be as follows:
						// <at> --> ForecastedParameter.AIRTEMPERATURE150CM
						// <td> --> ForecastedParameter.DEWPOINT150CM
						// <ra> --> ForecastedParameter.RAINPRECIPITATIONSURFACE
						// <sn> --> ForecastedParameter.SNOWPRECIPITATIONSURFACE
						// <ws> --> ForecastedParameter.WINDSPEED10M
						// <ap> --> ForecastedParameter.PRESSURESURFACE
						// <cc> --> ForecastedParameter.CLOUDCOVERAGE
						// <sf> --> ForecastedParameter.SOLARFLUXSURFACE
						// <ir> --> ForecastedParameter.INFRAREDFLUXSURFACE
						// <fa> --> ForecastedParameter.ANTHROPOGENICFLUXSURFACE
						//
						else {
							if(!temp.getType().equals(DataSourceType.FORECAST)) {
								return false;
							}							
							if(mparam.equals(MetroWeatherForecastParameter.AT)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.AIRTEMPERATURE150CM) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.TD)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.DEWPOINT150CM) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.RA)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.RAINPRECIPITATIONSURFACE) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.SN)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.SNOWPRECIPITATIONSURFACE) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.WS)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.WINDSPEED10M) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.AP)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.PRESSURESURFACE) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.CC)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.CLOUDCOVERAGE) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.SF)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.SOLARFLUXSURFACE) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.IR)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.INFRAREDFLUXSURFACE) {
									return false;
								}
							}
							if(mparam.equals(MetroWeatherForecastParameter.FA)) {
								if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.ANTHROPOGENICFLUXSURFACE) {
									return false;
								}
							}
						}
					}
				}
			}
			return true;			
		}
	}
}

