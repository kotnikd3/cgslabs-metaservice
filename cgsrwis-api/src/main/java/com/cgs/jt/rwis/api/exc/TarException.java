/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc;

/**
 * Represents our custom exception indicating something went wrong while performing untar operation. 
 * 
 * @author  Jernej Trnkoczy
 * 
 */
//NOTE:  Thrown by methods in the {@link FileManipulation} class.
public class TarException extends Exception{

	/**
	 * Constructor that takes error message only.
	 * @param errorMessage The error message.
	 */
	public TarException(String errorMessage) {
        super(errorMessage);
    }
	
	/**
	 * Contrustor that takes error message and the original exception (the root cause) that caused the {@link TarException}.
	 * @param errorMessage The error message.
	 * @param err The exception (root cause) that caused the {@link TarException}.
	 */
	public TarException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}

