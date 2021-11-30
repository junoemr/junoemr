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

import org.oscarehr.dataMigration.converter.out.hrm.HrmSubClassDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.dao.HRMCategoryDao;
import org.oscarehr.hospitalReportManager.dao.HRMSubClassDao;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.oscarehr.hospitalReportManager.search.HrmSubClassCriteriaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class HRMSubClassService
{
	@Autowired
	HRMSubClassDao subClassDao;

	@Autowired
	HRMCategoryDao categoryDao;

	@Autowired
	HrmSubClassDbToModelConverter entityToModel;

	public HrmSubClassModel findActiveByAttributes(String facilityId, String reportClass, String subClassName, String accompanyingSubClassName)
	{
		HrmSubClassCriteriaSearch searchParams = new HrmSubClassCriteriaSearch();
		searchParams.setSendingFacilityId(facilityId);
		searchParams.setClassName(reportClass);
		searchParams.setSubClassName(subClassName);
		searchParams.setAccompanyingSubClassName(accompanyingSubClassName);
		searchParams.setActiveOnly(true);

		List<HRMSubClass> results = subClassDao.criteriaSearch(searchParams);

		HRMSubClass found = null;
		if (results.size() > 1)
		{
			throw new DataIntegrityViolationException("Fatal:  More than one identical active subclass found");
		}
		if (results.size() == 1)
		{
			found = results.get(0);
		}

		return entityToModel.convert(found);
	}

	public HrmSubClassModel deactivateSubClass(Integer subClassId)
	{
		HRMSubClass entity = subClassDao.find(subClassId);
		if (!entity.isDisabled())
		{
			entity.setDisabledAt(LocalDateTime.now());
			subClassDao.merge(entity);
		}

		return entityToModel.convert(entity);
	}

	public HrmSubClassModel createSubClass(HrmSubClassModel model)
	{
		HRMCategory categoryEntity = categoryDao.find(model.getHrmCategoryId());

		HRMSubClass subClassEntity = new HRMSubClass();
		subClassEntity.setHrmCategory(categoryEntity);
		subClassEntity.setSendingFacilityId(model.getFacilityNumber());
		subClassEntity.setClassName(model.getClassName());
		subClassEntity.setSubClassName(model.getSubClassName());
		subClassEntity.setAccompanyingSubClassName(model.getAccompanyingSubClassName());

		subClassDao.merge(subClassEntity);

		return entityToModel.convert(subClassEntity);
	}
}