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


package oscar.form;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DemographicExtDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicExt;
import org.oscarehr.util.SpringUtils;

/**
 * 
 * @author Dennis Warren
 * @company OSCARprn
 * 
 * Rewritten June 2012
 *
 */
@SuppressWarnings("unused")
public abstract class FrmRecord {
	
	protected Demographic demographic;
	protected DemographicExt demographicExt;
	protected Map<String, String> demographicExtMap;
	private DemographicDao demographicDao;
	private DemographicExtDao demographicExtDao;
	
	public abstract Properties getFormRecord(int demographicNo, int existingID) throws SQLException;
    public abstract int saveFormRecord(Properties props) throws SQLException ;
    public abstract String findActionValue(String submit) throws SQLException ;
    public abstract String createActionURL(String where, String action, String demoId, String formId) throws SQLException ;

	public FrmRecord() {
		// this DAO is used in every transaction - it should be ready to go.
		this.demographicDao = SpringUtils.getBean(DemographicDao.class);
		this.demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
	} 
	
	public FrmRecord(int demographicNo) {
		// this DAO is used in every transaction - it should be ready to go.
		this.demographicDao = SpringUtils.getBean(DemographicDao.class);
		this.demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
		setDemographic(demographicNo);
		setDemographicExt(demographicNo);		
	}
	
	/**
	 * @throws SQLException
	 */
	public Properties getGraph(int demographicNo, int existingID) throws SQLException {
		try {
			return new Properties();
		}
		catch(Exception ex) {
			throw new SQLException(ex);
		}
	}

	/**
	 * @throws SQLException
	 */
	public Properties getCaisiFormRecord(int demographicNo, int existingID, int providerNo, int programNo) throws SQLException {
		try {
			return new Properties();
		}
		catch(Exception ex) {
			throw new SQLException(ex);
		}
	}

	public void setGraphType(String graphType) { /*Rourke needs to know whether plotting head circ or height*/
	}
	
	protected void setDemographic(int demographicNo) {
		if(this.demographicDao != null) {
			this.demographic = demographicDao.getClientByDemographicNo(demographicNo);
			this.setDemographicExt(demographicNo);
		}
	}
	
	protected void setDemographicExt(int demographicNo) {
		if(this.demographicExtDao != null) {
			this.demographicExt = demographicExtDao.getDemographicExt(demographicNo);
			this.demographicExtMap = demographicExtDao.getAllValuesForDemo(""+demographicNo);
		}
	}
}
