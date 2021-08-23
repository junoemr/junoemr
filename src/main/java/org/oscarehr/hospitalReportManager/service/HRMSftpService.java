/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.hospitalReportManager.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Component
public class HRMSftpService
{
	// CONNECTION SETTINGS
	private static final String OMD_HRM_USER = OscarProperties.getInstance().getProperty("omd.hrm.user");
	private static final String OMD_HRM_IP = OscarProperties.getInstance().getProperty("omd.hrm.address");
	private static final int OMD_HRM_PORT = Integer.parseInt(OscarProperties.getInstance().getProperty("omd.hrm.port"));
	private static final String REMOTE_PATH = OscarProperties.getInstance().getProperty("omd.hrm.remote_path");
	
	// LOCAL CONFIG
	private static final String OMD_DIRECTORY = OscarProperties.getInstance().getProperty("omd.hrm.local_base_directory");
	private static final String OMD_SFTP_SSH_KEY = Paths.get(OMD_DIRECTORY, OscarProperties.getInstance().getProperty("omd.hrm.private_key_file")).toString();
	private static final String DECRYPTION_KEY = OscarProperties.getInstance().getProperty("omd.hrm.decryption_key");
	
	// UTIL
	private static final int TIMEOUT_SECONDS = 20;
	private static final Logger logger = MiscUtils.getLogger();
	
	@Autowired
	private HRMReportProcessor processor;
	
	public void pullHRMFromSource()
	{
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp sftp = null;
		
		List<GenericFile> downloadedFiles = null;
		LocalDate dateSubDirectory = LocalDate.now();
		
		try
		{
			jsch.addIdentity(OMD_SFTP_SSH_KEY);
			session = jsch.getSession(OMD_HRM_USER, OMD_HRM_IP, OMD_HRM_PORT);
			
			java.util.Properties confProp = new java.util.Properties();
			confProp.put("StrictHostKeyChecking", "no");
			session.setConfig(confProp);
			
			session.connect(TIMEOUT_SECONDS * 1000);
			
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
			sftp.cd(REMOTE_PATH);
			Vector<ChannelSftp.LsEntry> remoteDirectoryContents = sftp.ls(".");
			
			List<ChannelSftp.LsEntry> remoteFiles = remoteDirectoryContents.stream()
			                                                               .filter(entry -> !entry.getAttrs().isDir())
			                                                               .collect(Collectors.toList());
			
			downloadedFiles = downloadFiles(sftp, remoteFiles, dateSubDirectory, true);
		}
		catch (JSchException | SftpException e)
		{
			logger.error("Error connecting to HRM sftp", e);
		}
		finally
		{
			if (sftp != null)
			{
				sftp.disconnect();
			}
			
			if (session != null)
			{
				session.disconnect();
			}
		}
		
		if (downloadedFiles != null && !downloadedFiles.isEmpty())
		{
			List<GenericFile> decryptedFiles = decryptFiles(downloadedFiles, dateSubDirectory);
			
			for (GenericFile hrmFile : decryptedFiles)
			{
				processor.processHRMFile_43(hrmFile);
			}
		}
	}
	
	/**
	 * Downloads files of the SFTP into the hrm documents folder inside a subdirectory formatted according to the
	 * current date yyyyMMdd.
	 *
	 * @param sftp sftp command object
	 * @param remoteFiles collection of remote files
	 * @param deleteAfterDownload true to remove each file from the remote server after downloading it
	 *
	 * @return a map of temporary files, each associated with it's original filename
	 */
	public List<GenericFile> downloadFiles(ChannelSftp sftp, Collection<ChannelSftp.LsEntry> remoteFiles, LocalDate dateSubDirectory, boolean deleteAfterDownload)
	{
		List<GenericFile> downloadedFiles = new ArrayList<>();
		
		for (ChannelSftp.LsEntry remoteFile : remoteFiles)
		{
			try
			{
				GenericFile downloadedFile = downloadRemoteFile(sftp, remoteFile, dateSubDirectory);
				downloadedFiles.add(downloadedFile);
				LogAction.addLogEntry(Provider.SYSTEM_PROVIDER_NO, LogConst.ACTION_DOWNLOAD, LogConst.CON_HRM, LogConst.STATUS_SUCCESS, remoteFile.getFilename());
				
				if (deleteAfterDownload)
				{
					removeRemoteFile(sftp, remoteFile);
				}
			}
			catch (Exception e)
			{
				// Any exception downloading the file leaves it on the server
				logger.error("Could not download remote HRM file: " + remoteFile.getFilename(), e);
				LogAction.addLogEntry(Provider.SYSTEM_PROVIDER_NO, LogConst.ACTION_DOWNLOAD, LogConst.CON_HRM, LogConst.STATUS_FAILURE, remoteFile.getFilename());
			}
		}
		
		return downloadedFiles;
	}
	
	private GenericFile downloadRemoteFile(ChannelSftp sftp, ChannelSftp.LsEntry remoteFile, LocalDate dateSubDirectory) throws Exception
	{
		LocalDateTime start = LocalDateTime.now();
		GenericFile tempFile = FileFactory.createHRMFile(remoteFile.getFilename(), dateSubDirectory, "encrypted");
		sftp.get(remoteFile.getFilename(), tempFile.getPath());
		long elapsed = ChronoUnit.MILLIS.between(start, LocalDateTime.now());
		
		logger.info(String.format("Downloaded HRM File in %d ms: %s", elapsed, remoteFile.getFilename()));
		
		return tempFile;
	}
	
	private void removeRemoteFile(ChannelSftp sftp, ChannelSftp.LsEntry remoteFile) throws SftpException
	{
		logger.info("Removing remote file: " + remoteFile.getFilename());
		sftp.rm(remoteFile.getFilename());
	}
	
	private List<GenericFile> decryptFiles(List<GenericFile> encryptedFiles, LocalDate dateSubDirectory)
	{
		List<GenericFile> decryptedFiles = new ArrayList<>();
		
		for (GenericFile encryptedFile : encryptedFiles)
		{
			GenericFile decryptedFile = decryptFile(encryptedFile, dateSubDirectory);
			if (decryptedFile != null && decryptedFile.getFileObject().exists())
			{
				decryptedFiles.add(decryptedFile);
			}
		}
		
		return decryptedFiles;
	}
	
	/**
	 * Process a file by decrypting it if necessary, and moving it to the correct location on disk
	 *
	 * @param encryptedFile temporary file
	 * @param dateSubDirectory date subdirectory to put the file in
	 *
	 * @return processed file
	 */
	private GenericFile decryptFile(GenericFile encryptedFile, LocalDate dateSubDirectory)
	{
		try
		{
			String plainText = decryptContents(encryptedFile);
			GenericFile hrmFile = FileFactory.createHRMFile(encryptedFile.getName().replaceAll("_encrypted", ""), dateSubDirectory);
				
			FileOutputStream stream = new FileOutputStream(hrmFile.getFileObject());
			stream.write(plainText.getBytes(StandardCharsets.UTF_8));
				
			return hrmFile;
		}
		catch (Exception e)
		{
			logger.error("Could not decrypt file: " + encryptedFile.getPath(), e);
			return null;
		}
	}
	
	/**
	 * Decrypt the contents of the HRM file
	 * @param encryptedFile encrypted file
	 * @return plain text string
	 * @throws Exception decryption failed
	 */
	private String decryptContents(GenericFile encryptedFile) throws Exception
	{
		try (FileInputStream inputStream = new FileInputStream(encryptedFile.getFileObject()))
		{
			byte[] buffer = new byte[(int) encryptedFile.getFileObject().length()];
			inputStream.read(buffer);
			
			Hex hex = new Hex(StandardCharsets.UTF_8.toString());
			byte[] keyBytes = hex.decode(DECRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
			
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");;
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			String plaintext = new String(cipher.doFinal(buffer));
			return plaintext;
		}
	}
}
