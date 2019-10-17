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

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static org.oscarehr.billing.CA.transfer.EligibilityCheckTransfer.ValidationStatus.COMPLETE;
import static org.oscarehr.billing.CA.transfer.EligibilityCheckTransfer.ValidationStatus.UNAVAILABLE;

@Service
public class EligibilityCheckService
{
	private static final OscarProperties properties = OscarProperties.getInstance();

	public EligibilityCheckTransfer checkEligibility(Demographic demo) throws IOException, InterruptedException
	{
		EligibilityCheckTransfer transfer = new EligibilityCheckTransfer();

		/* Eligibility checking only works in the same province.
		Ex: asking BC teleplan for an AB hin type will never be eligible */
		if(properties.getInstanceTypeUpperCase().equalsIgnoreCase(demo.getHcType()))
		{
			if(properties.isClinicaidBillingType())
			{
				ClinicaidAPIService clinicaidAPIService = SpringUtils.getBean(ClinicaidAPIService.class);
				Map<String, String> clinicaidResponse = clinicaidAPIService.checkEligibility(demo);

				transfer.setError(clinicaidResponse.get("error"));
				transfer.setMessage(clinicaidResponse.get("msg"));
				transfer.setResult(clinicaidResponse.get("result"));
				transfer.setEligible(Boolean.valueOf(clinicaidResponse.get("isEligible")));
				transfer.setValidationStatus(COMPLETE);
			}
			else if(properties.isBritishColumbiaBillingType())
			{
				TeleplanUserPassDAO dao = new TeleplanUserPassDAO();
				String[] userpass = dao.getUsernamePassword();
				TeleplanService tService = new TeleplanService();
				LocalDate billingDate = LocalDate.now();

				TeleplanAPI tAPI = tService.getTeleplanAPI(userpass[0], userpass[1]);
				TeleplanResponse tr = tAPI.checkElig(demo.getHin(), demo.getYearOfBirth(), demo.getMonthOfBirth(), demo.getDateOfBirth(),
						billingDate, true, true, true);

				transfer.setResult(tr.getResult());
				transfer.setMessage(tr.getMsgs());
				transfer.setRealFilename(tr.getRealFilename());
				transfer.setEligible(tr.isSuccess());
				transfer.setValidationStatus(COMPLETE);
			}
			else
			{
				transfer.setEligible(false);
				transfer.setMessage("Results unavailable: " + properties.getBillingTypeUpperCase() + " not supported");
				transfer.setValidationStatus(UNAVAILABLE);
			}
		}
		else
		{
			transfer.setEligible(false);
			transfer.setMessage("Results unavailable. Out of province hin");
			transfer.setValidationStatus(UNAVAILABLE);
		}
		return transfer;
	}
}
