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


package org.oscarehr.casemgmt.service;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Site;
import org.oscarehr.common.printing.FontSettings;
import org.oscarehr.common.printing.PdfWriterFactory;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import oscar.OscarProperties;
import oscar.oscarClinic.ClinicData;
import oscar.util.ConversionUtils;

/**
 *
 * @author rjonasz
 */
public class CaseManagementPrintPdf {

    private HttpServletRequest request;
    private OutputStream os;

    private Document document;
    private BaseFont bf;
    private Font font;
    private boolean newPage = false;

    private SimpleDateFormat formatter;

    public final int LINESPACING = 1;
    public final float LEADING = 12;
    public final float FONTSIZE = 10;

    /** Creates a new instance of CaseManagementPrintPdf */
    public CaseManagementPrintPdf(HttpServletRequest request, OutputStream os) {
        this.request = request;
        this.os = os;
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
    }

    public HttpServletRequest getRequest() {
    	return request;
    }

    public OutputStream getOutputStream() {
    	return os;
    }

    public Font getFont() {
    	return font;
    }
    public SimpleDateFormat getFormatter() {
    	return formatter;
    }

    public Document getDocument() {
    	return document;
    }

    public boolean getNewPage() {
    	return newPage;
    }
    public void setNewPage(boolean b) {
    	this.newPage = b;
    }

    public BaseFont getBaseFont() {
    	return bf;
    }

    public void printDocHeaderFooter() throws IOException, DocumentException
    {
        //Create the document we are going to write to
        document = new Document();
        PdfWriter writer = PdfWriterFactory.newInstance(document, os, FontSettings.HELVETICA_12PT);
        
        // writer.setPageEvent(new EndPage());
        document.setPageSize(PageSize.LETTER);
        document.open();

        //Create the font we are going to print to
        bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        font = new Font(bf, FONTSIZE, Font.NORMAL);


        //set up document title and header
        ResourceBundle propResource = ResourceBundle.getBundle("oscarResources");
        String title = propResource.getString("oscarEncounter.pdfPrint.title") + " " + (String)request.getAttribute("demoName") + "\n";
        String gender = propResource.getString("oscarEncounter.pdfPrint.gender") + " " + (String)request.getAttribute("demoSex") + "\n";
        String dob = propResource.getString("oscarEncounter.pdfPrint.dob") + " " + (String)request.getAttribute("demoDOB") + "\n";
        String age = propResource.getString("oscarEncounter.pdfPrint.age") + " " + (String)request.getAttribute("demoAge") + "\n";
        String mrp = propResource.getString("oscarEncounter.pdfPrint.mrp") + " " + (String)request.getAttribute("mrp") + "\n";
        String hin = propResource.getString("oscarEncounter.pdfPrint.hin") + " " + (String)request.getAttribute("hin") + "\n";
        Integer selectedSite = (Integer) request.getAttribute("site");

        List<String> info = new ArrayList<>();
        info.add(title);
        info.add(gender);
        info.add(dob);
        info.add(age);
        info.add(hin);
        if("true".equals(OscarProperties.getInstance().getProperty("print.includeMRP", "true")))
        {
            info.add(mrp);
        }

        List<String> clinic = new ArrayList<>();
        if (selectedSite == null || selectedSite == 0)
        {
            ClinicData clinicData = new ClinicData();
            clinicData.refreshClinicData();
            clinic.add(clinicData.getClinicName());
            clinic.add(clinicData.getClinicAddress());
            clinic.add(clinicData.getClinicCity() + ", " + clinicData.getClinicProvince());
            clinic.add(clinicData.getClinicPostal());
            clinic.add("Phone: " + clinicData.getClinicPhone());
            clinic.add("Fax: " + clinicData.getClinicFax());
        }
        else
        {
            SiteDao siteDao = SpringUtils.getBean(SiteDao.class);
            Site site = siteDao.getById((Integer) request.getAttribute("site"));
            clinic.add(site.getName());
            clinic.add(site.getAddress());
            clinic.add(site.getCity() + ", " + site.getProvince());
            clinic.add(site.getPostal());
            clinic.add("Phone: " + site.getPhone());
            clinic.add("Fax: " + site.getFax());
        }

        if("true".equals(OscarProperties.getInstance().getProperty("print.useCurrentProgramInfoInHeader", "false")))
        {
        	ProgramManager2 programManager2 = SpringUtils.getBean(ProgramManager2.class);
        	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        	ProgramProvider programProvider = programManager2.getCurrentProgramInDomain(loggedInInfo,loggedInInfo.getLoggedInProviderNo());
    		if(programProvider != null)
    		{
    			Program program = programProvider.getProgram();
    			clinic.add(program.getDescription());
    			clinic.add(program.getAddress());
    			clinic.add(program.getPhone());
    		}
        }
        //Header will be printed at top of every page beginning with p2
        Phrase headerPhrase = new Phrase(LEADING, title, font);
        HeaderFooter header = new HeaderFooter(headerPhrase,false);
        header.setAlignment(HeaderFooter.ALIGN_CENTER);
        document.setHeader(header);

        //Write title with top and bottom borders on p1
        PdfContentByte cb = writer.getDirectContent();
        cb.setColorStroke(new Color(0,0,0));
        cb.setLineWidth(0.5f);

        cb.moveTo(document.left(), document.top());
        cb.lineTo(document.right(), document.top());
        cb.stroke();
        //cb.setFontAndSize(bf, FONTSIZE);

        float upperYcoord = document.top() - (font.getCalculatedLeading(LINESPACING) * 2f);

        ColumnText ct = new ColumnText(cb);
        Paragraph p = new Paragraph();
        p.setAlignment(Paragraph.ALIGN_LEFT);
        Phrase phrase = new Phrase();
        Phrase dummy = new Phrase();
        for(String idx: clinic)
        {
            phrase.add(idx + "\n");
            dummy.add("\n");
            upperYcoord -= phrase.getLeading();
        }

        dummy.add("\n");
        ct.setSimpleColumn(document.left(), upperYcoord, document.right()/2f, document.top());
        ct.addElement(phrase);
        ct.go();

        p.add(dummy);
        document.add(p);

        //add patient info
        phrase = new Phrase();
        p = new Paragraph();
        p.setAlignment(Paragraph.ALIGN_RIGHT);

        for(String idx: info)
        {
            phrase.add(idx);
        }

        ct.setSimpleColumn(document.right()/2f, upperYcoord, document.right(), document.top());
        p.add(phrase);
        ct.addElement(p);
        ct.go();

        cb.moveTo(document.left(), upperYcoord);
        cb.lineTo(document.right(), upperYcoord);
        cb.stroke();
        upperYcoord -= phrase.getLeading();

    }

    public void printRx(String demoNo) throws DocumentException {
        printRx(demoNo,null);
    }
    public void printRx(String demoNo,List<org.oscarehr.encounterNote.model.CaseManagementNote> cpp) throws DocumentException {
        if( demoNo == null )
            return;

        if( newPage )
            document.newPage();
        else
            newPage = true;

        Paragraph p = new Paragraph();
        Font obsfont = new Font(bf, FONTSIZE, Font.UNDERLINE);
        Phrase phrase = new Phrase(LEADING, "", obsfont);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        phrase.add("Patient Rx History");
        p.add(phrase);
        document.add(p);

        Font normal = new Font(bf, FONTSIZE, Font.NORMAL);

        oscar.oscarRx.data.RxPrescriptionData prescriptData = new oscar.oscarRx.data.RxPrescriptionData();
        oscar.oscarRx.data.RxPrescriptionData.Prescription [] arr = {};
        arr = prescriptData.getUniquePrescriptionsByPatient(Integer.parseInt(demoNo));


        Font curFont;
        for(int idx = 0; idx < arr.length; ++idx ) {
            oscar.oscarRx.data.RxPrescriptionData.Prescription drug = arr[idx];
            p = new Paragraph();
            p.setAlignment(Paragraph.ALIGN_LEFT);
            if(drug.isCurrent() && !drug.isArchived() ){
                curFont = normal;
                phrase = new Phrase(LEADING, "", curFont);
                phrase.add(formatter.format(drug.getRxDate()) + " - ");
                phrase.add(drug.getFullOutLine().replaceAll(";", " "));
                p.add(phrase);
                document.add(p);
            }
        }

        if (cpp != null ){
            List<org.oscarehr.encounterNote.model.CaseManagementNote>notes = cpp;
            if (notes != null && notes.size() > 0){
                p = new Paragraph();
                p.setAlignment(Paragraph.ALIGN_LEFT);
                phrase = new Phrase(LEADING, "\nOther Meds\n", obsfont); //TODO-legacy:Needs to be i18n
                p.add(phrase);
                document.add(p);
                newPage = false;
                this.printEncounterNotes(notes);
            }

        }
    }

    public void printCPP(HashMap<String,List<org.oscarehr.encounterNote.model.CaseManagementNote> >cpp)
            throws DocumentException
    {
        if (cpp == null)
        {
            return;
        }

        if (newPage)
        {
            document.newPage();
        }
        else
        {
            newPage = true;
        }

        Font obsfont = new Font(bf, FONTSIZE, Font.UNDERLINE);

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        Phrase phrase = new Phrase(LEADING, "\n\n", font);
        paragraph.add(phrase);
        phrase = new Phrase(LEADING, "Patient CPP", obsfont);
        paragraph.add(phrase);
        document.add(paragraph);

        Map<String, String> issueHeaders = new HashMap<>();
        issueHeaders.put(Issue.SUMMARY_CODE_SOCIAL_HISTORY, "Social History\n");
        issueHeaders.put(Issue.SUMMARY_CODE_OTHER_MEDS, "Other Meds\n");
        issueHeaders.put(Issue.SUMMARY_CODE_MEDICAL_HISTORY, "Medical History\n");
        issueHeaders.put(Issue.SUMMARY_CODE_CONCERNS, "Ongoing Concerns\n");
        issueHeaders.put(Issue.SUMMARY_CODE_REMINDERS, "Reminders\n");
        issueHeaders.put(Issue.SUMMARY_CODE_FAMILY_HISTORY, "Family History\n");
        issueHeaders.put(Issue.SUMMARY_CODE_RISK_FACTORS, "Risk Factors\n");

        for (String issue : cpp.keySet())
        {
            paragraph = new Paragraph();
            paragraph.setAlignment(Paragraph.ALIGN_LEFT);
            phrase = new Phrase(LEADING, issueHeaders.get(issue), obsfont);
            paragraph.add(phrase);
            document.add(paragraph);
            newPage = false;
            this.printEncounterNotes(cpp.get(issue));
        }

    }

    /**
     * Very similar to the printNotes function, except using the newer CaseManagementNote model.
     * @param notes list of notes to print
     * @throws DocumentException if the document in question can't be written to
     */
    public void printEncounterNotes(List<org.oscarehr.encounterNote.model.CaseManagementNote> notes)
        throws DocumentException
    {
        Font obsfont = new Font(bf, FONTSIZE, Font.UNDERLINE);
        Paragraph paragraph;
        Phrase phrase;
        Chunk chunk;

        if(newPage)
        {
            document.newPage();
        }
        else
        {
            newPage = true;
        }

        for (org.oscarehr.encounterNote.model.CaseManagementNote note : notes)
        {
            paragraph = new Paragraph();
            phrase = new Phrase(LEADING, "", font);
            String observationDate = ConversionUtils.toDateString(note.getObservationDate());
            chunk = new Chunk("Documentation Date: " + observationDate + "\n", obsfont);
            phrase.add(chunk);
            phrase.add(note.getNote() + "\n\n");
            paragraph.add(phrase);
            document.add(paragraph);
        }
    }

    public void finish() {
        document.close();
    }

}
