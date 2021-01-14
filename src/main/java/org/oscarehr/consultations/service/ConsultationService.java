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
package org.oscarehr.consultations.service;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.consultations.dao.ConsultRequestDao;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.managers.ProviderManager2;
import org.oscarehr.managers.model.ProviderSettings;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.ws.rest.to.model.LetterheadTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ConsultationService
{
	@Autowired
	private ConsultRequestDao consultRequestDao;

	@Autowired
	private ClinicDAO clinicDAO;

	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private ProviderManager2 providerManager;

	@Autowired
	private DemographicDao demographicDao;

	public List<LetterheadTo1> getLetterheadList()
	{
		List<LetterheadTo1> letterheadList = new ArrayList<>();

		LetterheadTo1 clinicLetterhead = getClinicLetterhead();
		letterheadList.add(clinicLetterhead);

		//TODO-legacy add multi-site letterheads

		//provider letterheads
		//- find non-empty phone/address in the following priority:
		//- 1) UserProperty ("property" table)
		//- 2) Provider
		//- 3) Clinic
		List<ProviderData> providerList = providerDao.findByActiveStatus(true);
		for(ProviderData provider : providerList)
		{
			Integer providerNo = provider.getProviderNo();
			if(providerNo == -1) continue; //skip user "system"

			ProviderSettings providerSettings = providerManager.getProviderSettings(String.valueOf(providerNo));
			LetterheadTo1 providerLetterhead = getProviderLetterhead(provider, clinicLetterhead, providerSettings);
			letterheadList.add(providerLetterhead);
		}

		return letterheadList;
	}

	public LetterheadTo1 getDefaultLetterhead(Integer providerNo, Integer demographicNo)
	{
		return getDefaultLetterhead(String.valueOf(providerNo), demographicNo);
	}
	public LetterheadTo1 getDefaultLetterhead(String providerNoStr, Integer demographicNo)
	{
		ProviderSettings providerSettings = providerManager.getProviderSettings(providerNoStr);

		LetterheadTo1 letterhead;
		String setting = providerSettings.getConsultationLetterHeadNameDefault();
		setting = (setting == null)? "" : setting; // prevent null values in switch statement
		switch(setting)
		{
			case "3": letterhead = getClinicLetterhead(); break;
			case "2": {
				Demographic demographic = demographicDao.find(demographicNo);
				String mrpProvider = demographic.getProviderNo();
				if(StringUtils.stripToNull(mrpProvider) != null)
				{
					letterhead = getProviderLetterhead(mrpProvider);
					break;
				}
				// else use case 1
			}
			case "1":
			default: letterhead = getProviderLetterhead(providerNoStr); break;
		}
		return letterhead;
	}
	public LetterheadTo1 getProviderLetterhead(Integer providerNo)
	{
		return getProviderLetterhead(String.valueOf(providerNo));
	}
	public LetterheadTo1 getProviderLetterhead(String providerNoStr)
	{
		ProviderSettings providerSettings = providerManager.getProviderSettings(providerNoStr);
		return getProviderLetterhead(providerDao.find(providerNoStr), getClinicLetterhead(), providerSettings);
	}
	private LetterheadTo1 getProviderLetterhead(Integer providerNo, ProviderSettings providerSettings)
	{
		String providerNoStr = String.valueOf(providerNo);
		return getProviderLetterhead(providerDao.find(providerNoStr), getClinicLetterhead(), providerSettings);
	}
	private LetterheadTo1 getProviderLetterhead(ProviderData provider, LetterheadTo1 defaultLetterhead, ProviderSettings providerSettings)
	{
		Integer providerNo = provider.getProviderNo();
		String providerNoStr = String.valueOf(providerNo);

		String settingsAddr = buildAddress(providerSettings.getRxAddress(), providerSettings.getRxCity(),
				providerSettings.getRxProvince(), providerSettings.getRxPostal());

		LetterheadTo1 letterhead = new LetterheadTo1(providerNoStr, provider.getDisplayName());
		letterhead.setPhone(coalesceProperty(providerSettings.getRxPhone(), provider.getWorkPhone(), defaultLetterhead.getPhone()));
		letterhead.setAddress(coalesceProperty(settingsAddr, provider.getAddress(), defaultLetterhead.getAddress()));
		letterhead.setFax(coalesceProperty(providerSettings.getFaxNumber(), provider.getFaxNumber(), defaultLetterhead.getFax()));
		return letterhead;
	}

	private LetterheadTo1 getClinicLetterhead()
	{
		//clinic letterhead
		Clinic clinic = clinicDAO.getClinic();
		LetterheadTo1 letterhead = new LetterheadTo1("-1", clinic.getClinicName());

		String clinicPhone = StringUtils.trimToEmpty(clinic.getClinicPhone());
		String clinicAddress = buildAddress(clinic.getClinicAddress(), clinic.getClinicCity(),
				clinic.getClinicProvince(), clinic.getClinicPostal());
		String clinicFax = StringUtils.trimToEmpty(clinic.getClinicFax());

		letterhead.setPhone(clinicPhone);
		letterhead.setAddress(clinicAddress);
		letterhead.setFax(clinicFax);
		return letterhead;
	}

	private String buildAddress(String address, String city, String province, String postal)
	{
		address = StringUtils.trimToEmpty(address) + " " + StringUtils.trimToEmpty(city);
		address = StringUtils.trimToEmpty(address) + " " + StringUtils.trimToEmpty(province);
		address = StringUtils.trimToEmpty(address) + " " + StringUtils.trimToEmpty(postal);
		return StringUtils.stripToNull(address);
	}

	private String coalesceProperty(String...valueList)
	{
		for(String value : valueList)
		{
			value = StringUtils.trimToNull(value);
			if(value != null)
			{
				return value;
			}
		}
		return null;
	}
}
