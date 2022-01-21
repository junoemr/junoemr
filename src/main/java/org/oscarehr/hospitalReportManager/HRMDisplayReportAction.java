/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentComment;
import org.oscarehr.hospitalReportManager.model.HRMObservation;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentCommentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentSubClassDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.dao.HRMProviderConfidentialityStatementDao;
import org.oscarehr.hospitalReportManager.service.HRMDocumentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

public class HRMDisplayReportAction extends DispatchAction {
	
	private static Logger logger=MiscUtils.getLogger();

	private static HRMDocumentService hrmDocumentService = (HRMDocumentService) SpringUtils.getBean(HRMDocumentService.class);
	private static HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
	private static HRMDocumentToDemographicDao hrmDocumentToDemographicDao = (HRMDocumentToDemographicDao) SpringUtils.getBean("HRMDocumentToDemographicDao");
	private static HRMDocumentToProviderDao hrmDocumentToProviderDao = (HRMDocumentToProviderDao) SpringUtils.getBean("HRMDocumentToProviderDao");
	private static HRMDocumentSubClassDao hrmDocumentSubClassDao = (HRMDocumentSubClassDao) SpringUtils.getBean("HRMDocumentSubClassDao");
	private static HRMDocumentCommentDao hrmDocumentCommentDao = (HRMDocumentCommentDao) SpringUtils.getBean("HRMDocumentCommentDao");
	private static HRMProviderConfidentialityStatementDao hrmProviderConfidentialityStatementDao = (HRMProviderConfidentialityStatementDao) SpringUtils.getBean("HRMProviderConfidentialityStatementDao");
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String hrmDocumentId = request.getParameter("id");

		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		Integer demographicNumberForLog = null;

		if(!securityInfoManager.hasPrivileges(loggedInInfo.getLoggedInProviderNo(), Permission.HRM_READ))
		{
			LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_FAILURE, hrmDocumentId, request.getRemoteAddr());
			throw new SecurityException("missing required permission: " + Permission.HRM_READ.name());
		}
		
		if (hrmDocumentId != null) {
			HrmDocument document = hrmDocumentService.getHrmDocument(Integer.parseInt(hrmDocumentId));

			if (document != null)
			{
				try
				{
					HRMReport report = HRMReportParser.parseReport(document.getReportFile().getPath(), document.getReportFileSchemaVersion());
					request.setAttribute("hrmDocument", document);
					
					request.setAttribute("hrmReport", report);
					request.setAttribute("hrmReportId", document.getId());
					request.setAttribute("hrmReportTime", ConversionUtils.toDateString(document.getReceivedDateTime()));
					
					request.setAttribute("hrmDuplicateNum", document.getMatchingData().getNumDuplicatesReceived());
					request.setAttribute("facilityName", document.getSendingFacility());
					
					List<HRMDocumentToDemographic> demographicLinkList = hrmDocumentToDemographicDao.findByHrmDocumentId(document.getId());
					HRMDocumentToDemographic demographicLink = (demographicLinkList.size() > 0 ? demographicLinkList.get(0) : null);
					
					if (demographicLink != null)
					{
						demographicNumberForLog = demographicLink.getDemographicNo();
					}
					
					request.setAttribute("demographicLink", demographicLink);
					
					List<HRMDocumentToProvider> providerLinkList = hrmDocumentToProviderDao.findByHrmDocumentIdNoSystemUser(document.getId());
					request.setAttribute("providerLinkList", providerLinkList);
					
					List<HRMObservation> subClassList = hrmDocumentSubClassDao.getSubClassesByDocumentId(document.getId());
					request.setAttribute("subClassList", subClassList);
					
					HRMDocumentToProvider thisProviderLink = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(document.getId(), loggedInInfo.getLoggedInProviderNo());
					request.setAttribute("thisProviderLink", thisProviderLink);
					
					if (thisProviderLink != null)
					{
						thisProviderLink.setViewed(true);
						hrmDocumentToProviderDao.merge(thisProviderLink);
					}
					
					HRMObservation hrmObservation = null;
					if (subClassList != null)
					{
						for (HRMObservation temp : subClassList)
						{
							if (temp.isActive())
							{
								hrmObservation = temp;
								break;
							}
						}
					}

					// Get all the other HRM documents that are either a child, sibling, or parent
					List<HRMDocument> allDocumentsWithRelationship = hrmDocumentDao.findAllDocumentsWithRelationship(document.getId());
					request.setAttribute("allDocumentsWithRelationship", allDocumentsWithRelationship);

					List<HRMDocumentComment> documentComments = hrmDocumentCommentDao.getCommentsForDocument(Integer.parseInt(hrmDocumentId));
					request.setAttribute("hrmDocumentComments", documentComments);
					
					String confidentialityStatement = hrmProviderConfidentialityStatementDao.getConfidentialityStatementForProvider(loggedInInfo.getLoggedInProviderNo());
					request.setAttribute("confidentialityStatement", confidentialityStatement);
					
					String duplicateLabIdsString = StringUtils.trimToNull(request.getParameter("duplicateLabIds"));
					Map<Integer, Date> dupReportDates = new HashMap<Integer, Date>();
					Map<Integer, Date> dupTimeReceived = new HashMap<Integer, Date>();
					
					if (duplicateLabIdsString != null)
					{
						String[] duplicateLabIdsStringSplit = duplicateLabIdsString.split(",");
						for (String tempId : duplicateLabIdsStringSplit)
						{
							HRMDocument doc = hrmDocumentDao.find(Integer.parseInt(tempId));
							dupReportDates.put(Integer.parseInt(tempId), doc.getReportDate());
							dupTimeReceived.put(Integer.parseInt(tempId), doc.getTimeReceived());
						}
						
					}
					
					request.setAttribute("dupReportDates", dupReportDates);
					request.setAttribute("dupTimeReceived", dupTimeReceived);
				}
				catch (Exception e)
				{
					LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), demographicNumberForLog, LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_FAILURE, hrmDocumentId, request.getRemoteAddr());
				}
			}
		}
		
		
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), demographicNumberForLog, LogConst.ACTION_READ, LogConst.CON_HRM, LogConst.STATUS_SUCCESS, hrmDocumentId, request.getRemoteAddr());
		return mapping.findForward("display");
	}
	
	public static HRMDocumentToProvider getHRMDocumentFromProvider(String providerNo, Integer hrmDocumentId)
	{
		return(hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(hrmDocumentId, providerNo));
	}
}