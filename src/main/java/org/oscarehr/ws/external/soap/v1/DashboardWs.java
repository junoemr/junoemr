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
import org.hibernate.exception.ConstraintViolationException;
import org.oscarehr.dashboard.dao.BillingMasterClinicaidDao;
import org.oscarehr.dashboard.model.BillingMasterClinicaid;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.BillingTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.persistence.PersistenceException;
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
	public String addBillingRecord(BillingTransfer billingTransfer)
	{
		return saveBillingTransfer(billingTransfer);
	}

	//add a records to the, billingmaster_clinicaid table (used for dashboard reports)
	public String[] addBillingRecords(BillingTransfer[] billingTransfers)
	{
		ArrayList<String> result_list = new ArrayList<>();
		for(BillingTransfer billingTransfer: billingTransfers)
		{
			result_list.add(saveBillingTransfer(billingTransfer));
		}

		return result_list.toArray(new String[0]);
	}

	private String saveBillingTransfer(BillingTransfer billingTransfer)
	{
		MiscUtils.getLogger().info(billingTransfer.toString());

		// save record
		BillingMasterClinicaid newBillingRecord = new BillingMasterClinicaid();
		billingTransfer.copyTo(newBillingRecord);
		try
		{
			if (newBillingRecord.getDemographicNo() == null && newBillingRecord.getPhn() != null)
			{
				//attempt to map to demographic by health number
				DemographicCriteriaSearch demographicCs = new DemographicCriteriaSearch();
				demographicCs.setHin(newBillingRecord.getPhn());
				List<Demographic> demographicResults = demographicDao.criteriaSearch(demographicCs);
				if (demographicResults.size() == 1)
				{
					newBillingRecord.setDemographicNo(demographicResults.get(0).getId());
				}
				else
				{
					MiscUtils.getLogger().warn("failed to map billing data. hin: [" + newBillingRecord.getPhn() + "] does not map to unique demographic");
				}
			}

			if (newBillingRecord.getDemographicNo() == null)
			{
				MiscUtils.getLogger().warn("billing data [" +
						newBillingRecord.getSequenceNo() + "-" + newBillingRecord.getInvoiceCreationYear() + "-" + newBillingRecord.getDataCenterNo() + "]" +
						" is not mapped to a demographic");
			}

			billingMasterClinicaidDao.persist(newBillingRecord);
			return ADD_BILLING_STATUS_OK;
		}
		catch (PersistenceException e)
		{
			Throwable cause = e.getCause();
			while (cause != null && !(cause instanceof ConstraintViolationException))
			{
				cause = cause.getCause();
			}

			if (cause != null)
			{
				MiscUtils.getLogger().warn("Attempt to insert duplicate record: " +
						newBillingRecord.getSequenceNo() + "-" + newBillingRecord.getInvoiceCreationYear() + "-" + newBillingRecord.getDataCenterNo());
				return ADD_BILLING_STATUS_DUPLICATE;
			}
			else
			{
				throw e;
			}
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Unknown Error while saving billing transfer to DB: " + e.getMessage() + " \n " + e.getStackTrace().toString());
			return ADD_BILLING_STATUS_FAILED;
		}

	}

}