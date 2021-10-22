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
 * Created on 2005-6-1
 */
package oscar.log;

/**
 * @author yilee18
 */
public class LogConst {
	/* Action constants 
	 * These should describe what user action was done */
	public static final String ACTION_LOGIN = "log in";
	public static final String ACTION_LOGOUT = "log out";
	public static final String ACTION_READ = "read";
	public static final String ACTION_ADD = "add";
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_RESTORE = "restore";
	public static final String ACTION_ACCESS = "access";
	public static final String ACTION_UNLOCK = "unlock";
	public static final String ACTION_ASSIGN = "assign";
	public static final String DISCONTINUE = "discontinue";
	public static final String ARCHIVE = "archive";
	public static final String REPRINT = "reprint";
	public static final String ACTION_REVIEWED = "reviewed";
	public static final String ACK = "acknowledge";
	public static final String NORIGHT = "no right";
	public static final String ACTION_EDIT = "edit";
	public static final String REPRESCRIBE = "represcribe";
	public static final String ANNOTATE = "annotate";
	public static final String VERIFY = "verify";
	public static final String REFUSED = "refused";
	public static final String ACTION_UNLINK = "unlink";
	public static final String ACTION_SENT = "sent";
	public static final String ACTION_DOWNLOAD = "download";

	/* Content/Module constants
	 * These should describe what module the action applies to.
	 * Example: action ADD module CON_DOCUMENT */
	public static final String CON_CASELOAD = "Caseload";
	public static final String CON_LOGIN_AGREEMENT = "login agreement";
	public static final String CON_LOGIN = "login";
	public static final String CON_LOGOUT = "logout";
	public static final String CON_SHELTER_SELECTION = "select shelter";
	public static final String CON_APPT = "appointment";
	public static final String CON_ECHART = "eChart";
	public static final String CON_EFORM_TEMPLATE = "eFormTemplate";
	public static final String CON_EFORM_DATA = "eForm";
	public static final String CON_DEMOGRAPHIC = "demographic";
	public static final String CON_DEMOGRAPHIC_CONTACT = "demographic_contact";
	public static final String CON_DEMOGRAPHIC_RELATION = "demographic_relations";
	public static final String CON_DEMOGRAPHIC_MERGE = "demographic_merge";
	public static final String CON_DISEASE_REG = "DX";
	public static final String CON_ROLE = "role";
	public static final String CON_PROVIDER = "provider";
	public static final String CON_PRIVILEGE = "privilege";
	public static final String CON_PASSWORD = "password/pin";
	public static final String CON_ADMIN = "admin";
	public static final String CON_FORM = "form";
	public static final String CON_PRESCRIPTION = "prescription";
	public static final String CON_MEDICATION = "medication";
	public static final String CON_DRUGS = "drugs";
	public static final String CON_DRUGREASON = "drugReason";
	public static final String CON_ALLERGY = "allergy";
	public static final String CON_JASPERREPORTLETER = "jr_letter";
	public static final String CON_TICKLER = "tickler";
	public static final String CON_CME_NOTE = "encounter note";
	public static final String CON_DOCUMENT = "document";
	public static final String CON_HL7_LAB = "lab";
	public static final String CON_HRM = "hrm";
	public static final String CON_OLIS_LAB = "OLIS lab";
	public static final String CON_CML_LAB = "cml lab";
	public static final String CON_MDS_LAB = "mds lab";
	public static final String CON_PATHNET_LAB = "pathnet lab";
	public static final String CON_FLOWSHEET = "FLWST_";
	public static final String CON_SECURITY = "securityRecord";
	public static final String CON_ANNOTATION = "annotation";
	public static final String CON_PHR = "phr";
	public static final String CON_DOCUMENTDESCRIPTIONTEMPLATE = "documentDescriptionTemplate";
	public static final String CON_DOCUMENTDESCRIPTIONTEMPLATEPREFERENCE = "documentDescriptionTemplatePreference";
	public static final String CON_FAX = "fax";
	public static final String CON_PHARMACY = "pharmacy";
	public static final String CON_SYSTEM = "system";
	public static final String CON_ELECTRONIC_MESSAGING_CONSENT_STATUS = "ELECTRONIC_MESSAGING_CONSENT_STATUS";

	/* Status constants
	 * These should describe the outcome of the action */
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_FAILURE = "FAILURE";
}
