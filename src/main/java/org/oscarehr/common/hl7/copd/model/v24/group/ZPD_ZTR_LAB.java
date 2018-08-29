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
package org.oscarehr.common.hl7.copd.model.v24.group;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

public class ZPD_ZTR_LAB extends AbstractGroup
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZPD_ZTR_LAB(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			this.add(OBR.class, true, false);
			this.add(OBX.class, true, true);
			this.add(NTE.class, false, true);
		}
		catch(HL7Exception var3)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(var3);
		}
	}

	public OBR getOBR()
	{
		return this.getTyped("OBR", OBR.class);
	}

	public OBX getOBX(int rep)
	{
		return this.getTyped("OBX", rep, OBX.class);
	}

	public int getOBXReps()
	{
		try
		{
			return this.getAll("OBX").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public NTE getNTE(int rep)
	{
		return this.getTyped("NTE", rep, NTE.class);
	}

	public int getNTEReps()
	{
		try
		{
			return this.getAll("NTE").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}
}
