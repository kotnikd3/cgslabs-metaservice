/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc;

/**
 * Represents our custom exception which will be thrown by the method that creates the weather forecast XML input for Metro
 * if the creation of XML is unsuccessful (for whatever reason - the reason will be included in the exception)
 *  
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class CreateMetroWeatherForecastXmlException  extends Exception{

	/**
	 * Constructor that takes error message only.
	 * @param errorMessage The error message.
	 */
	public CreateMetroWeatherForecastXmlException(String errorMessage) {
        super(errorMessage);
    }
	
	/**
	 * Contrustor that takes error message and the original exception (the root cause) that caused HTTP request to fail.
	 * @param errorMessage The error message.
	 * @param err The exception (root cause) that caused HTTP request to fail.
	 */
	public CreateMetroWeatherForecastXmlException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
