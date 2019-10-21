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

package org.oscarehr.casemgmt.dto;

import java.util.List;

public class EncounterSection
{
	public static final String TYPE_PREVENTIONS = "Preventions";
	public static final String TYPE_TICKLER = "Tickler";
	public static final String TYPE_DISEASE_REGISTRY = "DiseaseRegistry";
	public static final String TYPE_FORMS = "Forms";
	public static final String TYPE_EFORMS = "eForms";
	public static final String TYPE_DOCUMENTS = "Documents";
	public static final String TYPE_LAB_RESULTS = "LabResults";
	public static final String TYPE_MESSENGER = "Messenger";
	public static final String TYPE_MEASUREMENTS = "Measurments";
	public static final String TYPE_CONSULTATIONS = "Consultations";
	public static final String TYPE_ALLERGIES = "Allergies";
	public static final String TYPE_MEDICATIONS = "Medications";
	public static final String TYPE_OTHER_MEDS = "OMeds";
	public static final String TYPE_RISK_FACTORS = "RiskFactors";
	public static final String TYPE_FAMILY_HISTORY = "FamHistory";
	public static final String TYPE_UNRESOLVED_ISSUES = "UnresolvedIssues";
	public static final String TYPE_RESOLVED_ISSUES = "ResolvedIssues";
	public static final String TYPE_DECISION_SUPPORT_ALERTS = "DecisionSupportAlerts";
	public static final String TYPE_EPISODES = "Episodes";
	public static final String TYPE_HEALTH_CARE_TEAM = "HealthCareTeam";

	private String title;
	private String titleKey;
	private String cppIssues;
	private String addUrl;
	private String identUrl;
	private String onClickTitle;
	private String onClickPlus;
	private String menuTitle;
	private String menuHeaderKey;
	private String menuId;
	private List<EncounterSectionMenuItem> menuItems;
	private String colour;
	private Integer remainingNotes;
	private List<EncounterSectionNote> notes;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitleKey()
	{
		return titleKey;
	}

	public void setTitleKey(String titleKey)
	{
		this.titleKey = titleKey;
	}

	public String getCppIssues()
	{
		return cppIssues;
	}

	public void setCppIssues(String cppIssues)
	{
		this.cppIssues = cppIssues;
	}

	public String getAddUrl()
	{
		return addUrl;
	}

	public void setAddUrl(String addUrl)
	{
		this.addUrl = addUrl;
	}

	public String getIdentUrl()
	{
		return identUrl;
	}

	public void setIdentUrl(String identUrl)
	{
		this.identUrl = identUrl;
	}

	public String getOnClickTitle()
	{
		return onClickTitle;
	}

	public void setOnClickTitle(String onClickTitle)
	{
		this.onClickTitle = onClickTitle;
	}

	public String getOnClickPlus()
	{
		return onClickPlus;
	}

	public void setOnClickPlus(String onClickPlus)
	{
		this.onClickPlus = onClickPlus;
	}

	public String getMenuTitle()
	{
		return menuTitle;
	}

	public void setMenuTitle(String menuTitle)
	{
		this.menuTitle = menuTitle;
	}

	public String getMenuHeaderKey()
	{
		return menuHeaderKey;
	}

	public void setMenuHeaderKey(String menuHeaderKey)
	{
		this.menuHeaderKey = menuHeaderKey;
	}

	public String getMenuId()
	{
		return menuId;
	}

	public void setMenuId(String menuId)
	{
		this.menuId = menuId;
	}

	public List<EncounterSectionMenuItem> getMenuItems()
	{
		return menuItems;
	}

	public void setMenuItems(List<EncounterSectionMenuItem> menuItems)
	{
		this.menuItems = menuItems;
	}

	public String getColour()
	{
		return colour;
	}

	public void setColour(String colour)
	{
		this.colour = colour;
	}

	public Integer getRemainingNotes()
	{
		return remainingNotes;
	}

	public void setRemainingNotes(Integer remainingNotes)
	{
		this.remainingNotes = remainingNotes;
	}

	public List<EncounterSectionNote> getNotes()
	{
		return notes;
	}

	public void setNotes(List<EncounterSectionNote> notes)
	{
		this.notes = notes;
	}
}
