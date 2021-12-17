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


package org.oscarehr.common.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.validator.DynaValidatorForm;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.ContactDao;
import org.oscarehr.common.dao.ContactSpecialtyDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.ProfessionalContactDao;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.Contact;
import org.oscarehr.common.model.ContactSpecialty;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.ProfessionalContact;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.Provider;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.BeanUtils;
import oscar.OscarProperties;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ContactAction extends DispatchAction
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final ContactDao contactDao = (ContactDao)SpringUtils.getBean("contactDao");
	private static final ProfessionalContactDao proContactDao = (ProfessionalContactDao)SpringUtils.getBean("professionalContactDao");
	private static final DemographicContactDao demographicContactDao = (DemographicContactDao)SpringUtils.getBean("demographicContactDao");
	private static final DemographicDao demographicDao= (DemographicDao)SpringUtils.getBean("demographicDao");
	private static final ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
	private static final ProfessionalSpecialistDao professionalSpecialistDao = SpringUtils.getBean(ProfessionalSpecialistDao.class);
	private static final ContactSpecialtyDao contactSpecialtyDao = SpringUtils.getBean(ContactSpecialtyDao.class);
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	 
	@Override
	protected ActionForward unspecified(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		return manage(mapping,form,request,response);
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
	{
		int demographicNo = Integer.parseInt(request.getParameter("demographic_no"));

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), demographicNo, Permission.DEMOGRAPHIC_READ);
		
		List<DemographicContact> dcs = demographicContactDao.findByDemographicNoAndCategory(demographicNo,DemographicContact.CATEGORY_PERSONAL);
		for(DemographicContact dc:dcs) {
			if(dc.getType() == (DemographicContact.TYPE_DEMOGRAPHIC)) {
				dc.setContactName(demographicDao.getClientByDemographicNo(Integer.parseInt(dc.getContactId())).getFormattedName());
			}
			if(dc.getType() == (DemographicContact.TYPE_CONTACT)) {
				dc.setContactName(contactDao.find(Integer.parseInt(dc.getContactId())).getFormattedName());
			}
		}

		request.setAttribute("contacts", dcs);
		request.setAttribute("contact_num", dcs.size());

		List<DemographicContact> pdcs = demographicContactDao.findByDemographicNoAndCategory(demographicNo,DemographicContact.CATEGORY_PROFESSIONAL);
		for(DemographicContact dc:pdcs) {
			// workaround: UI allows to enter specialist with  a type that is not set, prevent NPE and display 'Unknown' as name
			// user then can choose to delete this entry
			String contactName = null;
			if(dc.getType() == (DemographicContact.TYPE_PROVIDER)) {
				Provider provider = providerDao.getProvider(dc.getContactId()); 
				contactName = (provider == null)?"Unknown":provider.getFormattedName();
			}
			if(dc.getType() == (DemographicContact.TYPE_CONTACT)) {
				Contact contact = contactDao.find(Integer.parseInt(dc.getContactId()));
				contactName = (contact == null)?"Unknown":contact.getFormattedName();
			}
			if(dc.getType() == (DemographicContact.TYPE_PROFESSIONALSPECIALIST)) {
				ProfessionalSpecialist profSpecialist = professionalSpecialistDao.find(Integer.parseInt(dc.getContactId()));
				contactName = (profSpecialist == null)?"Unknown":profSpecialist.getFormattedName();
			}
			StringUtils.trimToEmpty(contactName);
			dc.setContactName(contactName);
		}
		request.setAttribute("procontacts", pdcs);
		request.setAttribute("procontact_num", pdcs.size());

		if(request.getParameter("demographic_no") != null && request.getParameter("demographic_no").length()>0)
			request.setAttribute("demographic_no", request.getParameter("demographic_no"));

		return mapping.findForward("manage");
	}

	public ActionForward saveManage(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		int demographicNo = Integer.parseInt(request.getParameter("demographic_no"));
    	int maxContact = Integer.parseInt(request.getParameter("contact_num"));
    	String forward = "windowClose";
    	String postMethod = request.getParameter("postMethod");

		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), demographicNo, Permission.DEMOGRAPHIC_CREATE, Permission.DEMOGRAPHIC_UPDATE);

		List<String> existingExternalContacts = getDemographicContactIds(String.valueOf(demographicNo), DemographicContact.TYPE_CONTACT);
		List<String> existingInternalContacts = getDemographicContactIds(String.valueOf(demographicNo), DemographicContact.TYPE_DEMOGRAPHIC);

		if( "ajax".equalsIgnoreCase( postMethod ) ) {
    		forward = postMethod;
    	}

    	for(int x=1;x<=maxContact;x++) {
    		String id = request.getParameter("contact_"+x+".id");
    		if(id != null) {
				boolean newContact = id.isEmpty() || Integer.parseInt(id) <= 0;

    			String otherId = request.getParameter("contact_"+x+".contactId");
    			if(otherId.length() == 0 || otherId.equals("0")) {
    				continue;
    			}

    			DemographicContact demographicContact = new DemographicContact();
				int type;
			    if(newContact)
			    {
				    if (request.getParameter("contact_"+x+".type") != null) {
					    type = Integer.parseInt(request.getParameter("contact_"+x+".type"));
					    demographicContact.setType(type);
				    }
				    else
				    {
					    throw new IllegalArgumentException("Invalid/missing contact type");
				    }

				    if((DemographicContact.TYPE_DEMOGRAPHIC == type && existingInternalContacts.contains(otherId))
						    || (DemographicContact.TYPE_CONTACT == type && existingExternalContacts.contains(otherId)))
				    {
					    throw new IllegalStateException("Duplicate Contact");
				    }
			    }
				else
			    {
				    demographicContact = demographicContactDao.find(Integer.parseInt(id));
			    }

				demographicContact.setDemographicNo(demographicNo);
    			demographicContact.setRole(request.getParameter("contact_"+x+".role"));


    			demographicContact.setNote(request.getParameter("contact_"+x+".note"));
    			demographicContact.setContactId(otherId);
    			demographicContact.setCategory(DemographicContact.CATEGORY_PERSONAL);
    			if(request.getParameter("contact_"+x+".sdm") != null) {
    				demographicContact.setSdm("true");
    			} else {
    				demographicContact.setSdm("");
    			}
    			if(request.getParameter("contact_"+x+".ec") != null) {
    				demographicContact.setEc("true");
    			} else {
    				demographicContact.setEc("");
    			}
    			demographicContact.setFacilityId(loggedInInfo.getCurrentFacility().getId());
    			demographicContact.setCreator(loggedInInfo.getLoggedInProviderNo());

    			if(request.getParameter("contact_"+x+".consentToContact").equals("1")) {
    				demographicContact.setConsentToContact(true);
    			} else {
    				demographicContact.setConsentToContact(false);
    			}

    			if(request.getParameter("contact_"+x+".active").equals("1")) {
    				demographicContact.setActive(true);
    			} else {
    				demographicContact.setActive(false);
    			}

			    if(newContact)
			    {
				    demographicContactDao.persist(demographicContact);
			    }
			    else
			    {
				    demographicContactDao.merge(demographicContact);
			    }

    			//internal - do the reverse
    			if(demographicContact.getType() == DemographicContact.TYPE_DEMOGRAPHIC)
				{
    				//check if it exists
					Integer otherDemographicId = Integer.parseInt(otherId);
					if(!demographicContactDao.findOptional(otherDemographicId, String.valueOf(demographicNo), DemographicContact.TYPE_DEMOGRAPHIC).isPresent())
					{
						demographicContact = new DemographicContact();
						if(id.length() > 0 && Integer.parseInt(id) > 0)
						{
							demographicContact = demographicContactDao.find(Integer.parseInt(id));
						}

	    				demographicContact.setDemographicNo(otherDemographicId);
	    				String role = getReverseRole(request.getParameter("contact_"+x+".role"),demographicNo);
	    				if(role != null) {
		        			demographicContact.setRole(role);
		        			demographicContact.setType(DemographicContact.TYPE_DEMOGRAPHIC);
		        			demographicContact.setNote(request.getParameter("contact_"+x+".note"));
		        			demographicContact.setContactId(request.getParameter("demographic_no"));
		        			demographicContact.setCategory(DemographicContact.CATEGORY_PERSONAL);
		        			demographicContact.setSdm("");
		        			demographicContact.setEc("");
		        			demographicContact.setCreator(loggedInInfo.getLoggedInProviderNo());

		        			if(demographicContact.getId() == null)
		        				demographicContactDao.persist(demographicContact);
		        			else
		        				demographicContactDao.merge(demographicContact);
	    				}
    				}
    			}
    		}
    	}

    	int maxProContact = Integer.parseInt(request.getParameter("procontact_num"));
    	for(int x=1;x<=maxProContact;x++) {
    		String id = request.getParameter("procontact_"+x+".id");
    		if(id != null) {
    			String otherId = request.getParameter("procontact_"+x+".contactId");
    			if(otherId.length() == 0 || otherId.equals("0")) {
    				continue;
    			}

    			DemographicContact c = new DemographicContact();
    			if(id.length()>0 && Integer.parseInt(id)>0) {
    				c = demographicContactDao.find(Integer.parseInt(id));
    			}

				c.setDemographicNo(Integer.parseInt(request.getParameter("demographic_no")));
    			c.setRole(request.getParameter("procontact_"+x+".role"));
    			if (request.getParameter("procontact_"+x+".type") != null) {
    			    c.setType(Integer.parseInt(request.getParameter("procontact_"+x+".type")));
    			}
    			c.setContactId(otherId);
    			c.setCategory(DemographicContact.CATEGORY_PROFESSIONAL);
    			c.setFacilityId(loggedInInfo.getCurrentFacility().getId());
    			c.setCreator(loggedInInfo.getLoggedInProviderNo());
    			
    			if( "1".equals(request.getParameter("procontact_"+x+".consentToContact")) ) {
    				c.setConsentToContact(true);
    			} else {
    				c.setConsentToContact(false);
    			}
    			
    			if("1".equals( request.getParameter("procontact_"+x+".active") )) {
    				c.setActive(true);
    			} else {
    				c.setActive(false);
    			}
    			
    			if(c.getId() == null) {
    				demographicContactDao.persist(c);
    			} else {
    				demographicContactDao.merge(c);
    			}
    		}
    	}

    	//handle removes
    	removeContact(mapping, form, request, response);

		return mapping.findForward( forward );
	}

	private String getReverseRole(String roleName, int targetDemographicNo) {
		Demographic demographic = demographicDao.getDemographicById(targetDemographicNo);

		if(roleName.equals("Mother") || roleName.equals("Father") || roleName.equals("Parent")) {
			if(demographic.getSex().equalsIgnoreCase("M")) {
				return "Son";
			} else {
				return "Daughter";
			}

		} else if(roleName.equals("Wife")) {
			return "Husband";

		} else if(roleName.equals("Husband")) {
			return "Wife";
		} else if(roleName.equals("Partner")) {
			return "Partner";
		} else if(roleName.equals("Son") || roleName.equals("Daughter")) {
			if(demographic.getSex().equalsIgnoreCase("M")) {
				return "Father";
			} else {
				return "Mother";
			}

		} else if(roleName.equals("Brother") || roleName.equals("Sister")) {
			if(demographic.getSex().equalsIgnoreCase("M")) {
				return "Brother";
			} else {
				return "Sister";
			}
		}

		return null;
	}
	
	public ActionForward removeContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		List<String> arrayListIds = new ArrayList<>();
		String[] proContactIds = request.getParameterValues("procontact.delete");
		String[] contactIds = request.getParameterValues("contact.delete");
		String postMethod = request.getParameter("postMethod");
		String removeSingleId = request.getParameter("contactId");
		ActionForward actionForward = null;

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DEMOGRAPHIC_READ);
		
    	if( "ajax".equalsIgnoreCase( postMethod ) ) {
    		actionForward = mapping.findForward( postMethod );
    	}
    	
    	if(removeSingleId != null) {
			arrayListIds.add(removeSingleId);
		}
    	
		if( proContactIds != null || contactIds != null) {

			if(proContactIds != null) {
				arrayListIds.addAll(Arrays.asList( proContactIds ) );
			}
			
			if(contactIds != null) {
				arrayListIds.addAll(Arrays.asList( contactIds ) );
			}
			
		}
		
		int contactId;
		for( String id : arrayListIds ) {
			if (!id.isEmpty())
			{

				contactId = Integer.parseInt(id);
				DemographicContact dc = demographicContactDao.find(contactId);
				dc.setDeleted(true);
				demographicContactDao.merge(dc);
			}
		}

    	
    	return actionForward; 

	}

	public ActionForward addContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		return mapping.findForward("cForm");
	}

	public ActionForward addProContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		ContactSpecialtyDao specialtyDao = SpringUtils.getBean(ContactSpecialtyDao.class);
		List<ContactSpecialty> specialties = specialtyDao.findAll();
		OscarProperties prop = OscarProperties.getInstance();
		request.setAttribute( "region", prop.getInstanceType() );
		request.setAttribute( "specialties", specialties );
		request.setAttribute( "pcontact.lastName", request.getParameter("keyword") );
		request.setAttribute( "contactRole", request.getParameter("contactRole")  );
		return mapping.findForward("pForm");
	}
	
	public ActionForward editHealthCareTeam(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		String demographicContactId = request.getParameter("contactId");
		DemographicContact demographicContact = null;
		Integer contactType = null;
		String contactCategory = "";
		String contactId = "";
		ProfessionalSpecialist professionalSpecialist = null;
		String contactRole = "";
		List<ContactSpecialty> specialtyList = null;

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DEMOGRAPHIC_UPDATE);
		
		
		if( StringUtils.isNotBlank( demographicContactId ) ) {
			
			specialtyList = contactSpecialtyDao.findAll();						
			demographicContact = demographicContactDao.find( Integer.parseInt( demographicContactId ) );
			contactType = demographicContact.getType();
			contactCategory = demographicContact.getCategory();
			contactId = demographicContact.getContactId();
			contactRole = demographicContact.getRole();
			
			if( DemographicContact.CATEGORY_PROFESSIONAL.equalsIgnoreCase( contactCategory ) ) {
				
				if( DemographicContact.TYPE_CONTACT == contactType ) {
					
					ProfessionalContact contact = proContactDao.find( Integer.parseInt( contactId ) );
					request.setAttribute("pcontact", contact);

				} else if( DemographicContact.TYPE_PROFESSIONALSPECIALIST == contactType ) {
					
					professionalSpecialist = professionalSpecialistDao.find( Integer.parseInt( contactId ) );
					
					if( professionalSpecialist != null ) { 
						request.setAttribute( "pcontact", buildContact( professionalSpecialist ) );
					}
				}			
			}
			
			// specialty should be from the relational table via specialty id.
			// converting back to id here.
			
			if( ! StringUtils.isNumeric( contactRole ) ) {
				String specialtyDesc;
				for( ContactSpecialty specialty : specialtyList ) {
					specialtyDesc = specialty.getSpecialty().trim();
					if( specialtyDesc.equalsIgnoreCase( contactRole ) ) {
						 contactRole = specialty.getId()+"";
					}
				}
			}

			request.setAttribute( "specialties", specialtyList );			
			request.setAttribute( "contactRole", contactRole );
			request.setAttribute( "demographicContactId", demographicContactId );
		}
		
		return mapping.findForward("pForm");
	}
	
	public ActionForward editContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("contact.id");
		Contact contact = null;
		if(StringUtils.isNotBlank(id)) {
			id = id.trim();
			contact = contactDao.find(Integer.parseInt(id));
			request.setAttribute("contact", contact);
		}
		return mapping.findForward("cForm");
	}

	public ActionForward editProContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("pcontact.id");
		ProfessionalContact contact = null;
		if( StringUtils.isNotBlank(id) ) {
			id = id.trim();
			contact = proContactDao.find(Integer.parseInt(id));
			request.setAttribute("pcontact", contact);
		}
		return mapping.findForward("pForm");
	}

	public ActionForward saveContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(),
				Permission.DEMOGRAPHIC_CREATE, Permission.DEMOGRAPHIC_UPDATE);
		
		DynaValidatorForm dform = (DynaValidatorForm)form;
		Contact contact = (Contact)dform.get("contact");
		String id = StringUtils.trimToNull(request.getParameter("contact.id"));
		if(ConversionUtils.hasContent(id))
		{
			Contact savedContact = contactDao.find(Integer.parseInt(id));
			if (savedContact != null)
			{
				String[] ignoreProps = {"id"};
				BeanUtils.copyProperties(contact, savedContact, ignoreProps);
				contactDao.merge(savedContact);
			}
		}
		else
		{
			// The ID on the request parameter is null or empty string, which
			// gets converted to 0 by the DynaValidator parser for Integer types.
			// To persist a new record, it needs to be null.
			contact.setId(null);
			contactDao.persist(contact);
		}

	   return mapping.findForward("windowClose");
	}

	public ActionForward saveProContact(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {		
		DynaValidatorForm dform = (DynaValidatorForm)form;
		ProfessionalContact contact = (ProfessionalContact) dform.get("pcontact");
		
		String id = request.getParameter("pcontact.id");
		String demographicContactId = request.getParameter("demographicContactId");
		DemographicContact demographicContact = null;
		Integer contactType = null; // this needs to be null as there are -1 and 0 contact types
		String contactRole = ""; 

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(),
				Permission.DEMOGRAPHIC_CREATE, Permission.DEMOGRAPHIC_UPDATE);
		
		if(id != null && id.length() > 0) {
			
			logger.info("Editing a current Professional Contact with id " + contact.getId());
			
			// changes for the DemographicContact table
			if( StringUtils.isNumeric( demographicContactId )) {
				demographicContact = demographicContactDao.find( Integer.parseInt( demographicContactId ) );		
				contactType = demographicContact.getType();
			}
			
			// changes for the ProfessionalSpecialist table
			if( DemographicContact.TYPE_PROFESSIONALSPECIALIST == contactType ) { 
				// convert from a ProfessionalContact to ProfessionalSpecialist				
				ProfessionalSpecialist professionalSpecialist = professionalSpecialistDao.find( Integer.parseInt( id ) );

				String address =  contact.getAddress().trim() + " " + 
						contact.getAddress2().trim() + " " +
						contact.getPostal().trim() + ", " +
						contact.getCity().trim() + ", " + 
						contact.getProvince().trim()  + ", " +
						contact.getCountry().trim();
				
				professionalSpecialist.setStreetAddress( address );
				professionalSpecialist.setFirstName( contact.getFirstName() );
				professionalSpecialist.setLastName( contact.getLastName() );				
				professionalSpecialist.setEmailAddress( contact.getEmail() );
				professionalSpecialist.setPhoneNumber( contact.getWorkPhone() ); 
				professionalSpecialist.setFaxNumber( contact.getFax() );
				professionalSpecialist.setReferralNo( contact.getCpso() );
				
				professionalSpecialistDao.merge( professionalSpecialist );
			
			// changes for the Contact table.
			} else {
			
				ProfessionalContact savedContact = proContactDao.find( Integer.parseInt( id ) );
				if(savedContact != null) {
					
					BeanUtils.copyProperties( contact, savedContact, new String[]{"id"} );
					proContactDao.merge( savedContact );
					contactRole = savedContact.getSpecialty();
				}
			}
		
		// persist by default for new contacts.
		} else {
			
			logger.info("Saving a new Professional Contact with id " + contact.getId());
			
			proContactDao.persist(contact);
			
			contactRole = contact.getSpecialty();
			id = contact.getId() + "";
			
		}
		
		// slingshot the DemographicContact details back to the request.
		// the saveManage method is to difficult to re-engineer
		request.setAttribute("demographicContactId", demographicContactId);
		request.setAttribute( "contactId", id );
		request.setAttribute( "contactRole", contactRole );
		request.setAttribute( "contactType", contactType );
		request.setAttribute( "contactName", contact.getFormattedName() );	
		
	   return mapping.findForward("pForm");
	}
	
	/**
	 * Return a list of of all the contacts in Oscar's database.
	 * Contact, Professional Contact, and Professional Specialists
	 * @param searchMode
	 * @param orderBy
	 * @param keyword
	 * @return List of type Contact
	 */
	public static List<Contact> searchAllContacts(String searchMode, String orderBy, String keyword) {
		List<Contact> contacts = new ArrayList<Contact>();
		List<ProfessionalSpecialist> professionalSpecialistContact = professionalSpecialistDao.search(keyword);		
		
		// if there is a future in adding personal contacts.
		// contacts.addAll( contactDao.search(searchMode, orderBy, keyword) );		
		contacts.addAll( proContactDao.search(searchMode, orderBy, keyword) );		
		contacts.addAll( buildContact( professionalSpecialistContact ) );
		
		Collections.sort(contacts, byLastName);

		return contacts;
	}


	public static List<Contact> searchContacts(String searchMode, String orderBy, String keyword)
	{
		List<Contact> contacts = contactDao.search(searchMode, orderBy, keyword);
		return contacts;
	}

	public static List<ProfessionalContact> searchProContacts(String searchMode, String orderBy, String keyword) {
		List<ProfessionalContact> contacts = proContactDao.search(searchMode, orderBy, keyword);
		return contacts;
	}
	
	public static List<ProfessionalSpecialist> searchProfessionalSpecialists(String keyword) {
		List<ProfessionalSpecialist> contacts = professionalSpecialistDao.search(keyword);
		return contacts;
	}

	public static List<DemographicContact> getDemographicProfessionalContacts(Demographic demographic, String category) {
		List<DemographicContact> contacts = demographicContactDao.findByDemographicNoAndCategory(demographic.getDemographicNo(), category);
		return fillContactNames(contacts);
	}

	public static List<String> getDemographicContactIds(String demographicNo, int type)
	{
		List<DemographicContact> contacts = demographicContactDao.findByDemographicNoAndType(Integer.parseInt(demographicNo), type);
		return contacts.stream().map(DemographicContact::getContactId).collect(Collectors.toList());
	}

	public static List<DemographicContact> fillContactNames(List<DemographicContact> contacts)
	{

		Provider provider;
		Contact contact; 
		ProfessionalSpecialist professionalSpecialist;
		ContactSpecialty specialty;
		String providerFormattedName = ""; 
		String role = "";
		
		for (DemographicContact demographicContact : contacts)
		{
			role = demographicContact.getRole();
			if (StringUtils.isNumeric(role) && !role.isEmpty())
			{
				specialty = contactSpecialtyDao.find(Integer.parseInt(role.trim()));

				if (specialty != null)
				{
					demographicContact.setRole(specialty.getSpecialty());
				}
			}

			switch (demographicContact.getType())
			{
				case DemographicContact.TYPE_DEMOGRAPHIC:
					demographicContact.setContactName(demographicDao.getClientByDemographicNo(Integer.parseInt(demographicContact.getContactId())).getFormattedName());
					break;

				case DemographicContact.TYPE_PROVIDER:
					provider = providerDao.getProvider(demographicContact.getContactId());
					if (provider != null)
					{
						providerFormattedName = provider.getFormattedName();
					}

					if (StringUtils.isBlank(providerFormattedName))
					{
						providerFormattedName = "Error: Contact Support";
						logger.error("Formatted name for provder was not avaialable. Contact number: " + demographicContact.getContactId());
					}
					demographicContact.setContactName(providerFormattedName);

					contact = new ProfessionalContact();
					contact.setWorkPhone("internal");
					contact.setFax("internal");
					demographicContact.setDetails(contact);
					break;

				case DemographicContact.TYPE_CONTACT:
					contact = contactDao.find(Integer.parseInt(demographicContact.getContactId()));
					demographicContact.setContactName(contact.getFormattedName());
					demographicContact.setDetails(contact);
					break;

				case DemographicContact.TYPE_PROFESSIONALSPECIALIST:
					professionalSpecialist = professionalSpecialistDao.find(Integer.parseInt(demographicContact.getContactId()));
					demographicContact.setContactName(professionalSpecialist.getFormattedName());
					contact = buildContact(professionalSpecialist);
					demographicContact.setDetails(contact);
					break;
			}
		}
		return contacts;
	}
	
	private static final List<Contact> buildContact(final List<?> contact) {
		List<Contact> contactlist = new ArrayList<Contact>();
		Contact contactitem;
		Iterator<?> contactiterator = contact.iterator();
		while( contactiterator.hasNext() ) {
			contactitem = buildContact( contactiterator.next() );
			contactlist.add( contactitem );
		}		
		return contactlist;
	}
	

	/**
	 * Return a generic Contact class from any other class of 
	 * contact. 
	 * @return
	 */
	private static final Contact buildContact(final Object contactobject) {
		ProfessionalContact contact = new ProfessionalContact();
		
		Integer id = null;
		String systemId = "";
		String firstName = ""; 
		String lastName = "";
		String address = "";
		String address2 = "";
		String city = "";
		String country = "";
		String postal = "";
		String province = "";
		boolean deleted = false;
		String cellPhone = "-";
		String workPhone = "";
		String email = "";
		String residencePhone = "";
		String fax = ""; 
		String specialty = "";
		String cpso = "";
		
		if(contactobject instanceof ProfessionalSpecialist) {
			
			ProfessionalSpecialist professionalSpecialist = (ProfessionalSpecialist) contactobject;
			
			// assuming that the address String is always csv.
			address = professionalSpecialist.getStreetAddress();

			if (address != null && address.contains(","))
			{
				String[] addressArray = address.split(",");
				address = addressArray[0].trim();
				if (addressArray.length > 3)
				{
					city = addressArray[1].trim();
					province = addressArray[2].trim();
					country = addressArray[3].trim();
				} else if (addressArray.length == 3)
				{
					province = addressArray[1].trim();
					country = addressArray[2].trim();
				} else
				{
					province = addressArray[1].trim();
				}
			}
			
			// mark the contact with Specialist Type - Later parsed in client Javascript.
			// using SystemId as a transient parameter only.
			systemId = DemographicContact.TYPE_PROFESSIONALSPECIALIST+"";
			id = professionalSpecialist.getId();
			firstName = professionalSpecialist.getFirstName();
			lastName = professionalSpecialist.getLastName();
			email = professionalSpecialist.getEmailAddress();
			residencePhone = professionalSpecialist.getPhoneNumber();
			workPhone = professionalSpecialist.getPhoneNumber(); 
			fax = professionalSpecialist.getFaxNumber();
			cpso = professionalSpecialist.getReferralNo();
			
		}
		
		contact.setId(id);
		contact.setSystemId(systemId);
		contact.setFirstName(firstName);
		contact.setLastName(lastName);
		contact.setAddress(address);
		contact.setAddress2(address2);
		contact.setCity(city);
		contact.setCountry(country);
		contact.setPostal(postal);
		contact.setProvince(province);
		contact.setDeleted(deleted);
		contact.setCellPhone(cellPhone);
		contact.setWorkPhone(workPhone);
		contact.setResidencePhone(residencePhone);
		contact.setFax(fax);
		contact.setEmail(email);
		contact.setSpecialty(specialty);
		contact.setCpso(cpso);

		return contact;
	}
	
	public static Comparator<Contact> byLastName = new Comparator<Contact>() {
		public int compare(Contact contact1, Contact contact2) {
			String lastname1 = contact1.getLastName().toUpperCase();
			String lastname2 = contact2.getLastName().toUpperCase();
			return lastname1.compareTo(lastname2);
		}
	};
	
}
