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
import org.oscarehr.eform.model.EForm;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
	private EFormDao eFormTemplateDao;

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
	public EForm addEFormTemplate(String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, String roleType)
	{
		EForm eFormTemplate = new EForm();
		eFormTemplate = copyToModel(eFormTemplate, formName, formSubject, fileName, htmlStr, creator, showLatestFormOnly, patientIndependent, roleType);

		eFormTemplateDao.persist(eFormTemplate);
		return eFormTemplate;
	}

	public EForm updateEFormTemplate(Integer id, String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, String roleType)
	{
		EForm eFormTemplate = eFormTemplateDao.find(id);
		if(eFormTemplate == null)
		{
			throw new IllegalArgumentException("No EFormTemplate found for id " + id);
		}
		eFormTemplate = copyToModel(eFormTemplate, formName, formSubject, fileName, htmlStr, creator, showLatestFormOnly, patientIndependent, roleType);

		eFormTemplateDao.merge(eFormTemplate);
		return eFormTemplate;
	}
	private EForm copyToModel(EForm eFormTemplate, String formName, String formSubject, String fileName, String htmlStr, String creator, boolean showLatestFormOnly, boolean patientIndependent, String roleType)
	{
		eFormTemplate.setFormName(formName);
		eFormTemplate.setFileName(fileName);
		eFormTemplate.setSubject(formSubject);
		eFormTemplate.setCreator(creator);
		eFormTemplate.setCurrent(true);
		eFormTemplate.setFormHtml(htmlStr);
		eFormTemplate.setShowLatestFormOnly(showLatestFormOnly);
		eFormTemplate.setPatientIndependent(patientIndependent);
		eFormTemplate.setRoleType(roleType);

		Date now = new Date();
		eFormTemplate.setFormDate(now);
		eFormTemplate.setFormTime(now);
		return eFormTemplate;
	}


	/**
	 * delete an eform template with the given ID
	 *
	 * @param formId - id of the eform template to delete
	 * @throws IllegalArgumentException - if the id is invalid
	 */
	public EForm deleteTemplate(Integer formId)
	{
		return setTemplateDeleted(formId, true);
	}
	/**
	 * restore an eform template with the given ID
	 * @param formId - id of the eform template to restore
	 * @throws IllegalArgumentException - if the id is invalid
	 */
	public EForm restoreTemplate(Integer formId)
	{
		return setTemplateDeleted(formId, false);
	}
	private EForm setTemplateDeleted(Integer formId, boolean isDeleted)
	{
		EForm eFormTemplate = eFormTemplateDao.find(formId);
		if (eFormTemplate == null) {
			throw new IllegalArgumentException("No EForm exists with id " + formId);
		}
		eFormTemplate.setCurrent(!isDeleted);
		eFormTemplateDao.merge(eFormTemplate);
		return eFormTemplate;
	}
}
