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

import org.oscarehr.common.model.FlowSheetCustomization;
import org.springframework.stereotype.Repository;

@Repository
public class FlowSheetCustomizationDao extends AbstractDao<FlowSheetCustomization>{

	public FlowSheetCustomizationDao() {
		super(FlowSheetCustomization.class);
	}

    public FlowSheetCustomization getFlowSheetCustomization(String id){
    	return this.find(Integer.valueOf(id));
    }

    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet,String provider,String demographic){
    	if(demographic == null || demographic.isEmpty())                        
            demographic = "0";                                                  
        Query query = entityManager.createQuery("SELECT fc FROM FlowSheetCustomization fc WHERE fc.flowsheet=? and fc.archived=0 and ( ( fc.providerNo = ?  and fc.demographicNo = 0) or (fc.providerNo =? and fc.demographicNo = ?  ) )");
        
    	query.setParameter(1, flowsheet);
    	query.setParameter(2, provider);
    	query.setParameter(3, provider);
    	query.setParameter(4, demographic);

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }
}
