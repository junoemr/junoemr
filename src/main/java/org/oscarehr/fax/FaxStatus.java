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
package org.oscarehr.fax;

import org.oscarehr.common.server.ServerStateHandler;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.preferences.SystemPreferenceConstants;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;

@Component
public class FaxStatus
{
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final boolean outboundPropEnabled = props.isPropertyActive("fax.outbound.enabled");
	private static final boolean inboundPropEnabled = props.isPropertyActive("fax.inbound.enabled");

	@Autowired
	private ServerStateHandler serverStateHandler;

	@Autowired
	private FaxAccountDao faxAccountDao;

	@Autowired
	private SystemPreferenceService systemPreferenceService;

	public boolean canSendFaxes()
	{
		// the master setting must be enabled.
		Boolean masterSettingEnabled = systemPreferenceService.isPreferenceEnabled(SystemPreferenceConstants.MASTER_FAX_ENABLED_OUTBOUND, false);
		if(outboundPropEnabled && masterSettingEnabled)
		{
			// at least one fax account must have the outgoing route turned on
			FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
			criteriaSearch.setIntegrationEnabledStatus(true);
			criteriaSearch.setOutboundEnabledStatus(true);

			int activeCount = faxAccountDao.criteriaSearchCount(criteriaSearch);
			return (activeCount > 0);
		}
		return false;
	}

	public boolean canSendFaxesAndIsMaster()
	{
		// the server must be in the master state
		return canSendFaxes() && serverStateHandler.isThisServerMaster();
	}

	public boolean canPullFaxes()
	{
		// the master setting must be enabled.
		Boolean masterSettingEnabled = systemPreferenceService.isPreferenceEnabled(SystemPreferenceConstants.MASTER_FAX_ENABLED_INBOUND, false);
		if(inboundPropEnabled && masterSettingEnabled)
		{
			// at least one fax account must have the incoming route turned on
			FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
			criteriaSearch.setIntegrationEnabledStatus(true);
			criteriaSearch.setInboundEnabledStatus(true);

			int activeCount = faxAccountDao.criteriaSearchCount(criteriaSearch);
			return (activeCount > 0);
		}
		return false;
	}

	public boolean canPullFaxesAndIsMaster()
	{
		// the server must be in the master state
		return canPullFaxes() && serverStateHandler.isThisServerMaster();
	}
}
