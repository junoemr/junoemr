/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmDocument.ReportClass;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
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
import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.oscarehr.hospitalReportManager.service.HRMDocumentService;
import org.oscarehr.hospitalReportManager.service.HRMSubClassService;
import org.oscarehr.inbox.service.InboxManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.on.CommonLabResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public class HRMModifyDocumentAction extends DispatchAction {

	private HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
	private HRMDocumentToDemographicDao hrmDocumentToDemographicDao = (HRMDocumentToDemographicDao) SpringUtils.getBean("HRMDocumentToDemographicDao");
	private HRMDocumentToProviderDao hrmDocumentToProviderDao = (HRMDocumentToProviderDao) SpringUtils.getBean("HRMDocumentToProviderDao");
	private HRMDocumentSubClassDao hrmDocumentSubClassDao = (HRMDocumentSubClassDao) SpringUtils.getBean("HRMDocumentSubClassDao");
	private HRMDocumentCommentDao hrmDocumentCommentDao = (HRMDocumentCommentDao) SpringUtils.getBean("HRMDocumentCommentDao");
	private ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);

	private static HRMCategoryService categoryService = SpringUtils.getBean(HRMCategoryService.class);
	private static HRMDocumentService hrmDocumentService = SpringUtils.getBean(HRMDocumentService.class);
	private static HRMSubClassService subClassService = SpringUtils.getBean(HRMSubClassService.class);

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	 
	public ActionForward undefined(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String method = request.getParameter("method");

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_UPDATE);

		if (method != null) {
			if (method.equalsIgnoreCase("makeIndependent"))
				return makeIndependent(mapping, form, request, response);
			else if (method.equalsIgnoreCase("signOff"))
				return signOff(mapping, form, request, response);
			else if (method.equalsIgnoreCase("assignProvider"))
				return assignProvider(mapping, form, request, response);
			else if (method.equalsIgnoreCase("removeDemographic"))
				return removeDemographic(mapping, form, request, response);
			else if (method.equalsIgnoreCase("assignDemographic"))
				return assignDemographic(mapping, form, request, response);
			else if (method.equalsIgnoreCase("makeActiveSubClass"))
				return makeActiveSubClass(mapping, form, request, response);
			else if (method.equalsIgnoreCase("removeProvider"))
				return removeProvider(mapping, form, request, response);
			else if (method.equalsIgnoreCase("addComment"))
				return addComment(mapping, form, request, response);
			else if (method.equalsIgnoreCase("deleteComment"))
				return deleteComment(mapping, form, request, response);
			else if (method.equalsIgnoreCase("setDescription"))
				return setDescription(mapping, form, request, response);
			else if (method.equalsIgnoreCase("recategorize"))
				return recategorize(mapping, form, request, response);
			else if (method.equalsIgnoreCase("recategorizeFuture"))
				return recategorizeFuture(mapping, form, request, response);
		}

		return mapping.findForward("ajax");
	}

	public ActionForward makeIndependent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_UPDATE);
		
		try {
			String reportId = request.getParameter("reportId");

			HRMDocument document = hrmDocumentDao.find(Integer.parseInt(reportId));
			if (document.getParentReport() != null) {
				document.setParentReport(null);
				hrmDocumentDao.merge(document);
			} else {
				// This is the parent document; choose the closest one 
				List<HRMDocument> documentChildren = hrmDocumentDao.getAllChildrenOf(document.getId());
				if (documentChildren != null && documentChildren.size() > 0) {
					HRMDocument newParentDoc = documentChildren.get(0);
					newParentDoc.setParentReport(null);
					hrmDocumentDao.merge(newParentDoc);
					for (HRMDocument childDoc : documentChildren) {
						if (childDoc.getId().intValue() != newParentDoc.getId().intValue()) {
							childDoc.setParentReport(newParentDoc.getId());
							hrmDocumentDao.merge(childDoc);
						}
					}
				}
			}

			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to set make document independent but failed.", e); 
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}

	public ActionForward signOff(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(providerNo, Permission.HRM_UPDATE);

		try {
			String reportId = request.getParameter("reportId");
			String signedOff = request.getParameter("signedOff");

			HRMDocumentToProvider providerMapping = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(Integer.parseInt(reportId), providerNo);
			if(providerMapping == null) {
				// No longer required for Juno data, but keeping this in for now in case removing it does something
				// weird to input data.

				// Check for unclaimed record, if that exists..update that one
				providerMapping = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(Integer.parseInt(reportId), "-1");
				if(providerMapping != null) {
					providerMapping.setProviderNo(providerNo);
				}
			}

			if (providerMapping != null) {
				providerMapping.setSignedOff(Integer.parseInt(signedOff) == 1);
				providerMapping.setSignedOffTimestamp(new Date());
				hrmDocumentToProviderDao.merge(providerMapping);
			}
			else
			{
				HRMDocumentToProvider hrmDocumentToProvider=new HRMDocumentToProvider();
				hrmDocumentToProvider.setHrmDocument(hrmDocumentDao.find(Integer.parseInt(reportId)));
				hrmDocumentToProvider.setProviderNo(providerNo);
				hrmDocumentToProvider.setSignedOff(Integer.parseInt(signedOff) == 1);
				hrmDocumentToProvider.setSignedOffTimestamp(new Date());
				hrmDocumentToProviderDao.persist(hrmDocumentToProvider);

				// Once the provider has signed off for the first time, try to file the report in their inbox.
				CommonLabResultData.updateReportStatus(Integer.parseInt(reportId), providerNo, ProviderInboxItem.FILE, "Signed Off", InboxManager.INBOX_TYPE_HRM);
			}

			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to set signed off status on document but failed.", e); 
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}

	public ActionForward assignProvider(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.HRM_UPDATE);

		try {
			String assignee = request.getParameter("providerNo");
			Integer hrmDocumentId = Integer.parseInt(request.getParameter("reportId"));
			HRMDocumentToProvider existing = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(hrmDocumentId, assignee);

			if (existing == null)
			{
				HRMDocumentToProvider providerMapping = new HRMDocumentToProvider();

				providerMapping.setHrmDocument(hrmDocumentDao.find(hrmDocumentId));
				providerMapping.setProviderNo(assignee);
				providerMapping.setSignedOff(false);

				hrmDocumentToProviderDao.merge(providerMapping);

				// Pretty sure this is no longer required...  Keeping it in for now in case removing it does something
				// weird to import data.

				//we want to remove any unmatched entries when we do a manual match like this. -1 means unclaimed in this table.
				HRMDocumentToProvider existingUnmatched = hrmDocumentToProviderDao
					.findByHrmDocumentIdAndProviderNo(hrmDocumentId, "-1");
				if (existingUnmatched != null)
				{
					hrmDocumentToProviderDao.remove(existingUnmatched.getId());
				}

				request.setAttribute("success", true);
			}
			else
			{
				request.setAttribute("success", false);
			}
		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to assign HRM document to provider but failed.", e); 
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}

	public ActionForward removeDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_UPDATE);

		try {
			String hrmDocumentId = request.getParameter("reportId");
			List<HRMDocumentToDemographic> currentMappingList = hrmDocumentToDemographicDao.findByHrmDocumentId(Integer.parseInt(hrmDocumentId));

			if (currentMappingList != null) {
				for (HRMDocumentToDemographic currentMapping : currentMappingList) {
					hrmDocumentToDemographicDao.remove(currentMapping.getId());
				}
			}

			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to remove HRM document from demographic but failed.", e); 
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");

	}

	public ActionForward assignDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String hrmDocumentId = request.getParameter("reportId");
		String demographicNo = request.getParameter("demographicNo");

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Integer.valueOf(demographicNo), Permission.HRM_UPDATE);

		try {
			try {
				List<HRMDocumentToDemographic> currentMappingList = hrmDocumentToDemographicDao.findByHrmDocumentId(Integer.parseInt(hrmDocumentId));

				if (currentMappingList != null) {
					for (HRMDocumentToDemographic currentMapping : currentMappingList) {
						hrmDocumentToDemographicDao.remove(currentMapping);
					}
				}
			} catch (Exception e) {
				// Do nothing
			}

			HRMDocumentToDemographic demographicMapping = new HRMDocumentToDemographic();

			demographicMapping.setHrmDocumentId(Integer.parseInt(hrmDocumentId));
			demographicMapping.setDemographicNo(Integer.parseInt(demographicNo));
			demographicMapping.setTimeAssigned(new Date());

			hrmDocumentToDemographicDao.merge(demographicMapping);

			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to assign HRM document to demographic but failed.", e); 
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}

	public ActionForward makeActiveSubClass(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_UPDATE);
		
		try {
			String hrmDocumentId = request.getParameter("reportId");
			String subClassId = request.getParameter("subClassId");
			hrmDocumentSubClassDao.setAllSubClassesForDocumentAsInactive(Integer.parseInt(hrmDocumentId));

			HRMObservation newActiveSubClass = hrmDocumentSubClassDao.find(Integer.parseInt(subClassId));
			if (newActiveSubClass != null) {
				newActiveSubClass.setActive(true);
				hrmDocumentSubClassDao.merge(newActiveSubClass);
			}

			request.setAttribute("success", true);

		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to change active subclass but failed.", e); 
			request.setAttribute("success", false);
		}



		return mapping.findForward("ajax");
	}

	public ActionForward removeProvider(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_UPDATE);
		
		try {
			String providerMappingId = request.getParameter("providerMappingId");

			hrmDocumentToProviderDao.remove(Integer.parseInt(providerMappingId));

			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Tried to remove provider from HRM document but failed.", e); 
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}


	public ActionForward addComment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.HRM_UPDATE);
		
		try {
			String documentId = request.getParameter("reportId");
			String commentString = request.getParameter("comment");

			HRMDocumentComment comment = new HRMDocumentComment();

			comment.setHrmDocument(hrmDocumentDao.find(Integer.parseInt(documentId)));
			comment.setComment(commentString);
			comment.setCommentTime(new Date());
			comment.setProvider(providerDataDao.find(loggedInInfo.getLoggedInProviderNo()));

			hrmDocumentCommentDao.merge(comment);
			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Couldn't add a comment for HRM document", e);
			request.setAttribute("success", false);
		}
		
		return mapping.findForward("ajax");
	}
	
	public ActionForward deleteComment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.HRM_UPDATE);
		
		try {
			String commentId = request.getParameter("commentId");
			hrmDocumentCommentDao.deleteComment(Integer.parseInt(commentId));
			request.setAttribute("success", true);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Couldn't delete comment on HRM document", e);
			request.setAttribute("success", false);
		}
		
		return mapping.findForward("ajax");
	}

	public ActionForward setDescription(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.HRM_UPDATE);

		try
		{
			String documentId = request.getParameter("reportId");
			String descriptionString = request.getParameter("description");

			boolean updated = false;
			HRMDocument document = hrmDocumentDao.find(Integer.parseInt(documentId));
			if (document != null)
			{
				document.setDescription(descriptionString);
				hrmDocumentDao.merge(document);
				updated = true;
			}
			request.setAttribute("success", updated);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Couldn't set description for HRM document", e);
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}

	public ActionForward recategorize(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.HRM_UPDATE);

		final Integer UNCATEGORIZED = -1;

		try
		{
			Integer documentId = Integer.parseInt(request.getParameter("documentId"));
			Integer categoryId = Integer.parseInt(request.getParameter("categoryId"));

			HrmDocument documentModel = hrmDocumentService.getHrmDocument(documentId);

			if (categoryId.equals(UNCATEGORIZED))
			{
				documentModel.setCategory(null);
			}
			else
			{
				HrmCategoryModel categoryModel = categoryService.getActiveCategory(categoryId);
				if (categoryModel != null)
				{
					documentModel.setCategory(categoryModel);
				}
			}

			hrmDocumentService.updateHrmDocument(documentModel);
			request.setAttribute("success", true);
		}
		catch (Exception e)
		{
			request.setAttribute("success", false);
		}

		return mapping.findForward("ajax");
	}

	/**
	 * Reclassify future HRM documents which are similar to the specified document
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward recategorizeFuture(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.HRM_UPDATE);

		final Integer UNCATEGORIZED = -1;

		Integer documentId = Integer.parseInt(request.getParameter("documentId"));
		Integer categoryId = Integer.parseInt(request.getParameter("categoryId"));

		if (categoryId.equals(UNCATEGORIZED))
		{
			request.setAttribute("success", false);
			return mapping.findForward("ajax");
		}

		HRMDocumentService hrmDocumentService = SpringUtils.getBean(HRMDocumentService.class);
		HrmDocument documentModel = hrmDocumentService.getHrmDocument(documentId);

		String facilityId = documentModel.getSendingFacilityId();
		String reportClass = documentModel.getReportClass().getValue();
		String subClassName = null;
		String accompanyingSubClass = null;

		if (documentModel.getReportClass().equals(ReportClass.MEDICAL_RECORDS))
		{
			subClassName = documentModel.getReportSubClass();
		}
		else if (documentModel.getFirstObservation().isPresent())
		{
			HrmObservation firstObservation = documentModel.getFirstObservation().get();
			accompanyingSubClass = firstObservation.getAccompanyingSubClass();
		}

		HrmSubClassModel existingSubClass = subClassService
			.findActiveByAttributes(facilityId, reportClass, subClassName, accompanyingSubClass);

		if (existingSubClass != null)
		{
			subClassService.deactivateSubClass(existingSubClass.getId());
		}

		HrmSubClassModel subClassToAdd = new HrmSubClassModel();
		subClassToAdd.setFacilityNumber(facilityId);
		subClassToAdd.setClassName(reportClass);
		subClassToAdd.setSubClassName(subClassName);
		subClassToAdd.setAccompanyingSubClassName(accompanyingSubClass);

		HrmCategoryModel category = categoryService.getActiveCategory(categoryId);
		category.getSubClasses().add(subClassToAdd);
		categoryService.updateCategory(category);
		request.setAttribute("success", true);

		return mapping.findForward("ajax");
	}
}