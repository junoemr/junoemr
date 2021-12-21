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
package org.oscarehr.demographic.service;

import org.apache.commons.lang3.NotImplementedException;
import org.oscarehr.contact.dao.ContactDao;
import org.oscarehr.contact.dao.DemographicContactDao;
import org.oscarehr.contact.entity.Contact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.dataMigration.converter.in.contact.DemographicContactModelToDbConverter;
import org.oscarehr.dataMigration.converter.in.contact.ExternalContactModelToDbConverter;
import org.oscarehr.dataMigration.model.contact.DemographicContact;
import org.oscarehr.dataMigration.model.contact.ExternalContact;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.oscarehr.contact.entity.DemographicContact.TYPE_CONTACT;
import static org.oscarehr.contact.entity.DemographicContact.TYPE_DEMOGRAPHIC;
import static org.oscarehr.contact.entity.DemographicContact.TYPE_PROFESSIONALSPECIALIST;
import static org.oscarehr.contact.entity.DemographicContact.TYPE_PROVIDER;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DemographicContactService
{
	@Autowired
	private DemographicContactModelToDbConverter demographicContactModelToDbConverter;

	@Autowired
	private ExternalContactModelToDbConverter externalContactModelToDbConverter;

	@Autowired
	private DemographicContactDao demographicContactDao;

	@Autowired
	private ContactDao contactDao;

	public org.oscarehr.contact.entity.DemographicContact addNewContact(DemographicContact demographicContactModel, Demographic demographic)
	{
		org.oscarehr.contact.entity.DemographicContact dbContact = demographicContactModelToDbConverter.convert(demographicContactModel);
		dbContact.setDemographicNo(demographic.getId());

		// check if contact needs to be saved first
		if(dbContact.getContactId() == null)
		{
			String newContactId;
			switch(dbContact.getType())
			{
				case TYPE_PROVIDER:
				{
					newContactId = persistProviderContact((Provider) demographicContactModel.getContact()).getId();
					break;
				}
				case TYPE_DEMOGRAPHIC:
				{
					newContactId = String.valueOf(persistDemographicContact((Demographic) demographicContactModel.getContact()).getId());
					break;
				}
				case TYPE_CONTACT:
				{
					newContactId = String.valueOf(persistExternalContact((ExternalContact) demographicContactModel.getContact()).getId());
					break;
				}
				case TYPE_PROFESSIONALSPECIALIST:
				{
					newContactId = String.valueOf(persistSpecialistContact(demographicContactModel.getContact()).getId());
					break;
				}
				default:
				{
					throw new IllegalArgumentException("Unknown Contact type: " + dbContact.getType());
				}
			}
			dbContact.setContactId(newContactId);
		}

		demographicContactDao.persist(dbContact);
		return dbContact;
	}

	public void addNewContacts(List<DemographicContact> demographicContactModelList, Demographic demographic)
	{
		for(DemographicContact demographicContact : demographicContactModelList)
		{
			addNewContact(demographicContact, demographic);
		}
	}

	private ProviderData persistProviderContact(Provider providerModel)
	{
		throw new NotImplementedException("Conversion not implemented");
	}

	private org.oscarehr.demographic.model.Demographic persistDemographicContact(Demographic demographicModel)
	{
		throw new NotImplementedException("Conversion not implemented");
	}

	private Contact persistExternalContact(ExternalContact externalContactModel)
	{
		Contact dbContact = externalContactModelToDbConverter.convert(externalContactModel);
		contactDao.persist(dbContact);
		return dbContact;
	}

	private ProfessionalSpecialist persistSpecialistContact(org.oscarehr.dataMigration.model.contact.Contact specialistContactPlaceholder)
	{
		throw new NotImplementedException("Conversion not implemented");
	}
}
