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

package integration.tests.util.junoUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientCollection
{
	public static ArrayList<Patient> patients = new ArrayList<Patient>();
	public static HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	static {
		Patient patient = new Patient();
		patient.setLastName("Momlastname");
		patient.setFirstName("Momfirstname");
		patient.setDobYear("1980");
		patient.setDobMonth("09");
		patient.setDobDate("09");
		patient.setSex("F");
		patient.setHin("01234566540");
		patients.add(patient);
		patientMap.put(patient.getLastName(), patient);

		//Demographic demo = new Demographic();
		Patient patient2 = new Patient();
		patient2.setLastName("Dadlastname");
		patient2.setFirstName("Dadfirstname");
		//patient2.setFirstNames(demo.getFirstName());
		patient2.setDobYear("1988");
		patient2.setDobMonth("08");
		patient2.setDobDate("08");
		patient2.setSex("M");
		patient2.setHin("111111111");
		patients.add(patient2);
		patientMap.put(patient2.getLastName(), patient2);

		Patient patient3 = new Patient();
		patient3.setLastName("Sonlastname");
		patient3.setFirstName("Sonfirstname");
		patient3.setDobYear("2008");
		patient3.setDobMonth("08");
		patient3.setDobDate("08");
		patient3.setSex("M");
		patient3.setHin("9874397159");
		patients.add(patient3);
		patientMap.put(patient3.getLastName(), patient3);

	}

}
