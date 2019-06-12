
package oscar.form;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.oscarehr.util.LoggedInInfo;

import oscar.oscarDB.DBHandler;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

public class FrmAdfV2Record extends FrmRecord {
	private String _dateFormat = "yyyy/MM/dd";
	public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID)
	throws SQLException {
		Properties props = new Properties();

		if (existingID <= 0)
		{
			
			String sql =
				"SELECT demographic_no, last_name, first_name, sex, address, city, province, postal, phone, phone2, year_of_birth, month_of_birth, date_of_birth, hin FROM demographic WHERE demographic_no = "
				+ demographicNo;
			ResultSet rs = DBHandler.GetSQL(sql);
			if (rs.next()) {
				java.util.Date date = UtilDateUtilities.calcDate(
							oscar.Misc.getString(rs, "year_of_birth"),
							oscar.Misc.getString(rs, "month_of_birth"),
							oscar.Misc.getString(rs, "date_of_birth"));
				props.setProperty("demographic_no", oscar.Misc.getString(rs, "demographic_no"));
				props.setProperty("formCreated", ConversionUtils.toDateString(new Date(), _dateFormat));
				props.setProperty("c_patientname", oscar.Misc.getString(rs, "last_name") + ", " + oscar.Misc.getString(rs, "first_name"));
				props.setProperty(
						"c_address",
						oscar.Misc.getString(rs, "address")
						+ ", "
						+ oscar.Misc.getString(rs, "city")
						+ ", "
						+ oscar.Misc.getString(rs, "province")
						+ " "
						+ oscar.Misc.getString(rs, "postal"));
				props.setProperty("c_phn", oscar.Misc.getString(rs, "hin"));
				props.setProperty("pg1_dateOfBirth", ConversionUtils.toDateString(date, _dateFormat));
				props.setProperty("pg1_age", String.valueOf(UtilDateUtilities.calcAge(date)));
				props.setProperty("c_phone", oscar.Misc.getString(rs, "phone"));
				props.setProperty("sigDate", ConversionUtils.toDateString(new Date(), _dateFormat));
			}
			rs.close();
		} else {
			String sql =
				"SELECT * FROM formAdfV2 WHERE demographic_no = "
				+ demographicNo
				+ " AND ID = "
				+ existingID;
			FrmRecordHelp frh = new FrmRecordHelp();
			frh.setDateFormat(_dateFormat);
			props = (frh).getFormRecord(sql);
		}

		return props;
	}

	public int saveFormRecord(Properties props) throws SQLException {
		String demographic_no = props.getProperty("demographic_no");
		String sql =
			"SELECT * FROM formAdfV2 WHERE demographic_no="
			+ demographic_no
			+ " AND ID=0";

		FrmRecordHelp frh = new FrmRecordHelp();
		frh.setDateFormat(_dateFormat);
		return ((frh).saveFormRecord(props, sql));
	}

	public Properties getPrintRecord(int demographicNo, int existingID)
	throws SQLException {
		String sql =
			"SELECT * FROM formAdfV2 WHERE demographic_no = "
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

	public String createActionURL(String where, String action, String demoId, String formId)
		throws SQLException
	{
		FrmRecordHelp frh = new FrmRecordHelp();
		frh.setDateFormat(_dateFormat);
		return ((frh).createActionURL(where, action, demoId, formId));
	}

}
