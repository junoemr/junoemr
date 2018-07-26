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
import ca.uhn.hl7v2.model.v24.datatype.ID;
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This segment contains prescription-related information that is not present in the ORC, RXO or RXE segments. For
 * POSP CoPD the ZRX.2, ZRX.6 and ZRX.7 components are used.
 */
public class ZRX extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public ZRX(Group parent, ModelClassFactory factory)
	{
		super(parent, factory);
		Message message = this.getMessage();

		try
		{
			this.add(CE.class, false, 1, 4104, new Object[]{message}, "Prescription Source");
			this.add(TS.class, true, 1, 24, new Object[]{message}, "Administration Start Date");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Administration Stop Date");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Expiry Date");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "First Fill Expiry Date");
			this.add(CE.class, true, 1, 4104, new Object[]{message}, "Drug Use Type");
			this.add(ID.class, false, 1, 1, new Object[]{message}, "Trial Dispenses Authorized");
			this.add(CE.class, false, 1, 4104, new Object[]{message}, "Prescription Active Status");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Modification Time");
			this.add(ID.class, false, 1, 1, new Object[]{message}, "Contradictions Detected Indicator");
			this.add(ID.class, false, 1, 1, new Object[]{message}, "Dispensing Revoked Indicator");
			this.add(NM.class, false, 1, 4, new Object[]{message}, "Doses Remaining");
			this.add(NM.class, false, 1, 4, new Object[]{message}, "Days Supply Remaining");
			this.add(NM.class, false, 1, 31, new Object[]{message}, "Undispensed Days Supply Remaining");
			this.add(ST.class, false, 1, 4, new Object[]{message}, "Percentage Dispensed");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Supply Exhaustion Date");
			this.add(TS.class, false, 1, 24, new Object[]{message}, "Prescription Exhaustion Date");
		}
		catch(HL7Exception e)
		{
			logger.error("Can't instantiate " + this.getClass().getName());
			throw new RuntimeException(e);
		}
	}

	public CE getZrx1_prescriptionSource() throws HL7Exception
	{
		try
		{
			Type t = this.getField(1, 0);
			return (CE) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx2_administrationStartDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(2, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx3_administrationStopDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(3, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx4_expiryDateDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(4, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx5_firstFillExpiryDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(5, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getZrx6_drugUseType() throws HL7Exception
	{
		try
		{
			Type t = this.getField(6, 0);
			return (CE) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ID getZrx7_trialDispensesAuthorized() throws HL7Exception
	{
		try
		{
			Type t = this.getField(7, 0);
			return (ID) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public CE getZrx8_prescriptionActiveStatus() throws HL7Exception
	{
		try
		{
			Type t = this.getField(8, 0);
			return (CE) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx9_modificationTime() throws HL7Exception
	{
		try
		{
			Type t = this.getField(9, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ID getZrx10_contradictionsDetectedIndicator() throws HL7Exception
	{
		try
		{
			Type t = this.getField(10, 0);
			return (ID) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ID getZrx11_dispensingRevokedIndicatortrial() throws HL7Exception
	{
		try
		{
			Type t = this.getField(11, 0);
			return (ID) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public NM getZrx12_dosesRemaining() throws HL7Exception
	{
		try
		{
			Type t = this.getField(12, 0);
			return (NM) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public NM getZrx13_daysSupplyRemaining() throws HL7Exception
	{
		try
		{
			Type t = this.getField(13, 0);
			return (NM) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public NM getZrx14_undispensedDaysSupplyRemaining() throws HL7Exception
	{
		try
		{
			Type t = this.getField(14, 0);
			return (NM) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public ST getZrx15_percentageDispensed() throws HL7Exception
	{
		try
		{
			Type t = this.getField(15, 0);
			return (ST) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx16_supplyExhaustionDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(16, 0);
			return (TS) t;
		}
		catch(ClassCastException var4)
		{
			throw new RuntimeException(var4);
		}
	}

	public TS getZrx17_prescriptionExhaustionDate() throws HL7Exception
	{
		try
		{
			Type t = this.getField(17, 0);
			return (TS) t;
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
