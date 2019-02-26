
package oscar;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class OscarDocumentCreator
{
	private static Logger logger = MiscUtils.getLogger();

	public static final String PDF = "pdf";
	public static final String CSV = "csv";
	public static final String EXCEL = "excel";

	public OscarDocumentCreator()
	{

	}

	public InputStream getDocumentStream(String path)
	{
		InputStream reportInstream = null;
		reportInstream = getClass().getClassLoader().getResourceAsStream(path);
		return reportInstream;
	}

	/**
	 * fillDocumentStream fills the specified output stream with the results of a jasperreport.
	 * works like {@link OscarDocumentCreator#fillDocumentStream(HashMap, OutputStream, String, InputStream, Object, String, LocalJasperReportsContext)}
	 */
	@SuppressWarnings("rawtypes")
	public void fillDocumentStream(HashMap parameters, OutputStream sos,
	                               String docType, InputStream xmlDesign,
	                               Object dataSrc)
	{
		fillDocumentStream(parameters, sos, docType, xmlDesign, dataSrc,
				null, new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance()));
	}

	/**
	 * fillDocumentStream fills the specified output stream with the results of a jasperreport.
	 * works like {@link OscarDocumentCreator#fillDocumentStream(HashMap, OutputStream, String, InputStream, Object, String, LocalJasperReportsContext)}
	 */
	@SuppressWarnings("rawtypes")
	public void fillDocumentStream(HashMap parameters, OutputStream sos,
								   String docType, InputStream xmlDesign,
								   Object dataSrc, String exportPdfJavascript)
	{
		fillDocumentStream(parameters, sos, docType, xmlDesign, dataSrc,
				exportPdfJavascript, new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance()));
	}


	/**
	 * fillDocumentStream fills the specified output stream with the results of a jasperreport.
	 * @param parameters report parameters. often times these will appear in a report header. like, DOB and Name.
	 * @param sos		 the stream to which the report will be written
	 * @param docType	 output type. one of PDF, CSV or EXCEL
	 * @param xmlDesign	 an input stream on the jrxml file to parse to create the report
	 * @param dataSrc	 the data source to use when creating the file. This often comprises the row data.
	 * @param exportPdfJavascript javascript code to embed in the PDF report
	 * @param reportContext the local context under which to run the report. This allows you to set dynamic report properties
	 *                      see: http://jasperreports.sourceforge.net/config.reference.html for a list.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void fillDocumentStream(HashMap parameters, OutputStream sos,
	                               String docType, InputStream xmlDesign,
	                               Object dataSrc, String exportPdfJavascript, LocalJasperReportsContext reportContext)
	{
		try
		{
			JasperReport jasperReport = null;
			JasperPrint print = null;
			jasperReport = getJasperReport(xmlDesign, reportContext);
			if(docType.equals(OscarDocumentCreator.PDF) && exportPdfJavascript != null)
			{
				jasperReport.setProperty("net.sf.jasperreports.export.pdf.javascript", exportPdfJavascript);
			}
			if(dataSrc == null)
			{
				print = JasperFillManager.getInstance(reportContext).fill(jasperReport, parameters, new JREmptyDataSource());
			}
			else if(dataSrc instanceof List)
			{
				JRDataSource ds = new JRBeanCollectionDataSource((List<?>) dataSrc);
				print = JasperFillManager.getInstance(reportContext).fill(jasperReport, parameters, ds);
			}
			else if(dataSrc instanceof java.sql.Connection)
			{
				print = JasperFillManager.getInstance(reportContext).fill(jasperReport, parameters, (Connection) dataSrc);
			}
			else if(dataSrc instanceof ResultSet)
			{
				JRDataSource ds = new JRResultSetDataSource((ResultSet) dataSrc);
				print = JasperFillManager.getInstance(reportContext).fill(jasperReport, parameters, ds);
			}
			else
			{
				JRDataSource ds = (JRDataSource) dataSrc;
				print = JasperFillManager.getInstance(reportContext).fill(jasperReport, parameters, ds);
			}
			print.setJasperReportsContext(reportContext);

			switch(docType)
			{
				case OscarDocumentCreator.PDF:
				{
					JasperExportManager.exportReportToPdfStream(print, sos);
					break;
				}
				case OscarDocumentCreator.CSV:
				{
					this.exportReportToCSVStream(print, sos);
					break;
				}
				case OscarDocumentCreator.EXCEL:
				{
					this.exportReportToExcelStream(print, sos);
					break;
				}
				default:
				{
					logger.error("Invalid document Type: " + docType);
					break;
				}
			}
		}
		catch(JRException ex)
		{
			MiscUtils.getLogger().error("Error", ex);
		}
	}

	/**
	 * Returns a JasperReport instance reprepesenting the supplied InputStream
	 *
	 * @param xmlDesign InputStream
	 * @return JasperReport
	 */
	public JasperReport getJasperReport(InputStream xmlDesign)
	{
		return getJasperReport(xmlDesign, null);
	}

	/**
	 * Returns a JasperReport instance reprepesenting the supplied InputStream
	 *
	 * @param xmlDesign InputStream
	 * @param jrCtx local jasper report context under which to create the report.
	 * @return JasperReport
	 */
	public JasperReport getJasperReport(InputStream xmlDesign, LocalJasperReportsContext jrCtx)
	{
		JasperReport jasperReport = null;
		try
		{
			if (jrCtx != null)
			{
				jasperReport = JasperCompileManager.getInstance(jrCtx).compile(xmlDesign);
				jasperReport.setJasperReportsContext(jrCtx);
			}
			else
			{
				jasperReport = JasperCompileManager.compileReport(xmlDesign);
			}
		}
		catch(JRException ex)
		{
			MiscUtils.getLogger().error("Error", ex);
		}
		return jasperReport;
	}

	public JasperReport getJasperReport(byte[] xmlDesign)
	{
		JasperReport jasperReport = null;
		try
		{
			jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(xmlDesign));
		}
		catch(JRException ex)
		{
			MiscUtils.getLogger().error("Error", ex);
		}
		return jasperReport;
	}

	/**
	 * Fills a servletoutout stream with data from a JasperReport
	 *
	 * @param jasperPrint JasperPrint
	 * @param sos         ServletOutputStream
	 * @throws JRException
	 */
	private void exportReportToCSVStream(JasperPrint jasperPrint, OutputStream sos) throws JRException
	{
		JRCsvExporter exp = new JRCsvExporter();
		exp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exp.setParameter(JRExporterParameter.OUTPUT_STREAM, sos);
		exp.exportReport();
	}

	private void exportReportToExcelStream(JasperPrint jasperPrint, OutputStream os) throws JRException
	{
		JRXlsExporter exp = new JRXlsExporter();
		exp.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exp.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
		exp.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		exp.setParameter(JRXlsExporterParameter.OFFSET_X, 0);
		exp.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
		exp.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, true);
		exp.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
		exp.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, false);
		exp.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, Integer.decode("65000"));
		exp.exportReport();
	}

}
