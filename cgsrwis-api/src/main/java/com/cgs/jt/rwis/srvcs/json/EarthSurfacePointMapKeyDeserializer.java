/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.json;

import java.io.IOException;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides custom key deserializer for Maps with {@link EarthSurfacePoint} objects as keys.
 * Dropwizard uses Jackson for JSON and the default Jackson deserializer for Map is {@link KeyDeserializer}. 
 * To achieve custom non-default deserialization of Map keys a custom key deserializer is needed.
 * NOTE: The custom deserializer needs to be register with the ObjectMapper - either using Jackson
 * annotations or using SimpleModule. 
 * 
 * @author Jernej Trnkoczy
 *
 */
public class EarthSurfacePointMapKeyDeserializer extends KeyDeserializer{

	/**
	 * Instance of {@link ObjectMapper} that is used to deserialize string into {@link EarthSurfacePoint} object.
	 */
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		//construct the EarthSurfacePoint object from the given string.		
		return mapper.readValue(key, EarthSurfacePoint.class);				
	}
}
