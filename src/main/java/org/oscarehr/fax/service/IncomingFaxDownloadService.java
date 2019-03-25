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
import org.oscarehr.fax.FaxStatus;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxInboxResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxInbound;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.provider.model.ProviderData;
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
	private IncomingFaxService incomingFaxService;

	@Autowired
	private FaxStatus faxStatus;

	public void pullNewFaxes()
	{
		if(faxStatus.canPullFaxesAndIsMaster())
		{
			FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
			criteriaSearch.setIntegrationEnabledStatus(true);
			criteriaSearch.setInboundEnabledStatus(true);
			List<FaxAccount> faxAccountList = faxAccountDao.criteriaSearch(criteriaSearch);

			String startDate = ConversionUtils.toDateString(LocalDate.now().minusDays(faxDaysPast), SRFaxApiConnector.DATE_FORMAT);
			String endDate = ConversionUtils.toDateString(LocalDate.now(), SRFaxApiConnector.DATE_FORMAT);

			for(FaxAccount faxAccount : faxAccountList)
			{
				try
				{
					SRFaxApiConnector srFaxApiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());

					// get list of un-downloaded faxes from external api
					ListWrapper<GetFaxInboxResult> listResultWrapper = srFaxApiConnector.getFaxInbox(
							SRFaxApiConnector.PERIOD_RANGE,
							startDate,
							endDate,
							SRFaxApiConnector.VIEWED_STATUS_UNREAD,
							null);

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
				String referenceIdStr = result.getDetailsId();

				// for each new fax to get, call api and request document.
				SingleWrapper<String> getDocResultWrapper = srFaxApiConnector.retrieveFax(null,
						referenceIdStr, SRFaxApiConnector.RETRIEVE_DIRECTION_IN);

				if(getDocResultWrapper.isSuccess())
				{
					// save document to input stream
					FaxInbound faxInbound = incomingFaxService.saveFaxDocument(faxAccount, result, getDocResultWrapper.getResult());
					inboundId = String.valueOf(faxInbound.getId());
					logStatus = LogConst.STATUS_SUCCESS;

					// mark the fax as downloaded if the download/save is successful
					SingleWrapper<String> markReadResultWrapper = srFaxApiConnector.updateViewedStatus(null,
							referenceIdStr,
							SRFaxApiConnector.RETRIEVE_DIRECTION_IN,
							SRFaxApiConnector.MARK_AS_READ);
					if(!markReadResultWrapper.isSuccess())
					{
						logger.error("Failed to mark fax("+referenceIdStr+") as read. " + markReadResultWrapper.getError());
						logData = "Failed to mark fax as read: " + markReadResultWrapper.getError();
					}
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
				LogAction.addLogEntry(ProviderData.SYSTEM_PROVIDER_NO, null, LogConst.ACTION_DOWNLOAD, LogConst.CON_FAX,
						logStatus, inboundId, null, logData);
			}
		}
	}
}
