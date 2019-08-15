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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;

import oscar.oscarDB.DBHandler;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

public class FrmRhImmuneGlobulinRecord extends FrmRecord {
	private String _dateFormat = "yyyy-MM-dd";

	public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID)
		throws SQLException {
		Properties props = new Properties();

		ResultSet rs;
		String sql;

		if (existingID <= 0)
		{
			sql = "SELECT * FROM demographic WHERE demographic_no = "
                              + demographicNo;
			rs = DBHandler.GetSQL(sql);
			if (rs.next())
			{
				java.util.Date dob = UtilDateUtilities.calcDate(oscar.Misc.getString(rs, "year_of_birth"),
						oscar.Misc.getString(rs, "month_of_birth"),
						oscar.Misc.getString(rs, "date_of_birth"));
				props.setProperty("demographic_no",oscar.Misc.getString(rs, "demographic_no"));
				props.setProperty("formCreated", ConversionUtils.toDateString(new Date(),_dateFormat));
				props.setProperty("dob", ConversionUtils.toDateString(dob,_dateFormat));
				props.setProperty("sex", oscar.Misc.getString(rs, "sex"));
				props.setProperty("phone", oscar.Misc.getString(rs, "phone"));

				String lastname = oscar.Misc.getString(rs, "last_name");
				MiscUtils.getLogger().debug("last name "+lastname);
				props.setProperty("motherSurname",lastname);
				props.setProperty("motherFirstname",oscar.Misc.getString(rs, "first_name"));
				props.setProperty("motherHIN",oscar.Misc.getString(rs, "hin"));
				props.setProperty("motherVC",oscar.Misc.getString(rs, "ver"));
				props.setProperty("motherCity",oscar.Misc.getString(rs, "city"));
				props.setProperty("motherProvince",oscar.Misc.getString(rs, "province"));
				props.setProperty("motherPostalCode",oscar.Misc.getString(rs, "postal"));

				props.setProperty("motherAddress",oscar.Misc.getString(rs, "address"));

				Hashtable measurementHash = EctMeasurementsDataBeanHandler.getLast(""+demographicNo, "BLDT");

				if (measurementHash != null && measurementHash.get("value") != null)
				{
					props.setProperty("motherABO",(String) measurementHash.get("value"));
				}

				measurementHash = EctMeasurementsDataBeanHandler.getLast(""+demographicNo, "RHT");
				if (measurementHash != null  && measurementHash.get("value") != null)
				{
					props.setProperty("motherRHtype", (String)measurementHash.get("value"));
				}
			}
			rs.close();
		}
		else
		{
			sql =
				"SELECT * FROM formRhImmuneGlobulin WHERE demographic_no = "
					+ demographicNo
					+ " AND ID = "
					+ existingID;
			rs = DBHandler.GetSQL(sql);

			if (rs.next())
			{
				MiscUtils.getLogger().debug("getting metaData");
				ResultSetMetaData md = rs.getMetaData();

				for (int i = 1; i <= md.getColumnCount(); i++)
				{
					String name = md.getColumnName(i);

					String value;
					MiscUtils.getLogger().debug(" name = "+name+" type = "+md.getColumnTypeName(i)+" scale = "+md.getScale(i));
					if (md.getColumnTypeName(i).equalsIgnoreCase("TINY"))
					{
						if (rs.getInt(i) == 1)
						{
							value = "checked='checked'";
							MiscUtils.getLogger().debug("checking "+name);
						}
						else
						{
							value = "";
							MiscUtils.getLogger().debug("not checking "+name);
						}
					}
					else
					{
						if(md.getColumnTypeName(i).equalsIgnoreCase("date"))
						{
							value = ConversionUtils.toDateString(rs.getDate(i),_dateFormat);
						}
						else
						{
							value = oscar.Misc.getString(rs, i);
						}
					}

					if (value!=null)
					{
						props.setProperty(name, value);
					}
				}
			}
			rs.close();
		}
		return props;
	}

	public int saveFormRecord(Properties props) throws SQLException {
		String demographic_no = props.getProperty("demographic_no");
		String sql =
			"SELECT * FROM formRhImmuneGlobulin WHERE demographic_no="
				+ demographic_no
				+ " AND ID=0";

		FrmRecordHelp frh = new FrmRecordHelp();
		frh.setDateFormat(_dateFormat);
		return ((frh).saveFormRecord(props, sql));
	}

	public Properties getPrintRecord(int demographicNo, int existingID)
		throws SQLException {
		String sql =
			"SELECT * FROM formRhImmuneGlobulin WHERE demographic_no = "
				+ demographicNo
				+ " AND ID = "
				+ existingID;
		FrmRecordHelp frh = new FrmRecordHelp();
		frh.setDateFormat(_dateFormat);
		return ((frh).getPrintRecord(sql));
	}

	public String findActionValue(String submit) throws SQLException {
		FrmRecordHelp frh = new FrmRecordHelp();
		frh.setDateFormat(_dateFormat);
		return ((frh).findActionValue(submit));
	}

	public String createActionURL(
		String where,
		String action,
		String demoId,
		String formId)
		throws SQLException {
		FrmRecordHelp frh = new FrmRecordHelp();
		frh.setDateFormat(_dateFormat);
		return ((frh).createActionURL(where, action, demoId, formId));
	}

}
