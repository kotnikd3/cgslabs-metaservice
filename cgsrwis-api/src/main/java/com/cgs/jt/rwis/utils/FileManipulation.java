/*
 * Copyright (c) 1990, 2018, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. *
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */

package com.cgs.jt.rwis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.cgs.jt.rwis.api.exc.FileManipulationException;

/**
 * Contains utilities for file and directory manipulation.
 * 
 * @author Jernej Trnkoczy
 *
 */

public class FileManipulation{

	/**
	 * Creates an empty directory based on the provided path. If the directory already exists it's contents are deleted. 
	 * 
	 * @return The created (and empty) directory or null if the empty directory could not be created.
	 * @throws An {@link FileManipulationException} if the empty directory cannot be created (e.g. the directory
	 * already exists however it's contents cannot be deleted, a directory cannot be created due to security reasons etc...
	 */
	public static File createEmptyDir(Path dirPath) throws FileManipulationException{		
		File tmpDir = dirPath.toFile();
		//if temp dir exists - delete the contents
		if(tmpDir.exists()) {			
			try {
				FileUtils.deleteDirectory(tmpDir);
			} catch (Exception e) {
				throw new FileManipulationException("Cannot create empty directory "+tmpDir.getAbsolutePath()+" . Root cause: ", e);
			} 
		}
		//(re)create the directory
		try {
			tmpDir.mkdirs();
		} catch (Exception e) {
			throw new FileManipulationException("Cannot create empty directory "+tmpDir.getAbsolutePath()+" . Root cause: ", e);
		}
		return tmpDir;				
	}

	/**
	 * Deletes the specified directory. 
	 * @param dir Directory to delete. 
	 * @throws An {@link FileManipulationException} if the directory cannot be created (e.g. due to security reasons etc...)
	 */	
	public static void deleteDir(File dir) throws FileManipulationException {
		try {							
			FileUtils.deleteDirectory(dir);			
		} catch (Exception e) {
			throw new FileManipulationException("Cannot delete directory "+dir.getAbsolutePath()+" . Root cause: ", e);
		} 
	}



	/**
	 * Concatenates a list of input files into one output file. The files are appended to each other byte-by-byte.
	 * 
	 * @param inputFiles The list of files that need to be concatenated into a single file. The list must contain only files (otherwise
	 * the method throws exception).
	 * @param concatenatedFile The path to the concatenated output file. 
	 * 
	 * @return Returns the concatenated file. 
	 * @throws An {@link FileManipulationException} if the input files cannot be concatenated into the file represented by 
	 * the given {@code pathToConcatenatedFile} path (e.g. because all of the given files are not files or do not exist, the output
	 * file already exists, etc...)
	 *
	 */	
	public static File concatenateFiles(File[] files, Path pathToConcatenatedFile) throws FileManipulationException{

		File concatenatedFile = pathToConcatenatedFile.toFile();
		if(concatenatedFile.exists()) {
			throw new FileManipulationException("Cannot concatenate files into output file "+concatenatedFile.getAbsolutePath()+" . The output file already exists!");
		}
		else {
			try(FileOutputStream fos = new FileOutputStream(concatenatedFile)){
				for (File file : files) {
					if(file.isFile()) {
						try(FileInputStream fis = new FileInputStream(file)){					
							int len = 0;
							byte[] buf = new byte[1024 * 1024]; // 1MB buffer
							while ((len = fis.read(buf)) != -1) {
								fos.write(buf, 0, len);	
							}
						}
						catch(Exception e) {
							throw new FileManipulationException("Cannot concatenate files into output file "+concatenatedFile.getAbsolutePath()+" . Root cause: ", e);							
						}
					}					
					else {
						throw new FileManipulationException("Cannot concatenate files into output file "+concatenatedFile.getAbsolutePath()+" . One of the given input files is not a file!");
					}
				}
				return concatenatedFile;
			}
			catch(Exception e) {
				throw new FileManipulationException("Cannot concatenate files into output file "+concatenatedFile.getAbsolutePath()+" . Root cause: ", e);				
			}
		}		
	}



	/**
	 * Moves the provided files to the destination path (directory). If a file with the same name already exists in the
	 * destination directory it is replaced.
	 * @param f The file to be moved.
	 * @param dirPath The path to the directory where to move the files.
	 * @throws An {@link FileManipulationException} if the file cannot be moved to the destination 
	 */	
	public static void moveFile(File f, Path dirPath) throws FileManipulationException {
		Path filePath = dirPath.resolve(f.getName());			
		try {				
			Files.move(f.toPath(), filePath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (Exception e) {				
			throw new FileManipulationException("Cannot move file "+f.getAbsolutePath()+" to directory "+dirPath.toString()+" . Root cause: ", e);
		}
	}
}
