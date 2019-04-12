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

import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;

import oscar.SxmlMisc;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;


/**
 * 
 * @author Dennis Warren
 * OSCARprn
 * 
 * Rewritten June 2012
 * New code is a proper re-hash of the file FrmBCAR2007Record.java 
 *
 */
public class FrmBCAR2012Record extends FrmRecord{

	private Properties props;
	
    public Properties getFormRecord(LoggedInInfo loggedInInfo, int demographicNo, int existingID) {
    	      
        Date dateOfBirth;
        Date dateToday = new Date();
    	
        if (demographicNo > 0)
        {
    		super.setDemographic(loggedInInfo, demographicNo);
        }
    	
        if (existingID <= 0)
        {
        	
        	props = new Properties();
        	
            dateOfBirth = UtilDateUtilities.calcDate(demographic.getYearOfBirth(), demographic.getMonthOfBirth(), demographic.getDateOfBirth());
            
            this.setDemographicProperties();
            
            props.setProperty("demographic_no", demographic.getDemographicNo().toString());            
            props.setProperty("formCreated", ConversionUtils.toDateString(dateToday, dateFormat));
            props.setProperty("pg1_dateOfBirth", ConversionUtils.toDateString(dateOfBirth, dateFormat));
            props.setProperty("pg1_age", String.valueOf(UtilDateUtilities.calcAge(dateOfBirth)));
            props.setProperty("pg1_formDate", ConversionUtils.toDateString(dateToday, dateFormat));
            props.setProperty("pg2_formDate", ConversionUtils.toDateString(dateToday, dateFormat));
            props.setProperty("pg3_formDate", ConversionUtils.toDateString(dateToday, dateFormat));

            String rd = SxmlMisc.getXmlContent(demographic.getFamilyDoctor(), "rd");
            rd = rd != null ? rd : "";
            props.setProperty("pg1_famPhy", rd);
        }
        else
        {
        	
            String sql = "SELECT * FROM formBCAR2012 WHERE demographic_no = " + demographicNo + " AND ID = " + existingID;     
        	FrmRecordHelp frh = new FrmRecordHelp();
            frh.setDateFormat(dateFormat);
            
            try
            {
	            props = frh.getFormRecord(sql);
	            this.setDemographicProperties();
            }
            catch (SQLException e)
            {
            	MiscUtils.getLogger().error("Error", e);
            }

        }
        
        return props;
    }
   
    /**
     * @author dennis warren
     * @ OSCARprn
     */
    private void setDemographicProperties() {

        String surname = demographic.getLastName();
        String firstname = demographic.getFirstName();
        String address = demographic.getAddress();
        String city = demographic.getCity();
        String province = demographic.getProvince();
        String postal = demographic.getPostal();
        String phn = demographic.getHin();
        String phone = demographic.getPhone();
        String phoneAlt = demographic.getPhone2();
    	
        if (surname != null)
        {
            props.setProperty("c_surname", surname);
        }
        if (firstname != null)
        {
            props.setProperty("c_givenName", firstname);
        }
        if (address != null)
        {
            props.setProperty("c_address", address);
        }
        if (city != null)
        {
            props.setProperty("c_city", city);
        }
        if (province != null)
        {
            props.setProperty("c_province", province);
        }
        if (postal != null)
        {
            props.setProperty("c_postal", postal);
        }
        if (phn != null)
        {
            props.setProperty("c_phn", phn);
        }
        if (phone != null)
        {
            props.setProperty("c_phone", phone);
        }
        if (phoneAlt != null)
        {
            props.setProperty("c_phoneAlt1", demographic.getPhone2());
        }
        
        String cell = null;  
 
        if (demographicExtMap.containsKey("demo_cell"))
        {
        	cell = demographicExtMap.get("demo_cell");
        }
        		
        if (cell != null)
        {
            props.setProperty("c_phoneAlt2",cell );
        }
        
    }

    public int saveFormRecord(Properties props) throws SQLException {
        String demographic_no = props.getProperty("demographic_no");
        String sql = "SELECT * FROM formBCAR2012 WHERE demographic_no=" + demographic_no + " AND ID=0";

        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(dateFormat);
        return ((frh).saveFormRecord(props, sql));
    }

    public Properties getPrintRecord(int demographicNo, int existingID) throws SQLException {
        String sql = "SELECT * FROM formBCAR2012 WHERE demographic_no = " + demographicNo + " AND ID = " + existingID;
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(dateFormat);
        return ((frh).getPrintRecord(sql));
    }

    public String findActionValue(String submit) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(dateFormat);
        return ((frh).findActionValue(submit));
    }

    public String createActionURL(String where, String action, String demoId, String formId) throws SQLException {
        FrmRecordHelp frh = new FrmRecordHelp();
        frh.setDateFormat(dateFormat);
        return ((frh).createActionURL(where, action, demoId, formId));
    }

}
