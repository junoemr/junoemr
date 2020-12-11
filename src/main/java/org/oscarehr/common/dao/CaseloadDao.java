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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oscarehr.caseload.CaseloadCategory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CaseloadDao {

	@PersistenceContext
	protected EntityManager entityManager = null;

	public CaseloadDao() {
		if (caseloadSearchQueries == null) { initializeSearchQueries(); }
		if (caseloadSortQueries == null) { initializeSortQueries(); }
	}

	private static HashMap<String,String> caseloadSearchQueries;

	private static void initializeSearchQueries() {
		caseloadSearchQueries = new HashMap<String, String>();
		caseloadSearchQueries.put("search_notes", "select distinct Z.demographic_no, Z.last_name, Z.first_name FROM (select distinct demographic_no, first_name, last_name, year_of_birth, month_of_birth, date_of_birth, sex from demographic left join demographiccust using (demographic_no) where (provider_no='%s' or cust1='%s' or cust2='%s' or cust4='%s') and patient_status not in ('FI','MO','DE','IN')) as Z INNER JOIN casemgmt_note using (demographic_no) where note like '%s' and locked <> '1'");		
		
		caseloadSearchQueries.put("search_allpg_alldemo_rodxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where dx.dxresearch_code='%s' and dx.status='A' and d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		caseloadSearchQueries.put("search_allpg_alldemo_dxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where dx.dxresearch_code='%s' and dx.status='A' and d.patient_status not in ('FI','MO','DE','IN') and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		caseloadSearchQueries.put("search_allpg_alldemo_rofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join admission ad on (ad.client_id=d.demographic_no) where d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		caseloadSearchQueries.put("search_allpg_alldemo_nofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join admission ad on (ad.client_id=d.demographic_no) where d.patient_status not in ('FI','MO','DE','IN') and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");

		caseloadSearchQueries.put("search_allpg_provdemo_rodxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join demographiccust dc using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where (d.provider_no='%s' or dc.cust1='%s' or dc.cust2='%s' or dc.cust4='%s') and d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and dx.dxresearch_code='%s' and dx.status='A' and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		caseloadSearchQueries.put("search_allpg_provdemo_dxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join demographiccust dc using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where (d.provider_no='%s' or dc.cust1='%s' or dc.cust2='%s' or dc.cust4='%s') and d.patient_status not in ('FI','MO','DE','IN') and dx.dxresearch_code='%s' and dx.status='A' and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		caseloadSearchQueries.put("search_allpg_provdemo_rofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join demographiccust dc using (demographic_no)  left join admission ad on (ad.client_id=d.demographic_no) where (d.provider_no='%s' or dc.cust1='%s' or dc.cust2='%s' or dc.cust4='%s') and d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		caseloadSearchQueries.put("search_allpg_provdemo_nofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join demographiccust dc using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where (d.provider_no='%s' or dc.cust1='%s' or dc.cust2='%s' or dc.cust4='%s') and d.patient_status not in ('FI','MO','DE','IN') and ad.program_id in (select distinct pg.id from program pg, program_provider pp where pp.program_id=pg.id and pg.facilityId=%d)");
		
		caseloadSearchQueries.put("search_alldemo_rodxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where dx.dxresearch_code='%s' and dx.status='A' and d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and ad.program_id=%d");
		caseloadSearchQueries.put("search_alldemo_dxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where dx.dxresearch_code='%s' and dx.status='A' and d.patient_status not in ('FI','MO','DE','IN') and ad.program_id=%d");
		caseloadSearchQueries.put("search_alldemo_rofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join admission ad on (ad.client_id=d.demographic_no) where d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and ad.program_id=%d");
		caseloadSearchQueries.put("search_alldemo_nofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join admission ad on (ad.client_id=d.demographic_no) where d.patient_status not in ('FI','MO','DE','IN') and ad.program_id=%d");
		
		caseloadSearchQueries.put("search_provdemo_rodxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join demographiccust dc using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no ) where (d.provider_no='%s' or dc.cust1='%s' or dc.cust2='%s' or dc.cust4='%s') and d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and dx.dxresearch_code='%s' and dx.status='A' and ad.program_id=%d");
		caseloadSearchQueries.put("search_provdemo_dxfilter", "select distinct d.demographic_no, d.last_name, d.first_name from dxresearch dx left join demographic d using (demographic_no) left join demographiccust dc using (demographic_no) left join admission ad on (ad.client_id=d.demographic_no) where (d.provider_no='%s' or dc.cust1='%s' or dc.cust2='%s' or dc.cust4='%s')  and d.patient_status not in ('FI','MO','DE','IN') and dx.dxresearch_code='%s' and dx.status='A' and ad.program_id=%d");
		caseloadSearchQueries.put("search_provdemo_rofilter", "select distinct d.demographic_no, d.last_name, d.first_name from demographic d left join demographiccust dc using (demographic_no) left join admission ad on (ad.client_id = d.demographic_no) where (d.provider_no='%s' OR dc.cust1='%s' OR dc.cust2='%s' OR dc.cust4='%s') and d.patient_status not in ('FI','MO','DE','IN') and d.roster_status='%s' and ad.program_id=%d");
		caseloadSearchQueries.put("search_provdemo_nofilter", "SELECT DISTINCT d.demographic_no, d.last_name, d.first_name FROM demographic d LEFT JOIN demographiccust dc USING (demographic_no) left join admission ad on (ad.client_id = d.demographic_no) WHERE (d.provider_no='%s' OR dc.cust1='%s' OR dc.cust2='%s' OR dc.cust4='%s') AND d.patient_status NOT IN ('FI','MO','DE','IN') AND ad.program_id=%d");
	}

	private static HashMap<String,String> caseloadSortQueries;

	private static void initializeSortQueries() {
		caseloadSortQueries = new HashMap<String, String>();
		caseloadSortQueries.put("cl_search_demographic_query", "select demographic_no, last_name, first_name, sex, month_of_birth, date_of_birth, CAST((DATE_FORMAT(NOW(), '%Y') - DATE_FORMAT(concat(year_of_birth,month_of_birth,date_of_birth), '%Y') - (DATE_FORMAT(NOW(), '00-%m-%d') < DATE_FORMAT(concat(year_of_birth,month_of_birth,date_of_birth), '00-%m-%d'))) as UNSIGNED INTEGER) as age from demographic");
		caseloadSortQueries.put("cl_search_last_appt", "SELECT p.demographic_no, max(appointment_date) appointment_date FROM appointment p where addtime(appointment_date, start_time) < now() GROUP BY p.demographic_no");
		caseloadSortQueries.put("cl_search_next_appt", "SELECT p.demographic_no, min(appointment_date) appointment_date FROM appointment p where addtime(appointment_date, start_time) > now() GROUP BY p.demographic_no");
		caseloadSortQueries.put("cl_search_num_appts", "select demographic_no, count(1) as count from appointment where appointment_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 YEAR) AND CURDATE() group by demographic_no");
		caseloadSortQueries.put("cl_search_new_labs", "select demographic_no, count(1) as count from providerLabRouting left join patientLabRouting using (lab_no) where providerLabRouting.lab_type='HL7' and status='N' and provider_no='%s' group by demographic_no");
		caseloadSortQueries.put("cl_search_new_docs", "select demographic_no, count(1) as count from providerLabRouting left join patientLabRouting using (lab_no) where providerLabRouting.lab_type='DOC' and status='N' and provider_no='%s' group by demographic_no");
		caseloadSortQueries.put("cl_search_new_ticklers", "select demographic_no, count(1) as count from tickler where status='A' group by demographic_no");
		caseloadSortQueries.put("cl_search_new_msgs", "select demographic_no, count(1) as count from msgDemoMap left join messagelisttbl on message = messageID where status='new' group by demographic_no");		
		caseloadSortQueries.put("cl_search_measurement", "SELECT m.demographicNo AS demographic_no, m.dataField AS dataField FROM measurements m JOIN (SELECT demographicNo, MAX(dateObserved) AS dateObserved FROM measurements m1 WHERE type='%s' GROUP BY demographicNo ORDER BY dateEntered DESC) m1 ON m.demographicNo=m1.demographicNo AND m.dateObserved=m1.dateObserved WHERE m.type='%s' GROUP BY m.demographicNo");
		
		caseloadSortQueries.put("cl_search_lastencdate", "SELECT cn.demographic_no, cn.update_date FROM casemgmt_note cn JOIN (SELECT note_id, demographic_no, MAX(update_date) FROM casemgmt_note GROUP BY demographic_no) cn2 ON cn.note_id=cn2.note_id");
		caseloadSortQueries.put("cl_search_lastenctype", "SELECT cn.demographic_no, cn.encounter_type FROM casemgmt_note cn JOIN (SELECT note_id, demographic_no, MAX(update_date) FROM casemgmt_note GROUP BY demographic_no) cn2 ON cn.note_id=cn2.note_id ");
	}

	private String getFormatedSearchQuery(String searchQuery, String[] searchParams) {
		if ("search_notes".equals(searchQuery)){
			return String.format(caseloadSearchQueries.get(searchQuery), (Object[])searchParams);
		} else {
			if (searchParams.length > 1) {
				Object[] tempParms = new Object[searchParams.length];
				System.arraycopy(searchParams, 0, tempParms, 0, searchParams.length - 1);
				tempParms[searchParams.length - 1] = Integer.parseInt(searchParams[searchParams.length - 1]);
				return String.format(caseloadSearchQueries.get(searchQuery), tempParms);
			} else {
				return String.format(caseloadSearchQueries.get(searchQuery), Integer.parseInt(searchParams[0]));
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Integer> getCaseloadDemographicSet(String searchQuery, String[] searchParams, String[] sortParams, CaseloadCategory category, String sortDir, int page, int pageSize) {

		String demoQuery = "";
		String sortQuery = "";
		String query = "";
		demoQuery = getFormatedSearchQuery(searchQuery,searchParams);
		if (category == CaseloadCategory.Demographic) {
			query = demoQuery + String.format(" ORDER BY last_name %s, first_name %s LIMIT %d, %d", sortDir, sortDir, page * pageSize, pageSize);
		} else if (category == CaseloadCategory.Age) {
			int split = demoQuery.indexOf(",", demoQuery.indexOf("demographic_no"));
			query = demoQuery.substring(0,split) + ", TIMESTAMPDIFF(YEAR, CONCAT(year_of_birth, '-', month_of_birth, '-', date_of_birth), current_date) AS age " + demoQuery.substring(split) + String.format(" ORDER BY ISNULL(age) ASC, age %s, last_name %s, first_name %s LIMIT %d, %d", sortDir, sortDir, sortDir, page * pageSize, pageSize);
		} else if (category == CaseloadCategory.Sex) {
			int split = demoQuery.indexOf(",", demoQuery.indexOf("demographic_no"));
			query = demoQuery.substring(0,split) + ", sex " + demoQuery.substring(split) + String.format(" ORDER BY sex = '' ASC, sex %s, last_name %s, first_name %s LIMIT %d, %d", sortDir, sortDir, sortDir, page * pageSize, pageSize);
		} else {
			sortQuery = sortParams != null ? String.format(caseloadSortQueries.get(category.getQuery()), (Object[])sortParams) : caseloadSortQueries.get(category.getQuery());
			if (category.isMeasurement()) {
				// Note: This query in particular seems to really, really struggle when casting BP as a decimal
				query = String.format("SELECT Y.demographic_no, Y.last_name, Y.first_name, X.%s FROM (%s) as Y LEFT JOIN (%s) as X on Y.demographic_no = X.demographic_no ORDER BY ISNULL(X.%s) ASC, CAST(X.%s as DECIMAL(10,4)) %s, Y.last_name %s, Y.first_name %s LIMIT %d, %d",
						category.getField(), demoQuery, sortQuery, category.getField(), category.getField(), sortDir, sortDir, sortDir, page * pageSize, pageSize);
			} else {
				query = String.format("SELECT Y.demographic_no, Y.last_name, Y.first_name, X.%s FROM (%s) as Y LEFT JOIN (%s) as X on Y.demographic_no = X.demographic_no ORDER BY ISNULL(X.%s) ASC, X.%s %s, Y.last_name %s, Y.first_name %s LIMIT %d, %d",
						category.getField(), demoQuery, sortQuery, category.getField(), category.getField(), sortDir, sortDir, sortDir, page * pageSize, pageSize);
			}
		}
		Query q = entityManager.createNativeQuery(query);

		List<Object[]> result = q.getResultList();

		List<Integer> demographicNoList = new ArrayList<Integer>();
		for (Object[] r : result) {
			demographicNoList.add((Integer) r[0]);
		}

		return demographicNoList;
	}

	@SuppressWarnings("unchecked")
	public Integer getCaseloadDemographicSearchSize(String searchQuery, String[] searchParams) {

		String demoQuery ="";
		String query = "";

		demoQuery = getFormatedSearchQuery(searchQuery,searchParams);
		query = String.format("SELECT count(1) AS count FROM (%s) AS X", demoQuery);

		Query q = entityManager.createNativeQuery(query);
		List<BigInteger> result = q.getResultList();

		return result.get(0).intValue();
	}

	/**
	 * Given text to look for in a note, find all demographics for a given MRP that have a similar-looking note.
	 * Note: doing a search like this
	 * @param providerNo provider whose caseload we want to populate
	 * @param note the note text to search for
	 * @param page where to begin searching
	 * @param pageSize the number of results we want
	 * @return a list of demographics, ordered by demographicNo, that have the given MRP and a similar-looking note
	 */
	public List<Integer> searchDemographicSetByNote(String providerNo, String note, int page, int pageSize)
	{
		String sql = "SELECT DISTINCT Z.demographic_no " +
				"FROM (" +
				"    SELECT DISTINCT d.demographic_no, d.last_name, d.first_name " +
				"    FROM demographic d " +
				"    LEFT JOIN demographiccust dc " +
				"    ON d.demographic_no=dc.demographic_no " +
				"    WHERE (d.provider_no=:providerNo OR dc.cust1=:providerNo OR dc.cust2=:providerNo OR dc.cust4=:providerNo) " +
				"    AND d.patient_status NOT IN ('FI', 'MO', 'DE', 'IN') " +
				") as Z " +
				"INNER JOIN casemgmt_note cn " +
				"ON Z.demographic_no=cn.demographic_no  " +
				"WHERE cn.note LIKE CONCAT('%', :noteSearch, '%') AND cn.locked <> '1' " +
				"LIMIT :page, :pageSize ";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("providerNo", providerNo);
		query.setParameter("noteSearch", note);
		query.setParameter("page", page * pageSize);
		query.setParameter("pageSize", pageSize);

		return (List<Integer>)query.getResultList();
	}

	/**
	 * Given a demographic, get the number of labs that have newly come in and haven't been handled yet.
	 * @param providerNo provider whose caseload is being queried
	 * @param demographicNo the demographic number to search against
	 * @return the number of unhandled labs for the given demographic
	 */
	public Integer getNumberNewLabs(String providerNo, int demographicNo)
	{
		String sql = "SELECT COUNT(*) " +
				"FROM providerLabRouting " +
				"LEFT JOIN patientLabRouting USING (lab_no) " +
				"WHERE providerLabRouting.lab_type='HL7' " +
				"AND status='N' " +
				"AND provider_no=:providerNo " +
				"AND demographic_no=:demographicNo";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("providerNo", providerNo);
		query.setParameter("demographicNo", demographicNo);

		BigInteger numResults = (BigInteger) query.getSingleResult();
		return numResults.intValue();
	}

	/**
	 * Given a demographic, get the number of new documents associated with the demographic.
	 * @param providerNo provider whose caseload is being queried
	 * @param demographicNo the demographic number to search against
	 * @return the number of unhandled documents for the given demographic
	 */
	public Integer getNumNewDocs(String providerNo, int demographicNo)
	{
		String sql = "SELECT COUNT(*) " +
				"FROM providerLabRouting " +
				"LEFT JOIN patientLabRouting USING (lab_no) " +
				"WHERE providerLabRouting.lab_type='DOC' " +
				"AND status='N' " +
				"AND provider_no=:providerNo " +
				"AND demographic_no=:demographicNo";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("providerNo", providerNo);
		query.setParameter("demographicNo", demographicNo);

		BigInteger numResults = (BigInteger) query.getSingleResult();
		return numResults.intValue();
	}

	/**
	 * Get the last date we had an encounter note written on.
	 * @param demographicNo the demographic to search against
	 * @return a Date for the last encounter note, or null if we can't find one
	 */
	public Date getLastEncounterDate(int demographicNo)
	{
		String sql = "SELECT MAX(update_date) " +
				"FROM casemgmt_note " +
				"WHERE update_date < NOW() " +
				"AND demographic_no=:demographicNo";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("demographicNo", demographicNo);

		return (Date)query.getSingleResult();
	}

	/**
	 * Given a demographic, get the last type of encounter note that was written
	 * @param demographicNo the demographic to search against
	 * @return the type of encounter that was last had, or null if we can't find one
	 */
	public String getLastEncounterType(int demographicNo)
	{
		String sql = "SELECT encounter_type " +
				"FROM casemgmt_note AS c " +
				"WHERE c.demographic_no=:demographicNo " +
				"AND NOT EXISTS (" +
				"    SELECT * " +
				"    FROM casemgmt_note " +
				"    WHERE demographic_no=:demographicNo " +
				"    AND update_date > c.update_date " +
				")";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("demographicNo", demographicNo);

		// We don't have access to our usual getSingleResultOrNull() wrapper here due to this being a native query
		try
		{
			return (String)query.getSingleResult();
		}
		catch (NoResultException e)
		{
			return null;
		}
	}

}
