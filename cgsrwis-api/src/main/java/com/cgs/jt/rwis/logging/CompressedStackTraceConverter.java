/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.logging;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * Used to generate custom format of throwable stack trace when printed by the Logback logger. All the newline characters 
 * in the stack trace are replaced by ~~ - so the stack trace appears in a single line.
 * NOTE: can be used for example when feeding stacktrace to systemd/journal (or elasticsearch) - where multiline events are
 * hard to diagnose.   
 * 
 * @author  Jernej Trnkoczy
 * 
 */
//inspired by https://stackoverflow.com/questions/47771611/logback-configuration-to-have-exceptions-on-a-single-line
public class CompressedStackTraceConverter extends ThrowableProxyConverter {
    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        String original = super.throwableProxyToString(tp);

        // replace the new line characters in throwable stack trace with ~~
        return original.replaceAll("\n", " ~~ ");
    }
}
