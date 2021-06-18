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
package org.oscarehr.dataMigration.service;

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
import org.oscarehr.common.model.DemographicPharmacy;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.dataMigration.converter.in.PharmacyModelToDbConverter;
import org.oscarehr.dataMigration.converter.in.PreventionModelToDbConverter;
import org.oscarehr.dataMigration.converter.in.ReviewerModelToDbConverter;
import org.oscarehr.dataMigration.exception.DuplicateDemographicException;
import org.oscarehr.dataMigration.logger.ImportLogger;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.immunization.Immunization;
import org.oscarehr.dataMigration.model.lab.Lab;
import org.oscarehr.dataMigration.model.lab.LabObservation;
import org.oscarehr.dataMigration.model.lab.LabObservationResult;
import org.oscarehr.dataMigration.model.pharmacy.Pharmacy;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.service.DemographicContactService;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
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
import org.oscarehr.provider.service.ProviderService;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.oscarehr.provider.model.ProviderData.SYSTEM_PROVIDER_NO;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class PatientImportService
{
	private static final Logger logger = Logger.getLogger(PatientImportService.class);

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
	private PharmacyInfoDao pharmacyInfoDao;

	@Autowired
	private DemographicPharmacyDao demographicPharmacyDao;

	@Autowired
	private PharmacyModelToDbConverter pharmacyModelToDbConverter;

	@Autowired
	private HRMService hrmService;

	@Autowired
	private ProviderService providerService;

	public org.oscarehr.demographic.model.Demographic importDemographic(GenericFile importFile,
	                              PatientImportContext context,
	                              DemographicImporter.MERGE_STRATEGY mergeStrategy) throws Exception
	{
		Instant instant = Instant.now();
		DemographicImporter importer = context.getImporter();
		ImportLogger importLogger = context.getImportLogger();
		importer.verifyFileFormat(importFile);
		PatientRecord patientRecord = importer.importDemographic(importFile);

		Demographic demographicModel = patientRecord.getDemographic();
		org.oscarehr.demographic.model.Demographic dbDemographicDuplicate = findDuplicate(demographicModel);
		boolean duplicateDetected = (dbDemographicDuplicate != null);

		org.oscarehr.demographic.model.Demographic dbDemographic;
		try
		{
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
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist/load demographic");

			patientRecord.getDemographic().setId(dbDemographic.getId());
			demographicContactService.addNewContacts(patientRecord.getContactList(), dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist contacts");

			persistNotes(patientRecord, dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist notes");

			persistLabs(patientRecord, dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist labs");

			appointmentService.saveNewAppointments(patientRecord.getAppointmentList(), dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist appointments");
			appointmentStatusCache.clear();

			medicationService.saveNewMedications(patientRecord.getMedicationList(), dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist medications");

			persistMeasurements(patientRecord, dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist measurements");

			allergyService.saveNewAllergies(patientRecord.getAllergyList(), dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist allergies");

			persistPreventions(patientRecord, dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist preventions");

			persistPharmacy(patientRecord, dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: match/persist pharmacy");

			// persist documents last to minimize import errors with disk IO
			persistHrmDocuments(patientRecord, dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist hrm documents");

			documentService.uploadAllNewDemographicDocument(patientRecord.getDocumentList(), dbDemographic);
			instant = LogAction.printDuration(instant, "[" + dbDemographic.getId() + "] Import Service: persist document files");

			importLogger.logSummaryLine(patientRecord);
		}
		catch(Exception e)
		{
			// clean up all document files that will be orphaned by a rollback
			cleanTempFiles(patientRecord);
			throw e;
		}
		writeAuditLogImportStatement(dbDemographic, context.getImportType(), context.getImportPreferences().getImportSource(), duplicateDetected);
		return dbDemographic;
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
		for(org.oscarehr.dataMigration.model.measurement.Measurement measurement : patientRecord.getMeasurementList())
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
		for(Immunization immunization : patientRecord.getImmunizationList())
		{
			Prevention prevention = preventionModelToDbConverter.convert(immunization);
			prevention.setDemographicId(dbDemographic.getId());
			preventionDao.persist(prevention);

			for(PreventionExt ext : prevention.getPreventionExtensionList())
			{
				preventionExtDao.persist(ext);
			}

			Optional<CaseManagementNote> immunizationNoteOptional = encounterNoteService.buildBaseAnnotationNote(
					null, immunization.getResidualInfo());
			if(immunizationNoteOptional.isPresent())
			{
				CaseManagementNote immunizationNote = immunizationNoteOptional.get();
				ProviderData providerData = providerService.getProvider(prevention.getProviderNo());
				immunizationNote.setProvider(providerData);
				immunizationNote.setSigningProvider(providerData);
				immunizationNote.setDemographic(dbDemographic);
				immunizationNote.setObservationDate(prevention.getPreventionDate());
				encounterNoteService.savePreventionNote(immunizationNote, prevention);
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
			hrmDocument.setReportFile(exporter.exportDemographic(tempRecord));
			hrmDocument.setReportFileSchemaVersion(exporter.getSchemaVersion());
		}

		hrmService.uploadAllNewHRMDocuments(patientRecord.getHrmDocumentList(), dbDemographic);

		// clean up temp document files.
		// since HRM document files are embedded in the xml we write, we don't need the document anymore
		for(HrmDocument hrmDocument : patientRecord.getHrmDocumentList())
		{
			GenericFile docFile = hrmDocument.getDocument().getFile();

			// check that it's for sure in the temp directory still, just in case
			if(GenericFile.TEMP_DIRECTORY.equals(docFile.getDirectory()))
			{
				docFile.deleteFile();
			}
		}
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

	private void cleanTempFiles(PatientRecord patientRecord)
	{
		for(Document document : patientRecord.getDocumentList())
		{
			try
			{
				document.getFile().deleteFile();
			}
			catch(Exception e)
			{
				logger.error("error cleaning temp file", e);
			}
		}

		for(HrmDocument document : patientRecord.getHrmDocumentList())
		{
			try
			{
				document.getDocument().getFile().deleteFile();
			}
			catch(Exception e)
			{
				logger.error("error cleaning temp file", e);
			}
		}
	}
}
