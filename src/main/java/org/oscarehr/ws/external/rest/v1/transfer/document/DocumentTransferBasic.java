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
package org.oscarehr.ws.external.rest.v1.transfer.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.ws.validator.DocumentClassConstraint;
import org.oscarehr.ws.validator.DocumentTypeConstraint;
import org.oscarehr.ws.validator.ProviderNoConstraint;
import org.oscarehr.ws.validator.StringValueConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@XmlRootElement
@Schema(description = "Document data transfer object")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class DocumentTransferBasic implements Serializable
{
	// important document info
	@Schema(description = "document record identifier")
	private Integer documentNo;

	@Schema(description = "Document file name")
	@Getter
	@Setter
	private String fileName;

	@NotNull
	@StringValueConstraint(allows = {"A","D"})
	@Schema(description = "document status", allowableValues = {"A","D"}, example = "A")
	private String status;

	@Schema(description = "document date created timestamp")
	private LocalDateTime createdDateTime;

	@Schema(description = "document date")
	private LocalDate observationDate;

	@Schema(description = "true if this is a public document, false otherwise")
	private Boolean publicDocument = false;

	@NotNull
	@Schema(description = "base64 encoded document")
	private String base64EncodedFile;

	// document additional info
	@Size(max=60)
	@DocumentTypeConstraint
	@Schema(description = "document type")
	private String documentType;

	@Size(max=60)
	@DocumentClassConstraint
	@Schema(description = "document class")
	private String documentClass;

	@Size(max=60)
	@Schema(description = "document sub-class")
	private String documentSubClass;

	@NotNull
	@Size(max=60)
	@Schema(description = "document description")
	private String documentDescription;

	@Schema(description = "document xml content")
	private String documentXml;

	@Size(max=60)
	@Schema(description = "document source")
	private String source;

	@Size(max=255)
	@Schema(description = "document source facility")
	private String sourceFacility;

	@NotNull
	@Size(max=6)
	@ProviderNoConstraint
	@Schema(description = "document creator unique identifier. Id must match an existing provider record.")
	private String documentCreator;

	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "document responsible provider unique identifier. Id must match an existing provider record.")
	private String responsible;

	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "document reviewer unique identifier. Id must match an existing provider record.")
	private String reviewer;
	@Schema(description = "document review timestamp")
	private LocalDateTime reviewDateTime;

	@Schema(description = "the associated appointment unique identifier")
	private Integer appointmentNo;

	public Integer getDocumentNo()
	{
		return documentNo;
	}

	public void setDocumentNo(Integer documentNo)
	{
		this.documentNo = documentNo;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public LocalDateTime getCreatedDateTime()
	{
		return createdDateTime;
	}

	public void setCreatedDateTime(LocalDateTime createdDateTime)
	{
		this.createdDateTime = createdDateTime;
	}

	public LocalDate getObservationDate()
	{
		return observationDate;
	}

	public void setObservationDate(LocalDate observationDate)
	{
		this.observationDate = observationDate;
	}

	public String getBase64EncodedFile()
	{
		return base64EncodedFile;
	}

	public void setBase64EncodedFile(String base64EncodedFile)
	{
		this.base64EncodedFile = base64EncodedFile;
	}

	public String getDocumentType()
	{
		return documentType;
	}

	public void setDocumentType(String documentType)
	{
		this.documentType = documentType;
	}

	public String getDocumentClass()
	{
		return documentClass;
	}

	public void setDocumentClass(String documentClass)
	{
		this.documentClass = documentClass;
	}

	public String getDocumentSubClass()
	{
		return documentSubClass;
	}

	public void setDocumentSubClass(String documentSubClass)
	{
		this.documentSubClass = documentSubClass;
	}

	public String getDocumentDescription()
	{
		return documentDescription;
	}

	public void setDocumentDescription(String documentDescription)
	{
		this.documentDescription = documentDescription;
	}

	public String getDocumentXml()
	{
		return documentXml;
	}

	public void setDocumentXml(String documentXml)
	{
		this.documentXml = documentXml;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getSourceFacility()
	{
		return sourceFacility;
	}

	public void setSourceFacility(String sourceFacility)
	{
		this.sourceFacility = sourceFacility;
	}

	public String getDocumentCreator()
	{
		return documentCreator;
	}

	public void setDocumentCreator(String documentCreator)
	{
		this.documentCreator = documentCreator;
	}

	public String getResponsible()
	{
		return responsible;
	}

	public void setResponsible(String responsible)
	{
		this.responsible = responsible;
	}

	public Boolean getPublicDocument()
	{
		return publicDocument;
	}

	public void setPublicDocument(Boolean publicDocument)
	{
		this.publicDocument = publicDocument;
	}

	public String getReviewer()
	{
		return reviewer;
	}

	public void setReviewer(String reviewer)
	{
		this.reviewer = reviewer;
	}

	public LocalDateTime getReviewDateTime()
	{
		return reviewDateTime;
	}

	public void setReviewDateTime(LocalDateTime reviewDateTime)
	{
		this.reviewDateTime = reviewDateTime;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}
}
