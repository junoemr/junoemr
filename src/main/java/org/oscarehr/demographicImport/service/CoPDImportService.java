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

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.CustomModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import org.apache.log4j.Logger;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.allergy.service.AllergyService;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.TicklerDao;
import org.oscarehr.common.hl7.copd.mapper.*;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.copd.parser.CoPDParser;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.Tickler;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.labs.service.LabService;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.service.PreventionManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import org.oscarehr.provider.service.ProviderRoleService;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.rx.dao.DrugDao;
import org.oscarehr.rx.dao.PrescriptionDao;
import org.oscarehr.rx.model.Drug;
import org.oscarehr.rx.model.Prescription;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.all.parsers.other.JunoGenericLabHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CoPDImportService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties properties = OscarProperties.getInstance();

	private static final String IMPORT_PROVIDER = properties.getProperty("copd_import_service.system_provider_no", "999900");
	private static final String DEFAULT_PROVIDER_LAST_NAME = properties.getProperty("copd_import_service.default_provider.last_name", "CoPD-provider");
	private static final String DEFAULT_PROVIDER_FIRST_NAME = properties.getProperty("copd_import_service.default_provider.first_name", "CoPD-missing");

	public enum IMPORT_SOURCE
	{
		WOLF,
		MEDIPLAN,
		MEDACCESS,
		ACCURO,
		UNKNOWN
	}

	@Autowired
	DemographicService demographicService;

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	ProviderService providerService;

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	DocumentService documentService;

	@Autowired
	OscarAppointmentDao appointmentDao;

	@Autowired
	AllergyService allergyService;

	@Autowired
	EncounterNoteService encounterNoteService;

	@Autowired
	DxresearchDAO dxresearchDAO;

	@Autowired
	PreventionDao preventionDao;

	@Autowired
	PreventionManager preventionManager;

	@Autowired
	DrugDao drugDao;

	@Autowired
	PrescriptionDao prescriptionDao;

	@Autowired
	LabService labService;

	@Autowired
	TicklerDao ticklerDao;

	@Autowired
	MeasurementDao measurementDao;

	@Autowired
	ProviderRoleService providerRoleService;

	private static long missingDocumentCount = 0;

	public void importFromHl7Message(String message, String documentLocation, IMPORT_SOURCE importSource, boolean skipMissingDocs, boolean mergeDemographics) throws HL7Exception, IOException, InterruptedException
	{
		logger.info("Initialize HL7 parser");
		HapiContext context = new DefaultHapiContext();
		// default Obx2 types to string
		context.getParserConfiguration().setDefaultObx2Type("ST");
		// otherwise multiple spaces are trimmed to single spaces and document file names won't match
		context.getParserConfiguration().setXmlDisableWhitespaceTrimmingOnAllNodes(true);

		// this package string needs to match the custom model location in the oscar source code.
		ModelClassFactory modelClassFactory = new CustomModelClassFactory(ZPD_ZTR.ROOT_PACKAGE);
		context.setModelClassFactory(modelClassFactory);

		Parser p = new CoPDParser(context);
		logger.info("Parse Message");

		ZPD_ZTR zpdZtrMessage = (ZPD_ZTR) p.parse(message);

		missingDocumentCount = 0;
		importRecordData(zpdZtrMessage, documentLocation, importSource, skipMissingDocs, mergeDemographics);
	}
	public long getMissingDocumentCount()
	{
		return missingDocumentCount;
	}

	private void importRecordData(ZPD_ZTR zpdZtrMessage, String documentLocation, IMPORT_SOURCE importSource, boolean skipMissingDocs, boolean mergeDemographics)
			throws HL7Exception, IOException, InterruptedException
	{
		logger.info("Creating Demographic Record ...");
		Demographic demographic = importDemographicData(zpdZtrMessage, importSource, mergeDemographics);
		if (demographic != null)
		{
			logger.info("Created record " + demographic.getDemographicId() + " for patient: " + demographic.getLastName() + ", " + demographic.getFirstName());

			logger.info("Find/Create Provider Record(s) ...");
			ProviderData mrpProvider = importProviderData(zpdZtrMessage, demographic, documentLocation, importSource, skipMissingDocs);

			// set the mrp doctor after all the provider records are created
			demographic.setProviderNo(mrpProvider.getId());
			demographicDao.merge(demographic);

			logger.info("Create Appointments ...");
			importAppointmentData(zpdZtrMessage, demographic, mrpProvider, importSource);
		}
	}

	/**
	 * imports provider data for each provider group in the message
	 * @param zpdZtrMessage the hl7 message to parse
	 * @param demographic the new demographic record
	 * @return the MRP doctor record. This should never be null, as all messages are required to have at least one provider record
	 * @throws HL7Exception
	 */
	private ProviderData importProviderData(ZPD_ZTR zpdZtrMessage, Demographic demographic,
	                                        String documentLocation, IMPORT_SOURCE importSource, boolean skipMissingDocs)
			throws HL7Exception, IOException, InterruptedException
	{
		ProviderData mrpProvider = null;
		ProviderMapper providerMapper = MapperFactory.newProviderMapper(zpdZtrMessage);

		int numProviders = providerMapper.getNumProviders();
		logger.info("Found " + numProviders + " provider groups");
		if(numProviders < 1)
		{
			throw new RuntimeException("No provider information found");
		}

		//TODO how to determine MRP doctor when there are more than 1 (most vendors will send 1 apparently)
		for(int i=0; i< numProviders; i++)
		{
			ProviderData assignedProvider;
			mrpProvider = findOrCreateProviderRecord(providerMapper.getProvider(i));

			switch(importSource)
			{
				/*
				 * Wolf has stated that most of their information is not associated with a provider, and that the provider information in the
				 * PRD segment is not a reliable indicator of who created anything nested within the provider group.
				 * So we only use the provider information to indicate the MRP
				 */
				case WOLF:
				{
					assignedProvider = findOrCreateProviderRecord(getDefaultProvider()); break;
				}
				default:
				{
					assignedProvider = mrpProvider; break;
				}
			}

			logger.info("Import Notes & History ...");
			importProviderNotes(zpdZtrMessage, i, assignedProvider, demographic, importSource);
			logger.info("Import Alerts ...");
			importAlerts(zpdZtrMessage, i, assignedProvider, demographic, importSource);
			logger.info("Import diagnosed health problems ...");
			importDxData(zpdZtrMessage, i, assignedProvider, demographic);
			logger.info("Import Medications ...");
			importMedicationData(zpdZtrMessage, i, assignedProvider, demographic, importSource);
			logger.info("Import Pediatrics ...");
			importPediatricsData(zpdZtrMessage, i, assignedProvider, demographic);
			logger.info("Import Pregnancy ...");
			importPregnancyData(zpdZtrMessage, i, assignedProvider, demographic);
			logger.info("Import Allergies ...");
			importAllergyData(zpdZtrMessage, i, assignedProvider, demographic, importSource);
			logger.info("Import Immunizations ...");
			importPreventionData(zpdZtrMessage, i, assignedProvider, demographic);
			logger.info("Import Labs ...");
			importLabData(zpdZtrMessage, i, assignedProvider, demographic, importSource);
			logger.info("Import Documents ...");
			importDocumentData(zpdZtrMessage, i, assignedProvider, demographic, documentLocation, importSource, skipMissingDocs);
			logger.info("Import Ticklers ...");
			importTicklers(zpdZtrMessage, i, assignedProvider, demographic, importSource);
			logger.info("Importing Measurements ...");
			importMeasurements(zpdZtrMessage, demographic, i, assignedProvider, importSource);
		}

		return mrpProvider;
	}

	private ProviderData getDefaultProvider()
	{
		ProviderData defaultProvider = new ProviderData();
		defaultProvider.setLastName(DEFAULT_PROVIDER_LAST_NAME);
		defaultProvider.setFirstName(DEFAULT_PROVIDER_FIRST_NAME);
		return defaultProvider;
	}

	private ProviderData findOrCreateProviderRecord(ProviderData newProvider)
	{
		// CoPD spec does not require the provider PRD to have a name. in this case, assign a default
		if(newProvider.getFirstName() == null || newProvider.getLastName() == null)
		{
			logger.warn("Not enough provider info found to link or create provider record (first and last name are required). \n" +
					"Default provider (" + DEFAULT_PROVIDER_LAST_NAME + "," + DEFAULT_PROVIDER_FIRST_NAME + ") will be assigned.");
			newProvider = getDefaultProvider();
		}

		ProviderCriteriaSearch criteriaSearch = new ProviderCriteriaSearch();
		criteriaSearch.setFirstName(newProvider.getFirstName());
		criteriaSearch.setLastName(newProvider.getLastName());

		ProviderData provider;
		List<ProviderData> matchedProviders = providerDataDao.criteriaSearch(criteriaSearch);
		if(matchedProviders.isEmpty())
		{
			provider = newProvider;
			// providers don't have auto-generated id's, so we have to pick one
			Integer newProviderId = providerService.getNextProviderNumberInSequence(9999, 900000);
			newProviderId = (newProviderId == null) ? 10000 : newProviderId;
			provider.set(String.valueOf(newProviderId));

			String billCenterCode = properties.getProperty("default_bill_center","");
			provider = providerService.addNewProvider(IMPORT_PROVIDER, provider, billCenterCode);
			providerRoleService.setPrimaryRole(provider.getProviderNo(), "doctor");

			logger.info("Created new Provider record " + provider.getId() + " (" + provider.getLastName() + "," + provider.getFirstName() + ")");
		}
		else if(matchedProviders.size() == 1)
		{
			provider = matchedProviders.get(0);
			logger.info("Use existing Provider record " + provider.getId() + " (" + provider.getLastName() + "," + provider.getFirstName() + ")");
		}
		else
		{
			throw new RuntimeException("Multiple providers exist in the system with the same name (" + newProvider.getLastName() + "," + newProvider.getFirstName() + ").");
		}
		return provider;
	}

	private Demographic importDemographicData(ZPD_ZTR zpdZtrMessage, IMPORT_SOURCE importSource, boolean mergeDemographics) throws HL7Exception
	{
		DemographicMapper demographicMapper = MapperFactory.newDemographicMapper(zpdZtrMessage, importSource);
		Demographic demographic = demographicMapper.getDemographic();

		// Optional flag you can run the importer with.
		if (mergeDemographics && demographic.getHin() != null && !demographic.getHin().trim().isEmpty())
		{
			DemographicCriteriaSearch searchQuery = new DemographicCriteriaSearch();
			searchQuery.setHin(demographic.getHin());
			searchQuery.setDateOfBirth(demographic.getDateOfBirth());

			List<Demographic> possibleMatches = demographicDao.criteriaSearch(searchQuery);
			if (possibleMatches.size() == 1)
			{
				demographic = possibleMatches.get(0);
				LogAction.addLogEntry(LoggedInInfo.getLoggedInInfoAsCurrentClassAndMethod().getLoggedInProviderNo(),
						demographic.getId(),
						LogConst.ACTION_ADD,
						LogConst.CON_DEMOGRAPHIC,
						LogConst.STATUS_SUCCESS,
						"Merged patient information from " + importSource.toString());
				logger.warn("Merging into demographic record " + demographic.getId() + " with HIN: " + demographic.getHin());
				return demographic;
			}
		}

		if (demographic != null)
		{
			DemographicCust demographicCust = demographicMapper.getDemographicCust();
			List<DemographicExt> demographicExtList = demographicMapper.getDemographicExtensions();

			demographicService.addNewDemographicRecord(IMPORT_PROVIDER, demographic, demographicCust, demographicExtList);
		}
		return demographic;
	}

	private void importMeasurements(ZPD_ZTR zpdZtrMessage, Demographic demographic, int provderRep, ProviderData assignedProvider, IMPORT_SOURCE importSource) throws HL7Exception
	{
		MeasurementsMapper measurementsMapper = MapperFactory.newMeasurementsMapper(zpdZtrMessage, provderRep, importSource);

		List<Measurement> measurements = measurementsMapper.getMeasurementList(demographic, assignedProvider);
		for (Measurement measurement : measurements)
		{
			logger.info("Saving measurement of type: " + measurement.getType() + " value: " + measurement.getDataField() + " to demographic: " + demographic.getDemographicId());
			measurementDao.persist(measurement);
		}
	}

	private void importAppointmentData(ZPD_ZTR zpdZtrMessage, Demographic demographic, ProviderData defaultProvider, IMPORT_SOURCE importSource) throws HL7Exception
	{
		if(properties.isPropertyActive("multisites"))
		{
			//TODO how to handle multisite assignment
			throw new RuntimeException("Multisite Imports not supported");
		}

		AppointmentMapper appointmentMapper = MapperFactory.newAppointmentMapper(zpdZtrMessage, importSource);

		int numAppointments = appointmentMapper.getNumAppointments();

		for(int i=0; i<numAppointments; i++)
		{
			Appointment appointment = appointmentMapper.getAppointment(i);
			ProviderData apptProvider = appointmentMapper.getAppointmentProvider(i);
			ProviderData assignedProvider = defaultProvider;
			if(apptProvider != null)
			{
				assignedProvider = findOrCreateProviderRecord(apptProvider);
			}
			appointment.setDemographicNo(demographic.getDemographicId());
			appointment.setName(demographic.getLastName() + "," + demographic.getFirstName());
			appointment.setCreator(IMPORT_PROVIDER);
			appointment.setProviderNo(String.valueOf(assignedProvider.getProviderNo()));

			logger.info("Add appointment: " + appointment.getAppointmentDate());
			appointmentDao.persist(appointment);
		}
	}

	private void importMedicationData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic, IMPORT_SOURCE importSource)
			throws HL7Exception
	{
		MedicationMapper medicationMapper = MapperFactory.newMedicationMapper(zpdZtrMessage, providerRep, importSource);

		int numMedications = medicationMapper.getNumMedications();

		for(int i=0; i<numMedications; i++)
		{
			Drug drug = medicationMapper.getDrug(i);
			Prescription prescription = medicationMapper.getPrescription(i);
			CaseManagementNote note = medicationMapper.getDrugNote(i);

			prescription.setDemographicId(demographic.getDemographicId());
			prescription.setProviderNo(String.valueOf(provider.getProviderNo()));
			prescriptionDao.persist(prescription);

			drug.setDemographicId(demographic.getDemographicId());
			drug.setProviderNo(String.valueOf(provider.getProviderNo()));
			drug.setScriptNo(prescription.getId());

			if (!medicationMapper.isDrugMostRecent(i) && importSource != IMPORT_SOURCE.WOLF)
			{
				drug.setArchived(true);
				drug.setArchivedReason("represcribed");
			}


			drugDao.persist(drug);

			if(note != null)
			{
				note.setProvider(provider);
				note.setSigningProvider(provider);
				note.setDemographic(demographic);
				encounterNoteService.saveDrugNote(note, drug);
			}
		}
	}

	private void importDxData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic) throws HL7Exception
	{
		DxMapper dxMapper = MapperFactory.newDxMapper(zpdZtrMessage, providerRep);

		for(Dxresearch dx : dxMapper.getDxResearchList())
		{
			dx.setDemographicNo(demographic.getDemographicId());
			dx.setProviderNo(String.valueOf(provider.getProviderNo()));

			if (!dxresearchDAO.entryExists(dx.getDemographicNo(), dx.getCodingSystem(), dx.getDxresearchCode()))
			{
				this.dxresearchDAO.save(dx);
			}
		}
	}

	private void importPediatricsData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
		//TODO - not implemented
	}
	private void importPregnancyData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic)
	{
		//TODO - not implemented
	}

	private void importAllergyData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception
	{
		AllergyMapper allergyMapper = MapperFactory.newAllergyMapper(zpdZtrMessage, providerRep, importSource);

		for (int rep = 0; rep < allergyMapper.getNumAllergies(); rep++)
		{
			Allergy allergy = allergyMapper.getAllergy(rep);
			CaseManagementNote allergyNote = allergyMapper.getAllergyNote(rep);

			allergy.setDemographicNo(demographic.getDemographicId());
			allergy.setProviderNo(String.valueOf(provider.getProviderNo()));
			allergyService.addNewAllergy(allergy);

			if (allergyNote != null)
			{
				allergyNote.setProvider(provider);
				allergyNote.setSigningProvider(provider);
				allergyNote.setDemographic(demographic);
				encounterNoteService.saveAllergyNote(allergyNote, allergy);
			}
		}
	}

	private void importPreventionData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic) throws HL7Exception
	{
		PreventionMapper preventionMapper = MapperFactory.newPreventionMapper(zpdZtrMessage, providerRep);
		preventionMapper.setValidPreventionTypes(preventionManager.getPreventionTypeList());

		for(Prevention prevention : preventionMapper.getPreventionList())
		{
			prevention.setDemographicId(demographic.getDemographicId());
			prevention.setProviderNo(String.valueOf(provider.getProviderNo()));
			prevention.setProviderName(provider.getFirstName() + " " + provider.getLastName());
			prevention.setCreatorProviderNo(String.valueOf(provider.getProviderNo()));

			preventionDao.persist(prevention);
		}
	}

	private void importLabData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception, IOException
	{
		LabMapper labMapper = MapperFactory.newLabMapper(zpdZtrMessage, providerRep, importSource);

		for(String msg : labMapper.getLabList())
		{
			MessageHandler parser = Factory.getHandler(JunoGenericLabHandler.LAB_TYPE_VALUE, msg);
			// just in case
			if(parser == null)
				throw new RuntimeException("No Parser available for lab");

			// allow each lab type to make modifications to the hl7 if needed.
			// This is for special cases only most labs return an identical string to the input parameter
			msg = parser.preUpload(msg);

			// check if the lab has passed validation and can be saved
			if(parser.canUpload())
			{
				try
				{
					ArrayList<ProviderData> routeProviders = new ArrayList<>(1);
					routeProviders.add(provider);

					labService.persistNewHL7Lab(parser, msg, "CoPD-Import", 0, demographic, routeProviders, ProviderInboxItem.FILE);
					parser.postUpload();
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			else
			{
				logger.warn("Hl7 Report Could Not be Uploaded");
			}
		}
	}

	private void importDocumentData(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic,
	                                String documentLocation, IMPORT_SOURCE importSource, boolean skipMissingDocs)
			throws IOException, InterruptedException
	{
		DocumentMapper documentMapper = MapperFactory.newDocumentMapper(zpdZtrMessage, providerRep, importSource);

		for(Document document : documentMapper.getDocumentList())
		{
			document.setDocCreator(provider.getId());
			document.setResponsible(provider.getId());

			GenericFile documentFile = null;
			try
			{
				documentFile = FileFactory.getExistingFile(documentLocation, document.getDocfilename());
			}
			catch(IOException e)
			{
				missingDocumentCount++;
				if(skipMissingDocs)
				{
					logger.error("Skipped missing document: " + document.getDocfilename());
					continue;
				}
				else
				{
					throw e;
				}
			}

			if(importSource.equals(IMPORT_SOURCE.WOLF) && documentFile instanceof XMLFile)
			{
				/* Wolf has instructed us not to import the xml files they include.
				 * The content of their internal wolf referral docs are also included as regular documents in the data.
				 * Not sure why they include the xml in the export, but we don't want/need them */
				continue;
			}
			InputStream stream = new FileInputStream(documentFile.getFileObject());
			try
			{
				documentService.uploadNewDemographicDocument(document, stream, demographic.getDemographicId(), false);
			}
			catch (FileAlreadyExistsException e)
			{
				logger.warn("SKIPPING: File: " + document.getDocfilename() + " already exists in document directory! skipping.");
				continue;
			}
			documentService.routeToProviderInbox(document.getDocumentNo(), true, provider.getId());
		}
	}

	private void importAlerts(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic, IMPORT_SOURCE importSource) throws HL7Exception
	{
		AlertMapper alertMapper = MapperFactory.newAlertMapper(zpdZtrMessage, providerRep, importSource);
		for(CaseManagementNote reminderNote : alertMapper.getReminderNoteList())
		{
			reminderNote.setProvider(provider);
			reminderNote.setSigningProvider(provider);
			reminderNote.setDemographic(demographic);
			encounterNoteService.saveReminderNote(reminderNote);
		}
	}

	private void importTicklers(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic, IMPORT_SOURCE importSource) throws HL7Exception
	{
		TicklerMapper ticklerMapper = MapperFactory.newTicklerMapper(zpdZtrMessage, providerRep, importSource);

		int numTicklers = ticklerMapper.getNumTicklers();
		for(int i=0; i< numTicklers; i++)
		{
			Tickler tickler = ticklerMapper.getTickler(i);
			if(tickler != null)
			{
				ProviderData assignedProvider = ticklerMapper.getAttendingProvider(i);
				assignedProvider = findOrCreateProviderRecord(assignedProvider);

				tickler.setDemographicNo(demographic.getDemographicId());
				tickler.setCreator(provider.getId());
				tickler.setTaskAssignedTo(assignedProvider.getId());
				ticklerDao.persist(tickler);
			}
		}
	}

	private void importProviderNotes(ZPD_ZTR zpdZtrMessage, int providerRep, ProviderData provider, Demographic demographic, IMPORT_SOURCE importSource) throws HL7Exception
	{
		EncounterNoteMapper encounterNoteMapper = MapperFactory.newEncounterNoteMapper(zpdZtrMessage, providerRep, importSource);

		int numNotes = encounterNoteMapper.getNumEncounterNotes();
		for(int i=0; i< numNotes; i++)
		{
			CaseManagementNote encounterNote = encounterNoteMapper.getEncounterNote(i);
			ProviderData signingProvider = encounterNoteMapper.getSigningProvider(i);
			ProviderData noteProvider = provider;
			if(signingProvider == null)
			{
				signingProvider = provider;
			}
			else
			{
				signingProvider = findOrCreateProviderRecord(signingProvider);
			}
			/* Wolf wants us to use the parsed provider name in the notes as the note creator and signature */
			if(importSource.equals(IMPORT_SOURCE.WOLF))
			{
				noteProvider = signingProvider;
			}
			encounterNote.setProvider(noteProvider);
			encounterNote.setSigned(true);
			encounterNote.setSigningProvider(signingProvider);
			encounterNote.setDemographic(demographic);
			encounterNoteService.saveChartNote(encounterNote);
		}

		HistoryNoteMapper historyNoteMapper = MapperFactory.newHistoryNoteMapper(zpdZtrMessage, providerRep, importSource);
		for(CaseManagementNote medHistNote : historyNoteMapper.getMedicalHistoryNoteList())
		{
			medHistNote.setProvider(provider);
			medHistNote.setSigningProvider(provider);
			medHistNote.setDemographic(demographic);
			encounterNoteService.saveMedicalHistoryNote(medHistNote);
		}
		for(CaseManagementNote socHistNote : historyNoteMapper.getSocialHistoryNoteList())
		{
			socHistNote.setProvider(provider);
			socHistNote.setSigningProvider(provider);
			socHistNote.setDemographic(demographic);
			encounterNoteService.saveSocialHistoryNote(socHistNote);
		}
		for(CaseManagementNote famHistNote : historyNoteMapper.getFamilyHistoryNoteList())
		{
			famHistNote.setProvider(provider);
			famHistNote.setSigningProvider(provider);
			famHistNote.setDemographic(demographic);
			encounterNoteService.saveFamilyHistoryNote(famHistNote);
		}
	}
}
