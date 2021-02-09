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
package org.oscarehr.demographicImport.service;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.log4j.Logger;
import org.oscarehr.allergy.service.AllergyService;
import org.oscarehr.appointment.service.Appointment;
import org.oscarehr.common.dao.DemographicPharmacyDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.PharmacyInfoDao;
import org.oscarehr.common.hl7.copd.writer.JunoGenericImportLabWriter;
import org.oscarehr.common.hl7.writer.HL7LabWriter;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.model.DemographicPharmacy;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.service.DemographicContactService;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.demographicImport.converter.in.PharmacyModelToDbConverter;
import org.oscarehr.demographicImport.converter.in.PreventionModelToDbConverter;
import org.oscarehr.demographicImport.converter.in.ReviewerModelToDbConverter;
import org.oscarehr.demographicImport.converter.out.BaseDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.PatientRecordModelConverter;
import org.oscarehr.demographicImport.exception.DuplicateDemographicException;
import org.oscarehr.demographicImport.logger.ExportLogger;
import org.oscarehr.demographicImport.logger.ImportLogger;
import org.oscarehr.demographicImport.model.PatientRecord;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.encounterNote.EncounterNote;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.lab.LabObservation;
import org.oscarehr.demographicImport.model.lab.LabObservationResult;
import org.oscarehr.demographicImport.model.pharmacy.Pharmacy;
import org.oscarehr.demographicImport.util.ExportPreferences;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.encounterNote.service.ConcernNoteService;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.encounterNote.service.FamilyHistoryNoteService;
import org.oscarehr.encounterNote.service.MedicalHistoryNoteService;
import org.oscarehr.encounterNote.service.ReminderNoteService;
import org.oscarehr.encounterNote.service.RiskFactorNoteService;
import org.oscarehr.encounterNote.service.SocialHistoryNoteService;
import org.oscarehr.hospitalReportManager.service.HRMService;
import org.oscarehr.labs.service.LabService;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dao.PreventionExtDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.rx.service.MedicationService;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.all.parsers.other.JunoGenericLabHandler;
import oscar.util.ConversionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.oscarehr.provider.model.ProviderData.SYSTEM_PROVIDER_NO;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ImportExportService
{
	private static final Logger logger = Logger.getLogger(ImportExportService.class);

	@Autowired
	private AllergyService allergyService;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private DemographicService demographicService;

	@Autowired
	private DemographicContactService demographicContactService;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private EncounterNoteService encounterNoteService;

	@Autowired
	private MedicalHistoryNoteService medicalHistoryNoteService;

	@Autowired
	private FamilyHistoryNoteService familyHistoryNoteService;

	@Autowired
	private SocialHistoryNoteService socialHistoryNoteService;

	@Autowired
	private RiskFactorNoteService riskFactorNoteService;

	@Autowired
	private ConcernNoteService concernNoteService;

	@Autowired
	private ReminderNoteService reminderNoteService;

	@Autowired
	private LabService labService;

	@Autowired
	private Appointment appointmentService;

	@Autowired
	private ReviewerModelToDbConverter reviewerModelToDbConverter;

	@Autowired
	private MedicationService medicationService;

	@Autowired
	private MeasurementsService measurementsService;

	@Autowired
	private MeasurementDao measurementDao;

	@Autowired
	private PreventionDao preventionDao;

	@Autowired
	private PreventionExtDao preventionExtDao;

	@Autowired
	private PreventionModelToDbConverter preventionModelToDbConverter;

	@Autowired
	private AppointmentStatusCache appointmentStatusCache;

	@Autowired
	private ImporterExporterFactory importerExporterFactory;

	@Autowired
	private PatientRecordModelConverter patientRecordModelConverter;

	@Autowired
	private PharmacyInfoDao pharmacyInfoDao;

	@Autowired
	private DemographicPharmacyDao demographicPharmacyDao;

	@Autowired
	private PharmacyModelToDbConverter pharmacyModelToDbConverter;

	@Autowired
	private HRMService hrmService;

	public List<GenericFile> exportDemographics(ImporterExporterFactory.EXPORTER_TYPE importType,
	                                            ExportLogger exportLogger,
	                                            List<PatientRecord> patientRecords,
	                                            ExportPreferences preferences) throws Exception
	{
		exportLogger.logSummaryHeader();
		DemographicExporter exporter = importerExporterFactory.getExporter(importType, exportLogger, preferences);
		List<GenericFile> fileList = new ArrayList<>(patientRecords.size() + 2);

		try
		{
			for(PatientRecord patientRecord : patientRecords)
			{
				logger.info("Export Demographic " + patientRecord.getDemographic().getId());
				GenericFile file = exporter.exportDemographic(patientRecord);
				fileList.add(file);
			}
			exportLogger.logSummaryFooter();
			fileList.addAll(exporter.getAdditionalFiles(preferences));
		}
		finally
		{
			BaseDbToModelConverter.clearProviderCache();
			appointmentStatusCache.clear();
		}

		return fileList;
	}

	public List<GenericFile> exportDemographicsWithLookup(ImporterExporterFactory.EXPORTER_TYPE importType,
	                                                      ExportLogger exportLogger,
	                                                      List<String> demographicIdList,
	                                                      ExportPreferences preferences) throws Exception
	{
		//TODO batch query get demographics
		List<PatientRecord> patientRecords = new ArrayList<>(demographicIdList.size());
		for(String demographicIdStr : demographicIdList)
		{
			logger.info("Load Demographic " + demographicIdStr);
			Integer demographicId = Integer.parseInt(demographicIdStr);
			org.oscarehr.demographic.model.Demographic demographic = demographicDao.find(demographicId);
			PatientRecord patientRecord = patientRecordModelConverter.convert(demographic);
			patientRecords.add(patientRecord);
		}

		return exportDemographics(importType, exportLogger, patientRecords, preferences);
	}

	public void importDemographic(ImporterExporterFactory.IMPORTER_TYPE importType,
	                              ImporterExporterFactory.IMPORT_SOURCE importSource,
	                              ImportLogger importLogger,
	                              GenericFile importFile,
	                              String documentLocation,
	                              boolean skipMissingDocs,
	                              DemographicImporter.MERGE_STRATEGY mergeStrategy) throws Exception
	{
		DemographicImporter importer = importerExporterFactory.getImporter(importType, importSource, importLogger, documentLocation, skipMissingDocs);
		importer.verifyFileFormat(importFile);
		PatientRecord patientRecord = importer.importDemographic(importFile);

		Demographic demographicModel = patientRecord.getDemographic();
		org.oscarehr.demographic.model.Demographic dbDemographicDuplicate = findDuplicate(demographicModel);
		boolean duplicateDetected = (dbDemographicDuplicate != null);

		org.oscarehr.demographic.model.Demographic dbDemographic;
		if(duplicateDetected)
		{
			dbDemographic = dbDemographicDuplicate;
			if(DemographicImporter.MERGE_STRATEGY.MERGE.equals(mergeStrategy))
			{
				logger.warn("Merge with existing demographic: " + dbDemographic.getId());
			}
			else
			{
				throw new DuplicateDemographicException("Duplicate demographic: " + dbDemographic.getId());
			}
		}
		else
		{
			dbDemographic = demographicService.addNewDemographicRecord(SYSTEM_PROVIDER_NO, demographicModel);
			logger.info("Persisted new demographic: " + dbDemographic.getId());
		}

		patientRecord.getDemographic().setId(dbDemographic.getId());
		demographicContactService.addNewContacts(patientRecord.getContactList(), dbDemographic);
		persistNotes(patientRecord, dbDemographic);
		persistLabs(patientRecord, dbDemographic);

		appointmentService.saveNewAppointments(patientRecord.getAppointmentList(), dbDemographic);
		appointmentStatusCache.clear();

		medicationService.saveNewMedications(patientRecord.getMedicationList(), dbDemographic);
		persistMeasurements(patientRecord, dbDemographic);

		allergyService.saveNewAllergies(patientRecord.getAllergyList(), dbDemographic);

		persistPreventions(patientRecord, dbDemographic);
		persistPharmacy(patientRecord, dbDemographic);

		// persist documents last to minimize import errors with disk IO
		persistHrmDocuments(patientRecord, dbDemographic);
		documentService.uploadAllNewDemographicDocument(patientRecord.getDocumentList(), dbDemographic);

		importLogger.logSummaryLine(patientRecord);
		writeAuditLogImportStatement(dbDemographic, importType, importSource, duplicateDetected);
	}

	private void persistNotes(PatientRecord patientRecord, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		socialHistoryNoteService.saveSocialHistoryNote(patientRecord.getSocialHistoryNoteList(), dbDemographic);
		familyHistoryNoteService.saveFamilyHistoryNote(patientRecord.getFamilyHistoryNoteList(), dbDemographic);
		medicalHistoryNoteService.saveMedicalHistoryNotes(patientRecord.getMedicalHistoryNoteList(), dbDemographic);
		reminderNoteService.saveReminderNote(patientRecord.getReminderNoteList(), dbDemographic);
		riskFactorNoteService.saveRiskFactorNote(patientRecord.getRiskFactorNoteList(), dbDemographic);
		concernNoteService.saveConcernNote(patientRecord.getConcernNoteList(), dbDemographic);
		encounterNoteService.saveChartNotes(patientRecord.getEncounterNoteList(), dbDemographic);
	}

	private void persistMeasurements(PatientRecord patientRecord, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		for(org.oscarehr.demographicImport.model.measurement.Measurement measurement : patientRecord.getMeasurementList())
		{
			Measurement dbMeasurement = measurementsService.createNewMeasurement(
					dbDemographic.getId(),
					SYSTEM_PROVIDER_NO,
					measurement.getTypeCode(),
					measurement.getMeasurementValue(),
					ConversionUtils.toLegacyDateTime(measurement.getObservationDateTime()));
			measurementDao.persist(dbMeasurement);
		}
	}

	private void persistPreventions(PatientRecord patientRecord, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		List<Prevention> preventions = preventionModelToDbConverter.convert(patientRecord.getImmunizationList());

		for(Prevention prevention : preventions)
		{
			prevention.setDemographicId(dbDemographic.getId());
			preventionDao.persist(prevention);

			for(PreventionExt ext : prevention.getPreventionExtensionList())
			{
				preventionExtDao.persist(ext);
			}
		}
	}

	private void persistLabs(PatientRecord patientRecord, org.oscarehr.demographic.model.Demographic dbDemographic) throws HL7Exception, IOException
	{
		for(Lab lab : patientRecord.getLabList())
		{
			HL7LabWriter labWriter = new JunoGenericImportLabWriter(patientRecord.getDemographic(), lab);
			String labHl7 = labWriter.encode();

			MessageHandler parser = Factory.getHandler(JunoGenericLabHandler.LAB_TYPE_VALUE, labHl7);
			// just in case
			if(parser == null)
			{
				throw new RuntimeException("No Parser available for lab");
			}

			// allow each lab type to make modifications to the hl7 if needed.
			// This is for special cases only most labs return an identical string to the input parameter
			labHl7 = parser.preUpload(labHl7);

			// check if the lab has passed validation and can be saved
			if(parser.canUpload())
			{
				List<ProviderData> reviewers = reviewerModelToDbConverter.convert(lab.getReviewers());

				// remove provider duplicates
				Map<String, ProviderData> duplicateMap = new HashMap<>();
				for(ProviderData provider : reviewers)
				{
					duplicateMap.put(provider.getId(), provider);
				}
				List<ProviderData> filteredReviewers = new ArrayList<>(duplicateMap.values());

				Hl7TextMessage hl7TextMessage = labService.persistNewHL7Lab(parser, labHl7, "Juno-Import", 0, dbDemographic, filteredReviewers, ProviderInboxItem.FILE);
				parser.postUpload();

				// indexes are assumed to line up since this is the same iteration happening in the JunoGenericImportLabWriter
				int obrIndex = 0;
				for(LabObservation labObservation : lab.getLabObservationList())
				{
					int obxIndex = 0;
					for(LabObservationResult result : labObservation.getResults())
					{
						EncounterNote annotationNote = result.getAnnotation();
						if(annotationNote != null)
						{
							encounterNoteService.saveLabObxNote(annotationNote, dbDemographic, hl7TextMessage, obrIndex, obxIndex);
						}
						obxIndex++;
					}
					obrIndex++;
				}
			}
			else
			{
				logger.warn("Hl7 Lab Could Not be Uploaded");
			}
		}
	}

	private void persistHrmDocuments(PatientRecord patientRecord, org.oscarehr.demographic.model.Demographic dbDemographic) throws Exception
	{
		// for imports, we build our own HRM documents before saving the record
		DemographicExporter exporter = importerExporterFactory.getExporter(ImporterExporterFactory.EXPORTER_TYPE.HRM_4);

		for(HrmDocument hrmDocument : patientRecord.getHrmDocumentList())
		{
			/* HRM docs can have multiple records per file, just like CDS. However we need 1 file per report.
			* To do this, we create a temporary data structure for each report */
			PatientRecord tempRecord = new PatientRecord();
			tempRecord.setDemographic(patientRecord.getDemographic());
			tempRecord.addHrmDocument(hrmDocument);
			XMLFile hrmFile = (XMLFile) exporter.exportDemographic(tempRecord);
			hrmDocument.setReportFile(hrmFile);
		}

		hrmService.uploadAllNewHRMDocuments(patientRecord.getHrmDocumentList(), dbDemographic);
	}

	private void persistPharmacy(PatientRecord patientRecord, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		//TODO replace this with prescribeIt lookup/save when available
		Pharmacy pharmacy = patientRecord.getPreferredPharmacy();
		if(pharmacy != null)
		{
			PharmacyInfo pharmacyInfo = pharmacyModelToDbConverter.convert(pharmacy);
			PharmacyInfo existingPharmacyInfo = pharmacyInfoDao.findMatchingPharmacy(pharmacyInfo);

			Integer pharmacyId;
			if(existingPharmacyInfo == null)
			{
				pharmacyInfoDao.persist(pharmacyInfo);
				pharmacyId = pharmacyInfo.getId();
			}
			else
			{
				pharmacyId = existingPharmacyInfo.getId();
			}

			DemographicPharmacy demographicPharmacy = new DemographicPharmacy();
			demographicPharmacy.setAddDate(ConversionUtils.toNullableLegacyDateTime(pharmacy.getCreatedDateTime()));
			demographicPharmacy.setDemographicNo(dbDemographic.getId());
			demographicPharmacy.setPharmacyId(pharmacyId);
			demographicPharmacy.setStatus(DemographicPharmacy.ACTIVE);
			demographicPharmacy.setPreferredOrder(1);
			demographicPharmacyDao.persist(demographicPharmacy);
		}
	}

	private org.oscarehr.demographic.model.Demographic findDuplicate(Demographic demographicModel)
	{
		String hin = demographicModel.getHealthNumber();
		if(hin != null)
		{
			DemographicCriteriaSearch searchQuery = new DemographicCriteriaSearch();
			searchQuery.setHin(hin);
			searchQuery.setDateOfBirth(demographicModel.getDateOfBirth());

			List<org.oscarehr.demographic.model.Demographic> possibleMatches = demographicDao.criteriaSearch(searchQuery);
			if(possibleMatches.size() == 1)
			{
				return possibleMatches.get(0);
			}
			else if(possibleMatches.size() > 1)
			{
				throw new RuntimeException("Multiple duplicate record found for hin: " + hin);
			}
		}
		return null;
	}

	private void writeAuditLogImportStatement(org.oscarehr.demographic.model.Demographic dbDemographic,
	                                          ImporterExporterFactory.IMPORTER_TYPE importType,
	                                          ImporterExporterFactory.IMPORT_SOURCE importSource,
	                                          boolean duplicateDetected)
	{
		String logAction;
		String logMessage = "[" + importType.toString() + "] import data (from source '" +importSource.toString() + "') ";
		if(duplicateDetected)
		{
			logMessage += "merged";
			logAction = LogConst.ACTION_UPDATE;
		}
		else
		{
			logMessage += "saved as new patient record";
			logAction = LogConst.ACTION_ADD;
		}
		LogAction.addLogEntry(LoggedInInfo.getLoggedInInfoAsCurrentClassAndMethod().getLoggedInProviderNo(),
				dbDemographic.getId(),
				logAction,
				LogConst.CON_DEMOGRAPHIC,
				LogConst.STATUS_SUCCESS,
				logMessage);
	}
}
