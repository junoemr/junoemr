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

public class HL7LabWriter
{
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
}
