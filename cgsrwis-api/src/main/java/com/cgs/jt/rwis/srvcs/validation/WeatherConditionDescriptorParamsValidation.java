/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * Annotation that triggers partial custom validation of the WeatherConditionDescriptor object. 
 * NOTE: This annotation only triggers validator that check if the values in the HashMap are not null!
 * 
 * @author Jernej Trnkoczy
 *
 */
@Constraint(validatedBy = {WeatherConditionDescriptorParamsValidator.class})
@Target({ElementType.TYPE}) //the validated "thing" can be an instance of class
@Retention(value = RetentionPolicy.RUNTIME)
public @interface WeatherConditionDescriptorParamsValidation  {

	
	String message() default "variable params should contain only non-null and non-empty values describing road forecast model ID!";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
