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
import ca.uhn.hl7v2.model.v24.segment.NK1;
import ca.uhn.hl7v2.model.v24.segment.PRD;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZAL;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZAT;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZCP;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZDV;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZHF;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZHR;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZPB;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZPR;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZPV;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZQO;
import org.oscarehr.common.hl7.copd.model.v24.segment.ZSH;

public class ZPD_ZTR_PROVIDER extends AbstractGroup
{
	public ZPD_ZTR_PROVIDER(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		try
		{
			this.add(PRD.class, true, false);
			this.add(NK1.class, false, true);
			this.add(ZAL.class, false, true);
			this.add(ZSH.class, false, true);
			this.add(ZQO.class, false, true);
			this.add(ZPV.class, false, true);
			this.add(ZPB.class, false, true);
			this.add(ZPD_ZTR_MEDS.class, false, true);
			this.add(ZPR.class, false, true);
			this.add(ZDV.class, false, true);
			this.add(ZPD_ZTR_DEV_HISTORY.class, false, false);
			this.add(ZHR.class, false, false);
			this.add(ZPD_ZTR_PREGNANCY.class, false, true);
			this.add(ZPD_ZTR_ALLERGY.class, false, true);
			this.add(ZPD_ZTR_IMMUNIZATION.class, false, true);
			this.add(ZPD_ZTR_LAB.class, false, true);
			this.add(ZHF.class, false, true);
			this.add(ZCP.class, false, true);
			this.add(ZAT.class, false, true);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public PRD getPRD()
	{
		return this.getTyped("PRD", PRD.class);
	}

	public ZPD_ZTR_MEDS getMEDS(int rep)
	{
		return this.getTyped("MEDS", rep, ZPD_ZTR_MEDS.class);
	}

	public int getMEDSReps()
	{
		try
		{
			return this.getAll("MEDS").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}
}
