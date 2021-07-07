/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.api.ParameterForecast;


/**
 * A custom Hibernate validator used to (partially!) validate the ParameterForecast object.
 * NOTE: This custom validator only checks if the forecasted values in the TreeMap<Instant, Double> are non-null , are in 
 * the expected range of values (defined for each forecasted parameter), and have timestamp that is bigger than the 
 * forecast reference time. The checking of the non-null instance variables of 
 * the ParameterForecast object should be handled by non-custom Hibernate validators.
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class ParameterForecastValidator implements ConstraintValidator<ParameterForecastValidation, ParameterForecast> {

	@Override
	public void initialize(ParameterForecastValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(ParameterForecast pf, ConstraintValidatorContext context) {

		//TODO: the TreeMap<Instant, Double> forecast in the ParameterForecast class is annotated with @NotEmpty, however this 
		//only checks that the TreeMap is not null and is not empty (and does not validate what is inside the TreeMap).
		//Using the latest Bean Validation API it would be possible to annotate with 
		//TreeMap<@NotNull Instant, @NotNull Double>
		//however current Dropwizard version does not support the latest Bean Validation API (see comments in ParameterForecast class).
		//So we need to check everything inside the TreeMap for not being null and also the Double values to be inside the expected range. 
		//When Dropwizard starts supporting the latest Bean Validation API - we should remove the checking for null from this class (and in case of 
		//null just return true  - and the non-custom Hibernate validator will take care of null checking - not this class). 

		//NOTE: the forecast and other instance variables in the ParameterForecast class are annotated 
		//with @NotNull and @NotEmpty so checking of these variables is done by the non-custom Hibernate validator (and not
		//this validator). So in case any of these instance variables is null we should return true here and let the
		//non-custom validator handle it...
		if(pf.getParameter() == null || pf.getForecast() == null) {
			return true;
		}
		else {
			//NOTE: The default natural ordering comparator of TreeMap does not allow to insert null keys
			//see https://stackoverflow.com/questions/48687829/why-null-key-is-not-allowed-in-treemap . Since we are using the default 
			//comparator - the TreeMap will never contain null keys (if we try to make it in Java - we will get NullPointerException, if
			//we send JSON string containing null to service - we will get JerseyViolationException - Validation failure - and 400: "unable
			//to process JSON" back to client. BOTTOM LINE  - we do not need to check the keys for being null!
			for(Map.Entry<Instant, Double> entry : pf.getForecast().entrySet()) {
				Instant refTime = pf.getReferenceTime();
				Instant time = entry.getKey();
				Double val = entry.getValue();				
				//the actual forecast cannot be before the forecast reference time,
				//the forecasted value cannot be null, and
				//the forecasted value must be in the specified range
				if(time.isBefore(refTime) || val == null || !pf.getParameter().checkValue(val)) {
					return false;
				}
			}
			return true;
		}
	}
}


