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
package org.oscarehr.dataMigration.model.immunization;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.provider.ProviderModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Immunization extends AbstractTransientModel
{
	private Integer id;
	private String preventionType;
	private String drugIdentificationNumber;
	private PartialDateTime administrationDate;
	private LocalDate nextDate;
	private Boolean refused;
	private Boolean never;

	private ProviderModel provider;
	private ProviderModel createdBy;
	private LocalDateTime createdAt;
	private LocalDateTime lastUpdateDate;

	private String name;
	private String dose;
	private String manufacture;
	private String route;
	private String lot;
	private String location;
	private String reason;
	private String result;
	private String comments;
	private List<ResidualInfo> residualInfo;
}
