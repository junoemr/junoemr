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

			// Get documents by demographic
			//if (demographicNo != null && !"".equals(demographicNo)) {
			// Get mix from labs
			if (mixLabsAndDocs) {
				if ("0".equals(demographicNo) || "0".equals(providerNo)) {
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 3; lastNameLoc = 4; firstNameLoc = 5; hinLoc = 6; sexLoc = 7; moduleLoc = 8; obsDateLoc = 9;
					sql = " SELECT X.id, X.lab_no as document_no, X.status, X.lab_type as doctype, d.last_name, d.first_name, hin, sex, d.demographic_no as module_id, doc.observationdate "
							+ " FROM document doc, "
							+ " (SELECT plr.id, plr.lab_type, plr.lab_no, plr.status "
							+ "  FROM patientLabRouting plr2, providerLabRouting plr, hl7TextInfo info "
							+ "  WHERE plr.lab_no = plr2.lab_no "
							+ (searchProvider ? " AND plr.provider_no = '?' " : " ")
							+ "    AND plr.status like ?"
							+ "    AND plr.lab_type = 'HL7'   "
							+ "    AND plr2.lab_type = 'HL7' "
							+ "    AND plr2.demographic_no = '0' "
							+ "    AND info.lab_no = plr.lab_no "
							+ (isAbnormal != null && isAbnormal ? "AND info.result_status = 'A'" : isAbnormal != null
									&& !isAbnormal ? "AND (info.result_status IS NULL OR info.result_status != 'A')" : "")
							+ " UNION "
							+ " SELECT plr.id, plr.lab_type, plr.lab_no, plr.status "
							+ " FROM providerLabRouting plr  "
							+ " WHERE plr.lab_type = 'DOC' AND plr.status like ?"
							+ (searchProvider ? " AND plr.provider_no = '?' " : " ")
							+ " ORDER BY id DESC "
							+ (isPaged ? "	LIMIT ?,?" : "")
							+ " ) AS X "
							+ " LEFT JOIN demographic d "
							+ " ON d.demographic_no = -1 "
							+ " WHERE X.lab_type = 'DOC' AND doc.document_no = X.lab_no ";
					q = entityManager.createNativeQuery(sql);
					if(searchProvider){
						q.setParameter(1, providerNo);
						q.setParameter(2, "%"+status+"%");
						q.setParameter(3, "%"+status+"%");
						q.setParameter(4, providerNo);
						if(isPaged){
							q.setParameter(5, (page * pageSize));
							q.setParameter(6, pageSize);
						}
					}else{
						q.setParameter(1, "%"+status+"%");
						q.setParameter(2, "%"+status+"%");
						if(isPaged){
							q.setParameter(5, (page * pageSize));
							q.setParameter(6, pageSize);
						}
					}
				} else if (demographicNo != null && !"".equals(demographicNo)) {
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8;
					sql = " SELECT plr.id, doc.document_no, plr.status, d.last_name, d.first_name, hin, sex, d.demographic_no as module_id, doc.observationdate, plr.lab_type as doctype "
							+ " FROM demographic d, providerLabRouting plr, document doc, "
							+ " (SELECT * FROM "
							+ " (SELECT DISTINCT plr.id, plr.lab_type  FROM providerLabRouting plr, ctl_document cd "
							+ " WHERE 	" + " (cd.module_id = ? "
							+ "	AND cd.document_no = plr.lab_no"
							+ "	AND plr.lab_type = 'DOC'  	"
							+ "	AND plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? )" : " )")
							+ " ORDER BY id DESC) AS Y"
							+ " UNION"
							+ " SELECT * FROM"
							+ " (SELECT DISTINCT plr.id, plr.lab_type  FROM providerLabRouting plr, patientLabRouting plr2"
							+ " WHERE"
							+ "	plr.lab_type = 'HL7' AND plr2.lab_type = 'HL7'"
							+ "	AND plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? " : " ")
							+ " 	AND plr.lab_no = plr2.lab_no AND plr2.demographic_no = ?"
							+ " ORDER BY id DESC) AS Z"
							+ " ORDER BY id DESC "
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "")
							+ " ) AS X "
							+ " WHERE X.lab_type = 'DOC' and X.id = plr.id and doc.document_no = plr.lab_no and d.demographic_no = ? ";
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, demographicNo);
					q.setParameter(2, "%"+status+"%");
					if(searchProvider){
						q.setParameter(3, providerNo);
						q.setParameter(4, "%"+status+"%");
						q.setParameter(5, providerNo);
						q.setParameter(6, demographicNo);
						if(isPaged){
							q.setParameter(7, (page * pageSize));
							q.setParameter(8, pageSize);
						}
						q.setParameter(9, demographicNo);
					}else{
						q.setParameter(3, "%"+status+"%");
						q.setParameter(4, demographicNo);
						if(isPaged){
							q.setParameter(5, (page * pageSize));
							q.setParameter(6, pageSize);
						}
						q.setParameter(7, demographicNo);
					}
				} else if (patientSearch) { // N arg
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8;
					sql = " SELECT plr.id, doc.document_no, plr.status, d.last_name, d.first_name, hin, sex, d.demographic_no as module_id, doc.observationdate, plr.lab_type as doctype "
							+ " FROM demographic d, providerLabRouting plr, document doc,  "
							+ " (SELECT * FROM "
							+ "	 (SELECT * FROM  "
							+ "		(SELECT DISTINCT plr.id, plr.lab_type, d.demographic_no "
							+ "			FROM providerLabRouting plr, ctl_document cd, demographic d "
							+ "			WHERE 	 "
							+ "			(d.first_name like ? AND d.last_name like ? AND d.hin like ? "
							+ "		AND cd.module_id = d.demographic_no 	AND cd.document_no = plr.lab_no	AND plr.lab_type = 'DOC' "
							+ "				AND plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? " : " ")
							+ "		) ORDER BY id DESC) AS Y "
							+ " 	UNION "
							+ "	SELECT * FROM "
							+ "		(SELECT DISTINCT plr.id, plr.lab_type, d.demographic_no "
							+ "		FROM providerLabRouting plr, patientLabRouting plr2, demographic d"
							+ (isAbnormal != null ? ", hl7TextInfo info " : " ")
							+ "		WHERE d.first_name like ? AND d.last_name like ? AND d.hin like ? "
							+ "		AND	plr.lab_type = 'HL7' AND plr2.lab_type = 'HL7' "
							+ (isAbnormal != null ? " AND plr.lab_no = info.lab_no AND (info.result_status IS NULL OR info.result_status != 'A') "
									: " ")
							+ "				AND plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? " : " ")
							+ " 	AND plr.lab_no = plr2.lab_no AND plr2.demographic_no = d.demographic_no ORDER BY id DESC) AS Z "
							+ " 			ORDER BY id DESC) AS X "
							+ " 	  ) AS Z  "
							+ " WHERE Z.lab_type = 'DOC' and Z.id = plr.id and doc.document_no = plr.lab_no and d.demographic_no = Z.demographic_no "
							+ (isPaged ? "	LIMIT ?,?" : "");
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, "%"+patientFirstName+"%");
					q.setParameter(2, "%"+patientLastName+"%");
					q.setParameter(3, "%"+patientHealthNumber+"%");
					q.setParameter(4, "%"+status+"%");
					if(searchProvider){
						q.setParameter(5, providerNo);
						q.setParameter(6, "%"+patientFirstName+"%");
						q.setParameter(7, "%"+patientLastName+"%");
						q.setParameter(8, "%"+patientHealthNumber+"%");
						q.setParameter(9, "%"+status+"%");
						q.setParameter(10, providerNo);
						if(isPaged){
							q.setParameter(11, (page * pageSize));
							q.setParameter(12, pageSize);
						}
					}else{
						q.setParameter(5, "%"+patientFirstName+"%");
						q.setParameter(6, "%"+patientLastName+"%");
						q.setParameter(7, "%"+patientHealthNumber+"%");
						q.setParameter(8, "%"+status+"%");
						if(isPaged){
							q.setParameter(9, (page * pageSize));
							q.setParameter(10, pageSize);
						}
					}

				} else {
					docNoLoc = 0; statusLoc = 1; docTypeLoc = 8; lastNameLoc = 2; firstNameLoc = 3; hinLoc = 4; sexLoc = 5; moduleLoc = 6; obsDateLoc = 7;
					// N
					// document_no, status, last_name, first_name, hin, sex, module_id, observationdate
					sql = " SELECT doc.document_no, plr.status, last_name, first_name, hin, sex, module_id, observationdate, plr.lab_type as doctype"
							+ " FROM document doc, "
							+ " 	(SELECT DISTINCT plr.* FROM providerLabRouting plr"
							+ (isAbnormal != null ? ", hl7TextInfo info " : "")
							+ " 	WHERE plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? " : "")
							// The only time abnormal matters for documents is when we are looking for normal documents since there are no abnormal documents.
							+ (isAbnormal != null ? "     AND (plr.lab_type = 'DOC' OR (plr.lab_no = info.lab_no AND (info.result_status IS NULL OR info.result_status != 'A'))) "
									: " ")
							+ " 	ORDER BY id DESC "
							+ (isPaged ? "	LIMIT ?,?": "")
							+ "     ) AS plr"
							+ " LEFT JOIN "
							+ "(SELECT module_id, document_no FROM ctl_document cd "
							+ "WHERE cd.module = 'demographic' AND cd.module_id != '-1') AS Y "
							+ "ON plr.lab_type = 'DOC' AND plr.lab_no = Y.document_no"
							+ " LEFT JOIN "
							+ "(SELECT demographic_no, first_name, last_name, hin, sex "
							+ "FROM demographic d) AS Z "
							+ "ON Y.module_id = Z.demographic_no "
							+ "WHERE doc.document_no = plr.lab_no AND plr.lab_type = 'DOC'";
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, "%"+status+"%");
					if(searchProvider){
						q.setParameter(2, providerNo);
						if(isPaged){
							q.setParameter(3, (page * pageSize));
							q.setParameter(4, pageSize);
						}
					}else if(isPaged){
						q.setParameter(2, (page * pageSize));
						q.setParameter(3, pageSize);
					}
					
				}
			} else { // Don't mix labs and docs.
				if ("0".equals(demographicNo) || "0".equals(providerNo)) {
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 5; lastNameLoc = 6; firstNameLoc = 7; hinLoc = 8; sexLoc = 9; moduleLoc = 3; obsDateLoc = 4;
					sql = " SELECT id, document_no, status, demographic_no as module_id, observationdate, doctype, last_name, first_name, hin, sex"
							+ " FROM "
							+ " (SELECT plr.id, doc.document_no, plr.status, observationdate, plr.lab_type as doctype"
							+ " FROM providerLabRouting plr, document doc"
							+ " WHERE plr.lab_type = 'DOC' "
							+ " AND plr.status like ?  "
							+ " AND plr.provider_no = '0' "
							+ " AND doc.document_no = plr.lab_no"
							+ " ORDER BY id DESC 	"
							+ (isPaged ? "	LIMIT ?,?" : "")
							+ ") as X"
							+ " LEFT JOIN demographic d" + " ON d.demographic_no = -1";
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, "%"+status+"%");
					if(isPaged){
						q.setParameter(2, (page * pageSize));
						q.setParameter(3, pageSize);
					}
				} else if (demographicNo != null && !"".equals(demographicNo)) {
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8;
					sql = "SELECT plr.id, doc.document_no, plr.status, last_name, first_name, hin, sex, module_id, observationdate, plr.lab_type as doctype "
							+ "FROM ctl_document cd, demographic d, providerLabRouting plr, document doc "
							+ "WHERE d.demographic_no = ?"
							+ "	AND cd.module_id = d.demographic_no "
							+ "	AND cd.document_no = plr.lab_no "
							+ "	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? " : "")
							+ "	AND doc.document_no = cd.document_no "
							+ " ORDER BY id DESC "
							+ (isPaged ? "	LIMIT ?,?": "");
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, demographicNo);
					q.setParameter(2, "%"+status+"%");
					if(searchProvider){
						q.setParameter(3, providerNo);
						if(isPaged){
							q.setParameter(4, (page * pageSize));
							q.setParameter(5, pageSize);
						}
					}else if(isPaged){
						q.setParameter(3, (page * pageSize));
						q.setParameter(4, pageSize);
					}
				} else if (patientSearch) {
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8;
					sql = "SELECT plr.id, doc.document_no, plr.status, last_name, first_name, hin, sex, module_id, observationdate, plr.lab_type as doctype "
							+ "FROM ctl_document cd, demographic d, providerLabRouting plr, document doc "
							+ "WHERE d.first_name like ? AND d.last_name like ? AND d.hin like ? "
							+ "	AND cd.module_id = d.demographic_no "
							+ "	AND cd.document_no = plr.lab_no "
							+ "	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status like ? "
							+ (searchProvider ? " AND plr.provider_no = ? " : "")
							+ "	AND doc.document_no = cd.document_no "
							+ " ORDER BY id DESC "
							+ (isPaged ? "	LIMIT ?,?": "");
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, "%"+patientFirstName+"%");
					q.setParameter(2, "%"+patientLastName+"%");
					q.setParameter(3, "%"+patientHealthNumber+"%");
					q.setParameter(4, "%"+status+"%");
					
					if(searchProvider){
						q.setParameter(5, providerNo);
						if(isPaged){
							q.setParameter(6, (page * pageSize));
							q.setParameter(7, pageSize);
						}
					}else if(isPaged){
						q.setParameter(5, (page * pageSize));
						q.setParameter(6, pageSize);
					}
				} else {
					idLoc = 0; docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8;
					sql = " SELECT * "
							+ " FROM (SELECT plr.id, doc.document_no, plr.status, last_name, first_name, hin, sex, module_id, observationdate, plr.lab_type as doctype "
							+ " FROM ctl_document cd, demographic d, providerLabRouting plr, document doc "
							+ " WHERE (cd.module_id = d.demographic_no) "
							+ " 	AND cd.document_no = plr.lab_no "
							+ " 	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status like ?  "
							+ (searchProvider ? " AND plr.provider_no = ? " : "")
							+ " 	AND doc.document_no = cd.document_no  "
							+ " UNION "
							+ " SELECT X.id, X.lab_no as document_no, X.status, last_name, first_name, hin, sex, X.module_id, X.observationdate, X.lab_type as doctype "
							+ " FROM (SELECT plr.id, plr.lab_no, plr.status, plr.lab_type, cd.module_id, observationdate "
							+ " FROM ctl_document cd, providerLabRouting plr, document d "
							+ " WHERE plr.lab_type = 'DOC' " + "	AND plr.status like ?'  "
							+ (searchProvider ? " AND plr.provider_no = ? " : "")
							+ " AND plr.lab_no = cd.document_no " + " AND cd.module_id = -1 "
							+ " AND d.document_no = cd.document_no " + " ) AS X " + " LEFT JOIN demographic d "
							+ " ON d.demographic_no = -1) AS X " + " ORDER BY id DESC "
							+ (isPaged ? "	LIMIT ?,?" : "");
					q = entityManager.createNativeQuery(sql);
					q.setParameter(1, "%"+status+"%");
					
					if(searchProvider){
						q.setParameter(2, providerNo);
						q.setParameter(3, "%"+status+"%");
						q.setParameter(4, providerNo);
						if(isPaged){
							q.setParameter(5, (page * pageSize));
							q.setParameter(6, pageSize);
						}
					}else{
						q.setParameter(2, "%"+status+"%");
						if(isPaged){
							q.setParameter(3, (page * pageSize));
							q.setParameter(4, pageSize);
						}
					}
				}
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
