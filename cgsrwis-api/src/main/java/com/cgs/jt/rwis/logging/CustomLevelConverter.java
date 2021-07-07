/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.LevelConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Used to generate custom format of level printout. We need to map Logback levels TRACE, DEBUG, INFO, WARN, ERROR to 
 * journal <7>=DEBUG, <7>=DEBUG, <6>=INFO, <4>=WARNING, <3>=ERR levels.   
 * 
 * @author  Jernej Trnkoczy
 * 
 */
//inspired by https://stackoverflow.com/questions/47771611/logback-configuration-to-have-exceptions-on-a-single-line
public class CustomLevelConverter extends LevelConverter {
    @Override
    public String convert(ILoggingEvent e) {
        if(e.getLevel().equals(Level.TRACE)) {
        	return "<7>";
        }
        else if(e.getLevel().equals(Level.DEBUG)) {
        	return "<7>";
        }
        else if(e.getLevel().equals(Level.INFO)) {
        	return "<6>";
        }
        else if(e.getLevel().equals(Level.WARN)) {
        	return "<4>";
        }
        else if(e.getLevel().equals(Level.ERROR)) {
        	return "<3>";
        }
        else {
        	//anything that is not Logback level TRACE, DEBUG, INFO, WARN or ERROR will be logged as journal <7>=DEBUG level...
        	//TODO: is this OK?
        	return "<7>";
        }        
    }
}