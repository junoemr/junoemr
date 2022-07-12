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

package org.oscarehr.fax.schedulingTasks;

import org.apache.log4j.Logger;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxAccountConnectionStatus;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.fax.provider.FaxProviderFactory;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.preferences.SystemPreferenceConstants;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class KeepAliveFaxTask
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final long thirtyMinutes = 1000L * 60L * 30L;

	@Autowired
	protected FaxAccountService faxAccountService;

	@Autowired
	protected SystemPreferenceService systemPreferenceService;

	@Scheduled(fixedRate = thirtyMinutes)
	public void keepAlive()
	{
		Boolean masterInSettingEnabled = systemPreferenceService.isPreferenceEnabled(SystemPreferenceConstants.MASTER_FAX_ENABLED_INBOUND, false);
		Boolean masterOutSettingEnabled = systemPreferenceService.isPreferenceEnabled(SystemPreferenceConstants.MASTER_FAX_ENABLED_OUTBOUND, false);

		// only attempt connections when faxing is not disabled by master settings
		if(masterInSettingEnabled || masterOutSettingEnabled)
		{
			List<FaxAccount> activeAccounts = faxAccountService.getActiveFaxAccounts();
			for(FaxAccount faxAccount : activeAccounts)
			{
				FaxAccountProvider accountProvider = FaxProviderFactory.createFaxAccountProvider(faxAccount);
				if(accountProvider.requiresKeepAlive())
				{
					FaxAccountConnectionStatus accountStatus = accountProvider.testConnectionStatus();
					if(accountStatus != FaxAccountConnectionStatus.SUCCESS)
					{
						logger.warn(String.format("Fax account %d (%s) has status [%s]",
								faxAccount.getId(),
								faxAccount.getIntegrationType(),
								accountStatus.name()));
					}
				}
			}
		}
	}
}