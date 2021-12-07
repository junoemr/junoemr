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


package oscar.oscarLab.ca.on;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.caisi_integrator.IntegratorFallBackManager;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.billing.CA.BC.dao.Hl7MshDao;
import org.oscarehr.caisi_integrator.ws.CachedDemographicLabResult;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.common.dao.DocumentResultsDao;
import org.oscarehr.common.dao.Hl7TextMessageDao;
import org.oscarehr.common.dao.LabPatientPhysicianInfoDao;
import org.oscarehr.common.dao.MdsMSHDao;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.common.model.QueueDocumentLink;
import org.oscarehr.document.dao.CtlDocumentDao;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.inbox.service.InboxManager;
import org.oscarehr.labs.LabIdAndType;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import oscar.OscarProperties;
import oscar.oscarDB.ArchiveDeletedRecords;
import oscar.oscarDB.DBPreparedHandler;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.all.upload.ProviderLabRouting;
import oscar.oscarLab.ca.bc.PathNet.PathnetResultsData;
import oscar.oscarMDS.data.MDSResultsData;
import oscar.oscarMDS.data.ReportStatus;
import oscar.util.ConversionUtils;

public class CommonLabResultData {

	private static Logger logger = MiscUtils.getLogger();

	public static final boolean ATTACHED = true;
	public static final boolean UNATTACHED = false;

	public static final String NOT_ASSIGNED_PROVIDER_NO = "0";
	
	private static final PatientLabRoutingDao patientLabRoutingDao = SpringUtils.getBean(PatientLabRoutingDao.class);
	private static final ProviderLabRoutingDao providerLabRoutingDao = SpringUtils.getBean(ProviderLabRoutingDao.class);
	private static final QueueDocumentLinkDao queueDocumentLinkDao = SpringUtils.getBean(QueueDocumentLinkDao.class);
	private static final InboxManager inboxManager = SpringUtils.getBean(InboxManager.class);
	
	public CommonLabResultData() {

	}

	public static String[] getLabTypes() {
		return new String[] { "MDS", "CML", "BCP", "HL7", "DOC", "Epsilon", "HRM" };
	}

	//Populate Lab data for consultation request
	public ArrayList<LabResultData> populateConsultLabResultsData(LoggedInInfo loggedInInfo, String demographicNo, String reqId, boolean attach) {
		return populateConsultLabResultsData(loggedInInfo, demographicNo, reqId, attach, false);
	}
	
	//Populate Lab data for consultation response
	public ArrayList<LabResultData> populateLabResultsDataConsultResponse(LoggedInInfo loggedInInfo, String demographicNo, String respId, boolean attach) {
		return populateConsultLabResultsData(loggedInInfo, demographicNo, respId, attach, true);
	}
	
	//Populate Lab data for consultation (private shared method)
	private ArrayList<LabResultData> populateConsultLabResultsData(LoggedInInfo loggedInInfo, String demographicNo, String consultId, boolean attach, boolean isConsultResponse) {
		ArrayList<LabResultData> hl7Labs = isConsultResponse ?
				Hl7textResultsData.populateHL7ResultsDataConsultResponse(demographicNo, consultId, attach) :
				Hl7textResultsData.populateHL7ResultsData(demographicNo, consultId, attach);

		Collections.sort(hl7Labs);
		return hl7Labs;
	}

	public ArrayList<LabResultData> populateLabResultsData(LoggedInInfo loggedInInfo, String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber, String status)
	{
		ArrayList<LabResultData> labs = populateLabsData(
			loggedInInfo, providerNo, demographicNo, patientFirstName, patientLastName, patientHealthNumber, status, "I");
		labs.addAll(populateDocumentData(providerNo, demographicNo, status));

		return labs;
	}

	// return documents specific to this provider only, doesn't include documents that are not linked to any provider
	public ArrayList<LabResultData> populateDocumentDataSpecificProvider(String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber, String status, String scannedDocStatus) {
		ArrayList<LabResultData> labs = new ArrayList<LabResultData>();
		if (scannedDocStatus != null && (scannedDocStatus.equals("O") || scannedDocStatus.equals("I") || scannedDocStatus.equals(""))) {
			DocumentResultsDao documentResultsDao = (DocumentResultsDao) SpringUtils.getBean("documentResultsDao");
			ArrayList<LabResultData> docs = documentResultsDao.populateDocumentResultsDataLinkToProvider(providerNo, demographicNo, status);
			return docs;
		}
		return labs;
	}

	public ArrayList<LabResultData> populateDocumentData(String providerNo, String demographicNo, String status) {
		DocumentResultsDao documentResultsDao = (DocumentResultsDao) SpringUtils.getBean("documentResultsDao");
		ArrayList<LabResultData> docs = documentResultsDao.populateDocumentResultsData(providerNo, demographicNo, status);
		return docs;
	}

	public ArrayList<LabResultData> populateLabsData(LoggedInInfo loggedInInfo, String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber, String status, String scannedDocStatus) {

		ArrayList<LabResultData> result = new ArrayList<>();
		List<Integer> demographicIds = new ArrayList<>();

		DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);

		demographicIds.add(ConversionUtils.fromIntString(demographicNo));
		demographicIds.addAll(demographicManager.getMergedDemographicIds(loggedInInfo, ConversionUtils.fromIntString(demographicNo)));
		
		for(Integer demographicId : demographicIds)
		{
			if (scannedDocStatus != null && (scannedDocStatus.equals("N") || scannedDocStatus.equals("I") || scannedDocStatus.equals("")))
			{
				ArrayList<LabResultData> hl7Labs = Hl7textResultsData.populateHl7ResultsData(
					providerNo, demographicId.toString(), patientFirstName, patientLastName, patientHealthNumber, status,null);
				result.addAll(hl7Labs);
			}
		}
		return result;
	}

	public static void updateReportStatus(int labNo, String providerNo, String status, String comment, String labType) throws SQLException
	{
		try {
			DBPreparedHandler db = new DBPreparedHandler();
			// handles the case where this provider/lab combination is not already in providerLabRouting table
			String sql = "select id, status from providerLabRouting where lab_type = '" + labType + "' and provider_no = '" + providerNo + "' and lab_no = '" + labNo + "'";

			ResultSet rs = db.queryResults(sql);
			boolean empty = true;
			while (rs.next()) {
				empty = false;
				String id = oscar.Misc.getString(rs, "id");
				if (!oscar.Misc.getString(rs, "status").equals(ProviderInboxItem.ACK)) {
					ProviderLabRoutingModel plr  = providerLabRoutingDao.find(Integer.parseInt(id));
					if(plr != null) {
						plr.setStatus(""+status);
						//we don't want to clobber existing comments when filing labs
						if( !status.equals(ProviderInboxItem.FILE) ) {
							plr.setComment(comment);
						}
						plr.setTimestamp(new Date());
						providerLabRoutingDao.merge(plr);
					}
				}
			} 
			if (empty) {
				ProviderLabRoutingModel p = new ProviderLabRoutingModel();
				p.setProviderNo(providerNo);
				p.setLabNo(labNo);
				p.setStatus(String.valueOf(status));
				p.setComment(comment);
				p.setLabType(labType);
				p.setTimestamp(new Date());
				providerLabRoutingDao.persist(p);
			}

			if (!NOT_ASSIGNED_PROVIDER_NO.equals(providerNo)) {
				List<ProviderLabRoutingModel> modelRecords = providerLabRoutingDao.findByLabNoAndLabTypeAndProviderNo(labNo, labType, providerNo);
				ArchiveDeletedRecords adr = new ArchiveDeletedRecords();
				adr.recordRowsToBeDeleted(modelRecords, "" + providerNo, "providerLabRouting");
				
				for(ProviderLabRoutingModel plr : providerLabRoutingDao.findByLabNoAndLabTypeAndProviderNo(labNo, labType, NOT_ASSIGNED_PROVIDER_NO)) {
					providerLabRoutingDao.remove(plr.getId());
				}
			}

			// If we updated the status to X, then we want to see if all other statuses for the labNo are also X.
			// If they are then there are no more providers associated with the document, so move the document to the unclaimed inbox
			if (status.equals(ProviderInboxItem.ARCHIVED))
			{
				List<ProviderLabRoutingModel> allDocsWithLabNo = providerLabRoutingDao.getProviderLabRoutingDocuments(labNo);
				List<ProviderLabRoutingModel> docsWithStatusX = providerLabRoutingDao.findByStatusANDLabNoType(labNo, labType, ProviderInboxItem.ARCHIVED);

				if(allDocsWithLabNo.size() == docsWithStatusX.size())
				{
					inboxManager.addToProviderInbox(labNo, labType, NOT_ASSIGNED_PROVIDER_NO);
				}
			}
		}
		finally
		{
			DbConnectionFilter.releaseThreadLocalDbConnection();
		}
	}

	public ArrayList<ReportStatus> getStatusArray(String labId, String labType)
	{
		ArrayList<ReportStatus> statusArray = new ArrayList<ReportStatus>();
		ProviderLabRoutingDao dao = SpringUtils.getBean(ProviderLabRoutingDao.class);
		ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
		List<ProviderLabRoutingModel> providerLabRoutings = dao.getProviderLabRoutings(ConversionUtils.fromIntString(labId), labType);
		for (ProviderLabRoutingModel routings : providerLabRoutings)
		{
			Provider provider = providerDao.getProvider(routings.getProviderNo());

			// Provider can be null. For example, the unclaimed inbox has a route for provider 0 which will never match to an actual provider record
			if (provider != null)
			{
				statusArray.add(new ReportStatus(provider.getFullName(),
						provider.getProviderNo(),
						descriptiveStatus(routings.getStatus()),
						routings.getComment(),
						ConversionUtils.toTimestampString(routings.getTimestamp()),
						labId));
			}
		}
		return statusArray;
	}

	public String descriptiveStatus(String status) {
		switch (status.charAt(0)) {
		case 'A':
			return "Acknowledged";
		case 'F':
			return "Filed but not acknowledged";
		case 'U':
			return "N/A";
		default:
			return "Not Acknowledged";
		}
	}

	public static String searchPatient(String labNo, String labType) {
		PatientLabRoutingDao dao = SpringUtils.getBean(PatientLabRoutingDao.class);
		List<PatientLabRouting> routings = dao.findByLabNoAndLabType(ConversionUtils.fromIntString(labNo), labType);
		if (routings.isEmpty()) {
			return "0";
		}
		
		return routings.get(0).getDemographicNo().toString();
	}

	public static boolean updatePatientLabRouting(String labNo, String demographicNo, String labType) {
		boolean result = false;

		try {

			// update pateintLabRouting for labs with the same accession number
			CommonLabResultData data = new CommonLabResultData();
			String[] labArray = data.getMatchingLabs(labNo, labType).split(",");
			for (int i = 0; i < labArray.length; i++) {

				// delete old entries
				for(PatientLabRouting p:patientLabRoutingDao.findByLabNoAndLabType(Integer.parseInt(labArray[i]),labType)) {
					patientLabRoutingDao.remove(p.getId());
				}
				

				// add new entries
				PatientLabRouting plr = new PatientLabRouting();
				plr.setLabNo(Integer.parseInt(labArray[i]));
				plr.setDemographicNo(Integer.parseInt(demographicNo));
				plr.setLabType(labType);
				patientLabRoutingDao.persist(plr);
				

				// add labs to measurements table
				populateMeasurementsTable(labArray[i], demographicNo, labType);

			}

			return result;

		} catch (Exception e) {
			Logger l = Logger.getLogger(CommonLabResultData.class);
			l.error("exception in CommonLabResultData.updateLabRouting()", e);
			return false;
		}
	}

	public static boolean updateLabRouting(ArrayList<String[]> flaggedLabs, String selectedProviders) {
		boolean result;

		try {

			String[] providersArray = selectedProviders.split(",");

			CommonLabResultData data = new CommonLabResultData();
			ProviderLabRouting plr = new ProviderLabRouting();
			// MiscUtils.getLogger().info(flaggedLabs.size()+"--");
			for (int i = 0; i < flaggedLabs.size(); i++) {
				String[] strarr = flaggedLabs.get(i);
				String lab = strarr[0];
				String labType = strarr[1];

				// Forward all versions of the lab
				String matchingLabs = data.getMatchingLabs(lab, labType);
				String[] labIds = matchingLabs.split(",");
				// MiscUtils.getLogger().info(labIds.length+"labIds --");
				for (int k = 0; k < labIds.length; k++) {

					for (int j = 0; j < providersArray.length; j++) {
						plr.route(labIds[k], providersArray[j], DbConnectionFilter.getThreadLocalDbConnection(), labType);
					}

					// delete old entries
					for(ProviderLabRoutingModel p:providerLabRoutingDao.findByLabNoAndLabTypeAndProviderNo(Integer.parseInt(labIds[k]),labType,"0")) {
						providerLabRoutingDao.remove(p.getId());
					}
					
				}

			}

			return true;
		} catch (Exception e) {
			Logger l = Logger.getLogger(CommonLabResultData.class);
			l.error("exception in CommonLabResultData.updateLabRouting()", e);
			return false;
		}
	}

	// //
	public static boolean fileLabs(ArrayList<String[]> flaggedLabs, String provider) {

		CommonLabResultData data = new CommonLabResultData();

		try
		{
			for (int i = 0; i < flaggedLabs.size(); i++)
			{
				String[] strarr = flaggedLabs.get(i);
				String lab = strarr[0];
				String labType = strarr[1];
				String labs = data.getMatchingLabs(lab, labType);

				if (labs != null && !labs.equals(""))
				{
					String[] labArray = labs.split(",");
					for (int j = 0; j < labArray.length; j++)
					{
						updateReportStatus(Integer.parseInt(labArray[j]), provider, ProviderInboxItem.FILE, "", labType);
						removeFromQueue(Integer.parseInt(labArray[j]));
					}

				}
				else
				{
					updateReportStatus(Integer.parseInt(lab), provider, ProviderInboxItem.FILE, "", labType);
					removeFromQueue(Integer.parseInt(lab));
				}
			}
		}
		catch (SQLException e)
		{
			logger.error("Error filing labs.", e);
			return false;
		}
		return true;
	}
	
	
	private static void removeFromQueue(Integer lab_no) {
		List<QueueDocumentLink> queues = queueDocumentLinkDao.getQueueFromDocument(lab_no);
		
		for( QueueDocumentLink queue : queues ) {
			queueDocumentLinkDao.remove(queue.getId());
		}
	}

	// //

	public String getMatchingLabs(String lab_no, String lab_type) {
		String labs = null;
		if (lab_type.equals(LabResultData.HL7TEXT)) {
			labs = Hl7textResultsData.getMatchingLabs(lab_no);
		} else if (lab_type.equals(LabResultData.MDS)) {
			MDSResultsData data = new MDSResultsData();
			labs = data.getMatchingLabs(lab_no);
		} else if (lab_type.equals((LabResultData.EXCELLERIS))) {
			PathnetResultsData data = new PathnetResultsData();
			labs = data.getMatchingLabs(lab_no);
		} else if (lab_type.equals(LabResultData.CML)) {
			MDSResultsData data = new MDSResultsData();
			labs = data.getMatchingCMLLabs(lab_no);
		} else if (lab_type.equals(LabResultData.DOCUMENT)) {
			labs = lab_no;// one document is only linked to one patient.
		}else if (lab_type.equals(LabResultData.HRM)){
        		labs = lab_no;
        	}

		return labs;
	}

	public String getDemographicNo(String labId, String labType) {
		return searchPatient(labId, labType);
	}

	public boolean isDocLinkedWithPatient(String labId, String labType) {
		CtlDocumentDao dao = SpringUtils.getBean(CtlDocumentDao.class);
		List<CtlDocument> docList = dao.findByDocumentNoAndModule(ConversionUtils.fromIntString(labId), "demographic");
		if (docList.isEmpty()) {
			return false;
		}
		
		String mi = ConversionUtils.toIntString(docList.get(0).getId().getModuleId());
		return mi != null && !mi.trim().equals("-1");		
	}

	public boolean isLabLinkedWithPatient(String labId, String labType) {
		PatientLabRoutingDao dao = SpringUtils.getBean(PatientLabRoutingDao.class);
		PatientLabRouting routing = dao.findDemographics(labType, ConversionUtils.fromIntString(labId));
		if (routing == null)
			return false;
		
		String demo = ConversionUtils.toIntString(routing.getDemographicNo());
		return demo != null && !demo.trim().equals("0");
	}

	public boolean isHRMLinkedWithPatient(String labId, String labType) {
		boolean ret = false;
		try {
			HRMDocumentToDemographicDao hrmDocumentToDemographicDao = (HRMDocumentToDemographicDao)  SpringUtils.getBean("HRMDocumentToDemographicDao");
			List<HRMDocumentToDemographic> docToDemo = hrmDocumentToDemographicDao.findByHrmDocumentId(Integer.parseInt(labId));
			if(docToDemo != null && docToDemo.size() > 0){
				ret = true;
			}
		} catch (Exception e) {
			logger.error("exception in isLabLinkedWithPatient", e);

		}
		return ret;
	}


	public int getAckCount(String labId, String labType) {
		ProviderLabRoutingDao dao = SpringUtils.getBean(ProviderLabRoutingDao.class);
		return dao.findByStatusANDLabNoType(ConversionUtils.fromIntString(labId), labType, "A").size();
	}

	public static void populateMeasurementsTable(String labId, String demographicNo, String labType) {
		if (labType.equals(LabResultData.HL7TEXT)) {
			Hl7textResultsData.populateMeasurementsTable(labId, demographicNo);
		}
	}

	public static ArrayList<LabResultData> getRemoteLabs(LoggedInInfo loggedInInfo,Integer demographicId) {
		ArrayList<LabResultData> results = new ArrayList<LabResultData>();

		try {
			List<CachedDemographicLabResult> labResults  = null;
			try {
				if (!CaisiIntegratorManager.isIntegratorOffline(loggedInInfo.getSession())){
					DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility());
					labResults = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility()).getLinkedCachedDemographicLabResults(demographicId);
				}
			} catch (Exception e) {
				MiscUtils.getLogger().error("Unexpected error.", e);
				CaisiIntegratorManager.checkForConnectionError(loggedInInfo.getSession(),e);
			}

			if(CaisiIntegratorManager.isIntegratorOffline(loggedInInfo.getSession())){
				labResults = IntegratorFallBackManager.getLabResults(loggedInInfo,demographicId);
			}

			for (CachedDemographicLabResult cachedDemographicLabResult : labResults) {
				results.add(toLabResultData(cachedDemographicLabResult));
			}
		} catch (Exception e) {
			logger.error("Error retriving remote labs", e);
		}

		return (results);
	}

	private static LabResultData toLabResultData(CachedDemographicLabResult cachedDemographicLabResult) throws IOException, SAXException, ParserConfigurationException {
		LabResultData result = new LabResultData();
		result.setRemoteFacilityId(cachedDemographicLabResult.getFacilityIdLabResultCompositePk().getIntegratorFacilityId());

		result.labType = cachedDemographicLabResult.getType();

		Document doc = XmlUtils.toDocument(cachedDemographicLabResult.getData());
		Node root = doc.getFirstChild();
		result.acknowledgedStatus = XmlUtils.getChildNodeTextContents(root, "acknowledgedStatus");
		result.accessionNumber = XmlUtils.getChildNodeTextContents(root, "accessionNumber");
		result.dateTime = XmlUtils.getChildNodeTextContents(root, "dateTime");
		result.discipline = XmlUtils.getChildNodeTextContents(root, "discipline");
		result.healthNumber = XmlUtils.getChildNodeTextContents(root, "healthNumber");
		result.labPatientId = XmlUtils.getChildNodeTextContents(root, "labPatientId");
		result.patientName = XmlUtils.getChildNodeTextContents(root, "patientName");
		result.priority = XmlUtils.getChildNodeTextContents(root, "priority");
		result.reportStatus = XmlUtils.getChildNodeTextContents(root, "reportStatus");
		result.requestingClient = XmlUtils.getChildNodeTextContents(root, "requestingClient");
		result.segmentID = XmlUtils.getChildNodeTextContents(root, "segmentID");
		result.sex = XmlUtils.getChildNodeTextContents(root, "sex");
		result.setAckCount(Integer.parseInt(XmlUtils.getChildNodeTextContents(root, "ackCount")));
		result.setMultipleAckCount(Integer.parseInt(XmlUtils.getChildNodeTextContents(root, "multipleAckCount")));

		return result;
	}
	
	public List<LabIdAndType> getCmlAndEpsilonLabResultsSince(Integer demographicNo, Date updateDate) {
		LabPatientPhysicianInfoDao labPatientPhysicianInfoDao = (LabPatientPhysicianInfoDao) SpringUtils.getBean("labPatientPhysicianInfoDao");
		
		//This case handles Epsilon and the old CML data
		List<Integer> ids = labPatientPhysicianInfoDao.getLabResultsSince(demographicNo,updateDate);
		List<LabIdAndType> results = new ArrayList<LabIdAndType>();
		
		for(Integer id:ids) {
			results.add(new LabIdAndType(id,"CML"));
		}
		return results;
	}
	
	public List<LabIdAndType> getMdsLabResultsSince(Integer demographicNo, Date updateDate) {
		MdsMSHDao mdsMSHDao = SpringUtils.getBean(MdsMSHDao.class);
		
		//This case handles old MDS data
		List<Integer> ids = mdsMSHDao.getLabResultsSince(demographicNo,updateDate);
		List<LabIdAndType> results = new ArrayList<LabIdAndType>();
		
		for(Integer id:ids) {
			results.add(new LabIdAndType(id,"MDS"));
		}
		return results;
	}

	public List<LabIdAndType> getPathnetResultsSince(Integer demographicNo, Date updateDate) {
		Hl7MshDao hl7MshDao = SpringUtils.getBean(Hl7MshDao.class);
		
		List<Integer> ids = hl7MshDao.getLabResultsSince(demographicNo,updateDate);
		List<LabIdAndType> results = new ArrayList<LabIdAndType>();
		
		for(Integer id:ids) {
			results.add(new LabIdAndType(id,"BCP"));
		}
		return results;
	}
	
	public List<LabIdAndType> getHl7ResultsSince(Integer demographicNo, Date updateDate) {
		Hl7TextMessageDao hl7TextMessageDao = SpringUtils.getBean(Hl7TextMessageDao.class);
		
		List<Integer> ids = hl7TextMessageDao.getLabResultsSince(demographicNo,updateDate);
		List<LabIdAndType> results = new ArrayList<LabIdAndType>();
		
		for(Integer id:ids) {
			results.add(new LabIdAndType(id,"HL7"));
		}
		return results;
	}
	
	public LabResultData getLab(LabIdAndType labIdAndType) {
		oscar.oscarMDS.data.MDSResultsData mDSData = new oscar.oscarMDS.data.MDSResultsData();
		PathnetResultsData pathData = new PathnetResultsData();
		List<LabResultData> resultsList = new ArrayList<LabResultData>();
		
		if("Epsilon".equals(labIdAndType.getLabType())) {
			resultsList.addAll(mDSData.populateEpsilonResultsData(null, null, null, null, null, null,labIdAndType.getLabId()));
		} else if("CML".equals(labIdAndType.getLabType())) {
			resultsList.addAll(mDSData.populateCMLResultsData(null, null, null, null, null, null,labIdAndType.getLabId()));
		} else if("BCP".equals(labIdAndType.getLabType())) {
			resultsList.addAll(pathData.populatePathnetResultsData(null, null, null, null, null, null, labIdAndType.getLabId()));
		} else if("MDS".equals(labIdAndType.getLabType())) {
			resultsList.addAll(mDSData.populateMDSResultsData2(null, null, null, null, null, null,labIdAndType.getLabId()));
		} else if("HL7".equals(labIdAndType.getLabType())) {
			resultsList.addAll(Hl7textResultsData.populateHl7ResultsData(null, null, null, null, null, null,labIdAndType.getLabId()));   
		}
		if(!resultsList.isEmpty()) {
			return resultsList.get(0);
		}
		return null;
	}
	
}
