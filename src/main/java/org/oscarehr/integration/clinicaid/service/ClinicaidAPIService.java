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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.ProviderSitePK;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.clinicaid.dto.ClinicaidApiLimitInfoTo1;
import org.oscarehr.integration.clinicaid.dto.ClinicaidResultTo1;
import org.oscarehr.integration.clinicaid.dto.PatientEligibilityDataTo1;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.util.LoggedInInfo;

import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.util.ConversionUtils;
import oscar.util.UtilMisc;
import oscar.oscarBilling.data.BillingFormData;

import static java.lang.Thread.sleep;


@Service
public class ClinicaidAPIService
{
	static private final int ELIG_CHECK_TIMEOUT_MS = 12000;
	static private final int ELIG_CHECK_POLL_FREQUENCY_MS = 2000;
	static private String apiPath = "/api/v2/";

	private ClinicaidSessionManager sessionManager;

	private OscarProperties oscarProperties = OscarProperties.getInstance();

	@Autowired
	public ClinicaidAPIService(ClinicaidSessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}

	@Autowired
	private ProviderService providerService;

	@Autowired
	private ClinicDAO clinicDAO;

	@Autowired
	private OscarAppointmentDao appointmentDao;

	@Autowired
	private SiteDao siteDao;

	@Autowired
	private ProviderSiteDao provSiteDao;

	private String formatEligibilityData(PatientEligibilityDataTo1 data)
	{
		// strip out numeric message code at the front
		String message = StringUtils.trimToEmpty(data.getMessage())
				.replaceFirst("\\d*", "")
				.replace(";", "\n");
		return String.format(
				"First Name: %s\nMiddle Name: %s\nLast Name: %s\nBirth Date: %s\nGender: %s\nMessage: %s",
				StringUtils.trimToEmpty(data.getFirstName()),
				StringUtils.trimToEmpty(data.getMiddleName()),
				StringUtils.trimToEmpty(data.getLastName()),
				ConversionUtils.toDateString(data.getBirthDate()),
				StringUtils.trimToEmpty(data.getGender()),
				message
		);
	}

	private ClinicaidResultTo1 doAsyncEligibilityCheck(String queryString)
			throws IOException, InterruptedException
	{
		String urlString = sessionManager.getApiDomain() + apiPath + "patient/eligibility/";
		URL url = new URL(urlString + queryString);

		ClinicaidResultTo1 result;
		ClinicaidResultTo1 lastUsableResult = null;
		long start = System.currentTimeMillis();
		int sleepDuration = 0;

		long startTime = System.currentTimeMillis();
		do
		{
			sleep(sleepDuration);

			result = sessionManager.get(url);

			ClinicaidApiLimitInfoTo1 limitInfo = result.getApiLimitInfo();
			if (result.hasError() && limitInfo != null && limitInfo.isLimitReached())
			{
				sleepDuration = limitInfo.getIntervalExpireSeconds() * 1000;
				continue;
			}

			lastUsableResult = result;
			sleepDuration = ELIG_CHECK_POLL_FREQUENCY_MS;

			if (result.hasError() || !result.getData().getEligibilityData().isPending())
				break;

		} while (System.currentTimeMillis() - start < ELIG_CHECK_TIMEOUT_MS);
		MiscUtils.getLogger().info("Clinicaid API: eligibility check took: " + (System.currentTimeMillis() - startTime) + " ms");

		return lastUsableResult;
	}
	
	public Map<String, String> checkEligibility(Demographic demo) throws IOException, InterruptedException
	{
		String urlString = sessionManager.getApiDomain() + apiPath + "patient/eligibility/";

		Map<String, String> data = new HashMap<>();
		data.put("health_number", demo.getHin());

		ClinicaidResultTo1 result;
		if (oscarProperties.isOntarioInstanceType())
		{
			data.put("ontario_version_code", demo.getVer());
			String queryString = sessionManager.buildQueryString(data);
			result = doAsyncEligibilityCheck(queryString);
		}
		else
		{
			data.put("birth_date", ConversionUtils.toDateString(demo.getBirthDate()));
			String queryString = sessionManager.buildQueryString(data);
			result = sessionManager.get(new URL(urlString + queryString));
		}

		HashMap<String, String> response = new HashMap<>();
		if (result == null)
		{
			response.put("error", "Request timeout");
		}
		else if (result.hasError())
		{
			response.put("error", result.getErrors().getErrorString());
		}
		else
		{
			PatientEligibilityDataTo1 eligibilityData = result.getData().getEligibilityData();
			response.put("isEligible", String.valueOf(eligibilityData.isEligible()));
			response.put("result", eligibilityData.isEligible() ? "Patient Eligible" : "Patient Not Eligible");
			response.put("msg", oscarProperties.isOntarioInstanceType()
							? formatEligibilityData(eligibilityData)
							: eligibilityData.getMessage()
			);

			if (!eligibilityData.isChecked())
			{
				LocalDate checkedAt = eligibilityData.getCheckedAt();
				response.put("error", String.format("Check timed out. Last check from %s:", ConversionUtils.toDateString(checkedAt)));
			}
		}

		return response;
	}
	
	public String buildClinicaidURL(HttpServletRequest request, String action)
	{
		String clinicaidLink = sessionManager.getClinicaidDomain();
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
			return clinicaidLink;
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
			if (provider_no == null || provider_no.equals(""))
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

			HashMap<String, String> data = new HashMap<>();
			data.put("service_recipient_uli", StringUtils.trimToEmpty(demo.getHin()));
			data.put("service_recipient_ver", StringUtils.trimToEmpty(demo.getVer()));
			data.put("service_recipient_first_name", StringUtils.trimToEmpty(UtilMisc.toUpperLowerCase(demo.getFirstName())));
			data.put("service_recipient_last_name", StringUtils.trimToEmpty(UtilMisc.toUpperLowerCase(demo.getLastName())));
			data.put("service_recipient_oscar_number", StringUtils.trimToEmpty(service_recipient_oscar_number));
			data.put("service_recipient_status", StringUtils.trimToEmpty(demo.getPatientStatus()));
			data.put("service_recipient_age", StringUtils.trimToEmpty(demo.getAge()));
			data.put("service_recipient_gender", StringUtils.trimToEmpty(demo.getSex()));
			data.put("service_provider_oscar_number", StringUtils.trimToEmpty(provider_no));
			data.put("service_provider_first_name", StringUtils.trimToEmpty(provider_first_name));
			data.put("service_provider_last_name", StringUtils.trimToEmpty(provider_last_name));
			data.put("service_provider_uli", StringUtils.trimToEmpty(provider_uli));
			data.put("service_start_date", StringUtils.trimToEmpty(request.getParameter("appointment_date")));
			data.put("province", StringUtils.trimToEmpty(StringUtils.upperCase(demo.getProvince())));
			data.put("hc_province", StringUtils.trimToEmpty(StringUtils.upperCase(demo.getHcType())));
			data.put("city", StringUtils.trimToEmpty(demo.getCity()));
			data.put("postal_code", StringUtils.trimToEmpty(demo.getPostal()));
			data.put("chart_number", StringUtils.trimToEmpty(request.getParameter("chart_no")));
			data.put("service_recipient_birth_date", StringUtils.trimToEmpty(demo.getYearOfBirth()) + "-" + StringUtils.trimToEmpty(demo.getMonthOfBirth()) + "-" + StringUtils.trimToEmpty(demo.getDateOfBirth()));
			data.put("appointment_number", StringUtils.trimToEmpty(request.getParameter("appointment_no")));
			data.put("appointment_start_time", StringUtils.trimToEmpty(request.getParameter("start_time")));
			data.put("referral_number", StringUtils.trimToEmpty(referral_no));
			data.put("referral_first_name", StringUtils.trimToEmpty(referral_first_name));
			data.put("referral_last_name", StringUtils.trimToEmpty(referral_last_name));
			data.put("diagnostic_code", StringUtils.trimToEmpty(dx_codes));
			data.put("address", StringUtils.trimToEmpty(demo.getAddress()));
			data.put("appointment_site", StringUtils.trimToEmpty(request.getParameter("appointment_site")));

			// In Oscar, Newborns are denoted with the Mother's PHN and version Code 66
			// Before sending to Clinicaid, we will remove the version code
			// and move the PHN into the guardian_health_number field instead

			if (oscarProperties.isBritishColumbiaInstanceType())
			{
				if (demo.isNewBorn())
				{
					data.remove("service_recipient_ver");
					data.put("guardian_health_number", data.remove("service_recipient_uli"));
				}

				ProviderData providerData = providerService.getProviderEager(provider_no);

				// Default facility number
				String facilityNumber = null;

				if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable())
				{
					Integer appointmentNo = NumberUtils.toInt(request.getParameter("appointment_no"), 0);
					Appointment appointment = appointmentDao.find(appointmentNo);

					// If billed from the master file, appointmentNo = 0
					// So we just can't check for the present of the query parameter
					if (appointment != null)
					{
						Site site = siteDao.findByName(appointment.getLocation());

						ProviderSitePK key = new ProviderSitePK(provider_no, site.getId());
						ProviderSite provSite = provSiteDao.find(key);

						if (provSite != null && provSite.isBcBCPEligible())
						{
							facilityNumber = site.getBcFacilityNumber();
						}
					}
				}
				else
				{
					if (providerData.getBillingOpts() != null && providerData.getBillingOpts().getBcBCPEligible())
					{
						Clinic clinic = clinicDAO.getClinic();
						facilityNumber = clinic.getBcFacilityNumber();
					}
				}

				if (facilityNumber != null)
				{
					data.put("facility_number", StringUtils.trimToEmpty(facilityNumber));
				}
			}

			clinicaidLink = clinicaidLink + "/?nonce=" + nonce +
					"#/invoice/add" + sessionManager.buildQueryString(data);

		}
		else if (action.equals("invoice_reports"))
		{
			if (request.getParameter("patient_remote_id") != null && !request.getParameter("patient_remote_id").isEmpty())
			{
				clinicaidLink = clinicaidLink + "/?nonce=" + nonce + "#/reports?patient_remote_id=" + request.getParameter("patient_remote_id") + "&status_group=all";
			}
			else
			{
				clinicaidLink = clinicaidLink + "/?nonce=" + nonce + "#/reports";
			}
		}
		return clinicaidLink;
	}
}
