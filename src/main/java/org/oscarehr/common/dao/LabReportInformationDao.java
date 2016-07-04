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


package org.oscarehr.common.dao;

import oscar.oscarLab.ca.all.parsers.MessageHandler;
import java.util.ArrayList;
import org.oscarehr.common.model.LabReportInformation;
import org.oscarehr.common.map.LabReport;
import org.oscarehr.common.map.LabReportHeader;
import org.oscarehr.common.map.LabReportOBX;
import org.oscarehr.common.map.LabReportUnit;
import org.springframework.stereotype.Repository;
import oscar.oscarLab.ca.all.parsers.Factory;

@Repository
public class LabReportInformationDao extends AbstractDao<LabReportInformation>{

	public LabReportInformationDao() {
		super(LabReportInformation.class);
	}

	public LabReport mapLabVersion(String current_lab_no)
		throws Exception
	{
		MessageHandler handler = Factory.getHandler(current_lab_no);

		if(handler == null)
		{
			throw new Exception("No lab found for lab number: " + current_lab_no);
		}

		LabReport lab_data = new LabReport();

		ArrayList<String> headers = handler.getHeaders();
		int i,j,k;
		for(i=0;i<headers.size();i++){

			LabReportHeader header_map = new LabReportHeader();

			int OBRCount = handler.getOBRCount();
			for ( j=0; j < OBRCount; j++){
				String obrName = handler.getOBRName(j);
				int obxCount = handler.getOBXCount(j);
				for (k=0; k < obxCount; k++){
					if(!headers.get(i).equals(handler.getObservationHeader(j,k)))
					{
						continue;
					}

					String obxName = handler.getOBXName(j, k);
					
					LabReportUnit unit_map = new LabReportUnit();

					unit_map.put("id", handler.getOBXIdentifier(j,k));
					unit_map.put("result", handler.getOBXResult(j, k));
					unit_map.put("abnormal_flag", handler.getOBXAbnormalFlag(j, k));
					unit_map.put("reference_range", handler.getOBXReferenceRange(j, k));
					unit_map.put("timestamp", handler.getTimeStamp(j, k));
					unit_map.put("result_status", handler.getOBXResultStatus(j, k));

					// Create a new on if one doesn't exist
					LabReportOBX obx_map = header_map.get(obxName);

					if(obx_map == null)
					{
						obx_map = new LabReportOBX();
						header_map.put(obxName, obx_map);
					}

					obx_map.put(handler.getOBXUnits(j, k), unit_map);
				}
			}

			lab_data.put(headers.get(i), header_map);
		}

		return lab_data;
	}
}
