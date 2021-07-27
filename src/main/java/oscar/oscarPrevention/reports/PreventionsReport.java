/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package oscar.oscarPrevention.reports;

import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarPrevention.pageUtil.PreventionReportDisplay;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public abstract class PreventionsReport implements PreventionReport
{
    public String letterProcessing(PreventionReportDisplay reportDisplay, String measurementType, Date asOfDate, @Nullable Date preventionDate)
    {
        EctMeasurementsDataBeanHandler measurementDataHandler;
        Collection<EctMeasurementsDataBean> followupData;

        if (reportDisplay != null)
        {
            measurementDataHandler = new EctMeasurementsDataBeanHandler(reportDisplay.demographicNo, measurementType);
            followupData = measurementDataHandler.getMeasurementsData();
            boolean inclUpToDate = false;

            if (reportDisplay.state.equals(PreventionReport.UP_TO_DATE) && preventionDate != null)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(asOfDate);
                if (measurementType == "PAPF")
                {
                    cal.add(Calendar.YEAR, -3);
                }
                else
                {
                    cal.add(Calendar.YEAR, -2);
                }

                Date dueDate = cal.getTime();
                cal.add(Calendar.MONTH, -6);
                Date cutoffDate = cal.getTime();

                if ((dueDate.after(preventionDate) && cutoffDate.before(preventionDate)) || cutoffDate.after(preventionDate))
                {
                    inclUpToDate = true;
                }
            }

            if (reportDisplay.state.equals(PreventionReport.NO_INFO) ||
                    reportDisplay.state.equals(PreventionReport.DUE) ||
                    reportDisplay.state.equals(PreventionReport.OVERDUE) ||
                    inclUpToDate)
            {
                if (followupData.size() == 0)
                {
                    reportDisplay.nextSuggestedProcedure = PreventionReport.FIRST_LETTER;
                    return PreventionReport.FIRST_LETTER;
                }
                else
                {
                    Calendar oneyear = Calendar.getInstance();
                    oneyear.setTime(asOfDate);
                    oneyear.add(Calendar.YEAR, -1);

                    Date observationDate = null;
                    int index = 0;
                    EctMeasurementsDataBean measurementData = null;

                    @SuppressWarnings("unchecked")
                    Iterator<EctMeasurementsDataBean> iterator = followupData.iterator();

                    while (iterator.hasNext())
                    {
                        measurementData = iterator.next();
                        observationDate = measurementData.getDateObservedAsDate();

                        if (index == 0)
                        {
                            reportDisplay.lastFollowup = observationDate;
                            reportDisplay.lastFollupProcedure = measurementData.getDataField();

                            if (measurementData.getDateObservedAsDate().before(oneyear.getTime()))
                            {
                                reportDisplay.nextSuggestedProcedure = PreventionReport.FIRST_LETTER;
                                return PreventionReport.FIRST_LETTER;
                            }

                            if (reportDisplay.lastFollupProcedure.equals(PreventionReport.PHONE_CALL))
                            {
                                reportDisplay.nextSuggestedProcedure = PreventionReport.NO_FOLLOWUP;
                                return PreventionReport.NO_FOLLOWUP;
                            }
                        }
                        ++index;
                    }

                    switch (reportDisplay.lastFollupProcedure)
                    {
                        case PreventionReport.NO_FOLLOWUP:
                            reportDisplay.nextSuggestedProcedure = PreventionReport.FIRST_LETTER;
                            break;
                        case PreventionReport.FIRST_LETTER:
                            reportDisplay.nextSuggestedProcedure = PreventionReport.SECOND_LETTER;
                            break;
                        case PreventionReport.SECOND_LETTER:
                            reportDisplay.nextSuggestedProcedure = PreventionReport.PHONE_CALL;
                            break;
                        default:
                            reportDisplay.nextSuggestedProcedure = PreventionReport.NO_FOLLOWUP;
                    }

                    return reportDisplay.nextSuggestedProcedure;
                }
            }
            else if (reportDisplay.state.equals(PreventionReport.REFUSED))
            {  //Not sure what to do about refused
                measurementDataHandler = new EctMeasurementsDataBeanHandler(reportDisplay.demographicNo, measurementType);
                followupData = measurementDataHandler.getMeasurementsData();

                if (followupData.size() > 0)
                {
                    EctMeasurementsDataBean measurementData = followupData.iterator().next();
                    reportDisplay.lastFollowup = measurementData.getDateObservedAsDate();
                    reportDisplay.lastFollupProcedure = measurementData.getDataField();
                }
                reportDisplay.nextSuggestedProcedure = PreventionReport.NO_FOLLOWUP;
            }
            else if (reportDisplay.state.equals(PreventionReport.INELIGIBLE))
            {
                // Do nothing
                reportDisplay.nextSuggestedProcedure = PreventionReport.NO_FOLLOWUP;
            }
            else if (reportDisplay.state.equals(PreventionReport.PENDING))
            {
                reportDisplay.nextSuggestedProcedure = PreventionReport.CALL_FOLLOWUP;
            }
            else if (reportDisplay.state.equals(PreventionReport.UP_TO_DATE))
            {
                //Do nothing
                measurementDataHandler = new EctMeasurementsDataBeanHandler(reportDisplay.demographicNo, measurementType);
                followupData = measurementDataHandler.getMeasurementsData();

                if (followupData.size() > 0)
                {
                    EctMeasurementsDataBean measurementData = followupData.iterator().next();
                    reportDisplay.lastFollowup = measurementData.getDateObservedAsDate();
                    reportDisplay.lastFollupProcedure = measurementData.getDataField();
                }
                reportDisplay.nextSuggestedProcedure = PreventionReport.NO_FOLLOWUP;
            }
            else
            {
                // log.debug("NOT SURE WHAT HAPPENED IN THE LETTER PROCESSING");
            }
            return reportDisplay.nextSuggestedProcedure;
        }
        return null;
    }

    //FLu is different then the others IT only has one letter and a phone call
     protected abstract String letterProcessing(PreventionReportDisplay prd, Date cuttoffDate);
}
