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
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.copd.model.v24.segment.SCH;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZBA;

public class ZPD_ZTR_PATIENT extends AbstractGroup
{
	public ZPD_ZTR_PATIENT(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			this.add(PID.class, true, false);
			this.add(ZPD_ZTR_PROVIDER.class, true, true);
			this.add(SCH.class, false, true);
			this.add(ZBA.class, false, true);
			this.add(ZPD_ZTR_THIRD_PARTY.class, false, true);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}
	public PID getPID()
	{
		return this.getTyped("PID", PID.class);
	}

	public SCH getSCH(int rep)
	{
		return this.getTyped("SCH", rep, SCH.class);
	}

	public int getSCHReps()
	{
		try
		{
			return this.getAll("SCH").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZBA getZBA(int rep)
	{
		return this.getTyped("ZBA", rep, ZBA.class);
	}

	public int getZBAReps()
	{
		try
		{
			return this.getAll("ZBA").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPD_ZTR_PROVIDER getPROVIDER(int rep)
	{
		return this.getTyped("PROVIDER", rep, ZPD_ZTR_PROVIDER.class);
	}

	public int getPROVIDERReps()
	{
		try
		{
			return this.getAll("PROVIDER").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPD_ZTR_THIRD_PARTY getTHIRD_PARTY(int rep)
	{
		return this.getTyped("THIRD_PARTY", rep, ZPD_ZTR_THIRD_PARTY.class);
	}

	public int getTHIRD_PARTYReps()
	{
		try
		{
			return this.getAll("THIRD_PARTY").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}
}
