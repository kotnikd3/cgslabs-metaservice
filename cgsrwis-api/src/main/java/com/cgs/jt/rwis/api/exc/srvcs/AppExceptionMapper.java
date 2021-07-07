/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc.srvcs;

import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.jersey.errors.ErrorMessage;

/**
 * Represents a custom exception mapper used to handle service response when an  
 * {@link com.cgs.jt.rwis.srvcs.AppException) exception is thrown. Performs logging and sends back to client
 * HTTP response with appropriately formatted JSON string.
 *  
 * @author Jernej Trnkoczy
 * 
 */
//TODO: as far as I know this class is currently not used. However it might be good to keep it if the need arises...

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
//Note that Dropwizard already provides several exception mappers for this purpose - they can be found in the 
//io.dropwizard.jersey.errors package!

//NOTE: contrary to the plain Jersey environement in Dropwizard we need to register this mapper in the 
//dropwizard application class (in the run() method).
//see: https://dzone.com/articles/powerful-tactic-to-use-exception-mapper-in-dropwiz
@Provider//TODO: I believe this annotation is not needed because we are registering the exception mapper explicitly in run() method
//the "implements ExceptionMapper<AppException>" statement tells JAX-RS that AppExceptionMapper will handle AppException-s
public class AppExceptionMapper implements ExceptionMapper<AppException> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionMapper.class);

	//in case AppException is thrown the response that is sent back to client is an ErrorMessage object
	//(constructed from AppException) serialized as JSON.
	@Override
	public Response toResponse(AppException ex) {
		//Since we provided our own implementation of ExceptionMapper - the AppException will not be handled
		//by Dropwizard's LoggingExceptionMapper - so there will be no standard output/log on server side. 
		//We should implement logging by ourselves!
		final long id = logException(ex);


		//we make a new ErrorMessage object
		ErrorMessage errorMessage = new ErrorMessage(
				//TODO: in case of AppException the HTTP status 500 will be returned. However we are throwing
				//AppException also in cases when the incomint HTTP request did not fulfill our requirements
				//(e.g. if it is bigger than configured number of bytes, if it has parameter that is "too old" etc...)
				//So in these cases it would be better to return HTTP status from 4XX family (client error!).
				Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
				String.format("There was an error processing your request. It has been logged (ID %016x).", id),
				". Developer message: "+ex.getDeveloperMessage()
				);
		//format this ErrorMessage object as JSON string and send it back to client...
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				.entity(errorMessage)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	protected long logException(AppException ex) {
		//calculate a random number which is then assigned to log line...
		final long id = ThreadLocalRandom.current().nextLong();
		LOGGER.error(String.format("Error handling a request. Elevate logger to DEBUG to see details (ID %016x).", id));
		LOGGER.debug("Developer message: "+ex.getDeveloperMessage()+". Root cause: ", ex);
		return id;
	}


}
