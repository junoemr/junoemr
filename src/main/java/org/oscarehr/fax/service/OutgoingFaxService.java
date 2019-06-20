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
import org.oscarehr.fax.FaxStatus;
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.fax.exception.FaxException;
import org.oscarehr.fax.exception.FaxNumberException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxStatusResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
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
import oscar.util.ConversionUtils;

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
	private static final String STATUS_MESSAGE_IN_TRANSIT = "Sending";
	private static final String STATUS_MESSAGE_COMPLETED = "Success";
	private static final String DEFAULT_MAX_SEND_COUNT = "5";

	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();

	private static HashMap<Long, Integer> faxAttemptCounterMap = new HashMap<>();
	private static int MAX_SEND_COUNT = Integer.parseInt(props.getProperty("fax.max_send_attempts", DEFAULT_MAX_SEND_COUNT));

	@Autowired
	private ProviderDataDao providerDataDao;

	@Autowired
	private FaxOutboundDao faxOutboundDao;

	@Autowired
	private FaxAccountService faxAccountService;

	@Autowired
	private FaxStatus faxStatus;

	public boolean isOutboundFaxEnabled()
	{
		return (faxStatus.canSendFaxes() || isLegacyFaxEnabled());
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
			if(formattedFaxNo.length() == 10)
			{
				formattedFaxNo = "1" + formattedFaxNo;
			}
			if(formattedFaxNo.length() != 11)
			{
				throw new FaxNumberException("Invalid Fax Number: " + faxNumber, "fax.exception.invalidFaxNumber");
			}
			recipients.add(formattedFaxNo);
		}
		return recipients;
	}

	/**
	 * Send a fax with the default fax account
	 * @throws IOException
	 */
	public FaxOutboxTransferOutbound queueAndSendFax(String providerId, Integer demographicId, String faxNumber, FaxOutbound.FileType fileType, GenericFile fileToFax) throws IOException, InterruptedException
	{
		FaxAccount faxAccount = faxAccountService.getDefaultFaxAccount();
		return queueAndSendFax(faxAccount, providerId, demographicId, faxNumber, fileType, fileToFax);
	}

	/**
	 * Send a fax with the given fax account
	 * @throws IOException
	 */
	public FaxOutboxTransferOutbound queueAndSendFax(FaxAccount faxAccount, String providerId, Integer demographicId, String faxNumber, FaxOutbound.FileType fileType, GenericFile fileToFax) throws IOException, InterruptedException
	{
		FaxOutboxTransferOutbound transfer;
		// check for enabled fax routes
		if(faxStatus.canSendFaxesAndIsMaster())
		{
			FaxOutbound faxOutbound = queueNewFax(providerId, demographicId, faxAccount, faxNumber, fileType, fileToFax);
			sendQueuedFax(faxOutbound, fileToFax);
			transfer = FaxTransferConverter.getAsOutboxTransferObject(faxAccount, faxOutbound);
		}
		// if legacy faxing is enabled, write to the outgoing folder.
		else if(isLegacyFaxEnabled())
		{
			writeToFaxOutgoing(faxNumber, fileToFax);
			LogAction.addLogEntry(providerId, demographicId, LogConst.ACTION_SENT, LogConst.CON_FAX,
					LogConst.STATUS_SUCCESS, null, null, "Faxed To: " + faxNumber);

			// fake a transfer object to return
			transfer = new FaxOutboxTransferOutbound();
			transfer.setSystemStatus(FaxOutbound.Status.SENT);
			transfer.setToFaxNumber(faxNumber);
			transfer.setFileType(fileType.name());
			transfer.setDemographicNo(demographicId);
			transfer.setProviderNo(providerId);
			transfer.setSystemStatusMessage("Using legacy fax system");
		}
		else
		{
			throw new IllegalStateException("No outbound fax routes enabled!");
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
		if(!faxStatus.canSendFaxesAndIsMaster())
		{
			throw new IllegalStateException("Invalid state for sending faxes.");
		}

		FaxOutbound faxOutbound = faxOutboundDao.find(faxOutId);
		GenericFile fileToResend;

		if(faxOutbound.isStatusQueued())
		{
			fileToResend = FileFactory.getOutboundPendingFaxFile(faxOutbound.getFileName());
		}
		else if(faxOutbound.isStatusError())
		{
			fileToResend = FileFactory.getOutboundUnsentFaxFile(faxOutbound.getFileName());
			fileToResend.moveToOutgoingFaxPending();
			faxOutbound.setStatusQueued();
		}
		else if(faxOutbound.isStatusSent()
				&& SRFaxApiConnector.RESPONSE_STATUS_FAILED.equalsIgnoreCase(faxOutbound.getExternalStatus()))
		{
			/*Here the fax was sent to the integration but failed remotely.
			* In this case, duplicate the fax record and send it again as a new copy.
			* Archive the old one to prevent multiple resend attempts by the user. */

			GenericFile fileToCopy = FileFactory.getOutboundSentFaxFile(faxOutbound.getFileName());
			fileToResend = FileFactory.copy(fileToCopy);
			faxOutbound.setArchived(true);
			faxOutboundDao.merge(faxOutbound);

			faxOutbound = queueNewFax(
					faxOutbound.getProviderNo(),
					faxOutbound.getDemographicNo(),
					faxOutbound.getFaxAccount(),
					faxOutbound.getSentTo(),
					faxOutbound.getFileType(),
					fileToResend);
		}
		else
		{
			throw new FaxException("Attempt to resend fax with invalid status: " + faxOutbound.getStatus().name());
		}

		sendQueuedFax(faxOutbound, fileToResend);
		return FaxTransferConverter.getAsOutboxTransferObject(faxOutbound.getFaxAccount(), faxOutbound);
	}

	public FaxOutboxTransferOutbound setNotificationStatus(Long faxOutId, String status)
	{
		FaxOutbound faxOutbound = faxOutboundDao.find(faxOutId);
		faxOutbound.setNotificationStatus(FaxOutbound.NotificationStatus.valueOf(status));
		faxOutboundDao.persist(faxOutbound);
		return FaxTransferConverter.getAsOutboxTransferObject(faxOutbound.getFaxAccount(), faxOutbound);
	}
	public FaxOutboxTransferOutbound setArchived(Long faxOutId, boolean isArchived)
	{
		FaxOutbound faxOutbound = faxOutboundDao.find(faxOutId);
		faxOutbound.setArchived(isArchived);
		faxOutboundDao.persist(faxOutbound);
		return FaxTransferConverter.getAsOutboxTransferObject(faxOutbound.getFaxAccount(), faxOutbound);
	}

	/**
	 * find a list of all queued faxes and attempt to send them.
	 */
	public void sendQueuedFaxes()
	{
		// get a list of queued faxes filtered by accounts with active outbound faxing
		List<FaxOutbound> queuedFaxList = faxOutboundDao.findActiveQueued();

		// only send faxes from a master server
		if(!queuedFaxList.isEmpty() && faxStatus.canSendFaxesAndIsMaster())
		{
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
				logger.info("Attempting to send queued fax: (id=" + queuedFax.getId() + ")");
				sendQueuedFax(queuedFax, fileToSend);
			}
		}
	}

	/**
	 * Ask remote sources for status updates on sent files that have not been completed.
	 * Update any records that have status changes
	 */
	public void requestPendingStatusUpdates()
	{
		FaxOutboundCriteriaSearch criteriaSearch = new FaxOutboundCriteriaSearch();
		criteriaSearch.setStatus(FaxOutbound.Status.SENT); // only check records with a local sent status
		criteriaSearch.setArchived(false); // ignore archived records
		criteriaSearch.setRemoteStatusList(SRFaxApiConnector.RESPONSE_STATUSES_FINAL, false);
		List<FaxOutbound> pendingList = faxOutboundDao.criteriaSearch(criteriaSearch);

		if(!pendingList.isEmpty() && faxStatus.canSendFaxesAndIsMaster())
		{
			for(FaxOutbound faxOutbound : pendingList)
			{
				logger.info("Checking status for outbound record id:" + faxOutbound.getId() +
						" (Current status: " + faxOutbound.getExternalStatus() + ")");
				FaxAccount faxAccount = faxOutbound.getFaxAccount();
				SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());
				SingleWrapper<GetFaxStatusResult> apiResult = apiConnector.getFaxStatus(String.valueOf(faxOutbound.getExternalReferenceId()));

				if(apiResult.isSuccess())
				{
					GetFaxStatusResult result = apiResult.getResult();
					String remoteSentStatus = result.getSentStatus();
					faxOutbound.setExternalStatus(result.getSentStatus());

					// if the remote status is sent, update accordingly.
					if(SRFaxApiConnector.RESPONSE_STATUS_SENT.equalsIgnoreCase(remoteSentStatus))
					{
						Date dateDelivered = ConversionUtils.fromDateString(result.getDateSent(), "MMM dd/yy hh:mm a");
						faxOutbound.setExternalDeliveryDate(dateDelivered);
						faxOutbound.setStatusMessage(STATUS_MESSAGE_COMPLETED);
						faxOutbound.setArchived(true);
					}
					else
					{
						faxOutbound.setStatusMessage(result.getErrorCode());
					}
					faxOutboundDao.merge(faxOutbound);
					logger.info("Updated Status to: " + remoteSentStatus);
				}
				else
				{
					logger.warn("SRFAX API Connection Failure: " + apiResult.getError());
				}
			}
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
	private void writeToFaxOutgoing(String faxNumber, GenericFile...filesToFax) throws IOException, InterruptedException
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
		ProviderData provider = providerDataDao.find(providerId);

		FaxOutbound faxOutbound = new FaxOutbound();
		faxOutbound.setStatusQueued();
		faxOutbound.setFaxAccount(faxAccount);
		faxOutbound.setSentTo(faxNumber);
		faxOutbound.setExternalAccountType(faxAccount.getIntegrationType());
		faxOutbound.setExternalAccountId(faxAccount.getLoginId());
		faxOutbound.setFileType(fileType);
		faxOutbound.setFileName(fileToFax.getName());
		faxOutbound.setCreatedAt(new Date());
		faxOutbound.setProvider(provider);
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
			SingleWrapper<Integer> resultWrapper = apiConnector.queueFax(
					faxAccount.getReplyFaxNumber(),
					faxAccount.getEmail(),
					faxOutbound.getSentTo(),
					fileMap,
					coverLetterOption
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
					faxOutbound.setStatusSent();
					faxOutbound.setStatusMessage(STATUS_MESSAGE_IN_TRANSIT);
					faxOutbound.setExternalReferenceId(resultWrapper.getResult().longValue());
					logStatus = LogConst.STATUS_SUCCESS;
					logData = "Faxed To: " + faxOutbound.getSentTo();
					fileToFax.moveToOutgoingFaxSent();
				}
				else
				{
					logger.warn("Fax send failure " + resultWrapper.getError());
					faxOutbound.setStatusError();
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
			faxOutbound.setStatusMessage(e.getUserFriendlyMessage());

			// if the maximum sent attempts has been hit, set the error status.
			if(faxAttemptCounterMap.get(faxOutbound.getId()) >= MAX_SEND_COUNT)
			{
				faxOutbound.setStatusError();
			}
		}
		catch(FaxApiValidationException e)
		{
			logger.warn("Fax API failure: " + e.getMessage());
			logData = e.getMessage();
			faxOutbound.setStatusError();
			faxOutbound.setStatusMessage(e.getUserFriendlyMessage());
		}
		catch(Exception e)
		{
			logger.error("Unknown error sending queued fax", e);
			logData = "System Error";
			faxOutbound.setStatusError();
			faxOutbound.setStatusMessage("Unknown error, please contact support");
		}
		finally
		{
			// status changes must be saved
			faxOutboundDao.merge(faxOutbound);
			// log the fax send attempt in the security log
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
