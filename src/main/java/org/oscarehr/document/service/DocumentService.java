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

package org.oscarehr.document.service;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.CtlDocumentPK;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.dataMigration.converter.in.DocumentModelToDbConverter;
import org.oscarehr.document.dao.CtlDocumentDao;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.factory.DocumentFactory;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.Document;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.inbox.service.InboxManager;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.MyDateFormat;
import oscar.OscarProperties;
import oscar.dms.EDocUtil;
import oscar.dms.data.AddEditDocumentForm;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static oscar.util.StringUtils.filled;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DocumentService
{
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private CtlDocumentDao ctlDocumentDao;

	@Autowired
	private DocumentFactory documentFactory;

	@Autowired
	private PatientLabRoutingDao patientLabRoutingDao;

	@Autowired
	private ProgramManager programManager;

	@Autowired
	private DocumentModelToDbConverter documentModelToDbConverter;

	@Autowired
	private EncounterNoteService encounterNoteService;

	@Autowired
	private InboxManager inboxManagerService;


	/**
	 * Create a new document from the given document model and a file
	 * This method will move the file to the documents directory and persist the record
	 * NOTE: It is recommended to use the file input stream version wherever possible
	 * Document metadata fields that can be read from the file directly will replace the values in the model.
	 * @param document - the un-persisted document model
	 * @param file - an existing file that has no database record.
	 * @param demographicNo - demographic id of the attached demographic
	 * @return - the persisted document model
	 * @throws IOException
	 */
	public Document uploadNewDemographicDocument(Document document, GenericFile file, Integer demographicNo) throws IOException
	{
		// force this file to be treated as valid, as validation is only performed for new files
		file.forceSetValidation(true);

		// make sure we format the file the same as other uploads
		String formattedFileName = GenericFile.getFormattedFileName(file.getName());
		file.rename(formattedFileName);

		return uploadNewDemographicDocumentLogic(document, file, demographicNo);
	}

	public Document uploadNewDemographicDocument(org.oscarehr.dataMigration.model.document.Document documentModel, Demographic demographic) throws IOException
	{
		Document dbDocument = uploadNewDemographicDocument(documentModelToDbConverter.convert(documentModel), documentModel.getFile(), demographic.getId());

		Optional<CaseManagementNote> documentNoteOptional = encounterNoteService.buildBaseAnnotationNote(
				documentModel.getAnnotation(), documentModel.getResidualInfo());
		if(documentNoteOptional.isPresent())
		{
			ProviderData createdBy = dbDocument.getCreatedBy();
			CaseManagementNote documentNote = documentNoteOptional.get();
			documentNote.setProvider(createdBy);
			documentNote.setSigningProvider(createdBy);
			documentNote.setDemographic(demographic);
			documentNote.setObservationDate(dbDocument.getObservationdate());
			documentNote.setProgramNo(documentModel.getProgramId() != null ? String.valueOf(documentModel.getProgramId()) : null);
			encounterNoteService.saveDocumentNote(documentNote, dbDocument);
		}

		if(dbDocument.getReviewer() != null)
		{
			this.routeToProviderInbox(dbDocument.getDocumentNo(), false, true, dbDocument.getDoccreator(), dbDocument.getResponsible(), dbDocument.getReviewer());
		}
		else
		{
			this.routeToProviderInbox(dbDocument.getDocumentNo(), false, true, dbDocument.getDoccreator(), dbDocument.getResponsible());
		}

		return dbDocument;
	}

	public void uploadAllNewDemographicDocument(List<org.oscarehr.dataMigration.model.document.Document> documentModels, Demographic demographic) throws IOException
	{
		// load program ID outside of loop to prevent excess queries
		Integer defaultProgramId = programManager.getDefaultProgramId();

		for(org.oscarehr.dataMigration.model.document.Document documentModel : documentModels)
		{
			if(documentModel.getProgramId() == null)
			{
				documentModel.setProgramId(defaultProgramId);
			}
			uploadNewDemographicDocument(documentModel, demographic);
		}
	}

	/**
	 * Create a new document from the given document model and a file input stream.
	 * This method will write the file from the stream and persist the document record.
	 * Document metadata fields that can be read from the file directly will replace the values in the model.
	 * @param document - the un-persisted document model
	 * @param fileInputStream - input stream
	 * @param demographicNo - demographic id of the attached demographic
	 * @param markValid - mark the file as valid, allowing file type inference.
	 * @return - the persisted document model
	 * @throws IOException
	 */
	public Document uploadNewDemographicDocument(Document document, InputStream fileInputStream, Integer demographicNo, boolean markValid) throws IOException, InterruptedException
	{
		GenericFile file = FileFactory.createDocumentFile(fileInputStream, document.getDocfilename());
		if (markValid)
		{
			file.forceSetValidation(true);
		}
		return uploadNewDemographicDocumentLogic(document, file, demographicNo);
	}

	public Document uploadNewDemographicDocument(Document document, InputStream fileInputStream, Integer demographicNo) throws IOException, InterruptedException
	{
		return uploadNewDemographicDocument(document, fileInputStream, demographicNo, false);
	}

	public Document uploadNewDemographicDocument(Document document, InputStream fileInputStream) throws IOException, InterruptedException
	{
		return uploadNewDemographicDocument(document, fileInputStream, null);
	}

	/**
	 * upload (add) a new document to a demographics chart.
	 * @param loggedInInfo - the logged in info of the provider performing the upload
	 * @param document - the document to upload
	 * @param demographic - the demographic to attach this document to.
	 * @param base64Data - base64 data to be contained in the document.
	 * @return - the newly uploaded document
	 */
	public Document uploadNewDemographicDocument(
			LoggedInInfo loggedInInfo,
			Document document,
			Demographic demographic,
			String base64Data) throws IOException, InterruptedException
	{
		// set creator of document to user performing addition.
		document.setDocCreator(loggedInInfo.getLoggedInProviderNo());

		// decode doc data.
		InputStream inputStream;
		byte[] binaryData = Base64.getDecoder().decode(base64Data);
		inputStream = new ByteArrayInputStream(binaryData);

		// upload.
		Document newDocument = uploadNewDemographicDocument(document, inputStream, demographic.getId());

		// audit log
		LogAction.addLogEntry(
				loggedInInfo.getLoggedInProviderNo(),
				demographic.getId(),
				LogConst.ACTION_ADD,
				LogConst.CON_DOCUMENT,
				LogConst.STATUS_SUCCESS,
				String.valueOf(document.getDocumentNo()), loggedInInfo.getIp(), document.getDocfilename());

		return newDocument;
	}

	/**
	 * upload a new document to a demographic
	 * @param loggedInInfo - the loggedin user.
	 * @param demographicId - the demographic to assign the document to
	 * @param documentName - the document name
	 * @param base64Data - the document data base64 encoded.
	 * @return - the newly uploaded document
	 */
	public Document uploadNewDemographicDocument(LoggedInInfo loggedInInfo,
												 String demographicId,
												 String documentName,
												 String base64Data)
		throws IOException, InterruptedException
	{
		byte[] docContents = Base64.getDecoder().decode(base64Data);

		if(docContents.length == 0)
		{
			throw new IllegalArgumentException("document data is empty");
		}

		InputStream fileInputStream = new ByteArrayInputStream(docContents);
		final Document document = this.documentFactory.create(loggedInInfo, documentName);

		return this.uploadNewDemographicDocument(document, fileInputStream, Integer.parseInt(demographicId));
	}

	/**
	 * Create a new document from the given document model and a file
	 * This method will move the file to the documents directory and persist the record
	 * NOTE: It is recommended to use the file input stream version wherever possible
	 * Document metadata fields that can be read from the file directly will replace the values in the model.
	 * @param document - the un-persisted document model
	 * @param file - an existing file that has no database record.
	 * @param providerNo - demographic id of the attached demographic
	 * @return - the persisted document model
	 * @throws IOException
	 */
	public Document uploadNewProviderDocument(Document document, GenericFile file, Integer providerNo) throws IOException
	{
		// force this file to be treated as valid, as validation is only performed for new files
		file.forceSetValidation(true);
		return uploadNewProviderDocumentLogic(document,file,providerNo);
	}

	public Document uploadNewProviderDocument(Document document, InputStream fileInputStream, Integer providerNo) throws IOException, InterruptedException
	{
		GenericFile file = FileFactory.createDocumentFile(fileInputStream, document.getDocfilename());
		return uploadNewProviderDocumentLogic(document, file, providerNo);
	}

	private Document uploadNewDemographicDocumentLogic(Document document, GenericFile file, Integer demographicNo) throws IOException
	{
		file.moveToDocuments();

		document = setDocumentProperties(document, file);
		document = addDocumentModel(document);
		if(demographicNo == null || demographicNo < 1)
		{
			// unassigned documents still get a link with id -1
			createDemographicCtlLink(document, -1);
		}
		else
		{
			assignDocumentToDemographic(document, demographicNo);
		}
		logger.info("Uploaded Demographic Document " + document.getDocumentNo());
		return document;
	}

	private Document uploadNewProviderDocumentLogic(Document document, GenericFile file, Integer providerNo) throws IOException
	{
		file.moveToDocuments();

		document = setDocumentProperties(document, file);
		document = addDocumentModel(document);
		if(providerNo == null || providerNo < 1)
		{
			// unassigned documents still get a link with id -1
			createProviderCtlLink(document, -1);
		}
		else
		{
			createProviderCtlLink(document, providerNo);
		}
		logger.info("Uploaded Provider Document " + document.getDocumentNo());
		return document;
	}

	/**
	 * Persist a document record without an associated filestream.
	 * Use other methods for creating documents whenever there is an available filestream
	 * @param document - the un-persisted document model
	 * @return - The document model that was persisted
	 */
	private Document addDocumentModel(Document document)
	{
		// default some values if they are missing
		if(document.getObservationdate() == null)
		{
			document.setObservationdate(new Date());
		}
		if(document.getContentdatetime() == null)
		{
			document.setContentdatetime(new Date());
		}
		if(document.getProgramId() == null)
		{
			document.setProgramId(programManager.getDefaultProgramId());
		}
		if (document.getResponsible() == null)
		{
			// NULL is not allowed however empty string is fine.
			document.setResponsible("");
		}
		documentDao.persist(document);
		return document;
	}

	private Document setDocumentProperties(Document document, GenericFile file) throws IOException
	{
		document.setDocfilename(file.getName());
		document.setContenttype(file.getContentType());
		document.setNumberofpages(file.getPageCount());
		document.setEncodingError(file.hasBeenValidated() && !file.isValid());

		return document;
	}

	public void updateDocument(AddEditDocumentForm fm,
	                           InputStream documentInputStream,
	                           String documentFileName,
	                           Integer documentNo,
	                           Integer demographicNo,
	                           String providerNo,
	                           String userIP,
	                           Integer programId
	) throws IOException, InterruptedException
	{
		//TODO-legacy - replace AddEditDocumentForm with transfer object

		// load existing models & retrieve file object
		Document documentModel = documentDao.find(documentNo);
		if(documentModel == null)
		{
			throw new IllegalArgumentException("No Document exists with documentId " + documentNo);
		}
		CtlDocument ctlDocumentModel = ctlDocumentDao.getCtrlDocument(documentNo);
		if(ctlDocumentModel == null)
		{
			throw new IllegalStateException("No CtlDocument exists for documentId " + documentNo);
		}
		GenericFile file = FileFactory.getDocumentFile(documentModel.getDocfilename());

		boolean hasFileChanges = (documentFileName != null && !documentFileName.trim().isEmpty() && documentInputStream != null);
		boolean allowContentOverwrite = props.isPropertyActive("ALLOW_UPDATE_DOCUMENT_CONTENT");
		String formattedFileName = null;
		GenericFile tempFile = null;

		boolean isReview = fm.getReviewDoc();
		Date timestamp = new Date(); // still a legacy date
		boolean isPublicDoc = ("1".equals(fm.getDocPublic()) || "checked".equalsIgnoreCase(fm.getDocPublic()));

		// update the model info
		// intentionally skip updating doc creator
		documentModel.setDocdesc(fm.getDocDesc());
		documentModel.setDoctype(fm.getDocType());
		documentModel.setResponsible(fm.getResponsibleId());
		documentModel.setObservationdate(MyDateFormat.getSysDate(fm.getObservationDate()));
		documentModel.setSource(fm.getSource());
		documentModel.setSourceFacility(fm.getSourceFacility());
		documentModel.setPublic1(isPublicDoc);
		documentModel.setAppointmentNo(Integer.parseInt(fm.getAppointmentNo()));
		documentModel.setDocClass(fm.getDocClass());
		documentModel.setDocSubClass(fm.getDocSubClass());
		if (programId != null)
		{
			documentModel.setProgramId(programId);
		}

		if(hasFileChanges)
		{
			// replace an existing file with the new content
			if(allowContentOverwrite)
			{
				formattedFileName = GenericFile.getFormattedFileName(documentFileName);
				// get a tempfile. it will replace the existing doc at last step of the transaction
				tempFile = FileFactory.createTempFile(documentInputStream, "_tempdoc");
			}
			// save the content as new and update the references, but keep the previous document in the folder.
			else
			{
				tempFile = FileFactory.createDocumentFile(documentInputStream, documentFileName);
				formattedFileName = tempFile.getName();
				tempFile.moveToDocuments();
			}
			documentModel.setDocfilename(formattedFileName);
			documentModel.setContenttype(tempFile.getContentType());
			documentModel.setNumberofpages(tempFile.getPageCount());
			documentModel.setContentdatetime(timestamp);
		}

		if(isReview)
		{
			String reviewerId = filled(fm.getReviewerId()) ? fm.getReviewerId() : providerNo;
			String reviewDateTimeStr = filled(fm.getReviewDateTime()) ? fm.getReviewDateTime() : null;
			Date reviewDateTime = ConversionUtils.fromDateString(reviewDateTimeStr, EDocUtil.REVIEW_DATETIME_FORMAT);
			reviewDateTime = (reviewDateTime == null)? timestamp : reviewDateTime;

			documentModel.setReviewer(reviewerId);
			documentModel.setReviewdatetime(reviewDateTime);
			LogAction.addLogEntry(reviewerId, demographicNo, LogConst.ACTION_REVIEWED, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
					String.valueOf(documentNo), userIP, documentModel.getDocfilename());
		}
		else
		{
			// review info is removed if the document is modified.
			documentModel.setReviewer(null);
			documentModel.setReviewdatetime(null);
		}

		documentDao.merge(documentModel);

		LogAction.addLogEntry(providerNo, demographicNo, LogConst.ACTION_UPDATE, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
				String.valueOf(documentNo), userIP, documentModel.getDocfilename());

		// Important to do the file overwrite/rename after the database operations.
		// Transaction can roll back if file move fails, but file move can't roll back if db operation fails.
		if(hasFileChanges && allowContentOverwrite)
		{
			file.replaceWith(tempFile);
			file.rename(formattedFileName);
		}
	}


	/**
	 * Add the document to the given provider inbox
	 * @param documentNo - document id to route
	 * @param providerNoList - list of provider id(s) to route to
	 */
	public void routeToProviderInbox(Integer documentNo, String...providerNoList)
	{
		inboxManagerService.addDocumentToProviderInbox(documentNo, providerNoList);
	}
	/**
	 * Add the document to the given provider(s) inbox
	 * @param documentNo - document id to route
	 * @param alwaysFile - when true, all routes will be set as filed. otherwise default routing rules are applied
	 * @param providerNoList - list of provider id(s) to route to
	 */
	public void routeToProviderInbox(Integer documentNo, boolean applyForwardingRules, boolean alwaysFile, String...providerNoList)
	{
		inboxManagerService.addDocumentToProviderInbox(documentNo, applyForwardingRules, alwaysFile, providerNoList);
	}
	/**
	 * Add the document to the unclaimed/general inbox
	 * @param documentNo - document id to route
	 */
	public void routeToGeneralInbox(Integer documentNo)
	{
		routeToProviderInbox(documentNo, Provider.UNCLAIMED_PROVIDER_NO);
	}

	/**
	 * Assign the given document to a demographic record
	 * @param document - the document to assign
	 * @param demographicNo - the demographic id to assign to
	 */
	public void assignDocumentToDemographic(Document document, Integer demographicNo)
	{
		CtlDocument ctlDocument = ctlDocumentDao.getCtrlDocument(document.getDocumentNo());
		if(ctlDocument != null)
		{
			// since the demographic module id is a primary key, the old entry can't be updated by jpa. so we replace it instead.
			ctlDocumentDao.remove(ctlDocument);
		}
		createDemographicCtlLink(document, demographicNo);
		createDemographicRouteLink(document, demographicNo);
	}
	/**
	 * Assign the given document to a demographic record
	 * @param documentNo - the document id to assign
	 * @param demographicNo - the demographic id to assign to
	 */
	public void assignDocumentToDemographic(Integer documentNo, Integer demographicNo)
	{
		Document document = documentDao.find(documentNo);
		assignDocumentToDemographic(document, demographicNo);
	}

	private void createDemographicRouteLink(Document document, Integer demographicNo)
	{
		// Check to see if we have to route document to patient
		List<PatientLabRouting> patientLabRoutingList = patientLabRoutingDao.findByLabNoAndLabType(document.getDocumentNo(), PatientLabRoutingDao.DOC);
		if(patientLabRoutingList.isEmpty())
		{
			PatientLabRouting patientLabRouting = new PatientLabRouting();
			patientLabRouting.setDemographicNo(demographicNo);
			patientLabRouting.setLabNo(document.getDocumentNo());
			patientLabRouting.setLabType(PatientLabRoutingDao.DOC);
			patientLabRoutingDao.persist(patientLabRouting);
		}
		//TODO handle re-assigning a linked document?
	}

	private void createDemographicCtlLink(Document document, Integer demographicNo)
	{
		createCtlLink(document, demographicNo, CtlDocument.MODULE_DEMOGRAPHIC);
	}

	private void createProviderCtlLink(Document document, Integer providerNo)
	{
		createCtlLink(document, providerNo, CtlDocument.MODULE_PROVIDER);
	}

	private void createCtlLink(Document document, Integer moduleId, String moduleType)
	{
		CtlDocumentPK cdpk = new CtlDocumentPK();
		CtlDocument cd = new CtlDocument();
		cd.setId(cdpk);
		cdpk.setModule(moduleType);
		cdpk.setDocumentNo(document.getDocumentNo());
		cd.getId().setModuleId(moduleId);
		cd.setStatus(String.valueOf(document.getStatus()));
		ctlDocumentDao.persist(cd);
	}
}
