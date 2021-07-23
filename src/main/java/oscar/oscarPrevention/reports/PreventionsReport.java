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

            if (reportDisplay.state.equals(PreventionReport.UPTODATE) && preventionDate != null)
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime(asOfDate);
                cal.add(Calendar.YEAR, -2);
                Date dueDate = cal.getTime();
                cal.add(Calendar.MONTH, -6);
                Date cutoffDate = cal.getTime();

                if ((dueDate.after(preventionDate) && cutoffDate.before(preventionDate)) || cutoffDate.after(preventionDate))
                {
                    inclUpToDate = true;
                }
            }
            if (reportDisplay.state.equals(PreventionReport.NOINFO) || reportDisplay.state.equals(PreventionReport.DUE) || reportDisplay.state.equals(PreventionReport.OVERDUE) || inclUpToDate)
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

                    Calendar onemonth = Calendar.getInstance();
                    onemonth.setTime(asOfDate);
                    onemonth.add(Calendar.MONTH, -1);

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
            else if (reportDisplay.state.equals(PreventionReport.UPTODATE))
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
                // log.debug("NOT SURE WHAT HAPPEND IN THE LETTER PROCESSING");
            }
            return reportDisplay.nextSuggestedProcedure;
        }
        return null;
    }
}
