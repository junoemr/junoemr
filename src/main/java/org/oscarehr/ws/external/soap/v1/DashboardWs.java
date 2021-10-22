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
package org.oscarehr.ws.external.soap.v1;

import org.apache.cxf.annotations.GZIP;
import org.oscarehr.dashboard.dao.BillingMasterClinicaidDao;
import org.oscarehr.dashboard.model.BillingMasterClinicaid;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.common.annotation.SkipContentLoggingInbound;
import org.oscarehr.ws.external.soap.v1.transfer.BillingTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class DashboardWs extends AbstractWs
{
	public final String ADD_BILLING_STATUS_OK="ok";
	public final String ADD_BILLING_STATUS_FAILED="failed";
	public final String ADD_BILLING_STATUS_DUPLICATE="duplicate";

	@Autowired
	BillingMasterClinicaidDao billingMasterClinicaidDao;

	@Autowired
	DemographicDao demographicDao;

	//add a record to the, billingmaster_clinicaid table (used for dashboard reports)
	@SkipContentLoggingInbound
	public String addBillingRecord(BillingTransfer billingTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.BILLING_CREATE);

		return saveBillingTransfer(billingTransfer);
	}

	//add a records to the, billingmaster_clinicaid table (used for dashboard reports)
	@SkipContentLoggingInbound
	public String[] addBillingRecords(BillingTransfer[] billingTransfers)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.BILLING_CREATE);

		ArrayList<String> result_list = new ArrayList<>();
		for(BillingTransfer billingTransfer: billingTransfers)
		{
			result_list.add(saveBillingTransfer(billingTransfer));
		}

		return result_list.toArray(new String[0]);
	}

	private String saveBillingTransfer(BillingTransfer billingTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.BILLING_CREATE);

		try
		{
			//check for existing record
			BillingMasterClinicaid billingRecord = null;
			BillingMasterClinicaid existingRecord = billingMasterClinicaidDao.getRecordByUniqueIndex(
					billingTransfer.getSequenceNo(), billingTransfer.getInvoiceCreationYear(), billingTransfer.getDataCenterNo());
			if (existingRecord != null)
			{// update existing record
				billingRecord = existingRecord;

			}
			else
			{// create new record
				billingRecord = new BillingMasterClinicaid();
			}
			billingTransfer.copyTo(billingRecord);

			if ((billingRecord.getDemographicNo() == null || billingRecord.getDemographicNo() == 0) && billingRecord.getPhn() != null)
			{
				//attempt to map to demographic by health number
				DemographicCriteriaSearch demographicCs = new DemographicCriteriaSearch();
				demographicCs.setHin(billingRecord.getPhn());
				List<Demographic> demographicResults = demographicDao.criteriaSearch(demographicCs);
				if (demographicResults.size() == 1)
				{
					billingRecord.setDemographicNo(demographicResults.get(0).getId());
				}
				else
				{
					MiscUtils.getLogger().warn("failed to map billing data. hin: [" + billingRecord.getPhn() + "] does not map to unique demographic");
				}
			}

			if (billingRecord.getDemographicNo() == null)
			{
				MiscUtils.getLogger().warn("billing data [" +
						billingRecord.getSequenceNo() + "-" + billingRecord.getInvoiceCreationYear() + "-" + billingRecord.getDataCenterNo() + "]" +
						" is not mapped to a demographic");
			}

			billingMasterClinicaidDao.merge(billingRecord);

			return ADD_BILLING_STATUS_OK;
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Unknown Error while saving billing transfer to DB: " + e.getMessage() + " \n " + e.getStackTrace().toString());
			return ADD_BILLING_STATUS_FAILED;
		}

	}

}