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


package oscar.form.pdfservlet;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import com.lowagie.text.DocumentException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import oscar.OscarProperties;
import oscar.oscarRx.templates.RxPdfTemplate;
import oscar.oscarRx.templates.RxPdfTemplateCustom1;
import oscar.oscarRx.templates.RxPdfTemplatePrescriptionPad;



public class FrmCustomedPDFServlet extends HttpServlet {

	public static final String HSFO_RX_DATA_KEY = "hsfo.rx.data";
	private static Logger logger = MiscUtils.getLogger();
	private OscarProperties props = OscarProperties.getInstance();

	@Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws javax.servlet.ServletException, java.io.IOException {
		
		logger.info("CREATE CUSTOM RX PDF SERVICE");

		ByteArrayOutputStream baosPDF = null;

		try {
			String method = req.getParameter("__method");
			boolean isFax = method.equals("oscarRxFax");
			baosPDF = generatePDFDocumentBytes(req, this.getServletContext());
			if (isFax) {
				res.setContentType("text/html");
				PrintWriter writer = res.getWriter();
				String faxNo = req.getParameter("pharmaFax").trim().replaceAll("\\D", "");
			    if (faxNo.length() < 7) {
					writer.println("<script>alert('Error: No fax number found!');window.close();</script>");
				}
				else {
					// write to file
					String pdfFile = OscarProperties.getInstance().getProperty("fax_file_location") + "/prescription_"
							+ req.getParameter("pdfId") + ".pdf";
					FileOutputStream fos = new FileOutputStream(pdfFile);
					baosPDF.writeTo(fos);
					fos.close();

					String txtFile = OscarProperties.getInstance().getProperty("fax_file_location") + "/prescription_"
							+ req.getParameter("pdfId") + ".txt";
					FileWriter fstream = new FileWriter(txtFile);
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(faxNo);
					out.close();

					writer.println("<script>alert('Fax sent to: " + req.getParameter("pharmaName") + " ("
							+ req.getParameter("pharmaFax") + ")');window.close();</script>");
				}
			}
			else {
				StringBuilder sbFilename = new StringBuilder();
				sbFilename.append("filename_");
				sbFilename.append(".pdf");

				// set the Cache-Control header
				res.setHeader("Cache-Control", "max-age=0");
				res.setDateHeader("Expires", 0);

				res.setContentType("application/pdf");

				// The Content-disposition value will be inline
				StringBuilder sbContentDispValue = new StringBuilder();
				sbContentDispValue.append("inline; filename="); // inline - display
				// the pdf file
				// directly rather
				// than open/save
				// selection
				// sbContentDispValue.append("; filename=");
				sbContentDispValue.append(sbFilename);

				res.setHeader("Content-disposition", sbContentDispValue.toString());

				res.setContentLength(baosPDF.size());

				ServletOutputStream sos;

				sos = res.getOutputStream();

				baosPDF.writeTo(sos);

				sos.flush();
			}
		}
		catch (DocumentException dex) {
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();
			writer.println("Exception from: " + this.getClass().getName() + " " + dex.getClass().getName() + "<br>");
			writer.println("<pre>");
			writer.println(dex.getMessage());
			writer.println("</pre>");
		}
		catch (java.io.FileNotFoundException dex) {
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();
			writer.println("<script>alert('Signature not found. Please sign the prescription.');</script>");
		}
		finally {
			if (baosPDF != null) {
				baosPDF.reset();
			}
		}
	}

	// added by vic, hsfo
	private ByteArrayOutputStream generateHsfoRxPDF(HttpServletRequest req) {

		HsfoRxDataHolder rx = (HsfoRxDataHolder) req.getSession().getAttribute(HSFO_RX_DATA_KEY);

		JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(rx.getOutlines());
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/oscar/form/prop/Hsfo_Rx.jasper");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JasperRunManager.runReportToPdfStream(is, baos, rx.getParams(), ds);
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
		return baos;
	}
	
	protected ByteArrayOutputStream generatePDFDocumentBytes(final HttpServletRequest req, final ServletContext ctx) throws DocumentException {

		if (HSFO_RX_DATA_KEY.equals(req.getParameter("__title"))) {
			return generateHsfoRxPDF(req);
		}
		
		RxPdfTemplate template;
		if ("custom1".equals(props.getProperty("rx_custom_template"))) {
			template = new RxPdfTemplateCustom1(req, ctx);
		}
		else {
			template = new RxPdfTemplatePrescriptionPad(req, ctx);
		}
		return template.getOutputStream();
	}
}
