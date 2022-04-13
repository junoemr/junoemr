/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.SpringUtils;

/**
 * Handler for:
 * Calgary Lab Services Diagnostic Imaging
 *
 * @author Robert
 */
public class CLSDIHandler extends CLSHandler {

	private static Logger logger = Logger.getLogger(CLSDIHandler.class);
	private static Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao) SpringUtils.getBean("hl7TextInfoDao");

	protected static final String CLSDI_SENDING_APPLICATION = "OPEN ENGINE";
	protected static final String CLSDI_SENDING_FACILITY = "DI";

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.3"))
		{
			ORU_R01 msh = (ORU_R01) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplication = messageHeaderSegment.getSendingApplication().getNamespaceID().getValue();
			String sendingFacility = messageHeaderSegment.getSendingFacility().getNamespaceID().getValue();

			return CLSDI_SENDING_APPLICATION.equalsIgnoreCase(sendingApplication) &&
					CLSDI_SENDING_FACILITY.equalsIgnoreCase(sendingFacility);
		}
		return false;
	}

	public CLSDIHandler() {
		super();
	}
	public CLSDIHandler(String hl7Body) throws HL7Exception
	{
		super(hl7Body);
	}
	public CLSDIHandler(Message msg) throws HL7Exception
	{
		super(msg);
	}

	/**
	 * This method should determine if the lab can be routed
	 * @return true if the lab can be routed, false otherwise
	 */
	@Override
	public boolean canUpload()
	{
		String accessionNumber = this.getUniqueIdentifier();
		Hl7TextInfo hl7TextInfo = hl7TextInfoDao.findLatestVersionByAccessionNo(accessionNumber);

		// if the report exists the new version must be a correction
		return (hl7TextInfo == null || this.getOrderStatus().equals("C"));
	}

    /* ===================================== Hl7 Parsing ====================================== */

	public String getMsgType() {
		return "CLSDI";
	}

	@Override
	public String getServiceDate() {
		try {
			String serviceDate = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObr8_ObservationEndDateTime().getTimeOfAnEvent().getValue());
			return (formatDateTime(serviceDate));
		}
		catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getOBXResultStatus(int i, int j) {
		String status = "";
		try {
			status = getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBR().getResultStatus().getValue());
			if (status.equalsIgnoreCase("C")) {
				status = "Corrected";
			}
			else if (status.equalsIgnoreCase("F")) {
				status = "Final";
			}
		}
		catch (Exception e) {
			logger.error("Error retrieving obx result status", e);
		}
		return status;
	}

	@Override
	public String getOrderStatus() {
		try {
			return get("/.OBR-25-1");
		}
		catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getUniqueVersionIdentifier() {
		/* CLS-DI switches accession number and order number positions */
		return get("/.OBR-20");
	}

	@Override
	public String getUniqueIdentifier() {
		/* CLS-DI switches accession number and order number positions */
		return get("/.OBR-3-1");
	}

	@Override
	public boolean isUnstructured() {
		return true;
	}
}
