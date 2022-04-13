package org.oscarehr.fax.schedulingTasks;

import org.apache.log4j.Logger;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxAccountConnectionStatus;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.fax.provider.FaxProviderFactory;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class KeepAliveFaxTask
{
	@Autowired
	FaxAccountService faxAccountService;

	private static final Logger logger = MiscUtils.getLogger();
	private static final long thirtyMinutes = 1000L * 60L * 30L;

	@Scheduled(fixedRate = thirtyMinutes)
	public void keepAlive()
	{
		List<FaxAccount> activeAccounts = faxAccountService.getActiveFaxAccounts();
		for (FaxAccount faxAccount : activeAccounts)
		{
			FaxAccountProvider accountProvider = FaxProviderFactory.createFaxAccountProvider(faxAccount);
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