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
package oscar.oscarLab.ca.all.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilitiesTest
{
	private static final String SAMPLE_VALID_HL7 = "MSH|^~\\&|^OLIS^X500||^2.16.840.1.113883.3.239.14:10000029^ISO||20210927194835-0400||ERP^Z99^ERP_R09|d9dbdae4-bd4b-4b5a-952c-9582fc5c0612|P|2.3.1\r\n" +
			"PID|4||2000052247^^^^JHN^^^^ON&ONTARIO&HL70347^^GP||ABCONavy^Royal^Michael^^^^U||19401212|M|||755 ABC  ASDASD Rd^^Toronto^ON^M3B 1X3^CAN^H|||||||||||||||||||\r\n" +
			"NTE|1|L|\\.br\\Please phone all rsults to the doctor's office ASAP\\.br\\Speciment is moderately lipemic \\.br\\ |RE^REMARK^HL70364\r\n" +
			"PV1|1|Z|||||^^^^^^^^^^^^^^^^^^^^^||||||||||^^^^^^^^^^^^^^^^^^^^^";

	@Test
	public void testFixLineBreaks_validLab()
	{
		String marker = "\\.br\\";
		String result = Utilities.fixLineBreaks(SAMPLE_VALID_HL7, marker);

		assertEquals("Valid hl7 was modified", SAMPLE_VALID_HL7, result);
	}

	@Test
	public void testFixLineBreaks_labWithInvalidNewlines()
	{
		String marker = "\\.br\\";
		String hl7 = "MSH|^~\\&|^OLIS^X500||^2.16.840.1.113883.3.239.14:10000029^ISO||20210927194835-0400||ERP^Z99^ERP_R09|d9dbdae4-bd4b-4b5a-952c-9582fc5c0612|P|2.3.1\r\n" +
				"PID|4||2000052247^^^^JHN^^^^ON&ONTARIO&HL70347^^GP||ABCONavy^Royal^Michael^^^^U||19401212|M|||755 ABC  ASDASD Rd^^Toronto^ON^M3B 1X3^CAN^H|||||||||||||||||||\r\n" +
				"NTE|1|L|\r\nPlease phone all rsults to the doctor's office ASAP\r\nSpeciment is moderately lipemic \\.br\\ |RE^REMARK^HL70364\r\n" +
				"PV1|1|Z|||||^^^^^^^^^^^^^^^^^^^^^||||||||||^^^^^^^^^^^^^^^^^^^^^";

		String result = Utilities.fixLineBreaks(hl7, marker);
		assertEquals("modified hl7 did not match expectation", SAMPLE_VALID_HL7, result);
	}
}
