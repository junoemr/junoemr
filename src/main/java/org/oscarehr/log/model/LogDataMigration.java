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

package org.oscarehr.log.model;

import lombok.Data;
import org.json.JSONObject;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="log_data_migration")
public class LogDataMigration extends AbstractModel<Integer>
{
	public static final String DATA_KEY_FILE = "file";
	public static final String DATA_KEY_PATIENT_SET = "patientSet";

	public enum TYPE
	{
		IMPORT,
		EXPORT,
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "uuid")
	private String uuid;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private TYPE type;

	@Column(name = "start_datetime", columnDefinition = "TIMESTAMP")
	private LocalDateTime startDatetime;

	@Column(name = "end_datetime", columnDefinition = "TIMESTAMP")
	private LocalDateTime endDatetime;

	@Column(name = "data")
	private String data;

	@Override
	public Integer getId()
	{
		return id;
	}

	public void setTypeImport()
	{
		this.setType(TYPE.IMPORT);
	}

	public void setTypeExport()
	{
		this.setType(TYPE.EXPORT);
	}

	public JSONObject getDataAsJson()
	{
		return new JSONObject(getData());
	}

	public void setJsonData(JSONObject data)
	{
		this.setData(data.toString());
	}
}
