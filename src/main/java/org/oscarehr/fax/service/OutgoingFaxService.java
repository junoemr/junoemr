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
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.fax.exception.FaxNumberException;
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
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class OutgoingFaxService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();

	private static HashMap<Long, Integer> faxAttemptCounterMap = new HashMap<>();
	private static int MAX_SEND_COUNT = Integer.parseInt(props.getProperty("fax.max_send_attempts", "5"));

	@Autowired
	FaxOutboundDao faxOutboundDao;

	@Autowired
	FaxAccountDao faxAccountDao;

	@Autowired
	FaxAccountService faxAccountService;

	public boolean isOutboundFaxEnabled()
	{
		return (isIntegratedFaxEnabled() || isLegacyFaxEnabled());
	}
	protected boolean isIntegratedFaxEnabled()
	{
		List<FaxAccount> faxAccountList = faxAccountDao.findByActiveOutbound(true, true);
		return !faxAccountList.isEmpty();
	}
	protected boolean isLegacyFaxEnabled()
	{
		return props.isFaxEnabled();
	}

	/** Remove duplicate phone numbers and all non digit characters from fax numbers. */
	public static HashSet<String> preProcessFaxNumbers(String...faxNumberList)
	{
		HashSet<String> recipients = new HashSet<>(faxNumberList.length);
		for(String faxNumber : faxNumberList)
		{
			String formattedFaxNo = faxNumber.trim().replaceAll("\\D", "");
			if(formattedFaxNo.length() < 10)
			{
				throw new FaxNumberException("Invalid Fax Number: " + faxNumber);
			}
			if(formattedFaxNo.length() == 10)
			{
				formattedFaxNo = "1" + formattedFaxNo;
			}
			recipients.add(formattedFaxNo);
		}
		return recipients;
	}

	/**
	 * Send a fax with the default fax account
	 * @throws IOException
	 */
	public FaxOutboxTransferOutbound sendFax(String providerId, Integer demographicId, String faxNumber, FaxOutbound.FileType fileType, GenericFile fileToFax) throws IOException
	{
		FaxAccount faxAccount = faxAccountService.getDefaultFaxAccount();
		return sendFax(faxAccount, providerId, demographicId, faxNumber, fileType, fileToFax);
	}

	/**
	 * Send a fax with the given fax account
	 * @throws IOException
	 */
	public FaxOutboxTransferOutbound sendFax(FaxAccount faxAccount, String providerId, Integer demographicId, String faxNumber, FaxOutbound.FileType fileType, GenericFile fileToFax) throws IOException
	{
		FaxOutboxTransferOutbound transfer;
		// check for enabled fax routes
		if(isIntegratedFaxEnabled())
		{
			FaxOutbound faxOutbound = queueNewFax(providerId, demographicId, faxAccount, faxNumber, fileType, fileToFax);
			sendQueuedFax(faxOutbound, fileToFax);
			transfer = FaxTransferConverter.getAsOutboxTransferObject(faxAccount, faxOutbound);
		}
		// if legacy faxing is enabled, write to the outgoing folder.
		else if(isLegacyFaxEnabled())
		{
			writeToFaxOutgoing(faxNumber, fileToFax);
			// fake a transfer object to return
			transfer = new FaxOutboxTransferOutbound();
			transfer.setSystemStatus(FaxOutbound.Status.QUEUED.name());
			transfer.setToFaxNumber(faxNumber);
			transfer.setFileType(fileType.name());
			transfer.setDemographicNo(demographicId);
			transfer.setProviderNo(providerId);
		}
		else
		{
			throw new RuntimeException("No outbound fax routes enabled!");
		}
		return transfer;
	}

	/**
	 * re-queue an existing outbound fax and attempt to resend it.
	 * @return - the updated transfer object
	 * @throws IOException - if the file to be re-sent encounters a file system error
	 */
	public FaxOutboxTransferOutbound resendFax(Long faxOutId) throws IOException
	{
		FaxOutbound faxOutbound = faxOutboundDao.find(faxOutId);
		GenericFile fileToResend;

		if(faxOutbound.isStatusQueued())
		{
			fileToResend = FileFactory.getOutboundPendingFaxFile(faxOutbound.getFileName());
		}
		else if(faxOutbound.isStatusError())
		{
			fileToResend = FileFactory.getOutboundUnsentFaxFile(faxOutbound.getFileName());
		}
		else
		{
			throw new RuntimeException("Attempt to resend fax with invalid status: " + faxOutbound.getStatus().name());
		}
		fileToResend.moveToOutgoingFaxPending();
		faxOutbound.setStatus(FaxOutbound.Status.QUEUED);

		sendQueuedFax(faxOutbound, fileToResend);
		return FaxTransferConverter.getAsOutboxTransferObject(faxOutbound.getFaxAccount(), faxOutbound);
	}

	/**
	 * find a list of all queued faxes and attempt to send them.
	 */
	public void sendQueuedFaxes()
	{
		// get a list of queued faxes filtered by accounts with active outbound faxing
		List<FaxOutbound> queuedFaxList = faxOutboundDao.findActiveQueued();
		for(FaxOutbound queuedFax : queuedFaxList)
		{
			GenericFile fileToSend;
			try
			{
				fileToSend = FileFactory.getOutboundUnsentFaxFile(queuedFax.getFileName());
			}
			catch(IOException e)
			{
				// ignore this error and continue, can't rollback previous object fax status updates
				logger.error("IOError", e);
				continue;
			}
			sendQueuedFax(queuedFax, fileToSend);
		}
	}

	public GenericFile getFile(long faxOutId) throws IOException
	{
		FaxOutbound faxOutbound = faxOutboundDao.find(faxOutId);
		GenericFile file;
		if(faxOutbound.isStatusQueued())
		{
			file = FileFactory.getOutboundPendingFaxFile(faxOutbound.getFileName());
		}
		else if(faxOutbound.isStatusError())
		{
			file = FileFactory.getOutboundUnsentFaxFile(faxOutbound.getFileName());
		}
		else
		{
			file = FileFactory.getOutboundSentFaxFile(faxOutbound.getFileName());
		}
		return file;
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
	private FaxOutbound queueNewFax(String providerId, Integer demographicId, FaxAccount faxAccount, String faxNumber,
	                                FaxOutbound.FileType fileType, GenericFile fileToFax) throws IOException
	{
		FaxOutbound faxOutbound = new FaxOutbound();
		faxOutbound.setStatus(FaxOutbound.Status.QUEUED);
		faxOutbound.setFaxAccount(faxAccount);
		faxOutbound.setSentTo(faxNumber);
		faxOutbound.setExternalAccountType(faxAccount.getIntegrationType());
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

	private void sendQueuedFax(FaxOutbound faxOutbound, GenericFile fileToFax)
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
					faxOutbound.setStatusMessage(null);
					faxOutbound.setExternalReferenceId(resultWrapper.getResult().longValue());
					logStatus = LogConst.STATUS_SUCCESS;
					logData = "Faxed To: " + faxOutbound.getSentTo();
					fileToFax.moveToOutgoingFaxSent();
				}
				else
				{
					logger.warn("Fax send failure " + resultWrapper.getError());
					faxOutbound.setStatus(FaxOutbound.Status.ERROR);
					faxOutbound.setStatusMessage(resultWrapper.getError());
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
		}
		catch(FaxApiConnectionException e)
		{
			// if the api connection fails, leave the fax as queued
			logger.warn("Fax API failure: " + e.getMessage());
			logData = e.getMessage();
			faxOutbound.setStatusMessage(e.getMessage());

			// if the maximum sent attempts has been hit, set the error status.
			if(faxAttemptCounterMap.get(faxOutbound.getId()) >= MAX_SEND_COUNT)
			{
				faxOutbound.setStatus(FaxOutbound.Status.ERROR);
				faxOutbound.setStatusMessage(e.getMessage());
			}
		}
		catch(FaxApiValidationException e)
		{
			logger.warn("Fax API failure: " + e.getMessage());
			logData = e.getMessage();
			faxOutbound.setStatus(FaxOutbound.Status.ERROR);
			faxOutbound.setStatusMessage(e.getMessage());
		}
		catch(Exception e)
		{
			logger.error("Unknown error sending queued fax", e);
			logData = "System Error";
			faxOutbound.setStatus(FaxOutbound.Status.ERROR);
			faxOutbound.setStatusMessage(e.getMessage());
		}
		finally
		{
			faxOutboundDao.merge(faxOutbound);
			LogAction.addLogEntry(faxOutbound.getProviderNo(), faxOutbound.getDemographicNo(), LogConst.ACTION_SENT, LogConst.CON_FAX,
					logStatus, String.valueOf(faxOutbound.getId()), null, logData);
			updateSendAttempts(faxOutbound);

			// make sure errors move the file to the error folder
			if(faxOutbound.isStatusError())
			{
				try
				{
					fileToFax.moveToOutgoingFaxUnsent();
				}
				catch(IOException e)
				{
					logger.error("IOError", e);
				}
			}
		}
	}

	private void updateSendAttempts(FaxOutbound faxOutbound)
	{
		Long faxId = faxOutbound.getId();
		if(faxOutbound.isStatusQueued())
		{
			Integer count = faxAttemptCounterMap.get(faxId);
			count = (count == null)? 0 : count;
			faxAttemptCounterMap.put(faxId, count+1);
		}
		else
		{
			// clear out the map value if the fax is no longer queued
			faxAttemptCounterMap.remove(faxId);
		}
	}

	private String toBase64String(GenericFile file) throws IOException
	{
		byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file.getFileObject()));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
