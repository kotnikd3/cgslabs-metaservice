/*
 * Copyright (c) 1990, 2019, CGS Labs d.o.o and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. 
 *
 * Please contact CGS Labs d.o.o., Brnciceva ul. 13, SI-1000 Ljubljana, Slovenia
 * or visit www.cgs-labs.com if you need additional information or have any questions.
 */
package com.cgs.jt.rwis.clients.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import com.cgs.jt.rwis.api.exc.FTPfileTransferException;

//TODO: probably needs some refactoring and Javadoc updated! 
/**
 * Provides utility methods for FTP operations. Uses {@link org.apache.commons.net.ftp.FTPClient}
 * @see https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html  
 * NOTE: the client lifecycle should be managed by the caller (don't forget to close the client).
 *
 * @author  Jernej Trnkoczy
 * 
 */
public class FTPtransfer {
	/**
	 * Represents the URL (containing protocol scheme, host and port) of the FTP server.
	 */
	private URL url;

	/**
	 * Represents the username of the user that is connecting to FTP server.
	 */
	private String user;

	/**
	 * Represents the password of the user that is connection to FTP server.
	 */
	private String pwd;	


	/**
	 * Constructor.
	 * @param url The URL containing protocol scheme, host and port of the FTP server.
	 * @param username FTP username (in name of which the files will be collected from FTP host)	 	 
	 * @param password FTP password (of a user in name of which the files will be collected from FTP host)
	 * 
	 */
	public FTPtransfer(URL url, String username, String password){
		this.url = url;
		this.user = username;
		this.pwd = password;		
	}


	/**
	 * Retrieves/downloads the "new" files (that have not been retrieved yet) from the FTP server. Only the files with 
	 * names that match the provided regular expression are considered. The retrieved files are stored locally in the 
	 * specified directory and the given list (input parameter) of "already transferred" files is updated.
	 * 
	 * NOTE: CGS does not have any control on when the new files are added and old files removed from the remote FTP server.
	 * The weather collector modules therefore need a mechanism to find out which of the files (that are currently on the
	 * remote FTP server) were already transferred (in order not to transfer and process them again). This method takes a 
	 * list of "already transferred" files as input and compares it with current FTP directory listing - to find out which files
	 * are "new". Then it downloads these files and updates the "already transferred" list of files. Note however that the list
	 * of already transferred files cannot expand indefinitely (storage constraints, processing power constraints...). This is why 
	 * the method removes the files that cannot be found on the FTP server anymore from the list. The assumption here is that the 
	 * files which were removed from the FTP server will never appear on the server again (in such case they will be downloaded and 
	 * processed again).  
	 * 
	 * @param alreadyTransferred The list of files (their names) that were already downloaded by the weather collector module in 
	 * the previous run of the module. This list is then compared with the current FTP directory listing to find the "new" files
	 * that need to be downloaded. The list is then updated (files that are no longer on remote FTP are removed from the list and
	 * the sucesfully downloaded files are added to the list).
	 * @param storageDir The local directory where to store the downloaded files.
	 * @param ftpDir The relative path (relative to home directory of the FTP user) to the remote FTP directory 
	 * in which the files are located.
	 * @param regex The regular expression the file name must match in order to be a candidate for download.
	 * 
	 * @return The list of files downloaded from the server. 
	 */		
	//TODO: this needs to be refactored and changed - updating the list given as input parameter- this is not a nice solution...
	public ArrayList<File> getNewFiles(
			ArrayList<String> alreadyTransferred,
			File storageDir,			
			String ftpDir,
			String regex
			) throws FTPfileTransferException{


		FTPClient ftp=new FTPClient();	
		//to printout all the protocol exchange messages uncomment the line below
		//ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));		

		try {
			ftp.connect(url.getHost(), url.getPort());
			//String ftpReply = ftp.getReplyString();
			//System.out.println("Connected to "+url+" Reply message: ");
			//System.out.println(ftpReply.substring(0, ftpReply.length()-1));//-1 = eliminate newline at the end
			int connectReplyCode = ftp.getReplyCode();					
			//the first response from the FTP server shoul be "positive completion"
			if(FTPReply.isPositiveCompletion(connectReplyCode)) {
				//after initial communication that ended with "positve completion" we should try to login
				ftp.login(user, pwd);
				int loginReplyCode = ftp.getReplyCode();	
				//the response to login attempt should be "200 OK"
				if(loginReplyCode == 230) {
					//a list that represents successfully transferred files
					ArrayList<File> sucesfullyTransferredFiles = new ArrayList<File>();

					//get the list of files (ignore directories!) in the remote FTP directory whose names match the given regular expression
					FTPFile[] fileList = ftp.listFiles(
							ftpDir, 
							new FTPFileFilter() {
								@Override
								public boolean accept(FTPFile ftpFile) {
									return (ftpFile.isFile() && ftpFile.getName().matches(regex));
								}
							});
					//write the file names to the list 
					ArrayList<String> currentState = new ArrayList<String>();	
					for (int i = 0; i < fileList.length; i++) {
						FTPFile f = fileList[i];
						currentState.add(f.getName());					
					}


					//from the "already transferred" list first remove all the files that are not on the remote FTP server anymore
					//(i.e. files that were removed from FTP since the last run of this program)			
					alreadyTransferred.retainAll(currentState);
					//find the files that are currently on FTP but are not in the "already transferred" list  - these need to be transferred			
					currentState.removeAll(alreadyTransferred);
					//try to transfer these files (only if the list is not empty of course!)				
					if(!currentState.isEmpty()) {	
						//transfer files one by one - and put in the list those that were sucesfully transferred
						ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
						ftp.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
						ftp.enterLocalPassiveMode();
						//a problem with hanging (and also poor performance) can be solved by increasing buffer size (the default buffer size is 0 ;)
						//see here - https://stackoverflow.com/questions/9706968/apache-commons-ftpclient-hanging/16446030			
						ftp.setBufferSize(1024 * 1024);

						for (String remoteFileName : currentState) {
							Path dirForStoragePath = storageDir.toPath();
							Path downloadedFilePath = dirForStoragePath.resolve(remoteFileName);
							File downloadedFile = downloadedFilePath.toFile();
							//try with resources should close the outputstream even in case of exceptions...
							try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadedFile))){						

								Path remoteDirPath = Paths.get(ftpDir);
								Path remoteFilePath = remoteDirPath.resolve(remoteFileName);					
								boolean success = ftp.retrieveFile(remoteFilePath.toString(), outputStream); 
								if (success) {
									sucesfullyTransferredFiles.add(downloadedFile);
								}
								else {
									//TODO: this is kind of hacky
									throw new Exception("When downloading file the retrieveFile() method of FTP client returned false!");
								}

							}
							catch(Exception e) {
								//the current file has not been successfully transferred - however we will consider this as if 
								//entire file transfer process failed...
								throw new FTPfileTransferException("Exception during transfer of one of the files. The transfer of new files from FTP failed! Root cause: ", e);
							}
						}									

						//update the list of "already transferred" files with the names of successfully downloaded files
						//TODO: ideally we would need to add to the "already transferred" list only the files that were 
						//successfully PROCESSED (information stored into the database). However this would significantly increase 
						//the complexity of the program (especially in situations where the downloaded files need to be pre-processed
						//before data extraction happens - e.g. concatenation of individual grib files into one etc..).
						for (Iterator<File> iterator = sucesfullyTransferredFiles.iterator(); iterator.hasNext();) {
							alreadyTransferred.add(((File) iterator.next()).getName());						
						}					
					}
					return sucesfullyTransferredFiles;
				}
				else {
					throw new FTPfileTransferException("FTP reply code to login attempt is "+loginReplyCode+". Shouls be 200 OK! Check if the FTP client's IP is allowed by the server and client's authorization tokens!");
				}
			}
			else {						
				throw new FTPfileTransferException("FTP reply code to connection attampt is "+connectReplyCode+". Should be one of postivie completion 2XX codes! Check URL, username and password!");			
			}
		}
		catch (SocketException e) {
			throw new FTPfileTransferException("FTP error, root cause: ", e);	
		}
		catch (IOException e) {
			throw new FTPfileTransferException("FTP error, root cause: ", e);	
		}
		finally {
			try {
				ftp.disconnect();
			} catch(IOException ioe) {
				// do nothing
			}	
		}
	}
}


