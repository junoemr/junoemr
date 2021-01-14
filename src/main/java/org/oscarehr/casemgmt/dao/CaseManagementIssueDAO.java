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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.caisi_integrator.ws.CodeType;
import org.oscarehr.caisi_integrator.ws.FacilityIdDemographicIssueCompositePk;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.util.MiscUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @deprecated use the jpa version instead
 */
@Deprecated
public class CaseManagementIssueDAO extends HibernateDaoSupport {
	
	private static final Logger logger = MiscUtils.getLogger();

    @SuppressWarnings("unchecked")
    public List<CaseManagementIssue> getIssuesByDemographic(String demographic_no) {
        return (List<CaseManagementIssue>) this.getHibernateTemplate().find("from CaseManagementIssue cmi where cmi.demographic_no = ?0", new Object[] {demographic_no});
    }

    @SuppressWarnings("unchecked")
    public List<CaseManagementIssue> getIssuesByDemographicOrderActive(Integer demographic_no, Boolean resolved) {
        return (List<CaseManagementIssue>) getHibernateTemplate().find("from CaseManagementIssue cmi where cmi.demographic_no = ?0 "+(resolved!=null?" and cmi.resolved="+resolved:"")+" order by cmi.resolved", new Object[] {demographic_no.toString()});
    }
    
    @SuppressWarnings("unchecked")
    public List<CaseManagementIssue> getIssuesByNote(Integer noteId, Boolean resolved) {
        return (List<CaseManagementIssue>) getHibernateTemplate().find("from CaseManagementIssue cmi where cmi.notes.id = ?0 "+(resolved!=null?" and cmi.resolved="+resolved:"")+" order by cmi.resolved", new Object[] {noteId});
    }
    
    @SuppressWarnings("unchecked")
    public Issue getIssueByCmnId(Integer cmnIssueId) {
        List<Issue> result = (List<Issue>) getHibernateTemplate().find("select issue from CaseManagementIssue cmi where cmi.id = ?0", new Object[] {Long.valueOf(cmnIssueId)});
        if(result.size()>0)
        	return result.get(0);
        return null;
    }

    public CaseManagementIssue getIssuebyId(String demo, String issueId) {
        @SuppressWarnings("unchecked")
        List<CaseManagementIssue> list = (List<CaseManagementIssue>) this.getHibernateTemplate().find("from CaseManagementIssue cmi where cmi.issue_id = ?0 and demographic_no = ?1 order by cmi.id desc",new Object[]{Long.parseLong(issueId),demo});
        
        if(list == null || list.isEmpty()) {
        	return null;
        }
        if(list.size() > 1) {
        	// the database should not have more than one result here, but return something to prevent further db errors
        	logger.error("Multiple CaseManagementIssue entries with same issue_id found for demographic: "+ demo +" (issue_id: "+issueId+" ). Check database for duplicates.");
        	//(new NonUniqueResultException("Expected 1 result got more : "+list.size() + "(" + demo + "," + issueId + ")"));
        }
        return list.get(0); //always return the result with the highest id
    }

    public CaseManagementIssue getIssuebyIssueCode(String demo, String issueCode) {
        @SuppressWarnings("unchecked")
        List<CaseManagementIssue> list = (List<CaseManagementIssue>) this.getHibernateTemplate().find("select cmi from CaseManagementIssue cmi, Issue issue where cmi.issue_id=issue.id and issue.code = ?0 and cmi.demographic_no = ?1",new Object[]{issueCode,demo});
        
        if(list == null || list.size()<1) return(null);
        	
        if (list.size() == 1 ) return list.get(0);
        
        logger.error("getIssuebyIssueCode returned ("+list.size() + ") results");
        throw(new NonUniqueResultException("Expected 1 result got more : "+list.size() + "(" + demo + "," + issueCode + ")"));          
    }

    public void deleteIssueById(CaseManagementIssue issue) {
    	logger.info("DELETE casemgmt issue: [id:" + issue.getId() + ", demoNo:" + issue.getDemographic_no() + ", issue_id:" + issue.getIssue_id() + "]");
        getHibernateTemplate().delete(issue);
        return;

    }
    public CaseManagementIssue getById(Long id) {
    	@SuppressWarnings("unchecked")
        List<CaseManagementIssue> list = (List<CaseManagementIssue>) getHibernateTemplate().find("from CaseManagementIssue cmi where id = ?0 ",new Object[] {id});
        if(list == null || list.isEmpty()) {
        	return null;
        }
        return list.get(0);
    }

    /** temporary debugging method that will log errors saving these notes. */
    private void checkDemoIssueId(CaseManagementIssue issue) {
    	logger.info("casemgmt_issue MATCH TEST: [id:" + issue.getId() + ", demoNo:" + issue.getDemographic_no() + ", issue_id:" + issue.getIssue_id() + "]");
    	try {
    		CaseManagementIssue cmi = getIssuebyId(issue.getDemographic_no(), Long.toString(issue.getIssue_id()));
    		logger.info("getIssuebyId check: returned id " + ((cmi==null)? "null":cmi.getId()));
    		if(cmi != null) {
    			logger.info("This CaseManagementIssue should be updated (based on get by issue_id)");
    		}
    	} 
    	catch(Exception e) {
    		logger.error("Error retrieving by issue id", e);
    	}
    	try {
    		CaseManagementIssue cmi = getIssuebyIssueCode(issue.getDemographic_no(), issue.getIssue().getCode());
    		logger.info("getIssueByIssueCode check: returned id " + ((cmi==null)? "null":cmi.getId()));
    		if(cmi != null) {
    			logger.info("This CaseManagementIssue should be updated (based on get by issue_code)");
    		}
    	}
    	catch(NonUniqueResultException e) {
    		logger.error("CaseManagementIssue SHOULD NOT BE SAVED AS NEW", e);
    	}
    	catch(Exception e) {
    		logger.error("Error retrieving by issue code", e);
    	}
    }
    public void saveAndUpdateCaseIssues(List<CaseManagementIssue> issuelist) {
    	
    	for(CaseManagementIssue cmi : issuelist) {
        	cmi.setUpdate_date(new Date());
        	checkDemoIssueId(cmi);// TODO-legacy remove this once duplication error resolved
        	if(cmi.getId()!=null && cmi.getId().longValue()>0) {
        		logger.info("MERGE casemgmt issue: [id:" + cmi.getId() + ", demoNo:" + cmi.getDemographic_no() + ", issue_id:" + cmi.getIssue_id() + "]");
        		getHibernateTemplate().merge(cmi);
        	}
        	else {
        		logger.info("SAVE casemgmt issue: [id:" + cmi.getId() + ", demoNo:" + cmi.getDemographic_no() + ", issue_id:" + cmi.getIssue_id() + "]");
        		getHibernateTemplate().save(cmi);
        	}
        }
    }

    public void saveIssue(CaseManagementIssue issue) {
    	issue.setUpdate_date(new Date());
    	checkDemoIssueId(issue);// TODO-legacy remove this once duplication error resolved
        logger.info("SAVE OR UPDATE casemgmt issue: [id:" + issue.getId() + ", demoNo:" + issue.getDemographic_no() + ", issue_id:" + issue.getIssue_id() + "]");
        getHibernateTemplate().saveOrUpdate(issue);
    }
    
    @SuppressWarnings("unchecked")
    public List<CaseManagementIssue> getAllCertainIssues() {
        return (List<CaseManagementIssue>) getHibernateTemplate().find("from CaseManagementIssue cmi where cmi.certain = true");
    }

    //for integrator
    @SuppressWarnings("unchecked")
    public List<Integer> getIssuesByProgramsSince(Date date, List<Program> programs) {
    	StringBuilder sb = new StringBuilder();
    	int i=0;
    	for(Program p:programs) {
    		if(i++ > 0)
    			sb.append(",");
    		sb.append(p.getId());
    	}
        return (List<Integer>) this.getHibernateTemplate().find("select cmi.demographic_no from CaseManagementIssue cmi where cmi.update_date > ?0 and program_id in ("+sb.toString()+")", new Object[] {date});
    }

    @SuppressWarnings("unchecked")
    public List<CaseManagementIssue> getIssuesByDemographicSince(String demographic_no,Date date) {
        return (List<CaseManagementIssue>) this.getHibernateTemplate().find("from CaseManagementIssue cmi where cmi.demographic_no = ?0 and cmi.update_date > ?1", new Object[] {demographic_no,date});
    }
    
    @SuppressWarnings("unchecked")
    public List<FacilityIdDemographicIssueCompositePk> getIssueIdsForIntegrator(Integer facilityId, Integer demographicNo) {
        List<Object[]> rs = (List<Object[]>) this.getHibernateTemplate().find("select i.code,i.type from CaseManagementIssue cmi, Issue i where cmi.issue_id = i.id and cmi.demographic_no = ?0", new Object[] {demographicNo.toString()});
        List<FacilityIdDemographicIssueCompositePk> results = new ArrayList<FacilityIdDemographicIssueCompositePk>();
        for(Object[] item:rs) {
        	FacilityIdDemographicIssueCompositePk key = new FacilityIdDemographicIssueCompositePk();
        	key.setIntegratorFacilityId(facilityId);
        	key.setCaisiDemographicId(demographicNo);
        	key.setIssueCode((String)item[0]);
        	
        	if("icd9".equals(item[1])) {
				key.setCodeType(CodeType.ICD_9);
			}
			else if("icd10".equals(item[1])) {
				key.setCodeType(CodeType.ICD_10);
			} else {
				key.setCodeType(CodeType.CUSTOM_ISSUE);
			}
        	results.add(key);
        }
        return results;
    }

}
