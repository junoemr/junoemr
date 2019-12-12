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
import ca.uhn.hl7v2.model.v25.segment.TQ2;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.copd.model.v24.segment.TQ1;

public class ZPD_ZTR_TIMING_QUANTITY extends AbstractGroup
{
	public ZPD_ZTR_TIMING_QUANTITY(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			// TODO -- figure out why this is v25 and up????
			this.add(TQ1.class, true, true);
			this.add(TQ2.class, false, false);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public TQ1 getTQ1(int rep)
	{
		return this.getTyped("TQ1", rep, TQ1.class);
	}

	public int getTQ1Reps()
	{
		try
		{
			return this.getAll("TQ1").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public TQ2 getTQ2()
	{
		return this.getTyped("TQ2", TQ2.class);
	}
}
