/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.params;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cgs.jt.rwis.api.exc.FailedToTransformIntoMetroInputException;
import com.cgs.jt.rwis.api.exc.NotConvertableToMetroInputException;
import com.cgs.jt.rwis.api.exc.StringValueFormatException;
import com.cgs.jt.rwis.api.params.values.IncaPrecipitationType;
import com.cgs.jt.rwis.api.params.values.LufftIRS31ProRoadCondition;
import com.cgs.jt.rwis.metro.inoutvalues.MetroModelRoadCondition;
import com.cgs.jt.rwis.metro.inoutvalues.MetroOctalCloudCoverage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Represents definition of all of the forecasted parameters (enum instances) that are used in RWIS system.
 * Some of the parameters might be weather parameters (e.g. Temperature in [deg. C] @ 150cm above ground etc...) while others
 * might be road parameters (e.g. Temperature in [deg. C] @ road surface, Quantity of snow in [cm] @ road surface, etc...) - 
 * however all these parameters are "forecasted" (and not measured). 
 *   
 * Each enum instance is labeled with the human-readable parameter name (e.g. Temperature in [deg. C] @ 150cm above ground). 
 * For human convenience the name should follow the following naming conventions:
 * 1) Should identify the parameter AND it's units (so when a human reads the name he should have clear 
 * understanding of the semantics of the parameter and the parameter values units).
 * 2) Should follow the NetCDF naming conventions: NAME @ LAYER - where name specifies the parameter
 * name and the layer identifies the z-axis (in a 3-dimensional grid data).  
 * 3) Should include unit identification after the name.
 * 
 * The name should therefore have the following form: NAME in [UNIT] @ LAYER.
 * for example: "Temperature in [deg. C] @ 150cm above ground" 
 * 
 * NOTE: All of the defined parameters MUST have the definition of the "layer" (i.e. vertical dimension). In other
 * words - the parameters without defined layer are not allowed in this RWIS system! 
 * 
 * NOTE: The names of parameters (i.e. labels of enum instances) MUST BE UNIQUE PER SYSTEM (i.e. when adding the support 
 * for new parameters THE PROGRAMER MUST MAKE SURE that the label used has not already been used for some other parameter. 
 *   
 * @author Jernej Trnkoczy
 *
 */
//NOTE: This list of parameters just provides the definition of the parameters that are used in RWIS system - so we have a clear 
//understanding what they are. The list by itself does not have any effect on the functionality of the system - for example one
//cannot assume that the forecast data for these parameters is actually stored in the system database (this may be or may be not true).
public enum ForecastedParameter {

	/**
	 * Enum instance representing the "Temperature in [deg. C] @ 150cm above ground" parameter.
	 * NOTE: The values associated with this parameter represent the temperature at the time of forecast (not average).
	 * 
	 * NOTE: This parameter represents mandatory Metro input - the requirements for the forecasts of this parameter are therefore:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).
	 * 
	 */
	AIRTEMPERATURE150CM("Temperature in [deg. C] @ 150cm above ground"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible air temperature 150cm above ground in deg. C. 
			 * Based on the lowest air temperature recorded on planet Earth (-98C = 175.15K) - see http://fortune.com/2018/06/25/lowest-temperature-earth/.
			 */
			final double minValue = -100.0;

			/**
			 * Defines the expected maximum possible air temperature 150cm above ground in deg. C. Based on the highest air temperature recorded on planet
			 * Earth (supposedly in Dasht-e Loot, Iran=70.7C=343.85K).
			 * See https://en.wikipedia.org/wiki/Atmospheric_pressure#Records.			 
			 */
			final double maxValue = 75.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		//NOTE: the air temperature forecasts can be used as a replacement for input Metro measurements for:
		//1) measured air temperature 
		//2) in case of road stations of type bridge - can be used as road sub-surface temperature
		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes air temperature degrees Celsius as input (in both cases described above)
			return original;
		}
	},


	/**
	 * Enum instance representing the "Temperature in [deg. C] @ Ground or water surface" parameter.
	 * NOTE: The values associated with this parameter represent the ground temperature at the time of forecast (not average).
	 * NOTE: This parameter is not needed for Metro input - therefore there are no (Metro-related) restrictions on time interval between forecasts. 
	 */
	GROUNDTEMPERATURE("Temperature in [deg. C] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible temperature at ground or water surface level expressed in degrees Celsius. 	 
			 * Based on the lowest air temperature recorded on planet Earth (-98C = 175.15K) - see http://fortune.com/2018/06/25/lowest-temperature-earth/.
			 * and ground is never colder than air...			 
			 */
			final double minValue = -100.0;

			/**
			 * Defines the expected maximum possible temperature at ground or water surface level expressed in degrees Celsius.
			 * Based on the highest ground temperature recorded on planet Earth (93.9C = 367.05K).
			 * See https://rmets.onlinelibrary.wiley.com/doi/pdf/10.1002/j.1477-8696.2001.tb06577.x			 
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		//NOTE: the air temperature at ground level forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Temperature in [deg. C] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Dew point temperature in [deg. C] @ 150cm above ground"
	 * NOTE: The values associated with this parameter represent the dew point at the time of forecast (not average).
	 * 
	 * NOTE: This parameter represents mandatory Metro input - the requirements for the forecasts of this parameter are therefore:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).	 
	 * 
	 */
	DEWPOINT150CM("Dew point temperature in [deg. C] @ 150cm above ground"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible dew point temperature at 150cm above ground expressed in deg. Celsius. 	 
			 * Lowest recorded dew point temperature on planet Earth is hard to find so we will set -150 (reasoning: dew point is always lower than the air temp - and 
			 * the lowest recorded air temperature (-98C = 175.15K) - see http://fortune.com/2018/06/25/lowest-temperature-earth/
			 */
			final double minValue = -150.0;

			/**
			 * Defines the expected maximum possible dew point temperature at 150cm above ground expressed in deg. Celsius.
			 * Based on the highest Dew point temperature recorded on planet Earth (35 °C = 95 °F = 308.15K).
			 * See https://en.wikipedia.org/wiki/Dew_point 				 
			 */
			final double maxValue = 40.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}

		//NOTE: the dew point temperature forecasts can be used as a replacement for measured dew point temperature (for Metro input)
		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes dew point temperature in degrees Celsius as input
			return original;
		}
	},


	/**
	 * Enum instance representing the "Relative air humidity [%] @ 200cm above ground"
	 * 
	 * NOTE: The values associated with this parameter represent the relative humidity at the time of forecast (not average).
	 *
	 * NOTE: This parameter is not needed for Metro input - therefore there are no (Metro-related) restrictions on time interval between forecasts.	  
	 */
	RELATIVEHUMIDITY200CM("Relative air humidity [%] @ 200cm above ground"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible relative humidity in percentage.			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible relative humidity in percentage.			
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}

		//NOTE: the relative humidity forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Relative air humidity [%] @ 200cm above ground is not expected as a replacement for measurements Metro input!");
		}
	},




	/**
	 * Enum instance representing the "Wind in [km h^-1] @ 10m above ground"
	 * NOTE: The values associated with this parameter represent the wind speed at the time of forecast (not average).
	 * 
	 * NOTE: This parameter represents mandatory Metro input - the requirements for the forecasts of this parameter are therefore:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).
	 *
	 */
	WINDSPEED10M("Wind in [km h^-1] @ 10m above ground"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible wind speed 10m above ground.			 
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible wind speed 10m above ground. Based on the highest wind speed 
			 * recorded on planet Earth (408km/h=113.3m/s). See: https://en.wikipedia.org/wiki/Wind_speed			  
			 */
			final double maxValue = 420.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}

		//NOTE: the wind speed forecasts can be used as a replacement for measured wind speed (for Metro input)
		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes wind speed in km/h as input
			return original;
		}
	},



	/**
	 * Enum instance representing the "Rain precipitation rate in [mm h^-1] @ Ground or water surface"
	 * 
	 * The values associated with this parameter represent the average rain precipitation rate in the PREVIOUS 
	 * time interval (i.e. if the value is forecasted at 13h then the value represents average value of rain precipitation
	 * rate from previous time offset to 13h (e.g. from 12h to 13h if the time interval between forecasted values is 1h). 
	 * The value is therefore calculated by taking accumulated rainfall in the last time interval divided by the duration 
	 * of the time interval.
	 * 
	 * NOTE: This parameter represents mandatory Metro input. However the values cannot be used in Metro directly - because 
	 * Metro requires accumulated quantity since the beginning of the forecast. A transformation is therefore required. 
	 * The requirements for the forecasts of this parameter are therefore:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).	  	
	 */
	RAINPRECIPITATIONSURFACE("Rain precipitation rate in [mm h^-1] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the minimum possible rain precipitation rate. Negative values are of course not acceptable.			 
			 */
			final double minValue = 0.0;

			/**
			 * Defines the maximum possible rain precipitation rate. It is difficult to estimate the maximum possible rain 
			 * precipitation rate - see https://earthscience.stackexchange.com/questions/5169/is-there-an-upper-bound-to-the-amount-of-rain-that-can-fall-in-an-hour
			 * The value depends also on the averaging interval (most intense precipitation lasts only seconds) - but the 
			 * interval is arbitrary in our case. So all we can do is to be conservative and put the limit according to 
			 * the highest measured rain precipitation quantity in one minute (3cm/min = 1860mmm/hour) recorded
			 * so far. 			 
			 */
			final double maxValue = 1860.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}

		}


		//NOTE: the rain precipitation forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Rain precipitation rate in [mm h^-1] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Snow precipitation rate [cm h^-1] @ Ground or water surface"
	 * The values associated with this parameter represent the average snow precipitation rate in the PREVIOUS time 
	 * interval (i.e. if the value is forecasted at 13h then the value represents average value of rain precipitation 
	 * rate from previous time offset to 13h (e.g. from 12h to 13h if the time interval between forecasted values is 1h). 
	 * The value is therefore calculated by taking accumulated snow fall in the last time interval divided by the duration
	 * of the time interval.
	 *
	 * NOTE: This parameter represents mandatory Metro input. However the values cannot be used in Metro directly - because 
	 * Metro requires accumulated quantity since the beginning of the forecast. A transformation is therefore required. 
	 * The requirements for the forecasts of this parameter are therefore:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).
	 */
	SNOWPRECIPITATIONSURFACE("Snow precipitation rate [cm h^-1] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the minimum possible snow precipitation rate. Negative values are of course not acceptable.
			 * Used to filter outlier values.
			 */
			final double minValue = 0.0;

			/**
			 * Defines the maximum possible snow precipitation rate. It is difficult to estimate the maximum possible 
			 * snow precipitation rate - see https://earthscience.stackexchange.com/questions/5169/is-there-an-upper-bound-to-the-amount-of-rain-that-can-fall-in-an-hour
			 * The value depends also on the averaging interval (most intense precipitation lasts only seconds) - but the 
			 * interval is arbitrary in our case. So all we can do is to be very conservative and put an imaginative 
			 * (however common sense) value 100cm/hour for the maximum.			 
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}

		}

		//NOTE: the snow precipitation forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Snow precipitation rate [cm h^-1] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Total precipitation rate in [mm h^-1] @ Ground or water surface"
	 * 
	 * The values associated with this parameter represent the average total (i.e. rain + snow + anything else) precipitation 
	 * rate in the PREVIOUS time interval (i.e. if the value is forecasted at 13h then the value represents average value of total
	 * precipitation rate from previous time offset to 13h (e.g. from 12:30h to 13h if the time interval between forecasted values is 30min). 
	 * The value is therefore calculated by taking accumulated rainfall in the last time interval divided by the duration 
	 * of the time interval.
	 * 
	 * NOTE: This parameter is not needed for Metro input - therefore there are no (Metro-related) restrictions on time interval between forecasts.
	 * 		
	 */
	TOTALPRECIPITATIONSURFACE("Total precipitation rate in [mm h^-1] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the minimum possible total precipitation rate. Negative values are of course not acceptable.			 
			 */
			final double minValue = 0.0;

			/**
			 * Defines the maximum possible total precipitation rate. It is difficult to estimate the maximum possible total 
			 * precipitation rate - see https://earthscience.stackexchange.com/questions/5169/is-there-an-upper-bound-to-the-amount-of-rain-that-can-fall-in-an-hour
			 * The value depends also on the averaging interval (most intense precipitation lasts only seconds) - but the 
			 * interval is arbitrary in our case. So all we can do is to be conservative and put the limit according to 
			 * the highest measured rain (assumption made here is that the highest total precipitation rate occurs when it rains - not when it snows)
			 * precipitation quantity in one minute (3cm/min = 1860mmm/hour) recorded
			 * so far. 			 
			 */
			final double maxValue = 1860.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}

		}


		//NOTE: the total precipitation forecasts can be used as a replacement for measured presence of precipitation (for Metro input)
		@Override		
		public Double toMetroMeasurement(Double original){
			//if "Total precipitation rate in [mm h^-1] @ Ground or water surface" is larger than 0.0 then "Metro Presence of precipitation" 
			//is 1 (meaning that precipitations are present) otherwise the the "Metro Presence of precipitation" 
			//is 0 (meaning there is no precipitations).
			if(original>0.0) {
				return 1.0;
			}
			else {
				return 0.0;
			}			
		}
	},




	/**
	 * Enum instance representing the 
	 * "Precipitation type in [0=no, 1=rain, 2=rain with snow, 3=snow, 4=frozen rain] @ Ground or water surface"
	 * 
	 * NOTE: The values associated with this parameter represent the precipitation type at the time of forecast (not average).
	 * 
	 * NOTE: The associated values do not have units - because this is categorical data and can take one of the following 
	 * possible values:
	 * 0=no precipitation 
	 * 1=rain 
	 * 2=rain with snow
	 * 3=snow
	 * 4=frozen rain
	 * 
	 * NOTE: This parameter is not needed for Metro input - therefore there are no (Metro-related) restrictions on time interval between forecasts. 
	 */
	PRECIPITATIONTYPEINCASURFACE("INCA precipitation type in [categorical] @ Ground or water surface"){		

		@Override
		public boolean checkValue(Double value) {			
			for (IncaPrecipitationType pt : IncaPrecipitationType.values()) {
				//TODO: I am not sure here but the given Double value must be exactly one of 0.000000000000 , 1.000000000000, 2.000000000000, 3.0000000000000000000 or 4.000000000000000000000000000
				//if there are some rounding errors - e.g. instead of 0.00000000000000000000000 we test value 0.00000000000000000000000001 the function will return false
				//so is this a problem or not???
				if (pt.getDoubleValue().equals(value)) { 
					return true;
				}
			}
			return false;			
		}


		//NOTE: the precipitation type forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted INCA precipitation type in [categorical] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},


	/**
	 * Enum instance representing the "Pressure in [Pa] @ Ground or water surface" (i.e. atmospheric pressure at the locations's
	 * height). The values associated with this parameter represent the pressure at the time of forecast (not average).
	 * 
	 * NOTE: This parameter represents mandatory Metro input - the requirements for the forecasts of this parameter are therefore:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).
	 * 
	 * NOTE: Metro takes values with units in [mb] (not in [Pa]) - a transformation is therefore required.
	 * 
	 * 	 
	 */
	PRESSURESURFACE("Pressure in [Pa] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible pressure at location's height. Determining the lower 
			 * bound is difficult because theoretically some customers might be interested in the pressure on top
			 * of Mount Everest (where pressure is roughly around 30kPa). 
			 */
			final double minValue = 30000.0;

			/**
			 * Defines the expected maximum possible MSL pressure. Based on the highest MSL pressure ever recorded on planet
			 * Earth (108480Pa). See https://en.wikipedia.org/wiki/Atmospheric_pressure#Records.			  
			 */
			final double maxValue = 110000.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}

		//NOTE: the air pressure forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Pressure in [Pa] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},




	/**
	 * Enum instance representing the "Downwards infrared radiation flux in [W m^-2] @ Ground or water surface" 
	 * (i.e. thermal = infrared = long-wave radiation flux density coming from the atmosphere downwards to the 
	 * surface and measured at ground or water surface). 
	 * 
	 * NOTE: The values associated with this parameter represent average energy flux density in the NEXT time interval.
	 * For example if the value is forecasted at 13h then the value represents average value in the time interval from 
	 * 13h to the next forecast time offset - e.g. from 13h to 14h if the time interval between forecasted values is 1h.
	 * 
	 * NOTE: The forecasted values for this parameter are calculated for "true horizon" - i.e. the "conditions at the location" 
	 * (e.g. if the location is in a deep gorge) are not taken into account, and the forecasted values do not reflect the fact
	 * that the "visible horizon" is (on majority of locations) not the same as "true horizon". 
	 * 
	 * NOTE: This parameter represents optional Metro input. If Metro is run with --use-solarflux-forecast then the requirements
	 * for this parameter are: 
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).	 
	 *  	 
	 */
	INFRAREDFLUXSURFACE("Downwards infrared radiation flux in [W m^-2] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the minimum possible downwards infrared flux density. The flux is always directed
			 * downwards - so negative values are not acceptable.			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the maximum possible downwards infrared flux density (ever measured on planet Earth).
			 * For the maximum measured value I could not find reliable data - so I defined the upper bound by the following 
			 * speculation:
			 * 1) maximum infrared flux cannot be bigger than maximum solar flux (because we would rather build 
			 * infrared-flux-powered electricity powerplants).
			 * 2) the maximum solar flux cannot exeed 1367W/m2 (this is solar constant - solar flux received at the upper 
			 * bound of our atmosphere) - see https://www.e-education.psu.edu/eme812/node/644
			 * 3) so the maximum infrared flux should be smaller than 1367W/m2 ;)! 			 
			 */
			//TODO: find out the maximum measured value from a reliable source and specify the upper bound.
			final double maxValue = 1367.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}

		//NOTE: the infrared flux forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Downwards infrared radiation flux in [W m^-2] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Downwards solar radiation flux in [W m^-2] @ Ground or water surface"
	 * i.e. short-wave radiation flux coming from the sun downwards to the surface and measured at ground 
	 * or water surface.
	 * 
	 * NOTE: The values associated with this parameter represent average energy flux density in the NEXT time interval.
	 * For example if the value is forecasted at 13h then the value represents average value in the time interval from
	 * 13h to the next forecast time offset - e.g. from 13h to 14h if the time interval between forecasted values is 1h.
	 * 
	 * NOTE: The forecasted values for this parameter are calculated for "true horizon" - i.e. the "conditions at the location" 
	 * (e.g. if the location is in a deep gorge) are not taken into account, and the forecasted values do not reflect the fact
	 * that the "visible horizon" is (on majority of locations) not the same as "true horizon". 
	 * 
	 * NOTE: This parameter represents optional Metro input. If Metro is run with --use-infrared-forecast then the requirements 
	 * for this parameter are: 
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).
	 *    	 
	 */
	SOLARFLUXSURFACE("Downwards solar radiation flux in [W m^-2] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {

			/**
			 * Defines the minimum possible downwards solar flux density. The flux is always directed
			 * downwards - so negative values are not acceptable.			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the maximum possible downwards solar flux density (ever measured on planet Earth).
			 * For the maximum measured value I could not find reliable data - so I defined the upper bound by 
			 * the following speculation: The maximum solar flux cannot exeed 1367W/m2 (that is a solar constant
			 * - solar flux received at the upper bound of our atmosphere) - see https://www.e-education.psu.edu/eme812/node/644 
			 */
			//TODO: find out the maximum measured value at ground level from a reliable source and set the upper bound.
			final double maxValue = 1367.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}

		//NOTE: the solar flux forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Downwards solar radiation flux in [W m^-2] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},




	/**
	 * Enum instance representing the "Downwards anthropogenic radiation flux in [W m^-2] @ Ground or water surface"
	 * i.e. radiation flux coming from human sources downwards to the road surface and measured at ground 
	 * or water surface.
	 * 
	 * NOTE: The values associated with this parameter represent average energy flux density in the NEXT time interval.
	 * For example if the value is forecasted at 13h then the value represents average value in the time interval from
	 * 13h to the next forecast time offset - e.g. from 13h to 14h if the time interval between forecasted values is 1h.
	 * 
	 * NOTE: The forecasted values for this parameter are calculated for "true horizon" - i.e. the "conditions at the location" 
	 * (e.g. if the location is in a deep gorge) are not taken into account, and the forecasted values do not reflect the fact
	 * that the "visible horizon" is (on majority of locations) not the same as "true horizon". 
	 * 
	 * NOTE: This parameter represents optional Metro input. If Metro is run with --use-anthropogenic-flux then the requirements 
	 * for this parameter are:
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).	 
	 */
	ANTHROPOGENICFLUXSURFACE("Downwards anthropogenic radiation flux in [W m^-2] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {

			/**
			 * Defines the minimum possible downwards solar flux density. The flux is always directed
			 * downwards - so negative values are not acceptable.			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the maximum possible downwards anthropogenic flux density.
			 * For the maximum measured value I could not find reliable data - so I defined the upper bound by 
			 * the following speculation: Assuming that anthropogenic flux is always smaller than the maximum solar flux 
			 * (which is 1367W/m2 - a solar constant = solar flux received at the upper bound of our atmosphere - see 
			 * https://www.e-education.psu.edu/eme812/node/644 ) - let's set the maximum value to 1000.0
			 */
			//TODO: find out the maximum measured value at ground level from a reliable source and set the upper bound.
			final double maxValue = 1000.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}


		//NOTE: the anthropogenic flux forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Downwards anthropogenic radiation flux in [W m^-2] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},





	/**
	 * Enum instance representing the "Total cloud cover in [%] @ Ground or water surface"
	 * 
	 * NOTE: The values associated with this parameter represent the cloud coverage at the time of forecast (not average).
	 * 
	 * NOTE: This parameter represents optional Metro input. If Metro is NOT run with both --use-solarflux-forecast and --use-infrared-forecast
	 * then the requirements for this parameter are: 
	 * 1) the forecasting period must be smaller or equal to an hour (e.g. weather forecasts where forecasted values are 
	 *     at ...14:00, 16:00, 18:00h,... are not supported by Vedra)
	 * 2) The period (in minutes) must be a whole number divider of 60 (e.g. 30, 20, 15, 10, 6, 5, 4, 3, 2, 1)
	 * 3) The forecasted values must be "synchronized" to "full hour" (e.g. forecasted values at ...12:00, 12:15, 12:30, 12:45, 13:00,... 
	 *    represent a valid dataset, however forecasted values at ...12:02, 12:17, 12:32, 12:47, 13:03... do NOT!).	  	 
	 */
	CLOUDCOVERAGE("Total cloud cover in [%] @ Ground or water surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible cloud coverage in percentage.			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible cloud coverage in percentage.			
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		//NOTE: the cloud cover in % forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Total cloud cover in [%] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}

	},



	/**
	 * Enum instance representing the "Octal cloud cover in [categorical] @ Ground or water surface". The Octal cloud coverage is cloud 
	 * coverage expressed in octals. The transformation from % to octal is the following:
	 * 0-10% = 0 octal
	 * 11-21% = 1 octal
	 * 22-32% = 2 octal
	 * 33-43% = 3 octal
	 * 44-54% = 4 octal
	 * 55-65% = 5 octal
	 * 66-76% = 6 octal
	 * 77-87% = 7 octal
	 * 88-100% = 8 octal		 
	 * 
	 * NOTE: The values associated with this parameter represent the octal cloud coverage at the time of forecast (not average).
	 * 
	 * NOTE: This is (usually) a Metro output. The cloud coverage needed for Metro input is obtained from {@link #CLOUDCOVERAGE}.  
	 * 
	 * 	 
	 */
	//NOTE: The octal representation of cloud coverage is used for example in Metro input - https://framagit.org/metroprojects/metro/-/wikis/Input_forecast_(METRo)
	//and is given as Metro output as well - https://framagit.org/metroprojects/metro/-/wikis/Output_roadcast_(METRo). Note however that in our system
	//the octal cloud coverage Metro input is always obtained from the "Total cloud cover in [%] @ Ground or water surface" (see the 
	//retrieveAndCreateWeatherForecastXML() method of the com.cgs.jt.rwis.rfm.cgsmetro01.SingleLocationRoadcastThread class where the transformation
	//from % into octal is done). In other words the weather forecast models need to provide "Total cloud cover in [%] @ Ground or water surface" 
	//and the "Octal cloud coverage [categorical] @ Ground or water surface" parameter is defined here only to capture Metro output (because it is 
	//somehow impossible to do the reverse transformation from octal to % - if it would be possible than we would not need this parameter at all...).
	OCTALCLOUDCOVERAGE("Octal cloud cover in [categorical] @ Ground or water surface"){
		@Override
		public boolean checkValue(Double value) {
			for (MetroOctalCloudCoverage oc : MetroOctalCloudCoverage.values()) {
				//TODO: I am not sure here but the given Double value must be exactly one of 0.000000000000 , 1.000000000000, 2.000000000000, 3.0000000000000000000 etc..
				//if there are some rounding errors - e.g. instead of 0.00000000000000000000000 we test value 0.00000000000000000000000001 the function will return false
				//so is this a problem or not???
				if (oc.getDoubleValue().equals(value)) { 
					return true;
				}
			}
			return false;	
		}


		//NOTE: the octal cloud cover forecasts are not expected as a replacement for any of the Metro measurements input
		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			throw new NotConvertableToMetroInputException("The forecasted Octal cloud cover in [categorical] @ Ground or water surface is not expected as a replacement for measurements Metro input!");
		}
	},




	/**
	 * Enum instance representing the "Road temperature in [deg. C] @ Ground surface"
	 * 
	 * NOTE: The values associated with this parameter represent the road temperature at the time of forecast (not average).
	 * 
	 * NOTE: This is (usually) a Metro output. There may however be other models that produce this kind of forecast. 
	 * 	 
	 */	
	ROADTEMPERATURESURFACE("Road temperature in [deg. C] @ Ground surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -25.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table) - 
			 * however 10deg C higher value was taken - to adopt the case when the measurement is taken on the bridge.
			 */			
			final double maxValue = 80.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}

		//NOTE: the road temperature forecasts can be used as a replacement for measured road temperature (for Metro input).
		//NOTE: even if the road temperature forecasts are coming from Metro model - we can use them iteratively - we provide the Metro
		//with some initial values and then use it's output for the measurement input for the next Metro run.
		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - the unit is Celsius
			return original;
		}
	},
	
	
	
	/**
	 * Enum instance representing the "Road temperature in [deg. C] @ 400mm below ground surface"
	 * 
	 * NOTE: The values associated with this parameter represent the road temperature at the time of forecast (not average).
	 * 
	 * NOTE: This is (usually) a Metro output. There may however be other models that produce this kind of forecast. 
	 * 	 
	 */	
	ROADTEMPERATURE400MM("Road temperature in [deg. C] @ 400mm below ground surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -25.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table) - 
			 * however 10deg C higher value was taken - to adopt the case when the measurement is taken on the bridge.
			 */			
			final double maxValue = 80.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}

		//NOTE: the road temperature forecasts can be used as a replacement for measured road sub-surface temperature (for Metro input).
		//NOTE: even if the road temperature forecasts are coming from Metro model - we can use them iteratively - we provide the Metro
		//with some initial values and then use it's output for the measurement input for the next Metro run.
		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - the unit is Celsius
			return original;
		}
	},
	
	
	


	/**
	 * Enum instance representing the "Metro road condition in [categorical] @ Road surface"
	 * Possible values are 1,2,3,4,5,6,7,8 - @see  https://framagit.org/metroprojects/metro/wikis/Road_condition_(METRo)#Criteria_for_determination_of_the_road_condition
	 * 
	 * NOTE: The values associated with this parameter represent the road condition at the time of forecast (not average).
	 * 
	 * NOTE: This is (usually) a Metro output. There may however be other models that produce this kind of forecast. 
	 * 	 
	 */	
	ROADCONDITIONMETRO("Metro road condition in [categorical] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {			
			for (MetroModelRoadCondition rc : MetroModelRoadCondition.values()) {
				//TODO: I am not sure here but the given Double value must be exactly right (for example 0.000000000000...)
				//if there are some rounding errors - e.g. instead of 0.00000000000000... we test value 0.00000000000000000000000001 the function will return false
				//is this a problem or not???
				if (rc.getDoubleValue().equals(value)) { 
					return true;
				}
			}
			return false;			
		}


		//NOTE: if we do not have the road sensor capable of measuring road conditions then we could run Metro iteratively - provide some initial values 
		//and after that use the calculated Metro road condition forecasts as input for the Metro measurements (i.e. <sc> measurements tags) in the next runs
		//of the Metro model. Therefore we must make a transformation from the Metro output <rc> tag - see https://framagit.org/metroprojects/metro/-/wikis/Output_roadcast_(METRo)
		//to the Metro input <sc> tag (which has the value of SSI code) - see https://framagit.org/metroprojects/metro/-/wikis/Input_observation_(METRo)
		@Override		
		public Double toMetroMeasurement(Double original) throws FailedToTransformIntoMetroInputException{
			//Metro output (i.e. <rc> tag (1=DRY, 2=WET, 3=ICE/SNOW, 4=WATER/SNOW, 5=DEW, 6=MELTING SNOW, 7=FROST, 8=ICING RAIN)
			//needs to be converted to SSI code (33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43) - which is Metro input (i.e. <sc> tag)
			//see: https://framagit.org/metroprojects/metro/-/wikis/Road_condition_(METRo)#Criteria_for_determination_of_the_road_condition
			//TODO: these mappings could maybe be improved... Because half of the metro output cannot be mapped to SSI codes - as can be seen 
			//here - https://framagit.org/metroprojects/metro/-/wikis/Road_condition_(METRo)#Criteria_for_determination_of_the_road_condition
			if(original.equals(1.0)) {
				return 33.0;
			}
			else if(original.equals(2.0)) {
				return 34.0;
			}
			else if(original.equals(3.0)) {
				return 35.0;
			}
			else if(original.equals(4.0)) {
				return 35.0;
			}
			else if(original.equals(5.0)) {
				return 34.0;
			}
			else if(original.equals(6.0)) {
				return 35.0;
			}
			else if(original.equals(7.0)) {
				return 40.0;
			}
			else if(original.equals(8.0)) {
				return 35.0;
			}
			else {
				throw new FailedToTransformIntoMetroInputException("The value of "+ original+"representing the Metro road condition in [categorical] @ Road surface parameter cannot be transformed into appropriate Metro measurement input!");
			}
		}
	};




	/**
	 * Represents a label that is given to each of the enum instances - as to be able to retrieve the enum instance
	 * by the label.
	 */
	private final String  label;

	/**
	 * Stores a map of key-value pairs - where keys are label strings and values are the enum instances/constants 
	 * associated with this name
	 */
	private static final Map<String,ForecastedParameter> ENUM_MAP;

	/**
	 * Constructor of enumeration
	 * @param The label associated with the enum instance.
	 */
	private ForecastedParameter(String label) {
		this.label = label;	
	}

	//annotation to get the label in JSON (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums	
	@JsonValue
	public String getLabel() {
		return this.label;
	}


	// Build an immutable map of String label to enum instance pairs.
	static {
		Map<String,ForecastedParameter> map = new ConcurrentHashMap<String, ForecastedParameter>();
		for (ForecastedParameter instance : ForecastedParameter.values()) {
			//we should not allow two instances to have the same label!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getLabel())) {
				map.put(instance.getLabel(),instance);
			}
			else {
				throw new IllegalArgumentException("The labels (i.e. names) of the forecasted parameters should all be different!");
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
	public static ForecastedParameter get(String label) {
		return ENUM_MAP.get(label);
	}




	/**
	 * Abstract method that each enum instance must implement. The method converts the forecasted value into the value
	 * that is suitable for metro measurement input. For example in some cases where there are no sensors on the station
	 * we are taking the replacement values from the weather forecasts.
	 * 
	 * 
	 * NOTE: if a certain parameter is not expected to be transformed into the appropriate Metro observations input (see 
	 * https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) ) then the implementation of this function should 
	 * just throw a NotConvertableToMetroInputException exception. The FailedToTransofrmIntoMetroInputException exception should be thrown
	 * when during the transformation of the given value an exception happens and the value cannot be transformed.
	 * @return Returns the transformed value.
	 */
	//TODO: the question here remains how do we know to which metro measurement tag a certain weather forecast is allowed to map. Because it is
	//the allowed mapping that should define the toMetroMeasurement() implementation. For example if it is allowed to get the replacement values
	//for air temperature measurement (metro <at> tags) from wind speed forecast - then the implementation of the function would be rather complex :)! 
	//The current mappings that are allowed are enforced by the MetroLocationDescription validation on the subscription service endpoint. However if
	//somebody pushes a MetroLocationDescription with a "strange" mapping directly to database (not using service) - then we might get a strange
	//behavior!
	public abstract Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException, FailedToTransformIntoMetroInputException;



	/**
	 * Abstract method that each enum instance must implement. The method checks if the given value is a "meaningful"
	 * value (i.e. it is within the expected range) for this parameter. Used to filter the outlier values 
	 * (for example temperature -400 deg C is not "meaningful" and the method should return false). It is upon the 
	 * implementer to define the rules for value filtering (which values are normal and allowed and which not).
	 * 
	 * @return Returns true if the provided value is "meaningful", false otherwise.
	 */
	public abstract boolean checkValue(Double val);


}
