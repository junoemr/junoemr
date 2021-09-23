/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.utility.UtilDateUtilities;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.transaction.annotation.Transactional;

import oscar.oscarLab.ca.on.LabResultData;
import oscar.oscarLab.ca.on.LabSummaryData;
import oscar.oscarMDS.data.PatientInfo;
import oscar.util.ConversionUtils;

@Transactional
public class InboxResultsDao
{

	Logger logger = Logger.getLogger(InboxResultsDao.class);

	@PersistenceContext
	protected EntityManager entityManager = null;

	/** Creates a new instance of Hl7textResultsData */
	public InboxResultsDao()
	{
	}

	@SuppressWarnings({ "unchecked" })
	private List<Object[]> getRawInboxResults(
			boolean do_counts,
			String providerNo,
			String demographicNo,
			String patientFirstName,
			String patientLastName,
			String patientHealthNumber,
			String status,
			boolean isPaged,
			Integer page,
			Integer pageSize,
			Boolean isAbnormal,
			List<String> providerNoArr,
			boolean neverAcknowledgedItems,
			String labType,
			Date startDate,
			Date endDate)
	{

		logger.info("POPULATING DOCUMENT RESULTS: provider:"+providerNo+", demographic:"+demographicNo);

		boolean qp_provider_no = false;
		boolean qp_status = false;
		boolean qp_first_name = false;
		boolean qp_last_name = false;
		boolean qp_hin = false;
		boolean qp_start_date = false;
		boolean qp_end_date = false;
		boolean qp_lab_type = false;
		boolean qp_demographic_no = false;
		boolean qp_page = false;
		boolean qp_hrm_first_name = false;
		boolean qp_hrm_last_name = false;
		boolean qp_hrm_hin = false;
		boolean qp_hrm_demographic_no = false;

		String providerNoList = "";
		if(providerNoArr != null && providerNoArr.size() > 0){
			providerNoList = StringUtils.join(providerNoArr, ",");
		}

		logger.debug("populateDocumentResultsData("
				+ "providerNo = " + ((providerNo == null) ? "NULL" : providerNo) + ", "
				+ "demographicNo = " + ((demographicNo == null) ? "NULL" : demographicNo) + ", "
				+ "patientFirstName = " + ((patientFirstName == null) ? "NULL" : patientFirstName) + ", "
				+ "patientLastName = " + ((patientLastName == null) ? "NULL" : patientLastName) + ", "
				+ "patientHealthNumber = " + ((patientHealthNumber == null) ? "NULL" : patientHealthNumber) + ", "
				+ "startDate = " + ((startDate == null) ? "NULL" : startDate.toString()) + ", "
				+ "endDate = " + ((endDate == null) ? "NULL" : endDate.toString()) + ", "
				+ "status = " + ((status == null) ? "NULL" : status) + ", "
				+ "isPaged = " + ((isPaged) ? "true" : "false") + ", "
				+ "page = "+ ((page == null) ? "NULL" : page.toString()) + ", "
				+ "pageSize = "+ ((pageSize == null) ? "NULL" : pageSize.toString()) + ", "
				+ "isAbnormal = " + ((isAbnormal == null) ? "NULL" : ((isAbnormal) ? "true" : "false"))  +", "
				+ "providerNoList = "+ ((providerNoList == null) ? "NULL" : (providerNoList.equals("") ? "empty" : providerNoList)) + ", "
				+ "neverAcknowledgedItems = " + ((neverAcknowledgedItems) ? "true" : "false") + ", "
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

		Query q;
		String sql = "";

		List<Object[]> result = null;

		try
		{
			
			// Notes on query:
			//
			// If do_counts is set, the whole query is wrapped in a select and summed up for
			// each demographic.  A null demographic id means the counts are for unassigned labs/documents.
			//
			// The providerLabRouting, patientLabRouting and hl7TextInfo tables are joined twice each to perform a
			// join only the row with the most recent date.  The joins with the *_filter aliases
			// are the second join.  To get the row with the most recent date, only rows with a smaller date are joined
			// from the second table and then only the row that has nothing joined to it is kept.
			//
			// Most of the where clause is built before the rest of the query because some of the sections need to be
			// added to the *_filter joins.
			
			// Filters to add to the where clause
			String filterSql = "";
			String proLrSql = "";
			String labSql = "";
			
			if (labType != null && !"".equals(labType))
			{
				filterSql += "AND proLR.lab_type = :lab_type ";
				proLrSql += "AND proLR_filter.lab_type = :lab_type ";
				qp_lab_type = true;
			}
			
			if (isAbnormal != null)
			{
				if (isAbnormal)
				{
					filterSql += "AND lab.result_status = 'A' ";
					labSql += "AND lab_filter.result_status = 'A' ";
				}
				else
				{
					filterSql += "AND (lab.result_status IS NULL OR lab.result_status != 'A') ";
					labSql += "AND (lab_filter.result_status IS NULL OR lab_filter.result_status != 'A') ";
				}
				filterSql += "AND lab.id IS NOT NULL ";
				labSql += "AND lab_filter.id IS NOT NULL ";
			}
			
			if ("-1".equals(providerNo) || "".equals(providerNo))
			{
				// any provider
			}
			else if ("0".equals(providerNo))
			{
				// unclaimed
				filterSql += "AND (proLR.provider_no = '0') ";
				proLrSql += "AND (proLR_filter.provider_no = '0') ";
			}
			else
			{
				filterSql += "AND (proLR.provider_no = :provider_no) ";
				proLrSql += "AND (proLR_filter.provider_no = :provider_no) ";
				qp_provider_no = true;
			}
			
			if (providerNoList != null && !providerNoList.equals(""))
			{
				filterSql += "AND (proLR.provider_no IN (" + providerNoList + ") ) ";
				proLrSql += "AND (proLR_filter.provider_no IN (" + providerNoList + ") ) ";
			}
			
			if ("N".equals(status) || "A".equals(status) || "F".equals(status))
			{
				filterSql += "AND (proLR.status = :status) ";
				proLrSql += "AND (proLR_filter.status = :status) ";
				qp_status = true;
			}
			
			if (!"".equals(patientFirstName))
			{
				filterSql += "AND (CASE "
						             + "WHEN d1.demographic_no IS NOT NULL THEN d1.first_name "
						             + "WHEN d2.demographic_no IS NOT NULL THEN d2.first_name "
									 + "WHEN d3.demographic_no IS NOT NULL THEN d3.first_name "
						             + "ELSE lab.first_name END LIKE :first_name) ";
				labSql += "AND (CASE "
						          + "WHEN d1.demographic_no IS NOT NULL THEN d1.first_name "
						          + "WHEN d2.demographic_no IS NOT NULL THEN d2.first_name "
						          + "ELSE lab_filter.first_name END LIKE :first_name) ";
				qp_first_name = true;
			}
			
			if (!"".equals(patientLastName))
			{
				filterSql += "AND (CASE "
						             + "WHEN d1.demographic_no IS NOT NULL THEN d1.last_name "
						             + "WHEN d2.demographic_no IS NOT NULL THEN d2.last_name "
									 + "WHEN d3.demographic_no IS NOT NULL THEN d3.last_name "
						             + "ELSE lab.last_name END LIKE :last_name) ";
				labSql += "AND (CASE "
						          + "WHEN d1.demographic_no IS NOT NULL THEN d1.last_name "
						          + "WHEN d2.demographic_no IS NOT NULL THEN d2.last_name "
						          + "ELSE lab_filter.last_name END LIKE :last_name) ";
				qp_last_name = true;
			}
			
			if (!"".equals(patientHealthNumber))
			{
				filterSql += "AND (CASE "
						             + "WHEN d1.demographic_no IS NOT NULL THEN d1.hin "
						             + "WHEN d2.demographic_no IS NOT NULL THEN d2.hin "
									 + "WHEN d3.demographic_no IS NOT NULL THEN d3.hin "
						             + "ELSE lab.health_no END LIKE :hin) ";
				labSql += "AND (CASE "
						          + "WHEN d1.demographic_no IS NOT NULL THEN d1.hin "
						          + "WHEN d2.demographic_no IS NOT NULL THEN d2.hin "
						          + "ELSE lab_filter.health_no END LIKE :hin) ";
				qp_hin = true;
			}
			
			if (startDate != null)
			{
				filterSql += "AND proLR.obr_date >= :start_date ";
				proLrSql += "AND proLR_filter.obr_date >= :start_date ";
				qp_start_date = true;
			}
			
			if (endDate != null)
			{
				filterSql += "AND proLR.obr_date <= :end_date + INTERVAL 1 DAY ";
				proLrSql += "AND proLR_filter.obr_date <= :end_date + INTERVAL 1 DAY  ";
				qp_end_date = true;
			}
			
			if ("0".equals(demographicNo))
			{
				filterSql += "AND NOT CAST(CASE     WHEN d1.demographic_no IS NULL AND d2.demographic_no IS NULL AND d3.demographic_no IS NULL THEN false     ELSE true END AS int) ";
			}
			else if (demographicNo != null && !"".equals(demographicNo))
			{
				filterSql += "AND CAST(CASE     WHEN d1.demographic_no IS NULL AND d2.demographic_no IS NULL AND d3.demographic_no IS NULL THEN false     ELSE true END AS int) ";
				filterSql += "AND CASE ";
				filterSql += "  WHEN d1.demographic_no IS NOT NULL THEN d1.demographic_no ";
				filterSql += "  WHEN d2.demographic_no IS NOT NULL THEN d2.demographic_no ";
				filterSql += "  WHEN d3.demographic_no IS NOT NULL THEN d3.demographic_no ";
				filterSql += "  ELSE 0 END = :demographic_no ";
				qp_demographic_no = true;
			}
			
			if (do_counts)
			{
				sql = "SELECT "
						      + "  CAST(demographic_no AS int) AS demographic_no, "
						      + "  first_name, "
						      + "  last_name, "
						      + "  CAST(SUM(IF(result_status = 'A', 1, 0)) AS int) AS abnormal, "
						      + "  CAST(SUM(IF(has_demographic AND demographic_no NOT IN ('0', '-1') AND doctype = 'DOC', 1, 0)) AS int) AS doc_count, "
						      + "  CAST(SUM(IF(has_demographic AND demographic_no NOT IN ('0', '-1') AND doctype = 'HL7', 1, 0)) AS int) AS lab_count, "
						      + "  CAST(SUM(IF((NOT has_demographic OR demographic_no IN ('0', '-1')) AND doctype = 'DOC', 1, 0)) AS int) AS unmatched_doc_count, "
						      + "  CAST(SUM(IF((NOT has_demographic OR demographic_no IN ('0', '-1')) AND doctype = 'HL7', 1, 0)) AS int) AS unmatched_lab_count, "
						      + "  CAST(count(*) AS int) AS total_count "
						      + "FROM ( ";
			}
			
			sql += "SELECT "
					       + "  result_type, "
					       + "  id, "
					       + "  document_no, "
					       + "  status, "
					       + "  provider_no, "
					       + "  doctype, "
					       + "  CAST(has_demographic AS integer) AS has_demographic, "
					       + "  last_name, "
					       + "  first_name, "
					       + "  hin, "
					       + "  sex, "
					       + "  demographic_no, "
					       + "  observationdate, "
					       + "  description, "
					       + "  update_date_time, "
					       + "  uploadedBy, "
					       + "  label, "
					       + "  result_status, "
					       + "  priority, "
					       + "  requesting_client, "
					       + "  discipline, "
					       + "  report_status, "
					       + "  accessionNum, "
					       + "  final_result_count, "
					       + "  report_file, "
						   + "  schema_version "
					       + "FROM ( "
					
					       // This side of the union is for labs and documents.  It is grouped by accession number
					       // because labs can have multiple entries for a single lab.  If any of the records for a lab
					       // are unmatched, the entire lab is marked as unmatched because that is how it is treated
					       // when viewing the lab.
					       + "SELECT "
					       + "  CASE "
					       + "    WHEN lab.id IS NOT NULL THEN '" + LabResultData.HL7TEXT + "'"
					       + "    WHEN doc.document_no IS NOT NULL THEN '" + LabResultData.DOCUMENT + "' "
						   + "    WHEN hrm.id IS NOT NULL THEN '" + LabResultData.HRM + "' "
					       + "    ELSE 'UNKNOWN' "
					       + "  END AS result_type, "
					       + "  proLR.id AS id, "
					       + "  proLR.lab_no AS document_no, "
					       + "  proLR.status, "
					       + "  proLR.provider_no, "
					       + "  proLR.lab_type AS doctype, "
					
					       // There seems to be an inconsistency where a ctl_document.module_id of either 0 or -1
					       // (when module='demographic') indicates that the document is unassigned.  This CASE
					       // deals with that by checking if a demographic record is successfully joined.
					       + "  CAST(CASE "
					       + "    WHEN d1.demographic_no IS NULL AND d2.demographic_no IS NULL AND d3.demographic_no IS NULL THEN false "
					       + "    ELSE true END AS int) as has_demographic, "
					       + "  CASE "
					       + "    WHEN d1.demographic_no IS NOT NULL THEN d1.last_name "
					       + "    WHEN d2.demographic_no IS NOT NULL THEN d2.last_name  "
						   + "    WHEN d3.demographic_no IS NOT NULL THEN d3.last_name "
					       + "    ELSE lab.last_name END AS last_name, "
					       + "  CASE "
					       + "    WHEN d1.demographic_no IS NOT NULL THEN d1.first_name "
					       + "    WHEN d2.demographic_no IS NOT NULL THEN d2.first_name  "
						   + "    WHEN d3.demographic_no IS NOT NULL THEN d3.first_name  "
					       + "    ELSE lab.first_name END AS first_name, "
					       + "  CASE "
					       + "    WHEN d1.demographic_no IS NOT NULL THEN d1.hin "
					       + "    WHEN d2.demographic_no IS NOT NULL THEN d2.hin "
					       + "    WHEN d3.demographic_no IS NOT NULL THEN d3.hin "
					       + "    ELSE lab.health_no END AS hin, "
					       + "  CASE "
					       + "    WHEN d1.demographic_no IS NOT NULL "
					       + "      THEN CASE WHEN d1.sex IN ('F', 'M') THEN d1.sex ELSE '?' END "
					       + "    WHEN d2.demographic_no IS NOT NULL "
					       + "      THEN CASE WHEN d2.sex IN ('F', 'M') THEN d2.sex ELSE '?' END "
					       + "    WHEN d3.demographic_no IS NOT NULL "
					       + "      THEN CASE WHEN d3.sex IN ('F', 'M') THEN d3.sex ELSE '?' END "
					       + "    ELSE CASE WHEN lab.sex IN ('F', 'M') THEN lab.sex ELSE '?' END "
					       + "    END AS sex, "
					       + "  CASE "
					       + "    WHEN d1.demographic_no IS NOT NULL THEN d1.demographic_no "
					       + "    WHEN d2.demographic_no IS NOT NULL THEN d2.demographic_no"
						   + "    WHEN d3.demographic_no IS NOT NULL THEN d3.demographic_no"
					       + "    ELSE 0 "
					       + "  END AS demographic_no, "
					       + "  proLR.obr_date AS observationdate, "
					       + "  doc.doctype AS description, "
					       + "  date(doc.updatedatetime) as update_date_time, "
					       + "  CONCAT(creator.last_name, ', ', creator.first_name) AS uploadedBy, "
					       + "  lab.label, "
					       + "  lab.result_status, "
					       + "  lab.priority, "
					       + "  lab.requesting_client, "
					       + "  lab.discipline, "
					       + "  lab.report_status, "
					       + "  lab.accessionNum, "
					       + "  lab.final_result_count,"
						   + "  CASE "
					       + "    WHEN hrm.id IS NOT NULL THEN hrm.reportFile"
					       + "    ELSE null"
						   + "  END AS report_file, "
						   + "  CASE "
						   + "    WHEN hrm.id IS NOT NULL THEN hrm.reportFileSchemaVersion"
						   + "    ELSE null"
					       + "  END AS schema_version ";
			
			if (neverAcknowledgedItems && "N".equals(status))
			{
				
				sql = sql + "FROM (" +
						      "    SELECT * FROM (" +
						      "        SELECT plr.*" +
						      "        FROM providerLabRouting plr" +
						      "        GROUP BY lab_no, status" +
						      "    ) lab_status_grouped GROUP BY lab_no HAVING count(lab_no) = 1" +
						      ") proLR ";
			}
			else
			{
				
				sql = sql + "FROM providerLabRouting proLR ";
			}
			
			sql += "LEFT JOIN providerLabRouting proLR_filter ON "
					       + "  proLR.lab_no = proLR_filter.lab_no "
					       + "  AND proLR.lab_type = proLR_filter.lab_type "
					       + "  AND ( "
					       + "    proLR.timestamp < proLR_filter.timestamp OR ( "
					       + "      proLR.timestamp = proLR_filter.timestamp AND proLR.id < proLR_filter.id)) "
					       + "  AND proLR_filter.status != 'X' "
					       + proLrSql
					       + "LEFT JOIN patientLabRouting patLR ON ( proLR.lab_type = patLR.lab_type AND proLR.lab_no = patLR.lab_no ) "
					       + "LEFT JOIN patientLabRouting patLR_filter ON ( "
					       + "  proLR.lab_type = patLR_filter.lab_type "
					       + "  AND patLR.lab_no = patLR_filter.lab_no "
					       + "  AND (patLR.dateModified < patLR_filter.dateModified OR (patLR.dateModified = patLR_filter.dateModified AND patLR.id < patLR_filter.id))) "
					       + "LEFT JOIN document doc ON ( proLR.lab_type = 'DOC' AND proLR.lab_no = doc.document_no AND doc.status <> 'D' ) "
					       + "LEFT JOIN provider creator ON ( doc.doccreator = creator.provider_no ) "
					       + "LEFT JOIN ctl_document cdoc ON ( doc.document_no = cdoc.document_no AND cdoc.module='demographic' AND cdoc.module_id > 0) "
					       + "LEFT JOIN demographic d2 ON ( cdoc.module_id IS NOT NULL AND cdoc.module_id = d2.demographic_no ) "
					
					       + "LEFT JOIN demographic d1 ON ( patLR.demographic_no = d1.demographic_no ) "
					       + "LEFT JOIN hl7TextInfo lab ON ( proLR.lab_type = 'HL7' AND lab.lab_no = proLR.lab_no ) "
					       + "LEFT JOIN hl7TextInfo lab_filter ON  "
					       + "  lab.accessionNum = lab_filter.accessionNum "
					       + "  AND (lab.obr_date < lab_filter.obr_date OR ("
					       + "    lab.obr_date = lab_filter.obr_date AND lab.id < lab_filter.id)) "
					       + labSql
					
					       + "LEFT JOIN HRMDocument hrm ON ( proLR.lab_type ='HRM' AND proLR.lab_no = hrm.id ) "
					       + "LEFT JOIN HRMDocumentToProvider hrmProv ON hrm.id = hrmProv.hrmDocumentId "
					       + "LEFT JOIN HRMDocumentToDemographic hrmDemo ON hrm.id = hrmDemo.hrmDocumentId "
						   + "LEFT JOIN demographic d3 ON hrmDemo.demographicNo = d3.demographic_no "

					+ "WHERE proLR.lab_type IN ('DOC', 'HL7', 'HRM') "
					+ "AND proLR_filter.id IS NULL "
					+ "AND patLR_filter.id IS NULL "
					+ "AND lab_filter.id IS NULL "
					+ "AND (doc.document_no IS NOT NULL OR lab.id IS NOT NULL OR hrm.id IS NOT NULL) "
					+ "AND proLR.status != 'X' "
					+ filterSql;
			
			sql = sql + "  "
					+ "ORDER BY observationdate desc, document_no desc ";

			if (isPaged) {
				sql = sql + "LIMIT :page_start, :page_size";
				qp_page = true;
			}
			sql = sql + ") AS the_result ";

			if(do_counts) {
				sql += ") AS count_query "
						+ "GROUP BY demographic_no, first_name, last_name ";
			}

			q = entityManager.createNativeQuery(sql);

			if (qp_provider_no) { q.setParameter("provider_no", providerNo); }
			if (qp_status) { q.setParameter("status", status); }
			if (qp_first_name) { q.setParameter("first_name", "%" + patientFirstName + "%"); }
			if (qp_last_name) { q.setParameter("last_name", "%" + patientLastName + "%"); }
			if (qp_hin) { q.setParameter("hin", "%" + patientHealthNumber + "%"); }
			if (qp_lab_type) { q.setParameter("lab_type", labType); }
			if (qp_start_date) { q.setParameter("start_date", startDate, TemporalType.DATE); }
			if (qp_end_date) { q.setParameter("end_date", endDate, TemporalType.DATE); }
			if (qp_demographic_no) { q.setParameter("demographic_no", demographicNo); }
			if (qp_page) {
				q.setParameter("page_start", page * pageSize);
				q.setParameter("page_size", pageSize);
			}

			logger.debug("QUERY: " + sql);

			result = q.getResultList();

		} catch (Exception e) {
			logger.error("exception in DOCPopulate:", e);
		}

		return result;
	}

	public LabSummaryData getInboxSummary(
			String providerNo,
			String demographicNo,
			String patientFirstName,
			String patientLastName,
			String patientHealthNumber,
			String status,
			boolean isPaged,
			Integer page,
			Integer pageSize,
			boolean mixLabsAndDocs,
			Boolean isAbnormal,
			List<String> providerNoArr,
			boolean neverAcknowledgedItems,
			String labType,
			Date startDate,
			Date endDate)
	{

		int pos = 0;
		int demographicLoc = pos++;
		int firstNameLoc = pos++;
		int lastNameLoc = pos++;
		int abnormalCountLoc = pos++;
		int docCountLoc = pos++;
		int labCountLoc = pos++;
		int unmatchedDocCountLoc = pos++;
		int unmatchedLabCountLoc = pos++;
		int totalCountLoc = pos++;

		HashMap<Integer,PatientInfo> patients = new HashMap<Integer,PatientInfo>();

		int docCount = 0;
		int labCount = 0;
		int abnormalCount = 0;
		int unmatchedDocCount = 0;
		int unmatchedLabCount = 0;
		int totalCount = 0;

		try {

			List<Object[]> result = getRawInboxResults(true, providerNo, demographicNo, patientFirstName,
					patientLastName, patientHealthNumber, status, isPaged, page,
					pageSize, isAbnormal, providerNoArr, neverAcknowledgedItems,
					labType, startDate, endDate);

			for (Object[] r : result) {

				docCount += getIntValue(r[docCountLoc]);
				labCount += getIntValue(r[labCountLoc]);
				abnormalCount += getIntValue(r[abnormalCountLoc]);
				unmatchedDocCount += getIntValue(r[unmatchedDocCountLoc]);
				unmatchedLabCount += getIntValue(r[unmatchedLabCountLoc]);
				totalCount += getIntValue(r[totalCountLoc]);

				if(r[demographicLoc] != null && getIntValue(r[demographicLoc]) != 0) {
					PatientInfo info = new PatientInfo(getIntValue(r[demographicLoc]),
							getStringValue(r[firstNameLoc]), getStringValue(r[lastNameLoc]));

					info.setDocCount(getIntValue(r[docCountLoc]));
					info.setLabCount(getIntValue(r[labCountLoc]));

					patients.put(info.id, info);
				}
			}
		} catch (Exception e) {

			logger.error("exception in DOCPopulate:", e);
		}

		LabSummaryData summary = new LabSummaryData();

		summary.setPatients(patients);
		summary.setDocumentCount(docCount);
		summary.setLabCount(labCount);
		summary.setAbnormalCount(abnormalCount);
		summary.setUnmatchedDocumentCount(unmatchedDocCount);
		summary.setUnmatchedLabCount(unmatchedLabCount);
		summary.setTotalCount(totalCount);

		return summary;
	}

	@SuppressWarnings({ "deprecation" })
	public ArrayList<LabResultData> getInboxResults(
			LoggedInInfo loggedInInfo,
			String providerNo,
			String demographicNo,
			String patientFirstName,
			String patientLastName,
			String patientHealthNumber,
			String status,
			boolean isPaged,
			Integer page,
			Integer pageSize,
			boolean mixLabsAndDocs,
			Boolean isAbnormal,
			List<String> providerNoArr,
			boolean neverAcknowledgedItems,
			String labType,
			Date startDate,
			Date endDate)
	{
		int pos = 0;
		int resultTypeLoc = pos++;
		int idLoc = pos++;
		int docNoLoc = pos++;
		int statusLoc = pos++;
		int providerNoLoc = pos++;
		int docTypeLoc = pos++;
		int hasDemographicLoc = pos++;
		int lastNameLoc = pos++;
		int firstNameLoc = pos++;
		int hinLoc = pos++;
		int sexLoc = pos++;
		int moduleLoc = pos++;
		int obsDateLoc = pos++;
		int descriptionLoc = pos++;
		int updateDateLoc = pos++;
		int docUploadedByLoc = pos++;
		int labelLoc = pos++;
		int resultStatusLoc = pos++;
		int priorityLoc = pos++;
		int requestingClientLoc = pos++;
		int disciplineLoc = pos++;
		int reportStatusLoc = pos++;
		int accessionNumLoc = pos++;
		int finalResultCountLoc = pos++;
		int hrmReportFileLoc = pos++;
		int schemaVersion = pos++;
		
		HRMReport hrmReport = null;
		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();

		try {
			List<Object[]> result = getRawInboxResults(false, providerNo, demographicNo, patientFirstName,
					patientLastName, patientHealthNumber, status, isPaged, page,
					pageSize, isAbnormal, providerNoArr, neverAcknowledgedItems,
					labType, startDate, endDate);

			for (Object[] r : result) {

				String resultType = getStringValue(r[resultTypeLoc]);

				LabResultData lbData = null;
				if(LabResultData.DOCUMENT.equals(resultType)) {
					lbData = new LabResultData(LabResultData.DOCUMENT);
					lbData.labType = LabResultData.DOCUMENT;

					lbData.accessionNumber = "";
					lbData.resultStatus = "N";
					lbData.requestingClient = "";
					lbData.reportStatus = "F";

					lbData.discipline = StringUtils.trimToNull(getStringValue(r[docTypeLoc]));
					lbData.description = StringUtils.trimToNull(getStringValue(r[descriptionLoc]));
					lbData.lastUpdateDate = getStringValue(r[updateDateLoc]);

					lbData.finalResultsCount = 0;
				}
				else if(LabResultData.HL7TEXT.equals(resultType)) {

					String label = getStringValue(r[labelLoc]);
					String requesting_client = getStringValue(r[requestingClientLoc]);
					String discipline = getStringValue(r[disciplineLoc]);
					String report_status = getStringValue(r[reportStatusLoc]);
					String accessionNum = getStringValue(r[accessionNumLoc]);
					String final_result_count = getStringValue(r[finalResultCountLoc]);

					lbData = new LabResultData(LabResultData.HL7TEXT);
					lbData.labType = LabResultData.HL7TEXT;

					lbData.accessionNumber = accessionNum;
					lbData.label = label;

					lbData.requestingClient = requesting_client;
					lbData.reportStatus = report_status;

					lbData.discipline = discipline;
					lbData.finalResultsCount = ConversionUtils.fromIntString(final_result_count);
				}
				else if(LabResultData.HRM.equals(resultType)) {
					lbData = new LabResultData(LabResultData.HRM);
					lbData.labType = LabResultData.HRM;

					lbData.priority = "----";
					lbData.requestingClient = "";
					lbData.discipline = "HRM";

					hrmReport = HRMReportParser.parseRelativeLocation(getStringValue(r[hrmReportFileLoc]), getStringValue(r[schemaVersion]));
					lbData.reportStatus = hrmReport.getResultStatus();
				}

				// The query is built to ignore entries that are not a lab, hrm or document.
				if(lbData == null) {
					continue;
				}

				lbData.segmentID = getStringValue(r[docNoLoc]);

				if (demographicNo == null && !providerNo.equals("0") && !LabResultData.HRM.equals(resultType)) {
					lbData.acknowledgedStatus = getStringValue(r[statusLoc]);
				} else {
					lbData.acknowledgedStatus = "U";
				}

				lbData.healthNumber = "";
				lbData.patientName = "Not, Assigned";
				lbData.sex = "";

				lbData.isMatchedToPatient = getBooleanValue(r[hasDemographicLoc]);

				if (r[lastNameLoc] != null) {
					lbData.patientName = getStringValue(r[lastNameLoc]) + ", " + getStringValue(r[firstNameLoc]);
					lbData.healthNumber = getStringValue(r[hinLoc]);
					lbData.sex = getStringValue(r[sexLoc]);
					lbData.setLabPatientId(getStringValue(r[moduleLoc]));
				}
				else if(hrmReport != null && LabResultData.HRM.equals(resultType)) {
					lbData.patientName = hrmReport.getLegalName();
					lbData.healthNumber = hrmReport.getHCN();
					lbData.sex = hrmReport.getGender();
				}
				else {
					lbData.patientName = "Not, Assigned";
				}

				lbData.resultStatus = getStringValue(r[resultStatusLoc]);
				if (lbData.resultStatus != null && lbData.resultStatus.equals("A")) {
					lbData.abn = true;
				}

				logger.debug("DOCUMENT " + lbData.isMatchedToPatient());

				lbData.dateTime = getStringValue(r[obsDateLoc]);

				lbData.setDateObj(UtilDateUtilities.StringToDate(getStringValue(r[obsDateLoc])));

				String priority = getStringValue(r[priorityLoc]);
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

				// the "C" is for corrected Excelleris labs
				if (lbData.reportStatus != null)
				{
					switch(lbData.reportStatus)
					{
						case "C":
							lbData.correctedRes = true;
							break;
						case "F":
							lbData.finalRes = true;
							break;
						case "X":
							lbData.cancelledReport = true;
							break;
						default:
							lbData.finalRes = false;
							break;
					}
				}
				else
				{
					lbData.finalRes = false;
				}

				labResults.add(lbData);
			}

		} catch (Exception e) {
			logger.error("exception in DOCPopulate:", e);
		}

		return labResults;
	}

	private String getStringValue(Object value)
	{
		return value != null ? value.toString() : null;
	}

	private int getIntValue(Object value)
	{
		if(value == null)
		{
			return 0;
		}

		if(value instanceof Integer)
		{
			return ((Integer) value).intValue();
		}
		else if(value instanceof BigInteger)
		{
			return ((BigInteger) value).intValue();
		}
		return 0;
	}

	private boolean getBooleanValue(Object value)
	{
		if(value == null)
		{
			return false;
		}

		int flag = ((BigInteger) value).intValue();
		if(flag <= 0)
		{
			return false;
		}
		return true;
	}
}
