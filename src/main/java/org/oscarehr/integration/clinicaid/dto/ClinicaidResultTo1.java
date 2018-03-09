package org.oscarehr.integration.clinicaid.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicaidResultTo1 implements Serializable
{
	final String ERROR_STRING = "error";


	private String result;

	private Map<String, String> data;

	private ClinicaidErrorResultTo1 errors;

	private String nonce;

	private boolean hasError = false;


	public String getResult()
	{
		return this.result;
	}

	public void setResult(String result)
	{
		this.result = result;
		if (this.result.equals(ERROR_STRING))
		{
			this.hasError = true;
		}
	}

	public String getNonce()
	{
		return this.nonce;
	}

	public Map<String, String> getData()
	{
		return this.data;
	}

	public ClinicaidErrorResultTo1 getErrors()
	{
		return this.errors;
	}

	public void setErrors(ClinicaidErrorResultTo1 errors)
	{
		this.errors = errors;
	}

	public boolean hasError()
	{
		return this.hasError;
	}
}
