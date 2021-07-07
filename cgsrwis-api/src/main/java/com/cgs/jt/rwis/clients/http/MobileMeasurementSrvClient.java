/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions. 
 */
package com.cgs.jt.rwis.clients.http;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterMeasurements;
import com.cgs.jt.rwis.api.exc.HttpRequestExecutionException;
import com.cgs.jt.rwis.api.params.MeasuredParameter;
import com.cgs.jt.rwis.route.MobileSample;
import com.cgs.jt.rwis.srvcs.conf.MeasurementServiceConf;
import com.cgs.jt.rwis.srvcs.conf.MobileMeasurementServiceConf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.dropwizard.jersey.errors.ErrorMessage;

/**
 * Utility class to access the endpoints provided by the mobile measurement service (one of the dropwizard-based services
 * constituting the rwis system).Based on Jersey client provided by Dropwizard. 
 * NOTE: the client lifecycle should be managed by the caller (don't forget to close the client).
 * 
 * @author Jernej Trnkoczy
 *
 */
//inspired by https://github.com/bszeti/dropwizard-dwexample/blob/master/dwexample-client/src/main/java/bszeti/dw/example/client/ServiceClient.java
//TODO: HTTP/2 support needs to be added (the service is already HTTP/2 enabled). However dropwizard's HTTP client currently (June 2019) does not support HTTP/2
public class MobileMeasurementSrvClient {


	/**
	 * Represents the custom-configured HTTP client.
	 */
	private Client client;

	/**
	 * Represents the web target pointing to the root path of the forecast service.
	 */
	private WebTarget target;



	/**
	 * Constructor. 
	 * @param The URL containing protocol scheme, host and port.
	 * 
	 */
	public MobileMeasurementSrvClient(URI uri){
		ClientConfig clientConfig = new ClientConfig();

		//Connection settings
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(1, TimeUnit.HOURS); //Keep idle connection in pool
		connectionManager.setMaxTotal(1024); //Default is 20
		connectionManager.setDefaultMaxPerRoute(1024); //Default is 2 only, not OK for production use
		connectionManager.setValidateAfterInactivity(0); //Disable connection validation period (if it's closed on the server side). Might make sense with keepalive
		clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);

		//Socket/connection timeout
		//For additional details use connectionManager.setDefaultSocketConfig(SocketConfig.custom()...) 
		//and clientConfig.property(ApacheClientProperties.REQUEST_CONFIG, RequestConfig.custom()...)
		//TODO: submitting a large JSON could trigger "Read timed out"  - increase as needed!
		//NOTE: this is especially important if you make many requests at the same time - the local (client side) TCP/IP buffers may fill up, the server
		//may not serve all requests so quickly etc... - so it is quite tricky to determine this timeout!!!
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 1000);
		clientConfig.property(ClientProperties.READ_TIMEOUT, 1000);

		//Use Apache Http client 
		ApacheConnectorProvider apacheConnectorProvider = new  ApacheConnectorProvider();
		clientConfig.connectorProvider(apacheConnectorProvider);

		//use "Content-Length: ..." instead of "Transfer-Encoding: chunked"
		clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);

		//To Accept-Encoding: gzip,deflate (added by default if EncodingFilter is not used)
		clientConfig.register(GZipEncoder.class);
		clientConfig.register(DeflateEncoder.class);

		//To force gzip request encoding for POST
		//NOTE: if you force the use of gzip encoding then the Dropwizard/Jersey service will determine that the content length 
		//is -1 despite	the fact that Content-Length header is set! And therefore our simple DDoS prevention mechanism which checks 
		//the content length on service side will return HTTP 411 Length Required. So better turn this off.... 
		//clientConfig.register(EncodingFilter.class);
		//clientConfig.property(ClientProperties.USE_ENCODING, "gzip");

		//register custom ObjectMapper-s (serializing objects into JSON strings)
		ObjectMapper objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule()) //Support java.time.Instant marshaling - you can specify the format then - see https://stackoverflow.com/questions/45662820/how-to-set-format-of-string-for-java-time-instant-using-objectmapper
				//.enable(SerializationFeature.WRAP_ROOT_VALUE) //If you want the client to serialize/marshal with @JsonRootName (the name of the class which is serialized will appear in the JSON as root element)
				//.enable(DeserializationFeature.UNWRAP_ROOT_VALUE) //If you want the client to deserialize/unmarshal with @JsonRootName (the name of the class which is deserialized needs to be present in JSON as root element)
				//add custom serializers/deserializers here...
				;
		clientConfig.register(new JacksonJaxbJsonProvider(objectMapper,null));

		//ClientBuilder uses org.glassfish.jersey.client.JerseyClientBuilder
		this.client = ClientBuilder.newClient(clientConfig);

		//Create webtarget
		this.target = client.target(uri).path(MobileMeasurementServiceConf.MOBMSRMNTS_ROOT_PATH);

		/* Example GET:
		GET //127.0.0.1:8448/mobilemeasurements HTTP/1.1
		Accept: application/json
		myCustomHeader: value
		Content-Type: application/json
		Accept-Encoding: deflate,gzip,x-gzip
		Content-Encoding: gzip
		User-Agent: Jersey/2.25.1 (Apache HttpClient 4.5.3)
		Content-Length: 62
		Host: 127.0.0.1:8080
		Connection: keep-alive
		 */
	}




	/**
	 * Sends HTTP request containing the mobile sample to the service, which inserts it 
	 * into the in-memory database. 
	 *  
	 * @param sample The mobile sample to be inserted into the in-memory database.	
	 */	
	public void insertMobileSample(MobileSample sample) throws HttpRequestExecutionException{		

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(MobileMeasurementServiceConf.INSERT_MOBILEMEASUREMENTS_PATH)
					.request(MediaType.APPLICATION_JSON)				
					.post(Entity.json(sample));

			if (!response.getStatusInfo().equals(Response.Status.OK)){			

				if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SERVER_ERROR) || 
						response.getStatusInfo().getFamily().equals(Response.Status.Family.CLIENT_ERROR)){
					//in this case we can be sure that the server returned an {@link io.dropwizard.jersey.errors.ErrorMessage} 
					//containing the description of the error - so we can use that to populate the Exception that we throw
					ErrorMessage em = response.readEntity(ErrorMessage.class); 					
					if(em.getDetails()!=null) {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage()+ ". Details are: "+em.getDetails());
					}
					else {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage());
					}
				}
				//TODO: maybe handling of some other HTTP response codes (except 4xx and 5xx is needed).
				else {
					//the response status is not 200, and is not from 4xx or 5xx family 
					//so this is something really unusual... so unusual that we will throw exception...
					throw new HttpRequestExecutionException("HTTP response status is: "+response.getStatusInfo().getStatusCode()+". This kind of response is not expected!");
				}
			}
		}
		catch(Exception e) {
			//we will catch any kind of exception (checked and non-checked/runtime, HTTP transport exceptions and/or HTTP protocol 
			//exceptions - e.g. service is down, service not sent 200, client cannot process service response, 
			//client cannot connect to service, etc...) and re-throw it as Exception. This will signal to the consumer that the HTTP 
			//request did not succeed.
			//TODO: not sure if this is the correct approach. See also:
			//https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/fundamentals.html#d5e279 and
			//https://hc.apache.org/httpclient-3.x/exception-handling.html
			throw new HttpRequestExecutionException("HTTP request failed!", e);
		}
		finally {
			//Close response object to close underlying stream if it's not fully read.
			//See related bug https://github.com/jersey/jersey/issues/3505 - which seems to be closed now??
			if(response != null) {
				response.close();
			}
		}
	}	



	/**
	 * Closes the underlying HTTP client.
	 */
	public void close(){
		this.client.close();
	}
}
