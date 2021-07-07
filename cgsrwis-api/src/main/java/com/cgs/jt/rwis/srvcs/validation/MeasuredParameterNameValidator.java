/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.api.params.MeasuredParameter;

/**
 * A custom Hibernate validator used to validate the measured parameter name.
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class MeasuredParameterNameValidator implements ConstraintValidator<MeasuredParameterNameValidation, String> {

	@Override
	public void initialize(MeasuredParameterNameValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		//the parameter will be also annotated with the @NotEmpty - if empty then the built-in Hibernate
		//validator will take care of it  - so just return true here and let the built-in validator handle - 
		//otherwise you will get both messages in the response - e.g. 
		//"message": [query param parameterName must be one of valid measured parameter names, query param parameterName may not be empty]
		if(value == null || value.isEmpty()) {
			return true;
		}
		else {
			for (MeasuredParameter mp : MeasuredParameter.values()) { 
				if(value.equals(mp.getLabel())){
					return true;
				}
			}
			return false;
		}
	}

}

