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

package org.oscarehr.ws.external.rest.v1.conversion;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferInbound;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DemographicConverter
{
	public static Demographic getAsDomainObject(DemographicTransferInbound transfer)
	{
		Demographic demographic = new Demographic();

		// base info
		demographic.setFirstName(transfer.getFirstName());
		demographic.setLastName(transfer.getLastName());
		demographic.setDateOfBirth(transfer.getDateOfBirth());
		demographic.setTitle(transfer.getTitle());
		demographic.setHin(transfer.getHin());
		demographic.setVer(transfer.getHcVersion());
		demographic.setHcType(transfer.getHcType());
		demographic.setHcRenewDate(ConversionUtils.toNullableLegacyDate(transfer.getHcRenewDate()));
		demographic.setSex(transfer.getSex());
		demographic.setHcEffectiveDate(ConversionUtils.toNullableLegacyDate(transfer.getHcEffectiveDate()));
		demographic.setSin(transfer.getSin());
		demographic.setDateJoined(ConversionUtils.toNullableLegacyDate(transfer.getDateJoined()));
		demographic.setEndDate(ConversionUtils.toNullableLegacyDate(transfer.getEndDate()));
		demographic.setPatientStatus(transfer.getPatientStatus());
		demographic.setPatientStatusDate(ConversionUtils.toNullableLegacyDate(transfer.getPatientStatusDate()));
		demographic.setVeteranNo(transfer.getVeteranNo());

		// contact info
		demographic.setAddress(transfer.getAddress());
		demographic.setProvince(transfer.getProvince());
		demographic.setCity(transfer.getCity());
		demographic.setPostal(transfer.getPostal());
		demographic.setEmail(transfer.getEmail());
		demographic.setPhone(transfer.getPrimaryPhone());
		demographic.setPhone2(transfer.getSecondaryPhone());

		//roster info
		demographic.setRosterStatus(transfer.getRosterStatus());
		demographic.setRosterDate(ConversionUtils.toNullableLegacyDate(transfer.getRosterDate()));
		demographic.setRosterTerminationDate(ConversionUtils.toNullableLegacyDate(transfer.getRosterTerminationDate()));
		demographic.setRosterTerminationReason(transfer.getRosterTerminationReason());

		// physician info
		demographic.setProviderNo(transfer.getProviderNo());
		demographic.setReferralDoctor(
				"<rdohip>" + StringEscapeUtils.escapeXml(StringUtils.trimToEmpty(transfer.getReferralDoctorNo())) + "</rdohip>" +
				"<rd>" + StringEscapeUtils.escapeXml(StringUtils.trimToEmpty(transfer.getReferralDoctorName())) + "</rd>");
		demographic.setFamilyDoctor(
				"<fd>" + StringEscapeUtils.escapeXml(StringUtils.trimToEmpty(transfer.getFamilyDoctorNo())) + "</fd>" +
				"<fdname>" + StringEscapeUtils.escapeXml(StringUtils.trimToEmpty(transfer.getFamilyDoctorName())) + "</fdname>");

		// other info
		demographic.setPcnIndicator(transfer.getPcnIndicator());
		demographic.setChartNo(transfer.getChartNo());
		demographic.setAlias(transfer.getAlias());
		demographic.setChildren(transfer.getChildren());
		demographic.setSourceOfIncome(transfer.getSourceOfIncome());
		demographic.setCitizenship(transfer.getCitizenship());
		demographic.setSpokenLanguage(transfer.getSpokenLanguage());
		demographic.setOfficialLanguage(transfer.getOfficialLanguage());
		demographic.setCountryOfOrigin(transfer.getCountryOfOrigin());
		demographic.setNewsletter(transfer.getNewsletter());
		demographic.setAnonymous(transfer.getAnonymous());

		return demographic;
	}
	public static List<DemographicExt> getExtensionList(DemographicTransferInbound transfer)
	{
		List<DemographicExt> extensionList = new ArrayList<>(1);

		if(transfer.getCellPhone() != null)
		{
			DemographicExt extension = new DemographicExt();
			extension.setDateCreated(new Date());
			extension.setKey("demo_cell");
			extension.setValue(transfer.getCellPhone());
			extensionList.add(extension);
		}
		return extensionList;
	}
	public static DemographicCust getCustom(DemographicTransferInbound transfer)
	{
		DemographicCust demographicCustom = null;
		if (transfer.getNurse() != null || transfer.getResident() != null || transfer.getAlert() != null || transfer.getMidwife() != null || transfer.getNotes() != null)
		{
			demographicCustom = new DemographicCust();
			demographicCustom.setNurse(transfer.getNurse());
			demographicCustom.setResident(transfer.getResident());
			demographicCustom.setAlert(transfer.getAlert());
			demographicCustom.setMidwife(transfer.getMidwife());
			demographicCustom.setNotes("<unotes>" + StringEscapeUtils.escapeXml(StringUtils.trimToEmpty(transfer.getNotes())) + "</unotes>");
		}
		return demographicCustom;
	}
}
