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
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.CNE;
import ca.uhn.hl7v2.model.v24.datatype.CWE;
import ca.uhn.hl7v2.model.v24.datatype.DT;
import ca.uhn.hl7v2.model.v24.datatype.EI;
import ca.uhn.hl7v2.model.v24.datatype.SI;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.datatype.XON;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This replaces the regular IAM segment as the CoPD spec has changed the IAM.5 segment to CWE
 * The rest is copied from the decompiled library version of IAM, with some minor modification
 */
public class IAM extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public IAM(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(SI.class, true, 1, 4, new Object[]{message}, "Set ID - IAM");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Allergen Type Code");
			this.add(CWE.class, true, 1, 2381, new Object[]{message}, "Allergen Code/Mnemonic/Description");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Allergy Severity Code");
			this.add(CWE.class, false, 0, 6104, new Object[]{message}, "Allergy Reaction Code");
			this.add(CNE.class, true, 1, 250, new Object[]{message}, "Allergy Action Code");
			this.add(EI.class, true, 1, 80, new Object[]{message}, "Allergy Unique Identifier");
			this.add(ST.class, false, 1, 60, new Object[]{message}, "Action Reason");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Sensitivity to Causative Agent Code");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Allergen Group Code/Mnemonic/Description");
			this.add(DT.class, false, 1, 8, new Object[]{message}, "Onset Date");
			this.add(ST.class, false, 1, 60, new Object[]{message}, "Onset Date Text");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Reported Date/Time");
			this.add(XPN.class, false, 1, 250, new Object[]{message}, "Reported By");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Relationship to Patient Code");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Alert Device Code");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Allergy Clinical Status Code");
			this.add(XCN.class, false, 1, 250, new Object[]{message}, "Statused by Person");
			this.add(XON.class, false, 1, 250, new Object[]{message}, "Statused by Organization");
			this.add(TS.class, false, 1, 8, new Object[]{message}, "Statused at Date/Time");
		}
		catch(HL7Exception var5)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
		}
	}

	public SI getSetIDIAM()
	{
		SI ret = null;

		try
		{
			Type t = this.getField(1, 0);
			ret = (SI) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public SI getIam1_SetIDIAM()
	{
		SI ret = null;

		try
		{
			Type t = this.getField(1, 0);
			ret = (SI) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getAllergenTypeCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(2, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam2_AllergenTypeCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(2, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CWE getAllergenCodeMnemonicDescription()
	{
		return getIam3_AllergenCodeMnemonicDescription();
	}

	public CWE getIam3_AllergenCodeMnemonicDescription()
	{
		CWE ret = null;

		try
		{
			Type t = this.getField(3, 0);
			ret = (CWE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getAllergySeverityCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(4, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam4_AllergySeverityCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(4, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CWE getAllergyReactionCode(int rep) throws HL7Exception
	{
		return this.getIam5_AllergyReactionCode(rep);
	}

	public CWE[] getAllergyReactionCode()
	{
		CWE[] ret = null;

		try
		{
			Type[] t = this.getField(5);
			ret = new CWE[t.length];

			for(int i = 0; i < ret.length; ++i)
			{
				ret[i] = (CWE) t[i];
			}

			return ret;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
		catch(HL7Exception var5)
		{
			throw new RuntimeException(var5);
		}
	}

	public int getAllergyReactionCodeReps()
	{
		try
		{
			return this.getField(5).length;
		}
		catch(ClassCastException var2)
		{
			throw new RuntimeException(var2);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public CWE insertAllergyReactionCode(int index) throws HL7Exception
	{
		return (CWE) super.insertRepetition(5, index);
	}

	public CWE removeAllergyReactionCode(int index) throws HL7Exception
	{
		return (CWE) super.removeRepetition(5, index);
	}

	public CWE getIam5_AllergyReactionCode(int rep) throws HL7Exception
	{
		CWE ret = null;

		try
		{
			Type t = this.getField(5, rep);
			ret = (CWE) t;
			return ret;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST[] getIam5_AllergyReactionCode()
	{
		ST[] ret = null;

		try
		{
			Type[] t = this.getField(5);
			ret = new ST[t.length];

			for(int i = 0; i < ret.length; ++i)
			{
				ret[i] = (ST) t[i];
			}

			return ret;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
		catch(HL7Exception var5)
		{
			throw new RuntimeException(var5);
		}
	}

	public int getIam5_AllergyReactionCodeReps()
	{
		try
		{
			return this.getField(5).length;
		}
		catch(ClassCastException var2)
		{
			throw new RuntimeException(var2);
		}
		catch(HL7Exception var3)
		{
			throw new RuntimeException(var3);
		}
	}

	public ST insertIam5_AllergyReactionCode(int index) throws HL7Exception
	{
		return (ST) super.insertRepetition(5, index);
	}

	public ST removeIam5_AllergyReactionCode(int index) throws HL7Exception
	{
		return (ST) super.removeRepetition(5, index);
	}

	public CNE getAllergyActionCode()
	{
		CNE ret = null;

		try
		{
			Type t = this.getField(6, 0);
			ret = (CNE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CNE getIam6_AllergyActionCode()
	{
		CNE ret = null;

		try
		{
			Type t = this.getField(6, 0);
			ret = (CNE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public EI getAllergyUniqueIdentifier()
	{
		EI ret = null;

		try
		{
			Type t = this.getField(7, 0);
			ret = (EI) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public EI getIam7_AllergyUniqueIdentifier()
	{
		EI ret = null;

		try
		{
			Type t = this.getField(7, 0);
			ret = (EI) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getActionReason()
	{
		ST ret = null;

		try
		{
			Type t = this.getField(8, 0);
			ret = (ST) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getIam8_ActionReason()
	{
		ST ret = null;

		try
		{
			Type t = this.getField(8, 0);
			ret = (ST) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getSensitivityToCausativeAgentCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(9, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam9_SensitivityToCausativeAgentCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(9, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getAllergenGroupCodeMnemonicDescription()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(10, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam10_AllergenGroupCodeMnemonicDescription()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(10, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public DT getOnsetDate()
	{
		DT ret = null;

		try
		{
			Type t = this.getField(11, 0);
			ret = (DT) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public DT getIam11_OnsetDate()
	{
		DT ret = null;

		try
		{
			Type t = this.getField(11, 0);
			ret = (DT) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getOnsetDateText()
	{
		ST ret = null;

		try
		{
			Type t = this.getField(12, 0);
			ret = (ST) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getIam12_OnsetDateText()
	{
		ST ret = null;

		try
		{
			Type t = this.getField(12, 0);
			ret = (ST) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getReportedDateTime()
	{
		TS ret = null;

		try
		{
			Type t = this.getField(13, 0);
			ret = (TS) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getIam13_ReportedDateTime()
	{
		TS ret = null;

		try
		{
			Type t = this.getField(13, 0);
			ret = (TS) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public XPN getReportedBy()
	{
		XPN ret = null;

		try
		{
			Type t = this.getField(14, 0);
			ret = (XPN) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public XPN getIam14_ReportedBy()
	{
		XPN ret = null;

		try
		{
			Type t = this.getField(14, 0);
			ret = (XPN) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getRelationshipToPatientCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(15, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam15_RelationshipToPatientCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(15, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getAlertDeviceCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(16, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam16_AlertDeviceCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(16, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getAllergyClinicalStatusCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(17, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getIam17_AllergyClinicalStatusCode()
	{
		CE ret = null;

		try
		{
			Type t = this.getField(17, 0);
			ret = (CE) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public XCN getStatusedByPerson()
	{
		XCN ret = null;

		try
		{
			Type t = this.getField(18, 0);
			ret = (XCN) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public XCN getIam18_StatusedByPerson()
	{
		XCN ret = null;

		try
		{
			Type t = this.getField(18, 0);
			ret = (XCN) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public XON getStatusedByOrganization()
	{
		XON ret = null;

		try
		{
			Type t = this.getField(19, 0);
			ret = (XON) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public XON getIam19_StatusedByOrganization()
	{
		XON ret = null;

		try
		{
			Type t = this.getField(19, 0);
			ret = (XON) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getStatusedAtDateTime()
	{
		TS ret = null;

		try
		{
			Type t = this.getField(20, 0);
			ret = (TS) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getIam20_StatusedAtDateTime()
	{
		TS ret = null;

		try
		{
			Type t = this.getField(20, 0);
			ret = (TS) t;
			return ret;
		}
		catch(ClassCastException var3)
		{
			throw new RuntimeException(var3);
		}
		catch(HL7Exception var4)
		{
			throw new RuntimeException(var4);
		}
	}

	protected Type createNewTypeWithoutReflection(int field)
	{
		switch(field)
		{
			case 0:
				return new SI(this.getMessage());
			case 1:
				return new CE(this.getMessage());
			case 2:
				return new CWE(this.getMessage());
			case 3:
				return new CE(this.getMessage());
			case 4:
				return new CWE(this.getMessage());
			case 5:
				return new CNE(this.getMessage());
			case 6:
				return new EI(this.getMessage());
			case 7:
				return new ST(this.getMessage());
			case 8:
				return new CE(this.getMessage());
			case 9:
				return new CE(this.getMessage());
			case 10:
				return new DT(this.getMessage());
			case 11:
				return new ST(this.getMessage());
			case 12:
				return new TS(this.getMessage());
			case 13:
				return new XPN(this.getMessage());
			case 14:
				return new CE(this.getMessage());
			case 15:
				return new CE(this.getMessage());
			case 16:
				return new CE(this.getMessage());
			case 17:
				return new XCN(this.getMessage());
			case 18:
				return new XON(this.getMessage());
			case 19:
				return new TS(this.getMessage());
			default:
				return null;
		}
	}
}
