/*
 * Copyright (c) 1990, 2021, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions. 
 */
package com.cgs.jt.rwis.clients.http;

import java.net.URI;
import java.util.HashMap;
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
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterForecastSubscription;
import com.cgs.jt.rwis.api.exc.HttpRequestExecutionException;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.cgs.jt.rwis.srvcs.conf.MetaServiceConf;
import com.cgs.jt.rwis.srvcs.conf.SubscriptionServiceConf;
import com.cgs.jt.rwis.srvcs.json.EarthSurfacePointMapKeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.dropwizard.jersey.errors.ErrorMessage;

/**
 * Utility class to access the endpoints provided by the metaservice service (one of the dropwizard-based services
 * constituting the rwis system).Based on Jersey client provided by Dropwizard. 
 * NOTE: the client lifecycle should be managed by the caller (don't forget to close the client).
 * 
 * @author Jernej Trnkoczy
 *
 */
//inspired by https://github.com/bszeti/dropwizard-dwexample/blob/master/dwexample-client/src/main/java/bszeti/dw/example/client/ServiceClient.java
//TODO: HTTP/2 support needs to be added (the service is already HTTP/2 enabled). However dropwizard's HTTP client currently (June 2019) does not support HTTP/2
//TODO: the code could be optimized - refactor it so the same blocks of code do not repeat multiple times (in each method)
public class MetaserviceSrvClient {


	/**
	 * Represents the custom-configured HTTP client.
	 */
	private Client client;

	/**
	 * Represents the web target pointing to the root path of the metaservice service.
	 */
	private WebTarget target;



	/**
	 * Constructor.
	 * NOTE: When completed using client the client needs to be closed by calling {@link #close()} method.
	 * @param The URL containing protocol scheme, host and port.
	 *  
	 */
	public MetaserviceSrvClient(URI uri){
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
		//for example: when submitting a JSON containing subscriptions for 33 locations, each with 11
		//parameters (resulting in a list of 33x11 = 363 ParameterSubscription objects serialized into JSON)
		//a "Read timed out" happens if timeouts set to 500ms
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		clientConfig.property(ClientProperties.READ_TIMEOUT, 5000);

		//Use Apache Http client - see:
		//documentation https://hc.apache.org/httpcomponents-client-4.5.x/ and
		//tutorial for Apache HTTP client - https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/index.html
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
				.registerModule(new SimpleModule().addKeyDeserializer(EarthSurfacePoint.class, new EarthSurfacePointMapKeyDeserializer()))
				;
		clientConfig.register(new JacksonJaxbJsonProvider(objectMapper,null));

		//ClientBuilder uses org.glassfish.jersey.client.JerseyClientBuilder
		this.client = ClientBuilder.newClient(clientConfig);

		//Create webtarget
		this.target = client.target(uri).path(MetaServiceConf.META_ROOT_PATH);

		/* Example GET:
		GET //127.0.0.1:8080/subscriptions HTTP/1.1
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
	 * Sends HTTP request containing the identification of forecast model (in path param), obtains HTTP response, and - if successful - returns
	 * all the "parameter subscriptions" associated with the given forecast model (in other words: all the locations and for 
	 * each location the associated (weather) parameters and for each parameter also the list of customers subscribed to this parameter - that 
	 * are forecasted by the given forecast model). 
	 * @param forecastModelId The forecast model for which to find out which locations and (weather) parameters this forecast model serves.
	 * @return The locations each with a set of associated (weather) parameters and for each parameter also the list of customers subscribed to 
	 * this parameter- that are served by the given forecast model, or null if HTTP status 204 No Content is returned (NOTE: 204 No Content 
	 * is for example returned by Jetty server if null is returned on the Dropwizard service endpoint - for example because nothing is 
	 * found in the database).
	 */
	//TODO: we cannot return empty HashMap (and print exception) if something went wrong (e.g. service is down, service not sent 200 OK, client
	//cannot process service response, client cannot connect to service, etc...). Why not? Because empty HashMap means that nothing has been 
	//found in the database! I believe we should re-throw and let the caller handle the exception!
	//TODO: processing of server response codes is also not appropriate - for example if nothing is found in database and null is returned - then
	//the service endpoint will return 204 No Content - but since this is not 200 OK we are just printing a stupid message....
	public HashMap<EarthSurfacePoint, HashMap<ForecastedParameter, HashSet<String>>> getSubscriptionsByModel(String forecastModelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(MetaServiceConf.SUBSCR_PATH+"/"+forecastModelId)
					.request(MediaType.APPLICATION_JSON)
					.get();
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				//NOTE: when reading Map entity in JAX-RS client you need to provide the GenericType instance (a hint 
				//to JAX-RS how to deserialize)
				HashMap<EarthSurfacePoint, HashMap<ForecastedParameter, HashSet<String>>> result = response.readEntity(new GenericType<HashMap<EarthSurfacePoint, HashMap<ForecastedParameter, HashSet<String>>>>() { });
				return result;
			}
			else if (response.getStatusInfo().equals(Response.Status.NO_CONTENT)){
				//if Dropwizard service resource endpoint returned null (for example if nothing was found in the database) - then 
				//Jersey server will return HTTP status 204 No Content. So in this case we can return null (identifying that nothing
				//was found in the database).
				return null;
			}
			else {
				if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SERVER_ERROR) || 
						response.getStatusInfo().getFamily().equals(Response.Status.Family.CLIENT_ERROR)){
					//in this case we can be sure that the server returned an {@link io.dropwizard.jersey.errors.ErrorMessage} 
					//containing the description of the error - so we can use that to populate the Exception that we throw
					//TODO: check if Denis's implementation of the meta service is indeed such!
					ErrorMessage em = response.readEntity(ErrorMessage.class); 					
					if(em.getDetails()!=null) {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage()+ ". Details are: "+em.getDetails());
					}
					else {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage());
					}
				}
				//TODO: maybe handling of some other HTTP response codes (except 200, 204, 4xx and 5xx is needed).
				else {
					//the response status is not 200, 204 or from 4xx or 5xx family
					//so this is something really unusual... so unusual that we will throw exception...
					throw new HttpRequestExecutionException("HTTP response status is: "+response.getStatusInfo().getStatusCode()+". This kind of response is not expected!");
				}
			}
		}
		catch(Exception e) {
			//we will catch any kind of exception (checked and non-checked/runtime, HTTP transport exceptions and/or HTTP protocol 
			//exceptions - e.g. service is down, service not sent 200 or 204, client cannot process service response, 
			//client cannot connect to service, etc...) and re-throw it as Exception. This will signal to the consumer that the HTTP 
			//request did not succeed.
			//TODO: not sure if this is the correct way thou. See also:
			//https://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/fundamentals.html#d5e279 and
			//https://hc.apache.org/httpclient-3.x/exception-handling.html
			throw new HttpRequestExecutionException("HTTP request failed!",e);
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
	 * Sends HTTP request containing the parameter subscription, obtains HTTP response,
	 * checks the response, and if not 200 OK throws exception.
	 * 
	 * @param pfs The parameter forecast subscription.
	 */
	public ParameterForecastSubscription insertForecastSubscription(ParameterForecastSubscription pfs) throws HttpRequestExecutionException {

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(MetaServiceConf.SUBSCR_PATH)
					.request(MediaType.APPLICATION_JSON)				
					.post(Entity.json(pfs));


			if (response.getStatusInfo().equals(Response.Status.CREATED)){				
				//NOTE: when reading entity in JAX-RS client you need to provide the GenericType instance (a hint 
				//to JAX-RS how to deserialize)
				//TODO: when Dropwizard metaservice returns HTTP status 201 = created it also automatically sends back JSON representing the created object.
				//but maybe this is a waste of resources???? Why sending back (using network resources) and extracting ParameterForecastSubscription object
				//(using memory resources) is necessary???
				ParameterForecastSubscription result = response.readEntity(new GenericType<ParameterForecastSubscription>() { });
				return result;
			}			
			else {
				if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SERVER_ERROR) || 
						response.getStatusInfo().getFamily().equals(Response.Status.Family.CLIENT_ERROR)){
					//in this case we can be sure that the server returned an {@link io.dropwizard.jersey.errors.ErrorMessage} 
					//containing the description of the error - so we can use that to populate the Exception that we throw
					//TODO: check if Denis's implementation of the meta service is indeed such!
					ErrorMessage em = response.readEntity(ErrorMessage.class); 					
					if(em.getDetails()!=null) {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage()+ ". Details are: "+em.getDetails());
					}
					else {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage());
					}
				}
				//TODO: maybe handling of some other HTTP response codes (except 201, 4xx and 5xx is needed).
				else {
					//the response status is not 201 or from 4xx or 5xx family
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
			//TODO: not sure if this is the correct way thou. See also:
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
	 * Sends HTTP request containing the geographic location (for which we would like to retrieve Metro location description), and modelId (that will
	 * be processing this description) obtains HTTP response, and returns the MetroLocationDescription object for this location/model combination. 
	 * @param geoLocation The geographic location for which to find out Metro location description.
	 * @param modelId
	 * @return The Metro location description for the given location/model or null if HTTP status 204 No Content is returned (NOTE: 204 No Content 
	 * is for example returned by Jetty server if null is returned on the Dropwizard service endpoint - for example because nothing is 
	 * found in the database) .
	 * 
	 */	
	public MetroLocationDescription getMetroLocationDescription(GeographicLocation geoLocation, String modelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(MetaServiceConf.METROCONFIG_PATH+"/"+modelId+"/"+geoLocation.getLatitude()+"/"+geoLocation.getLongitude())
					.request(MediaType.APPLICATION_JSON)
					.get();
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				MetroLocationDescription locDescr = response.readEntity(MetroLocationDescription.class);
				return locDescr;
			}
			else if (response.getStatusInfo().equals(Response.Status.NO_CONTENT)){
				//if Dropwizard service resource endpoint returned null (for example if nothing was found in the database) - then 
				//Jersey server will return HTTP status 204 No Content. So in this case we can return null (identifying that nothing
				//was found in the database).
				return null;
			}
			else {
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
				//TODO: maybe handling of some other HTTP response codes (except 200, 204, 4xx and 5xx is needed).
				else {
					//the response status is not 200, 204 or from 4xx or 5xx family
					//so this is something really unusual... so unusual that we will throw exception...
					throw new HttpRequestExecutionException("HTTP response status is: "+response.getStatusInfo().getStatusCode()+". This kind of response is not expected!");
				}
			}
		}
		catch(Exception e) {
			//we will catch any kind of exception (checked and non-checked/runtime, HTTP transport exceptions and/or HTTP protocol 
			//exceptions - e.g. service is down, service not sent 200 or 204, client cannot process service response, 
			//client cannot connect to service, etc...) and re-throw it as Exception. This will signal to the consumer that the HTTP 
			//request did not succeed.
			//TODO: not sure if this is the correct way thou. See also:
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
	 * Sends HTTP request containing the Metro location description, obtains HTTP response,
	 * checks the response, and if not 200 OK throws exception. 
	 * 
	 * @param locationDescription The Metro location description object to sent to the service. 
	 */	
	public MetroLocationDescription insertMetroLocationDescription(MetroLocationDescription locationDescription) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(MetaServiceConf.METROCONFIG_PATH)
					.request(MediaType.APPLICATION_JSON)				
					.post(Entity.json(locationDescription));
			
			
			if (response.getStatusInfo().equals(Response.Status.CREATED)){				
				//NOTE: when reading entity in JAX-RS client you need to provide the GenericType instance (a hint 
				//to JAX-RS how to deserialize)
				//TODO: when Dropwizard metaservice returns HTTP status 201 = created it also automatically sends back JSON representing the created object.
				//but maybe this is a waste of resources???? Why sending back (using network resources) and extracting MetroLocationDescription object
				//(using memory resources) is necessary???
				MetroLocationDescription result = response.readEntity(new GenericType<MetroLocationDescription>() { });
				return result;
			}			
			else {
				if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SERVER_ERROR) || 
						response.getStatusInfo().getFamily().equals(Response.Status.Family.CLIENT_ERROR)){
					//in this case we can be sure that the server returned an {@link io.dropwizard.jersey.errors.ErrorMessage} 
					//containing the description of the error - so we can use that to populate the Exception that we throw
					//TODO: check if Denis's implementation of the meta service is indeed such!
					ErrorMessage em = response.readEntity(ErrorMessage.class); 					
					if(em.getDetails()!=null) {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage()+ ". Details are: "+em.getDetails());
					}
					else {
						throw new HttpRequestExecutionException("HTTP response status code is: "+response.getStatusInfo().getStatusCode()+" . Error description: "+em.getMessage());
					}
				}
				//TODO: maybe handling of some other HTTP response codes (except 201, 4xx and 5xx is needed).
				else {
					//the response status is not 201 or from 4xx or 5xx family
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
			//TODO: not sure if this is the correct way thou. See also:
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
