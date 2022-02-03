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

package org.oscarehr.integration.hrm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.converter.in.hrm.HrmSubClassModelToDbConverter;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xml.hrm.v4_3.ReportClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HRMCategoryServiceTest
{
	@Autowired
	@InjectMocks
	HRMCategoryService categoryService;

	@Mock
	HrmSubClassModelToDbConverter subClassConverter;

	@Before
	public void before() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		when(subClassConverter.convert(anyCollection(), any(HRMCategory.class))).thenCallRealMethod();
		when(subClassConverter.convert(any(HrmSubClassModel.class), any(HRMCategory.class))).thenCallRealMethod();
	}

	@Test
	public void reconcile_baseCaseNoChangesEmpty()
	{
		HRMCategory entity = generateCategoryEntity();
		HrmCategoryModel incomingTransfer = generateCategoryModel();
		HRMCategory reconciled = categoryService.reconcile(entity, incomingTransfer);

		Assert.assertTrue("Category has no subclasses", reconciled.getActiveSubClasses().isEmpty());
	}

	@Test
	public void reconcile_noChangesNotEmpty()
	{
		HRMCategory entity = generateCategoryEntity();
		bindSubClasses(entity, generateDISubClassEntity(entity));

		HrmCategoryModel incomingTransfer = generateCategoryModel();
		HrmSubClassModel noChanges = generateDISubClassModel(incomingTransfer);
		bindSubClasses(incomingTransfer, noChanges);

		HRMCategory reconciled = categoryService.reconcile(entity, incomingTransfer);

		HRMSubClass reconciledSubClass = reconciled.getActiveSubClasses().get(0);

		Assert.assertEquals("Category has one subclass", 1, reconciled.getActiveSubClasses().size());
		Assert.assertTrue("First subclass is mapped correctly", isMappedCorrectly(reconciledSubClass, noChanges));
	}

	@Test
	public void reconcile_addSubClass()
	{
		HRMCategory entity = generateCategoryEntity();
		bindSubClasses(entity, generateDISubClassEntity(entity));

		HrmCategoryModel twoSubClasses = generateCategoryModel();
		HrmSubClassModel firstSubClass = generateDISubClassModel(twoSubClasses);
		HrmSubClassModel secondSubClass = generateMRRSubClassModel(twoSubClasses);
		secondSubClass.setId(null);

		bindSubClasses(twoSubClasses, firstSubClass, secondSubClass);

		HRMCategory reconciled = categoryService.reconcile(entity, twoSubClasses);

		HRMSubClass firstReconciled = reconciled.getSubClassList().get(0);
		HRMSubClass secondReconciled = reconciled.getSubClassList().get(1);

		Assert.assertEquals("Two active subclasses", 2, reconciled.getActiveSubClasses().size());
		Assert.assertTrue("First subclass is mapped correctly", isMappedCorrectly(firstReconciled, firstSubClass));
		Assert.assertTrue("Second subclass is mapped correctly", isMappedCorrectly(secondReconciled, secondSubClass));
	}

	@Test
	public void reconcile_deleteSubClasses()
	{
		HRMCategory entity = generateCategoryEntity();
		bindSubClasses(entity, generateDISubClassEntity(entity), generateMRRSubClassEntity(entity));

		HrmCategoryModel deleteOne = generateCategoryModel();
		// First subclass (DI) is deleted
		HrmSubClassModel remainingSubClass = generateMRRSubClassModel(deleteOne);
		bindSubClasses(deleteOne, remainingSubClass);

		HRMCategory reconciled = categoryService.reconcile(entity, deleteOne);

		HRMSubClass firstReconciled = reconciled.getSubClassList().get(0);
		HRMSubClass secondReconciled = reconciled.getSubClassList().get(1);

		Assert.assertEquals("One active subclass", 1, reconciled.getActiveSubClasses().size());
		Assert.assertNotNull("The first subclass is disabled", firstReconciled.getDisabledAt());
		Assert.assertNull("The second subclass is active", secondReconciled.getDisabledAt());
		Assert.assertTrue("Second subclass is mapped correctly", isMappedCorrectly(secondReconciled, remainingSubClass));
	}

	@Test
	public void reconcile_addDeleteSubClass()
	{
		HRMCategory entity = generateCategoryEntity();
		bindSubClasses(entity, generateDISubClassEntity(entity), generateMRRSubClassEntity(entity));

		HrmCategoryModel deleteOneAddOne = generateCategoryModel();

		// First subclass (DI) is deleted

		HrmSubClassModel unchanged = generateMRRSubClassModel(deleteOneAddOne);

		HrmSubClassModel added = new HrmSubClassModel();
		added.setHrmCategoryId(deleteOneAddOne.getId());
		added.setFacilityNumber("TimHortons");
		added.setClassName(ReportClass.CARDIO_RESPIRATORY_REPORT.value());
		added.setAccompanyingSubClassName("DoubleDouble");
		added.setDisabledAt(null);

		bindSubClasses(deleteOneAddOne, unchanged, added);

		HRMCategory reconciled = categoryService.reconcile(entity, deleteOneAddOne);
		HRMSubClass firstReconciled = reconciled.getSubClassList().get(0);
		HRMSubClass secondReconciled = reconciled.getSubClassList().get(1);
		HRMSubClass thirdReconciled = reconciled.getSubClassList().get(2);

		Assert.assertEquals("Two active subclasses", 2, reconciled.getActiveSubClasses().size());
		Assert.assertNotNull("The first subclass is disabled", firstReconciled.getDisabledAt());
		Assert.assertNull("The second subclass is active", secondReconciled.getDisabledAt());
		Assert.assertNull("The third subclass is active", thirdReconciled.getDisabledAt());
		Assert.assertTrue("Second subclass is mapped correctly", isMappedCorrectly(secondReconciled, unchanged));
		Assert.assertTrue("Third subclass is mapped correctly", isMappedCorrectly(thirdReconciled, added));
	}

	//
	//  Convenience methods below
	//

	private void bindSubClasses(HRMCategory parent, HRMSubClass... subClasses)
	{
		List<HRMSubClass> subClassList = new ArrayList<>(Arrays.asList(subClasses));
		parent.setSubClassList(subClassList);
	}

	private void bindSubClasses(HrmCategoryModel parent, HrmSubClassModel... subClasses)
	{
		List<HrmSubClassModel> subClassList = new ArrayList<>(Arrays.asList(subClasses));
		parent.setSubClasses(subClassList);
	}

	private HrmCategoryModel generateCategoryModel()
	{
		HrmCategoryModel model = new HrmCategoryModel();
		model.setId(1);
		model.setName("Breakfast");
		model.setSubClasses(new ArrayList<>());
		model.setDisabledAt(null);

		return model;
	}

	private HRMCategory generateCategoryEntity()
	{
		HRMCategory entity = new HRMCategory();
		entity.setId(1);
		entity.setCategoryName("test");
		entity.setDisabledAt(null);
		entity.setSubClassList(new ArrayList<>());

		return entity;
	}

	private HRMSubClass generateDISubClassEntity(HRMCategory parent)
	{
		HRMSubClass subclass = new HRMSubClass();
		subclass.setId(1);
		subclass.setHrmCategory(parent);
		subclass.setSendingFacilityId("Dennys");
		subclass.setClassName("Diagnostic Imaging");
		subclass.setAccompanyingSubClassName("Pancakes");
		subclass.setDisabledAt(null);

		return subclass;
	}

	private HRMSubClass generateMRRSubClassEntity(HRMCategory parent)
	{
		HRMSubClass subclass = new HRMSubClass();
		subclass.setId(2);
		subclass.setHrmCategory(parent);
		subclass.setSendingFacilityId("McDonalds");
		subclass.setClassName("Medical Records Report");
		subclass.setAccompanyingSubClassName("Hashbrowns");
		subclass.setDisabledAt(null);

		return subclass;
	}

	private HrmSubClassModel generateDISubClassModel(HrmCategoryModel parent)
	{
		HrmSubClassModel subclass = new HrmSubClassModel();
		subclass.setId(1);
		subclass.setHrmCategoryId(parent.getId());
		subclass.setFacilityNumber("Dennys");
		subclass.setClassName("Diagnostic Imaging");
		subclass.setAccompanyingSubClassName("Pancakes");
		subclass.setDisabledAt(null);

		return subclass;
	}

	private HrmSubClassModel generateMRRSubClassModel(HrmCategoryModel parent)
	{
		HrmSubClassModel subclass = new HrmSubClassModel();
		subclass.setId(2);
		subclass.setHrmCategoryId(parent.getId());
		subclass.setFacilityNumber("McDonalds");
		subclass.setClassName("Medical Records Report");
		subclass.setAccompanyingSubClassName("Hashbrowns");
		subclass.setDisabledAt(null);

		return subclass;
	}

	private boolean isMappedCorrectly(HRMSubClass entity, HrmSubClassModel model)
	{
		// Intentional .equals here, we want this to fail if id is ever null
		boolean parentMatch = entity.getHrmCategory().getId().equals(model.getHrmCategoryId());

		boolean facilityNumberMatch = Objects.equals(entity.getSendingFacilityId(), model.getFacilityNumber());
		boolean nameMatch = Objects.equals(entity.getClassName(), model.getClassName());
		boolean subClassNameMatch = Objects.equals(entity.getSubClassName(), model.getSubClassName());
		boolean accompanyingSubClassNameMatch = Objects.equals(entity.getAccompanyingSubClassName(), model.getAccompanyingSubClassName());
		boolean disabledAtMatch = Objects.equals(entity.getDisabledAt(), model.getDisabledAt());

		return parentMatch &&
			facilityNumberMatch &&
			nameMatch &&
			subClassNameMatch &&
			accompanyingSubClassNameMatch &&
			disabledAtMatch;
	}
}