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

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.document.dao.CtlDocumentDao;
import org.oscarehr.document.dao.DocumentDao;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static oscar.util.StringUtils.filled;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class Document
{
	private static final OscarProperties props = OscarProperties.getInstance();

	@Autowired
	DocumentDao documentDao;

	@Autowired
	CtlDocumentDao ctlDocumentDao;

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
		//TODO - replace AddEditDocumentForm with transfer object

		// load existing models & retrieve file object
		org.oscarehr.document.model.Document documentModel = documentDao.find(documentNo);
		if(documentModel == null)
		{
			throw new IllegalArgumentException("No Document exists with documentId " + documentNo);
		}
		org.oscarehr.document.model.CtlDocument ctlDocumentModel = ctlDocumentDao.getCtrlDocument(documentNo);
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
		documentModel.setDocdesc(fm.getDocDesc());
		documentModel.setDoctype(fm.getDocType());
		documentModel.setDoccreator(fm.getDocCreator());
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
				tempFile = FileFactory.createTempFile(documentInputStream);
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
}
