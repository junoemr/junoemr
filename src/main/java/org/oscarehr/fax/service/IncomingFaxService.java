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
import org.apache.log4j.Logger;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxInboxResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxInbound;
import org.oscarehr.util.MiscUtils;
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
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * This service should be responsible for handling all logic around receiving faxes
 */
@Service
@Transactional(propagation=Propagation.NOT_SUPPORTED)
public class IncomingFaxService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final int faxDaysPast = Integer.parseInt(props.getProperty("fax.inbound_days_past", "1"));

	@Autowired
	private FaxAccountDao faxAccountDao;

	@Autowired
	private FaxInboundDao faxInboundDao;

	@Autowired
	private DocumentService documentService;

	public void pullNewFaxes()
	{
		List<FaxAccount> faxAccountList = faxAccountDao.findByActiveInbound(true, true);
		String dateFormat = "yyyyMMdd";
		String startDate = ConversionUtils.toDateString(LocalDate.now().minusDays(faxDaysPast), dateFormat);
		String endDate = ConversionUtils.toDateString(LocalDate.now(), dateFormat);
		logger.info("Date range: '" + startDate + "' -> '" + endDate + "'");

		for(FaxAccount faxAccount : faxAccountList)
		{
			try
			{
				SRFaxApiConnector srFaxApiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());

				// get list of un-downloaded faxes from external api
				ListWrapper<GetFaxInboxResult> listResultWrapper = srFaxApiConnector.Get_Fax_Inbox(
						SRFaxApiConnector.RESPONSE_FORMAT_JSON,
						SRFaxApiConnector.PERIOD_RANGE,
						startDate,
						endDate,
						SRFaxApiConnector.VIEWED_STATUS_UNREAD,
						null);
				logger.info(listResultWrapper.toString()); //TODO - remove when done

				if(listResultWrapper.isSuccess())
				{
					handleResults(faxAccount, srFaxApiConnector, listResultWrapper);
				}
				else
				{
					logger.warn("API Failure: " + listResultWrapper.getError());
				}
			}
			catch(FaxApiConnectionException e)
			{
				logger.warn("Fax API connection error: " + e.getMessage());
			}
			catch(FaxApiValidationException e)
			{
				logger.warn("Fax API validation error: " + e.getMessage());
			}
			catch(Exception e)
			{
				logger.error("Unexpected Inbound Fax Error", e);
			}
		}
	}

	private void handleResults(FaxAccount faxAccount, SRFaxApiConnector srFaxApiConnector, ListWrapper<GetFaxInboxResult> listResultWrapper)
	{
		for(GetFaxInboxResult result : listResultWrapper.getResult())
		{
			String logStatus = LogConst.STATUS_FAILURE;
			String logData = null;
			String inboundId = null;
			try
			{
				String filename = result.getFileName();
				String referenceId = filename.split("\\|")[1];

				// for each new fax to get, call api and request document.
				SingleWrapper<String> getDocResultWrapper = srFaxApiConnector.Retrieve_Fax(null,
						referenceId,
						SRFaxApiConnector.RETRIEVE_DIRECTION_IN,
						SRFaxApiConnector.RETRIEVE_DOC_FORMAT,
						SRFaxApiConnector.RETRIEVE_DONT_CHANGE_STATUS,
						SRFaxApiConnector.RESPONSE_FORMAT_JSON,
						null);

				if(getDocResultWrapper.isSuccess())
				{
					// save document to input stream
					FaxInbound faxInbound = saveFaxDocument(faxAccount, referenceId, getDocResultWrapper.getResult());
					inboundId = String.valueOf(faxInbound.getId());
					logStatus = LogConst.STATUS_SUCCESS;

					// mark the fax as downloaded if the download/save is successful
					SingleWrapper<String> markReadResultWrapper = srFaxApiConnector.Update_Viewed_Status(null,
							referenceId,
							SRFaxApiConnector.RETRIEVE_DIRECTION_IN,
							SRFaxApiConnector.MARK_AS_READ,
							SRFaxApiConnector.RESPONSE_FORMAT_JSON);
					if(!markReadResultWrapper.isSuccess())
					{
						logger.error("Failed to mark fax("+referenceId+") as read. " + markReadResultWrapper.getError());
						logData = "Failed to mark fax as read: " + markReadResultWrapper.getError();
					}
					logger.info(markReadResultWrapper.toString()); //TODO - remove when done
				}
				else
				{
					logger.warn("API Failure: " + getDocResultWrapper.getError());
					logData = getDocResultWrapper.getError();
				}
			}
			catch(FaxApiConnectionException e)
			{
				logger.warn("Fax API connection error: " + e.getMessage());
				logData = e.getMessage();
			}
			catch(FaxApiValidationException e)
			{
				logger.warn("Fax API validation error: " + e.getMessage());
				logData = e.getMessage();
			}
			catch(Exception e)
			{
				logger.error("Fax API Unknown Error", e);
				logData = e.getMessage();
			}
			finally
			{
				// log download attempt to security log
				LogAction.addLogEntry("-1", null, LogConst.ACTION_DOWNLOAD, LogConst.CON_FAX,
						logStatus, inboundId, null, logData);
			}
		}
	}

	/** force a new transaction due to inclusion of document upload process.
	 * If the document upload fails the transaction should roll back regardless of if the caller is in a transaction */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	protected FaxInbound saveFaxDocument(FaxAccount faxAccount, String referenceId, String result) throws IOException, InterruptedException
	{
		InputStream documentStream = base64ToStream(result);

		// upload a new document through document service
		Document document = new Document();
		document.setSource(faxAccount.getIntegrationType());
		document.setDoccreator("-1");
		document.setResponsible("-1");
		document.setDocdesc("Incoming Fax");


		document = documentService.uploadNewDemographicDocument(document, documentStream, null);

		// create a record in the fax_inbound table
		FaxInbound faxInbound = new FaxInbound();
		faxInbound.setCreatedAt(new Date());
		faxInbound.setDocument(document);
		faxInbound.setFaxAccount(faxAccount);
		faxInbound.setExternalAccountId(faxAccount.getLoginId());
		faxInbound.setExternalAccountType(faxAccount.getIntegrationType());
		faxInbound.setExternalReferenceId(Long.parseLong(referenceId));
		faxInboundDao.persist(faxInbound);
		return faxInbound;
	}

	private InputStream base64ToStream(String base64String)
	{
		return new ByteArrayInputStream(Base64.decodeBase64(base64String));
	}
}
