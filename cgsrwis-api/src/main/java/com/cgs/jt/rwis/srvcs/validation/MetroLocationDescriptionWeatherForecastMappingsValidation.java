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
 * NOTE: This annotation triggers validator that check if the weather forecast mappings contain 
 * the mappings for the mandatory weather forecast parameters (which are <at>, <td>, <ra>, <sn>, <ws> and <ap> 
 * - and according to the command line arguments that are used when Metro is run - also the <cc>, <ir>, <sf> and <fa>.
 * Additionally the mapped data sources must be appropriate - the data sources can only be of forecast type, and the mappings
 * must be reasonable - for example <at> values should not come from ForecastedParameter.WINDSPEED10M! 
 * 
 * @author Jernej Trnkoczy
 *
 */
@Constraint(validatedBy = {MetroLocationDescriptionWeatherForecastMappingsValidator.class})
@Target({ElementType.TYPE}) //the validated "thing" can be an instance of class
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MetroLocationDescriptionWeatherForecastMappingsValidation  {

	
	String message() default "should contain the mandatory weather forecast mappings for <at>, <td>, <ra>, <sn>, <ws> and <ap>, additionally it should contain the mappings for some of the <cc>, <sf>, <ir>, <fa> tags (according to what you specified for useAnthropogenicFlux, useInfraredFlux and useSolarFlux), aditionally all the ArrayLists containing DataSources must be non-null, non-empty, and the DataSources these array lists contain must be non-null, valid, of type DataSourceType.FORECAST, and appropriate forecast parameter must be mapped (e.g. <at> values should not come from the ForecastedParameter.WINDSPEED10M for example)!";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
