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
import org.oscarehr.fax.exception.FaxApiResultException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.fax.result.FaxInboxResult;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxInbound;
import org.oscarehr.fax.provider.FaxDownloadProvider;
import org.oscarehr.fax.provider.FaxProviderFactory;
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

import java.util.List;

/**
 * This service should be responsible for handling logic around downloading faxes from external API sources
 * This class handles exceptions and should not be run as a transaction
 * @see IncomingFaxService IncomingFaxService for transactional services
 */
@Service
@Transactional(propagation = Propagation.NEVER)
public class FaxDownloadService
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

	@Autowired
	private FaxProviderFactory faxProviderFactory;

	/**
	 * Pulls, downloads and saves any new faxes for each active inbound fax account
	 */
	public void pullNewFaxes()
	{
		if(faxStatus.canPullFaxesAndIsMaster())
		{
			FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
			criteriaSearch.setIntegrationEnabledStatus(true);
			criteriaSearch.setInboundEnabledStatus(true);
			List<FaxAccount> faxAccountList = faxAccountDao.criteriaSearch(criteriaSearch);
			FaxDownloadProvider faxDownloadProvider;

			for(FaxAccount faxAccount : faxAccountList)
			{
				try
				{
					faxDownloadProvider = faxProviderFactory.createFaxDownloadProvider(faxAccount);
					List<? extends FaxInboxResult> faxInboxResults = faxDownloadProvider.getFaxInbox(faxDaysPast);
					handleResults(faxAccount, faxInboxResults);
				}
				catch(FaxApiConnectionException e)
				{
					logger.warn("Fax API connection error: " + e.getMessage());
				}
				catch(FaxApiValidationException e)
				{
					logger.warn("Fax API validation error: " + e.getMessage());
				}
				catch(FaxApiResultException e)
				{
					logger.warn("Fax API result error: " + e.getMessage());
				}
				catch(Exception e)
				{
					logger.error("Unexpected Inbound Fax Error", e);
				}
			}
		}
	}

	/**
	 * Handles each result in inboxResults by retrieving the fax file, saving the fax and marking as downloaded
	 * @param faxAccount Fax account these inbox results are pulled from
	 * @param inboxResults Fax inbox results to handle
	 */
	private void handleResults(FaxAccount faxAccount, List<? extends FaxInboxResult> inboxResults)
	{
		for(FaxInboxResult result : inboxResults)
		{
			String logStatus = LogConst.STATUS_FAILURE;
			String logData = null;
			String inboundId = null;
			try
			{
				String referenceIdStr = result.getDetailsId();
				FaxDownloadProvider faxDownloadProvider = faxProviderFactory.createFaxDownloadProvider(faxAccount);

				// for each new fax to get, call api and request document.
				String faxContent = faxDownloadProvider.retrieveFax(referenceIdStr);

				// save document to input stream
				FaxInbound faxInbound = incomingFaxService.saveFaxDocument(faxAccount, result, faxContent);
				inboundId = String.valueOf(faxInbound.getId());
				logStatus = LogConst.STATUS_SUCCESS;

				// mark the fax as downloaded if the download/save is successful
				faxDownloadProvider.markAsDownloaded(referenceIdStr);
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
			catch (FaxApiResultException e) {
				logger.warn("Fax API result error: " + e.getMessage());
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