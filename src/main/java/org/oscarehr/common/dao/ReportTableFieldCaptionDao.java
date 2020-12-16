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

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.ReportTableFieldCaption;
import org.springframework.stereotype.Repository;

@Repository
public class ReportTableFieldCaptionDao extends AbstractDao<ReportTableFieldCaption>{

	public ReportTableFieldCaptionDao() {
		super(ReportTableFieldCaption.class);
	}
	
	public List<ReportTableFieldCaption> findByTableNameAndName(String tableName, String name) {
		Query q = entityManager.createQuery("SELECT x FROM ReportTableFieldCaption x WHERE x.tableName=?1 AND x.name=?2");
		q.setParameter(1, tableName);
		q.setParameter(2, name);
		
		@SuppressWarnings("unchecked")
		List<ReportTableFieldCaption> results = q.getResultList();
		
		return results;
		
	}
}
