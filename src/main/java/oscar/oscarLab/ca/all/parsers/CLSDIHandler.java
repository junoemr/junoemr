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
package oscar.oscarLab.ca.all.parsers;

import org.apache.log4j.Logger;

/**
 * Handler for:
 * Calgary Lab Services Diagnostic Imaging
 *
 * @author Robert
 */
public class CLSDIHandler extends CLSHandler implements MessageHandler {

	private static Logger logger = Logger.getLogger(CLSDIHandler.class);

	public CLSDIHandler() {
		super();
	}

	public String getMsgType() {
		return "CLSDI";
	}

	@Override
	public String getServiceDate() {
		try {
			//String serviceDate = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getORC().getOrderEffectiveDateTime().getTimeOfAnEvent().getValue());
			String serviceDate = getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getOBR().getObservationEndDateTime().getTimeOfAnEvent().getValue());
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
			//status = getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservResultStatus().getValue());
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
	public String getFillerOrderNumber() {
		/* CLS-DI switches accession number and order number positions */
		return get("/.OBR-20");

	}

	@Override
	public String getAccessionNum() {
		/* CLS-DI switches accession number and order number positions */
		return get("/.OBR-3-1");
	}

	@Override
	public boolean isUnstructured() {
		return true;
	}
}
