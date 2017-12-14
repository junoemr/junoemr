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

package org.oscarehr.eform.service;

import org.apache.log4j.Logger;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EForm service for handling interactions with EForms.
 * These are the forms found in admin that are used as templates when adding new eforms to patients
 * See also - EForm service
 */
@Service
@Transactional
public class EFormTemplate
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDao eFormDao;

	/**
	 * Save a new EForm template
	 * @param formName
	 * @param formSubject
	 * @param fileName
	 * @param htmlStr
	 * @param creator
	 * @param showLatestFormOnly
	 * @param patientIndependent
	 * @param roleType
	 * @return
	 */
	public org.oscarehr.eform.model.EForm saveEFormTemplate(String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, String roleType) {

		org.oscarehr.eform.model.EForm eformTemplate = new org.oscarehr.eform.model.EForm();
		eformTemplate.setFormName(formName);
		eformTemplate.setFileName(fileName);
		eformTemplate.setSubject(formSubject);
		eformTemplate.setCreator(creator);
		eformTemplate.setCurrent(true);
		eformTemplate.setFormHtml(htmlStr);
		eformTemplate.setShowLatestFormOnly(showLatestFormOnly);
		eformTemplate.setPatientIndependent(patientIndependent);
		eformTemplate.setRoleType(roleType);

		eFormDao.persist(eformTemplate);

		return eformTemplate;
	}

	/**
	 * delete an eform template with the given ID
	 * @param formId - id of the eform template to delete
	 * @throws IllegalArgumentException - if the id is invalid
	 */
	public void deleteTemplate(Integer formId)
	{
		setTemplateDeleted(formId, true);
	}
	/**
	 * restore an eform template with the given ID
	 * @param formId - id of the eform template to restore
	 * @throws IllegalArgumentException - if the id is invalid
	 */
	public void restoreTemplate(Integer formId)
	{
		setTemplateDeleted(formId, false);
	}
	private void setTemplateDeleted(Integer formId, boolean isDeleted)
	{
		org.oscarehr.eform.model.EForm eform = eFormDao.find(formId);
		if (eform == null) {
			throw new IllegalArgumentException("No EForm exists with id " + formId);
		}
		eform.setCurrent(isDeleted);
		eFormDao.merge(eform);
	}
}
