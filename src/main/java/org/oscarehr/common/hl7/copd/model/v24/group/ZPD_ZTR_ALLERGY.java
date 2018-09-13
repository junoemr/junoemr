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
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.copd.model.v24.segment.IAM;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZAS;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZAU;

/**
 * Custom hl7 message class for TOPD formatted messages
 */
public class ZPD_ZTR_ALLERGY extends AbstractGroup
{
	public ZPD_ZTR_ALLERGY(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			this.add(IAM.class, true, false);
			this.add(NTE.class, false, false);
			this.add(ZAS.class, false, false);
			this.add(ZAU.class, false, false);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public IAM getIAM()
	{
		return this.getTyped("IAM", IAM.class);
	}

	public NTE getNTE()
	{
		return this.getTyped("NTE", NTE.class);
	}

	public ZAS getZAS()
	{
		return this.getTyped("ZAS", ZAS.class);
	}

	public ZAU getZAU()
	{
		return this.getTyped("ZAU", ZAU.class);
	}
}
