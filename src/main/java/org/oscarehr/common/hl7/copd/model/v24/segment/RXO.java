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
import ca.uhn.hl7v2.model.v24.datatype.CQ;
import ca.uhn.hl7v2.model.v24.datatype.CWE;
import ca.uhn.hl7v2.model.v24.datatype.ID;
import ca.uhn.hl7v2.model.v24.datatype.LA1;
import ca.uhn.hl7v2.model.v24.datatype.NM;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

/**
 * This replaces the regular RXO segment as the CoPD spec has changed the RXO.1 and RXO.6, RXO.20 segments to CWE.
 * They also added extra segments to the end (see segment 27/28)
 * The rest is copied from the decompiled library version of RXO, with some minor modification
 */
public class RXO extends AbstractSegment
{
	private static final Logger logger = MiscUtils.getLogger();

	public RXO(Group parent, ModelClassFactory factory) {
		super(parent, factory);
		Message message = this.getMessage();

		try {
			this.add(CWE.class, false, 1, 250, new Object[]{message}, "Requested Give Code");
			this.add(NM.class, false, 1, 20, new Object[]{message}, "Requested Give Amount - Minimum");
			this.add(NM.class, false, 1, 20, new Object[]{message}, "Requested Give Amount - Maximum");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Requested Give Units");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Requested Dosage Form");
			this.add(CWE.class, false, 0, 250, new Object[]{message}, "Provider's Pharmacy/Treatment Instructions");
			this.add(CE.class, false, 0, 250, new Object[]{message}, "Provider's Administration Instructions");
			this.add(LA1.class, false, 1, 200, new Object[]{message}, "Deliver-To Location");
			this.add(ID.class, false, 1, 1, new Object[]{message, new Integer(161)}, "Allow Substitutions");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Requested Dispense Code");
			this.add(NM.class, false, 1, 20, new Object[]{message}, "Requested Dispense Amount");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Requested Dispense Units");
			this.add(NM.class, false, 1, 3, new Object[]{message}, "Number of Refills");
			this.add(XCN.class, false, 0, 250, new Object[]{message}, "Ordering Provider's DEA Number");
			this.add(XCN.class, false, 0, 250, new Object[]{message}, "Pharmacist/Treatment Supplier's Verifier ID");
			this.add(ID.class, false, 1, 1, new Object[]{message, new Integer(136)}, "Needs Human Review");
			this.add(ST.class, false, 1, 20, new Object[]{message}, "Requested Give Per (Time Unit)");
			this.add(NM.class, false, 1, 20, new Object[]{message}, "Requested Give Strength");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Requested Give Strength Units");
			this.add(CWE.class, false, 0, 250, new Object[]{message}, "Indication");
			this.add(ST.class, false, 1, 6, new Object[]{message}, "Requested Give Rate Amount");
			this.add(CE.class, false, 1, 250, new Object[]{message}, "Requested Give Rate Units");
			this.add(CQ.class, false, 1, 10, new Object[]{message}, "Total Daily Dose");
			this.add(CE.class, false, 0, 250, new Object[]{message}, "Supplementary Code");

			this.add(ST.class, false, 0, 0, new Object[]{message}, "PLACEHOLDER");
			this.add(ST.class, false, 0, 0, new Object[]{message}, "PLACEHOLDER");
			this.add(ID.class, false, 1, 1, new Object[]{message}, "Pharmacy order Type");
			this.add(CQ.class, false, 1, 4135, new Object[]{message}, "Dispersal Interval");

		} catch (HL7Exception var5) {
			logger.error("Can't instantiate " + this.getClass().getName());
		}

	}

	public CWE getRequestedGiveCode() {
		CWE ret = null;

		try {
			Type t = this.getField(1, 0);
			ret = (CWE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE getRxo1_RequestedGiveCode() {
		CWE ret = null;

		try {
			Type t = this.getField(1, 0);
			ret = (CWE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRequestedGiveAmountMinimum() {
		NM ret = null;

		try {
			Type t = this.getField(2, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRxo2_RequestedGiveAmountMinimum() {
		NM ret = null;

		try {
			Type t = this.getField(2, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRequestedGiveAmountMaximum() {
		NM ret = null;

		try {
			Type t = this.getField(3, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRxo3_RequestedGiveAmountMaximum() {
		NM ret = null;

		try {
			Type t = this.getField(3, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRequestedGiveUnits() {
		CE ret = null;

		try {
			Type t = this.getField(4, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRxo4_RequestedGiveUnits() {
		CE ret = null;

		try {
			Type t = this.getField(4, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRequestedDosageForm() {
		CE ret = null;

		try {
			Type t = this.getField(5, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRxo5_RequestedDosageForm() {
		CE ret = null;

		try {
			Type t = this.getField(5, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE getProviderSPharmacyTreatmentInstructions(int rep) throws HL7Exception {
		CWE ret = null;

		try {
			Type t = this.getField(6, rep);
			ret = (CWE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE[] getProviderSPharmacyTreatmentInstructions() {
		CWE[] ret = null;

		try {
			Type[] t = this.getField(6);
			ret = new CWE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CWE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getProviderSPharmacyTreatmentInstructionsReps() {
		try {
			return this.getField(6).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CWE insertProviderSPharmacyTreatmentInstructions(int index) throws HL7Exception {
		return (CWE)super.insertRepetition(6, index);
	}

	public CWE removeProviderSPharmacyTreatmentInstructions(int index) throws HL7Exception {
		return (CWE)super.removeRepetition(6, index);
	}

	public CWE getRxo6_ProviderSPharmacyTreatmentInstructions(int rep) throws HL7Exception {
		CWE ret = null;

		try {
			Type t = this.getField(6, rep);
			ret = (CWE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE[] getRxo6_ProviderSPharmacyTreatmentInstructions() {
		CWE[] ret = null;

		try {
			Type[] t = this.getField(6);
			ret = new CWE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CWE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getRxo6_ProviderSPharmacyTreatmentInstructionsReps() {
		try {
			return this.getField(6).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CWE insertRxo6_ProviderSPharmacyTreatmentInstructions(int index) throws HL7Exception {
		return (CWE)super.insertRepetition(6, index);
	}

	public CWE removeRxo6_ProviderSPharmacyTreatmentInstructions(int index) throws HL7Exception {
		return (CWE)super.removeRepetition(6, index);
	}

	public CE getProviderSAdministrationInstructions(int rep) throws HL7Exception {
		CE ret = null;

		try {
			Type t = this.getField(7, rep);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE[] getProviderSAdministrationInstructions() {
		CE[] ret = null;

		try {
			Type[] t = this.getField(7);
			ret = new CE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getProviderSAdministrationInstructionsReps() {
		try {
			return this.getField(7).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CE insertProviderSAdministrationInstructions(int index) throws HL7Exception {
		return (CE)super.insertRepetition(7, index);
	}

	public CE removeProviderSAdministrationInstructions(int index) throws HL7Exception {
		return (CE)super.removeRepetition(7, index);
	}

	public CE getRxo7_ProviderSAdministrationInstructions(int rep) throws HL7Exception {
		CE ret = null;

		try {
			Type t = this.getField(7, rep);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE[] getRxo7_ProviderSAdministrationInstructions() {
		CE[] ret = null;

		try {
			Type[] t = this.getField(7);
			ret = new CE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getRxo7_ProviderSAdministrationInstructionsReps() {
		try {
			return this.getField(7).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CE insertRxo7_ProviderSAdministrationInstructions(int index) throws HL7Exception {
		return (CE)super.insertRepetition(7, index);
	}

	public CE removeRxo7_ProviderSAdministrationInstructions(int index) throws HL7Exception {
		return (CE)super.removeRepetition(7, index);
	}

	public LA1 getDeliverToLocation() {
		LA1 ret = null;

		try {
			Type t = this.getField(8, 0);
			ret = (LA1)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public LA1 getRxo8_DeliverToLocation() {
		LA1 ret = null;

		try {
			Type t = this.getField(8, 0);
			ret = (LA1)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public ID getAllowSubstitutions() {
		ID ret = null;

		try {
			Type t = this.getField(9, 0);
			ret = (ID)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public ID getRxo9_AllowSubstitutions() {
		ID ret = null;

		try {
			Type t = this.getField(9, 0);
			ret = (ID)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRequestedDispenseCode() {
		CE ret = null;

		try {
			Type t = this.getField(10, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRxo10_RequestedDispenseCode() {
		CE ret = null;

		try {
			Type t = this.getField(10, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRequestedDispenseAmount() {
		NM ret = null;

		try {
			Type t = this.getField(11, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRxo11_RequestedDispenseAmount() {
		NM ret = null;

		try {
			Type t = this.getField(11, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRequestedDispenseUnits() {
		CE ret = null;

		try {
			Type t = this.getField(12, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRxo12_RequestedDispenseUnits() {
		CE ret = null;

		try {
			Type t = this.getField(12, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getNumberOfRefills() {
		NM ret = null;

		try {
			Type t = this.getField(13, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRxo13_NumberOfRefills() {
		NM ret = null;

		try {
			Type t = this.getField(13, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public XCN getOrderingProviderSDEANumber(int rep) throws HL7Exception {
		XCN ret = null;

		try {
			Type t = this.getField(14, rep);
			ret = (XCN)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public XCN[] getOrderingProviderSDEANumber() {
		XCN[] ret = null;

		try {
			Type[] t = this.getField(14);
			ret = new XCN[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (XCN)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getOrderingProviderSDEANumberReps() {
		try {
			return this.getField(14).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public XCN insertOrderingProviderSDEANumber(int index) throws HL7Exception {
		return (XCN)super.insertRepetition(14, index);
	}

	public XCN removeOrderingProviderSDEANumber(int index) throws HL7Exception {
		return (XCN)super.removeRepetition(14, index);
	}

	public XCN getRxo14_OrderingProviderSDEANumber(int rep) throws HL7Exception {
		XCN ret = null;

		try {
			Type t = this.getField(14, rep);
			ret = (XCN)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public XCN[] getRxo14_OrderingProviderSDEANumber() {
		XCN[] ret = null;

		try {
			Type[] t = this.getField(14);
			ret = new XCN[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (XCN)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getRxo14_OrderingProviderSDEANumberReps() {
		try {
			return this.getField(14).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public XCN insertRxo14_OrderingProviderSDEANumber(int index) throws HL7Exception {
		return (XCN)super.insertRepetition(14, index);
	}

	public XCN removeRxo14_OrderingProviderSDEANumber(int index) throws HL7Exception {
		return (XCN)super.removeRepetition(14, index);
	}

	public XCN getPharmacistTreatmentSupplierSVerifierID(int rep) throws HL7Exception {
		XCN ret = null;

		try {
			Type t = this.getField(15, rep);
			ret = (XCN)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public XCN[] getPharmacistTreatmentSupplierSVerifierID() {
		XCN[] ret = null;

		try {
			Type[] t = this.getField(15);
			ret = new XCN[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (XCN)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getPharmacistTreatmentSupplierSVerifierIDReps() {
		try {
			return this.getField(15).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public XCN insertPharmacistTreatmentSupplierSVerifierID(int index) throws HL7Exception {
		return (XCN)super.insertRepetition(15, index);
	}

	public XCN removePharmacistTreatmentSupplierSVerifierID(int index) throws HL7Exception {
		return (XCN)super.removeRepetition(15, index);
	}

	public XCN getRxo15_PharmacistTreatmentSupplierSVerifierID(int rep) throws HL7Exception {
		XCN ret = null;

		try {
			Type t = this.getField(15, rep);
			ret = (XCN)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public XCN[] getRxo15_PharmacistTreatmentSupplierSVerifierID() {
		XCN[] ret = null;

		try {
			Type[] t = this.getField(15);
			ret = new XCN[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (XCN)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getRxo15_PharmacistTreatmentSupplierSVerifierIDReps() {
		try {
			return this.getField(15).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public XCN insertRxo15_PharmacistTreatmentSupplierSVerifierID(int index) throws HL7Exception {
		return (XCN)super.insertRepetition(15, index);
	}

	public XCN removeRxo15_PharmacistTreatmentSupplierSVerifierID(int index) throws HL7Exception {
		return (XCN)super.removeRepetition(15, index);
	}

	public ID getNeedsHumanReview() {
		ID ret = null;

		try {
			Type t = this.getField(16, 0);
			ret = (ID)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public ID getRxo16_NeedsHumanReview() {
		ID ret = null;

		try {
			Type t = this.getField(16, 0);
			ret = (ID)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public ST getRequestedGivePerTimeUnit() {
		ST ret = null;

		try {
			Type t = this.getField(17, 0);
			ret = (ST)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public ST getRxo17_RequestedGivePerTimeUnit() {
		ST ret = null;

		try {
			Type t = this.getField(17, 0);
			ret = (ST)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRequestedGiveStrength() {
		NM ret = null;

		try {
			Type t = this.getField(18, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public NM getRxo18_RequestedGiveStrength() {
		NM ret = null;

		try {
			Type t = this.getField(18, 0);
			ret = (NM)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRequestedGiveStrengthUnits() {
		CE ret = null;

		try {
			Type t = this.getField(19, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRxo19_RequestedGiveStrengthUnits() {
		CE ret = null;

		try {
			Type t = this.getField(19, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE getIndication(int rep) throws HL7Exception {
		CWE ret = null;

		try {
			Type t = this.getField(20, rep);
			ret = (CWE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE[] getIndication() {
		CWE[] ret = null;

		try {
			Type[] t = this.getField(20);
			ret = new CWE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CWE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getIndicationReps() {
		try {
			return this.getField(20).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CWE insertIndication(int index) throws HL7Exception {
		return (CWE)super.insertRepetition(20, index);
	}

	public CWE removeIndication(int index) throws HL7Exception {
		return (CWE)super.removeRepetition(20, index);
	}

	public CWE getRxo20_Indication(int rep) throws HL7Exception {
		CWE ret = null;

		try {
			Type t = this.getField(20, rep);
			ret = (CWE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CWE[] getRxo20_Indication() {
		CWE[] ret = null;

		try {
			Type[] t = this.getField(20);
			ret = new CWE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CWE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getRxo20_IndicationReps() {
		try {
			return this.getField(20).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CWE insertRxo20_Indication(int index) throws HL7Exception {
		return (CWE)super.insertRepetition(20, index);
	}

	public CWE removeRxo20_Indication(int index) throws HL7Exception {
		return (CWE)super.removeRepetition(20, index);
	}

	public ST getRequestedGiveRateAmount() {
		ST ret = null;

		try {
			Type t = this.getField(21, 0);
			ret = (ST)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public ST getRxo21_RequestedGiveRateAmount() {
		ST ret = null;

		try {
			Type t = this.getField(21, 0);
			ret = (ST)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRequestedGiveRateUnits() {
		CE ret = null;

		try {
			Type t = this.getField(22, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getRxo22_RequestedGiveRateUnits() {
		CE ret = null;

		try {
			Type t = this.getField(22, 0);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CQ getTotalDailyDose() {
		CQ ret = null;

		try {
			Type t = this.getField(23, 0);
			ret = (CQ)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CQ getRxo23_TotalDailyDose() {
		CQ ret = null;

		try {
			Type t = this.getField(23, 0);
			ret = (CQ)t;
			return ret;
		} catch (ClassCastException var3) {
			throw new RuntimeException(var3);
		} catch (HL7Exception var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE getSupplementaryCode(int rep) throws HL7Exception {
		CE ret = null;

		try {
			Type t = this.getField(24, rep);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE[] getSupplementaryCode() {
		CE[] ret = null;

		try {
			Type[] t = this.getField(24);
			ret = new CE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getSupplementaryCodeReps() {
		try {
			return this.getField(24).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CE insertSupplementaryCode(int index) throws HL7Exception {
		return (CE)super.insertRepetition(24, index);
	}

	public CE removeSupplementaryCode(int index) throws HL7Exception {
		return (CE)super.removeRepetition(24, index);
	}

	public CE getRxo24_SupplementaryCode(int rep) throws HL7Exception {
		CE ret = null;

		try {
			Type t = this.getField(24, rep);
			ret = (CE)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CE[] getRxo24_SupplementaryCode() {
		CE[] ret = null;

		try {
			Type[] t = this.getField(24);
			ret = new CE[t.length];

			for(int i = 0; i < ret.length; ++i) {
				ret[i] = (CE)t[i];
			}

			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		} catch (HL7Exception var5) {
			throw new RuntimeException(var5);
		}
	}

	public int getRxo24_SupplementaryCodeReps() {
		try {
			return this.getField(24).length;
		} catch (ClassCastException var2) {
			throw new RuntimeException(var2);
		} catch (HL7Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public CE insertRxo24_SupplementaryCode(int index) throws HL7Exception {
		return (CE)super.insertRepetition(24, index);
	}

	public CE removeRxo24_SupplementaryCode(int index) throws HL7Exception {
		return (CE)super.removeRepetition(24, index);
	}

	public ID getRxo27_pharmacyOrderType(int rep) throws HL7Exception {
		ID ret = null;

		try {
			Type t = this.getField(27, rep);
			ret = (ID)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	public CQ getRxo28_dispensingInterval(int rep) throws HL7Exception {
		CQ ret = null;

		try {
			Type t = this.getField(28, rep);
			ret = (CQ)t;
			return ret;
		} catch (ClassCastException var4) {
			throw new RuntimeException(var4);
		}
	}

	protected Type createNewTypeWithoutReflection(int field) {
		switch(field) {
			case 0:
				return new CWE(this.getMessage());
			case 1:
				return new NM(this.getMessage());
			case 2:
				return new NM(this.getMessage());
			case 3:
				return new CE(this.getMessage());
			case 4:
				return new CE(this.getMessage());
			case 5:
				return new CWE(this.getMessage());
			case 6:
				return new CE(this.getMessage());
			case 7:
				return new LA1(this.getMessage());
			case 8:
				return new ID(this.getMessage(), new Integer(161));
			case 9:
				return new CE(this.getMessage());
			case 10:
				return new NM(this.getMessage());
			case 11:
				return new CE(this.getMessage());
			case 12:
				return new NM(this.getMessage());
			case 13:
				return new XCN(this.getMessage());
			case 14:
				return new XCN(this.getMessage());
			case 15:
				return new ID(this.getMessage(), new Integer(136));
			case 16:
				return new ST(this.getMessage());
			case 17:
				return new NM(this.getMessage());
			case 18:
				return new CE(this.getMessage());
			case 19:
				return new CWE(this.getMessage());
			case 20:
				return new ST(this.getMessage());
			case 21:
				return new CE(this.getMessage());
			case 22:
				return new CQ(this.getMessage());
			case 23:
				return new CE(this.getMessage());
			case 26:
				return new ID(this.getMessage());
			case 27:
				return new CQ(this.getMessage());
			default:
				return null;
		}
	}
}
