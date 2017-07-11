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
import java.util.Map;
import java.util.Properties;

import org.oscarehr.common.dao.DemographicExtDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.SxmlMisc;
import oscar.util.UtilDateUtilities;

public class FrmBCAR2007Record extends FrmRecord {
    private String _dateFormat = "dd/MM/yyyy";

    private DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);

    public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID) throws SQLException {
        
    	Demographic demo = demographicManager.getDemographic(loggedInInfo, demographicNo);
    	Properties props = new Properties();

        String demographicNoStr = Integer.toString(demographicNo);
        String formCreated = UtilDateUtilities.DateToString(new Date(),_dateFormat);
        String surname = demo.getLastName();
        String firstname = demo.getFirstName();
        String address = demo.getAddress();
        String city = demo.getCity();
        String province = demo.getProvince();
        String postal = demo.getPostal();
        String phn = demo.getHin();
        String dateOfBirth = UtilDateUtilities.DateToString(date, _dateFormat);
        String age = String.valueOf(UtilDateUtilities.calcAge(date));
        String phone = demo.getPhone();
        String phoneAlt = demo.getPhone2();
        String pg1FormDate = UtilDateUtilities.DateToString(new Date(), _dateFormat);
        String pg2FormDate = UtilDateUtilities.DateToString(new Date(), _dateFormat);
        String pg3FormDate = UtilDateUtilities.DateToString(new Date(), _dateFormat);

        if (existingID <= 0) {
            if (demo != null) {
                java.util.Date date = UtilDateUtilities.calcDate(demo.getYearOfBirth(), demo.getMonthOfBirth(), demo.getDateOfBirth());

                if(demographicNoStr != null)
                    props.setProperty("demographic_no", demographicNoStr);
                if(formCreated != null)
                    props.setProperty("formCreated", formCreated);
                if(surname != null)
                    props.setProperty("c_surname", surname);
                if(firstname != null)
                    props.setProperty("c_givenName", firstname);
                if(address != null)
                    props.setProperty("c_address", address);
                if(city != null)
                    props.setProperty("c_city", city);
                if(province != null)
                    props.setProperty("c_province", province);
                if(postal != null)
                    props.setProperty("c_postal", postal);
                if(phn != null)
                    props.setProperty("c_phn", postal);
                if(dateOfBirth != null)
                    props.setProperty("pg1_dateOfBirth", dateOfBirth);
                if(age != null)
                    props.setProperty("pg1_age", age);
                if(phone != null)
                    props.setProperty("c_phone", phone);
                if(phoneAlt != null)
                    props.setProperty("c_phoneAlt1", phoneAlt);
                if(pg1FormDate != null)
                    props.setProperty("pg1_formDate", pg1FormDate);
                if(pg2FormDate != null)
                    props.setProperty("pg2_formDate", pg2FormDate);
                if(pg2FormDate != null)
                    props.setProperty("pg3_formDate", pg2FormDate);
                
                String rd = SxmlMisc.getXmlContent(demo.getFamilyDoctor(), "rd");
                rd = rd != null ? rd : "";
                props.setProperty("pg1_famPhy", rd);

                Map<String,String> demoExt = demographicExtDao.getAllValuesForDemo(demographicNo);
                String cell = demoExt.get("demo_cell");
                if ( cell != null ){
                    props.setProperty("c_phoneAlt2",cell );
                }
            }
        } else {
            String sql = "SELECT * FROM formBCAR2007 WHERE demographic_no = " + demographicNo + " AND ID = " + existingID;
            FrmRecordHelp frh = new FrmRecordHelp();
            frh.setDateFormat(_dateFormat);
            props = (frh).getFormRecord(sql);
            
            if (demo != null) {
                if(surname != null)
                    props.setProperty("c_surname_cur", surname);
                if(firstname != null)
                    props.setProperty("c_givenName_cur", firstname);
                if(address != null)
                    props.setProperty("c_address_cur", address);
                if(city != null)
                    props.setProperty("c_city_cur", city);
                if(province != null)
                    props.setProperty("c_province_cur", province);
                if(postal != null)
                    props.setProperty("c_postal_cur", postal);
                if(phn != null)
                    props.setProperty("c_phn_cur", phn);
                if(phone != null)
                    props.setProperty("c_phone_cur", phone);
                if(phoneAlt != null)
                    props.setProperty("c_phoneAlt1_cur", phoneAlt);
                
                Map<String,String> demoExt = demographicExtDao.getAllValuesForDemo(demographicNo);
                String cell = demoExt.get("demo_cell");
                if ( cell != null ){
                    props.setProperty("c_phoneAlt2_cur",cell );
                }
            }
        }
        return props;
    }

    public int saveFormRecord(Properties props) throws SQLException {
        String demographic_no = props.getProperty("demographic_no");
        String sql = "SELECT * FROM formBCAR2007 WHERE demographic_no=" + demographic_no + " AND ID=0";

        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).saveFormRecord(props, sql));
    }

    public Properties getPrintRecord(int demographicNo, int existingID) throws SQLException {
        String sql = "SELECT * FROM formBCAR2007 WHERE demographic_no = " + demographicNo + " AND ID = " + existingID;
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).getPrintRecord(sql));
    }

    public String findActionValue(String submit) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).findActionValue(submit));
    }

    public String createActionURL(String where, String action, String demoId, String formId) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(_dateFormat);
        return ((frh).createActionURL(where, action, demoId, formId));
    }

}
