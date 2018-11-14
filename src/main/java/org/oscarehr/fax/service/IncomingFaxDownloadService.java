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

import org.apache.log4j.Logger;
import org.oscarehr.fax.dao.FaxAccountDao;
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

import java.time.LocalDate;
import java.util.List;

/**
 * This service should be responsible for handling logic around downloading faxes from external API sources
 * This class handles exceptions and should not be run as a transaction
 * @see IncomingFaxService IncomingFaxService for transactional services
 */
@Service
@Transactional(propagation = Propagation.NEVER)
public class IncomingFaxDownloadService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final int faxDaysPast = Integer.parseInt(props.getProperty("fax.inbound_days_past", "1"));

	@Autowired
	private FaxAccountDao faxAccountDao;

	@Autowired
	private IncomingFaxService faxServiceTransactional;

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
				String referenceIdStr = filename.split("\\|")[1];
				Long referenceId = Long.parseLong(referenceIdStr);

				// for each new fax to get, call api and request document.
				SingleWrapper<String> getDocResultWrapper = srFaxApiConnector.Retrieve_Fax(null,
						referenceIdStr,
						SRFaxApiConnector.RETRIEVE_DIRECTION_IN,
						SRFaxApiConnector.RETRIEVE_DOC_FORMAT,
						SRFaxApiConnector.RETRIEVE_DONT_CHANGE_STATUS,
						SRFaxApiConnector.RESPONSE_FORMAT_JSON,
						null);

				if(getDocResultWrapper.isSuccess())
				{
					// save document to input stream
					FaxInbound faxInbound = faxServiceTransactional.saveFaxDocument(faxAccount, referenceId, getDocResultWrapper.getResult());
					inboundId = String.valueOf(faxInbound.getId());
					logStatus = LogConst.STATUS_SUCCESS;

					// mark the fax as downloaded if the download/save is successful
					SingleWrapper<String> markReadResultWrapper = srFaxApiConnector.Update_Viewed_Status(null,
							referenceIdStr,
							SRFaxApiConnector.RETRIEVE_DIRECTION_IN,
							SRFaxApiConnector.MARK_AS_READ,
							SRFaxApiConnector.RESPONSE_FORMAT_JSON);
					if(!markReadResultWrapper.isSuccess())
					{
						logger.error("Failed to mark fax("+referenceIdStr+") as read. " + markReadResultWrapper.getError());
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
}
