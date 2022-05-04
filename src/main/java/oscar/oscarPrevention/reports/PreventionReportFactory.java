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


package oscar.oscarPrevention.reports;

/**
 *
 * @author jay
 */
public class PreventionReportFactory {
    
    /** Creates a new instance of PreventionReportFactory */
    public PreventionReportFactory() {
    }
    
    static public PreventionReport getPreventionReport(String preventionName){
        PreventionReport report = null;
        if ( preventionName == null){
          //Not sure what to do          
        }else if (preventionName.equals("PAP")){  
           report = new PapReport();
        }else if (preventionName.equals("Mammogram")){          
           report = new MammogramReport();
        }else if (preventionName.equals("Flu")){  
           report = new FluReport();
        }else if (preventionName.equals("ChildImmunizations")){
            report = new ChildImmunizationReport();     
        }else if (preventionName.equals("FOBT")){
            report = new FOBTReport();
        } else if (preventionName.equals("FIT")){
		report = new FITReport();
	}
        
       return report;
    }
    
}
