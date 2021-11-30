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
import org.oscarehr.dataMigration.converter.in.hrm.HrmSubClassModelToDbConverter;
import org.oscarehr.dataMigration.converter.out.hrm.HrmCategoryDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.hospitalReportManager.dao.HRMCategoryDao;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

	@Autowired
	HrmSubClassModelToDbConverter subClassToDBConverter;

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

	public HrmCategoryModel deactivateCategory(Integer categoryId)
	{
		HRMCategory entity = categoryDao.find(categoryId);
		if (!entity.isDisabled())
		{
			entity.setDisabledAt(LocalDateTime.now());

			for (HRMSubClass subClassEntity : entity.getSubClassList())
			{
				if (!subClassEntity.isDisabled())
				{
					subClassEntity.setDisabledAt(LocalDateTime.now());
				}
			}

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

	public HrmCategoryModel updateCategoryName(Integer categoryId, String newName)
	{
		Optional<HRMCategory> sameName = categoryDao.findActiveByName(newName);
		sameName.ifPresent(existingCategory ->
		{
			// Prevent renames to existing names;
			if (!existingCategory.getId().equals(categoryId))
			{
				throw new ValidationException("An active category with name " + newName + " already exists");
			}
		});

		HRMCategory category = categoryDao.find(categoryId);
		category.setCategoryName(newName);
		categoryDao.merge(category);

		return toModelConverter.convert(category);
	}

	public HrmCategoryModel updateCategory(HrmCategoryModel newModel)
	{
		HRMCategory existingEntity = categoryDao.find(newModel.getId());
		HRMCategory updated = reconcile(existingEntity, newModel);
		categoryDao.merge(updated);

		return toModelConverter.convert(updated);
	}

	public HrmCategoryModel getActiveCategory(Integer categoryId)
	{
		HRMCategory category = categoryDao.find(categoryId);
		if (category.isDisabled())
		{
			return null;
		}

		return toModelConverter.convert(category);
	}

	HRMCategory reconcile(HRMCategory entity, HrmCategoryModel newModel)
	{
		Set<Integer> newSubClassIds = new HashSet<>();
		newModel.getSubClasses().forEach(subClass -> {
			if (subClass.getId() != null)
			{
				newSubClassIds.add(subClass.getId());
			}
		});

		// If a subclass exists in the old set, but not in the new set, it was deactivated
		LocalDateTime now = LocalDateTime.now();
		entity.getSubClassList().forEach(subClass -> {
			if (!newSubClassIds.contains(subClass.getId()))
			{
				subClass.setDisabledAt(now);
			}
		});

		// If a subclass doesn't have an id in the new set, it needs to be created
		newModel.getSubClasses()
			.stream()
			.filter(subClass -> subClass.getId() == null)
			.forEach(newSubClass -> entity.getSubClassList().add(subClassToDBConverter.convert(newSubClass, entity)));

		return entity;
	}


}