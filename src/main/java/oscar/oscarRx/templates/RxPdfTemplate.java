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

package oscar.oscarRx.templates;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class RxPdfTemplate {

	protected static Logger logger = MiscUtils.getLogger();

	protected final HttpServletRequest req;
	protected final ServletContext ctx;

	public RxPdfTemplate(final HttpServletRequest req, final ServletContext ctx) {
		this.ctx = ctx;
		this.req = req;
	}
	public ByteArrayOutputStream getOutputStream() throws DocumentException {

		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
		PdfWriter writer = null;
		Document document = null;
		try {
			document = new Document();

			Rectangle pageSize = getPageSize(req.getParameter("rxPageSize"));
			document.setPageSize(pageSize);

			writer = PdfWriter.getInstance(document, baosPDF);
			document = documentSetup(document, writer);

			document.open();
			document.newPage();

			buildPdfLayout(document, writer);
		}
		catch (DocumentException dex) {
			baosPDF.reset();
			throw dex;
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if (document != null && document.isOpen()) {
				document.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		return baosPDF;
	}

	/**
	 * page size is determined here.
	 */
	protected abstract Rectangle getPageSize(String pageSizeParameter);
	/**
	 * Document is initialized here, and given custom metadata.
	 */
	protected abstract Document documentSetup(Document document, PdfWriter writer);
	/**
	 * custom pdf layout and data is set up here.
	 */
	protected abstract void buildPdfLayout(Document document, PdfWriter writer) throws DocumentException, IOException;

	//TODO-legacy this is stupid, make it not stupid
	protected HashMap<String, String> parseSCAddress(String s)
	{
		logger.debug("Parse Address HTML:\n" + s);
		HashMap<String, String> addressMap = new HashMap<>();
		String[] addressFields = s.split("<br>");
		ArrayList<String> addressFieldList = new ArrayList<>(Arrays.asList(addressFields));
		String clinicName = String.join("\n", addressFieldList.get(0), addressFieldList.get(1),addressFieldList.get(2));
		String tel = addressFieldList.get(3);
		tel = tel.replace("Tel: ", "");
		tel = tel.replace("Tel", "");
		String fax = addressFieldList.get(4);
		fax = fax.replace("Fax: ", "");
		logger.debug(tel);
		logger.debug(fax);
		logger.debug(clinicName);
		addressMap.put("clinicName", clinicName);
		addressMap.put("clinicTel", tel);
		addressMap.put("clinicFax", fax);

		return addressMap;
	}
}
