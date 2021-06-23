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
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.managers.SecurityInfoManager;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EncounterDocumentService extends EncounterSectionService
{
	public static final String SECTION_ID = "docs";
	protected static final String SECTION_DOC_TITLE_KEY = "oscarEncounter.Index.msgDocuments";
	protected static final String SECTION_INBOX_TITLE_KEY = "oscarEncounter.Index.inboxManager";
	protected static final String SECTION_TITLE_COLOUR = "#476BB3";

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		if (getInboxFlag())
		{
			return SECTION_INBOX_TITLE_KEY;
		}

		return SECTION_DOC_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		// Set the plus link to call addDocument in index jsp
		String winName = "addDoc" + sectionParams.getDemographicNo();

		if (getInboxFlag())
		{
			String url = sectionParams.getContextPath() + "/mod/docmgmtComp/FileUpload.do" +
					"?method=newupload" +
					"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

			return "popupPage(300,600,'" + winName + "','" + url + "');";
		}

		String url = sectionParams.getContextPath() + "/dms/documentReport.jsp" +
				"?function=demographic" +
				"&doctype=lab" +
				"&functionid=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&curUser=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&mode=add" +
				"&parentAjaxId=" + SECTION_ID;

		return "popupPage(500,1115,'" + winName + "','" + url + "');";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String winName = "docs" + encodeUrlParam(sectionParams.getDemographicNo());

		if (getInboxFlag())
		{
			String url = sectionParams.getContextPath() + "/mod/docmgmtComp/DocList.do" +
					"?method=list" +
					"&demographic_no=" + encodeUrlParam(sectionParams.getDemographicNo());

			return "popupPage(600,1024,'" + winName + "', '" + url + "');";

		}

		String url = sectionParams.getContextPath() + "/dms/documentReport.jsp" +
				"?function=demographic" +
				"&doctype=lab" +
				"&functionid=" + encodeUrlParam(sectionParams.getDemographicNo()) +
				"&curUser=" + encodeUrlParam(sectionParams.getProviderNo());

		return "popupPage(500,1115,'" + winName + "', '" + url + "')";
	}

	private boolean getInboxFlag()
	{
		return oscar.util.plugin.IsPropertiesOn.propertiesOn("inboxmnger");
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

    	if (!securityInfoManager.hasPrivilege(sectionParams.getLoggedInInfo(), "_edoc", "r", null))
    	{
			return EncounterNotes.noNotes();
		}

		ArrayList<EDoc> docList = EDocUtil.listDocs(
				sectionParams.getLoggedInInfo(),
				"demographic",
				sectionParams.getDemographicNo(),
				null,
				EDocUtil.PRIVATE,
				EDocUtil.EDocSort.OBSERVATIONDATE,
				"active"
		);

		String dbFormat = "yyyy-MM-dd";

		for (int i = 0; i < docList.size(); i++)
		{
			EDoc curDoc = docList.get(i);

			String dispFilename = org.apache.commons.lang.StringUtils.trimToEmpty(curDoc.getFileName());

			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String dispDocNo = curDoc.getDocId();
			String title = StringUtils.maxLenString(curDoc.getDescription(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			if (EDocUtil.getDocUrgentFlag(dispDocNo))
			{
				title = StringUtils.maxLenString("!" + curDoc.getDescription(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			}

			String dateString = curDoc.getObservationDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dbFormat);
			LocalDate date;
			String formattedDate = "";
			try
			{
				date = LocalDate.parse(dateString, formatter);
				sectionNote.setUpdateDate(date.atStartOfDay());
				formattedDate = ConversionUtils.toDateTimeString(date.atStartOfDay(), ConversionUtils.DEFAULT_DATE_PATTERN);
			}
			catch(DateTimeParseException ignored) {}

			sectionNote.setText(title);

			sectionNote.setTitle(curDoc.getDescription() + " " + formattedDate);

			String winName = curDoc.getFileName() + sectionParams.getDemographicNo();
			int hash = Math.abs(winName.hashCode());


			String url = sectionParams.getContextPath() + "/dms/ManageDocument.do" +
					"?method=display" +
					"&doc_no=" + encodeUrlParam(dispDocNo) +
					"&providerNo=" + encodeUrlParam(sectionParams.getProviderNo());

			if(curDoc.getRemoteFacilityId() != null)
			{
				url += "&remoteFacilityId=" + curDoc.getRemoteFacilityId().toString();
			}

			String onClickString = "popupPage( 700, 800, '" + hash + "', '" + url +"');";

			if (getInboxFlag())
			{
				if (!EDocUtil.getDocReviewFlag(dispDocNo))
				{
					sectionNote.setColour("FF0000");
				}
				String path = oscar.util.plugin.IsPropertiesOn.getProperty("DOCUMENT_DIR");
				url = sectionParams.getContextPath() + "/mod/docmgmtComp/FillARForm.do" +
						"?method=showInboxDocDetails" +
						"&path=" + path +
						"&demoNo=" + encodeUrlParam(sectionParams.getDemographicNo())+
						"&name=" + encodeUrlParam(dispFilename);
				onClickString = "popupPage( 700, 800, '" + hash + "', '" + url +"');";
			}
			else if(curDoc.getRemoteFacilityId() == null && curDoc.isPDF())
			{
				url = sectionParams.getContextPath() + "/dms/showDocument.jsp" +
						"?segmentID=" + encodeUrlParam(dispDocNo) +
						"&searchProviderNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
						"&status=A" +
						"&inWindow=true" +
						"&chartView";
				onClickString = "popupPage(window.screen.width,window.screen.height,'" + hash + "','" + url + "'); return false;";
			}

			sectionNote.setOnClick(onClickString);

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
