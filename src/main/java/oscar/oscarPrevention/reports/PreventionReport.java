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

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.util.LoggedInInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 *
 * @author jay
 */


public interface PreventionReport {

    //Prevention followup
    String FIRST_LETTER = "L1";
    String SECOND_LETTER = "L2";
    String PHONE_CALL = "P1";
    String NO_FOLLOWUP = "------";
    String CALL_FOLLOWUP = "Follow Up";

    //Prevention state
    String NO_INFO = "No Info";
    String DUE = "due";
    String OVERDUE = "Overdue";
    String UP_TO_DATE = "Up to date";
    String REFUSED = "Refused";
    String INELIGIBLE = "Ineligible";
    String PENDING = "Pending";

    enum PreventionReportType
    {
        PAP,
        MAMMOGRAM,
        FLU,
        CHILD_IMMUNIZATIONS,
        FOBT;

        public static PreventionReportType fromStringIgnoreCase(String enumString)
        {
            if(EnumUtils.isValidEnumIgnoreCase(PreventionReportType.class, enumString))
            {
                return PreventionReportType.valueOf(enumString.toUpperCase());
            }
            return null;
        }
    }

    boolean displayNumShots();

    Hashtable<String, Object> runReport(LoggedInInfo loggedInInfo, ArrayList<ArrayList<String>> list, Date asofDate);
}

