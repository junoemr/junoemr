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
import ca.uhn.hl7v2.model.v24.datatype.IS;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment contains information specific to a significant health problem, diagnosis or procedure pertaining to a
 * member of the patient's family that may indicate a risk factor for the patient's health
 */
public class ZHF extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZHF(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZHF");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "Diagnosis Date");
			this.add(ST.class, false, 1, 1600, new Object[]{message}, "Diagnosis Description");
			this.add(IS.class, true, 1, 15, new Object[]{message}, "Relationship to patient");
			this.add(ST.class, false, 1, 5, new Object[]{message}, "Age at onset");
			this.add(ST.class, false, 1, 5, new Object[]{message}, "Age at death");
			this.add(ST.class, false, 1, 200, new Object[]{message}, "Cause of death");
			this.add(ST.class, false, 1, 64000, new Object[]{message}, "Comments");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public SI getZhf1_setId() throws HL7Exception
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

	public TS getZhf2_diagnosisDate() throws HL7Exception
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

	public ST getZhf3_diagnosisDescription() throws HL7Exception
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

	public IS getZhf4_relationshipToPatient() throws HL7Exception
	{
		try
		{
			Type t = this.getField(4, 0);
			return (IS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZhf5_ageAtOnset() throws HL7Exception
	{
		try
		{
			Type t = this.getField(5, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZhf6_ageAtDeath() throws HL7Exception
	{
		try
		{
			Type t = this.getField(6, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZhf7_causeOfDeath() throws HL7Exception
	{
		try
		{
			Type t = this.getField(7, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZhf8_comments() throws HL7Exception
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

	/**
	 * This method must be overridden. The easiest way is just to return null.
	 */
	@Override
	protected Type createNewTypeWithoutReflection(int field)
	{
		return null;
	}
}
