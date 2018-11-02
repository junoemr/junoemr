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
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.exception.FaxApiException;
import org.oscarehr.fax.exception.FaxNumberException;
import org.oscarehr.fax.exception.FaxSendException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.FaxTransferConverter;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;

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
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = FaxSendException.class)
public class OutgoingFaxService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();

	@Autowired
	FaxOutboundDao faxOutboundDao;

	@Autowired
	FaxAccountDao faxAccountDao;

	public boolean isOutboundFaxEnabled()
	{
		List<FaxAccount> faxAccountList = faxAccountDao.findByActiveOutbound(true, true, 0, 1);
		return (!faxAccountList.isEmpty() || props.isFaxEnabled());
	}

	public void sendFax(String providerId, Integer demographicId, String faxNumber, FaxOutbound.FileType fileType, GenericFile...filesToFax) throws IOException, FaxSendException
	{
		//TODO determine which fax route to use
		List<FaxAccount> faxAccountList = faxAccountDao.findByActiveOutbound(true, true, 0, 1);
		// check for enabled fax routes
		if(!faxAccountList.isEmpty())
		{
			FaxAccount faxAccount = faxAccountList.get(0);
			for(GenericFile fileToFax : filesToFax)
			{
				FaxOutbound queuedFax = queueFax(providerId, demographicId, faxAccount, faxNumber, fileType, fileToFax);
				sendQueuedFax(queuedFax);
			}
		}
		// if legacy faxing is enabled, write to the outgoing folder.
		else if(props.isFaxEnabled())
		{
			writeToFaxOutgoing(faxNumber, filesToFax);
		}
		else
		{
			throw new RuntimeException("No outbound fax routes enabled!");
		}
	}

	public FaxOutboxTransferOutbound resendFax(Long faxOutId) throws IOException
	{
		FaxOutbound faxOutbound = faxOutboundDao.find(faxOutId);
		GenericFile fileToResend = FileFactory.getOutboundUnsentFaxFile(faxOutbound.getFileName());
		fileToResend.moveToOutgoingFaxPending();

		faxOutbound.setStatus(FaxOutbound.Status.QUEUED);
		faxOutboundDao.merge(faxOutbound);

		try
		{
			sendQueuedFax(faxOutbound);
		}
		// queueing but not completing a send is ok, we still want to return th transfer with the queued status in this case
		catch(FaxSendException e)
		{
			logger.warn("Failed to send queued fax (id:"+faxOutbound.getId()+"):"+ e.getMessage());
		}
		return FaxTransferConverter.getAsOutboxTransferObject(faxOutbound.getFaxAccount(), faxOutbound);
	}

	public void sendQueuedFaxes()
	{
		List<FaxOutbound> queuedFaxList = faxOutboundDao.findByStatus(FaxOutbound.Status.QUEUED);
		for(FaxOutbound queuedFax : queuedFaxList)
		{
			try
			{
				sendQueuedFax(queuedFax);
			}
			catch(FaxSendException e)
			{
				logger.warn("Failed to send queued fax (id:"+queuedFax.getId()+"):"+ e.getMessage());
			}
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

	/**
	 * add entries to the outgoing fax table with the queued status.
	 * @return - the fax objects that were added
	 * @throws IOException - if a fax file cannot be moved to the pending folder
	 */
	private FaxOutbound queueFax(String providerId, Integer demographicId, FaxAccount faxAccount, String faxNumber,
	                      FaxOutbound.FileType fileType, GenericFile fileToFax) throws IOException
	{
		FaxOutbound faxOutbound = new FaxOutbound();
		faxOutbound.setStatus(FaxOutbound.Status.QUEUED);
		faxOutbound.setFaxAccount(faxAccount);
		faxOutbound.setSentTo(faxNumber);
		faxOutbound.setExternalAccountType(FaxAccount.INTEGRATION_TYPE_SRFAX);
		faxOutbound.setExternalAccountId(faxAccount.getLoginId());
		faxOutbound.setFileType(fileType);
		faxOutbound.setFileName(fileToFax.getName());
		faxOutbound.setCreatedAt(new Date());
		faxOutbound.setProviderNo(providerId);
		faxOutbound.setDemographicNo(demographicId);
		faxOutboundDao.persist(faxOutbound);

		fileToFax.moveToOutgoingFaxPending();

		return faxOutbound;
	}

	private void sendQueuedFax(FaxOutbound faxOutbound) throws FaxSendException
	{
		String logStatus = LogConst.STATUS_FAILURE;
		String logData = null;
		try
		{
			FaxAccount faxAccount = faxOutbound.getFaxAccount();
			SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());

			String coverLetterOption = faxAccount.getCoverLetterOption();
			if(coverLetterOption == null || !SRFaxApiConnector.validCoverLetterNames.contains(coverLetterOption))
			{
				coverLetterOption = null;
			}

			HashMap<String, String> fileMap = new HashMap<>(1);
			GenericFile fileToFax = FileFactory.getOutboundPendingFaxFile(faxOutbound.getFileName());
			fileMap.put(fileToFax.getName(), toBase64String(fileToFax));

			// external api call
			SingleWrapper<Integer> resultWrapper = apiConnector.Queue_Fax(
					faxAccount.getReplyFaxNumber(),
					faxAccount.getEmail(),
					SRFaxApiConnector.FAX_TYPE_SINGLE,
					faxOutbound.getSentTo(),
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

			try
			{
				faxOutbound.setExternalAccountId(faxAccount.getLoginId());
				faxOutbound.setExternalAccountType(faxAccount.getIntegrationType());
				faxOutbound.setFaxAccount(faxAccount);

				if(sendSuccess)
				{
					logger.info("Fax send success " + String.valueOf(resultWrapper.getResult()));
					faxOutbound.setStatus(FaxOutbound.Status.SENT);
					faxOutbound.setExternalReferenceId(resultWrapper.getResult().longValue());
					logStatus = LogConst.STATUS_SUCCESS;
					logData = "Faxed To: " + faxOutbound.getSentTo();
					fileToFax.moveToOutgoingFaxSent();
				}
				else
				{
					logger.warn("Fax send failure " + resultWrapper.getError());
					faxOutbound.setStatus(FaxOutbound.Status.ERROR);
					logData = resultWrapper.getError();
					fileToFax.moveToOutgoingFaxUnsent();
				}
			}
			catch(IOException e)
			{
				// don't throw exceptions here.
				// fax may have been sent in which case we want to report a success
				logger.error("IOError", e);
			}
			faxOutboundDao.merge(faxOutbound);
		}
		catch(FaxApiException e)
		{
			logData = e.getMessage();
			throw new FaxSendException(e);
		}
		catch(Exception e)
		{
			logger.error("Unknown error sending queued fax", e);
			logData = "System Error";
			throw new FaxSendException(e);
		}
		finally
		{
			LogAction.addLogEntry(faxOutbound.getProviderNo(), faxOutbound.getDemographicNo(), LogConst.ACTION_SENT, LogConst.CON_FAX,
					logStatus, String.valueOf(faxOutbound.getId()), null, logData);
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
