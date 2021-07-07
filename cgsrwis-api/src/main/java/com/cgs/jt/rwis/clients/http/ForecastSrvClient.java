/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
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
import java.util.TreeMap;
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
import com.cgs.jt.rwis.api.ParameterForecast;
import com.cgs.jt.rwis.api.exc.HttpRequestExecutionException;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.srvcs.conf.ForecastServiceConf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.dropwizard.jersey.errors.ErrorMessage;

/**
 * Utility class to access the endpoints provided by the forecast service (one of the dropwizard-based services
 * constituting the rwis system).Based on Jersey client provided by Dropwizard. 
 * NOTE: the client lifecycle should be managed by the caller (don't forget to close the client).
 * 
 * @author Jernej Trnkoczy
 *
 */
//inspired by https://github.com/bszeti/dropwizard-dwexample/blob/master/dwexample-client/src/main/java/bszeti/dw/example/client/ServiceClient.java
//TODO: HTTP/2 support needs to be added (the service is already HTTP/2 enabled). However dropwizard's HTTP client currently (June 2019) does not support HTTP/2
public class ForecastSrvClient {


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
	public ForecastSrvClient(URI uri){
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
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 2000);
		clientConfig.property(ClientProperties.READ_TIMEOUT, 2000);

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
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
				//.enable(SerializationFeature.WRAP_ROOT_VALUE) //If you want the client to serialize/marshal with @JsonRootName (the name of the class which is serialized will appear in the JSON as root element)
				//.enable(DeserializationFeature.UNWRAP_ROOT_VALUE) //If you want the client to deserialize/unmarshal with @JsonRootName (the name of the class which is deserialized needs to be present in JSON as root element)
				//add custom serializers/deserializers here...
				;
		clientConfig.register(new JacksonJaxbJsonProvider(objectMapper,null));

		//ClientBuilder uses org.glassfish.jersey.client.JerseyClientBuilder
		this.client = ClientBuilder.newClient(clientConfig);

		//Create webtarget
		this.target = client.target(uri).path(ForecastServiceConf.FORCST_ROOT_PATH);

		/* Example GET:
		GET //127.0.0.1:8080/forecasts HTTP/1.1
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
	 * Sends HTTP request containing the forecasted values and inserts them into the database. 
	 * @param pfvs The forecasted values to insert.
	 */
	
	public void insertForecasts(HashSet<ParameterForecast> pfs) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.INSERT_FORECASTS_PATH)
					.request(MediaType.APPLICATION_JSON)					
					.post(Entity.json(pfs));

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
	 * Sends HTTP request containing query parameters, obtains HTTP response, and - if successful - returns
	 * the "latest" forecasted values of the specified parameter at a specified location and produced by specified
	 * forecast model that have their absolute time bigger or equal to the provided "from" date/time.
	 * The "latest" here means the forecasted values that have the biggest forecast reference time (NOTE: there can be multiple
	 * forecasted values with the same absolute time but with different reference time - i.e. time of the model run). 
	 * @param from The returned forecasted values have the absolute time bigger or equal to this time.
	 * @param param The parameter for which we want the forecasts.
	 * @param location The location for which we want the forecasts.
	 * @param forecastModelId The model that produced the forecasts.
	 * @return The latest forecasted values, or null if HTTP status 204 No Content is returned (NOTE: 204 No Content 
	 * is for example returned by Jetty server if null is returned on the Dropwizard service endpoint - for example because nothing is 
	 * found in the database).
	 */
	public TreeMap<Instant, Double> getLatestForecastedValsFrom(
			Instant from, 
			ForecastedParameter param, 
			GeographicLocation location, 
			String forecastModelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.GET_LATEST_VALS_FROM)
					.queryParam(ForecastServiceConf.QUERYPARAM_FROM, from)
					.queryParam(ForecastServiceConf.QUERYPARAM_FORCST_PARAM_NAME , param.getLabel())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LAT , location.getLatitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LON , location.getLongitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_FORECAST_MODEL_ID , forecastModelId)					
					.request(MediaType.APPLICATION_JSON)
					.get();	
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				return response.readEntity(new GenericType<TreeMap<Instant, Double>>() { });				
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
	 * Sends HTTP request containing query parameters, obtains HTTP response, and - if successful - returns
	 * the "latest" forecasted values of the specified parameter at a specified location and produced by specified
	 * forecast model that have their absolute time bigger or equal to the provided "from" date/time and smaller or equal to the
	 * provided "to" date/time.
	 * The "latest" here means the forecasted values that have the biggest forecast reference time (NOTE: there can be multiple
	 * forecasted values with the same absolute time but with different reference time - i.e. time of the model run). 
	 * @param from The returned forecasted values have the absolute time bigger or equal to this time.
	 * @param to The returned forecasted values have the absolute time smaller or equal to this time.
	 * @param param The parameter for which we want the forecasts.
	 * @param location The location for which we want the forecasts.
	 * @param forecastModelId The model that produced the forecasts.
	 * @return The latest forecasted values, or null if HTTP status 204 No Content is returned (NOTE: 204 No Content 
	 * is for example returned by Jetty server if null is returned on the Dropwizard service endpoint - for example because nothing is 
	 * found in the database).
	 */
	public TreeMap<Instant, Double> getLatestForecastedValsFromTo(
			Instant from, 
			Instant to,
			ForecastedParameter param, 
			GeographicLocation location, 
			String forecastModelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.GET_LATEST_VALS_FROM_TO)
					.queryParam(ForecastServiceConf.QUERYPARAM_FROM, from)
					.queryParam(ForecastServiceConf.QUERYPARAM_TO, to)
					.queryParam(ForecastServiceConf.QUERYPARAM_FORCST_PARAM_NAME , param.getLabel())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LAT , location.getLatitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LON , location.getLongitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_FORECAST_MODEL_ID , forecastModelId)					
					.request(MediaType.APPLICATION_JSON)
					.get();	
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				return response.readEntity(new GenericType<TreeMap<Instant, Double>>() { });				
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
	

	
	
	


	/*
	 * Sends HTTP request containing query parameters, obtains HTTP response, and - if successful - returns
	 * last "numForecasts" forecasts (they have different forecast reference times - they were generated at 
	 * different times) of the specified parameter at a specified location and produced by specified
	 * forecast model. 
	 * @param numForecasts The number of forecasts to retrieve.
	 * @param param The parameter for which we want the forecasts.
	 * @param location The location for which we want the forecasts.
	 * @param forecastModelId The model that produced the forecasts.
	 * @return The forecasts, or null if HTTP status 204 No Content is returned (NOTE: 204 No Content 
	 * is for example returned by Jetty server if null is returned on the Dropwizard service endpoint - for example because nothing is 
	 * found in the database).
	 */
	/*
	public ParameterForecasts getLastNforecasts(
			int numForecasts, 
			ForecastedParameter param, 
			GeographicLocation location, 
			String forecastModelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.GET_LAST_N_FORCST_PATH)
					.queryParam(ForecastServiceConf.QUERYPARAM_NUM_FORECASTS , numForecasts)
					.queryParam(ForecastServiceConf.QUERYPARAM_FORCST_PARAM_NAME , param.getLabel())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LAT , location.getLatitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LON , location.getLongitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_FORECAST_MODEL_ID , forecastModelId)					
					.request(MediaType.APPLICATION_JSON)
					.get();	
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				ParameterForecasts result = response.readEntity(new GenericType<ParameterForecasts>() { });
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
	*/











	/*
	 * Sends HTTP request containing query parameters, obtains HTTP response, and - if successful - returns
	 * the latest forecasted valus of the last "numForecasts" forecasts (they have different forecast reference times - they were generated at 
	 * different times - and they are usually overlapping) of the specified parameter at a specified location and produced by specified
	 * forecast model. 
	 * @param numForecasts The number of forecasts to retrieve.
	 * @param param The parameter for which we want the forecasts.
	 * @param location The location for which we want the forecasts.
	 * @param forecastModelId The model that produced the forecasts.
	 * @return The latest forecasted values, or null if HTTP status 204 No Content is returned (NOTE: 204 No Content 
	 * is for example returned by Jetty server if null is returned on the Dropwizard service endpoint - for example because nothing is 
	 * found in the database).
	 */
	/*
	public ParameterForecastedLatestVals getLatestValsFromLastNforecasts(
			int numForecasts, 
			ForecastedParameter param, 
			GeographicLocation location, 
			String forecastModelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.GET_LATEST_FROM_LAST_N_FORCST_PATH)
					.queryParam(ForecastServiceConf.QUERYPARAM_NUM_FORECASTS , numForecasts)
					.queryParam(ForecastServiceConf.QUERYPARAM_FORCST_PARAM_NAME , param.getLabel())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LAT , location.getLatitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LON , location.getLongitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_FORECAST_MODEL_ID , forecastModelId)					
					.request(MediaType.APPLICATION_JSON)
					.get();	
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				ParameterForecastedLatestVals result = response.readEntity(new GenericType<ParameterForecastedLatestVals>() { });
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
	*/














	/*
	 * Sends HTTP request containing query parameters, obtains HTTP response, and - if successful - returns
	 * the number of forecasts  (of the specified parameter at a specified location and produced by specified
	 * forecast model) that have reference time bigger or equal to the given date/time. 
	 * @param from The date/time - the forecasts with forecast reference time bigger or equal to this date/time will be retrieved.
	 * @param param The parameter for which we want the forecasts.
	 * @param location The location for which we want the forecasts.
	 * @param forecastModelId The model that produced the forecasts.
	 * @return The number of forecasts with forecast reference time bigger or equal to the given date/time.
	 */	
	/*
	public int getNumberOfForecastsFrom(
			Instant from, 
			ForecastedParameter param, 
			GeographicLocation location, 
			String forecastModelId) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.GET_NUM_FORCST_FROM_PATH)
					.queryParam(ForecastServiceConf.QUERYPARAM_FROM , from)
					.queryParam(ForecastServiceConf.QUERYPARAM_FORCST_PARAM_NAME , param.getLabel())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LAT , location.getLatitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_LOC_LON , location.getLongitude())	
					.queryParam(ForecastServiceConf.QUERYPARAM_FORECAST_MODEL_ID , forecastModelId)					
					.request(MediaType.APPLICATION_JSON)
					.get();	
			if (response.getStatusInfo().equals(Response.Status.OK)){				
				//NOTE: when reading Map entity in JAX-RS client you need to provide the GenericType instance (a hint 
				//to JAX-RS how to deserialize)
				return response.readEntity(Integer.class);				
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
				//TODO: maybe handling of some other HTTP response codes (except 200, 4xx and 5xx is needed).
				else {
					//the response status is not 200, or from 4xx or 5xx family
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
	*/






	/*
	 * Sends HTTP request containing the forecasts and inserts them into database. 
	 * @param pfs The forecasts to insert.
	 */
	/*
	public void insertForecasts(HashSet<ParameterForecasts> pfs) throws HttpRequestExecutionException{

		//TODO: seems like Response does not support try-with-resources does it??? 
		//NOTE: Closing the response is not strictly necessary (if not using InputStream), but it does not hurt anyway - see:
		//https://stackoverflow.com/questions/33083961/closing-jax-rs-client-response
		Response response = null;
		try {
			response = target
					.path(ForecastServiceConf.INSERT_FORECASTS_PATH)
					.request(MediaType.APPLICATION_JSON)
					//NOTE: the list contains ParameterForecast objects and these contain TreeMap<Instant, Double> in
					//forecastedValues instance variable. Serialization of this TreeMap keys (i.e. Instant instances)
					//into JSON is however not problematic and we do not need to register a custom key serializer - this 
					//is because the Instant.toString() produces a result that can then be deserialized back to Instant :)
					.post(Entity.json(pfs));

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
	*/


	/**
	 * Closes the underlying HTTP client.
	 */
	public void close(){
		this.client.close();
	}
}

