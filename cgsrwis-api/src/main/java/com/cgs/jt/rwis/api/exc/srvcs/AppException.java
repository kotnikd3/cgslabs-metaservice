/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc.srvcs;

/**
 * Represents a custom Exception that is used to notify about "checked" exceptions (i.e. logical/business
 * exceptions) of RWIS services. The server-side code should generate and throw an {@link AppException} 
 * (with custom message etc.) which is then handled by the exception mapper for this type of 
 * exceptions. The exception mapper must be registered in the environment.
 * NOTE: the HTTP response body that is sent back to client is defined by the exception mapper. 
 * 
 * @author Jernej Trnkoczy
 *
 */

//NOTE: CGS-RWIS services use {@link com.cgs.jt.rwis.api.srvcs.AppException} exceptions  
//to signal the business exceptions for which we want our custom handling (and
//custom response body). Other exceptions are already handled by Dropwizard's exception mappers
//from package io.dropwizard.jersey.errors

//EXPLANATION: Dropwizard is based on Jersey which runs in the container and if we do not do anything
//to handle the exceptions these will be by default handled by the Servlet container - which always 
//sends back HTML response ->
//see https://www.journaldev.com/1973/servlet-exception-and-error-handling-example-tutorial  
//If JSON response is needed (instead HTML) - the default Servlet container exception handling behavior
//needs to be modified. This is done with exception mapping:
//https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-en/cn/part1/chapter7/exception_handling.html. 
//The exception mapper defines what happens when certain exceptions are thrown. Which exception mapper is
//associated with which exceptions is defined in the exception mapper class declaration. JAX-RS supports 
//exception inheritance - this means that when an exception is thrown, JAX-RS will first try to find an 
//ExceptionMapper for that exception’s type. If it cannot find one, it will look for a mapper that can 
//handle the exception’s superclass. It will continue this process until there are no more superclasses 
//to match against. 
//Note that Dropwizard already provides exception mappers for this purpose - they can be found in the 
//io.dropwizard.jersey.errors package!


public class AppException extends Exception{

	
	/** detailed error description for developers*/
	private String developerMessage;

	//NOTE: the "shorter" error message field is inherited from Exception class



	/**
	 * Constructor
	 * @param message A message (shorter version) that is assigned as the message of the Exception (which is a superclass).
	 * @param developerMessage A message (longer version) containing detailed error description for developers. 
	 */
	public AppException(String message, String developerMessage) {
		//call of super constructor (constructor of Exception) - giving it a message string
		super(message);		
		this.developerMessage = developerMessage;		
	}


	/**
	 * Returns the value of {@link #developerMessage} instance variable.
	 * @return The value of the {@link #developerMessage} instance variable.
	 */
	public String getDeveloperMessage() {
		return developerMessage;
	}
}
