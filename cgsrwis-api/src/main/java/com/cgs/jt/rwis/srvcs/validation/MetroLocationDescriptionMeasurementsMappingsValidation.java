/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
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
 * Annotation that triggers partial custom validation of the MetroLocationDescription object. 
 * NOTE: This annotation only triggers validator that check if the measurements mappings contain 
 * the mappings for all (at, td, pi, ws, sc, st, and sst) of the Metro measurement input tags. 
 * Additionally the mapped data sources must be appropriate - the data sources can be of type measurement or 
 * forecast, however the mappings must be reasonable - for example <at> values should not come from 
 * MeasuredParameter.WIND_SPEED_AT_STATION_HEIGHT!.
 * 
 * @author Jernej Trnkoczy
 *
 */
@Constraint(validatedBy = {MetroLocationDescriptionMeasurementsMappingsValidator.class})
@Target({ElementType.TYPE}) //the validated "thing" can be an instance of class
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MetroLocationDescriptionMeasurementsMappingsValidation  {

	
	String message() default "should contain the mandatory measurements mappings for <at>, <td>, <pi>, <ws>, <sc>, <st>, and <sst>, additionally all the provided ArrayList<DataSource> lists must be non-null, non-empty and the DataSources these array lists contain must be non-null, valid and of appropriate type, and appropriate parameter must be mapped (e.g. <at> should not be mapped to MeasuredParameter.WIND_SPEED_AT_STATION_HEIGHT for example)!";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}