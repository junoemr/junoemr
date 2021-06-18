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
package org.oscarehr.dataMigration.converter.in;

import org.apache.commons.lang.BooleanUtils;
import org.oscarehr.document.model.Document;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.document.model.Document.STATUS_ACTIVE;
import static org.oscarehr.document.model.Document.STATUS_DELETED;

@Component
public class DocumentModelToDbConverter extends BaseModelToDbConverter<org.oscarehr.dataMigration.model.document.Document, Document>
{
	@Override
	public Document convert(org.oscarehr.dataMigration.model.document.Document input)
	{
		Document document = new Document();

		document.setDocumentNo(input.getId());
		document.setDoctype(input.getDocumentType());
		document.setContentdatetime(ConversionUtils.toNullableLegacyDateTime(input.getCreatedAt()));
		document.setDocClass(input.getDocumentClass());
		document.setDocSubClass(input.getDocumentSubClass());
		document.setDocdesc(input.getDescription());
		document.setPublic1(BooleanUtils.toBooleanDefaultIfNull(input.getPublicDocument(), false));
		document.setStatus(getDocumentStatus(input.getStatus()));
		document.setObservationdate(ConversionUtils.toNullableLegacyDate(input.getObservationDate()));
		document.setUpdatedatetime(ConversionUtils.toNullableLegacyDateTime(input.getUpdatedAt()));
		document.setSource(input.getSource());
		document.setSourceFacility(input.getSourceFacility());
		document.setProgramId(input.getProgramId());

		document.setCreatedBy(findOrCreateProviderRecord(input.getCreatedBy(), false));
		document.setResponsible(findOrCreateProviderRecord(input.getResponsible(), false).getId());

		ProviderData reviewer = findOrCreateProviderRecord(input.getReviewer(), true);
		if(reviewer != null)
		{
			document.setReviewer(reviewer.getId());
			document.setReviewdatetime(ConversionUtils.toNullableLegacyDateTime(input.getReviewer().getReviewDateTime()));
		}

		document.setDocfilename(input.getFile().getName());

		Integer appointmentNo = input.getAppointment() != null ? input.getAppointment().getId() : 0;
		document.setAppointmentNo(appointmentNo);

		return document;
	}

	private char getDocumentStatus(org.oscarehr.dataMigration.model.document.Document.STATUS status)
	{
		char statusVal = STATUS_ACTIVE;
		if(status != null)
		{
			switch(status)
			{
				case DELETED: statusVal = STATUS_DELETED; break;
				default:
				case ACTIVE: statusVal = STATUS_ACTIVE; break;
			}
		}
		return statusVal;
	}
}
