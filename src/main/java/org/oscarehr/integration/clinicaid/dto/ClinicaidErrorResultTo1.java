package org.oscarehr.integration.clinicaid.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicaidErrorResultTo1 implements Serializable
{
	@JsonProperty("standard_errors")
	private ArrayList<String> standardErrors;

	public void setStandardErrors(ArrayList<String> standardErrors)
	{
		this.standardErrors = standardErrors;
	}

	public ArrayList<String> getStandardErrors()
	{
		return this.standardErrors;
	}

	public String getErrorString()
	{
		String errorString = "";
		Iterator<String> standardErrorsI = this.standardErrors.iterator();
		while (standardErrorsI.hasNext())
		{
			errorString += standardErrorsI.next() + "\n";
		}
		return errorString;
	}
}
