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
package org.oscarehr.integration.SRFax.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.integration.SRFax.api.result.GetFaxInboxResult;
import org.oscarehr.integration.SRFax.api.result.GetFaxOutboxResult;
import org.oscarehr.integration.SRFax.api.result.SRFaxFaxStatusResult;
import org.oscarehr.integration.SRFax.api.result.GetUsageResult;
import org.oscarehr.integration.SRFax.api.resultWrapper.ListWrapper;
import org.oscarehr.integration.SRFax.api.resultWrapper.SingleWrapper;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SRFaxApiConnector
{
	private static final Logger logger = MiscUtils.getLogger();

	private static final String SERVER_URL = "https://www.srfax.com/SRF_SecWebSvc.php";

	private static final String ACCESS_ID = "access_id";
	private static final String ACCESS_PW = "access_pwd";
	private static final String ACTION = "action";
	private static final String ACTION_QUEUE_FAX = "Queue_Fax";
	private static final String ACTION_GET_FAX_STATUS = "Get_FaxStatus";
	private static final String ACTION_GET_MULTI_FAX_STATUS = "Get_MultiFaxStatus";
	private static final String ACTION_GET_FAX_INBOX = "Get_Fax_Inbox";
	private static final String ACTION_GET_FAX_OUTBOX = "Get_Fax_Outbox";
	private static final String ACTION_RETRIEVE_FAX = "Retrieve_Fax";
	private static final String ACTION_UPDATE_VIEWED_STATUS = "Update_Viewed_Status";
	private static final String ACTION_DELETE_FAX = "Delete_Fax";
	private static final String ACTION_STOP_FAX = "Stop_Fax";
	private static final String ACTION_GET_FAX_USAGE = "Get_Fax_Usage";

	private static final String S_CALLER_ID = "sCallerID";
	private static final String S_SENDER_EMAIL = "sSenderEmail";
	private static final String S_FAX_TYPE = "sFaxType";
	private static final String S_TO_FAX_NUMBER = "sToFaxNumber";
	private static final String S_RESPONSE_FORMAT = "sResponseFormat";
	private static final String S_ACCOUNT_CODE = "sAccountCode";
	private static final String S_RETRIES = "sRetries";
	private static final String S_COVER_PAGE = "sCoverPage";
	private static final String S_CP_FROM_NAME = "sCPFromName";
	private static final String S_CP_TO_NAME = "sCPToName";
	private static final String S_CP_ORGANIZATION = "sCPOrganization";
	private static final String S_CP_SUBJECT = "sCPSubject";
	private static final String S_CP_COMMENTS = "sCPComments";
	private static final String S_FILE_NAME_BASE = "sFileName_";
	private static final String S_FILE_NAME_WILDCARD = S_FILE_NAME_BASE + "*";
	private static final String S_FILE_CONTENT_BASE = "sFileContent_";
	private static final String S_FILE_CONTENT_WILDCARD = S_FILE_CONTENT_BASE + "*";
	private static final String S_NOTIFY_URL = "sNotifyURL";
	private static final String S_FAX_FROM_HEADER = "sFaxFromHeader";
	private static final String S_QUEUE_FAX_DATE = "sQueueFaxDate";
	private static final String S_QUEUE_FAX_TIME = "sQueueFaxTime";
	private static final String S_PERIOD = "sPeriod";
	private static final String S_START_DATE = "sStartDate";
	private static final String S_END_DATE = "sEndDate";
	private static final String S_INCLUDE_SUB_USERS = "sIncludeSubUsers";
	private static final String S_FAX_DETAILS_ID = "sFaxDetailsID";
	private static final String S_FAX_FILE_NAME = "sFaxFileName";
	private static final String S_VIEWED_STATUS = "sViewedStatus";
	private static final String S_DIRECTION = "sDirection";
	private static final String S_MARKAS_VIEWED = "sMarkasViewed";
	private static final String S_FAX_FORMAT = "sFaxFormat";
	private static final String S_SUB_USER_ID = "sSubUserID";

	public static final String DATE_FORMAT = "yyyyMMdd";

	public static final String RESPONSE_FORMAT_JSON = "JSON";
	public static final String RESPONSE_FORMAT_XML = "XML";
	public static final String PERIOD_ALL = "ALL";
	public static final String PERIOD_RANGE = "RANGE";
	public static final String FAX_TYPE_SINGLE = "SINGLE";
	public static final String FAX_TYPE_BROADCAST = "BROADCAST";
	public static final String RETRIEVE_DIRECTION_IN = "IN";
	public static final String RETRIEVE_DIRECTION_OUT = "IN";
	public static final String RETRIEVE_DOC_FORMAT = "PDF";
	public static final String RETRIEVE_DONT_CHANGE_STATUS = "N";
	public static final String MARK_AS_READ = "Y";
	public static final String MARK_AS_UNREAD = "N";
	public static final String VIEWED_STATUS_ALL = "ALL";
	public static final String VIEWED_STATUS_READ = "READ";
	public static final String VIEWED_STATUS_UNREAD = "UNREAD";
	public static final List<String> validCoverLetterNames = new ArrayList<String>(4) {{
		add("Basic");
		add("Standard");
		add("Company");
		add("Personal");
	}};

	public static final String RESPONSE_STATUS_SENT="Sent";
	public static final String RESPONSE_STATUS_FAILED="Failed";
	public static final String RESPONSE_STATUS_PROGRESS="In Progress";
	public static final String RESPONSE_STATUS_EMAIL="Sending Email";
	public static final List<String> RESPONSE_STATUSES_FINAL = new ArrayList<String>(2) {{
		add(RESPONSE_STATUS_SENT);
		add(RESPONSE_STATUS_FAILED);
	}};

	private final String access_id;
	private final String access_pwd;

	public SRFaxApiConnector(String username, String password)
	{
		this.access_id = username;
		this.access_pwd = password;
	}

	private SingleWrapper<Integer> queueFax(Map<String, String> parameters)
	{
		String[] requiredFields = {S_CALLER_ID, S_SENDER_EMAIL, S_FAX_TYPE, S_TO_FAX_NUMBER};
		String[] optionalFields = {
				S_RESPONSE_FORMAT, S_ACCOUNT_CODE, S_RETRIES, S_COVER_PAGE,
				S_CP_FROM_NAME, S_CP_TO_NAME, S_CP_ORGANIZATION,
				S_CP_SUBJECT, S_CP_COMMENTS, S_FILE_NAME_WILDCARD, S_FILE_CONTENT_WILDCARD,
				S_NOTIFY_URL, S_FAX_FROM_HEADER, S_QUEUE_FAX_DATE, S_QUEUE_FAX_TIME
		};
		String result = processRequest(ACTION_QUEUE_FAX, requiredFields, optionalFields, parameters);
		return processSingleResponse(result);
	}

	/** Queue a fax (all available api options)*/
	public SingleWrapper<Integer> queueFax(String sCallerID, String sSenderEmail, String sFaxType, String sToFaxNumber,
	                                       Map<String,String> sFileMap,
	                                       String sResponseFormat,
	                                       String sAccountCode,
	                                       String sRetries,
	                                       String sCoverPage,
	                                       String sCPFromName,
	                                       String sCPToName,
	                                       String sCPOrganization,
	                                       String sCPSubject,
	                                       String sCPComments,
	                                       String sNotifyURL,
	                                       String sFaxFromHeader,
	                                       String sQueueFaxDate,
	                                       String sQueueFaxTime)
	{
		Map<String, String> parameters = new HashMap<>();
		parameters.put(S_CALLER_ID, sCallerID);
		parameters.put(S_SENDER_EMAIL, sSenderEmail);
		parameters.put(S_FAX_TYPE, sFaxType);
		parameters.put(S_TO_FAX_NUMBER, sToFaxNumber);

		if(sFileMap != null)
		{
			int counter = 1;
			for(Map.Entry<String,String> entry : sFileMap.entrySet())
			{
				parameters.put(S_FILE_NAME_BASE + counter, entry.getKey());
				parameters.put(S_FILE_CONTENT_BASE + counter, entry.getValue());
				counter++;
			}
		}
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		putIfPresent(parameters, S_ACCOUNT_CODE, sAccountCode);
		putIfPresent(parameters, S_RETRIES, sRetries);
		putIfPresent(parameters, S_COVER_PAGE, sCoverPage);
		putIfPresent(parameters, S_CP_FROM_NAME, sCPFromName);
		putIfPresent(parameters, S_CP_TO_NAME, sCPToName);
		putIfPresent(parameters, S_CP_ORGANIZATION, sCPOrganization);
		putIfPresent(parameters, S_CP_SUBJECT, sCPSubject);
		putIfPresent(parameters, S_CP_COMMENTS, sCPComments);
		putIfPresent(parameters, S_NOTIFY_URL, sNotifyURL);
		putIfPresent(parameters, S_FAX_FROM_HEADER, sFaxFromHeader);
		putIfPresent(parameters, S_QUEUE_FAX_DATE, sQueueFaxDate);
		putIfPresent(parameters, S_QUEUE_FAX_TIME, sQueueFaxTime);

		return queueFax(parameters);
	}
	/** Queue a standard fax with all cover letter options*/
	public SingleWrapper<Integer> queueFax(String sCallerID, String sSenderEmail, String sToFaxNumber,
	                                       Map<String,String> sFileMap,
	                                       String sCoverPage,
	                                       String sCPFromName,
	                                       String sCPToName,
	                                       String sCPOrganization,
	                                       String sCPSubject,
	                                       String sCPComments,
	                                       String sFaxFromHeader)
	{
		return queueFax(sCallerID, sSenderEmail, FAX_TYPE_SINGLE, sToFaxNumber, sFileMap, RESPONSE_FORMAT_JSON, null, null,
				sCoverPage, sCPFromName, sCPToName, sCPOrganization, sCPSubject, sCPComments, null, sFaxFromHeader, null, null);
	}

	/** Queue a standard fax with basic cover letter */
	public SingleWrapper<Integer> queueFax(String sCallerID, String sSenderEmail, String sToFaxNumber, Map<String,String> sFileMap, String sCoverPage)
	{
		return queueFax(sCallerID, sSenderEmail, FAX_TYPE_SINGLE, sToFaxNumber, sFileMap, RESPONSE_FORMAT_JSON, null, null,
				sCoverPage, null, null, null, null, null, null, null, null, null);
	}

	/** Queue a standard fax with no cover letter */
	public SingleWrapper<Integer> queueFax(String sCallerID, String sSenderEmail, String sToFaxNumber, Map<String,String> sFileMap)
	{
		return queueFax(sCallerID, sSenderEmail, FAX_TYPE_SINGLE, sToFaxNumber, sFileMap, RESPONSE_FORMAT_JSON, null, null,
				null, null, null, null, null, null, null, null, null, null);
	}

	private SingleWrapper<SRFaxFaxStatusResult> getFaxStatus(Map<String, String> parameters)
	{
		String[] requiredFields = {S_FAX_DETAILS_ID};
		String[] optionalFields = {S_RESPONSE_FORMAT};
		String result = processRequest(ACTION_GET_FAX_STATUS, requiredFields, optionalFields, parameters);
		return processSingleResponse(result, new TypeReference<SingleWrapper<SRFaxFaxStatusResult>>(){});
	}
	public SingleWrapper<SRFaxFaxStatusResult> getFaxStatus(String sFaxDetailsID, String sResponseFormat)
	{
		Map<String, String> parameters = new HashMap<>();
		parameters.put(S_FAX_DETAILS_ID, sFaxDetailsID);
		parameters.put(S_RESPONSE_FORMAT, sResponseFormat);
		return getFaxStatus(parameters);
	}
	public SingleWrapper<SRFaxFaxStatusResult> getFaxStatus(String sFaxDetailsID)
	{
		return getFaxStatus(sFaxDetailsID, RESPONSE_FORMAT_JSON);
	}

	private ListWrapper<SRFaxFaxStatusResult> getMultiFaxStatus(Map<String, String> parameters)
	{
		String[] requiredFields = {S_FAX_DETAILS_ID};
		String[] optionalFields = {S_RESPONSE_FORMAT};
		String result = processRequest(ACTION_GET_MULTI_FAX_STATUS, requiredFields, optionalFields, parameters);
		return processListResponse(result, new TypeReference<ListWrapper<SRFaxFaxStatusResult>>(){});
	}
	public ListWrapper<SRFaxFaxStatusResult> getMultiFaxStatus(List<String> sFaxDetailsIDList, String sResponseFormat)
	{
		Map<String, String> parameters = new HashMap<>();
		parameters.put(S_FAX_DETAILS_ID, String.join("|", sFaxDetailsIDList));
		parameters.put(S_RESPONSE_FORMAT, sResponseFormat);
		return getMultiFaxStatus(parameters);
	}
	public ListWrapper<SRFaxFaxStatusResult> getMultiFaxStatus(List<String> sFaxDetailsIDList)
	{
		return getMultiFaxStatus(sFaxDetailsIDList, RESPONSE_FORMAT_JSON);
	}

	private ListWrapper<GetFaxInboxResult> getFaxInbox(Map<String, String> parameters)
	{
		String[] requiredFields = {};
		String[] optionalFields = {S_RESPONSE_FORMAT, S_PERIOD, S_START_DATE, S_END_DATE, S_VIEWED_STATUS, S_INCLUDE_SUB_USERS};
		String result = processRequest(ACTION_GET_FAX_INBOX, requiredFields, optionalFields, parameters);
		return processListResponse(result, new TypeReference<ListWrapper<GetFaxInboxResult>>(){});
	}
	public ListWrapper<GetFaxInboxResult> getFaxInbox(String sResponseFormat, String sPeriod,
	                                                  String sStartDate, String sEndDate, String sViewedStatus,
	                                                  String sIncludeSubUsers)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		putIfPresent(parameters, S_PERIOD, sPeriod);
		putIfPresent(parameters, S_START_DATE, sStartDate);
		putIfPresent(parameters, S_END_DATE, sEndDate);
		putIfPresent(parameters, S_VIEWED_STATUS, sViewedStatus);
		putIfPresent(parameters, S_INCLUDE_SUB_USERS, sIncludeSubUsers);
		return getFaxInbox(parameters);
	}

	public ListWrapper<GetFaxInboxResult> getFaxInbox(String sPeriod, String sStartDate, String sEndDate, String sViewedStatus,
	                                                  String sIncludeSubUsers)
	{
		return getFaxInbox(RESPONSE_FORMAT_JSON, sPeriod, sStartDate, sEndDate, sViewedStatus, sIncludeSubUsers);
	}

	private ListWrapper<GetFaxOutboxResult> getFaxOutbox(Map<String, String> parameters)
	{
		String[] requiredFields = {};
		String[] optionalFields = {S_RESPONSE_FORMAT, S_PERIOD, S_START_DATE, S_END_DATE, S_INCLUDE_SUB_USERS};
		String result = processRequest(ACTION_GET_FAX_OUTBOX, requiredFields, optionalFields, parameters);
		return processListResponse(result, new TypeReference<ListWrapper<GetFaxOutboxResult>>(){});
	}
	public ListWrapper<GetFaxOutboxResult> getFaxOutbox(String sResponseFormat, String sPeriod,
	                                                    String sStartDate, String sEndDate,
	                                                    String sIncludeSubUsers)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		putIfPresent(parameters, S_PERIOD, sPeriod);
		putIfPresent(parameters, S_START_DATE, sStartDate);
		putIfPresent(parameters, S_END_DATE, sEndDate);
		putIfPresent(parameters, S_INCLUDE_SUB_USERS, sIncludeSubUsers);
		return getFaxOutbox(parameters);
	}

	private SingleWrapper<String> retrieveFax(Map<String, String> parameters)
	{
		String[] requiredFields = {S_FAX_FILE_NAME + "|" + S_FAX_DETAILS_ID, S_DIRECTION};
		String[] optionalFields = {S_FAX_FORMAT, S_MARKAS_VIEWED, S_RESPONSE_FORMAT, S_SUB_USER_ID};
		String result = processRequest(ACTION_RETRIEVE_FAX, requiredFields, optionalFields, parameters);
		return processSingleResponse(result);
	}
	public SingleWrapper<String> retrieveFax(String sFaxFileName, String sFaxDetailsID, String sDirection,
	                                         String sFaxFormat, String sMarkasViewed, String sResponseFormat, String sSubUserID)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_FAX_FILE_NAME, sFaxFileName);
		putIfPresent(parameters, S_FAX_DETAILS_ID, sFaxDetailsID);
		putIfPresent(parameters, S_DIRECTION, sDirection);
		putIfPresent(parameters, S_FAX_FORMAT, sFaxFormat);
		putIfPresent(parameters, S_MARKAS_VIEWED, sMarkasViewed);
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		putIfPresent(parameters, S_SUB_USER_ID, sSubUserID);
		return retrieveFax(parameters);
	}

	public SingleWrapper<String> retrieveFax(String sFaxFileName, String sFaxDetailsID, String sDirection)
	{
		return retrieveFax(sFaxFileName, sFaxDetailsID, sDirection, RETRIEVE_DOC_FORMAT, RETRIEVE_DONT_CHANGE_STATUS, RESPONSE_FORMAT_JSON, null);
	}

	private SingleWrapper<String> updateViewedStatus(Map<String, String> parameters)
	{
		String[] requiredFields = {S_FAX_FILE_NAME + "|" + S_FAX_DETAILS_ID, S_DIRECTION, S_MARKAS_VIEWED};
		String[] optionalFields = {S_RESPONSE_FORMAT};
		String result = processRequest(ACTION_UPDATE_VIEWED_STATUS, requiredFields, optionalFields, parameters);
		return processSingleResponse(result);
	}
	public SingleWrapper<String> updateViewedStatus(String sFaxFileName, String sFaxDetailsID, String sDirection,
	                                                String sMarkasViewed, String sResponseFormat)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_FAX_FILE_NAME, sFaxFileName);
		putIfPresent(parameters, S_FAX_DETAILS_ID, sFaxDetailsID);
		putIfPresent(parameters, S_DIRECTION, sDirection);
		putIfPresent(parameters, S_MARKAS_VIEWED, sMarkasViewed);
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		return updateViewedStatus(parameters);
	}

	public SingleWrapper<String> updateViewedStatus(String sFaxFileName, String sFaxDetailsID, String sDirection, String sMarkasViewed)
	{
		return updateViewedStatus(sFaxFileName, sFaxDetailsID, sDirection, sMarkasViewed, RESPONSE_FORMAT_JSON);
	}

	private SingleWrapper<String> deleteFax(Map<String, String> parameters)
	{
		String[] requiredFields = {S_DIRECTION, S_FAX_FILE_NAME + "_*|" + S_FAX_DETAILS_ID + "_*"};
		String[] optionalFields = {S_RESPONSE_FORMAT};
		String result = processRequest(ACTION_DELETE_FAX, requiredFields, optionalFields, parameters);
		return processSingleResponse(result);
	}
	public SingleWrapper<String> deleteFax(String sDirection, String sFaxFileName, String sFaxDetailsID, String sResponseFormat)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_FAX_FILE_NAME, sFaxFileName);
		putIfPresent(parameters, S_FAX_DETAILS_ID, sFaxDetailsID);
		putIfPresent(parameters, S_DIRECTION, sDirection);
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		return deleteFax(parameters);
	}

	private SingleWrapper<String> stopFax(Map<String, String> parameters)
	{
		String[] requiredFields = {S_FAX_DETAILS_ID};
		String[] optionalFields = {S_RESPONSE_FORMAT};
		String result = processRequest(ACTION_STOP_FAX, requiredFields, optionalFields, parameters);
		return processSingleResponse(result);
	}
	public SingleWrapper<String> stopFax(String sFaxDetailsID, String sResponseFormat)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_FAX_DETAILS_ID, sFaxDetailsID);
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		return stopFax(parameters);
	}

	private ListWrapper<GetUsageResult> getFaxUsage(Map<String, String> parameters)
	{
		String[] requiredFields = {};
		String[] optionalFields = {S_RESPONSE_FORMAT, S_PERIOD, S_START_DATE, S_END_DATE, S_INCLUDE_SUB_USERS};
		String result = processRequest(ACTION_GET_FAX_USAGE, requiredFields, optionalFields, parameters);
		return processListResponse(result, new TypeReference<ListWrapper<GetUsageResult>>(){});
	}

	public ListWrapper<GetUsageResult> getFaxUsage(String sResponseFormat, String sPeriod, String sStartDate, String sEndDate, String sIncludeSubUsers)
	{
		Map<String, String> parameters = new HashMap<>();
		putIfPresent(parameters, S_RESPONSE_FORMAT, sResponseFormat);
		putIfPresent(parameters, S_PERIOD, sPeriod);
		putIfPresent(parameters, S_START_DATE, sStartDate);
		putIfPresent(parameters, S_END_DATE, sEndDate);
		putIfPresent(parameters, S_INCLUDE_SUB_USERS, sIncludeSubUsers);
		return getFaxUsage(parameters);
	}

	public ListWrapper<GetUsageResult> getFaxUsageByRange(String sStartDate, String sEndDate, String sIncludeSubUsers)
	{
		return getFaxUsage(RESPONSE_FORMAT_JSON, PERIOD_RANGE, sStartDate, sEndDate, sIncludeSubUsers);
	}

	/*******************INTERNAL FUNCTIONS*********************************/

	private static boolean putIfPresent(Map<String,String> parameterMap, String key, String optionalValue)
	{
		if(optionalValue != null)
		{
			parameterMap.put(key, optionalValue);
			return true;
		}
		return false;
	}

	private static <T> ListWrapper processListResponse(String response, TypeReference typeReference)
	{
		ListWrapper<T> result = null;
		try
		{
			if (response.trim().isEmpty())
			{// if the response is empty. SRFax has locked the account
				logger.warn("API Response Error: Account is blocked at this IP");
				result = new ListWrapper<>();
				result.setStatus("Error");
				result.setError("Account is Blocked at this IP");
			}
			else
			{
				JSONObject json = new JSONObject(response);
				String status = json.getString("Status");
				if (ListWrapper.STATUS_SUCCESS.equals(status))
				{
					ObjectMapper mapper = new ObjectMapper();
					result = (ListWrapper<T>) mapper.readValue(response, typeReference);
				} else
				{
					logger.warn("API Response Failure: " + response);
					result = new ListWrapper<>();
					result.setStatus(status);
					result.setError(json.getString("Result"));
				}
			}
		}
		catch(IOException e)
		{
			logger.error("Error", e);
		}
		return result;
	}

	private static <T> SingleWrapper processSingleResponse(String response)
	{
		return processSingleResponse(response, new TypeReference<SingleWrapper<T>>(){});
	}
	private static <T> SingleWrapper processSingleResponse(String response, TypeReference typeReference)
	{
		SingleWrapper<T> result = null;
		if (response.trim().isEmpty())
		{// if the response is empty. SRFax has locked the account
			logger.warn("API Response Error: Account is blocked at this IP");
			result = new SingleWrapper<>();
			result.setStatus("Error");
			result.setError("Account is Blocked at this IP");
		}
		else
		{
			JSONObject json = new JSONObject(response);
			String status = json.getString("Status");
			try
			{
				if (SingleWrapper.STATUS_SUCCESS.equals(status))
				{
					ObjectMapper mapper = new ObjectMapper();
					result = (SingleWrapper<T>) mapper.readValue(response, typeReference);
				} else
				{
					logger.warn("API Response Failure: " + response);
					result = new SingleWrapper<>();
					result.setStatus(status);
					result.setError(json.getString("Result"));
				}
			} catch (IOException e)
			{
				logger.error("Error", e);
			}
		}
		return result;
	}

	private String processRequest(String action, String[] requiredFields, String[] optionalFields, Map<String, String> parameters)
	{
		validateRequiredVariables(requiredFields, parameters);
		Map<String, String> postVariables = preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put(ACTION, action);
		postVariables.put(ACCESS_ID, access_id);
		postVariables.put(ACCESS_PW, access_pwd);

		return postRequest(postVariables);
	}

	private String postRequest(Map<String, String> postVariables)
	{
		String result = "";
		try
		{
			HttpClient httpClient = new DefaultHttpClient();

			logger.debug("POST URL: " + SERVER_URL);
			HttpPost httpPost = new HttpPost(SERVER_URL);

			// convert map to post-able list
			ArrayList<NameValuePair> postParameters = new ArrayList<>(postVariables.size());
			for(Map.Entry<String, String> entry : postVariables.entrySet())
			{
				postParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(postParameters);
			httpPost.setEntity(urlEntity);

			// execute api call
			HttpResponse httpResponse = httpClient.execute(httpPost);
			logger.debug("RESPONSE INFO:\n" +
					"statusCode=>" + httpResponse.getStatusLine().getStatusCode() + ",\n" +
					"reason=>" + httpResponse.getStatusLine().getReasonPhrase());

			HttpEntity entity = httpResponse.getEntity();
			if(entity != null)
			{
				try(InputStream inputStream = entity.getContent())
				{
					result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				}
			}
		}
		catch(IOException e)
		{
			logger.error("Error", e);
			throw new FaxApiConnectionException(e, "fax.exception.connectionError.srfax");
		}
		return result;
	}

	private Map<String, String> preparePostVariables(String[] requiredFields, String[] optionalFields, Map<String, String> parameters)
	{
		Map<String, String> postVariables = new HashMap<>();

		List<String> inputVariables = new ArrayList<>(requiredFields.length + optionalFields.length);
		inputVariables.addAll(Arrays.asList(requiredFields));
		inputVariables.addAll(Arrays.asList(optionalFields));

		for(String field : inputVariables)
		{
			if(field.endsWith("*") && field.indexOf('|') == -1) // non-piped wildcard
			{
				String fieldPrefix = field.replace("*", "");
				Map<String, String> wildCards = _getWildcardVariables(fieldPrefix, parameters);
				postVariables = _mergeDictionaries(postVariables, wildCards);
			}
			else
			{
				if(field.contains("|")) // piped, non-wildcard
				{
					String[] pipedFields = field.split("\\|");

					for(String pipedField : pipedFields)
					{
						if(pipedField.endsWith("*")) // piped wildcard
						{
							String fieldPrefix = pipedField.replace("*", "");
							Map<String, String> wildCards = _getWildcardVariables(fieldPrefix, parameters);
							postVariables = _mergeDictionaries(postVariables, wildCards);
						}
						else
						{
							if(parameters.containsKey(pipedField))
							{
								String value = parameters.get(pipedField);
								if(value.length() > 0)
								{
									postVariables.put(pipedField, value);
								}
							}
						}
					}
				}
				else //non-special fieldname
				{
					if(parameters.containsKey(field))
					{
						postVariables.put(field, parameters.get(field));
					}
				}
			}

		}
		return postVariables;
	}


	private Map<String, String> _getWildcardVariables(String fieldPrefix, Map<String, String> parameters)
	{
		Map<String, String> wildCards = new HashMap<>();
		boolean done = false;
		int suffix = 1;

		while(!done)
		{
			String field = fieldPrefix + suffix;

			if(parameters.containsKey(field))
			{
				String value = parameters.get(field);

				if(value.length() > 0) // add variable to the collection
				{
					wildCards.put(field, value);
				}
				else // field value is empty, so finish
				{
					done = true;
				}
			}
			else
			{
				done = true;
			}

			suffix++;

			// fail safe to ensure no infinite loops
			if(suffix > 1000)
			{
				done = true;
			}

		}
		return wildCards;
	}

	private void validateRequiredVariables(String[] requiredVariables, Map<String, String> parameters)
	{
		for(String field : requiredVariables)
		{
			if(field.endsWith("*") && !field.contains("|")) // non piped wildcard variable.  check for first instance
			{
				String fieldPrefix = field.replace("*", "");
				String wildCard = fieldPrefix + "1";

				if(!parameters.containsKey(wildCard))
				{
					throw new FaxApiValidationException("Required Field missing.  No values for " + fieldPrefix, "fax.exception.validationError");
				}
				else
				{
					String value = parameters.get(wildCard);
					if(value.length() <= 0)
					{
						throw new FaxApiValidationException("Required Field missing.  No values for " + fieldPrefix, "fax.exception.validationError");
					}
				}
			}
			else
			{
				if(field.contains("|")) // piped separated variable.  At lease 1 must be present.
				{
					String[] pipedFields = field.split("\\|");
					boolean checkSuccessful = false;

					for(String pipedField : pipedFields)
					{
						String trimmedPipedField = pipedField.trim();

						if(trimmedPipedField.endsWith("*")) // piped value has a wildcard, look for first value
						{
							String prefix = trimmedPipedField.replace("*", "");
							String wildcard = prefix + "1";

							if(parameters.containsKey(wildcard)) // parameter exists, check to make sure it has a value
							{
								String pVal = parameters.get(wildcard);
								if(pVal.length() > 0)
								{
									checkSuccessful = true;
								}
							}
						}
						else
						{
							if(parameters.containsKey(trimmedPipedField))
							{
								String pVal = parameters.get(trimmedPipedField);
								if(pVal.length() > 0)
								{
									checkSuccessful = true;
								}
							}
						}
					}
					if(!checkSuccessful)
					{
						throw new FaxApiValidationException(
								"Required field missing.  You must provide at lease 1 of the following: " + String.join(",", pipedFields),
								"fax.exception.validationError");
					}
				}
				else // standard field, check if it exists
				{
					if(!parameters.containsKey(field))
					{
						throw new FaxApiValidationException("Required field " + field + " is missing!", "fax.exception.validationError");
					}
					else // ensure field value is not empty
					{
						String value = parameters.get(field);
						if(value.length() <= 0)
						{
							throw new FaxApiValidationException("Required field " + field + " is missing!", "fax.exception.validationError");
						}
					}
				}
			}
		}
	}

	// merges 2 dictionaries into one, stops duplicates
	private Map<String, String> _mergeDictionaries(Map<String, String> d1, Map<String, String> d2)
	{
		Map<String, String> mergedDictionary = new HashMap<>();
		mergedDictionary.putAll(d1);
		mergedDictionary.putAll(d2);

		return mergedDictionary;
	}
}