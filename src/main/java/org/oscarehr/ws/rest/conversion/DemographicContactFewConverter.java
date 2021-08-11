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
package org.oscarehr.ws.rest.conversion;

import org.oscarehr.common.model.Contact;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.Provider;
import org.oscarehr.ws.rest.to.model.DemographicContactFewTo1;
import org.springframework.stereotype.Component;

@Component
public class DemographicContactFewConverter {
	
	public DemographicContactFewTo1 getAsTransferObject(DemographicContact demographicContact, Object obj) throws ConversionException {
		DemographicContactFewTo1 demographicContactFewTo1 = new DemographicContactFewTo1();
		
		demographicContactFewTo1.setRole(demographicContact.getRole());
		demographicContactFewTo1.setConsentToContact(demographicContact.getConsentToContact());
		demographicContactFewTo1.setEc(demographicContact.getEc());
		demographicContactFewTo1.setSdm(demographicContact.getSdm());
		demographicContactFewTo1.setCategory(demographicContact.getCategory());
		demographicContactFewTo1.setType(demographicContact.getType());
		demographicContactFewTo1.setContactId(demographicContact.getContactId());
		
		if (demographicContact.getType() == DemographicContact.TYPE_DEMOGRAPHIC) {
			Demographic demographic = (Demographic) obj;
			demographicContactFewTo1.setContactId(demographicContact.getContactId());
			demographicContactFewTo1.setFirstName(demographic.getFirstName());
			demographicContactFewTo1.setLastName(demographic.getLastName());
			demographicContactFewTo1.setAddress(demographic.getAddress());
			demographicContactFewTo1.setCity(demographic.getCity());
			demographicContactFewTo1.setPostal(demographic.getPostal());
			demographicContactFewTo1.setProvince(demographic.getProvince());
			demographicContactFewTo1.setEmail(demographic.getEmail());
			
			if (demographic.getPhone() != null) {
				demographicContactFewTo1.setHomePhone(demographic.getPhone());
			}
			if (demographic.getPhone2() != null) {
				demographicContactFewTo1.setWorkPhone(demographic.getPhone2());
			}
		}
		else if (demographicContact.getType() == DemographicContact.TYPE_PROVIDER) {
			Provider provider = (Provider) obj;
			demographicContactFewTo1.setFirstName(provider.getFirstName());
			demographicContactFewTo1.setLastName(provider.getLastName());
			demographicContactFewTo1.setWorkPhone(provider.getPhone());
		}
		else if (demographicContact.getType() == DemographicContact.TYPE_PROFESSIONALSPECIALIST) {
			ProfessionalSpecialist specialist = (ProfessionalSpecialist) obj;
			demographicContactFewTo1.setFirstName(specialist.getFirstName());
			demographicContactFewTo1.setLastName(specialist.getLastName());
			demographicContactFewTo1.setWorkPhone(specialist.getPhoneNumber());
		}
		else if (demographicContact.getType() == DemographicContact.TYPE_CONTACT) {
			Contact contact = (Contact) obj;
			if (contact.getFirstName() != null) demographicContactFewTo1.setFirstName(contact.getFirstName());
			if (contact.getLastName() != null) demographicContactFewTo1.setLastName(contact.getLastName());
			if (contact.getMiddleName() != null) demographicContactFewTo1.setMiddleName(contact.getMiddleName());
			if (contact.getAddress() != null) demographicContactFewTo1.setAddress(String.valueOf(contact.getAddress()));
			if (contact.getAddress2() != null) demographicContactFewTo1.setAddress2(String.valueOf(contact.getAddress2()));
			if (contact.getCity() != null) demographicContactFewTo1.setCity(contact.getCity());
			if (contact.getPostal() != null) demographicContactFewTo1.setPostal(contact.getPostal());
			if (contact.getProvince() != null) demographicContactFewTo1.setProvince(contact.getProvince());
			if (contact.getFax() != null) demographicContactFewTo1.setFax(String.valueOf(contact.getFax()));
			if (contact.getEmail() != null) demographicContactFewTo1.setEmail(contact.getEmail());
			if (contact.getNote() != null) demographicContactFewTo1.setNote(contact.getNote());
			if (contact.getResidencePhone()!=null) demographicContactFewTo1.setHomePhone(contact.getResidencePhone());
			if (contact.getWorkPhone()!=null) demographicContactFewTo1.setWorkPhone(contact.getWorkPhone());
			if (contact.getCellPhone()!=null) demographicContactFewTo1.setCellPhone(contact.getCellPhone());
			if (contact.getWorkPhoneExtension()!=null) demographicContactFewTo1.setWPhoneExt(contact.getWorkPhoneExtension());
			if (contact.getCellPhoneExtension() != null) demographicContactFewTo1.setCPhoneExt(contact.getCellPhoneExtension());
			if (contact.getResidencePhoneExtension() != null) demographicContactFewTo1.setHPhoneExt(contact.getResidencePhoneExtension());
		}
		return demographicContactFewTo1;
	}

	private boolean isPreferredPhone(String phone) {
		if (phone!=null) {
			if (phone.length()>0) {
				if (phone.charAt(phone.length()-1)=='*') return true;
			}
		}
		return false;
	}
}
