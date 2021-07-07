/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
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
 * Annotation that triggers custom validation of the {@link DataSource} object. 
 * 
 * @author Jernej Trnkoczy
 *
 */

@Constraint(validatedBy = {DataSourceValidator.class})
@Target({ElementType.TYPE}) //the validated "thing" can be an instance of class
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DataSourceValidation  {

	
	String message() default "must be labeled with parameter name that is equal to one of the labels in MeasuredParameter or ForecastedParameter enums";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
