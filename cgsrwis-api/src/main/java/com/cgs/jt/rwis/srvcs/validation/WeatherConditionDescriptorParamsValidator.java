/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.cgs.jt.rwis.metro.MetroObservationParameter;
import com.cgs.jt.rwis.route.WeatherConditionDescriptor;



/**
 * A custom Hibernate validator used to (partially!) validate the WeatherConditionDescriptor object.
 * This custom validator only check that the values in the HashMap<ForecastedParameter, String> are non-null 
 * (NOTE: checking for HashMap keys for being non-null is not needed
 * since Jackson will complain when serializing/deserializing - that null keys in map are not supported - before 
 * NullPointerExceptions are thrown at server side).
 *
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class WeatherConditionDescriptorParamsValidator implements ConstraintValidator<WeatherConditionDescriptorParamsValidation, WeatherConditionDescriptor> {

	@Override
	public void initialize(WeatherConditionDescriptorParamsValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(WeatherConditionDescriptor wcd, ConstraintValidatorContext context) {

		//for the WeatherConditionDescriptor.params - the reqirement is
		//not to be null and not to be empty - however @NotNull and @NotEmpty validation is performed by non-custom validator, so 
		//here we need to return true in this case (as if the object is valid) and let the non-custom validator handle it...
		if(wcd.getParams() == null || wcd.getParams().isEmpty()) {
			return true;
		}
		else {
			//now check for null and empty String values in HashMap
			//NOTE: checking for null keys in HashMap is not necessary since maps generally cannot contain null keys - 
			//and Jackson will complain when serializing/deserializing from JSON!
			for(Map.Entry<ForecastedParameter, String> entry : wcd.getParams().entrySet()) {
				String value = entry.getValue();				
				if(value == null || value.isEmpty()) {
					return false;
				}
			}
			return true;			
		}
	}
}



