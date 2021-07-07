/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc;

/**
 * Represents our custom exception which will be thrown by the FTP client whenever they cannot transfer the files (for
 * whatever reason it may be). This way the consumers can know that there was a problem with the FTP file transfer. 
 * 
 * @author  Jernej Trnkoczy
 * 
 */
public class FTPfileTransferException extends Exception{

	/**
	 * Constructor that takes error message only.
	 * @param errorMessage The error message.
	 */
	public FTPfileTransferException(String errorMessage) {
        super(errorMessage);
    }
	
	/**
	 * Contrustor that takes error message and the original exception (the root cause) that caused FTP file transfer to fail.
	 * @param errorMessage The error message.
	 * @param err The exception (root cause) that caused HTTP request to fail.
	 */
	public FTPfileTransferException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
}
