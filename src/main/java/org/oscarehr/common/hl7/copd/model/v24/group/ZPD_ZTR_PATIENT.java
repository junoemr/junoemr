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
import ca.uhn.hl7v2.model.v24.segment.SCH;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZBA;

public class ZPD_ZTR_PATIENT extends AbstractGroup
{
	public ZPD_ZTR_PATIENT(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			this.add(PID.class, true, false);
			this.add(ZPD_ZTR_PATIENT_PROVIDER.class, true, true);
			this.add(SCH.class, false, true);
			this.add(ZBA.class, false, true);
			this.add(ZPD_ZTR_PATIENT_THIRD_PARTY.class, false, true);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}
	public PID getPID()
	{
		PID ret = null;

		try
		{
			ret = (PID) this.get("PID");
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public SCH getSCH()
	{
		SCH ret = null;

		try
		{
			ret = (SCH) this.get("SCH");
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public SCH getSCH(int rep)
	{
		SCH ret = null;

		try
		{
			ret = (SCH) this.get("SCH", rep);
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public int getSCHReps() {
		try {
			int reps = this.getAll("SCH").length;
			return reps;
		} catch (HL7Exception var4) {
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZBA getZBA()
	{
		ZBA ret = null;

		try
		{
			ret = (ZBA) this.get("ZBA");
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}
	public ZBA getZBA(int rep)
	{
		ZBA ret = null;

		try
		{
			ret = (ZBA) this.get("ZBA", rep);
			return ret;
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public int getZBAReps() {
		try {
			int reps = this.getAll("ZBA").length;
			return reps;
		} catch (HL7Exception var4) {
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}
}
