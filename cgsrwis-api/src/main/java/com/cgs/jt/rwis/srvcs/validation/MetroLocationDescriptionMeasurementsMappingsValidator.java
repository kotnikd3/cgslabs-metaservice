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
import javax.validation.ValidatorFactory;
import javax.validation.Validator;

import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.metro.DataSource;
import com.cgs.jt.rwis.metro.DataSourceType;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.cgs.jt.rwis.metro.MetroObservationParameter;
import com.cgs.jt.rwis.metro.MetroWeatherForecastParameter;
import com.cgs.jt.rwis.metro.inoutvalues.MetroStationType;



/**
 * A custom Hibernate validator used to (partially!) validate the MetroLocationDescription object.
 * This custom validator only checks that in the HashMap<MetroObservationParameter, ArrayList<DataSource>> 
 * 1) all the mappings for  are defined (therefore contains keys for all these parameters)
 * 2) all the values (i.e. ArrayLists of DataSource objects) in the HashMap are non-null and non-empty 
 * 3) the DataSource objects are appropriate - the data sources can be of type measurement or 
 * forecast, however the mappings must be reasonable - for example <at> values should not come from 
 * MeasuredParameter.WIND_SPEED_AT_STATION_HEIGHT!.
 * 
 * 
 * * 1) that the HashMap<MetroMeasurementParameter, ArrayList<DataSource>> measurementMappings
 * contains mappings for the mandatory parameters (which by Metro input specifications are 
 * <at>, <td>, <pi>, <ws>, <sc>, <st>, and <sst>).
 *  * 
 * 2) that all the values in the HashMap (i.e. ArrayLists) are non-null (NOTE: checking for HashMap keys for being non-null is not needed
 * since Jackson will complain when serializing/deserializing - that null keys in map are not supported - before 
 * NullPointerExceptions are thrown at server side).
 * 
 * 3) that all of the DataSource objects in all ArrayLists are non-null, and 
 * 
 * 4) that for mappings are reasonable - the rules are as follows: 
 *    <at> --> can come from MeasuredParameter.AIR_TEMPERATURE_AT_STATION_HEIGHT or ForecastedParameter.AIRTEMPERATURE150CM
 *    <td> --> can come from MeasuredParameter.DEWPOINT_AT_STATION_HEIGHT or ForecastedParameter.DEWPOINT150CM
 *    <pi> --> can come from MeasuredParameter.PRECIPITATION_INTENSITY_SURFACE or ForecastedParameter.TOTALPRECIPITATIONSURFACE
 *    <ws> --> can come from MeasuredParameter.WIND_SPEED_AT_STATION_HEIGHT or ForecastedParameter.WINDSPEED10M
 *    <sc> --> can come from measurements - there are different sensors for this parameter (each having it's own output) - therefore 
 *             the toMetroMeasurement() function must be properly implemented, or it can come from the ForecastedParameter.ROADCONDITIONMETRO - for
 *             example when we are running Metro iteratively
 *    <st> --> can come from the MeasuredParameter.ROAD_TEMPERATURE_SURFACE parameter or ForecastedParameter.ROADTEMPERATURESURFACE (for example when running
 *             Metro model iteratively)
 *    <sst> --> can come from the measurements - however sensors are on different depths (ROAD_TEMPERATURE_25MM, ROAD_TEMPERATURE_50MM, ROAD_TEMPERATURE_300MM,...)
 *              and which one is used in a mapping depends on the value of MetroLocationDescription.subSurfaceSensorDepth variable. Can also come from the 
 *              ForecastedParameter.ROADTEMPERATURE400MM (for example when running Metro iteratively). Additionally in case the 
 *              MetroLocationDescription.type has value "bridge" this mapping can come from MeasuredParameter.AIR_TEMPERATURE_AT_STATION_HEIGHT or even 
 *              ForecastedParameter.AIRTEMPERATURE150CM
 * 
 * 
 * @author Jernej Trnkoczy
 *
 */
//NOTE: checking for HashMap keys for being non-null is not needed since Jackson will complain when serializing/deserializing - that null
//  keys in map are not supported - before NullPointerExceptions are thrown at server side).

//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class MetroLocationDescriptionMeasurementsMappingsValidator implements ConstraintValidator<MetroLocationDescriptionMeasurementsMappingsValidation, MetroLocationDescription> {

	@Override
	public void initialize(MetroLocationDescriptionMeasurementsMappingsValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(MetroLocationDescription mld, ConstraintValidatorContext context) {

		//for the MetroLocationDescription.measurementsMappings - the reqirement is
		//not to be null and not to be empty - however @NotNull and @NotEmpty validatoin is performed by non-custom validator, so 
		//here we need to return true in this case (as if the object is valid) and let the non-custom validator handle it...
		if(mld.getMeasurementsMappings() == null || mld.getMeasurementsMappings().isEmpty()) {
			return true;
		}
		else {
			HashMap<MetroObservationParameter, ArrayList<DataSource>> mm = mld.getMeasurementsMappings();

			if(!mm.containsKey(MetroObservationParameter.AT)) {
				return false;
			}
			if(!mm.containsKey(MetroObservationParameter.TD)) {
				return false;
			}
			if(!mm.containsKey(MetroObservationParameter.PI)) {
				return false;
			}
			if(!mm.containsKey(MetroObservationParameter.WS)) {
				return false;
			}
			if(!mm.containsKey(MetroObservationParameter.SC)) {
				return false;
			}
			if(!mm.containsKey(MetroObservationParameter.ST)) {
				return false;
			}
			if(!mm.containsKey(MetroObservationParameter.SST)) {
				return false;
			}

			//now check also for null or empty ArrayLists in HashMap (i.e. values of HashMap), and for null values in these ArrayLists
			for(Map.Entry<MetroObservationParameter, ArrayList<DataSource>> entry : mld.getMeasurementsMappings().entrySet()) {
				MetroObservationParameter mparam = entry.getKey();
				ArrayList<DataSource> value = entry.getValue();				
				if(value == null  || value.isEmpty() || value.contains(null)) {
					return false;
				}				
				//TODO: I believe this is a bit hacky (or maybe not). We are checking that all the DataSource objects inside ArrayList are of
				//type DataSourceType.FORECAST (this is because weather forecast input for Metro cannot be in any possible way retrieved from measurements!)
				else {
					for (DataSource temp : value) {
						//if any DataSource object in any of the ArrayLists contains null or empty instance variables - we should let the non-custom validator
						//handle the problem - so we should return true here (and then non-custom validator will return false - letting know something is wrong)
						//BUT! THE PROBLEM IS THAT NON-CUSTOM VALIDATOR IS NEVER EXECUTED - SINCE WE HAVE AN OLD VERSION OF HYBERNATE THE OBJECTS IN Map COLLECTION
						//ARE NOT VALIDATED - see comments in the MetroLocationDescription class - the  measurementsMappings variable...
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
						
						
						//check that mappings are meaningfull
						else {

							if(temp.getType().equals(DataSourceType.MEASUREMENT)) {
								if(mparam.equals(MetroObservationParameter.AT)) {
									if(MeasuredParameter.get(temp.getParameterLabel())!=MeasuredParameter.AIR_TEMPERATURE_AT_STATION_HEIGHT) {
										return false;
									}
								}
								if(mparam.equals(MetroObservationParameter.TD)) {
									if(MeasuredParameter.get(temp.getParameterLabel())!=MeasuredParameter.DEWPOINT_AT_STATION_HEIGHT) {
										return false;
									}
								}
								if(mparam.equals(MetroObservationParameter.PI)) {
									if(MeasuredParameter.get(temp.getParameterLabel())!=MeasuredParameter.PRECIPITATION_INTENSITY_SURFACE) {
										return false;
									}
								}
								if(mparam.equals(MetroObservationParameter.WS)) {
									if(MeasuredParameter.get(temp.getParameterLabel())!=MeasuredParameter.WIND_SPEED_AT_STATION_HEIGHT) {
										return false;
									}
								}
								//NOTE: the <sc> tag (i.e. road condition) measurements can come from different sensors (e.g. LfftIRSpro31, Vaisala, ....), therefore
								//we cannot check the mapping here...
								//TODO: the checking would be possible if we had a group of all possible road condition measured parameters...
								if(mparam.equals(MetroObservationParameter.ST)) {
									if(MeasuredParameter.get(temp.getParameterLabel())!=MeasuredParameter.ROAD_TEMPERATURE_SURFACE) {
										return false;
									}
								}								 
								if(mparam.equals(MetroObservationParameter.SST)) {
									//NOTE: if the station type is bridge then the <sst> (i.e. sub surface temperature) can come from measured air temperature 
									if(mld.getType().equals(MetroStationType.BRIDGE)) {
										if(MeasuredParameter.get(temp.getParameterLabel())!=MeasuredParameter.AIR_TEMPERATURE_AT_STATION_HEIGHT) {
											return false;
										}
									}									
									else {
										//NOTE: if station type is not bridge (meaning normal station) then there should be a sub-surface sensor there. 
										//however the <sst> tag (i.e. road sub surface temperature) measurements can come from sensors that are at 
										//different depths (25mm, 50mm, 300mm,... ) below road surface. From which sensor the mapping should be defined 
										//depends on the value of the MetroLocationDescription.subSurfaceSensorDepth variable (which represents the sub 
										//surface sensor depth measured in meters). Therefore for now no checking in this case...
										//TODO: the checking would be possible if we had a group of all possible sub surface measured parameters...
									}
								}
							}
							else if(temp.getType().equals(DataSourceType.FORECAST)) {
								if(mparam.equals(MetroObservationParameter.AT)) {
									if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.AIRTEMPERATURE150CM) {
										return false;
									}
								}								
								if(mparam.equals(MetroObservationParameter.TD)) {
									if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.DEWPOINT150CM) {
										return false;
									}
								}								
								if(mparam.equals(MetroObservationParameter.PI)) {
									if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.TOTALPRECIPITATIONSURFACE) {
										return false;
									}
								}								
								if(mparam.equals(MetroObservationParameter.WS)) {
									if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.WINDSPEED10M) {
										return false;
									}
								}
								if(mparam.equals(MetroObservationParameter.SC)) {
									if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.ROADCONDITIONMETRO) {
										return false;
									}

								}
								if(mparam.equals(MetroObservationParameter.ST)) {
									if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.ROADTEMPERATURESURFACE) {
										return false;
									}
								}							
								if(mparam.equals(MetroObservationParameter.SST)) {
									//NOTE: the <sst> (i.e. sub surface temperature) can come from forecasted air temperature if the station type
									//is bridge
									if(mld.getType().equals(MetroStationType.BRIDGE)) {
										if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.AIRTEMPERATURE150CM) {
											return false;
										}
									}
									//or it can come from the Metro output - when running Metro iteratively
									else {
										if(ForecastedParameter.get(temp.getParameterLabel())!=ForecastedParameter.ROADTEMPERATURE400MM) {
											return false;
										}
									}
								}
							}
							else {
								//the type is not forecast nor measurement - this is very strange and is not allowed - however 
								//we will let some other custom validator (the one that validates the DataSource objects) handle it 
								//so return true here
								return true;
							}
						}
					}
				}

			}
			return true;			
		}
	}
}


