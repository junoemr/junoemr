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

package org.oscarehr.integration.clinicaid.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientEligibilityDataTo1 implements Serializable
{
	private static final String CHECKED_STATUS_CHECKED = "checked";
	private static final String CHECKED_STATUS_UNKNOWN = "unknown";
	private static final String CHECKED_STATUS_PENDING = "pending";

	@JsonProperty("eligible_for_provincial_billing")
	private boolean eligible;

	@JsonProperty("eligibility_check_message")
	private String message;

	@JsonProperty("eligibility_queued_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date queuedAt;

	@JsonProperty("eligibility_checked_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date checkedAt;

	@JsonProperty("eligibility_expiry_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date expiryAt;

	@JsonProperty("check_status")
	private String checkStatus;

	public boolean isEligible()
	{
		return eligible;
	}

	public void setEligible(boolean eligible)
	{
		this.eligible = eligible;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Date getQueuedAt()
	{
		return queuedAt;
	}

	public void setQueuedAt(Date queuedAt)
	{
		this.queuedAt = queuedAt;
	}

	public Date getExpiryAt()
	{
		return expiryAt;
	}

	public void setExpiryAt(Date expiryAt)
	{
		this.expiryAt = expiryAt;
	}

	public Date getCheckedAt()
	{
		return checkedAt;
	}

	public String getCheckStatus()
	{
		return checkStatus;
	}

	public boolean isChecked()
	{
		return CHECKED_STATUS_CHECKED.equals(checkStatus);
	}
}
