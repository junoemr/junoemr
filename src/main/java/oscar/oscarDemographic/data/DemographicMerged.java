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
 * DemographicMergedDAO.java
 *
 * Created on September 14, 2007, 1:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarDemographic.data;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.oscarehr.util.DbConnectionFilter;

import oscar.util.UtilDateUtilities;

/**
 *
 * @author wrighd
 */
public class DemographicMerged {

    Logger logger = Logger.getLogger(DemographicMerged.class);

    /** Creates a new instance of DemographicMergedDAO */
    public DemographicMerged() {
    }

    public void Merge(String demographic_no, String head) throws SQLException{

        Connection conn = DbConnectionFilter.getThreadLocalDbConnection();

        String sql = "insert into demographic_merged (demographic_no, merged_to) values (?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // always merge the head of records that have already been merged to the new head
        String record_head = getHead(demographic_no);
        if (record_head == null)
            pstmt.setInt(1, Integer.parseInt( demographic_no ));
        else
            pstmt.setInt(1, Integer.parseInt(record_head));

        pstmt.setInt(2, Integer.parseInt( head ));
        pstmt.executeUpdate();
        pstmt.close();

        sql = "insert into secObjPrivilege (roleUserGroup, objectName, privilege, priority, provider_no) values ('_all','_eChart$"+demographic_no+"','|or|','0','0')";
        pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();
        pstmt.close();
    }


	public void FullMerge(String from_demographic_no_string, 
		String to_demographic_no_string, String provider_no_string) 
		throws Exception, SQLException
	{

        Connection conn = DbConnectionFilter.getThreadLocalDbConnection();

		try
		{
			int provider_no = Integer.parseInt(provider_no_string);
			int to_demographic_no = Integer.parseInt(to_demographic_no_string);
			int from_demographic_no = Integer.parseInt(
				from_demographic_no_string);

			// Save the autocommit state, then turn it off
			boolean auto_commit_state = conn.getAutoCommit();
			conn.setAutoCommit(false);

			
			// Log the merge
			String sql = "INSERT INTO demographic_merged_full(" +
				"provider_no, " +
				"to_demographic_no, " +
				"from_demographic_no" +
				") VALUES (?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, provider_no);
			statement.setInt(2, to_demographic_no);
			statement.setInt(3, from_demographic_no);

			this.makeDbCall(statement);

			// Perform the merge steps

			String[] sql_array = {
				"UPDATE demographicExt " +
					"SET demographicExt.demographic_no = ? " +
					"WHERE demographicExt.demographic_no = ? ;",

				"UPDATE eChart " +
					"SET eChart.demographicNo = ? " +
					"WHERE eChart.demographicNo = ? ;",

				"UPDATE casemgmt_note " +
					"SET casemgmt_note.demographic_no = ? " +
					"WHERE casemgmt_note.demographic_no = ? ;",

				"UPDATE formGrowth0_36 " +
					"SET formGrowth0_36.demographic_no = ? " +
					"WHERE formGrowth0_36.demographic_no = ? ;",

				"UPDATE casemgmt_cpp " +
					"SET casemgmt_cpp.demographic_no = ? " +
					"WHERE casemgmt_cpp.demographic_no = ? ;",

				"UPDATE preventions " +
					"SET preventions.demographic_no = ? " +
					"WHERE preventions.demographic_no = ? ;",

				"UPDATE consultationRequests " +
					"SET consultationRequests.demographicNo = ? " +
					"WHERE consultationRequests.demographicNo = ? ;",

				"UPDATE eform_values " +
					"SET eform_values.demographic_no = ? " +
					"WHERE eform_values.demographic_no = ? ;",

				"UPDATE eform_data " +
					"SET eform_data.demographic_no = ? " +
					"WHERE eform_data.demographic_no = ? ;",

				"UPDATE casemgmt_issue " +
					"SET casemgmt_issue.demographic_no = ? " +
					"WHERE casemgmt_issue.demographic_no = ? ;",

				"UPDATE ctl_document " +
					"SET ctl_document.module_id = ? " +
					"WHERE ctl_document.module_id = ? ;",

				"UPDATE msgDemoMap " +
					"SET msgDemoMap.demographic_no = ? " +
					"WHERE msgDemoMap.demographic_no = ? ;",

				"UPDATE formGrowthChart " +
					"SET formGrowthChart.demographic_no = ? " +
					"WHERE formGrowthChart.demographic_no = ? "
			};

			for(int i = 0; i < sql_array.length; i++)	
			{
				statement = conn.prepareStatement(sql_array[i]);
				statement.setInt(1, to_demographic_no);
				statement.setInt(2, from_demographic_no);
				this.makeDbCall(statement);
			}

			// TODO: merge growth charts
			this.mergeGrowthChart(conn, to_demographic_no);
			this.mergeGrowthChart0To36(conn, to_demographic_no);


			/*
			-- There doesn't seem to be anything useful in here to merge                    
			-- UPDATE admission           
			-- SET admission.demographic_no = ?                       
			-- WHERE admission.demographic_no = ? ;                   
			*/

			/*
			// Nothing in here
			-- UPDATE demographiccust     
			-- SET demographiccust.demographic_no = ?                 
			-- WHERE demographiccust.demographic_no = ? ;             
			*/






			// Mark old demographics as inactive
			sql = "UPDATE demographic SET patient_status = 'IN' " +
				"WHERE demographic_no = ?;";

			statement = conn.prepareStatement(sql);
			statement.setInt(1, from_demographic_no);
			this.makeDbCall(statement);


			// Mark new demographics as active
			sql = "UPDATE demographic SET patient_status = 'AC' " +
				"WHERE demographic_no = ?;";

			statement = conn.prepareStatement(sql);
			statement.setInt(1, to_demographic_no);
			this.makeDbCall(statement);

			// Merge fields between demographic records
			sql = "UPDATE demographic AS d_new, demographic AS d_old " +
				"SET d_new.hin = IF(d_new.hin = '' OR d_new.hin IS NULL, d_old.hin, d_new.hin)," +
				"d_new.address = IF(d_new.address = '' OR d_new.address IS NULL, d_old.address, d_new.address)," +
				"d_new.city = IF(d_new.city = '' OR d_new.city IS NULL, d_old.city, d_new.city)," +
				"d_new.province = IF(d_new.province = '' OR d_new.province IS NULL, d_old.province, d_new.province)," +
				"d_new.postal = IF(d_new.postal = '' OR d_new.postal IS NULL, d_old.postal, d_new.postal)," +
				"d_new.phone = IF(d_new.phone = '' OR d_new.phone IS NULL, d_old.phone, d_new.phone)," +
				"d_new.phone2 = IF(d_new.phone2 = '' OR d_new.phone2 IS NULL, d_old.phone2, d_new.phone2)," +
				"d_new.email = IF(d_new.email = '' OR d_new.email IS NULL, d_old.email, d_new.email)," +
				"d_new.chart_no = IF(d_new.chart_no = '' OR d_new.chart_no IS NULL, d_old.chart_no, d_new.chart_no)," +
				"d_new.official_lang = IF(d_new.official_lang = '' OR d_new.official_lang IS NULL, d_old.official_lang, d_new.official_lang)," +
				"d_new.spoken_lang = IF(d_new.spoken_lang = '' OR d_new.spoken_lang IS NULL, d_old.spoken_lang, d_new.spoken_lang)," +
				"d_new.sex = IF(d_new.sex = '' OR d_new.sex IS NULL, d_old.sex, d_new.sex)," +
				"d_new.hc_type = IF(d_new.hc_type = '' OR d_new.hc_type IS NULL, d_old.hc_type, d_new.hc_type)," +
				"d_new.hc_renew_date = IF(d_new.hc_renew_date = '' OR d_new.hc_renew_date IS NULL, d_old.hc_renew_date, d_new.hc_renew_date)," +
				"d_new.family_doctor = IF(d_new.family_doctor = '' OR d_new.family_doctor IS NULL, d_old.family_doctor, d_new.family_doctor)," +
				"d_new.alias = IF(d_new.alias = '' OR d_new.alias IS NULL, d_old.alias, d_new.alias)," +
				"d_new.previousAddress = IF(d_new.previousAddress = '' OR d_new.previousAddress IS NULL, d_old.previousAddress, d_new.previousAddress)," +
				"d_new.children = IF(d_new.children = '' OR d_new.children IS NULL, d_old.children, d_new.children)," +
				"d_new.sourceOfIncome = IF(d_new.sourceOfIncome = '' OR d_new.sourceOfIncome IS NULL, d_old.sourceOfIncome, d_new.sourceOfIncome)," +
				"d_new.citizenship = IF(d_new.citizenship = '' OR d_new.citizenship IS NULL, d_old.citizenship, d_new.citizenship)," +
				"d_new.sin = IF(d_new.sin = '' OR d_new.sin IS NULL, d_old.sin, d_new.sin)," +
				"d_new.country_of_origin = IF(d_new.country_of_origin = '' OR d_new.country_of_origin IS NULL, d_old.country_of_origin, d_new.country_of_origin)," +
				"d_new.newsletter = IF(d_new.newsletter = '' OR d_new.newsletter IS NULL, d_old.newsletter, d_new.newsletter)," +
				"d_new.anonymous = IF(d_new.anonymous = '' OR d_new.anonymous IS NULL, d_old.anonymous, d_new.anonymous) " +
				"WHERE d_new.demographic_no = ? " +
				"AND d_old.demographic_no = ?;";

			statement = conn.prepareStatement(sql);
			statement.setInt(1, to_demographic_no);
			statement.setInt(2, from_demographic_no);
			this.makeDbCall(statement);


			// Commit Transaction
			conn.commit();

			// Restore autocommit state
			conn.setAutoCommit(auto_commit_state);
		}
		catch(SQLException e)
		{
			// Roll back transaction
			conn.rollback();

			// Throw the exception
			throw e;
		}
	}

	private void mergeGrowthChart(Connection conn, int demographic_no)
		throws Exception, SQLException
	{
		String sql = "SELECT demographic_no, provider_no, formCreated, formEdited, " +
			"patientName, recordNo, motherStature, fatherStature " +
			"FROM formGrowthChart " +
			"WHERE demographic_no = ? " +
			"ORDER BY formCreated DESC, formEdited DESC " +
			"LIMIT 1;";

		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, demographic_no);
		ResultSet rs = statement.executeQuery();

		if(!rs.next())
		{
			// No rows, leave
			return;
		}

		sql = "CREATE TABLE IF NOT EXISTS growth_temp (" +
			"demographic_no int," +
			"record_date date," +
			"age varchar(5)," +
			"stature varchar(5)," +
			"weight varchar(6)," +
			"comment varchar(25)," +
			"bmi varchar(5)" +
			");";

		statement = conn.prepareStatement(sql);
		this.makeDbCall(statement);

		sql = "DELETE FROM growth_temp;";

		statement = conn.prepareStatement(sql);
		this.makeDbCall(statement);


		String update_sql = "UPDATE formGrowthChart SET ";

		for(int i = 1; i <= 42; i++)
		{
			sql = "INSERT INTO growth_temp " +
				"(demographic_no, record_date, age, stature, weight, comment, bmi)" +

				"SELECT demographic_no, date_" + i + ", age_" + i + 
				", stature_" + i + ", weight_" + i + ", comment_" + i + 
				", bmi_" + i + " " +
				"FROM formGrowthChart " +
				"WHERE demographic_no = ? " +
				"AND date_" + i + " IS NOT NULL;";

			statement = conn.prepareStatement(sql);
			statement.setInt(1, demographic_no);
			this.makeDbCall(statement);

			update_sql += "age_1 = IFNULL(age_1,'')," +
				"stature_1 = IFNULL(stature_1,'')," +
				"weight_1 = IFNULL(weight_1,'')," +
				"comment_1 = IFNULL(comment_1,'')," +
				"bmi_1 = IFNULL(bmi_1,''),";
		}

		String insert_sql = "INSERT INTO formGrowthChart (demographic_no, " +
			"provider_no, formCreated, formEdited, patientName, recordNo, " +
			"motherStature, fatherStature) " +
			"SELECT demographic_no, provider_no, formCreated, formEdited, " +
			"patientName, recordNo, motherStature, fatherStature " +
			"FROM formGrowthChart " +
			"WHERE demographic_no = ? " +
			"ORDER BY formCreated DESC, formEdited DESC " +
			"LIMIT 1;";

		statement = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
		statement.setInt(1, demographic_no);
		this.makeOpenDbCall(statement);

		// Get the key to insert to
		ResultSet keys = statement.getGeneratedKeys();
		int form_id;
		if(keys.next())
		{
			form_id = keys.getInt(1);
			logger.info("fid " + form_id);
		}
		else
		{
			throw new Exception("Error getting generated key.");
		}
		
		statement.close();


		for(int i = 1; i <= 42; i++)
		{
			sql = "UPDATE formGrowthChart " +
				"SET " +
				"date_" + i + " = (SELECT record_date FROM growth_temp WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, stature, weight, comment, bmi ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"age_" + i + " = (SELECT age FROM growth_temp WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, stature, weight, comment, bmi ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"stature_" + i + " = (SELECT IFNULL(stature,'') FROM growth_temp WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, stature, weight, comment, bmi ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"weight_" + i + " = (SELECT IFNULL(weight,'') FROM growth_temp WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, stature, weight, comment, bmi ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"comment_" + i + " = (SELECT IFNULL(comment,'') FROM growth_temp WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, stature, weight, comment, bmi ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"bmi_" + i + " = (SELECT IFNULL(bmi,'') FROM growth_temp WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, stature, weight, comment, bmi ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ") " +
				"WHERE ID = ?;";

			statement = conn.prepareStatement(sql);
			statement.setInt(1, demographic_no);
			statement.setInt(2, demographic_no);
			statement.setInt(3, demographic_no);
			statement.setInt(4, demographic_no);
			statement.setInt(5, demographic_no);
			statement.setInt(6, demographic_no);
			statement.setInt(7, form_id);
			this.makeDbCall(statement);
		}

		// Remove the trailing comma
		update_sql = update_sql.substring(0, update_sql.length() - 1);

		update_sql += "WHERE ID = ?;";

		statement = conn.prepareStatement(update_sql);
		statement.setInt(1, form_id);
		this.makeDbCall(statement);
	}

	private void mergeGrowthChart0To36(Connection conn, int demographic_no)
		throws Exception, SQLException
	{
		String sql = "SELECT demographic_no, provider_no, formCreated, formEdited, " +
			"patientName, recordNo, motherStature, fatherStature " +
			"FROM formGrowth0_36 " +
			"WHERE demographic_no = ? " +
			"ORDER BY formCreated DESC, formEdited DESC " +
			"LIMIT 1;";

		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setInt(1, demographic_no);
		ResultSet rs = statement.executeQuery();

		if(!rs.next())
		{
			// No rows, leave
			return;
		}

		sql = "CREATE TABLE IF NOT EXISTS growth_temp0_36 (" +
			"demographic_no int," +
			"record_date date," +
			"age varchar(5)," +
			"length varchar(5)," +
			"weight varchar(6)," +
			"comment varchar(25)," +
			"headCirc varchar(5)" +
			");";


		statement = conn.prepareStatement(sql);
		this.makeDbCall(statement);

		sql = "DELETE FROM growth_temp0_36;";

		statement = conn.prepareStatement(sql);
		this.makeDbCall(statement);


		String update_sql = "UPDATE formGrowth0_36 SET ";

		for(int i = 1; i <= 20; i++)
		{
			sql = "INSERT INTO growth_temp0_36 " +
				"(demographic_no, record_date, age, length, weight, comment, headCirc)" +

				"SELECT demographic_no, date_" + i + ", age_" + i + 
				", length_" + i + ", weight_" + i + ", comment_" + i + 
				", headCirc_" + i + " " +
				"FROM formGrowth0_36 " +
				"WHERE demographic_no = ? " +
				"AND date_" + i + " IS NOT NULL;";


			statement = conn.prepareStatement(sql);
			statement.setInt(1, demographic_no);
			this.makeDbCall(statement);

			update_sql += "age_1 = IFNULL(age_1,'')," +
				"length_1 = IFNULL(length_1,'')," +
				"weight_1 = IFNULL(weight_1,'')," +
				"comment_1 = IFNULL(comment_1,'')," +
				"headCirc_1 = IFNULL(headCirc_1,''),";
		}

		String insert_sql = "INSERT INTO formGrowth0_36 (demographic_no, " +
			"provider_no, formCreated, formEdited, patientName, recordNo, " +
			"motherStature, gestationalAge, fatherStature) " +
			"SELECT demographic_no, provider_no, formCreated, formEdited, " +
			"patientName, recordNo, motherStature, gestationalAge, fatherStature " +
			"FROM formGrowth0_36 " +
			"WHERE demographic_no = ? " +
			"ORDER BY formCreated DESC, formEdited DESC " +
			"LIMIT 1;";

		statement = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
		statement.setInt(1, demographic_no);
		this.makeOpenDbCall(statement);

		// Get the key to insert to
		ResultSet keys = statement.getGeneratedKeys();
		int form_id;
		if(keys.next())
		{
			form_id = keys.getInt(1);
			logger.info("fid " + form_id);
		}
		else
		{
			throw new Exception("Error getting generated key.");
		}
		
		statement.close();


		for(int i = 1; i <= 20; i++)
		{
			sql = "UPDATE formGrowth0_36 " +
				"SET " +
				"date_" + i + " = (SELECT record_date FROM growth_temp0_36 WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, length, weight, comment, headCirc ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"age_" + i + " = (SELECT age FROM growth_temp0_36 WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, length, weight, comment, headCirc ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"length_" + i + " = (SELECT IFNULL(length,'') FROM growth_temp0_36 WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, length, weight, comment, headCirc ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"weight_" + i + " = (SELECT IFNULL(weight,'') FROM growth_temp0_36 WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, length, weight, comment, headCirc ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"comment_" + i + " = (SELECT IFNULL(comment,'') FROM growth_temp0_36 WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, length, weight, comment, headCirc ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ")," +
				"headCirc_" + i + " = (SELECT IFNULL(headCirc,'') FROM growth_temp0_36 WHERE demographic_no = ? GROUP BY demographic_no, record_date, age, length, weight, comment, headCirc ORDER BY record_date LIMIT 1 OFFSET " + (i-1) + ") " +
				"WHERE ID = ?;";

			statement = conn.prepareStatement(sql);
			statement.setInt(1, demographic_no);
			statement.setInt(2, demographic_no);
			statement.setInt(3, demographic_no);
			statement.setInt(4, demographic_no);
			statement.setInt(5, demographic_no);
			statement.setInt(6, demographic_no);
			statement.setInt(7, form_id);
			this.makeDbCall(statement);
		}

		// Remove the trailing comma
		update_sql = update_sql.substring(0, update_sql.length() - 1);

		update_sql += "WHERE ID = ?;";

		statement = conn.prepareStatement(update_sql);
		statement.setInt(1, form_id);
		this.makeDbCall(statement);
	}

	private void makeOpenDbCall(PreparedStatement statement) throws SQLException
	{
		try
		{
			statement.executeUpdate();
		}
		catch(SQLException e)
		{
			// Log error on failure
			logger.error(e.getMessage());

			// Throw exception on failure
			throw e;
		}
	}

	private void makeDbCall(PreparedStatement statement) throws SQLException
	{
		try
		{
			statement.executeUpdate();
			statement.close();
		}
		catch(SQLException e)
		{
			// Log error on failure
			logger.error(e.getMessage());

			// Throw exception on failure
			throw e;
		}
	}

    public void UnMerge(String demographic_no, String curUser_no) throws SQLException{


        Connection conn = DbConnectionFilter.getThreadLocalDbConnection();

        String sql = "update demographic_merged set deleted=1 where demographic_no=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, Integer.parseInt( demographic_no ));

        pstmt.executeUpdate();
        pstmt.close();

        sql = "select * from secObjPrivilege where roleUserGroup='_all' and objectName='_eChart$"+demographic_no+"'";
        //rs = dbObj.searchDBRecord(sql);
        pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        String privilege = "";
        String priority = "";
        String provider_no = "";
        while (rs.next()) {
            privilege = oscar.Misc.getString(rs, "privilege");
            priority = oscar.Misc.getString(rs, "priority");
            provider_no = oscar.Misc.getString(rs, "provider_no");
        }
        pstmt.close();

        sql = "delete from secObjPrivilege where roleUserGroup='_all' and objectName='_eChart$"+demographic_no+"'";
        pstmt = conn.prepareStatement(sql);
        if(!pstmt.execute()){
            logger.info("we should be writing to the recycle bin");
            pstmt.close();
            sql = "insert into recyclebin (provider_no,updatedatetime,table_name,keyword,table_content) values(";
            sql += "'" + curUser_no + "',";
            sql += "?,";
            sql += "'" + "secObjPrivilege" + "',";
            sql += "'_all|_eChart$"+ demographic_no + "',";
            sql += "'" + "<roleUserGroup>_all</roleUserGroup>" + "<objectName>_eChart$" + demographic_no + "</objectName>";
            sql += "<privilege>" + privilege + "</privilege>" + "<priority>" + priority + "</priority>";
            sql += "<provider_no>" + provider_no + "</provider_no>" + "')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, oscar.MyDateFormat.getSysDate(UtilDateUtilities.getToday("yyyy-MM-dd HH:mm:ss")));
            pstmt.executeUpdate();
        }

        pstmt.close();

    }

    public String getHead(String demographic_no) throws SQLException{

        Connection conn = DbConnectionFilter.getThreadLocalDbConnection();
        ResultSet rs;
        String head = null;

        String sql = "select merged_to from demographic_merged where demographic_no = ? and deleted = 0";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, Integer.parseInt(demographic_no));
        rs = pstmt.executeQuery();
        if(rs.next())
            head = oscar.Misc.getString(rs, "merged_to");

        pstmt.close();
        if (head != null)
            head = getHead(head);
        else
            head = demographic_no;

        return head;

    }

    public ArrayList<String> getTail(String demographic_no) throws SQLException{


        Connection conn = DbConnectionFilter.getThreadLocalDbConnection();
        ResultSet rs;
        ArrayList<String> tailArray = new ArrayList<String>();

        String sql = "select demographic_no from demographic_merged where merged_to = ? and deleted = 0";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, Integer.parseInt(demographic_no));
        rs = pstmt.executeQuery();
        while(rs.next()){
            tailArray.add(oscar.Misc.getString(rs, "demographic_no"));
        }

        pstmt.close();
        int size = tailArray.size();
        for (int i=0; i < size; i++){
            tailArray.addAll(getTail(  tailArray.get(i) ));
        }

        return tailArray;

    }
}
