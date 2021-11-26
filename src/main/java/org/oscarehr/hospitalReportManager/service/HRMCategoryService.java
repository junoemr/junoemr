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

import org.oscarehr.dataMigration.converter.in.hrm.HrmCategoryModelToDbConverter;
import org.oscarehr.dataMigration.converter.out.hrm.HrmCategoryDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.hospitalReportManager.dao.HRMCategoryDao;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMCategoryService
{
	@Autowired
	HRMCategoryDao categoryDao;

	@Autowired
	HrmCategoryDbToModelConverter toModelConverter;

	@Autowired
	HrmCategoryModelToDbConverter toDBConverter;

	public HrmCategoryModel createCategory(HrmCategoryModel category)
	{
		if (isNameInUse(category.getName()))
		{
			throw new ValidationException("An active category with name " + category.getName() + " already exists");
		}

		HRMCategory entity = toDBConverter.convert(category);
		categoryDao.persist(entity);

		return toModelConverter.convert(entity);
	}

	public HrmCategoryModel updateCategory(HrmCategoryModel category)
	{
		Optional<HRMCategory> existing = categoryDao.findActiveByName(category.getName());
		existing.ifPresent(existingCategory ->
		{
			// Prevent renames to existing names;
			if (!existingCategory.getId().equals(category.getId()))
			{
				throw new ValidationException("An active category with name " + category.getName() + " already exists");
			}
		});

		HRMCategory entity = toDBConverter.convert(category);
		categoryDao.merge(entity);

		return toModelConverter.convert(entity);
	}

	public HrmCategoryModel deactivateCategory(Integer categoryId)
	{
		HRMCategory entity = categoryDao.find(categoryId);
		if (!entity.isDisabled())
		{
			entity.setDisabledAt(LocalDateTime.now());
			categoryDao.merge(entity);
		}

		return toModelConverter.convert(entity);
	}

	public List<HrmCategoryModel> getActiveCategories()
	{
		List<HRMCategory> entity = categoryDao.getActiveCategories();
		return toModelConverter.convert(entity);
	}

	private boolean isNameInUse(String categoryName)
	{
		return categoryDao.findActiveByName(categoryName).isPresent();
	}
}