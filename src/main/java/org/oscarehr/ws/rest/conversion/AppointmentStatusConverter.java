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
package org.oscarehr.ws.rest.conversion;

import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.AppointmentStatusTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


@Component
public class AppointmentStatusConverter extends AbstractConverter<AppointmentStatus, AppointmentStatusTo1> {

	@Override
	public AppointmentStatus getAsDomainObject(LoggedInInfo loggedInInfo,AppointmentStatusTo1 t) throws ConversionException {
		AppointmentStatus d = new AppointmentStatus();
		BeanUtils.copyProperties(t, d);
		d.setJunoColor(t.getColor());
		return d;
	}
	
	@Override
	public AppointmentStatusTo1 getAsTransferObject(LoggedInInfo loggedInInfo,AppointmentStatus d) throws ConversionException {
		AppointmentStatusTo1 t = new AppointmentStatusTo1();
		BeanUtils.copyProperties(d, t);
		t.setColor(d.getJunoColor());
		return t;
	}
}
	