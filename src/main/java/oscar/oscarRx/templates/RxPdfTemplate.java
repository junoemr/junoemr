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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

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
			document = getDocument();
			Rectangle pageSize = getPageSize(req.getParameter("rxPageSize"));
			document.setPageSize(pageSize);
			// 285=left margin+width of box, 5f is space for looking nice
			document.setMargins(15, pageSize.getWidth() - 285f + 5f, 170, 60);
			
			writer = PdfWriter.getInstance(document, baosPDF);
			buildPdfLayout(document, writer);
		}
		catch (DocumentException dex) {
			baosPDF.reset();
			throw dex;
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if (document != null) {
				document.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		return baosPDF;
	}
	
	protected abstract Rectangle getPageSize(String pageSizeParameter);
	protected abstract Document getDocument();		
	protected abstract void buildPdfLayout(Document document, PdfWriter writer) throws DocumentException, IOException;
	
	protected HashMap<String,String> parseSCAddress(String s) {
		HashMap<String,String> hm = new HashMap<String,String>();
		String[] ar = s.split("</b>");
		String[] ar2 = ar[1].split("<br>");
		ArrayList<String> lst = new ArrayList<String>(Arrays.asList(ar2));
		lst.remove(0);
		String tel = lst.get(3);
		tel = tel.replace("Tel: ", "");
		String fax = lst.get(4);
		fax = fax.replace("Fax: ", "");
		String clinicName = lst.get(0) + "\n" + lst.get(1) + "\n" + lst.get(2);
		logger.debug(tel);
		logger.debug(fax);
		logger.debug(clinicName);
		hm.put("clinicName", clinicName);
		hm.put("clinicTel", tel);
		hm.put("clinicFax", fax);

		return hm;
	}
}
