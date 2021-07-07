/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc;

/**
 * Represents a custom exception thrown when a String value cannot be mapped to a Double value.
 * @author Jernej Trnkoczy
 *
 */
public class StringValueFormatException extends Exception{
	public StringValueFormatException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
