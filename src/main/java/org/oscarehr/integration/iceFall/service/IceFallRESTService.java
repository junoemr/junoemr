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

package org.oscarehr.integration.iceFall.service;

import org.oscarehr.integration.iceFall.dao.IceFallCredentialsDao;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.oscarehr.integration.iceFall.service.transfer.IceFallAuthenticationResponseTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallAuthenticationTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreateCustomerResponseTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreateCustomerTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreatePrescriptionResponseTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreatePrescriptionTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCustomerTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorListTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import oscar.util.RESTClient;

@Service
public class IceFallRESTService
{
	public static final String API_BASE = "/api";

	// API endpoints
	public static final String API_AUTH_TOKEN = "/api-token-auth/";
	public static final String DOCTOR_LIST = API_BASE + "/partner/doctors/";
	public static final String CUSTOMER_DETAILS = API_BASE + "/partner/customers/";
	public static final String CREATE_CUSTOMER = API_BASE + "/partner/customers/";
	public static final String ADD_PRESCRIPTION = API_BASE + "/partner/prescriptions/";

	@Autowired
	IceFallCredentialsDao iceFallCredentialsDao;

	private RESTClient RESTClient = new RESTClient(new IceFallRESTErrorHandler());

	/**
	 * authenticate with the icefall api, updating the api token
	 * @return new api token
	 */
	public String authenticate()
	{
		IceFallCredentials iceFallCredentials = iceFallCredentialsDao.getCredentials();
		IceFallAuthenticationTo1 credentials = new IceFallAuthenticationTo1();;
		credentials.setUsername(iceFallCredentials.getUsername());
		credentials.setPassword(iceFallCredentials.getPassword());

		IceFallAuthenticationResponseTo1 authResponse = RESTClient.doPost(getIceFallUrlBase() + API_AUTH_TOKEN, null, credentials, IceFallAuthenticationResponseTo1.class);
		iceFallCredentials.setApiToken(authResponse.getApiToken());
		iceFallCredentialsDao.merge(iceFallCredentials);

		return authResponse.getApiToken();
	}

	/**
	 * get the full list of doctors in the icefall system
	 * @return - list of doctors
	 */
	public IceFallDoctorListTo1 getDoctorList()
	{
		return getDoctorList(getIceFallUrlBase() + DOCTOR_LIST);
	}

	/**
	 * bottom half of, getDoctorList. takes a url which is used for pagenation
	 * @param url - url to fetch doctor list from
	 * @return - the full doctor list.
	 */
	public IceFallDoctorListTo1 getDoctorList(String url)
	{
		IceFallDoctorListTo1 iceFallDoctorListTo1 =  RESTClient.doGet(url, getApiAuthenticationHeaders(), IceFallDoctorListTo1.class);
		if (iceFallDoctorListTo1.hasNext())
		{
			iceFallDoctorListTo1.getResults().addAll(getDoctorList(iceFallDoctorListTo1.getNext()).getResults());
		}
		return iceFallDoctorListTo1;
	}

	/**
	 * get customer information
	 * @param canopyCustomerId - customer remote id.
	 * @return - customer info.
	 */
	public IceFallCustomerTo1 getCustomerInformation(Integer canopyCustomerId)
	{
		return RESTClient.doGet(getIceFallUrlBase() + CUSTOMER_DETAILS + canopyCustomerId + "/", getApiAuthenticationHeaders(), IceFallCustomerTo1.class);
	}

	/**
	 * create a new ice fall customer
	 * @param createCustomerTo1 - customer creation transfer object
	 * @return - icefall response to customer creation.
	 */
	public IceFallCreateCustomerResponseTo1 createIceFallCustomer(IceFallCreateCustomerTo1 createCustomerTo1)
	{
		return RESTClient.doPost(getIceFallUrlBase() + CREATE_CUSTOMER, getApiAuthenticationHeaders(), createCustomerTo1, IceFallCreateCustomerResponseTo1.class);
	}

	/**
	 * send a prescription to ice fall
	 * @param iceFallCreatePrescriptionTo1 - prescription creation transfer object
	 * @return
	 */
	public IceFallCreatePrescriptionResponseTo1 sendPrescription(IceFallCreatePrescriptionTo1 iceFallCreatePrescriptionTo1)
	{
		return RESTClient.doPost(getIceFallUrlBase() + ADD_PRESCRIPTION, getApiAuthenticationHeaders(), iceFallCreatePrescriptionTo1, IceFallCreatePrescriptionResponseTo1.class);
	}


	protected HttpHeaders getApiAuthenticationHeaders()
	{
		IceFallCredentials iceFallCredentials = iceFallCredentialsDao.getCredentials();

		HttpHeaders headers = new HttpHeaders();
		headers.add("AUTHORIZATION", "JWT " + iceFallCredentials.getApiToken());

		return headers;
	}

	private String getIceFallUrlBase()
	{
		//TODO property setting. or perhaps option
		return "https://api-qa.canopygrowthweb.com";
	}
}
