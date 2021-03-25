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
package org.oscarehr.dataMigration.pref;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExportPreferences implements Serializable
{
	private boolean exportAlertsAndSpecialNeeds;
	private boolean exportAllergiesAndAdverseReactions;
	private boolean exportAppointments;
	private boolean exportCareElements;
	private boolean exportClinicalNotes;
	private boolean exportFamilyHistory;
	private boolean exportImmunizations;
	private boolean exportLaboratoryResults;
	private boolean exportMedicationsAndTreatments;
	private boolean exportPastHealth;
	private boolean exportPersonalHistory;
	private boolean exportProblemList;
	private boolean exportReportsReceived;
	private boolean exportRiskFactors;

	private int threadCount;
	private String exportDirectory;
}
