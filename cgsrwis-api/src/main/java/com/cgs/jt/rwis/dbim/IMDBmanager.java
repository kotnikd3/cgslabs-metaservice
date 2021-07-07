/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.dbim;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import io.dropwizard.lifecycle.Managed;

/**
 * The {@code IMDBmanager} enum represents means to connect and disconnect to Redis in-memory database and
 * obtain a singleton Redisson {@link } object needed to execute querries. The class implements 
 * {@link io.dropwizard.lifecycle.Managed} and overrides it's start() and stop() methods. A single enum 
 * instance of this class is registered with Dropwizard environment resulting in database session establishment
 * when Dropwizard service starts and it's destruction when service is stopped. 
 *
 * @author  Jernej Trnkoczy
 * 
 */
//NOTE: Redisson client is compatible with singleton concept - see https://github.com/redisson/redisson/wiki/16.-FAQ#q-when-do-i-need-to-shut-down-a-redisson-instance-at-the-end-of-each-request-or-the-end-of-the-life-of-a-thread
//see: https://dropwizard.readthedocs.io/en/stable/manual/core.html#managed-objects
public enum IMDBmanager implements Managed{


	/**Represents the only enumeration value of this enum - the singleton instance.*/
	INSTANCE;


	/**Represents the thread safe Redisson client.*/
	private RedissonClient client;

	

	/**
	 * The override of the {@link io.dropwizard.lifecycle.Managed#start()} method.
	 */
	@Override
	public void start() throws Exception {
		IMDBmanager.INSTANCE.connect();		
	}


	/**
	 * The override of the {@link io.dropwizard.lifecycle.Managed#stop()} method.
	 * NOTE: This method is called only if the Dropwizard service is shut down properly. The recommended
	 * procedure to shut down is to use {@code kill -SIGINT <pidNumber>}. Using system exit (Ctrl+C) is 
	 * NOT RECOMMENDED! There is no guarantee how the jetty server terminates in this case.
	 * @see https://stackoverflow.com/questions/25812816/how-to-shutdown-dropwizard-application
	 * @see https://stackoverflow.com/questions/31288721/dropwizard-shutdown-hook
	 * @see https://github.com/HubSpot/dropwizard-guice/issues/77		
	 */
	@Override
	public void stop() throws Exception {		
		IMDBmanager.INSTANCE.disconnect();		
	}


	/**
	 * Establishes session to Redis database, via Redisson client in accordance with the provided redisson reconfiguration 
	 * .yaml file and assigns the obtained client to the {@link #client} instance variable. Multiple calls to this method will have 
	 * no effects (once a connection is established).	
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * 
	 */
	public void connect() throws IOException, URISyntaxException {
		if (client == null) {
			//TODO: there is probably a bunch of possible configurations that need to be set in production 
			//for long-living db applications.
			//see also https://github.com/redisson/redisson/wiki/2.-Configuration#262-single-instance-json-and-yaml-config-format
			
			//TODO: maybe implement some retry logic - if the database is not up and running the Redisson.create() method
			//will throw exception immediately - the driver does not have any retry logic....
			//The retry logic is a must if we start components with Docker compose - where some container might start up
			//before the others. If we have a bare-metal installation of Redis this however is not really critical (Redis will
			//be up an runing before the dropwizard service starts)
			Config config = Config.fromYAML(Paths.get(IMDBmanager.class.getClassLoader().getResource("redisson-config.yaml").toURI()).toFile()); 
			client = Redisson.create(config);						
		}		
	}






	/**
	 * Invalidates and closes the session and connection to the Redis database
	 */
	public void disconnect() {		
		if (client != null) {			
			client.shutdown();
		}		
	}


	/**
	 * Returns the value of the {@link #client} instance variable. 
	 *  
	 * @return Redisson client. If the client has not been established then returns null.
	 * 
	 */
	public RedissonClient getClient() {				
		return this.client;
	}


	

}
