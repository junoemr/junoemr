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
package org.oscarehr.demographicImport.converter.out;

import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographicImport.model.common.PartialDateTime;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.oscarehr.document.model.Document;
import org.oscarehr.encounterNote.dao.CaseManagementNoteLinkDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.io.IOException;

@Component
public class DocumentDbToModelConverter extends
		BaseDbToModelConverter<Document, org.oscarehr.demographicImport.model.document.Document>
{
	@Autowired
	private OscarAppointmentDao appointmentDao;

	@Autowired
	private AppointmentDbToModelConverter appointmentConverter;

	@Autowired
	private CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Override
	public org.oscarehr.demographicImport.model.document.Document convert(Document input)
	{
		if(input == null)
		{
			return null;
		}
		org.oscarehr.demographicImport.model.document.Document exportDocument = new org.oscarehr.demographicImport.model.document.Document();

		exportDocument.setId(input.getId());
		exportDocument.setDocumentType(input.getDoctype());
		exportDocument.setCreatedAt(ConversionUtils.toLocalDateTime(input.getContentdatetime()));
		exportDocument.setDocumentClass(input.getDocClass());
		exportDocument.setDocumentSubClass(input.getDocSubClass());
		exportDocument.setDescription(input.getDocdesc());
		exportDocument.setPublicDocument(input.isPublic());
		exportDocument.setStatus(getDocumentStatus(input.getStatus()));
		exportDocument.setObservationDate(ConversionUtils.toNullableLocalDate(input.getObservationdate()));
		exportDocument.setUpdatedAt(ConversionUtils.toLocalDateTime(input.getUpdatedatetime()));
		exportDocument.setSource(input.getSource());
		exportDocument.setSourceFacility(input.getSourceFacility());

		exportDocument.setCreatedBy(findProvider(input.getDoccreator()));
		exportDocument.setResponsible(findProvider(input.getResponsible()));

		String reviewerId = input.getReviewer();
		if(reviewerId != null)
		{
			Reviewer reviewer = Reviewer.fromProvider(findProvider(reviewerId));
			reviewer.setReviewDateTime(PartialDateTime.from(ConversionUtils.toLocalDateTime(input.getReviewdatetime())));
			exportDocument.setReviewer(reviewer);
		}

		Integer appointmentId = input.getAppointmentNo();
		if(appointmentId != null && appointmentId > 0)
		{
			Appointment appointment = appointmentDao.find(appointmentId);
			exportDocument.setAppointment(appointmentConverter.convert(appointment));
		}

		try
		{
			exportDocument.setFile(FileFactory.getDocumentFile(input.getDocfilename()));
		}
		catch(IOException e)
		{
			//TODO handle missing documents?
			throw new RuntimeException("Missing Document File", e);
		}

		CaseManagementNoteLink noteLink = caseManagementNoteLinkDao.getNoteLinkByTableIdAndTableName(input.getId(), CaseManagementNoteLink.DOCUMENT);
		if(noteLink != null)
		{
			CaseManagementNote note = noteLink.getNote();

			// don't export system created notes
			if(!note.getProvider().getId().equals(ProviderData.SYSTEM_PROVIDER_NO))
			{
				exportDocument.setAnnotation(note.getNote());
			}
		}

		return exportDocument;
	}

	private org.oscarehr.demographicImport.model.document.Document.STATUS getDocumentStatus(char status)
	{
		switch(status)
		{
			case Document.STATUS_DELETED: return org.oscarehr.demographicImport.model.document.Document.STATUS.DELETED;
			default:
			case Document.STATUS_ACTIVE: return org.oscarehr.demographicImport.model.document.Document.STATUS.ACTIVE;
		}
	}
}
