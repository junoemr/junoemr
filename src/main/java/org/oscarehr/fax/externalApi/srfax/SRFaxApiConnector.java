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
package org.oscarehr.fax.externalApi.srfax;

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
import org.oscarehr.fax.externalApi.srfax.result.GetFaxInboxResult;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxOutboxResult;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxStatusResult;
import org.oscarehr.fax.externalApi.srfax.result.GetUsageResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SRFaxApiConnector
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String serverUrl = "https://www.srfax.com/SRF_SecWebSvc.php";
	private final String access_id;
	private final String access_pwd;

	public static final List<String> validCoverLetterNames = new ArrayList<String>(4) {{
		add("Basic");
		add("Standard");
		add("Company");
		add("Personal");
	}};

	public SRFaxApiConnector(String username, String password)
	{
		this.access_id = username;
		this.access_pwd = password;
	}

	public SingleWrapper<Integer> Queue_Fax(Map<String, String> parameters)
	{
		String[] requiredFields = {"sCallerID", "sSenderEmail", "sFaxType", "sToFaxNumber"};
		String[] optionalFields = {"sResponseFormat", "sAccountCode", "sRetries", "sCoverPage", "sCPFromName", "sCPToName", "sCPOrganization",
				"sCPSubject", "sCPComments", "sFileName_*", "sFileContent_*", "sNotifyURL", "sFaxFromHeader", "sQueueFaxDate", "sQueueFaxTime"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Queue_Fax");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processSingleResponse(result);
	}

	public SingleWrapper<GetFaxStatusResult> Get_FaxStatus(Map<String, String> parameters)
	{
		String[] requiredFields = {"sFaxDetailsID"};
		String[] optionalFields = {"sResponseFormat"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Get_FaxStatus");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processSingleResponse(result);
	}

	public ListWrapper<GetFaxStatusResult> Get_MultiFaxStatus(Map<String, String> parameters)
	{
		String[] requiredFields = {"sFaxDetailsID"};
		String[] optionalFields = {"sResponseFormat"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Get_MultiFaxStatus");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processListResponse(result);
	}

	public ListWrapper<GetFaxInboxResult> Get_Fax_Inbox(Map<String, String> parameters)
	{

		String[] requiredFields = {};
		String[] optionalFields = {"sResponseFormat", "sPeriod", "sStartDate", "sEndDate", "sViewedStatus", "sIncludeSubUsers", "sFaxDetailsID"};


		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Get_Fax_Inbox");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processListResponse(result);
	}

	public ListWrapper<GetFaxOutboxResult> Get_Fax_Outbox(Map<String, String> parameters)
	{

		String[] requiredFields = {};
		String[] optionalFields = {"sResponseFormat", "sPeriod", "sStartDate", "sEndDate", "sIncludeSubUsers", "sFaxDetailsID"};


		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Get_Fax_Outbox");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processListResponse(result);
	}

	public SingleWrapper<String> Retrieve_Fax(Map<String, String> parameters)
	{
		String[] requiredFields = {"sFaxFileName|sFaxDetailsID", "sDirection"};
		String[] optionalFields = {"sFaxFormat", "sMarkasViewed", "sResponseFormat", "sSubUserID"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Retrieve_Fax");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processSingleResponse(result);
	}

	public SingleWrapper<String> Update_Viewed_Status(Map<String, String> parameters)
	{
		String[] requiredFields = {"sFaxFileName|sFaxDetailsID", "sDirection", "sMarkasViewed"};
		String[] optionalFields = {"sResponseFormat"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Update_Viewed_Status");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processSingleResponse(result);
	}

	public SingleWrapper<String> Delete_Fax(Map<String, String> parameters)
	{
		String[] requiredFields = {"sDirection", "sFaxFileName_*|sFaxDetailsID_*"};
		String[] optionalFields = {"sResponseFormat"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Delete_Fax");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processSingleResponse(result);
	}

	public SingleWrapper<String> Stop_Fax(Map<String, String> parameters)
	{
		String[] requiredFields = {"sFaxDetailsID"};
		String[] optionalFields = {"sResponseFormat"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Stop_Fax");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processSingleResponse(result);
	}

	public ListWrapper<GetUsageResult> Get_Fax_Usage(Map<String, String> parameters)
	{
		String[] requiredFields = {};
		String[] optionalFields = {"sResponseFormat", "sPeriod", "sStartDate", "sEndDate", "sIncludeSubUsers"};

		_validateRequiredVariables(requiredFields, parameters);

		Map<String, String> postVariables = _preparePostVariables(requiredFields, optionalFields, parameters);

		postVariables.put("action", "Get_Fax_Usage");
		postVariables.put("access_id", access_id);
		postVariables.put("access_pwd", access_pwd);

		String result = _processRequest(postVariables);

		return processListResponse(result);
	}

	/*******************INTERNAL FUNCTIONS*********************************/

	private static <T> ListWrapper processListResponse(String response)
	{
		ListWrapper<T> result = null;
		try
		{
			JSONObject json = new JSONObject(response);
			String status = json.getString("Status");
			if(status.equals("Success"))
			{
				ObjectMapper mapper = new ObjectMapper();
				result = mapper.readValue(response, new TypeReference<ListWrapper<T>>(){});
			}
			else
			{
				logger.warn("API Response Failure: " + response);
				result = new ListWrapper<>();
				result.setStatus(status);
				result.setError(json.getString("Result"));
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
		JSONObject json = new JSONObject(response);
		String status = json.getString("Status");
		SingleWrapper<T> result = null;

		try
		{
			if(status.equals("Success"))
			{
				ObjectMapper mapper = new ObjectMapper();
				result = mapper.readValue(response, new TypeReference<SingleWrapper<T>>(){});
			}
			else
			{
				logger.warn("API Response Failure: " + response);
				result = new SingleWrapper<>();
				result.setStatus(status);
				result.setError(json.getString("Result"));
			}
		}
		catch(IOException e)
		{
			logger.error("Error", e);
		}
		return result;
	}

	private String _processRequest(Map<String, String> postVariables)
	{
		String result = "";
		try
		{
			HttpClient httpClient = new DefaultHttpClient();

			logger.debug("POST URL: " + serverUrl);
			HttpPost httpPost = new HttpPost(serverUrl);

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
					result = IOUtils.toString(inputStream, "UTF-8");
				}
			}
		}
		catch(IOException e)
		{
			logger.error("Error", e);
			throw new RuntimeException(e);
		}
		return result;
	}

	private Map<String, String> _preparePostVariables(String[] requiredFields, String[] optionalFields, Map<String, String> parameters)
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

	private void _validateRequiredVariables(String[] requiredVariables, Map<String, String> parameters)
	{

		for(String field : requiredVariables)
		{
			String error = "";

			if(field.endsWith("*") && !field.contains("|")) // non piped wildcard variable.  check for first instance
			{
				String fieldPrefix = field.replace("*", "");
				String wildCard = fieldPrefix + "1";

				if(!parameters.containsKey(wildCard))
				{
					error = "Required Field missing.  No values for " + fieldPrefix;
				}
				else
				{
					String value = parameters.get(wildCard);
					if(value.length() <= 0)
					{
						error = "Required Field missing.  No values for " + fieldPrefix;
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
						error = "Required field missing.  You must provide at lease 1 of the following: " + String.join(",", pipedFields);
					}
				}
				else // standard field, check if it exists
				{
					if(!parameters.containsKey(field))
					{
						error = "Required field " + field + " is missing!";
					}
					else // ensure field value is not empty
					{
						String value = parameters.get(field);
						if(value.length() <= 0)
						{
							error = "Required field " + field + " is missing!";
						}
					}
				}
			}
			if(error.length() > 0)
			{
				throw new RuntimeException(error);
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


