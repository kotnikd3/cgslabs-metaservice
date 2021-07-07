/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.params;

import javax.annotation.Nullable;

import io.dropwizard.jersey.params.AbstractParam;


/**
 * A parameter encapsulating dobule values. All non-parsable
 * values will return a {@code 400 Bad Request} response.
 *  
 * @author  Jernej Trnkoczy
 */
//Dropwizard already contains a set of classes that take input string and if this string is not "appropriate" (for example you cannot
//transform string "one" into an integer with value of 1) they return appropriate message in HTTP response. 
//These classes are https://github.com/dropwizard/dropwizard/tree/master/dropwizard-jersey/src/main/java/io/dropwizard/jersey/params
//If you do not use these classes (and instead of IntParam use normal Java Integer) - then when an "inappropriate" string comes
//in request (e.g. "one") the transformation to Integer will throw NumberFormatException which will cause Dropwizard to return
//404 Not found HTTP response (and this is not acceptable!)

//For the classes that are not already part of Dropwizard you can extend the AbstractParam<T>
//Inspired by:
//https://github.com/dropwizard/dropwizard/blob/master/dropwizard-jersey/src/main/java/io/dropwizard/jersey/params/IntParam.java
//see also: //https://stackoverflow.com/questions/39144371/implementing-strong-params-using-dropwizard

public class DoubleParam extends AbstractParam<Double> {
	
	public DoubleParam(@Nullable final String input) {
		super(input);
	}
	
	public DoubleParam(@Nullable String input, String parameterName) {
        super(input, parameterName);
    }
	

	@Override
	protected String errorMessage(final Exception e) {
		return "%s is not a floating point number.";
	}

	@Override
	protected Double parse(@Nullable final String input) throws Exception {
		return Double.valueOf(input);
	}
	
}
