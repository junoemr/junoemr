/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.labs.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.labs.model.Hl7DocumentLink;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class Hl7DocumentLinkDao extends AbstractDao<Hl7DocumentLink>
{
	public Hl7DocumentLinkDao()
	{
		super(Hl7DocumentLink.class);
	}

	/**
	 * One-to-many relationship between labs and documents.
	 * Given a lab number we can get all the documents associated
	 * @param labNo lab's primary key for which we want to grab documents
	 * @return a list of all documents associated for a lab, or null if no documents found
	 */
	public List<Hl7DocumentLink> getDocumentsForLab(Integer labNo)
	{
		String sql = "Select h FROM hl7DocumentLink h WHERE labNo=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, labNo);

		@SuppressWarnings("unchecked")
		List<Hl7DocumentLink> documentLinks = query.getResultList();

		return documentLinks;
	}
}
