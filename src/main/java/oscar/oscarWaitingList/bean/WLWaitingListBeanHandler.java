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


package oscar.oscarWaitingList.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import oscar.oscarDB.DBHandler;
import oscar.oscarWaitingList.util.WLWaitingListUtil;

public class WLWaitingListBeanHandler implements Serializable {
    
    List waitingListArrayList = new ArrayList();
    String waitingListName ="";
    private static Logger log = Logger.getLogger(WLWaitingListBeanHandler.class);
    
    public WLWaitingListBeanHandler(String waitingListID) {
        init(waitingListID);
    }

    public boolean init(String waitingListID) {
        
        boolean verdict = true;
        try {
            
            
            String sql = " SELECT CONCAT(d.last_name, ', ', d.first_name) AS patientName, d.demographic_no, " + 
            			 " d.phone, w.listID, w.position, w.note, w.onListSince FROM waitingList w, demographic d " + 
            			 " WHERE w.demographic_no = d.demographic_no AND w.listID='"+ waitingListID + "' " + 
            			 " AND w.is_history = 'N' " + " ORDER BY w.position ";
            log.debug(sql);
            ResultSet rs;      
            String onListSinceDateOnly = "";
            for(rs = DBHandler.GetSQL(sql); rs.next(); )
            {                
            	onListSinceDateOnly = oscar.Misc.getString(rs, "onListSince").substring(0, 10);//2007-01-01
            	
                WLPatientWaitingListBean wLBean = new WLPatientWaitingListBean( oscar.Misc.getString(rs, "demographic_no"),
                                                                                oscar.Misc.getString(rs, "listID"),
                                                                                oscar.Misc.getString(rs, "position"),
                                                                                oscar.Misc.getString(rs, "patientName"), 
                                                                                oscar.Misc.getString(rs, "phone"),
                                                                                oscar.Misc.getString(rs, "note"),
                                                                                onListSinceDateOnly);   
                waitingListArrayList.add(wLBean);
            }                            
            
            sql = "SELECT * FROM waitingListName where ID="+waitingListID + " AND is_history = 'N' ";
            log.debug(sql);
            rs = DBHandler.GetSQL(sql);
            if(rs.next()){
                waitingListName = oscar.Misc.getString(rs, "name");
            }
            rs.close();
        }
        catch(SQLException e) {
            MiscUtils.getLogger().error("Error", e);
            verdict = false;
        }
        return verdict;
    }
    
    static public void updateWaitingList(String waitingListID) {
                
        try {
            
            String sql = " SELECT demographic_no FROM waitingList WHERE listID=" + waitingListID + 
                         " AND is_history = 'N' ";
            log.debug(sql);
            ResultSet rs;
            boolean needUpdate = false;
            //go thru all the patient on the list
            for(rs = DBHandler.GetSQL(sql); rs.next();){
                
                //check if the patient has an appointment already
                sql = "select a.demographic_no, a.appointment_date, wl.onListSince from appointment a, waitingList wl where a.appointment_date >= wl.onListSince AND a.demographic_no=wl.demographic_no AND a.demographic_no="
                      + oscar.Misc.getString(rs, "demographic_no") + "";
                log.debug(sql);
                ResultSet rsCheck = DBHandler.GetSQL(sql);        
                
                if(rsCheck.next())
                {                
                    //delete patient from the waitingList

                	WLWaitingListUtil.removeFromWaitingList(waitingListID, oscar.Misc.getString(rs, "demographic_no"));
                    needUpdate = true;
                }
                rsCheck.close();
            }
            rs.close();
            //update the list
            if(needUpdate){
                sql = " SELECT * FROM waitingList WHERE listID=" + waitingListID + "  AND is_history = 'N' ORDER BY onListSince";
                log.debug(sql);
                int i=1;            
                for(rs = DBHandler.GetSQL(sql); rs.next();){                    
                    sql =   " UPDATE waitingList SET position="+ i + 
                    		" WHERE listID=" + waitingListID + 
                            " AND demographic_no=" + oscar.Misc.getString(rs, "demographic_no") +
                            " AND is_history = 'N' ";

                    log.debug(sql);
                    DBHandler.RunSQL(sql);
                    i++;
                }                            
                rs.close();
            }
        }
        catch(SQLException e) {
            MiscUtils.getLogger().error("Error", e);         
        }        
    } 
        
    
    
    public List getWaitingListArrayList(){
        return waitingListArrayList;
    }    
    
    public String getWaitingListName(){
        return waitingListName;
    }
}
