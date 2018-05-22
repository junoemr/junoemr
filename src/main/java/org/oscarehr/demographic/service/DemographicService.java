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
package org.oscarehr.demographic.service;

import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.dao.AdmissionDao;
import org.oscarehr.common.model.Admission;
import org.oscarehr.demographic.dao.DemographicCustDao;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.ws.external.rest.v1.conversion.DemographicConverter;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferInbound;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("demographic.service.DemographicService")
@Transactional
public class DemographicService
{
	@Autowired
	DemographicManager demographicManager;

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	DemographicCustDao demographicCustDao;

	@Autowired
	DemographicExtDao demographicExtDao;

	@Autowired
	private ProgramManager programManager;

	@Autowired
	private AdmissionDao admissionDao;

	public DemographicTransferOutbound getDemographicTransferOutbound(String providerNoStr, Integer demographicNo)
	{
		Demographic demographic = demographicDao.find(demographicNo);
		List<DemographicExt> demoExtras = demographic.getDemographicExtList();
		DemographicCust demoCustom = demographic.getDemographicCust().get(0);

		return DemographicConverter.getAsTransferObject(demographic, demoExtras, demoCustom);
	}

	public Demographic addNewDemographicRecord(String providerNoStr, DemographicTransferInbound demographicTransferInbound)
	{
		Demographic demographic = DemographicConverter.getAsDomainObject(demographicTransferInbound);
		DemographicCust demoCustom = DemographicConverter.getCustom(demographicTransferInbound);
		List<DemographicExt> demographicExtensions = DemographicConverter.getExtensionList(demographicTransferInbound);

		return addNewDemographicRecord(providerNoStr, demographic, demoCustom, demographicExtensions);
	}
	public Demographic addNewDemographicRecord(String providerNoStr, Demographic demographic,
	                                    DemographicCust demoCustom, List<DemographicExt> demographicExtensions)
	{
		// save the base demographic object
		addNewDemographicRecord(providerNoStr, demographic);
		Integer demographicNo = demographic.getDemographicId();

		if(demoCustom != null)
		{
			// save the custom fields
			demoCustom.setId(demographicNo);
			demographicManager.createUpdateDemographicCust(providerNoStr, demoCustom);
		}
		for(DemographicExt extension : demographicExtensions)
		{
			//save the extension fields
			extension.setDemographicNo(demographicNo);
			extension.setProviderNo(providerNoStr);
			demographicManager.createExtension(providerNoStr, extension);
		}
		return demographic;
	}
	public void addNewDemographicRecord(String providerNoStr, Demographic demographic)
	{
		addNewDemographicRecord(providerNoStr, demographic, programManager.getDefaultProgramId());
	}
	public void addNewDemographicRecord(String providerNoStr, Demographic demographic, Integer programId)
	{
		/* set some default values */
		demographic.setLastUpdateDate(new Date());
		demographic.setLastUpdateUser(providerNoStr);
		if(demographic.getPatientStatus() == null)
		{
			demographic.setPatientStatus(org.oscarehr.common.model.Demographic.PatientStatus.AC.name());
		}
		if(demographic.getReferralDoctor() == null)
		{
			demographic.setReferralDoctor("<rdohip></rdohip><rd></rd>");
		}
		demographicDao.persist(demographic);

		Admission admission = new Admission();
		admission.setClientId(demographic.getDemographicId());
		admission.setProgramId(programId);
		admission.setProviderNo(providerNoStr);
		admission.setAdmissionDate(new Date());
		admission.setAdmissionStatus(Admission.STATUS_CURRENT);
		admission.setAdmissionNotes("");

		admissionDao.saveAdmission(admission);
	}
}
