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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.api.ParameterMeasurements;


/**
 * A custom Hibernate validator used to (partially!) validate the ParameterMeasurements object.
 * NOTE: This custom validator only checks if the measured values in the TreeMap<Instant, Double> are non-null and are in 
 * the expected range of values (defined for each measured parameter). The checking of the non-null instance variables of 
 * the ParameterMeasurements object should be handled by non-custom Hibernate validators.
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class ParameterMeasurementsValidator implements ConstraintValidator<ParameterMeasurementsValidation, ParameterMeasurements> {

	@Override
	public void initialize(ParameterMeasurementsValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(ParameterMeasurements measurements, ConstraintValidatorContext context) {
		
		//TODO: the TreeMap<Instant,Double> measuredValues in the ParameterMeasurements class is annotated with @NotEmpty, however this 
		//only checks that the TreeMap is not null and is not empty (and does not validate what is inside the TreeMap).
		//Using the latest Bean Validation API it would be possible to annotate with TreeMap<@NotNull Instant, @NotNull Double>
		//however current Dropwizard version does not support the latest Bean Validation API (see comments in ParameterMeasurements class).
		//So we need to check the measured values that they are not null and that they are in the expected range. When Dropwizard
		//starts supporting the latest Bean Validation API - we should remove the checking for null from this class (and in case of 
		//null just return true  - and the non-custom Hibernate validator will take care of null checking - not this class). 

		//NOTE: the measuredValues and parameter instance variables in the ParameterMeasurements class are annotated 
		//with @NotNull and @NotEmpty so no-null checking of these variables is done by the non-custom Hibernate validator (and not
		//this validator). So in case any of the instance variables is null we should return true here and let the
		//non-custom validator handle it...
		if(measurements.getParameter() == null || measurements.getMeasuredValues() == null) {
			return true;
		}
		else {
			//NOTE: checking for null keys in TreeMap is not necessary since TreeMap cannot contain null keys!
			for(Map.Entry<Instant, Double> entry : measurements.getMeasuredValues().entrySet()) {
				Double value = entry.getValue();				
				if(value == null || !measurements.getParameter().checkValue(value)) {
					return false;
				}
			}
			return true;
		}
	}
}


