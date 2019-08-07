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
package org.oscarehr.common.hl7.AHS.model.v251.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.ST;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.util.MiscUtils;

public class ZBR extends AbstractSegment
{
	public ZBR(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(ST.class, false, 1,20, new Object[]{message}, "Not Specified");
			this.add(ST.class, false, 1,20, new Object[]{message}, "Culture set ID");
			this.add(ST.class, false, 9999,20, new Object[]{message}, "Sensitivity Set IDs");
		}
		catch (HL7Exception var5)
		{
			MiscUtils.getLogger().error("Can't instantiate " + this.getClass().getName(), var5);
		}
	}

	public int getZBR2Reps()
	{
		try
		{
			return getField(2).length;
		}
		catch (HL7Exception e)
		{
			MiscUtils.getLogger().error("Could not get ZBR 2 Reps with error: " + e.getMessage(), e);
		}
		return 0;
	}

	public int getZBR3Reps()
	{
		try
		{
			return getField(3).length;
		}
		catch (HL7Exception e)
		{
			MiscUtils.getLogger().error("Could not get ZBR 3 Reps with error: " + e.getMessage(), e);
		}
		return 0;
	}
}
