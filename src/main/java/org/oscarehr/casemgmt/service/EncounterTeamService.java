/*
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

package org.oscarehr.casemgmt.service;

import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.ContactDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.ProfessionalContact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterTeamService extends EncounterEpisodeService
{
	@Autowired
	DemographicContactDao demographicContactDao;

	@Autowired
	ContactDao contactDao;

	@Autowired
	ProviderDao providerDao;

	@Autowired
	ProfessionalSpecialistDao professionalSpecialistDao;

	public List<EncounterSectionNote> getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		try
		{
			//boolean healthCareTeamEnabled = OscarProperties.getInstance().isHealthcareTeamEnabled();

			////Set left hand module heading and link
			//String winName = "contact" + bean.demographicNo;
			//String pathview, pathedit;
			//int width = 0;
			//int height = 0;

			//if(healthCareTeamEnabled)
			//{
			//	pathview = request.getContextPath() +
			//			"/demographic/displayHealthCareTeam.jsp?view=detached&demographicNo=" +
			//			bean.demographicNo;
			//	pathedit = request.getContextPath() +
			//			"/demographic/manageHealthCareTeam.jsp?view=detached&demographicNo=" +
			//			bean.demographicNo;
			//	width = 650;
			//	height = 400;
			//}
			//else
			//{
			//	pathview = request.getContextPath() + "/demographic/professionalSpecialistSearch.jsp?keyword=&submit=Search";
			//	pathedit = request.getContextPath() + "/demographic/Contact.do?method=manage&demographic_no=" + bean.demographicNo;
			//	width = 650;
			//	height = 900;
			//}

			//String url = "popupPage(" + height + "," + width + ",'" + winName + "','" + pathview + "')";

			//if(healthCareTeamEnabled)
			//{
			//	Dao.setLeftHeading("Health Care Team");
			//	width = 700;
			//	height = 500;
			//} else {
			//	Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.contacts"));
			//	width = 800;
			//	height = 1000;
			//}

			//Dao.setLeftURL(url);

			////set right hand heading link
			//winName = "AddContact" + bean.demographicNo;
			//url = "popupPage(" + height + "," + width + ",'" + winName + "','" + pathedit + "'); return false;";
			//Dao.setRightURL(url);
			//Dao.setRightHeadingID(cmd);

			List<DemographicContact> contacts =
					demographicContactDao.findActiveByDemographicNo(Integer.parseInt(demographicNo));

			for(DemographicContact contact:contacts)
			{
				//only show professional contacts
				if(contact.getCategory().equals(DemographicContact.CATEGORY_PERSONAL))
				{
					continue;
				}

				String name="N/A";
				String specialty = "";
				String workPhone = "";
				//String consent = "";

				if(contact.getType() == DemographicContact.TYPE_CONTACT)
				{
					ProfessionalContact c = (ProfessionalContact)contactDao.find(Integer.parseInt(contact.getContactId()));
					name = c.getLastName() + "," + c.getFirstName();
					specialty = c.getSpecialty();
					workPhone = c.getWorkPhone();
				}
				else if(contact.getType() == DemographicContact.TYPE_PROVIDER)
				{
					Provider p = providerDao.getProvider(contact.getContactId());
					name = p.getFormattedName();
					specialty = p.getSpecialty();
					workPhone = p.getWorkPhone();
				}
				else if(contact.getType() == DemographicContact.TYPE_PROFESSIONALSPECIALIST)
				{
					ProfessionalSpecialist p = professionalSpecialistDao.find(Integer.parseInt(contact.getContactId()));
					name = p.getFormattedName();
					specialty = p.getSpecialtyType()!=null?p.getSpecialtyType():"";
					workPhone = p.getPhoneNumber();
				}

				//contactDao.find(Integer.parseInt(contact.getContactId()));
				//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
				//48.45

				EncounterSectionNote sectionNote = new EncounterSectionNote();

				String itemHeader = StringUtils.maxLenString(name, 20, 17, ELLIPSES) +
						((specialty.length()>0)?StringUtils.maxLenString("  "+ specialty, 14, 11, ELLIPSES):"") +
						((workPhone.length()>0)?StringUtils.maxLenString("  "+workPhone, 17, 14, ELLIPSES):"");
				//item.setTitle((contact.isConsentToContact()?"":"*") + itemHeader);
				//String consent = contact.isConsentToContact()?"Ok to contact":"Do not contact";
				//item.setLinkTitle(name + " " + specialty + " " + workPhone + " " + consent);

				sectionNote.setText((contact.isConsentToContact()?"":"*") + itemHeader);

			//	//item.setDate(contact.getUpdateDate());
			//	int hash = Math.abs(winName.hashCode());

			//	if(healthCareTeamEnabled)
			//	{
			//		if( contact.getType() == DemographicContact.TYPE_PROVIDER )
			//		{
			//			url = "alert('Edit internal providers from the provider menu');return false;";
			//		}
			//		else
			//		{
			//			url = "popupPage(650,500,'" + hash + "','" +
			//					request.getContextPath() +
			//					"/demographic/Contact.do?method=editHealthCareTeam&contactId="+
			//					contact.getId() +"&role=" +
			//					contact.getRole() +
			//					"'); return false;";
			//		}

			//	} else {

			//		if(contact.getType() == DemographicContact.TYPE_CONTACT) {
			//			url = "popupPage(500,900,'" + hash + "','" + request.getContextPath() + "/demographic/Contact.do?method=editProContact&pcontact.id="+ contact.getContactId() +"'); return false;";
			//		} else if (contact.getType() == DemographicContact.TYPE_CONTACT){
			//			String roles =(String) request.getSession().getAttribute("userrole");
			//			if(roles.indexOf("admin") != -1)
			//				url = "popupPage(500,900,'" + hash + "','" + request.getContextPath() + "/admin/providerupdateprovider.jsp?keyword="+ contact.getContactId() +"'); return false;";
			//			else
			//				url = "alert('Cannot Edit');return false;";
			//		} else if(contact.getType() == DemographicContact.TYPE_PROFESSIONALSPECIALIST) {
			//			url = "popupPage(500,900,'" + hash + "','" + request.getContextPath() + "/oscarEncounter/EditSpecialists.do?specId="+ contact.getContactId() +"'); return false;";
			//		}

			//	}
				//item.setURL(url);


				//Dao.addItem(item);
				out.add(sectionNote);
			}

		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
			return new ArrayList<>();
		}

		return out;
	}
}
