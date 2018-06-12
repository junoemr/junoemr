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
package org.oscarehr.ws.external.rest.v1.transfer.eform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotBlank;
import org.oscarehr.ws.validator.EFormTemplateIdConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Schema(description = "eForm data transfer object")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class EFormTransferBasic implements Serializable
{
	@NotNull
	@EFormTemplateIdConstraint
	@Schema(description = "eForm template identifier. Must match an existing eForm template")
	private Integer templateId;

	@NotBlank
	@Size(max=255)
	@Schema(description = "eForm subject/description")
	private String subject;

	public Integer getTemplateId()
	{
		return templateId;
	}

	public void setTemplateId(Integer templateId)
	{
		this.templateId = templateId;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}
}
