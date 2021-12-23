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