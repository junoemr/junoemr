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

package org.oscarehr.hospitalReportManager.service;

import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.converter.out.hrm.HrmDocumentDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMDocumentService
{
	@Autowired
	protected HRMDocumentDao hrmDocumentDao;

	@Autowired
	protected HrmDocumentDbToModelConverter entityToModel;

	@Autowired
	protected HrmDocumentModelToDbConverter modelToEntity;

	public HrmDocument getHrmDocument(int hrmDocumentId)
	{
		HRMDocument document = hrmDocumentDao.find(hrmDocumentId);
		return entityToModel.convert(document);
	}

	public int updateHrmDocument(HrmDocument model)
	{
		HRMDocument entity = modelToEntity.convert(model);
		hrmDocumentDao.merge(entity);

		return entity.getId();
	}
}