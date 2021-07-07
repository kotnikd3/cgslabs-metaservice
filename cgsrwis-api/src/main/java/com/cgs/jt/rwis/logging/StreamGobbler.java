/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.logging;

/**
 * Redirects the specified inputStream to the consumer (and adds the specified prefix to each input line). 
 * It can be used for example to redirect the stdout and stderr of some process (e.g.  Python process called from Java) to 
 * the logger (so that the stdout and stderr of the process ends up in the location where the logger is writing to).
 *
 * @author Jernej Trnkoczy
 *
 */
//inspired by https://www.baeldung.com/run-shell-command-in-java//and https://stackoverflow.com/questions/14165517/processbuilder-forwarding-stdout-and-stderr-of-started-processes-without-blocki (Adam Michalick answer)

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.function.Consumer;

public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumeInputLine;
    private String prefix;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumeInputLine, String prefix) {
        this.inputStream = inputStream;
        this.consumeInputLine = consumeInputLine;
        this.prefix = prefix;
    }

    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(line -> consumeInputLine.accept(prefix+line));
    }
} 

