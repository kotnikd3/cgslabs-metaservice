/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import com.cgs.jt.rwis.api.exc.FileManipulationException;
import com.cgs.jt.rwis.api.exc.TarException;

/**
 * Provides utility methods for operations upon .tar.gz files.  
 *
 * @author  Jernej Trnkoczy
 * 
 */
public class Tar {

	/**
	 * Untars the provided .tar.gz file into the specified directory. 
	 * NOTE: if the files/folders with the same names (as the ones in .tar.gz file) are already in the target directory they 
	 * are overwritten.
	 *
	 * @param source The .tar.gz file to uncompress.
	 * @param targetDirPath The path to the directory where to store the results (files and directories) of the untar operation. 
	 * 
	 * @throws An {@link TarException} if the input file cannot be untarred to the output directory (e.g. the input file is not a .tar 
	 * file, the output directory cannot be created, etc...)  	 
	 *
	 */	
	//NOTE: how to use apache commons-compress for unpacking - see https://commons.apache.org/proper/commons-compress/examples.html
	//see also https://memorynotfound.com/java-tar-example-compress-decompress-tar-tar-gz-files/		
	public static void extract(File source , Path targetDirPath) throws TarException {
		try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
				new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(source))))
				){ 

			//each entry in tar represents either a folder or a file
			TarArchiveEntry entry = null;
			//loop through entries and make file or folder, store it in the targetDir
			while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
				//put together the absolute path of the file/folder representing the tar entry
				Path tarEntryPath = targetDirPath.resolve(entry.getName());				
				File f = tarEntryPath.toFile();
				if (entry.isDirectory()) {
					//create directory tree 
					try {
						f.mkdirs();
					} catch (Exception e) {
						throw new TarException("Cannot untar the provided file "+source.getAbsolutePath()+" into destination directory "+targetDirPath.toString()+" . Cannot create subfolder structure for the current directory tar entry "+entry.getName()+" . Root cause: ", e);
					}
				}
				//if entry is a file write
				else {
					//try-catch needed to close the OutputStream (try-with-resources)
					try (OutputStream o = Files.newOutputStream(tarEntryPath)) {
						IOUtils.copy(tarIn, o);
					}
					catch(Exception e) {
						throw new TarException("Cannot untar the provided file "+source.getAbsolutePath()+" into destination directory "+targetDirPath.toString()+" . Cannot copy the current file tar entry "+entry.getName()+" . Root cause: ", e);						
					}
				}
			}
		}
		catch(Exception e) {
			throw new TarException("Cannot untar the provided file "+source.getAbsolutePath()+" into destination directory "+targetDirPath.toString()+" . Root cause: ", e);			
		}		
	}

}


