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
package org.oscarehr.common.hl7.writer;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class HL7LabWriter
{
	private static final Logger logger = MiscUtils.getLogger();

	protected Parser parser;
	protected Message message;
	protected Terser terser;

	public HL7LabWriter(Message message)
	{
		this(message, new PipeParser());
	}

	public HL7LabWriter(Message message, Parser parser)
	{
		this.parser = parser;
		this.message = message;
		this.terser = new Terser(message);
	}

	/**
	 * Encode the message as a string using the hl7 parser encode method.
	 * The exact format will depend on the parser.
	 * @return the hl7 encoded String
	 */
	public String encode() throws HL7Exception
	{
		return parser.encode(message);
	}

	public Terser getTerser()
	{
		return terser;
	}

	public Parser getParser()
	{
		return parser;
	}

	public void setParser(Parser parser)
	{
		this.parser = parser;
	}

	public static String generateRandomAccessionNumber()
	{
		// generate a unique uuid to use as accession number of 36 characters
		UUID uuid = UUID.randomUUID();

		// base 64 encode the uuid to shorten the length to 22 characters (just happens to be the max length of ORC-3 in hl7 2.4)
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		String base64Key = Base64.encodeBase64URLSafeString(bb.array());

		if(base64Key.length() != 22)
		{
			logger.error("Invalid key length: " + base64Key.length() + "; key: " + base64Key);
		}
		return base64Key;
	}
}
