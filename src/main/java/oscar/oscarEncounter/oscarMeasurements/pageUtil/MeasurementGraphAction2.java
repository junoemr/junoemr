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

package oscar.oscarEncounter.oscarMeasurements.pageUtil;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYDataset;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarDemographic.data.DemographicNameAgeString;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarLab.ca.on.CommonLabTestValues;
import oscar.oscarRx.data.RxPrescriptionData;

/**
 *
 * @author jaygallagher
 */
public class MeasurementGraphAction2 extends Action {

	private static Logger log = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	// This is somewhat ugly. In words:
	// - match the character(s) defining how the range is meant to be interpreted
	// - match any positive or negative number with optional decimals (may not exist)
	// - match the number following the range delimiter (may or may not exist)
	private static Pattern rangePattern = Pattern.compile("[+-]?(\\d*.?\\d*)?[\\s]?(<=|>=|<|>|-)[\\s]?[+-]?(\\d*.?\\d*)?");

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {

		String userrole = (String) request.getSession().getAttribute("userrole");
		if (userrole == null)
		{
			response.sendRedirect("../logout.jsp");
		}

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		securityInfoManager.requireOnePrivilege(providerNo, SecurityInfoManager.READ, null, "_measurement");

		Integer demographicNo = Integer.valueOf(request.getParameter("demographic_no"));
		String typeIdName = request.getParameter("type");
		String typeIdName2 = request.getParameter("type2");

		String patientName = DemographicNameAgeString.getInstance().getNameAgeString(loggedInInfo, demographicNo);
		String chartTitle = "Data Graph for " + patientName;
		int width = 800;
		int height = 400;

		String method = StringUtils.trimToEmpty(request.getParameter("method"));

		JFreeChart chart;

		if (method.equals("actualLab"))
		{
			String labType    = request.getParameter("labType");
			String identifier = request.getParameter("identifier");
			String testName   = request.getParameter("testName");
			String[] drugs = request.getParameterValues("drug");
			chart = actualLabChartRef(demographicNo, labType, identifier, testName, chartTitle, drugs);
		}
		else if(method.equals("ChartMeds"))
		{
			String[] drugs = request.getParameterValues("drug");
			chart = ChartMeds(demographicNo, chartTitle, drugs);
			if (drugs != null && drugs.length > 10)
			{
				height = (drugs.length * 30);
			}
		}
		else
		{
			chart = defaultChart(demographicNo, typeIdName, typeIdName2, chartTitle);
		}

		response.setContentType("image/png");
		OutputStream outputStream = response.getOutputStream();
		ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		outputStream.close();
		return null;
	}

	private List<EctMeasurementsDataBean> getList(Integer demographicNo, String typeIdName) {
		EctMeasurementsDataBeanHandler ectMeasure = new EctMeasurementsDataBeanHandler(demographicNo, typeIdName);
		Collection<EctMeasurementsDataBean> dataVector = ectMeasure.getMeasurementsData();
		return new ArrayList<>(dataVector);
	}

	private static XYTaskDataset getDrugDataSet(Integer demographicId, String[] dins) {
		TaskSeriesCollection datasetDrug = new TaskSeriesCollection();
		RxPrescriptionData prescriptData = new RxPrescriptionData();

		for (String din : dins)
		{
		   RxPrescriptionData.Prescription [] prescriptions =  prescriptData.getPrescriptionScriptsByPatientRegionalIdentifier(demographicId, din);
			if (prescriptions.length > 0)
			{
				TaskSeries ts = new TaskSeries(prescriptions[0].getBrandName());
				for (RxPrescriptionData.Prescription pres : prescriptions)
				{
					ts.add(new Task(pres.getBrandName(), pres.getRxDate(), pres.getEndDate()));
				}
				datasetDrug.add(ts);
			}
		}

		XYTaskDataset dataset = new XYTaskDataset(datasetDrug);
		dataset.setTransposed(true);
		dataset.setSeriesWidth(0.6);

		return dataset;
	}

	private static String[] getDrugSymbol(Integer demographic,String[] dins){
		List<String> list = new ArrayList<>();
		RxPrescriptionData prescriptData = new RxPrescriptionData();

		for (String din : dins)
		{
			 RxPrescriptionData.Prescription [] prescriptions =  prescriptData.getPrescriptionScriptsByPatientRegionalIdentifier(demographic,din);
			 if (prescriptions.length > 0)
			 {
				list.add(prescriptions[0].getBrandName());
			 }

		}

		int listSize = list.size();
		return list.toArray(new String[listSize]);
	}

	/**
	 * Helper procedure to modify ranges that we know we can reinterpret.
	 * @param range String to modify
	 * @return modified string if possible, original or empty string if string can't be modified
	 */
	public static String reformatRange(String range)
	{
		if (range == null || range.isEmpty() || StringUtils.trimToEmpty(range).equals("-"))
		{
			return "";
		}

		Matcher match;
		final double upperBoundMagnitude = 2.0;

		try
		{
			match = rangePattern.matcher(range);
		}
		catch (Exception ex)
		{
			MiscUtils.getLogger().error("Error when matching range " + range, ex);
			return range;
		}

		if (match.matches())
		{
			try
			{
				String lowerBound = StringUtils.trimToEmpty(match.group(1));
				String delimiter = StringUtils.trimToEmpty(match.group(2));
				String upperBound = StringUtils.trimToEmpty(match.group(3));
				if (lowerBound.isEmpty())
				{
					switch(delimiter)
					{
						case "-":
						case "<":
						case "<=":
							return "0 - " + upperBound;
						case ">":
						case ">=":
							return Double.parseDouble(upperBound) + " - " + Double.parseDouble(upperBound)*upperBoundMagnitude;
					}
				}
				else if (upperBound.isEmpty())
				{
					switch (delimiter)
					{
						case ">":
						case ">=":
						case "<":
							return "0 - " + lowerBound;
						case "-":
						case "<=":
							return Double.parseDouble(lowerBound) + " - " + Double.parseDouble(lowerBound)*upperBoundMagnitude;
					}
				}
				else if (delimiter.equals("-"))
				{
					// kind of a base case -- if neither upper nor lower bound is empty and it has proper delimiter, we done
					return range;
				}
			}
			catch (Exception e)
			{
				MiscUtils.getLogger().warn("Unhandled error: " + e);
			}
		}
		// no match, no idea how we got here
		MiscUtils.getLogger().warn("Following string did not match graphing range pattern: '" + range + "'");
		return range;
	}

	/**
	 * Pull and parse measurement parameters out of a string-based range
	 * @param range The string that we're trying to interpret
	 * @param units Any written suffix associated with the measurement that we want to discard
	 * @return double array containing the measurements we can pull out or null if no measurements can be found
	 */
	public static double[] getParameters(String range, String units)
	{
		double[] measurementParams = {};
		if (range == null)
		{
			return measurementParams;
		}

		if (units != null)
		{
			range = range.replace(units, "");
		}

		// Some range interpretations can be adjusted for graphing purposes
		range = reformatRange(range);

		if (range.contains("-"))
		{
			try
			{
				String[] split = range.split("-");
				double open = Double.parseDouble(split[0]);
				double high = Double.parseDouble(split[1]);
				double low = Double.parseDouble(split[0]);
				double close = Double.parseDouble(split[1]);
				double volume = 1045;
				measurementParams = new double[]{open, high, low, close, volume};
			}
			catch (NumberFormatException | ArrayIndexOutOfBoundsException ex)
			{
				MiscUtils.getLogger().warn("Exception occurred when trying to split range: " + ex);
				return null;
			}
		}

		return measurementParams;
	}

	private JFreeChart actualLabChartRef(Integer demographicNo, String labType, String identifier,String testName, String chartTitle, String[] drugs) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		List<Map<String,Serializable>> list;
		if (labType.equals("loinc"))
		{
			list = CommonLabTestValues.findValuesByLoinc2(demographicNo.toString(), identifier, null);
		}
		else
		{
			list = CommonLabTestValues.findValuesForTest(labType, demographicNo, testName, identifier);
		}

		List<OHLCDataItem> dataItems = new ArrayList<>();

		String typeLegendName = "Lab Value";
		String typeYAxisName = "type Y";

		boolean nameSet = false;
		TimeSeries newSeries = new TimeSeries(typeLegendName);
		for (Map mdb : list)
		{
			if (!nameSet)
			{
				typeYAxisName = (String)mdb.get("units");
				typeLegendName = (String) mdb.get("testName");
				if (typeLegendName == null)
				{
					typeLegendName = testName;
				}
				newSeries.setKey(typeLegendName);
				nameSet = true;
			}
			String mdbResult = (String)mdb.get("result");
			mdbResult = mdbResult.replaceAll("<|>|=", "");
			newSeries.addOrUpdate(new Day((Date) mdb.get("collDateDate")), Double.parseDouble(mdbResult));

			if (mdb.get("range") != null)
			{
				String range = (String) mdb.get("range");
				String units = (String) mdb.get("units");



				double[] measurementRange = getParameters(range, units);
				if (measurementRange.length > 0)
				{
					dataItems.add(new OHLCDataItem(
							new Day((Date) mdb.get("collDateDate")).getStart(),
							measurementRange[0],
							measurementRange[1],
							measurementRange[2],
							measurementRange[3],
							measurementRange[4]));
				}
			}

		}
		dataset.addSeries(newSeries);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Days", typeYAxisName, dataset, true, true, true);

		XYPlot plot = chart.getXYPlot();
		plot.getDomainAxis().setAutoRange(true);

		plot.getDomainAxis().setUpperMargin(plot.getDomainAxis().getUpperMargin()*6);
		plot.getDomainAxis().setLowerMargin(plot.getDomainAxis().getLowerMargin()*6);
		plot.getRangeAxis().setUpperMargin(plot.getRangeAxis().getUpperMargin()*1.7);

		plot.getDomainAxis().setUpperMargin(0.9);
		plot.getDomainAxis().setLowerMargin(0.9);
		plot.getRangeAxis().setUpperMargin(plot.getRangeAxis().getUpperMargin() * 4);

		ValueAxis va = plot.getRangeAxis();
		va.setAutoRange(true);
		XYItemRenderer renderer = plot.getRenderer();
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator("{1} \n {2}", new SimpleDateFormat("yyyy.MM.dd"), new DecimalFormat("0.00"));
		renderer.setSeriesItemLabelGenerator(0, generator);

		renderer.setBaseItemLabelsVisible(true);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainCrosshairPaint(Color.GRAY);

		if (renderer instanceof XYLineAndShapeRenderer)
		{
			XYLineAndShapeRenderer rend = (XYLineAndShapeRenderer) renderer;
			rend.setBaseShapesVisible(true);
			rend.setBaseShapesFilled(true);
		}

		plot.setRenderer(renderer);

		int dataSize = dataItems.size();

		if (dataSize > 0)
		{
			OHLCDataItem[] ohlc = dataItems.toArray(new OHLCDataItem[dataSize]);
			XYDataset referenceRangeDataset = new DefaultOHLCDataset("Normal Reference Range", ohlc);
			plot.setDataset(1, referenceRangeDataset);
			plot.mapDatasetToRangeAxis(1, 0);
			plot.setRenderer(1,new HighLowRenderer());
		}

		if (drugs != null)
		{
			chart = setupMedGraphChart(demographicNo, drugs, plot, chartTitle, true);
		}
		return chart;
	}

	/**
	 * Sub-procedure to generate a graph indicating which medications were prescribed when and for how long.
	 *
	 * @param demographicNo
	 * 		patient's demographic number
	 * @param drugs
	 * 		a list of every drug prescribed to the patient we want to add to the chart
	 * @param plot
	 * 		pre-existing plot that may be used alongside the medical graph we're setting up
	 * @param chartTitle
	 * 		name to apply to the overall set of graphs
	 * @param multiPlot
	 * 		boolean indicating whether we want to graph just the medication timeline or other data with it
	 * @return
	 * 		a chart containing a medication history plot along with any other plots we wanted to graph beside it
	 */
	private JFreeChart setupMedGraphChart(Integer demographicNo, String[] drugs, XYPlot plot, String chartTitle, boolean multiPlot)
	{
		XYTaskDataset drugDataset = getDrugDataSet(demographicNo, drugs);

		SymbolAxis yAxis = new SymbolAxis("Meds", getDrugSymbol(demographicNo, drugs));
		yAxis.setGridBandsVisible(false);
		XYBarRenderer xyRenderer = new XYBarRenderer();
		xyRenderer.setUseYInterval(true);
		xyRenderer.setBarPainter(new StandardXYBarPainter());

		XYPlot xyplot = new XYPlot(drugDataset, plot.getDomainAxis(), yAxis, xyRenderer);

		final double percentMargin = 0.9;
		xyplot.getDomainAxis().setUpperMargin(percentMargin);
		xyplot.getDomainAxis().setLowerMargin(percentMargin);

		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(new DateAxis("Date/Time"));
		if (multiPlot)
		{
			combinedPlot.add(plot);
		}
		combinedPlot.add(xyplot);

		JFreeChart chart = new JFreeChart(chartTitle, combinedPlot);
		chart.setBackgroundPaint(Color.white);

		return chart;
	}

	/**
	 * Wrapper for creating and filling a TimeSeries object with measurement data.
	 * @param typeLegendName name of the types in the legend
	 * @param list list of measurement data to add to the time series
	 * @return a TimeSeries object containing all parseable data fields
	 */
	private TimeSeries fillTimeSeries(String typeLegendName, List<EctMeasurementsDataBean> list)
	{
		TimeSeries timeSeries = new TimeSeries(typeLegendName);
		for (EctMeasurementsDataBean mdb : list)
		{
			if (org.apache.commons.lang3.math.NumberUtils.isCreatable(mdb.getDataField()))
			{
				timeSeries.addOrUpdate(new Day(mdb.getDateObservedAsDate()), Double.parseDouble(mdb.getDataField()));
			}
			else
			{
				double[] measurementRange = getParameters(mdb.getDataField(), "");
				if (measurementRange == null || measurementRange.length < 1)
				{
					log.warn("Error passing measurement value to chart. DataField can't be read for ID: " + mdb.getId());
					continue;
				}
				timeSeries.addOrUpdate(new Day(mdb.getDateObservedAsDate()), measurementRange[0]);
			}
		}
		return timeSeries;
	}

	private JFreeChart defaultChart(Integer demographicNo, String typeIdName, String typeIdName2, String chartTitle) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		List<EctMeasurementsDataBean> list = getList(demographicNo, typeIdName);
		String typeYAxisName;

		if (typeIdName.equals("BP"))
		{
			log.debug("Using BP LOGIC FOR type 1 ");
			EctMeasurementsDataBean sampleLine = list.get(0);
			typeYAxisName = sampleLine.getTypeDescription();
			TimeSeries systolic = new TimeSeries("Systolic");
			TimeSeries diastolic = new TimeSeries("Diastolic");
			for (EctMeasurementsDataBean mdb : list)
			{
				if (!mdb.getDataField().equals(""))
				{
					String[] str = mdb.getDataField().split("/");
					systolic.addOrUpdate(new Day(mdb.getDateObservedAsDate()), Double.parseDouble(str[0]));
					diastolic.addOrUpdate(new Day(mdb.getDateObservedAsDate()), Double.parseDouble(str[1]));
				}
				else
				{
					log.debug("Error passing measurement value to chart. DataField is empty for ID:" + mdb.getId());
				}
			}
			dataset.addSeries(diastolic);
			dataset.addSeries(systolic);

		}
		else
		{
			log.debug("Not Using BP LOGIC FOR type 1 ");
			// get the name from the TimeSeries
			EctMeasurementsDataBean sampleLine = list.get(0);
			String typeLegendName = sampleLine.getTypeDisplayName();
			typeYAxisName = sampleLine.getTypeDescription(); // this should be the type of measurement

			TimeSeries newSeries = fillTimeSeries(typeLegendName, list);
			dataset.addSeries(newSeries);
		}

		JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Days", typeYAxisName, dataset, true, true, true);

		if (typeIdName2 != null)
		{
			log.debug("type id name 2" + typeIdName2);

			List<EctMeasurementsDataBean> list2 = getList(demographicNo, typeIdName2);
			TimeSeriesCollection dataset2 = new TimeSeriesCollection();

			log.debug("list2 " + list2);

			EctMeasurementsDataBean sampleLine2 = list2.get(0);
			String typeLegendName = sampleLine2.getTypeDisplayName();
			String typeYAxisName2 = sampleLine2.getTypeDescription(); // this should be the type of measurement

			TimeSeries newSeries = fillTimeSeries(typeLegendName, list2);
            dataset2.addSeries(newSeries);

            final XYPlot plot = chart.getXYPlot();
            final NumberAxis axis2 = new NumberAxis(typeYAxisName2);
            axis2.setAutoRangeIncludesZero(false);
            plot.setRangeAxis(1, axis2);
            plot.setDataset(1, dataset2);
            plot.mapDatasetToRangeAxis(1, 1);
            final XYItemRenderer renderer = plot.getRenderer();
            renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
            if (renderer instanceof StandardXYItemRenderer) {
                final StandardXYItemRenderer rr = (StandardXYItemRenderer) renderer;

                rr.setBaseShapesFilled(true);
            }

            final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
            renderer2.setSeriesPaint(0, Color.black);
            renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
            plot.setRenderer(1, renderer2);

		}
		return chart;
	}

	/*
	 * Just Drugs
	 */
	private JFreeChart ChartMeds(Integer demographicNo, String chartTitle, String[] drugs) {
		MiscUtils.getLogger().debug("In ChartMeds");
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Days", "MEDS", dataset, true, true, true);

		XYPlot plot = chart.getXYPlot();

		chart = setupMedGraphChart(demographicNo, drugs, plot, chartTitle, false);

		return chart;
	}
}
