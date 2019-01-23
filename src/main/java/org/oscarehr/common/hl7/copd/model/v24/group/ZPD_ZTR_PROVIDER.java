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
import org.oscarehr.common.hl7.copd.model.v24.segment.ZFU;

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
			this.add(ZFU.class, false, true); // custom wolf-only segment
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

	public NK1 getNK1(int rep)
	{
		return this.getTyped("NK1", rep, NK1.class);
	}

	public int getNK1Reps()
	{
		try
		{
			return this.getAll("NK1").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZAL getZAL(int rep)
	{
		return this.getTyped("ZAL", rep, ZAL.class);
	}

	public int getZALReps()
	{
		try
		{
			return this.getAll("ZAL").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZFU getZFU(int rep)
	{
		return this.getTyped("ZFU", rep, ZFU.class);
	}

	public int getZFUReps()
	{
		try
		{
			return this.getAll("ZFU").length;
		}
		catch(HL7Exception var)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZSH getZSH(int rep)
	{
		return this.getTyped("ZSH", rep, ZSH.class);
	}

	public int getZSHReps()
	{
		try
		{
			return this.getAll("ZSH").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZQO getZQO(int rep)
	{
		return this.getTyped("ZQO", rep, ZQO.class);
	}

	public int getZQOReps()
	{
		try
		{
			return this.getAll("ZQO").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPV getZPV(int rep)
	{
		return this.getTyped("ZPV", rep, ZPV.class);
	}

	public int getZPVReps()
	{
		try
		{
			return this.getAll("ZPV").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPB getZPB(int rep)
	{
		return this.getTyped("ZPB", rep, ZPB.class);
	}

	public int getZPBReps()
	{
		try
		{
			return this.getAll("ZPB").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
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

	public ZPR getZPR(int rep)
	{
		return this.getTyped("ZPR", rep, ZPR.class);
	}

	public int getZPRReps()
	{
		try
		{
			return this.getAll("ZPR").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZDV getZDV(int rep)
	{
		return this.getTyped("ZDV", rep, ZDV.class);
	}

	public int getZDVReps()
	{
		try
		{
			return this.getAll("ZDV").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPD_ZTR_DEV_HISTORY getDEV_HISTORY()
	{
		return this.getTyped("DEV_HISTORY", ZPD_ZTR_DEV_HISTORY.class);
	}

	public ZHR getZHR()
	{
		return this.getTyped("ZHR", ZHR.class);
	}

	public ZPD_ZTR_ALLERGY getALLERGY(int rep)
	{
		return this.getTyped("ALLERGY", rep, ZPD_ZTR_ALLERGY.class);
	}

	public int getALLERGYReps()
	{
		try
		{
			return this.getAll("ALLERGY").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPD_ZTR_IMMUNIZATION getIMMUNIZATION(int rep)
	{
		return this.getTyped("IMMUNIZATION", rep, ZPD_ZTR_IMMUNIZATION.class);
	}

	public int getIMMUNIZATIONReps()
	{
		try
		{
			return this.getAll("IMMUNIZATION").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZPD_ZTR_LAB getLAB(int rep)
	{
		return this.getTyped("LAB", rep, ZPD_ZTR_LAB.class);
	}

	public int getLABReps()
	{
		try
		{
			return this.getAll("LAB").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZHF getZHF(int rep)
	{
		return this.getTyped("ZHF", rep, ZHF.class);
	}

	public int getZHFReps()
	{
		try
		{
			return this.getAll("ZHF").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZCP getZCP(int rep)
	{
		return this.getTyped("ZCP", rep, ZCP.class);
	}

	public int getZCPReps()
	{
		try
		{
			return this.getAll("ZCP").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}

	public ZAT getZAT(int rep)
	{
		return this.getTyped("ZAT", rep, ZAT.class);
	}

	public int getZATReps()
	{
		try
		{
			return this.getAll("ZAT").length;
		}
		catch(HL7Exception var4)
		{
			String message = "Unexpected error accessing data - this is probably a bug in the source code generator.";
			throw new RuntimeException(message);
		}
	}
}
