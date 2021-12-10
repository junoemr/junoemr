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

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.converter.in.hrm.HrmCategoryModelToDbConverter;
import org.oscarehr.dataMigration.converter.in.hrm.HrmSubClassModelToDbConverter;
import org.oscarehr.dataMigration.converter.out.hrm.HrmCategoryDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmDocument.ReportClass;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.dao.HRMCategoryDao;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
	HRMSubClassService subClassService;

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

	public HRMCategory reconcile(HRMCategory entity, HrmCategoryModel newModel)
	{
		// Separate the subclass list on the updated entity into two parts
		// subClasses with id (previously existing) and those without id (new)

		Set<Integer> activeSubClasses = new HashSet<>();
		List<HrmSubClassModel> newSubClasses = new ArrayList<>();

		if (newModel.getSubClasses() != null)
		{
			for (HrmSubClassModel subClass : newModel.getSubClasses())
			{
				if (subClass.getId() != null)
				{
					activeSubClasses.add(subClass.getId());
				}
				else
				{
					newSubClasses.add(subClass);
				}
			}
		}

		// Check if any existing subclasses on the entity are missing from the updated set.
		// If so, those subclasses should be deactivated.
		LocalDateTime now = LocalDateTime.now();
		List<HRMSubClass> existingSubClasses = entity.getActiveSubClasses();
		for (HRMSubClass existingSubClass: existingSubClasses)
		{
			if (!activeSubClasses.contains(existingSubClass.getId()))
			{
				existingSubClass.setDisabledAt(now);
			}
		}

		// Add any new ones to the entity
		newSubClasses.forEach(newSubClass -> entity.getSubClassList().add(subClassToDBConverter.convert(newSubClass, entity)));

		return entity;
	}

	public Optional<HrmCategoryModel> categorize(HrmDocument report)
	{
		String sendingFacilityId = report.getSendingFacilityId();
		ReportClass reportClass = report.getReportClass();
		String subClassName = StringUtils.trimToNull(report.getReportSubClass());

		String firstAccompanyingSubClassName = null;
		List<HrmObservation> observations = report.getObservations();
		if (observations != null && !observations.isEmpty())
		{
			firstAccompanyingSubClassName = observations.get(0).getAccompanyingSubClass();
		}

		HrmSubClassModel subClass = subClassService.findActiveByAttributes(sendingFacilityId,
			reportClass.getValue(),
			subClassName,
			firstAccompanyingSubClassName);

		if (subClass != null)
		{
			HRMCategory entity = categoryDao.find(subClass.getHrmCategoryId());

			return Optional.ofNullable(toModelConverter.convert(entity));
		}

		return Optional.empty();
	}
}