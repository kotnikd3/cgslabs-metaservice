/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.utils;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.TreeMap;

import com.cgs.jt.rwis.api.exc.ForecastExtractionException;

import ucar.ma2.Array;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis1DTime;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

/**
 * Provides utility methods for reading data from grib files.  
 *
 * @author  Jernej Trnkoczy
 * 
 */
public class RawGribData {


	/**
	 * Represents the reference time of the forecast. This is the absolute time (representing time when the forecast 
	 * model has been run). The individual forecasts are then referencing this time with relative time offset values
	 * (e.g. +1h, +2h, +3h...). To avoid problems with different time zones etc. the reference time is stored as
	 * milliseconds/nanoseconds after epoch (in a form of the {@link java.time.Instant} (Java 8 or later) object).
	 * 
	 */
	//see https://stackoverflow.com/questions/26142864/how-to-get-utc0-date-in-java-8
	private Instant forecastReferenceTime;



	/**
	 * Represents a {@link java.util.TreeMap} of key-value pairs. The keys represent the time offsets of the forecasts 
	 * (e.g. +1h, +2h, +3h... however expressed as absolute times in a form of millis after epoch. The values 
	 * represent the actual extracted (raw) forecast values.
	 */
	private TreeMap<Instant, Double> forecastedValues;	




	/**
	 * Constructor which retrieves the raw forecasted values of a given three dimensional weather parameter 
	 * for the grid point that is the closest to the given latitude/longitude position from a given grib file.
	 * The values for all time offsets along the time dimension of variable are extracted (e.g. values
	 * at +1h, +2h, +3h, +4h....). The extracted raw values are then assigned to the instance variables of 
	 * this class. The instance variables are left null if: 
	 * <ul>
	 *   <li>the variable is not 3D variable</li>
	 *   <li>the variable cannot be found in given GridDataset</li>
	 *   <li>the given lat/lon location is outside the variable grid</li>	    
	 * </ul>
	 * NOTE: The constructor can be used only for three dimensional parameters (i.e. NetCDF variables with time, X, and Y coordinates)!
	 * NOTE: The indexes of Y and X (i.e. the grid point) are automatically calculated from given latitude/longitude position. The projection 
	 * of the grid (e.g. LamberConformal, Latitude/Longitude etc.) is taken into account automatically.
	 * 
	 * @param gds The {@link ucar.nc2.dt.grid.GridDataset} dataset (the gridded data from which to extract).
	 * @param netCdfVariableId The ID of the NetCDF variable.
	 * @param latitude The latitude of the point-of-interest (the geographic location for which we want to get the values). The closest 
	 * point on grid is then calculated automatically. The value must be given in degrees with decimal fraction (not minutes, seconds...).
	 * @param longitude The longitude of the point-of-interest (the geographic location for which we want to get the values). The closest
	 * point on grid is then calculated automatically. The value must be given in degrees with decimal fraction. 
	 *  	 
	 */
	public RawGribData(GridDataset gds, String netCdfVariableId, double latitude, double longitude) throws ForecastExtractionException { 
		GridDatatype gridDtp = gds.findGridDatatypeByAttribute("Grib_Variable_Id", netCdfVariableId);
		if(gridDtp!=null) {			
			//get coordinate system of the specific NetCDF variable (assuming this variable contains gridded data)
			GridCoordSystem gcs =  gridDtp.getCoordinateSystem();			
			//use this coordinate system to find the closest grid point to the given latitude/longitude location
			//NOTE: the projection used in grib file (e.g. Lambert Conformal, lat/lon, etc...) is automatically taken into account
			int[] gridClosestPointIndexes = new int[2];
			//returns index value or -1 if the given geographic position is outside of the grid
			//NOTE: outside the grid means if it is outside for more than a half of the delta between two grid points
			gcs.findXYindexFromLatLon(latitude, longitude, gridClosestPointIndexes);
			int indexInYDimension = gridClosestPointIndexes[1];
			int indexInXDimension = gridClosestPointIndexes[0];	
			if(indexInYDimension!=-1 && indexInXDimension!=-1) {
				//get the time axis and obtain the needed info about this axis (reference time, units of offset time, etc...)
				CoordinateAxis1DTime tax = gcs.getTimeAxis1D();
				//getUnitsString() should return a String like "Hour since 2018-05-11T06:00:00Z" (in Aladin files) or "Minute since 2018-05-11T06:00:00Z" 
				//(in Inca files). Get the units (e.g. hours) and grib reference time (e.g. 2018-05-11T06:00:00Z) from this string.
				//TODO: parsing a String? It would be better to get absolute reference time by other means - but how? Maybe here is a hint - https://stackoverflow.com/questions/57201388/using-java-netcdfall-to-read-noaa-cfs-grib-file - 
				//there is a variable named "reftime"??? Morda gds.getCalendarDateStart()???
				String timeAxisUnits = tax.getUnitsString();				
				try {
					Instant referenceTime;
					Integer timeOffsetToMillisFactor;
					if(timeAxisUnits.startsWith("Hour since ")) {
						//if the string in standard ISO 8601 format (such as 2017-01-22T18:21:13.354Z - where Z means Zulu time - i.e basically UTC) then the conversion is easy
						//TODO: is there really no other way of obtaining reference time than parsing the name of the variable?
						referenceTime = Instant.parse(timeAxisUnits.substring(11, 31));						
						//the value represents hours (so the scaling factor to milliseconds is 3600*1000)
						timeOffsetToMillisFactor = 3600*1000;
					}
					else if(timeAxisUnits.startsWith("Minute since ")) {						
						//TODO: is there really no other way of obtaining reference time than parsing the name of the variable?
						referenceTime = Instant.parse(timeAxisUnits.substring(13, 33));
						//the value represents minutes (so the scaling factor to milliseconds is  60*1000)
						timeOffsetToMillisFactor = 60*1000;
					}
					else {
						throw new ForecastExtractionException("The time axis units string does not start with Hour since or Minute since! Currently we do not support other time axis units!");
					}

					//check if the variable has three dimensions
					if(gridDtp.getRank() == 3) {
						//set the instance variable with forecast absolute reference time
						forecastReferenceTime = referenceTime;						
						//for every value in time axis obtain data, convert time axis value into milliseconds
						double[] timeOffsets = tax.getCoordValues();
						if(timeOffsets.length == 0) {
							throw new ForecastExtractionException("The array of time offsets for variable "+netCdfVariableId+" is empty!");
						}
						else {
							//set instance variable with values to an empty TreeMap
							forecastedValues = new TreeMap<Instant, Double>();
							for (int s = 0; s < timeOffsets.length; s++) {
								double timeOffsetVal = timeOffsets[s];
								int timeOffsetValInMillis = (int) timeOffsetVal*timeOffsetToMillisFactor;
								//add the offset to the reference time - to get the time Instant for this particular forecast
								Instant absoluteForecastTime = referenceTime.plusMillis(timeOffsetValInMillis);
								//get the data with GridDatatype.readDataSlice() function
								//the function returns data in canonical order (t-z-y-x). If any dimension does not exist, it ignores it.
								//parameters: t_index, z_index, y_index, x_index --> if value < 0 is given it will return all the data in that dimension
								//further info: https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/javadoc/ucar/nc2/dt/GridDatatype.html#readDataSlice-int-int-int-int-
								try {								
									//get the data at indexes
									//NOTE: for 3D variables the layer (i.e. indexInZDimension) is ignored by readDataSlice function 
									//so we can put whatever integer value - for example -1
									Array data = gridDtp.readDataSlice(s, -1, indexInYDimension, indexInXDimension);								
									if(data.getSize() == 1) {
										double dataValue = data.getDouble(0);									
										forecastedValues.put(absoluteForecastTime , dataValue);																	
									}
									else {
										throw new ForecastExtractionException("GRIB format error. The returned data slice array at time_index="+s+" y_index="+indexInYDimension+" x_index="+indexInXDimension+" contains more than one value!");
									}

								} catch (IOException e) {
									throw new ForecastExtractionException("Exception while trying to access the data at time_index="+s+" y_index="+indexInYDimension+" x_index="+indexInXDimension+"!" , e);

								}
							}
						}
					}					
					else {
						throw new ForecastExtractionException("The variable "+netCdfVariableId+" is not 3 dimensional!");						
					}
				}				
				catch(DateTimeParseException e) {
					throw new ForecastExtractionException("Exception while parsing the GRIB reference time!" , e);
				}
				catch(StringIndexOutOfBoundsException e) {
					throw new ForecastExtractionException("Exception while parsing the GRIB reference time!" , e);
				}
				catch(IllegalArgumentException e) {
					throw new ForecastExtractionException("Exception while parsing the GRIB reference time!" , e);					
				}						
			}
			else {
				throw new ForecastExtractionException("The given location (latitude: "+latitude+" longitude: "+longitude+" is outside the grid of the "+netCdfVariableId+" variable!");				
			}
		}
		else {
			throw new ForecastExtractionException("The GridDatatype with attribute Grib_Variable_Id="+netCdfVariableId+" does not exist in GridDataset "+gds.getLocationURI()+"!");

		}		
	}








	/**
	 * 
	 * Constructor which retrieves the raw forecasted values of specific layer of given four dimensional weather parameter
	 * for the grid point that is the closest to the given latitude/longitude position from a given grib file.
	 * The values for all time offsets along the time dimension of variable are extracted (e.g. values
	 * at +1h, +2h, +3h, +4h....). The extracted raw values are then assigned to the instance variables of 
	 * this class. 
	 *  
	 * The instance variables are left null if: 
	 *  <ul>
	 *   <li>the variable is not 4D variable</li>
	 *   <li>the variable cannot be found in given GridDataset</li>
	 *   <li>the given lat/lon location is outside the variable grid</li>
	 *   <li>the specified layer can not be found or the units of the layer (specified by the input parameter) do not
	 *       match the units in grib file .</li>
	 * </ul>
	 * 
	 * NOTE: The constructor can be used only for four dimensional parameters (i.e. NetCDF variables with time, X, Y and Z coordinates)!
	 * NOTE: The indexes of X and Y (i.e. the grid point) are automatically calculated from given latitude/longitude position. The projection 
	 * of the grid (e.g. LamberConformal, Latitude/Longitude etc.) is taken into account automatically.
	 * NOTE: The Z coordinate axis of the 4D variable represents the "layer" of the weather parameter (e.g. "5m above ground", 
	 * "10m above ground" etc.). The value of the layer (i.e. from which layer the data needs to be extracted) is provided as input
	 * parameter. Therefore the user needs to provide appropriate value taking into account also the unit system used in a particular
	 * grib file to indicate the layer (e.g. if user is interested in values at layer "5000mm above ground" and the identification of layer
	 * in particular grib file is expressed in meters then the input parameter for Z axis should be 5 and not 5000). In order to make the 
	 * user aware of this fact, additional input parameter {@code layerValueUnits} is requested from the user - specifying the units of the
	 * value given in {@code layerValue} input parameter. The value of {@code layerValueUnits} is compared to the layer units used in the
	 * grib file - and if they do not match the constructor does not extract the data.
	 * 
	 * @param gds The {@link ucar.nc2.dt.grid.GridDataset} dataset (the gridded data from which to extract).
	 * @param netCdfVariableId The ID of the NetCDF variable.
	 * @param latitude The latitude of the point-of-interest (the geographic location for which we want to get the values). The closest 
	 * point on grid is then calculated automatically. The value must be given in degrees with decimal fraction (not minutes, seconds...).
	 * @param longitude The longitude of the point-of-interest (the geographic location for which we want to get the values). The closest
	 * point on grid is then calculated automatically. The value must be given in degrees with decimal fraction.
	 * @param layerValue Defines the layer (Z axis) for which to extract the data. The provided value needs to be from the unit system
	 * used in the grib file (if user is interested in values at layer "5000mm above ground" and the identification of layer
	 * in grib file is expressed in meters then the input parameter for Z axis should be 5 and not 5000). 
	 * @param layerValueUnits Defines the units of the given {@code layerValue} value. This value should match the layer units used in grib
	 * file - otherwise the data is not extracted.
	 *  	 
	 */
	public RawGribData(GridDataset gds, String netCdfVariableId, double latitude, double longitude, Double layerValue, String layerValueUnits) throws ForecastExtractionException{ 
		GridDatatype gridDtp = gds.findGridDatatypeByAttribute("Grib_Variable_Id", netCdfVariableId);
		if(gridDtp!=null) {
			//get coordinate system of the NetCDF variable 
			GridCoordSystem gcs =  gridDtp.getCoordinateSystem();
			//use this coordinate system to find the closest grid point to the given latitude/longitude location
			//NOTE: the projection used in grib file (e.g. Lambert Conformal, lat/lon, etc...) is automatically taken into account
			int[] gridClosestPointIndexes = new int[2];
			//returns index value or -1 if the given geographic position is outside of the grid
			//NOTE: outside the grid means if it is outside for more than a half of the delta between two grid points
			gcs.findXYindexFromLatLon(latitude, longitude, gridClosestPointIndexes);
			int indexInYDimension = gridClosestPointIndexes[1];
			int indexInXDimension = gridClosestPointIndexes[0];	
			if(indexInYDimension!=-1 && indexInXDimension!=-1) {
				//get the time axis and obtain the needed info about this axis (reference time, units of offset time, etc...)
				CoordinateAxis1DTime tax = gcs.getTimeAxis1D();
				//getUnitsString() should return a String like "Hour since 2018-05-11T06:00:00Z" (in Aladin files) or "Minute since 2018-05-11T06:00:00Z" 
				//(in Inca files). Get the units (e.g. hours) and grib reference time (e.g. 2018-05-11T06:00:00Z) from this string.
				//TODO: parsing a String? It would be better to get absolute reference time by other means - but how?
				String timeAxisUnits = tax.getUnitsString();				
				Instant referenceTime = null;
				Integer timeOffsetToMillisFactor = null;
				try {
					if(timeAxisUnits.startsWith("Hour since ")) {
						//if the string in standard ISO 8601 format (such as 2017-01-22T18:21:13.354Z - where Z means Zulu time - i.e basically UTC) then the conversion is easy
						//TODO: is there really no other way of obtaining reference time than parsing the name of the variable?
						referenceTime = Instant.parse(timeAxisUnits.substring(11, 31));						
						//the value represents hours (so the scaling factor to milliseconds is 3600*1000)
						timeOffsetToMillisFactor = 3600*1000;
					}
					else if(timeAxisUnits.startsWith("Minute since ")) {						
						//TODO: is there really no other way of obtaining reference time than parsing the name of the variable?
						referenceTime = Instant.parse(timeAxisUnits.substring(13, 33));
						//the value represents minutes (so the scaling factor to milliseconds is  60*1000)
						timeOffsetToMillisFactor = 60*1000;
					}
					else {
						throw new ForecastExtractionException("The time axis units string does not start with Hour since or Minute since! Currently we do not support other time axis units!");
					}

					//check if the variable has four dimensions					
					if(gridDtp.getRank() == 4) {
						//find out the index of the layer (based on given input parameter layerValue)
						//however first check if the units of the given layerValue match the units of Z-axis
						CoordinateAxis1D zax = gcs.getVerticalAxis();
						if(zax.getUnitsString().equals(layerValueUnits)) {
							int indexInZDimension= RawGribData.findIndexOfValue(zax, layerValue);//returns index or -1 if coordinate axis does not contain this value
							if(indexInZDimension != -1) {										
								//set the instance variable with forecast absolute reference time
								forecastReferenceTime = referenceTime;
								//for every value in time axis obtain data, convert time axis value into milliseconds
								double[] timeOffsets = tax.getCoordValues();										
								if(timeOffsets.length == 0) {
									throw new ForecastExtractionException("The array of time offsets for variable "+netCdfVariableId+" is empty!");
								}								
								else {
									//set instance variable with values to an empty TreeMap
									forecastedValues = new TreeMap<Instant, Double>();
									for (int s = 0; s < timeOffsets.length; s++) {
										double timeOffsetVal = timeOffsets[s];
										int timeOffsetValInMillis = (int) timeOffsetVal*timeOffsetToMillisFactor;
										//add the offset to the reference time - to get the time Instant for this particular forecast
										Instant absoluteForecastTime = referenceTime.plusMillis(timeOffsetValInMillis);
										//get the data with GridDatatype.readDataSlice() function
										//the function returns data in canonical order (t-z-y-x). If any dimension does not exist, it ignores it (so it is possible to get data from 3D and 4D variables).
										//parameters: t_index, z_index, y_index, x_index --> if value < 0 is given it will return all the data in that dimension
										//further info: https://www.unidata.ucar.edu/software/thredds/current/netcdf-java/javadoc/ucar/nc2/dt/GridDatatype.html#readDataSlice-int-int-int-int-
										try {								
											//get the data at indexes
											Array data = gridDtp.readDataSlice(s, indexInZDimension, indexInYDimension, indexInXDimension);								
											if(data.getSize() == 1) {
												double dataValue = data.getDouble(0);
												forecastedValues.put(absoluteForecastTime , dataValue);																				
											}
											else {
												throw new ForecastExtractionException("GRIB format error. The returned data slice array at time_index="+s+" z_index="+indexInZDimension+" y_index="+indexInYDimension+" x_index="+indexInXDimension+" contains more than one value!");
											}

										} catch (IOException e) {
											throw new ForecastExtractionException("Exception while trying to access the data at time_index="+s+" z_index="+indexInZDimension+" y_index="+indexInYDimension+" x_index="+indexInXDimension+"!" , e);
										}
									}
								}
							}
							else {
								throw new ForecastExtractionException("The given layerValue "+layerValue+" cannot be found in the Z-coordinate axis array. The layer of 4D variable cannot be determined!");									
							}
						}
						else {
							throw new ForecastExtractionException("The given layerValueUnits "+layerValueUnits+" does not match the units of the Z-coordinate axis. The layer of 4D variable cannot be determined!");
						}											
					}
					else {
						throw new ForecastExtractionException("The variable "+netCdfVariableId+" is not 4 dimensional!");						
					}
				}				
				catch(DateTimeParseException e) {
					throw new ForecastExtractionException("Exception while parsing the GRIB reference time!" , e);					
				}
				catch(StringIndexOutOfBoundsException e) {
					throw new ForecastExtractionException("Exception while parsing the GRIB reference time!" , e);					
				}
				catch(IllegalArgumentException e) {
					throw new ForecastExtractionException("Exception while parsing the GRIB reference time!" , e);
				}
			}
			else {
				throw new ForecastExtractionException("The given location (latitude: "+latitude+" longitude: "+longitude+" is outside the grid of the "+netCdfVariableId+" variable!");				
			}			
		}
		else {
			throw new ForecastExtractionException("The GridDatatype with attribute Grib_Variable_Id="+netCdfVariableId+"does not exist in GridDataset "+gds.getLocationURI()+" !");
		}	
	}









	/**
	 * Finds the index of element in the {@link ucar.nc2.dataset.CoordinateAxis1D} object having the same value as given {@code layerValue}.
	 * If the element is not found -1 is returned.	
	 * 
	 * @param zax An {@link ucar.nc2.dataset.CoordinateAxis1D} object in which to find the index.
	 * @param layerValue A value of the element in {@link ucar.nc2.dataset.CoordinateAxis1D} object for which to find the index.
	 * @return The index at which the value is stored. -1 if the value is not found.
	 */
	//TODO: impelement a more resource efficient algorithm...
	//NOTE: for {@link ucar.nc2.dataset.CoordinateAxis1D} objects with relatively few elements the resource efficiency is not critical
	//NOTE: we provided our own implementation because the NetCDF's CoordinateAxis1D.findCoordElement(double coordVal) fails if the
	//CoordinateAxis1D contains only one element...
	//TODO: check if this is so even with newer versions of NetCDF Java...
	public static int findIndexOfValue(CoordinateAxis1D zax, double layerValue){
		double[] layers= zax.getCoordValues();
		for (int i = 0; i < layers.length; i++) {
			double val = layers[i];
			if(val == layerValue) {
				return i;
			}
		}
		return -1;
	}




	/**
	 * Gets the value of {@link #forecastReferenceTime} instance variable.
	 * @return The value of {@link #forecastReferenceTime} instance variable.
	 */
	public Instant getForecastReferenceTime() {
		return forecastReferenceTime;
	}



	/**
	 * Gets the value of {@link #forecastedValues} instance variable.
	 * @return The value of {@link #forecastedValues} instance variable.
	 */
	public TreeMap<Instant, Double> getForecastedValues() {
		return forecastedValues;
	}	


}
