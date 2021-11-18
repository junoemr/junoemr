/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

/*
 * OLISHL7Handler.java
 */

package oscar.oscarLab.ca.all.parsers.OLIS;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.parser.CustomModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.oscarehr.common.hl7.OLIS.model.v231.message.ERP_R09;
import org.oscarehr.olis.dao.OLISRequestNomenclatureDao;
import org.oscarehr.olis.dao.OLISResultNomenclatureDao;
import org.oscarehr.olis.model.OLISRequestNomenclature;
import org.oscarehr.olis.model.OLISResultNomenclature;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.parsers.messageTypes.ORU_R01MessageHandler;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.oscarehr.olis.service.OLISPollingService.OLIS_DATE_FORMAT;

/**
 * @author Adam Balanga
 */
public class OLISHL7Handler extends ORU_R01MessageHandler
{
	public static final String OLIS_MESSAGE_TYPE = "OLIS_HL7";
	public static final String TEXT_HIGHLIGHT_COLOUR = "#767676";

	protected static final String OLIS_SENDING_APPLICATION_ID = "OLIS";
	protected static final String OLIS_SENDING_APPLICATION_ID_TYPE = "X500";

	protected static final OLISRequestNomenclatureDao requestNomenclatureDao = SpringUtils.getBean(OLISRequestNomenclatureDao.class);
	protected static final OLISResultNomenclatureDao resultNomenclatureDao = SpringUtils.getBean(OLISResultNomenclatureDao.class);

	private static final Logger logger = Logger.getLogger(OLISHL7Handler.class);
	private static final String finalStatus = "CFEX";

	protected ERP_R09 msg = null;

	protected boolean isFinal = true;
	protected boolean isCorrected = false;
	protected boolean reportBlocked = false;
	protected boolean reportInvalidated = false;
	protected boolean reportIsFullReplacement = false;

	private boolean centered = false;
	private HashMap<String, String> sourceOrganizations;

	private HashMap<String, String> defaultSourceOrganizations;

	private List<OLISRequestSortKey> obrSortKeys;
	private List<List<OLISResultSortKey>> obxSortKeyArray;

	private HashMap<String, String[]> patientIdentifiers;
	private HashMap<String, String> patientIdentifierNames;

	private HashMap<String, String> addressTypeNames;
	private HashMap<String, String> telecomUseCode;
	private HashMap<String, String> telecomEquipType;
	private HashMap<Integer, Integer> obrParentMap;

	private ArrayList<String> disciplines;
	private List<OLISError> errors;
	private ArrayList<String> headers;

	private ArrayList<HashMap<String, String>> patientHomeTelecom;
	private ArrayList<HashMap<String, String>> patientWorkTelecom;
	private ArrayList<HashMap<String, String>> patientAddresses;

	public static boolean handlerTypeMatch(Message message)
	{
		String version = message.getVersion();
		if (version.equals("2.3.1"))
		{
			ERP_R09 msh = (ERP_R09) message;
			MSH messageHeaderSegment = msh.getMSH();

			String sendingApplicationId = messageHeaderSegment.getMsh3_SendingApplication().getHd2_UniversalID().getValue();
			String sendingApplicationIdType = messageHeaderSegment.getMsh3_SendingApplication().getHd3_UniversalIDType().getValue();

			return OLIS_SENDING_APPLICATION_ID.equalsIgnoreCase(sendingApplicationId) &&
				OLIS_SENDING_APPLICATION_ID_TYPE.equalsIgnoreCase(sendingApplicationIdType);
		}
		return false;
	}

	private void initDefaultSourceOrganizations()
	{
		defaultSourceOrganizations = new HashMap<>();
		defaultSourceOrganizations.put("4001", "BSD Lab1");
		defaultSourceOrganizations.put("4002", "BSD Lab2");
		defaultSourceOrganizations.put("4003", "BSD Lab3");
		defaultSourceOrganizations.put("4004", "BSD Lab4");
		defaultSourceOrganizations.put("4005", "BSD Lab5");
		defaultSourceOrganizations.put("4006", "BSD Lab6");
		defaultSourceOrganizations.put("4007", "BSD Lab7");
		defaultSourceOrganizations.put("4008", "BSD Lab8");
		defaultSourceOrganizations.put("4009", "BSD Lab9");
		defaultSourceOrganizations.put("4010", "BSD Lab10");
	}

	@Override
	public String preUpload(String hl7Message) throws HL7Exception
	{
		return hl7Message;
	}

	@Override
	public boolean canUpload()
	{
		return "OK".equalsIgnoreCase(getAckStatus());
	}

	public String getAckStatus()
	{
		String queryAckStatus = null;
		try
		{
			queryAckStatus = terser.get("/.QAK-2");
		}
		catch(HL7Exception e)
		{
			logger.error("Terser Error", e);
		}
		return queryAckStatus;
	}

	@Override
	public void postUpload() {}

	public String getSourceOrganization(String org)
	{
		return sourceOrganizations.containsKey(org) ? sourceOrganizations.get(org) : defaultSourceOrganizations.get(org);
	}

	public String getObrStatus(int rep)
	{
		return getString(get("/.ORDER_OBSERVATION(" + rep + ")/OBR-25-1-1"));
	}

	public String getObrStatusDisplayValue(int index)
	{
		return getObrTestResultStatusValue(getObrStatus(index));
	}

	public String getObrStatusDisplayMessage(int index)
	{
		return getObrTestResultStatusMessage(getObrStatus(index));
	}

	public String getObrSpecimenSource(int obrRep)
	{
		String specimenSource = getString(get("/.ORDER_OBSERVATION(" + obrRep + ")/OBR-15-1-2"));
		String specimenSiteModifier = getString(get("/.ORDER_OBSERVATION(" + obrRep + ")/OBR-15-5-2"));
		return StringUtils.trimToEmpty(specimenSource + " " + specimenSiteModifier);
	}

	/** Creates a new instance of OLISHL7Handler */
	public OLISHL7Handler() throws HL7Exception
	{
		super();
	}

	public OLISHL7Handler(String hl7message) throws HL7Exception
	{
		HapiContext context = new DefaultHapiContext();
		context.setValidationContext(new NoValidation());
		ModelClassFactory modelClassFactory = new CustomModelClassFactory(ERP_R09.ROOT_PACKAGE);
		context.setModelClassFactory(modelClassFactory);
		context.getParserConfiguration().setDefaultObx2Type("ST");

		Parser p = context.getPipeParser();
		this.message = p.parse(hl7message);
		this.msg = (ERP_R09) this.message;
		this.terser = new Terser(msg);
		init(hl7message);
	}

	public OLISHL7Handler(Message message) throws HL7Exception
	{
		super(message);
		this.msg = (ERP_R09) this.message;
		init(null);
	}

	String[] getDentistLicenceNumber() {
		return patientIdentifiers.get("DDSL");
	}

	String[] getDriversLicenceNumber() {
		return patientIdentifiers.get("DL");
	}

	String[] getJurisdictionalHealthNumber() {
		return patientIdentifiers.get("JHN");
	}

	String[] getPhysicianLicenceNumber() {
		return patientIdentifiers.get("MDL");
	}

	String[] getMidwifeLicenceNumber() {
		return patientIdentifiers.get("ML");
	}

	String[] getMedicalRecordNumber() {
		return patientIdentifiers.get("MR");
	}

	String[] getNursePractitionerLicenceNumber() {
		return patientIdentifiers.get("NPL");
	}

	String[] getPassportNumber() {
		return patientIdentifiers.get("PPN");
	}

	String[] getUSASocialSecurityNumber() {
		return patientIdentifiers.get("SS");
	}

	public String[] getPatientIdentifier(String ident)
	{
		return patientIdentifiers.get(ident);
	}

	public Set<String> getPatientIdentifiers()
	{
		return patientIdentifiers.keySet();
	}

	public String getNameOfIdentifier(String ident)
	{
		return patientIdentifierNames.get(ident);
	}

	private void initPatientIdentifierNames()
	{
		patientIdentifierNames = new HashMap<>();
		patientIdentifierNames.put("ANON", "Non Nominal Identifier");
		patientIdentifierNames.put("DDSL", "Dentist Licence Number");
		patientIdentifierNames.put("DL", "Driver's Licence Number");
		patientIdentifierNames.put("JHN", "Jurisdictional Health Number");
		patientIdentifierNames.put("MDL", "Physician Licence Number");
		patientIdentifierNames.put("ML", "Midwife Licence Number");
		patientIdentifierNames.put("MR", "Medical Record Number");
		patientIdentifierNames.put("NPL", "Nurse Practitioner Licence Number");
		patientIdentifierNames.put("PPN", "Passport Number");
		patientIdentifierNames.put("SS", "USA Social Security number");

	}

	public String getAddressTypeName(String ident)
	{
		return addressTypeNames.get(ident);
	}

	private void initAddressTypeNames()
	{
		addressTypeNames = new HashMap<>();
		addressTypeNames.put("M", "Mailing Address");
		addressTypeNames.put("B", "Business");
		addressTypeNames.put("O", "Office");
		addressTypeNames.put("H", "Home Address");
		addressTypeNames.put("E", "Emergency Contact");
	}

	private void initTelecomUseCodes()
	{
		telecomUseCode = new HashMap<>();
		telecomUseCode.put("PRN", "Primary Residence Number");
		telecomUseCode.put("ORN", "Other Residence Number");
		telecomUseCode.put("WPN", "Work Number");
		telecomUseCode.put("VHN", "Vacation Home Number");
		telecomUseCode.put("ASN", "Answering Service Number");
		telecomUseCode.put("EMR", "Emergency Number");
		telecomUseCode.put("NET", "Network (email) Address");
	}

	private void initTelecomEquipTypes()
	{
		telecomEquipType = new HashMap<>();
		telecomEquipType.put("PH", "Telephone");
		telecomEquipType.put("FX", "Fax");
		telecomEquipType.put("CP", "Cellular Phone");
		telecomEquipType.put("BP", "Beeper");
		telecomEquipType.put("Internet", "Internet Address");
	}

	public ArrayList<HashMap<String, String>> getPatientAddresses()
	{
		return patientAddresses;
	}

	public ArrayList<HashMap<String, String>> getPatientHomeTelecom()
	{
		return patientHomeTelecom;
	}

	public ArrayList<HashMap<String, String>> getPatientWorkTelecom()
	{
		return patientWorkTelecom;
	}

	public String getAdmittingProviderName() {
		try {
			return getFullDocName("/.PV1-17-");
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return "";
		}
	}

	public String getAdmittingProviderNameShort() {
		try {
			return getShortName("/.PV1-17-");
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return "";
		}
	}

	public String getAttendingProviderName() {
		try {
			return getFullDocName("/.PV1-7-");
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return "";
		}
	}

	public String getOrderingProviderFullName()
	{
		try
		{
			return getFullDocName("/.OBR-16-");
		}
		catch(Exception e)
		{
			return ("");
		}
	}

	public boolean isReportBlocked()
	{
		return reportBlocked;
	}

	public boolean isOBRBlocked(int obr)
	{
		String indicator = get("/.ORDER_OBSERVATION(" + obr + ")/ZBR-1-1-1");
		return "Y".equals(indicator);
	}

	public boolean hasBlockedTest()
	{
		boolean isBlocked = false;
		for(int i = 0; i < getOBRCount(); i++)
		{
			if(isOBRBlocked(i))
			{
				isBlocked = true;
				break;
			}
		}
		return isBlocked;
	}

	public String getOBRPerformingFacilityName(int obr)
	{
		try
		{
			String ident = "";
			String key = getString(get("/.ORDER_OBSERVATION(" + obr + ")/ZBR-6-6-2"));
			if (key != null && key.indexOf(":") > 0) {
				ident = key.substring(0, key.indexOf(":"));
				ident = getOrganizationType(ident);
				key = key.substring(key.indexOf(":") + 1);
			}
			if (key == null || "".equals(key.trim())) {
				return "";
			}

			String value = getString(get("/.ORDER_OBSERVATION(" + obr + ")/ZBR-6-1-1"));
			return String.format("%s (%s %s)", value, ident, key);

		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}

	public HashMap<String, String> getPerformingFacilityAddress(int obr)
	{
		try
		{
			String value = "";
			Segment zbr = terser.getSegment("/.ORDER_OBSERVATION(" + obr + ")/ZBR");

			HashMap<String, String> address;

			String identifier = getString(Terser.get(zbr, 7, 0, 7, 1));
			if ("".equals(identifier)) {
				return null;
			}
			address = new HashMap<String, String>();
			value = getString(Terser.get(zbr, 7, 0, 1, 1));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Street Address", value);
			}
			value = getString(Terser.get(zbr, 7, 0, 2, 1));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Other Designation", value);
			}
			value = getString(Terser.get(zbr, 7, 0, 3, 1));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("City", value);
			}
			value = getString(Terser.get(zbr, 7, 0, 4, 1));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Province", value);
			}
			value = getString(Terser.get(zbr, 7, 0, 5, 1));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Postal Code", value);
			}
			value = getString(Terser.get(zbr, 7, 0, 6, 1));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Country", value);
			}
			address.put("Address Type", addressTypeNames.get(identifier));
			return address;

		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return null;
	}

	public String getCategoryList() {
		String result = "";
		ArrayList<String> categories = new ArrayList<String>();
		for (int i = 0; i < getOBRCount(); i++) {
			categories.add(getOBRCategory(i));
		}
		String[] uniqueCategories = new HashSet<String>(categories).toArray(new String[0]);
		Arrays.sort(uniqueCategories);
		int count = 0;
		for (String category : uniqueCategories) {
			result += (count++ > 0 ? " / " : "") + category;
		}
		return result;
	}

	public String getTestList() {
		String result = "";
		String[] uniqueTests = new HashSet<String>(headers).toArray(new String[0]);
		Arrays.sort(uniqueTests);
		int count = 0;
		for (String test : uniqueTests) {
			result += (count++ > 0 ? " / " : "") + test;
		}
		return result;
	}
	
	
	/*
	Return the sending lab in the format of 2.16.840.1.113883.3.59.1:9999 where 9999 is the lab identifier
	
	 5047 Canadian Medical Laboratories
	 5552 Gamma Dynacare
	 5687 LifeLabs
	 5254 Alpha Laboratories
	 */
	public String getPlacerGroupNumber(){
		try {
			String value = getString(terser.get("/.ORC-4-3"));
			return value;
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return null;
	}

	public String getPerformingFacilityNameOnly() {
		try {
			String value = getString(terser.get("/.ZBR-6-1"));
			return value;
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}
	
	public String getPerformingFacilityName() {
		try {
			String key = "", value = "", ident = "";
			key = getString(terser.get("/.ZBR-6-6-2"));
			if (key != null && key.indexOf(":") > 0) {
				ident = key.substring(0, key.indexOf(":"));
				ident = getOrganizationType(ident);
				key = key.substring(key.indexOf(":") + 1);
			} else {
				key = "";
			}
			if (key == null || key.trim().equals("")) {
				return "";
			}
			value = getString(terser.get("/.ZBR-6-1"));

			return String.format("%s (%s %s)", value, ident, key);
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}

	public HashMap<String, String> getPerformingFacilityAddress() {
		try {
			String value;
			HashMap<String, String> address;
			String identifier = getString(terser.get("/.ZBR-7-7"));
			if ("".equals(identifier)) {
				return null;
			}
			address = new HashMap<String, String>();
			value = getString(terser.get("/.ZBR-7-1"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Street Address", value);
			}
			value = getString(terser.get("/.ZBR-7-2"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Other Designation", value);
			}
			value = getString(terser.get("/.ZBR-7-3"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("City", value);
			}
			value = getString(terser.get("/.ZBR-7-4"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Province", value);
			}
			value = getString(terser.get("/.ZBR-7-5"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Postal Code", value);
			}
			value = getString(terser.get("/.ZBR-7-6"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Country", value);
			}
			address.put("Address Type", addressTypeNames.get(identifier));
			return address;
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return null;
		}
	}

	public String getReportingFacilityName() {
		try {
			String key = "", value = "", ident = "";
			key = getString(terser.get("/.ZBR-4-6-2"));
			if (key != null && key.indexOf(":") > 0) {
				ident = key.substring(0, key.indexOf(":"));
				ident = getOrganizationType(ident);
				key = key.substring(key.indexOf(":") + 1);
			} else {
				key = "";
			}
			if (key == null || key.trim().equals("")) {
				return "";
			}
			value = getString(terser.get("/.ZBR-4-1"));
			return String.format("%s (%s %s)", value, ident, key);
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}

	public HashMap<String, String> getReportingFacilityAddress() {
		try {
			String value;
			HashMap<String, String> address;
			String identifier = getString(terser.get("/.ZBR-5-7"));
			if ("".equals(identifier)) {
				return null;
			}
			address = new HashMap<String, String>();
			value = getString(terser.get("/.ZBR-5-1"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Street Address", value);
			}
			value = getString(terser.get("/.ZBR-5-2"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Other Designation", value);
			}
			value = getString(terser.get("/.ZBR-5-3"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("City", value);
			}
			value = getString(terser.get("/.ZBR-5-4"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Province", value);
			}
			value = getString(terser.get("/.ZBR-5-5"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Postal Code", value);
			}
			value = getString(terser.get("/.ZBR-5-6"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Country", value);
			}
			address.put("Address Type", addressTypeNames.get(identifier));
			return address;
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return null;
		}
	}

	public String getOrderingFacilityName() {
		try {
			return (getString(terser.get("/.ORC-21-1")));
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return "";
		}
	}

	public HashMap<String, String> getOrderingFacilityAddress() {
		try {
			String value;
			HashMap<String, String> address;
			String identifier = getString(terser.get("/.ORC-22-7"));
			if ("".equals(identifier)) {
				return null;
			}
			address = new HashMap<String, String>();
			value = getString(terser.get("/.ORC-22-1"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Street Address", value);
			}
			value = getString(terser.get("/.ORC-22-2"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Other Designation", value);
			}
			value = getString(terser.get("/.ORC-22-3"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("City", value);
			}
			value = getString(terser.get("/.ORC-22-4"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Province", value);
			}
			value = getString(terser.get("/.ORC-22-5"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Postal Code", value);
			}
			value = getString(terser.get("/.ORC-22-6"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Country", value);
			}
			address.put("Address Type", addressTypeNames.get(identifier));
			return address;
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return null;
		}
	}

	public HashMap<String, String> getOrderingProviderAddress() {
		try {
			String value;
			HashMap<String, String> address;
			String identifier = getString(terser.get("/.ORC-24-7"));
			if ("".equals(identifier)) {
				return null;
			}
			address = new HashMap<String, String>();
			value = getString(terser.get("/.ORC-24-1"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Street Address", value);
			}
			value = getString(terser.get("/.ORC-24-2"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Other Designation", value);
			}
			value = getString(terser.get("/.ORC-24-3"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("City", value);
			}
			value = getString(terser.get("/.ORC-24-4"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Province", value);
			}
			value = getString(terser.get("/.ORC-24-5"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Postal Code", value);
			}
			value = getString(terser.get("/.ORC-24-6"));
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Country", value);
			}
			address.put("Address Type", addressTypeNames.get(identifier));
			return address;
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return null;
		}
	}

	private boolean stringIsNotNullOrEmpty(String value) {
		return value != null && value.trim().length() > 0;
	}

	public ArrayList<HashMap<String, String>> getOrderingProviderPhones() {
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		try {
			int rep = -1;

			String value;
			HashMap<String, String> telecom;
			String identifier;
			while (!"".equals((identifier = getString(terser.get("/.OBR-17(" + (++rep) + ")-2"))))) {
				telecom = new HashMap<String, String>();
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-1"));
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("phoneNumber", value);
				}
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-3"));
				if (stringIsNotNullOrEmpty(value)) {
					value = telecomEquipType.get(value);
					if (stringIsNotNullOrEmpty(value)) {
						telecom.put("equipType", value);
					}
				}
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-4"));
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("email", value);
				}
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-5"));
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("countryCode", value);
				}
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-6"));
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("areaCode", value);
				}
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-7"));
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("localNumber", value);
				}
				value = getString(terser.get("/.OBR-17(" + (rep) + ")-8"));
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("extension", value);
				}
				telecom.put("useCode", telecomUseCode.get(identifier));
				results.add(telecom);
			}

			return results;
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return null;
		}
	}

	public String getSpecimenReceivedDateTime() {
		try {
			String date = getString(terser.get("/.OBR-14-1"));
			if (date.length() > 13) {
				return formatDateTime(date);
			}
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}

	public String getOrderDate() {
		try {
			return (formatDate(getString(terser.get("/.OBR-27-4")).substring(0, 8)));
		} catch (HL7Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return "";
		}
	}


	public String getLastUpdateInOLISUnformatted()
	{
		int obrCount = getOBRCount();
		return getString(get("/.ORDER_OBSERVATION(" + (obrCount-1) + ")/OBR-22-1-1"));
	}

	public String getLastUpdateInOLIS()
	{
		String date = getLastUpdateInOLISUnformatted();
		if(date.length() > 0) return formatDateTime(date);
		return "";
	}

	public String getOBXCEParentId(int obr, int obx)
	{
		return getOBXField(obr, obx, 4, 0, 1);
	}

	/**
	 * @param setId parent set Id - 1 indexed (matches hl7 id value)
	 * @return mapped child id, 0 indexed
	 */
	public int getChildOBR(String setId)
	{
		try
		{
			int parentIndex = Integer.parseInt(setId);
			return obrParentMap.get(parentIndex);
		}
		catch(Exception e)
		{
			logger.error("Invalid obr child lookup", e);
			return -1;
		}
	}

	/**
	 * @param obr segment index - 0 indexed
	 * @return true if a mapped value exists
	 */
	public boolean isChildOBR(int obr)
	{
		return obrParentMap.containsValue(obr);
	}

	public List<String> getDiagnosis(int obr)
	{
		try
		{
			return msg.getRESPONSE().getORDER_OBSERVATION(obr).getDG1All().stream()
					.map((dg1) -> dg1.getDg13_DiagnosisCodeDG1().getCe2_Text().getValue())
					.collect(Collectors.toList());
		}
		catch(HL7Exception e)
		{
			logger.error("hl7 lookup error", e);
		}
		return new ArrayList<>(0);
	}

	public int getMappedOBR(int obr)
	{
		return obrSortKeys.get(obr).getOriginalIndex();
	}

	public int getMappedOBX(int obr, int obx)
	{
		return obxSortKeyArray.get(obr).get(obx).getOriginalIndex();
	}

	public ArrayList<String> getDisciplines() {
		return disciplines;
	}

	public List<OLISError> getReportErrors() {
		List<OLISError> result = new ArrayList<OLISError>();
		if (errors == null) {
			return result;
		}
		for(OLISError error : errors)
		{
			String segment = error.getSegment();
			if(segment == null || segment.equals("") || segment.equals("ERR") || segment.equals("SPR"))
			{
				result.add(error);
			}
		}
		return result;
	}

	@Override
	public void init(String hl7Body) throws HL7Exception
	{
		initDefaultSourceOrganizations();
		initPatientIdentifierNames();
		initAddressTypeNames();
		initTelecomUseCodes();
		initTelecomEquipTypes();

		obrParentMap = new HashMap<>();
		sourceOrganizations = new HashMap<>();
		disciplines = new ArrayList<>();

		headers = new ArrayList<>();
		int obrCount = getOBRCount();
		parseZPDSegment();
		parseERRSegment();

		// We only need to parse a few segments if there are no OBRs.
		if (obrCount == 0)
		{
			return;
		}

		parsePIDSegment(terser.getSegment("/.PID"));

		obrSortKeys = mapOBRSortKeys();
		obxSortKeyArray = new ArrayList<>(obrCount);

		for(int i = 0; i < obrCount; i++)
		{
			headers.add(getOBRName(i));
			parseZBRSegment(terser.getSegment("/.ORDER_OBSERVATION(" + i + ")/ZBR"));

			for(int j = 0; j < getOBXCount(i); j++)
			{
				String resultStatus = getOBXResultStatus(i, j);
				if("W".equals(resultStatus))
				{
					reportInvalidated = true;
				}
			}

			String status = getObrStatus(i);
			isFinal &= isStatusFinal(status);
			isCorrected |= status.equals("C");

			String parentSetId = getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-26-2-1"));
			if(StringUtils.isNotBlank(parentSetId))
			{
				// key is 1 indexed to match the lab data setId as is, value is 0 indexed like other obr lookups
				obrParentMap.put(Integer.parseInt(parentSetId), i);
			}

			obxSortKeyArray.add(mapOBXSortKey(i));
			disciplines.add(getOBRCategory(i));
		}
	}

	private void parseZBRSegment(Segment zbr)
	{
		try {
			String key = "", value = "";
			int[] indexes = { 2, 3, 4, 6, 8 };
			for (int index : indexes) {
				if (getString(Terser.get(zbr, index, 0, 6, 2)).equals("")) {
					continue;
				}
				key = getString(Terser.get(zbr, index, 0, 6, 2));
				if (key != null && key.indexOf(":") > 0) {
					key = key.substring(key.indexOf(":") + 1);
				}
				value = getString(Terser.get(zbr, index, 0, 1, 1));
				sourceOrganizations.put(key, value);
			}
			String clinicInfo = getString(Terser.get(zbr, 13, 0, 1, 1));
			if("Y".equals(clinicInfo))
			{
				reportIsFullReplacement = true;
			}

		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
	}

	private void parsePIDSegment(Segment pid) throws HL7Exception
	{
		int rep = -1;
		String identifier = "";
		String value = "";
		String attrib = "";

		patientIdentifiers = new HashMap<String, String[]>();
		while ((identifier = Terser.get(pid, 3, ++rep, 5, 1)) != null) {

			value = Terser.get(pid, 3, rep, 1, 1);

			attrib = Terser.get(pid, 3, rep, 4, 2);
			if (attrib != null) {
				attrib = attrib.substring(attrib.indexOf(":") + 1);
			}

			patientIdentifiers.put(identifier, new String[] { value, attrib });

		}
		patientAddresses = new ArrayList<HashMap<String, String>>();
		rep = -1;
		HashMap<String, String> address;
		while ((identifier = Terser.get(pid, 11, ++rep, 7, 1)) != null) {
			address = new HashMap<String, String>();
			value = Terser.get(pid, 11, rep, 1, 1);
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Street Address", value);
			}
			value = Terser.get(pid, 11, rep, 2, 1);
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Other Designation", value);
			}
			value = Terser.get(pid, 11, rep, 3, 1);
			if (stringIsNotNullOrEmpty(value)) {
				address.put("City", value);
			}
			value = Terser.get(pid, 11, rep, 4, 1);
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Province", value);
			}
			value = Terser.get(pid, 11, rep, 5, 1);
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Postal Code", value);
			}
			value = Terser.get(pid, 11, rep, 6, 1);
			if (stringIsNotNullOrEmpty(value)) {
				address.put("Country", value);
			}
			address.put("Address Type", addressTypeNames.get(identifier));
			patientAddresses.add(address);
		}

		patientHomeTelecom = new ArrayList<HashMap<String, String>>();
		rep = -1;
		HashMap<String, String> telecom;
		while ((identifier = Terser.get(pid, 13, ++rep, 2, 1)) != null) {
			telecom = new HashMap<String, String>();
			value = Terser.get(pid, 13, rep, 1, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("phoneNumber", value);
			}
			value = Terser.get(pid, 13, rep, 3, 1);
			if (stringIsNotNullOrEmpty(value)) {
				value = telecomEquipType.get(value);
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("equipType", value);
				}
			}
			value = Terser.get(pid, 13, rep, 4, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("email", value);
			}
			value = Terser.get(pid, 13, rep, 5, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("countryCode", value);
			}
			value = Terser.get(pid, 13, rep, 6, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("areaCode", value);
			}
			value = Terser.get(pid, 13, rep, 7, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("localNumber", value);
			}
			value = Terser.get(pid, 13, rep, 8, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("extension", value);
			}
			telecom.put("useCode", telecomUseCode.get(identifier));
			patientHomeTelecom.add(telecom);
		}

		patientWorkTelecom = new ArrayList<HashMap<String, String>>();
		rep = -1;
		while ((identifier = Terser.get(pid, 14, ++rep, 2, 1)) != null) {
			telecom = new HashMap<String, String>();
			value = Terser.get(pid, 14, rep, 1, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("phoneNumber", value);
			}
			value = Terser.get(pid, 14, rep, 3, 1);
			if (stringIsNotNullOrEmpty(value)) {
				value = telecomEquipType.get(value);
				if (stringIsNotNullOrEmpty(value)) {
					telecom.put("equipType", value);
				}
			}
			value = Terser.get(pid, 14, rep, 4, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("email", value);
			}
			value = Terser.get(pid, 14, rep, 5, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("countryCode", value);
			}
			value = Terser.get(pid, 14, rep, 6, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("areaCode", value);
			}
			value = Terser.get(pid, 14, rep, 7, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("localNumber", value);
			}
			value = Terser.get(pid, 14, rep, 8, 1);
			if (stringIsNotNullOrEmpty(value)) {
				telecom.put("extension", value);
			}
			telecom.put("useCode", telecomUseCode.get(identifier));
			patientWorkTelecom.add(telecom);
		}
	}

	private void parseZPDSegment() throws HL7Exception
	{
		Segment zpd = terser.getSegment("/.ZPD");
		boolean rb = "Y".equals(oscar.Misc.getStr(Terser.get(zpd, 3, 0, 1, 1), ""));
		if(!reportBlocked && rb)
		{
			reportBlocked = true;
		}
	}

	private void parseERRSegment() throws HL7Exception
	{
		Segment err = terser.getSegment("/.ERR");
		errors = new ArrayList<>();
		String segment, sequence, field, identifier, text;
		int rep = -1;
		while ((identifier = Terser.get(err, 1, ++rep, 4, 1)) != null) {
			if (identifier.trim().equals("320")) {
				reportBlocked = true;
			}
			segment = Terser.get(err, 1, rep, 1, 1);
			sequence = Terser.get(err, 1, rep, 1, 2);
			field = Terser.get(err, 1, rep, 1, 3);
			text = Terser.get(err, 1, rep, 4, 2);
			errors.add(new OLISError(this, segment, sequence, field, identifier, text));
		}
	}

	/**
	 * @return ordered list of obr sort keys
	 * @throws HL7Exception if hl7 error occurs
	 */
	protected List<OLISRequestSortKey> mapOBRSortKeys() throws HL7Exception
	{
		int obrCount = getOBRCount();
		List<OLISRequestSortKey> obrKeys = new ArrayList<>(obrCount);

		Segment zbr;
		for(int obrRep = 0; obrRep < obrCount; obrRep++)
		{
			zbr = terser.getSegment("/.ORDER_OBSERVATION(" + obrRep + ")/ZBR");

			String collectionDateStr = StringUtils.trimToNull(get("/.ORDER_OBSERVATION(" + obrRep + ")/OBR-7"));
			String placerGroupNo = this.getPlacerGroupNumber();
			String requestSortKey = getString(Terser.get(zbr, 11, 0, 1, 1));

			String obxCategory = StringUtils.trimToNull(get("/.ORDER_OBSERVATION(" + obrRep + ")/OBR-4-1-1"));
			String olisSortKey = null;
			String olisAlternateName1 = null;
			if(obxCategory != null)
			{
				OLISRequestNomenclature olisRequestNomenclature = requestNomenclatureDao.findByNameId(obxCategory);
				if(olisRequestNomenclature != null)
				{
					olisSortKey = olisRequestNomenclature.getSortKey();
					olisAlternateName1 = olisRequestNomenclature.getAltName1();
				}
			}

			OLISRequestSortKey key = new OLISRequestSortKey(
					ConversionUtils.toNullableZonedDateTime(collectionDateStr, DateTimeFormatter.ofPattern(OLIS_DATE_FORMAT)),
					placerGroupNo,
					requestSortKey,
					olisSortKey,
					olisAlternateName1,
					obrRep);

			key.setOriginalIndex(obrRep);
			obrKeys.add(key);
		}
		Collections.sort(obrKeys);
		return obrKeys;
	}

	/**
	 * @param obrRep the obr rep
	 * @return ordered list of obx sort keys
	 * @throws HL7Exception if hl7 error occurs
	 */
	protected List<OLISResultSortKey> mapOBXSortKey(int obrRep) throws HL7Exception
	{
		int obxCount = getOBXCount(obrRep);
		List<OLISResultSortKey> obxKeys = new ArrayList<>(obxCount);
		for(int obxRep = 0; obxRep < obxCount; obxRep++)
		{
			String msgKey = null;
			String subId = getOBXField(obrRep, obxRep, 4, 0, 1);
			ZonedDateTime zbxDate = null;

			String obxName = StringUtils.trimToNull(getOBXField(obrRep, obxRep, 3, 0, 1));
			String olisSortKey = null;
			String olisAlternateName1 = null;
			if(obxName != null)
			{
				OLISResultNomenclature olisResultNomenclature = resultNomenclatureDao.findByNameId(obxName);
				if(olisResultNomenclature != null)
				{
					olisSortKey = olisResultNomenclature.getSortKey();
					olisAlternateName1 = olisResultNomenclature.getAltName1();
				}
			}

			Segment zbxSegment = terser.getSegment("/.ORDER_OBSERVATION(" + obrRep + ")/OBSERVATION(" + obxRep + ")/ZBX");
			if(zbxSegment != null)
			{
				String zbxDateStr = Terser.get(zbxSegment, 1, 0, 1, 1);
				msgKey = Terser.get(zbxSegment, 2, 0, 1, 1);
				zbxDate = ConversionUtils.toNullableZonedDateTime(zbxDateStr, DateTimeFormatter.ofPattern(OLIS_DATE_FORMAT));
			}
			OLISResultSortKey key = new OLISResultSortKey(
					getOBXResultStatus(obrRep, obxRep),
					msgKey,
					olisSortKey,
					olisAlternateName1,
					subId,
					zbxDate);

			key.setOriginalIndex(obxRep);
			obxKeys.add(key);
		}
		Collections.sort(obxKeys);
		return obxKeys;
	}

	public boolean isStatusFinal(String status)
	{
		return finalStatus.contains(status);
	}

	public String getNatureOfAbnormalTest(int obr, int obx)
	{
		String nature = getString(getOBXField(obr, obx, 10, 0, 1));
		return stringIsNotNullOrEmpty(nature) ? getNatureOfAbnormalTest(nature) : "";
	}

	public String getNatureOfAbnormalTest(String nature)
	{
		switch(nature)
		{
			case "A":
				return "An age-based population";
			case "N":
				return "None ‚Äì generic normal range";
			case "R":
				return "A race-based population";
			case "S":
				return "A sex-based population";
			default:
				return "";
		}
	}

	public static String getObxTestResultStatusValue(String status)
	{
		switch(status)
		{
			case "C":
				return "Amended";
			case "F":
				return "Final";
			case "P":
				return "Preliminary";
			case "X":
				return "Could not obtain results";
			case "W":
				return "Invalid";
			case "Z":
				return "Ancillary information";
			case "N":
				return "Not performed";
			default:
				return "";
		}
	}

	public static String getObrTestResultStatusValue(String status)
	{
		switch (status)
		{
			case "A":
				return "Partial";
			case "C":
				return "Amended";
			case "E":
				return "Expired";
			case "F":
				return "Final";
			case "I":
				return "Incomplete";
			case "O":
				return "Ordered";
			case "P":
				return "Preliminary";
			case "X":
				return "Canceled";
			default:
				return "";
		}
	}

	public String getObrTestResultStatusMessage(String status)
	{
		switch (status)
		{
			case "A":
				return "Some, but not all, results available";
			case "C":
				return "Correction to results";
			case "E":
				return "OLIS has expired the test request because no activity has occurred within a reasonable amount of time.";
			case "F":
				return "Final results; results stored and verified. Can only be changed with a corrected result.";
			case "I":
				return "No results available; specimen received, procedure incomplete.";
			case "O":
				return "Order received; specimen not yet received. ";
			case "P":
				return "Preliminary: A verified early result is available, final results not yet obtained.";
			case "X":
				return "No results available; Order canceled";
			default:
				return "";
		}
	}

	@Override
	public String getOrderStatusDisplayValue()
	{
		if(reportIsFullReplacement)
		{
			return "Full Replace Amendment";
		}
		else if (reportInvalidated)
		{
			return "Amended/Invalidation";
		}
		return super.getOrderStatusDisplayValue();
	}

	public String getPointOfCare(int i)
	{
		return getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-30-1-1"));
	}

	public String getPointOfCareMessage(int i)
	{
		String pointOfCare = getPointOfCare(i);
		String message = null;

		if(StringUtils.isNotBlank(pointOfCare))
		{
			if("PORT".equalsIgnoreCase(pointOfCare))
			{
				message = "Test performed at point of care";
			}
			else
			{
				message = "Test performed at patient location";
			}
		}
		return message;
	}

	@Override
	public String getMsgType()
	{
		return (OLIS_MESSAGE_TYPE);
	}

	@Override
	public String getMsgDate()
	{
		String dateString;

		// labs with status O do not have obr-7 filled. all others should
		if("O".equals(getObrStatus(0)))
		{
			// TODO figure out the correct date to use in this case
			dateString = get("/.ORC-9");
		}
		else
		{
			dateString = get("/.OBR(0)-7-1");
		}

		// handle date or datetime formatting
		if(dateString.length() == 8)
		{
			return formatDate(dateString);
		}
		else
		{
			return formatDateTime(dateString).substring(0, 19);
		}
	}

	@Override
	public String getRequestDate(int i) {
		return getOrderDate();
	}

	@SuppressWarnings("unused")
	public void processEncapsulatedData(HttpServletRequest request, HttpServletResponse response, int obr, int obx) throws HL7Exception
	{
		getOBXField(obr, obx, 5, 0, 2);
		String subtype = getOBXField(obr, obx, 5, 0, 3);
		String data = get("/.ORDER_OBSERVATION(" + obr + ")/OBSERVATION(" + obx + ")/OBX-5-5-1");
		try {
			if (subtype.equals("PDF")) {
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + getAccessionNum().replaceAll("\\s", "_") + "_" + obr + "-" + obx + "_Document.pdf\"");
			} else if (subtype.equals("JPEG")) {
				response.setContentType("image/jpeg");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + getAccessionNum().replaceAll("\\s", "_") + "_" + obr + "-" + obx + "_Image.jpg\"");
			} else if (subtype.equals("GIF")) {
				response.setContentType("image/gif");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + getAccessionNum().replaceAll("\\s", "_") + "_" + obr + "-" + obx + "_Image.gif\"");
			} else if (subtype.equals("RTF")) {
				response.setContentType("application/rtf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + getAccessionNum().replaceAll("\\s", "_") + "_" + obr + "-" + obx + "_Document.rtf\"");
			} else if (subtype.equals("HTML")) {
				response.setContentType("text/html");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + getAccessionNum().replaceAll("\\s", "_") + "_" + obr + "-" + obx + "_Document.html\"");
			} else if (subtype.equals("XML")) {
				response.setContentType("text/xml");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + getAccessionNum().replaceAll("\\s", "_") + "_" + obr + "-" + obx + "_Document.xml\"");
			}


			byte[] buf = Base64.decode(data);
			/*
			int pos = 0;
			int read;
			while (pos < buf.length) {
				read = buf.length - pos > 1024 ? 1024 : buf.length - pos;
				response.getOutputStream().write(buf, pos, read);
				pos += read;
			}
			*/
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(buf, 0, buf.length);
			baos.writeTo(response.getOutputStream());


		} catch (IOException e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
	}

	public String getCollectorsComment(int i)
	{
		return getString(get("/.ORDER_OBSERVATION(" + i + ")/OBR-39-2"));
	}

	public String getCollectorsCommentSourceOrganization(int i)
	{
		String id = getString(get("/.ORDER_OBSERVATION(" + i + ")/ZBR-3-6-2"));
		String ident = getString(get("/.ORDER_OBSERVATION(" + i + ")/ZBR-3-1"));

		if(StringUtils.isNotBlank(id))
		{
			id = id.substring(id.indexOf(":") + 1);
		}
		return ident + " (" + id + ")";
	}

	@Override
	public String getMsgPriority()
	{
		return ("");
	}

	/**
	 * Methods to get information about the Observation Request
	 */
	@Override
	public int getOBRCount()
	{
		return msg.getRESPONSE().getORDER_OBSERVATIONReps();
	}

	@Override
	public String getOBRName(int i)
	{
		String obrName = get("/.ORDER_OBSERVATION(" + i + ")/OBR-4-2");
		if(StringUtils.isBlank(obrName))
		{
			obrName = get("/.ORDER_OBSERVATION(" + i + ")/OBR-4-1");
		}
		return getString(obrName);
	}

	@Override
	public String getTimeStamp(int i, int j)
	{
		return formatDateTime(get("/.ORDER_OBSERVATION(" + i + ")/OBR-7-1"));
	}

	@Override
	public boolean isOBXAbnormal(int i, int j)
	{
		String abnormalFlag = getOBXAbnormalFlag(i, j);
		if (abnormalFlag.equals("") || abnormalFlag.equals("N")) return (false);
		else return (true);
	}

	@Override
	public String getOBXAbnormalFlag(int i, int j)
	{
		return (getOBXField(i, j, 8, 0, 1));
	}

	@Override
	public String getObservationHeader(int i, int j)
	{
		return getOBRName(i);
	}

	@Override
	public int getOBXCount(int i)
	{
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATIONReps();
	}

	@Override
	public String getOBXValueType(int i, int j)
	{
		return (getOBXField(i, j, 2, 0, 1));
	}

	@Override
	public String getOBXIdentifier(int i, int j) {
		return (getOBXField(i, j, 3, 0, 1));
	}

	public String getOBXObservationMethod(int i, int j) {
		return getOBXField(i, j, 17, 0, 2);
	}

	public String getOBXObservationDate(int i, int j) {
		try {
			String date = getOBXField(i, j, 14, 0, 1);
			if (date == null || date.trim().length() == 0) {
				return "";
			}
			return formatDateTime(date);
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
			return "";
		}
	}

	public String getOBRCategory(int obrRep)
	{
		String obxCategory = get("/.ORDER_OBSERVATION(" + obrRep + ")/OBR-4-1-1");
		if(obxCategory == null)
		{
			logger.error("Missing obxCategory [" + obrRep + "]");
			return "";
		}
		OLISRequestNomenclature requestNomenclature = requestNomenclatureDao.findByNameId(obxCategory);
		String nomenclatureCategory = "";
		if(requestNomenclature != null)
		{
			nomenclatureCategory = StringUtils.trimToEmpty(requestNomenclature.getCategory());
		}
		return nomenclatureCategory;
	}

	@Override
	public String getOBXName(int i, int j)
	{
		String obxName = getOBXField(i, j, 3, 0, 1);
		OLISResultNomenclature resultNomenclature = resultNomenclatureDao.findByNameId(obxName);

		String nomenclatureName = null;
		if (resultNomenclature != null)
		{
			nomenclatureName = StringUtils.trimToNull(resultNomenclature.getAltName1());
			if(nomenclatureName == null)
			{
				nomenclatureName = StringUtils.trimToNull(resultNomenclature.getName());
			}
		}
		if(nomenclatureName == null)
		{
			// If we're unable to find a LOINC match for the identifier then try to parse out the obx name.
			obxName = getOBXField(i, j, 3, 0, 2);
			nomenclatureName = StringUtils.trimToEmpty("".equals(obxName) ? " " : !obxName.contains(":") ? obxName : obxName.substring(0, obxName.indexOf(":")));
		}

		return nomenclatureName;
	}

	public String getOBXCEName(int i, int j) {
		return getOBXField(i, j, 5, 0, 2);
	}

	public boolean renderAsFT(int i, int j) {
		String obxIdent = getOBXField(i, j, 3, 0, 2).split(":")[4];
		return obxIdent != null && obxIdent.toUpperCase().startsWith("NAR");
	}

	public boolean renderAsNM(int i, int j) {
		String obxIdent = getOBXField(i, j, 3, 0, 2).split(":")[4];
		return obxIdent != null && (obxIdent.toUpperCase().startsWith("ORD") || obxIdent.toUpperCase().startsWith("QN"));
	}

	public boolean isAncillary(int i, int j) {
		String obxIdent = getOBXField(i, j, 3, 0, 3);
		return obxIdent != null && (obxIdent.toUpperCase().startsWith("LN"));
	}

	public String getOBXCESensitivity(int i, int j)
	{
		String obxValueType = getOBXValueType(i,j);
		switch(obxValueType)
		{
			case "SN": return getOBXSNResult(i, j);
			case "TS": return getOBXTSResult(i, j);
			case "DT": return getOBXDTResult(i, j);
			case "TM": return getOBXTMResult(i, j);
			default: return getOBXResult(i, j);
		}
	}

	@Override
	public String getOBXResult(int i, int j)
	{
		return (getOBXField(i, j, 5, 0, 1));
	}

	public String getOBXTSResult(int i, int j) {
		String date = getOBXField(i, j, 5, 0, 1);
		return formatDateTime(date);
	}

	public String getOBXDTResult(int i, int j) {
		String date = getOBXField(i, j, 5, 0, 1);
		return formatDate(date);
	}

	public String getOBXTMResult(int i, int j) {
		String date = getOBXField(i, j, 5, 0, 1);
		return formatTime(date);
	}

	public String getOBXSNResult(int i, int j) {
		return getOBXField(i, j, 5, 0, 1) + getOBXField(i, j, 5, 0, 2);
	}

	@Override
	public String getOBXReferenceRange(int i, int j) {
		return (getOBXField(i, j, 7, 0, 1));
	}

	@Override
	public String getOBXUnits(int i, int j) {
		return (getOBXField(i, j, 6, 0, 1));
	}

	@Override
	public String getOBXResultStatus(int i, int j)
	{
		return (getOBXField(i, j, 11, 0, 1));
	}

	@Override
	public int getOBXFinalResultCount()
	{
		int obrCount = getOBRCount();
		int obxCount;
		int count = 0;
		String status;
		for (int i = 0; i < obrCount; i++) {
			obxCount = getOBXCount(i);
			for (int j = 0; j < obxCount; j++) {
				status = getOBXResultStatus(i, j);
				if (status.startsWith("F") || status.startsWith("f")) count++;
			}
		}
		return count;
	}

	/**
	 * Retrieve the possible segment headers from the OBX fields
	 */
	@Override
	public ArrayList<String> getHeaders()
	{
		return this.headers;
	}

	/**
	 * Methods to get information from observation notes
	 */
	@Override
	public int getOBRCommentCount(int i)
	{
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getNOTEReps();
	}

	@Override
	public String getOBRComment(int i, int k)
	{
		return formatString(getString(msg.getRESPONSE().getORDER_OBSERVATION(i).getNOTE(k).getNTE().getNte3_Comment(0).getValue()));
	}

	public String getOBRSourceOrganization(int i, int k)
	{
		String key = get("/.ORDER_OBSERVATION(" + i + ")/NOTE(" + k + ")/ZNT-1-2-1");
		if (key == null || !key.contains(":")) {
			return "";
		}
		String ident = key.substring(0, key.indexOf(":"));
		ident = getOrganizationType(ident);
		key = key.substring(key.indexOf(":") + 1);
		return StringUtils.trimToEmpty(sourceOrganizations.get(key)) + " (" + ident + " " + key + ")";
	}

	public String getCollectionDateTime(int obrIndex)
	{
		String from = getString(get("/.ORDER_OBSERVATION(" + obrIndex + ")/OBR-7-1-1"));
		String to = getString(get("/.ORDER_OBSERVATION(" + obrIndex + ")/OBR-8-1-1"));

		if(from.length() > 13)
		{
			from = formatDateTime(from);
		}
		if(to.length() > 13)
		{
			to = formatDateTime(to);
		}
		boolean hasBoth = stringIsNotNullOrEmpty(from) && stringIsNotNullOrEmpty(to);
		return String.format("%s %s %s", from, hasBoth ? "-" : "", to);
	}

	public String getOrganizationType(String ident)
	{
		if(ident.equals("2.16.840.1.113883.3.59.1"))
		{
			return "Lab";
		}
		if(ident.equals("2.16.840.1.113883.3.59.2"))
		{
			return "SCC";
		}
		if(ident.equals("2.16.840.1.113883.3.59.3"))
		{
			return "Hospital";
		}
		return "";
	}

	public String getSpecimenCollectedBy(int obr)
	{
		try {
			String key = "", value = "", ident = "";

			key = getString(get("/.ORDER_OBSERVATION(" + obr + ")/ZBR-3-6-2"));
			if (key != null && key.indexOf(":") > 0) {
				ident = key.substring(0, key.indexOf(":"));
				ident = getOrganizationType(ident);
				key = key.substring(key.indexOf(":") + 1);
			}
			if (key == null || key.trim().equals("")) {
				return "";
			}
			value = getString(get("/.ORDER_OBSERVATION(" + obr + ")/ZBR-3-1-1"));
			return String.format("%s (%s %s)", value, ident, key);
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}

	public String getCollectionVolume(int obrIndex)
	{
		String volume = getString(get("/.ORDER_OBSERVATION(" + obrIndex + ")/OBR-9-1-1"));
		String units = getString(get("/.ORDER_OBSERVATION(" + obrIndex + ")/OBR-9-2-1"));
		return volume + " " + units;
	}

	public String getNoOfSampleContainers(int obrIndex)
	{
		return getString(get("/.ORDER_OBSERVATION(" + obrIndex + ")/OBR-37-1-1"));
	}

	/**
	 * Methods to get information from observation notes
	 */
	public int getReportCommentCount()
	{
		return msg.getRESPONSE().getPATIENT().getNOTEReps();
	}

	public String getReportComment(int k)
	{
		return formatString(getString(msg.getRESPONSE().getPATIENT().getNOTE(k).getNTE().getNte3_Comment(0).getValue()));
	}

	public String getReportSourceOrganization(int k)
	{
		try {
			String key = msg.getRESPONSE().getPATIENT().getNOTE(k).getZNT().getZnt1_sourceOrganization().getHd2_UniversalID().getValue();
			if (key == null || !key.contains(":"))
			{
				return "";
			}
			String ident = key.substring(0, key.indexOf(":"));
			ident = getOrganizationType(ident);
			key = key.substring(key.indexOf(":") + 1);
			return String.format("%s (%s %s)", StringUtils.trimToEmpty(sourceOrganizations.get(key)), ident, key);
		}
		catch(Exception e)
		{
			logger.error("Could not retrieve OBX comment ZNT", e);
			return ("");
		}
	}

	/**
	 * Methods to get information from observation notes
	 */
	@Override
	public int getOBXCommentCount(int i, int j)
	{
		return msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNOTEReps();
	}

	@Override
	public String getOBXComment(int i, int j, int nteNum)
	{
		return formatString(msg.getRESPONSE().getORDER_OBSERVATION(i).getOBSERVATION(j).getNOTE(nteNum).getNTE().getNte3_Comment(0).getValue());
	}

	public String getOBXSourceOrganization(int i, int j, int nteNum)
	{
		String key = get("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + j + ")/NOTE(" + nteNum + ")/ZNT-1-2-1");
		if (key == null || !key.contains(":"))
		{
			return "";
		}
		String ident = key.substring(0, key.indexOf(":"));
		ident = getOrganizationType(ident);
		key = key.substring(key.indexOf(":") + 1);
		return String.format("%s (%s %s)", StringUtils.trimToEmpty(sourceOrganizations.get(key)), ident, key);
	}

	/*
	 * Patient Name 1 Last Name 2 First Name 3 Second Name 4 Suffix (e.g., JR or III) 5 Prefix (e.g., DR) 6 Degree 7 Name Type Code
	 */

	public String parseFullNameFromSegment(String ident) {
		String name = "";
		String temp = null;

		// get name prefix ie/ DR.
		try {
			temp = terser.get(ident + "5");
		} catch (HL7Exception e) {
			// TODO-legacy Auto-generated catch block
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		if (temp != null) {
			name = temp;
		}

		// get the name
		try {
			temp = terser.get(ident + "2");
		} catch (HL7Exception e) {
			temp = null;
		}
		if (temp != null) {
			if (name.equals("")) {
				name = temp;
			} else {
				name = name + " " + temp;
			}
		}
		try {
			if (terser.get(ident + "3") != null) name = name + " " + terser.get(ident + "3");
		} catch (HL7Exception e) {
			name = null;
		}
		try {
			if (terser.get(ident + "1") != null) name = name + " " + terser.get(ident + "1");
		} catch (HL7Exception e) {
			temp = null;
		}
		try {
			if (terser.get(ident + "4") != null) name = name + " " + terser.get(ident + "4");
		} catch (HL7Exception e) {
			temp = null;
		}
		try {
			if (terser.get(ident + "6") != null) name = name + " " + terser.get(ident + "6");
		} catch (HL7Exception e) {
			temp = null;
		}

		return (name);
	}

	/**
	 * acts as the unique lab version number
	 */
	@Override
	public String getFillerOrderNumber()
	{
		// not totally sure which fields to use here.
		// OLIS doesn't want to give us an official way to check versions.
		// use the lastUpdatedDate date for now.
		return getLastUpdateInOLISUnformatted();
	}

	@Override
	public String getEncounterId()
	{
		return "";
	}

	@Override
	public String getRadiologistInfo()
	{
		return "";
	}

	@Override
	public String getNteForOBX(int i, int j)
	{
		return "";
	}
	/**
	 * Methods to get information about the patient
	 */
	@Override
	public String getPatientName() {
		return (parseFullNameFromSegment("/.PID-5-"));
	}

	@Override
	public String getFirstName() {
		try {
			return (getString(terser.get("/.PID-5-2")));
		} catch (HL7Exception ex) {
			return ("");
		}
	}

	@Override
	public String getLastName() {
		try {
			return (getString(terser.get("/.PID-5-1")));
		} catch (HL7Exception ex) {
			return ("");
		}
	}

	@Override
	public String getDOB() {
		try {
			return (formatDateTime(getString(terser.get("/.PID-7-1"))));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getSex() {
		try {
			return (getString(terser.get("/.PID-8-1")));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getHealthNum() {
		String healthNum;

		try {

			// Try finding the health number in the external ID
			healthNum = getString(terser.get("/.PID-2-1"));
			if (healthNum.length() == 10) return (healthNum);

			// Try finding the health number in the alternate patient ID
			healthNum = getString(terser.get("/.PID-4-1"));
			if (healthNum.length() == 10) return (healthNum);

			// Try finding the health number in the internal ID
			healthNum = getString(terser.get("/.PID-3-1"));
			if (healthNum.length() == 10) return (healthNum);

			// Try finding the health number in the SSN field
			healthNum = getString(terser.get("/.PID-19-1"));
			if (healthNum.length() == 10) return (healthNum);
		} catch (Exception e) {
			// ignore exceptions
		}

		return ("");
	}

	@Override
	public String getHomePhone() {
		try {
			String ext = getString(terser.get("/.PID-13-8"));
			return (getString(terser.get("/.PID-13-6")) + "-" + getString(terser.get("/.PID-13-7")) + " " + (ext != null && ext.length() > 0 ? "x" : "") + ext);
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getWorkPhone() {
		try {
			String ext = getString(terser.get("/.PID-14-8"));
			return (getString(terser.get("/.PID-14-6")) + "-" + getString(terser.get("/.PID-14-7")) + " " + (ext != null && ext.length() > 0 ? "x" : "") + ext);
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getPatientLocation() {
		/*
		 * try{ String address = getString(terser.get("/.PID-11-1")); String mailing = String.format("%s %s %s", getString(terser.get("/.PID-11-3")), getString(terser.get("/.PID-11-4")), getString(terser.get("/.PID-11-5"))); return address + "<br/>" +
		 * mailing; }catch(Exception e){ return(""); }
		 */
		return getPerformingFacilityName();
	}

	public String getWorkLocation() {
		try {
			String address = getString(terser.get("/.PID-11-1"));
			String mailing = String.format("%s %s %s", getString(terser.get("/.PID-11-3")), getString(terser.get("/.PID-11-4")), getString(terser.get("/.PID-11-5")));
			return address + "<br/>" + mailing;
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getServiceDate() {
		try {
			Date mshDate = UtilDateUtilities.StringToDate(getMsgDate(), "yyyy-MM-dd");
			return UtilDateUtilities.DateToString(mshDate, "dd-MMM-yyyy");
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getOrderStatus() {
		return isCorrected ? "C" : isFinal ? "F" : "P";
	}

	@Override
	public String getClientRef() {
		try {
			return (getString(terser.get("/.OBR-16-1")));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getAccessionNum()
	{
		return getString(msg.getRESPONSE().getORDER_OBSERVATION(0).getORC().getOrc4_PlacerGroupNumber().getEi1_EntityIdentifier().getValue());
	}

	public String getAccessionNumSourceOrganization() {
		try {
			String key = getString(terser.get("/.ORC-4-3"));
			String ident = "";
			if (key != null && key.indexOf(":") > 0) {
				ident = key.substring(0, key.indexOf(":"));
				ident = getOrganizationType(ident);
				key = key.substring(key.indexOf(":") + 1);
			} else {
				key = "";
			}
			if (key == null || "".equals(key.trim())) {
				return "";
			}
			String sourceOrg = sourceOrganizations.get(key);
			if(sourceOrg == null)
				sourceOrg = defaultSourceOrganizations.get(key);
			return String.format("%s (%s %s)", StringUtils.trimToEmpty(sourceOrg), ident, key);
		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}
		return "";
	}

	@Override
	public String getDocName() {
		try {
			// Previously getFullDocName
			// Changed to stop injecting HTML into OLIS labs
			return (getShortName("/.OBR-16-"));
		} catch (Exception e) {
			return ("");
		}
	}

	public String getShortDocName() {
		try {
			return (getShortName("/.OBR-16-"));
		} catch (Exception e) {
			return ("");
		}
	}

	@Override
	public String getCCDocs() {

		try {
			int i = 0;
			String docs = getShortName("/.OBR-28(" + i + ")-");
			i++;
			String nextDoc = getShortName("/.OBR-28(" + i + ")-");

			while (!nextDoc.equals("")) {
				docs = docs + ", " + nextDoc;
				i++;
				nextDoc = getShortName("/.OBR-28(" + i + ")-");
			}

			return (docs);
		} catch (Exception e) {
			return ("");
		}
	}

	public List<String> getCCDocsList()
	{
		List<String> ccDocList = new LinkedList<>();
		try
		{
			int i = 0;
			String nextDoc = getFullDocName("/.OBR-28(" + i + ")-");

			while(!nextDoc.equals(""))
			{
				ccDocList.add(nextDoc);
				i++;
				nextDoc = getFullDocName("/.OBR-28(" + i + ")-");
			}
		}
		catch(Exception e)
		{
			logger.error("Olis hl7 error", e);
		}
		return ccDocList;
	}

	@Override
	public ArrayList<String> getDocNums() {
		ArrayList<String> nums = new ArrayList<String>();
		String docNum;
		try {
			if ((docNum = terser.get("/.OBR-16-1")) != null) nums.add(docNum);

			int i = 0;
			while ((docNum = terser.get("/.OBR-28(" + i + ")-1")) != null) {
				nums.add(docNum);
				i++;
			}

		} catch (Exception e) {
			MiscUtils.getLogger().error("OLIS HL7 Error", e);
		}

		return (nums);
	}

	@Override
	public String audit()
	{
		return "";
	}

	@Override
	public String getNteForPID()
	{
		return "";
	}

	protected String getOBXField(int i, int j, int field, int rep, int comp)
	{
		try
		{
			Segment obx = terser.getSegment("/.ORDER_OBSERVATION(" + i + ")/OBSERVATION(" + j + ")/OBX");
			return getString(Terser.get(obx, field, rep, comp, 1));
		}
		catch(Exception e)
		{
			logger.error("invalid obx segment lookup: obr(" + i + "), obx(" + j + ")-" + field + "(" + rep + ")-" + comp, e);
			throw new RuntimeException(e);
		}
	}

	private String getFullDocName(String docSeg) throws HL7Exception {
		String docName = "";
		String temp;

		// get name prefix ie/ DR.
		temp = terser.get(docSeg + "6");
		if (temp != null) docName = temp;

		// get the name
		temp = terser.get(docSeg + "3");
		if (temp != null) {
			if (docName.equals("")) {
				docName = temp;
			} else {
				docName = docName + " " + temp;
			}
		}

		if (terser.get(docSeg + "4") != null) {
			docName = docName + " " + terser.get(docSeg + "4");
		}
		if (terser.get(docSeg + "2") != null) {
			docName = docName + " " + terser.get(docSeg + "2");
		}
		if (terser.get(docSeg + "5") != null) {
			docName = docName + " " + terser.get(docSeg + "5");
		}
		if (terser.get(docSeg + "7") != null) {
			docName = docName + " " + terser.get(docSeg + "7");
		}
		String modifier = "";
		if (terser.get(docSeg + "13") != null) {
			modifier = terser.get(docSeg + "13").toUpperCase();
			if (modifier.equals("MDL")) {
				modifier = "MD";
			}
			if (modifier.equals("ML")) {
				modifier = "RM";
			}
			if (modifier.equals("NPL")) {
				modifier = "RN(EC)";
			}
			if (modifier.equals("DDSL")) {
				modifier = "DDS";
			}

		}

		String licenseInfo = "";
		String licenseNo = terser.get(docSeg + "1");
		if (StringUtils.isNotBlank(licenseNo))
		{
			licenseInfo = StringUtils.trimToEmpty(modifier + " " + licenseNo);
		}

		String jurisdiction = "";
		String provinceCode = terser.get(docSeg + "22-1");
		String provinceName = terser.get(docSeg + "22-2");
		if(StringUtils.isNotBlank(provinceCode) && !provinceCode.equals("ON"))
		{
			jurisdiction = " (" + provinceName + ")";
		}

		if (StringUtils.isNotBlank(licenseInfo) || StringUtils.isNotBlank(jurisdiction))
		{
			docName += " " + "<span style=\"margin-left:15px; font-size:8px; color:#333333;\">" + licenseInfo + jurisdiction + "</span>";
		}

		return (docName);
	}

	private String getShortName(String docSeg) throws HL7Exception {
		String docName = "";
		String temp;

		// get name prefix ie/ DR.
		temp = terser.get(docSeg + "6");
		if (temp != null) docName = temp;

		// get the name
		temp = terser.get(docSeg + "3");
		if (temp != null) {
			if (docName.equals("")) {
				docName = temp;
			} else {
				docName = docName + " " + temp;
			}
		}

		if (terser.get(docSeg + "4") != null) {
			docName = docName + " " + terser.get(docSeg + "4");
		}
		if (terser.get(docSeg + "2") != null) {
			docName = docName + " " + terser.get(docSeg + "2");
		}
		if (terser.get(docSeg + "5") != null) {
			docName = docName + " " + terser.get(docSeg + "5");
		}
		if (terser.get(docSeg + "7") != null) {
			docName = docName + " " + terser.get(docSeg + "7");
		}

		return docName;
	}

	protected String formatTime(String plain) {

		String dateFormat = "HHmmss";
		dateFormat = dateFormat.substring(0, plain.length());
		String stringFormat = "HH:mm:ss";
		stringFormat = stringFormat.substring(0, stringFormat.lastIndexOf(dateFormat.charAt(dateFormat.length() - 1)) + 1);

		Date date = UtilDateUtilities.StringToDate(plain, dateFormat);
		return UtilDateUtilities.DateToString(date, stringFormat);
	}

	protected String formatDateTime(String plain) {
		if (plain==null || plain.trim().equals("")) return "";

		String offset = "";
		if (plain.length() > 14) {
			offset = plain.substring(14, 19);
			plain = plain.substring(0, 14);
		}
		String dateFormat = "yyyyMMddHHmmss";
		dateFormat = dateFormat.substring(0, plain.length());
		String stringFormat = "yyyy-MM-dd HH:mm:ss";
		stringFormat = stringFormat.substring(0, stringFormat.lastIndexOf(dateFormat.charAt(dateFormat.length() - 1)) + 1);

		Date date = UtilDateUtilities.StringToDate(plain, dateFormat);
		return UtilDateUtilities.DateToString(date, stringFormat) + " " + getOffsetName(offset);
	}

	private String getOffsetName(String offset)
	{
		if(offset.equals("-0400"))
		{
			return "EDT";
		}
		else if(offset.equals("-0500"))
		{
			return "EST";
		}
		else if(offset.equals("-0600"))
		{
			return "CST";
		}
		else if(!offset.trim().equals(""))
		{
			return "UTC" + offset;
		}
		return "";
	}

	public void importSourceOrganizations(OLISHL7Handler instance)
	{
		if(instance == null)
		{
			return;
		}
		HashMap<String, String> foreignSource = instance.sourceOrganizations;
		for(String key : foreignSource.keySet())
		{
			if(!sourceOrganizations.containsKey(key))
			{
				sourceOrganizations.put(key, foreignSource.get(key));
			}
		}
	}

	protected String formatDate(String plain) {

		String dateFormat = "yyyyMMdd";
		dateFormat = dateFormat.substring(0, plain.length());
		String stringFormat = "yyyy-MM-dd";
		stringFormat = stringFormat.substring(0, stringFormat.lastIndexOf(dateFormat.charAt(dateFormat.length() - 1)) + 1);

		Date date = UtilDateUtilities.StringToDate(plain, dateFormat);
		return UtilDateUtilities.DateToString(date, stringFormat);
	}

	protected String getString(String retrieve) {
		if (retrieve != null) {
			return retrieve.trim();
		} else {
			return ("");
		}
	}

	public String formatString(String str)
	{
		if (StringUtils.isBlank(str))
		{
			return "";
		}

		// for legacy purposes. this was previously run pre-upload,
		// which altered saved lab text to include these values
		String s = str.replace("\\E\\", "\\SLASHHACK\\")
			.replace("\\H\\", "\\.H\\")
			.replace("\\N\\", "\\.N\\");

		int pos = 0;
		StringBuilder sb = new StringBuilder();
		centered = false;

		int pieceStart = 0;
		int pieceEnd = 0;
		String op = "";
		String result = "";
		while (pos < s.length()) {
			pieceStart = s.indexOf('\\', pos);
			pieceEnd = s.indexOf('\\', pieceStart + 1);

			// If there are no delimiters take the whole string from this position.
			if (pieceStart == -1 || pieceEnd == -1) {
				sb.append(s.substring(pos, s.length()));
				pos = s.length();
			} else {
				if (pos < pieceStart) {
					sb.append(s.substring(pos, pieceStart));
					pos = pieceStart;
				}
				// If two delimiters are adjacent ignore the first one
				if (pieceStart + 1 == pieceEnd) {
					sb.append("\\");
					pos = pieceEnd;
				} else {
					op = s.substring(pieceStart + 1, pieceEnd);
					result = parseOperator(op);
					sb.append(result);
					pos = pieceEnd + 1;
				}
			}
		}

		return sb.toString();
	}

	protected String parseOperator(String op) {
		if (op == null || op.equals("")) {
			return "";
		}

		String piece = op.toUpperCase();
		boolean matchFound = true;

		if (piece.equals(".BR")) {
			boolean old = centered;
			centered = false;
			return old ? "</center>" : "<br/>";

		} else if (piece.equals(".H")) {
			return "<span style=\"color:" + TEXT_HIGHLIGHT_COLOUR + "\">";
		} else if (piece.equals(".N")) {
			return "</span>";
		} else if (piece.equals(".CE")) {
			centered = true;
			return (centered ? "</center>" : "") + "<br/><center>";

		} else if (piece.equals(".FE")) {
			// TODO-legacy: Implement
		} else if (piece.equals(".NF")) {
			// TODO-legacy: Implement
		} else if (piece.equals("F")) {
			return "|";
		} else if (piece.equals("S")) {
			return "^";
		} else if (piece.equals("T")) {
			return "&";
		} else if (piece.equals("R")) {
			return "~";
		} else if (piece.equals("SLASHHACK")) {
			return "\\";
		} else if (piece.equals("MUHACK")) { // legacy files only
			return "&#181;";
		} else {
			matchFound = false;
		}

		if (!matchFound) {
			// If we haven't already matched a command, look for a command with a parameter.
			String patternStr = "\\.(SP|IN|TI|SK)\\s*([+-]?)(\\d*)\\s*";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(piece.toUpperCase());
			matchFound = matcher.find();
			if (matchFound) {
				// Get all groups for this match
				String result = parseParamsAndFormat(matcher.group(1), matcher.group(2), matcher.group(3), centered);
				if (result != null && result.contains("</center>"))
				{
					centered = false;
				}
				return result == null ? "" : result;
			}
		}
		return "";
	}

	protected static String parseParamsAndFormat(String operator, String sign, String operand, boolean centered)
	{
		int opInt = operand.equals("") ? 1 : Integer.parseInt(operand);
		String result = "";

		// ignore negative spacing for now
		if("-".equals(sign))
		{
			return result;
		}

		switch (operator)
		{
			case "SP":
			{
				while (opInt > 0)
				{
					if (centered)
					{
						result += "</center>";
						centered = false;
					}
					result += "<br/>";
					opInt--;
				}
				break;
			}
			case "IN":
			case "TI":
			case "SK":
			{
				result = String.join("", Collections.nCopies(opInt, "&nbsp;"));
				break;
			}
			default:
			{
				result = null;
				break;
			}
		}
		return result;
	}

}
