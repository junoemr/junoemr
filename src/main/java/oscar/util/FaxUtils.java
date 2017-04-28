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

package oscar.util;

import java.util.Date;

import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.dms.EDocUtil;

/**
 * Added for OscarHost fax utilities extended functionality, 2016
 * @author robert
 *
 */
public class FaxUtils {
	
	private static final Logger logger = MiscUtils.getLogger();

	/**
	 * Add a note to the encounter page recording the eform fax recipient, sender, time etc.
	 */
	/*
	public static boolean addFaxEformEncounterNote(String demographic_no, String providerId, String programNo, String faxNo, Long formId) {
		return addFaxEncounterNote(demographic_no, providerId, programNo, faxNo, formId, CaseManagementNoteLink.EFORMDATA, "eForm");
	}
	*/
	/**
	 * Add a note to the encounter page recording the Consultation fax recipient, sender, time etc.
	 */
	/*
	public static boolean addFaxConsultEncounterNote(String demographic_no, String providerId, String programNo, String faxNo, Long formId) {
		return addFaxEncounterNote(demographic_no, providerId, programNo, faxNo, formId, CaseManagementNoteLink.CONSULTATION, "Consultation");
	}
	*/
	/**
	 * Add a note to the encounter page recording the Document fax recipient, sender, time etc.
	 */
	public static boolean addFaxDocumentEncounterNote(String demographic_no, 
		String providerId, String programNo, String faxNo, Long documentId) {

		return addFaxEncounterNote(demographic_no, providerId, programNo, 
			faxNo, documentId, CaseManagementNoteLink.DOCUMENT, "Document");
	}
	/**
	 * Add an encounter note specific to faxing.
	 */
	private static boolean addFaxEncounterNote(String demographic_no, String providerId, 
			String programNo, String faxNo, Long formId, Integer linkType, String noteTypeName) {
		
		try {
			CaseManagementManager cmm = (CaseManagementManager) SpringUtils.getBean("caseManagementManager");
			if(demographic_no != null && !demographic_no.trim().isEmpty()) {
				Provider provider = EDocUtil.getProvider(providerId);
				if( providerId == null || provider == null) {
					providerId = "-1"; //system
					provider = EDocUtil.getProvider(providerId);
					logger.warn("Missing or invalid providerNo for fax encounter note. Assigned to system (-1)");
				}
				SecRoleDao secRoleDao = (SecRoleDao) SpringUtils.getBean("secRoleDao");
				SecRole doctorRole = secRoleDao.findByName("doctor");
				Date now = new Date();
				String provFirstName = provider.getFirstName();
				String provLastName = provider.getLastName();
				
				String strNote = "Faxed " + noteTypeName +" to " + faxNo + " at " + now + " by " + provFirstName + " " + provLastName + ".";
	
				// create the note
				CaseManagementNote cmn = new CaseManagementNote();
				cmn.setDemographic_no(demographic_no);
				cmn.setProgram_no(programNo);
				cmn.setUpdate_date(now);
				cmn.setObservation_date(now);
				cmn.setPosition(0);
				cmn.setReporter_program_team("0");
				cmn.setPassword("NULL");
				cmn.setLocked(false);
				cmn.setReporter_caisi_role(doctorRole.getId().toString());
				cmn.setNote(strNote);
				cmn.setHistory(strNote);
				cmn.setProviderNo(providerId);
				cmn.setSigned(true);
				cmn.setSigning_provider_no(providerId);
				
				// save the note and create the link
				Long note_id = cmm.saveNoteSimpleReturnID(cmn);
				CaseManagementNoteLink cmLink = new CaseManagementNoteLink(linkType, formId, note_id);
				EDocUtil.addCaseMgmtNoteLink(cmLink);
				
				logger.info("Saved note_id=" + note_id.toString() + " for demographic " + demographic_no);
				return true;
			}
			else {
				logger.error("failed to add fax note to encounter notes. Missing demographicNo");
			}
		}
		catch (Exception e) {
			logger.error("Error adding fax encounter note to patient echart", e);
		}
		return false;
	}
}
