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

package org.oscarehr.common.dao;

import javax.persistence.Query;

import org.oscarehr.common.model.ProviderExt;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderMediSproutDao extends AbstractDao<ProviderExt> {

	public ProviderMediSproutDao() {
		super(ProviderExt.class);
	}
	
	public ProviderExt getProviderExt(String providerNo) {
		
		// return the most recent data for this demographic 
		String sqlCommand = "select x from ProviderExt x where x.id=?1";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, providerNo);
		
		return getSingleResultOrNull(query);
	}
	
	public String getProviderMediSproutApiKey(String providerNo) {
		ProviderExt pe = getProviderExt(providerNo);

		if (pe == null) {
			return "";
		}
		
		return pe.getMediasproutapikey();		
	}
	
	public ProviderExt saveApiKey(String providerNo, String apiKey) {
		ProviderExt providerExt = this.getProviderExt(providerNo);
    	if (providerExt == null) {
    		providerExt = new ProviderExt();
    		providerExt.set(providerNo);
    	}
    	providerExt.setMediasproutapikey(apiKey);
    	this.persist(providerExt);
    	
    	return providerExt;
	}

}