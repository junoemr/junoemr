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
package org.oscarehr.demographicImportService;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.dataMigration.service.CoPDMessageStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CoPDMessageStreamTest
{
	private String xml;
	private Object[] expectedResult;
	private int msgCount;

	public CoPDMessageStreamTest(String xml, int messageCount, Object[] results)
	{
		this.xml = xml;
		this.msgCount = messageCount;
		this.expectedResult = results;
	}

	@Parameterized.Parameters
	public static Collection testData()
	{
		return Arrays.asList(new Object[][]
				{
						{"<ZPD_ZTR.MESSAGE></ZPD_ZTR.MESSAGE>", 1, new Object[] {"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"/>"}},
						{"<ZPD_ZTR.MESSAGE><SCH><TS.1>1970</TS.1></SCH></ZPD_ZTR.MESSAGE>", 1, new Object[] {"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><SCH><TS.1>1970</TS.1></SCH></ZPD_ZTR>"}},
						{
							"<ZPD_ZTR.MESSAGE><SCH><TS.1>1970</TS.1></SCH></ZPD_ZTR.MESSAGE>" +
							"<ZPD_ZTR.MESSAGE><SCH>\n<TS.1>2000</TS.1>\n<RP.2>/var/log/foobar.txt</RP.2></SCH></ZPD_ZTR.MESSAGE>" +
							"<ZPD_ZTR.MESSAGE><PID><PID.6>\n<FN.1>MicMan</FN.1>\n</PID.6></PID></ZPD_ZTR.MESSAGE>",
							3,
							new Object[] {
									"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><SCH><TS.1>1970</TS.1></SCH></ZPD_ZTR>",
									"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><SCH>\n<TS.1>2000</TS.1>\n<RP.2>/var/log/foobar.txt</RP.2></SCH></ZPD_ZTR>",
									"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><PID><PID.6>\n<FN.1>MicMan</FN.1>\n</PID.6></PID></ZPD_ZTR>"
							}
						},
						{
								"<ZPD_ZTR.MESSAGE></ZPD_ZTR.MESSAGE><ZPD_ZTR.MESSAGE></ZPD_ZTR.MESSAGE>",
								2,
								new Object[] {"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"/>", "<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"/>"}
						},
						{
								"<CRAP><ZPD_ZTR.MESSAGE></ZPD_ZTR.MESSAGE><ZPD_ZTR.MESSAGE></ZPD_ZTR.MESSAGE></CRAP>",
								2,
								new Object[] {"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"/>", "<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"/>"}
						},
						{
							"<v2:ZPD_ZTR.MESSAGE xmlns:v2=\"http://www.hl7.org/\"></v2:ZPD_ZTR.MESSAGE>",
							1,
							new Object[] {"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"/>"}
						},
						{
							"<v2:ZPD_ZTR.MESSAGE xmlns:v2=\"http://www.hl7.org/\"><v2:ST xmlns:v2=\"http://www.hl7.org/\">namespaces are cool!</v2:ST></v2:ZPD_ZTR.MESSAGE>",
							1,
							new Object [] {"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><ST>namespaces are cool!</ST></ZPD_ZTR>"}
						},
						{
								"<v2:ZPD_ZTR.MESSAGE xmlns:v2=\"http://www.hl7.org/\"><v2:ST>namespaces are cool!</v2:ST></v2:ZPD_ZTR.MESSAGE>" +
								"<v2:ZPD_ZTR.MESSAGE xmlns:v2=\"http://www.hl7.org/\"><v2:ST>Tow messages! my god!</v2:ST></v2:ZPD_ZTR.MESSAGE>",
								2,
								new Object [] {
										"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><ST>namespaces are cool!</ST></ZPD_ZTR>",
										"<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\"><ST>Tow messages! my god!</ST></ZPD_ZTR>",
								}
						},
				});
	}

	@Test
	public void testGetCoPDMessageForEach() throws Exception
	{
		InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		CoPDMessageStream messageStream = new CoPDMessageStream(xmlInputStream);

		ArrayList<String> results = new ArrayList<>();
		messageStream.forEach(results::add);

		Assert.assertEquals("did not get the expected number of messages", this.msgCount, results.size());

		int i =0;
		for (String msgContent : results)
		{
			Assert.assertEquals("did not get expected message content!\n EXPECTED: " +  (String)(expectedResult[i]) + "\n GOT:" + msgContent, (String)(expectedResult[i]), msgContent);
			i ++;
		}
	}

	@Test
	public void testGetCoPDMessageNextMessage() throws Exception
	{
		InputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		CoPDMessageStream messageStream = new CoPDMessageStream(xmlInputStream);

		ArrayList<String> results = new ArrayList<>();
		String msg;
		while (!(msg = messageStream.getNextMessage()).isEmpty())
		{
			results.add(msg);
		}

		Assert.assertEquals("did not get the expected number of messages", this.msgCount, results.size());

		int i =0;
		for (String msgContent : results)
		{
			Assert.assertEquals("did not get expected message content!\n EXPECTED: " +  (String)(expectedResult[i]) + "\n GOT:" + msgContent, (String)(expectedResult[i]), msgContent);
			i ++;
		}
	}
}
