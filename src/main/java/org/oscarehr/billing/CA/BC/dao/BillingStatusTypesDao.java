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
package org.oscarehr.billing.CA.BC.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.billing.CA.BC.model.BillingStatusTypes;
import org.oscarehr.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

@Repository
public class BillingStatusTypesDao extends AbstractDao<BillingStatusTypes> {

	protected BillingStatusTypesDao() {
	    super(BillingStatusTypes.class);
    }

    @SuppressWarnings("unchecked")
	public List<BillingStatusTypes> findAll() {
		Query query = entityManager.createQuery("SELECT x FROM " + modelClass.getSimpleName() + " x");
		List<BillingStatusTypes> results = query.getResultList();
		return results;
	}
    
	@SuppressWarnings("unchecked")
    public List<BillingStatusTypes> findByCodes(List<String> codes)
	{
		// Convert codes to chars.  Toss any longer than one character because it can't match
		List<Character> cleanedCodes = new ArrayList<>();
		for(String code: codes) {
			if (code.length() == 1)
			{
				cleanedCodes.add(code.toCharArray()[0]);
			}
		}
	    Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " bst WHERE bst.id IN (:typeCodes)");
	    query.setParameter("typeCodes", cleanedCodes);
	    return query.getResultList(); 
    }

}
