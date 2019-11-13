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


package oscar.oscarDemographic.pageUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 *
 * @author Jay Gallagher
 */
public class ImportDemographicDataForm extends ActionForm {
   FormFile importFile = null;
   boolean matchProviderNames = true;
   int timeshiftInDays;
   String courseId;
   // Multisite setting to determine which site to associate items with by default
   private String defaultSite;

   public ImportDemographicDataForm() {
   
   }



   public FormFile getImportFile(){
      return importFile;
   }
   
   public void setImportFile(FormFile file){
      this.importFile = file;
   }

   public boolean getMatchProviderNames() {
       return matchProviderNames;
   }

   public void setMatchProviderNames(boolean matchProviderNames) {
       this.matchProviderNames = matchProviderNames;
   }
   
   public int getTimeshiftInDays() {
		return timeshiftInDays;
	}



	public void setTimeshiftInDays(int timeshiftInDays) {
		this.timeshiftInDays = timeshiftInDays;
	}



	public String getCourseId() {
		return courseId;
	}



	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getDefaultSite()
	{
		return defaultSite;
	}

	public void setDefaultSite(String defaultSite)
	{
		this.defaultSite = defaultSite;
	}

}
