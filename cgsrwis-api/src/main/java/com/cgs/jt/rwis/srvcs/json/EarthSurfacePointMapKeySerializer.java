/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.json;

import java.io.IOException;
import java.io.StringWriter;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Provides custom key serializer for Maps with {@link EarthSurfacePoint} objects as keys.
 * Dropwizard uses Jackson for JSON and the default Jackson serializer for Map keys is {@linkplain StdKeySerializer}. 
 * To achieve custom non-default serialization of Map keys a custom key serializer is needed.
 * NOTE: The custom serializer needs to be register with the ObjectMapper - either using Jackson
 * annotations or using SimpleModule. 
 * 
 * @author Jernej Trnkoczy
 *
 */
public class EarthSurfacePointMapKeySerializer extends JsonSerializer<Object> {
	/**
	 * Instance of {@link ObjectMapper} that is used to serialize {@link EarthSurfacePoint} object into JSON.
	 */
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		//convert the given object into the JSON string you want as result
		StringWriter writer = new StringWriter();
        mapper.writeValue(writer, value);
        gen.writeFieldName(writer.toString());
	}
}
