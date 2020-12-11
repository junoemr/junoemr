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
package org.oscarehr.demographicImport.model.medication;

import lombok.Data;
import org.oscarehr.demographicImport.model.AbstractTransientModel;
import org.oscarehr.demographicImport.model.common.PartialDate;
import org.oscarehr.demographicImport.model.common.PartialDateTime;
import org.oscarehr.demographicImport.model.provider.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public abstract class Medication extends AbstractTransientModel
{
	private Integer id;

	// prescription given
	private Provider prescribingProvider;
	private String outsideProviderName;
	private String outsideProviderOhip;

	private PartialDate rxStartDate;
	private PartialDateTime writtenDate;
	private LocalDate rxEndDate;
	private LocalDateTime createdDateTime;
	private Boolean patientCompliance;
	private LocalDateTime pickupDateTime;
	private String rxStatus;


	// prescription details
	private Integer scriptNo;
	private String drugForm;
	private String method;
	private String route;
	private Boolean nonAuthoritative;


	// dosage info
	private float takeMin;
	private float takeMax;
	private String frequencyCode;
	private String duration;
	private String durationUnit;
	private String quantity;
	private Integer repeat;
	private Boolean longTerm;
	private Boolean pastMed;

	// refill info
	private LocalDate lastRefillDate;
	private Integer refillDuration;
	private Integer refillQuantity;
	private Integer dispenseInterval;
	private Boolean dispenseInternal;

	// notes, comments, instructions
	private String instructions;
	private String specialInstructions;
	private String comment;

	// other
	private String archivedReason;
	private LocalDateTime archivedDateTime;
	private LocalDateTime lastUpdateDateTime;
	private String eTreatmentType;

	public abstract String getDrugName();
}
