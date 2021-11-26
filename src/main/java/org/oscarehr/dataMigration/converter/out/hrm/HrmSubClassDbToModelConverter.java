package org.oscarehr.dataMigration.converter.out.hrm;

import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HrmSubClassDbToModelConverter {

	public List<HrmSubClassModel> convert(List<HRMSubClass> entities, HrmCategoryModel parent)
	{
		if (entities == null)
		{
			return Collections.emptyList();
		}

		return entities.stream().map(entity -> convert(entity, parent)).collect(Collectors.toList());
	}

	public HrmSubClassModel convert(HRMSubClass entity, HrmCategoryModel parent)
	{
		HrmSubClassModel model = new HrmSubClassModel();
		model.setId(entity.getId());
		model.setParentCategory(parent);
		model.setFacilityNumber(entity.getSendingFacilityId());
		model.setClassName(entity.getClassName());
		model.setSubClassName(entity.getSubClassName());
		model.setAccompanyingSubClassName(entity.getAccompanyingSubClassName());
		model.setDisabledAt(entity.getDisabledAt());

		return model;
	}
}