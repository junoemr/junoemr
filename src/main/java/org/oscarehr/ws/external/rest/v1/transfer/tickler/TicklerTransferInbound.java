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

@XmlRootElement
@Schema(description = "Tickler data transfer object, inbound")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class TicklerTransferInbound extends TicklerTransferBase
{
	public TicklerTransferInbound() {}

	public TicklerTransferInbound(Tickler tickler)
	{
		BeanUtils.copyProperties(tickler, this);
	}

	public Tickler copyToTickler(Tickler t)
	{
		BeanUtils.copyProperties(this, t, getNullPropertyNames(this));
		//dates will not copy properly, must do manual copy
		if (this.getServiceDateTime() != null)
		{
			t.setServiceDate(ConversionUtils.toLegacyDateTime(this.getServiceDateTime()));
		}

		return t;
	}
}
