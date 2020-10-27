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


package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.CtlDocType;
import org.springframework.stereotype.Repository;

@Repository
public class CtlDocTypeDao extends AbstractDao<CtlDocType>{

	public CtlDocTypeDao() {
		super(CtlDocType.class);
	}

	public Integer updateDocTypeStatus(String docType, String module, String status){
		String sql = "UPDATE ctl_doctype SET status = :status WHERE module = :module AND BINARY doctype = :docType";
		Query query = entityManager.createNativeQuery(sql, CtlDocType.class);
		query.setParameter("status", status);
		query.setParameter("module", module);
		query.setParameter("docType", docType);

		return query.executeUpdate();
	}

	public List<CtlDocType> findByStatusAndModule(String[] statuses, String module){
		List<String> search = new ArrayList<>();
		Collections.addAll(search, statuses);

		return this.findByStatusAndModule(search, module);
	}
	
	public List<CtlDocType> findByStatusAndModule(List<String> statuses, String module){

		String sql = "SELECT c FROM CtlDocType c WHERE c.status IN (:status) AND c.module = :module ORDER BY doctype ASC";
		Query query = entityManager.createQuery(sql);
		query.setParameter("status", statuses);
		query.setParameter("module", module);

		@SuppressWarnings("unchecked")
		List<CtlDocType> results = query.getResultList();

		return results;
	}

	public List<CtlDocType> findByDocTypeAndModule(String docType, String module){

		String sql = "SELECT * FROM ctl_doctype c WHERE BINARY c.doctype = :docType AND c.module = :module";
		Query query = entityManager.createNativeQuery(sql, CtlDocType.class);
		query.setParameter("docType", docType);
		query.setParameter("module", module);
		@SuppressWarnings("unchecked")
		List<CtlDocType> results = query.getResultList();

		return results;

	}

	public Integer addDocType(String name, String module) {

		CtlDocType docType = new CtlDocType();
        docType.setDocType(name);
        docType.setModule(module.toLowerCase());
        docType.setStatus(CtlDocType.Status.Active.toString());
        entityManager.persist(docType);

        return docType.getId();
    }

    public List<CtlDocType> findByModule(String module)
    {
    	String sql = "SELECT c FROM CtlDocType c WHERE c.module = :module ORDER BY c.docType ASC";
	    Query query = entityManager.createQuery(sql);
	    query.setParameter("module", module.toLowerCase());
	    @SuppressWarnings("unchecked")
	    List<CtlDocType> results = query.getResultList();

	    return results;
    }
}
