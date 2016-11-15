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
package oscar.oscarLab.ca.all.upload.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.OscarAuditLogger;
import org.oscarehr.util.SpringUtils;

import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

public class GDMLQCHandler implements MessageHandler {
	
	Logger logger = Logger.getLogger(GDMLHandler.class);	
	Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao)SpringUtils.getBean("hl7TextInfoDao");

	@Override
	public String parse(String serviceName, String fileName, int fileId) {
		int i = 0;
		try {
			ArrayList<String> messages = Utilities.separateMessages(fileName);
			for (i = 0; i < messages.size(); i++) {

				String msg = messages.get(i);
				if(isDuplicate(msg)) {
					return ("success");
				}
				MessageUploader.routeReport(serviceName, "GDMLQC", msg, fileId);

			}

			// Since the GDML labs show more than one lab on the same page when grouped
			// by accession number their abnormal status must be updated to reflect the
			// other labs that they are grouped with as well
			updateLabStatus(messages.size());
			logger.info("Parsed OK");
		} catch (Exception e) {
			MessageUploader.clean(fileId);
			logger.error("Could not upload message", e);
			return null;
		}
		return ("success");
	}
	
	/** re-check the abnormal status of the last 'n' labs */
	private void updateLabStatus(int n) {
		
		List<Hl7TextInfo> labs = hl7TextInfoDao.getAllLabsByLabNumberResultStatus();
		for(Hl7TextInfo lab : labs) {
			String labResultStatus = lab.getResultStatus();
			if(n > 0 && !labResultStatus.equals("A")) {
				
				oscar.oscarLab.ca.all.parsers.MessageHandler h = Factory.getHandler(lab.getLabNumber());
				
				for(int i=0; i<h.getOBRCount(); i++ ) {
					for(int j=0; j<h.getOBXCount(i); j++ ) {
						if (h.isOBXAbnormal(i, j)) {
							lab.setResultStatus("A");							
						}
					}
				}
			}
		}		
	}
	private boolean isDuplicate(String msg) {
		//OLIS requirements - need to see if this is a duplicate
		oscar.oscarLab.ca.all.parsers.MessageHandler h = Factory.getHandler("GDMLQC", msg);
		//if final		
		if(h.getOrderStatus().equals("F")) {
			String fullAcc = h.getAccessionNum();
			String acc = h.getAccessionNum();
			if(acc.indexOf("-")!=-1) {
				acc = acc.substring(acc.indexOf("-")+1);
			}
			//do we have this?
			List<Hl7TextInfo> dupResults = hl7TextInfoDao.searchByAccessionNumber(acc);
			for(Hl7TextInfo dupResult:dupResults) {
				if(dupResult.equals(fullAcc)) {
					//if(h.getHealthNum().equals(dupResult.getHealthNumber())) {
					OscarAuditLogger.getInstance().log("Lab", "Skip", "Duplicate lab skipped - accession " + fullAcc + "\n" + msg);
					return true;
					//}
				}
				if(dupResult.getAccessionNumber().length()>4 && dupResult.getAccessionNumber().substring(4).equals(acc)) {
					//if(h.getHealthNum().equals(dupResult.getHealthNumber())) {
					OscarAuditLogger.getInstance().log("Lab", "Skip", "Duplicate lab skipped - accession " + fullAcc + "\n" + msg);
					return true;
					//}
				}
			}		
		}
		return false;	
	}
}
