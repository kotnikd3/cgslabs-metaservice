/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc;

/**
 * Represents a custom exception thrown when the transformation of the value into Metro observation input (see 
 * https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo) fails.
 * For example if sensor output value for parameter "Lufft IRS31pro road condition in [categorical] @ Road surface" is such 
 * that it cannot be transformed into one of the expected Metro input values (Metro road conditions) then the toMetro() function 
 * implementation of the "Lufft IRS31pro road condition in [categorical] @ Road surface" enum instance throws this exception. 
 * 
 * @author Jernej Trnkoczy
 *
 */
public class FailedToTransformIntoMetroInputException extends Exception{
	
	/**
	 * One arg constructor.
	 */
	public FailedToTransformIntoMetroInputException(String errorMessage) {
	    super(errorMessage);
	}
	
	/**
	 * Two arg constructor.
	 * @param errorMessage
	 * @param err
	 */
	public FailedToTransformIntoMetroInputException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}

