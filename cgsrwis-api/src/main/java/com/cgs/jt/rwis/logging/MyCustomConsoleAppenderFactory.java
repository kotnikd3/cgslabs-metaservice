/*
 * Copyright (c) 1990, 2020, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.logging;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonTypeName;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import io.dropwizard.logging.ConsoleAppenderFactory;


/**
 * Custom implementation of the Dropwizard {@link io.dropwizard.logging.AppenderFactory} that extends the 
 * {@link io.dropwizard.logging.ConsoleAppenderFactory} and overrides it's {@link #appender(LoggerContext)) method.
 * Inside that method we get a reference to the {@link ch.qos.logback.classic.LoggerContext} and register a custom
 * conversion word (the "journallevel") which can then be used when specifying the logger format in the Dropwizard config.yml. 
 * 
 * @author  Jernej Trnkoczy
 * 
 */
//NOTE: this class needs to be "registered" in the /src/main/resources/META-INF/services/io.dropwizard.logging.AppenderFactory
//file
@JsonTypeName("mycustomconsoleappender")
public class MyCustomConsoleAppenderFactory<E extends DeferredProcessingAware> extends ConsoleAppenderFactory<E> {  

    @Override
    protected OutputStreamAppender<E> appender(LoggerContext context) {
        final ConsoleAppender<E> appender = new ConsoleAppender<>();
        appender.setName("console-appender");
        //now we want to register our custom conversion word - so we will be able to use it in the drowpizard config.yml
        //when specifying the log format! Inspired by https://stackoverflow.com/questions/43387987/how-to-configure-logback-conversionrule-programmatically-using-java/43388295
        Map<String, String> ruleRegistry = (Map) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        if (ruleRegistry == null) {
            ruleRegistry = new HashMap<String, String>();
        }
        context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
        ruleRegistry.put("journallevel", "com.cgs.jt.rwis.logging.CustomLevelConverter");
        
        appender.setContext(context);
        appender.setTarget(super.getTarget().get());
        return appender;
    }
}
