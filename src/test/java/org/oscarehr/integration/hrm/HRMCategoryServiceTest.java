package org.oscarehr.integration.hrm;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HRMCategoryServiceTest
{
	HRMCategoryService categoryService = new HRMCategoryService();

	@Test
	public void test_reconcile_baseCaseNoChangesEmpty()
	{
		HRMCategory entity = generateEntity();
		HrmCategoryModel incomingTransfer = generateModel();
		HRMCategory reconciled = categoryService.reconcile(entity, incomingTransfer);

		Assert.assertTrue("Subclasses are empty", reconciled.getActiveSubClasses().isEmpty());
	}

	@Test
	public void test_reconcile_baseCaseNoChangesNotEmpty()
	{
		HRMCategory entity = generateEntity();
		entity.setSubClassList(generateEntitySubClassList(entity));

		HrmCategoryModel incomingTransfer = generateModel();
		incomingTransfer.setSubClasses(generateModelSubClassList(incomingTransfer));

		HRMCategory reconciled = categoryService.reconcile(entity, incomingTransfer);

		Assert.assertEquals("Subclasses has size 1", 1, reconciled.getActiveSubClasses().size());

		HRMSubClass onlySubClass = reconciled.getActiveSubClasses().get(0);

		Assert.assertEquals("Facility number name is the same", "Dennys", onlySubClass.getSendingFacilityId());
		Assert.assertEquals("Class name is the same", "Diagnostic Imaging", onlySubClass.getClassName());
		Assert.assertEquals("Subclass name is the same", "Breakfast", onlySubClass.getSubClassName());
		Assert.assertEquals("Accompanying subclass name is the same", "Pancakes", onlySubClass.getAccompanyingSubClassName());
		Assert.assertNull("Subclass is not disabled", onlySubClass.getDisabledAt());
	}

	@Test
	public void test_nameChange()
	{
		String newName = "foo";

		HRMCategory entity = generateEntity();
		HrmCategoryModel incomingTransfer = generateModel();
		incomingTransfer.setName(newName);

		HRMCategory reconciled = categoryService.reconcile(entity, incomingTransfer);
		Assert.assertEquals("Entity's name has been updated", reconciled.getCategoryName(), newName);
	}

	@Test
	public void test_deactivate()
	{
		HRMCategory entity = generateEntity();
		HrmCategoryModel incomingTransfer = generateModel();
		incomingTransfer.setDisabledAt(LocalDateTime.now());

		HRMCategory reconciled = categoryService.reconcile(entity, incomingTransfer);
		Assert.assertNotNull("Entity has been disabled", reconciled.getDisabledAt());
	}

	@Test
	public void test_reconcile_addSubClass()
	{

	}

	@Test
	public void test_reconcile_deleteSubClass()
	{

	}

	@Test
	public void test_reconcile_addDeleteSubClasses()
	{

	}

	private HrmCategoryModel generateModel()
	{
		HrmCategoryModel model = new HrmCategoryModel();
		model.setId(1);
		model.setName("test");
		model.setSubClasses(new ArrayList<>());
		model.setDisabledAt(null);

		return model;
	}

	private HRMCategory generateEntity()
	{
		HRMCategory entity = new HRMCategory();
		entity.setId(1);
		entity.setCategoryName("test");
		entity.setDisabledAt(null);
		entity.setSubClassList(new ArrayList<>());

		return entity;
	}

	private List<HRMSubClass> generateEntitySubClassList(HRMCategory parent)
	{
		HRMSubClass subclass = new HRMSubClass();
		subclass.setId(1);
		subclass.setHrmCategory(parent);
		subclass.setSendingFacilityId("Dennys");
		subclass.setClassName("Diagnostic Imaging");
		subclass.setSubClassName("Breakfast");
		subclass.setAccompanyingSubClassName("Pancakes");
		subclass.setDisabledAt(null);

		return Collections.singletonList(subclass);
	}

	private List<HrmSubClassModel> generateModelSubClassList(HrmCategoryModel parent)
	{
		HrmSubClassModel subclass = new HrmSubClassModel();
		subclass.setId(1);
		subclass.setHrmCategoryId(parent.getId());
		subclass.setFacilityNumber("Dennys");
		subclass.setClassName("Diagnostic Imaging");
		subclass.setSubClassName("Breakfast");
		subclass.setAccompanyingSubClassName("Pancakes");
		subclass.setDisabledAt(null);

		return Collections.singletonList(subclass);
	}
}