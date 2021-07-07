/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 * 
 */
package com.cgs.jt.rwis.metro;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * This class represents our custom serializer of the Double values. The problem when serializing Double (to XML or JSON) is
 * that the Double value 34.0 is serialized as 34.0 (and similarly the Double value 9999.0 is serialized as 9999.0). 
 * Now Metro model represents missing values as 9999 (and not as 9999.0). So if we set a Double to 9999.0 we should get
 * 9999 when serialized (in XML or JSON).
 * 
 * @author  Jernej Trnkoczy
 * 
 */

public class MetroDoubleSerializer extends StdSerializer<Double> {

	public MetroDoubleSerializer() {
		this(null);
	}

	public MetroDoubleSerializer(Class<Double> t) {
		super(t);
	}

	@Override
	public void serialize(
			Double value, JsonGenerator jgen, SerializerProvider provider) 
					throws IOException, JsonProcessingException {
		if(value % 1 == 0) {
			jgen.writeString(String.format("%.0f", value));
		}
		else {
			jgen.writeString(value.toString());
		}
	}
}
