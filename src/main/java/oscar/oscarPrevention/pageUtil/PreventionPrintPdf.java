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
 * PreventionPrintPdf.java
 *
 * Created on March 12, 2007, 4:05 PM
 */

package oscar.oscarPrevention.pageUtil;

import java.awt.Color;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.model.Clinic;
import org.oscarehr.common.printing.FontSettings;
import org.oscarehr.common.printing.PdfWriterFactory;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dao.PreventionExtDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import oscar.util.ConversionUtils;

import static org.oscarehr.prevention.model.Prevention.REFUSED_STATUS_COMPLETED;
import static org.oscarehr.prevention.model.Prevention.REFUSED_STATUS_REFUSED;
import static org.oscarehr.prevention.model.Prevention.REFUSED_STATUS_INELIGIBLE;

/*
 * @author rjonasz
 */
public class PreventionPrintPdf
{
    private final float LEADING = 12;

    private final Map<Character, String> READABLE_STATUSES = new HashMap<>();
    private final Font SECTION_HEADER_FONT;
    private final Font BODY_FONT;

    /** Creates a new instance of PreventionPrintPdf */
    public PreventionPrintPdf()
    {
        READABLE_STATUSES.put(REFUSED_STATUS_COMPLETED,"Completed or Normal");
        READABLE_STATUSES.put(REFUSED_STATUS_REFUSED,"Refused");
        READABLE_STATUSES.put(REFUSED_STATUS_INELIGIBLE,"Ineligible");

        SECTION_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, Color.BLACK);
        BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    }

    /**
     * Generate PDF containing prevention information for a given demographic.
     * Should produce a PDF looking similar to what Ontario's "Yellow Card" looks like.
     * @param preventionSections section names that we want to print
     * @param demographicId demographic identifier to read prevention data for
     * @param outputStream output stream we are filling with a PDF
     */
    public void generatePDF(List<String> preventionSections, Integer demographicId, OutputStream outputStream) throws DocumentException
    {
        DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographic.dao.DemographicDao");
        Demographic demographic = demographicDao.find(demographicId);

        //Create the document we are going to write to
        Document document = new Document();
        PdfWriter writer = PdfWriterFactory.newInstance(document, outputStream, FontSettings.HELVETICA_10PT);
        document.setPageSize(PageSize.LETTER);

        //Header will be printed at top of every page beginning with p2
        Phrase titlePhrase = createTitle(demographic);
        HeaderFooter header = new HeaderFooter(titlePhrase,false);
        header.setAlignment(HeaderFooter.ALIGN_RIGHT);
        header.setBorder(Rectangle.BOTTOM);
        document.setHeader(header);
        document.open();
        PdfContentByte contentBytes = writer.getDirectContent();

        //Clinic Address Information
        Paragraph clinicParagraph = getClinicInfo();
        document.add(clinicParagraph);

        //get top y-coord for starting to print columns
        int LINE_SPACING = 1;
        float upperYcoord = document.top() - header.getHeight() - (clinicParagraph.getLeading() * 4f) - BODY_FONT.getCalculatedLeading(LINE_SPACING);

        ColumnText columnText = new ColumnText(contentBytes);
        columnText.setSimpleColumn(document.left(), document.bottom(), document.right()/2f, upperYcoord);

        boolean onColumnLeft = true;
        PreventionDao preventionDao = SpringUtils.getBean(PreventionDao.class);

        // get everything then filter by the ones we want to keep
        for (String preventionType : preventionSections)
        {
            List<Prevention> preventions = preventionDao.findByTypeAndDemoNo(preventionType, demographic.getDemographicId());
            String preventionHeader = "Prevention " + preventionType + "\n";
            Phrase procHeader = new Phrase(LEADING, preventionHeader, SECTION_HEADER_FONT);
            columnText.addText(procHeader);
            columnText.setAlignment(Element.ALIGN_LEFT);
            columnText.setIndent(0);
            columnText.setFollowingIndent(0);
            float titleYPos = columnText.getYLine();

            for (Prevention prevention : preventions)
            {
                //check whether the Prevention Title can fit on the page
                int status = columnText.go(true);
                boolean writeTitleOk = !ColumnText.hasMoreText(status);

                Phrase procedure = buildPreventionBody(demographic, prevention);

                //check if the Date/Age/Comments title can fit on the page.
                columnText.addText(procedure);
                columnText.setAlignment(Element.ALIGN_LEFT);
                columnText.setIndent(10);
                columnText.setFollowingIndent(0);
                float detailYPos = columnText.getYLine();
                status = columnText.go(true);

                boolean writeDetailOk = !ColumnText.hasMoreText(status);

                Phrase commentsPhrase = buildPreventionComments(prevention);

                //Check if the comments can fit on the page
                columnText.addText(commentsPhrase);
                columnText.setAlignment(Element.ALIGN_JUSTIFIED);
                columnText.setIndent(25);
                columnText.setFollowingIndent(25);
                float commentYPos = columnText.getYLine();
                status = columnText.go(true);

                boolean writeCommentsOk = !ColumnText.hasMoreText(status);

                boolean proceedWrite = true;
                if (writeDetailOk && writeCommentsOk)
                {

                    //write on the same column and page
                    if (writeTitleOk)
                    {
                        //we still need to write the title
                        columnText.addText(procHeader);
                        columnText.setAlignment(Element.ALIGN_LEFT);
                        columnText.setYLine(titleYPos);
                        columnText.setIndent(0);
                        columnText.setFollowingIndent(0);
                        columnText.go();
                    }
                    else
                    {
                        proceedWrite = false;
                    }

                    if (proceedWrite)
                    {
                        //Date and Age
                        columnText.addText(procedure);
                        columnText.setAlignment(Element.ALIGN_LEFT);
                        columnText.setYLine(detailYPos);
                        columnText.setIndent(10);
                        columnText.setFollowingIndent(0);
                        columnText.go();

                        //Comments
                        columnText.addText(commentsPhrase);
                        columnText.setAlignment(Element.ALIGN_JUSTIFIED);
                        columnText.setYLine(commentYPos);
                        columnText.setIndent(25);
                        columnText.setFollowingIndent(25);
                        columnText.go();
                    }
                }
                else
                {
                    proceedWrite = false;
                }
                //We can't fit the prevention we are printing into the current column on the current page we are printing to
                if (!proceedWrite)
                {
                    if (onColumnLeft)
                    {
                        //Print to the right column (i.e. we are printing to the current page)
                        onColumnLeft = false;
                        columnText.setSimpleColumn(document.right()/2f, document.bottom(), document.right(), upperYcoord);
                    }
                    else
                    {
                        //Print to the left column (i.e. we are starting a new page)
                        onColumnLeft = true;
                        upperYcoord = document.top() - header.getHeight() - BODY_FONT.getCalculatedLeading(LINE_SPACING);
                        document.newPage();

                        columnText.setSimpleColumn(document.left(), document.bottom(), document.right()/2f, upperYcoord);
                    }

                    columnText.setText(procHeader);
                    columnText.setAlignment(Element.ALIGN_LEFT);
                    columnText.setIndent(0);
                    columnText.setFollowingIndent(0);
                    columnText.go();

                    //Date and Age
                    columnText.setText(procedure);
                    columnText.setAlignment(Element.ALIGN_LEFT);
                    columnText.setIndent(10);
                    columnText.setFollowingIndent(0);
                    columnText.go();

                    //Comments
                    columnText.setText(commentsPhrase);
                    columnText.setAlignment(Element.ALIGN_JUSTIFIED);
                    columnText.setIndent(25);
                    columnText.setFollowingIndent(25);
                    columnText.go();
                }
            }

        }
        document.close();
    }

    // *** HELPER FUNCTIONS ***

    /**
     * Given a demographic, create and populate the header of a PDF printout with its information
     * @param demographic demographic to create header for
     * @return a Phrase to be re-used for building the PDF
     */
    private Phrase createTitle(Demographic demographic)
    {
        //Header will be printed at top of every page beginning with p2
        Phrase titlePhrase = new Phrase(16, "Preventions", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD, Color.BLACK));
        titlePhrase.add(Chunk.NEWLINE);
        titlePhrase.add(new Chunk(demographic.getFormattedName(), FontFactory.getFont(FontFactory.HELVETICA, 14, Font.NORMAL, Color.BLACK)));
        titlePhrase.add(Chunk.NEWLINE);
        String demographicInformation = getDescriptionForSex(demographic.getSex()) +
                " Age: " + ChronoUnit.YEARS.between(demographic.getDateOfBirth(), LocalDateTime.now()) +
                " (" + ConversionUtils.toDateString(demographic.getDateOfBirth()) + ") " +
                "HIN: (" + demographic.getHcType() + ") " + demographic.getHin() + " " + demographic.getVer();
        titlePhrase.add(new Chunk(demographicInformation, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, Color.BLACK)));

        ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);

        if (demographic.getProviderNo() != null)
        {
            // Note: if we had this class managed by Spring we could remove this hit on the provider dao
            ProviderData providerData = providerDataDao.find(demographic.getProviderNo());
            titlePhrase.add(Chunk.NEWLINE);
            titlePhrase.add(new Chunk("MRP: " + providerData.getDisplayName(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, Color.BLACK)));
        }

        return titlePhrase;
    }

    /**
     * Get and populate a Paragraph object with information about the clinic.
     * @return paragraph object containing structured clinic info for re-use
     */
    private Paragraph getClinicInfo()
    {
        ClinicDAO clinicDAO = SpringUtils.getBean(ClinicDAO.class);
        Clinic clinic = clinicDAO.getClinic();

        Paragraph clinicParagraph = new Paragraph(LEADING, clinic.getClinicName(), SECTION_HEADER_FONT);
        clinicParagraph.add(Chunk.NEWLINE);
        clinicParagraph.add(new Chunk(clinic.getClinicAddress(), BODY_FONT));
        clinicParagraph.add(Chunk.NEWLINE);
        clinicParagraph.add(new Chunk(clinic.getClinicCity() + ", " + clinic.getClinicProvince() + " " + clinic.getClinicPostal(), BODY_FONT));
        clinicParagraph.add(Chunk.NEWLINE);
        clinicParagraph.add(new Chunk("Ph.", BODY_FONT));
        clinicParagraph.add(new Chunk(clinic.getClinicPhone(), BODY_FONT));
        clinicParagraph.add(new Chunk(" Fax.", BODY_FONT));
        clinicParagraph.add(new Chunk(clinic.getClinicFax(), BODY_FONT));
        clinicParagraph.setAlignment(Paragraph.ALIGN_CENTER);

        return clinicParagraph;
    }

    /**
     * Given a demographic and a prevention, build a printable version of that prevention.
     * @param demographic demographic to read, largely for comparing dates
     * @param prevention the prevention to build a printable section for
     * @return printable Phrase
     */
    private Phrase buildPreventionBody(Demographic demographic, Prevention prevention)
    {
        ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);

        String procedureAge = Long.toString(
                ChronoUnit.YEARS.between(
                        demographic.getDateOfBirth(),
                        ConversionUtils.toLocalDateTime(prevention.getPreventionDate())));
        String procedureDate = ConversionUtils.toDateString(prevention.getPreventionDate());

        String procedureStatus = READABLE_STATUSES.get(REFUSED_STATUS_COMPLETED);
        if (prevention.isRefused())
        {
            procedureStatus = READABLE_STATUSES.get(REFUSED_STATUS_REFUSED);
        }
        else if (prevention.isIneligible())
        {
            procedureStatus = READABLE_STATUSES.get(REFUSED_STATUS_INELIGIBLE);
        }

        String procedureAdministeringProvider = providerDataDao.find(prevention.getProviderNo()).getDisplayName();

        Phrase procedure = new Phrase(LEADING, "Age: ", BODY_FONT);
        procedure.add(new Chunk(procedureAge, BODY_FONT));
        procedure.add(Chunk.NEWLINE);

        //Date
        procedure.add("Date: ");
        procedure.add(new Chunk(procedureDate, BODY_FONT));
        procedure.add(Chunk.NEWLINE);

        //Status
        procedure.add("Status: ");
        procedure.add(new Chunk(procedureStatus, BODY_FONT));
        procedure.add(Chunk.NEWLINE);

        procedure.add("Administering Provider: ");
        procedure.add(new Chunk(procedureAdministeringProvider, BODY_FONT));
        procedure.add(Chunk.NEWLINE);

        return procedure;
    }

    /**
     * Build a section specifically for comments left by a provider about a given prevention.
     *
     * Only populates with information if 'prevention_show_comments' is overridden on the properties file with true.
     * @param prevention prevention to build comments section for
     * @return printable phrase containing prevention comments
     */
    private Phrase buildPreventionComments(Prevention prevention)
    {
        Phrase commentsPhrase = new Phrase(LEADING, "", BODY_FONT);

        // only add comments in if property is set to true (or not set at all)
        if (OscarProperties.getInstance().getBooleanProperty("prevention_show_comments", "true"))
        {
            String procedureComments = "";

            // Note: if this were managed by Spring we wouldn't have to get this manually
            PreventionExtDao preventionExtDao = SpringUtils.getBean(PreventionExtDao.class);
            List<PreventionExt> preventionExtList = preventionExtDao.findByPreventionId(prevention.getId());
            for (PreventionExt preventionExt : preventionExtList)
            {
                if (preventionExt.getkeyval().equals("comments"))
                {
                    procedureComments = preventionExt.getVal();
                }
            }

            if (procedureComments != null && !procedureComments.isEmpty())
            {
                commentsPhrase.add(procedureComments);
                commentsPhrase.add(Chunk.NEWLINE);
            }
        }

        commentsPhrase.add(Chunk.NEWLINE);
        return commentsPhrase;
    }

    /**
     * Given the sex for a demographic, return a more print-friendly description.
     *
     * When the Gender enum updates are merged into spring-boot we can replace this with that.
     * @param sex presumably a single-character value indicating what sex we're asking about
     * @return a description if we can match it
     */
    private String getDescriptionForSex(String sex)
    {
        switch(sex)
        {
            case "M":
                return "Male";
            case "F":
                return "Female";
            case "T":
                return "Transgender";
            default:
                return "Unknown";
        }
    }
}
