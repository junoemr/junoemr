/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.integration.clinicaid.service;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.model.Provider;
import org.oscarehr.integration.clinicaid.dto.ClinicaidResultTo1;
import org.oscarehr.integration.clinicaid.dto.PatientEligibilityDataTo1;
import org.oscarehr.util.LoggedInInfo;

import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.UtilMisc;
import oscar.oscarBilling.data.BillingFormData;


@Service
public class ClinicaidAPIService
{
	static private String apiPath = "/api/v2/";

	private ClinicaidSessionManager sessionManager;

	@Autowired
	public ClinicaidAPIService(ClinicaidSessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}

	// This method assumes the eligibility check is synchronous. This is only the case for BC eligibility checks.
	// TODO: This needs to be changed when implementing Ontario.
	public Map<String, String> checkEligibility(String hin, String birthDate) throws IOException
	{
		HashMap<String, String> response = new HashMap<>();

		String queryString = String.format(
				"/?health_number=%s&birth_date=%s",
				sessionManager.urlEncode(hin),
				sessionManager.urlEncode(birthDate)
		);

		String urlString = sessionManager.getApiDomain() + apiPath + "patient/eligibility/" + queryString;
		ClinicaidResultTo1 result = sessionManager.get(new URL(urlString));

		if (result.hasError())
		{
			response.put("error", result.getErrors().getErrorString());
		}
		else
		{
			PatientEligibilityDataTo1 eligibilityData = result.getData().getEligibilityData();
			response.put("result", eligibilityData.isEligible() ? "Eligible" : "Not Eligible");
			response.put("msg", eligibilityData.getMessage());
		}

		return response;
	}

	public String buildClinicaidURL(HttpServletRequest request, String action)
	{
		String clinicaidLink = "";
		String nonce = "";

		HttpSession session = request.getSession();
		String user_no = (String) session.getAttribute("user");
		String user_first_name = (String) session.getAttribute("userfirstname");
		String user_last_name = (String) session.getAttribute("userlastname");

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Provider loggedInProvider = loggedInInfo.getLoggedInProvider();

		try
		{
			nonce = sessionManager.getLoginToken(loggedInProvider);
		}
		catch (IOException error)
		{
			String message = "Failed to get login token for Clinicaid integration.";
			MiscUtils.getLogger().error(message, error);
			return null;
		}

		// If creating a new invoice in Clinicaid
		if (action.equals("create_invoice"))
		{
			BillingFormData billform = new BillingFormData();
			String service_recipient_oscar_number = request.getParameter("demographic_no");
			String appointment_provider_no = request.getParameter("apptProvider_no");

			oscar.oscarDemographic.data.DemographicData demoData =
					new oscar.oscarDemographic.data.DemographicData();
			org.oscarehr.common.model.Demographic demo =
					demoData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), service_recipient_oscar_number);

			// Get latest diagnostic code
			String dx_codes = "";

			String referral_no = demo.getFamilyDoctorNumber();
			String referral_first_name = demo.getFamilyDoctorFirstName();
			String referral_last_name = demo.getFamilyDoctorLastName();

			String provider_no = "";
			String provider_uli = "";
			String provider_first_name = "";
			String provider_last_name = "";

			// If this invoice was created from an appointment, use the provider
			// who performed the appointment as the billing provider
			if (appointment_provider_no != null)
			{
				try
				{
					provider_no = appointment_provider_no;

					// Make sure the provider_no is a valid integer. 
					// ProviderData will throw an un-catchable exception if it 
					// is not
					//TODO: this is so wrong
					Integer test = Integer.parseInt(provider_no);

					provider_uli = billform.getPracNo(provider_no);

					oscar.oscarProvider.data.ProviderData providerData =
							new oscar.oscarProvider.data.ProviderData(provider_no);

					provider_first_name = providerData.getFirst_name();
					provider_last_name = providerData.getLast_name();
					provider_no = providerData.getProviderNo();
				}
				catch (Exception E)
				{
					provider_no = null;
				}

			}

			// If no appointment provider exists, try to get the patients 
			// provider to use for billing
			if (provider_no == null || provider_no == "")
			{
				try
				{
					provider_no = demo.getProviderNo();

					// Make sure the provider_no is a valid integer. If a patient doesn't
					// have a provider assigned to them, the demo returns an invalid provider_no
					// which then causes ProviderData to throw an un-catchable exception
					Integer test = Integer.parseInt(provider_no);

					provider_uli = billform.getPracNo(provider_no);
					oscar.oscarProvider.data.ProviderData providerData =
							new oscar.oscarProvider.data.ProviderData(provider_no);

					provider_first_name = providerData.getFirst_name();
					provider_last_name = providerData.getLast_name();
					provider_no = providerData.getProviderNo();
				}
				catch (Exception E)
				{
					provider_no = null;
				}
			}

			// If no patient or appointment provider was found, use the 
			// current user for billing
			if (provider_no == null || ("").equals(provider_no))
			{
				try
				{
					provider_no = user_no;
					provider_first_name = user_first_name;
					provider_last_name = user_last_name;
					provider_uli = billform.getPracNo(provider_no);
				}
				catch (Exception E)
				{
					provider_uli = "";
				}

			}
			if (provider_uli == null)
			{
				provider_uli = "";
			}

			String urlFormat = sessionManager.getApiDomain() + "/?nonce=" + nonce +
					"#/invoice/add?service_recipient_first_name=%s" +
					"&service_recipient_uli=%s" +
					"&service_recipient_ver=%s" +
					"&service_recipient_last_name=%s" +
					"&service_recipient_oscar_number=%s" +
					"&service_recipient_status=%s" +
					"&service_recipient_age=%s" +
					"&service_recipient_gender=%s" +
					"&service_provider_oscar_number=%s" +
					"&service_provider_first_name=%s" +
					"&service_provider_last_name=%s" +
					"&service_provider_uli=%s" +
					"&service_start_date=%s" +
					"&province=%s" +
					"&hc_province=%s" +
					"&city=%s" +
					"&postal_code=%s" +
					"&chart_number=%s" +
					"&service_recipient_birth_date=%s" +
					"&appointment_number=%s" +
					"&appointment_start_time=%s" +
					"&referral_number=%s" +
					"&referral_first_name=%s" +
					"&referral_last_name=%s" +
					"&diagnostic_code=%s" +
					"&address=%s";

			clinicaidLink = String.format(urlFormat,
					sessionManager.urlEncode(provider_no),
					sessionManager.urlEncode(provider_first_name),
					sessionManager.urlEncode(provider_last_name),
					sessionManager.urlEncode(provider_uli),
					sessionManager.urlEncode(dx_codes),
					sessionManager.urlEncode(request.getParameter("appointment_no")),
					sessionManager.urlEncode(demo.getYearOfBirth() + "-" + demo.getMonthOfBirth() + "-" + demo.getDateOfBirth()),
					sessionManager.urlEncode(demo.getSex()),
					sessionManager.urlEncode(UtilMisc.toUpperLowerCase(demo.getFirstName())),
					sessionManager.urlEncode(UtilMisc.toUpperLowerCase(demo.getLastName())),
					sessionManager.urlEncode(StringUtils.upperCase(demo.getProvince())),
					sessionManager.urlEncode(StringUtils.upperCase(demo.getHcType())),
					sessionManager.urlEncode(demo.getCity()),
					sessionManager.urlEncode(demo.getAddress()),
					sessionManager.urlEncode(demo.getPostal()),
					sessionManager.urlEncode(service_recipient_oscar_number),
					sessionManager.urlEncode(demo.getPatientStatus()),
					sessionManager.urlEncode(demo.getHin()),
					sessionManager.urlEncode(demo.getVer()),
					sessionManager.urlEncode(demo.getAge()),
					sessionManager.urlEncode(referral_no),
					sessionManager.urlEncode(referral_first_name),
					sessionManager.urlEncode(referral_last_name),
					sessionManager.urlEncode(request.getParameter("start_time")),
					sessionManager.urlEncode(request.getParameter("chart_no")),
					sessionManager.urlEncode(request.getParameter("appointment_date"))
			);
		}
		else if (action.equals("invoice_reports"))
		{
			if (request.getParameter("patient_remote_id") != null && !request.getParameter("patient_remote_id").isEmpty())
			{
				clinicaidLink = sessionManager.getApiDomain() + "/?nonce=" + nonce + "#/reports?patient_remote_id=" + request.getParameter("patient_remote_id");
			}
			else
			{
				clinicaidLink = sessionManager.getApiDomain() + "/?nonce=" + nonce + "#/reports";
			}
		}
		return clinicaidLink;
	}
}
