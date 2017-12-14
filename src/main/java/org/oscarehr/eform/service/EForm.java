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
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.dao.EFormValueDao;
import org.oscarehr.eform.model.EFormValue;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * EForm service for handling interactions with patient EForms.
 * These are the forms attached to patients which contain patient data
 * See also - EFormTemplate service
 */
@Service
@Transactional
public class EForm
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private EFormDataDao eformDataDao;

	@Autowired
	private EFormValueDao eFormValueDao;

	public void saveEformValues(Integer formId, Integer formDataId, Integer demographicNo, Map<String,String> valueMap)
	{
		for(Map.Entry<String, String> entry : valueMap.entrySet())
		{
			EFormValue eFormValue = new EFormValue();
			eFormValue.setFormId(formId);
			eFormValue.setFormDataId(formDataId);
			eFormValue.setDemographicId(demographicNo);
			eFormValue.setVarName(entry.getKey());
			eFormValue.setVarValue(entry.getValue());

			eFormValueDao.persist(eFormValue);
		}
	}

}
