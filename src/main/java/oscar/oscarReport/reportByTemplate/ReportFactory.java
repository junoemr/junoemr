/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */


package oscar.oscarReport.reportByTemplate;

/**
 *
 * @author rjonasz
 */
public class ReportFactory {
    
    public static final String SQL_TYPE = "sql";
    public static final String UnusedMinutes = "unusedminutes";
    public static final String ThirdAppt = "thirdAppt";
    public static final String INR = "inr";
    public static final String CONTINUITY = "continuity";
    
    /** Creates a new instance of ReportFactory */
    public ReportFactory() {
    }

    public static Reporter getReporter(String type)
    {
        if(type == null || type.equals("") || type.equalsIgnoreCase(SQL_TYPE))
        {
            return new SQLReporter();
        }
        else if(type.equalsIgnoreCase(UnusedMinutes))
        {
            return new UnusedMinutesReporter();
        }
        else if(type.equalsIgnoreCase(ThirdAppt))
        {
            return new ThirdApptTimeReporter();
        }
        else if(type.equalsIgnoreCase(INR))
        {
            return new INRReporter();
        }
        else if(type.equalsIgnoreCase(CONTINUITY))
        {
            return new DepressionContinuityReporter();
        }
        else
        {
            throw new IllegalArgumentException("Unknown reporting type: " + type);
        }
    }
}
