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
package org.oscarehr.common.hl7.copd.model.v24.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment contains information related to undiagnosed or diagnosed health problems, symptoms or chronic
 * health conditions experience by the patient. These conditions may be ongoing, or may be part of the patient's
 * history.
 */
public class ZPB extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZPB(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZPB");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "Diagnosis Date");
			this.add(ST.class, false, 1, 1600, new Object[]{message}, "Diagnosis Description");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Diagnosis Code");
			this.add(CE.class, false, 1, 82, new Object[]{message}, "Symptoms Present");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "Onset Date");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Date Resolved");
			this.add(ST.class, false, 1, 10, new Object[]{message}, "Problem Status");
			this.add(NM.class, true, 1, 2, new Object[]{message}, "Outcome Code");
			this.add(ST.class, false, 1, 1000, new Object[]{message}, "Note Text");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public SI getZpb1_setId() throws HL7Exception
	{
		try
		{
			Type t = this.getField(1, 0);
			return (SI) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZpb2_diagnosisDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(2, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZpb3_diagnosisDescription() throws HL7Exception
	{
		try
		{
			Type t = this.getField(3, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getZpb4_diagnosisCode() throws HL7Exception
	{
		try
		{
			Type t = this.getField(4, 0);
			return (CE) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getZpb5_symptomsPresent() throws HL7Exception
	{
		try
		{
			Type t = this.getField(5, 0);
			return (CE) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZpb6_onsetDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(6, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZpb7_dateResolved() throws HL7Exception
	{
		try
		{
			Type t = this.getField(7, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZpb8_problemStatus() throws HL7Exception
	{
		try
		{
			Type t = this.getField(8, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public NM getZpb9_outcomeCode() throws HL7Exception
	{
		try
		{
			Type t = this.getField(9, 0);
			return (NM) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZpb10_noteText() throws HL7Exception
	{
		try
		{
			Type t = this.getField(10, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}




	/**
	 * This method must be overridden. The easiest way is just to return null.
	 */
	@Override
	protected Type createNewTypeWithoutReflection(int field)
	{
		return null;
	}
}
