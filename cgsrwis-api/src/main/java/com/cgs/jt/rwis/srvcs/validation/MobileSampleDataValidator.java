/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.route.MobileSampleData;


/**
 * A custom Hibernate validator used to (partially!) validate the MobileSampleData object.
 * NOTE: This custom validator only checks if the HashMap<MesuredParameter, Double> contains appropriate keys (only the
 * defined measured parameters are allowed) and their values should be non-null and in 
 * the expected range of values (defined for each measured parameter). The checking of the non-null instance variables of 
 * the MobileSampleData object should be handled by non-custom Hibernate validators.
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class MobileSampleDataValidator implements ConstraintValidator<MobileSampleDataValidation, MobileSampleData> {

	@Override
	public void initialize(MobileSampleDataValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(MobileSampleData msd, ConstraintValidatorContext context) {
		
		//TODO: the <MeasuredParameter, Double> in the MobileSampleData class is annotated with @NotEmpty, however this 
		//only checks that the HashMap is not null and is not empty (and does not validate what is inside the HashMap).
		//Using the latest Bean Validation API it would be possible to annotate with HashMap<@NotNull @Valid MeasuredParameter, @NotNull Double>
		//however current Dropwizard version does not support the latest Bean Validation API (see comments in MobileSampleData class).
		//So we need to check the measured values that they are not null and that they are in the expected range. When Dropwizard
		//starts supporting the latest Bean Validation API - we should remove the checking for null from this class (and in case of 
		//null just return true  - and the non-custom Hibernate validator will take care of null checking - not this class). 

		//NOTE: the vehicleID, measurementTime, snesorTrustLevel and data instance variables in the MobileSampleData class are annotated 
		//with @NotNull and @NotEmpty so no-null checking of these variables is done by the non-custom Hibernate validator (and not
		//this validator). So in case any of the instance variables is null we should return true here and let the
		//non-custom validator handle it...
		if(msd.getVehicleID() == null || msd.getMeasurementTime() == null || msd.getSensorTrustLevel() == null || msd.getSampledData() == null || msd.getSampledData().isEmpty()) {
			return true;
		}
		else {
			//NOTE: checking for null keys in HashMap is not necessary since HashMap cannot contain null keys!
			for(Map.Entry<MeasuredParameter, Double> entry : msd.getSampledData().entrySet()) {
				MeasuredParameter parameter = entry.getKey();
				Double value = entry.getValue();				
				if(value == null || !parameter.checkValue(value)) {
					return false;
				}
			}
			return true;
		}
	}
}



