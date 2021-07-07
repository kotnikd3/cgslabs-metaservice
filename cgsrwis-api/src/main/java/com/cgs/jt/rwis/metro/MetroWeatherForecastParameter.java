/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.metro;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines the possible Metro weather forecast parameters (which are at, td, ra, sn, ws, ap, cc, sf, ir, fa)  along with their 
 * human readable names.
 * @see https://framagit.org/metroprojects/metro/wikis/Input_forecast_(METRo)
 *   
 * @author Jernej Trnkoczy
 */
public enum MetroWeatherForecastParameter { 
	/**
	 * Enum instance representing the <at> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Temperature in [deg. C] @ 150cm above ground" parameter at the time of measurement (not a long-term average).
	 */
	AT("Metro weather forecast <at> = Temperature in [deg. C] @ 150cm above ground"),	


	/**
	 * Enum instance representing the <td> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Dew point temperature in [deg. C] @ 150cm above ground" parameter at the time of measurement (not a long-term average).
	 */
	TD("Metro weather forecast <td> = Dew point temperature in [deg. C] @ 150cm above ground"),
	
	
	/**
	 * Enum instance representing the <ra> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Rain precipitation accumulation since beginning of the forecast in [mm] @ Ground or water surface" parameter. The value
	 * represents the accumulated quantity of the rain since the "beginning of the forecast" (i.e. from the time in the first
	 * <prediction> XML tag in the weather forecast input for Metro - the <ra> element in the first <prediction> tag therefore
	 * always contains 0.0 value).
	 */
	RA("Metro weather forecast <ra> = Rain precipitation accumulation since beginning of the forecast in [mm] @ Ground or water surface"),

	
	/**
	 * Enum instance representing the <sn> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Snow precipitation accumulation since beginning of the forecast in [cm] @ Ground or water surface" parameter. The value
	 * represents the accumulated quantity of the snow since the "beginning of the forecast" (i.e. from the time in the first
	 * <prediction> XML tag in the weather forecast input for Metro - the <sn> element in the first <prediction> tag therefore
	 * always contains 0.0 value).
	 */
	SN("Metro weather forecast <sn> = Snow precipitation accumulation since beginning of the forecast in [cm] @ Ground or water surface"),

	
	/**
	 * Enum instance representing the <ws> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Wind in [km h^-1] @ 10m above ground" parameter at the time of measurement (not a long-term average).
	 */
	WS("Metro weather forecast <ws> = Wind in [km h^-1] @ 10m above ground"),
	
	
	/**
	 * Enum instance representing the <ap> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Pressure in [mb] @ Ground or water surface" parameter at the time of measurement (not a long-term average).
	 */
	AP("Metro weather forecast <ap> = Pressure in [mb] @ Ground or water surface"),
	
	
	/**
	 * Enum instance representing the <cc> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Total cloud cover in [octal = 0..8] @ Ground or water surface" parameter at the time of measurement (not a long-term average).
	 */
	CC("Metro weather forecast <cc> = Total cloud cover in [octal = 0..8] @ Ground or water surface"),	
	
	
	/**
	 * Enum instance representing the <sf> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Downwards solar radiation flux in [W m^-2] @ Ground or water surface" parameter.
	 * 
	 * NOTE: The values associated with this parameter represent average energy flux density in the NEXT HOUR.
	 * For example if the value is given for time 13:00h it represents average value in the time interval from
	 * 13:00h to 14:00h (the time interval between Metro weather forecasts input is always 1 hour).
	 * 
	 * NOTE: The location for which Metro road forecast is calculated can be for example in a deep gorge or under the trees, buildings etc.
	 * In this situation the "visible horizon" is not the same as "true horizon". The visible obstacles (mountains, trees, buildings)
	 * obstruct the sun and influence the received amount of solar flux. However the weather forecasts usually cannot take such 
	 * micro-location specific facts into account, and forecasted solar flux values are therefore usually supplied for a "true horizon".
	 * In case of solar radiation (mainly short wavelength solar radiation = UV + visible light) the influence of the "visible obstacles"
	 * on solar radiation flux is complex since the position of the sun in the sky changes over day and solar radiation is highly 
	 * directional (note: there is some indirect solar radiation too). For example the solar radiation when the sun is just below visible 
	 * horizon is very small, however when the sun rises above horizon the radiation is immediately very high. Therefore multiplying 
	 * the "true horizon" forecast with a constant corrective factor (obtained for a specific location) is not optimal. To correct these
	 * forecasted values Metro (only later versions) provides the "sunshadow" algorithm. 
	 * However for this algorithm to work the "skyview description" input needs to be provided as well, and Metro needs to be run 
	 * with the --enable-sunshadow option. If the "skyview description" data is not available for particular location the less accurate
	 * approach of multiplying the forecasted solar flux values by a constant corrective factor (before supplying the value as input
	 * to Metro) can be used. 
	 * THEREFORE THE VALUES THAT NEED TO BE PROVIDED IN THE <sf> TAG DEPEND ON HOW THE METRO MODEL IS BEING RUN. IF IT IS RUN WITH THE
	 * --enable-sunshadow OPTION THEN THE RAW "TRUE HORIZON" FORECASTED VALUES NEED TO BE PROVIDED. IF NOT THEN THE VALUES NEED TO BE
	 * CORRECTED WITH CORRECTIVE FACTOR BEFORE THEY ARE PROVIDED AS INPUT FOR METRO. 
	 * 
	 */
	SF("Metro weather forecast <sf> = Downwards solar radiation flux in [W m^-2] @ Ground or water surface"),
	
	
	/**
	 * Enum instance representing the <ir> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Downwards infrared radiation flux in [W m^-2] @ Ground or water surface" parameter.
	 * 
	 * NOTE: The values associated with this parameter represent average energy flux density in the NEXT HOUR.
	 * For example if the value is given for time 13:00h it represents average value in the time interval from
	 * 13:00h to 14:00h (the time interval between Metro weather forecasts input is always 1 hour).
	 * 
	 * NOTE: The location for which Metro road forecast is calculated can be for example in a deep gorge or under the trees, buildings etc.
	 * In this situation the "visible horizon" is not the same as "true horizon". The visible obstacles (mountains, trees, buildings)
	 * influence the received infrared flux. However the weather forecasts usually cannot take such micro-location specific facts into 
	 * account, and forecasted infrared flux values are therefore usually supplied for a "true horizon". In case of infrared radiation
	 * flux (infrared energy emitted by the Earth's athmosphere) the influence of the "visible obstacles" is not very complex, since it 
	 * does not change over the course of the day. Therefore the forecasted infrared flux values need to be multiplied by a constant
	 * corrective factor (for particular location) before supplying them as input to Metro. 
	 * 
	 */
	IR("Metro weather forecast <ir> = Downwards infrared radiation flux in [W m^-2] @ Ground or water surface"),
	
	
	/**
	 * Enum instance representing the <fa> tag in Metro weather forecast XML. This tag should be populated with values representing
	 * "Anthropogenic heat flux in [W m^-2] @ Ground or water surface" parameter.
	 * 
	 * NOTE: The values associated with this parameter represent average energy flux density in the NEXT HOUR.
	 * For example if the value is given for time 13:00h it represents average value in the time interval from
	 * 13:00h to 14:00h (the time interval between Metro weather forecasts input is always 1 hour).
	 * 
	 * NOTE: The location for which Metro road forecast is calculated can be for example in a deep gorge or under the trees, buildings etc.
	 * In this situation the "visible horizon" is not the same as "true horizon". The visible obstacles (mountains, trees, buildings)
	 * influence the received anthropogenic flux. However the weather forecasts usually cannot take such micro-location specific facts into 
	 * account, and forecasted anthropogenic flux values are therefore usually supplied for a "true horizon". In case of anthropogenic
	 * radiation flux (energy emitted from man-mande sources) the influence of the "visible obstacles" is not very complex, since it 
	 * does not change over the course of the day. Therefore the forecasted anthropogenic flux values need to be multiplied by a constant
	 * corrective factor (for particular location) before supplying them as input to Metro. 
	 * 
	 */
	FA("Metro weather forecast <fa> = Anthropogenic heat flux in [W m^-2] @ Ground or water surface");
	



	/**
	 * Represents a label that is given to each of the enum instances - as to be able to retrieve the enum instance
	 * by the label.
	 */
	private final String  label;

	/**
	 * Stores a map of key-value pairs - where keys are label strings and values are the enum instances/constants 
	 * associated with this name
	 */
	private static final Map<String,MetroWeatherForecastParameter> ENUM_MAP;

	/**
	 * Constructor of enumeration
	 * @param The label associated with the enum instance.
	 */
	private MetroWeatherForecastParameter(String label) {
		this.label = label;	
	}

	/**
	 * Get the label of the enum instance (i.e. the value of {@link #label} instance variable).
	 * @return The label of the enum instance (i.e. the value of {@link #label} instance variable)
	 */
	//annotation to get the label in JSON (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums
	@JsonValue
	public String getLabel() {
		return this.label;
	}


	// Build an immutable map of String label to enum instance pairs.
	static {
		Map<String, MetroWeatherForecastParameter> map = new ConcurrentHashMap<String, MetroWeatherForecastParameter>();
		for (MetroWeatherForecastParameter instance : MetroWeatherForecastParameter.values()) {
			//we should not allow two instances to have the same label!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getLabel())) {
				map.put(instance.getLabel(),instance);
			}
			else {
				throw new IllegalArgumentException("The labels (i.e. names) of the Metro weather forecast parameters should all be different!");
			}			
		}
		ENUM_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * To retrieve the enum instance by the given label...
	 * @param label The label for which we want to retrieve the enum instance.
	 * @return The enum instance associated with the given label or null if no enum instance is associated with the given label.
	 */
	//when deserializing from JSON to enum - the label should be used to create enum instance. see:
	//https://www.libmonk.com/java-enum-serialization-deserialization-jackson-api/
	//https://medium.com/@ryanbrookepayne/deserializing-to-a-java-enum-type-975d8cf01ac4
	@JsonCreator
	public static MetroWeatherForecastParameter get(String label) {
		return ENUM_MAP.get(label);
	}
}
