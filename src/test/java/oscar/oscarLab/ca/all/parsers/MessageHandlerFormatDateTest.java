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
package oscar.oscarLab.ca.all.parsers;

import ca.uhn.hl7v2.HL7Exception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MessageHandlerFormatDateTest
{
	private final String dateTime;
	private final String expected;

	public MessageHandlerFormatDateTest(String dateTime, String expected)
	{
		this.dateTime = dateTime;
		this.expected = expected;
	}

	@Parameterized.Parameters
	public static Collection<?> testData()
	{
		return Arrays.asList(new Object[][]
				{
						{null, ""},
						{"", ""},
						{"20121021", "2012-10-21"},
						{"2012102105", "2012-10-21"},
						{"201210210525", "2012-10-21"},
						{"20121021052556", "2012-10-21"},
						{"20121021052556-0700", "2012-10-21"},
						{"20121021052556-0400", "2012-10-21"},
				});
	}

	@Test
	public void testFormatDate()
	{
		MessageHandler messageHandler = new TestMessageHandler();
		String result = messageHandler.formatDate(this.dateTime);
		assertEquals(this.expected, result);
	}

	private static class TestMessageHandler extends MessageHandler
	{
		@Override
		public String preUpload(String hl7Message) throws HL7Exception
		{
			return null;
		}

		@Override
		public boolean canUpload()
		{
			return false;
		}

		@Override
		public void postUpload()
		{

		}

		@Override
		public void init(String hl7Body) throws HL7Exception
		{

		}

		@Override
		public String getMsgType()
		{
			return null;
		}

		@Override
		public String getNteForPID()
		{
			return null;
		}

		@Override
		public String getAccessionNum()
		{
			return null;
		}

		@Override
		public String getNteForOBX(int i, int j)
		{
			return null;
		}
	}

}
