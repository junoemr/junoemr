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
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This is a custom segment created for wolf followup data
 * This is not part of the official CoPD specification
 */
public class ZFU extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZFU(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(ST.class, true, 1, 6, new Object[]{message}, "followupnumber");
			this.add(ST.class, true, 1, 1000, new Object[]{message}, "mdattending");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "date");
			this.add(TS.class, true, 1, 8, new Object[]{message}, "dateoffollowup");
			this.add(ST.class, true, 1, 2000, new Object[]{message}, "followupproblem");
			this.add(ST.class, true, 1, 1, new Object[]{message}, "Done");
			this.add(ST.class, true, 1, 2000, new Object[]{message}, "notes");
			this.add(ST.class, true, 1, 1, new Object[]{message}, "actiondesc");
			this.add(ST.class, true, 1, 1, new Object[]{message}, "urgency");
		}
		catch(HL7Exception var5)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
		}
	}

	public ST getZfu1_followupNumber() throws HL7Exception
	{
		try
		{
			Type t = this.getField(1, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public ST getZfu2_mdAttending() throws HL7Exception
	{
		try
		{
			Type t = this.getField(2, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public TS getZfu3_date() throws HL7Exception
	{
		try
		{
			Type t = this.getField(3, 0);
			return (TS) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public TS getZfu4_followupDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(4, 0);
			return (TS) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public ST getZfu5_followupProblem() throws HL7Exception
	{
		try
		{
			Type t = this.getField(5, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public ST getZfu6_done() throws HL7Exception
	{
		try
		{
			Type t = this.getField(6, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public ST getZfu7_notes() throws HL7Exception
	{
		try
		{
			Type t = this.getField(7, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public ST getZfu8_actionDesc() throws HL7Exception
	{
		try
		{
			Type t = this.getField(8, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}

	public ST getZfu9_urgency() throws HL7Exception
	{
		try
		{
			Type t = this.getField(9, 0);
			return (ST) t;
		}
		catch(ClassCastException var)
		{
			throw new RuntimeException(var);
		}
	}
}
