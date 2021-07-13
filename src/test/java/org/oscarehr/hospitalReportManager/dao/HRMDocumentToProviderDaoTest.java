/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.hospitalReportManager.dao;

import static org.junit.Assert.assertNotNull;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_BILLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_CANCELLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_NEW;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.CARDIO_RESPIRATORY;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.CONSULTANT;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.DIAGNOSTIC_IMAGING;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.DIAGNOSTIC_TEST;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.LAB;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.MEDICAL_RECORDS;
import static org.oscarehr.dataMigration.model.hrm.HrmDocument.REPORT_CLASS.OTHER;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.dataMigration.model.appointment.AppointmentStatus;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HRMDocumentToProviderDaoTest extends DaoTestFixtures
{
	@Autowired
	public HRMDocumentToProviderDao hrmDocumentToProviderDao;

	@Before
	public void before() throws Exception
	{
		//SchemaUtils.restoreTable("HRMDocumentToProvider","HRMDocument");
		MockitoAnnotations.initMocks(this);

		List<HRMDocument> hrmDocuments = new ArrayList<>();
		List<HRMDocumentToProvider> hrmDocumentToProviders = new ArrayList<>();

		HRMDocument hrmDocumentDiagnosticImaging = new HRMDocument();
		HRMDocument hrmDocumentDiagnosticTest = new HRMDocument();
		HRMDocument hrmDocumentCardioRespiratory = new HRMDocument();
		HRMDocument hrmDocumentMedicalRecords = new HRMDocument();
		HRMDocument hrmDocumentConsultant = new HRMDocument();
		HRMDocument hrmDocumentLab = new HRMDocument();
		HRMDocument hrmDocumentOther = new HRMDocument();

		// fill in hrmDocumentMock values here
		hrmDocumentDiagnosticImaging.setReportType(DIAGNOSTIC_IMAGING.getValue());
		hrmDocumentDiagnosticTest.setReportType(DIAGNOSTIC_TEST.getValue());
		hrmDocumentCardioRespiratory.setReportType(CARDIO_RESPIRATORY.getValue());
		hrmDocumentMedicalRecords.setReportType(MEDICAL_RECORDS.getValue());
		hrmDocumentConsultant.setReportType(CONSULTANT.getValue());
		hrmDocumentLab.setReportType(LAB.getValue());
		hrmDocumentOther.setReportType(OTHER.getValue());

		hrmDocuments.add(hrmDocumentDiagnosticImaging);
		hrmDocuments.add(hrmDocumentDiagnosticTest);
		hrmDocuments.add(hrmDocumentCardioRespiratory);
		hrmDocuments.add(hrmDocumentMedicalRecords);
		hrmDocuments.add(hrmDocumentConsultant);
		hrmDocuments.add(hrmDocumentLab);
		hrmDocuments.add(hrmDocumentOther);

		for (HRMDocument hrmDocument: hrmDocuments)
		{
			HRMDocumentToProvider hrmDocumentToProvider = new HRMDocumentToProvider();
			ProviderData providerData = new ProviderData();
			providerData.setProviderNo(-1);
			providerData.setProviderType(ProviderData.PROVIDER_TYPE_DOCTOR);

			hrmDocumentToProvider.setProvider(providerData);
			hrmDocumentToProvider.setHrmDocument(hrmDocument);
			hrmDocumentToProvider.setViewed(true);
			hrmDocumentToProvider.setSignedOff(true);

			hrmDocumentToProviders.add(hrmDocumentToProvider);
		}

		Mockito.when(hrmDocumentToProviderDao.findByProviderNoLimit("", null, null, true, true)).thenAnswer(invocationOnMock ->
		{
			for (HRMDocumentToProvider hrmDocumentToProvider : hrmDocumentToProviders)
			{
				String reportType = hrmDocumentToProvider.getHrmDocument().getReportType();

				//String reportType = invocationOnMock.getArgument(0);
				final String DIAGNOSTIC_IMAGING = "Diagnostic Imaging Report";
				final String DIAGNOSTIC_TEST = "Diagnostic Test Report";
				final String CARDIO_RESPIRATORY = "Cardio Respiratory Report";
				final String MEDICAL_RECORDS = "Medical Records Report";
				final String CONSULTANT = "Consultant Report";
				final String LAB = "Lab Report";
				final String OTHER = "Other Letter";

				switch (reportType)
				{
					case DIAGNOSTIC_IMAGING: return hrmDocumentDiagnosticImaging;
					case DIAGNOSTIC_TEST: return hrmDocumentDiagnosticTest;
					case CARDIO_RESPIRATORY: return hrmDocumentCardioRespiratory;
					case MEDICAL_RECORDS: return hrmDocumentMedicalRecords;
					case CONSULTANT: return hrmDocumentConsultant;
					case LAB: return hrmDocumentLab;
					case OTHER: return hrmDocumentOther;
				}
			}
			return null;
		});
	}

	@Test
	public void testCreate() throws Exception {
		HRMDocumentToProvider entity = new HRMDocumentToProvider();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		hrmDocumentToProviderDao.persist(entity);
		assertNotNull(entity.getId());
	}
}
