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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.util.MiscUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.quatro.model.security.Secrole;

/**
 * @deprecated use the jpa version instead
 */
@Deprecated
public class IssueDAO extends HibernateDaoSupport {
	private static Logger logger = MiscUtils.getLogger();

    public Issue getIssue(Long id) {
		return getHibernateTemplate().get(Issue.class, id);
    }

    public List<Issue> getIssues() {
        return (List<Issue>) this.getHibernateTemplate().find("from Issue");
    }

    public List<Issue> findIssueByCode(String[] codes) {
        String code = "'" + StringUtils.join(codes,"','") + "'";
        return (List<Issue>) this.getHibernateTemplate().find("from Issue i where i.code in (" + code + ")");
    }

    public Issue findIssueByCode(String code) {
        List<Issue>list = (List<Issue>) this.getHibernateTemplate().find("from Issue i where i.code = ?", new Object[] {code});
        if( list.size() > 0 )
            return list.get(0);

        return null;
    }

    public Issue findIssueByTypeAndCode(String type, String code) {
        List<Issue>list = (List<Issue>) this.getHibernateTemplate().find("from Issue i where i.type=? and i.code = ?", new Object[] {type,code});
        if( list.size() > 0 )
            return list.get(0);

        return null;
    }

    public void saveIssue(Issue issue) {
        this.getHibernateTemplate().saveOrUpdate(issue);
    }
    
    @Deprecated
    public void delete(Long issueId) {
    	this.getHibernateTemplate().delete(getIssue(issueId));
    }

    @SuppressWarnings("unchecked")
    public List<Issue> findIssueBySearch(String search) {
        search = "%" + search + "%";
        search = search.toLowerCase();
        String sql = "from Issue i where lower(i.code) like ? or lower(i.description) like ?";
        return (List<Issue>) this.getHibernateTemplate().find(sql, new Object[] {search, search});
    }
    
    public List<Long> getIssueCodeListByRoles(List<Secrole> roles) {
    	if (roles.size() == 0) {
            return new ArrayList<Long>();
        }

        StringBuilder buf = new StringBuilder();
        for (int x = 0; x < roles.size(); x++) {
            if (x != 0) {
                buf.append(",");
            }
            buf.append("\'" + StringEscapeUtils.escapeSql((roles.get(x)).getName()) + "\'");
        }
        String roleList = buf.toString();

        String sql = "select i.id from Issue i where i.role in (" + roleList + ") order by sortOrderId";
        logger.debug(sql);
        return (List<Long>) this.getHibernateTemplate().find(sql);
    }

    @SuppressWarnings("unchecked")
    public List<Issue> search(String search, List<Secrole> roles, final int startIndex, final int numToReturn) {
        if (roles.size() == 0) {
            return new ArrayList<Issue>();
        }

        StringBuilder buf = new StringBuilder();
        for (int x = 0; x < roles.size(); x++) {
            if (x != 0) {
                buf.append(",");
            }
            buf.append("\'" + StringEscapeUtils.escapeSql((roles.get(x)).getName()) + "\'");
        }
        final String roleList = buf.toString();

        search = "%" + search + "%";
        search = search.toLowerCase();
        final String sql = "from Issue i where (lower(i.code) like :term or lower(i.description) like :term  or lower(i.role) like :roles) and i.role in (" + roleList + ") order by sortOrderId";
        logger.debug(sql);
        final String s = search;
        //return this.getHibernateTemplate().find(sql, new Object[] {search, search,roleList});

		Session session = getSession();

        Query q = session.createQuery(sql);
        q.setMaxResults(Math.min(numToReturn,AbstractDao.MAX_LIST_RETURN_SIZE));
        q.setFirstResult(startIndex);
        q.setParameter("term", s);
        q.setParameter("roles", roleList);
        return q.list();


/*
        return getHibernateTemplate().executeFind(new HibernateCallback<List<Issue>>() {
            public List<Issue> doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery(sql);
                q.setMaxResults(Math.min(numToReturn,AbstractDao.MAX_LIST_RETURN_SIZE));
                q.setFirstResult(startIndex);
                q.setParameter("term", s);
                q.setParameter("roles", roleList);
                return q.list();
            }
        });
*/

    }
    
    @SuppressWarnings("unchecked")
    public Integer searchCount(String search, List<Secrole> roles) {
        if (roles.size() == 0) {
            return 0;
        }

        StringBuilder buf = new StringBuilder();
        for (int x = 0; x < roles.size(); x++) {
            if (x != 0) {
                buf.append(",");
            }
            buf.append("\'" + StringEscapeUtils.escapeSql((roles.get(x)).getName()) + "\'");
        }
        final String roleList = buf.toString();

        search = "%" + search + "%";
        search = search.toLowerCase();
        final String sql = "select count(i) from Issue i where (lower(i.code) like ? or lower(i.description) like ?  or lower(i.role) like ?) and i.role in (" + roleList + ") order by sortOrderId";
        logger.debug(sql);
        List<Long> result = (List<Long>) this.getHibernateTemplate().find(sql, new Object[] {search, search,roleList});

        if(result.size()>0) {
        	return result.get(0).intValue();
        }

        return 0;
    }
    

    public List searchNoRolesConcerned(String search) {
        search = "%" + search + "%";
        search = search.toLowerCase();
        String sql = "from Issue i where (lower(i.code) like ? or lower(i.description) like ?)";
        logger.debug(sql);
        return this.getHibernateTemplate().find(sql, new Object[] {search, search});
    }

    /**
     * Retrieves a list of Issue codes that have a type matching what is configured in oscar_mcmaster.properties as COMMUNITY_ISSUE_CODETYPE,
     * or an empty list if this property is not found.
     * @param type
     */
    @SuppressWarnings("unchecked")
    public List<String> getLocalCodesByCommunityType(String type)
    {
    	List<String> codes;
    	if(type == null || type.equals(""))
    	{
    		codes = new ArrayList<String>();
    	}
    	else
    	{
    		codes = (List<String>) this.getHibernateTemplate().find("FROM Issue i WHERE i.type = ?", new Object[] {type.toLowerCase()});
    	}
    	return codes;
    }
}
