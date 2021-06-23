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

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.dto.EncounterSectionMenuItem;
import org.oscarehr.casemgmt.exception.EncounterSectionException;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.UrlUtils;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public abstract class EncounterSectionService
{
	protected static final String ELLIPSES = "...";
	protected static final int MAX_LEN_TITLE = 48;
	protected static final int CROP_LEN_TITLE = 45;
	protected static final int MAX_LEN_KEY = 12;
	protected static final int CROP_LEN_KEY = 9;

	protected static final String COLOUR_HIGHLITE = "#FF0000";
	protected static final String COLOUR_INELLIGIBLE = "#FF6600";
	protected static final String COLOUR_PENDING = "#FF00FF";
	protected static final String COLOUR_WARNING = "#FFA500";
	protected static final String COLOUR_RED = "red";

	public static final int INITIAL_ENTRIES_TO_SHOW = 6;
	public static final int INITIAL_OFFSET = 0;

	private String sectionTitle;
	private String sectionTitleColour;

	public abstract String getSectionId();

	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		return "";
	}

	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		return "";
	}

	protected String getSectionTitle()
	{
		return this.sectionTitle;
	}

	protected String getSectionTitleColour()
	{
		return this.sectionTitleColour;
	}

	protected String getSectionTitleKey()
	{
		return null;
	}

	protected String getMenuId()
	{
		return null;
	}

	protected String getMenuHeaderKey()
	{
		return null;
	}

	protected List<EncounterSectionMenuItem> getMenuItems(SectionParameters sectionParams)
	{
		return new ArrayList<>();
	}

	public EncounterSection getInitialSection(
			SectionParameters sectionParams, String title,
			String colour
	) throws EncounterSectionException
	{
		this.sectionTitle = title;
		this.sectionTitleColour = colour;
		return getSection(
				sectionParams,
				INITIAL_ENTRIES_TO_SHOW,
				INITIAL_OFFSET
		);
	}

	public EncounterSection getDefaultSection(SectionParameters params) throws EncounterSectionException
	{
		return getSection(params, INITIAL_ENTRIES_TO_SHOW, INITIAL_OFFSET);
	}

	public EncounterSection getSection(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	) throws EncounterSectionException
	{
		EncounterSection section = new EncounterSection();

		section.setTitle(getSectionTitle());
		section.setTitleKey(getSectionTitleKey());
		section.setColour(getSectionTitleColour());
		section.setCppIssues("");
		section.setAddUrl("");
		section.setIdentUrl("");
		section.setOnClickTitle(getOnClickTitle(sectionParams));
		section.setOnClickPlus(getOnClickPlus(sectionParams));
		section.setMenuId(getMenuId());
		section.setMenuHeaderKey(getMenuHeaderKey());
		section.setMenuItems(getMenuItems(sectionParams));

		EncounterNotes notes = getNotes(
				sectionParams,
				limit,
				offset);

		section.setNotes(notes.getEncounterSectionNotes());

		section.setRemainingNotes(notes.getNoteCount() - notes.getEncounterSectionNotes().size());

		return section;
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	) throws EncounterSectionException
	{
		return EncounterNotes.noNotes();
	}

	public String encodeUrlParam(String param)
	{
		return UrlUtils.encodeUrlParam(param);
	}

	protected static void addMenuItem(List<EncounterSectionMenuItem> menuItems, String text,
									  String textKey, String onClick)
	{
		EncounterSectionMenuItem menuItem = new EncounterSectionMenuItem();
		menuItem.setOnClick(onClick);
		menuItem.setText(text);
		menuItem.setTextKey(textKey);
		menuItems.add(menuItem);
	}

	protected static String formatTitleWithLocalDateTime(String title, LocalDateTime dateTime)
	{
		String formattedTitle = title;

		if(dateTime != null)
		{
			formattedTitle += " " + ConversionUtils.toDateString(dateTime);
		}

		return formattedTitle;
	}

	protected static String getTrimmedText(String text)
	{
		return StringUtils.maxLenString(text, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
	}

	public static class SectionParameters
	{
		private LoggedInInfo loggedInInfo;
		private Locale locale;
		private String contextPath;
		private String roleName;
		private String providerNo;
		private String demographicNo;
		private String patientFirstName;
		private String patientLastName;
		private String familyDoctorNo;
		private String appointmentNo;
		private String chartNo;
		private String programId;
		private String userName;
		private String eChartUUID;

		// XXX: do I remove these?
		private String title;
		private String colour;

		public LoggedInInfo getLoggedInInfo()
		{
			return loggedInInfo;
		}

		public void setLoggedInInfo(LoggedInInfo loggedInInfo)
		{
			this.loggedInInfo = loggedInInfo;
		}

		public Locale getLocale()
		{
			return locale;
		}

		public void setLocale(Locale locale)
		{
			this.locale = locale;
		}

		public String getContextPath()
		{
			return contextPath;
		}

		public void setContextPath(String contextPath)
		{
			this.contextPath = contextPath;
		}

		public String getRoleName()
		{
			return roleName;
		}

		public void setRoleName(String roleName)
		{
			this.roleName = roleName;
		}

		public String getProviderNo()
		{
			return providerNo;
		}

		public void setProviderNo(String providerNo)
		{
			this.providerNo = providerNo;
		}

		public String getDemographicNo()
		{
			return demographicNo;
		}

		public void setDemographicNo(String demographicNo)
		{
			this.demographicNo = demographicNo;
		}

		public String getPatientFirstName()
		{
			return patientFirstName;
		}

		public void setPatientFirstName(String patientFirstName)
		{
			this.patientFirstName = patientFirstName;
		}

		public String getPatientLastName()
		{
			return patientLastName;
		}

		public void setPatientLastName(String patientLastName)
		{
			this.patientLastName = patientLastName;
		}

		public String getFamilyDoctorNo()
		{
			return familyDoctorNo;
		}

		public void setFamilyDoctorNo(String familyDoctorNo)
		{
			this.familyDoctorNo = familyDoctorNo;
		}

		public String getAppointmentNo()
		{
			return appointmentNo;
		}

		public void setAppointmentNo(String appointmentNo)
		{
			this.appointmentNo = appointmentNo;
		}

		public String getChartNo()
		{
			return chartNo;
		}

		public void setChartNo(String chartNo)
		{
			this.chartNo = chartNo;
		}

		public String getProgramId()
		{
			return programId;
		}

		public void setProgramId(String programId)
		{
			this.programId = programId;
		}

		public String getUserName()
		{
			return userName;
		}

		public void setUserName(String userName)
		{
			this.userName = userName;
		}

		public String geteChartUUID()
		{
			return eChartUUID;
		}

		public void seteChartUUID(String eChartUUID)
		{
			this.eChartUUID = eChartUUID;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public String getColour()
		{
			return colour;
		}

		public void setColour(String colour)
		{
			this.colour = colour;
		}
	}
}
