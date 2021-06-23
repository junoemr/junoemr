/**
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.Icd9;
import org.springframework.stereotype.Repository;

/**
 *
 * @author toby
 */
@Repository
public class Icd9Dao extends AbstractCodeSystemDao<Icd9>{

	public Icd9Dao() {
		super(Icd9.class);
	}

	public List<Icd9> getIcd9Code(String icdCode){
		Query query = entityManager.createQuery("select i from Icd9 i where i.icd9=?1");
		query.setParameter(1, icdCode);

		@SuppressWarnings("unchecked")
		List<Icd9> results = query.getResultList();

		return results;
	}


    public List<Icd9> getIcd9(String query) {
		Query q = entityManager.createQuery("select i from Icd9 i where i.icd9 like ?1 or i.description like ?2 order by i.description");
		q.setParameter(1, "%"+query+"%");
		q.setParameter(2, "%"+query+"%");

		@SuppressWarnings("unchecked")
		List<Icd9> results = q.getResultList();

		return results;
    }

	@Override
    public List<Icd9> searchCode(String term) {
	    return getIcd9(term);
    }
	
	@Override
    public Icd9 findByCode(String code) {
	    List<Icd9> results =  getIcd9Code(code);
	    if(results.isEmpty())
	    	return null;
	    return results.get(0);
    }

	@Override
    public AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem) {
		Query query = entityManager.createQuery("FROM Icd9 i WHERE i.icd9 like :cs");
		query.setParameter("cs", codingSystem);
		query.setMaxResults(1);
		
		return getSingleResultOrNull(query);
    }

}
