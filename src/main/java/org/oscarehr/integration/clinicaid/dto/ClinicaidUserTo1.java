package org.oscarehr.integration.clinicaid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ClinicaidUserTo1 implements Serializable
{
	private String identifier;

	@JsonProperty("first_name")

	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	public ClinicaidUserTo1()
	{
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getLastName()
	{
		return this.lastName;
	}
}
