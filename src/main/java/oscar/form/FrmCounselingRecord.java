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
import java.util.Date;
import java.util.Properties;

import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.util.ConversionUtils;
/**
 *
 * @author kimleanhoffman
 */

public class FrmCounselingRecord extends FrmRecord{

	@Override
	public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID) throws SQLException{
		Properties props = new Properties();
		Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicNo);

		if (existingID <= 0 && demographic != null)
		{
			props.setProperty("demographic_no", String.valueOf(demographicNo));
			props.setProperty("formCreated", ConversionUtils.toDateString(new Date(),"dd/MM/yyyy"));
		}
		else
		{
			String sql = "SELECT * FROM formCounseling WHERE demographic_no = " +demographicNo +" AND ID = " +existingID;
			FrmRecordHelp frmRec = new FrmRecordHelp();
			frmRec.setDateFormat(dateFormat);
			props = frmRec.getFormRecord(sql);
		}

		// Get local clinic information
		ClinicDAO clinicDao = (ClinicDAO)SpringUtils.getBean("clinicDAO");
		Clinic clinic = clinicDao.getClinic();

		props.setProperty("clinicName", clinic.getClinicName()==null ? "" : clinic.getClinicName());
		props.setProperty("clinicProvince", clinic.getClinicProvince()==null ? "" : clinic.getClinicProvince());
		props.setProperty("clinicAddress", clinic.getClinicAddress()==null ? "" : clinic.getClinicAddress());
		props.setProperty("clinicCity", clinic.getClinicCity()==null ? "" : clinic.getClinicCity());
		props.setProperty("clinicPhone", clinic.getClinicPhone()==null ? "" : clinic.getClinicPhone());
		props.setProperty("clinicFax", clinic.getClinicFax()==null ? "" : clinic.getClinicFax());

		return props;
	}

	@Override
	public int saveFormRecord(Properties props) throws SQLException {
		String demographicNo = props.getProperty("demographic_no");
		String sql = "SELECT * FROM formCounseling WHERE demographic_no=" +demographicNo +" AND ID=0";
		FrmRecordHelp frmRec = new FrmRecordHelp();
		frmRec.setDateFormat(dateFormat);
		return frmRec.saveFormRecord(props, sql);
	}

	@Override
	public String findActionValue(String submit) throws SQLException {
		return ((new FrmRecordHelp()).findActionValue(submit));
	}

	@Override
	public String createActionURL(String where, String action, String demoId, String formId) throws SQLException {
		return ((new FrmRecordHelp()).createActionURL(where, action, demoId, formId));
	}

}
