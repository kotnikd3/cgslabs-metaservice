/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc;

/**
 * Represents a custom exception thrown when a given measured parameter cannot be meaningfully transformed into one
 * of the Metro observations imput (https://framagit.org/metroprojects/metro/wikis/Input_observation_(METRo)). 
 * For example measured parameter "Weather station supply voltage in [V]" cannot be meaningfully transformed into any of
 * the Metro observations - this exception is therefore thrown in the toMetro() function implementation of the 
 * "Weather station supply voltage in [V]" enum instance.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class NotConvertableToMetroInputException extends Exception{
	public NotConvertableToMetroInputException(String errorMessage) {
	    super(errorMessage);
	}
}
