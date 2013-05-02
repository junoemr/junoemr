/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.http.impl.cookie.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import oscar.oscarLab.ca.on.LabResultData;

@Transactional
public class InboxResultsDao {

	Logger logger = Logger.getLogger(InboxResultsDao.class);

	@PersistenceContext
	protected EntityManager entityManager = null;

	/** Creates a new instance of Hl7textResultsData */
	public InboxResultsDao() {
	}

	/**
	 * Populates ArrayList with labs attached to a consultation request
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public ArrayList populateHL7ResultsData(String demographicNo, String consultationId, boolean attached) {
		String sql = "SELECT hl7.label, hl7.lab_no, hl7.obr_date, hl7.discipline, hl7.accessionNum, hl7.final_result_count, patientLabRouting.id "
				+ "FROM hl7TextInfo hl7, patientLabRouting "
				+ "WHERE patientLabRouting.lab_no = hl7.lab_no "
				+ "AND patientLabRouting.lab_type = 'HL7' AND patientLabRouting.demographic_no="
				+ demographicNo
				+ " GROUP BY hl7.lab_no";

		String attachQuery = "SELECT consultdocs.document_no FROM consultdocs, patientLabRouting "
				+ "WHERE patientLabRouting.id = consultdocs.document_no AND " + "consultdocs.requestId = "
				+ consultationId
				+ " AND consultdocs.doctype = 'L' AND consultdocs.deleted IS NULL ORDER BY consultdocs.document_no";

		ArrayList labResults = new ArrayList<LabResultData>();
		ArrayList attachedLabs = new ArrayList<LabResultData>();


		try {
			Query q = entityManager.createNativeQuery(attachQuery);

			List<Object[]> result = q.getResultList();
			for (Object[] r : result) {
				LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
				lbData.labPatientId = (String) r[0];
				attachedLabs.add(lbData);
			}

			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			LabResultData.CompareId c = lbData.getComparatorId();

			q = entityManager.createNativeQuery(sql);
			result = q.getResultList();
			for (Object[] r : result) {
				lbData.segmentID = (String) r[1];
				lbData.labPatientId = (String) r[6];
				lbData.dateTime = (String) r[2];
				lbData.discipline = (String) r[3];
				lbData.accessionNumber = (String) r[4];
				lbData.finalResultsCount = (Integer) r[5];
				lbData.label = (String) r[0];

				if (attached && Collections.binarySearch(attachedLabs, lbData, c) >= 0)
					labResults.add(lbData);
				else if (!attached && Collections.binarySearch(attachedLabs, lbData, c) < 0)
					labResults.add(lbData);

				lbData = new LabResultData(LabResultData.HL7TEXT);
			}
		} catch (Exception e) {
			logger.error("exception in HL7Populate", e);
		}


		return labResults;
	}

	@SuppressWarnings("unchecked")
	public boolean isSentToProvider(String docNo, String providerNo) {
		if (docNo != null && providerNo != null) {
			int dn = Integer.parseInt(docNo.trim());
			providerNo = providerNo.trim();
			String sql = "select * from providerLabRouting plr where plr.lab_type='DOC' and plr.lab_no=" + dn
					+ " and plr.provider_no='" + providerNo + "'";
			try {

				Query q = entityManager.createNativeQuery(sql);
				List<Object[]> rs = q.getResultList();

				logger.info(sql);
				if (!rs.isEmpty()) {
					return true;
				} else
					return false;
			} catch (Exception e) {
				logger.error(e.toString());
				return false;
			}
		} else {
			return false;
		}
	}

	//retrieve all documents from database
	/**
	 * Wrapper function for non paged document queries.
	 */
	@SuppressWarnings("rawtypes")
    public ArrayList populateDocumentResultsData(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status) {
		return populateDocumentResultsData(providerNo, demographicNo, patientFirstName, patientLastName,
				patientHealthNumber, status, false, null, null, false, null);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public ArrayList<LabResultData> populateDocumentResultsData(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status, boolean isPaged, Integer page,
			Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal) {

        boolean qp_provider_no = false;
        boolean qp_status = false;
        boolean qp_first_name = false;
        boolean qp_last_name = false;
        boolean qp_hin = false;
        boolean qp_demographic_no = false;
        boolean qp_page = false;

        logger.debug("populateDocumentResultsData(" 
                    + "providerNo = " + ((providerNo == null) ? "NULL" : providerNo) + ", "
                    + "demographicNo = " + ((demographicNo == null) ? "NULL" : demographicNo) + ", "
                    + "patientFirstName = " + ((patientFirstName == null) ? "NULL" : patientFirstName) + ", "
                    + "patientLastName = " + ((patientLastName == null) ? "NULL" : patientLastName) + ", "
                    + "patientHealthNumber = " + ((patientHealthNumber == null) ? "NULL" : patientHealthNumber) + ", "
                    + "status = " + ((status == null) ? "NULL" : status) + ", "
                    + "isPaged = " + ((isPaged) ? "true" : "false") + ", "
                    + "page = "+ ((page == null) ? "NULL" : page.toString()) + ", "
                    + "pageSize = "+ ((pageSize == null) ? "NULL" : pageSize.toString()) + ", "
                    + "mixLabsAndDocs = " + ((mixLabsAndDocs) ? "true" : "false") + ", "
                    + "isAbnormal = " + ((isAbnormal == null) ? "NULL" : ((isAbnormal) ? "true" : "false")) 
                    + ")");

		if (providerNo == null) {
			providerNo = "";
		}
		boolean searchProvider = !"-1".equals(providerNo);
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
		if (status == null) {
			status = "";
		}

		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();

		if (isAbnormal != null) { 
			// Only need to return DOCs.  If isAbnormal is set, this would imply 
			// HL7 (i.e. something that identifies whether it is normal or not)
			// so we return nothing without running any queries.
			return labResults;
		}

		Query q;
		String sql = "";

		int idLoc = -1;
		int docNoLoc = -1;
		int statusLoc = -1;
		int docTypeLoc = -1;
		int lastNameLoc = -1;
		int firstNameLoc = -1;
		int hinLoc = -1;
		int sexLoc = -1;
		int moduleLoc = -1;
		int obsDateLoc = -1;
		try {


			idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 3; lastNameLoc = 4; firstNameLoc = 5; hinLoc = 6; sexLoc = 7; moduleLoc = 8; obsDateLoc = 9;
            // Notes on query:
            //
            // Returns only DOCUMENTS so INNER JOIN providerLabRouting with
            // document and ctl_document because they must exist for this 
            // to be a valid document.  
            //
            // patientLabRouting is written into 
            // the query as a LEFT JOIN but with a FALSE WHERE clause so
            // it's never actually used.  Sometimes a patientLabRouting 
            // exists for a DOCUMENT, sometimes it doesn't -- I don't know
            // why, but it seems to be consistent with the ctl_document with
            // respect to the demographic_no (module_id in ctl_document).
            // 
            // Similarly, getting the demographic based on the patientLabRouting
            // is written into the query, but since the patientLabRouting is 
            // never joined, it will never be used.
			sql = "SELECT  proLR.id, "
				+ "        proLR.lab_no AS document_no, "
				+ "        proLR.status, "
				+ "        proLR.lab_type AS doctype, "
				+ "        CASE WHEN d1.demographic_no IS NOT NULL THEN d1.last_name ELSE d2.last_name END AS last_name, "
				+ "        CASE WHEN d1.demographic_no IS NOT NULL THEN d1.first_name ELSE d2.first_name END AS first_name, "
				+ "        CASE WHEN d1.demographic_no IS NOT NULL THEN d1.hin ELSE d2.hin END AS hin, "
				+ "        CASE WHEN d1.demographic_no IS NOT NULL "
				+ "            THEN CASE WHEN d1.sex IN ('F', 'M') THEN d1.sex ELSE '?' END "
				+ "            ELSE CASE WHEN d2.sex IN ('F', 'M') THEN d2.sex ELSE '?' END "
				+ "            END AS sex, "
				+ "        CASE WHEN d1.demographic_no IS NOT NULL THEN d1.demographic_no ELSE d2.demographic_no END AS demographic_no, "
				+ "        doc.observationdate AS observationdate "
				+ "FROM providerLabRouting proLR "
				+ "LEFT JOIN patientLabRouting patLR ON ( proLR.lab_type = patLR.lab_type AND proLR.lab_no = patLR.lab_no AND FALSE ) "
				+ "INNER JOIN document doc ON ( proLR.lab_type = 'DOC' AND proLR.lab_no = doc.document_no ) "
				+ "INNER JOIN ctl_document cdoc ON ( doc.document_no = cdoc.document_no AND cdoc.module='demographic' ) "
				+ "LEFT JOIN demographic d1 ON ( patLR.demographic_no = d1.demographic_no AND FALSE ) "
				+ "LEFT JOIN demographic d2 ON ( cdoc.module_id IS NOT NULL AND cdoc.module_id > 0 AND cdoc.module_id = d2.demographic_no ) "
				+ "WHERE proLR.lab_type = 'DOC' ";


			if ("-1".equals(providerNo) || "".equals(providerNo)) {
				// any provider
			} else if ("0".equals(providerNo)) {
				// unclaimed
				sql = sql + "AND (NOT EXISTS ( SELECT 1 FROM provider WHERE proLR.provider_no = provider.provider_no )) ";
			} else {
				sql = sql + "AND (proLR.provider_no = :provider_no) ";
				qp_provider_no = true;
			}

			if ("N".equals(status) || "A".equals(status) || "F".equals(status)) {
				sql = sql + "AND (proLR.status = :status) ";
				qp_status = true;
			}

			if (!"".equals(patientFirstName)) {
				sql = sql + "AND ((CASE WHEN d1.demographic_no IS NOT NULL THEN d1.first_name ELSE d2.first_name END) LIKE :first_name) ";
				qp_first_name = true;
			}

			if (!"".equals(patientLastName)) {
				sql = sql + "AND ((CASE WHEN d1.demographic_no IS NOT NULL THEN d1.last_name ELSE d2.last_name END) LIKE :last_name) ";
				qp_last_name = true;
			}

			if (!"".equals(patientHealthNumber)) {
				sql = sql + "AND ((CASE WHEN d1.demographic_no IS NOT NULL THEN d1.hin ELSE d2.hin END) LIKE :hin) ";
				qp_hin = true;
			}

			if ("0".equals(demographicNo)) {
				// There seems to be an inconsistency where a ctl_document.module_id of either 0 or -1
				// (when module='demographic') indicates that the document is unassigned hence the 
				// checking for either in the following condition
				sql = sql + "AND (COALESCE(d1.demographic_no, d2.demographic_no) IS NULL OR COALESCE(d1.demographic_no, d2.demographic_no) IN ('0', '-1')) ";
			} else if (demographicNo != null && !"".equals(demographicNo)) {
				sql = sql + "AND ((CASE WHEN d1.demographic_no IS NOT NULL THEN d1.demographic_no ELSE d2.demographic_no END) = :demographic_no) ";
				qp_demographic_no = true;
			}

			if (isPaged) {
				sql = sql + "LIMIT :page_start, :page_size";
				qp_page = true;
			}

			q = entityManager.createNativeQuery(sql);

			if (qp_provider_no) { q.setParameter("provider_no", providerNo); }
			if (qp_status) { q.setParameter("status", status); }
			if (qp_first_name) { q.setParameter("first_name", "%" + patientFirstName + "%"); }
			if (qp_last_name) { q.setParameter("last_name", "%" + patientLastName + "%"); }
			if (qp_hin) { q.setParameter("hin", "%" + patientHealthNumber + "%"); }
			if (qp_demographic_no) { q.setParameter("demographic_no", demographicNo); }
			if (qp_page) {
				q.setParameter("page_start", page * pageSize);
				q.setParameter("page_size", pageSize);
			}
			


			logger.info(sql);

			List<Object[]> result = q.getResultList();
			for (Object[] r : result) {
				LabResultData lbData = new LabResultData(LabResultData.DOCUMENT);
				lbData.labType = LabResultData.DOCUMENT;


				lbData.segmentID = getStringValue(r[docNoLoc]);

				if (demographicNo == null && !providerNo.equals("0")) {
					lbData.acknowledgedStatus = getStringValue(r[statusLoc]);
				} else {
					lbData.acknowledgedStatus = "U";
				}

				lbData.healthNumber = "";
				lbData.patientName = "Not, Assigned";
				lbData.sex = "";

				lbData.isMatchedToPatient = r[lastNameLoc] != null;

				if (lbData.isMatchedToPatient) {
					lbData.patientName = getStringValue(r[lastNameLoc]) + ", " + getStringValue(r[firstNameLoc]);
					lbData.healthNumber = getStringValue(r[hinLoc]);
					lbData.sex = getStringValue(r[sexLoc]);
					lbData.setLabPatientId(getStringValue(r[moduleLoc]));

				}else {
					lbData.patientName = "Not, Assigned";
				}

				logger.debug("DOCUMENT " + lbData.isMatchedToPatient());
				lbData.accessionNumber = "";
				lbData.resultStatus = "N";

				if (lbData.resultStatus.equals("A")) lbData.abn = true;

				lbData.dateTime = getStringValue(r[obsDateLoc]);
				lbData.setDateObj(DateUtils.parseDate(getStringValue(r[obsDateLoc]), new String[] {
						"yyyy-MM-dd"
				}));

				String priority = "";
				if (priority != null && !priority.equals("")) {
					switch (priority.charAt(0)) {
						case 'C':
							lbData.priority = "Critical";
							break;
						case 'S':
							lbData.priority = "Stat/Urgent";
							break;
						case 'U':
							lbData.priority = "Unclaimed";
							break;
						case 'A':
							lbData.priority = "ASAP";
							break;
						case 'L':
							lbData.priority = "Alert";
							break;
						default:
							lbData.priority = "Routine";
							break;
					}
				} else {
					lbData.priority = "----";
				}

				lbData.requestingClient = "";

				lbData.reportStatus = "F";


				// the "C" is for corrected excelleris labs
				if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))) {
					lbData.finalRes = true;
				} else if (lbData.reportStatus != null && lbData.reportStatus.equals("X")){
					lbData.cancelledReport = true;
				} else{

					lbData.finalRes = false;
				}




				lbData.discipline = getStringValue(r[docTypeLoc]);
				if (lbData.discipline.trim().equals("")) {
					lbData.discipline = null;
				}

				lbData.finalResultsCount = 0;//rs.getInt("final_result_count");
				labResults.add(lbData);
			}

		} catch (Exception e) {
			logger.error("exception in DOCPopulate:", e);
		}
		return labResults;
	}

	private String getStringValue(Object value) {
		return value != null ? value.toString() : null;
	}
}
