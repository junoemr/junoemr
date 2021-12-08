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
package org.oscarehr.ws.rest.conversion.summary;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.labs.transfer.BasicLabInfo;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.to.model.SummaryItemTo1;
import org.oscarehr.ws.rest.to.model.SummaryTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.dms.EDocUtil.EDocSort;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

@Component
public class LabsDocsSummary implements Summary
{
	@Autowired
	private OscarLogDao oscarLogDao;

	public static final int DISPLAY_SIZE = 20;

	private static Logger logger = MiscUtils.getLogger();
	protected static final String ELLIPSES = "...";
	protected static final int MAX_LEN_TITLE = 48;
	protected static final int CROP_LEN_TITLE = 45;
	protected static final int MAX_LEN_KEY = 12;
	protected static final int CROP_LEN_KEY = 9;

	@Override
	public SummaryTo1 getSummary(LoggedInInfo loggedInInfo, Integer demographicNo, String summaryCode)
	{
		SummaryTo1 summary = new SummaryTo1("Incoming", 0, SummaryTo1.INCOMING_CODE);
		summary.setDisplaySize(String.valueOf(DISPLAY_SIZE));
		
		List<SummaryItemTo1> list = summary.getSummaryItem();

		Hl7TextInfoDao hl7TextInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);
		List<BasicLabInfo> basicLabInfos = hl7TextInfoDao.listBasicInfoByDemographicNo(
			demographicNo.toString(), 0, null);

		for (BasicLabInfo basicLabInfo : basicLabInfos)
		{

			int segmentID = basicLabInfo.getLabId();
			String label = basicLabInfo.getLabel();
			if (!oscarLogDao.hasRead(loggedInInfo.getLoggedInProvider().getProviderNo(), "lab", Integer.toString(segmentID)))
			{
				label = "*" + label + "*";
			}
			SummaryItemTo1 summaryItem = new SummaryItemTo1(segmentID, label, "action", "lab");
			summaryItem.setDate(
				ConversionUtils.toLegacyDate(basicLabInfo.getObservationDateTime().toLocalDate()));

			String url = "../lab/CA/ALL/labDisplay.jsp?providerNo=" +
				loggedInInfo.getLoggedInProvider().getProviderNo() + "&segmentID=" + segmentID;
			summaryItem.setAction(url);

			if (basicLabInfo.getAbnormal())
			{
				summaryItem.setAbnormalFlag(true);
			}
			list.add(summaryItem);
		}

        //Docs
        ArrayList<EDoc> docList = EDocUtil.listDocs(loggedInInfo, "demographic", ""+demographicNo, null, EDocUtil.PRIVATE, EDocSort.OBSERVATIONDATE, "active");
		String dbFormat = "yyyy-MM-dd";

		String title;

		for (int i = 0; i < docList.size(); i++) {
			EDoc curDoc = docList.get(i);
			String dispStatus = String.valueOf(curDoc.getStatus());

			if (dispStatus.equals("A")) dispStatus = "active";
			else if (dispStatus.equals("H")) dispStatus = "html";

			String dispDocNo = curDoc.getDocId();
			title = StringUtils.maxLenString(curDoc.getDescription(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			if (EDocUtil.getDocUrgentFlag(dispDocNo)) title = StringUtils.maxLenString("!" + curDoc.getDescription(), MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			
			SummaryItemTo1 summaryItem = new SummaryItemTo1(Integer.parseInt(curDoc.getDocId()), title,"action","document");
			

			DateFormat formatter = new SimpleDateFormat(dbFormat);
			String dateStr = curDoc.getObservationDate();
			
			try {
				Date date = formatter.parse(dateStr);
				summaryItem.setDate(date);
			} catch (ParseException ex) {
				MiscUtils.getLogger().debug("EctDisplayDocsAction: Error creating date " + ex.getMessage());
			}

			String url;
			if( curDoc.getRemoteFacilityId()==null && curDoc.isPDF() ) {
				url = "../dms/showDocument.jsp?segmentID=" + dispDocNo +
						"&providerNo=" + loggedInInfo.getLoggedInProviderNo() +
						"&status=A&inWindow=true&chartView&demoName=";//'); return false;";
			}
			else {
				url = "../dms/ManageDocument.do?method=display&doc_no=" + dispDocNo +
						"&providerNo=" + loggedInInfo.getLoggedInProviderNo() +
						"&remoteFacilityId=" + ((curDoc.getRemoteFacilityId() != null) ? curDoc.getRemoteFacilityId() : "");
			}
			summaryItem.setAction(url);
			if(summaryItem.getDisplayName().trim().equals("")){
				summaryItem.setDisplayName("N/A");
			}

			 list.add(summaryItem);
		}

		
		Collections.sort(list, Collections.reverseOrder(new Comparator<SummaryItemTo1>() {
			public int compare(SummaryItemTo1 o1, SummaryItemTo1 o2)
			{
				if (o1 == null && o2 == null)
				{
					return 0;
				}
				if(o1 == null ^ o2 == null) //XOR
				{
					return (o1 == null) ? -1 : 1;
				}

				Date date1 = o1.getDate();
				Date date2 = o2.getDate();

				if (date1 == null && date2 == null)
				{
					return 0;
				}
				if(date1 == null ^ date2 == null) //XOR
				{
					return (date1 == null) ? -1 : 1;
				}
				return date1.compareTo(date2);
			}
		}));
		
		
		
		return summary;
	}
}
