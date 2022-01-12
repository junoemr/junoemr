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


package oscar;

import org.apache.commons.lang.math.NumberUtils;
import org.oscarehr.util.MiscUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This class will hold OSCAR &amp; CAISI properties. It is a singleton class. Do not instantiate it, use the method getInstance(). Every time the properties file changes, tomcat must be restarted.
 */
public class OscarProperties extends Properties {
	private static final long serialVersionUID = -5965807410049845132L;
	private static OscarProperties oscarProperties = new OscarProperties();
	private static final Set<String> activeMarkers = new HashSet<>(Arrays.asList("true", "yes", "on"));
	
	// Put property names here
	private static final String KEY_INSTANCE_TYPE = "instance_type";
	private static final String KEY_BILLING_TYPE = "billing_type";
	public static final String KEY_TEMP_DIR = "TMP_DIR";

	private static final String BILLING_TYPE_ONTARIO = "ON";
	private static final String BILLING_TYPE_BC = "BC";
	private static final String BILLING_TYPE_CLINICAID = "CLINICAID";
	
	private static final String INSTANCE_TYPE_ONTARIO = "ON";
	private static final String INSTANCE_TYPE_BC = "BC";
	private static final String INSTANCE_TYPE_ALBERTA = "AB";

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + " HH:mm:ss";

	private static final String MODULE_PROPERTY_NAME = "ModuleNames";


	public enum Module
	{
		MODULE_BORN18M("BORN18M"),
		MODULE_BORN("BORN"),
		MODULE_CAISI("Caisi"),
		MODULE_CBI("CBI"),
		MODULE_E2E("E2E"),
		MODULE_EMERALDA04("EmeraldA04"),
		MODULE_ERX("ERx"),
		MODULE_HRM("HRM"),
		MODULE_INDIVO("Indivo"),
		MODULE_JOBS("Jobs"),
		MODULE_OLIS("OLIS"),
		MODULE_ORN("ORN"),
		REST("REST"),
		MODULE_SPIRE("Spire");

		public final String moduleName;

		Module(String moduleName)
		{
			this.moduleName = moduleName;
		}
	}

	/* Do not use this constructor. Use getInstance instead */
	private OscarProperties() {
		MiscUtils.getLogger().debug("OSCAR PROPS CONSTRUCTOR");

		try {
			readFromFile("/oscar_mcmaster.properties");

			String overrideProperties = System.getProperty("oscar_override_properties");
			if (overrideProperties != null) {
				MiscUtils.getLogger().info("Applying override properties : "+overrideProperties);
				readFromFile(overrideProperties);
			}
		} catch (IOException e) {
			MiscUtils.getLogger().error("Error", e);
		}
	}

	/**
	 * @return OscarProperties the instance of OscarProperties
	 */
	public static OscarProperties getInstance() {
		return oscarProperties;
	}

	public void readFromFile(String url) throws IOException {
		InputStream is = getClass().getResourceAsStream(url);
		if (is == null) is = new FileInputStream(url);

		try {
			load(is);
		} finally {
			is.close();
		}
	}

	
	// =========================================================================
	// Methods for accessing general properties in various ways
	// TODO-legacy: make these private and access all properties through the specific
	//       methods below
	// =========================================================================
	
	/**
	 * Get the value of a property.  Trims output.
	 * 
	 * @param key The key for the property
	 * @return String
	 */
	public String getProperty(String key) {
		String property = super.getProperty(key);
		return property == null ? property : property.trim();
	}

	/**
	 * Get the uppercase value of a property.  Trims output.
	 * 
	 * @param key The key for the property
	 * @return String
	 */
	public String getPropertyUpperCase(String key) {
		String property = this.getProperty(key);
		return property == null ? property : property.toUpperCase();
	}


	/**
	 * Check to see if the properties to see if that property exists.
	 * 
	 * @param key key of property
	 * @return boolean
	 */
	public boolean hasProperty(String key) {
		boolean prop = false;
		String propertyValue = getProperty(key.trim());
		if (propertyValue != null) {
			prop = true;
		}
		return prop;
	}

	/**
	 * Will check the properties to see if that property is set and if it's 
	 * set to the given value.  If it is method returns true if not method 
	 * returns false. 
	 * 
	 * @param key key of property
	 * @param val value that will cause a true value to be returned
	 * @return boolean
	 */
	public boolean isPropertyEqual(String key, String val) {
		key = key==null ? null : key.trim();
		val = val==null ? null : val.trim();
		
		return getProperty(key, "").trim().equals(val);
	}

	/**
	 * Will check the properties to see if that property is set and if it's 
	 * set to the given value, ignoring case.  If it is method returns true if 
	 * not method returns false. 
	 * 
	 * @param key key of property
	 * @param val value that will cause a true value to be returned
	 * @return boolean
	 */
	public boolean isPropertyEqualCaseInsensitive(String key, String val) {
		key = key==null ? null : key.trim();
		val = val==null ? null : val.trim();
		
		return getProperty(key, "").trim().equalsIgnoreCase(val);
	}

	/**
	 * Will check the properties to see if that property is set and if it's set to the given value. 
	 * If it is method returns true if not method returns false. 
	 * This method returns positive response on any "true", "yes" or "on" values.
	 * 
	 * @param key key of property
	 * @param val value that will cause a true value to be returned
	 * @return boolean
	 */
	public boolean getBooleanProperty(String key, String val) {
		key = key==null ? null : key.trim();
		val = val==null ? null : val.trim();
		// if we're checking for positive value, any "active" one will do
		if (val != null && activeMarkers.contains(val.toLowerCase())) {
			return isPropertyActive(key);
		}
		
		return getProperty(key, "").trim().equalsIgnoreCase(val);
	}

	/**
	 * Gets a folder path property and ensures it contains a trailing slash
	 * @param key
	 * @return string
	 */
	public String getPathProperty(String key)
	{
		String folderPath = getProperty(key, "").trim();
		if(folderPath.length() != 0 && !folderPath.endsWith("/"))
		{
			folderPath = folderPath + "/";
		}
		return folderPath;
	}

	public Integer getIntegerProperty(String key, Integer defaultValue) {
		key = key==null ? null : key.trim();

		String propertyValue = getProperty(key, defaultValue.toString());

		return Integer.parseInt(propertyValue);
	}

	/**
	 * Will check the properties to see if that property is set and if it's set to "true", "yes" or "on".
	 * If it is method returns true if not method returns false.
	 *
	 * @param key key of property
	 * @return boolean whether the property is active
	 */
	public boolean isPropertyActive(String key) {
		key = key==null ? null : key.trim();
		return activeMarkers.contains(getProperty(key, "").trim().toLowerCase());
	}

	public long getPDFMaxMemUsage()
	{
		return NumberUtils.toLong(getProperty("PDF_MAX_MEM_USAGE"), -1);
	}

	
	// =========================================================================
	// Methods for getting specific property values
	// =========================================================================

	public String getDisplayDateFormat()
	{
		return getDisplayDateFormat(getProperty("display_date_format"), DEFAULT_DATE_FORMAT);
	}

	public String getDisplayDateTimeFormat()
	{
		return getDisplayDateFormat(getProperty("display_datetime_format"), DEFAULT_DATETIME_FORMAT);
	}

	private String getDisplayDateFormat(String preferredFormat, String defaultFormat)
	{
		String dateTimeFormat;
		try
		{
			dateTimeFormat = (preferredFormat != null)? preferredFormat : defaultFormat;
			new SimpleDateFormat(dateTimeFormat);
		}
		catch(NullPointerException | IllegalArgumentException e)
		{
			dateTimeFormat = defaultFormat;
			MiscUtils.getLogger().error("Invalid Date/Time display format", e);
		}

		return dateTimeFormat;
	}

	public Date getStartTime() {
		String str = getProperty("OSCAR_START_TIME");
		Date ret = null;
		try {
			ret = new Date(Long.parseLong(str));
		} catch (Exception e) {/* No Date Found */
		}
		return ret;
	}

	/**
	 * Get the providers to route the labs to.
	 * By default we will just return the list of providers that were requested in the lab
	 *
	 * @param defaultProviderNumbers The requested providers to route the labs to. Returned by default
	 * @return ArrayList of the providers to route the labs to
	 */
	public List<String> getRouteLabsToProviders(List<String> defaultProviderNumbers)
	{
		String property = getProperty("route_labs_to_provider", "");

		//Send all labs to the unclaimed inbox
		if (property.equals("0"))
		{
			return null;
		} else if (!property.equals("")) //Send all labs to providers listed in property
		{
			ArrayList<String> providers = new ArrayList<>(Arrays.asList(property.split(",")));
			return providers;
		} else
		{
			//Default. Send labs to requested providers
			return defaultProviderNumbers;
		}
	}

	public boolean isTorontoRFQ() {
		return isPropertyActive("TORONTO_RFQ");
	}

	public boolean isProviderNoAuto() {
		return isPropertyActive("AUTO_GENERATE_PROVIDER_NO");
	}

	public boolean isPINEncripted() {
		return isPropertyActive("IS_PIN_ENCRYPTED");
	}

	public boolean isSiteSecured() {
		return isPropertyActive("security_site_control");
	}

	public boolean isAdminOptionOn() {
		return isPropertyActive("with_admin_option");
	}

	public boolean isLogAccessClient() {
		return isPropertyActive("log_accesses_of_client");
	}

	public boolean isLogAccessProgram() {
		return isPropertyActive("log_accesses_of_program");
	}

	public boolean isAccountLockingEnabled() {
		return isPropertyActive("ENABLE_ACCOUNT_LOCKING");
	}
	
	public String getInstanceType() {
		return getProperty(KEY_INSTANCE_TYPE);
	}

	public String getInstanceTypeUpperCase() {
		return getPropertyUpperCase(KEY_INSTANCE_TYPE);
	}
	
	public boolean isOntarioInstanceType() {
		return ( INSTANCE_TYPE_ONTARIO.equalsIgnoreCase( getProperty(KEY_INSTANCE_TYPE) ) );
	}
	
	public boolean isBritishColumbiaInstanceType() {
		return ( INSTANCE_TYPE_BC.equalsIgnoreCase( getProperty(KEY_INSTANCE_TYPE) ) );
	}
	
	public boolean isAlbertaInstanceType() {
		return ( INSTANCE_TYPE_ALBERTA.equalsIgnoreCase( getProperty(KEY_INSTANCE_TYPE) ) );
	}

	public boolean isEligibilityCheckEnabled() {
		return (isBritishColumbiaInstanceType() || isOntarioInstanceType());
	}
	
	public String getBillingType() {
		return getProperty(KEY_BILLING_TYPE);
	}
	
	public String getBillingTypeUpperCase() {
		return getPropertyUpperCase(KEY_BILLING_TYPE);
	}
	
	public boolean isOntarioBillingType() {
		return ( BILLING_TYPE_ONTARIO.equalsIgnoreCase( getProperty(KEY_BILLING_TYPE) ) );
	}
	
	public boolean isBritishColumbiaBillingType() {
		return ( BILLING_TYPE_BC.equalsIgnoreCase( getProperty(KEY_BILLING_TYPE) ) );
	}
	
	public boolean isClinicaidBillingType() {
		return ( BILLING_TYPE_CLINICAID.equalsIgnoreCase( getProperty(KEY_BILLING_TYPE) ) );
	}

	public boolean isCaisiLoaded() {
		return isPropertyActive("caisi");
	}

	public String getDbType() {
		return getProperty("db_type");
	}

	public String getDbName()
	{
		return getProperty("db_name");
	}

	public String getDbUserName() {
		return getProperty("db_username");
	}

	public String getDbPassword() {
		return getProperty("db_password");
	}

	public String getDbUri() {
		return getProperty("db_uri");
	}

	public String getDbDriver() {
		return getProperty("db_driver");
	}

	public static String getBuildDate() {
		return oscarProperties.getProperty("buildDateTime");
	}

	public static String getBuildTag() {
		return oscarProperties.getProperty("buildtag");
	}

	/** for legacy faxing setup, use the outboundFaxService check */
	public boolean isFaxEnabled()
	{
		return isPropertyActive("faxEnable");
	}

	public boolean isRxSignatureEnabled() {
		return isFaxEnabled() || isPropertyActive("rx_signature_enabled");
	}
	
	public boolean isConsultationSignatureEnabled() {
		return isPropertyActive("consultation_signature_enabled");
	}

	public boolean isEFormSignatureEnabled() {
		return isPropertyActive("eform_signature_enabled");
	}
	
	public boolean isSpireClientEnabled() {
		return isPropertyActive("SPIRE_CLIENT_ENABLED");
	}
	
	public int getSpireClientRunFrequency() {
		String prop = getProperty("spire_client_run_frequency");
		return Integer.parseInt(prop);
	}
	
	public String getSpireServerUser() {
		return getProperty("spire_server_user");
	}
	
	public String getSpireServerPassword() {
		return getProperty("spire_server_password");
	}
	
	public String getSpireServerHostname() {
		return getProperty("spire_server_hostname");
	}
	
	public String getSpireDownloadDir() {
		return getProperty("spire_download_dir");
	}

	public String getHL7A04BuildDirectory() {
		return getProperty("hl7_a04_build_dir");
	}
	
	public String getHL7A04SentDirectory() {
		return getProperty("hl7_a04_sent_dir");
	}
	
	public String getHL7A04FailDirectory() {
		return getProperty("hl7_a04_fail_dir");
	}
	
	public String getHL7SendingApplication() {
		return getProperty("HL7_SENDING_APPLICATION");
	}
	
	public String getHL7SendingFacility() {
		return getProperty("HL7_SENDING_FACILITY");
	}
	
	public String getHL7ReceivingApplication() {
		return getProperty("HL7_RECEIVING_APPLICATION");
	}
	
	public String getHL7ReceivingFacility() {
		return getProperty("HL7_RECEIVING_FACILITY");
	}
	
	public boolean isHL7A04GenerationEnabled() {
		return isPropertyActive("HL7_A04_GENERATION");
	}
	
	public boolean isEmeraldHL7A04TransportTaskEnabled() {
		return isPropertyActive("EMERALD_HL7_A04_TRANSPORT_TASK");
	}
	
	public String getEmeraldHL7A04TransportAddr() {
		return getProperty("EMERALD_HL7_A04_TRANSPORT_ADDR");
	}
	
	public int getEmeraldHL7A04TransportPort() {
		String prop = getProperty("EMERALD_HL7_A04_TRANSPORT_PORT", "3987"); // default to port 3987
		return Integer.parseInt(prop);
	}

	public boolean isAppointmentIntakeFormEnabled()
	{
		return isPropertyActive("appt_intake_form");
	}

	public boolean isNewEyeformEnabled()
	{
		return isPropertyActive("new_eyeform_enabled");
	}

	public boolean isSinglePageChartEnabled()
	{
		return isPropertyActive("SINGLE_PAGE_CHART");
	}

	public boolean isAppointmentShowShortLettersEnabled()
	{
		return isPropertyActive("APPT_SHOW_SHORT_LETTERS");
	}

	public boolean isToggleReasonByProviderEnabled()
	{
		return isPropertyActive("TOGGLE_REASON_BY_PROVIDER");
	}

	public boolean isDisplayAlertsOnScheduleScreenEnabled()
	{
		return isPropertyActive("displayAlertsOnScheduleScreen");
	}

	public boolean isAppoinmtnetAlwaysShowLinksEnabled()
	{
		return isPropertyActive("APPT_ALWAYS_SHOW_LINKS");
	}

	public boolean isEditAppointmentStatusEnabled()
	{
		return isPropertyActive("ENABLE_EDIT_APPT_STATUS");
	}

	public boolean isMultisiteEnabled()
	{
		return isPropertyActive("multisites");
	}

	public boolean isMyHealthAccessEnabled()
	{
		return isPropertyActive("myhealthaccess_telehealth_enabled");
	}

	public boolean isScheduleEnabled()
	{
		return isPropertyActive("schedule.enabled");
	}

	public boolean hasHRMDocuments()
	{
		return isPropertyActive("has_hrm_documents");
	}

	public boolean isModuleEnabled(Module module)
	{
		// Get the module string from the config
		// separate it by commas
		// See if the module is included

		String moduleNames = getProperty(MODULE_PROPERTY_NAME);
		if(moduleNames == null || moduleNames.trim().length() == 0)
		{
			return false;
		}

		String[] moduleList = moduleNames.split(",");
		for (String selectedModule : moduleList)
		{
			if(module.moduleName.equals(selectedModule))
			{
				return true;
			}
		}

		return false;
	}

	public String getProjectHome()
	{
		return getProperty("project_home");
	}

	public String getCmeJs()
	{
		return getProperty("cme_js");
	}

	// Methods for Google OAuth
	public String getGoogleClientID()
	{
		return getProperty("google_client_id");
	}

	public String getGoogleResource()
	{
		return getProperty("google_resource");
	}

	// =========================================================================
	// Static methods for getting specific property values
	// =========================================================================

	public static String getIntakeProgramAccessServiceId() {
		return oscarProperties.getProperty("form_intake_program_access_service_id");
	}
	
	public static String getIntakeProgramCashServiceId() {
		return oscarProperties.getProperty("form_intake_program_cash_service_id");
	}
	
	public static String getIntakeProgramAccessFId() {
		return oscarProperties.getProperty("form_intake_program_access_fid");
	}
	
	public static String getConfidentialityStatement() {
		String result = null;
		int count = 1;
		String statement = null;
		while ((statement = oscarProperties.getProperty("confidentiality_statement.v" + count)) != null) {
			count++;
			result = statement;
		}
		return result;
	}
	
	public static String getIntakeProgramCashFId() {
		return oscarProperties.getProperty("form_intake_program_cash_fid");
	}
	
	public static boolean isLdapAuthenticationEnabled() {
		return Boolean.parseBoolean(oscarProperties.getProperty("ldap.enabled"));
	}

	public boolean isEChartAdditionalPatientInfoEnabled()
	{
		return isPropertyActive("echart_additional_patient_info");
	}

	public boolean isJunoEncounterEnabled()
	{
		return isPropertyActive("juno_encounter.enabled");
	}

	public boolean isJunoEncounterLinkToOldEncounterPageEnabled()
	{
		return isPropertyActive("juno_encounter.link_to_old_encounter_page");
	}

	public boolean isHealthcareTeamEnabled()
	{
		return isPropertyActive("DEMOGRAPHIC_PATIENT_HEALTH_CARE_TEAM");
	}

	public static Integer getNumLoadedNotes(int defaultValue) 
	{
		return oscarProperties.getIntegerProperty("num_loaded_notes", defaultValue);
	}

	public boolean isOptimizeSmallSchedulesEnabled()
	{
		return isPropertyActive("optimize_small_schedules");
	}
}
