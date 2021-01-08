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
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.hl7.copd.writer.JunoGenericImportLabWriter;
import org.oscarehr.common.hl7.writer.HL7LabWriter;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.demographicImport.converter.in.PreventionModelToDbConverter;
import org.oscarehr.demographicImport.converter.in.ReviewerModelToDbConverter;
import org.oscarehr.demographicImport.converter.out.BaseDbToModelConverter;
import org.oscarehr.demographicImport.converter.out.DemographicDbToModelConverter;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.encounterNote.EncounterNote;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.lab.LabObservation;
import org.oscarehr.demographicImport.model.lab.LabObservationResult;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.encounterNote.service.ConcernNoteService;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.encounterNote.service.FamilyHistoryNoteService;
import org.oscarehr.encounterNote.service.MedicalHistoryNoteService;
import org.oscarehr.encounterNote.service.ReminderNoteService;
import org.oscarehr.encounterNote.service.RiskFactorNoteService;
import org.oscarehr.encounterNote.service.SocialHistoryNoteService;
import org.oscarehr.labs.service.LabService;
import org.oscarehr.measurements.service.MeasurementsService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dao.PreventionExtDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.rx.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
	private DocumentService documentService;

	@Autowired
	private DemographicDbToModelConverter modelToExportConverter;

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

	public List<GenericFile> exportDemographics(ImporterExporterFactory.EXPORTER_TYPE importType,
	                                            ExportLogger exportLogger,
	                                            List<Demographic> demographicList,
	                                            ExportPreferences preferences) throws Exception
	{
		exportLogger.logSummaryHeaderLine();
		DemographicExporter exporter = importerExporterFactory.getExporter(importType, exportLogger, preferences);
		List<GenericFile> fileList = new ArrayList<>(demographicList.size() + 2);

		for (Demographic demographic : demographicList)
		{
			GenericFile file = exporter.exportDemographic(demographic);
			fileList.add(file);
		}
		fileList.addAll(exporter.getAdditionalFiles(preferences));
		BaseDbToModelConverter.clearProviderCache();
		appointmentStatusCache.clear();

		return fileList;
	}

	public List<GenericFile> exportDemographicsWithLookup(ImporterExporterFactory.EXPORTER_TYPE importType,
	                                                      ExportLogger exportLogger,
	                                                      List<String> demographicIdList,
	                                                      ExportPreferences preferences) throws Exception
	{
		//TODO batch query get demographics
		List<Demographic> demographicList = new ArrayList<>(demographicIdList.size());
		for(String demographicIdStr : demographicIdList)
		{
			Integer demographicId = Integer.parseInt(demographicIdStr);
			org.oscarehr.demographic.model.Demographic demographic = demographicDao.find(demographicId);
			Demographic exportDemographic = modelToExportConverter.convert(demographic);
			demographicList.add(exportDemographic);
		}

		return exportDemographics(importType, exportLogger, demographicList, preferences);
	}

	public void importDemographic(ImporterExporterFactory.IMPORTER_TYPE importType,
	                              ImporterExporterFactory.IMPORT_SOURCE importSource,
	                              ImportLogger importLogger,
	                              GenericFile importFile,
	                              String documentLocation,
	                              boolean skipMissingDocs,
	                              boolean mergeDemographics) throws Exception
	{
		DemographicImporter importer = importerExporterFactory.getImporter(importType, importSource, importLogger, documentLocation, skipMissingDocs);
		importer.verifyFileFormat(importFile);
		Demographic demographic = importer.importDemographic(importFile);

		// TODO handle demographic merging & duplicate check

		org.oscarehr.demographic.model.Demographic dbDemographic = demographicService.addNewDemographicRecord(SYSTEM_PROVIDER_NO, demographic);
		demographic.setId(dbDemographic.getId());
		logger.info("persisted new demographic: " + demographic.getId());

		persistNotes(demographic, dbDemographic);
		persistLabs(demographic, dbDemographic);

		appointmentService.saveNewAppointments(demographic.getAppointmentList(), dbDemographic);
		appointmentStatusCache.clear();

		medicationService.saveNewMedications(demographic.getMedicationList(), dbDemographic);
		persistMeasurements(demographic, dbDemographic);

		allergyService.saveNewAllergies(demographic.getAllergyList(), dbDemographic);

		persistPreventions(demographic, dbDemographic);

		documentService.uploadAllNewDemographicDocument(demographic.getDocumentList(), dbDemographic);
	}

	private void persistNotes(Demographic demographic, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		socialHistoryNoteService.saveSocialHistoryNote(demographic.getSocialHistoryNoteList(), dbDemographic);
		familyHistoryNoteService.saveFamilyHistoryNote(demographic.getFamilyHistoryNoteList(), dbDemographic);
		medicalHistoryNoteService.saveMedicalHistoryNotes(demographic.getMedicalHistoryNoteList(), dbDemographic);
		reminderNoteService.saveReminderNote(demographic.getReminderNoteList(), dbDemographic);
		riskFactorNoteService.saveRiskFactorNote(demographic.getRiskFactorNoteList(), dbDemographic);
		concernNoteService.saveConcernNote(demographic.getConcernNoteList(), dbDemographic);
		encounterNoteService.saveChartNotes(demographic.getEncounterNoteList(), dbDemographic);
	}

	private void persistMeasurements(Demographic demographic, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		for(org.oscarehr.demographicImport.model.measurement.Measurement measurement : demographic.getMeasurementList())
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

	private void persistPreventions(Demographic demographic, org.oscarehr.demographic.model.Demographic dbDemographic)
	{
		List<Prevention> preventions = preventionModelToDbConverter.convert(demographic.getImmunizationList());

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

	private void persistLabs(Demographic demographic, org.oscarehr.demographic.model.Demographic dbDemographic) throws HL7Exception, IOException
	{
		for(Lab lab : demographic.getLabList())
		{
			HL7LabWriter labWriter = new JunoGenericImportLabWriter(demographic, lab);
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
				// TODO find a better way to get indexes?
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
}
