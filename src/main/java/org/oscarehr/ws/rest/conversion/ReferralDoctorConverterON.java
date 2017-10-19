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

import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.ReferralDoctorTo1;
import org.springframework.stereotype.Component;

@Component
public class ReferralDoctorConverterON extends AbstractConverter<ProfessionalSpecialist, ReferralDoctorTo1>
{
	@Override
	public ProfessionalSpecialist getAsDomainObject(LoggedInInfo loggedInInfo, ReferralDoctorTo1 t) throws ConversionException {
		ProfessionalSpecialist proSpec = new ProfessionalSpecialist();

		proSpec.setFirstName(t.getFirstName());
		proSpec.setLastName(t.getLastName());
		proSpec.setProfessionalLetters(t.getProfessionalLetters());
		proSpec.setStreetAddress(t.getStreetAddress());
		proSpec.setPhoneNumber(t.getPhoneNumber());
		proSpec.setFaxNumber(t.getFaxNumber());
		proSpec.setWebSite(t.getWebSite());
		proSpec.setEmailAddress(t.getEmailAddress());
		proSpec.setSpecialtyType(t.getSpecialtyType());
		proSpec.setAnnotation(t.getAnnotation());
		proSpec.setReferralNo(t.getReferralNo());

		return proSpec;
	}
	@Override
	public ReferralDoctorTo1 getAsTransferObject(LoggedInInfo loggedInInfo, ProfessionalSpecialist d) throws ConversionException {
		ReferralDoctorTo1 refDoc = new ReferralDoctorTo1();

		refDoc.setId(d.getId());
		refDoc.setFirstName(d.getFirstName());
		refDoc.setLastName(d.getLastName());
		refDoc.setProfessionalLetters(d.getProfessionalLetters());
		refDoc.setStreetAddress(d.getStreetAddress());
		refDoc.setPhoneNumber(d.getPhoneNumber());
		refDoc.setFaxNumber(d.getFaxNumber());
		refDoc.setWebSite(d.getWebSite());
		refDoc.setEmailAddress(d.getEmailAddress());
		refDoc.setSpecialtyType(d.getSpecialtyType());
		refDoc.setAnnotation(d.getAnnotation());
		refDoc.setReferralNo(d.getReferralNo());

		return refDoc;
	}
}
