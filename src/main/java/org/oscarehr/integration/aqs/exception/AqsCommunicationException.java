package org.oscarehr.integration.aqs.exception;

public class AqsCommunicationException extends RuntimeException
{
	public AqsCommunicationException(String msg)
	{
		super(msg);
	}

	public AqsCommunicationException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
