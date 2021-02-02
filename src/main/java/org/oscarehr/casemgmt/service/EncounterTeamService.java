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

package org.oscarehr.casemgmt.service;

import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.dao.ContactDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.ProfessionalContact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterTeamService extends EncounterSectionService
{
	public static final String SECTION_ID = "contacts";
	private static final String SECTION_TEAM_TITLE_KEY = "global.healthCareTeam";
	private static final String SECTION_CONTACTS_TITLE_KEY = "global.contacts";
	private static final String SECTION_TITLE_COLOUR = "#6699CC";

	private static final String CONTACT_OK = "Ok to contact";
	private static final String CONTACT_NOT_OK = "Do not contact";
	private static final String CONTACT_OK_PREFIX = "";
	private static final String CONTACT_NOT_OK_PREFIX = "*";

	private static final int NAME_MAX_LENGTH = 20;
	private static final int NAME_SHORTED = 17;
	private static final int SPECIALTY_MAX_LENGTH = 14;
	private static final int SPECIALTY_SHORTED = 11;
	private static final int PHONE_MAX_LENGTH = 17;
	private static final int PHONE_SHORTED = 14;

	private static final String WIN_NAME_PREFIX = "AddContact";


	@Autowired
	DemographicContactDao demographicContactDao;

	@Autowired
	ContactDao contactDao;

	@Autowired
	ProviderDao providerDao;

	@Autowired
	ProfessionalSpecialistDao professionalSpecialistDao;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		if(isHealthCareTeamEnabled())
		{
			return SECTION_TEAM_TITLE_KEY;
		}

		return SECTION_CONTACTS_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		String pathedit;
		int width = 0;
		int height = 0;

		if(isHealthCareTeamEnabled())
		{
			pathedit = sectionParams.getContextPath() +
					"/demographic/manageHealthCareTeam.jsp" +
					"?view=detached&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());
			width = 650;
			height = 400;
		}
		else
		{
			pathedit = sectionParams.getContextPath() + "/demographic/Contact.do" +
					"?method=manage" +
					"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());
			width = 650;
			height = 900;
		}

		String winName = WIN_NAME_PREFIX + sectionParams.getDemographicNo();
		return "popupPage(" + height + "," + width + ",'" + winName + "','" + pathedit + "');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String pathview;
		int width = 0;
		int height = 0;
		if(isHealthCareTeamEnabled())
		{
			pathview = sectionParams.getContextPath() + "/demographic/displayHealthCareTeam.jsp" +
					"?view=detached" +
					"&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());
			width = 700;
			height = 500;
		}
		else
		{
			pathview = sectionParams.getContextPath() + "/demographic/professionalSpecialistSearch.jsp" +
					"?keyword=" +
					"&submit=Search";
			width = 800;
			height = 1000;
		}

		String winName = "contact" + sectionParams.getDemographicNo();
		return "popupPage(" + height + "," + width + ",'" + winName + "','" + pathview + "')";
	}

	private boolean isHealthCareTeamEnabled()
	{
		return OscarProperties.getInstance().isHealthcareTeamEnabled();
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> notes = new ArrayList<>();

		try
		{

			//Dao.setRightHeadingID(cmd);

			List<DemographicContact> contacts =
					demographicContactDao.findActiveByDemographicNo(Integer.parseInt(sectionParams.getDemographicNo()));

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

				EncounterSectionNote sectionNote = new EncounterSectionNote();

				String text = StringUtils.maxLenString(name, NAME_MAX_LENGTH, NAME_SHORTED, ELLIPSES);

				if(specialty.length() > 0)
				{
					text += StringUtils.maxLenString("  " + specialty, SPECIALTY_MAX_LENGTH, SPECIALTY_SHORTED, ELLIPSES);
				}

				if(workPhone.length() > 0)
				{
					text += StringUtils.maxLenString("  " + workPhone, PHONE_MAX_LENGTH, PHONE_SHORTED, ELLIPSES);
				}


				String consent = CONTACT_OK;
				String consentPrefix = CONTACT_OK_PREFIX;
				if(!contact.isConsentToContact())
				{
					consent = CONTACT_NOT_OK;
					consentPrefix = CONTACT_NOT_OK_PREFIX;
				}

				sectionNote.setText(consentPrefix + text);
				sectionNote.setTitle(name + " " + specialty + " " + workPhone + " " + consent);

				String winName = WIN_NAME_PREFIX + sectionParams.getDemographicNo();
				int hash = Math.abs(winName.hashCode());
				String onClickString = "";

				if(isHealthCareTeamEnabled())
				{
					if( contact.getType() == DemographicContact.TYPE_PROVIDER )
					{
						 onClickString = "alert('Edit internal providers from the provider menu');";
					}
					else
					{
						String url = sectionParams.getContextPath() + "/demographic/Contact.do" +
								"?method=editHealthCareTeam" +
								"&contactId=" + encodeUrlParam(contact.getId().toString()) +
								"&role=" + encodeUrlParam(contact.getRole());

						onClickString = "popupPage(650,500,'" + hash + "','" + url + "');";
					}
				}
				else
				{
					if(contact.getType() == DemographicContact.TYPE_CONTACT)
					{
						String url = sectionParams.getContextPath() + "/demographic/Contact.do" +
								"?method=editProContact" +
								"&pcontact.id="+ contact.getContactId();
						onClickString = "popupPage(500,900,'" + hash + "','" + url +"');";
					}
					else if (contact.getType() == DemographicContact.TYPE_CONTACT)
					{
						if(sectionParams.getRoleName().indexOf("admin") != -1)
						{
							String url = sectionParams.getContextPath() + "/admin/providerupdateprovider.jsp" +
									"?keyword=" + contact.getContactId();
							onClickString = "popupPage(500,900,'" + hash + "','" + url +"');";
						}
						else
						{
							onClickString = "alert('Cannot Edit');return false;";
						}
					}
					else if(contact.getType() == DemographicContact.TYPE_PROFESSIONALSPECIALIST)
					{
						String url = sectionParams.getContextPath() + "/oscarEncounter/EditSpecialists.do" +
								"?specId="+ contact.getContactId();
						onClickString = "popupPage(500,900,'" + hash + "','" + url +"');";
					}
				}

				sectionNote.setOnClick(onClickString);

				notes.add(sectionNote);
			}

		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
			return EncounterNotes.noNotes();
		}

		return EncounterNotes.limitedEncounterNotes(notes, offset, limit);
	}
}
