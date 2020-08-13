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

package org.oscarehr.ws.rest.integrations.aqs.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalTime;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnDemandBookingSettingsTransfer implements Serializable
{
	private Boolean enabled;
	private OnDemandBookingHoursTransfer[] bookingHours;

	public OnDemandBookingSettingsTransfer()
	{
		// TODO - remove sample data
		this.enabled = true;
		this.bookingHours = new OnDemandBookingHoursTransfer[7];
		this.bookingHours[0] = new OnDemandBookingHoursTransfer("Monday", true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[1] = new OnDemandBookingHoursTransfer("Tuesday", true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[2] = new OnDemandBookingHoursTransfer("Wednesday", true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[3] = new OnDemandBookingHoursTransfer("Thursday", true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[4] = new OnDemandBookingHoursTransfer("Friday", true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[5] = new OnDemandBookingHoursTransfer("Saturday", false, LocalTime.of(8,0), LocalTime.of(14,0));
		this.bookingHours[6] = new OnDemandBookingHoursTransfer("Sunday", false, LocalTime.of(8,0), LocalTime.of(14,0));
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public OnDemandBookingHoursTransfer[] getBookingHours()
	{
		return bookingHours;
	}

	public void setBookingHours(OnDemandBookingHoursTransfer[] bookingHours)
	{
		this.bookingHours = bookingHours;
	}
}