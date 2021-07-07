/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.api;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The {@code Customer} class represents the organization or it's representative that is subscribing to RWIS services.  
 * 
 * @author  Jernej Trnkoczy
 * 
 */
//###########################################################################################################################
//##NOTE: Currently this class is not used in Vedra system - the class is here because it might be useful in the future######
//###########################################################################################################################
//TODO: if it turns out we do not need this class then delete it!
public class Customer {
	/**
	 * The name of the organization or organization representative that is subscribing to RWIS services.
	 */
	@NotEmpty //for the Hibernate validator (packaged with Dropwizard) to check that the customerName is not null or empty string - when deserializing from JSON
	private String customerName;

	/**
	 * The e-mail of the organization or organization representative that is subscribing to RWIS services.
	 */
	@NotEmpty //for the Hibernate validator (packaged with Dropwizard) to check that the customerEmail is not null or empty string - when deserializing from JSON
	private String customerEmail;


	/**
	 * Constructor.
	 * 
	 * @param customerName The name of the organization or organization representative that is subscribing to RWIS services.
	 * @param cusomerEmail The e-mail of the organization or organization representative that is subscribing to RWIS services.
	 */
	@JsonCreator
	public Customer(
			@JsonProperty("customerName") String customerName, 
			@JsonProperty("customerEmail") String customerEmail) {
		this.customerName = customerName;		
		this.customerEmail = customerEmail;
	}


	/**
	 * Returns the value of {@link #customerName} instance variable of the object.
	 * @return The {@link #customerName} instance variable.
	 */
	@JsonProperty("customerName")//the name in the JSON will be the same as the name of variable (i.e. customerName)
	public String getCustomerName() {
		return this.customerName;	
	}


	/**
	 * Returns the value of {@link #customerEmail} instance variable of the object.
	 * @return The {@link #customerEmail} instance variable.
	 */
	@JsonProperty("customerEmail")//the name in the JSON will be the same as the name of variable (i.e. customerEmail)
	public String getCustomerEmail() {
		return this.customerEmail;	
	}
}

