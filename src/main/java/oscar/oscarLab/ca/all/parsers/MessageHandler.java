/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package oscar.oscarLab.ca.all.parsers;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.SegmentFinder;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.v2.oscar_to_oscar.DataTypeUtils;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.labs.service.Hl7TextInfoService;
import oscar.oscarLab.ca.all.model.EmbeddedDocument;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class MessageHandler
{
	protected static Logger logger = Logger.getLogger(MessageHandler.class);

	protected Terser terser;
	protected Message message;
	protected boolean legacyHandler = false;

	// Embedded PDF strings that show up in OBX messages
	public static final String embeddedPdfPrefix = "JVBERi0xLj";
	public static final String pdfReplacement = "embedded_doc_id_";

	public enum OBX_CONTENT_TYPE {
		UNKNOWN,
		TEXT,
		SUSCEPTIBILITY,
		STRUCTURED_NUMERIC,
		PDF
	}

	/**
	 * default constructor. init must be called after instantiation.
	 */
	public MessageHandler()
	{
	}

	/**
	 * constructor which also calls init
	 * @param hl7Body - hl7 formatted string
	 * @throws HL7Exception if init fails
	 */
	public MessageHandler(String hl7Body) throws HL7Exception
	{
		HapiContext context = new DefaultHapiContext();
		context.setValidationContext(new NoValidation());
		Parser p = context.getPipeParser();

		Message msg = p.parse(hl7Body);
		this.message = msg;
		this.terser = new Terser(msg);
	}
	public MessageHandler(Message msg) throws HL7Exception
	{
		this.message = msg;
		this.terser = new Terser(msg);
	}

	/**
	 * This method is run before labs are uploaded/saved to the database
	 * This is where duplicates can be checked, merged, or rejected based on each lab's specifications before the lab is routed
	 * @param hl7Message - the hl7 string for the message
	 * @return - the same hl7 message as the parameter with any required modifications
	 */
	public abstract String preUpload(String hl7Message) throws HL7Exception;

	/**
	 * This method should determine if the lab can be routed
	 * @return true if the lab can be routed, false otherwise
	 */
	public abstract boolean canUpload();

	/**
	 * This gets run after the lab is routed
	 */
	public abstract void postUpload();

	/**
	 * return a list of pairs &lt;title, value> that will be displayed in the Patient Detail Results area of the lab display
	 * @return - list of fields
	 */
	public  ArrayList<Pair<String, String>> getExtendedPatientDescriptionFields()
	{
		return new ArrayList<Pair<String, String>> ();
	}

	/**
	 * return a list of pairs &lt;title, value> Results Info area of the lab display
	 * @return - list of fields
	 */
	public ArrayList<Pair<String, String>> getExtendedResultDescriptionFields()
	{
		return new ArrayList<>();
	}

	/* ===================================== Hl7 Parsing ====================================== */

	/**
	 * Each handler should implement this method.
	 * This method should return true if this instance of MessageHandler can parse the lab
	 * @param message hl7 Message used for determining lab parser type
	 * @return true if this parser matches the lab type. false otherwise
	 */
	@SuppressWarnings("unused")
	protected static boolean handlerTypeMatch(Message message)
	{
		return true;
	}


	/**
	 *  Initialize the 'msg' object and any other global variables that may be
	 *  needed.
	 *
	 *  The 'msg' object should be initialized with the following code:
	 *       Parser p = new PipeParser();
	 *       p.setValidationContext(new NoValidation());
	 *       msg = (ORU_R01) p.parse(hl7Body.replaceAll( "\n", "\r\n" ));

	 *	- If you wish to validate the message please see the hapi documentation at
	 *		'http://hl7api.sourceforge.net/'
	 *  - The replaceAll statement is necessary to ensure that the parser
	 *  correctly reads the end of each line.
	 *
	 *  @deprecated - initialization should be done in constructors.
	 */
	@Deprecated
	public abstract void init(String hl7Body) throws HL7Exception;

	/**
	 * true if the hl7 spec referenced by this message handler supports embedded documents
	 * @return true if embedded documents are supported.
	 */
	public boolean supportsEmbeddedDocuments()
	{
		return false;
	}

	public List<EmbeddedDocument> getEmbeddedDocuments()
	{
		return new ArrayList<>(0);
	}

	protected EmbeddedDocument toEmbeddedPdf(String base64String, int index)
	{
		return toEmbeddedDocument(base64String, "application/pdf", "pdf", index, "embedded_pdf");
	}

	protected EmbeddedDocument toEmbeddedDocument(String base64String, String mimeType, String extension, int index, String description)
	{
		EmbeddedDocument embeddedDocument = new EmbeddedDocument();
		embeddedDocument.setBase64Data(base64String);
		embeddedDocument.setMimeType(mimeType);

		String fileName = "-" + getUniqueIdentifier() + "-" + getUniqueVersionIdentifier() + "-" + index + "-" + (int) (Math.random() * 1000000000) + "." + extension;
		embeddedDocument.setFileName(fileName);
		embeddedDocument.setDescription(description);
		embeddedDocument.setSourceFacility("HL7Upload");
		embeddedDocument.setSource("Embedded Lab Data");

		return embeddedDocument;
	}

	/* ===================================== MSH ====================================== */

	/**
	 *  Return the message type
	 *  - The message type returned should be the same as the prefix of your
	 *  handlers name
	 *      ie/ message type = XXXX
	 *          handler name = XXXXHandler
	 */
	public abstract String getMsgType();

	/**
	 *  Return the date and time of the message, usually located in the 7th
	 *  field of the MSH segment
	 */
	public String getMsgDate()
	{
		return formatDateTime(get("/.MSH-7"));
	}

	/**
	 *  A String containing a single letter represinting the priority
	 *		"C" - Critical 		"S" - Stat/Urgent
	 *		"U" - Unclaimed 	"A" - ASAP
	 *		"L" - Alert 		""  - Routine
	 *  If there is no priority specified in the documentation for your message
	 *  type then just return the empty string ""
	 */
	public String getMsgPriority()
	{
		return "";
	}

	/**
	 *  Return the patients location, usually the facility from which the
	 *  report has been sent ( the 4th field of the MSH segment )
	 */
	public String getPatientLocation()
	{
		return getString(get("/.MSH-4"));
	}

	/* ===================================== PID ====================================== */

	/**
	 *  Return the name of the patient. The format should be the first name
	 *  followed by the last name while being separated by a space.
	 *  String firstName = getFirstName();
	 *  String lastName = getLastName();
	 */
	public String getPatientName()
	{
		return "";
	}

	/**
	 *  Return the patients health number
	 */
	public String getHealthNum()
	{
		return "";
	}

	/**
	 *  Return the patients health number province code (ie: AB, BC, ON, etc.)
	 */
	public String getHealthNumProvince()
	{
		return "";
	}

	/**
	 * Return the family name of the patient
	 */
	public String getLastName()
	{
		return "";
	}

	/**
	 *  Return the given name of the patient
	 */
	public String getFirstName()
	{
		return "";
	}

	/**
	 * Return the middle name of the patient
	 */
	public String getMiddleName()
	{
		return "";
	}

	/**
	 *  Return the patients date of birth
	 */
	public String getDOB()
	{
		return "";
	}

	/**
	 *  Return the gender of the patient: 'M' or 'F'
	 */
	public String getSex()
	{
		return "";
	}


	protected String getBusinessPhone(int i) throws HL7Exception
	{
		return "";
	}

	protected String getHomePhone(int i) throws HL7Exception
	{
		return "";
	}

	/**
	 *  Return the home phone number of the patient
	 */
	public String getHomePhone()
	{
		String phone = "";
		int i = 1;
		try
		{
			while (!getHomePhone(i).isEmpty())
			{
				if (i == 1)
				{
					phone = getHomePhone(i);
				}
				else
				{
					phone = phone + ", " + getHomePhone(i);
				}
				i++;
			}
			return (phone);
		}
		catch (Exception e)
		{
			logger.error("Could not return phone number", e);

			return ("");
		}
	}

	/**
	 * get work phone number
	 * @return work phone number
	 */
	public String getWorkPhone()
	{
		String phone = "";
		int i = 1;
		try
		{
			while (!getBusinessPhone(i).isEmpty())
			{
				if (i == 1)
				{
					phone = getBusinessPhone(i);
				}
				else
				{
					phone = phone + ", " + getBusinessPhone(i);
				}
				i++;
			}
			return (phone);
		}
		catch (Exception e)
		{
			logger.error("Could not return phone number", e);

			return ("");
		}
	}

	public abstract String getNteForPID();

	/* ===================================== OBR ====================================== */

	/**
	 * this determines a unique identification string for a lab. This is used for grouping lab versions.
	 * It is assumed that two labs with the same unique identifier are different versions of the same lab.
	 * This field is usually the accession number, but may include multiple fields in order to guarantee uniqueness.
	 * the lab type does not need to be considered, as identifiers are only expected to be unique to the same lab type.
	 * @return lab unique identifier
	 */
	public abstract String getUniqueIdentifier();

	/**
	 * get lab accession number
	 * @return accession number
	 */
	public String getAccessionNumber()
	{
		return getUniqueIdentifier();
	}

	/**
	 *  Return the number of OBR Segments in the message
	 */
	public int getOBRCount()
	{
		return 0;
	}

	/**
	 * this determines a unique version identification string for a lab.
	 * This may be used for duplicate checking lab uploads with the same unique identifier.
	 * It is assumed that two labs with the same unique identifier and version identifier are the same, and thus duplicates.
	 * This field is usually the filler order number, but may include multiple fields in order to guarantee uniqueness.
	 * @return lab unique version identifier
	 */
	public String getUniqueVersionIdentifier()
	{
		return "";
	}

	public String getFillerOrderNumber()
	{
		return getUniqueVersionIdentifier();
	}

	/**
	 *  Return the name of the ith OBR Segment, usually stored in the
	 *  UniversalServiceIdentifier
	 */
	public String getOBRName(int i)
	{
		return "";
	}

	/**
	 * get the OBR procedure code
	 * @param i - obr rep
	 * @return procedure code string
	 */
	public String getOBRProcedureCode(int i)
	{
		return "";
	}

	/**
	 *  Return the request date of the message
	 */
	public String getRequestDate(int i)
	{
		return "";
	}

	/**
	 *  Return the service date of the message
	 */
	public String getServiceDate()
	{
		return "";
	}

	/**
	 * get the report date (obr-22)
	 * @param i - obr rep
	 * @return the report date string
	 */
	public String getReportDate(int i)
	{
		return "";
	}

	/**
	 *  Return the status of the report, 'F' is returned for a final report,
	 *  otherwise the report is partial
	 */
	public String getOrderStatus()
	{
		return "";
	}

	/**
	 *  Return the status of the report in a human-readable format
	 */
	public String getOrderStatusDisplayValue()
	{
		String orderStatusCode = getString(getOrderStatus());
		switch (orderStatusCode)
		{
			case "F": return "Final";
			case "C": return "Corrected";
			case "X": return "DELETED";
			default: return orderStatusCode;
		}
	}

	/**
	 * get the internal juno order status code
	 * @return - internal juno order status code
	 */
	public Hl7TextInfo.REPORT_STATUS  getJunoOrderStatus()
	{
		return Hl7TextInfoService.getReportStatusFromString(getOrderStatus());
	}

	/**
	 *  Return the number of comments (usually NTE segments) that follow ith
	 *  OBR segment, this should usually be either 0 or 1.
	 */
	public int getOBRCommentCount(int i)
	{
		return 0;
	}

	/**
	 *  Return the kth comment of the ith OBR segment.
	 */
	public String getOBRComment(int i, int k)
	{
		return "";
	}

	/**
	 *  Return the observation header which represents the observation stored in
	 *  the jth OBX segment of the ith OBR group. May be stored in either the
	 *  OBR or OBX segment. It is used to separate the observations into groups.
	 *  ie/ 'CHEMISTRY' 'HEMATOLOGY' '
	 */
	public String getObservationHeader(int i, int j)
	{
		return "";
	}

	/**
	 *  Return a list of all possible headers retrieved from getObservationHeader
	 *  each header will only occur once in the list
	 */
	public ArrayList<String> getHeaders()
	{
		HashSet<String> headers = new HashSet<>();
		for (int i = 0; i < getOBRCount(); i++) {
			// duplicates are ignored in hash set add
			headers.add(getOBRName(i));
		}
		return new ArrayList<>(headers);
	}

	/**
	 * an optional subheader that displays for each header based on OBR index
	 */
	public String getSubHeader(int i)
	{
		return "";
	}


	protected String getClientRef(int i, int k) throws HL7Exception
	{
		return "";
	}

	/**
	 *  Return the clients reference number, usually corresponds to the doctor
	 *  who requested the report or the requesting facility.
	 */
	public String getClientRef() {
		String docNum = "";
		int i = 0;
		try {
			while (!getClientRef(0,i).isEmpty()) {
				if (i == 0) {
					docNum = getClientRef(0,i);
				} else {
					docNum = docNum + ", " + getClientRef(0,i);
				}
				i++;
			}
			return (docNum);
		} catch (Exception e) {
			logger.error("Could not return doctor id numbers", e);
			return ("");
		}
	}


	protected String getOrderingProvider(int i, int k) throws HL7Exception
	{
		return "";
	}


	protected String getResultCopiesTo(int i, int k) throws HL7Exception
	{
		return "";
	}


	protected String getOrderingProviderNo(int i, int k) throws HL7Exception
	{
		return "";
	}


	protected String getResultCopiesToProviderNo(int i, int k) throws HL7Exception
	{
		return "";
	}

	/**
	 *  Return the name of the doctor who requested the report, the name should
	 *  be formatted as follows:
	 *      'PREFIX' 'GIVEN NAME' 'MIDDLE INITIALS' 'FAMILY NAME' 'SUFFIX' 'DEGREE'
	 */
	public String getDocName()
	{
		String docName = "";
		int i = 0;
		try {
			while (!getOrderingProvider(0,i).isEmpty()) {
				if (i == 0) {
					docName = getOrderingProvider(0,i);
				} else {
					docName = docName + ", " + getOrderingProvider(0,i);
				}
				i++;
			}
			return (docName);
		} catch (Exception e) {
			logger.error("Could not return doctor names", e);
			return ("");
		}
	}

	/**
	 *  Return the names of the doctors which the report should be copied to. The
	 *  formatting of the names should be the same as in the method above. The
	 *  names should be separated by a comma and a space.
	 */
	public String getCCDocs() {
		String docName = "";
		int i = 0;
		try {
			while (!getResultCopiesTo(0,i).isEmpty()) {
				if (i == 0) {
					docName = getResultCopiesTo(0,i);
				} else {
					docName = docName + ", " + getResultCopiesTo(0,i);
				}
				i++;
			}
			return (docName);
		} catch (Exception e) {
			logger.error("Could not return cc'ed doctors", e);
			return ("");
		}
	}

	/**
	 *  Return an ArrayList of the requesting doctors billing number and the
	 *  billing numbers of the cc'd docs
	 */
	public ArrayList<String> getDocNums() {
		ArrayList<String> docNums = new ArrayList<>();
		String id;
		int i;

		try {
			String providerId = getOrderingProviderNo(0, 0);
			docNums.add(providerId);

			i = 0;
			id = getResultCopiesToProviderNo(0, i);
			while (id != null && !id.isEmpty()) {
				if (!id.equals(providerId))
					docNums.add(id);
				i++;
				id = getResultCopiesToProviderNo(0, i);
			}
		} catch (Exception e) {
			logger.error("Could not return doctor nums", e);

		}

		return (docNums);
	}

	/**
	 * check if the report is marked as blocked. mostly related to OLIS and custom lab imports
	 * @return true if blocked
	 */
	public boolean isReportBlocked()
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

	/**
	 * check for a blocked marker on an OBR segment
	 * @param obr segment index
	 * @return true if blocked
	 */
	public boolean isOBRBlocked(int obr)
	{
		return false;
	}

	/**
	 * is the specific OBR unstructured. similar to the unstructured check except for specific OBR segments
	 * @param obr segment index
	 * @return true if unstructured
	 */
	public boolean isOBRUnstructured(int obr)
	{
		return false;
	}

	/**
	 * @param obr segment index - 0 indexed
	 * @return true if a mapped value exists
	 */
	public boolean isChildOBR(int obr)
	{
		return false;
	}

	/* ===================================== OBX ====================================== */

	/**
	 *  Return the number of OBX Segments within the OBR group specified by i.
	 */
	public int getOBXCount(int i)
	{
		return 0;
	}

	/**
	 * get the number of OBX reps for the specimen j in order observation i
	 * @param i order observation rep
	 * @param j specimen rep
	 * @return the number of OBX reps
	 */
	public int getSpecimenOBXCount(int i, int j)
	{
		return 0;
	}

	/**
	 * get the content type of the OBX segment
	 * @param i - obr group
	 * @param j - obx rep
	 * @return - the content type of this obx segment
	 */
	public OBX_CONTENT_TYPE getOBXContentType(int i, int j)
	{
		return OBX_CONTENT_TYPE.TEXT;
	}

	/**
	 *  Return true if an abnormal flag other than 'N' is returned by
	 *  getOBXAbnormalFlag( i, j ) for the OBX segment specified by j, in the
	 *  ith OBR group. Return false otherwise.
	 */
	public boolean isOBXAbnormal(int i, int j)
	{
		String abnormalFlags = getOBXAbnormalFlag(i,j);
		return !("N".equals(abnormalFlags));
	}

	public boolean isAbnormal()
	{
		for(int i = 0; i < getOBRCount(); i++)
		{
			for(int j = 0; j < getOBXCount(i); j++)
			{
				if(isOBXAbnormal(i, j))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return the obx value type
	 * @param i
	 * @param j
	 * @return String the obx value
	 */
	public String getOBXValueType(int i, int j)
	{
		return "";
	}

	/**
	 *  Return the identifier from jth OBX segment of the ith OBR group. It is
	 *  usually stored in the first component of the third field of the OBX
	 *  segment.
	 */
	public String getOBXIdentifier(int i, int j)
	{
		return "";
	}

	/**
	 *  Return the name of the jth OBX segment of the ith OBR group. It is
	 *  usually stored in the second component of the third field of the OBX
	 *  segment.
	 */
	public String getOBXName( int i, int j)
	{
		return "";
	}

    /**
     *  Return the result from the jth OBX segment of the ith OBR group
     */
    public String getOBXResult(int i, int j)
    {
        return getOBXResult(i, j, 1);
    }

	/**
	 *  Return the result from the jth OBX segment at the kth component of the ith OBR group
	 */
	public String getOBXResult(int i, int j, int k)
	{
		return "";
	}

	/**
	 *  Return the units from the jth OBX segment of the ith OBR group
	 */
	public String getOBXUnits( int i, int j)
	{
		return "";
	}

	/**
	 *  Return the reference range from the jth OBX segment of the ith OBR group
	 */
	public String getOBXReferenceRange( int i, int j)
	{
		return "";
	}

	/**
	 *  Retrieve the abnormal flag if any from the OBX segment specified by j in
	 *  the ith OBR group.
	 */
	public String getOBXAbnormalFlag( int i, int j)
	{
		return "N";
	}

	/**
	 *  Return the result status from the jth OBX segment of the ith OBR group
	 */
	public String getOBXResultStatus( int i, int j)
	{
		return "";
	}

	/**
	 *  Return the result status from the jth OBX segment of the ith OBR group, in a human-readable format
	 */
	public String getOBXResultStatusDisplayValue(int i, int j)
	{
		return getOBXResultStatus(i, j);
	}

	/**
	 *  Return the date and time of the observation referred to by the jth obx
	 *  segment of the ith obr group. If the date and time is not specified
	 *  within the obx segment it should be specified within the obr segment.
	 */
	public String getTimeStamp(int i, int j)
	{
		return "";
	}

	/**
	 *  Return the number of comments (usually NTE segments) following the jth
	 *  OBX segment of the ith OBR group.
	 */
	public int getOBXCommentCount( int i, int j)
	{
		return 0;
	}

	/**
	 *  Return the kth comment of the jth OBX segment of the ith OBR group
	 */
	public String getOBXComment( int i, int j, int k)
	{
		return "";
	}

	/**
	 *  Returns the number used to order labs with matching accession numbers.
	 *
	 *  - Multiple labs with the same accession number must display in a certain
	 *  order. They are ordered by their date but if two labs with the same
	 *  accession number have the same date they are ordered by the number
	 *  retrieved by this method
	 *
	 *  - The newest lab will have the greatest number returned from this method.
	 *
	 *  - If the hl7 messages do not contain a version number or other such
	 *  number, the total number of obx segments with final results should be
	 *  returned
	 */
	public int getOBXFinalResultCount()
	{
		int obrCount = getOBRCount();
		int obxCount;
		int count = 0;
		for (int i = 0; i < obrCount; i++) {
			obxCount = getOBXCount(i);
			for (int j = 0; j < obxCount; j++) {
				if (getOBXResultStatus(i, j).equals("F"))
				{
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * get the long name of the lab responsible for the given OBX segment
	 * @param i OBR rep
	 * @param k OBX rep
	 * @return lab name
	 */
	public String getPerformingOrganizationName(int i, int k)
	{
		return "";
	}

	/**
	 * get address string for the lab responsible for the given OBX segment
	 * @param i OBR rep
	 * @param k OBX rep
	 * @return lab address string
	 */
	public String getPerformingOrganizationAddress(int i, int k)
	{
		return null;
	}

	/**
	 * get the short name of the lab responsible for the lab
	 * @return lab short name
	 */
	public String getAssignedPatientLocation()
	{
		return "";
	}

	public abstract String getNteForOBX(int i,int j);

	/**
	 * @param obr segment index - 0 indexed
	 * @param obx segment index - 0 indexed
	 * @return true if the given segment has a linked OBR child segment
	 */
	public boolean hasChildOBR(int obr, int obx)
	{
		return false;
	}

	/**
	 * @param obr segment index - 0 indexed
	 * @param obx segment index - 0 indexed
	 * @return index of linked OBR child segment, or -1
	 */
	public List<Integer> getChildOBRIndexList(int obr, int obx)
	{
		return new ArrayList<>(0);
	}

	/**
	 * Return the name from the jth OBX segment of the ith OBR group,
	 * specific to obx segments within an OBR child segment, as it may map differently from regular OBXNames
	 * @param obr segment index - 0 indexed
	 * @param obx segment index - 0 indexed
	 * @return the name for display
	 */
	public String getChildOBR_OBXName(int obr, int obx)
	{
		return "";
	}

	/**
	 * Return the result from the jth OBX segment of the ith OBR group,
	 * specific to obx segments within an OBR child segment, as it may map differently from regular OBXResults
	 * @param obr segment index - 0 indexed
	 * @param obx segment index - 0 indexed
	 * @return the result for display
	 */
	public String getChildOBR_OBXResult(int obr, int obx)
	{
		return "";
	}

	/* ============================== Specimen ============================= */
	/**
	 * true if this order observation has at least one specimen segment
	 * @param i the order observation group to check
	 * @return true / false indicating existence of specimen segment
	 */
	public boolean hasSpecimenSegment(int i )
	{
		return false;
	}

	/**
	 * true if performing organization information is available
	 * @param i - the order observation group to check
	 * @param k - the obx rep to check
	 * @return - true if the performing organization is available
	 */
	public boolean hasPerformingOrganization(int i, int k)
	{
		return false;
	}

	/**
	 * return the number of specimen segments in the given order observation group
	 * @param i the order observation group to check
	 * @return count of specimen segments
	 */
	public int getSpecimenCount(int i)
	{
		return 0;
	}

	/**
	 * get the type string of the specimen j in order observation i
	 * @param i the order observation in which to look
	 * @param j the specimen to check
	 * @return the specimen type string
	 */
	public String getSpecimenType(int i, int j)
	{
		return "";
	}

	/**
	 * get the specimen site for specimen j in order observation i
	 * @param i the order observation in which to look
	 * @param j the specimen to check
	 * @return the specimen site string
	 */
	public String getSpecimenSite(int i, int j)
	{
		return "";
	}

	/**
	 * get the specimen collection datetime
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return the specimen collection date as a string
	 */
	public String getSpecimenCollectionDateTime(int i, int j)
	{
		return "";
	}

	/**
	 * get the specimen received datetime
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return the specimen received date as a string
	 */
	public String getSpecimenReceivedDateTime(int i, int j)
	{
		return "";
	}

	/**
	 * get extended specimen description built by concatenating specimen OBX segments
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return a specimen description string
	 */
	public String getSpecimenExtendedDescription(int i, int j)
	{
		String desc = "";
		for (int k = 0; k < getSpecimenOBXCount(i, j); k ++)
		{
			String obs = getSpecimenOBXObservationValue(i, j, k);
			desc += (obs != null ? obs: "") + " ";
		}
		return desc;
	}

	/**
	 * check if the specimen, j has an extended description
	 * @param i the order observation rep
	 * @param j the specimen rep
	 * @return true if the specimen has an extended description
	 */
	public boolean hasExtendedSpecimenDescription(int i, int j)
	{
		return getSpecimenOBXCount(i, j) != 0;
	}

	/**
	 * get the OBX observation value for order observation i , specimen j , OBX k
	 * @param i order observation rep
	 * @param j specimen rep
	 * @param k OBX rep
	 * @return the OBX Observation value string
	 */
	public String getSpecimenOBXObservationValue(int i, int j, int k)
	{
		return "";
	}

	/* ===================================== MISC ====================================== */

	/**
	 * Returns a string audit of the messages.  If not required handler should just return an empty string;
	 */
	public String audit()
	{
		return "";
	}

	public String getEncounterId()
	{
		return "";
	}

	public String getRadiologistInfo()
	{
		return "";
	}

	public boolean isUnstructured()
	{
		return false;
	}

	public boolean showStatusForUnstructured()
	{
		return false;
	}


	/* ================================== Extra Methods and helpers ==================================== */

	/**
	 * use the terser to retrieve segment info, with exception handling
	 * @param path - terser formatted segment path
	 * @return String value of the segment, or null if parsing the path failed
	 */
	protected String get(String path)
	{
		try
		{
			return terser.get(path);
		}
		catch(HL7Exception e)
		{
			logger.warn("Unable to get field at " + path, e);
			return null;
		}
	}

	/**
	 * Check if the specified component exists. true if it does, false otherwise
	 * @param path - the terser path to check
	 * @return - true if it exists, false otherwise
	 */
	protected boolean isComponentPresent(String path)
	{
		try
		{
			terser.get(path);
			return true;
		}
		catch (HL7Exception e)
		{
			return false;
		}
	}

	/**
	 * finds a count of the segments under the group name. This uses terser groups
	 * The basic group structure of HL7 is similar to this:
	 * ORU_R01/(RESPONSE|PATIENT_RESULT)/ORDER_OBSERVATION/OBSERVATION
	 * for searching an OBR count, the parent group would be RESPONSE, and the rep 0 (only 1 response per message)
	 * but OBX would have parent ORDER_OBSERVATION, with n reps
	 * Overload of getReps but searches at the root level of the message.
	 * @param groupName - the group to count
	 * @return - the group count matching the group name
	 */
	protected int getReps(String groupName)
	{
		int count = 0;
		try
		{
			count = message.getAll(groupName).length;
			logger.debug("get reps -> " + groupName + ": " + count);
		}
		catch(HL7Exception e) {
			logger.error("Terser Repetition Error", e);
		}
		return count;
	}

	/**
	 * finds a count of the segments under the group name. This uses terser groups
	 * The basic group structure of HL7 is similar to this:
	 * ORU_R01/(RESPONSE|PATIENT_RESULT)/ORDER_OBSERVATION/OBSERVATION
	 * for searching an OBR count, the parent group would be RESPONSE, and the rep 0 (only 1 response per message)
	 * but OBX would have parent ORDER_OBSERVATION, with n reps
	 * @param parentGroupName - name of the group to search within. this is paired with the repetition number.
	 * @param parentRep - the parent repetition to search for the segments in
	 * @param groupName - the group name type to count
	 * @return - the group count matching the group name
	 */
	protected int getReps(String parentGroupName, int parentRep, String groupName)
	{
		int count = 0;
		try
		{
			Group g = findGroupFromTop(parentGroupName, parentRep);
			count = g.getAll(groupName).length;
			logger.debug("get reps -> " + parentGroupName + "("+parentRep+")" + groupName + ": " + count);
		}
		catch(HL7Exception e) {
			logger.error("Terser Repetition Error", e);
		}
		return count;
	}

	/**
	 * finds a count of the segments under the group name. This uses terser groups
	 * This overloads the basic getReps method allowing a sub group with index.
	 * This is used for nested groups such as OBX NTE comments.
	 * Example: for searching an OBX NTE count, the parent group would be ORDER_OBSERVATION(i),the sub-parent OBSERVATION(j), groupName NTE
	 * @param parentGroupName - name of the group to search within. this is paired with the repetition number.
	 * @param parentRep - the parent repetition to search for the segments in
	 * @param subParentGroupName - name of the sup group to search within. this is paired with the sub repetition number.
	 * @param subParentRep - the sub parent repetition to search for the segments in
	 * @param groupName - the group name type to count
	 * @return - the group count matching the group name
	 */
	protected int getReps(String parentGroupName, int parentRep, String subParentGroupName, int subParentRep, String groupName)
	{
		int count = 0;
		try
		{
			Group g = findGroupFromTop(parentGroupName, parentRep);
			Group g2 = (Group) g.get(subParentGroupName, subParentRep);
			count = g2.getAll(groupName).length;
			logger.debug("get reps -> " + g.getName() + "("+parentRep+")/" + g2.getName() + "("+ subParentRep +")" + groupName + ": " + count);
		}
		catch(HL7Exception e) {
			logger.error("Terser Repetition Error", e);
		}
		return count;
	}

	protected int getReps(String[] parentGroups, int[] parentReps, String subGroup)
	{
		try
		{
			Group currGroup = null;
			for (int i =0; i < parentGroups.length; i ++)
			{
				if (currGroup == null)
				{
					currGroup = findGroupFromTop(parentGroups[i], parentReps[i]);
				}
				else
				{
					currGroup = (Group) currGroup.get(parentGroups[i], parentReps[i]);
				}
			}

			if (currGroup != null)
			{
				return currGroup.getAll(subGroup).length;
			}
		}
		catch (HL7Exception e)
		{
			logger.error("Terser Repetition Error", e);
		}
		return 0;
	}

	/**
	 * convenient way to always search the entire hl7 message for the given group
	 */
	private Group findGroupFromTop(String groupName, int groupRep) throws HL7Exception
	{
		SegmentFinder finder = terser.getFinder();
		finder.reset(); // reset or it only searches from current parse position
		logger.debug("Find Group: " + groupName + "(" + groupRep + ")");
		return finder.findGroup(groupName, groupRep);
	}

	protected String formatDateTime(String plain)
	{
		return getHL7V2DateTime(plain, ConversionUtils.DEFAULT_TS_PATTERN);
	}
	protected String formatDate(String plain)
	{
		return getHL7V2DateTime(plain, ConversionUtils.DEFAULT_DATE_PATTERN);
	}
	protected String formatTime(String plain)
	{
		return getHL7V2DateTime(plain, ConversionUtils.DEFAULT_TIME_PATTERN);
	}

	/**
	 *
	 * @param dateInput
	 * @param outputFormat
	 * @return formated date based on @param outputFormat.
	 */
	protected String getHL7V2DateTime(String dateInput, String outputFormat)
	{
		String dateOutput = "";

		if(dateInput != null && !dateInput.trim().isEmpty())
		{
			String datePattern = matchHL7V2DateTimePattern(dateInput);
			dateOutput = formatDateTime(dateInput, datePattern, outputFormat);
		}

		return dateOutput;
	}

	/**
	 * to match this HL7 v2.* version time format design YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ]
	 * @param dateInput
	 * @return datePattern String that is matched, if no matched date pattern, return null
	 */
	private String matchHL7V2DateTimePattern(String dateInput)
	{
		String datePattern = ConversionUtils.HL7_V2_DATE_TIME_OFFICIAL_PATTERN;

		if(datePattern.length() > dateInput.length())
		{
			// For this format YYYYMMDD[HH[MM[SS[.S[S[S[S]]]]]]][+/-ZZZZ]
			datePattern = datePattern.substring(0, dateInput.length());
		}

		return datePattern;
	}

	private String formatDateTime(String plain, String inFormat, String outFormat)
	{
		String formatted = "";

		if( plain != null && !plain.trim().isEmpty())
		{
			try
			{
				DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
				DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);

				// use format builder to set default missing time values
				DateTimeFormatter customFormatter = new DateTimeFormatterBuilder().append(inFormatter)
						.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
						.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
						.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
						.toFormatter();

				// attempt to parse as a zonedDateTime, otherwise a LocalDateTime
				TemporalAccessor parsed = customFormatter.parseBest(plain, ZonedDateTime::from, LocalDateTime::from);
				ZonedDateTime zonedDateTime;

				if(parsed instanceof ZonedDateTime)
				{
					zonedDateTime = (ZonedDateTime) parsed;
				}
				else if(parsed instanceof LocalDateTime)
				{
					LocalDateTime dt = (LocalDateTime) parsed;
					zonedDateTime = dt.atZone(ZoneId.systemDefault());
				}
				else
				{
					// shouldn't be possible
					throw new RuntimeException("Unknown Temporal type: " + parsed.getClass().getName());
				}

				formatted = ConversionUtils.toDateTimeString(zonedDateTime,
						DateTimeFormatter.ofPattern(outFormat).withZone(ZoneId.systemDefault()));
			}
			catch (DateTimeParseException exception)
			{
				logger.error("Error parsing datetime: ", exception);
			}
		}

		return formatted;
	}

	public String getAge()
	{
		String age = "N/A";
		String dob = getDOB();
		try
		{
			// Some examples
			if(StringUtils.isNotBlank(dob))
			{
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = formatter.parse(dob);
				age = UtilDateUtilities.calcAge(date);
			}
		}
		catch(ParseException e)
		{
			logger.error("Could not get age", e);
		}
		return age;
	}

	protected String getString(String retrieve)
	{
		return StringUtils.trimToEmpty(retrieve);
	}

	// kept for older parsers
	protected String getFullDocNameV25(ca.uhn.hl7v2.model.v25.datatype.XCN xcn) {
		String docName = "";

		if (xcn.getPrefixEgDR().getValue() != null) docName = xcn.getPrefixEgDR().getValue();

		if (xcn.getGivenName().getValue() != null) {
			if (docName.equals("")) docName = xcn.getGivenName().getValue();
			else docName = docName + " " + xcn.getGivenName().getValue();

		}
		if (xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue() != null) {
			if (docName.equals("")) docName = xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
			else docName = docName + " " + xcn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
		}
		if (xcn.getFamilyName().getSurname().getValue() != null) {
			if (docName.equals("")) docName = xcn.getFamilyName().getSurname().getValue();
			else docName = docName + " " + xcn.getFamilyName().getSurname().getValue();

		}
		if (xcn.getSuffixEgJRorIII().getValue() != null) {
			if (docName.equals("")) docName = xcn.getSuffixEgJRorIII().getValue();
			else docName = docName + " " + xcn.getSuffixEgJRorIII().getValue();
		}
		if (xcn.getDegreeEgMD().getValue() != null) {
			if (docName.equals("")) docName = xcn.getDegreeEgMD().getValue();
			else docName = docName + " " + xcn.getDegreeEgMD().getValue();
		}

		return docName;
	}

	// kept for older parsers
	protected String getFullDocName(ca.uhn.hl7v2.model.v23.datatype.XCN docSeg) {
		String docName = "";

		if (docSeg.getPrefixEgDR().getValue() != null) docName = docSeg.getPrefixEgDR().getValue();

		if (docSeg.getGivenName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getGivenName().getValue();
			else docName = docName + " " + docSeg.getGivenName().getValue();
		}
		if (docSeg.getMiddleInitialOrName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getMiddleInitialOrName().getValue();
			else docName = docName + " " + docSeg.getMiddleInitialOrName().getValue();
		}
		if (docSeg.getFamilyName().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getFamilyName().getValue();
			else docName = docName + " " + docSeg.getFamilyName().getValue();
		}
		if (docSeg.getSuffixEgJRorIII().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getSuffixEgJRorIII().getValue();
			else docName = docName + " " + docSeg.getSuffixEgJRorIII().getValue();
		}
		if (docSeg.getDegreeEgMD().getValue() != null) {
			if (docName.equals("")) docName = docSeg.getDegreeEgMD().getValue();
			else docName = docName + " " + docSeg.getDegreeEgMD().getValue();
		}

		return (docName);
	}

	protected String getRootGroupName()
	{
		if (getMsgVersion() == DataTypeUtils.HL7_VERSION.VERSION_251)
		{
			return "PATIENT_RESULT";
		}
		else
		{
			return "RESPONSE";
		}
	}

	protected DataTypeUtils.HL7_VERSION getMsgVersion()
	{
		return DataTypeUtils.getHL7Version(this.message);
	}
}
