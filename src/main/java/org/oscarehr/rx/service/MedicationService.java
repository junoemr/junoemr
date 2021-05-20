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
package org.oscarehr.rx.service;

import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.dataMigration.converter.in.DrugModelToDbConverter;
import org.oscarehr.dataMigration.converter.in.PrescriptionModelToDbConverter;
import org.oscarehr.dataMigration.model.medication.Medication;
import org.oscarehr.rx.dao.DrugDao;
import org.oscarehr.rx.dao.PrescriptionDao;
import org.oscarehr.rx.model.Drug;
import org.oscarehr.rx.model.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MedicationService
{
	@Autowired
	private DrugDao drugDao;

	@Autowired
	private PrescriptionDao prescriptionDao;

	@Autowired
	private DrugModelToDbConverter drugModelToDbConverter;

	@Autowired
	protected PartialDateDao partialDateDao;

	@Autowired
	private PrescriptionModelToDbConverter prescriptionModelToDbConverter;


	public void saveNewMedication(Medication medication, Demographic demographic)
	{
		Drug drug = drugModelToDbConverter.convert(medication);
		drug.setDemographicId(demographic.getId());
		Prescription prescription = prescriptionModelToDbConverter.convert(medication);
		prescription.setDemographicId(demographic.getId());

		prescriptionDao.persist(prescription);
		drug.setScriptNo(prescription.getId());
		drugDao.persist(drug);

		/* save drug partial dates */
		org.oscarehr.dataMigration.model.common.PartialDate partialStartDate = medication.getRxStartDate();
		if(partialStartDate != null)
		{
			partialDateDao.setPartialDate(partialStartDate,
					PartialDate.TABLE.DRUGS,
					drug.getId(),
					PartialDate.DRUGS_STARTDATE);
		}
		org.oscarehr.dataMigration.model.common.PartialDate partialWrittenDate = medication.getWrittenDate();
		if(partialWrittenDate != null)
		{
			partialDateDao.setPartialDate(partialWrittenDate,
					PartialDate.TABLE.DRUGS,
					drug.getId(),
					PartialDate.DRUGS_WRITTENDATE);
		}
		org.oscarehr.dataMigration.model.common.PartialDate partialEndDate = medication.getRxEndDate();
		if(partialEndDate != null)
		{
			partialDateDao.setPartialDate(partialEndDate,
					PartialDate.TABLE.DRUGS,
					drug.getId(),
					PartialDate.DRUGS_ENDDATE);
		}
	}

	public void saveNewMedications(List<Medication> medicationList, Demographic demographic)
	{
		for(Medication medication : medicationList)
		{
			saveNewMedication(medication, demographic);
		}
	}
}
