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


package org.oscarehr.phr.web;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Property;
import org.oscarehr.document.dao.DocumentDAO;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.CtlDocumentPK;
import org.oscarehr.myoscar_server.ws.AccountWs;
import org.oscarehr.myoscar_server.ws.InvalidRelationshipException_Exception;
import org.oscarehr.myoscar_server.ws.InvalidRequestException_Exception;
import org.oscarehr.myoscar_server.ws.NoSuchItemException_Exception;
import org.oscarehr.myoscar_server.ws.NotAuthorisedException_Exception;
import org.oscarehr.myoscar_server.ws.PersonTransfer;
import org.oscarehr.myoscar_server.ws.Relation;
import org.oscarehr.phr.PHRAuthentication;
import org.oscarehr.phr.RegistrationHelper;
import org.oscarehr.phr.dao.PHRActionDAO;
import org.oscarehr.phr.dao.PHRDocumentDAO;
import org.oscarehr.phr.indivo.service.accesspolicies.IndivoAPService;
import org.oscarehr.phr.model.PHRAction;
import org.oscarehr.phr.service.PHRService;
import org.oscarehr.phr.util.MyOscarServerRelationManager;
import org.oscarehr.phr.util.MyOscarServerWebServicesManager;
import org.oscarehr.phr.util.MyOscarUtils;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WebUtils;

import oscar.OscarProperties;
import oscar.oscarDemographic.data.DemographicData;
import oscar.util.UtilDateUtilities;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;


public class PHRUserManagementAction extends DispatchAction {

    private static Logger log = MiscUtils.getLogger();

    PHRDocumentDAO phrDocumentDAO;
    PHRActionDAO phrActionDAO;
    PHRService phrService;

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
       return super.execute(mapping, form, request, response);
    }

    public PHRUserManagementAction() {
    }

    public void setPhrDocumentDAO(PHRDocumentDAO phrDocumentDAO) {
        this.phrDocumentDAO = phrDocumentDAO;
    }

    public void setPhrActionDAO(PHRActionDAO phrActionDAO) {
        this.phrActionDAO = phrActionDAO;
    }

    public void setPhrService(PHRService phrService) {
      this.phrService = phrService;
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm  form,
           HttpServletRequest request, HttpServletResponse response)
           throws Exception {
           return null;
    }

    protected Properties getCfgProp() {
        Properties ret = new Properties();

        int intialNameOffset = 150;

        PropertyDao propertyDao = (PropertyDao) SpringUtils.getBean("propertyDao");

        try{
	        List<Property> propertyList = propertyDao.findByName("MYOSCAR_REGISTRATION_LETTER_NAME_OFFSET");
            if (propertyList.size() > 0){
    		    log.debug("property size list "+propertyList.size());
		        Property property =  propertyList.get(propertyList.size()-1);
        		log.debug("property value "+property.getValue());
		        intialNameOffset = Integer.parseInt(property.getValue());
			}
	    }catch(Exception e){
        	log.error("OFFSET ERROR",e);
        }

        ret.setProperty("name","left, 70, "+intialNameOffset+", 0, BaseFont.HELVETICA, 11");
        intialNameOffset += 11;
		ret.setProperty("address","left, 70, "+intialNameOffset+", 0, BaseFont.HELVETICA, 11");
		intialNameOffset += 11;
		ret.setProperty("city","left, 70, "+intialNameOffset+", 0, BaseFont.HELVETICA, 11");
		intialNameOffset += 11;
		ret.setProperty("postalCode","left, 70, "+intialNameOffset+", 0, BaseFont.HELVETICA, 11");

		ret.setProperty("letterDate","left, 70, 101, 0, BaseFont.HELVETICA, 11");
		ret.setProperty("intro","left, 70, 340, 0, BaseFont.HELVETICA, 11, _, 550, 250, 13");
		ret.setProperty("credHeading","left, 80, 350, 0, BaseFont.HELVETICA, 11");
		ret.setProperty("username","left, 80, 378, 0, BaseFont.HELVETICA, 11");
		ret.setProperty("password","left, 80, 395, 0, BaseFont.HELVETICA, 11");
		ret.setProperty("URL","left, 80, 412, 0, BaseFont.HELVETICA, 11");

        return ret;
    }

    /*
     Date	( generate date today )
     name address city province postal code
     username passed in username
     password passed in password

     */
    public ByteArrayOutputStream generateUserRegistrationLetter(String demographicNo,String username, String password) throws Exception{
    	log.debug("Demographic "+demographicNo+" username "+username+" password "+password);
    	DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
    	Demographic demographic = demographicDao.getDemographic(demographicNo);

    	final String PAGESIZE = "printPageSize";
        Document document = new Document();

        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        PdfWriter writer = null;

        try {
            writer = PdfWriter.getInstance(document, baosPDF);

            String title = "TITLE";
            String template = "MyOscarLetterHead.pdf";

            Properties printCfg =  getCfgProp();

            String[] cfgVal = null;
            StringBuilder tempName = null;

            // get the print prop values

            Properties props = new Properties();
            props.setProperty("letterDate", UtilDateUtilities.getToday("yyyy-MM-dd"));
            props.setProperty("name",demographic.getFirstName()+" "+demographic.getLastName());
            props.setProperty("dearname",demographic.getFirstName()+" "+demographic.getLastName());
            props.setProperty("address", demographic.getAddress());
            props.setProperty("city",demographic.getCity()+", "+demographic.getProvince());
            props.setProperty("postalCode",demographic.getPostal());
            props.setProperty("credHeading", "MyOscar User Account Details");
            props.setProperty("username","Username: "+username);
            props.setProperty("password", "Password: "+password);
            //Temporary - the intro will change to be dynamic
            props.setProperty("intro","We are pleased to provide you with a log in and password for your new MyOSCAR Personal Health Record. This account will allow you to connect electronically with our clinic. Please take a few minutes to review the accompanying literature for further information.We look forward to you benefiting from this service.");

            document.addTitle(title);
            document.addSubject("");
            document.addKeywords("pdf, itext");
            document.addCreator("OSCAR");
            document.addAuthor("");
            document.addHeader("Expires", "0");

            Rectangle pageSize = PageSize.LETTER;

            document.setPageSize(pageSize);
            document.open();

            // create a reader for a certain document
            String propFilename = oscar.OscarProperties.getInstance().getProperty("pdfFORMDIR", "") + "/" + template;
            PdfReader reader = null;
            try {
                reader = new PdfReader(propFilename);
                log.debug("Found template at " + propFilename);
            } catch (Exception dex) {
                log.debug("change path to inside oscar from :" + propFilename);
                reader = new PdfReader("/oscar/form/prop/" + template);
                log.debug("Found template at /oscar/form/prop/" + template);
            }

            // retrieve the total number of pages
            int n = reader.getNumberOfPages();
            // retrieve the size of the first page
            Rectangle pSize = reader.getPageSize(1);
            float width = pSize.getWidth();
            float height = pSize.getHeight();
            log.debug("Width :"+width+" Height: "+height);

            PdfContentByte cb = writer.getDirectContent();
            ColumnText ct = new ColumnText(cb);
            int fontFlags = 0;

            document.newPage();
            PdfImportedPage page1 = writer.getImportedPage(reader, 1);
            cb.addTemplate(page1, 1, 0, 0, 1, 0, 0);

            BaseFont bf; // = normFont;
            String encoding;

            cb.setRGBColorStroke(0, 0, 255);

            String[] fontType;
            for (Enumeration e = printCfg.propertyNames(); e.hasMoreElements();) {
                tempName = new StringBuilder(e.nextElement().toString());
                cfgVal = printCfg.getProperty(tempName.toString()).split(" *, *");

                if( cfgVal[4].indexOf(";") > -1 ) {
                    fontType = cfgVal[4].split(";");
                    if( fontType[1].trim().equals("italic") )
                        fontFlags = Font.ITALIC;
                    else if( fontType[1].trim().equals("bold") )
                        fontFlags = Font.BOLD;
                    else if( fontType[1].trim().equals("bolditalic") )
                        fontFlags = Font.BOLDITALIC;
                    else
                        fontFlags = Font.NORMAL;
                } else {
                    fontFlags = Font.NORMAL;
                    fontType = new String[] { cfgVal[4].trim() };
                }

                if(fontType[0].trim().equals("BaseFont.HELVETICA")) {
                    fontType[0] = BaseFont.HELVETICA;
                    encoding = BaseFont.CP1252;  //latin1 encoding
                } else if(fontType[0].trim().equals("BaseFont.HELVETICA_OBLIQUE")) {
                    fontType[0] = BaseFont.HELVETICA_OBLIQUE;
                    encoding = BaseFont.CP1252;
                } else if(fontType[0].trim().equals("BaseFont.ZAPFDINGBATS")) {
                    fontType[0] = BaseFont.ZAPFDINGBATS;
                    encoding = BaseFont.ZAPFDINGBATS;
                } else {
                    fontType[0] = BaseFont.COURIER;
                    encoding = BaseFont.CP1252;
                }

                bf = BaseFont.createFont(fontType[0],encoding,BaseFont.NOT_EMBEDDED);

                // write in a rectangle area
                if (cfgVal.length >= 9) {
                    Font font = new Font(bf, Integer.parseInt(cfgVal[5].trim()), fontFlags);
                    ct.setSimpleColumn(Integer.parseInt(cfgVal[1].trim()), (height - Integer.parseInt(cfgVal[2]
                            .trim())), Integer.parseInt(cfgVal[7].trim()), (height - Integer.parseInt(cfgVal[8]
                            .trim())), Integer.parseInt(cfgVal[9].trim()), (cfgVal[0].trim().equals("left") ?
                                Element.ALIGN_LEFT: (cfgVal[0].trim().equals("right") ? Element.ALIGN_RIGHT :
                                    Element.ALIGN_CENTER)));

                    ct.setText(new Phrase(12, props.getProperty(tempName.toString(), ""), font));
                    ct.go();
                    continue;
                }

                // draw line directly
                if (tempName.toString().startsWith("__$line")) {
                    cb.setRGBColorStrokeF(0f, 0f, 0f);
                    cb.setLineWidth(Float.parseFloat(cfgVal[4].trim()));
                    cb.moveTo(Float.parseFloat(cfgVal[0].trim()), Float.parseFloat(cfgVal[1].trim()));
                    cb.lineTo(Float.parseFloat(cfgVal[2].trim()), Float.parseFloat(cfgVal[3].trim()));
                    // stroke the lines
                    cb.stroke();
                    // write text directly

                } else if (tempName.toString().startsWith("__")) {
                    cb.beginText();
                    cb.setFontAndSize(bf, Integer.parseInt(cfgVal[5].trim()));
                    cb
                            .showTextAligned((cfgVal[0].trim().equals("left") ? PdfContentByte.ALIGN_LEFT
                            : (cfgVal[0].trim().equals("right") ? PdfContentByte.ALIGN_RIGHT
                            : PdfContentByte.ALIGN_CENTER)), (cfgVal.length >= 7 ? (cfgVal[6]
                            .trim()) : props.getProperty(tempName.toString(), "")), Integer
                            .parseInt(cfgVal[1].trim()), (height - Integer.parseInt(cfgVal[2].trim())), 0);

                    cb.endText();
                } else if (tempName.toString().equals("forms_promotext")){
                    if ( OscarProperties.getInstance().getProperty("FORMS_PROMOTEXT") != null ){
                        cb.beginText();
                        cb.setFontAndSize(bf, Integer.parseInt(cfgVal[5].trim()));
                        cb.showTextAligned((cfgVal[0].trim().equals("left") ? PdfContentByte.ALIGN_LEFT : (cfgVal[0].trim().equals("right") ? PdfContentByte.ALIGN_RIGHT : PdfContentByte.ALIGN_CENTER)),
                                OscarProperties.getInstance().getProperty("FORMS_PROMOTEXT"),
                                Integer.parseInt(cfgVal[1].trim()),
                                (height - Integer.parseInt(cfgVal[2].trim())),
                                0);

                        cb.endText();
                    }
                } else { // write prop text

                    cb.beginText();
                    cb.setFontAndSize(bf, Integer.parseInt(cfgVal[5].trim()));
                    cb
                            .showTextAligned((cfgVal[0].trim().equals("left") ? PdfContentByte.ALIGN_LEFT
                            : (cfgVal[0].trim().equals("right") ? PdfContentByte.ALIGN_RIGHT
                            : PdfContentByte.ALIGN_CENTER)), (cfgVal.length >= 7 ? ((props
                            .getProperty(tempName.toString(), "").equals("") ? "" : cfgVal[6].trim()))
                            : props.getProperty(tempName.toString(), "")), Integer.parseInt(cfgVal[1]
                            .trim()), (height - Integer.parseInt(cfgVal[2].trim())), 0);

                    cb.endText();
                }
            }


        } catch (DocumentException dex) {
            baosPDF.reset();
            throw dex;
        } finally {
            if (document != null)
                document.close();
            if (writer != null)
                writer.close();
        }
    	return baosPDF;
    }

    public ActionForward setRegistrationLetterData(ActionMapping mapping, ActionForm  form,HttpServletRequest request, HttpServletResponse response) {
    	String nameOffset = request.getParameter("nameOffset");
    	String upperText  = request.getParameter("upperText");
    	String lowerText  = request.getParameter("lowerText");

    	PropertyDao propertyDao = (PropertyDao) SpringUtils.getBean("propertyDao");

    	try{
            List<Property> propertyList = propertyDao.findByName("MYOSCAR_REGISTRATION_LETTER_NAME_OFFSET");
            if (propertyList.size() > 0){
            	Property property =  propertyList.get(propertyList.size()-1);
            	int currentValue = Integer.parseInt(property.getValue());
            	int proposedValue = Integer.parseInt(nameOffset);
            	if( currentValue != proposedValue){
            		property = new Property();
                	property.setName("MYOSCAR_REGISTRATION_LETTER_NAME_OFFSET");
                    property.setValue(nameOffset);
                    property.setProviderNo((String) request.getSession().getAttribute("user"));
                    propertyDao.persist(property);
            	}
            }
    	}catch(Exception e){
        	log.error("OFFSET SAVING ERROR ",e);
        }

        /*
        property = new Property();
    	property.setName("MYOSCAR_REGISTRATION_LETTER_UPPER_TEXT");
        property.setValue(nameOffset);
        propertyDao.persist(property);

        property = new Property();
    	property.setName("MYOSCAR_REGISTRATION_LETTER_NAME_OFFSET");
        property.setValue(nameOffset);
        propertyDao.persist(property);
         */

    	return mapping.findForward("registrationLetter");
    }

    public ActionForward registerUser(ActionMapping mapping, ActionForm  form,HttpServletRequest request, HttpServletResponse response) {
    	String user = (String) request.getSession().getAttribute("user");

    	ActionRedirect ar = new ActionRedirect(mapping.findForward("registrationResult").getPath());

        PHRAuthentication phrAuth = (PHRAuthentication) request.getSession().getAttribute(PHRAuthentication.SESSION_PHR_AUTH);
        if (phrAuth == null || phrAuth.getMyOscarUserId() == null) {
            ar.addParameter("failmessage", "Permission Denied: You must be logged into myOSCAR to register users");
            return ar;
        }

        HashMap<String, Object> ht = new HashMap<String, Object>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String param = (String) paramNames.nextElement();
            if (param.indexOf("list:") == -1)
                ht.put(param, request.getParameter(param));
            else
                ht.put(param, request.getParameterValues(param));
        }
        ht.put("registeringProviderNo", request.getSession().getAttribute("user"));
        try {
            PersonTransfer newAccount=phrService.sendUserRegistration(phrAuth, ht);
            //if all is well, add the "pin" in the demographic screen
            String demographicNo = request.getParameter("demographicNo");

            DemographicData dd = new DemographicData();
            dd.setDemographicPin(demographicNo, newAccount.getUserName());
            //Then create the record in the demographic file for record.
            ByteArrayOutputStream boas = generateUserRegistrationLetter(demographicNo,newAccount.getUserName(), request.getParameter("password"));
            String docDir = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
            File docDirectory = new File(docDir);
            Date registrationDate = new Date();
            String filename = "MyoscarRegistartionLetter."+demographicNo+"."+registrationDate.getTime()+".pdf";
            File patientRegistrationDocument = new File(docDirectory,filename);
    	    FileOutputStream fos = new FileOutputStream(patientRegistrationDocument);
    	    boas.writeTo(fos);
    	    fos.close();
    	    boas.close();

    	    org.oscarehr.document.model.Document document = new org.oscarehr.document.model.Document();
    	    document.setContenttype("application/pdf");
    	    document.setDocdesc("MyOscar Registration");
    	    document.setDocfilename(filename);
    	    document.setDoccreator(user);
    	    document.setPublic(new Byte("0"));
    	    document.setStatus("A");
    	    document.setObservationdate(registrationDate);
    	    document.setUpdatedatetime(registrationDate);
    	    document.setDoctype("others");

    	    DocumentDAO documentDAO = (DocumentDAO) SpringUtils.getBean("documentDAO");
    	    documentDAO.save(document);

    	    CtlDocumentPK ctlDocumentPK = new CtlDocumentPK(Integer.parseInt(""+document.getId()),"demographic");

    	    CtlDocument ctlDocument = new CtlDocument();
    	    ctlDocument.setId(ctlDocumentPK);
    	    ctlDocument.setModuleId(Integer.parseInt(demographicNo));
    	    ctlDocument.setStatus("A");
    	    documentDAO.saveCtlDocument(ctlDocument);

    	    ar.addParameter("DocId",""+document.getId());

            addRelationships(request, newAccount);
            enableExternalComponent( request, newAccount );
            enableEmailNotifications( request, newAccount );
        }
        catch (InvalidRequestException_Exception e)
        {
        	log.debug("error", e);
            ar.addParameter("failmessage", "Error, most likely cause is the username already exists. Check to make sure this person doesn't already have a myoscar user or try a different username.");
        }
        catch (Exception e) {
            log.error("Failed to register myOSCAR user", e);
            if (e.getClass().getName().indexOf("ActionNotPerformedException") != -1) {
                ar.addParameter("failmessage", "Error on the myOSCAR server.  Perhaps the user already exists.");
            } else if (e.getClass().getName().indexOf("IndivoException") != -1) {
                ar.addParameter("failmessage", "Error on the myOSCAR server.  Perhaps the user already exists.");
            } else if (e.getClass().getName().indexOf("JAXBException") != -1) {
                ar.addParameter("failmessage", "Error: Could not generate sharing permissions (JAXBException)");
            } else {
                ar.addParameter("failmessage", "Unknown Error: Check the log file for details.");
            }
        }

        return ar;
    }

    private void addRelationships(HttpServletRequest request, PersonTransfer newAccount) throws NotAuthorisedException_Exception, InvalidRequestException_Exception, InvalidRelationshipException_Exception {

    	if (log.isDebugEnabled())
    	{
    		WebUtils.dumpParameters(request);
    	}

    	PHRAuthentication auth=MyOscarUtils.getPHRAuthentication(request.getSession());
		AccountWs accountWs=MyOscarServerWebServicesManager.getAccountWs(auth.getMyOscarUserId(), auth.getMyOscarPassword());

		@SuppressWarnings("unchecked")
        Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();

			if (key.startsWith("enable_primary_relation_")) handlePrimaryRelation(accountWs, request, newAccount, key);
			if (key.startsWith("enable_reverse_relation_")) handleReverseRelation(accountWs, request, newAccount, key);
		}

		RegistrationHelper.storeSelectionDefaults(request);
    }
    
    private void enableExternalComponent(HttpServletRequest request, PersonTransfer newAccount) throws NotAuthorisedException_Exception, NoSuchItemException_Exception {
		
		OscarProperties props = OscarProperties.getInstance();
		String externalComponentName = props.getProperty("myoscar_external_component");

		if( externalComponentName != null ){
			PHRAuthentication auth=MyOscarUtils.getPHRAuthentication(request.getSession());
			AccountWs accountWs=MyOscarServerWebServicesManager.getAccountWs(auth.getMyOscarUserId(), auth.getMyOscarPassword());
		
			//See setExternalComponentVisible on myoscar_client_utils' AccountManager.java
			String myoscar_client_prefix = "MyOscarClient.External.";
			accountWs.setPersonPreferences3(newAccount.getId(), myoscar_client_prefix + externalComponentName, "true");
			log.debug("Registering component: "+externalComponentName);
		}
    }
    
    private void enableEmailNotifications(HttpServletRequest request, PersonTransfer newAccount) throws NotAuthorisedException_Exception, NoSuchItemException_Exception{
    	OscarProperties props = OscarProperties.getInstance();
		String enableEmailNotifications = props.getProperty("myoscar_email_notifications");

		if( enableEmailNotifications != null && ("yes").equals(enableEmailNotifications)){
			PHRAuthentication auth=MyOscarUtils.getPHRAuthentication(request.getSession());
			AccountWs accountWs=MyOscarServerWebServicesManager.getAccountWs(auth.getMyOscarUserId(), auth.getMyOscarPassword());
		
			accountWs.setPersonPreferences3(newAccount.getId(), "EMAIL_NOTIFICATION_ON_NEW_DOCUMENT", "true");
			log.debug("Setting email notifications to true for new documents");
			
			/*
			//Our version of oscar can't handle this at the moment
			MessageWs messageWs = MyOscarServerWebServicesManager.getMessageWs(auth.getMyOscarUserId(), auth.getMyOscarPassword());
			MessagingPreferencesTransfer messagePrefTransfer = new MessagingPreferencesTransfer();
			messagePrefTransfer.setEmailNotifyUponNewMessage(true)
			messageWs.updateMessagingPreferences(messagePrefTransfer);
			log.debug("Setting email notifications to true for new messages");
			
			*/
		}
    }

	private void handleReverseRelation(AccountWs accountWs, HttpServletRequest request, PersonTransfer newAccount, String key) throws NotAuthorisedException_Exception, InvalidRequestException_Exception, InvalidRelationshipException_Exception {
		if (!WebUtils.isChecked(request, key)) return;

		Long otherMyOscarUserId=new Long(key.substring("enable_reverse_relation_".length()));
		Relation relation=Relation.valueOf(request.getParameter("reverse_relation_"+otherMyOscarUserId));
		accountWs.createRelationship(otherMyOscarUserId, newAccount.getId(), relation);
    }

	private void handlePrimaryRelation(AccountWs accountWs, HttpServletRequest request, PersonTransfer newAccount, String key) throws NotAuthorisedException_Exception, InvalidRequestException_Exception, InvalidRelationshipException_Exception {
		if (!WebUtils.isChecked(request, key)) return;

		Long otherMyOscarUserId=new Long(key.substring("enable_primary_relation_".length()));
		Relation relation=Relation.valueOf(request.getParameter("primary_relation_"+otherMyOscarUserId));
		accountWs.createRelationship(newAccount.getId(), otherMyOscarUserId, relation);
    }

	public ActionForward approveAction(ActionMapping mapping, ActionForm  form,HttpServletRequest request, HttpServletResponse response) {
        IndivoAPService apService = new IndivoAPService(phrService);
        String actionId = request.getParameter("actionId");
        PHRAction action = phrActionDAO.getActionById(actionId);
        apService.approveAccessPolicy(action);
        return mapping.findForward("msgIndex");
    }

    public ActionForward denyAction(ActionMapping mapping, ActionForm  form,HttpServletRequest request, HttpServletResponse response)  {
        IndivoAPService apService = new IndivoAPService(phrService);
        String actionId = request.getParameter("actionId");
        PHRAction action = phrActionDAO.getActionById(actionId);
        apService.denyAccessPolicy(action);
        return mapping.findForward("msgIndex");
    }

    public ActionForward addPatientRelationship(ActionMapping mapping, ActionForm  form,HttpServletRequest request, HttpServletResponse response) throws Exception {
    	if (log.isDebugEnabled()){
    		WebUtils.dumpParameters(request);
    	}
    	String demoNo = request.getParameter("demoNo");
    	String myOscarUserName = request.getParameter("myOscarUserName");

    	PHRAuthentication auth=MyOscarUtils.getPHRAuthentication(request.getSession());
    	boolean patientRelationshipCreated = MyOscarServerRelationManager.addPatientRelationship(auth,  demoNo );
    	log.debug("Patient Added: "+patientRelationshipCreated);
		request.setAttribute("myOscarUserName",myOscarUserName);
		request.setAttribute("demoNo",demoNo);
		return mapping.findForward("relationfragment");
    	
    }

}
