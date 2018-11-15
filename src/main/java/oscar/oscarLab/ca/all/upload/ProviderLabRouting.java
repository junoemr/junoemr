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


/*
 * ProviderLabRouting.java
 *
 * Created on July 17, 2007, 9:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.upload;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.model.ProviderLabRoutingModel;

import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarDB.DBHandler;
import oscar.oscarLab.ForwardingRules;

/**
 *
 * @author wrighd
 */
public class ProviderLabRouting {

    Logger logger = Logger.getLogger(ProviderLabRouting.class);

    /** Creates a new instance of ProviderLabRouting */
    public ProviderLabRouting() {
    }

    public static Hashtable<String,Object> getInfo(String lab_no) throws SQLException {
	Hashtable<String,Object> info = new Hashtable<String,Object>();
	String sql = "SELECT * FROM providerLabRouting WHERE lab_no='"+lab_no+"'";

	ResultSet rs = DBHandler.GetSQL(sql);

	if (rs.next()) {
	    info.put("lab_no", lab_no);
	    info.put("provider_no", rs.getString("provider_no"));
	    info.put("status", rs.getString("status"));
	    info.put("comment", rs.getString("comment"));
	    info.put("timestamp", rs.getString("timestamp"));
	    info.put("lab_type", rs.getString("lab_type"));
	    info.put("id", rs.getInt("id"));
	}
	return info;
    }


    public void addToLabRouting(String labId, String provider_no, String labType) throws SQLException
	{
		ForwardingRules fr = new ForwardingRules();
		ProviderLabRoutingDao providerLabRoutingDao = (ProviderLabRoutingDao) SpringUtils.getBean("providerLabRoutingDao");
		List<ProviderLabRoutingModel> rs = providerLabRoutingDao.getProviderLabRoutingForLabProviderType(labId, provider_no, labType);

		OscarProperties props = OscarProperties.getInstance();
		String autoUnfileLabs = props.getProperty("AUTO_UNFILE_LABS");

		if(rs.isEmpty()) {
			String status = fr.getStatus(provider_no);
			ArrayList<ArrayList<String>> forwardProviders = fr.getProviders(provider_no);

			ProviderLabRoutingModel newRouted = new ProviderLabRoutingModel();
			newRouted.setProviderNo(provider_no);
			newRouted.setLabNo(Integer.parseInt(labId));
			newRouted.setLabType(labType);
			newRouted.setStatus(status);

			providerLabRoutingDao.persist(newRouted);

			//forward lab to specified providers
			for (int j=0; j < forwardProviders.size(); j++){
				logger.info("FORWARDING PROVIDER: "+((forwardProviders.get(j)).get(0)));
				route(labId, ( ( forwardProviders.get(j)).get(0)),labType);
			}

		// if auto-unfile-labs is turned on, when a lab is forwarded and
		// the status is filed for that provider, change it to new
		} else if ("yes".equalsIgnoreCase(autoUnfileLabs))
		{
			logger.info(String.format("unfiling lab %s (%s) for %s", labId, labType, provider_no));
			providerLabRoutingDao.updateStatus("N", labId, labType, provider_no, "F");
		}
	}

	public void route(String labId, String provider_no, String labType) throws SQLException
	{
        addToLabRouting(labId, provider_no, labType);
    }

    public static HashMap<String,Object> getInfo(String lab_no, String lab_type) throws SQLException {
	HashMap<String, Object> info = new HashMap<String, Object>();
	String sql = "SELECT * FROM providerLabRouting WHERE lab_no='"+lab_no+"' AND lab_type='"+lab_type+"'";

	ResultSet rs = DBHandler.GetSQL(sql);

	if (rs.next()) {
	    info.put("lab_no", lab_no);
	    info.put("provider_no", rs.getString("provider_no"));
	    info.put("status", rs.getString("status"));
	    info.put("comment", rs.getString("comment"));
	    info.put("timestamp", rs.getString("timestamp"));
	    info.put("lab_type", rs.getString("lab_type"));
	    info.put("id", rs.getInt("id"));
	}
	return info;
    }
}
