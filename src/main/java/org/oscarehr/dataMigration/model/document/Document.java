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
package org.oscarehr.dataMigration.model.document;

import lombok.Data;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.appointment.Appointment;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.dataMigration.model.provider.ProviderModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Document extends AbstractTransientModel
{
	public enum STATUS
	{
		ACTIVE,
		DELETED,
	}

	private Integer id;

	private String description;
	private String documentType;
	private String documentClass;
	private String documentSubClass;
	private LocalDate observationDate;

	private GenericFile file;

	private String source;
	private String sourceFacility;
	private LocalDateTime updatedAt;
	private STATUS status;
	private LocalDateTime createdAt;
	private Boolean publicDocument;
	private Appointment appointment;

	private ProviderModel createdBy;
	private ProviderModel responsible;
	private Reviewer reviewer;

	private String annotation;
	private Integer programId;

	private List<ResidualInfo> residualInfo;
}
