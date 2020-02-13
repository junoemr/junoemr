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
import java.util.Properties;

import javax.persistence.Query;

import org.oscarehr.common.model.FrmLabReqPreSet;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mweston4
 */
@Repository
public class FrmLabReqPreSetDao extends AbstractDao<FrmLabReqPreSet>{
      
    public FrmLabReqPreSetDao() {
        super(FrmLabReqPreSet.class);	
    }      
    
    public Properties fillPropertiesByLabType(String labType, Properties prop) {
        String sql = "select frmPreset from FrmLabReqPreSet frmPreset where labType=?1 and status=?2";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, labType); 
        query.setParameter(2, 1); 
                      
        @SuppressWarnings("unchecked")
        List<FrmLabReqPreSet> results = query.getResultList();        
        
        if (results != null) { 
            for (FrmLabReqPreSet flrp : results) {
                prop.setProperty(flrp.getPropertyName(), flrp.getPropertyValue());
            }
        }
        
        return prop;
    }
}
