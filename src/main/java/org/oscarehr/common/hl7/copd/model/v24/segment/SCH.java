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
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.TX;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This extends the regular SCH segment as the CoPD spec has extra data on the end
 */
public class SCH extends ca.uhn.hl7v2.model.v24.segment.SCH
{
	private static final Logger logger = MiscUtils.getLogger();

	public SCH(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(TS.class, false, 1, 8, new Object[]{message}, "zCreation Date");
			this.add(TX.class, false, 1, 200, new Object[]{message}, "zAppointment Reason");
			this.add(TX.class, false, 1, 500, new Object[]{message}, "zNotes");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public TS getSch28_zCreationDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(28, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TX getSch29_zAppointmentReason() throws HL7Exception
	{
		try
		{
			Type t = this.getField(29, 0);
			return (TX) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TX getSch30_zNotes() throws HL7Exception
	{
		try
		{
			Type t = this.getField(30, 0);
			return (TX) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	/**
	 * This method must be overridden.
	 */
	@Override
	protected Type createNewTypeWithoutReflection(int field)
	{
		switch(field) {
			case 27:
				return new TS(this.getMessage());
			case 28:
				return new TX(this.getMessage());
			case 29:
				return new TX(this.getMessage());
			default:
				return super.createNewTypeWithoutReflection(field);
		}
	}
}
