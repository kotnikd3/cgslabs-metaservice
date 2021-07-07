/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.utils;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides utiliti methods for storing and retrieving information from persistent storage. Used by:
 * <ul>
 * <li> weather data collector modules (219-0-Aladin, 219-0-Inca30, 219-0-Inca60, ....) to remember which of the
 * weather forecast files were already transferred from remote FTP server.
 * </li> road stations data collecto modules (all CGS stations) to remember which of the data from LoggerNet files
 * were already submitted to the database.
 * </ul>
 * 
 * @author Jernej Trnkoczy
 *
 */

//TODO: the approach with Java Preferences API has multiple drawbacks:
//1) the program is not scalable because it needs to run on only one machine 
//2) sharing preferences between multiple Linux users is difficult. The program should be run (multiple times)
//in the name of only one Linux user. Preferences.systemNodeForPackage (instead of userNodeForPackage) could be
//used however in this case the program must be run with root privileges to write into Linux /etc directory.
//3) different data collector modules MUST use different names for the preferences keys and it is hard to prevent this from happening
//accidentally (especially if different programmers are programming different collector modules). However this might not be critical
//since the "absolute" key name is a combination of the "relative" key name and the package of the class requesting the read/write of the key.
//TODO: A better approach might be storing the information in a database like Cassandra, etcd2, Consul...
//NOTE: however that if the preferences file is deleted between two consecutive application runs this should only impact 
//application performance (and not functionality). Even if the same files are downloaded and processed
//multiple times the implementation of the procedure storing the forecasts should be such that it does not allow duplicates - 
//see com.cgs.jt.rwis.api.db.ParameterForecastDAO#insert() method!

public class PersistentStore {
	/**Initializes the Logback LOGGER. The configuration logback.xml file needs to be on classpath.*/
	//private static final Logger LOGGER = LoggerFactory.getLogger(PersistentStore.class);

	/**
	 * Gets a list of strings deserialized from JSON string found under the given preferencesKeyName (the persistent storage 
	 * is based on Java preferences API).
	 * @param preferencesKeyName The "relative" name of the preferences key to get. NOTE: The "absolute" preferences key name is 
	 * a combination of relative key name and the package of the requesting class.
	 * @param requestingClass The class requesting the preferences key. NOTE: The "absolute" preferences key name is 
	 * a combination of relative key name and the package of the requesting class.  
	 * @return A list of strings found under the given preferences key name. An empty list if a given preferencesKeyName
	 * does not exist. Null if the information under given preferencesKeyName cannot be deserialized into a list of strings.
	 */
	public static ArrayList<String> getPrefStringList(String preferencesKeyName, Class requestingClass) throws Exception{
		Preferences prefs = Preferences.userNodeForPackage(requestingClass);		
		String alreadyTransferredJson = prefs.get(preferencesKeyName , "[]");//if the key does not exist (or backing store is inaccessible) assign "[]" value (representing empty list)
		//deserialize object from JSON (stored in preferences)
		ObjectMapper jsonParser = new ObjectMapper();		
		return jsonParser.readValue(alreadyTransferredJson, new TypeReference<ArrayList<String>>(){});			
	}




	/**
	 * Gets an {@link java.time.Instant} object retrieved from a long number (representing millis after epoch) found under 
	 * the given preferencesKeyName (the persistent storage is based on Java preferences API).
	 * @param preferencesKeyName The "relative" name of the preferences key to get. NOTE: The "absolute" preferences key name is 
	 * a combination of relative key name and the package of the requesting class.
	 * @param requestingClass The class requesting the preferences key. NOTE: The "absolute" preferences key name is 
	 * a combination of relative key name and the package of the requesting class.  
	 * @return An {@link java.time.Instant} object created from string found under the given preferences key name. null if a given preferencesKeyName
	 * does not exist or the String under given preferencesKeyName cannot be "deserialized" into {@link java.time.Instant} object.
	 */
	public static Instant getPrefInstant(String preferencesKeyName, Class requestingClass){
		Preferences prefs = Preferences.userNodeForPackage(requestingClass);		
		long lastInserted = prefs.getLong(preferencesKeyName , 0);//if the key does not exist (or backing store is inaccessible) assign 0 value 
		return Instant.ofEpochMilli(lastInserted);				
	}





	/**
	 * Stores the given list of strings as JSON in the persistent storage. The persistent storage is based 
	 * on Java preferences API, the JSON is stored under preferences key name provided as input parameter.
	 * 
	 * @param list The list to serialize into JSON and store.
	 * @param preferencesKeyName The "relative" name of the preferences key under which the serialized list is stored. NOTE: The 
	 * "absolute" preferences key name is a combination of relative key name and the package of the requesting class.
	 * @param requestingClass The class requesting the preferences key writing. NOTE: The "absolute" preferences key name is 
	 * a combination of relative key name and the package of the requesting class.
	 * 
	 */	
	public static void setPrefStringList(ArrayList<String> list, String preferencesKeyName, Class requestingClass) throws JsonProcessingException{
		//serialize list into JSON string and put it into preferences
		ObjectMapper jsonParser = new ObjectMapper();
		Preferences prefs = Preferences.userNodeForPackage(requestingClass);	
		prefs.put(preferencesKeyName , jsonParser.writeValueAsString(list));
		//to remove preferences from persistent storage (disk) uncomment below
		//prefs.remove(preferencesKeyName);	
	}





	/**
	 * Stores the given {@link java.time.Instant} as a long number (representing millis after epoch) in the persistent 
	 * storage. The persistent storage is based on Java preferences API, the long value is stored under preferences 
	 * key name provided as input parameter.
	 * 
	 * @param instant The Instant to convert to millis after epoch and store.	 
	 * @param preferencesKeyName The "relative" name of the preferences key under which the Instant is stored. NOTE: The 
	 * "absolute" preferences key name is a combination of relative key name and the package of the requesting class.
	 * @param requestingClass The class requesting the preferences key writing. NOTE: The "absolute" preferences key name is 
	 * a combination of relative key name and the package of the requesting class.
	 * 
	 */	
	public static void setPrefInstant(Instant instant, String preferencesKeyName, Class requestingClass) {						
		Preferences prefs = Preferences.userNodeForPackage(requestingClass);	
		prefs.putLong(preferencesKeyName , instant.toEpochMilli());
		//to remove preferences from persistent storage (disk) uncomment below
		//prefs.remove(preferencesKeyName);				
	}
}
