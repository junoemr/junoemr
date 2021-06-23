/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.casemgmt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementSearchBean;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.EncounterUtil;
import org.oscarehr.util.MiscUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate5.HibernateTemplate;
import oscar.OscarProperties;
import oscar.util.SqlUtils;

/**
 * @deprecated use the jpa version instead
 */
@Deprecated
public class CaseManagementNoteDAO extends HibernateDaoSupport {

	private static Logger log = MiscUtils.getLogger();

	@SuppressWarnings("unchecked")
    public List<Provider> getEditors(CaseManagementNote note) {
		String uuid = note.getUuid();
		String hql = "select distinct p from Provider p, CaseManagementNote cmn where p.ProviderNo = cmn.providerNo and cmn.uuid = ?0";
		return (List<Provider>) this.getHibernateTemplate().find(hql, uuid);
	}

	@SuppressWarnings("unchecked")
    public List<Provider> getAllEditors(String demographicNo) {
		String hql = "select distinct p from Provider p, CaseManagementNote cmn where p.ProviderNo = cmn.providerNo and cmn.demographic_no = ?0";
		return (List<Provider>) this.getHibernateTemplate().find(hql, demographicNo);
	}

	@SuppressWarnings("unchecked")
	public List<CaseManagementNote> getHistory(CaseManagementNote note) {
		String uuid = note.getUuid();
		return (List<CaseManagementNote>) this.getHibernateTemplate().find("from CaseManagementNote cmn where cmn.uuid = ?0 order by cmn.update_date asc", uuid);
	}

	@SuppressWarnings("unchecked")
	public List<CaseManagementNote> getIssueHistory(String issueIds, String demoNo) {
		return getNotesByDemographicIssues(demoNo, issueIds.split(","), false, null);
	}

	public CaseManagementNote getNote(Long id) {
		CaseManagementNote note = this.getHibernateTemplate().get(CaseManagementNote.class, id);
		getHibernateTemplate().initialize(note.getIssues());
		return note;
	}
	
	public List<CaseManagementNote> getNotes(List<Long> ids) {
		if(ids.size()==0)
			return new ArrayList<CaseManagementNote>();
		@SuppressWarnings("unchecked")
		List<CaseManagementNote> notes = (List<CaseManagementNote>) this.getHibernateTemplate().findByNamedParam("SELECT n FROM CaseManagementNote n WHERE n.id IN (:ids)","ids",ids);
		return notes;
	}

	public CaseManagementNote getMostRecentNote(String uuid) {
		String hql = "select distinct cmn from CaseManagementNote cmn where cmn.uuid = ?0 and cmn.id = (select max(cmn.id) from cmn where cmn.uuid = ?1)";
		@SuppressWarnings("unchecked")
		List<CaseManagementNote> tmp = (List<CaseManagementNote>) this.getHibernateTemplate().find(hql, new Object[] { uuid, uuid });
		if (tmp == null) return null;

		return tmp.get(0);
	}

	public List<CaseManagementNote> getNotesByUUID(String uuid) {
		String hql = "select cmn from CaseManagementNote cmn where cmn.uuid = ?0 order by cmn.id";
		@SuppressWarnings("unchecked")
		List<CaseManagementNote> ret = (List<CaseManagementNote>) this.getHibernateTemplate().find(hql, uuid);
		return ret;
	}


	public List<CaseManagementNote> getCPPNotes(String demoNo, long issueId, String staleDate) {
		return getNotesByDemographic(demoNo, new String[] { Long.toString(issueId) }, staleDate);
	}

	public List<CaseManagementNote> getNotesByDemographic(String demographicNo, String[] issues, String staleDate) {
		Date observationDate;
		GregorianCalendar cal = new GregorianCalendar(1970, 1, 1);
		if (staleDate != null) {

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				observationDate = formatter.parse(staleDate);
			}
			catch (ParseException e) {
				observationDate = cal.getTime();
				MiscUtils.getLogger().error("Error", e);
			}
		}
		else {
			observationDate = cal.getTime();
		}

		return getNotesByDemographicIssues(demographicNo, issues, false, null,
				observationDate, true);
	}

	@SuppressWarnings("unchecked")
    public List<CaseManagementNote> getNotesByDemographic(String demographic_no, String staleDate) {
		return (List<CaseManagementNote>) getHibernateTemplate().findByNamedQuery("mostRecentTime", new Object[] { demographic_no, staleDate });
	}

	// This was created by OSCAR. if all notes' UUID are same like null, it will only get one note.
	@SuppressWarnings("unchecked")
    public List<CaseManagementNote> getNotesByDemographic(String demographic_no)
	{
		String hql = "select cmn from CaseManagementNote cmn " +
				"where cmn.demographic_no = ?0 " +
				"and cmn.id = (select max(cmn2.id) from CaseManagementNote cmn2 where cmn2.uuid = cmn.uuid) " +
				"order by cmn.observation_date";
		return (List<CaseManagementNote>) getHibernateTemplate().find(hql, demographic_no);
	}
	
	public List<CaseManagementNote> getNotesByDemographicSince(String demographic_no,Date date)
	{
		String hql = "select cmn from CaseManagementNote cmn " +
				"where cmn.demographic_no = ?0 " +
				"and cmn.update_date > ?1 and cmn.locked != '1' " +
				"and cmn.id = (select max(cmn2.id) from CaseManagementNote cmn2 where cmn2.uuid = cmn.uuid) " +
				"order by cmn.observation_date";
		return (List<CaseManagementNote>) getHibernateTemplate().find(hql, demographic_no, date);
	}
	
	
	public long getNotesCountByDemographicId(String demographic_no) {
		String hql = "select count(*) from CaseManagementNote cmm where cmm.demographic_no = ?0";
		return ((Long)getHibernateTemplate().find(hql, demographic_no).get(0)).longValue();
	}
	
		@SuppressWarnings("unchecked")
	    public List<Object[]> getRawNoteInfoByDemographic(String demographic_no) {
			String hql = "select cmn.id,cmn.observation_date,cmn.providerNo,cmn.program_no,cmn.reporter_caisi_role,cmn.uuid from CaseManagementNote cmn where cmn.demographic_no = ?0 order by cmn.update_date DESC";
			return (List<Object[]>) getHibernateTemplate().find(hql, demographic_no);
		}
	    
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getRawNoteInfoMapByDemographic(String demographic_no) {
		String hql = "select new map(cmn.id as id,cmn.observation_date as observation_date,cmn.providerNo as providerNo,cmn.program_no as program_no,cmn.reporter_caisi_role as reporter_caisi_role,cmn.uuid as uuid, cmn.update_date as update_date) from CaseManagementNote cmn where cmn.demographic_no = ?0 order by cmn.update_date DESC, cmn.id DESC";
		return (List<Map<String, Object>>) getHibernateTemplate().find(hql, demographic_no);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getUnsignedRawNoteInfoMapByDemographic(String demographic_no) {
		String hql = "select new map(cmn.id as id,cmn.observation_date as observation_date,cmn.providerNo as providerNo,cmn.program_no as program_no,cmn.reporter_caisi_role as reporter_caisi_role,cmn.uuid as uuid, cmn.update_date as update_date) from CaseManagementNote cmn where cmn.demographic_no = ?0 and cmn.signed=?1 and cmn.id = (select max(cmn2.id) from CaseManagementNote cmn2 where cmn2.uuid = cmn.uuid) order by cmn.update_date DESC";
		return (List<Map<String, Object>>) getHibernateTemplate().find(hql, new Object[]{demographic_no,false});
	}

	@SuppressWarnings("unchecked")
    public List<CaseManagementNote> getNotesByDemographic(String demographic_no, Integer maxNotes) {
		if (OscarProperties.getInstance().getDbType().equals("oracle")) {
			return (List<CaseManagementNote>) getHibernateTemplate().findByNamedQuery("mostRecentOra", new Object[] { demographic_no });
		}
		else {
			String hql = "select cmn from CaseManagementNote cmn where cmn.demographic_no = ?0 and cmn.id = (select max(cmn2.id) from CaseManagementNote cmn2 where cmn2.uuid = cmn.uuid) order by cmn.observation_date desc";

			HibernateTemplate Hibernatetemplate = getHibernateTemplate();
			if( maxNotes != -1 ) {
				Hibernatetemplate.setMaxResults(maxNotes);
			}

			List<CaseManagementNote> list = (List<CaseManagementNote>) Hibernatetemplate.find(hql, demographic_no);
			Hibernatetemplate.setMaxResults(0);
			return list;
			//return getHibernateTemplate().findByNamedQuery("mostRecent", new Object[] { demographic_no });

		}
	}

	public List<CaseManagementNote> getNotesByDemographicIssues(
			String demographic_no, String[] issueIds, Boolean active, Integer maxNotes)
	{
		return getNotesByDemographicIssues(
				demographic_no, issueIds, active, maxNotes, null, null);
	}

	@SuppressWarnings("unchecked")
	public List<CaseManagementNote> getNotesByDemographicIssues(
			String demographic_no, String[] issueIds, Boolean active, Integer maxNotes,
			Date observationDate, Boolean distinct )
	{
		List<CaseManagementNote> issueListReturn = new ArrayList<CaseManagementNote>();
		if (issueIds != null && issueIds.length > 0)
		{
			HibernateTemplate hibernateTemplate = getHibernateTemplate();
			int originalMaxResults = hibernateTemplate.getMaxResults();
			if( maxNotes != null)
			{
				hibernateTemplate.setMaxResults(maxNotes);
			}

			String[] placeHolderArray = new String[issueIds.length];

			for(int i = 0; i < issueIds.length; i++)
			{
				placeHolderArray[i] = "?" + i;
			}

			String issues_placeholder = StringUtils.join(placeHolderArray, ",");

			int paramsLength = issueIds.length + 2;

			String hql = "select ";
			if(distinct != null && distinct)
			{
				hql += "distinct ";
			}

			hql += "cmn from CaseManagementNote cmn " +
					"join cmn.issues i " +
					"where i.issue_id in (" + issues_placeholder + ") " +
					"and cmn.demographic_no = ?" + (issueIds.length) + " " +
					"and i.demographic_no = ?" + (issueIds.length + 1) + " ";

			if(active != null && active)
			{
				hql += "and cmn.archived = 0 ";
			}

			if(observationDate != null)
			{
				hql += "and cmn.observation_date >= ?" + (issueIds.length + 2) + " ";
				paramsLength++;
			}

			hql += "and cmn.id = (select max(cmn2.id) from CaseManagementNote cmn2 where cmn.uuid = cmn2.uuid GROUP BY uuid) " +
					"ORDER BY ";

			// The old active query was the only one that also ordered by position.
			if(active != null && active)
			{
				hql += "cmn.position, ";
			}
			hql += "cmn.observation_date desc";

			Object[] params = new Object[paramsLength];
			int paramIndex = 0;
			for(String issue: issueIds)
			{
				params[paramIndex] = Long.parseLong(issue);
				paramIndex++;
			}

			params[paramIndex] = demographic_no;
			paramIndex++;
			params[paramIndex] = demographic_no;

			if(observationDate != null)
			{
				paramIndex++;
				params[paramIndex] = observationDate;
			}

			issueListReturn = (List<CaseManagementNote>) this.getHibernateTemplate().find(hql, params);
			hibernateTemplate.setMaxResults(originalMaxResults);
		}
		return issueListReturn;
	}

	@SuppressWarnings("unchecked")
    public List<CaseManagementNote> getActiveNotesByDemographic(String demographic_no, String[] issues)
	{
		return getNotesByDemographicIssues(demographic_no, issues, true, null);
	}

	@SuppressWarnings("unchecked")
    public List<CaseManagementNote> getNotesByDemographic(String demographic_no, String[] issueIds, Integer maxNotes)
	{
		if(maxNotes == -1)
		{
			maxNotes = null;
		}
		return getNotesByDemographicIssues(demographic_no, issueIds, false, maxNotes);
	}

	@SuppressWarnings("unchecked")
    public List<CaseManagementNote> getNotesByDemographic(String demographic_no, String[] issueIds)
	{
		return getNotesByDemographicIssues(demographic_no, issueIds, false, null);
	}

    public Collection<CaseManagementNote> findNotesByDemographicAndIssueCode(Integer demographic_no, String[] issueCodes) {
		String issueCodeList=null;
		if (issueCodes!=null && issueCodes.length>0) issueCodeList=SqlUtils.constructInClauseForStatements(issueCodes, true);

		String sqlCommand="select distinct casemgmt_note.note_id from issue,casemgmt_issue,casemgmt_issue_notes,casemgmt_note where casemgmt_issue.issue_id=issue.issue_id and casemgmt_issue.demographic_no='"+demographic_no+"' "+(issueCodeList!=null?"and issue.code in "+issueCodeList:"")+" and casemgmt_issue_notes.id=casemgmt_issue.id and casemgmt_issue_notes.note_id=casemgmt_note.note_id";
		Session session=getSession();
		List<CaseManagementNote> notes=new ArrayList<CaseManagementNote>();

		SQLQuery query=session.createSQLQuery(sqlCommand);
		@SuppressWarnings("unchecked")
		List<Integer> ids=query.list();
		for (Integer id : ids)
		{
			notes.add(getNote(id.longValue()));
		}

		// make unique for uuid
		HashMap<String,CaseManagementNote> uniqueForUuid=new HashMap<String,CaseManagementNote>();
		for (CaseManagementNote note : notes)
		{
			CaseManagementNote existingNote=uniqueForUuid.get(note.getUuid());
			if (existingNote==null || note.getUpdate_date().after(existingNote.getUpdate_date())) uniqueForUuid.put(note.getUuid(), note);
		}

		// sort by observationdate
		TreeMap<Date,CaseManagementNote> sortedResults=new TreeMap<Date,CaseManagementNote>();
		for (CaseManagementNote note : uniqueForUuid.values())
		{
			sortedResults.put(note.getObservation_date(), note);
		}

		return(sortedResults.values());
	}

	public Collection<CaseManagementNote> findNotesByDemographicAndIssueCodeInEyeform(Integer demographic_no, String[] issueCodes) {
                String issueCodeList=null;
                if (issueCodes!=null && issueCodes.length>0) issueCodeList=SqlUtils.constructInClauseForStatements(issueCodes, true);

                String sqlCommand="select distinct casemgmt_note.note_id from issue,casemgmt_issue,casemgmt_issue_notes,casemgmt_note where casemgmt_issue.issue_id=issue.issue_id and casemgmt_issue.demographic_no='"+demographic_no+"' "+(issueCodeList!=null?"and issue.code in "+issueCodeList:"")+" and casemgmt_issue_notes.id=casemgmt_issue.id and casemgmt_issue_notes.note_id=casemgmt_note.note_id order by casemgmt_note.note_id DESC";
                Session session=getSession();
                List<CaseManagementNote> notes=new ArrayList<CaseManagementNote>();
                try
                {
                        SQLQuery query=session.createSQLQuery(sqlCommand);
                        @SuppressWarnings("unchecked")
                        List<Integer> ids=query.list();
                        for (Integer id : ids) notes.add(getNote(id.longValue()));
                }
                finally
                {
                        session.close();
                }

                // make unique for appointmentNo
                HashMap<Integer,CaseManagementNote> uniqueForApptId=new HashMap<Integer,CaseManagementNote>();
                for (CaseManagementNote note : notes)
                {
                        CaseManagementNote existingNote=uniqueForApptId.get(note.getAppointmentNo());
                        if (existingNote==null || note.getUpdate_date().after(existingNote.getUpdate_date())) uniqueForApptId.put(note.getAppointmentNo(), note);
                }

                // sort by observationdate
                //observation date is same for cpp in eyeform
                //sort by update update
                TreeMap<Date,CaseManagementNote> sortedResults=new TreeMap<Date,CaseManagementNote>();
                for (CaseManagementNote note : uniqueForApptId.values())
                {
                        sortedResults.put(note.getUpdate_date(), note);
                }

                return(sortedResults.values());
        }


    @SuppressWarnings("unchecked")
	public List<CaseManagementNote> getNotesByDemographicDateRange(String demographic_no, Date startDate, Date endDate) {
		return (List<CaseManagementNote>) getHibernateTemplate().findByNamedQuery("mostRecentDateRange", new Object[] { demographic_no, startDate, endDate });
	}

	@SuppressWarnings("unchecked")
	public List<CaseManagementNote> getNotesByDemographicLimit(String demographic_no, Integer offset, Integer numToReturn) {
		return (List<CaseManagementNote>) getHibernateTemplate().findByNamedQuery("mostRecentLimit", new Object[] { demographic_no, offset, numToReturn });
	}
	
	public void updateNote(CaseManagementNote note) {
		note.setUpdate_date(new Date());
		this.getHibernateTemplate().update(note);
	}

	public void saveNote(CaseManagementNote note) {
		if (note.getUuid() == null) {
			UUID uuid = UUID.randomUUID();
			note.setUuid(uuid.toString());
		}
		note.setUpdate_date(new Date());

		// actually an insert. done this way to prevent Hibernate session error
		note.setId(null);
		CaseManagementNote mergedNote = this.getHibernateTemplate().merge(note);
		note.setId(mergedNote.getId());
	}

	public Object saveAndReturn(CaseManagementNote note) {
		if (note.getUuid() == null) {
			UUID uuid = UUID.randomUUID();
			note.setUuid(uuid.toString());
		}
		note.setUpdate_date(new Date());
		return this.getHibernateTemplate().save(note);
	}

	public List<CaseManagementNote> search(CaseManagementSearchBean searchBean) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Session session = getSession();
		
		List<CaseManagementNote> results = null;
		
		try {
			Criteria criteria = session.createCriteria(CaseManagementNote.class);
	
			criteria.add(Expression.eq("demographic_no", searchBean.getDemographicNo()));
	
			if (searchBean.getSearchRoleId() > 0) {
				criteria.add(Expression.eq("reporter_caisi_role", String.valueOf(searchBean.getSearchRoleId())));
			}
	
			if (searchBean.getSearchProgramId() > 0) {
				criteria.add(Expression.eq("program_no", String.valueOf(searchBean.getSearchProgramId())));
			}
	
		
			Date startDate;
			Date endDate;
			if (searchBean.getSearchStartDate().length() > 0) {
				startDate = formatter.parse(searchBean.getSearchStartDate());
			}
			else {
				startDate = formatter.parse("1970-01-01");
			}
			if (searchBean.getSearchEndDate().length() > 0) {
				endDate = formatter.parse(searchBean.getSearchEndDate());
			}
			else {
				endDate = new Date();
			}
			criteria.add(Restrictions.between("update_date", startDate, endDate));
		
			criteria.addOrder(Order.desc("update_date"));
			results = criteria.list();
			
		} catch (ParseException e) {
			log.warn("Warning", e);
		} finally {
			this.releaseSession(session);
		}

		
		
		return results;

	}

	public boolean haveIssue(Long issid, String demoNo) {
		Session session = getSession();
		try {
			SQLQuery query = session.createSQLQuery("select * from casemgmt_issue_notes where id=" + issid.longValue());
			List results = query.list();
			// log.info("haveIssue - DAO - # of results = " + results.size());
			if (results.size() > 0) return true;
			return false;
		} finally {
			this.releaseSession(session);
		}
	}

	public boolean haveIssue(String issueCode, Integer demographicId) {
		Session session=getSession();

		SQLQuery query = session.createSQLQuery("select casemgmt_issue.id from casemgmt_issue_notes,casemgmt_issue,issue   where issue.issue_id=casemgmt_issue.issue_id and casemgmt_issue.id=casemgmt_issue_notes.id and demographic_no="+demographicId+" and issue.code='"+issueCode+"'");
		List results = query.list();
		// log.info("haveIssue - DAO - # of results = " + results.size());
		if (results.size() > 0)
		{
			return true;
		}

		return false;
	}

	public static class EncounterCounts {
		public HashMap<EncounterUtil.EncounterType, Integer> uniqueCounts = new HashMap<EncounterUtil.EncounterType, Integer>();
		public HashMap<EncounterUtil.EncounterType, Integer> nonUniqueCounts = new HashMap<EncounterUtil.EncounterType, Integer>();
		public int totalUniqueCount = 0;

		public EncounterCounts() {
			// initialise with 0 values as 0 values won't show up in a select
			for (EncounterUtil.EncounterType tempType : EncounterUtil.EncounterType.values()) {
				uniqueCounts.put(tempType, 0);
				nonUniqueCounts.put(tempType, 0);
			}
		}
	}

	/**
	 * Get the count of demographic Id's based on the providerId and encounterType, 2 numbers will be provided, the unique count and the non unique count (which just represents the
	 * number of encounters in general) All encounter types are represented in the resulting hashMap, even ones with 0 counts.
	 *
	 * @param programId can be null at which point it's across the entire agency
	 */
	public static EncounterCounts getDemographicEncounterCountsByProgramAndRoleId(Integer programId, int roleId, Date startDate, Date endDate) {
		Connection c = null;
		try {
			EncounterCounts results = new EncounterCounts();
			c = DbConnectionFilter.getThreadLocalDbConnection();

			// get the numbers broken down by encounter types
			{
				String sqlCommand = "select encounter_type,count(demographic_no), count(distinct demographic_no) from casemgmt_note where reporter_caisi_role=? and observation_date>=? and observation_date<?"+(programId==null?"":" and program_no=?")+" group by encounter_type";
				PreparedStatement ps = c.prepareStatement(sqlCommand);
				ps.setInt(1, roleId);
				ps.setTimestamp(2, new Timestamp(startDate.getTime()));
				ps.setTimestamp(3, new Timestamp(endDate.getTime()));
				if (programId!=null) ps.setInt(4, programId);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					EncounterUtil.EncounterType encounterType = EncounterUtil.getEncounterTypeFromOldDbValue(rs.getString(1));
					results.nonUniqueCounts.put(encounterType, rs.getInt(2));
					results.uniqueCounts.put(encounterType, rs.getInt(3));
				}
			}

			// get the numbers in total, not broken down.
			{
				String sqlCommand = "select count(distinct demographic_no) from casemgmt_note where reporter_caisi_role=? and observation_date>=? and observation_date<?"+(programId==null?"":" and program_no=?");
				PreparedStatement ps = c.prepareStatement(sqlCommand);
				ps.setInt(1, roleId);
				ps.setTimestamp(2, new Timestamp(startDate.getTime()));
				ps.setTimestamp(3, new Timestamp(endDate.getTime()));
				if (programId!=null) ps.setInt(4, programId);

				ResultSet rs = ps.executeQuery();
				rs.next();

				results.totalUniqueCount=rs.getInt(1);
			}

			return (results);
		}
		catch (SQLException e) {
			throw (new PersistenceException(e));
		}
		finally {
			SqlUtils.closeResources(c, null, null);
		}
	}


	public int getNoteCountForProviderForDateRange(String providerNo,Date startDate,Date endDate){
		int ret = 0;

		Connection c = null;
		try {
			c = DbConnectionFilter.getThreadLocalDbConnection();
		String sqlCommand = "select count(distinct uuid) from casemgmt_note where provider_no = ? and observation_date >= ? and observation_date <= ?";
		PreparedStatement ps = c.prepareStatement(sqlCommand);
		ps.setString(1, providerNo);
		ps.setTimestamp(2, new Timestamp(startDate.getTime()));
		ps.setTimestamp(3, new Timestamp(endDate.getTime()));


		ResultSet rs = ps.executeQuery();
		rs.next();

		ret=rs.getInt(1);
		}catch(Exception e){
			MiscUtils.getLogger().error("Error", e);
		}
		return ret;
	}

	public int getNoteCountForProviderForDateRangeWithIssueId(String providerNo,Date startDate,Date endDate,String issueCode){
		int ret = 0;

		Connection c = null;
		try {
			c = DbConnectionFilter.getThreadLocalDbConnection();
			String sql = "select issue_id from issue where code = '"+issueCode+"' ";
			log.debug(sql);
			PreparedStatement ps = c.prepareStatement(sql);
			//ps.setString(1, issueCode);
			ResultSet rs= ps.executeQuery(sql);
			String id = null;
			if (rs.next()){
				id = rs.getString("issue_id");
			}else{
				log.debug("Could not find issueCode "+issueCode);
				return 0;
			}

			log.debug("issue Code "+issueCode+" id :"+id);

			String sqlCommand = "select count(distinct uuid) from casemgmt_issue c, casemgmt_issue_notes cin, casemgmt_note cn where c.issue_id = ? and c.id = cin.id and cin.note_id = cn.note_id and cn.provider_no = ?  and observation_date >= ? and observation_date <= ?";
			log.debug(sqlCommand);
			ps = c.prepareStatement(sqlCommand);
			ps.setString(1, id);
			ps.setString(2, providerNo);
			ps.setTimestamp(3, new Timestamp(startDate.getTime()));
			ps.setTimestamp(4, new Timestamp(endDate.getTime()));


			rs = ps.executeQuery();
			rs.next();

			ret=rs.getInt(1);
		}catch(Exception e){
			log.error("Error counting notes for issue :"+issueCode,e);
		}
		return ret;
	}
    //used by decision support to search through the notes for a string
	public List<CaseManagementNote> searchDemographicNotes(String demographic_no, String searchString) {
		String hql = "select distinct cmn from CaseManagementNote cmn where cmn.id in (select max(cmn.id) from cmn where cmn.demographic_no = ?0 GROUP BY uuid) and cmn.demographic_no = ?1 and cmn.note like ?2 and cmn.archived = 0";

		@SuppressWarnings("unchecked")
		List<CaseManagementNote> result = (List<CaseManagementNote>) getHibernateTemplate().find(hql, new Object[] {demographic_no, demographic_no, searchString });
		return result;
	}

    public List<CaseManagementNote> getCaseManagementNoteByProgramIdAndObservationDate(Integer programId, Date minObservationDate, Date maxObservationDate) {
        String queryStr = "FROM CaseManagementNote x WHERE x.program_no=?0 and x.observation_date>=?1 and x.observation_date<=?2";

        @SuppressWarnings("unchecked")
        List<CaseManagementNote> rs = (List<CaseManagementNote>) getHibernateTemplate().find(queryStr, new Object[] {programId.toString(), minObservationDate, maxObservationDate});

        return rs;
    }

	public List<CaseManagementNote> getMostRecentNotesByAppointmentNo(int appointmentNo) {
		String hql = "select distinct cmn.uuid from CaseManagementNote cmn where cmn.appointmentNo = ?0";
		@SuppressWarnings("unchecked")
		List<String> tmp = (List<String>) this.getHibernateTemplate().find(hql, appointmentNo);
		List<CaseManagementNote> mostRecents = new ArrayList<CaseManagementNote>();
		for(String uuid:tmp) {
			mostRecents.add(this.getMostRecentNote(uuid));
		}
		return mostRecents;
	}

	public List<CaseManagementNote> getMostRecentNotes(Integer demographicNo) {
		String hql = "select distinct cmn.uuid from CaseManagementNote cmn where cmn.demographic_no = ?0";
		@SuppressWarnings("unchecked")
		List<String> tmp = (List<String>) this.getHibernateTemplate().find(hql, new Object[]{String.valueOf(demographicNo)});
		List<CaseManagementNote> mostRecents = new ArrayList<CaseManagementNote>();
		for(String uuid:tmp) {
			mostRecents.add(this.getMostRecentNote(uuid));
		}
		return mostRecents;
	}

	public Long findMaxNoteId() {
		String sql = "select max(c.id) from CaseManagementNote c";
		@SuppressWarnings("unchecked")
		List<Object> r = (List<Object>) getHibernateTemplate().find(sql);
		if (r.isEmpty()) {
			return 0L;
		}
		return (Long) r.get(0);
		
    }
	
	public List<Integer> getNotesByFacilitySince(Date date, List<Program> programs) {		
		StringBuilder sb = new StringBuilder();
    	int i=0;
    	for(Program p:programs) {
    		if(i++ > 0)
    			sb.append(",");
    		sb.append(p.getId());
    	}
		String hql = "select cmn.demographic_no from CaseManagementNote cmn where cmn.program_no in ("+sb.toString()+") and cmn.update_date > ?0 and cmn.locked != '1' and cmn.id = (select max(cmn2.id) from CaseManagementNote cmn2 where cmn2.uuid = cmn.uuid) order by cmn.observation_date";
		return (List<Integer>) getHibernateTemplate().find(hql,date);
	}
}
