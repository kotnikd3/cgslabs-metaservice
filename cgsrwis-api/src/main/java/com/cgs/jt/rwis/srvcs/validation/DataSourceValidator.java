/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.api.ParameterForecast;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.metro.DataSource;
import com.cgs.jt.rwis.metro.DataSourceType;



/**
 * A custom Hibernate validator used to (partially!) validate the DataSource object.
 * This custom validator only checks if the {@link DataSource.prameterLabel} instance variable has value equal to one of the 
 * labels in {@link ParameterMeasurement} or {@link ParameterForecast} enums. 
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class DataSourceValidator implements ConstraintValidator<DataSourceValidation, DataSource> {

	@Override
	public void initialize(DataSourceValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(DataSource ds, ConstraintValidatorContext context) {
		//NOTE: the validation of DataSource object instance variables being @NotNull, @NotEmpty etc. - is done by the non-custom validator, so 
		//here we need to return true in this case (as if the object is valid) and let the non-custom validator handle it...
		if(ds.getType() == null || ds.getGeographicLocation() == null || ds.getParameterLabel() == null || ds.getParameterLabel().isEmpty() || ds.getDataSourceId() == null || ds.getDataSourceId().isEmpty()) {
			return true;
		}
		else {
			if(ds.getType().equals(DataSourceType.FORECAST)) {
				if(ForecastedParameter.get(ds.getParameterLabel()) == null) {
					return false;
				}
			}			            	
			else if(ds.getType().equals(DataSourceType.MEASUREMENT)) {
				if(MeasuredParameter.get(ds.getParameterLabel()) == null) {
					return false;
				}
			}
			else {
				//the type of the datasource is not forecast and is not measurement! Thats strange...
				return false;
			}
		}
		return true;
	}
}


