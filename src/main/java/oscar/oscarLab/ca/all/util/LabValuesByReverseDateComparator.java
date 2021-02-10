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
 * CumulativeLabValuesComparator.java
 *
 * Created on August 3, 2007, 3:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarLab.ca.all.util;

import java.util.Comparator;
import java.util.Date;

import java.util.Map;

import oscar.util.ConversionUtils;

/**
 *  A custom comparator used to compare the HashMaps within an array by the
 *  date value.
 *
 *  Used by /oscar/lab/CumulativeLabValues3.jsp
 *
 *
 * @author wrighd
 */
public class LabValuesByReverseDateComparator implements Comparator<Map<String, String>>
{
    public int compare(Map<String, String> o1, Map<String, String> o2)
    {
        Date dateA = ConversionUtils.fromDateString(o1.get("date") , ConversionUtils.DEFAULT_TS_PATTERN);
        Date dateB = ConversionUtils.fromDateString(o2.get("date") , ConversionUtils.DEFAULT_TS_PATTERN);

        return dateA.compareTo(dateB) * -1;
    }
    
}
