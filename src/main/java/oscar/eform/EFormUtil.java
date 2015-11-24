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


package oscar.eform;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.dao.CaseManagementNoteLinkDAO;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.dao.EFormValueDao;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.EFormValue;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.eform.actions.DisplayImageAction;
import oscar.eform.data.EForm;
import oscar.eform.data.EFormBase;
import oscar.oscarDB.DBHandler;
import oscar.oscarMessenger.data.MsgMessageData;
import oscar.oscarProvider.data.ProviderData;
import oscar.util.OscarRoleObjectPrivilege;
import oscar.util.UtilDateUtilities;

public class EFormUtil {
	private static final Logger logger = MiscUtils.getLogger();

	// for sorting....
	public static final String NAME = "form_name";
	public static final String SUBJECT = "subject";
	public static final String DATE = "form_date DESC, form_time DESC";
	public static final String FILE_NAME = "file_name";
	// -----------
	public static final String DELETED = "deleted";
	public static final String CURRENT = "current";
	public static final String ALL = "all";

	private static CaseManagementManager cmm = (CaseManagementManager) SpringUtils.getBean("caseManagementManager");
	private static CaseManagementNoteLinkDAO cmDao = (CaseManagementNoteLinkDAO) SpringUtils.getBean("CaseManagementNoteLinkDAO");
	private static EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
	private static EFormValueDao eFormValueDao = (EFormValueDao) SpringUtils.getBean("EFormValueDao");

	private EFormUtil() {
	}

	public static String saveEForm(EForm eForm) {
		return saveEForm(eForm.getFormName(), eForm.getFormSubject(), eForm.getFormFileName(), eForm.getFormHtml(), eForm.getFormCreator(), eForm.getPatientIndependent(), eForm.getRoleType());
	}

	public static String saveEForm(String formName, String formSubject, String fileName, String htmlStr) {
		return saveEForm(formName, formSubject, fileName, htmlStr, false, null);
	}

	public static String saveEForm(String formName, String formSubject, String fileName, String htmlStr, boolean patientIndependent, String roleType) {
		return saveEForm(formName, formSubject, fileName, htmlStr, null, patientIndependent, roleType);
	}

	public static String saveEForm(String formName, String formSubject, String fileName, String htmlStr, String creator, boolean patientIndependent, String roleType) {
		// called by the upload action, puts the uploaded form into DB
		String nowDate = UtilDateUtilities.DateToString(UtilDateUtilities.now(), "yyyy-MM-dd");
		String nowTime = UtilDateUtilities.DateToString(UtilDateUtilities.now(), "HH:mm:ss");
		htmlStr = "\n" + org.apache.commons.lang.StringEscapeUtils.escapeSql(htmlStr);
		formName = org.apache.commons.lang.StringEscapeUtils.escapeSql(formName);
		formSubject = org.apache.commons.lang.StringEscapeUtils.escapeSql(formSubject);
		fileName = org.apache.commons.lang.StringEscapeUtils.escapeSql(fileName);
		roleType = org.apache.commons.lang.StringEscapeUtils.escapeSql(roleType);
		if (creator == null) creator = "NULL";
		else creator = "'" + creator + "'";
		if (roleType == null) roleType = "NULL";
		else roleType = "'" + roleType + "'";
		String sql = "INSERT INTO eform (form_name, file_name, subject, form_date, form_time, form_creator, status, form_html, patient_independent,roleType) VALUES " + "('" + formName + "', '" + fileName + "', '" + formSubject + "', '" + nowDate + "', '" + nowTime + "', " + creator + ", 1, '" + htmlStr + "', " + patientIndependent + "," + roleType + ")";
		return (runSQLinsert(sql));
	}

	public static ArrayList<HashMap<String, ? extends Object>> listEForms(String sortBy, String deleted) {

		// sends back a list of forms that were uploaded (those that can be added to the patient)
		String sql = "";
		if (deleted.equals("deleted")) {
			sql = "SELECT * FROM eform where status=0 ORDER BY " + sortBy;
		} else if (deleted.equals("current")) {
			sql = "SELECT * FROM eform where status=1 ORDER BY " + sortBy;
		} else if (deleted.equals("all")) {
			sql = "SELECT * FROM eform ORDER BY " + sortBy;
		}
		ResultSet rs = getSQL(sql);
		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		try {
			while (rs.next()) {
				HashMap<String, Object> curht = new HashMap<String, Object>();
				curht.put("fid", rsGetString(rs, "fid"));
				curht.put("formName", rsGetString(rs, "form_name"));
				curht.put("formSubject", rsGetString(rs, "subject"));
				curht.put("formFileName", rsGetString(rs, "file_name"));
				curht.put("formDate", rsGetString(rs, "form_date"));
				curht.put("formDateAsDate", rs.getDate("form_date"));
				curht.put("formTime", rsGetString(rs, "form_time"));
				curht.put("roleType", rsGetString(rs, "roleType"));
				results.add(curht);
			}
			rs.close();
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static ArrayList<HashMap<String, ? extends Object>> listEForms(String sortBy, String deleted, String userRoles) {
		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		ArrayList<HashMap<String, ? extends Object>> eForms = listEForms(sortBy, deleted);
		if (eForms.size() > 0) {
			for (int i = 0; i < eForms.size(); i++) {
				HashMap<String, ? extends Object> curForm = eForms.get(i);
				// filter eform by role type
				if (curForm.get("roleType") != null && !curForm.get("roleType").equals("")) {
					// ojectName: "_admin,_admin.eform"
					// roleName: "doctor,admin"
					String objectName = "_eform." + curForm.get("roleType");
					Vector v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
					if (!OscarRoleObjectPrivilege.checkPrivilege(userRoles, (Properties) v.get(0), (Vector) v.get(1))) {
						continue;
					}
				}
				results.add(curForm);
			}
		}
		return (results);
	}

	public static ArrayList<String> listSecRole() {
		// sends back a list of forms that were uploaded (those that can be added to the patient)
		String sql = "";
		sql = "SELECT role_name FROM secRole";

		ResultSet rs = getSQL(sql);
		ArrayList<String> results = new ArrayList<String>();
		try {
			while (rs.next()) {
				results.add(rsGetString(rs, "role_name"));
			}
			rs.close();
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static ArrayList<String> listImages() {
		String imagePath = OscarProperties.getInstance().getProperty("eform_image");
		logger.debug("Img Path: " + imagePath);
		File dir = new File(imagePath);
		String[] files = dir.list();
		ArrayList<String> fileList;
		if (files != null) fileList = new ArrayList<String>(Arrays.asList(files));
		else fileList = new ArrayList<String>();

		return fileList;
	}

	public static ArrayList<HashMap<String,? extends Object>> listPatientEForms(String sortBy, String deleted, String demographic_no, String userRoles) {

		Boolean current = null;
		if (deleted.equals("deleted")) current = false;
		else if (deleted.equals("current")) current = true;

		List<EFormData> allEformDatas = eFormDataDao.findByDemographicIdCurrentPatientIndependent(Integer.parseInt(demographic_no), current, false);

		if (NAME.equals(sortBy)) Collections.sort(allEformDatas, EFormData.FORM_NAME_COMPARATOR);
		else if (SUBJECT.equals(sortBy)) Collections.sort(allEformDatas, EFormData.FORM_SUBJECT_COMPARATOR);
		else Collections.sort(allEformDatas, EFormData.FORM_DATE_COMPARATOR);

		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		try {
			for (EFormData eFormData : allEformDatas) {
				// filter eform by role type
				String tempRole = StringUtils.trimToNull(eFormData.getRoleType());
				if (userRoles != null && tempRole != null) {
					// ojectName: "_admin,_admin.eform"
					// roleName: "doctor,admin"
					String objectName = "_eform." + tempRole;
					Vector v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
					if (!OscarRoleObjectPrivilege.checkPrivilege(userRoles, (Properties) v.get(0), (Vector) v.get(1))) {
						continue;
					}
				}
				HashMap<String, Object> curht = new HashMap<String, Object>();
				curht.put("fdid", eFormData.getId().toString());
				curht.put("fid", eFormData.getFormId().toString());
				curht.put("formName", eFormData.getFormName());
				curht.put("formSubject", eFormData.getSubject());
				curht.put("formDate", eFormData.getFormDate().toString());
				curht.put("formTime", eFormData.getFormTime().toString());
				curht.put("formDateAsDate", eFormData.getFormDate());
				curht.put("roleType", eFormData.getRoleType());
				curht.put("providerNo", eFormData.getProviderNo());
				results.add(curht);
			}
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static ArrayList<HashMap<String,? extends Object>> listPatientIndependentEForms(String sortBy, String deleted) {

		Boolean current = null;
		if (deleted.equals("deleted")) current = false;
		else if (deleted.equals("current")) current = true;

		List<EFormData> allEformDatas = eFormDataDao.findPatientIndependent(current);

		if (NAME.equals(sortBy)) Collections.sort(allEformDatas, EFormData.FORM_NAME_COMPARATOR);
		else if (SUBJECT.equals(sortBy)) Collections.sort(allEformDatas, EFormData.FORM_SUBJECT_COMPARATOR);
		else Collections.sort(allEformDatas, EFormData.FORM_DATE_COMPARATOR);

		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		try {
			for (EFormData eFormData : allEformDatas) {
				HashMap<String, Object> curht = new HashMap<String, Object>();
				curht.put("fdid", eFormData.getId().toString());
				curht.put("fid", eFormData.getFormId().toString());
				curht.put("formName", eFormData.getFormName());
				curht.put("formSubject", eFormData.getSubject());
				curht.put("formDate", eFormData.getFormDate().toString());
				curht.put("formTime", eFormData.getFormTime().toString());
				curht.put("formDateAsDate", eFormData.getFormDate());
				curht.put("roleType", eFormData.getRoleType());
				curht.put("providerNo", eFormData.getProviderNo());
				results.add(curht);
			}
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static ArrayList<HashMap<String,? extends Object>> listPatientEFormsNoData(String demographic_no, String userRoles) {

		Boolean current = true;

		List<Map<String,Object>> allEformDatas = eFormDataDao.findByDemographicIdCurrentPatientIndependentNoData(Integer.parseInt(demographic_no), current, false);


		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		try {
			for (Map<String,Object> eFormData : allEformDatas) {
				// filter eform by role type
				String tempRole = StringUtils.trimToNull((String)eFormData.get("roleType"));
				if (userRoles != null && tempRole != null) {
					// ojectName: "_admin,_admin.eform"
					// roleName: "doctor,admin"
					String objectName = "_eform." + tempRole;
					Vector v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
					if (!OscarRoleObjectPrivilege.checkPrivilege(userRoles, (Properties) v.get(0), (Vector) v.get(1))) {
						continue;
					}
				}
				HashMap<String, Object> curht = new HashMap<String, Object>();
				curht.put("fdid", String.valueOf(eFormData.get("id")));
				curht.put("fid",  String.valueOf(eFormData.get("formId")));
				curht.put("formName", eFormData.get("formName"));
				curht.put("formSubject", eFormData.get("subject"));
				curht.put("formDate",  String.valueOf(eFormData.get("formDate")));
				curht.put("formTime",  String.valueOf(eFormData.get("formTime")));
				curht.put("formDateAsDate", eFormData.get("formDate"));
				curht.put("roleType", eFormData.get("roleType"));
				curht.put("providerNo", eFormData.get("providerNo"));
				results.add(curht);
			}
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static ArrayList<HashMap<String,? extends Object>> loadEformsByFdis(List<Integer> ids) {

		List<EFormData> allEformDatas = eFormDataDao.findByFdids(ids);

		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		try {
			for (EFormData eFormData : allEformDatas) {
				HashMap<String, Object> curht = new HashMap<String, Object>();
				curht.put("fdid", eFormData.getId().toString());
				curht.put("fid", eFormData.getFormId().toString());
				curht.put("formName", eFormData.getFormName());
				curht.put("formSubject", eFormData.getSubject());
				curht.put("formDate", eFormData.getFormDate().toString());
				curht.put("formTime", eFormData.getFormTime().toString());
				curht.put("formDateAsDate", eFormData.getFormDate());
				curht.put("roleType", eFormData.getRoleType());
				curht.put("providerNo", eFormData.getProviderNo());
				results.add(curht);
			}
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static Hashtable<String,Object> loadEForm(String fid) {
		// opens an eform that was uploaded (used to fill out patient data)
		String sql = "SELECT * FROM eform where fid=" + fid + " LIMIT 1";
		ResultSet rs = getSQL(sql);
		Hashtable<String,Object> curht = new Hashtable<String,Object>();
		try {
			rs.next();
			// must have FID and form_name otherwise throws null pointer on the hashtable
			curht.put("fid", rsGetString(rs, "fid"));
			curht.put("formName", rsGetString(rs, "form_name"));
			curht.put("formSubject", rsGetString(rs, "subject"));
			curht.put("formFileName", rsGetString(rs, "file_name"));
			curht.put("formDate", rsGetString(rs, "form_date"));
			curht.put("formTime", rsGetString(rs, "form_time"));
			curht.put("formCreator", rsGetString(rs, "form_creator"));
			curht.put("formHtml", rsGetString(rs, "form_html"));
			curht.put("patientIndependent", rs.getBoolean("patient_independent"));
			curht.put("roleType", rsGetString(rs, "roleType"));
			rs.close();
		} catch (SQLException sqe) {
			curht.put("formName", "");
			curht.put("formHtml", "No Such Form in Database");
			logger.error("Error", sqe);
		}
		return (curht);
	}

	public static void updateEForm(EFormBase updatedForm) {
		// Updates the form - used by editForm
		String formHtml = "\n" + org.apache.commons.lang.StringEscapeUtils.escapeSql(updatedForm.getFormHtml());
		String formName = org.apache.commons.lang.StringEscapeUtils.escapeSql(updatedForm.getFormName());
		String formSubject = org.apache.commons.lang.StringEscapeUtils.escapeSql(updatedForm.getFormSubject());
		String fileName = org.apache.commons.lang.StringEscapeUtils.escapeSql(updatedForm.getFormFileName());
		String roleType = org.apache.commons.lang.StringEscapeUtils.escapeSql(updatedForm.getRoleType());

		String sql = "UPDATE eform SET " + "form_name='" + formName + "', " + "file_name='" + fileName + "', " + "subject='" + formSubject + "', " + "form_date='" + updatedForm.getFormDate() + "', " + "form_time='" + updatedForm.getFormTime() + "', " + "form_html='" + formHtml + "', " + "patient_independent=" + updatedForm.getPatientIndependent() + ", " + "roleType='" + roleType + "' " + "WHERE fid=" + updatedForm.getFid() + ";";
		runSQL(sql);
	}

	/*
	 * +--------------+--------------+------+-----+---------+----------------+ | Field | Type | Null | Key | Default | Extra | +--------------+--------------+------+-----+---------+----------------+ | fid | int(8) | | PRI | NULL | auto_increment | |
	 * form_name | varchar(255) | YES | | NULL | | | file_name | varchar(255) | YES | | NULL | | | subject | varchar(255) | YES | | NULL | | | form_date | date | YES | | NULL | | | form_time | time | YES | | NULL | | | form_creator | varchar(255) | YES | |
	 * NULL | | | status | tinyint(1) | | | 1 | | | form_html | text | YES | | NULL | | +--------------+--------------+------+-----+---------+----------------+
	 */

	public static String getEFormParameter(String fid, String Column) {
		String dbColumn = "";
		if (Column.equalsIgnoreCase("formName")) dbColumn = "form_name";
		else if (Column.equalsIgnoreCase("formSubject")) dbColumn = "subject";
		else if (Column.equalsIgnoreCase("formFileName")) dbColumn = "file_name";
		else if (Column.equalsIgnoreCase("formDate")) dbColumn = "form_date";
		else if (Column.equalsIgnoreCase("formTime")) dbColumn = "form_time";
		else if (Column.equalsIgnoreCase("formStatus")) dbColumn = "status";
		else if (Column.equalsIgnoreCase("formHtml")) dbColumn = "form_html";
		else if (Column.equalsIgnoreCase("patientIndependent")) dbColumn = "patient_independent";
		else if (Column.equalsIgnoreCase("roleType")) dbColumn = "roleType";
		String sql = "SELECT " + dbColumn + " FROM eform WHERE fid=" + fid;
		ResultSet rs = getSQL(sql);
		try {
			while (rs.next()) {
				return rsGetString(rs, dbColumn);
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return null;
	}

	public static String getEFormIdByName(String name) {
		// opens an eform that was uploaded (used to fill out patient data)
		String sql = "SELECT MAX(fid) FROM eform WHERE form_name='" + name + "' AND status=1";
		ResultSet rs = getSQL(sql);
		try {
			if (rs.next()) {
				return rsGetString(rs, "MAX(fid)");
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return null;
	}

	public static void delEForm(String fid) {
		// deletes the form so no one can add it to the patient (sets status to deleted)
		String sql = "UPDATE eform SET status=0 WHERE fid=" + fid;
		runSQL(sql);
	}

	public static void restoreEForm(String fid) {
		String sql = "UPDATE eform SET status=1 WHERE fid=" + fid;
		runSQL(sql);
	}

	public static ArrayList<String> getValues(ArrayList<String> names, String sql) {
		// gets the values for each column name in the sql (used by DatabaseAP)
		ResultSet rs = getSQL(sql);
		ArrayList<String> values = new ArrayList<String>();
		try {
			while (rs.next()) {
				values = new ArrayList<String>();
				for (int i = 0; i < names.size(); i++) {
					try {
						values.add(oscar.Misc.getString(rs, names.get(i)));
						logger.debug("VALUE ====" + rs.getObject(names.get(i)) + "|");
					} catch (Exception sqe) {
						values.add("<(" + names.get(i) + ")NotFound>");
						logger.error("Error", sqe);
					}
				}
			}
			rs.close();
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return (values);
	}

	// used by addEForm for escaping characters
	public static String charEscape(String S, char a) {
		if (null == S) {
			return S;
		}
		int N = S.length();
		StringBuilder sb = new StringBuilder(N);
		for (int i = 0; i < N; i++) {
			char c = S.charAt(i);
			// escape the escape characters
			if (c == '\\') {
				sb.append("\\\\");
			} else if (c == a) {
				sb.append("\\" + a);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static void addEFormValues(ArrayList<String> names, ArrayList<String> values, Integer fdid, Integer fid, Integer demographic_no) {
		// adds parsed values and names to DB
		// names.size and values.size must equal!
		try {
			for (int i = 0; i < names.size(); i++) {
				EFormValue eFormValue = new EFormValue();
				eFormValue.setFormId(fid);
				eFormValue.setFormDataId(fdid);
				eFormValue.setDemographicId(demographic_no);
				eFormValue.setVarName(names.get(i));
				eFormValue.setVarValue(values.get(i));

				eFormValueDao.persist(eFormValue);
			}
		} catch (PersistenceException ee) {
			logger.error("Unexpected Error", ee);
		}
	}

	public static boolean formExistsInDB(String eFormName) {
		String sql = "SELECT * FROM eform WHERE status=1 AND form_name='" + eFormName + "'";
		try {
			ResultSet rs = getSQL(sql);
			if (rs.next()) {
				rs.close();
				return true;
			} else {
				rs.close();
				return false;
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return false;
	}

	public static int formExistsInDBn(String formName, String fid) {
		// returns # of forms in the DB with that name other than itself
		String sql = "SELECT count(*) AS count FROM eform WHERE status=1 AND form_name='" + formName + "' AND fid!=" + fid;
		try {
			ResultSet rs = getSQL(sql);
			while (rs.next()) {
				int numberRecords = rs.getInt("count");
				rs.close();
				return numberRecords;
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return 0;
	}

	// --------------eform groups---------
	public static ArrayList<Hashtable<String,String>> getEFormGroups() {
		String sql;
		sql = "SELECT DISTINCT eform_groups.group_name, count(*)-1 AS 'count' FROM eform_groups " + "LEFT JOIN eform ON eform.fid=eform_groups.fid WHERE eform.status=1 OR eform_groups.fid=0 " + "GROUP BY eform_groups.group_name;";
		ArrayList<Hashtable<String,String>> al = new ArrayList<Hashtable<String,String>>();
		try {
			ResultSet rs = getSQL(sql);
			while (rs.next()) {
				Hashtable<String,String> curhash = new Hashtable<String,String>();
				curhash.put("groupName", oscar.Misc.getString(rs, "group_name"));
				curhash.put("count", oscar.Misc.getString(rs, "count"));
				al.add(curhash);
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return al;
	}

	public static ArrayList<Hashtable<String,String>> getEFormGroups(String demographic_no) {
		String sql;

		sql = "SELECT group_name, sum(count) AS 'count' FROM (SELECT eform_groups.group_name, count(*)-1 AS 'count' FROM eform_groups LEFT JOIN eform_data ON eform_data.fid=eform_groups.fid WHERE eform_data.status=1 AND eform_data.demographic_no=" + demographic_no + " GROUP BY eform_groups.group_name UNION SELECT eg.group_name, count(*)-1 AS 'count' FROM eform_groups AS eg WHERE eg.fid = 0 GROUP BY eg.group_name) as sub_query GROUP BY group_name";

		ArrayList<Hashtable<String,String>> al = new ArrayList<Hashtable<String,String>>();
		try {
			ResultSet rs = getSQL(sql);
			while (rs.next()) {
				Hashtable<String,String> curhash = new Hashtable<String,String>();
				curhash.put("groupName", oscar.Misc.getString(rs, "group_name"));
				curhash.put("count", oscar.Misc.getString(rs, "count"));
				al.add(curhash);
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return al;
	}

	public static void delEFormGroup(String name) {
		String sql = "DELETE FROM eform_groups WHERE group_name='" + name + "'";
		runSQL(sql);
	}

	public static void addEFormToGroup(String groupName, String fid) {
		try {

			String sql1 = "SELECT eform_groups.fid FROM eform_groups, eform WHERE eform_groups.fid=" + fid + " AND eform_groups.fid=eform.fid AND eform.status=1 AND eform_groups.group_name='" + groupName + "'";
			ResultSet rs = DBHandler.GetSQL(sql1);
			if (!rs.next()) {
				String sql = "INSERT INTO eform_groups (fid, group_name) " + "VALUES (" + fid + ", '" + groupName + "')";
				DBHandler.RunSQL(sql);
			}
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
	}

	public static void remEFormFromGroup(String groupName, String fid) {
		String sql = "DELETE FROM eform_groups WHERE group_name='" + groupName + "' and fid=" + fid + ";";
		runSQL(sql);
	}

	public static ArrayList<HashMap<String,? extends Object>> listEForms(String sortBy, String deleted, String group, String userRoles) {
		// sends back a list of forms that were uploaded (those that can be added to the patient)
		String sql = "";
		if (deleted.equals("deleted")) {
			sql = "SELECT * FROM eform, eform_groups where eform.status=0 AND eform.fid=eform_groups.fid AND eform_groups.group_name='" + group + "' ORDER BY " + sortBy;
		} else if (deleted.equals("current")) {
			sql = "SELECT * FROM eform, eform_groups where eform.status=1 AND eform.fid=eform_groups.fid AND eform_groups.group_name='" + group + "' ORDER BY " + sortBy;
		} else if (deleted.equals("all")) {
			sql = "SELECT * FROM eform AND eform.fid=eform_groups.fid AND eform_groups.group_name='" + group + "' ORDER BY " + sortBy;
		}
		ResultSet rs = getSQL(sql);
		ArrayList<HashMap<String, ? extends Object>> results = new ArrayList<HashMap<String, ? extends Object>>();
		try {
			while (rs.next()) {
				HashMap<String,String> curht = new HashMap<String,String>();
				curht.put("fid", rsGetString(rs, "fid"));
				curht.put("formName", rsGetString(rs, "form_name"));
				curht.put("formSubject", rsGetString(rs, "subject"));
				curht.put("formFileName", rsGetString(rs, "file_name"));
				curht.put("formDate", rsGetString(rs, "form_date"));
				curht.put("formTime", rsGetString(rs, "form_time"));
				curht.put("roleType", rsGetString(rs, "roleType"));
				results.add(curht);
			}
			rs.close();
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static ArrayList<HashMap<String,? extends Object>> listPatientEForms(String sortBy, String deleted, String demographic_no, String groupName, String userRoles) {
		// sends back a list of forms added to the patient
		String sql = "";
		if (deleted.equals("deleted")) {
			sql = "SELECT * FROM eform_data, eform_groups WHERE eform_data.status=0 AND eform_data.patient_independent=0 AND eform_data.demographic_no=" + demographic_no + " AND eform_data.fid=eform_groups.fid AND eform_groups.group_name='" + groupName + "' ORDER BY " + sortBy;
		} else if (deleted.equals("current")) {
			sql = "SELECT * FROM eform_data, eform_groups WHERE eform_data.status=1 AND eform_data.patient_independent=0 AND eform_data.demographic_no=" + demographic_no + " AND eform_data.fid=eform_groups.fid AND eform_groups.group_name='" + groupName + "' ORDER BY " + sortBy;
		} else if (deleted.equals("all")) {
			sql = "SELECT * FROM eform_data, eform_groups WHERE eform_data.patient_independent=0 AND eform_data.demographic_no=" + demographic_no + " AND eform_data.fid=eform_groups.fid AND eform_groups.group_name='" + groupName + "' ORDER BY " + sortBy;
		}
		ResultSet rs = getSQL(sql);
		ArrayList<HashMap<String,? extends Object>> results = new ArrayList<HashMap<String,? extends Object>>();
		try {
			while (rs.next()) {
				// filter eform by role type
				if (rsGetString(rs, "roleType") != null && !rsGetString(rs, "roleType").equals("") && !rsGetString(rs, "roleType").equals("null")) {
					// ojectName: "_admin,_admin.eform"
					// roleName: "doctor,admin"
					String objectName = "_eform." + rsGetString(rs, "roleType");
					Vector v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
					if (!OscarRoleObjectPrivilege.checkPrivilege(userRoles, (Properties) v.get(0), (Vector) v.get(1))) {
						continue;
					}
				}
				HashMap<String,String> curht = new HashMap<String,String>();
				curht.put("fdid", oscar.Misc.getString(rs, "fdid"));
				curht.put("fid", rsGetString(rs, "fid"));
				curht.put("formName", rsGetString(rs, "form_name"));
				curht.put("formSubject", rsGetString(rs, "subject"));
				curht.put("formDate", rsGetString(rs, "form_date"));
				curht.put("formTime", rsGetString(rs, "form_time"));
				curht.put("roleType", rsGetString(rs, "roleType"));
				results.add(curht);
			}
			rs.close();
		} catch (Exception sqe) {
			logger.error("Error", sqe);
		}
		return (results);
	}

	public static void writeEformTemplate(ArrayList<String> paramNames, ArrayList<String> paramValues, EForm eForm, String fdid, String programNo, String context_path, String ipAddress) {
		String text = eForm != null ? eForm.getTemplate() : null;
		if (blank(text)) return;

		text = putTemplateValues(paramNames, paramValues, text);
		text = putTemplateEformValues(eForm, fdid, context_path, text);
		String[] template_echart = { "EncounterNote", "SocialHistory", "FamilyHistory", "MedicalHistory", "OngoingConcerns", "RiskFactors", "Reminders", "OtherMeds" };
		String[] code = { "", "SocHistory", "FamHistory", "MedHistory", "Concerns", "RiskFactors", "Reminders", "OMeds" };
		ArrayList<String> templates = new ArrayList<String>();

		// write to echart
		for (int i = 0; i < template_echart.length; i++) {
			templates = getWithin(template_echart[i], text);
			for (String template : templates) {
				if (blank(template)) continue;

				template = putTemplateEformHtml(eForm.getFormHtml(), template);
				saveCMNote(eForm, fdid, programNo, code[i], template);
			}
		}

		// write to document
		templates = getWhole("document", text);
		for (String template : templates) {
			if (blank(template)) continue;

			String docDesc = getInfo("docdesc", template, eForm.getFormName());
			String belong = getAttribute("belong", getBeginTag("document", template));
			if (blank(belong)) belong = "provider";
			String docOwner = getInfo("docowner", template, eForm.getProviderNo());
			if (belong.equalsIgnoreCase("patient")) docOwner = getInfo("docowner", template, eForm.getDemographicNo());
			String docText = getContent(template);
			docText = putTemplateEformHtml(eForm.getFormHtml(), docText);

                        if (NumberUtils.isDigits(docOwner)) {
                            EDoc edoc = new EDoc(docDesc,"forms","html",docText,docOwner,eForm.getProviderNo(),"",'H',eForm.getFormDate().toString(),"",null,belong,docOwner, ipAddress);
                            edoc.setContentType("text/html");
                            edoc.setDocPublic("0");
                            EDocUtil.addDocumentSQL(edoc);
                        }
		}

		// write to message
		templates = getWithin("message", text);
		for (String template : templates) {
			if (blank(template)) continue;

			String subject = getInfo("subject", template, eForm.getFormName());
			String sentWho = getSentWho(template);
			String[] sentList = getSentList(template);
			String userNo = eForm.getProviderNo();
			String userName = getProviderName(eForm.getProviderNo());
			String message = getContent(template);
			message = putTemplateEformHtml(eForm.getFormHtml(), message);

			MsgMessageData msg = new MsgMessageData();
			msg.sendMessage2(message, subject, userName, sentWho, userNo, msg.getProviderStructure(sentList), null, null);
		}
	}

	public static int findIgnoreCase(String phrase, String text, int start) {
		if (blank(phrase) || blank(text)) return -1;

		text = text.toLowerCase();
		phrase = phrase.toLowerCase();
		return text.indexOf(phrase, start);
	}

	public static String removeQuotes(String s) {
		if (blank(s)) return s;

		s = s.trim();
		if (blank(s)) return s;

                if (s.charAt(0)=='"' && s.charAt(s.length()-1)=='"') s = s.substring(1, s.length()-1);
		if (blank(s)) return s;

                if (s.charAt(0)=='\'' && s.charAt(s.length()-1)=='\'') s = s.substring(1, s.length()-1);
		return s.trim();
	}

	public static String getAttribute(String key, String htmlTag) {
		return getAttribute(key, htmlTag, false);
	}

	public static String getAttribute(String key, String htmlTag, boolean startsWith) {
		if (blank(key) || blank(htmlTag)) return null;

		Matcher m = getAttributeMatcher(key, htmlTag, startsWith);
		if (m == null) return null;

		String value = m.group();
		int keysplit = m.group().indexOf("=");
		if (!startsWith && keysplit >= 0) value = m.group().substring(keysplit + 1, m.group().length());

		return value.trim();
	}

	public static int getAttributePos(String key, String htmlTag) {
		int pos = -1;
		if (blank(key) || blank(htmlTag)) return pos;

		Matcher m = getAttributeMatcher(key, htmlTag, false);
		if (m == null) return pos;

		return m.start();
	}

	public static boolean blank(String s) {
		return (s == null || s.trim().equals(""));
	}

	// ------------------private
	private static void runSQL(String sql) {
		try {

			DBHandler.RunSQL(sql);
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
	}

	private static String runSQLinsert(String sql) {
		try {

			DBHandler.RunSQL(sql);
			sql = "SELECT LAST_INSERT_ID()";
			ResultSet rs = DBHandler.GetSQL(sql);
			rs.next();
			String lastID = oscar.Misc.getString(rs, "LAST_INSERT_ID()");
			rs.close();
			return (lastID);
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return "";
	}

	private static ResultSet getSQL(String sql) {
		ResultSet rs = null;
		try {

			rs = DBHandler.GetSQL(sql);
		} catch (SQLException sqe) {
			logger.error("Error", sqe);
		}
		return (rs);
	}

	private static String rsGetString(ResultSet rs, String column) throws SQLException {
		// protects agianst null values;
		String thisStr = oscar.Misc.getString(rs, column);
		if (thisStr == null) return "";
		return thisStr;
	}

	private static Matcher getAttributeMatcher(String key, String htmlTag, boolean startsWith) {
		Matcher m_return = null;
		if (blank(key) || blank(htmlTag)) return m_return;

		Pattern p = Pattern.compile("\\b[^\\s'\"=>]+[ ]*=[ ]*\"[^\"]*\"|\\b[^\\s'\"=>]+[ ]*=[ ]*'[^']*'|\\b[^\\s'\"=>]+[ ]*=[ ]*[^ >]*|\\b[^\\s>]+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(htmlTag);

		while (m.find()) {
			int keysplit = m.group().indexOf("=");
			if (keysplit < 0) keysplit = m.group().length();

			String keypart = m.group().substring(0, keysplit).trim().toLowerCase();
			key = key.trim().toLowerCase();
			if ((keypart.equals(key)) || (startsWith && keypart.startsWith(key))) {
				m_return = m;
				break;
			}
		}
		return m_return;
	}

	private static String putTemplateValues(ArrayList<String> paramNames, ArrayList<String> paramValues, String template) {
		if (blank(template)) return template;

		String tag = "$t{";
		String nwTemplate = "";
		int pointer = 0;
		ArrayList<Integer> fieldBeginList = getFieldIndices(tag, template);
		for (int fieldBegin : fieldBeginList) {
			nwTemplate += template.substring(pointer, fieldBegin);
			int fieldEnd = template.indexOf("}", fieldBegin);
			pointer = fieldEnd + 1;
			String field = template.substring(fieldBegin + tag.length(), fieldEnd);
			if (paramNames.contains(field)) {
				nwTemplate +=  paramValues.get(paramNames.indexOf(field));
			} else {
				nwTemplate += "{" + field + "}";
				logger.error("EForm Template Error! Cannot find input name {" + field + "} in eform");
			}
		}
		nwTemplate += template.substring(pointer, template.length());
		return nwTemplate;
	}

	private static String putTemplateEformValues(EForm eForm, String fdid, String path, String template) {
		if (eForm == null || blank(template)) return template;

		String[] efields = { "name", "subject", "patient", "provider", "link" };
		String[] eValues = { eForm.getFormName(), eForm.getFormSubject(), eForm.getDemographicNo(), eForm.getProviderNo(), "<a href='" + path + "/eform/efmshowform_data.jsp?fdid=" + fdid + "' target='_blank'>" + eForm.getFormName() + "</a>" };

		String tag = "$te{";
		String nwTemplate = "";
		int pointer = 0;
		ArrayList<Integer> fieldBeginList = getFieldIndices(tag, template);
		for (int fieldBegin : fieldBeginList) {
			nwTemplate += template.substring(pointer, fieldBegin);
			int fieldEnd = template.indexOf("}", fieldBegin);
			pointer = fieldEnd + 1;
			String field = template.substring(fieldBegin + tag.length(), fieldEnd);
			if (field.equalsIgnoreCase("eform.html")) {
				nwTemplate += "$te{eform.html}";
				continue;
			}
			boolean match = false;
			for (int i = 0; i < efields.length; i++) {
				if (field.equalsIgnoreCase("eform." + efields[i])) {
					nwTemplate += eValues[i];
					match = true;
					break;
				}
			}
			if (!match) {
				nwTemplate += "{" + field + "}";
				logger.error("EForm Template Error! Cannot find input name {" + field + "} in eform");
			}
		}
		nwTemplate += template.substring(pointer, template.length());
		return nwTemplate;
	}

	private static String putTemplateEformHtml(String html, String template) {
		if (blank(html) || blank(template)) return "";

		html = removeAction(html);
		String tag = "$te{eform.html}";
		String nwTemplate = "";
		int pointer = 0;
		ArrayList<Integer> fieldBeginList = getFieldIndices(tag, template);
		for (int fieldBegin : fieldBeginList) {
			nwTemplate += template.substring(pointer, fieldBegin);
			nwTemplate += html;
			pointer = fieldBegin + tag.length();
		}
		nwTemplate += template.substring(pointer, template.length());
		return nwTemplate;
	}

	private static String removeAction(String html) {
		if (blank(html)) return html;

		Pattern p = Pattern.compile("<form[^<>]*>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(html);
		String nwHtml = "";
		int pointer = 0;
		while (m.find()) {
			nwHtml += html.substring(pointer, m.start());
			String formTag = m.group();
			pointer += m.start() + formTag.length();
			p = Pattern.compile("\\baction[ ]*=[ ]*\"[^>\"]*\"|\\b[a-zA-Z]+[ ]*=[ ]*'[^>']*'|\\b[a-zA-Z]+[ ]*=[^ >]*", Pattern.CASE_INSENSITIVE);
			m = p.matcher(formTag);
			nwHtml += m.replaceAll("");
		}
		nwHtml += html.substring(pointer, html.length());
		return nwHtml;
	}

	private static void saveCMNote(EForm eForm, String fdid, String programNo, String code, String note) {
		if (blank(note)) return;

		CaseManagementNote cNote = createCMNote(eForm.getDemographicNo(), eForm.getProviderNo(), programNo, note);
		if (!blank(code)) {
			Set<CaseManagementIssue> scmi = createCMIssue(eForm.getDemographicNo(), code);
			cNote.setIssues(scmi);
		}
		cmm.saveNoteSimple(cNote);
		CaseManagementNoteLink cmLink = new CaseManagementNoteLink(CaseManagementNoteLink.EFORMDATA, Long.valueOf(fdid), cNote.getId());
		cmDao.save(cmLink);
	}

	private static CaseManagementNote createCMNote(String demographicNo, String providerNo, String programNo, String note) {
		CaseManagementNote cmNote = new CaseManagementNote();
		cmNote.setUpdate_date(new Date());
		cmNote.setObservation_date(new Date());
		cmNote.setDemographic_no(demographicNo);
		cmNote.setProviderNo(providerNo);
		cmNote.setSigning_provider_no(providerNo);
		cmNote.setSigned(true);
		cmNote.setHistory("");

		SecRoleDao secRoleDao = (SecRoleDao) SpringUtils.getBean("secRoleDao");
		SecRole doctorRole = secRoleDao.findByName("doctor");
		cmNote.setReporter_caisi_role(doctorRole.getId().toString());

		cmNote.setReporter_program_team("0");
		cmNote.setProgram_no(programNo);
		cmNote.setUuid(UUID.randomUUID().toString());
		cmNote.setNote(note);

		return cmNote;
	}

	private static Set<CaseManagementIssue> createCMIssue(String demographicNo, String code) {
		Issue isu = cmm.getIssueInfoByCode(code);
		CaseManagementIssue cmIssu = cmm.getIssueById(demographicNo, isu.getId().toString());
		if (cmIssu == null) {
			cmIssu = new CaseManagementIssue();
			cmIssu.setDemographic_no(demographicNo);
			cmIssu.setIssue_id(isu.getId());
			cmIssu.setType(isu.getType());
			cmm.saveCaseIssue(cmIssu);
		}

		Set<CaseManagementIssue> sCmIssu = new HashSet<CaseManagementIssue>();
		sCmIssu.add(cmIssu);
		return sCmIssu;
	}

	private static ArrayList<Integer> getFieldIndices(String fieldtag, String s) {
		ArrayList<Integer> fieldIndexList = new ArrayList<Integer>();
		if (blank(fieldtag) || blank(s)) return fieldIndexList;

		fieldtag = fieldtag.toLowerCase();
		s = s.toLowerCase();

		int from = 0;
		int fieldAt = s.indexOf(fieldtag, from);
		while (fieldAt >= 0) {
			fieldIndexList.add(fieldAt);
			from = fieldAt + 1;
			fieldAt = s.indexOf(fieldtag, from);
		}
		return fieldIndexList;
	}

	private static String getInfo(String tag, String template, String deflt) {
		if (blank(tag) || blank(template)) return deflt;

		ArrayList<String> infos = getWithin(tag, template);
		if (infos.isEmpty()) return deflt;

		String info = infos.get(0).replaceAll("\\s", "");
		return blank(info) ? deflt : info;
	}

	private static String getContent(String template) {
		ArrayList<String> contents = getWithin("content", template);
		if (contents.isEmpty()) return "";

		String content = contents.get(0).trim();
		if (content.startsWith("\n")) content = content.substring(1); // remove 1st line break, UNIX style
		else if (content.startsWith("\r\n")) content = content.substring(2); // remove 1st line break, WINDOWS style
		return content;
	}

	private static ArrayList<String> getWithin(String tag, String s) {
		ArrayList<String> within = new ArrayList<String>();
		if (blank(tag) || blank(s)) return within;

		ArrayList<String> w = getWhole(tag, s);
		for (String whole : w) {
			int begin = getBeginTag(tag, whole).length();
			int end = whole.length() - ("</" + tag + ">").length();
			within.add(whole.substring(begin, end));
		}
		return within;
	}

	private static ArrayList<String> getWhole(String tag, String s) {
		ArrayList<String> whole = new ArrayList<String>();
		if (blank(tag) || blank(s)) return whole;

		String sBegin = "<" + tag;
		String sEnd = "</" + tag + ">";
		int begin = -1;
		boolean search = true;
		while (search) {
			begin = findIgnoreCase(sBegin, s, begin + 1);
			if (begin == -1) {
				search = false;
				break;
			}
			int end = findIgnoreCase(sEnd, s, begin);

			// search for middle begin tags; if found, extend end tag to include complete begin-end within
			int mid_begin = findIgnoreCase(sBegin, s, begin + 1);
			int count_mbegin = 0;
			while (mid_begin >= 0 && mid_begin < end) {
				count_mbegin++;
				mid_begin = findIgnoreCase(sBegin, s, mid_begin + 1);
			}
			while (count_mbegin > 0) {
				end = findIgnoreCase(sEnd, s, end + 1);
				if (end >= 0) count_mbegin--;
				else count_mbegin = -1;
			}
			// end search

			if (begin >= 0 && end >= 0) {
				whole.add(s.substring(begin, end + sEnd.length()));
			}
		}
		return whole;
	}

	private static String getBeginTag(String tag, String template) {
		if (blank(tag) || blank(template)) return "";

		Pattern p = Pattern.compile("<" + tag + "[^<>]*>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(template);
		return m.find() ? m.group() : "";
	}

	private static String getProviderName(String providerNo) {
                if (blank(providerNo)) return null;

		ProviderData pd = new ProviderData(providerNo);
		String firstname = pd.getFirst_name();
		String lastname = pd.getLast_name();
		String name = blank(firstname) ? "" : firstname;
		name += blank(lastname) ? "" : " " + lastname;
		return name;
	}

	private static String[] getSentList(String msgtxt) {
		String sentListTxt = getInfo("sendto", msgtxt, "");
		String[] sentList = sentListTxt.split(",");
		for (int i = 0; i < sentList.length; i++) {
			sentList[i] = sentList[i].trim();
		}
		return sentList;
	}

	private static String getSentWho(String msgtxt) {
		String[] sentList = getSentList(msgtxt);
		String sentWho = "";
		for (String sent : sentList) {
			sent = getProviderName(sent);
			if (!blank(sentWho) && !blank(sent)) {
				sentWho += ", " + sent;
			} else {
				sentWho += sent;
			}
		}
		return sentWho;
	}

	public static ArrayList<String> listRichTextLetterTemplates() {
		String imagePath = OscarProperties.getInstance().getProperty("eform_image");
		MiscUtils.getLogger().debug("Img Path: " + imagePath);
		File dir = new File(imagePath);
		String[] files = DisplayImageAction.getRichTextLetterTemplates(dir);
		ArrayList<String> fileList;
		if( files != null )
			fileList = new ArrayList<String>(Arrays.asList(files));
		else
			fileList = new ArrayList<String>();

		return fileList;
	}

}
