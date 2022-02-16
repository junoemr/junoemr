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
package org.oscarehr.common.hl7.OLIS.model.v231.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.util.MiscUtils;

/**
 * 10.2.5.13.4 ZBX Segment – Observation Result Extension Segment
 */
public class ZBX extends AbstractSegment
{
	public ZBX(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(TS.class, true, 1,19, new Object[]{message}, "Test Result Release Date/Time");
			this.add(ST.class, false, 1,15, new Object[]{message}, "Test Result Sort Key");
		}
		catch (HL7Exception var5)
		{
			MiscUtils.getLogger().error("Can't instantiate " + this.getClass().getName(), var5);
		}
	}

	public TS getZbx1_testResultReleaseDateTime()
	{
		return (TS) this.getTypedField(1, 0);
	}
	public ST getZbx2_testResultSortKey()
	{
		return (ST) this.getTypedField(2, 0);
	}
}
