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

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.ORC;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

/**
 * Handler for:
 * Calgary Lab Services Diagnostic Imaging
 * @author Robert
 */
public class CLSDIHandler extends CLSHandler implements MessageHandler {
	
	public CLSDIHandler() {
		super();
		logger = Logger.getLogger(CLSDIHandler.class);
	}
	
	public String parse(String serviceName, String fileName, int fileId) {

		int i = 0;
        oscar.oscarLab.ca.all.parsers.CLSDIHandler newVersionCLSParser = new oscar.oscarLab.ca.all.parsers.CLSDIHandler();
        
        Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao)SpringUtils.getBean("hl7TextInfoDao");
        Hl7TextMessage hl7TextMessage = new Hl7TextMessage();
		
		try {
			ArrayList<String> messages = Utilities.separateMessages(fileName);
			for (i = 0; i < messages.size(); i++) {
				String msg = messages.get(i);
				
				// HACK -- some labs start with an extra line which oscar doesn't read, so skip it
				if( i==0 && msg.startsWith("BHS")) {
					continue;
				}
				/*
				if(isDuplicate(msg)) {
					return ("success");
				}
				*/
                newVersionCLSParser.init(msg);
                String accessionNumber = newVersionCLSParser.getAccessionNum();
                String fillerOrderNumber = newVersionCLSParser.getFillerOrderNumber();
                Hl7TextInfo hl7TextInfo = hl7TextInfoDao.findLatestVersionByAccessionNumberOrFillerNumber(
                		accessionNumber, fillerOrderNumber);
                
                // Glucose labs come back with different accession numbers, but the same filler number.
                // We are going to replace any successive accession numbers with the originals as
                // suggested in the CLS conformance documentation
                if(hl7TextInfo != null && hl7TextInfo.getFillerOrderNum().equals(fillerOrderNumber) && 
                		!hl7TextInfo.getAccessionNumber().equals(accessionNumber)) {

                	msg = ReplaceAccessionNumber(msg, accessionNumber, hl7TextInfo.getAccessionNumber());
                }

                if(hl7TextInfo != null) {
                	String lastVersionLab = oscar.oscarLab.ca.all.parsers.Factory.getHL7Body(Integer.toString(hl7TextInfo.getLabNumber()));
                	msg = mergeLabs(lastVersionLab, msg);
                }
				MessageUploader.routeReport(serviceName, "CLSDI", msg, fileId);

			}
		} catch (Exception e) {
			MessageUploader.clean(fileId);
			logger.error("Could not upload message: ", e);
			MiscUtils.getLogger().error("Error", e);
			return null;
		}
		return ("success");

	}
	
	private String mergeLabs(String oldVersion, String newVersion) throws HL7Exception
	{
		String outLabString = newVersion;
		StringBuilder test = new StringBuilder(newVersion);

        oscar.oscarLab.ca.all.parsers.CLSDIHandler oldVersionCLSParser = new oscar.oscarLab.ca.all.parsers.CLSDIHandler();
        oldVersionCLSParser.init(oldVersion);
        oscar.oscarLab.ca.all.parsers.CLSDIHandler newVersionCLSParser = new oscar.oscarLab.ca.all.parsers.CLSDIHandler();
        newVersionCLSParser.init(newVersion);

        int currentObrCount = newVersionCLSParser.getOBRCount();
        
        // Get all OBRs from the old version that don't exist in the new version
        // and append them to the current version
        ArrayList<OBR> oldObrs = this.getObrs(oldVersionCLSParser);
        int obrIndex = 0;
        for(OBR oldObr : oldObrs)
        {
        	logger.info("CHECK OBR for merge: " + oldObr.getName());
        	String fillerNumber = this.getObrFillerNumber(oldObr);
        	if(!this.obrExists(fillerNumber, newVersionCLSParser))
        	{
        		logger.info("OBR Not found in new lab");
        		currentObrCount++;
        		// Remove the old OBR index so we can add the new one
        		String tempObr = oldObr.encode();
                tempObr = tempObr.substring(tempObr.indexOf('|') + 1);
                tempObr = tempObr.substring(tempObr.indexOf('|') + 1);

        		// Set the OBR index
                outLabString += this.lineDelimiter + "OBR|" + Integer.toString(currentObrCount) + "|" + tempObr;

                // Get OBR NTE records
                int obrNteCount = oldVersionCLSParser.getOBRCommentCount(obrIndex);
                for(int obrNteIndex = 0; obrNteIndex < obrNteCount; obrNteIndex++) {
					outLabString += this.lineDelimiter + oldVersionCLSParser.getOBRNTE(obrIndex, obrNteIndex).encode();
                }

        		// Get Previous version OBX records
                int obxCount = oldVersionCLSParser.getOBXCount(obrIndex);
                for(int obxIndex = 0; obxIndex < obxCount; obxIndex++) {
                    outLabString += this.lineDelimiter + oldVersionCLSParser.getOBX(obrIndex, obxIndex).encode();

                    // Get Previous version OBX NTE records
                    int nteCount = oldVersionCLSParser.getOBXCommentCount(obrIndex, obxIndex);
                    for(int nteIndex = 0; nteIndex < nteCount; nteIndex++) {
                        outLabString += this.lineDelimiter + oldVersionCLSParser.getNTE(obrIndex, obxIndex, nteIndex).encode();
                    }
                }

        		// Get Previous version ORC record if one exists
                ORC orc = oldVersionCLSParser.getORC(obrIndex);
                if(orc != null && orc.encode().length() > 5) {
                    test.append(outLabString += this.lineDelimiter + orc.encode());
                }
        	}
        	obrIndex++;
        }
		return outLabString;
	}

}
