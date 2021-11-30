package org.oscarehr.hospitalReportManager.service;

import org.oscarehr.dataMigration.converter.out.hrm.HrmSubClassDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.dao.HRMCategoryDao;
import org.oscarehr.hospitalReportManager.dao.HRMSubClassDao;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Optional;

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
		Optional<HRMSubClass> result = subClassDao.findByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName);

		if (result.isPresent())
		{
			HrmSubClassModel model = entityToModel.convert(result.get());
			return model;
		}

		return null;
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