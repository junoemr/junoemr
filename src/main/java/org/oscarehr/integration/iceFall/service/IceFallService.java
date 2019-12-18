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

import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.integration.iceFall.dao.IceFallCredentialsDao;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorListTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IceFallService
{
	@Autowired
	IceFallCredentialsDao iceFallCredentialsDao;

	@Autowired
	IceFallRESTService iceFallRESTService;

	@Autowired
	DemographicExtDao demographicExtDao;

	public static final String CANOPY_CUSTOMER_ID_KEY = "canopy_customer_id";

	/**
	 * get icefall credentials from DB
	 * @return - the icefall credentials
	 */
	public IceFallCredentials getCredentials()
	{
		return iceFallCredentialsDao.getCredentials();
	}

	/**
	 * save icefall credentials to the database
	 * @param creds - the credentials to save
	 * @return - the saved credentials object
	 */
	public IceFallCredentials updateCredentials(IceFallCredentials creds)
	{
		iceFallCredentialsDao.merge(creds);
		return creds;
	}


	/**
	 * submit eform to icefall for processing
	 * @param provider - the provider who is submitting the eform
	 * @param fdid - the fdid of the eform to submit.
	 */
	public void sendIceFallForm(Provider provider, Demographic demo, Integer fdid)
	{
		//login to api
		iceFallRESTService.authenticate();

		//get doctor id
		Integer iceFallDocId = findDoctorId(provider, iceFallRESTService.getDoctorList());

		//get customer id
		Integer demoCanopyId = getDemoCanopyId(demo);
	}

	protected void getDemoCanopyInfo(Demographic demo)
	{
		DemographicExt demoExt = demographicExtDao.getDemographicExt(demo.getId(), CANOPY_CUSTOMER_ID_KEY);
		if (demoExt != null && !demoExt.getValue().isEmpty())
		{
			importCanopyCustomer(demoExt.getValue());
		}
	}

	protected void importCanopyCustomer(String canopyCustomerId)
	{
		//TODO
	}

	/**
	 * locate the ice fall doctor id of the current provider
	 * @param doctorListTo1 - the list of ids to search
	 * @return - the id of the currently logged in provider
	 */
	protected Integer findDoctorId(Provider provider, IceFallDoctorListTo1 doctorListTo1)
	{
		for (IceFallDoctorTo1 doc : doctorListTo1.getResults())
		{
			if (
							doc.getFirstName().trim().equals(provider.getFirstName().trim()) &&
											doc.getLastName().trim().equals(provider.getLastName().trim()) &&
											doc.getEmail().trim().equals(provider.getEmail())
			)
			{
				return doc.getId();
			}
		}

		//TODO figure out the "authorize_bodystream@tweed.com" doctor thing.
		throw new RuntimeException("Could not find doctor! And authorize doctor is not configured");
	}

}
