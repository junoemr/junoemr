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

    public FlowSheetCustomization getFlowSheetCustomization(Integer id){
    	return this.find(id);
    }

    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet,String provider,Integer demographic){
    	Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=?1 and fd.archived=0 and ( ( fd.providerNo = ?2  and fd.demographicNo = 0) or (fd.providerNo =?3 and fd.demographicNo = ?4  ) )");
    	query.setParameter(1, flowsheet);
    	query.setParameter(2, provider);
    	query.setParameter(3, provider);
    	query.setParameter(4, String.valueOf(demographic));

        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }
    
    public List<FlowSheetCustomization> getFlowSheetCustomizations(String flowsheet,String provider){
    	Query query = entityManager.createQuery("SELECT fd FROM FlowSheetCustomization fd WHERE fd.flowsheet=?1 and fd.archived=0 and fd.providerNo = ?2  and fd.demographicNo = 0");
    	query.setParameter(1, flowsheet);
    	query.setParameter(2, provider);
    	
        @SuppressWarnings("unchecked")
        List<FlowSheetCustomization> list = query.getResultList();
        return list;
    }
}
