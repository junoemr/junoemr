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

package org.oscarehr.billing.CA.service;

import org.oscarehr.billing.CA.transfer.EligibilityCheckTransfer;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.integration.clinicaid.service.ClinicaidAPIService;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanAPI;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanResponse;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanService;
import oscar.oscarBilling.ca.bc.Teleplan.TeleplanUserPassDAO;
import oscar.util.UtilDateUtilities;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
public class EligibilityCheckService
{
	private static final OscarProperties properties = OscarProperties.getInstance();

	public EligibilityCheckTransfer checkEligibility(Demographic demo) throws IOException, InterruptedException
	{
		EligibilityCheckTransfer transfer = new EligibilityCheckTransfer();

		if (properties.isClinicaidBillingType())
		{
			ClinicaidAPIService clinicaidAPIService = SpringUtils.getBean(ClinicaidAPIService.class);
			Map<String, String> clinicaidResponse = clinicaidAPIService.checkEligibility(demo);

			transfer.setError(clinicaidResponse.get("error"));
			transfer.setMessage(clinicaidResponse.get("msg"));
			transfer.setResult(clinicaidResponse.get("result"));
			transfer.setEligible(Boolean.valueOf(clinicaidResponse.get("isEligible")));
		}
		else if(properties.isBritishColumbiaBillingType())
		{
			TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
			String[] userpass = dao.getUsernamePassword();
			TeleplanService tService = new TeleplanService();
			Date billingDate = new Date();

			TeleplanAPI tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);

			String phn = demo.getHin();
			String dateofbirthyyyy = demo.getYearOfBirth();
			String dateofbirthmm = demo.getMonthOfBirth();
			String dateofbirthdd = demo.getDateOfBirth();
			String dateofserviceyyyy = UtilDateUtilities.justYear(billingDate);
			String dateofservicemm = UtilDateUtilities.justMonth(billingDate);
			String dateofservicedd = UtilDateUtilities.justDay(billingDate);
			boolean patientvisitcharge = true;
			boolean lasteyeexam = true;
			boolean patientrestriction = true;

			TeleplanResponse tr = tAPI.checkElig(phn, dateofbirthyyyy, dateofbirthmm, dateofbirthdd,
					dateofserviceyyyy, dateofservicemm, dateofservicedd,
					patientvisitcharge, lasteyeexam, patientrestriction);

			transfer.setResult(tr.getResult());
			transfer.setMessage(tr.getMsgs());
			transfer.setFilename(tr.getFilename());
			transfer.setRealFilename(tr.getRealFilename());
			transfer.setEligible(tr.isSuccess());
		}
		else
		{
			transfer.setEligible(false);
			transfer.setError("Unsupported province & billing type combination");
		}

		return transfer;
	}
}
