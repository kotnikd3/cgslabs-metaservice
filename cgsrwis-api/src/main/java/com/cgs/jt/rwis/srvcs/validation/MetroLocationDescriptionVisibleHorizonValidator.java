
/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.srvcs.validation;

import java.util.Iterator;
import java.util.TreeSet;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.cgs.jt.rwis.metro.VisibleHorizonDirection;



/**
 * A custom Hibernate validator used to (partially!) validate the MetroLocationDescription object.
 * This custom validator only checks if the elements inside the TreeSet<VisibleHorizonDescription> are conforming to the Metro 
 * input specification. The TreeSet itself can be null or empty (since the TreeSet<VisibleHorizonDirection> visibleHorizonDirections 
 * is optional parameter) and in this case validator returns true. Also the elements inside the TreeSet 
 * (i.e. VisibleHorizonDirection objects) are not checked for having non-null azimuth/elevation. This validation must be 
 * performed elsewhere. This class only validates that:
 * 1) azimuth, elevation pairs should be ordered by growing azimuths values (this is ensured by storing in a TreeSet and implementing compareTo() in VisibleHorizonDirection class)
 * 2) uniform step in azimuth is required (i.e, each neighbour azimuth values are displaced by same distance)
 * 3) the whole horizon should be covered by the data, i.e. from 0 to 360 degrees. If there is no 360 degrees value given, the value of 0 degrees is taken as 360 degrees.
 *	@see https://framagit.org/metroprojects/metro/wikis/Input_station_(METRo))	
 *
 * 
 * @author Jernej Trnkoczy
 *
 */
//Hibernate custom validator - see https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints
//inspired by:
//https://stackoverflow.com/questions/37837497/how-do-i-use-a-custom-validator-with-dropwizard
//https://stackoverflow.com/questions/32179991/with-dropwizard-validation-can-i-access-the-db-to-insert-a-record/
public class MetroLocationDescriptionVisibleHorizonValidator implements ConstraintValidator<MetroLocationDescriptionVisibleHorizonValidation, MetroLocationDescription> {

	@Override
	public void initialize(MetroLocationDescriptionVisibleHorizonValidation constraintAnnotation) {

	}

	@Override
	public boolean isValid(MetroLocationDescription mld, ConstraintValidatorContext context) {

		//The MetroLocationDescription.visibleHorizonDirections is an optional filed - meaning it can be null or empty and this would still 
		//make MetroLocationDescription object valid (so return true)
		if(mld.getVisibleHorizonDirections() == null || 
				mld.getVisibleHorizonDirections().isEmpty()) {
			return true;
		}
		else {
			
			TreeSet<VisibleHorizonDirection> vhd = mld.getVisibleHorizonDirections();
			VisibleHorizonDirection first = vhd.first();
			Double firstAzimuth = first.getAzimuth();
			VisibleHorizonDirection last = vhd.last();
			Double lastAzimuth = last.getAzimuth();
			//NOTE: the azimuths (i.e. VisibleHorizonDirection.azimuth instance variables) in the TreeSet of VisibleHorizonDirection objects
			//should not be null - however since they are annotated 
			//with @NotNull (see VisibleHorizonDirection class) the no-null checking of these is done by the non-custom Hibernate validator (and not
			//this validator). So in case any of these is null we should return true here and let the non-custom validator handle it...
			if(firstAzimuth==null || lastAzimuth==null) {
				return true;
			}
			else {
				//the first azimuth must always be 0
				if(!firstAzimuth.equals(0.0)) {
					return false;
				}
			}
			Double uniformStep;
			//if the last azimuth is not 360.0 then we can calculate the "uniform azimuth step" (which should be 360 minus last azimuth)
			if(!lastAzimuth.equals(360.0)) {
				//in this sitution the minimun requirement is two elements in the TreeSet (azimuth 0 and azimuth 180)
				if(vhd.size()<2) {
					return false;
				}
				//and the reqirement is also that the last azimuth is smaller than 360.0 of course
				if(lastAzimuth>360.0) {
					return false;
				}			

				uniformStep = 360.0 -lastAzimuth;
			}
			//if the last azimuth is 360.0 then we need to calculate the step from the number of elements in the TreeSet - 
			//it vould be 360/(n-1) 			
			else {
				//in this case the elevation value for azimuth 0 and 360.0 must be the same!
				if(!first.getElevation().equals(last.getElevation())) {
					return false;
				}
				//and also the minimum requirement is three elements in the TreeSet( azimut 0, azimuth 180 and azimuth 360)
				if(vhd.size()<3) {
					return false;
				}

				uniformStep = 360.0/(vhd.size()-1);
			}

			//now we need to iterate through the TreeSet and see if there is "uniform azimuth step" between each consecutive pair of elements
			Iterator<VisibleHorizonDirection> itr = vhd.iterator();
			Double formerAzimuth = itr.next().getAzimuth();
			while (itr.hasNext()) {
				Double latterAzimuth = itr.next().getAzimuth();
				if(formerAzimuth == null || latterAzimuth == null) {
					//NOTE: the azimuths (i.e. VisibleHorizonDirection.azimuth instance variables) in the TreeSet of VisibleHorizonDirection objects
					//should not be null - however since they are annotated 
					//with @NotNull (see VisibleHorizonDirection class) the no-null checking of these is done by the non-custom Hibernate validator (and not
					//this validator). So in case any of these is null we should return true here and let the non-custom validator handle it...
					return true;
				}
				if((latterAzimuth - formerAzimuth)!=uniformStep) {
					return false;
				}
				formerAzimuth = latterAzimuth;
			}
			return true;
		}
	}
}


