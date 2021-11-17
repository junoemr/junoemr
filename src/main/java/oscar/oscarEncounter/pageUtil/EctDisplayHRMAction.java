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
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dto.HRMDemographicDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.service.HRMService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.OscarProperties.Module;
import oscar.util.DateUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EctDisplayHRMAction extends EctDisplayAction {

	private static final Logger logger = MiscUtils.getLogger();
	private static final String cmd = "HRM";
	private final HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
	private final HRMService hrmService = (HRMService) SpringUtils.getBean("HRMService");
	private final OscarLogDao oscarLogDao = (OscarLogDao) SpringUtils.getBean("oscarLogDao");
	
	public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		Integer demographicNo = Integer.parseInt(bean.demographicNo);

		if (!OscarProperties.getInstance().isModuleEnabled(Module.MODULE_HRM) ||
			!securityInfoManager.hasPrivileges(loggedInInfo.getLoggedInProviderNo(), demographicNo, Permission.HRM_READ))
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

			Map<String, HRMDemographicDocument> demographicDocuments =
				hrmService.getHrmDocumentsForDemographic(demographicNo);

			for (Map.Entry<String, HRMDemographicDocument> entry : demographicDocuments.entrySet()) {
				
				HRMDocument hrmDocument = entry.getValue().getHrmDocument();
				List<Integer> duplicateIdList = entry.getValue().getDuplicateIds();

				String reportStatus = hrmDocument.getReportStatus().toValueString();
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

				item.setDate(date);
				hash = Math.abs(winName.hashCode());

				StringBuilder duplicateLabIdQueryString=new StringBuilder();
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