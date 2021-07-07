/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.metro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.metro.inoutvalues.MetroStationType;
import com.cgs.jt.rwis.srvcs.validation.MetroLocationDescriptionMeasurementsMappingsValidation;
import com.cgs.jt.rwis.srvcs.validation.MetroLocationDescriptionVisibleHorizonValidation;
import com.cgs.jt.rwis.srvcs.validation.MetroLocationDescriptionWeatherForecastMappingsValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the container for the Metro metadata describing a specific location on the Earth's surface (the geographic 
 * location of the RWIS station). This information is retrieved from the database and is needed by the module that triggers
 * Metro model execution for a particular location. Some of this information ({@link #type}, {@link #subSurfaceSensorDepth},
 * {@link #roadLayers}, {@link #visibleHorizonDirections}) will end in the XML documents that are input for Metro, some of the 
 * information ({@link #measurementsMappings}, {@link #wetherForecastMappings} is needed to retrieve the data from the database,
 * some of the information ({@link #sfFactor}, {@link #irFactor}, {@link #afFactor} is needed to perform (optional) weather forecast 
 * pre-processing (ahead of forecast XML generation) and some of the data ({@link #metroCommandLine}) is needed to run Metro
 * according to user-defined options (e.g. users can define that for certain location Metro is run in such and such way).
 * 
 * @author  Jernej Trnkoczy
 * 
 */
//TODO: MetroLocationDescription is not appropriate name for this class. The metadata of this class does not describe only the location - there is a lot of 
//metadata describing how Metro model should be run - and this does not depend on the location - remember that there could be several different Metro models
//run in parallel for the same location! So better name would be MetroExecutionDescription (or something similar). NOTE: we might even separate this metadata in two 
//classes - MetroLocationDescription (containing metadata about the location - i.e. road layers etc...) and MetroExecutionConfiguration (containing metadata describing
//how to run Metro). This would allow us (at least in relational database) to have only one location description in database. However it might be even good to allow the
//option to have different location descriptions for the same location (for example if we modify the road layers thickness to get better results - it might be good to run
//Metro with two different thicknesses and then compare the results...)

@MetroLocationDescriptionVisibleHorizonValidation //our custom validation that checks the values in the visibleHorizonDirections TreeSet (so that they fulfill Metro input specification requirements)
@MetroLocationDescriptionMeasurementsMappingsValidation //our custom validation that check the measurements mappings (so that they contain mandatory <st> and <sst> mappings)
@MetroLocationDescriptionWeatherForecastMappingsValidation //our custom validation that check the weather forecast mappings (so that they contain mandatory <at>, <td>, <ra>, <sn>, <ws> and <ap> mappings)
public class MetroLocationDescription {

	/**
	 * Represents the geographic location for which the description is made.	 
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@Valid //validator of the MetroLocationDescription needs to check the validness of GeographicLocation too (if GeographicLocation is not valid then MetroLocationDescription is not valid either)
	private GeographicLocation geoLocation;

	/**
	 * Represents the identification of the road forecast model that will be processing this description (for example SI:CGS:Metro01 roadcast model).	 
	 * This allows to run multiple road forecast models on the same location but with different configuration (contained in the description metadata)
	 */	
	@NotNull
	@NotEmpty
	private  String forecastModelId;


	/**
	 * Represents the type of the station (which can be road or bridge).
	 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
	 *  
	 */
	//for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	//NOTE: due to the implementation (see @JsonCreator in MetroStationType) if in JSON there is "unknown" String
	//this will result in null MetroStationType - and since it must not be null - the @NotNull validator will report
	//object not valid. This is why we added the "and must be one of the supported types" in the message.
	@NotNull(message = "may not be null and must be one of the supported types")
	private MetroStationType type;




	/**
	 * Represents the sub surface sensor depth (the sensor that is measuring sub surface temperature which represents
	 * input for the Metro model). Measured in [m].
	 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
	 *  
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	//Metro supports values from 0.2 m to 0.6 m - https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo) (see --use-sst-sensor-depth option)
	@DecimalMin(value = "0.2", message = "Minimum sub surface sensor depth is 0.2 m")
	@DecimalMax(value = "0.6", message = "Maximum sub surface sensor depth is 0.6 m")
	//TODO: this information is actually redundant - because the sensor depth can be deduced from the name of the measured parameter
	//from which we retrieve the values for the <sst> tag in observations XML of Metro (see instance variable measurementsMappings below).
	//However on the other hand deducing sensor depth from a name of parameter is dirty hacking... This needs some further thinking....
	private Double subSurfaceSensorDepth;

	/**
	 * Represents the set of road surface layers at this location. 
	 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
	 *  
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null when deserializing from JSON
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the set contains at least one element - the field is mandatory - see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) 
	@Valid //for the validator (packaged with Dropwizard - Hibernate) to check the validness of Roadlayer objects (if one fo the Roadlayers objects in the set is not valid then MetroLocationDescription is not valid either)
	private TreeSet<Roadlayer> roadLayers;

	/**
	 * Represents the set of "directions" describing the visible horizon of this location.
	 * @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo) 
	 *  
	 */
	//NOTE: this filed is optional (not every road station has this kind of information). However if this field is null or empty collection
	//we might create Cassandra tombstones (when inserted into Cassandra DB). Also Cassandra will retrieve an empty Set if there is CQL value 
	//NULL in the database, or even if the value was unset/unbound. Therefore we will NOT PERFORM @NotNull and @NotEmpty validation here - and
	//it will be left to the methods that insert/retrieve from the database!
	//NOTE: however if the horizon description is non-null and non-empty then (according to Metro documentation - see
	//https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)) the description should:
	//1) azimuth, elevation pairs should be ordered by growing azimuths values
	//2) uniform step in azimuth is required (i.e, each neighbour azimuth values are displaced by same distance)
	//3) the whole horizon should be covered by the data, i.e. from 0 to 360 degrees. If there is no 360 degrees value given, the value of 0 degrees is taken as 360 degrees.
	//Therefore we need a custom validation for this case. So I implemented custom validator 
	//(com.cgs.jt.rwis.srvcs.validation.MetroLocationDescriptionValidator) and annotation (com.cgs.jt.rwis.srvcs.validation.MetroLocationDescriptionValidation)
	//and annotated this class with it (above - just before class declaration)
	@Valid //if visibleHorizonDirections is non-null and non-empty then all the objects inside it should be validated!
	private TreeSet<VisibleHorizonDirection> visibleHorizonDirections;

	/**
	 * Represents the mapping from the Metro observations (at, td, pi, ws, sc, st, sst - @see https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) )
	 * to the data data sources from which to obtain the values for the observations. The definition of this kind of mapping is needed because of the following scenarios:
	 * 1) in different road weather stations there can be different sensors whose output can be mapped to the same Metro observation. For example the road condition
	 *    in station1 is measured by Lufft sensor while on station2 the road condition is measured by Vaisala sensor. Because the output values (range and units) of 
	 *    both sensors are in this case different we cannot use the same {@link MeasuredParameter} for both sensors. The module calculating Metro road forecast needs 
	 *    to know that for station1 it needs to query the database for parameter "Lufft IRS31pro road condition in [categorical] @ Road surface" while for station2 
	 *    it needs to query the database for parameter "Vaisala DRS511 road condition in [categorical] @ Road surface". The transformation of the measured values into
	 *    Metro-compatible values is done by the {@link MeasuredParameter.toMetro()} function.
	 *    	  
	 * 2) in the station there can be multiple sensors measuring the same parameter (e.g. for redundancy purposes). Therefore the sensor inside certain road weather
	 *    station is identified by the parameter name and sensor id. Metro values can therefore be obtained from
	 *    any sensor in the set of redundant sensors. However some of the sensors in the set of redundant sensors might not be working at the moment. Therefore our Metro
	 *    module will try to obtain the values from the sensors successively. However the ORDER of the sensors to use when producing Metro input XML should not be 
	 *    arbitrary - one of the sensors in the set of redundant sensors might be more accurate than the others for example. Therefore the order of data sources in which
	 *    to try to obtain measurements is defined by system administrator  - and is maintained by using a {@link ArrayList} of the {@link DataSource} instances.
	 *    
	 * 3) The values for the at, td, pi, ws, sc, st, sst Metro input XML tags might even come from some other source (and not from the sensor in the road station).
	 *    This is especially true for the at, td, pi, ws, sc tags. For example currently not all of the RWS stations are equipped with the wind speed sensor - and 
	 *    in such cases we need the possibility to retrieve the missing measurements from the weather forecast.
	 *    
	 * 4) The calculation of Metro on a certain geographic location might use the measurements input on some other (possibly nearby - the closer the better ;) geographic 
	 *    location. For example a certain implementation of the thermal mapping-based road forecast (such as the one used in old MDSS for DARS) might calculate Metro on
	 *    locations where there are no road weather stations (in case of old MDSS for DARS Metro is calculated on all Inca grid points covering the Slovenian highway
	 *    cross). Therefore we need the possibility to obtain values for the at, td, pi, ws, sc, st, sst Metro input from data sources (sensors, weather forecasts, ...)
	 *    on some other (possibly nearby) location. 
	 *  
	 * NOTE: The mappings for all <at>, <td>, <pi>, <ws>, <sc>, <st>, and <sst> are mandatory.
	 * NOTE: For each of the at, td, pi, ws, sc, st, and sst an {@link ArrayList} of data sources can be defined. The list contains the data sources by their priority - 
	 * i.e. the first data source in the list has "highest priority". This means that the module calculating Metro will try to obtain the data from the first data
	 * source in the list, then the second, and so on. If data cannot be obtained from any data source in the list then Metro module will fail.
	 */	


	//NOTE: The mappings for all <at>, <td>, <pi>, <ws>, <sc>, <st>, and <sst> to a list of either {@link com.cgs.jt.rwis.api.params.MeasuredParameter} or
	//{@link com.cgs.jt.rwis.api.params.ForecastedParameter} are mandatory. However not all mappings are meaningful. For example it would be very controversial 
	//to use air temperature at ground as a replacement for air temperature at 150cm above ground. Even worse it would be to get values for <at> measurement from 
	//forecasts of wind speed. Therefore the following rules are adopted (and enforced by the service endpoint validation):
	//    <at> --> can come from MeasuredParameter.AIR_TEMPERATURE_AT_STATION_HEIGHT or ForecastedParameter.AIRTEMPERATURE150CM
	//    <td> --> can come from MeasuredParameter.DEWPOINT_AT_STATION_HEIGHT or ForecastedParameter.DEWPOINT150CM
	//    <pi> --> can come from MeasuredParameter.PRECIPITATION_INTENSITY_SURFACE or ForecastedParameter.TOTALPRECIPITATIONSURFACE
	//    <ws> --> can come from MeasuredParameter.WIND_SPEED_AT_STATION_HEIGHT or ForecastedParameter.WINDSPEED10M
	//    <sc> --> can come from measurements - there are different sensors for this parameter (each having it's own output) - therefore 
	//             the toMetroMeasurement() function must be properly implemented, or it can come from the ForecastedParameter.ROADCONDITIONMETRO - for
	//             example when we are running Metro iteratively
	//    <st> --> can come from the MeasuredParameter.ROAD_TEMPERATURE_SURFACE parameter or ForecastedParameter.ROADTEMPERATURESURFACE (for example when running
	//             Metro model iteratively)
	//    <sst> --> can come from the measurements - however sensors are on different depths (ROAD_TEMPERATURE_25MM, ROAD_TEMPERATURE_50MM, ROAD_TEMPERATURE_300MM,...)
	//              and which one is used in a mapping depends on the value of MetroLocationDescription.subSurfaceSensorDepth variable. Can also come from the 
	//              ForecastedParameter.ROADTEMPERATURE400MM (for example when running Metro iteratively). Additionally in case the 
	//              MetroLocationDescription.type has value "bridge" this mapping can come from MeasuredParameter.AIR_TEMPERATURE_AT_STATION_HEIGHT or even 
	//              ForecastedParameter.AIRTEMPERATURE150CM
	//However the different location or different forecast model of the DataSource (on a certain location) is meaningful and arbitrarily allowed! 
	//NOTE: the validation of HashMap content and for presence of mandatory and appropriate mappings is done by
	//our custom validator - MetroLocationDescriptionMeasurementsMappingsValidator - and is triggered by the 
	//@MetroLocationDescriptionMeasurementsMappingsValidation annotation (above - just before class declaration begins)
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the set contains at least one element  
	//We also need to assure that the elements inside HashMap are non-null, and valid
	//this can be handled in two ways:
	//1) write custom validation - see https://stackoverflow.com/questions/27984137/java-beans-validation-collection-map-does-not-contain-nulls
	//2) use the latest Bean Validation API which supports this kind of validation out-of-the-box
	//The problem with 2) is that the latest Dropwizard does not include the latest JSR-303 implementation. The
	//the default Maven dependencies of Dropwizard 1.3.12 are: validation-api-1.1.0.Final.jar. and hibernate-validator-5.4.3.Final.jar 
	//However we need validation-api-2.0.0.Final.jar and hibernate-validator-6.0.2.Final.jar - see: https://www.baeldung.com/javax-validation
	//I've checked and it works (if you add the dependencies for newer version into pom.xml). However then you have dependency
	//conflict (at least shade plugin is complaining). This can be resolved with proper pom.xml but is troublesome. Also you cannot
	//validate your custom constraints - therefore I implemented custom validator/validation and annotated this class with it (above - just before class declaration)
	//TODO: the keys inside the HashMap are enum instances - however JSON representation will contain String (i.e. the label of the 
	//enum instance) - if in JSON there will be a String (label) that does not correspond to any enum instance - then the @JsonCreator in the
	//MetroObservationParameter will return null. However null key in a map is a problem - so even before MetroLocationDescription is deserialized Jackson will complain with JSON processing exception. 
	//Because this happens before validation - the user will not get validation error, instead he will get JSON processing error - and this is
	//not very user friendly...
	private HashMap<MetroObservationParameter, ArrayList<DataSource>> measurementsMappings;
	//private HashMap<MetroObservationParameter, ArrayList<StationLevelSensorId>> measurementsMappings;


	/**
	 * Represents the mapping describing which Metro input forecast parameter is retrieved from 
	 * which data source. This is needed because of the following scenarios:
	 * 1) Certain geographic locations might be "covered" by multiple weather forecast models - therefore Metro module needs to know from
	 * which model to take the forecast. An ArrayList of data sources can be specified - the one with the lower indeks having higher priority
	 * than the one with the higher indeks. The module calculating Metro will try to obtain the data in the order of the priorites (first in 
	 * the ArrayList has highest priority and the Metro module tries to get the data from this data source, then the next one etc.)
	 * 
	 * 2) sometimes the flexibility to take the weather forecasts that are input for Metro calculation at a specific geographic location from 
	 * another location is needed. 
	 * 
	 * NOTE: when specifying forecast mapping only forecasted parameters can be specified as data source (contrary to the measurements mappings - where 
	 * both measurements and forecasts can be specified as a data source). This is logical because measurements can never be measured "in the future".
	 * 
	 * The Metro atmoshperic forecasts are at, td, ra, sn, ws, ap, cc, sf, ir, and fa -
	 * @see https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo) ). 
	 * 
	 * The XML tags for <at>, <td>, <ra>, <sn>, <ws> and <ap> are mandatory input for Metro, while the <cc>, <sf>, <ir>, <fa> are not.
	 * The mappings for <at>, <td>, <ra>, <sn>, <ws> and <ap> are therefore mandatory. Whether the mappings for <cc>, <sf>, <ir>, <fa> need to 
	 * be defined or not depends on the way Metro is run (therefore it depends on the values of {@link #useAnthropogenicFlux}, 
	 * {@link #useInfraredFlux} and {@link #useSolarFlux} variables):
	 * The <cc> XML tag can be missing if both <sf> and <if> XML tags are present in XML, and Metro is run with both 
	 * --use-solarflux-forecast and --use-infrared-forecast options. 
	 * The <sf> XML tag is mandatory only if Metro is run with --use-solarflux-forecast 
	 * The <ir> XML tag is mandatory only if Metro is run with --use-infrared-forecast
	 * The <fa> XML tag is mandatory only if Metro is run with --use-anthropogenic-flux 
	 * 	 
	 * If certain mapping (for <cc>, <sf>, <ir> or <fa> ) is not present in the HashMap this means that the database is not
	 * queried for the forecasts of this parameter and corresponding XML tag will not be included into the weather forecasts XML 
	 * document (Metro input). 
	 * 
	 */	
	//NOTE: the mappings of at, td, ra, sn, ws, ap, cc, sf, ir, and fa to {@link com.cgs.jt.rwis.api.params.ForecastedParameter} parameters SHOULD BE  
	//(by any logical means) STATIC	. For example it would be very controversial to use air temperature at ground as a replacement for air temperature
	//at 150cm above ground. Even worse would be to get values for <at> from ForecastedParameter.WINDSPEED10M - although theoretically it would be possible. 
	//This means that the weather forecasts parameters used to generate Metro forecasts input SHOULD BE AS FOLLOWS (this is enforced by the service 
	//endpoint validation):
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
	//However the different location of different forecast model of the DataSource (on a certain location) is meaningful and allowed! 
	//NOTE: the validation for non-null values of the HashMap and for presence of mandatory <at>, <td>, <ra>, <sn>, <ws> and <ap> and 
	//suitable mappings is done by our custom validator - MetroLocationDescriptionWeatherForecastMappingsValidator - and is triggered by the 
	//@MetroLocationDescriptionWeatherForecastMappingsValidation annotation (above - just before class declaration begins)
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the map is not null
	@NotEmpty //for the validator (packaged with Dropwizard - Hibernate) to check that the map is not empty
	//We also need to assure that the elements inside HashMap are non-null, and valid
	//this can be handled in two ways:
	//1) write custom validation - see https://stackoverflow.com/questions/27984137/java-beans-validation-collection-map-does-not-contain-nulls
	//2) use the latest Bean Validation API which supports this kind of validation out-of-the-box
	//The problem with 2) is that the latest Dropwizard does not include the latest JSR-303 implementation. The
	//the default Maven dependencies of Dropwizard 1.3.12 are: validation-api-1.1.0.Final.jar. and hibernate-validator-5.4.3.Final.jar 
	//However we need validation-api-2.0.0.Final.jar and hibernate-validator-6.0.2.Final.jar - see: https://www.baeldung.com/javax-validation
	//I've checked and it works (if you add the dependencies for newer version into pom.xml). However then you have dependency
	//conflict (at least shade plugin is complaining). This can be resolved with proper pom.xml but is troublesome. Also you cannot
	//validate your custom constraints - therefore I implemented custom validator/validation and annotated this class with it (above - just before class declaration)
	//TODO: the keys inside the HashMap are enum instances - however JSON representation will contain String (i.e. the label of the 
	//enum instance) - if in JSON there will be a String (label) that does not correspond to any enum instance - then the @JsonCreator in the
	//MetroWeatherForecastParameter will return null. However null key in a map is a problem - so even before MetroLocationDescription object is
	//deserialized Jackson will complain with JSON processing exception. Because this happens before validation - the user will not get validation 
	//error, instead he will get JSON processing error - and this is not very user friendly...
	private HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> weatherForecastMappings;
	//private HashMap<MetroWeatherForecastParameter, String> weatherForecastMappings;

	/**
	 * Represents the corrective factor (constant) with which the forecasted solar flux values are multiplied (before 
	 * fed to Metro as input). This multiplication can for example simulate the influence of visible objects (visible horizon) 
	 * near the road station - causing the reduction in the solar flux reaching the station (compared to the solar flux forecasted for the "true horizon" case).
	 * NOTE: If this multiplication needs to be performed or not depends on the way how the Metro model is run. If Metro is run
	 * with --enable-sunshadow then this multiplication should not be done and the factor value is not needed. 
	 * If --use-solarflux-forecast is not enabled then this factor is also not needed.
	 * The value is however mandatory (i.e. even if --use-solraflux-forecast is not enabled or --enable-sunshadow is enabled the factor
	 * must be supplied) - when in doubt set the value to 1 (multiplying by 1 will not hurt).  
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	private Double solarFluxFactor;

	/**
	 * Represents the corrective factor (constant) with which the forecasted infrared flux values are multiplied (before 
	 * fed to Metro as input). This multiplication simulates the micro-location effects/peculiarities that cannot be 
	 * taken into account when generating "generalized" weather forecasts. Incoming infrared flux is different if the road station is
	 * located on open flatland (receiving only the IR from the atmosphere) or if the road station is located in a deep 
	 * gorge (receiving IR from the atmosphere and IR from the mountains).	
	 * NOTE: If --use-infrared-forecast is not enabled then this factor is not needed. The value is however mandatory (i.e. 
	 * even if --use-infrared-forecast is not enabled the factor must be supplied) - when in doubt set the value to 1 (multiplying by 1 will not hurt).  
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	private Double infraredFluxFactor;

	/**
	 * Represents the corrective factor (constant) with which the forecasted anthropogenic flux values are multiplied (before 
	 * fed to Metro as input). The forecasted incoming anthropogenic flux can be different than the measured anthropogenic
	 * flux at the road station for variety of reasons, and this factor represents means to make corrections of the forecasted
	 * values.
	 * NOTE: If --use-anthropogenic-flux is not enabled then this factor is not needed. The value is however mandatory (i.e. 
	 * even if --use-anthropogenic-flux is not enabled the factor must be supplied) - when in doubt set the value to 1 (multiplying by 1 will not hurt).	
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	private Double anthropogenicFluxFactor;

	/**
	 * Represents the flag whether to use --enable-sunshadow option (when Metro is run) or not. 
	 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo)
	 * NOTE: if this option is used the station_configuration.xml must contain horizon desription - @see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo)
	 * NOTE: there exist two methods to compute the shadow. Which will be used can be set with --sunshadow-method option (1=BAsic method, 2=Enhanced method). The default is 1. 
	 */
	//TODO: it is not clear to me if the <sf> tags need to be present in the weather_forecast.xml in order to use --enable-sunshadow (or are <cc> tags enough???)
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null	
	private Boolean enableSunshadow;

	/**
	 * Defines the version of the sunshadow method Metro should use. There exist two methods to compute the shadow - and which  
	 * to use can be set with --sunshadow-method option. 
	 * <li>1 = Basic method (default). It sets solar flux to zero when Sun is below visible horizon</li>
	 * <li>2 = Enhanced method. Replaces global solar flux with its diffuse component when Sun is below visible horizon.</li>
	 * The default (if no --sunshadow-method option is used) is method 1. 
	 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo)
	 * NOTE: it is mandatory to set --enable-sunshadow option in order the --sunshadow-method  takes some effect
	 */
	//for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	//NOTE: due to the implementation (see @JsonCreator in MetroSunshadowMethod) if in JSON there is "unknown" String
	//this will result in null MetroSunshadowMethod - and since it must not be null - the @NotNull validator will report
	//object not valid. This is why we added the "and must be one of the supported sunshadow methods" in the message.
	@NotNull(message = "may not be null and must be one of the supported sunshadow methods")
	private MetroSunshadowMethod sunshadowMethod;


	/**
	 * Represents the flag whether to use --use-anthropogenic-flux option (when Metro is run) or not.
	 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo)
	 * NOTE: if this option is used the input_forecast.xml must contain anthropogenic flux forecasts (XML should contain <fa> tags)
	 * 	
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null	
	private Boolean useAnthropogenicFlux;

	/**
	 * Represents the flag whether to use --use-infrared-forecast option (when Metro is run) or not.
	 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo)
	 * NOTE: if this option is used the input_forecast.xml must contain infrared flux forecasts (XML should contain <ir> tags)
	 * 	
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null	
	private Boolean useInfraredFlux;

	/**
	 * Represents the flag whether to use --use-solarflux-forecast option (when Metro is run) or not.
	 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo)
	 * NOTE: if this option is used the input_forecast.xml must contain solar flux forecasts (XML should contain <sf> tags)
	 * 	
	 */
	@NotNull //for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null	
	private Boolean useSolarFlux;

	/**
	 * Defines the verbosity level of Metro standard output/error. The possible levels are the following: 
	 * <li>0- No log is made of any message</li>
	 * <li>1- Minimal level</li>
	 * <li>2- Normal (default)</li>
	 * <li>3- Full</li>
	 * <li>4- Debug</li>
	 * The default level (if --verbose-level option is not used) is 2 - Normal.	 
	 * @see https://framagit.org/metroprojects/metro/wikis/Man_page_(METRo)
	 * 
	 */
	//for the validator (packaged with Dropwizard - Hibernate) to check that the element is not null
	//NOTE: due to the implementation (see @JsonCreator in MetroVerbosityLevel) if in JSON there is "unknown" String
	//this will result in null MetroVerbosityLevel - and since it must not be null - the @NotNull validator will report
	//object not valid. This is why we added the "and must be one of the supported verbosity levels" in the message.
	@NotNull(message = "may not be null and must be one of the supported verbosity levels")		
	private MetroVerbosityLevel verbosityLevel;







	/**
	 * Constructor with arguments.
	 * 
	 * @param geoLocation The geographic location of this location/road station.
	 * @param type The Metro station type on this location (can be road or bridge).
	 * @param subSurfaceSensorDepth The depth of the sub surface sensor (from which the input measurements for Metro are taken)
	 * @param roadLayers The description of the road layers on this particular location.
	 * @param visibleHorizonDirections The description of the horizon on this particular location.
	 * @param measurementsMappings The mappings from measured parameters to the measurement input of Metro (answering the question: the values of which measurement XML tag are coming from which measured parameter/sensor?).
	 * @param weatherForecastMappings The mappings from weather forecast providers to the forecast input of Metro (answering the question: the values of which forecast XML tag are coming from which weather forecast model?)
	 * @param solarFluxFactor The corrective multiplication factor/constant to correct forecasted solar flux values (since actual solar flux is different than the forecasted solar flux) for this particular location.
	 * @param infraredFluxFactor The corrective multiplication factor/constant to correct forecasted infrared flux values (since actual infrared flux is different than the forecasted infrared flux) for this particular location.
	 * @param anthropogenicFluxFactor The corrective multiplication factor/constant to correct forecasted anthropogenic flux values (since actual anthropogenic flux is different than the forecasted anthropogenic flux) for this particular location.
	 * @param enableSunshadow Defines whether Metro is run with --enable-sunshadow option
	 * @param sunshadowMethod Defines the sunshadow method Metro will use
	 * @param useAnthropogenicFlux Defines whether Metro is run with --use-anthropogenic-flux option
	 * @param useInfraredFlux Defines whether Metro is run with --use-infrared-forecast option
	 * @param useSloarFlux Defines whether Metro is run with --use-solarflux-forecast option
	 * @param verbosityLevel Defines the verbosity level of Metro logger
	 */
	@JsonCreator
	public MetroLocationDescription(
			@JsonProperty("geoLocation") GeographicLocation geoLocation, 
			@JsonProperty("forecastModelId") String forecastModelId,
			@JsonProperty("type") MetroStationType type,
			@JsonProperty("subSurfaceSensorDepth") Double subSurfaceSensorDepth,
			@JsonProperty("roadLayers") TreeSet<Roadlayer> roadLayers,
			@JsonProperty("visibleHorizonDirections") TreeSet<VisibleHorizonDirection> visibleHorizonDirections,
			@JsonProperty("measurementsMappings") HashMap<MetroObservationParameter, ArrayList<DataSource>> measurementsMappings,
			@JsonProperty("weatherForecastMappings") HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> weatherForecastMappings,
			@JsonProperty("solarFluxFactor") Double solarFluxFactor,
			@JsonProperty("infraredFluxFactor") Double infraredFluxFactor,
			@JsonProperty("anthropogenicFluxFactor") Double anthropogenicFluxFactor,
			@JsonProperty("enableSunshadow") Boolean enableSunshadow,
			@JsonProperty("sunshadowMethod") MetroSunshadowMethod sunshadowMethod,
			@JsonProperty("useAnthropogenicFlux") Boolean useAnthropogenicFlux,
			@JsonProperty("useInfraredFlux") Boolean useInfraredFlux,
			@JsonProperty("useSolarFlux") Boolean useSolarFlux,
			@JsonProperty("verbosityLevel") MetroVerbosityLevel verbosityLevel
			) {		
		this.geoLocation = geoLocation;
		this.forecastModelId = forecastModelId;
		this.type = type;
		this.subSurfaceSensorDepth = subSurfaceSensorDepth;
		this.roadLayers = roadLayers;
		this.visibleHorizonDirections = visibleHorizonDirections;
		this.measurementsMappings = measurementsMappings;
		this.weatherForecastMappings = weatherForecastMappings;
		this.solarFluxFactor = solarFluxFactor;
		this.infraredFluxFactor = infraredFluxFactor;
		this.anthropogenicFluxFactor = anthropogenicFluxFactor;
		this.enableSunshadow = enableSunshadow;
		this.sunshadowMethod = sunshadowMethod;	
		this.useAnthropogenicFlux = useAnthropogenicFlux;	
		this.useInfraredFlux = useInfraredFlux;	
		this.useSolarFlux = useSolarFlux;		
		this.verbosityLevel = verbosityLevel;
	}



	/**
	 * Returns the value of {@link #geoLocation} instance variable of the object.
	 * @return The {@link #geoLocation} instance variable.
	 */
	@JsonProperty("geoLocation")//the name in the JSON will be the same as the name of variable (i.e. geoLocation)
	public GeographicLocation getGeoLocation() {
		return this.geoLocation;
	}

	/**
	 * Returns the value of {@link #forecastModelId} instance variable of the object.
	 * @return The {@link #forecastModelId} instance variable.
	 */
	@JsonProperty("forecastModelId")
	public String getForecastModelId() {
		return this.forecastModelId;
	}

	/**
	 * Returns the value of {@link #type} instance variable of the object.
	 * @return The {@link #type} instance variable.
	 */
	@JsonProperty("type")//the name in the JSON will be the same as the name of variable (i.e. type)
	public MetroStationType getType() {
		return this.type;	
	}


	/**
	 * Returns the value of {@link #subSurfaceSensorDepth} instance variable of the object.
	 * @return The {@link #subSurfaceSensorDepth} instance variable.
	 */
	@JsonProperty("subSurfaceSensorDepth")//the name in the JSON will be the same as the name of variable (i.e. subSurfaceSensorDepth)
	public Double getSubSurfaceSensorDepth() {
		return this.subSurfaceSensorDepth;	
	}

	/**
	 * Returns the value of {@link #roadLayers} instance variable of the object.
	 * @return The {@link #roadLayers} instance variable.
	 */
	@JsonProperty("roadLayers")//the name in the JSON will be the same as the name of variable (i.e. roadLayers)
	public TreeSet<Roadlayer> getRoadLayers() {
		return this.roadLayers;	
	}


	/**
	 * Returns the value of {@link #visibleHorizonDirections} instance variable of the object.
	 * @return The {@link #visibleHorizonDirections} instance variable.
	 */
	@JsonProperty("visibleHorizonDirections")//the name in the JSON will be the same as the name of variable (i.e. visibleHorizonDirections)
	public TreeSet<VisibleHorizonDirection> getVisibleHorizonDirections() {
		return this.visibleHorizonDirections;	
	}

	/**
	 * Returns the value of {@link #measurementsMappings} instance variable of the object.
	 * @return The {@link #measurementsMappings} instance variable.
	 */
	@JsonProperty("measurementsMappings")//the name in the JSON will be the same as the name of variable (i.e. measurementsMappings)
	public HashMap<MetroObservationParameter, ArrayList<DataSource>> getMeasurementsMappings() {
		return this.measurementsMappings;	
	}

	/**
	 * Returns the value of {@link #weatherForecastMappings} instance variable of the object.
	 * @return The {@link #weatherForecastMappings} instance variable.
	 */
	@JsonProperty("weatherForecastMappings")//the name in the JSON will be the same as the name of variable (i.e. weatherForecastMappings)
	public HashMap<MetroWeatherForecastParameter, ArrayList<DataSource>> getWeatherForecastMappings() {
		return this.weatherForecastMappings;
	}

	/**
	 * Returns the value of {@link #solarFluxFactor} instance variable of the object.
	 * @return The {@link #solarFluxFactor} instance variable.
	 */
	@JsonProperty("solarFluxFactor")//the name in the JSON will be the same as the name of variable (i.e. solarFluxFactor)
	public Double getSolarFluxFactor() {
		return this.solarFluxFactor;	
	}

	/**
	 * Returns the value of {@link #infraredFluxFactor} instance variable of the object.
	 * @return The {@link #infraredFluxFactor} instance variable.
	 */
	@JsonProperty("infraredFluxFactor")//the name in the JSON will be the same as the name of variable (i.e. infraredFluxFactor)
	public Double getInfraredFluxFactor() {
		return this.infraredFluxFactor;	
	}

	/**
	 * Returns the value of {@link #anthropogenicFluxFactor} instance variable of the object.
	 * @return The {@link #anthropogenicFluxFactor} instance variable.
	 */
	@JsonProperty("anthropogenicFluxFactor")//the name in the JSON will be the same as the name of variable (i.e. anthropogenicFluxFactor)
	public Double getAnthropogenicFluxFactor() {
		return this.anthropogenicFluxFactor;	
	}

	/**
	 * Returns the value of {@link #enableSunshadow} instance variable of the object.
	 * @return The {@link #enableSunshadow} instance variable.
	 */
	@JsonProperty("enableSunshadow")//the name in the JSON will be the same as the name of variable (i.e. enableSunshadow)
	public Boolean getEnableSunshadow() {
		return this.enableSunshadow;	
	}

	/**
	 * Returns the value of {@link #sunshadowMethod} instance variable of the object.
	 * @return The {@link #sunshadowMethod} instance variable.
	 */
	@JsonProperty("sunshadowMethod")//the name in the JSON will be the same as the name of variable (i.e. sunshadowMethod)
	public MetroSunshadowMethod getSunshadowMethod() {
		return this.sunshadowMethod;	
	}	


	/**
	 * Returns the value of {@link #useAnthropogenicFlux} instance variable of the object.
	 * @return The {@link #useAnthropogenicFlux} instance variable.
	 */
	@JsonProperty("useAnthropogenicFlux")//the name in the JSON will be the same as the name of variable (i.e. useAnthropogenicFlux)
	public Boolean getUseAnthropogenicFlux() {
		return this.useAnthropogenicFlux;	
	}	

	/**
	 * Returns the value of {@link #useInfraredFlux} instance variable of the object.
	 * @return The {@link #useInfraredFlux} instance variable.
	 */
	@JsonProperty("useInfraredFlux")//the name in the JSON will be the same as the name of variable (i.e. useInfraredFlux)
	public Boolean getUseInfraredFlux() {
		return this.useInfraredFlux;	
	}		

	/**
	 * Returns the value of {@link #useSolarFlux} instance variable of the object.
	 * @return The {@link #useSolarFlux} instance variable.
	 */
	@JsonProperty("useSolarFlux")//the name in the JSON will be the same as the name of variable (i.e. useSolarFlux)
	public Boolean getUseSolarFlux() {
		return this.useSolarFlux;	
	}	

	/**
	 * Returns the value of {@link #verbosityLevel} instance variable of the object.
	 * @return The {@link #verbosityLevel} instance variable.
	 */
	@JsonProperty("verbosityLevel")//the name in the JSON will be the same as the name of variable (i.e. verbosityLevel)
	public MetroVerbosityLevel getVerbosityLevel() {
		return this.verbosityLevel;	
	}	

}
