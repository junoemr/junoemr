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
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment contains social history about the patient.
 */
public class ZSH extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZSH(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - ZSH");
			this.add(ST.class, false, 1, 1000, new Object[]{message}, "Social Alert");
			this.add(ST.class, false, 1, 2000, new Object[]{message}, "Journal Notes");
			this.add(IS.class, false, 1, 5, new Object[]{message}, "Relationship Status");
			this.add(ST.class, false, 1, 50, new Object[]{message}, "Occupation");
			this.add(ST.class, false, 1, 1000, new Object[]{message}, "Employer");
			this.add(ST.class, false, 1, 50, new Object[]{message}, "Education");
			this.add(ST.class, false, 1, 1000, new Object[]{message}, "Leisure Activities");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public SI getZsh1_setId() throws HL7Exception
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

	public ST getZsh2_socialAlert() throws HL7Exception
	{
		try
		{
			Type t = this.getField(2, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZsh3_journalNotes() throws HL7Exception
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

	public IS getZsh4_relationshipStatus() throws HL7Exception
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

	public ST getZsh5_occupation() throws HL7Exception
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

	public ST getZsh6_employer() throws HL7Exception
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

	public ST getZsh7_education() throws HL7Exception
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

	public ST getZsh8_leisureActivities() throws HL7Exception
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
