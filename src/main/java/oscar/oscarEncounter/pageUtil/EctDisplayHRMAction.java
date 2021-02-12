/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */


package oscar.oscarEncounter.pageUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarLab.ca.on.HRMResultsData;
import oscar.util.DateUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EctDisplayHRMAction extends EctDisplayAction {

	private static final Logger logger = MiscUtils.getLogger();
	private static final String cmd = "HRM";
	private final HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
	private final OscarLogDao oscarLogDao = (OscarLogDao) SpringUtils.getBean("oscarLogDao");
	
	public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		Integer demographicNo = Integer.parseInt(bean.demographicNo);

		if(!securityInfoManager.hasPrivilege(loggedInInfo, SecObjectName._HRM, SecurityInfoManager.READ, demographicNo)
				|| !OscarProperties.getInstance().hasHRMDocuments())
		{
			return true; // HRM section does not show up at all
		}
		else
		{
			String winName = "docs" + bean.demographicNo;
			String url = "popupPage(500,1115,'" + winName + "', '" + request.getContextPath() + "/hospitalReportManager/displayHRMDocList.jsp?demographic_no=" + demographicNo + "')";
			Dao.setLeftURL(url);
			Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.Index.msgHRMDocuments"));

			Dao.setRightHeadingID(cmd); //no menu so set div id to unique id for this action

			StringBuilder javascript = new StringBuilder("<script type=\"text/javascript\">");
			String js = "";
			String dbFormat = "yyyy-MM-dd";
			String serviceDateStr = "";
			String key;
			String title;
			int hash;
			String BGCOLOUR = request.getParameter("hC");
			Date date;

			List<HRMDocument> allHrmDocsForDemo = hrmDocumentDao.findByDemographicId(demographicNo);

			List<Integer> doNotShowList = new LinkedList<>();
			HashMap<String, HRMDocument> docsToDisplay = new HashMap<>();
			HashMap<String, HRMDocument> labReports = new HashMap<>();
			HashMap<String, ArrayList<Integer>> duplicateLabIds = new HashMap<>();

			for (HRMDocument doc : allHrmDocsForDemo)
			{
				String facilityId = doc.getSendingFacilityId();
				String facilityReportId = doc.getSendingFacilityReportId();
				String deliverToUserId = doc.getDeliverToUserId();

				// filter duplicate reports
				String duplicateKey;
				//TODO - figure out version lookup here too
				if(!"4.3".equals(doc.getReportFileSchemaVersion())) // legacy xml lookup
				{
					HRMReport hrmReport = HRMReportParser.parseReport(doc.getReportFile(), doc.getReportFileSchemaVersion());
					if(hrmReport != null)
					{
						facilityId = hrmReport.getSendingFacilityId();
						facilityReportId = hrmReport.getSendingFacilityReportNo();
						deliverToUserId = hrmReport.getDeliverToUserId();
					}
				}

				// if we are missing too much data (cds imports can cause this), we don't want to filter the reports, just choose a unique key
				if(facilityId == null && facilityReportId == null)
				{
					duplicateKey = String.valueOf(doc.getId());
				}
				else
				{
					// the key = SendingFacility+':'+ReportNumber+':'+DeliverToUserID as per HRM spec can be used to signify duplicate report
					duplicateKey = facilityId + ':' + facilityReportId + ':' + deliverToUserId;
				}

				List<HRMDocument> relationshipDocs = hrmDocumentDao.findAllDocumentsWithRelationship(doc.getId());

				HRMDocument oldestDocForTree = doc;
				for(HRMDocument relationshipDoc : relationshipDocs)
				{
					if(relationshipDoc.getId().intValue() != doc.getId().intValue())
					{
						if(relationshipDoc.getReportDate().compareTo(oldestDocForTree.getReportDate()) >= 0
								|| relationshipDoc.getReportStatus().equalsIgnoreCase(HrmDocument.REPORT_STATUS.CANCELLED.getValue()))
						{
							doNotShowList.add(oldestDocForTree.getId());
							oldestDocForTree = relationshipDoc;
						}
					}
				}

				boolean addToList = true;
				for(HRMDocument displayDoc : docsToDisplay.values())
				{
					if(displayDoc.getId().intValue() == oldestDocForTree.getId().intValue())
					{
						addToList = false;
						break;
					}
				}

				for(Integer doNotShowId : doNotShowList)
				{
					if(doNotShowId.intValue() == oldestDocForTree.getId().intValue())
					{
						addToList = false;
						break;
					}
				}

				if (addToList)
				{
					// if no duplicate
					if (!docsToDisplay.containsKey(duplicateKey))
					{
						docsToDisplay.put(duplicateKey,oldestDocForTree);
						labReports.put(duplicateKey, doc);
					}
					else // there exists an entry like this one
					{
						HRMDocument previousHrmReport=labReports.get(duplicateKey);

						logger.debug("Duplicate report found : previous=" + previousHrmReport.getId() + ", current=" + doc.getId());
						
						Integer duplicateIdToAdd;
						
						// if the current entry is newer than the previous one then replace it, other wise just keep the previous entry
						if (HRMResultsData.isNewer(doc, previousHrmReport))
						{
							HRMDocument previousHRMDocument = docsToDisplay.get(duplicateKey);
							duplicateIdToAdd=previousHRMDocument.getId();
							
							docsToDisplay.put(duplicateKey,oldestDocForTree);
							labReports.put(duplicateKey, doc);
						}
						else
						{
							duplicateIdToAdd=doc.getId();
						}

						ArrayList<Integer> duplicateIds=duplicateLabIds.get(duplicateKey);
						if (duplicateIds==null)
						{
							duplicateIds=new ArrayList<Integer>();
							duplicateLabIds.put(duplicateKey, duplicateIds);
						}
						
						duplicateIds.add(duplicateIdToAdd);						
					}
				}
			}

			for (Map.Entry<String, HRMDocument> entry : docsToDisplay.entrySet()) {
				
				String duplicateKey=entry.getKey();
				HRMDocument hrmDocument=entry.getValue();
				
				String reportStatus = hrmDocument.getReportStatus();
				String dispFilename = hrmDocument.getReportType();
				String dispDocNo    = hrmDocument.getId().toString();
				String description = hrmDocument.getDescription();
				
				String t = StringUtils.isNullOrEmpty(description)?dispFilename:description;
				if (reportStatus != null && reportStatus.equalsIgnoreCase("C")) {
					t = "(Cancelled) " + t;
				}

				title = StringUtils.maxLenString(t, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
				
				DateFormat formatter = new SimpleDateFormat(dbFormat);
				String dateStr = hrmDocument.getTimeReceived().toString();
				NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
				try {
					date = formatter.parse(dateStr);
					serviceDateStr =  DateUtils.formatDate(date,request.getLocale()); 
				}
				catch(ParseException ex ) {
					MiscUtils.getLogger().debug("EctDisplayHRMAction: Error creating date " + ex.getMessage());
					serviceDateStr = "Error";
					date = null;
				}

				String user = (String) request.getSession().getAttribute("user");
				item.setDate(date);
				hash = Math.abs(winName.hashCode());

				StringBuilder duplicateLabIdQueryString=new StringBuilder();
				ArrayList<Integer> duplicateIdList=duplicateLabIds.get(duplicateKey);
            	if (duplicateIdList!=null)
            	{
					for (Integer duplicateLabIdTemp : duplicateIdList)
	            	{
	            		if (duplicateLabIdQueryString.length()>0) duplicateLabIdQueryString.append(',');
	            		duplicateLabIdQueryString.append(duplicateLabIdTemp);
	            	}
				}

				url = "popupPage(700,800,'" + hash + "', '" + request.getContextPath() + "/hospitalReportManager/Display.do?id="+dispDocNo+"&duplicateLabIds="+duplicateLabIdQueryString+"');";

				String labRead = "";
				if(!oscarLogDao.hasRead(( (String) request.getSession().getAttribute("user")   ),"hrm",dispDocNo)){
                	labRead = "*";	
                }

				
				item.setLinkTitle(title + serviceDateStr);
				item.setTitle(labRead+title+labRead);
				key = StringUtils.maxLenString(dispFilename, MAX_LEN_KEY, CROP_LEN_KEY, ELLIPSES) + "(" + serviceDateStr + ")";
				key = StringEscapeUtils.escapeJavaScript(key);


				js = "itemColours['" + key + "'] = '" + BGCOLOUR + "'; autoCompleted['" + key + "'] = \"" + url + "\"; autoCompList.push('" + key + "');";
				javascript.append(js);
				url += "return false;";
				item.setURL(url);
				Dao.addItem(item);

			}
			javascript.append("</script>");

			Dao.setJavaScript(javascript.toString());
			return true;
		}
	}

	@Override
	public String getCmd() {
		return cmd;
	}

}
