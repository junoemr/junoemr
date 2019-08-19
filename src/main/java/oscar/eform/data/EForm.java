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


package oscar.eform.data;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessages;
import org.oscarehr.common.OtherIdManager;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.ui.servlet.ImageRenderingServlet;
import org.oscarehr.util.DigitalSignatureUtils;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.eform.EFormLoader;
import oscar.eform.EFormUtil;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import oscar.oscarEncounter.oscarMeasurements.util.WriteNewMeasurements;
import oscar.util.ConversionUtils;
import oscar.util.StringBuilderUtils;
import oscar.util.UtilDateUtilities;

import javax.management.AttributeNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use oscarhr.eform.service for new stuff
 * @deprecated
 */
@Deprecated
public class EForm extends EFormBase {
	private static EFormDataDao eFormDataDao = SpringUtils.getBean(EFormDataDao.class);
	private static PreventionDao preventionDao = SpringUtils.getBean(PreventionDao.class);
	private static Logger log = MiscUtils.getLogger();

	private String appointment_no = "-1";
	private HashMap<String,String> sql_params = new HashMap<String, String>();
	private String parentAjaxId = null;
	private String eform_link = null;
	private HashMap<String, String> fieldValues = new HashMap<String, String>();
	private int needValueInForm = 0;
	private boolean setAP2nd = false;
	
	private String loggedInProvider = null;

	private static final String EFORM_DEMOGRAPHIC = "eform_demographic";
	private static final String VAR_NAME = "var_name";
	private static final String VAR_VALUE = "var_value";
	private static final String REF_FID = "fid";
	private static final String REF_VAR_NAME = "ref_var_name";
	private static final String REF_VAR_VALUE = "ref_var_value";
	private static final String TABLE_NAME = "table_name";
	private static final String TABLE_ID = "table_id";
	private static final String OTHER_KEY = "other_key";
	private static final String OPENER_VALUE = "link$eform";
	private static final String PRECHECKED = "checked=\"checked\"";

	public EForm() {
	}

	/**
	 * Construct from the model object for backwards compatability
	 * This avoids the built in database query on loading
	 * @param model - eForm Data model
	 */
	public EForm(EFormData model)
	{
		this.fdid = (model.getId() != null)? model.getId().toString() : null;
		this.fid = model.getFormId().toString();
		this.providerNo = model.getProviderNo();
		this.demographicNo = model.getDemographicId().toString();
		this.formName = model.getFormName();
		this.formSubject = model.getSubject();
		this.formDate = model.getFormDate().toString();
		this.formHtml = model.getFormData();
		this.showLatestFormOnly = model.isShowLatestFormOnly();
		this.patientIndependent = model.isPatientIndependent();
		this.roleType = model.getRoleType();
	}

	public EForm(String fid, String demographicNo) {
		loadEForm(fid, demographicNo);
	}

	public EForm(String fid, String demographicNo, String providerNo) {
		// used to load an uploaded eform
		loadEForm(fid, demographicNo);
		this.providerNo = providerNo;
	}

	public EForm(String fdid) {
		if(!StringUtils.isBlank(fdid) && !"null".equalsIgnoreCase(fdid)) {
			EFormData eFormData = eFormDataDao.find(new Integer(fdid));
			if (eFormData != null) {
				this.fdid = fdid;
				this.fid = eFormData.getFormId().toString();
				this.providerNo = eFormData.getProviderNo();
				this.demographicNo = eFormData.getDemographicId().toString();
				this.formName = eFormData.getFormName();
				this.formSubject = eFormData.getSubject();
				this.formDate = eFormData.getFormDate().toString();
				this.formHtml = eFormData.getFormData();
				this.showLatestFormOnly = eFormData.isShowLatestFormOnly();
				this.patientIndependent = eFormData.isPatientIndependent();
				this.roleType = eFormData.getRoleType();
			}
		} else {
			this.formName = "";
			this.formSubject = "";
			this.formHtml = "No Such Form in Database";
		}
	}

	public void loadEForm(String fid, String demographicNo) {
		HashMap loaded = EFormUtil.loadEForm(fid);
		this.fid = fid;
		this.formName = (String) loaded.get("formName");
		this.formHtml = (String) loaded.get("formHtml");
		this.formSubject = (String) loaded.get("formSubject");
		this.formDate = (String) loaded.get("formDate");
		this.formFileName = (String) loaded.get("formFileName");
		this.formCreator = (String) loaded.get("formCreator");
		this.demographicNo = demographicNo;
		this.showLatestFormOnly = (Boolean)loaded.get("showLatestFormOnly");
		this.patientIndependent = (Boolean)loaded.get("patientIndependent");
		this.instanced = (Boolean)loaded.get("instanced");
		this.roleType = (String) loaded.get("roleType");
	}

	public String getAppointmentNo() {
		return this.appointment_no;
	}
	public void setAppointmentNo(String appt_no) {
		this.appointment_no = StringUtils.isBlank(appt_no) ? "-1" : appt_no;
	}
	
	public void setLoggedInProvider( String providerNo ) {
		this.loggedInProvider = providerNo;
	}

	public String getEformLink() {
		return this.eform_link;
	}
	public void setEformLink(String el) {
		this.eform_link = el;
	}

	public String getParentAjaxId()
	{
		return parentAjaxId;
	}

	public void setParentAjaxId(String parentAjaxId)
	{
		this.parentAjaxId = parentAjaxId;
	}

	/**
	 * Disable commonly used e-form controls which are used to submit the form.  These are the submit button,
	 * print submit button and the subject field (as a cue).  Since the names of these controls can vary in the wild,
	 * this method is only a best attempt at disabling these inputs.
	 */
	public void disableSubmitControls()
	{
		StringBuilder html = new StringBuilder(this.formHtml);

		// Our e-form generator labels the control buttons using this div.
		// For other e-forms that don't do this, we'll have to search the whole document.
		int bottomButtonsIndex = StringBuilderUtils.indexOfIgnoreCase(html, "id=\"BottomButtons\"",0);
		if (bottomButtonsIndex < 0)
		{
			bottomButtonsIndex = 0;
		}

		disableInput(html, "SubmitButton", bottomButtonsIndex);
		disableInput(html, "PrintSubmitButton", bottomButtonsIndex);
		disableInput(html, "subject", bottomButtonsIndex);

		this.formHtml = html.toString();
	}

	/**
	 * Append a disabled tag to the first html element with a name or id matching inputIdentifier.
	 *
	 * @param html StringBuilder containing the html to parse
	 * @param inputIdentifier Html input element name or id to disable
	 * @param searchStartIndex The character index in the html to start the search
	 */
	private void disableInput(StringBuilder html, String inputIdentifier, int searchStartIndex)
	{
		int identifierStart = StringBuilderUtils.indexOfIgnoreCase(html, inputIdentifier, searchStartIndex);

		while (identifierStart >= 0)
		{
			int identifierEnd = identifierStart + inputIdentifier.length();

			// Forms in the wild are inconsistent with regards to name or id.  We'll search for both to be safe.
			if ((identifierStart >= 4 && html.substring(identifierStart - 4, identifierStart).toLowerCase().equals("id=\"")) ||
				(identifierStart >= 6 && html.substring(identifierStart - 6, identifierStart).toLowerCase().equals("name=\"")))
			{
				if ((html.length() > identifierEnd + 1) && html.substring(identifierEnd, identifierEnd + 1).equals("\""))
				{
					int elementEnd = html.indexOf(">", identifierEnd);
					html.insert(elementEnd, " disabled");
				}

				break;
			}

			identifierStart = StringBuilderUtils.indexOfIgnoreCase(html, inputIdentifier, identifierEnd + 1);
		}
	}

	public void setAction() {
		setAction(false);
	}

	public void setAction(boolean unset) {
		// sets action= in the form
		StringBuilder html = new StringBuilder(this.formHtml);
		int index = StringBuilderUtils.indexOfIgnoreCase(html, "<form", 0);
		int endtag = html.indexOf(">", index + 1);
		// --remove all previous actions, methods and names from the form tag
		if (index < 0) return;

		int pointer, pointer2;
		while (((pointer = StringBuilderUtils.indexOfIgnoreCase(html, " action=", index)) >= 0) && (pointer < endtag)) {
			pointer2 = nextSpot(html, pointer + 1);
			html = html.delete(pointer, pointer2);
			endtag = html.indexOf(">", index + 1);
		}
		while (((pointer = StringBuilderUtils.indexOfIgnoreCase(html, " method=", index)) >= 0) && (pointer < endtag)) {
			pointer2 = nextSpot(html, pointer + 1);
			html = html.delete(pointer, pointer2);
			endtag = html.indexOf(">", index + 1);
		}
		pointer = StringBuilderUtils.indexOfIgnoreCase(html, " name=", index);
		String name = "name=\"saveEForm\" ";
		if ((pointer >= 0) && (pointer < endtag)) {
			pointer2 = nextSpot(html, pointer + 1);
			endtag = html.indexOf(">", index + 1);
			name = "";
		}
		if (index < 0) return;
		if (unset) {
			this.formHtml = html.toString();
			return;
		}
		index += 5;

		String formDataId = StringUtils.trimToEmpty(this.fdid);
		StringBuilder action = new StringBuilder("action=\"../eform/addEForm.do?efmfid="+this.fid+"&efmfdid="+formDataId+"&efmdemographic_no="+this.demographicNo+"&efmprovider_no="+this.providerNo+"&eform_link="+this.eform_link);
		if (this.parentAjaxId != null) action.append("&parentAjaxId=" + this.parentAjaxId);

		action.append("\"");

		String method = "method=\"POST\"";
		html.insert(index, " " + action.toString() + " " + name + method);
		this.formHtml = html.toString();
	}

	// ------------------Saving the Form (inserting value= statements)---------------------
	public void setValues(List<String> allNames, List<String> allValues)
	{
		StringBuilder html = new StringBuilder(this.formHtml);
		int pointer = -1;
		// Iterates through every relevant html tag in 'this.formHtml'
		while ((pointer = getFieldIndex(html, pointer + 1)) >= 0)
		{
			String fieldHeader = getFieldHeader(html, pointer);
			// fieldName is set to the 'name' attribute of the current html tag the while loop is on
			String fieldName = EFormUtil.removeQuotes(EFormUtil.getAttribute("name", fieldHeader));
			String val;

			// allNames is an array of all the values submitted from the form
			int i = allNames.indexOf(fieldName);
			// If the current fieldName isn't in allNames, it wasn't submitted in the form.
			if (i < 0)
			{
				// If this fieldName doesn't contain checked="checked", it's not a prechecked checkbox
				if (!fieldHeader.contains(PRECHECKED))
				{
					if (fieldName == null)
					{
						continue;
					} else if (getFieldType(fieldHeader).equals("checkbox"))
					{
						//Default checkboxes to off. Unchecked checkboxes are not submitted with form, so the values stay as what they were previously
						val = "off";
					} else
					{
						// Save an empty string over all values that don't have values in eform values
						val = "";
					}
				} else
				{
					// putValue method needs to know if the current fieldHeader is prechecked or not
					val = "prechecked";
				}
			} else
			{
				val = allValues.get(i);
			}

			pointer = nextSpot(html, pointer);
			html = putValue(val, getFieldType(fieldHeader), pointer, html);
		}
		this.formHtml = html.toString();
	}

	// ------------------Saving the Form (inserting fdid$value= statements)---------------------
	public void setOpenerValues(ArrayList<String> names, ArrayList<String> values) {
		StringBuilder html = new StringBuilder(this.formHtml);
		EFormLoader.getInstance();
		String opener = EFormLoader.getOpener(); // default: opener: "oscarOPEN="
		int markerLoc = -1;
		while ((markerLoc = getFieldIndex(html, markerLoc + 1)) >= 0) {
			String fieldHeader = getFieldHeader(html, markerLoc);
			if (StringUtils.isBlank(EFormUtil.getAttribute(opener, fieldHeader))) continue;

			String fieldName = EFormUtil.removeQuotes(EFormUtil.getAttribute("name", fieldHeader));
			int i;
			if (StringUtils.isBlank(fieldName)) continue;
			if ((i = names.indexOf(fieldName)) < 0) continue;
			if (StringUtils.isBlank(values.get(i))) continue;

			// sets up the pointer where to write the value
			int pointer = nextSpot(html, markerLoc+EFormUtil.getAttributePos(opener,fieldHeader));
			int offset = EFormUtil.getAttributePos(OPENER_VALUE, fieldHeader);
			if (offset>=0) {
				//delete current OPENER_VALUE
				pointer = markerLoc+offset;
				int valueEnd = nextSpot(html, pointer);
				html.delete(pointer-1, valueEnd);
			}
			html = putValue(values.get(i), OPENER_VALUE, pointer, html);
		}
		formHtml = html.toString();
	}

	// --------------------------Setting APs utilities----------------------------------------
	public void setDatabaseAPs() {
		StringBuilder html = new StringBuilder(this.formHtml);
		EFormLoader.getInstance();
		String marker = EFormLoader.getMarker(); // default: marker: "oscarDB="
		for (int i = 0; i < 2; i++) { // run the following twice if "count"-type field is found
			int markerLoc = -1;
			while ((markerLoc = getFieldIndex(html, markerLoc + 1)) >= 0) {
				log.debug("===============START CYCLE===========");
				String fieldHeader = getFieldHeader(html, markerLoc);
				String apName = EFormUtil.getAttribute(marker, fieldHeader); // gets varname from oscarDB=varname
				if (StringUtils.isBlank(apName)) {
					if (!setAP2nd) saveFieldValue(html, markerLoc);
					continue;
				}
				int apNameLen = apName.length();
				apName = EFormUtil.removeQuotes(apName);

				log.debug("AP ==== " + apName);
				if (setAP2nd && !apName.startsWith("e$")) continue; // ignore non-e$ oscarDB on 2nd run

				int needing = needValueInForm;
				String fieldType = getFieldType(fieldHeader); // textarea, text, hidden etc..
				if ((fieldType==null || fieldType.equals("")) || (apName==null || apName.equals(""))) continue;

				// sets up the pointer where to write the value
				int pointer = markerLoc + EFormUtil.getAttributePos(marker,fieldHeader) + marker.length() + 1;
				if (!fieldType.equals("textarea")) {
					pointer += apNameLen;
				}
				EFormLoader.getInstance();
				DatabaseAP curAP = EFormLoader.getAP(apName);

				if (curAP == null) curAP = getAPExtra(apName, fieldHeader);
				if (curAP == null) continue;
				if (!setAP2nd) { // 1st run
					html = putValuesFromAP(curAP, fieldType, pointer, markerLoc, html);
					saveFieldValue(html, markerLoc);
				} else { // 2nd run
					if (needing > needValueInForm) html = putValuesFromAP(curAP, fieldType, pointer, markerLoc, html);
				}

				log.debug("Marker ==== " + markerLoc);
				log.debug("FIELD TYPE ====" + fieldType);
				log.debug("=================End Cycle==============");
			}
			formHtml = html.toString();
			if (needValueInForm > 0) setAP2nd = true;
			else i = 2;
		}
	}
	// --------------------------Setting Update APs utilities----------------------------------------
	public void setDatabaseUpdateAPs() {
		StringBuilder html = new StringBuilder(this.formHtml);
		EFormLoader.getInstance();
		String marker = EFormLoader.getUpdateMarker(); // default: marker: "oscarUpdateMarker="

		for (int i = 0; i < 2; i++) { // run the following twice if "count"-type field is found
			int markerLoc = -1;
			while ((markerLoc = getFieldIndex(html, markerLoc + 1)) >= 0) {
				log.debug("===============START UPDATE AP CYCLE===========");
				String fieldHeader = getFieldHeader(html, markerLoc);
				String apName = EFormUtil.getAttribute(marker, fieldHeader);
				String apName0 = EFormUtil.removeQuotes(apName);

				if (EFormUtil.blank(apName)) {
					if (!setAP2nd) saveFieldValue(html, markerLoc);
					continue;
				}
				log.debug("AP ==== " + apName);

				if (setAP2nd && !apName0.startsWith("e$")) continue; // ignore non-e$ oscarDB on 2nd run

				int needing = needValueInForm;
				String fieldType = getFieldType(fieldHeader); // textarea, text, hidden etc..

				if ((fieldType.equals("")) || (apName0.equals(""))) continue;

				// sets up the pointer where to write the value
				int header_pos = markerLoc;
				int pointer = markerLoc + EFormUtil.getAttributePos(marker, fieldHeader) + marker.length() + 1;

				if (!fieldType.equals("textarea")) {
					pointer += apName.length();
				}

				EFormLoader.getInstance();
				DatabaseAP curAP = EFormLoader.getAP(apName0);
				if (curAP == null) curAP = getAPExtra(apName0, fieldHeader);
				if (curAP == null) continue;
				if (!setAP2nd) { // 1st run
					// html = putValuesFromAP(curAP, fieldType, pointer, html);
					html = replaceFieldValueFromAP(curAP, fieldType, header_pos, html);
					saveFieldValue(html, markerLoc);
				}
				else { // 2nd run
					if (needing > needValueInForm) html = replaceFieldValueFromAP(curAP, fieldType, pointer, html);
				}
				log.debug("Marker ==== " + markerLoc);
				log.debug("FIELD TYPE ====" + fieldType);
				log.debug("=================End Cycle==============");
			}
			formHtml = html.toString();

			if (needValueInForm > 0) setAP2nd = true;
			else i = 2;
		}
	}

	// Gets all the fields that are "input" (i.e. write-to-database) fields.
	public void setupInputFields() {
		String marker = EFormLoader.getInputMarker();
		StringBuilder html = new StringBuilder(this.formHtml);
		int markerLoc;
		int pointer = 0;
		while ((markerLoc = StringBuilderUtils.indexOfIgnoreCase(html, marker, pointer)) >= 0) {
			pointer = (markerLoc + marker.length());
			updateFields.add(getFieldName(html, pointer));
		}

		generateInputCode();
	}

	private void generateInputCode() {
		if (updateFields.size() > 0) {

			StringBuilder html = new StringBuilder(this.formHtml);
			int formEndLoc = StringBuilderUtils.indexOfIgnoreCase(html, "</form>", 0);
			int scriptEndLoc = StringBuilderUtils.indexOfIgnoreCase(html, "</script>", 0);

			if (formEndLoc < 0) formEndLoc = 1;

			if (scriptEndLoc < 0) scriptEndLoc = 0;
			else scriptEndLoc += 9;

			String fieldValue = "";
			for (String field : updateFields) {
				fieldValue += field + "%";
			}

			html.insert(formEndLoc-1, "<span id='_oscardodatabaseupdatespan' style='position: absolute;' class='DoNotPrint'><input type='checkbox' name='_oscardodatabaseupdate' onchange='_togglehighlight()' /> Update Fields in Database<br />" +
					"<input type='button' id='_oscarrefreshfieldsbtn' name='_oscarrefreshfieldsbtn' value='Refresh DB Fields' onclick='_refreshfields()' /></span> " +
					"<input type='hidden' id='_oscarupdatefields' name='_oscarupdatefields' value='" + fieldValue + "' />" +
					"<input type='hidden' id='_oscardemographicno' name='_oscardemographicno' value='" + this.demographicNo + "' />" +
					"<input type='hidden' id='_oscarproviderno' name='_oscarproviderno' value='" + this.providerNo + "' />" +
					"<input type='hidden' id='_oscarfid' name='_oscarfid' value='" + this.fid + "' />");

			this.formHtml = html.insert(scriptEndLoc, "<script type='text/javascript' src='../share/javascript/jquery/jquery-1.4.2.js'></script>" +
					"<script type='text/javascript' src='../js/eform_highlight.js'></script>").toString();
		}
	}

	// --------------------------Setting oscarOPEN behaviours ----------------------------------------
	public void setOscarOPEN(String requestURI) {
		StringBuilder html = new StringBuilder(this.formHtml);
		EFormLoader.getInstance();
		String opener = EFormLoader.getOpener(); // default: opener: "oscarOPEN="
		int markerLoc = -1;
		while ((markerLoc = getFieldIndex(html, markerLoc + 1)) >= 0) {
			log.debug("=============START OPENER CYCLE===========");
			String fieldHeader = getFieldHeader(html, markerLoc);
			String efmName = EFormUtil.getAttribute(opener, fieldHeader); // gets eform name from oscarOPEN=rname
			if (StringUtils.isBlank(efmName)) continue;

			String fieldName = EFormUtil.removeQuotes(EFormUtil.getAttribute("name", fieldHeader));
			if (StringUtils.isBlank(fieldName)) continue;

			log.debug("OPEN ==== " + efmName);
			// sets up the pointer where to write the value
			String fdid = EFormUtil.removeQuotes(EFormUtil.getAttribute(OPENER_VALUE, fieldHeader));
			EFormLoader.getInstance();
			String onclick = EFormLoader.getOpenEform(requestURI, fdid, EFormUtil.removeQuotes(efmName), fieldName, this);
			int pointer = EFormUtil.getAttributePos("onclick", fieldHeader);
			String type = pointer<0 ? "onclick" : "onclick_append";
			if (pointer<0) {
				pointer = nextSpot(html, markerLoc+EFormUtil.getAttributePos(opener,fieldHeader));
			} else {
				pointer = nextSpot(html, markerLoc+pointer);
			}
			html = putValue(onclick, type, pointer, html);

			log.debug("Opener ==== " + markerLoc);
			log.debug("=================End Opener Cycle==============");
		}
		formHtml = html.toString();
	}

	public void setNowDateTime() {
		this.formTime = UtilDateUtilities.DateToString(new Date(), "HH:mm:ss");
		this.formDate = UtilDateUtilities.DateToString(new Date(), "yyyy-MM-dd");
	}

	public List<String> checkMeasurements(List<String> names, List<String> values)
	{
		return WriteNewMeasurements.validateMeasurements(names, values);
	}

	public ActionMessages setMeasurements(List<String> names, List<String> values) {
		return (WriteNewMeasurements.addMeasurements(names, values, this.demographicNo, this.providerNo));
	}

	public void setContextPath(String contextPath) {
		if (StringUtils.isBlank(contextPath)) return;

		String oscarJS = contextPath + "/share/javascript/";
		this.formHtml = this.formHtml.replace(jsMarker, oscarJS);
	}

	public void setSource(String source) {
		if (StringUtils.isBlank(source)) source="";

		this.formHtml = this.formHtml.replace(sourceMarker, source);
	}


	public ArrayList<String> getOpenerNames() {
		ArrayList<String> openerNames = new ArrayList<String>();
		EFormLoader.getInstance();
		String opener = EFormLoader.getOpener(); // default: opener: "oscarOPEN="
		StringBuilder html = new StringBuilder(this.formHtml);
		int markerLoc = -1;
		while ((markerLoc = getFieldIndex(html, markerLoc + 1)) >= 0) {
			String fieldHeader = getFieldHeader(html, markerLoc);
			String efmName = EFormUtil.getAttribute(opener, fieldHeader); // gets eform name from oscarOPEN=rname
			if (StringUtils.isBlank(efmName)) continue;

			String fieldName = EFormUtil.removeQuotes(EFormUtil.getAttribute("name", fieldHeader));
			if (!StringUtils.isBlank(fieldName)) openerNames.add(fieldName);
		}
		return openerNames;
	}

	public String getTemplate() {
		// Get content between "<!-- <template>" & "</template> -->"
		if (StringUtils.isBlank(formHtml)) return "";

		String sTemplateBegin = "<template>";
		String sTemplateEnd = "</template>";
		String sCommentBegin = "<!--", sCommentEnd = "-->";
		String text = "";

		int templateBegin = -1, templateEnd = -1;
		boolean searching = true;
		while (searching) {
			templateBegin = EFormUtil.findIgnoreCase(sTemplateBegin, formHtml, templateBegin + 1);
			templateEnd = EFormUtil.findIgnoreCase(sTemplateEnd, formHtml, templateBegin);
			int commentBegin = formHtml.lastIndexOf(sCommentBegin, templateBegin);
			int commentEnd = formHtml.indexOf(sCommentEnd, commentBegin);
			if (templateBegin == -1 || templateEnd == -1 || commentBegin == -1 || commentEnd == -1) {
				searching = false;
			} else if (commentEnd > templateEnd) {
				text += formHtml.substring(templateBegin + sTemplateBegin.length(), templateEnd);
			}
		}
		return text;
	}

	// ----------------------------------private -----------------------------------------
	private DatabaseAP getAPExtra(String apName, String fieldHeader) {
		// --------------------------Process extra attributes for APs --------------------------------
		Pattern p = Pattern.compile("\\b[a-z]\\$[^\\$#]+#[^\n]+");
		Matcher m = p.matcher(apName);
		if (!m.matches()) return null;

		String module = apName.substring(0, apName.indexOf("$"));
		String type = apName.substring(apName.indexOf("$") + 1, apName.indexOf("#"));
		String field = apName.substring(apName.indexOf("#") + 1, apName.length());
		DatabaseAP curAP = null;
		if (module.equals("m")) {
			log.debug("SWITCHING TO MEASUREMENTS");

			int maxResults = 1;
			int startAt = type.indexOf('@');
			if (startAt != -1)
			{
				String count = type.substring(startAt + 1);
				type = type.substring(0, startAt);
				maxResults = StringUtils.isNumeric(count) ? Integer.parseInt(count) : 1;
			}

			Hashtable data = EctMeasurementsDataBeanHandler.getLast(this.demographicNo, type, maxResults);

			if (!data.isEmpty()) {
				curAP = new DatabaseAP();
				curAP.setApName(apName);
				curAP.setApOutput((String) data.get(field));
				log.info("SET Measurement AP: " + apName + "=" + data.get(field));
			}
		} else if (module.equals("e")) {
			log.debug("SWITCHING TO EFORM_VALUES");
			String eform_name = EFormUtil.removeQuotes(EFormUtil.getAttribute("eform$name", fieldHeader));
			String var_value = EFormUtil.removeQuotes(EFormUtil.getAttribute("var$value", fieldHeader));
			String ref = EFormUtil.removeQuotes(EFormUtil.getAttribute("ref$", fieldHeader, true));

			String eform_demographic = this.demographicNo;
			if (this.patientIndependent) eform_demographic = "%";

			String ref_name = null, ref_value = null, ref_fid = fid;
			if (!StringUtils.isBlank(ref) && ref.contains("=")) {
				ref_name = ref.substring(4, ref.indexOf("="));
				ref_value = EFormUtil.removeQuotes(ref.substring(ref.indexOf("=") + 1));
			} else {
				ref_name = StringUtils.isBlank(ref) ? "" : ref.substring(4);
			}
			if (!StringUtils.isBlank(eform_name)) ref_fid = getRefFid(eform_name);
			if ((!StringUtils.isBlank(var_value) && var_value.trim().startsWith("{")) || (!StringUtils.isBlank(ref_value) && ref_value.trim().startsWith("{"))) {
				if (setAP2nd) { // 2nd run, put value in required field
					var_value = findValueInForm(var_value);
					ref_value = findValueInForm(ref_value);
					needValueInForm--;
				} else { // 1st run, note the need to reference other value in form
					needValueInForm++;
					return null;
				}
			}

			if (type.equalsIgnoreCase("count") && var_value == null) {
				type = "countname";
			}
			else if ((type.equalsIgnoreCase("first") || type.equalsIgnoreCase("last")) && field.equals("*")) {
				type += "_all_json";
			}
			if (!ref_name.equals("")) {
				type += "_ref";
				if (ref_value == null) type += "name";
			}

			EFormLoader.getInstance();
			curAP = EFormLoader.getAP("_eform_values_" + type);

			if (curAP != null) {
				setSqlParams(EFORM_DEMOGRAPHIC, eform_demographic);
				setSqlParams(VAR_NAME, field);
				setSqlParams(REF_VAR_NAME, ref_name);
				setSqlParams(VAR_VALUE, var_value);
				setSqlParams(REF_VAR_VALUE, ref_value);
				setSqlParams(REF_FID, ref_fid);
			}

		}
		else if (module.equals("p"))
		{ //preventions & vaccines
			log.debug("SWITCHING TO PREVENTIONS");

			// get the latest prevention
			Prevention prevention = preventionDao.findMostRecentByTypeAndDemoNo(type, Integer.parseInt(this.demographicNo));
			if(prevention != null) {

				curAP = new DatabaseAP();
				curAP.setApName(apName);
				String value = "";
				// quick dirty way to get specified value
				if("ID".equalsIgnoreCase(field))                        value = String.valueOf(prevention.getId());
				if("DEMOGRAPHIC_NO".equalsIgnoreCase(field))            value = String.valueOf(prevention.getDemographicId());
				if("CREATION_DATE".equalsIgnoreCase(field))             value = dateStringOrEmpty(prevention.getCreationDate());
				if("CREATOR".equalsIgnoreCase(field))                   value = String.valueOf(prevention.getCreatorProviderNo());
				if("PREVENTION_DATE".equalsIgnoreCase(field))           value = dateStringOrEmpty(prevention.getPreventionDate());
				if("PROVIDER_NO".equalsIgnoreCase(field))               value = String.valueOf(prevention.getProviderNo());
				if("REFUSED".equalsIgnoreCase(field))                   value = String.valueOf(prevention.isRefused());
				if("UPDATE_DATE".equalsIgnoreCase(field))               value = dateStringOrEmpty(prevention.getLastUpdateDate());

				curAP.setApOutput(value);
			}
		}
		else if (module.equals("o")) {
			log.debug("SWITCHING TO OTHER_ID");
			String table_name = "", table_id = "";
			EFormLoader.getInstance();
			curAP = EFormLoader.getAP("_other_id");
			if (type.equalsIgnoreCase("patient")) {
				table_name = OtherIdManager.DEMOGRAPHIC.toString();
				table_id = this.demographicNo;
			} else if (type.equalsIgnoreCase("appointment")) {
				table_name = OtherIdManager.APPOINTMENT.toString();
				table_id = appointment_no;
				if (StringUtils.isBlank(table_id)) table_id = "-1";
			}
			setSqlParams(OTHER_KEY, field);
			setSqlParams(TABLE_NAME, table_name);
			setSqlParams(TABLE_ID, table_id);
		}
		return curAP;
	}

	private StringBuilder putValue(String value, String type, int pointer, StringBuilder html)
	{

		value = StringEscapeUtils.escapeHtml(value);
		// inserts value= into tag or textarea
		if (type.equals("onclick") || type.equals("onclick_append"))
		{
			if (type.equals("onclick_append"))
			{
				if (html.charAt(pointer - 1) == '"') pointer -= 1;
				if (html.charAt(pointer - 1) != ';') value = ";" + value;
			} else
			{
				value = "onclick=\"" + value + "\"";
			}
			html.insert(pointer, " " + value);
		} else if (type.equals(OPENER_VALUE))
		{
			html.insert(pointer, " " + OPENER_VALUE + "=\"" + value + "\"");
		} else if (type.equals("text") || type.equals("hidden"))
		{
			// Here I am using the convention for an html attribute <element attributeKey="attributeValue">
			final String valueKey = "value=";
			String toInsert = valueKey + "\"" + value + "\"";

			int attributeStartIndex = pointer;

			if (html.charAt(pointer) == '>')
			{ // handle special case where pointer is on closing ">".
				toInsert = " " + toInsert;
			}
			else
			{ // if not on closing ">" then we are on end of last attribute add + 1
				attributeStartIndex++;
			}

			try
			{
				// look for existing value attribute and delete it.
				attributeStartIndex = findAttributeIndex(html, pointer, valueKey);
				int attributeEndIndex = nextAttribute(html, attributeStartIndex, null, null);

				html.delete(attributeStartIndex, attributeEndIndex);
			}
			catch (AttributeNotFoundException e)
			{
				// new attribute
				toInsert += " ";
			}

			html.insert(attributeStartIndex, toInsert);

		} else if (type.equals("textarea"))
		{
			pointer = html.indexOf(">", pointer) + 1;
			int endPointer = html.indexOf("<", pointer);
			html.delete(pointer, endPointer);
			html.insert(pointer, value);
		} else if (type.equals("checkbox"))
		{
			if ("prechecked".equals(value))
			{
				pointer = html.indexOf(PRECHECKED, pointer);
				html.delete(pointer, pointer + PRECHECKED.length());
			} else if (value.equals("off"))
			{
				if (html.substring(pointer, pointer + " checked".length()).equals(" checked"))
				{
					html.delete(pointer, pointer + " checked".length());
				}
			} else
			{
				html.insert(pointer, " checked");
			}
		} else if (type.equals("select"))
		{
			int endindex = StringBuilderUtils.indexOfIgnoreCase(html, "</select>", pointer);
			if (endindex < 0) return html; // if closing tag not found

			int valueLoc = nextIndex(html, " value=" + value, " value=\"" + value, pointer);
			if (valueLoc < 0 || valueLoc > endindex) return html;

			//In the current select tag: find all occurrences of the string "selected" and replace with an empty string as we are changing what is selected anyway
			int currentSelectedIndex = StringBuilderUtils.indexOfIgnoreCase(html, "selected", pointer);
			while (currentSelectedIndex > 0)
			{
				html.replace(currentSelectedIndex, currentSelectedIndex + "selected".length(), "");
				currentSelectedIndex = StringBuilderUtils.indexOfIgnoreCase(html, "selected", pointer);
			}

			//Update the value location as it may have changed due to removing selected strings
			valueLoc = nextIndex(html, " value=" + value, " value=\"" + value, pointer);

			pointer = nextSpot(html, valueLoc + 1);
			html.insert(pointer, " selected");
		} else if (type.equals("radio"))
		{
			int endindex = html.indexOf(">", pointer);
			int valindexS = nextIndex(html, " value=", " value=", pointer);
			if (valindexS < 0 || valindexS > endindex) return html; // if value= not found in tag

			valindexS += 7;
			if (html.charAt(valindexS) == '"') valindexS++;
			int valindexE = valindexS + value.length();
			if (html.substring(valindexS, valindexE).equals(value))
			{
				pointer = nextSpot(html, valindexE);
				html.insert(pointer, " checked");
			}
		}
		return html;
	}

	/**
	 * find the index of the requested attribute
	 * @param html html to parse
	 * @param tagStart position at which to start (should be the start of a tag)
	 * @param attrib the attribute to look for
	 * @return the index of the start of the attribute.
	 */
	private int findAttributeIndex(StringBuilder html, int tagStart, String attrib) throws AttributeNotFoundException
	{
		int tagEnd = findTagEnd(html, tagStart);

		int currIndex = tagStart;
		currIndex = html.indexOf(" ", currIndex);
		if (currIndex != -1)
		{
			while (currIndex < tagEnd && currIndex < html.length())
			{
				StringBuilder attribKey = new StringBuilder();
				StringBuilder attribString = new StringBuilder();
				int attributeEnd = nextAttribute(html, currIndex, attribKey, attribString);

				if (attribKey.toString().equals(attrib))
				{
					return nextNonSpace(html, currIndex);
				}

				currIndex = attributeEnd;
			}
		}

		throw new AttributeNotFoundException("attribute [" + attrib + "] was not found");
	}

	/**
	 * 	read the next attribute from an html tag.
	 * @param html the html to scan
	 * @param currIndex  the index at which to start
	 * @param attributeKey the key part of the attribute string
	 * @param attributeString the attribute tag string (return value)
	 * @return end index of the attribute (index of trainling white space).
	 */
	private int nextAttribute(StringBuilder html, int currIndex, StringBuilder attributeKey, StringBuilder attributeString)
	{
		// eat white space up to the attribute
		while (html.charAt(currIndex) == ' ')
		{
			currIndex ++;
		}

		// read attribute
		int attrKeyStart = currIndex;
		int attrKeyEnd = currIndex;
		boolean inQuotes = false;
		while(currIndex < html.length())
		{
			if (!inQuotes && html.charAt(currIndex) == ' ')
			{
				break;
			}
			if (!inQuotes && html.charAt(currIndex) == '>')
			{
				break;
			}
			else if (!inQuotes && html.charAt(currIndex) == '=')
			{
				attrKeyEnd = currIndex;
			}
			else if (!inQuotes && (html.charAt(currIndex) == '"' || html.charAt(currIndex) == '\''))
			{
				inQuotes = true;
			}
			else if (inQuotes && (html.charAt(currIndex) == '"' || html.charAt(currIndex) == '\''))
			{
				inQuotes = false;
			}

			currIndex ++;
		}

		if (attributeKey != null)
		{
			attributeKey.append(html.substring(attrKeyStart, attrKeyEnd + 1));
		}
		if (attributeString != null)
		{
			attributeString.append(html.substring(attrKeyStart, currIndex));
		}
		return currIndex;
	}

	/**
	 * get the index of the next non space character
	 * @param html html to scan
	 * @param currIndex current index in the string
	 * @return index of the next non space character
	 */
	private int nextNonSpace(StringBuilder html, int currIndex)
	{
		while(currIndex < html.toString().length())
		{
			if (html.charAt(currIndex) != ' ')
			{
				break;
			}
			currIndex++;
		}
		return currIndex;
	}

	/**
	 * locate the end '>' of a tag.
	 * @param html the html to scan
	 * @param currIndex the index at which to start. cannot be inside quotes
	 * @return the index if the '>' character.
	 */
	private int findTagEnd(StringBuilder html, int currIndex)
	{
		boolean inQuotes = false;
		for (int i = currIndex;i < html.length(); i++)
		{
			if (!inQuotes && (html.charAt(currIndex) == '"' || html.charAt(currIndex) == '\''))
			{
				inQuotes = true;
			}
			else if(!inQuotes && html.charAt(i) == '>')
			{
				return i;
			}
			else if (html.charAt(currIndex) == '"' || html.charAt(currIndex) == '\'')
			{
				inQuotes = false;
			}
		}
		return 0;
	}

	private int nextIndex(StringBuilder text, String option1, String option2, int pointer) {
		// converts text content to lowercase
		text = new StringBuilder(text.toString().toLowerCase());
		option1 = option1.toLowerCase();
		option2 = option2.toLowerCase();

		// returns the index of option1 or option2 whichever one is closer and exists
		int index;
		int option1i = text.indexOf(option1, pointer);
		int option2i = text.indexOf(option2, pointer);
		if (option1i < 0) index = option2i;
		else if (option2i < 0 || option1i < option2i) index = option1i;
		else index = option2i;
		return index;
	}

	private int nextSpot(StringBuilder text, int pointer) {
		//nextSport: \n, \r, >, ' '
		int end = nextIndex(text, "\n", "\r", pointer);
		if (end < 0) end = text.length();
		int index = text.substring(pointer, end).indexOf('=');
		if (index >= 0) {
			index = pointer + index;
			//deal with cases of quoted values with spaces ("xx xx" / 'xx xx')
			if (text.charAt(index + 1) == '"') {
				int close = text.substring(index + 2, end).indexOf("\"") + (index + 2);
				if (close > 0) return close + 1;
			}
			if (text.charAt(index + 1) == '\'') {
				int close = text.substring(index + 2, end).indexOf("'") + (index + 2);
				if (close > 0) return close + 1;
			}
			pointer = index;
		}
		return nextIndex(text, " ", ">", pointer);
	}

	private String getFieldType(String fieldHeader) {
		if (fieldHeader.substring(1, 9).equalsIgnoreCase("textarea")) return "textarea";
		if (fieldHeader.substring(1, 7).equalsIgnoreCase("select")) return "select";

		String type = EFormUtil.removeQuotes(EFormUtil.getAttribute("type", fieldHeader));

		if(null == type) {
			// Browsers should default to text if type is missing
			type = "text";
		}

		return type;
	}

	private String dateStringOrEmpty(Date date) {
		return ConversionUtils.toDateString(date, ConversionUtils.DEFAULT_DATE_PATTERN);
	}
/*
	private String getFieldType(StringBuilder html, int pointer) {
		// pointer can be any place in the tag - isolates tag and sends back field type
		int open = html.substring(0, pointer).lastIndexOf("<");
		int close = html.substring(pointer).indexOf(">") + pointer + 1;
		String tag = html.substring(open, close);
		log.debug("TAG ====" + tag);
		int start; // <input type="^text".....
		int end; // <input type="text^"....
		if (tag.substring(1, 9).equalsIgnoreCase("textarea")) return "textarea";
		if (tag.substring(1, 7).equalsIgnoreCase("select")) return "select";

		log.debug("TAG PROCESS ====" + tag.substring(1, 9));
		if ((start = tag.toLowerCase().indexOf(" type=")) >= 0) {
			start += 6; // account for type=...
			if (tag.charAt(start) == '\"') { // account for type="..."
				start += 1;
				end = tag.indexOf("\"", start);
			} else {
				int nextSpace = tag.indexOf(" ", start);
				int nextBracket = tag.indexOf(">", start);
				if (nextSpace < 0) end = nextBracket;
				else if ((nextBracket < 0) || (nextSpace < nextBracket)) end = nextSpace;
				else end = nextBracket;

			}
			return tag.substring(start, end).toLowerCase();
		}
		return "";
	}
 *
 */

	private StringBuilder putValuesFromAP(DatabaseAP ap, String type, int pointer, int tagStart, StringBuilder html) {
		//prepare all sql & output
		String sql = ap.getApSQL();
		String output = ap.getApOutput();
		if (!StringUtils.isBlank(sql)) {
			sql = replaceAllFields(sql);
			log.debug("SQL----" + sql);
			ArrayList<String> names = DatabaseAP.parserGetNames(output); // a list of ${apName} --> apName
			sql = DatabaseAP.parserClean(sql); // replaces all other ${apName} expressions with 'apName'

			if (ap.isJsonOutput()) {
				JSONArray values = EFormUtil.getJsonValues(names, sql);
				output = values.toString(); //in case of JsonOutput, return the whole JSONArray and let the javascript deal with it
			}
			else {
				ArrayList<String> values = EFormUtil.getValues(names, sql);
				if (values.size() != names.size()) {
					output = "";
				} else {
					for (int i = 0; i < names.size(); i++) {
						output = DatabaseAP.parserReplace( names.get(i), values.get(i), output);
					}
				}
			}
		}
		output = StringEscapeUtils.escapeHtml(output);
		//put values into according controls
		if (type.equals("textarea")) {
			pointer = html.indexOf(">", pointer) + 1;
			html.insert(pointer, output);
		} else if (type.equals("select")) {
			int selectEnd = StringBuilderUtils.indexOfIgnoreCase(html, "</select>", pointer);
			if (selectEnd >= 0) {
				int valueLoc = nextIndex(html, " value="+output, " value=\""+output, pointer);
				if (valueLoc < 0 || valueLoc > selectEnd) return html;
				pointer = nextSpot(html, valueLoc);
				html = html.insert(pointer, " selected");
			}
		} else { //type=input

			//delete existing value tag
			try
			{
				int attributeStartIndex = findAttributeIndex(html, tagStart, "value=");
				int attributeEndIndex = nextAttribute(html, attributeStartIndex, null, null);
				html.delete(attributeStartIndex, attributeEndIndex);
				pointer = attributeStartIndex;
			}
			catch (AttributeNotFoundException e)
			{
				//guess there is no value tag...
			}

			output = output.replace("\"", "&quot;");
			html.insert(pointer, " value=\""+output+"\"");
			log.debug("insert: (" + pointer + ")' value=\""+output+"\"'");
		}
		return (html);
	}

	public String replaceAllFields(String sql) {
		sql = DatabaseAP.parserReplace("demographic", demographicNo, sql);
		sql = DatabaseAP.parserReplace("provider", providerNo, sql);
		sql = DatabaseAP.parserReplace("loggedInProvider", loggedInProvider, sql);
		sql = DatabaseAP.parserReplace("appt_no", appointment_no, sql);

		sql = DatabaseAP.parserReplace(EFORM_DEMOGRAPHIC, getSqlParams(EFORM_DEMOGRAPHIC), sql);
		sql = DatabaseAP.parserReplace(REF_FID, getSqlParams(REF_FID), sql);
		sql = DatabaseAP.parserReplace(VAR_NAME, getSqlParams(VAR_NAME), sql);
		sql = DatabaseAP.parserReplace(VAR_VALUE, getSqlParams(VAR_VALUE), sql);
		sql = DatabaseAP.parserReplace(REF_VAR_NAME, getSqlParams(REF_VAR_NAME), sql);
		sql = DatabaseAP.parserReplace(REF_VAR_VALUE, getSqlParams(REF_VAR_VALUE), sql);
		sql = DatabaseAP.parserReplace(TABLE_NAME, getSqlParams(TABLE_NAME), sql);
		sql = DatabaseAP.parserReplace(TABLE_ID, getSqlParams(TABLE_ID), sql);
		sql = DatabaseAP.parserReplace(OTHER_KEY, getSqlParams(OTHER_KEY), sql);
		return sql;
	}

	private String getSqlParams(String key) {
		if (sql_params.containsKey(key)) {
			String val =  sql_params.get(key);
			return val==null ? "" : StringEscapeUtils.escapeSql(val);
		}
		return "";
	}

	private void setSqlParams(String key, String value) {
		if (sql_params.containsKey(key)) sql_params.remove(key);
		sql_params.put(key, value);
	}

	private String findValueInForm(String name) {
		// name format = {xxx}
		if (StringUtils.isBlank(name) || !name.trim().startsWith("{") || !name.trim().endsWith("}")) return name;

		// extract content from brackets {}
		name = name.trim().substring(1, name.trim().length() - 1).toLowerCase();
		if (StringUtils.isBlank(name)) return "";

		String value = fieldValues.get(name);
		return value == null ? "" : value;
	}

	private int getFieldIndex(StringBuilder html, int from) {
		if (html == null) return -1;
		Pattern p = Pattern.compile("<input|<select|<textarea|<div");
		Matcher matcher = p.matcher(html);
		if (matcher.find(from)) {
			int start = matcher.start();
			//org.oscarehr.util.MiscUtils.getLogger().info("New code shows: " + start);
			return start;
		} else {
			//org.oscarehr.util.MiscUtils.getLogger().info("New code shows: " + -1);

			return -1;
		}


		/*  Code too slow, replaced with regex
		if (html == null) return -1;

		Integer[] index = new Integer[4];
		index[0] = StringBuilderUtils.indexOfIgnoreCase(html, "<input", from);
		index[1] = StringBuilderUtils.indexOfIgnoreCase(html, "<select", from);
		index[2] = StringBuilderUtils.indexOfIgnoreCase(html, "<textarea", from);
                index[3] = StringBuilderUtils.indexOfIgnoreCase(html, "<div", from);

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < index.length; i++)
			if (index[i] >= 0) list.add(index[i]);

		int min = list.isEmpty() ? -1 : list.get(0);
		for (int i = 1; i < list.size(); i++)
			min = min > list.get(i) ? list.get(i) : min;

		return min;
		*/
	}

	private String getFieldName(StringBuilder html, int pointer) {
		//pointer can be any place in the tag - isolates tag and sends back field type
		int open = html.substring(0, pointer).lastIndexOf("<");
		int close = html.substring(pointer).indexOf(">") + pointer + 1;
		String tag = html.substring(open, close);
		log.debug("TAG ====" + tag);
		int start;  //<input type="^text".....
		int end;    //<input type="text^"....
		if ((start = tag.toLowerCase().indexOf(" name=")) >= 0) {
			start += 6;
			if (tag.charAt(start) == '"') {
				start += 1;
				end = tag.indexOf('"', start);
			} else if (tag.charAt(start) == '\'') {
				start += 1;
				end = tag.indexOf('\'', start);
			} else {
				int nextSpace = tag.indexOf(" ", start);
				int nextBracket = tag.indexOf(">", start);
				if (nextSpace < 0) end = nextBracket;
				else if ((nextBracket < 0) || (nextSpace < nextBracket)) end = nextSpace;
				else end = nextBracket;
			}

			return tag.substring(start, end);
		} else {
			return "";
		}
	}

	private String getFieldHeader(String html, int fieldIndex) {
		StringBuilder sb_html = new StringBuilder(html);
		return getFieldHeader(sb_html, fieldIndex);
	}

	private String getFieldHeader(StringBuilder html, int fieldIndex) {
		//fieldHeader: <input name=... type=... ... >, <select name=... ...>, etc.
		if (html == null || fieldIndex < 0) return "";
		if (html.charAt(fieldIndex) != '<') return ""; // field header must be "<...>"

		// look for char '>' which is NOT inside quotes ("..." or '...')
		int end = fieldIndex;
		boolean quoteOpen = false, quote2Open = false;
		for (int i = fieldIndex + 1; i < html.length(); i++) {
			char c = html.charAt(i);
			if (c == '"' && !quoteOpen) quote2Open = !quote2Open;
			if (c == '\'' && !quote2Open) quoteOpen = !quoteOpen;
			if (!quoteOpen && !quote2Open && c == '>') {
				end = i + 1;
				break;
			}
		}
		return html.substring(fieldIndex, end);
	}

	private StringBuilder replaceFieldValueFromAP(DatabaseAP ap, String type, int pointer, StringBuilder html) {
		// prepare all sql & output
		String sql = ap.getApSQL();
		String output = ap.getApOutput();
		int end_tag;

		if (!EFormUtil.blank(sql))
		{
			sql = replaceAllFields(sql);
			log.debug("SQL----" + sql);
			ArrayList<String> names = DatabaseAP.parserGetNames(output);
			sql = DatabaseAP.parserClean(sql); // replaces all other ${apName} expressions with 'apName'

			ArrayList<String> values = EFormUtil.getValues(names, sql);
			if(values.size() != names.size())
			{
				output = "";
			}
			else
			{
				for(int i = 0; i < names.size(); i++)
				{
					output = DatabaseAP.parserReplace(names.get(i), values.get(i), output);
				}
			}
		}

		//put values into according controls
		if (type.equals("textarea")) {
			pointer = html.indexOf(">", pointer) + 1;
			end_tag = html.indexOf("</textarea>", pointer);
			// html.insert(pointer, output);
			html.replace(pointer, end_tag, output);
		}
		else if (type.equals("select")) {
			int selectEnd = StringBuilderUtils.indexOfIgnoreCase(html, "</select>", pointer);
			if (selectEnd >= 0) {
				int valueLoc = nextIndex(html, " value=" + output, " value=\"" + output, pointer);
				if (valueLoc < 0 || valueLoc > selectEnd) return html;
				pointer = nextSpot(html, valueLoc);
				html = html.insert(pointer, " selected");
			}
		}
		else {
			int end_value, start_value;
			start_value = html.indexOf("value=\"", pointer);
			end_tag = html.indexOf(">", pointer);
			String quote = output.contains("\"") ? "'" : "\"";

			if(start_value > 0 && start_value < end_tag) {
				end_value = html.indexOf("\"", start_value + 7 ) + 1;
				log.debug(start_value);
				log.debug(end_tag);
				log.debug(end_value);
				html.replace(start_value, end_value, "value="+quote+output+quote);
			}
			else {
				start_value = html.indexOf(">", pointer);
				html.insert(start_value, " value=" + quote + output + quote);
				log.debug("insert: (" + pointer + ")' value=\""+output+"\"'");
			}
		}

		return (html);
	}

	private void saveFieldValue(StringBuilder html, int fieldIndex) {
		String header = getFieldHeader(html, fieldIndex);
		if (StringUtils.isBlank(header)) return;

		String name = EFormUtil.removeQuotes(EFormUtil.getAttribute("name", header));
		String value = EFormUtil.removeQuotes(EFormUtil.getAttribute("value", header));
		if (StringUtils.isBlank(name)) return;

		if (header.toLowerCase().startsWith("<input ")) {
			String type = EFormUtil.removeQuotes(EFormUtil.getAttribute("type", header));
			if (StringUtils.isBlank(type)) return;

			if (type.equalsIgnoreCase("radio")) {
				String checked = EFormUtil.removeQuotes(EFormUtil.getAttribute("checked", header));
				if (StringUtils.isBlank(checked) || !checked.equalsIgnoreCase("checked")) return;
			}
		} else if (header.toLowerCase().startsWith("<select ")) {
			String selects = html.substring(fieldIndex, html.indexOf("</select>", fieldIndex));
			int pos = selects.indexOf("<option ", 0);
			while (pos >= 0) {
				String option = getFieldHeader(selects, pos);
				String selected = EFormUtil.removeQuotes(EFormUtil.getAttribute("selected", option));
				if (!StringUtils.isBlank(selected) && selected.equalsIgnoreCase("selected")) {
					value = EFormUtil.removeQuotes(EFormUtil.getAttribute("value", option));
					break;
				}
				pos = selects.indexOf("<option ", pos + 1);
			}
		} else if (header.toLowerCase().startsWith("<textarea ")) {
			int fieldEnd = html.indexOf("</textarea>", fieldIndex);
			value = html.substring(fieldIndex + header.length(), fieldEnd).trim();
			if (value.startsWith("\n")) value = value.substring(1); // remove 1st line break, UNIX style
			else if (value.startsWith("\r\n")) value = value.substring(2); // remove 1st line break, WINDOWS style
		}
		name = name.toLowerCase();
		if (!StringUtils.isBlank(value)) fieldValues.put(name, value);
	}

	private String getRefFid(String eform_name) {
		if (StringUtils.isBlank(eform_name)) return fid;

		String refFid = EFormUtil.getEFormIdByName(eform_name);
		if (StringUtils.isBlank(refFid)) refFid = fid;
		return refFid;
	}

	public void setSignatureCode(String contextPath, String userAgent, String demographicNo, String providerId) {
		String signatureRequestId = DigitalSignatureUtils.generateSignatureRequestId(providerId);
		String imageUrl = contextPath+"/imageRenderingServlet?source="+ImageRenderingServlet.Source.signature_preview.name()+"&"+DigitalSignatureUtils.SIGNATURE_REQUEST_ID_KEY+"="+signatureRequestId;
		String storedImgUrl = contextPath+"/imageRenderingServlet?source="+ImageRenderingServlet.Source.signature_stored.name()+"&digitalSignatureId=";

		StringBuilder html = new StringBuilder(this.formHtml);
		int signatureLoc = StringBuilderUtils.indexOfIgnoreCase(html, signatureMarker, 0);

		if (signatureLoc > -1) {
			String browserType = "";
			if (userAgent != null) {
				if (userAgent.toLowerCase().indexOf("ipad") > -1) {
					browserType = "IPAD";
				} else {
					browserType = "ALL";
				}
			}

			String signatureCode = "<script type='text/javascript' src='${oscar_javascript_path}../jquery/jquery-1.4.2.js'></script>" +
					"<script type='text/javascript' src='${oscar_javascript_path}signature.js'></script>" +
					"<script type='text/javascript'>\n" +
					"var _signatureRequestId = '" + signatureRequestId + "';\n" +
					"var _imageUrl = '" + imageUrl + "';\n" +
					"var _storedImgUrl = '" + storedImgUrl + "';\n" +
					"var _contextPath = '" + contextPath + "';\n" +
					"var _digitalSignatureRequestIdKey = '" + DigitalSignatureUtils.SIGNATURE_REQUEST_ID_KEY + "';\n" +
					"var _browserType = '" + browserType + "';\n" +
					"var _demographicNo = '" + demographicNo + "';\n" +
					"</script>";


			html.replace(signatureLoc, signatureLoc + signatureMarker.length(), signatureCode);
			this.formHtml = html.toString();
		}
	}
}
