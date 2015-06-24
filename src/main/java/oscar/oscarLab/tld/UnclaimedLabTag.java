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


package oscar.oscarLab.tld;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.oscarehr.util.MiscUtils;

import oscar.oscarDB.DBHandler;

/**
 *
 * @author Jay Gallagher
 */
public class UnclaimedLabTag extends TagSupport {
   
  
   public UnclaimedLabTag() {
	numNewLabs = 0;
   }
   
   public int doStartTag() throws JspException    {
        try {

            String sql = new String("SELECT COUNT(*) from providerLabRouting plr " +
					"LEFT JOIN hl7TextInfo info ON (info.lab_no = plr.lab_no) AND (plr.lab_type = 'HL7') " +
					"LEFT JOIN document doc ON (doc.document_no = plr.lab_no) AND (plr.lab_type = 'DOC') " +
					"AND (doc.status <> 'D') " + 
					"WHERE  plr.status = 'N' AND plr.provider_no = '0';");          

            ResultSet rs = DBHandler.GetSQL(sql);
            while (rs.next()) {
               numNewLabs = (rs.getInt(1));

            }
            rs.close();
        }catch(SQLException e){
          MiscUtils.getLogger().error("Error", e);
        }
        
        if(numNewLabs > 0){

           return(EVAL_BODY_INCLUDE);
        }else{

           return(SKIP_BODY);                        
        }
    }
         
    public int doEndTag() throws JspException {
       return (EVAL_PAGE);
    }
   
    private int numNewLabs;
}
