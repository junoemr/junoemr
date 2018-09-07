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
import org.apache.log4j.Logger;
import org.oscarehr.fax.dao.FaxJobDao;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.model.FaxJob;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.SRFaxResultWrapper_Single;
import org.oscarehr.fax.model.FaxConfig;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

/**
 * This service should be responsible for handling all logic around sending outgoing faxes
 */
@Service
@Transactional
public class OutgoingFaxService
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	FaxJobDao faxJobDao;

	public void sendFax(FaxConfig faxSettings, String faxNumber, GenericFile...filesToFax) throws IOException
	{
		if(faxSettings == null)
		{
			sendFaxDefault(faxNumber, filesToFax);
		}
		else
		{
			sendBySRFax(faxSettings, faxNumber, filesToFax);
		}
	}

	/**
	 * When there are no direct integration fax routes, write file to outbound location (old fax system)
	 */
	private void sendFaxDefault(String faxNumber, GenericFile...filesToFax) throws IOException
	{
//		fileToFax.moveToOutgoingFax();
		//TODO write fax number to txt file
	}

	private void sendBySRFax(FaxConfig faxSettings, String faxNumber, GenericFile...filesToFax) throws IOException
	{
		SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxSettings.getFaxUser(), faxSettings.getFaxPasswd());

		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("sCallerID", faxSettings.getFaxNumber());
		parameters.put("sSenderEmail", faxSettings.getSiteUser());
		parameters.put("sFaxType", "SINGLE");
		parameters.put("sToFaxNumber", faxNumber);
		parameters.put("sCoverPage", faxSettings.getCoverLetterOption());

		int counter = 1;
		for(GenericFile fileToFax : filesToFax)
		{
			parameters.put("sFileName_"+counter, fileToFax.getName());
			parameters.put("sFileContent_"+counter, toBase64String(fileToFax));
			counter++;
		}

		// external api call
		SRFaxResultWrapper_Single<String> resultWrapper = apiConnector.Queue_Fax(parameters);


		for(GenericFile fileToFax : filesToFax)
		{
			FaxJob faxJob = new FaxJob();

			if(resultWrapper.isSuccess())
			{
				logger.info("Fax send success " + resultWrapper.getResult());
				fileToFax.moveToOutgoingFaxSent();
				faxJob.setStatus(FaxJob.STATUS.SENT);
				faxJob.setJobId(Long.parseLong(resultWrapper.getResult()));
			}
			else
			{
				logger.warn("Fax send failure " + resultWrapper.getResult());
				fileToFax.moveToOutgoingFaxUnsent();
				faxJob.setStatus(FaxJob.STATUS.ERROR);
			}

			//TODO log results

			faxJob.setDestination("SRFAX");
			faxJob.setFax_line(null);
			faxJob.setFile_name(fileToFax.getName());
			faxJob.setUser(faxSettings.getFaxUser());
			faxJob.setNumPages(fileToFax.getPageCount());
			faxJob.setStamp(new Date());
//			faxJob.setOscarUser(providerId);
//			faxJob.setDemographicNo(null);
			faxJobDao.persist(faxJob);
		}
	}

	private String toBase64String(GenericFile file) throws IOException
	{
		byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file.getFileObject()));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
