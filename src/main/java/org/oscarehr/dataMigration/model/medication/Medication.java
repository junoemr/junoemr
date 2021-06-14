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
package org.oscarehr.dataMigration.model.medication;

import lombok.Data;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.provider.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class Medication extends AbstractTransientModel
{
	private Integer id;

	// prescription given
	private Provider prescribingProvider;
	private String outsideProviderName;
	private String outsideProviderOhip;

	private PartialDateTime writtenDate;
	private PartialDate rxStartDate;
	private PartialDate rxEndDate;
	private LocalDateTime createdDateTime;
	private Boolean patientCompliance;
	private LocalDateTime pickupDateTime;
	private CDSConstants.PrescriptionStatus rxStatus;


	// prescription details
	private Integer scriptNo;
	private String drugForm;
	private String method;
	private String route;
	private Boolean nonAuthoritative;


	// dosage info
	private float takeMin;
	private float takeMax;
	private FrequencyCode frequencyCode;
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
	private Boolean archived;
	private String archivedReason;
	private LocalDateTime archivedDateTime;
	private LocalDateTime lastUpdateDateTime;
	private CDSConstants.TreatmentType eTreatmentType;
	private List<ResidualInfo> residualInfo;

	public abstract String getDrugName();

	/**
	 * calculate medication end date based on frequency, quantity and dosage.
	 * @param rxStartDate - the rx start date
	 * @return - the rx end date or null if end date cannot be determined
	 */
	public static LocalDate calculateEndDate(LocalDate rxStartDate, FrequencyCode frequency, double amount, double dosage)
	{
		if(rxStartDate == null || frequency == null)
		{
			return null;
		}
		double frequencyScaler = frequency.toScalar();
		long durationDays = Math.round(amount / (dosage * frequencyScaler));
		return rxStartDate.plusDays(durationDays);
	}
}
