/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.dbim;

import org.redisson.api.GeoEntry;
import org.redisson.api.RGeo;
import org.redisson.api.RedissonClient;

import com.cgs.jt.rwis.route.MobileSample;
import com.cgs.jt.rwis.route.MobileSampleData;


/**
 * The implementation of the {@link MobileSampleDAO} interface, providing actual implementation for 
 * database access.
 * 
 * @author Jernej Trnkoczy
 *
 */
public class MobileSampleDAOimpl implements MobileSampleDAO {


	//NOTE: whoever calls this function should check that the input is valid (non-null, non-empty etc...).	
	@Override
	public void insert(MobileSample msd){
		RedissonClient cli = IMDBmanager.INSTANCE.getClient();
		RGeo<MobileSampleData> geo = cli.getGeo(IMDBconstants.GEOSET_KEY);
		GeoEntry ge = new GeoEntry(msd.getGeographicLocation().getLongitude(), msd.getGeographicLocation().getLatitude(), msd.getMobileSampleData());
		geo.add(ge);
	} 		
}







