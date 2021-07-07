/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api.exc.srvcs;

import org.glassfish.jersey.server.model.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.validation.ConstraintMessage;
import io.dropwizard.jersey.validation.JerseyViolationException;

/**
 * Represents a custom exception mapper used to handle service response when the  
 * resource endpoint data validation (JSR-380/Hibernate) fails.
 * NOTE: Dropwizard already provides the default {@link io.dropwizard.jersey.validation.JerseyViolationExceptionMapper}
 * however we do not like the response in the form of {@link io.dropwizard.jersey.validation.ValidationErrorMessage}
 * therefore we provided our own implementation.
 *  
 * @author Jernej Trnkoczy
 * 
 */
public class RwisJerseyViolationExceptionMapper implements ExceptionMapper<JerseyViolationException> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RwisJerseyViolationExceptionMapper.class);

	@Override
	public Response toResponse(final JerseyViolationException exception) {
		// Provide a way to log if desired, Issue #2128, PR #2129
		LOGGER.warn("Validation failure", exception);

		final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
		final Invocable invocable = exception.getInvocable();
		final List<String> errors = exception.getConstraintViolations().stream()
				.map(violation -> ConstraintMessage.getMessage(violation, invocable))
				.collect(Collectors.toList());

		final int status = ConstraintMessage.determineStatus(violations, invocable);
		
		//we make a new ErrorMessage object
		ErrorMessage errorMessage = new ErrorMessage(
				status, 
				String.format("Validation error(s) when processing request!"),
				errors.toString()
				);
		return Response.status(status)
				.entity(errorMessage)
				.build();
	}
}
