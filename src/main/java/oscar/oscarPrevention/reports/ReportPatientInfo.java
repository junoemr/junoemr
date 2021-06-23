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

package oscar.oscarPrevention.reports;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportPatientInfo {

	public Integer demographicNo;
	public String lastName;
	public String firstName;
	
	private ReportPatientInfo(ArrayList<String> patientListTuple)
	{
		// None of these are actually guaranteed to be there... they just happen to be default checked values when creating a demographic query.
		// These could be totally random things depending on what "Search For" boxes happen to be checked.  Since all the reports require
		// this information anyways, AND they all naively assume that it's there, I've put it into a semi-readable format.
		// ... BUT, this whole prevention reports needs to be redone, because it's totally awful.
		
		this.demographicNo = NumberUtils.toInt(patientListTuple.get(0));
		this.lastName = patientListTuple.get(1);
		this.firstName = patientListTuple.get(2);
	}

	protected static List<ReportPatientInfo> fromList(ArrayList<ArrayList<String>> listOfPatientTuples)
	{
		return listOfPatientTuples.stream().map(ReportPatientInfo::new).collect(Collectors.toList());
	}
}
