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
import com.cgs.jt.rwis.api.params.values.LufftIRS31ProRoadCondition;
import com.cgs.jt.rwis.api.params.values.LufftIRS31ProStateOfCoupling;
import com.cgs.jt.rwis.api.params.values.ThiesClimaUS4920PrecipitationType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Represents an enumeration of all of the parameters (enum instances) that are measured in the CGS's RWIS system (e.g. 
 * parameters that are measured in road weather stations, parameters that are measured with mobile sensors etc...).
 *    
 * Each enum instance is labeled with the human-readable parameter name (e.g. Air temperature in [deg. C] @ Road weather station's height). 
 * For human convenience the name should follow the following naming conventions:
 * 1) Should identify the parameter (what is measured? air temperature or ground temperature?) AND it's units (so when a human 
 * reads the name he should have clear understanding of the semantics of the parameter and the parameter values units).
 * 2) Where possible - should follow the NetCDF naming conventions: NAME @ LAYER - where name specifies the parameter
 * name and the layer identifies where the measurement was taken (at road weather station's height or in a baloon 100m above the road
 * weather station, 5cm below ground or 30cm below ground, etc...). NOTE: some of the measured parameters do not have a meaningfull 
 * layer (e.g. parameter that tells whether the doors of the weather station are closed or not, the supply voltage of the station, etc...).  
 * 3) Should include unit identification after the name.
 * 
 * The name should therefore have the following form: NAME in [UNIT] @ LAYER.
 * for example: Air temperature in [deg. C] @ Road weather station's height)
 * 
 * 
 * NOTE: The names of parameters (i.e. labels of enum instances) MUST BE UNIQUE PER SYSTEM (i.e. when adding the support 
 * for new parameters THE PROGRAMER MUST MAKE SURE that the label used has not already been used for some other parameter. 
 *   
 * @author Jernej Trnkoczy
 *
 */
public enum MeasuredParameter {

	//TODO: min and max values defined in the "old CVIS" are different than the min and max values in the manufacturer
	//sensor specifications! TOREJ JE TREBA ZALOGE VREDNOSTI PRI VSEH PARAMETRIH PONOVNO SMISELNO DOLOCITI! Mislim da
	//so bile te vrednosti dolocene za razmere na Slovenskih cestah - ce imamo sistem za cel svet to ne bo OK!
	//TODO: For each parameter we should define whether the value is average or not, and if average in what time interval!


	/**
	 * Enum instance representing the "Air temperature in [deg. C] @ road weather station's height" parameter.
	 * NOTE: The values associated with this parameter represent the temperature at the time of measurement or an
	 * short-term average (depending on the sensor configuration).
	 */
	AIR_TEMPERATURE_AT_STATION_HEIGHT("Air temperature in [deg. C] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -40.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 60.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}



		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for AIR_TEMPERATURE_AT_STATION_HEIGHT parameter!", e);
			}
		}



		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes air temperature in degrees Celsius as input
			return original;
		}
	},






	/**
	 * Enum instance representing the "Road temperature in [deg. C] @ Ground surface" parameter 
	 * NOTE: The values associated with this parameter represent the temperature at the time of observation or an
	 * short-term average (depending on the sensor configuration).
	 *  	 
	 */
	ROAD_TEMPERATURE_SURFACE("Road temperature in [deg. C] @ Ground surface"){

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


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for ROAD_TEMPERATURE_SURFACE parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes road temperature in degrees Celsius as input
			return original;
		}
	},




	/**
	 * Enum instance representing the "Road temperature in [deg. C] @ 25mm below ground surface" parameter 
	 * NOTE: The values associated with this parameter represent the temperature at the time of observation or an
	 * short-term average (depending on the sensor configuration).
	 *  	 
	 */
	ROAD_TEMPERATURE_25MM("Road temperature in [deg. C] @ 25mm below ground surface"){

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


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for ROAD_TEMPERATURE_25MM parameter!", e);
			}
		}


		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes road temperature 25mm below ground in degrees Celsius as input
			return original;
		}
	},



	/**
	 * Enum instance representing the "Road temperature in [deg. C] @ 50mm below ground surface" parameter 
	 * NOTE: The values associated with this parameter represent the temperature at the time of observation or an
	 * short-term average (depending on the sensor configuration).
	 *  	 
	 */
	ROAD_TEMPERATURE_50MM("Road temperature in [deg. C] @ 50mm below ground surface"){

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


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for ROAD_TEMPERATURE_50MM parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes road temperature 50mm below ground in degrees Celsius as input
			return original;
		}
	},




	/**
	 * Enum instance representing the "Road temperature in [deg. C] @ 300mm below ground surface" parameter 
	 * NOTE: The values associated with this parameter represent the temperature at the time of observation or an
	 * short-term average (depending on the sensor configuration).
	 *  	 
	 */
	ROAD_TEMPERATURE_300MM("Road temperature in [deg. C] @ 300mm below ground surface"){

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


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for ROAD_TEMPERATURE_300MM parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes road temperature 300mm below ground in degrees Celsius as input
			return original;
		}
	},



	//TODO: bo to v redu?
	/**
	 * Enum instance representing the "Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface" parameter 
	 * NOTE: This parameter represents the output of the Lufft IRS31 Pro sensor - and is not a raw measurement because sensor
	 * performs calculation of the freezing point (the resulting value means: if you treated the road with NaCl then the freezing point
	 * would be such - this is because sensor cannot identify what kind of treating material has been used). Values associated with this
	 * parameter represent the freezing point at the time of observation or an short-term average (depending on the sensor configuration).
	 *  	 
	 */
	FREEZING_TEMPERATURE_LUFFT_IRS31PRO_NaCl("Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -25.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */			
			final double maxValue = 0.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for FREEZING_TEMPERATURE_LUFFT_IRS31PRO_NaCl parameter!", e);
			}
		}


		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the freezing temperature is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro freezing point temperature NaCl in [deg. C] @ Road surface is not expected as Metro input!");
		}
	},






	//TODO: bo to v redu?
	/**
	 * Enum instance representing the "Lufft IRS31 Pro freezing point temperature MgCl2 in [deg. C] @ Road surface" parameter 
	 * NOTE: This parameter represents the output of the Lufft IRS31 Pro sensor - and is not a raw measurement because sensor
	 * performs calculation of the freezing point (the resulting value means: if you treated the road with MgCl2 then the freezing point
	 * would be such - this is because sensor cannot identify what kind of treating material has been used). Values associated with this
	 * parameter represent the freezing point at the time of observation or an short-term average (depending on the sensor configuration).
	 *  	 
	 */
	FREEZING_TEMPERATURE_LUFFT_IRS31PRO_MgCl2("Lufft IRS31 Pro freezing point temperature MgCl2 in [deg. C] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -25.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */			
			final double maxValue = 0.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for FREEZING_TEMPERATURE_LUFFT_IRS31PRO_MgCl2 parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the freezing temperature is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro freezing point temperature MgCl2 in [deg. C] @ Road surface is not expected as Metro input!");
		}
	},



	//TODO: bo to v redu?
	/**
	 * Enum instance representing the "Lufft IRS31 Pro freezing point temperature CaCl2 in [deg. C] @ Road surface" parameter 
	 * NOTE: This parameter represents the output of the Lufft IRS31 Pro sensor - and is not a raw measurement because sensor
	 * performs calculation of the freezing point (the resulting value means: if you treated the road with CaCl2 then the freezing point
	 * would be such - this is because sensor cannot identify what kind of treating material has been used). Values associated with this
	 * parameter represent the freezing point at the time of observation or an short-term average (depending on the sensor configuration).
	 *  	 
	 */
	FREEZING_TEMPERATURE_LUFFT_IRS31PRO_CaCl2("Lufft IRS31 Pro freezing point temperature CaCl2 in [deg. C] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -25.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */			
			final double maxValue = 0.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for FREEZING_TEMPERATURE_LUFFT_IRS31PRO_CaCl2 parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the freezing temperature is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro freezing point temperature CaCl2 in [deg. C] @ Road surface is not expected as Metro input!");
		}
	},





	/**
	 * Enum instance representing the "Dew point temperature in [deg. C] @ Road weather station's height" parameter.
	 * NOTE: The values associated with this parameter represent the temperature at the time of measurement or an 
	 * short-term average (depending on the sensor configuration).
	 */
	DEWPOINT_AT_STATION_HEIGHT("Dew point temperature in [deg. C] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -40.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 40.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for DEWPOINT_AT_STATION_HEIGHT parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original){
			//no transformation is needed - Metro takes dew point in degrees Celsius as input
			return original;
		}
	},





	/**
	 * Enum instance representing the "Air temperature in [deg. C] @ Inside the station" parameter.
	 * NOTE: The values associated with this parameter represent the temperature at the time of measurement or 
	 * an short-term average (depending on the sensor configuration)
	 */
	AIR_TEMPERATURE_INSIDE_STATION("Air temperature in [deg. C] @ Inside the road weather station"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = -40.0;

			/**
			 * Defines the expected maximum possible value. 
			 * NOTE: Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 90.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for AIR_TEMPERATURE_INSIDE_STATION parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the air temperature inside station is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Air temperature in [deg. C] @ Inside the road weather station is not expected as Metro input!");
		}
	},





	/**
	 * Enum instance representing the "Air humidity in [%] @ Road weather station's height" parameter.
	 * NOTE: The values associated with this parameter represent the humidity at the time of measurement or 
	 * an short-term average (depending on the sensor configuration).
	 */
	AIR_HUMIDITY_AT_STATION_HEIGHT("Air humidity in [%] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for AIR_HUMIDITY_AT_STATION_HEIGHT parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the air humidity is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Air humidity in [%] @ Road weather station's height is not expected as Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Liquid water film thickness in [mm] @ Road surface" parameter.
	 * NOTE: The values associated with this parameter represent the thickness at the time of measurement or 
	 * an short-term average (depending on the sensor configuration).
	 */
	WATER_FILM_THICKNESS_AT_ROAD_SURFACE("Liquid water film thickness in [mm] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 6.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}



		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for WATER_FILM_THICKNESS_AT_ROAD_SURFACE parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the water film thickness is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Liquid water film thickness in [mm] @ Road surface is not expected as Metro input!");
		}
	},


	//TODO: to je bilo prej Vrsta padavin (SYNOP) - senzor daje subset kod iz WMO tabele 4680 (le da je namesto kode 00 kar koda 0).
	//Standard bo tezko dolocit - npr. v Vaisala dokumentih berem da senzor daje WMO 4680 output plus dolocene kode vzete tudi iz drugih tabel... 
	//npr. 4677 in 4678. Poleg tega zaloga vrednosti ki jih daje Thies Clima ali Vaisala senzor ni kar celotna WMO 4680 tabela (od 00 do 99) 
	//- ampak je subset... Torej mapiranje na nek standard bo tezko - zato sem se odlocil da bo parameter za tocno specificen senzor...
	PRECIPITATION_TYPE_THIESCLIMA_US4920_SURFACE("Precipitation type Thies Clima US4920 [categorical] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {			
			for (ThiesClimaUS4920PrecipitationType pt : ThiesClimaUS4920PrecipitationType.values()) {
				//TODO: I am not sure here but the given Double value must be exactly right (for example 0.000000000000...)
				//if there are some rounding errors - e.g. instead of 0.00000000000000... we test value 0.00000000000000000000000001 the function will return false
				//is this a problem or not???
				if (pt.getDoubleValue().equals(value)) { 
					return true;
				}
			}
			return false;			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			ThiesClimaUS4920PrecipitationType pt = ThiesClimaUS4920PrecipitationType.get(stringVal);
			try {
				return pt.getDoubleValue();
			}
			catch(NullPointerException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for PRECIPITATION_TYPE_THIESCLIMA_US4920_SURFACE parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the precipitation type is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Precipitation type Thies Clima US4920 [categorical] @ Road surface is not expected as Metro input!");
		}

	},



	//TODO: averaged value??
	/**
	 * Enum instance representing the "Precipitation intensity in [mm h^-1] @ Ground surface" parameter.
	 * NOTE: The values associated with this parameter represent the intensity of precipitation averaged 
	 * over ???? period and re-calculated to hourly average.
	 */
	PRECIPITATION_INTENSITY_SURFACE("Precipitation intensity in [mm h^-1] @ Ground surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 150.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}



		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for PRECIPITATION_INTENSITY_SURFACE parameter!", e);
			}
		}



		@Override		
		public Double toMetroMeasurement(Double original){
			//if "Precipitation intensity in [mm h^-1] @ Ground surface" is larger than 0.0 then "Metro Presence of precipitation" 
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



	//TODO: averaged value?? interval???
	//TODO: kako se smer veter loci od smeri sunkov vetra - verjetno drugacno povprecenje....
	/**
	 * Enum instance representing the "Wind in [m s^-1] @ Road weather station's height"
	 * NOTE: The values associated with this parameter represent the wind speed averaged over ????? period.	
	 */
	WIND_SPEED_AT_STATION_HEIGHT("Wind speed in [m s^-1] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for WIND_SPEED_AT_STATION_HEIGHT parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original){
			//wind speed needs transformation from [m s^-1] to [km h^-1] - Metro takes value in kilometers per hour
			return original*3.6;
		}
	},



	//TODO: averaged value?? interval???
	//TODO: kako se smer veter loci od smeri sunkov vetra - verjetno drugacno povprecenje....
	/**
	 * Enum instance representing the "Wind gusts in [m s^-1] @ Road weather station's height"
	 * NOTE: The values associated with this parameter represent the wind gusts speed averaged over ???? period.	
	 */
	WIND_GUSTS_AT_STATION_HEIGHT("Wind gusts speed in [m s^-1] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 100.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for WIND_GUSTS_AT_STATION_HEIGHT parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the wind gusts speed is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Wind gusts speed in [m s^-1] @ Road weather station's height is not expected as Metro input!");
		}
	},



	//TODO: kako se smer veter loci od smeri sunkov vetra - verjetno drugacno povprecenje....
	/**
	 * Enum instance representing the "Wind direction in [arc degree] @ Road weather station's height"
	 * NOTE: The values associated with this parameter represent the wind direction at the time of observation (not average).	
	 */
	WIND_DIRECTION_AT_STATION_HEIGHT("Wind direction in [arc degree] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 			 
			 */
			final double maxValue = 360.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}



		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for WIND_DIRECTION_AT_STATION_HEIGHT parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the wind direction is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Wind direction in [arc degree] @ Road weather station's height is not expected as Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Air pressure in [hPa] @ Road weather station's height"
	 * NOTE: The values associated with this parameter represent the air pressure at the time of observation or averaged
	 * over a short time range (depends on sensor configuration).	
	 */
	AIR_PRESSURE_AT_STATION_HEIGHT("Air pressure in [hPa] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 800.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 1200.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for AIR_PRESSURE_AT_STATION_HEIGHT parameter!", e);
			}
		}


		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the air pressure (as observation) is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Air pressure in [hPa] @ Road weather station's height is not expected as Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Snow depth in [cm] @ Ground surface"
	 * NOTE: The values associated with this parameter represent the snow depth at the time of observation (not average).	
	 */
	SNOW_DEPTH("Snow depth in [cm] @ Ground surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 300.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for SNOW_DEPTH parameter!", e);
			}
		}


		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the snow depth is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Snow depth in [cm] @ Ground surface is not expected as Metro input!");
		}
	},




	//TODO: to je bilo prej Stanje vozisca (ROAD) - verjetno senzor daje subset ROAD kod?? Kaj sploh so to ROAD kode?	
	//Standard bo tezko dolocit - ker verjetno razlicni senzorji malo po svoje te kode dajejo, poleg tega zaloga vrednosti ni 
	//pri vseh enaka. Torej mapiranje na nek standard bo tezko - zato sem se odlocil da bo parameter za tocno specificen senzor...
	/**
	 * Enum instance representing the "Lufft IRS31pro road condition in [categorical] @ Road surface" parameter
	 * NOTE: The values associated with this parameter represent the road condition at the time of observation or averaged
	 * over a short time range (depends on sensor configuration).	
	 */
	ROAD_CONDITION_LUFFT_IRS31PRO("Lufft IRS31pro road condition in [categorical] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {			
			for (LufftIRS31ProRoadCondition rc : LufftIRS31ProRoadCondition.values()) {
				//TODO: I am not sure here but the given Double value must be exactly right (for example 0.000000000000...)
				//if there are some rounding errors - e.g. instead of 0.00000000000000... we test value 0.00000000000000000000000001 the function will return false
				//is this a problem or not???
				if (rc.getDoubleValue().equals(value)) { 
					return true;
				}
			}
			return false;			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			LufftIRS31ProRoadCondition rc = LufftIRS31ProRoadCondition.get(stringVal);
			try {
				return rc.getDoubleValue();
			}
			catch(NullPointerException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for ROAD_CONDITION_LUFFT_IRS31PRO parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws FailedToTransformIntoMetroInputException{
			//Lufft IRS31PRO sensor's output (0=DRY, 1=MOIST, 2=WET, 3=ICE, 4=SNOW, 5=RESIDUAL_SALT, 6=FREEZING_WET, 7=CRITICAL)
			//needs to be converted to SSI code (33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43)
			//NOTE: on https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) one can read:
			//"All road condition values are considered wet except for 33 which is the SSI code for dry road" - which I do not 
			//understand completely - but it seems that 0=DRY must be translated into 33, and all others (1,2,3,4,5,6,7) can
			//be translated into one of (34, 35, 36, 37, 38, 39, 40, 41, 42, 43) - which one is not really important???

			//NOTE: according to Rok Krsmanc - class ZapisiOpazovanjaXML.java - the transformation is the following
			if(original.equals(0.0)) {
				return 33.0;
			}
			else if(original.equals(1.0)) {
				return 34.0;
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
				return 40.0;
			}
			//NOTE: the sensor output 7 (CRITICAL road state) will be mapped to SSI code 40. In the "old CVIS" this is missing...
			else if(original.equals(7.0)) {
				return 40.0;
			}
			//NOTE: the sensor outputs 98 and 99 (which mean that sensor cannot decide on the road state) cannot be transformed into SSI code...
			else {
				throw new FailedToTransformIntoMetroInputException("The value of "+ original+"representing the Lufft IRS31pro road condition in [categorical] @ Road surface parameter cannot be transformed into appropriate Metro input!");
			}
		}
	},



	//TODO: dokoncaj!
	//Zakaj je v stari CVIS tabeli "Vidljivost v casu padavin"??? Zakaj v casu padavin??
	//Ker senzor? Po kakšnem standardu - WMO? Zaloga vrednosti?
	/**
	 * Enum instance representing the ""Visibility by WMO standard in [m] @ Road weather station's height"" parameter
	 * NOTE: The values associated with this parameter represent the visibility at the time of observation or averaged
	 * over a short time range (depends on sensor configuration).	
	 */
	VISIBILITY_WMO("Visibility by WMO standard in [m] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 50000.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for VISIBILITY_WMO parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the visibility is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Visibility by WMO standard in [m] @ Road weather station's height is not expected as Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Weather station supply voltage in [V]"
	 * NOTE: The values associated with this parameter represent the voltage at the time of observation (not average).	
	 */
	STATION_SUPPLY_VOLTAGE("Weather station supply voltage in [V]"){
		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 240.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for STATION_SUPPLY_VOLTAGE parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the station supply voltage is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Weather station supply voltage in [V] is not expected as Metro input!");
		}
	},



	//TODO: V CVIS je samo "koncentracija soli" - predvidevam da gre za NaCl
	/**
	 * Enum instance representing the "Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface" parameter.
	 * NOTE: The values associated with this parameter represent the NaCl concentration at the time of observation (not average).	
	 */
	SALINE_CONCENTRATION_LUFFT_IRS31PRO_NaCl("Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface"){
		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 100.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for SALINE_CONCENTRATION_LUFFT_IRS31PRO_NaCl parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the saline concentration is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro - NaCl saline concentration in [%] @ Road surface is not expected as Metro input!");
		}
	},




	/**
	 * Enum instance representing the "Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface" parameter.
	 * NOTE: The values associated with this parameter represent the MgCl2 concentration at the time of observation (not average).	
	 */
	SALINE_CONCENTRATION_LUFFT_IRS31PRO_MgCl2("Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface"){
		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 100.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for SALINE_CONCENTRATION_LUFFT_IRS31PRO_MgCl2 parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the saline concentration is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro - MgCl2 saline concentration in [%] @ Road surface is not expected as Metro input!");
		}
	},



	/**
	 * Enum instance representing the "Lufft IRS31 Pro - CaCl2 saline concentration in [%] @ Road surface" parameter.
	 * NOTE: The values associated with this parameter represent the CaCl2 concentration at the time of observation (not average).	
	 */
	SALINE_CONCENTRATION_LUFFT_IRS31PRO_CaCl2("Lufft IRS31 Pro - CaCl2 saline concentration in [%] @ Road surface"){
		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 100.0;	

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for SALINE_CONCENTRATION_LUFFT_IRS31PRO_CaCl2 parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the saline concentration is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro - CaCl2 saline concentration in [%] @ Road surface is not expected as Metro input!");
		}
	},





	//TODO: averaged value?? over 10 minutes interval??? 
	/**
	 * Enum instance representing the "10 minutes precipitation accumulation in [mm] @ Ground surface" parameter.
	 * NOTE: The values associated with this parameter represent the accumulation in the last 10 minutes.
	 */
	PRECIPITATION_ACCUMULATION_10MIN("10 minutes precipitation accumulation in [mm] @ Ground surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 15.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for PRECIPITATION_ACCUMULATION_10MIN parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the precipitation accumulation is not expected Metro input.
			throw new NotConvertableToMetroInputException("The 10 minutes precipitation accumulation in [mm] @ Ground surface is not expected as Metro input!");
		}
	},


	//TODO: kako se smer sunkov vetra loci od smeri vetra - verjetno drugacno povprecenje....
	/**
	 * Enum instance representing the "Wind gust direction in [arc degree] @ Road weather station's height"
	 * NOTE: The values associated with this parameter represent the average wind gust in ???? period.	
	 */
	WIND_GUST_DIRECTION_AT_STATION_HEIGHT("Wind gust direction in [arc degree] @ Road weather station's height"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 			
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 			 
			 */
			final double maxValue = 360.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for WIND_GUST_DIRECTION_AT_STATION_HEIGHT parameter!", e);
			}
		}


		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the wind gust direction accumulation is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Wind gust direction in [arc degree] @ Road weather station's height is not expected as Metro input!");
		}
	},




	//TODO: po starem je bilo to "Stanje talnega senzorja", ali sem pravilno pogruntal da gre za "state of coupling" od
	//Lufft IRS31Pro in da je zaloga vrednosti 0, 1 ali 2??
	STATE_OF_COUPLING_LUFFT_IRS31PRO("Lufft IRS31 Pro sensor state of coupling [categorical]"){
		@Override
		public boolean checkValue(Double value) {			
			for (LufftIRS31ProStateOfCoupling sc : LufftIRS31ProStateOfCoupling.values()) {
				//TODO: I am not sure here but the given Double value must be exactly right (for example 0.000000000000...)
				//if there are some rounding errors - e.g. instead of 0.00000000000000... we test value 0.00000000000000000000000001 the function will return false
				//is this a problem or not???
				if (sc.getDoubleValue().equals(value)) { 
					return true;
				}
			}
			return false;			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			LufftIRS31ProStateOfCoupling sc = LufftIRS31ProStateOfCoupling.get(stringVal);
			try {
				return sc.getDoubleValue();
			}
			catch(NullPointerException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for STATE_OF_COUPLING_LUFFT_IRS31PRO parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the state of coupling is not expected Metro input.
			throw new NotConvertableToMetroInputException("The Lufft IRS31 Pro sensor state of coupling [categorical] is not expected as Metro input!");
		}
	},



	//TODO: averaged and estimated - or measured at time of observation? 
	/**
	 * Enum instance representing the "24 hours precipitation accumulation in [mm] @ Ground surface" parameter.
	 * NOTE: The values associated with this parameter represent the accumulation in the last 24 hours.
	 */
	PRECIPITATION_ACCUMULATION_24H("24 hours precipitation accumulation in [mm] @ Ground surface"){

		@Override
		public boolean checkValue(Double value) {
			/**
			 * Defines the expected minimum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double minValue = 0.0;

			/**
			 * Defines the expected maximum possible value. 
			 * Based on the data retrieved from the old RWIS system (i.e. from the Measurement database table)
			 */
			final double maxValue = 1000.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for PRECIPITATION_ACCUMULATION_24H parameter!", e);
			}
		}

		@Override		
		public Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException{
			//the 24h precipitation accumulation is not expected Metro input.
			throw new NotConvertableToMetroInputException("The 24 hours precipitation accumulation in [mm] @ Ground surface is not expected as Metro input!");
		}
	};

	//############################## ALL PARAMETERS UP UNTIL NOW ARE MEASURED ON CGS ROAD WEATHER STATIONS #################################################################################################
	/*
	//TODO: dokoncaj! Ker senzor? Enote? Morda kategoricno - kera kodna tabela? Zaloga vrednosti?
	CHEMICAL_FACTOR_VAISALA_DRS511("Vaisala DRS511 chemical factor in [????????????????] @ Road surface"){

	},



	//TODO: dokoncaj!
	//to je kao Vrsta padavin (NSI) al kaj?? Ker senzor? Kera WMO tabela? Zaloga vrednosti?
	PRECIPITATION_TYPE_NSI_SURFACE("Precipitation type NSI in [categorical - see WMO code table ????, or maybe ????, or maybe ????] @ Road surface"){

	},


	//TODO: dokoncaj!
	//Kuga je to? Ker senzor? Po kakšnem standardu - WMO? Zaloga vrednosti?
	STOPNJA_PADAVIN("Stopnja padavin po standardu in tabeli ????? in [v ?????????] @ Road weather station's height"){

	},

	//TODO: dokoncaj!
	//to je kao Stanje vozisca (CVIS) al kaj?? Ker senzor? Kera kodna tabela? Zaloga vrednosti?
	ROAD_CONDITION_VAISALA_xxxx("Vaisala XXXXX road condition in [categories ?????????????] @ Road surface"){

	},

	//TODO: dokoncaj!
	//to je kao Vrsta padavin (VAISALA) al kaj?? Ker senzor? Kera WMO tabela? Zaloga vrednosti?
	PRECIPITATION_TYPE_VAISALA_SURFACE("Precipitation type VAISALA in [categorical - see WMO code table ????, or maybe ????, or maybe ????] @ Road surface"){

	},


	//TODO: dokoncaj!
	//to je kao Vrata omarice? Ker senzor?  Zaloga vrednosti?
	WEATHER_STATION_DOORS_STATE("Weather station doors state [categorical - vrednosti ????]"){

	},


	//TODO: in CVIS the name is "Soncno sevanje" - but what exactly is it? Which sensor measures it? Is it average?
	//does it take into account that weather station can be in the gorge (i.e. does it measure 180degree angle)?

	SOLAR_FLUX_SURFACE("Downwards solar radiation flux in [W m^-2] @ Ground surface"){

		@Override
		public boolean checkValue(Double value) {


			final double minValue = 0.0;


			//TODO: For the maximum measured value we can make the following speculation: The maximum solar flux cannot 
			//exeed 1367W/m2 (that is a solar constant- solar flux received at the upper bound of our atmosphere) - 
			//see https://www.e-education.psu.edu/eme812/node/644 . SO WHY IS THE UPPER BOUND IN CVIS 9000 THEN???
			final double maxValue = 9000.0;

			if(value>=minValue && value<=maxValue) {
				return true;
			}
			else {
				return false;
			}			
		}


		@Override		
		public Double getDoubleFromString(String stringVal) throws StringValueFormatException {
			try {				 
				return Double.valueOf(stringVal);
			}
			catch(NumberFormatException e) {
				throw new StringValueFormatException("Invalid string value "+stringVal+ " for SOLAR_FLUX_SURFACE parameter!", e);
			}
		}
	},



	//TODO: dokoncaj!
	//to je kao Vrsta padavin (INCA) al kaj?? Pa kuga je to - ker senzor? Kera WMO tabela? Zaloga vrednosti?
	PRECIPITATION_TYPE_INCA_SURFACE("Precipitation type INCA in [categorical - see WMO code table ????, or maybe ????, or maybe ????] @ Road surface"){

		@Override
		public boolean checkValue(Double value) {
			return true;
		}
	};

	 */










	/**
	 * Represents a label that is given to each of the enum instances - as to be able to retrieve the enum instance
	 * by the label.
	 */
	private final String  label;

	/**
	 * Stores a map of key-value pairs - where keys are label strings and values are the enum instances/constants 
	 * associated with this name
	 */
	private static final Map<String,MeasuredParameter> ENUM_MAP;

	/**
	 * Constructor of enumeration
	 * @param The label associated with the enum instance.
	 */
	private MeasuredParameter(String label) {
		this.label = label;	
	}

	/**
	 * Get the label of the enum instance (i.e. the value of {@link #label} instance variable).
	 * @return The label of the enum instance (i.e. the value of {@link #label} instance variable)
	 */
	//annotation to get the label in JSON (instead of default behavior) when serializing to JSON - see:
	//https://www.baeldung.com/jackson-serialize-enums
	//
	@JsonValue
	public String getLabel() {
		return this.label;
	}


	// Build an immutable map of String label to enum instance pairs.
	static {
		Map<String,MeasuredParameter> map = new ConcurrentHashMap<String, MeasuredParameter>();
		for (MeasuredParameter instance : MeasuredParameter.values()) {
			//we should not allow two instances to have the same label!
			//standard map behavior is to replace the value - but this should not happen!
			if(!map.containsKey(instance.getLabel())) {
				map.put(instance.getLabel(),instance);
			}
			else {
				throw new IllegalArgumentException("The labels (i.e. names) of the measured parameters should all be different!");
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
	public static MeasuredParameter get(String label) {
		return ENUM_MAP.get(label);
	}




	/**
	 * Abstract method that each enum instance must implement. The method checks if the given value is a "meaningful"
	 * value (i.e. it is within the expected range) for this parameter. Used to filter the outlier values 
	 * (for example air temperature -400 deg C is not "meaningful" and the method should return false). It is upon the 
	 * implementer to define the rules for value filtering (which values are normal and allowed and which not).
	 * 
	 * @return Returns true if the provided value is "meaningful", false otherwise.
	 */
	public abstract boolean checkValue(Double val);

	/**
	 * Abstract method that each enum instance must implement. The method converts the measured value into the value
	 * that is suitable for metro measurement input. For example if we measure the road condition with Lufft IRS31PRO sensor
	 * and we want to use it's output (0=DRY, 1=MOIST, 2=WET, 3=ICE, 4=SNOW, 5=RESIDUAL_SALT, 6=FREEZING_WET, 7=CRITICAL)
	 * as input for Metro (<sc> tag in XML) - which expects road condition as SSI code (33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43)
	 * then we need appropriate transformation (which is performed by the implementation of this function).
	 * 
	 * NOTE: if a certain parameter cannot be meaningfully transformed into one of the Metro observations input (see 
	 * https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) ) then the implementation of this function should 
	 * just throw a NotConvertableToMetroInputException exception. The FailedToTransofrmIntoMetroInputException exception should be thrown
	 * when during the transformation of the given value an exception happens and the value cannot be transformed.
	 * @return Returns the transformed value.
	 */
	//TODO: the question here remains how do we know to which metro measurement tag a certain measurement is allowed to map. Because it is
	//the allowed mapping that should define the toMetroMeasurement() implementation. For example if it is allowed to get the values
	//for air temperature measurement (metro <at> tags) from wind speed measurement - then the implementation of the function would be rather complex :)! 
	//The current mappings that are allowed are enforced by the MetroLocationDescription validation on the subscription service endpoint. However if
	//somebody pushes a MetroLocationDescription with a "strange" mapping directly to database (not using service) - then we might get a strange
	//behavior!
	public abstract Double toMetroMeasurement(Double original) throws NotConvertableToMetroInputException, FailedToTransformIntoMetroInputException;




	/**
	 * Abstract method that each enum instance must implement. The method converts the given input "String value of the parameter"
	 * into a "Double value of the parameter". The "Double value of the parameter" is the one that is stored in the database. 
	 * For parameters that are normally presented as Double value (e.g. Temperature, Wind speed, Humidity etc...) the implementation
	 * of this method is straightforward - it should use Double.valueOf(String).
	 * For parameters that represent "categorical data" the appropriate mappings to Double should be defined. For example Vaisala FD12P
	 * sensor can be configured to output NWS Codes for precipitation type. The codes are C, P, L, R, S, IP, A etc. (code meanings are:
	 * No precipitation, Precipitation, Drizzle, Rain, Snov, Ice pellets, Hail etc.). Every code can be followed by +, nothing or -
	 * (e.g. S+ means heavy snow, S- means light snow, and S means moderate snow). If we want to store values of this parameter as Double
	 * values (in the database) all the "textual codes" need to be mapped to (different) numbers. Therefore a mapping function 
	 * needs to be defined (throwing StringValueFormatException if the input "String value" cannot be mapped to "Double value" ).
	 * 
	 * @return Returns a Double representation of the input String value.
	 * @throws StringValueFormatException
	 */
	//decided to use a custom StringValueFormatException instead NumberFormatException - because NumberFormatException is
	//runtime exception (does not requiere try-catch) - but I want to force the user to check!
	//see also: https://stackoverflow.com/questions/7205624/why-numberformatexception-is-runtime
	public abstract Double getDoubleFromString(String stringVal) throws StringValueFormatException;


}
