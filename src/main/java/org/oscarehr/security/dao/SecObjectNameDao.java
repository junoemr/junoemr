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


package org.oscarehr.security.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.security.model.SecObjectName;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository(value="secObjectNameDaoJpa")
public class SecObjectNameDao extends AbstractDao<SecObjectName>
{

	public SecObjectNameDao() {
		super(SecObjectName.class);
	}

	public List<SecObjectName> findAll() {
		String sql = "SELECT s FROM SecObjectName s order by s.id";

		Query query = entityManager.createQuery(sql);

		@SuppressWarnings("unchecked")
		List<SecObjectName> result =  query.getResultList();

		return result;
	}

	public List<String> findDistinctObjectNames() {
		String sql = "SELECT distinct(s.id) FROM SecObjectName s order by s.id";

		Query query = entityManager.createQuery(sql);

		@SuppressWarnings("unchecked")
		List<String> result =  query.getResultList();

		return result;
	}

	/**
	 * fetch all the SecObjectName entries as a map keyed on the id.
	 * @return -  a map containing all entries for the entity, keyed on the id
	 */
	public Map<String, SecObjectName> findAllMappedById()
	{
		String jpql = "SELECT x \n" +
				"FROM SecObjectName x \n" +
				"ORDER BY x.id asc";
		return entityManager.createQuery(jpql, SecObjectName.class)
				.getResultStream()
				.collect(
						Collectors.toMap(
								SecObjectName::getId, // map key is the entity Id
								entity -> (entity)    // value is the entity
						)
				);
	}
}
