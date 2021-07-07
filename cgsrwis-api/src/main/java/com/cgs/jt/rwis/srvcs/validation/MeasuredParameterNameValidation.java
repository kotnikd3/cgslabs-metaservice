/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotation that triggers custom validation of the measured parameter name (which is sent as String).
 * @author Jernej Trnkoczy
 *
 */
@Constraint(validatedBy = {MeasuredParameterNameValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER}) //the validated "thing" can be an instance variable or an parameter
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MeasuredParameterNameValidation {


	String message() default "must be one of valid measured parameter names";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
