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
package org.oscarehr.fax.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.dao.FaxJobDao;
import org.oscarehr.fax.exception.FaxApiException;
import org.oscarehr.fax.exception.FaxNumberException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxJob;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This service should be responsible for handling all logic around sending outgoing faxes
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = FaxApiException.class)
public class OutgoingFaxService
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	FaxJobDao faxJobDao;

	@Autowired
	FaxAccountDao faxAccountDao;

	public void sendFax(String providerId, Integer demographicId, String faxNumber, GenericFile...filesToFax) throws IOException
	{

		List<FaxAccount> faxAccountList = faxAccountDao.findByActiveOutbound(true, true, 0, 1);
		if(faxAccountList.isEmpty())
		{
			writeToFaxOutgoing(faxNumber, filesToFax);
		}
		else
		{
			FaxAccount faxSettings = faxAccountList.get(0); //TODO determine which fax route to use
			sendBySRFax(providerId, demographicId, faxSettings, faxNumber, filesToFax);
		}
	}

	/**
	 * When there are no direct integration fax routes, write file to outbound location (old fax system)
	 */
	private void writeToFaxOutgoing(String faxNumber, GenericFile...filesToFax) throws IOException
	{
		for(GenericFile fileToFax : filesToFax)
		{
			GenericFile faxNoFile = FileFactory.createTempFile(new ByteArrayInputStream(faxNumber.getBytes()), ".txt");
			String filenameWithoutExt = FilenameUtils.removeExtension(fileToFax.getFileObject().getName());
			faxNoFile.rename(filenameWithoutExt + ".txt");

			fileToFax.moveToOutgoingFaxPending();
			faxNoFile.moveToOutgoingFaxPending();
		}
	}

	private void sendBySRFax(String providerId, Integer demographicId, FaxAccount faxSettings, String faxNumber, GenericFile...filesToFax) throws IOException
	{
		SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxSettings.getLoginId(), faxSettings.getLoginPassword());

		String coverLetterOption = faxSettings.getCoverLetterOption();
		if(coverLetterOption == null || !SRFaxApiConnector.validCoverLetterNames.contains(coverLetterOption))
		{
			coverLetterOption = null;
		}

		HashMap<String, String> fileMap = new HashMap<>(filesToFax.length);
		for(GenericFile fileToFax : filesToFax)
		{
			fileMap.put(fileToFax.getName(), toBase64String(fileToFax));
		}

		// external api call
		SingleWrapper<Integer> resultWrapper = apiConnector.Queue_Fax(
				faxSettings.getReplyFaxNumber(),
				faxSettings.getLoginId(),
				SRFaxApiConnector.FAX_TYPE_SINGLE,
				faxNumber,
				fileMap,
				SRFaxApiConnector.RESPONSE_FORMAT_JSON,
				null,
				null,
				coverLetterOption,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
		);
		boolean sendSuccess = resultWrapper.isSuccess();

		for(GenericFile fileToFax : filesToFax)
		{
			FaxJob faxJob = new FaxJob();

			if(sendSuccess)
			{
				logger.info("Fax send success " + String.valueOf(resultWrapper.getResult()));
				faxJob.setStatus(FaxJob.STATUS.SENT);
				faxJob.setJobId(resultWrapper.getResult().longValue());
			}
			else
			{
				logger.warn("Fax send failure " + resultWrapper.getError());
				faxJob.setStatus(FaxJob.STATUS.ERROR);
			}

			faxJob.setDestination(faxNumber);
			faxJob.setFax_line(FaxAccount.INTEGRATION_TYPE_SRFAX);
			faxJob.setFile_name(fileToFax.getName());
			faxJob.setUser(faxSettings.getLoginId());
			faxJob.setNumPages(fileToFax.getPageCount());
			faxJob.setStamp(new Date());
			faxJob.setProviderNo(providerId);
			faxJob.setDemographicNo(demographicId);
			faxJobDao.persist(faxJob);

			try
			{
				if(sendSuccess)
				{
					fileToFax.moveToOutgoingFaxSent();
				}
				else
				{
					fileToFax.moveToOutgoingFaxUnsent();
				}
			}
			catch(IOException e)
			{
				// don't throw exceptions here.
				// fax may have been sent in which case we want to report a success
				logger.error("Error Moving Fax File", e);
			}
		}

		if(!sendSuccess)
		{
			// throw special exception that will not rollback the above transaction
			throw new FaxApiException("Failed to fax: " + resultWrapper.getError());
		}
	}

	/** Remove duplicate phone numbers and all non digit characters from fax numbers. */
	public static HashSet<String> preProcessFaxNumbers(String...faxNumberList)
	{
		HashSet<String> recipients = new HashSet<>(faxNumberList.length);
		for(String faxNumber : faxNumberList)
		{
			String formattedFaxNo = faxNumber.trim().replaceAll("\\D", "");
			if(formattedFaxNo.length() < 7)
			{
				throw new FaxNumberException("Invalid Fax Number: " + faxNumber);
			}
			recipients.add(formattedFaxNo);
		}
		return recipients;
	}

	private String toBase64String(GenericFile file) throws IOException
	{
		byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file.getFileObject()));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
