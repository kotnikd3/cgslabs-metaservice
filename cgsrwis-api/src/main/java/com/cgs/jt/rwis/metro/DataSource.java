package com.cgs.jt.rwis.metro;

import java.time.Instant;
import java.util.TreeMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.srvcs.validation.DataSourceValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//TODO: the part of this validation that checks the labels is probably not needed - since we are validating the mappings to be "meaningful"  in
//MetroLocationDescriptionMeasurementsMappingsValidator and MetroLocationDescriptionWeatherForecastMappingsValidator - therefore the validation
//of labels is already done there...
@DataSourceValidation //our custom validation that checks the value of the parameterLabel instance variable - the string must be equal to one of the labels in the MeasuredParameter or ForecastedParameter enums
public class DataSource {

	@NotNull (message = "may not be null and must be one of the supported data source types")
	private DataSourceType type;

	@NotNull 
	@Valid  
	private GeographicLocation geographicLocation;

	//TODO: DataSource can be of type measurement or of type forecast. Therefore we cannot use MeasuredParameter or ForecastedParameter - to identify 
	//the parameter of the data source. We would actually need an enum of the union of MeasuredParameter and ForecastedParameter enum instances. It might 
	//be possible to somehow use inheritance to solve this problem - I've tried it but did not find a meaningful solution. Therefore the identification of parameter
	//is of type String - which is not very nice...
	//NOTE: the validation of this string (that it is actually one of the MeasuredParameter of ForecastedParameter labels) is done by our custom validation/validator
	//see annotation before the class definition!
	@NotNull 
	@NotEmpty
	private String parameterLabel;

	/**
	 * If the {@link #type} is MEASUREMENT then this must be sensor id (identifying the sensor from which to take values), however if
	 * {@link #type} is FORECAST then this must be forecast model id (identifying the model that produced the forecast). 
	 */
	@NotNull 
	@NotEmpty
	private String dataSourceId;




	//TODO: would be good to to prevent creating DataSource of type MEASUREMENT with a parameterLabel that is one of the ForecastedParameter enum instances and vice-versa!
	@JsonCreator
	public DataSource (
			@JsonProperty("type") DataSourceType type, 
			@JsonProperty("geographicLocation") GeographicLocation geographicLocation,
			@JsonProperty("parameterLabel") String parameterLabel,
			@JsonProperty("dataSourceId") String dataSourceId) {		
		this.type = type;
		this.geographicLocation = geographicLocation;
		this.parameterLabel = parameterLabel;
		this.dataSourceId = dataSourceId;
	}




	/**
	 * Returns the {@link #type} instance variable of this object.
	 * @return The {@link #type} instance variable.
	 */
	@JsonProperty("type")
	public DataSourceType getType() {
		return this.type;
	}	



	/**
	 * Returns the {@link #geographicLocation} instance variable of this object.
	 * @return The {@link #geographicLocation} instance variable.
	 *
	 */
	@JsonProperty("geographicLocation")
	public GeographicLocation getGeographicLocation() {
		return this.geographicLocation;
	}


	/**
	 * Returns the {@link #parameterLabel} instance variable of this object.
	 * @return The {@link #parameterLabel} instance variable.
	 */
	@JsonProperty("parameterLabel")
	public String getParameterLabel() {
		return this.parameterLabel;
	}


	/**
	 * Returns the {@link #dataSourceId} instance variable of this object.
	 * @return The {@link #dataSourceId} instance variable.
	 *
	 */
	@JsonProperty("dataSourceId")
	public String getDataSourceId() {
		return this.dataSourceId;
	}
}
