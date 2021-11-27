package org.oscarehr.dataMigration.converter.out.hrm;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HrmSubClassDbToModelConverter extends AbstractModelConverter<HRMSubClass, HrmSubClassModel>
{
	@Override
	public HrmSubClassModel convert(HRMSubClass entity)
	{
		HrmSubClassModel model = new HrmSubClassModel();
		model.setId(entity.getId());
		model.setFacilityNumber(entity.getSendingFacilityId());
		model.setClassName(entity.getClassName());
		model.setSubClassName(entity.getSubClassName());
		model.setAccompanyingSubClassName(entity.getAccompanyingSubClassName());
		model.setDisabledAt(entity.getDisabledAt());

		return model;
	}
}