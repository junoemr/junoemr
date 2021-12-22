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
package org.oscarehr.ws.external.rest.v1.transfer.tickler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.oscarehr.ticklers.entity.Tickler;
import org.springframework.beans.BeanUtils;
import oscar.util.ConversionUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement
@Schema(description = "Tickler data transfer object, outbound")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class TicklerTransferOutbound extends TicklerTransferBase
{
	@Schema(description = "The id of the tickler")
	private Integer id;

	@Schema(description = "The last update date of the tickler")
	private LocalDateTime updateDateTime;

	public TicklerTransferOutbound() {}

	public TicklerTransferOutbound(Tickler tickler)
	{
		String [] ignore = {"updateDate", "serviceDate"};
		BeanUtils.copyProperties(tickler, this, ignore);
		//dates do not copy properly, do manually
		setServiceDateTime(ConversionUtils.toLocalDateTime(tickler.getServiceDate()));
		setUpdateDateTime(ConversionUtils.toLocalDateTime(tickler.getUpdateDate()));
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public LocalDateTime getUpdateDateTime()
	{
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime)
	{
		this.updateDateTime = updateDateTime;
	}
}
