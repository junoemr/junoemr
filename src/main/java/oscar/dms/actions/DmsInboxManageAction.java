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


package oscar.dms.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.security.model.SecUserRole;
import org.oscarehr.PMmodule.utility.UtilDateUtilities;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.QueueDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.Queue;
import org.oscarehr.common.model.QueueDocumentLink;
import org.oscarehr.inbox.InboxManagerResponse;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.inbox.service.InboxManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.oscarMDS.data.CategoryData;
import oscar.oscarMDS.data.PatientInfo;
import oscar.util.OscarRoleObjectPrivilege;

import com.quatro.dao.security.SecObjectNameDao;
import com.quatro.model.security.Secobjectname;

public class DmsInboxManageAction extends DispatchAction {

	private static Logger logger=MiscUtils.getLogger();

	private InboxManager inboxManager = (InboxManager) SpringUtils.getBean(InboxManager.class);

	private ProviderInboxRoutingDao providerInboxRoutingDAO = null;
	private QueueDocumentLinkDao queueDocumentLinkDAO = null;
	private SecObjectNameDao secObjectNameDao = null;
	private SecUserRoleDao secUserRoleDao = (SecUserRoleDao) SpringUtils.getBean("secUserRoleDao");
	private QueueDao queueDAO = (QueueDao) SpringUtils.getBean("queueDao");

	public void setProviderInboxRoutingDAO(ProviderInboxRoutingDao providerInboxRoutingDAO) {
		this.providerInboxRoutingDAO = providerInboxRoutingDAO;
	}

	public void setQueueDocumentLinkDAO(QueueDocumentLinkDao queueDocumentLinkDAO) {
		this.queueDocumentLinkDAO = queueDocumentLinkDAO;
	}

	public void setSecObjectNameDao(SecObjectNameDao secObjectNameDao) {
		this.secObjectNameDao = secObjectNameDao;
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		return null;
	}


	private void addQueueSecObjectName(String queuename, String queueid) {
		String q = "_queue.";
		if (queuename != null && queueid != null) {
			q += queueid;
			Secobjectname sbn = new Secobjectname();
			sbn.setObjectname(q);
			sbn.setDescription(queuename);
			sbn.setOrgapplicable(0);
			secObjectNameDao.saveOrUpdate(sbn);
		}
	}

	private boolean isSegmentIDUnique(ArrayList<LabResultData> doclabs, LabResultData data) {
		boolean unique = true;
		String sID = (data.segmentID).trim();
		for (int i = 0; i < doclabs.size(); i++) {
			LabResultData lrd = doclabs.get(i);
			if (sID.equals((lrd.segmentID).trim())) {
				unique = false;
				break;
			}
		}
		return unique;
	}

	public ActionForward previewPatientDocLab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String demographicNo = request.getParameter("demog");
		String docs = request.getParameter("docs");
		String labs = request.getParameter("labs");
		String providerNo = request.getParameter("providerNo");
		String searchProviderNo = request.getParameter("searchProviderNo");
		String ackStatus = request.getParameter("ackStatus");
		ArrayList<EDoc> docPreview = new ArrayList<EDoc>();
		ArrayList<LabResultData> labPreview = new ArrayList<LabResultData>();

		if (docs.length() == 0) {
			// do nothing
		} else {
			String[] did = docs.split(",");
			List<String> didList = new ArrayList<String>();
			for (int i = 0; i < did.length; i++) {
				if (did[i].length() > 0) {
					didList.add(did[i]);
				}
			}
			if (didList.size() > 0) docPreview = EDocUtil.listDocsPreviewInbox(didList);

		}

		if (labs.length() == 0) {
			// do nothing
		} else {
			String[] labids = labs.split(",");
			List<String> ls = new ArrayList<String>();
			for (int i = 0; i < labids.length; i++) {
				if (labids.length > 0) ls.add(labids[i]);
			}

			if (ls.size() > 0) labPreview = Hl7textResultsData.getNotAckLabsFromLabNos(ls);
		}

		request.setAttribute("docPreview", docPreview);
		request.setAttribute("labPreview", labPreview);
		request.setAttribute("providerNo", providerNo);
		request.setAttribute("searchProviderNo", searchProviderNo);
		request.setAttribute("ackStatus", ackStatus);
		DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
		Demographic demographic = demographicManager.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demographicNo);
		String demoName = "Not,Assigned";
		if (demographic != null) demoName = demographic.getFirstName() + "," + demographic.getLastName();
		request.setAttribute("demoName", demoName);
		return mapping.findForward("doclabPreview");
	}

	public ActionForward prepareForIndexPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		try {
			if (session.getAttribute("userrole") == null)
				response.sendRedirect("../logout.jsp");
		} catch (Exception e) {
			MiscUtils.getLogger().error("error",e);
		}

		String providerNo = (String) session.getAttribute("user");
		String searchProviderNo = request.getParameter("searchProviderNo");
		Boolean searchAll = request.getParameter("searchProviderAll") != null;
		String status = request.getParameter("status");

		boolean providerSearch = !"-1".equals(searchProviderNo);

		if (status == null) {
			status = "N";
		} // default to new labs only
		else if ("-1".equals(status)) {
			status = "";
		}
		if (providerNo == null) {
			providerNo = "";
		}

		if( searchAll ) {
			searchProviderNo = request.getParameter("searchProviderAll");
		}
		else if (searchProviderNo == null) {
			searchProviderNo = providerNo;
		} // default to current provider
		MiscUtils.getLogger().debug("SEARCH " + searchProviderNo);
		String patientFirstName = request.getParameter("fname");
		String patientLastName = request.getParameter("lname");
		String patientHealthNumber = request.getParameter("hnum");
		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		String ajax = request.getParameter("ajax");
		Boolean isAjax = (ajax != null && ajax.equals("true"));

		Date startDate = null;
		Date endDate = null;

		try {
			startDate = UtilDateUtilities.StringToDate(startDateStr);
			endDate = UtilDateUtilities.StringToDate(endDateStr);
		} catch (Exception e) {
			startDate = null;
			endDate = null;
		}

		if (patientFirstName == null) {
			patientFirstName = "";
		}
		if (patientLastName == null) {
			patientLastName = "";
		}
		if (patientHealthNumber == null) {
			patientHealthNumber = "";
		}
		boolean patientSearch = !"".equals(patientFirstName) || !"".equals(patientLastName)
				|| !"".equals(patientHealthNumber);
		try {

			if(isAjax)
			{
				CategoryData cData = new CategoryData(patientLastName, patientFirstName, patientHealthNumber,
						patientSearch, providerSearch, searchProviderNo, status, startDate, endDate);
				cData.populateCountsAndPatients();
				MiscUtils.getLogger().debug("LABS " + cData.getTotalLabs());
				request.setAttribute("patients", new ArrayList<PatientInfo>(cData.getPatients().values()));
				request.setAttribute("unmatchedDocs", cData.getUnmatchedDocs());
				request.setAttribute("unmatchedLabs", cData.getUnmatchedLabs());
				request.setAttribute("totalDocs", cData.getTotalDocs());
				request.setAttribute("totalLabs", cData.getTotalLabs());
				request.setAttribute("abnormalCount", cData.getAbnormalCount());
				request.setAttribute("normalCount", cData.getNormalCount());
				request.setAttribute("totalNumDocs", cData.getTotalNumDocs());
				request.setAttribute("categoryHash", cData.getCategoryHash());
			}
			request.setAttribute("patientFirstName", patientFirstName);
			request.setAttribute("patientLastName", patientLastName);
			request.setAttribute("patientHealthNumber", patientHealthNumber);
			request.setAttribute("providerNo", providerNo);
			request.setAttribute("searchProviderNo", searchProviderNo);
			request.setAttribute("ackStatus", status);
			request.setAttribute("startDate", startDateStr);
			request.setAttribute("endDate", endDateStr);
			return mapping.findForward("dms_index");
		} catch (SQLException e) {
			return mapping.findForward("error");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionForward prepareForContentPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		HttpSession session = request.getSession();

		try
		{
			if (session.getAttribute("userrole") == null) response.sendRedirect("../logout.jsp");
		}
		catch (Exception e)
		{
			logger.error("Error", e);
		}

		String providerNo = (String) session.getAttribute("user");
		String searchProviderNo = request.getParameter("searchProviderNo");
		String ackStatus = request.getParameter("status");
		String demographicNo = request.getParameter("demographicNo");
		String patientFirstName = request.getParameter("fname");
		String patientLastName = request.getParameter("lname");
		String patientHealthNumber = request.getParameter("hnum");


		Integer page;
		try
		{
			page = Integer.parseInt(request.getParameter("page"));
		}
		catch (NumberFormatException nfe)
		{
			page = 0;
		}

		Integer pageSize;
		try
		{
			String tmp = request.getParameter("pageSize");
			pageSize = Integer.parseInt(tmp);
		}
		catch (NumberFormatException nfe)
		{
			pageSize = 40;
		}

		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		String view = request.getParameter("view");

		Date startDate;
		Date endDate;
		try
		{
			startDate = UtilDateUtilities.StringToDate(startDateStr);
			endDate = UtilDateUtilities.StringToDate(endDateStr);
		}
		catch (Exception e)
		{
			logger.error("Error parsing dates: ", e);
			startDate = null;
			endDate = null;
		}

		InboxManagerResponse inboxManagerResponse = inboxManager.getInboxResults(
				loggedInInfo,
				view,
				providerNo,
				searchProviderNo,
				demographicNo,
				patientFirstName,
				patientLastName,
				patientHealthNumber,
				ackStatus,
				page,
				pageSize,
				startDate,
				endDate);

		// set attributes
		request.setAttribute("pageSize", pageSize);
		request.setAttribute("pageNum", inboxManagerResponse.getPageNum());
		request.setAttribute("providerNo", inboxManagerResponse.getProviderNo());
		request.setAttribute("searchProviderNo", inboxManagerResponse.getSearchProviderNo());
		request.setAttribute("demographicNo", inboxManagerResponse.getDemographicNo());
		request.setAttribute("ackStatus", inboxManagerResponse.getAckStatus());
		request.setAttribute("labdocs", inboxManagerResponse.getLabdocs());
		request.setAttribute("totalNumDocs", inboxManagerResponse.getLabdocs().size());

		return mapping.findForward("dms_page");
	}

	public ActionForward addNewQueue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		boolean success = false;
		try {
			String qn = request.getParameter("newQueueName");
			qn = qn.trim();
			if (qn != null && qn.length() > 0) {
				QueueDao queueDao = (QueueDao) SpringUtils.getBean("queueDao");
				success = queueDao.addNewQueue(qn);
				addQueueSecObjectName(qn, queueDao.getLastId());
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}

		HashMap<String, Boolean> hm = new HashMap<String, Boolean>();
		hm.put("addNewQueue", success);
		JSONObject jsonObject = JSONObject.fromObject(hm);
		try {
			response.getOutputStream().write(jsonObject.toString().getBytes());
		} catch (java.io.IOException ioe) {
			logger.error("Error", ioe);
		}
		return null;
	}

         public ActionForward isDocumentLinkedToDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
                boolean success = false;
                String demoId = null;
                try {
                       String docId = request.getParameter("docId");
                       logger.debug("DocId:"+docId);
                       if (docId != null) {
                            docId = docId.trim();
                            if (docId.length() > 0) {
                                EDoc doc = EDocUtil.getDoc(docId);
                                demoId = doc.getModuleId();                                

                                if (demoId != null) {
                                    logger.debug("DemoId:"+demoId);
                                    Integer demographicId = Integer.parseInt(demoId);
                                    if (demographicId > 0) {
                                        logger.debug("Success true");
                                        success = true;
                                    }
                                }
                            }
                        }
                } catch (Exception e) {
                    logger.error("Error", e);
                }

                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("isLinkedToDemographic", success);
                hm.put("demoId", demoId);
                JSONObject jsonObject = JSONObject.fromObject(hm);
                try {
                    response.getOutputStream().write(jsonObject.toString().getBytes());
                } catch (java.io.IOException ioe) {
                    logger.error("Error", ioe);
                }

                return null;
	}
         
	public ActionForward isLabLinkedToDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		boolean success = false;
		String demoId = null;
		try {
			String qn = request.getParameter("labid");
			if (qn != null) {
				qn = qn.trim();
				if (qn.length() > 0) {
					CommonLabResultData c = new CommonLabResultData();
					demoId = c.getDemographicNo(qn, InboxManager.INBOX_TYPE_HL7);
					if (demoId != null && !demoId.equals("0")) success = true;
				}
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}

		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("isLinkedToDemographic", success);
		hm.put("demoId", demoId);
		JSONObject jsonObject = JSONObject.fromObject(hm);
		try {
			response.getOutputStream().write(jsonObject.toString().getBytes());
		} catch (java.io.IOException ioe) {
			logger.error("Error", ioe);
		}
		return null;
	}

	public ActionForward updateDocStatusInQueue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String docid = request.getParameter("docid");
		if (docid != null) {
			if (!queueDocumentLinkDAO.setStatusInactive(Integer.parseInt(docid))) {
				logger.error("failed to set status in queue document link to be inactive");
			}
		}
		return null;
	}

	// return a hastable containing queue id to queue name, a hashtable of queue id and a list of document nos.
	// forward to documentInQueus.jsp
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionForward getDocumentsInQueues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		try {
			if (session.getAttribute("userrole") == null) response.sendRedirect("../logout.jsp");
		} catch (Exception e) {
			logger.error("Error", e);
		}
		String providerNo = (String) session.getAttribute("user");
		String searchProviderNo = request.getParameter("searchProviderNo");
		String ackStatus = request.getParameter("status");

		if (ackStatus == null) {
			ackStatus = "N";
		} // default to new labs only
		if (providerNo == null) {
			providerNo = "";
		}
		if (searchProviderNo == null) {
			searchProviderNo = providerNo;
		}

		StringBuilder roleName = new StringBuilder();
		List<SecUserRole> roles = secUserRoleDao.getUserRoles(searchProviderNo);
		for (SecUserRole r : roles) {
			if (roleName.length() != 0) {
				roleName.append(',');
			}
			roleName.append(r.getRoleName());
		}
		roleName.append("," + searchProviderNo);

		String patientIdNamesStr = "";
		List<QueueDocumentLink> qs = queueDocumentLinkDAO.getActiveQueueDocLink();
		HashMap<Integer, List<Integer>> queueDocNos = new HashMap<Integer, List<Integer>>();
		HashMap<Integer, String> docType = new HashMap<Integer, String>();
		HashMap<Integer, List<Integer>> patientDocs = new HashMap<Integer, List<Integer>>();
		DocumentDao documentDao = (DocumentDao) SpringUtils.getBean("documentDao");
		Demographic demo = new Demographic();
		List<Integer> docsWithPatient = new ArrayList<Integer>();
		HashMap<Integer, String> patientIdNames = new HashMap<Integer, String>();// lbData.patientName = demo.getLastName()+ ", "+demo.getFirstName();
		List<Integer> patientIds = new ArrayList<Integer>();
		Integer demoNo;
		HashMap<Integer, String> docStatus = new HashMap<Integer, String>();
		String patientIdStr = "";
		StringBuilder patientIdBud = new StringBuilder();
		HashMap<String, List<Integer>> typeDocLab = new HashMap<String, List<Integer>>();
		List<Integer> ListDocIds = new ArrayList<Integer>();
		for (QueueDocumentLink q : qs) {
			int qid = q.getQueueId();
			List<Object> vec = OscarRoleObjectPrivilege.getPrivilegeProp("_queue." + qid);
			// if queue is not default and provider doesn't have access to it,continue
			if (qid != Queue.DEFAULT_QUEUE_ID && !OscarRoleObjectPrivilege.checkPrivilege(roleName.toString(), (Properties) vec.get(0), (List) vec.get(1))) {
				continue;
			}
			int docid = q.getDocId();
			ListDocIds.add(docid);
			docType.put(docid, InboxManager.INBOX_TYPE_DOCUMENT);
			demo = documentDao.getDemoFromDocNo(Integer.toString(docid));
			if (demo == null) demoNo = -1;
			else demoNo = demo.getDemographicNo();
			if (!patientIds.contains(demoNo)) patientIds.add(demoNo);
			if (!patientIdNames.containsKey(demoNo)) {
				if (demoNo == -1) {
					patientIdNames.put(demoNo, "Not, Assigned");
					patientIdNamesStr += ";" + demoNo + "=" + "Not, Assigned";
				} else {
					patientIdNames.put(demoNo, demo.getLastName() + ", " + demo.getFirstName());
					patientIdNamesStr += ";" + demoNo + "=" + demo.getLastName() + ", " + demo.getFirstName();
				}

			}

			List<ProviderInboxItem> providers =
					providerInboxRoutingDAO.getProvidersWithRoutingForDocument(
							InboxManager.INBOX_TYPE_DOCUMENT, docid);

			if (providers.size() > 0) {
				ProviderInboxItem pii = providers.get(0);
				docStatus.put(docid, pii.getStatus());
			}
			if (patientDocs.containsKey(demoNo)) {
				docsWithPatient = patientDocs.get(demoNo);
				docsWithPatient.add(docid);
				patientDocs.put(demoNo, docsWithPatient);
			} else {
				docsWithPatient = new ArrayList<Integer>();
				docsWithPatient.add(docid);
				patientDocs.put(demoNo, docsWithPatient);
			}
			if (queueDocNos.containsKey(qid)) {

				List<Integer> ds = queueDocNos.get(qid);
				ds.add(docid);
				queueDocNos.put(qid, ds);

			} else {
				List<Integer> ds = new ArrayList<Integer>();
				ds.add(docid);
				queueDocNos.put(qid, ds);
			}
		}
		Integer dn = 0;
		for (int i = 0; i < patientIds.size(); i++) {
			dn = patientIds.get(i);
			patientIdBud.append(dn);
			if (i != patientIds.size() - 1) patientIdBud.append(",");
		}
		patientIdStr = patientIdBud.toString();
		typeDocLab.put(InboxManager.INBOX_TYPE_DOCUMENT, ListDocIds);
		List<Integer> normals = ListDocIds;// assume all documents are normal
		List<Integer> abnormals = new ArrayList<Integer>();
		request.setAttribute("typeDocLab", typeDocLab);
		request.setAttribute("docStatus", docStatus);
		request.setAttribute("patientDocs", patientDocs);
		request.setAttribute("patientIdNames", patientIdNames);
		request.setAttribute("docType", docType);
		request.setAttribute("patientIds", patientIds);
		request.setAttribute("patientIdStr", patientIdStr);
		request.setAttribute("normals", normals);
		request.setAttribute("abnormals", abnormals);
		request.setAttribute("queueDocNos", queueDocNos);
		request.setAttribute("patientIdNamesStr", patientIdNamesStr);
		request.setAttribute("queueIdNames", queueDAO.getHashMapOfQueues());
		request.setAttribute("providerNo", providerNo);
		request.setAttribute("searchProviderNo", searchProviderNo);
		return mapping.findForward("document_in_queues");

	}
}
