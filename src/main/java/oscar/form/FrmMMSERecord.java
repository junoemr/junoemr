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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.oscarehr.util.LoggedInInfo;

import oscar.oscarDB.DBHandler;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

public class FrmMMSERecord  extends FrmRecord {
    public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID)
            throws SQLException {
        Properties props = new Properties();

        if (existingID <= 0)
        {
            String sql = "SELECT demographic_no, CONCAT(last_name, ', ', first_name) AS pName, "
                + "sex, year_of_birth, month_of_birth, date_of_birth "
                + "FROM demographic WHERE demographic_no = " + demographicNo;
            ResultSet rs = DBHandler.GetSQL(sql);

            if (rs.next())
            {
                java.util.Date dob = UtilDateUtilities.calcDate(oscar.Misc.getString(rs, "year_of_birth"),
                        oscar.Misc.getString(rs, "month_of_birth"),
                        oscar.Misc.getString(rs, "date_of_birth"));
                props.setProperty("demographic_no", oscar.Misc.getString(rs, "demographic_no"));
                props.setProperty("pName", oscar.Misc.getString(rs, "pName"));
                props.setProperty("formDate", ConversionUtils.toDateString(new Date(), "yyyy/MM/dd"));
                props.setProperty("formCreated", ConversionUtils.toDateString(new Date(), "yyyy/MM/dd"));
                props.setProperty("sex", oscar.Misc.getString(rs, "sex"));
                props.setProperty("age", String.valueOf(UtilDateUtilities.calcAge(dob)));
            }
            rs.close();

        }
        else
        {
            String sql = "SELECT * FROM formMMSE WHERE demographic_no = " +demographicNo +" AND ID = " +existingID;
			props = (new FrmRecordHelp()).getFormRecord(sql);
        }

        return props;
    }

    public int saveFormRecord(Properties props) throws SQLException {
        String demographic_no = props.getProperty("demographic_no");
        String sql = "SELECT * FROM formMMSE WHERE demographic_no=" +demographic_no +" AND ID=0";

		return ((new FrmRecordHelp()).saveFormRecord(props, sql));
	}

    public Properties getPrintRecord(int demographicNo, int existingID) throws SQLException  {
        String sql = "SELECT * FROM formMMSE WHERE demographic_no = " +demographicNo +" AND ID = " +existingID ;
		return ((new FrmRecordHelp()).getPrintRecord(sql));
    }


    public String findActionValue(String submit) throws SQLException {
 		return ((new FrmRecordHelp()).findActionValue(submit));
    }

    public String createActionURL(String where, String action, String demoId, String formId) throws SQLException {
 		return ((new FrmRecordHelp()).createActionURL(where, action, demoId, formId));
    }

}
