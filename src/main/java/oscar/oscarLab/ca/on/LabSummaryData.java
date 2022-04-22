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


package oscar.oscarLab.ca.on;

import java.util.HashMap;
import lombok.Data;
import oscar.oscarMDS.data.PatientInfo;

@Data
public class LabSummaryData {
	
	private int demographicNo;
	private int documentCount;
	private int labCount;
	private int hrmCount;
	private int unmatchedDocumentCount;
	private int unmatchedLabCount;
	private int unmatchedHrmCount;
	private int abnormalCount;
	private int totalCount;

	private HashMap<Integer,PatientInfo> patients;
}