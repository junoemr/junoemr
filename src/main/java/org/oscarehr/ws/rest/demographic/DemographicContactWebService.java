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

package org.oscarehr.ws.rest.demographic;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.ContactDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.Contact;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.conversion.DemographicContactFewConverter;
import org.oscarehr.ws.rest.conversion.DemographicContactFewToContactDomainConverter;
import org.oscarehr.ws.rest.conversion.DemographicContactFewToDomainConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.model.DemographicContactFewTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("demographic/{demographicId}/contact/")
@Component("DemographicContactWebService")
@Tag(name = "demographic")
public class DemographicContactWebService extends AbstractServiceImpl
{
    protected SecurityInfoManager securityInfoManager;
    protected DemographicManager demographicManager;
    protected DemographicContactFewTo1 demographicContactFewTo1;
    protected DemographicContactFewToContactDomainConverter demographicContactToDomainConverter;
    protected DemographicContactFewToDomainConverter demoContactToDomainConverter;
    protected ContactDao contactDao;
	protected ProviderDao providerDao;
    protected DemographicContactDao demographicContactDao;
    protected DemographicContactFewConverter demoContactFewConverter;
    protected ProfessionalSpecialistDao specialistDao;

    // ==========================================================================
	// Public Methods
	// ==========================================================================

    @Autowired
	public DemographicContactWebService(
			SecurityInfoManager securityInfoManager,
            DemographicManager demographicManager,
            DemographicContactFewToContactDomainConverter demographicContactToDomainConverter,
            DemographicContactFewToDomainConverter demoContactToDomainConverter,
            ContactDao contactDao,
            ProviderDao providerDao,
            DemographicContactDao demographicContactDao,
			DemographicContactFewConverter demoContactFewConverter,
			ProfessionalSpecialistDao specialistDao)
	{
		this.securityInfoManager = securityInfoManager;
		this.demographicManager = demographicManager;
		this.demographicContactToDomainConverter = demographicContactToDomainConverter;
		this.demoContactToDomainConverter = demoContactToDomainConverter;
		this.contactDao = contactDao;
		this.demographicContactDao = demographicContactDao;
		this.demoContactFewConverter = demoContactFewConverter;
		this.providerDao = providerDao;
		this.specialistDao = specialistDao;
	}

    // ==========================================================================
	// Endpoints
	// ==========================================================================

    @PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DemographicContactFewTo1> updateExternalContact(@PathParam("demographicId") Integer demographicId,
																		DemographicContactFewTo1 demographicContactFewTo1)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.READ, demographicId, "_demographic");

		org.oscarehr.common.model.Contact domainContact = demographicContactToDomainConverter.convert(demographicContactFewTo1);
		Contact updatedContact = demographicManager.updateExternalContact(loggedInInfo, domainContact, demographicContactFewTo1.getContactId());

		org.oscarehr.common.model.DemographicContact domainDemographicContact = demoContactToDomainConverter.convert(demographicContactFewTo1);
		List<DemographicContact> updatedDemoContact = demographicManager.updateExternalDemographicContact(loggedInInfo, demographicId, domainDemographicContact, demographicContactFewTo1.getContactId(), String.valueOf(updatedContact.getId()));

		return RestResponse.successResponse(demographicContactFewTo1);
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<DemographicContactFewTo1> getDemographicContacts(@PathParam("demographicId") Integer demographicId, String category)
	{
		Logger logger = MiscUtils.getLogger();
		try
		{
			// return error if invalid category
			if(!DemographicContact.ALL_CATEGORIES.contains(category))
			{
				return RestSearchResponse.errorResponse("Invalid Category");
			}

			List<DemographicContactFewTo1> results = new ArrayList<>();

			List<DemographicContact> demoContacts = demographicContactDao.findByDemographicNoAndCategory(demographicId, category);
			for (DemographicContact demoContact : demoContacts)
			{
				Integer contactId = Integer.valueOf(demoContact.getContactId());
				DemographicContactFewTo1 demoContactTo1 = new DemographicContactFewTo1();

				if (demoContact.getCategory().equals(DemographicContact.CATEGORY_PERSONAL))
				{
					if (demoContact.getType() == DemographicContact.TYPE_DEMOGRAPHIC)
					{
						Demographic contactD = demographicManager.getDemographic(getLoggedInInfo(), contactId);
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactD);

						DemographicExt cell = demographicManager.getDemographicExt(getLoggedInInfo(), contactId, DemographicExt.KEY_DEMO_CELL);
						DemographicExt hPhoneExt = demographicManager.getDemographicExt(getLoggedInInfo(), contactId, DemographicExt.KEY_DEMO_H_PHONE_EXT);
						DemographicExt wPhoneExt = demographicManager.getDemographicExt(getLoggedInInfo(), contactId, DemographicExt.KEY_DEMO_W_PHONE_EXT);

						if (cell != null && !cell.toString().isEmpty())
						{
							demoContactTo1.setCellPhone(cell.getValue());
						}
						if (hPhoneExt != null && !hPhoneExt.toString().isEmpty())
						{
							demoContactTo1.setHPhoneExt(hPhoneExt.getValue());
						}
						if (wPhoneExt != null && !wPhoneExt.toString().isEmpty())
						{
							demoContactTo1.setWPhoneExt(wPhoneExt.getValue());
						}
					}
					else if (demoContact.getType() == DemographicContact.TYPE_CONTACT)
					{
						Contact contactC = contactDao.findActiveContactById(contactId);
						if (contactC != null)
						{
							demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactC);
						}
					}
					if(demoContactTo1.getContactId() != null)
					{
						results.add(demoContactTo1);
					}
				}
				else if (demoContact.getCategory().equals(DemographicContact.CATEGORY_PROFESSIONAL))
				{
					if (demoContact.getType() == DemographicContact.TYPE_PROVIDER)
					{
						Provider contactP = providerDao.getProvider(contactId.toString());
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactP);
					}
					else if (demoContact.getType() == DemographicContact.TYPE_PROFESSIONALSPECIALIST)
					{
						ProfessionalSpecialist contactS = specialistDao.find(contactId);
						demoContactTo1 = demoContactFewConverter.getAsTransferObject(demoContact, contactS);
					}
					results.add(demoContactTo1);
				}
			}
			return RestSearchResponse.successResponse(results, 0, 0, 0);

		}
		catch (Exception e)
		{
			logger.error("Error",e);
		}
		return RestSearchResponse.errorResponse("Error");
	}
}
