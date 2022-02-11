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
package org.oscarehr.dataMigration.model.measurement;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.appointment.Appointment;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;

@Data
public abstract class Measurement extends AbstractTransientModel
{
	public static final String VALUE_YES = "yes";
	public static final String VALUE_NO = "no";
	public static final String DEFAULT_COMMENT = "";

	protected Integer id;
	protected String measurementValue;
	protected String measuringInstruction;
	protected String comments;
	protected LocalDateTime observationDateTime;
	protected LocalDateTime createdDateTime;

	protected ProviderModel provider;
	protected Appointment appointment;

	public Measurement()
	{
	}

	public Measurement(org.oscarehr.common.model.Measurement dbModel)
	{
		this.id = dbModel.getId();
		this.measurementValue = dbModel.getDataField();
		this.measuringInstruction = dbModel.getMeasuringInstruction();
		this.comments = dbModel.getComments();
		this.observationDateTime = ConversionUtils.toNullableLocalDateTime(dbModel.getDateObserved());
		this.createdDateTime = ConversionUtils.toLocalDateTime(dbModel.getCreateDate());
	}

	public abstract String getTypeCode();
}
